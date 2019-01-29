package kr.pe.codda.client.connection.asyn.share;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.client.classloader.ClientTaskMangerIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporterIF;
import kr.pe.codda.client.connection.asyn.AsynConnectedConnectionAdderIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionPoolIF;
import kr.pe.codda.client.connection.asyn.ClientIOEventControllerIF;
import kr.pe.codda.client.connection.asyn.ClientIOEventHandlerIF;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.SocketOutputStreamFactoryIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;

public class AsynShareConnectionPool implements AsynConnectionPoolIF, AsynConnectedConnectionAdderIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynShareConnectionPool.class);
	private final Object monitor = new Object();

	// private ProjectPartConfiguration projectPartConfiguration = null;
	private String projectName = null;
	private String serverHost = null;
	private int serverPort  = 0;
	private long socketTimeout=0;
	private int clientConnectionCount = 0; 
	private int clientConnectionMaxCount = 0;
	private int clientSyncMessageMailboxCountPerAsynShareConnection=0;
	private int clientAsynInputMessageQueueCapacity=0;
	
	private MessageProtocolIF messageProtocol = null; 
	private ClientTaskMangerIF clientTaskManger = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;

	private ArrayList<AsynShareConnection> connectionList = null;

	private int index = -1;
	private int numberOfUnregisteredConnection = 0;

	private ClientIOEventControllerIF asynClientIOEventController = null;

	public AsynShareConnectionPool(ProjectPartConfiguration projectPartConfiguration,
			MessageProtocolIF messageProtocol, 
			ClientTaskMangerIF clientTaskManger,
			DataPacketBufferPoolIF dataPacketBufferPool, SocketOutputStreamFactoryIF socketOutputStreamFactory,
			ConnectionPoolSupporterIF connectionPoolSupporter)
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

		if (null == socketOutputStreamFactory) {
			throw new IllegalArgumentException("the parameter socketOutputStreamFactory is null");
		}
		if (null == connectionPoolSupporter) {
			throw new IllegalArgumentException("the parameter connectionPoolSupporter is null");
		}

		this.projectName = projectPartConfiguration.getProjectName();
		this.serverHost = projectPartConfiguration.getServerHost();
		this.serverPort = projectPartConfiguration.getServerPort();
		this.socketTimeout = projectPartConfiguration.getClientSocketTimeout();
		this.clientConnectionCount = projectPartConfiguration.getClientConnectionCount();
		this.clientConnectionMaxCount =  projectPartConfiguration.getClientConnectionMaxCount();
		this.clientSyncMessageMailboxCountPerAsynShareConnection = projectPartConfiguration.getClientSyncMessageMailboxCountPerAsynShareConnection();
		this.clientAsynInputMessageQueueCapacity = projectPartConfiguration.getClientAsynInputMessageQueueCapacity();
		
		this.messageProtocol = messageProtocol;
		this.clientTaskManger = clientTaskManger;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.socketOutputStreamFactory = socketOutputStreamFactory;
		this.connectionPoolSupporter = connectionPoolSupporter;

		connectionList = new ArrayList<AsynShareConnection>(clientConnectionMaxCount);

		connectionPoolSupporter.registerPool(this);
	}

	public void setAsynSelectorManger(ClientIOEventControllerIF asynClientIOEventController) {
		this.asynClientIOEventController = asynClientIOEventController;
	}

	@Override
	public ConnectionIF getConnection() throws InterruptedException, SocketTimeoutException, ConnectionPoolException {
		AsynShareConnection asynShareConnection = null;
		boolean loop = false;

		long currentSocketTimeOut = socketTimeout;
		long startTime = System.currentTimeMillis();

		synchronized (monitor) {
			do {
				if (0 == connectionList.size()) {
					connectionPoolSupporter.notice("no more connection");
					throw new ConnectionPoolException("check server is alive or something is bad");
				}

				index = (index + 1) % connectionList.size();

				asynShareConnection = connectionList.get(index);

				if (asynShareConnection.isConnected()) {
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
					String reasonForLoss = new StringBuilder("목록에서 꺼낸 연결[").append(asynShareConnection.hashCode())
							.append("]이 닫혀있어 폐기").toString();

					connectionList.remove(index);

					log.warn("{}, numberOfConnection[{}]", reasonForLoss, connectionList.size());

					connectionPoolSupporter.notice(reasonForLoss);

					currentSocketTimeOut -= (System.currentTimeMillis() - startTime);
					if (currentSocketTimeOut <= 0) {
						throw new SocketTimeoutException("2.synchronized private connection pool timeout");
					}
				}

			} while (loop);
		}

		return asynShareConnection;
	}

	@Override
	public void release(ConnectionIF conn) throws ConnectionPoolException {
		if (null == conn) {
			String errorMessage = "the parameter conn is null";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		if (!(conn instanceof AsynShareConnection)) {
			String errorMessage = "the parameter conn is not instace of AsynShareConnection class";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		AsynShareConnection asynPrivateConnection = (AsynShareConnection) conn;

		if (!asynPrivateConnection.isConnected()) {
			synchronized (monitor) {
				connectionList.remove(asynPrivateConnection);
			}

			String reasonForLoss = new StringBuilder("반환된 연결[").append(asynPrivateConnection.hashCode())
					.append("]이 닫혀있어 폐기").toString();

			log.warn("{}, numberOfConnection[{}]", reasonForLoss, connectionList.size());

			connectionPoolSupporter.notice(reasonForLoss);
			return;

		}

	}
	
	public void addAllLostConnection() throws NoMoreDataPacketBufferException, IOException, InterruptedException {
		while (isConnectionToAdd()) {				
			addConnection();
		}
		
		asynClientIOEventController.wakeup();
	}
	
	public boolean isConnectionToAdd() {
		boolean isInterestedConnection  = false;
		synchronized (monitor) {
			isInterestedConnection = ((numberOfUnregisteredConnection
					+ connectionList.size()) < clientConnectionCount);
		}		
		return isInterestedConnection;
	}

	public void addConnection() throws NoMoreDataPacketBufferException, IOException {
		ClientIOEventHandlerIF unregisteredAsynConnection = newUnregisteredConnection();
		addCountOfUnregisteredConnection();
		asynClientIOEventController.addUnregisteredAsynConnection(unregisteredAsynConnection);
	}

	private void addCountOfUnregisteredConnection() {
		synchronized (monitor) {
			numberOfUnregisteredConnection++;
		}		
	}

	@Override
	public void removeUnregisteredConnection(ClientIOEventHandlerIF asynInterestedConnection) {
		synchronized (monitor) {
			numberOfUnregisteredConnection--;
		}
		
		
		log.info("remove the interedted connection[{}]", asynInterestedConnection.hashCode());
	}

	private ClientIOEventHandlerIF newUnregisteredConnection() throws NoMoreDataPacketBufferException, IOException {
		SocketOutputStream sos = socketOutputStreamFactory.createSocketOutputStream();

		ClientIOEventHandlerIF asynInterestedConnection = new AsynShareConnection(projectName,
				serverHost,
				serverPort,
				socketTimeout,
				clientSyncMessageMailboxCountPerAsynShareConnection,
				clientAsynInputMessageQueueCapacity,
				sos,
				messageProtocol, dataPacketBufferPool, clientTaskManger, this, 
				asynClientIOEventController, connectionPoolSupporter);
		return asynInterestedConnection;
	}

	@Override
	public String getPoolState() {
		return new StringBuilder().append("numberOfConnection=").append(connectionList.size())
				.append(", numberOfInterrestedConnection=").append(numberOfUnregisteredConnection)
				.append(", connectionQueue.size=").append(connectionList.size()).toString();
	}

	@Override
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection) {
		/*if (0 == numberOfUnregisteredConnection) {
			throw new ConnectionPoolException(
					"fail to add a connection because the var numberOfInterrestedConnection is zero");
		}*/

		synchronized (monitor) {
			/*if (connectionList.size() >= clientConnectionMaxCount) {
				throw new ConnectionPoolException(
						"fail to add a connection because this connection pool's size is max");
			}*/

			AsynShareConnection asynShareConnection = (AsynShareConnection) connectedAsynConnection;
			numberOfUnregisteredConnection--;
			connectionList.add(asynShareConnection);

			monitor.notify();
		}
		
		log.info("Successfully added the connected connection[{}] to this connection pool",
				connectedAsynConnection.hashCode());
	}

	@Override
	public void removeInterestedConnection(AsynConnectionIF interestedAsynConnection) {
		synchronized (monitor) {
			numberOfUnregisteredConnection--;
		}
		
		log.info("remove the interedted connection[{}]", interestedAsynConnection.hashCode());
	}

	
}
