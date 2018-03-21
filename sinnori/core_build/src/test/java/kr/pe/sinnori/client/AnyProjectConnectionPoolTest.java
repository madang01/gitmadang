package kr.pe.sinnori.client;

import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

import org.junit.Test;

import kr.pe.sinnori.client.connection.ConnectionPoolIF;
import kr.pe.sinnori.client.connection.asyn.noshare.AsynPrivateConnectionPool;
import kr.pe.sinnori.client.connection.asyn.share.AsynPublicConnectionPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.IEOClientThreadPoolSetManagerIF;
import kr.pe.sinnori.client.connection.sync.noshare.SyncPrivateConnectionPool;
import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.type.ConnectionType;
import kr.pe.sinnori.common.type.MessageProtocolType;
import kr.pe.sinnori.common.type.ProjectType;
import kr.pe.sinnori.server.AnyProjectServer;

public class AnyProjectConnectionPoolTest extends AbstractJunitTest {
	private ProjectPartConfiguration buildMainProjectPartConfiguration(String projectName,
			String host, int port,
			int numberOfConnection,
			MessageProtocolType messageProtocolType,
			ConnectionType connectionType)
			throws SinnoriConfigurationException {		
		 
		
		ProjectPartConfiguration projectPartConfigurationForTest = new ProjectPartConfiguration(ProjectType.MAIN,
				projectName);
		
		ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
		Charset charset = CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET;
		int dataPacketBufferMaxCntPerMessage=50;
		int dataPacketBufferSize=4096;
		int dataPacketBufferPoolSize=1000;
		int messageIDFixedSize=20;
		// MessageProtocolType messageProtocolType;
		String firstPrefixDynamicClassFullName="kr.pe.sinnori.impl.";			
		long clientMonitorTimeInterval = 60*1000*5L;
		// final ConnectionType connectionType = ConnectionType.SYNC_PRIVATE;
		long clientSocketTimeout = 5000L;			
		int clientConnectionCount = numberOfConnection;
		int clientConnectionMaxCount = numberOfConnection;
		int clientAsynPirvateMailboxCntPerPublicConnection = 2;
		int clientAsynInputMessageQueueSize = 5;
		int clientAsynOutputMessageQueueSize = 5;
		long clientWakeupIntervalOfSelectorForReadEventOnly = 10L;			
		int clientAsynInputMessageWriterPoolSize = 2;			
		int clientAsynOutputMessageReaderPoolSize = 2;			
		int clientAsynExecutorPoolSize =2;
		long serverMonitorTimeInterval = 5000L;
		int serverMaxClients = numberOfConnection*2;
		int serverAcceptQueueSize = 5;
		int serverInputMessageQueueSize = 5;
		int serverOutputMessageQueueSize = 5;
		long serverWakeupIntervalOfSelectorForReadEventOnly = 10L;			
		int serverAcceptProcessorSize = 2; 
		int serverAcceptProcessorMaxSize = serverAcceptProcessorSize;			
		int serverInputMessageReaderPoolSize = 2;
		int serverInputMessageReaderPoolMaxSize = serverInputMessageReaderPoolSize;
		int serverExecutorPoolSize = 2;
		int serverExecutorPoolMaxSize = serverExecutorPoolSize;
		int serverOutputMessageWriterPoolSize = 2;
		int serverOutputMessageWriterPoolMaxSize = serverOutputMessageWriterPoolSize;	
		String serverMybatisConfigFileRelativePathString = "kr/pe/sinnori/impl/mybatis/mybatisConfig.xml";
		
		projectPartConfigurationForTest.build(host, port, 
				byteOrder, 
				charset, 
				dataPacketBufferMaxCntPerMessage, 
				dataPacketBufferSize, 
				dataPacketBufferPoolSize, 
				messageIDFixedSize, 
				messageProtocolType, 
				firstPrefixDynamicClassFullName, 
				clientMonitorTimeInterval, 
				connectionType, 
				clientSocketTimeout, 
				clientConnectionCount, 
				clientConnectionMaxCount, 
				clientAsynPirvateMailboxCntPerPublicConnection, 
				clientAsynInputMessageQueueSize, 
				clientAsynOutputMessageQueueSize, 
				clientWakeupIntervalOfSelectorForReadEventOnly, 
				clientAsynInputMessageWriterPoolSize, 
				clientAsynOutputMessageReaderPoolSize, 
				clientAsynExecutorPoolSize, 
				serverMonitorTimeInterval, 
				serverMaxClients, 
				serverAcceptQueueSize, 
				serverInputMessageQueueSize, 
				serverOutputMessageQueueSize, 
				serverWakeupIntervalOfSelectorForReadEventOnly, 
				serverAcceptProcessorSize, 
				serverAcceptProcessorMaxSize, 
				serverInputMessageReaderPoolSize, 
				serverInputMessageReaderPoolMaxSize, 
				serverExecutorPoolSize, 
				serverExecutorPoolMaxSize, 
				serverOutputMessageWriterPoolSize, 
				serverOutputMessageWriterPoolMaxSize, 
				serverMybatisConfigFileRelativePathString);

		return projectPartConfigurationForTest;
	}

