package kr.pe.codda.client.connection.asyn.share;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.client.connection.ClientMessageUtilityIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporterIF;
import kr.pe.codda.client.connection.asyn.AsynConnectedConnectionAdderIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionPoolIF;
import kr.pe.codda.client.connection.asyn.AsynClientIOEventControllerIF;
import kr.pe.codda.client.connection.asyn.InterestedAsynConnectionIF;
import kr.pe.codda.client.connection.asyn.executor.ClientExecutorIF;
import kr.pe.codda.client.connection.asyn.executor.ClientExecutorPoolIF;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.SocketOutputStreamFactoryIF;

public class AsynShareConnectionPool implements AsynConnectionPoolIF, AsynConnectedConnectionAdderIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynShareConnectionPool.class);
	private final Object monitor = new Object();

	private ProjectPartConfiguration projectPartConfiguration = null;
	private ClientMessageUtilityIF clientMessageUtility = null;
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;

	private ClientExecutorPoolIF clientExecutorPool = null;

	private ArrayList<AsynShareConnection> connectionList = null;

	private transient int index = -1;
	private transient int numberOfUnregisteredConnection = 0;

	private AsynClientIOEventControllerIF asynSelectorManger = null;

	public AsynShareConnectionPool(ProjectPartConfiguration projectPartConfiguration,
			ClientMessageUtilityIF clientMessageUtility, SocketOutputStreamFactoryIF socketOutputStreamFactory,
			ConnectionPoolSupporterIF connectionPoolSupporter, ClientExecutorPoolIF clientExecutorPool)
			throws NoMoreDataPacketBufferException, IOException {
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

		connectionList = new ArrayList<AsynShareConnection>(projectPartConfiguration.getClientConnectionMaxCount());

		connectionPoolSupporter.registerPool(this);

		// numberOfInterrestedConnection =
		// projectPartConfiguration.getClientConnectionCount();
		/*
		 * int numberOfConnection = projectPartConfiguration.getClientConnectionCount();
		 * 
		 * for (int i = 0; i < numberOfConnection; i++) { addInterestedConnection(); }
		 */

		/*
		 * projectPartConfiguration.getClientConnectionCount();
		 * projectPartConfiguration.getClientConnectionMaxCount();
		 * projectPartConfiguration.getClientSocketTimeout();
		 */
	}

	public void setAsynSelectorManger(AsynClientIOEventControllerIF asynIOEventManger) {
		this.asynSelectorManger = asynIOEventManger;
	}

	@Override
	public ConnectionIF getConnection() throws InterruptedException, SocketTimeoutException, ConnectionPoolException {
		AsynShareConnection asynShareConnection = null;
		boolean loop = false;

		long currentSocketTimeOut = projectPartConfiguration.getClientSocketTimeout();
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

	@Override
	public boolean isConnectionToAdd() {
		// FIXME!
		/*
		 * log.
		 * info("numberOfUnregisteredConnection={}, numberOfConnection={}, number of config's connection={}"
		 * , numberOfUnregisteredConnection, numberOfConnection,
		 * projectPartConfiguration.getClientConnectionCount());
		 */
		boolean isInterestedConnection = ((numberOfUnregisteredConnection
				+ connectionList.size()) < projectPartConfiguration.getClientConnectionCount());
		return isInterestedConnection;
	}

	@Override
	public void removeUnregisteredConnection(InterestedAsynConnectionIF asynInterestedConnection) {
		numberOfUnregisteredConnection--;
		log.info("remove the interedted connection[{}]", asynInterestedConnection.hashCode());
	}

	public InterestedAsynConnectionIF newUnregisteredConnection() throws NoMoreDataPacketBufferException, IOException {
		SocketOutputStream sos = socketOutputStreamFactory.newInstance();
		ClientExecutorIF clientExecutor = clientExecutorPool.getClientExecutorWithMinimumNumberOfConnetion();

		InterestedAsynConnectionIF asynInterestedConnection = new AsynShareConnection(projectPartConfiguration, sos,
				clientMessageUtility, this, clientExecutor, asynSelectorManger);
		return asynInterestedConnection;
	}

	public void addCountOfUnregisteredConnection() {
		numberOfUnregisteredConnection++;
	}

	public void addConnection() throws NoMoreDataPacketBufferException, IOException {
		InterestedAsynConnectionIF unregisteredAsynConnection = newUnregisteredConnection();
		addCountOfUnregisteredConnection();
		asynSelectorManger.addUnregisteredAsynConnection(unregisteredAsynConnection);
	}

	@Override
	public String getPoolState() {
		return new StringBuilder().append("numberOfConnection=").append(connectionList.size())
				.append(", numberOfInterrestedConnection=").append(numberOfUnregisteredConnection)
				.append(", connectionQueue.size=").append(connectionList.size()).toString();
	}

	@Override
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection) throws ConnectionPoolException {
		if (0 == numberOfUnregisteredConnection) {
			throw new ConnectionPoolException(
					"fail to add a connection because the var numberOfInterrestedConnection is zero");
		}

		synchronized (monitor) {
			if (connectionList.size() >= projectPartConfiguration.getClientConnectionMaxCount()) {
				throw new ConnectionPoolException(
						"fail to add a connection because this connection pool's size is max");
			}

			AsynShareConnection asynShareConnection = (AsynShareConnection) connectedAsynConnection;
			numberOfUnregisteredConnection--;
			connectionList.add(asynShareConnection);

			monitor.notify();
		}

		// FIXME!
		log.info("adding a connected connection[{}] to this connection pool success",
				connectedAsynConnection.hashCode());
	}

	@Override
	public void removeInterestedConnection(AsynConnectionIF interestedAsynConnection) {
		numberOfUnregisteredConnection--;
		log.info("remove the interedted connection[{}]", interestedAsynConnection.hashCode());
	}

}
