package kr.pe.sinnori.server.threadpool.executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.nio.channels.SocketChannel;

import org.junit.Test;
import org.mockito.Mockito;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.threadpool.IEOServerThreadPoolSetManagerIF;

public class ServerExecutorPoolTest extends AbstractJunitTest {

	@Test
	public void testConstructor_theParameterPoolSize_lessThanOne() {
		int poolSize = 0;
		int poolMaxSize = 10;
		String projectName = "sample_test";
		int inputMessageQueueSize = 10;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			
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
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			
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
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			
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
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			
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
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			
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
		MessageProtocolIF messageProtocol = null;
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			
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
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = null;
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			
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
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = null;
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			
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
	public void testConstructor_theParameterIEOThreadPoolManager_null() {
		int poolSize = 1;
		int poolMaxSize = 2;
		String projectName = "sample_test";
		int inputMessageQueueSize = 1;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = null;
		
		try {
			@SuppressWarnings("unused")
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = "the parameter ieoThreadPoolManager is null";
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theMethodSetExecutorPool_1번호출여부() {
		int poolSize = 1;
		int poolMaxSize = 2;
		String projectName = "sample_test";
		int inputMessageQueueSize = 1;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			

			verify(ieoThreadPoolManager, times(1)).setExecutorPool(executorPool);	
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
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			

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
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		try {
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			
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
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		
		try {
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			
			executorPool.addTask();			
			executorPool.addTask();
			
			fail("no IllegalStateException");
		} catch(IllegalStateException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("can't add any more tasks becase the number of %s ServerExecutorPool's tasks reached the maximum[%d] number", projectName, poolMaxSize);
			
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
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);		
		ServerObjectCacheManagerIF serverObjectCacheManager = Mockito.mock(ServerObjectCacheManagerIF.class);
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			ServerExecutorPool executorPool = new ServerExecutorPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					inputMessageQueueSize, 
					messageProtocol, 
					socketResourceManager,
					serverObjectCacheManager,
					ieoThreadPoolManager);
			
			ServerExecutorIF minServerExecutor = executorPool.getExecutorWithMinimumNumberOfSockets();
			minServerExecutor.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minServerExecutor.getNumberOfSocket());
			
			minServerExecutor = executorPool.getExecutorWithMinimumNumberOfSockets();
			minServerExecutor.addNewSocket(SocketChannel.open());
			
			// int num01 = minServerExecutor01.getNumberOfSocket();
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minServerExecutor.getNumberOfSocket());
			
			minServerExecutor = executorPool.getExecutorWithMinimumNumberOfSockets();
			minServerExecutor.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minServerExecutor.getNumberOfSocket());
			
			minServerExecutor = executorPool.getExecutorWithMinimumNumberOfSockets();
			minServerExecutor.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minServerExecutor.getNumberOfSocket());
			
			minServerExecutor = executorPool.getExecutorWithMinimumNumberOfSockets();
			minServerExecutor.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 3, minServerExecutor.getNumberOfSocket());			
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
}
