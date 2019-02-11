package kr.pe.codda.client.connection.asyn.noshare;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;

import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.client.classloader.ClientTaskMangerIF;
import kr.pe.codda.client.connection.ConnectionPoolIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporterIF;
import kr.pe.codda.client.connection.asyn.AsynConnectedConnectionAdderIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.client.connection.asyn.ClientIOEventControllerIF;
import kr.pe.codda.client.connection.asyn.ClientIOEventHandlerIF;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.ConnectionPoolTimeoutException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.ReceivedDataOnlyStream;
import kr.pe.codda.common.io.ReceivedDataOnlyStreamFactoryIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;

public class AsynNoShareConnectionPool implements ConnectionPoolIF, AsynConnectedConnectionAdderIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynNoShareConnectionPool.class);
	private final Object monitor = new Object();

	private String projectName = null;
	private String serverHost = null;
	private int serverPort  = 0;
	private long socketTimeout=0;
	private int clientConnectionCount = 0; 
	
	private int clientSyncMessageMailboxCountPerAsynShareConnection=0;
	private int clientAsynInputMessageQueueCapacity=0;
	
	private MessageProtocolIF messageProtocol = null; 
	private ClientTaskMangerIF clientTaskManger = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ReceivedDataOnlyStreamFactoryIF receivedDataOnlyStreamFactory = null;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;

	
	private ArrayDeque<AsynNoShareConnection> connectionQueue = null;
	private transient int numberOfConnection = 0;	
	private transient int numberOfUnregisteredConnections = 0;

	private ClientIOEventControllerIF asynClientIOEventController = null;

	public AsynNoShareConnectionPool(ProjectPartConfiguration projectPartConfiguration,
			MessageProtocolIF messageProtocol, 
			ClientTaskMangerIF clientTaskManger,
			DataPacketBufferPoolIF dataPacketBufferPool, ReceivedDataOnlyStreamFactoryIF receivedDataOnlyStreamFactory,
			ConnectionPoolSupporterIF connectionPoolSupporter,
			ClientIOEventControllerIF asynClientIOEventController)
			throws NoMoreDataPacketBufferException, IOException {
		if (null == projectPartConfiguration) {
			throw new IllegalArgumentException("the parameter projectPartConfiguration is null");
		}

		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}
		
		if (null == clientTaskManger) {
			throw new IllegalArgumentException("the parameter clientTaskManger is null");
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

		this.projectName = projectPartConfiguration.getProjectName();
		this.serverHost = projectPartConfiguration.getServerHost();
		this.serverPort = projectPartConfiguration.getServerPort();
		this.socketTimeout = projectPartConfiguration.getClientSocketTimeout();
		this.clientConnectionCount = projectPartConfiguration.getClientConnectionCount();
		this.clientSyncMessageMailboxCountPerAsynShareConnection = projectPartConfiguration.getClientSyncMessageMailboxCountPerAsynShareConnection();
		this.clientAsynInputMessageQueueCapacity = projectPartConfiguration.getClientAsynInputMessageQueueCapacity();
		
		this.messageProtocol = messageProtocol;
		this.clientTaskManger = clientTaskManger;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.receivedDataOnlyStreamFactory = receivedDataOnlyStreamFactory;
		this.connectionPoolSupporter = connectionPoolSupporter;
		this.asynClientIOEventController = asynClientIOEventController;

		connectionQueue = new ArrayDeque<AsynNoShareConnection>(projectPartConfiguration.getClientConnectionMaxCount());

		connectionPoolSupporter.registerPool(this);
	}


	@Override
	public ConnectionIF getConnection() throws InterruptedException, ConnectionPoolTimeoutException, ConnectionPoolException {
		AsynNoShareConnection asynNoShareConnection = null;
		boolean loop = false;

		long currentSocketTimeOut = socketTimeout;
		long startTime = System.nanoTime();

		synchronized (monitor) {
			do {				
				if (0 == numberOfConnection) {
					connectionPoolSupporter.notice("no more connection");
					throw new ConnectionPoolException("check server is alive or something is bad");
				}
				
				if (connectionQueue.isEmpty()) {					
					monitor.wait(currentSocketTimeOut);					
					
					if (connectionQueue.isEmpty()) {						
						throw new ConnectionPoolTimeoutException("1.asynchronized no-share connection pool timeout");
					}
				}
				
				asynNoShareConnection = connectionQueue.pollFirst();				
				
				if (asynNoShareConnection.isConnected()) {
					asynNoShareConnection.queueOut();
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
							.append(asynNoShareConnection.hashCode()).append("]이 닫혀있어 폐기").toString();

					numberOfConnection--;

					log.warn("{}, numberOfConnection[{}]", reasonForLoss, numberOfConnection);

					connectionPoolSupporter.notice(reasonForLoss);
					
					long elapsedTime = TimeUnit.MICROSECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS);
					
					currentSocketTimeOut -= elapsedTime;
					
					if (currentSocketTimeOut <= 0) {
						throw new ConnectionPoolTimeoutException("2.asynchronized no-share connection pool timeout");
					}
				}

			} while (loop);
		}
		
		/*long endTime = System.nanoTime();
		log.info("getConnection::elasped {} microseconds, connectionQueue.size={}",
				TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS),
				connectionQueue.size());*/

		return asynNoShareConnection;
	}

	@Override
	public void release(ConnectionIF conn) throws ConnectionPoolException {
		if (null == conn) {
			String errorMessage = "the parameter conn is null";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		if (!(conn instanceof AsynNoShareConnection)) {
			String errorMessage = "the parameter conn is not instace of AsynNoShareConnection class";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		AsynNoShareConnection asynNoShareConnection = (AsynNoShareConnection) conn;
	
		synchronized (monitor) {
			
			/**
			 * 연속 2회 큐 입력 방지
			 */
			if (asynNoShareConnection.isInQueue()) {
				String errorMessage = new StringBuilder()
				.append("the paramter conn[")
				.append(conn.hashCode())
				.append("] allready was in connection queue").toString();
				log.warn(errorMessage, new Throwable());
				throw new ConnectionPoolException(errorMessage);
			}

			/**
			 * 큐에 넣어진 상태로 변경
			 */
			asynNoShareConnection.queueIn();

			if (! asynNoShareConnection.isConnected()) {
				/**
				 * <pre>
				 * 반환된 연결이 닫힌 경우 폐기한다. 단  이러한 작업은 큐에 넣어진 상태에서 이루어 져야 한다.
				 * 왜냐하면 연결은 참조 포인트가 없어 gc 될때 큐에 넣어진 상태가 아니면 로그를 남기는데
				 * 정상적인 반환이므로 gc 될때 로그를 남기지 말아야 하기때문이다.  
				 * </pre>   
				 */
				numberOfConnection--;

				String reasonForLoss = new StringBuilder("반환된 연결[")
						.append(asynNoShareConnection.hashCode())
						.append("]이 닫혀있어 폐기").toString();

				log.warn("{}, numberOfConnection[{}]", reasonForLoss, numberOfConnection);

				connectionPoolSupporter.notice(reasonForLoss);
				return;
			}

			connectionQueue.addLast(asynNoShareConnection);
			
			monitor.notify();
		}
		
	}
	
	public void fillAllConnection() throws NoMoreDataPacketBufferException, IOException, InterruptedException {
		/*
		log.info("numberOfUnregisteredConnection={}, numberOfConnection={}," +
				"clientConnectionCount={}",
				numberOfUnregisteredConnection,
				numberOfConnection,
				clientConnectionCount);*/
		
		synchronized (monitor) {				
			while ((numberOfUnregisteredConnections
					+ numberOfConnection) < clientConnectionCount) {				
				
				ClientIOEventHandlerIF unregisteredAsynConnection = newUnregisteredConnection();
				numberOfUnregisteredConnections++;
				asynClientIOEventController.addUnregisteredAsynConnection(unregisteredAsynConnection);
			}
		}
		
		asynClientIOEventController.wakeup();
	}
	
	@Override
	public void subtractOneFromNumberOfUnregisteredConnections(AsynConnectionIF unregisteredAsynConnection) {
		synchronized (monitor) {
			numberOfUnregisteredConnections--;
		}
		
		log.info("subtract one[unregistered connection={}] from the number of unregistered connection", unregisteredAsynConnection.hashCode());
	}

	private ClientIOEventHandlerIF newUnregisteredConnection() throws NoMoreDataPacketBufferException, IOException {
		ReceivedDataOnlyStream sos = receivedDataOnlyStreamFactory.createReceivedDataOnlyStream();

		ClientIOEventHandlerIF asynInterestedConnection = new AsynNoShareConnection(projectName,
				serverHost,
				serverPort,
				socketTimeout,
				clientSyncMessageMailboxCountPerAsynShareConnection,
				clientAsynInputMessageQueueCapacity,
				sos,
				messageProtocol, dataPacketBufferPool, clientTaskManger, this, 
				asynClientIOEventController);
		return asynInterestedConnection;
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
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection) {
		/*if (0 == numberOfUnregisteredConnection) {
			throw new ConnectionPoolException(
					"fail to add a connection because the var numberOfInterrestedConnection is zero");
		}*/
		
		// long startTime = System.nanoTime();

		synchronized (monitor) {
			connectionQueue.addLast((AsynNoShareConnection)connectedAsynConnection);
			numberOfConnection++;
			numberOfUnregisteredConnections--;
			monitor.notify();
		}
		
		/*long endTime = System.nanoTime();
		
		log.info("Successfully added the connected connection[{}] to this connection pool, errasped {} micoseconds",
				connectedAsynConnection.hashCode(), 
				TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));*/
	}
}
