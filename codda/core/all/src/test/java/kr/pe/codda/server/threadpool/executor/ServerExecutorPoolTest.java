package kr.pe.codda.server.threadpool.executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import org.junit.Test;
import org.mockito.Mockito;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.type.ConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.type.ProjectType;
import kr.pe.codda.server.ProjectLoginManagerIF;
import kr.pe.codda.server.ServerObjectCacheManagerIF;

public class ServerExecutorPoolTest extends AbstractJunitTest {
	
	private ProjectPartConfiguration buildMainProjectPartConfiguration(String projectName,
			int serverExecutorPoolSize,
			int serverExecutorPoolMaxSize,	
			int serverInputMessageQueueSize) throws CoddaConfigurationException {		
		
		ProjectPartConfiguration projectPartConfigurationForTest = new ProjectPartConfiguration(ProjectType.MAIN,
				projectName);
		
		String host="localhost";
		int port=9090;
		ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
		Charset charset = CommonStaticFinalVars.SOURCE_FILE_CHARSET;		
		int messageIDFixedSize=20;
		MessageProtocolType messageProtocolType = MessageProtocolType.DHB;					
		long clientMonitorTimeInterval = 60*1000*5L;
		boolean clientDataPacketBufferIsDirect=true;
		int clientDataPacketBufferMaxCntPerMessage=50;
		int clientDataPacketBufferSize=2048;
		int clientDataPacketBufferPoolSize=1000;
		ConnectionType connectionType = ConnectionType.ASYN_PRIVATE;
		long clientSocketTimeout = 5000L;			
		int clientConnectionCount = 2;
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
		// int serverInputMessageQueueSize = 5;
		int serverOutputMessageQueueSize = 5;
		// int serverExecutorPoolSize = 2;
		// int serverExecutorPoolMaxSize = 3;
		
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
	public void testConstructor_theParameterPoolSize_lessThanOne() {
		String projectName = "sample_test";
		int serverExecutorPoolSize = 0;
		int serverExecutorPoolMaxSize = 10;	
		int serverInputMessageQueueSize = 10;
		
		ProjectPartConfiguration projectPartConfiguration = null;
		try {
			projectPartConfiguration = buildMainProjectPartConfiguration(projectName,
					serverExecutorPoolSize,
					serverExecutorPoolMaxSize,
					serverInputMessageQueueSize);
		} catch (CoddaConfigurationException e) {
			log.warn("error", e);

			String errorMessage = new StringBuilder()
					.append("fail to create a instance of ProjectPartConfiguration class, errmsg=")
					.append(e.getMessage()).toString();

			fail(errorMessage);
		}
				
		ProjectLoginManagerIF projectLoginManager = Mockito.mock(ProjectLoginManagerIF.class);
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(projectPartConfiguration,
					projectLoginManager,
					messageProtocol, 					
					serverObjectCacheManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter poolSize[%d] is less than or equal to zero", serverExecutorPoolSize);
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterPoolMaxSize_lessThanOne() {
		String projectName = "sample_test";
		int serverExecutorPoolSize = 1;
		int serverExecutorPoolMaxSize = 0;	
		int serverInputMessageQueueSize = 10;
		
		ProjectPartConfiguration projectPartConfiguration = null;
		try {
			projectPartConfiguration = buildMainProjectPartConfiguration(projectName,
					serverExecutorPoolSize,
					serverExecutorPoolMaxSize,
					serverInputMessageQueueSize);
		} catch (CoddaConfigurationException e) {
			log.warn("error", e);

			String errorMessage = new StringBuilder()
					.append("fail to create a instance of ProjectPartConfiguration class, errmsg=")
					.append(e.getMessage()).toString();

			fail(errorMessage);
		}
		
		ProjectLoginManagerIF projectLoginManager = Mockito.mock(ProjectLoginManagerIF.class);
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(projectPartConfiguration,
					projectLoginManager,
					messageProtocol, 					
					serverObjectCacheManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter poolMaxSize[%d] is less than or equal to zero", serverExecutorPoolMaxSize);
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterPoolSizeIsGreaterThanTheParameterPoolMaxSize() {
		String projectName = "sample_test";
		int serverExecutorPoolSize = 2;
		int serverExecutorPoolMaxSize = 1;	
		int serverInputMessageQueueSize = 10;
		
		ProjectPartConfiguration projectPartConfiguration = null;
		try {
			projectPartConfiguration = buildMainProjectPartConfiguration(projectName,
					serverExecutorPoolSize,
					serverExecutorPoolMaxSize,
					serverInputMessageQueueSize);
		} catch (CoddaConfigurationException e) {
			log.warn("error", e);

			String errorMessage = new StringBuilder()
					.append("fail to create a instance of ProjectPartConfiguration class, errmsg=")
					.append(e.getMessage()).toString();

			fail(errorMessage);
		}
				
		ProjectLoginManagerIF projectLoginManager = Mockito.mock(ProjectLoginManagerIF.class);
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(projectPartConfiguration,
					projectLoginManager,
					messageProtocol, 					
					serverObjectCacheManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter poolSize[%d] is greater than the parameter poolMaxSize[%d]", 
					serverExecutorPoolSize, serverExecutorPoolMaxSize);
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterProjectName_null() {
		String projectName = null;
		int serverExecutorPoolSize = 1;
		int serverExecutorPoolMaxSize = 2;	
		int serverInputMessageQueueSize = 10;
		
		ProjectPartConfiguration projectPartConfiguration = null;
		try {
			projectPartConfiguration = buildMainProjectPartConfiguration(projectName,
					serverExecutorPoolSize,
					serverExecutorPoolMaxSize,
					serverInputMessageQueueSize);
		} catch (CoddaConfigurationException e) {
			log.warn("error", e);

			String errorMessage = new StringBuilder()
					.append("fail to create a instance of ProjectPartConfiguration class, errmsg=")
					.append(e.getMessage()).toString();

			fail(errorMessage);
		}
				
		ProjectLoginManagerIF projectLoginManager = Mockito.mock(ProjectLoginManagerIF.class);
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(projectPartConfiguration,
					projectLoginManager,
					messageProtocol, 					
					serverObjectCacheManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = "the parameter projectName is null";
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterInputMessageQueueSize_lessThanOne() {
		String projectName = "sample_test";
		int serverExecutorPoolSize = 1;
		int serverExecutorPoolMaxSize = 2;	
		int serverInputMessageQueueSize = 0;
		
		ProjectPartConfiguration projectPartConfiguration = null;
		try {
			projectPartConfiguration = buildMainProjectPartConfiguration(projectName,
					serverExecutorPoolSize,
					serverExecutorPoolMaxSize,
					serverInputMessageQueueSize);
		} catch (CoddaConfigurationException e) {
			log.warn("error", e);

			String errorMessage = new StringBuilder()
					.append("fail to create a instance of ProjectPartConfiguration class, errmsg=")
					.append(e.getMessage()).toString();

			fail(errorMessage);
		}
				
		ProjectLoginManagerIF projectLoginManager = Mockito.mock(ProjectLoginManagerIF.class);
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(projectPartConfiguration,
					projectLoginManager,
					messageProtocol, 					
					serverObjectCacheManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter inputMessageQueueSize[%d] is less than or equal to zero", serverInputMessageQueueSize);
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterProjectLoginManager_null() {
		String projectName = "sample_test";
		int serverExecutorPoolSize = 1;
		int serverExecutorPoolMaxSize = 2;	
		int serverInputMessageQueueSize = 1;
		
		ProjectPartConfiguration projectPartConfiguration = null;
		try {
			projectPartConfiguration = buildMainProjectPartConfiguration(projectName,
					serverExecutorPoolSize,
					serverExecutorPoolMaxSize,
					serverInputMessageQueueSize);
		} catch (CoddaConfigurationException e) {
			log.warn("error", e);

			String errorMessage = new StringBuilder()
					.append("fail to create a instance of ProjectPartConfiguration class, errmsg=")
					.append(e.getMessage()).toString();

			fail(errorMessage);
		}
		
		ProjectLoginManagerIF projectLoginManager = null;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(projectPartConfiguration,
					projectLoginManager,
					messageProtocol, 					
					serverObjectCacheManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = "the parameter projectLoginManager is null";
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterMessageProtocol_null() {
		String projectName = "sample_test";
		int serverExecutorPoolSize = 1;
		int serverExecutorPoolMaxSize = 2;	
		int serverInputMessageQueueSize = 1;
		
		ProjectPartConfiguration projectPartConfiguration = null;
		try {
			projectPartConfiguration = buildMainProjectPartConfiguration(projectName,
					serverExecutorPoolSize,
					serverExecutorPoolMaxSize,
					serverInputMessageQueueSize);
		} catch (CoddaConfigurationException e) {
			log.warn("error", e);

			String errorMessage = new StringBuilder()
					.append("fail to create a instance of ProjectPartConfiguration class, errmsg=")
					.append(e.getMessage()).toString();

			fail(errorMessage);
		}
		
		ProjectLoginManagerIF projectLoginManager = Mockito.mock(ProjectLoginManagerIF.class);
		MessageProtocolIF messageProtocol = null;
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(projectPartConfiguration,
					projectLoginManager,
					messageProtocol, 					
					serverObjectCacheManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = "the parameter messageProtocol is null";
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	
	@Test
	public void testConstructor_theParameterServerObjectCacheManager_null() {
		String projectName = "sample_test";
		int serverExecutorPoolSize = 1;
		int serverExecutorPoolMaxSize = 2;	
		int serverInputMessageQueueSize = 1;
		
		ProjectPartConfiguration projectPartConfiguration = null;
		try {
			projectPartConfiguration = buildMainProjectPartConfiguration(projectName,
					serverExecutorPoolSize,
					serverExecutorPoolMaxSize,
					serverInputMessageQueueSize);
		} catch (CoddaConfigurationException e) {
			log.warn("error", e);

			String errorMessage = new StringBuilder()
					.append("fail to create a instance of ProjectPartConfiguration class, errmsg=")
					.append(e.getMessage()).toString();

			fail(errorMessage);
		}
		
		ProjectLoginManagerIF projectLoginManager = Mockito.mock(ProjectLoginManagerIF.class);
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = null;
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(projectPartConfiguration,
					projectLoginManager,
					messageProtocol, 					
					serverObjectCacheManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = "the parameter serverObjectCacheManager is null";
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	
	@Test
	public void testConstructor_TheRealPoolSizeIsSameToTheParameterPoolSize() {
		String projectName = "sample_test";
		int serverExecutorPoolSize = 1;
		int serverExecutorPoolMaxSize = 2;	
		int serverInputMessageQueueSize = 1;
		
		ProjectPartConfiguration projectPartConfiguration = null;
		try {
			projectPartConfiguration = buildMainProjectPartConfiguration(projectName,
					serverExecutorPoolSize,
					serverExecutorPoolMaxSize,
					serverInputMessageQueueSize);
		} catch (CoddaConfigurationException e) {
			log.warn("error", e);

			String errorMessage = new StringBuilder()
					.append("fail to create a instance of ProjectPartConfiguration class, errmsg=")
					.append(e.getMessage()).toString();

			fail(errorMessage);
		}	
		
		ProjectLoginManagerIF projectLoginManager = Mockito.mock(ProjectLoginManagerIF.class);
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		try {
			ServerExecutorPool executorPool = new ServerExecutorPool(projectPartConfiguration,
					projectLoginManager,
					messageProtocol, 					
					serverObjectCacheManager);			

			int auctualPoolSize = executorPool.getPoolSize();
			
			assertEquals("실제 폴 크기가 파라미터 값으로 지정한 폴크기와 같은지 검사", serverExecutorPoolSize, auctualPoolSize);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testAddTask_ok() {
		String projectName = "sample_test";
		int serverExecutorPoolSize = 2;
		int serverExecutorPoolMaxSize = 3;	
		int serverInputMessageQueueSize = 1;
		
		ProjectPartConfiguration projectPartConfiguration = null;
		try {
			projectPartConfiguration = buildMainProjectPartConfiguration(projectName,
					serverExecutorPoolSize,
					serverExecutorPoolMaxSize,
					serverInputMessageQueueSize);
		} catch (CoddaConfigurationException e) {
			log.warn("error", e);

			String errorMessage = new StringBuilder()
					.append("fail to create a instance of ProjectPartConfiguration class, errmsg=")
					.append(e.getMessage()).toString();

			fail(errorMessage);
		}	
		
		ProjectLoginManagerIF projectLoginManager = Mockito.mock(ProjectLoginManagerIF.class);
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		try {
			ServerExecutorPool executorPool = new ServerExecutorPool(projectPartConfiguration,
					projectLoginManager,
					messageProtocol, 					
					serverObjectCacheManager);
			
			executorPool.addTask();
			
			int auctualPoolSize = executorPool.getPoolSize();
			
			assertEquals("1개 추가된 폴 크기 검사", serverExecutorPoolSize+1, auctualPoolSize);
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testAddTask_greaterThanTheParameterPoolMaxSize() {
		String projectName = "sample_test";
		int serverExecutorPoolSize = 2;
		int serverExecutorPoolMaxSize = 3;	
		int serverInputMessageQueueSize = 1;
		
		ProjectPartConfiguration projectPartConfiguration = null;
		try {
			projectPartConfiguration = buildMainProjectPartConfiguration(projectName,
					serverExecutorPoolSize,
					serverExecutorPoolMaxSize,
					serverInputMessageQueueSize);
		} catch (CoddaConfigurationException e) {
			log.warn("error", e);

			String errorMessage = new StringBuilder()
					.append("fail to create a instance of ProjectPartConfiguration class, errmsg=")
					.append(e.getMessage()).toString();

			fail(errorMessage);
		}	
		
		ProjectLoginManagerIF projectLoginManager = Mockito.mock(ProjectLoginManagerIF.class);
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		try {
			ServerExecutorPool executorPool = new ServerExecutorPool(projectPartConfiguration,
					projectLoginManager,
					messageProtocol, 					
					serverObjectCacheManager);
			
			executorPool.addTask();			
			executorPool.addTask();
			
			fail("no IllegalStateException");
		} catch(IllegalStateException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = new StringBuilder("can't add a ServerExecutor in the project[")
					.append(projectName)
					.append("] becase the number of ServerExecutor is maximum[")
					.append(serverExecutorPoolMaxSize)
					.append("]").toString();
			
			assertEquals(exepecedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testGetExecutorWithMinimumNumberOfSockets() {
		String projectName = "sample_test";
		int serverExecutorPoolSize = 2;
		int serverExecutorPoolMaxSize = 3;	
		int serverInputMessageQueueSize = 1;
		
		ProjectPartConfiguration projectPartConfiguration = null;
		try {
			projectPartConfiguration = buildMainProjectPartConfiguration(projectName,
					serverExecutorPoolSize,
					serverExecutorPoolMaxSize,
					serverInputMessageQueueSize);
		} catch (CoddaConfigurationException e) {
			log.warn("error", e);

			String errorMessage = new StringBuilder()
					.append("fail to create a instance of ProjectPartConfiguration class, errmsg=")
					.append(e.getMessage()).toString();

			fail(errorMessage);
		}	
		
		ProjectLoginManagerIF projectLoginManager = Mockito.mock(ProjectLoginManagerIF.class);
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		try {
			ServerExecutorPool executorPool = new ServerExecutorPool(projectPartConfiguration,
					projectLoginManager,
					messageProtocol, 					
					serverObjectCacheManager);
			
			ServerExecutorIF minServerExecutor = executorPool.getExecutorWithMinimumNumberOfSockets();
			minServerExecutor.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minServerExecutor.getNumberOfConnection());
			
			minServerExecutor = executorPool.getExecutorWithMinimumNumberOfSockets();
			minServerExecutor.addNewSocket(SocketChannel.open());
			
			// int num01 = minServerExecutor01.getNumberOfSocket();
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minServerExecutor.getNumberOfConnection());
			
			minServerExecutor = executorPool.getExecutorWithMinimumNumberOfSockets();
			minServerExecutor.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minServerExecutor.getNumberOfConnection());
			
			minServerExecutor = executorPool.getExecutorWithMinimumNumberOfSockets();
			minServerExecutor.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minServerExecutor.getNumberOfConnection());
			
			minServerExecutor = executorPool.getExecutorWithMinimumNumberOfSockets();
			minServerExecutor.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 3, minServerExecutor.getNumberOfConnection());			
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
}