	@Test
	public void testGetIEOClientThreadPoolSetManager_동기비공유연결_미지원() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9291;
		
		int numberOfConnection = 2;
				
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
					host,  port,
					numberOfConnection,
					messageProtocolTypeForTest,
					ConnectionType.SYNC_PRIVATE);

		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}

		try {
			AnyProjectServer anyProjectServerForTest = new AnyProjectServer(projectPartConfigurationForTest);

			anyProjectServerForTest.startServer();

			AnyProjectConnectionPool anyProjectConnectionPoolForTest = new AnyProjectConnectionPool(
					projectPartConfigurationForTest);
			
			@SuppressWarnings("unused")
			IEOClientThreadPoolSetManagerIF ieoClientThreadPoolSetManager = anyProjectConnectionPoolForTest
				.getIEOClientThreadPoolSetManager();
						
			fail("not NotSupportedException");
		} catch (NotSupportedException e) {
			log.info(e.getMessage());
		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to get a output message::%s",
					e.getMessage());

			fail(errorMessage);
		}
	}
	
	@Test
	public void testGetconnectionPool() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9390;
		
		int numberOfConnection = 2;
		
		
		ConnectionType[] connectionTypeList = {
				ConnectionType.ASYN_PRIVATE, ConnectionType.ASYN_PUBLIC, ConnectionType.SYNC_PRIVATE	
		};
		
		for (ConnectionType connectionType : connectionTypeList) {
			port++;
			
			try {
				projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
						host,  port,
						numberOfConnection,
						messageProtocolTypeForTest,
						connectionType);

			} catch (Exception e) {
				log.warn("error", e);

				String errorMessage = String.format(
						"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
						e.getMessage());

				fail(errorMessage);
			}
			
			try {
				AnyProjectServer anyProjectServerForTest = new AnyProjectServer(projectPartConfigurationForTest);

				anyProjectServerForTest.startServer();

				AnyProjectConnectionPool anyProjectConnectionPoolForTest = new AnyProjectConnectionPool(
						projectPartConfigurationForTest);
				
				ConnectionPoolIF connectionPool = anyProjectConnectionPoolForTest.getconnectionPool();
				
				if (connectionType.equals(ConnectionType.ASYN_PRIVATE)) {					
					if (! (connectionPool instanceof AsynPrivateConnectionPool)) {
						fail("the returned value connectionPool is not a instance of AsynPrivateConnectionPool class");
					}					
				} else if (connectionType.equals(ConnectionType.ASYN_PUBLIC)) {
					if (! (connectionPool instanceof AsynPublicConnectionPool)) {
						fail("the returned value connectionPool is not a instance of AsynPublicConnectionPool class");
					}
				} else if (connectionType.equals(ConnectionType.SYNC_PRIVATE)) {
					if (! (connectionPool instanceof SyncPrivateConnectionPool)) {
						fail("the returned value connectionPool is not a instance of SyncPrivateConnectionPool class");
					}
				} else {
					fail("unknown connection type");
				}
					
			} catch (Exception e) {
				log.warn("error", e);

				String errorMessage = String.format(
						"fail to get a output message::%s",
						e.getMessage());

				fail(errorMessage);
			}
		}
	}
}
