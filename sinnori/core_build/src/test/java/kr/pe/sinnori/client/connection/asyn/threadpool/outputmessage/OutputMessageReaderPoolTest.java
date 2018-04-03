package kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.junit.Test;
import org.mockito.Mockito;

import kr.pe.sinnori.client.connection.asyn.IOEAsynConnectionIF;
import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;

public class OutputMessageReaderPoolTest extends AbstractJunitTest {

	@Test
	public void testConstructor_theParameterPoolSize_lessThanOne() {
		int poolSize = 0;
		String projectName = "sample_test";
		long wakeupIntervalOfSelectorForReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);

		try {
			@SuppressWarnings("unused")
			OutputMessageReaderPool outputMessageReaderPool = new OutputMessageReaderPool(poolSize, projectName,
					wakeupIntervalOfSelectorForReadEventOnly, messageProtocol);

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
		long wakeupIntervalOfSelectorForReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);

		try {
			@SuppressWarnings("unused")
			OutputMessageReaderPool outputMessageReaderPool = new OutputMessageReaderPool(poolSize, projectName,
					wakeupIntervalOfSelectorForReadEventOnly, messageProtocol);

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
	public void testConstructor_theParameterWakeupIntervalOfSelectorForReadEventOnly_lessThanZero() {
		int poolSize = 1;
		String projectName = "sample_test";
		long wakeupIntervalOfSelectorForReadEventOnly = -1;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);

		try {
			@SuppressWarnings("unused")
			OutputMessageReaderPool outputMessageReaderPool = new OutputMessageReaderPool(poolSize, projectName,
					wakeupIntervalOfSelectorForReadEventOnly, messageProtocol);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = String.format("the parameter wakeupIntervalOfSelectorForReadEventOnly[%d] is less than zero",
					wakeupIntervalOfSelectorForReadEventOnly);

			assertEquals(exepecedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterMessageProtocol_null() {
		int poolSize = 1;
		String projectName = "sample_test";
		long wakeupIntervalOfSelectorForReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = null;

		try {
			@SuppressWarnings("unused")
			OutputMessageReaderPool outputMessageReaderPool = new OutputMessageReaderPool(poolSize, projectName,
					wakeupIntervalOfSelectorForReadEventOnly, messageProtocol);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String exepecedErrorMessage = "the parameter messageProtocol is null";

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
		long wakeupIntervalOfSelectorForReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);

		try {
			OutputMessageReaderPool outputMessageReaderPool = new OutputMessageReaderPool(poolSize, projectName,
					wakeupIntervalOfSelectorForReadEventOnly, messageProtocol);

			assertEquals("지정한 폴 크기가 맞는지 검사", poolSize,  outputMessageReaderPool.getPoolSize());
		
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
	
	@Test
	public void testAddTask() {
		int poolSize = 1;
		String projectName = "sample_test";
		long wakeupIntervalOfSelectorForReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);

		try {
			OutputMessageReaderPool outputMessageReaderPool = new OutputMessageReaderPool(poolSize, projectName,
					wakeupIntervalOfSelectorForReadEventOnly, messageProtocol);

			
			outputMessageReaderPool.addTask();
			
			fail("no NotSupportedException");
		} catch (NotSupportedException e) {
			String errorMessage = e.getMessage();
			String expcetedErrorMessage = "this OutputMessageReaderPool dosn't support this addTask method";			
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
		long wakeupIntervalOfSelectorForReadEventOnly = 10L;
		MessageProtocolIF messageProtocol = Mockito.mock(MessageProtocolIF.class);

		try {
			OutputMessageReaderPool outputMessageReaderPool = new OutputMessageReaderPool(poolSize, projectName,
					wakeupIntervalOfSelectorForReadEventOnly, messageProtocol);

			
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
			
			OutputMessageReaderIF minOutputMessageReader = outputMessageReaderPool.getOutputMessageReaderWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minOutputMessageReader.registerAsynConnection(asynConnection);
			
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minOutputMessageReader.getNumberOfConnection());
			
			minOutputMessageReader = outputMessageReaderPool.getOutputMessageReaderWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minOutputMessageReader.registerAsynConnection(asynConnection);
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 1, minOutputMessageReader.getNumberOfConnection());
			
			minOutputMessageReader = outputMessageReaderPool.getOutputMessageReaderWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minOutputMessageReader.registerAsynConnection(asynConnection);
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minOutputMessageReader.getNumberOfConnection());			
			
			minOutputMessageReader = outputMessageReaderPool.getOutputMessageReaderWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minOutputMessageReader.registerAsynConnection(asynConnection);
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 2, minOutputMessageReader.getNumberOfConnection());			
			
			minOutputMessageReader = outputMessageReaderPool.getOutputMessageReaderWithMinimumNumberOfConnetion();
			asynConnection = new AsynConnectionMock();
			minOutputMessageReader.registerAsynConnection(asynConnection);
			assertEquals("균등 분배에 따라 예상되는 소캣수 검사", 3, minOutputMessageReader.getNumberOfConnection());	
			
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("error");
		}
	}
}
