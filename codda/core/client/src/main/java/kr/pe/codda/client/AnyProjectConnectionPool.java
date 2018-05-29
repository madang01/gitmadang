package kr.pe.codda.client;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.TimeUnit;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.connection.ClientMessageUtility;
import kr.pe.codda.client.connection.ClientMessageUtilityIF;
import kr.pe.codda.client.connection.ClientObjectCacheManager;
import kr.pe.codda.client.connection.ClientObjectCacheManagerIF;
import kr.pe.codda.client.connection.ConnectionPoolIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporter;
import kr.pe.codda.client.connection.asyn.AsynClientIOEventController;
import kr.pe.codda.client.connection.asyn.AsynConnectionPoolIF;
import kr.pe.codda.client.connection.asyn.executor.ClientExecutorIF;
import kr.pe.codda.client.connection.asyn.executor.ClientExecutorPool;
import kr.pe.codda.client.connection.asyn.noshare.AsynNoShareConnectionPool;
import kr.pe.codda.client.connection.asyn.share.AsynShareConnection;
import kr.pe.codda.client.connection.asyn.share.AsynShareConnectionPool;
import kr.pe.codda.client.connection.sync.SyncNoShareConnectionPool;
import kr.pe.codda.client.connection.sync.SyncThreadSafeSingleConnection;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CharsetUtil;
import kr.pe.codda.common.exception.BodyFormatException;
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
	private int clientAsynExecutorPoolSize;
	private int clientAsynOutputMessageQueueSize;
	private ConnectionType connectionType = null;	
	
	private ConnectionPoolIF connectionPool = null;	
	private ConnectionPoolSupporter connectionPoolSupporter = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ClientExecutorPool clientExecutorPool = null;
	private ClientMessageUtilityIF clientMessageUtility = null;
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	private AsynClientIOEventController asynSelectorManger = null;
	

	public AnyProjectConnectionPool(ProjectPartConfiguration projectPartConfiguration) throws NoMoreDataPacketBufferException, IOException {
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
		clientAsynExecutorPoolSize = projectPartConfiguration.getClientAsynExecutorPoolSize();
		clientAsynOutputMessageQueueSize = projectPartConfiguration.getClientAsynOutputMessageQueueSize();
		connectionType = projectPartConfiguration.getConnectionType();
		
		CharsetEncoder charsetEncoderOfProject = CharsetUtil
				.createCharsetEncoder(charset);
		CharsetDecoder charsetDecoderOfProject = CharsetUtil
				.createCharsetDecoder(charset);
		
		this.dataPacketBufferPool = new DataPacketBufferPool(clientDataPacketBufferIsDirect, byteOrder,
				clientDataPacketBufferSize,
				clientDataPacketBufferPoolSize);

		MessageProtocolIF messageProtocol = null;

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
		ClientObjectCacheManagerIF clientObjectCacheManager = new ClientObjectCacheManager();
		
		socketOutputStreamFactory = new SocketOutputStreamFactory(charsetDecoderOfProject,
				clientDataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
		
		clientMessageUtility = new ClientMessageUtility(messageProtocol, clientObjectCacheManager,
				dataPacketBufferPool);
		
		connectionPoolSupporter = new ConnectionPoolSupporter(1000L * 60 * 10);
		
		clientExecutorPool = new ClientExecutorPool(
				clientAsynExecutorPoolSize, 
				mainProjectName,
				clientAsynOutputMessageQueueSize, clientMessageUtility);
		
		clientExecutorPool.startAll();
		
		if (connectionType.equals(ConnectionType.SYNC_PRIVATE)) {
			connectionPool = new SyncNoShareConnectionPool(projectPartConfiguration,
					clientMessageUtility,
					socketOutputStreamFactory,
					connectionPoolSupporter);
		} else {
			AsynConnectionPoolIF asynConnectionPool = null;
			
			if (connectionType.equals(ConnectionType.ASYN_PRIVATE)) {
				asynConnectionPool = new AsynNoShareConnectionPool(projectPartConfiguration,
						clientMessageUtility,
						socketOutputStreamFactory,
						connectionPoolSupporter,
						clientExecutorPool);
				
				connectionPool = asynConnectionPool;
			} else {
				asynConnectionPool = new AsynShareConnectionPool(projectPartConfiguration,
						clientMessageUtility,
						socketOutputStreamFactory,
						connectionPoolSupporter,
						clientExecutorPool);
				
				connectionPool = asynConnectionPool;
			}
			asynSelectorManger = new AsynClientIOEventController(
					projectPartConfiguration.getClientSelectorWakeupInterval(),
					asynConnectionPool);
			
			asynSelectorManger.start();
		} 
		
		connectionPoolSupporter.start();
	}
	

	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException, BodyFormatException,
			DynamicClassCallException, ServerTaskException, ServerTaskPermissionException, ConnectionPoolException {
		long startTime = 0;
		long endTime = 0;
		startTime = System.nanoTime();

		AbstractMessage outObj = null;
		ConnectionIF conn = connectionPool.getConnection();
		try {
			outObj = conn.sendSyncInputMessage(inputMessage);
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
			try {
				conn.close();
			} catch (IOException e1) {
			}

			throw e;
		} finally {
			connectionPool.release(conn);
		}

		endTime = System.nanoTime();
		log.debug("elapsed={}", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));

		return outObj;
	}

	@Override
	public void sendAsynInputMessage(AbstractMessage inputMessage) throws InterruptedException, NotSupportedException,
			ConnectionPoolException, IOException, NoMoreDataPacketBufferException, DynamicClassCallException, BodyFormatException {
		long startTime = 0;
		long endTime = 0;
		startTime = System.nanoTime();

		ConnectionIF conn = connectionPool.getConnection();
		try {
			conn.sendAsynInputMessage(inputMessage);
		} finally {
			connectionPool.release(conn);
		}

		endTime = System.nanoTime();
		log.debug("elapsed={}", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));

	}

	@Override
	public ConnectionIF createAsynThreadSafeConnection(String host, int port) throws InterruptedException, IOException, NoMoreDataPacketBufferException {
		ConnectionIF connectedConnection = null;
		
		ClientExecutorIF clientExecutor  = clientExecutorPool.getClientExecutorWithMinimumNumberOfConnetion();
		SingleAyncShareConnectionAdder singleAyncShareConnectionAdder = new SingleAyncShareConnectionAdder();
		AsynShareConnection unregisteredAsynNoShareConnection = 
				new AsynShareConnection(projectPartConfiguration.getServerHost(),
						projectPartConfiguration.getServerPort(),
						projectPartConfiguration.getClientSocketTimeout(),
						projectPartConfiguration.getClientSyncMessageMailboxCountPerAsynShareConnection(),
						projectPartConfiguration.getClientAsynInputMessageQueueSize(),					 
				socketOutputStreamFactory.newInstance(), 
				clientMessageUtility, singleAyncShareConnectionAdder, 
				clientExecutor, asynSelectorManger);
		
		try {
			asynSelectorManger.addUnregisteredAsynConnection(unregisteredAsynNoShareConnection);
		} catch(IOException e) {
			unregisteredAsynNoShareConnection.close();
			unregisteredAsynNoShareConnection.releaseResources();
			
			log.warn("fail to register a connection[{}] to selector", unregisteredAsynNoShareConnection.hashCode());
			
			throw new IOException("fail to register a connection to selector");
		}
		
		try {
			connectedConnection =  singleAyncShareConnectionAdder.poll(socketTimeout);
		} catch(SocketTimeoutException e) {			
			log.warn("this connection[{}] timeout occurred", 
					unregisteredAsynNoShareConnection.hashCode());
			try {
				/** WARNING! don't delete this code, this code is the code that closes the socket to cancel the connection registered in the selector */
				unregisteredAsynNoShareConnection.close();
			} catch(IOException e1) {
				log.warn("fail to close the connection[{}], errmsg={}", unregisteredAsynNoShareConnection.hashCode(), e1.getMessage());
			}			
			
			throw e;
		}
		
		
		
		
		return connectedConnection;
	}
	
	@Override
	public ConnectionIF createSyncThreadSafeConnection(String host, int port) throws InterruptedException, IOException, NoMoreDataPacketBufferException {
		ConnectionIF connectedConnection = null;
		
		connectedConnection = new SyncThreadSafeSingleConnection(projectPartConfiguration.getServerHost(),
				projectPartConfiguration.getServerPort(),
				projectPartConfiguration.getClientSocketTimeout(),
				projectPartConfiguration.getClientDataPacketBufferSize(),
				socketOutputStreamFactory.newInstance(), clientMessageUtility);
		
		return connectedConnection;
	}

	@Override
	public String getPoolState() {
		return connectionPool.getPoolState();
	}

}
