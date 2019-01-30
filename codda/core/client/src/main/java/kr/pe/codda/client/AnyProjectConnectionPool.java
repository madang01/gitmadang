package kr.pe.codda.client;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.classloader.ClientClassLoaderFactory;
import kr.pe.codda.client.classloader.ClientDynamicTaskManger;
import kr.pe.codda.client.classloader.ClientStaticTaskManger;
import kr.pe.codda.client.classloader.ClientTaskMangerIF;
import kr.pe.codda.client.connection.ConnectionPoolIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporter;
import kr.pe.codda.client.connection.asyn.AsynConnectionPoolIF;
import kr.pe.codda.client.connection.asyn.AsynThreadSafeSingleConnection;
import kr.pe.codda.client.connection.asyn.AyncThreadSafeSingleConnectedConnectionAdder;
import kr.pe.codda.client.connection.asyn.ClientIOEventController;
import kr.pe.codda.client.connection.asyn.share.AsynShareConnectionPool;
import kr.pe.codda.client.connection.sync.SyncNoShareConnectionPool;
import kr.pe.codda.client.connection.sync.SyncThreadSafeSingleConnection;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CharsetUtil;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.ServerTaskPermissionException;
import kr.pe.codda.common.io.DataPacketBufferPool;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.SocketOutputStreamFactory;
import kr.pe.codda.common.io.SocketOutputStreamFactoryIF;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.dhb.DHBMessageProtocol;
import kr.pe.codda.common.protocol.thb.THBMessageProtocol;
import kr.pe.codda.common.type.ClassloaderType;
import kr.pe.codda.common.type.ConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;

