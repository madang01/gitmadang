package kr.pe.codda.client;

import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.type.ConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.type.ProjectType;
import kr.pe.codda.server.AnyProjectServer;

public class AnyProjectConnectionPoolTest extends AbstractJunitTest {
	
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
		int serverMaxClients = 10;		
		int serverInputMessageQueueSize = 5;
		int serverOutputMessageQueueSize = 5;
		
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
				serverOutputMessageQueueSize);

		return projectPartConfigurationForTest;
	}

	@Test
	public void testCreateAsynThreadSafeConnection_연결타입이_동기로_설정되어_NotSupportedException을던지는지검사() {
		String testProjectName = "sample_test";
		ProjectPartConfiguration projectPartConfigurationForTest = null;
		MessageProtocolType messageProtocolTypeForTest = MessageProtocolType.DHB;
		boolean clientDataPacketBufferIsDirect = false;
		String serverHost = null;
		int serverPort; 
		
		// host = "172.30.1.16";
		serverHost = "localhost";
		serverPort = 9393;
		
		int clientConnectionCount = 0;
		
		try {
			projectPartConfigurationForTest = buildMainProjectPartConfiguration(testProjectName,
					serverHost,  serverPort,
					clientConnectionCount,
					messageProtocolTypeForTest,
					clientDataPacketBufferIsDirect,
					ConnectionType.SYNC);

		} catch (Exception e) {
			log.warn("error", e);

			String errorMessage = String.format(
					"fail to mapping configuration's item value to ProjectPartConfiguration's item value::%s",
					e.getMessage());

			fail(errorMessage);
		}
		
		log.info("{}", projectPartConfigurationForTest.getClientConnectionCount());
		
		AnyProjectServer anyProjectServerForTest = null;
		try {
			String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
					.getServerAPPINFClassPathString(installedPath.getAbsolutePath(), 
							mainProjectName);
			String projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPath.getAbsolutePath(), mainProjectName);
			
			anyProjectServerForTest = new AnyProjectServer(serverAPPINFClassPathString,
					projectResourcesPathString,
					projectPartConfigurationForTest);
			anyProjectServerForTest.startServer();
		} catch (Exception e) {
			log.warn("fail to start a server", e);
			fail("fail to start a server");
		}		
		
		AnyProjectConnectionPoolIF  anyProjectConnectionPool  = null;
		
		try {
			anyProjectConnectionPool = new AnyProjectConnectionPool(projectPartConfigurationForTest);
		} catch (Exception e) {
			log.warn("fail to create a asyn no-share connection pool", e);
			fail("fail to create a asyn no-share connection pool");
		}		
		
		try {
			anyProjectConnectionPool.createAsynThreadSafeConnection(serverHost, serverPort);			
			fail("no NotSupportedException");
		} catch (NotSupportedException e) {
		} catch (Exception e) {
			log.warn("expected NotSupportedException but unknown error", e);
			fail("expected NotSupportedException but unknown error");
		}	
	}

}
