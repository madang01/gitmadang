package kr.pe.codda.client;

import java.io.IOException;
import java.net.SocketTimeoutException;
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
import kr.pe.codda.client.connection.asyn.AsynConnectionPoolIF;
import kr.pe.codda.client.connection.asyn.AsynSelectorManger;
import kr.pe.codda.client.connection.asyn.AsynSelectorMangerIF;
import kr.pe.codda.client.connection.asyn.SingleAyncConnectionAdder;
import kr.pe.codda.client.connection.asyn.executor.ClientExecutorIF;
import kr.pe.codda.client.connection.asyn.executor.ClientExecutorPool;
import kr.pe.codda.client.connection.asyn.executor.ClientExecutorPoolIF;
import kr.pe.codda.client.connection.asyn.noshare.AsynNoShareConnection;
import kr.pe.codda.client.connection.asyn.noshare.AsynNoShareConnectionPool;
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

public class AnyProjectConnectionPool implements AnyProjectConnectionPoolIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AnyProjectConnectionPool.class);
	
	private ProjectPartConfiguration mainProjectPartConfiguration = null;

	private ConnectionPoolIF connectionPool = null;	
	private ConnectionPoolSupporter connectionPoolSupporter = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ClientExecutorPoolIF clientExecutorPool = null;
	private ClientMessageUtilityIF clientMessageUtility = null;
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	private AsynSelectorMangerIF asynSelectorManger = null;

	public AnyProjectConnectionPool(ProjectPartConfiguration mainProjectPartConfiguration) throws NoMoreDataPacketBufferException, IOException {
		this.mainProjectPartConfiguration = mainProjectPartConfiguration;

		CharsetEncoder charsetEncoderOfProject = CharsetUtil
				.createCharsetEncoder(mainProjectPartConfiguration.getCharset());
		CharsetDecoder charsetDecoderOfProject = CharsetUtil
				.createCharsetDecoder(mainProjectPartConfiguration.getCharset());

		boolean isDirect = false;
		this.dataPacketBufferPool = new DataPacketBufferPool(isDirect, mainProjectPartConfiguration.getByteOrder(),
				mainProjectPartConfiguration.getDataPacketBufferSize(),
				mainProjectPartConfiguration.getDataPacketBufferPoolSize());

		MessageProtocolIF messageProtocol = null;

		switch (mainProjectPartConfiguration.getMessageProtocolType()) {
			case DHB: {
				messageProtocol = new DHBMessageProtocol(mainProjectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
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
				messageProtocol = new THBMessageProtocol(mainProjectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
						charsetEncoderOfProject, charsetDecoderOfProject, dataPacketBufferPool);
				break;
			}
			default: {
				log.error(String.format("project[%s] 지원하지 않는 메시지 프로토콜[%s] 입니다.",
						mainProjectPartConfiguration.getProjectName(),
						mainProjectPartConfiguration.getMessageProtocolType().toString()));
				System.exit(1);
			}
		}
		ClientObjectCacheManagerIF clientObjectCacheManager = new ClientObjectCacheManager();
		
		socketOutputStreamFactory = new SocketOutputStreamFactory(charsetDecoderOfProject,
				mainProjectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), dataPacketBufferPool);
		
		ClientMessageUtilityIF clientMessageUtility = new ClientMessageUtility(messageProtocol, clientObjectCacheManager,
				dataPacketBufferPool);
		
		connectionPoolSupporter = new ConnectionPoolSupporter(1000L * 60 * 10);
		
		clientExecutorPool = new ClientExecutorPool(
				mainProjectPartConfiguration.getClientAsynExecutorPoolSize(), 
				mainProjectPartConfiguration.getProjectName(),
				mainProjectPartConfiguration.getClientAsynOutputMessageQueueSize(), clientMessageUtility);
		
		if (mainProjectPartConfiguration.getConnectionType().equals(ConnectionType.SYNC_PRIVATE)) {
			
		} else {
			AsynConnectionPoolIF asynConnectionPool = null;
			
			if (mainProjectPartConfiguration.getConnectionType().equals(ConnectionType.ASYN_PUBLIC)) {
				asynConnectionPool = new AsynNoShareConnectionPool(mainProjectPartConfiguration,
						clientMessageUtility,
						socketOutputStreamFactory,
						connectionPoolSupporter,
						clientExecutorPool);
			} else {
				
			}
			asynSelectorManger = new AsynSelectorManger(asynConnectionPool);			
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
	public ConnectionIF createConnection(String host, int port) throws InterruptedException, IOException, NoMoreDataPacketBufferException {
		ClientExecutorIF clientExecutor  = clientExecutorPool.getClientExecutorWithMinimumNumberOfConnetion();
		SingleAyncConnectionAdder singleAyncConnectionAdder = new SingleAyncConnectionAdder();
		AsynNoShareConnection unregisteredAsynNoShareConnection = new AsynNoShareConnection(mainProjectPartConfiguration, 
				socketOutputStreamFactory.newInstance(), 
				clientMessageUtility, singleAyncConnectionAdder, clientExecutor, asynSelectorManger);
		
		try {
			asynSelectorManger.addUnregisteredAsynConnection(unregisteredAsynNoShareConnection);
		} catch(IOException e) {
			unregisteredAsynNoShareConnection.close();
			unregisteredAsynNoShareConnection.releaseResources();
			
			log.warn("fail to register a connection[{}] to selector", unregisteredAsynNoShareConnection.hashCode());
			
			throw new IOException("fail to register a connection to selector");
		}
		
		ConnectionIF connectedConnection =  singleAyncConnectionAdder.poll(mainProjectPartConfiguration.getClientSocketTimeout());
		if (null == connectedConnection) {
			if (! unregisteredAsynNoShareConnection.isConnected()) {
				unregisteredAsynNoShareConnection.releaseResources();
				
				log.warn("this connection[{}] disconnected", 
						unregisteredAsynNoShareConnection.hashCode());
				throw new IOException("the connection has been disconnected");
			}
			
			log.warn("this connection[{}] timeout occurred", 
					unregisteredAsynNoShareConnection.hashCode());
			try {
				unregisteredAsynNoShareConnection.close();
			} catch(IOException e) {
				log.warn("fail to close the connection[{}], errmsg={}", unregisteredAsynNoShareConnection.hashCode(), e.getMessage());
			}			
			
			throw new SocketTimeoutException("socket timeout occurred");
		}
		return connectedConnection;
	}

	@Override
	public String getPoolState() {
		return null;
	}

}
