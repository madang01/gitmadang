package kr.pe.codda.client.connection.sync;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.util.ArrayDeque;

import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.client.connection.ConnectionPoolIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporterIF;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.ConnectionPoolTimeoutException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.ReceivedDataOnlyStream;
import kr.pe.codda.common.io.ReceivedDataOnlyStreamFactoryIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;

public class SyncNoShareConnectionPool implements ConnectionPoolIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(SyncNoShareConnectionPool.class);
	
	private final Object monitor = new Object();
	
	//private ProjectPartConfiguration projectPartConfiguration = null;	
	private String serverHost = null;
	private int serverPort  = 0;
	private long socketTimeout=0;
	private int clientConnectionCount = 0; 
	@SuppressWarnings("unused")
	private int clientConnectionMaxCount = 0;
	private int clientDataPacketBufferSize = 0;
	
	private MessageProtocolIF messageProtocol = null; 
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	
	private ReceivedDataOnlyStreamFactoryIF receivedDataOnlyStreamFactory = null;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;
	
	private ArrayDeque<SyncNoShareConnection> connectionQueue = null;
	private int numberOfConnection = 0;
	
	public SyncNoShareConnectionPool(ProjectPartConfiguration projectPartConfiguration, 
			MessageProtocolIF messageProtocol, 
			DataPacketBufferPoolIF dataPacketBufferPool,
			ReceivedDataOnlyStreamFactoryIF receivedDataOnlyStreamFactory,
			ConnectionPoolSupporterIF connectionPoolSupporter) throws NoMoreDataPacketBufferException, IOException, ConnectionPoolException {
		if (null == projectPartConfiguration) {
			throw new IllegalArgumentException("the parameter projectPartConfiguration is null");
		}
		
		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}
				
		if (null == dataPacketBufferPool) {
			throw new IllegalArgumentException("the parameter dataPacketBufferPool is null");
		}
		
		if (null == receivedDataOnlyStreamFactory) {
			throw new IllegalArgumentException("the parameter receivedDataOnlyStreamFactory is null");
		}
		if (null == connectionPoolSupporter) {
			throw new IllegalArgumentException("the parameter connectionPoolSupporter is null");
		}
				
		
		this.serverHost = projectPartConfiguration.getServerHost();
		this.serverPort = projectPartConfiguration.getServerPort();
		this.socketTimeout = projectPartConfiguration.getClientSocketTimeout();
		this.clientConnectionCount = projectPartConfiguration.getClientConnectionCount();
		this.clientConnectionMaxCount =  projectPartConfiguration.getClientConnectionMaxCount();
		this.clientDataPacketBufferSize = projectPartConfiguration.getClientDataPacketBufferSize();
		
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.receivedDataOnlyStreamFactory = receivedDataOnlyStreamFactory;
		this.connectionPoolSupporter = connectionPoolSupporter;
				
		
		connectionQueue = new ArrayDeque<SyncNoShareConnection>(projectPartConfiguration.getClientConnectionMaxCount());
		
		try {			
			fillAllConnection();
		} catch(NoMoreDataPacketBufferException e) {
			log.warn("stops adding SyncNoShareConnection because a data packet buffer error has occurred");
			
		} catch(IOException e) {
			log.warn("stops adding SyncNoShareConnection because an I/O error has occurred", e);
		
			log.info("removes all of the SyncNoShareConnection from the queue because an I/O error has occurred");
			while (! connectionQueue.isEmpty()) {
				SyncNoShareConnection syncNoShareConnection = connectionQueue.removeFirst();
				syncNoShareConnection.close();
				
				log.info("removes the SyncNoShareConnection[{}] from the queue because an I/O error has occurred", syncNoShareConnection.hashCode());
			}
			numberOfConnection = 0;
		} catch(Exception e) {
			log.warn("stops adding SyncNoShareConnection because an unknown error has occurred", e);
			
			log.info("removes all of the SyncNoShareConnection from the queue because an I/O error has occurred");
			while (! connectionQueue.isEmpty()) {
				SyncNoShareConnection syncNoShareConnection = connectionQueue.removeFirst();
				syncNoShareConnection.close();
				
				log.info("removes the SyncNoShareConnection[{}] from the queue because an I/O error has occurred", syncNoShareConnection.hashCode());
			}
			numberOfConnection = 0;
		}
		
		connectionPoolSupporter.registerPool(this);
	}

	@Override
	public ConnectionIF getConnection() throws InterruptedException, ConnectionPoolTimeoutException, ConnectionPoolException {
		SyncNoShareConnection syncNoShareConnection = null;
		boolean loop = false;
		
		long currentSocketTimeOut = socketTimeout;
		long startTime = System.currentTimeMillis();

		synchronized (monitor) {
			do {
				if (0 == numberOfConnection) {
					connectionPoolSupporter.notice("no more connection");
					throw new ConnectionPoolException("check server is alive or something is bad");
				}
				
				if (connectionQueue.isEmpty()) {					
					monitor.wait(currentSocketTimeOut);					
					
					if (connectionQueue.isEmpty()) {						
						throw new ConnectionPoolTimeoutException("1.synchronized no-share connection pool timeout");
					}
				}	
				
				syncNoShareConnection = connectionQueue.pollFirst();				

				if (syncNoShareConnection.isConnected()) {
					syncNoShareConnection.queueOut();
					loop = false;
				} else {
					loop = true;					

					/**
					 * <pre>
					 * 폴에서 꺼낸 연결이 닫힌 경우 폐기한다. 단  이러한 작업은 큐에 넣어진 상태에서 이루어 져야 한다.
					 * 왜냐하면 연결은 참조 포인트가 없어 gc 될때 큐에 넣어진 상태가 아니면 로그를 남기는데
					 * 사용자 한테 넘기기전 폐기이므로 gc 될때 로그를 남기지 말아야 하기때문이다.  
					 * </pre>   
					 */
					String reasonForLoss = new StringBuilder("폴에서 꺼낸 연결[")							
							.append(syncNoShareConnection.hashCode()).append("]이 닫혀있어 폐기").toString();

					numberOfConnection--;

					log.warn("{}, numberOfConnection[{}]", reasonForLoss, numberOfConnection);

					connectionPoolSupporter.notice(reasonForLoss);
					
					currentSocketTimeOut -= (System.currentTimeMillis() - startTime);
					if (currentSocketTimeOut <= 0) {
						throw new ConnectionPoolTimeoutException("2.synchronized no-share connection pool timeout");
					}
				}

			} while (loop);
		}

		return syncNoShareConnection;
	}

	@Override
	public void release(ConnectionIF conn) throws ConnectionPoolException {
		if (null == conn) {
			String errorMessage = "the parameter conn is null";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		if (!(conn instanceof SyncNoShareConnection)) {
			String errorMessage = "the parameter conn is not instace of SyncNoShareConnection class";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		SyncNoShareConnection syncNoShareConnection = (SyncNoShareConnection) conn;

		synchronized (monitor) {
			/**
			 * 연속 2회 큐 입력 방지
			 */
			if (syncNoShareConnection.isInQueue()) {
				String errorMessage = String.format("the paramter conn[%d] allready was in connection queue",
						conn.hashCode());
				log.warn(errorMessage, new Throwable());
				throw new ConnectionPoolException(errorMessage);
			}

			/**
			 * 큐에 넣어진 상태로 변경
			 */
			syncNoShareConnection.queueIn();

			if (! syncNoShareConnection.isConnected()) {
				/**
				 * <pre>
				 * 반환된 연결이 닫힌 경우 폐기한다. 단  이러한 작업은 큐에 넣어진 상태에서 이루어 져야 한다.
				 * 왜냐하면 연결은 참조 포인트가 없어 gc 될때 큐에 넣어진 상태가 아니면 로그를 남기는데
				 * 정상적인 반환이므로 gc 될때 로그를 남기지 말아야 하기때문이다.  
				 * </pre>   
				 */
				numberOfConnection--;

				String reasonForLoss = new StringBuilder("반환된 연결[")
						.append(syncNoShareConnection.hashCode())
						.append("]이 닫혀있어 폐기").toString();

				log.warn("{}, numberOfConnection[{}]", reasonForLoss, numberOfConnection);

				connectionPoolSupporter.notice(reasonForLoss);
				return;
			}

			connectionQueue.addLast(syncNoShareConnection);
			monitor.notify();
		}
		
	}

	


	@Override
	public String getPoolState() {
		return new StringBuilder()
				.append("numberOfConnection=")
				.append(numberOfConnection)
				.append(", connectionQueue.size=")
				.append(connectionQueue.size()).toString();
	}

	@Override
	public void fillAllConnection() throws NoMoreDataPacketBufferException, IOException, InterruptedException {
		synchronized (monitor) {
			while (numberOfConnection  < clientConnectionCount) {				
				ReceivedDataOnlyStream receivedDataOnlyStream = receivedDataOnlyStreamFactory.createReceivedDataOnlyStream();
				
				SyncNoShareConnection syncNoShareConnection = new SyncNoShareConnection(serverHost,
							serverPort,
							socketTimeout,
							clientDataPacketBufferSize,
							receivedDataOnlyStream, messageProtocol, dataPacketBufferPool);
						
				
				log.info("the SyncNoShareConnection[{}] has been connected", syncNoShareConnection.hashCode());
				
				
				connectionQueue.addLast(syncNoShareConnection);
				numberOfConnection++;
			}
		}
		
	}
}
