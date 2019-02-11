package kr.pe.codda.server;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Set;

import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.ReceivedDataOnlyStream;
import kr.pe.codda.common.io.ReceivedDataOnlyStreamFactoryIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.server.classloader.ServerTaskMangerIF;

public class ServerIOEventController extends Thread implements
		ServerIOEvenetControllerIF, ProjectLoginManagerIF {
	private InternalLogger log = InternalLoggerFactory
			.getInstance(ServerIOEventController.class);

	private String projectName;
	private String serverHost;
	private int serverPort;
	private int maxClients;

	private long socketTimeOut = 5000;
	private int serverOutputMessageQueueCapacity = 5;
	private ReceivedDataOnlyStreamFactoryIF receivedDataOnlyStreamFactory = null;
	private MessageProtocolIF messageProtocol = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ServerTaskMangerIF serverTaskManager = null;

	private Selector ioEventSelector = null; // OP_ACCEPT 전용 selector
	private ServerSocketChannel ssc = null;

	private HashMap<SelectionKey, String> selectedKey2LonginIDHash = new HashMap<SelectionKey, String>();
	private HashMap<String, SelectionKey> longinID2SelectedKeyHash = new HashMap<String, SelectionKey>();

	public ServerIOEventController(
			ProjectPartConfiguration projectPartConfiguration,
			ReceivedDataOnlyStreamFactoryIF receivedDataOnlyStreamFactory,
			MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferPool,
			ServerTaskMangerIF serverTaskManager) {

		this.projectName = projectPartConfiguration.getProjectName();
		this.serverHost = projectPartConfiguration.getServerHost();
		this.serverPort = projectPartConfiguration.getServerPort();
		this.maxClients = projectPartConfiguration.getServerMaxClients();
		this.socketTimeOut = projectPartConfiguration.getClientSocketTimeout();
		this.serverOutputMessageQueueCapacity = projectPartConfiguration
				.getServerOutputMessageQueueCapacity();

		this.receivedDataOnlyStreamFactory = receivedDataOnlyStreamFactory;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.serverTaskManager = serverTaskManager;
		
		initServerSocket();
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

			InetSocketAddress address = new InetSocketAddress(serverHost,
					serverPort);
			ssc.socket().bind(address);

			ssc.register(ioEventSelector, SelectionKey.OP_ACCEPT);
		} catch (IOException ioe) {
			log.error("IOException", ioe);
			System.exit(1);
		}

	}
	
	private void closeAcceptedSocketChannel(SocketChannel acceptableSocketChannel) {
		try {
			acceptableSocketChannel
					.setOption(
							StandardSocketOptions.SO_LINGER,
							0);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
			.append("fail to set the value of a acceptable channel[")
			.append(acceptableSocketChannel
					.hashCode())
			.append("] option 'SO_LINGER'").toString();
			
			log.warn(errorMessage, e);
		}
		try {
			acceptableSocketChannel.close();
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
			.append("fail to close the acceptable channel[")
			.append(acceptableSocketChannel
					.hashCode())
			.append("]").toString();
			
			
			log.warn(errorMessage, e);
		}
	}
	

	@Override
	public void run() {
		log.info("ServerIOEventController::projectName[{}] start", projectName);

		

		try {
			while (!Thread.currentThread().isInterrupted()) {
				@SuppressWarnings("unused")
				int keyReady = ioEventSelector.select();
				

				Set<SelectionKey> selectedKeySet = ioEventSelector
						.selectedKeys();

				try {
					for (SelectionKey selectedKey : selectedKeySet) {
						try {
							if (selectedKey.isAcceptable()) {	
								ServerSocketChannel readyChannel = (ServerSocketChannel) selectedKey
										.channel();
								
								SocketChannel acceptedSocketChannel = null;
								
								try {
									acceptedSocketChannel = readyChannel.accept();
								} catch(Exception e) {
									String errorMessage = new StringBuilder()
									.append("fail to accept a connection[")
									.append(readyChannel.hashCode())
									.append("] made to this channel's socket, errmsg=")
									.append(e.getMessage()).toString();			

									log.warn(errorMessage);
									continue;
								}
								
								if (null == acceptedSocketChannel) {
									String errorMessage = new StringBuilder()
									.append("fail to accept a connection[")
									.append(readyChannel.hashCode())
									.append("] made to this channel's socket becase the returned value is null").toString();			

									log.warn(errorMessage);
									continue;
								}
								
								if (getNumberOfAcceptedConnection() >= maxClients) {			
									String errorMessage = new StringBuilder()
									.append("close the accepted socket channel[")
									.append(acceptedSocketChannel.hashCode())
									.append("] because the maximum number[")
									.append(maxClients)
									.append("] of sockets has been reached").toString();
									log.warn(errorMessage);
									continue;
								}
								
								try {
									if (acceptedSocketChannel
											.isConnectionPending()) {
										log.info("OP_CONNECT but a connection operation is in progress on this accepted channel[{}]", 
												acceptedSocketChannel.hashCode());
										
										boolean isSuccess = acceptedSocketChannel.finishConnect();
										
										if (! isSuccess) {
											String errorMessage = new StringBuilder()
											.append("fail to finish connect the accepted channel[")
											.append(acceptedSocketChannel.hashCode())
											.append("]").toString();
											
											log.warn(errorMessage);
											continue;
										}
									}
									
									setupAcceptedSocketChannel(acceptedSocketChannel);

									SelectionKey acceptedKey = acceptedSocketChannel
												.register(ioEventSelector,
														SelectionKey.OP_READ);		

									ReceivedDataOnlyStream receivedDataOnlyStream = receivedDataOnlyStreamFactory
												.createReceivedDataOnlyStream();		

									AcceptedConnection acceptedConnection = new AcceptedConnection(
											acceptedKey, acceptedSocketChannel,
											projectName, socketTimeOut,
											serverOutputMessageQueueCapacity,
											receivedDataOnlyStream, this,
											messageProtocol, dataPacketBufferPool,
											this, serverTaskManager);

									/** 소켓 자원 등록 작업 */
									acceptedKey.attach(acceptedConnection);
									

									log.info(
											"successfully changed acceptedKey[{}]'s accepted socket channel[{}] to accepted socket channel",
											acceptedKey.hashCode(),
											acceptedSocketChannel.hashCode());
								} catch (NoMoreDataPacketBufferException e) {
									String errorMessage = new StringBuilder()
											.append("the no more data packet buffer error occurred while registering the socket[")
											.append(acceptedSocketChannel.hashCode())
											.append("] in the accepted connection hash, errmsg=").append(e.getMessage()).toString();
									log.warn(errorMessage);

									closeAcceptedSocketChannel(acceptedSocketChannel);
									continue;
								} catch (IOException e) {
									String errorMessage = new StringBuilder()
											.append("the io error occurred while registering the socket[")
											.append(acceptedSocketChannel.hashCode())
											.append("] in the accepted connection hash, errmsg=").append(e.getMessage()).toString();
									log.warn(errorMessage);

									closeAcceptedSocketChannel(acceptedSocketChannel);
									continue;
								} catch(CancelledKeyException e) {
									String errorMessage = new StringBuilder()
									.append("this selector key[hashCode=socket channel=")
									.append(acceptedSocketChannel.hashCode())
									.append("] has been cancelled")
									.toString();
									
									log.warn(errorMessage);
									
									closeAcceptedSocketChannel(acceptedSocketChannel);
									continue;
								} catch (Exception e) {
									String errorMessage = new StringBuilder()
											.append("the unknown error occurred while registering the socket[")
											.append(acceptedSocketChannel.hashCode())
											.append("] in the accepted connection hash").toString();
									log.warn(errorMessage, e);

									closeAcceptedSocketChannel(acceptedSocketChannel);
									continue;									
								}
								continue;
							}
						} catch(CancelledKeyException e) {
							String errorMessage = new StringBuilder()
							.append("this selector key[")
							.append(selectedKey.hashCode())
							.append("] has been cancelled")
							.toString();
							
							log.warn(errorMessage);
							
							Object attachedObject = selectedKey.attachment();							
							
							if (null != attachedObject) {
								/** 등록된 셀렉터 키인 경우 자원 회수및 소켓을 닫는다 */
								ServerIOEventHandlerIF accpetedConneciton = (ServerIOEventHandlerIF)attachedObject;
								accpetedConneciton.close();
							}
							continue;
						} catch(Exception e) {
							String errorMessage = new StringBuilder()
							.append("dead code entering, this selector key[")
							.append(selectedKey.hashCode())
							.append("]")
							.toString();
							
							log.warn(errorMessage, e);
							
							Object attachedObject = selectedKey.attachment();							
							
							if (null != attachedObject) {
								/** 등록된 셀렉터 키인 경우 자원 회수및 소켓을 닫는다 */
								ServerIOEventHandlerIF accpetedConneciton = (ServerIOEventHandlerIF)attachedObject;
								accpetedConneciton.close();
							}
							continue;
						}
						
						Object attachedObject = selectedKey.attachment();
						
						if (null == attachedObject) {
							log.warn(
									"this selectedKey2AcceptedConnectionHash map contains no mapping for the key[{}][{}]",
									selectedKey.hashCode(), selectedKey
											.channel().hashCode());
							continue;
						}
						
						ServerIOEventHandlerIF accpetedConneciton = (ServerIOEventHandlerIF)attachedObject;
						
						try {
							if (selectedKey.isReadable()) {
								accpetedConneciton.onRead(selectedKey);
							}

							if (selectedKey.isWritable()) {
								accpetedConneciton.onWrite(selectedKey);
							}
						} catch (InterruptedException e) {
							String errorMessage = new StringBuilder()
							.append("InterruptedException occurred while reading the socket[")
							.append(accpetedConneciton.hashCode()).append("]").toString();
							log.warn(errorMessage);
							
							accpetedConneciton.close();
					
							throw e;
						} catch(CancelledKeyException e) {
							String errorMessage = new StringBuilder()
							.append("this selector key[socket channel=")
							.append(accpetedConneciton.hashCode())
							.append("] has been cancelled")
							.toString();
							
							log.warn(errorMessage);
							
							accpetedConneciton.close();
							continue;							
						} catch (NoMoreDataPacketBufferException e) {
							String errorMessage = new StringBuilder()
									.append("the no more data packet buffer error occurred while reading the socket[")
									.append(accpetedConneciton.hashCode())
									.append("], errmsg=").append(e.getMessage()).toString();
							log.warn(errorMessage);

							accpetedConneciton.close();
							continue;
						} catch (IOException e) {
							String errorMessage = new StringBuilder()
									.append("the io error occurred while reading or writing the socket[")
									.append(accpetedConneciton.hashCode())
									.append("], errmsg=").append(e.getMessage()).toString();
							log.warn(errorMessage);

							accpetedConneciton.close();
							continue;
						} catch (Exception e) {
							String errorMessage = new StringBuilder()
									.append("the unknown error occurred while reading or writing the socket[")
									.append(accpetedConneciton.hashCode())
									.append("]").toString();
							log.warn(errorMessage, e);

							accpetedConneciton.close();
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
					.append(" ServerIOEventController unknown error, errmsg=")
					.append(e.getMessage()).toString();
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

	private void setupAcceptedSocketChannel(SocketChannel acceptedSocketChannel)
			throws Exception {
		acceptedSocketChannel.configureBlocking(false);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE,
				true);
		acceptedSocketChannel
				.setOption(StandardSocketOptions.TCP_NODELAY, true);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_LINGER, 0);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR,
				true);
	}

	@Override
	public void cancel(SelectionKey selectedKey) {
		if (null == selectedKey) {
			return;
		}
		
		
		selectedKey.cancel();
		selectedKey.attach(null);
	}

	public int getNumberOfAcceptedConnection() {		
		Set<SelectionKey> selectionKeySet = ioEventSelector.keys();		
		return selectionKeySet.size() - 1;
	}

	@Override
	public void registerloginUser(SelectionKey selectedKey, String loginID) {
		if (null == selectedKey) {
			throw new IllegalArgumentException(
					"the parameter selectedKey is null");
		}

		if (null == loginID) {
			throw new IllegalArgumentException("the parameter loginID is null");
		}

		// synchronized (loginMangerMonitor) {
		if (selectedKey2LonginIDHash.containsKey(selectedKey)) {
			log.warn(
					"the parameter selectedKey[{}] is the socket channel that is already registered",
					selectedKey.hashCode());
			return;
		}
		if (longinID2SelectedKeyHash.containsKey(loginID)) {
			log.warn(
					"the parameter loginID[{}] is the login id that is already registered",
					loginID);
			return;
		}

		selectedKey2LonginIDHash.put(selectedKey, loginID);
		longinID2SelectedKeyHash.put(loginID, selectedKey);
		// }

		log.info(
				"login register success, selectedKey={}, socketChannel={}, loginID={}",
				selectedKey.hashCode(), selectedKey.channel().hashCode(),
				loginID);
	}

	private void doRemoveLoginUser(SelectionKey selectedKey, String loginID) {
		selectedKey2LonginIDHash.remove(selectedKey);
		longinID2SelectedKeyHash.remove(loginID);
	}

	public void removeLoginUser(SelectionKey selectedKey) {
		if (null == selectedKey) {
			throw new IllegalArgumentException(
					"the parameter selectedKey is null");
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
}
