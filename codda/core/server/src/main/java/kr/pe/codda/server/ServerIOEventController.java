package kr.pe.codda.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class ServerIOEventController extends Thread implements ServerIOEvenetControllerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ServerIOEventController.class);

	private Selector ioEventSelector = null; // OP_ACCEPT 전용 selector
	private ServerSocketChannel ssc = null;
	private String projectName;
	private String serverHost;
	private int serverPort;
	private int maxClients;	
	private SocketResourceManagerIF socketResourceManager = null;

	public ServerIOEventController(String projectName, String serverHost, int serverPort, int maxClients) {
		this.projectName = projectName;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.maxClients = maxClients;
	}

	public void setSocketResourceManager(SocketResourceManagerIF socketResourceManager) {
		this.socketResourceManager = socketResourceManager;
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
		log.info("AcceptSelector::projectName[{}] start", projectName);

		initServerSocket();
				
		// LinkedBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue = new LinkedBlockingQueue<WrapReadableMiddleObject>();

		try {
			while (!Thread.currentThread().isInterrupted()) {
				int keyReady = ioEventSelector.select();
				if (keyReady > 0) {

					Set<SelectionKey> selectedKeySet = ioEventSelector.selectedKeys();

					try {
						for (SelectionKey selectedKey : selectedKeySet) {

							if (selectedKey.isAcceptable()) {
								long startTime = 0, endTime = 0;
								startTime = System.nanoTime();

								ServerSocketChannel readyChannel = (ServerSocketChannel) selectedKey.channel();

								SocketChannel acceptedSocketChannel = readyChannel.accept();

								if (null == acceptedSocketChannel) {
									log.warn("acceptedSocketChannel is null");
									continue;
								}

								int numberOfSocketResources = socketResourceManager.getNumberOfSocketResources();

								if (numberOfSocketResources < maxClients) {
									// log.info("accepted socket channel=[{}]", sc.hashCode());
									setupSocketChannel(acceptedSocketChannel);

									try {
										acceptedSocketChannel.register(ioEventSelector, SelectionKey.OP_READ);

										socketResourceManager.addNewAcceptedSocketChannel(acceptedSocketChannel);
									} catch (ClosedChannelException e) {
										log.warn(
												"fail to register this channel[{}] with the given selector having a the interest set OP_READ",
												acceptedSocketChannel.hashCode());

										continue;
									}

								} else {
									acceptedSocketChannel.setOption(StandardSocketOptions.SO_LINGER, 0);
									acceptedSocketChannel.close();
									log.warn("max clients[{}] researched so close the selected socket channel[{}]",
											maxClients, acceptedSocketChannel.hashCode());

								}

								endTime = System.nanoTime();

								long elaspedTime = endTime - startTime;
								if (elaspedTime > 1000000) {
									log.info("accept elasped time={}", TimeUnit.MICROSECONDS.convert(elaspedTime, TimeUnit.NANOSECONDS));
								}
								
							} else if (selectedKey.isReadable()) {
								SocketChannel readableSocketChannel = (SocketChannel) selectedKey.channel();

								SocketResource selectedSocketResource = socketResourceManager
										.getSocketResource(readableSocketChannel);

								if (null == selectedSocketResource) {
									log.warn("this reable socket channel[{}] has no resoruce",
											readableSocketChannel.hashCode());
									continue;
								}

								selectedSocketResource.onRead(selectedKey);
							} else if (selectedKey.isWritable()) {
								SocketChannel writableSocketChannel = (SocketChannel) selectedKey.channel();

								SocketResource selectedSocketResource = socketResourceManager
										.getSocketResource(writableSocketChannel);

								if (null == selectedSocketResource) {
									log.warn("this writable socket channel[{}] has no resoruce",
											writableSocketChannel.hashCode());
									continue;
								}

								selectedSocketResource.onWrite(selectedKey);
							}
						}
					} finally {
						selectedKeySet.clear();
					}
				}
			}
			log.warn("{} AcceptSelector loop exit", projectName);
		} catch (InterruptedException e) {
			log.warn("{} AcceptSelector stop", projectName);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append(projectName)
					.append(" AcceptSelector unknown error, errmsg=").append(e.getMessage()).toString();
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

	private void setupSocketChannel(SocketChannel acceptedSocketChannel) throws IOException {
		acceptedSocketChannel.configureBlocking(false);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		acceptedSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_LINGER, 0);
		acceptedSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
	}

	@Override
	public void startWrite(InterestedResoruceIF asynInterestedConnectionIF) {
		SelectionKey selectedKey = asynInterestedConnectionIF.keyFor(ioEventSelector);
		if (null == selectedKey) {
			log.error("selectedKey is null");
			System.exit(1);
		}
		
		// FIXME!
		// log.info("before interestOps={}", selectedKey.interestOps());
		selectedKey.interestOps(selectedKey.interestOps() | SelectionKey.OP_WRITE);
		// log.info("after interestOps={}", selectedKey.interestOps());
		
		ioEventSelector.wakeup();
	}
	
	@Override
	public void endWrite(InterestedResoruceIF asynInterestedConnectionIF) {
		SelectionKey selectedKey = asynInterestedConnectionIF.keyFor(ioEventSelector);
		if (null == selectedKey) {
			log.error("selectedKey is null");
			System.exit(1);
		}
		selectedKey.interestOps(selectedKey.interestOps() & ~SelectionKey.OP_WRITE);
		ioEventSelector.wakeup();
	}
}
