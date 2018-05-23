package kr.pe.codda.client.connection.asyn.share;

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
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.type.ConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.type.ProjectType;
import kr.pe.codda.impl.message.Empty.Empty;
import kr.pe.codda.server.AnyProjectServer;

public class AsynShareConnectionTest extends AbstractJunitTest {
	private ProjectPartConfiguration buildMainProjectPartConfiguration(String projectName,
			String host, int port,
			int numberOfConnection,
			MessageProtocolType messageProtocolType,
			ConnectionType connectionType)
			throws CoddaConfigurationException {		
		 
		
		ProjectPartConfiguration projectPartConfigurationForTest = new ProjectPartConfiguration(ProjectType.MAIN,
				projectName);
		
		ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
		Charset charset = CommonStaticFinalVars.SOURCE_FILE_CHARSET;
		int dataPacketBufferMaxCntPerMessage=50;
		int dataPacketBufferSize=4096;
		int dataPacketBufferPoolSize=1000;
		int messageIDFixedSize=20;
		// MessageProtocolType messageProtocolType;
		String firstPrefixDynamicClassFullName="kr.pe.sinnori.impl.";			
		long clientMonitorTimeInterval = 60*1000*5L;
		// ConnectionType connectionType = connectionType;
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
	public void testSendSyncInputMessage_ok() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.THB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9291;
		
		int numberOfConnection = 1;
		
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
					host,  port,
					numberOfConnection,
					messageProtocolTypeForTest,
					ConnectionType.ASYN_PUBLIC);

		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}
		
		// log.info("{}", projectPartConfigurationForTest.getClientConnectionCount());
		
		AnyProjectServer anyProjectServerForTest = null;
		try {
			anyProjectServerForTest = new AnyProjectServer(projectPartConfigurationForTest);
			anyProjectServerForTest.startServer();
		} catch (Exception e) {
			log.warn("fail to start a server", e);
			fail("fail to start a server");
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
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
		}
		
		try {
			int retryCount = 2;
			long startTime = System.nanoTime();
			
			for (int i=0; i < retryCount; i++) {
				AbstractMessage emptyRes =  anyProjectConnectionPool.sendSyncInputMessage(emptyReq);				
				if (!(emptyRes instanceof Empty)) {
					fail("empty 메시지 수신 실패");
				}

				if (!emptyReq.messageHeaderInfo.equals(emptyRes.messageHeaderInfo)) {
					fail("수신한 empty 메시지의 메시지 헤더가 송신한 empty 메시지의 메시지 헤더와 다릅니다");
				}
			}
			
			long endTime = System.nanoTime();
			log.info("{} 회 평균시간[{}] microseconds", retryCount, TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS)/retryCount);
			
		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to get a output message::%s",
					e.getMessage());

			fail(errorMessage);
		}
	}
	
	@Test
	public void testSendAsynInputMessage_ok() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;

		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.THB;

		String host = null;
		int port;
		
		// host = "172.30.1.16";
		host = "localhost";
		port = 9291;
		
		int numberOfConnection = 1;
		
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
					host,  port,
					numberOfConnection,
					messageProtocolTypeForTest,
					ConnectionType.ASYN_PUBLIC);

		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}
		
		// log.info("{}", projectPartConfigurationForTest.getClientConnectionCount());
		
		AnyProjectServer anyProjectServerForTest = null;
		try {
			anyProjectServerForTest = new AnyProjectServer(projectPartConfigurationForTest);
			anyProjectServerForTest.startServer();
		} catch (Exception e) {
			log.warn("fail to start a server", e);
			fail("fail to start a server");
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
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
		}
		
		try {
			int retryCount = 1;
			long startTime = System.nanoTime();
			
			for (int i=0; i < retryCount; i++) {
				anyProjectConnectionPool.sendAsynInputMessage(emptyReq);
			}
			
			long endTime = System.nanoTime();
			log.info("{} 회 평균시간[{}] microseconds", retryCount, TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS)/retryCount);
			
		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to get a output message::%s",
					e.getMessage());

			fail(errorMessage);
		}
		
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
		}
	}
}
