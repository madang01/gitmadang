package kr.pe.codda.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.SocketOutputStreamFactoryIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.server.threadpool.executor.ServerExecutorIF;
import kr.pe.codda.server.threadpool.executor.ServerExecutorPoolIF;

public class ServerIOEventController extends Thread implements ServerIOEvenetControllerIF, ProjectLoginManagerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ServerIOEventController.class);

	private String projectName;
	private String serverHost;
	private int serverPort;
	private int maxClients;
	
	private long socketTimeOut=5000;
	private int outputMessageQueueSize=5;
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;		
	private MessageProtocolIF messageProtocol = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ServerExecutorPoolIF serverExecutorPool = null;
	
	private Selector ioEventSelector = null; // OP_ACCEPT 전용 selector
	private ServerSocketChannel ssc = null;
	
	private ConcurrentHashMap<SelectionKey, AcceptedConnection> selectedKey2AcceptedConnectionHash = new ConcurrentHashMap<SelectionKey, AcceptedConnection>();
	
	private final Object loginMangerMonitor = new Object();
	private HashMap<SelectionKey, String> selectedKey2LonginIDHash = new HashMap<SelectionKey, String>();
	private HashMap<String, SelectionKey> longinID2SelectedKeyHash = new HashMap<String, SelectionKey>();
	
	public ServerIOEventController(ProjectPartConfiguration projectPartConfiguration,				
			SocketOutputStreamFactoryIF socketOutputStreamFactory,			
			MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferPool) {
		
		this.projectName = projectPartConfiguration.getProjectName();
		this.serverHost = projectPartConfiguration.getServerHost();
		this.serverPort = projectPartConfiguration.getServerPort();
		this.maxClients = projectPartConfiguration.getClientConnectionMaxCount();
		this.socketTimeOut = projectPartConfiguration.getClientSocketTimeout();
		this.outputMessageQueueSize = projectPartConfiguration.getClientAsynOutputMessageQueueSize();
		
		this.socketOutputStreamFactory = socketOutputStreamFactory;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPool = dataPacketBufferPool;
	}
	
	public void setServerExecutorPool(ServerExecutorPoolIF serverExecutorPool) {
		this.serverExecutorPool = serverExecutorPool;
	}
	
	private void addNewAcceptedSocketChannel(SelectionKey acceptedKey, SocketChannel newAcceptedSC)
			throws NoMoreDataPacketBufferException, InterruptedException {
		if (null == newAcceptedSC) {
			throw new IllegalArgumentException("the parameter newAcceptedSC is null");
		}		

		SocketOutputStream socketOutputStreamOfAcceptedSC = socketOutputStreamFactory.newInstance();

		PersonalLoginManager personalLoginManagerOfAcceptedSC = new PersonalLoginManager(acceptedKey,
				this);
		
		ServerExecutorIF executorOfAcceptedSC = serverExecutorPool.getExecutorWithMinimumNumberOfSockets();

		AcceptedConnection acceptedConnection = new AcceptedConnection(acceptedKey, newAcceptedSC,
				socketTimeOut,
				outputMessageQueueSize,
				socketOutputStreamOfAcceptedSC,
				personalLoginManagerOfAcceptedSC,
				executorOfAcceptedSC,
				messageProtocol,				
				dataPacketBufferPool, 
				this);

		/** 소켓 자원 등록 작업 */
		selectedKey2AcceptedConnectionHash.put(acceptedKey, acceptedConnection);
		
		executorOfAcceptedSC.addNewSocket(newAcceptedSC);
	}

	
	/**
	 * 서버 소켓을 생성하고 selector에 OP_ACCEPT로 등록한다.
	 * 
	 * @throws ServerSocketChannel관련
	 *             작업에서 발생한다.
	 */
	private void initServerSocket() {
		try {
			ioEventSelector = Selector.open();

			ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false); // non block 설정
			ssc.setOption(StandardSocketOptions.SO_REUSEADDR, true);

			InetSocketAddress address = new InetSocketAddress(serverHost, serverPort);
			ssc.socket().bind(address);

			ssc.register(ioEventSelector, SelectionKey.OP_ACCEPT);
		} catch (IOException ioe) {
			log.error("IOException", ioe);
			System.exit(1);
		}

	}

	@Override
	public void run() {
		log.info("ServerIOEventController::projectName[{}] start", projectName);

		initServerSocket();

		try {
			while (!Thread.currentThread().isInterrupted()) {
				int keyReady = ioEventSelector.select();
				if (keyReady > 0) {

					Set<SelectionKey> selectedKeySet = ioEventSelector.selectedKeys();

					try {
						for (SelectionKey selectedKey : selectedKeySet) {
							if (! selectedKey.isValid()) {
								AcceptedConnection accpetedConneciton = selectedKey2AcceptedConnectionHash.get(selectedKey);
								
								if (null == accpetedConneciton) {
									log.warn("this selectedKey2AcceptedConnectionHash map contains no mapping for the key[{}][{}]",
											selectedKey.hashCode(), selectedKey.channel().hashCode());
									continue;
								}
								
								accpetedConneciton.close();
								accpetedConneciton.releaseResources();
								cancel(selectedKey);								
								continue;
							}
							

							if (selectedKey.isAcceptable()) {
								ServerSocketChannel readyChannel = (ServerSocketChannel) selectedKey.channel();

								SocketChannel acceptableSocketChannel = readyChannel.accept();

								if (null == acceptableSocketChannel) {
									log.warn("acceptableSocketChannel is null");
									continue;
								}

								int numberOfAcceptedConnection = selectedKey2AcceptedConnectionHash.size();

								if (numberOfAcceptedConnection < maxClients) {
									// log.info("accepted socket channel=[{}]", sc.hashCode());
									setupAcceptedSocketChannel(acceptableSocketChannel);

									try {
										SelectionKey acceptedKey = acceptableSocketChannel.register(ioEventSelector, SelectionKey.OP_READ);

										addNewAcceptedSocketChannel(acceptedKey, acceptableSocketChannel);
									} catch (ClosedChannelException e) {
										log.warn(
												"fail to register this channel[{}] with the given selector having a the interest set OP_READ",
												acceptableSocketChannel.hashCode());

										continue;
									}
								} else {
									acceptableSocketChannel.setOption(StandardSocketOptions.SO_LINGER, 0);
									acceptableSocketChannel.close();
									log.warn("max clients[{}] researched so close the selected socket channel[{}]",
											maxClients, acceptableSocketChannel.hashCode());

								}								
							} else if (selectedKey.isReadable()) {
								AcceptedConnection accpetedConneciton = selectedKey2AcceptedConnectionHash.get(selectedKey);
								
								if (null == accpetedConneciton) {
									log.warn("this selectedKey2AcceptedConnectionHash map contains no mapping for the key[{}][{}]",
											selectedKey.hashCode(), selectedKey.channel().hashCode());
									continue;
								}

								accpetedConneciton.onRead(selectedKey);
							} else if (selectedKey.isWritable()) {
								AcceptedConnection accpetedConneciton = selectedKey2AcceptedConnectionHash.get(selectedKey);
								
								if (null == accpetedConneciton) {
									log.warn("this selectedKey2AcceptedConnectionHash map contains no mapping for the key[{}][{}]",
											selectedKey.hashCode(), selectedKey.channel().hashCode());
									continue;
								}

								accpetedConneciton.onWrite(selectedKey);
							}
						}
					} finally {
						selectedKeySet.clear();
					}
				}
			}
			log.warn("{} ServerIOEventController loop exit", projectName);
		} catch (InterruptedException e) {
			log.warn("{} ServerIOEventController stop", projectName);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append(projectName)
					.append(" ServerIOEventController unknown error, errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
		} finally {
			try {
				ssc.close();
			} catch (IOException ioe) {
				log.warn("IOException", ioe);
			}

			try {
				ioEventSelector.close();
			} catch (IOException ioe) {
				log.warn("IOException", ioe);
			}
		}
	}

	private void setupAcceptedSocketChannel(SocketChannel acceptedSocketChannel) throws IOException {
		acceptedSocketChannel.configureBlocking(false);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		acceptedSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_LINGER, 0);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
	}

	@Override
	public void startWrite(ServerInterestedConnectionIF asynInterestedConnectionIF) {
		SelectionKey selectedKey = asynInterestedConnectionIF.keyFor(ioEventSelector);
		if (null == selectedKey) {
			log.warn("selectedKey is null", asynInterestedConnectionIF.hashCode());
			return;
		}

		selectedKey.interestOps(selectedKey.interestOps() | SelectionKey.OP_WRITE);
		
		ioEventSelector.wakeup();
	}
	
	@Override
	public void endWrite(ServerInterestedConnectionIF asynInterestedConnectionIF) {
		SelectionKey selectedKey = asynInterestedConnectionIF.keyFor(ioEventSelector);
		if (null == selectedKey) {
			log.error("selectedKey is null");
			System.exit(1);
		}
		selectedKey.interestOps(selectedKey.interestOps() & ~SelectionKey.OP_WRITE);
		ioEventSelector.wakeup();
	}


	@Override
	public void cancel(SelectionKey selectedKey) {
		selectedKey.channel();
		selectedKey2AcceptedConnectionHash.remove(selectedKey);
	}
	
	public int getNumberOfAcceptedConnection() {
		return selectedKey2AcceptedConnectionHash.size();
	}

	@Override
	public void registerloginUser(SelectionKey selectedKey, String loginID) {
		if (null == selectedKey) {
			throw new IllegalArgumentException("the parameter selectedKey is null");
		}

		if (null == loginID) {
			throw new IllegalArgumentException("the parameter loginID is null");
		}
		
		synchronized (loginMangerMonitor) {			
			if (selectedKey2LonginIDHash.containsKey(selectedKey)) {				
				log.warn("the parameter selectedKey[{}] is the socket channel that is already registered", selectedKey.hashCode());
				return;
			}
			if (longinID2SelectedKeyHash.containsKey(loginID)) {
				log.warn("the parameter loginID[{}] is the login id that is already registered", loginID);
				return;
			}			
			
			selectedKey2LonginIDHash.put(selectedKey, loginID);
			longinID2SelectedKeyHash.put(loginID, selectedKey);
		}
		
		log.info("login register success, selectedKey={}, socketChannel={}, loginID={}", 
				selectedKey.hashCode(), selectedKey.channel().hashCode(), loginID);
	}

	private void doRemoveLoginUser(SelectionKey selectedKey, String loginID) {
		selectedKey2LonginIDHash.remove(selectedKey);
		longinID2SelectedKeyHash.remove(loginID);
	}
	
	public void removeLoginUser(String loginID) {
		if (null == loginID) {
			throw new IllegalArgumentException("the loginID sc is null");
		}
		
		synchronized (loginMangerMonitor) {
			SelectionKey selectedKey = longinID2SelectedKeyHash.get(loginID);
			if (null != selectedKey) {
				doRemoveLoginUser(selectedKey, loginID);
			}
		}
	}
	
	public void removeLoginUser(SelectionKey selectedKey) {
		if (null == selectedKey) {
			throw new IllegalArgumentException("the parameter selectedKey is null");
		}
		
		synchronized (loginMangerMonitor) {
			String loginID = selectedKey2LonginIDHash.get(selectedKey);
			if (null != loginID) {
				doRemoveLoginUser(selectedKey, loginID);
			}
		}
	}
	
	@Override
	public boolean isLogin(String loginID) {
		if (null == loginID) {
			throw new IllegalArgumentException("the loginID sc is null");
		}
		
		boolean isLogin = false;
		SelectionKey selectedKey = null;
		synchronized (loginMangerMonitor) {			
			selectedKey = longinID2SelectedKeyHash.get(loginID);
		}
		
		if (null != selectedKey) {
			isLogin = ((SocketChannel)selectedKey.channel()).isConnected();
		}
		
		return isLogin;
	}

	@Override
	public boolean isLogin(SelectionKey selectedKey) {
		if (null == selectedKey) {
			throw new IllegalArgumentException("the parameter selectedKey is null");
		}
		
		boolean isLogin = false;
		String loginID = null;
		synchronized (loginMangerMonitor) {
			loginID = selectedKey2LonginIDHash.get(selectedKey);
		}		
		
		if (null != loginID) {
			isLogin = ((SocketChannel)selectedKey.channel()).isConnected();
		}
		
		return isLogin;
	}
	
	public String getUserID(SelectionKey selectedKey) {
		if (null == selectedKey) {
			throw new IllegalArgumentException("the parameter selectedKey is null");
		}
		String loginID = null;
		synchronized (loginMangerMonitor) {
			loginID = selectedKey2LonginIDHash.get(selectedKey);
		}
		
		if (null != loginID) {
			boolean isLogin = ((SocketChannel)selectedKey.channel()).isConnected();
			
			if (! isLogin) {
				return null;
			}
		}
		
		
		return loginID;
	}

	@Override
	public SelectionKey getSelectionKey(String loginUserID) {
		if (null == loginUserID) {
			throw new IllegalArgumentException("the parameter loginUserID is null");
		}
		
		SelectionKey selectedKey = null;		
		synchronized (loginMangerMonitor) {
			selectedKey = longinID2SelectedKeyHash.get(loginUserID);
		}
		return selectedKey;
	}
	
	public AcceptedConnection getAcceptedConnection(SelectionKey selectedKey) {
		return selectedKey2AcceptedConnectionHash.get(selectedKey);
	}
}
