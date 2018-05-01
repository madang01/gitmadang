package kr.pe.sinnori.server.threadpool.outputmessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.nio.channels.SocketChannel;

import org.junit.Test;
import org.mockito.Mockito;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.server.threadpool.IEOServerThreadPoolSetManagerIF;

public class OutputMessageWriterPoolTest extends AbstractJunitTest {

	@Test
	public void testConstructor_theParameterPoolSize_lessThanOne() {
		int poolSize = 0;
		int poolMaxSize = 10;
		String projectName = "sample_test";
		int outputMessageQueueSize = 10;
		DataPacketBufferPoolIF dataPacketBufferPool = Mockito.mock(DataPacketBufferPoolIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			OutputMessageWriterPool outputMessageWriterPool = new OutputMessageWriterPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					outputMessageQueueSize, 
					dataPacketBufferPool,
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
		int outputMessageQueueSize = 10;
		DataPacketBufferPoolIF dataPacketBufferPool = Mockito.mock(DataPacketBufferPoolIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			OutputMessageWriterPool outputMessageWriterPool = new OutputMessageWriterPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					outputMessageQueueSize, 
					dataPacketBufferPool,
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
		int outputMessageQueueSize = 10;
		DataPacketBufferPoolIF dataPacketBufferPool = Mockito.mock(DataPacketBufferPoolIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			OutputMessageWriterPool outputMessageWriterPool = new OutputMessageWriterPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					outputMessageQueueSize, 
					dataPacketBufferPool,
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
		int outputMessageQueueSize = 0;
		DataPacketBufferPoolIF dataPacketBufferPool = Mockito.mock(DataPacketBufferPoolIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			OutputMessageWriterPool outputMessageWriterPool = new OutputMessageWriterPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					outputMessageQueueSize, 
					dataPacketBufferPool,
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
	public void testConstructor_theParameterOutputMessageQueueSize_lessThanOne() {
		int poolSize = 1;
		int poolMaxSize = 2;
		String projectName = "sample_test";
		int outputMessageQueueSize = 0;
		DataPacketBufferPoolIF dataPacketBufferPool = Mockito.mock(DataPacketBufferPoolIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			OutputMessageWriterPool outputMessageWriterPool = new OutputMessageWriterPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					outputMessageQueueSize, 
					dataPacketBufferPool,
					ieoThreadPoolManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter outputMessageQueueSize[%d] is less than or equal to zero", outputMessageQueueSize);
			
			assertEquals(exepecedErrorMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterDataPacketBufferQueueManger_null() {
		int poolSize = 1;
		int poolMaxSize = 2;
		String projectName = "sample_test";
		int outputMessageQueueSize = 1;
		DataPacketBufferPoolIF dataPacketBufferPool = null;		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			@SuppressWarnings("unused")
			OutputMessageWriterPool outputMessageWriterPool = new OutputMessageWriterPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					outputMessageQueueSize, 
					dataPacketBufferPool,
					ieoThreadPoolManager);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = "the parameter dataPacketBufferPool is null";
			
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
		int outputMessageQueueSize = 1;
		DataPacketBufferPoolIF dataPacketBufferPool = Mockito.mock(DataPacketBufferPoolIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = null;
		
		try {
			@SuppressWarnings("unused")
			OutputMessageWriterPool outputMessageWriterPool = new OutputMessageWriterPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					outputMessageQueueSize, 
					dataPacketBufferPool,
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
	public void testConstructor_theMethodSetOutputMessageWriterPool_1번호출여부() {
		int poolSize = 1;
		int poolMaxSize = 2;
		String projectName = "sample_test";
		int outputMessageQueueSize = 1;
		DataPacketBufferPoolIF dataPacketBufferPool = Mockito.mock(DataPacketBufferPoolIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			OutputMessageWriterPool outputMessageWriterPool = new OutputMessageWriterPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					outputMessageQueueSize, 
					dataPacketBufferPool,
					ieoThreadPoolManager);
			

			verify(ieoThreadPoolManager, times(1)).setOutputMessageWriterPool(outputMessageWriterPool);	
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
		int outputMessageQueueSize = 1;
		DataPacketBufferPoolIF dataPacketBufferPool = Mockito.mock(DataPacketBufferPoolIF.class);
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			OutputMessageWriterPool outputMessageWriterPool = new OutputMessageWriterPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					outputMessageQueueSize, 
					dataPacketBufferPool,
					ieoThreadPoolManager);
			

			int auctualPoolSize = outputMessageWriterPool.getPoolSize();
			
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
		int outputMessageQueueSize = 1;
		DataPacketBufferPoolIF dataPacketBufferPool = Mockito.mock(DataPacketBufferPoolIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		try {
			OutputMessageWriterPool outputMessageWriterPool = new OutputMessageWriterPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					outputMessageQueueSize, 
					dataPacketBufferPool,
					ieoThreadPoolManager);
			
			outputMessageWriterPool.addTask();
			
			int auctualPoolSize = outputMessageWriterPool.getPoolSize();
			
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
		int outputMessageQueueSize = 1;
		DataPacketBufferPoolIF dataPacketBufferPool = Mockito.mock(DataPacketBufferPoolIF.class);
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);
		
		
		try {
			OutputMessageWriterPool outputMessageWriterPool = new OutputMessageWriterPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					outputMessageQueueSize, 
					dataPacketBufferPool,
					ieoThreadPoolManager);
			
			outputMessageWriterPool.addTask();			
			outputMessageWriterPool.addTask();
			
			fail("no IllegalStateException");
		} catch(IllegalStateException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = 
					new StringBuilder("can't add a OutputMessageWriter in the project[")
					.append(projectName)
					.append("] becase the number of OutputMessageWriter is maximum[")
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
		int outputMessageQueueSize = 1;
		DataPacketBufferPoolIF dataPacketBufferPool = Mockito.mock(DataPacketBufferPoolIF.class);		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = Mockito.mock(IEOServerThreadPoolSetManagerIF.class);		
		
		try {
			OutputMessageWriterPool outputMessageWriterPool = new OutputMessageWriterPool(				 
					poolSize,
					poolMaxSize,
					projectName,
					outputMessageQueueSize, 
					dataPacketBufferPool,
					ieoThreadPoolManager);
			
			OutputMessageWriterIF minOutputMessageWriter = outputMessageWriterPool.getOutputMessageWriterWithMinimumNumberOfSockets();
			minOutputMessageWriter.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minOutputMessageWriter.getNumberOfConnection());
			
			minOutputMessageWriter = outputMessageWriterPool.getOutputMessageWriterWithMinimumNumberOfSockets();
			minOutputMessageWriter.addNewSocket(SocketChannel.open());
			
			// int num01 = minServerExecutor01.getNumberOfSocket();
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minOutputMessageWriter.getNumberOfConnection());
			
			minOutputMessageWriter = outputMessageWriterPool.getOutputMessageWriterWithMinimumNumberOfSockets();
			minOutputMessageWriter.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minOutputMessageWriter.getNumberOfConnection());
			
			minOutputMessageWriter = outputMessageWriterPool.getOutputMessageWriterWithMinimumNumberOfSockets();
			minOutputMessageWriter.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minOutputMessageWriter.getNumberOfConnection());
			
			minOutputMessageWriter = outputMessageWriterPool.getOutputMessageWriterWithMinimumNumberOfSockets();
			minOutputMessageWriter.addNewSocket(SocketChannel.open());
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 3, minOutputMessageWriter.getNumberOfConnection());			
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
}
