package kr.pe.codda.client.connection.sync;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.client.connection.ClientMessageUtilityIF;
import kr.pe.codda.client.connection.ConnectionPoolIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporterIF;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.SocketOutputStreamFactoryIF;

public class SyncNoShareConnectionPool implements ConnectionPoolIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(SyncNoShareConnectionPool.class);
	
	private final Object monitor = new Object();
	
	private ProjectPartConfiguration projectPartConfiguration = null;	
	private ClientMessageUtilityIF clientMessageUtility = null;
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;
	
	private ArrayDeque<SyncNoShareConnection> connectionQueue = null;
	private transient int numberOfConnection = 0;
	
	public SyncNoShareConnectionPool(ProjectPartConfiguration projectPartConfiguration, 
			ClientMessageUtilityIF clientMessageUtility,
			SocketOutputStreamFactoryIF socketOutputStreamFactory,
			ConnectionPoolSupporterIF connectionPoolSupporter) throws NoMoreDataPacketBufferException, IOException {
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
				
		
		this.projectPartConfiguration = projectPartConfiguration;
		this.clientMessageUtility = clientMessageUtility;
		this.socketOutputStreamFactory = socketOutputStreamFactory;
		this.connectionPoolSupporter = connectionPoolSupporter;
				
		
		connectionQueue = new ArrayDeque<SyncNoShareConnection>(projectPartConfiguration.getClientConnectionMaxCount());
		
		for (int i=0; i < projectPartConfiguration.getClientConnectionCount(); i++) {
			addConnection();
		}		
		
		connectionPoolSupporter.registerPool(this);
	}

	@Override
	public ConnectionIF getConnection() throws InterruptedException, SocketTimeoutException, ConnectionPoolException {
		 SyncNoShareConnection asynNoShareConnection = null;
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
	public boolean isConnectionToAdd() {
		boolean isInterestedConnection = (numberOfConnection  < projectPartConfiguration.getClientConnectionCount());
		return isInterestedConnection;
	}

	@Override
	public void addConnection() throws NoMoreDataPacketBufferException, IOException {
		SocketOutputStream sos = socketOutputStreamFactory.newInstance();
		SyncNoShareConnection syncNoShareConnection = null;
		try {
			syncNoShareConnection = new SyncNoShareConnection(projectPartConfiguration.getServerHost(),
					projectPartConfiguration.getServerPort(),
					projectPartConfiguration.getClientSocketTimeout(),
					projectPartConfiguration.getClientDataPacketBufferSize(),
					sos, clientMessageUtility);
		} catch(Exception e) {
			sos.close();
			throw e;
		}
		
		log.info("the connection[{}] has been connected", syncNoShareConnection.hashCode());
		
		synchronized (monitor) {
			connectionQueue.addLast(syncNoShareConnection);
			numberOfConnection++;
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

}
