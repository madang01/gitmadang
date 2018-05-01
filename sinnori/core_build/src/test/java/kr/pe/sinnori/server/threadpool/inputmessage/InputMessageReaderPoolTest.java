package kr.pe.sinnori.server.threadpool.inputmessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.nio.channels.SocketChannel;

import org.junit.Test;
import org.mockito.Mockito;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.threadpool.IEOServerThreadPoolSetManagerIF;

public class InputMessageReaderPoolTest extends AbstractJunitTest {
	
	@Test
	public void testConstructor_theParameterPoolSize_lessThanOne() {
		int poolSize = 0;
		int poolMaxSize = 10;
		String projectName = "sample_test";
		long wakeupIntervalOfSelectorFoReadEventOnly = 10;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			InputMessageReaderPool inputMessageReaderPool = new InputMessageReaderPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					wakeupIntervalOfSelectorFoReadEventOnly, 
					messageProtocol, 
					socketResourceManager,
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
		long wakeupIntervalOfSelectorFoReadEventOnly = 10;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			InputMessageReaderPool inputMessageReaderPool = new InputMessageReaderPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					wakeupIntervalOfSelectorFoReadEventOnly, 
					messageProtocol, 
					socketResourceManager,
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
		long wakeupIntervalOfSelectorFoReadEventOnly = 10;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			InputMessageReaderPool inputMessageReaderPool = new InputMessageReaderPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					wakeupIntervalOfSelectorFoReadEventOnly, 
					messageProtocol, 
					socketResourceManager,
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
		long wakeupIntervalOfSelectorFoReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			InputMessageReaderPool inputMessageReaderPool = new InputMessageReaderPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					wakeupIntervalOfSelectorFoReadEventOnly, 
					messageProtocol, 
					socketResourceManager,
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
	public void testConstructor_theParameterWakeupIntervalOfSelectorFoReadEventOnly_lessThanZero() {
		int poolSize = 1;
		int poolMaxSize = 2;
		String projectName = "sample_test";
		long wakeupIntervalOfSelectorFoReadEventOnly = -1L;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			InputMessageReaderPool inputMessageReaderPool = new InputMessageReaderPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					wakeupIntervalOfSelectorFoReadEventOnly, 
					messageProtocol, 
					socketResourceManager,
					ieoThreadPoolManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter wakeupIntervalOfSelectorFoReadEventOnly[%d] is less than zero", wakeupIntervalOfSelectorFoReadEventOnly);
			
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
		long wakeupIntervalOfSelectorFoReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = null;
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			InputMessageReaderPool inputMessageReaderPool = new InputMessageReaderPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					wakeupIntervalOfSelectorFoReadEventOnly, 
					messageProtocol, 
					socketResourceManager,
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
		long wakeupIntervalOfSelectorFoReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = null;		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			InputMessageReaderPool inputMessageReaderPool = new InputMessageReaderPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					wakeupIntervalOfSelectorFoReadEventOnly, 
					messageProtocol, 
					socketResourceManager,
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
	public void testConstructor_theParameterIEOThreadPoolManager_null() {
		int poolSize = 1;
		int poolMaxSize = 2;
		String projectName = "sample_test";
		long wakeupIntervalOfSelectorFoReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);	
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = null;
		
		try {
			@SuppressWarnings("unused")
			InputMessageReaderPool inputMessageReaderPool = new InputMessageReaderPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					wakeupIntervalOfSelectorFoReadEventOnly, 
					messageProtocol, 
					socketResourceManager,
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
		long wakeupIntervalOfSelectorFoReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			InputMessageReaderPool inputMessageReaderPool = new InputMessageReaderPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					wakeupIntervalOfSelectorFoReadEventOnly, 
					messageProtocol, 
					socketResourceManager,
					ieoThreadPoolManager);
			

			verify(ieoThreadPoolManager, times(1)).setInputMessageReaderPool(inputMessageReaderPool);	
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
		long wakeupIntervalOfSelectorFoReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			InputMessageReaderPool inputMessageReaderPool = new InputMessageReaderPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					wakeupIntervalOfSelectorFoReadEventOnly, 
					messageProtocol, 
					socketResourceManager,
					ieoThreadPoolManager);
			

			int auctualPoolSize = inputMessageReaderPool.getPoolSize();
			
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
		long wakeupIntervalOfSelectorFoReadEventOnly = 10L;
		
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			InputMessageReaderPool inputMessageReaderPool = new InputMessageReaderPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					wakeupIntervalOfSelectorFoReadEventOnly, 
					messageProtocol, 
					socketResourceManager,
					ieoThreadPoolManager);
			
			inputMessageReaderPool.addTask();
			
			int auctualPoolSize = inputMessageReaderPool.getPoolSize();
			
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
		long wakeupIntervalOfSelectorFoReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			InputMessageReaderPool inputMessageReaderPool = new InputMessageReaderPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					wakeupIntervalOfSelectorFoReadEventOnly, 
					messageProtocol, 
					socketResourceManager,
					ieoThreadPoolManager);
			
			inputMessageReaderPool.addTask();			
			inputMessageReaderPool.addTask();
			
			fail("no IllegalStateException");
		} catch(IllegalStateException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = new StringBuilder("can't add a InputMessageReader in the project[")
					.append(projectName)
					.append("] becase the number of InputMessageReader is maximum[")
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
		long wakeupIntervalOfSelectorFoReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);
		SocketResourceManagerIF socketResourceManager = Mockito.mock(SocketResourceManagerIF.class);
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			InputMessageReaderPool inputMessageReaderPool = new InputMessageReaderPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					wakeupIntervalOfSelectorFoReadEventOnly, 
					messageProtocol, 
					socketResourceManager,
					ieoThreadPoolManager);
			
			InputMessageReaderIF minInputMessageReader = inputMessageReaderPool.getInputMessageReaderWithMinimumNumberOfSockets();
			minInputMessageReader.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minInputMessageReader.getNumberOfConnection());
			
			minInputMessageReader = inputMessageReaderPool.getInputMessageReaderWithMinimumNumberOfSockets();
			minInputMessageReader.addNewSocket(SocketChannel.open());
			
			// int num01 = minServerExecutor01.getNumberOfSocket();
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minInputMessageReader.getNumberOfConnection());
			
			minInputMessageReader = inputMessageReaderPool.getInputMessageReaderWithMinimumNumberOfSockets();
			minInputMessageReader.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minInputMessageReader.getNumberOfConnection());
			
			minInputMessageReader = inputMessageReaderPool.getInputMessageReaderWithMinimumNumberOfSockets();
			minInputMessageReader.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minInputMessageReader.getNumberOfConnection());
			
			minInputMessageReader = inputMessageReaderPool.getInputMessageReaderWithMinimumNumberOfSockets();
			minInputMessageReader.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 3, minInputMessageReader.getNumberOfConnection());			
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
}
