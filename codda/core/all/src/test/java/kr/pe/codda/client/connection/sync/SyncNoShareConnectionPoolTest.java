package kr.pe.codda.client.connection.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.client.AnyProjectConnectionPool;
import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.type.ConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.type.ProjectType;
import kr.pe.codda.impl.message.Empty.Empty;

public class SyncNoShareConnectionPoolTest extends AbstractJunitTest {
	
	private ProjectPartConfiguration buildMainProjectPartConfiguration(String projectName,
			String host, int port,
			int clientConnectionCount,
			MessageProtocolType messageProtocolType,
			boolean clientDataPacketBufferIsDirect,
			ConnectionType connectionType)
			throws CoddaConfigurationException {		
		 
		
		ProjectPartConfiguration projectPartConfigurationForTest = new ProjectPartConfiguration(ProjectType.MAIN,
				projectName);
		
		//String host="localhost";
		//int port=9090;
		ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
		Charset charset = CommonStaticFinalVars.SOURCE_FILE_CHARSET;		
		int messageIDFixedSize=20;
		//MessageProtocolType messageProtocolType = MessageProtocolType.DHB;					
		long clientMonitorTimeInterval = 60*1000*5L;
		// boolean clientDataPacketBufferIsDirect=true;
		int clientDataPacketBufferMaxCntPerMessage=50;
		int clientDataPacketBufferSize=2048;
		int clientDataPacketBufferPoolSize=1000;
		//ConnectionType connectionType = ConnectionType.ASYN_PRIVATE;
		long clientSocketTimeout = 5000L;			
		// int clientConnectionCount = 2;
		int clientConnectionMaxCount = 4;
		long clientConnectionPoolSupporterTimeInterval = 600000L;
		int clientAsynPirvateMailboxCntPerPublicConnection = 2;
		int clientAsynInputMessageQueueSize = 5;
		int clientAsynOutputMessageQueueSize = 5;
		long clientAsynSelectorWakeupInterval = 1L;			
		int clientAsynExecutorPoolSize =2;
		long serverMonitorTimeInterval = 5000L;
		boolean serverDataPacketBufferIsDirect=true;
		int serverDataPacketBufferMaxCntPerMessage=50;
		int serverDataPacketBufferSize=2048;
		int serverDataPacketBufferPoolSize=1000;
		int serverMaxClients = 100000;		
		int serverInputMessageQueueSize = 5;
		int serverOutputMessageQueueSize = 5;
		int serverExecutorPoolSize = 2;
		int serverExecutorPoolMaxSize = 3;
		
		projectPartConfigurationForTest.build(host, 
				port,
				byteOrder,
				charset,				
				messageIDFixedSize,
				messageProtocolType,		
				clientMonitorTimeInterval,
				clientDataPacketBufferIsDirect,
				clientDataPacketBufferMaxCntPerMessage,
				clientDataPacketBufferSize,
				clientDataPacketBufferPoolSize,
				connectionType,
				clientSocketTimeout,			
				clientConnectionCount,
				clientConnectionMaxCount,
				clientConnectionPoolSupporterTimeInterval,
				clientAsynPirvateMailboxCntPerPublicConnection,
				clientAsynInputMessageQueueSize,
				clientAsynOutputMessageQueueSize,
				clientAsynSelectorWakeupInterval,
				clientAsynExecutorPoolSize,
				serverMonitorTimeInterval,
				serverDataPacketBufferIsDirect,
				serverDataPacketBufferMaxCntPerMessage,
				serverDataPacketBufferSize,
				serverDataPacketBufferPoolSize,
				serverMaxClients,
				serverInputMessageQueueSize,
				serverOutputMessageQueueSize,
				serverExecutorPoolSize,
				serverExecutorPoolMaxSize);

		return projectPartConfigurationForTest;
	}


	@Test
	public void testGetConnection_서버없이폴생성하여연결요구() {
		String testProjectName = "sample_test";
		
		String host = "localhost";;
		int port = 9295;;
		int clientConnectionCount = 2;
		boolean clientDataPacketBufferIsDirect = false;
		ProjectPartConfiguration projectPartConfigurationForTest = null;
		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.THB;
		
		int retryCount = 5;
		
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
					host,  port,
					clientConnectionCount,
					messageProtocolTypeForTest,
					clientDataPacketBufferIsDirect,
					ConnectionType.SYNC_PRIVATE);

		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}
		
		
		Empty emptyReq = new Empty();
		
		AnyProjectConnectionPoolIF  anyProjectConnectionPool  = null;
		
		try {
			anyProjectConnectionPool = new AnyProjectConnectionPool(projectPartConfigurationForTest);
		} catch (Exception e) {
			log.warn("fail to create a asyn no-share connection pool", e);
			fail("fail to create a asyn no-share connection pool");
		}		
		
		try {
			
			long startTime = System.nanoTime();
			
			for (int i=0; i < retryCount; i++) {
				anyProjectConnectionPool.sendSyncInputMessage(emptyReq);
				
				fail("no ConnectionPoolException");
			}
			
			long endTime = System.nanoTime();
			log.info("{} 회 평균시간[{}] microseconds", retryCount, TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS)/retryCount);
		} catch(ConnectionPoolException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "check server is alive or something is bad";
			
			assertEquals(expectedErrorMessage, acutalErrorMessage);
			
		} catch (Exception e) {
			log.warn("unknown error", e);

			String errorMessage = String.format(
					"fail to get a output message::%s",
					e.getMessage());

			fail(errorMessage);
		}
	}

}
