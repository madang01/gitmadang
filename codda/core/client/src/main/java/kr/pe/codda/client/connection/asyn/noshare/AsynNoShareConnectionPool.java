package kr.pe.codda.client.connection.asyn.noshare;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.client.connection.ClientMessageUtilityIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporterIF;
import kr.pe.codda.client.connection.asyn.AsynConnectedConnectionAdderIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionPoolIF;
import kr.pe.codda.client.connection.asyn.AsynClientIOEventControllerIF;
import kr.pe.codda.client.connection.asyn.ClientInterestedConnectionIF;
import kr.pe.codda.client.connection.asyn.executor.ClientExecutorIF;
import kr.pe.codda.client.connection.asyn.executor.ClientExecutorPoolIF;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.SocketOutputStreamFactoryIF;

public class AsynNoShareConnectionPool implements AsynConnectionPoolIF, AsynConnectedConnectionAdderIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynNoShareConnectionPool.class);
	private final Object monitor = new Object();
	
	private ProjectPartConfiguration projectPartConfiguration = null;	
	private ClientMessageUtilityIF clientMessageUtility = null;
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;
	
	private ClientExecutorPoolIF clientExecutorPool = null;
	
	
	private ArrayDeque<AsynNoShareConnection> connectionQueue = null;
	private transient int numberOfConnection = 0;
	private transient int numberOfUnregisteredConnection = 0;
	
	private AsynClientIOEventControllerIF asynSelectorManger = null;
	
	public AsynNoShareConnectionPool(ProjectPartConfiguration projectPartConfiguration, 
			ClientMessageUtilityIF clientMessageUtility,
			SocketOutputStreamFactoryIF socketOutputStreamFactory,
			ConnectionPoolSupporterIF connectionPoolSupporter,
			ClientExecutorPoolIF clientExecutorPool) throws NoMoreDataPacketBufferException, IOException {
		if (null == projectPartConfiguration) {
			throw new IllegalArgumentException("the parameter projectPartConfiguration is null");
		}
		
		if (null == clientMessageUtility) {
			throw new IllegalArgumentException("the parameter clientMessageUtility is null");
		}
		
		if (null == socketOutputStreamFactory) {
			throw new IllegalArgumentException("the parameter socketOutputStreamFactory is null");
		}
		if (null == connectionPoolSupporter) {
			throw new IllegalArgumentException("the parameter connectionPoolSupporter is null");
		}
		
		
		if (null == clientExecutorPool) {
			throw new IllegalArgumentException("the parameter clientExecutorPool is null");
		}				
		
		this.projectPartConfiguration = projectPartConfiguration;
		this.clientMessageUtility = clientMessageUtility;
		this.socketOutputStreamFactory = socketOutputStreamFactory;
		this.connectionPoolSupporter = connectionPoolSupporter;
		this.clientExecutorPool = clientExecutorPool;				
		
		connectionQueue = new ArrayDeque<AsynNoShareConnection>(projectPartConfiguration.getClientConnectionMaxCount());
		
		connectionPoolSupporter.registerPool(this);		
		
		
		// numberOfInterrestedConnection = projectPartConfiguration.getClientConnectionCount();
		/*int numberOfConnection = projectPartConfiguration.getClientConnectionCount();
		
		for (int i = 0; i < numberOfConnection; i++) {
			addInterestedConnection();
		}*/
		
		
		
		/*projectPartConfiguration.getClientConnectionCount();
		projectPartConfiguration.getClientConnectionMaxCount();
		projectPartConfiguration.getClientSocketTimeout();*/
	}
	
	

	public void setAsynSelectorManger(AsynClientIOEventControllerIF asynIOEventManger) {
		this.asynSelectorManger = asynIOEventManger;
	}



	@Override
	public ConnectionIF getConnection() throws InterruptedException, SocketTimeoutException, ConnectionPoolException {		
		AsynNoShareConnection asynNoShareConnection = null;
		boolean loop = false;
		
		long currentSocketTimeOut = projectPartConfiguration.getClientSocketTimeout();
		long startTime = System.currentTimeMillis();

		synchronized (monitor) {
			do {
				if (0 == numberOfConnection) {						
					connectionPoolSupporter.notice("no more connection");
					throw new ConnectionPoolException("check server is alive or something is bad");
				}
				
				asynNoShareConnection = connectionQueue.pollFirst();
				
				if (null == asynNoShareConnection) {
					monitor.wait(currentSocketTimeOut);
					asynNoShareConnection = connectionQueue.pollFirst();
					if (null == asynNoShareConnection) {
						throw new SocketTimeoutException("1.asynchronized no-share connection pool timeout");
					}
				}

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
					
					currentSocketTimeOut -= (System.currentTimeMillis() - startTime);
					if (currentSocketTimeOut <= 0) {
						throw new SocketTimeoutException("2.synchronized private connection pool timeout");
					}
				}

			} while (loop);
		}

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
				String errorMessage = String.format("the paramter conn[%d] allready was in connection queue",
						conn.hashCode());
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

	@Override
	public boolean isConnectionToAdd() {
		// FIXME!
		/*log.info("numberOfUnregisteredConnection={}, numberOfConnection={}, number of config's connection={}", 
				numberOfUnregisteredConnection, numberOfConnection, projectPartConfiguration.getClientConnectionCount());*/
		boolean isInterestedConnection = ((numberOfUnregisteredConnection + numberOfConnection)  < projectPartConfiguration.getClientConnectionCount());
		return isInterestedConnection;
	}	

	@Override
	public void removeUnregisteredConnection(ClientInterestedConnectionIF asynInterestedConnection) {
		numberOfUnregisteredConnection--;
		log.info("remove the interedted connection[{}]", asynInterestedConnection.hashCode());
	}

	
	public ClientInterestedConnectionIF newUnregisteredConnection() throws NoMoreDataPacketBufferException, IOException {
		SocketOutputStream sos = socketOutputStreamFactory.newInstance();
		ClientExecutorIF clientExecutor = clientExecutorPool.getClientExecutorWithMinimumNumberOfConnetion();
		
		ClientInterestedConnectionIF asynInterestedConnection = 
				new AsynNoShareConnection(projectPartConfiguration, sos, clientMessageUtility, this, clientExecutor, asynSelectorManger);		
		return asynInterestedConnection;
	}
	
	public void addCountOfUnregisteredConnection() {
		numberOfUnregisteredConnection++;
	}
		
	public void addConnection() throws NoMoreDataPacketBufferException, IOException {
		ClientInterestedConnectionIF unregisteredAsynConnection = newUnregisteredConnection();
		addCountOfUnregisteredConnection();
		asynSelectorManger.addUnregisteredAsynConnection(unregisteredAsynConnection);
	}
	

	@Override
	public String getPoolState() {
		return new StringBuilder()
				.append("numberOfConnection=").append(numberOfConnection)
				.append(", numberOfInterrestedConnection=").append(numberOfUnregisteredConnection)
				.append(", connectionQueue.size=").append(connectionQueue.size()).toString();
	}


	@Override
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection) throws ConnectionPoolException {
		if (0 == numberOfUnregisteredConnection) {
			throw new ConnectionPoolException("fail to add a connection because the var numberOfInterrestedConnection is zero");
		}
		
		synchronized (monitor) {
			if (numberOfConnection >= projectPartConfiguration.getClientConnectionMaxCount()) {
				throw new ConnectionPoolException("fail to add a connection because this connection pool's size is max");
			}
			
			AsynNoShareConnection asynNoShareConnection = (AsynNoShareConnection)connectedAsynConnection;
			
			numberOfUnregisteredConnection--;
			numberOfConnection++;			
			connectionQueue.addLast(asynNoShareConnection);
			
			monitor.notify();
		}		
		
		// FIXME!
		log.info("adding a connected connection[{}] to this connection pool success", connectedAsynConnection.hashCode());
	}


	@Override
	public void removeInterestedConnection(AsynConnectionIF interestedAsynConnection) {
		numberOfUnregisteredConnection--;
		log.info("remove the interedted connection[{}]", interestedAsynConnection.hashCode());
	}

}