public final class AnyProjectConnectionPool implements AnyProjectConnectionPoolIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AnyProjectConnectionPool.class);
	
	private ProjectPartConfiguration projectPartConfiguration = null;
	
	private String mainProjectName = null;
	private Charset charset = null;
	private ByteOrder byteOrder = null;
	private long socketTimeout;	
	private MessageProtocolType messageProtocolType = null;
	private boolean clientDataPacketBufferIsDirect;
	private int clientDataPacketBufferMaxCntPerMessage;
	private int clientDataPacketBufferSize;
	private int clientDataPacketBufferPoolSize;
	private ConnectionType connectionType = null;	
	private long clientConnectionPoolSupporterTimeInterval;
	
	private ConnectionPoolIF connectionPool = null;	
	private ConnectionPoolSupporter connectionPoolSupporter = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private MessageProtocolIF messageProtocol = null;
	private ClientTaskMangerIF clientTaskManger = null;
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	private ClientIOEventController asynClientIOEventController = null;
	

	public AnyProjectConnectionPool(ProjectPartConfiguration projectPartConfiguration) throws NoMoreDataPacketBufferException, IOException, ConnectionPoolException {
		this.projectPartConfiguration = projectPartConfiguration;
		
		mainProjectName = projectPartConfiguration.getProjectName();
		charset = projectPartConfiguration.getCharset();
		byteOrder = projectPartConfiguration.getByteOrder();
		socketTimeout = projectPartConfiguration.getClientSocketTimeout();		
		messageProtocolType = projectPartConfiguration.getMessageProtocolType();
		clientDataPacketBufferIsDirect = projectPartConfiguration.getClientDataPacketBufferIsDirect();
		clientDataPacketBufferMaxCntPerMessage = projectPartConfiguration.getClientDataPacketBufferMaxCntPerMessage();
		clientDataPacketBufferSize = projectPartConfiguration.getClientDataPacketBufferSize();
		clientDataPacketBufferPoolSize = projectPartConfiguration.getClientDataPacketBufferPoolSize();		
		connectionType = projectPartConfiguration.getConnectionType();
		clientConnectionPoolSupporterTimeInterval = projectPartConfiguration.getClientConnectionPoolSupporterTimeInterval();
		
		CharsetEncoder charsetEncoderOfProject = CharsetUtil
				.createCharsetEncoder(charset);
		CharsetDecoder charsetDecoderOfProject = CharsetUtil
				.createCharsetDecoder(charset);
		
		this.dataPacketBufferPool = new DataPacketBufferPool(clientDataPacketBufferIsDirect, byteOrder,
				clientDataPacketBufferSize,
				clientDataPacketBufferPoolSize);
		

		switch (messageProtocolType) {
			case DHB: {
				messageProtocol = new DHBMessageProtocol(clientDataPacketBufferMaxCntPerMessage,
						charsetEncoderOfProject, charsetDecoderOfProject, dataPacketBufferPool);
	
				break;
			}
			/*
			 * case DJSON: { messageProtocol = new
			 * DJSONMessageProtocol(projectPartConfiguration.
			 * getDataPacketBufferMaxCntPerMessage(), charsetEncoderOfProject,
			 * charsetDecoderOfProject, dataPacketBufferPool); break; }
			 */
			case THB: {
				messageProtocol = new THBMessageProtocol(clientDataPacketBufferMaxCntPerMessage,
						charsetEncoderOfProject, charsetDecoderOfProject, dataPacketBufferPool);
				break;
			}
			default: {
				log.error(String.format("project[%s] 지원하지 않는 메시지 프로토콜[%s] 입니다.",
						mainProjectName,
						messageProtocolType.toString()));
				System.exit(1);
			}
		}	
		
		socketOutputStreamFactory = new SocketOutputStreamFactory(charsetDecoderOfProject,
				clientDataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
		
		connectionPoolSupporter = new ConnectionPoolSupporter(clientConnectionPoolSupporterTimeInterval);
		
		
		
		if (connectionType.equals(ConnectionType.SYNC_PRIVATE)) {
			connectionPool = new SyncNoShareConnectionPool(projectPartConfiguration,
					messageProtocol, dataPacketBufferPool,
					socketOutputStreamFactory,
					connectionPoolSupporter);
		} else {
			
			// FIXME! 환경 변수에 클래스로더 종류에 따라 설정하는 로직 필요함
			ClassloaderType clientClassloaderType = ClassloaderType.Static;		
			if (ClassloaderType.Static.equals(clientClassloaderType)) {
				clientTaskManger = new ClientStaticTaskManger();
			} else {
				CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
						.getRunningProjectConfiguration();
				String installedPathString = runningProjectConfiguration.getInstalledPathString();
				
				String clientClassloaderClassPathString = new StringBuilder()
						.append(WebRootBuildSystemPathSupporter.getUserWebINFPathString(installedPathString, mainProjectName))
						.append(File.separator)
						.append("classes")
						.toString();
				String clientClassloaderReousrcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName); 
				ClientClassLoaderFactory clientClassLoaderFactory = null;
				try {
					clientClassLoaderFactory = new ClientClassLoaderFactory(clientClassloaderClassPathString, clientClassloaderReousrcesPathString);
				} catch (CoddaConfigurationException e) {
					log.error("fail to create a instance of ClientClassLoaderFactory class, errmsg=", e.getMessage());
					System.exit(1);
				}
				clientTaskManger = new ClientDynamicTaskManger(clientClassLoaderFactory);
			}
			
			AsynConnectionPoolIF asynConnectionPool = 
					new AsynShareConnectionPool(projectPartConfiguration,
							messageProtocol, clientTaskManger, dataPacketBufferPool,
					socketOutputStreamFactory,
					connectionPoolSupporter);
			
			connectionPool = asynConnectionPool;
			
			asynClientIOEventController = new ClientIOEventController(
					projectPartConfiguration.getClientSelectorWakeupInterval(),
					asynConnectionPool);
			
			asynClientIOEventController.start();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		} 
		
		connectionPoolSupporter.start();
	}
	

	@Override
	public AbstractMessage sendSyncInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException, BodyFormatException,
			DynamicClassCallException, ServerTaskException, ServerTaskPermissionException, ConnectionPoolException {
		/*long startTime = 0;
		long endTime = 0;
		startTime = System.nanoTime();*/

		AbstractMessage outObj = null;
		ConnectionIF conn = connectionPool.getConnection();
		try {
			outObj = conn.sendSyncInputMessage(messageCodecManger, inputMessage);
		} catch (BodyFormatException e) {
			throw e;
		} catch (SocketTimeoutException e) {
			/** 연결 종류 마다  각자 처리하며 이곳에서는 아무 행동하지 않음 */
			throw e;
		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("this connection[")
					.append(conn.hashCode())
					.append("] was closed because of IOException").toString();
			log.warn(errorMessage, e);
			
			conn.close();
			

			throw e;
		} finally {
			connectionPool.release(conn);
		}

		/*endTime = System.nanoTime();
		log.debug("elapsed={}", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
*/
		return outObj;
	}

	@Override
	public void sendAsynInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage) throws InterruptedException, NotSupportedException,
			ConnectionPoolException, IOException, NoMoreDataPacketBufferException, DynamicClassCallException, BodyFormatException {
		/*long startTime = 0;
		long endTime = 0;
		startTime = System.nanoTime();*/

		ConnectionIF conn = connectionPool.getConnection();
		try {
			conn.sendAsynInputMessage(messageCodecManger, inputMessage);
		} finally {
			connectionPool.release(conn);
		}

		//endTime = System.nanoTime();
		//log.info("elapsed={}", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));

	}

	@Override
	public ConnectionIF createAsynThreadSafeConnection(String serverHost, int serverPort) throws InterruptedException, IOException, NoMoreDataPacketBufferException, NotSupportedException {
		if (connectionType.equals(ConnectionType.SYNC_PRIVATE)) {
			throw new NotSupportedException("the connection type is sync_private, it must be asyn_private or asyn_public, check the connection type in configuration");
		}
		
		ConnectionIF connectedConnection = null;
		
		AyncThreadSafeSingleConnectedConnectionAdder ayncThreadSafeSingleConnectedConnectionAdder = new AyncThreadSafeSingleConnectedConnectionAdder();
		AsynThreadSafeSingleConnection unregisteredAsynThreadSafeSingleConnection = 
				new AsynThreadSafeSingleConnection(mainProjectName, serverHost,
						serverPort,
						projectPartConfiguration.getClientSocketTimeout(),
						projectPartConfiguration.getClientSyncMessageMailboxCountPerAsynShareConnection(),
						projectPartConfiguration.getClientAsynInputMessageQueueCapacity(),					 
				socketOutputStreamFactory.createSocketOutputStream(), 
				messageProtocol, dataPacketBufferPool, clientTaskManger, ayncThreadSafeSingleConnectedConnectionAdder, 
				asynClientIOEventController, connectionPoolSupporter);		
		
		asynClientIOEventController.addUnregisteredAsynConnection(unregisteredAsynThreadSafeSingleConnection);
		asynClientIOEventController.wakeup();		
		
		try {
			connectedConnection = ayncThreadSafeSingleConnectedConnectionAdder.poll(socketTimeout);
		} catch(SocketTimeoutException e) {			
			log.warn("this connection[{}] timeout occurred", 
					unregisteredAsynThreadSafeSingleConnection.hashCode());
			
			/** WARNING! don't delete this code, this code is the code that closes the socket to cancel the connection registered in the selector */
			unregisteredAsynThreadSafeSingleConnection.close();
				
			
			throw e;
		}
		
		return connectedConnection;
	}
	
	@Override
	public ConnectionIF createSyncThreadSafeConnection(String serverHost, int serverPort) throws InterruptedException, IOException, NoMoreDataPacketBufferException {
		ConnectionIF connectedConnection = null;
		
		connectedConnection = new SyncThreadSafeSingleConnection(serverHost,
				serverPort,
				projectPartConfiguration.getClientSocketTimeout(),
				projectPartConfiguration.getClientDataPacketBufferSize(),
				socketOutputStreamFactory.createSocketOutputStream(), messageProtocol, dataPacketBufferPool);
		
		return connectedConnection;
	}

	@Override
	public String getPoolState() {
		return connectionPool.getPoolState();
	}

}
