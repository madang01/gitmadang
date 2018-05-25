package kr.pe.codda.server.threadpool.executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.channels.SocketChannel;

import org.junit.Test;
import org.mockito.Mockito;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.server.ServerObjectCacheManagerIF;
import kr.pe.codda.server.AcceptedConnectionManagerIF;
import kr.pe.codda.server.ProjectLoginManager;
import kr.pe.codda.server.ProjectLoginManagerIF;

public class ServerExecutorPoolTest extends AbstractJunitTest {

	@Test
	public void testConstructor_theParameterPoolSize_lessThanOne() {
		int poolSize = 0;
		int poolMaxSize = 10;
		String projectName = "sample_test";
		int inputMessageQueueSize = 10;
		ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		AcceptedConnectionManagerIF socketResourceManager = Mockito.mock(AcceptedConnectionManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize,
					projectLoginManager,
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter poolSize[%d] is less than or equal to zero", poolSize);
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterPoolMaxSize_lessThanOne() {
		int poolSize = 1;
		int poolMaxSize = 0;
		String projectName = "sample_test";
		int inputMessageQueueSize = 10;
		ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		AcceptedConnectionManagerIF socketResourceManager = Mockito.mock(AcceptedConnectionManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
				
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize,
					projectLoginManager,
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter poolMaxSize[%d] is less than or equal to zero", poolMaxSize);
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterPoolSizeIsGreaterThanTheParameterPoolMaxSize() {
		int poolSize = 2;
		int poolMaxSize = 1;
		String projectName = "sample_test";
		int inputMessageQueueSize = 10;
		ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		AcceptedConnectionManagerIF socketResourceManager = Mockito.mock(AcceptedConnectionManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
				
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					projectLoginManager,
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter poolSize[%d] is greater than the parameter poolMaxSize[%d]", poolSize, poolMaxSize);
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterProjectName_null() {
		int poolSize = 1;
		int poolMaxSize = 2;
		String projectName = null;
		int inputMessageQueueSize = 0;
		ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		AcceptedConnectionManagerIF socketResourceManager = Mockito.mock(AcceptedConnectionManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
				
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					projectLoginManager,
					messageProtocol, 
					socketResourceManager,
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
		int poolSize = 1;
		int poolMaxSize = 2;
		String projectName = "sample_test";
		int inputMessageQueueSize = 0;
		ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		AcceptedConnectionManagerIF socketResourceManager = Mockito.mock(AcceptedConnectionManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
				
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize,
					projectLoginManager,
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter inputMessageQueueSize[%d] is less than or equal to zero", inputMessageQueueSize);
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterMessageProtocol_null() {
		int poolSize = 1;
		int poolMaxSize = 2;
		String projectName = "sample_test";
		int inputMessageQueueSize = 1;
		ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();
		MessageProtocolIF messageProtocol = null;
		AcceptedConnectionManagerIF socketResourceManager = Mockito.mock(AcceptedConnectionManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
				
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize,
					projectLoginManager,
					messageProtocol, 
					socketResourceManager,
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
	public void testConstructor_theParameterSocketResourceManager_null() {
		int poolSize = 1;
		int poolMaxSize = 2;
		String projectName = "sample_test";
		int inputMessageQueueSize = 1;
		ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		AcceptedConnectionManagerIF socketResourceManager = null;
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
				
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize,
					projectLoginManager,
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = "the parameter socketResourceManager is null";
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterServerObjectCacheManager_null() {
		int poolSize = 1;
		int poolMaxSize = 2;
		String projectName = "sample_test";
		int inputMessageQueueSize = 1;
		ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		AcceptedConnectionManagerIF socketResourceManager = Mockito.mock(AcceptedConnectionManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = null;
				
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					projectLoginManager,
					messageProtocol, 
					socketResourceManager,
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
		int poolSize = 1;
		int poolMaxSize = 2;
		String projectName = "sample_test";
		int inputMessageQueueSize = 1;
		ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		AcceptedConnectionManagerIF socketResourceManager = Mockito.mock(AcceptedConnectionManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);		
		
		try {
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					projectLoginManager,
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager);
			

			int auctualPoolSize = executorPool.getPoolSize();
			
			assertEquals("실제 폴 크기가 파라미터 값으로 지정한 폴크기와 같은지 검사", poolSize, auctualPoolSize);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testAddTask_ok() {
		int poolSize = 2;
		int poolMaxSize = 3;
		String projectName = "sample_test";
		int inputMessageQueueSize = 1;
		ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);		
		AcceptedConnectionManagerIF socketResourceManager = Mockito.mock(AcceptedConnectionManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		try {
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize,
					projectLoginManager,
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager);
			
			executorPool.addTask();
			
			int auctualPoolSize = executorPool.getPoolSize();
			
			assertEquals("1개 추가된 폴 크기 검사", poolSize+1, auctualPoolSize);
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testAddTask_greaterThanTheParameterPoolMaxSize() {
		int poolSize = 2;
		int poolMaxSize = 3;
		String projectName = "sample_test";
		int inputMessageQueueSize = 1;
		ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);		
		AcceptedConnectionManagerIF socketResourceManager = Mockito.mock(AcceptedConnectionManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		try {
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					projectLoginManager,
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager);
			
			executorPool.addTask();			
			executorPool.addTask();
			
			fail("no IllegalStateException");
		} catch(IllegalStateException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = new StringBuilder("can't add a ServerExecutor in the project[")
					.append(projectName)
					.append("] becase the number of ServerExecutor is maximum[")
					.append(poolMaxSize)
					.append("]").toString();
			
			assertEquals(exepecedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testGetExecutorWithMinimumNumberOfSockets() {
		int poolSize = 2;
		int poolMaxSize = 5;
		String projectName = "sample_test";
		int inputMessageQueueSize = 1;
		ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		AcceptedConnectionManagerIF socketResourceManager = Mockito.mock(AcceptedConnectionManagerIF.class);		
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
				
		try {
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					projectLoginManager,
					messageProtocol, 
					socketResourceManager,
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
