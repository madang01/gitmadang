package kr.pe.sinnori.client.connection.asyn.threadpool.executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.junit.Test;
import org.mockito.Mockito;

import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.client.connection.asyn.IOEAsynConnectionIF;
import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.io.SocketOutputStream;

public class ClientExecutorPoolTest extends AbstractJunitTest {

	@Test
	public void testConstructor_theParameterPoolSize_lessThanOne() {
		int poolSize = 0;
		String projectName = "sample_test";
		int outputMessageQueueSize = 1;
		ClientMessageUtilityIF clientMessageUtility = Mockito.mock(ClientMessageUtilityIF.class);

		try {
			@SuppressWarnings("unused")
			ClientExecutorPool clientExecutorPool = new ClientExecutorPool(poolSize, projectName,
					outputMessageQueueSize, clientMessageUtility);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter poolSize[%d] is less than or equal to zero",
					poolSize);

			assertEquals(exepecedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterProjectName_null() {
		int poolSize = 1;
		String projectName = null;
		int outputMessageQueueSize = 1;
		ClientMessageUtilityIF clientMessageUtility = Mockito.mock(ClientMessageUtilityIF.class);

		try {
			@SuppressWarnings("unused")
			ClientExecutorPool clientExecutorPool = new ClientExecutorPool(poolSize, projectName,
					outputMessageQueueSize, clientMessageUtility);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
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
		String projectName = "sample_test";
		int outputMessageQueueSize = 0;
		ClientMessageUtilityIF clientMessageUtility = Mockito.mock(ClientMessageUtilityIF.class);

		try {
			@SuppressWarnings("unused")
			ClientExecutorPool clientExecutorPool = new ClientExecutorPool(poolSize, projectName,
					outputMessageQueueSize, clientMessageUtility);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter outputMessageQueueSize[%d] is less than or equal to zero",
					outputMessageQueueSize);

			assertEquals(exepecedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterClientMessageUtility_null() {
		int poolSize = 1;
		String projectName = "sample_test";
		int outputMessageQueueSize = 1;
		ClientMessageUtilityIF clientMessageUtility = null;

		try {
			@SuppressWarnings("unused")
			ClientExecutorPool clientExecutorPool = new ClientExecutorPool(poolSize, projectName,
					outputMessageQueueSize, clientMessageUtility);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = "the parameter clientMessageUtility is null";

			assertEquals(exepecedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_ok() {
		int poolSize = 2;
		String projectName = "sample_test";
		int outputMessageQueueSize = 1;
		ClientMessageUtilityIF clientMessageUtility = Mockito.mock(ClientMessageUtilityIF.class);

		try {
			ClientExecutorPool clientExecutorPool = new ClientExecutorPool(poolSize, projectName,
					outputMessageQueueSize, clientMessageUtility);
			
			assertEquals("지정한 폴 크기가 맞는지 검사", poolSize,  clientExecutorPool.getPoolSize());
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testAddTask() {
		int poolSize = 1;
		String projectName = "sample_test";
		int inputMessageQueueSize = 1;
		ClientMessageUtilityIF clientMessageUtility = Mockito.mock(ClientMessageUtilityIF.class);

		try {
			ClientExecutorPool clientExecutorPool = new ClientExecutorPool(poolSize, projectName,
					inputMessageQueueSize, clientMessageUtility);

			
			clientExecutorPool.addTask();
			
			fail("no NotSupportedException");
		} catch (NotSupportedException e) {
			String errorMessage = e.getMessage();
			String expcetedErrorMessage = "this ClientExecutorPool dosn't support this addTask method";			
			assertEquals(expcetedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testGetInputMessageWriterWithMinimumNumberOfConnetion() {
		int poolSize = 2;
		String projectName = "sample_test";
		int inputMessageQueueSize = 1;
		ClientMessageUtilityIF clientMessageUtility = Mockito.mock(ClientMessageUtilityIF.class);

		try {
			ClientExecutorPool clientExecutorPool = new ClientExecutorPool(poolSize, projectName,
					inputMessageQueueSize, clientMessageUtility);

			
			class AsynConnectionMock implements  IOEAsynConnectionIF {
				private SocketChannel socketChannel = null;
				
				public AsynConnectionMock() {
					try {
						socketChannel = SocketChannel.open();
					} catch (IOException e) {
						fail("fail to get a new socket channel");
					}
				}
				

				@Override
				public void putToOutputMessageQueue(FromLetter fromLetter) throws InterruptedException {
				}

				@Override
				public SocketChannel getSocketChannel() {
					return socketChannel;
				}


				@Override
				public SocketOutputStream getSocketOutputStream() {
					return null;
				}


				@Override
				public void setFinalReadTime() {					
				}


				@Override
				public String getSimpleConnectionInfo() {
					return null;
				}


				@Override
				public void noticeThisConnectionWasRemovedFromReadyOnleySelector() {
				}
				
			}
			
			IOEAsynConnectionIF asynConnection = null;
			
			ClientExecutorIF minClientExecutor = clientExecutorPool.getClientExecutorWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minClientExecutor.registerAsynConnection(asynConnection);
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minClientExecutor.getNumberOfConnection());
			
			minClientExecutor = clientExecutorPool.getClientExecutorWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minClientExecutor.registerAsynConnection(asynConnection);
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minClientExecutor.getNumberOfConnection());
			
			minClientExecutor = clientExecutorPool.getClientExecutorWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minClientExecutor.registerAsynConnection(asynConnection);
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minClientExecutor.getNumberOfConnection());			
			
			minClientExecutor = clientExecutorPool.getClientExecutorWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minClientExecutor.registerAsynConnection(asynConnection);
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minClientExecutor.getNumberOfConnection());			
			
			minClientExecutor = clientExecutorPool.getClientExecutorWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minClientExecutor.registerAsynConnection(asynConnection);
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 3, minClientExecutor.getNumberOfConnection());	
			
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
}
