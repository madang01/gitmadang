package kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage;

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

public class InputMessageWriterPoolTest extends AbstractJunitTest {

	@Test
	public void testConstructor_theParameterPoolSize_lessThanOne() {
		int poolSize = 0;
		String projectName = "sample_test";
		int inputMessageQueueSize = 1;
		ClientMessageUtilityIF clientMessageUtility = Mockito.mock(ClientMessageUtilityIF.class);

		try {
			@SuppressWarnings("unused")
			InputMessageWriterPool inputMessageWriterPool = new InputMessageWriterPool(poolSize, projectName,
					inputMessageQueueSize, clientMessageUtility);

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
		int inputMessageQueueSize = 1;
		ClientMessageUtilityIF clientMessageUtility = Mockito.mock(ClientMessageUtilityIF.class);

		try {
			@SuppressWarnings("unused")
			InputMessageWriterPool inputMessageWriterPool = new InputMessageWriterPool(poolSize, projectName,
					inputMessageQueueSize, clientMessageUtility);

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
	public void testConstructor_theParameterInputMessageQueueSize_lessThanOne() {
		int poolSize = 1;
		String projectName = "sample_test";
		int inputMessageQueueSize = 0;
		ClientMessageUtilityIF clientMessageUtility = Mockito.mock(ClientMessageUtilityIF.class);

		try {
			@SuppressWarnings("unused")
			InputMessageWriterPool inputMessageWriterPool = new InputMessageWriterPool(poolSize, projectName,
					inputMessageQueueSize, clientMessageUtility);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter inputMessageQueueSize[%d] is less than or equal to zero",
					inputMessageQueueSize);

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
		int inputMessageQueueSize = 1;
		ClientMessageUtilityIF clientMessageUtility = null;

		try {
			@SuppressWarnings("unused")
			InputMessageWriterPool inputMessageWriterPool = new InputMessageWriterPool(poolSize, projectName,
					inputMessageQueueSize, clientMessageUtility);

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
		int inputMessageQueueSize = 1;
		ClientMessageUtilityIF clientMessageUtility = Mockito.mock(ClientMessageUtilityIF.class);

		try {
			InputMessageWriterPool inputMessageWriterPool = new InputMessageWriterPool(poolSize, projectName,
					inputMessageQueueSize, clientMessageUtility);

			
			assertEquals("지정한 폴 크기가 맞는지 검사", poolSize,  inputMessageWriterPool.getPoolSize());
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
			InputMessageWriterPool inputMessageWriterPool = new InputMessageWriterPool(poolSize, projectName,
					inputMessageQueueSize, clientMessageUtility);

			
			inputMessageWriterPool.addTask();
			
			fail("no NotSupportedException");
		} catch (NotSupportedException e) {
			String errorMessage = e.getMessage();
			String expcetedErrorMessage = "this InputMessageWriterPool dosn't support this addTask method";			
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
			InputMessageWriterPool inputMessageWriterPool = new InputMessageWriterPool(poolSize, projectName,
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
			
			InputMessageWriterIF minInputMessageWriter = inputMessageWriterPool.getInputMessageWriterWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minInputMessageWriter.registerAsynConnection(asynConnection);
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minInputMessageWriter.getNumberOfAsynConnection());
			
			minInputMessageWriter = inputMessageWriterPool.getInputMessageWriterWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minInputMessageWriter.registerAsynConnection(asynConnection);
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minInputMessageWriter.getNumberOfAsynConnection());
			
			minInputMessageWriter = inputMessageWriterPool.getInputMessageWriterWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minInputMessageWriter.registerAsynConnection(asynConnection);
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minInputMessageWriter.getNumberOfAsynConnection());			
			
			minInputMessageWriter = inputMessageWriterPool.getInputMessageWriterWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minInputMessageWriter.registerAsynConnection(asynConnection);
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minInputMessageWriter.getNumberOfAsynConnection());			
			
			minInputMessageWriter = inputMessageWriterPool.getInputMessageWriterWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minInputMessageWriter.registerAsynConnection(asynConnection);
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 3, minInputMessageWriter.getNumberOfAsynConnection());	
			
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
}
