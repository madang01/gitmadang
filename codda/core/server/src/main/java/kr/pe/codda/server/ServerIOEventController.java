package kr.pe.codda.server;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Set;

import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.SocketOutputStreamFactoryIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.server.classloader.ServerTaskMangerIF;

public class ServerIOEventController extends Thread implements ServerIOEvenetControllerIF, ProjectLoginManagerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ServerIOEventController.class);

	private String projectName;
	private String serverHost;
	private int serverPort;
	private int maxClients;

	private long socketTimeOut = 5000;
	private int serverOutputMessageQueueCapacity = 5;
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	private MessageProtocolIF messageProtocol = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ServerTaskMangerIF serverTaskManager = null;

	private Selector ioEventSelector = null; // OP_ACCEPT 전용 selector
	private ServerSocketChannel ssc = null;

	private HashMap<SelectionKey, AcceptedConnection> selectedKey2AcceptedConnectionHash = new HashMap<SelectionKey, AcceptedConnection>();

	// private final Object loginMangerMonitor = new Object();
	private HashMap<SelectionKey, String> selectedKey2LonginIDHash = new HashMap<SelectionKey, String>();
	private HashMap<String, SelectionKey> longinID2SelectedKeyHash = new HashMap<String, SelectionKey>();

	public ServerIOEventController(ProjectPartConfiguration projectPartConfiguration,
			SocketOutputStreamFactoryIF socketOutputStreamFactory, MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferPool, ServerTaskMangerIF serverTaskManager) {

		this.projectName = projectPartConfiguration.getProjectName();
		this.serverHost = projectPartConfiguration.getServerHost();
		this.serverPort = projectPartConfiguration.getServerPort();
		this.maxClients = projectPartConfiguration.getServerMaxClients();
		this.socketTimeOut = projectPartConfiguration.getClientSocketTimeout();
		this.serverOutputMessageQueueCapacity = projectPartConfiguration.getServerOutputMessageQueueCapacity();

		this.socketOutputStreamFactory = socketOutputStreamFactory;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.serverTaskManager = serverTaskManager;
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
				@SuppressWarnings("unused")
				int keyReady = ioEventSelector.select();
				// log.info("keyReady={}", keyReady);

				// if (keyReady > 0) {

				Set<SelectionKey> selectedKeySet = ioEventSelector.selectedKeys();

				try {
					for (SelectionKey selectedKey : selectedKeySet) {
						try {
							if (selectedKey.isAcceptable()) {
								ServerSocketChannel readyChannel = (ServerSocketChannel) selectedKey.channel();

								SocketChannel acceptableSocketChannel = readyChannel.accept();

								if (null == acceptableSocketChannel) {
									log.warn("acceptableSocketChannel is null");
									continue;
								}							

								int numberOfAcceptedConnection = selectedKey2AcceptedConnectionHash.size();
								
								log.info("acceptable socket channel={}, numberOfAcceptedConnection={}", 
										acceptableSocketChannel.hashCode(), numberOfAcceptedConnection);
								
								if (numberOfAcceptedConnection >= maxClients) {
									try {
										acceptableSocketChannel.setOption(StandardSocketOptions.SO_LINGER, 0);
									} catch(Exception e) {
										log.warn("fail to set the value of a acceptable channel[{}] option 'SO_LINGER'", acceptableSocketChannel.hashCode());
									}
									try {
										acceptableSocketChannel.close();
									} catch(Exception e) {
										log.warn("fail to close the acceptable channel[{}]", acceptableSocketChannel.hashCode());
									}
									log.warn(
											"close the acceptable socket channel[{}] because the maximum number[{}] of sockets has been reached",
											acceptableSocketChannel.hashCode(), maxClients);
									
									continue;
								}
								
								try {
									if (acceptableSocketChannel.isConnectionPending()) {
										acceptableSocketChannel.finishConnect();
									}	
								} catch(Exception e) {
									log.warn("fail to finish connect the accepted channel[{}]", acceptableSocketChannel.hashCode());
									continue;
								}
								
								setupAcceptedSocketChannel(acceptableSocketChannel);

								SelectionKey acceptedKey = null;

								try {
									acceptedKey = acceptableSocketChannel.register(ioEventSelector, SelectionKey.OP_READ);
								} catch (ClosedChannelException e) {
									log.warn(
											"fail to register this channel[{}] with the given selector having a the interest set OP_READ",
											acceptableSocketChannel.hashCode());

									continue;
								}

								SocketOutputStream socketOutputStreamOfAcceptedSC = null;

								try {
									socketOutputStreamOfAcceptedSC = socketOutputStreamFactory.createSocketOutputStream();
								} catch (NoMoreDataPacketBufferException e) {
									acceptableSocketChannel.setOption(StandardSocketOptions.SO_LINGER, 0);
									acceptableSocketChannel.close();
									acceptedKey.cancel();
									log.warn(
											"close the acceptable socket channel[{}] becase there is no more data packet buffer",
											acceptableSocketChannel.hashCode());
									continue;
								}

								AcceptedConnection acceptedConnection = new AcceptedConnection(acceptedKey,
										acceptableSocketChannel, projectName, socketTimeOut,
										serverOutputMessageQueueCapacity, socketOutputStreamOfAcceptedSC, this,
										messageProtocol, dataPacketBufferPool, this, serverTaskManager);

								/** 소켓 자원 등록 작업 */
								selectedKey2AcceptedConnectionHash.put(acceptedKey, acceptedConnection);
								
								log.info("successfully changed acceptedKey[{}]'s acceptable socket channel[{}] to accepted socket channel", 
										acceptedKey.hashCode(), acceptableSocketChannel.hashCode());
							}

							//  && isEnoughDataPacketBuffer(100)
							if (selectedKey.isReadable()) {							
								ServerIOEventHandlerIF accpetedConneciton = selectedKey2AcceptedConnectionHash
										.get(selectedKey);

								if (null == accpetedConneciton) {
									log.warn(
											"this selectedKey2AcceptedConnectionHash map contains no mapping for the key[{}][{}]",
											selectedKey.hashCode(), selectedKey.channel().hashCode());
									continue;
								}

								accpetedConneciton.onRead(selectedKey);
							}

							if (selectedKey.isWritable()) {
								ServerIOEventHandlerIF accpetedConneciton = selectedKey2AcceptedConnectionHash
										.get(selectedKey);

								if (null == accpetedConneciton) {
									log.warn(
											"this selectedKey2AcceptedConnectionHash map contains no mapping for the key[{}][{}]",
											selectedKey.hashCode(), selectedKey.channel().hashCode());
									continue;
								}

								accpetedConneciton.onWrite(selectedKey);
							}
						} catch(CancelledKeyException e) {
							log.warn("CancelledKeyException occured, socket="+selectedKey.channel().hashCode(), e);
							
							ServerIOEventHandlerIF accpetedConneciton = selectedKey2AcceptedConnectionHash
									.get(selectedKey);

							if (null != accpetedConneciton) {								
								log.warn("the cancelled key[{}] doesn't be deleted in selectedKey2AcceptedConnectionHash", selectedKey.hashCode());
							}
							
							continue;
						}
					}	
				} finally {
					selectedKeySet.clear();
				}
			}
			// }
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

		// synchronized (loginMangerMonitor) {
		if (selectedKey2LonginIDHash.containsKey(selectedKey)) {
			log.warn("the parameter selectedKey[{}] is the socket channel that is already registered",
					selectedKey.hashCode());
			return;
		}
		if (longinID2SelectedKeyHash.containsKey(loginID)) {
			log.warn("the parameter loginID[{}] is the login id that is already registered", loginID);
			return;
		}

		selectedKey2LonginIDHash.put(selectedKey, loginID);
		longinID2SelectedKeyHash.put(loginID, selectedKey);
		// }

		log.info("login register success, selectedKey={}, socketChannel={}, loginID={}", selectedKey.hashCode(),
				selectedKey.channel().hashCode(), loginID);
	}

	private void doRemoveLoginUser(SelectionKey selectedKey, String loginID) {
		selectedKey2LonginIDHash.remove(selectedKey);
		longinID2SelectedKeyHash.remove(loginID);
	}

	public void removeLoginUser(SelectionKey selectedKey) {
		if (null == selectedKey) {
			throw new IllegalArgumentException("the parameter selectedKey is null");
		}

		String loginID = selectedKey2LonginIDHash.get(selectedKey);
		if (null != loginID) {
			doRemoveLoginUser(selectedKey, loginID);
		}
	}

	@Override
	public boolean isLogin(String loginID) {
		if (null == loginID) {
			throw new IllegalArgumentException("the loginID sc is null");
		}

		boolean isLogin = false;
		SelectionKey selectedKey = null;
		// synchronized (loginMangerMonitor) {
		selectedKey = longinID2SelectedKeyHash.get(loginID);
		// }

		if (null != selectedKey) {
			isLogin = ((SocketChannel) selectedKey.channel()).isConnected();
		}

		return isLogin;
	}

	@Override
	public SelectionKey getSelectionKey(String loginID) {
		if (null == loginID) {
			throw new IllegalArgumentException("the parameter loginID is null");
		}

		SelectionKey selectedKey = longinID2SelectedKeyHash.get(loginID);
		return selectedKey;
	}

	public AcceptedConnection getAcceptedConnection(SelectionKey selectedKey) {
		return selectedKey2AcceptedConnectionHash.get(selectedKey);
	}

	/*private boolean isEnoughDataPacketBuffer(int limitQueueSize) {
		int queueSize = dataPacketBufferPool.size();
		boolean isResult = (queueSize >= limitQueueSize);
		return isResult;
	}*/

	// isEnoughFreeMemory(10*1024*1024L)
	/*
	 * private boolean isEnoughFreeMemory(long limitFreeMemorySize) { long free =
	 * Runtime.getRuntime().freeMemory(); boolean isResult = (free >=
	 * limitFreeMemorySize); return isResult; }
	 */

	/*
	 * private boolean canRead() { for (ServerIOEventHandlerIF
	 * currentWorkingAcceptedConnection :
	 * selectedKey2AcceptedConnectionHash.values()) { if (!
	 * currentWorkingAcceptedConnection.canRead()) { return false; } } return true;
	 * }
	 */
}
