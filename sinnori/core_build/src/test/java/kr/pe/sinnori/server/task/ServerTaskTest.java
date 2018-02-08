package kr.pe.sinnori.server.task;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;



import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.DataPacketBufferPool;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.protocol.thb.THBMessageProtocol;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.impl.message.Echo.Echo;
import kr.pe.sinnori.impl.message.Echo.EchoEncoder;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.threadpool.executor.handler.ExecutorIF;
import kr.pe.sinnori.server.threadpool.inputmessage.handler.InputMessageReaderIF;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

public class ServerTaskTest extends AbstractJunitTest {
	
	@Test
	public void testSelfExn() {
		log.info("{}", SelfExn.ErrorPlace.SERVER);
		log.info("{}", SelfExn.ErrorType.BodyFormatException);
		
		SelfExnRes selfExnREs = new SelfExnRes();
		selfExnREs.setErrorPlace(SelfExn.ErrorPlace.SERVER);
		selfExnREs.setErrorType(SelfExn.ErrorType.BodyFormatException);
		selfExnREs.setErrorMessageID("Echo");
		selfExnREs.setErrorReason("test 1111 2222");
		selfExnREs.messageHeaderInfo.mailboxID = 10;
		selfExnREs.messageHeaderInfo.mailID = 101;
		
		log.info("selfExnREs={}", selfExnREs.toString());
	}
	
	private DataPacketBufferPoolIF getDataPacketBufferPool() {
		ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
		DataPacketBufferPoolIF dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 4096;
		int dataPacketBufferPoolSize = 100;
		
		try {
			dataPacketBufferPoolManager = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		}
		
		return dataPacketBufferPoolManager;
	}
	
	private MessageProtocolIF getMessageProtocol(
			CharsetEncoder streamCharsetEncoder,
			CharsetDecoder streamCharsetDecoder,
			DataPacketBufferPoolIF dataPacketBufferPoolManager) {
		int messageIDFixedSize = 25;
		int dataPacketBufferMaxCntPerMessage = 10;

		THBMessageProtocol thbMessageProtocol = 
				new THBMessageProtocol(messageIDFixedSize, 
						dataPacketBufferMaxCntPerMessage,
						streamCharsetEncoder,
						streamCharsetDecoder,
						dataPacketBufferPoolManager);
		
		return thbMessageProtocol;
		
	}
	
	private WrapReadableMiddleObject getWrapReadableMiddleObject(
			AbstractMessage message,
			AbstractMessageEncoder messageEncoder,
			CharsetDecoder streamCharsetDecoder, 
			int dataPacketBufferMaxCntPerMessage, 
			MessageProtocolIF messageProtocol, 
			DataPacketBufferPoolIF dataPacketBufferPoolManager) {		
		List<WrapBuffer> wrapBufferListOfInputMessage = null;
		try {
			wrapBufferListOfInputMessage = messageProtocol.M2S(message, messageEncoder);
		} catch (Exception e) {
			String errorMessage = "error::"+e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
		
		//log.info("2");
		
		for (WrapBuffer inputMessageWrapBuffer : wrapBufferListOfInputMessage) {
			if (inputMessageWrapBuffer.isInQueue()) {
				fail("bad wrap buffer where is in of queue");
			}
			
			ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
			inputMessageByteBuffer.position(inputMessageByteBuffer.limit());
		}
		
		//log.info("3");
		
		SocketOutputStream sos = null;
		try {
			sos = new SocketOutputStream(wrapBufferListOfInputMessage, streamCharsetDecoder, dataPacketBufferMaxCntPerMessage, dataPacketBufferPoolManager);
		} catch (Exception e) {
			String errorMessage = "error::"+e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
		
		// log.info("sos.size={}", sos.size());
		
		//log.info("4");
		
		ArrayList<WrapReadableMiddleObject> wrapReadableMiddleObjectList = null;
		try {
			wrapReadableMiddleObjectList = messageProtocol.S2MList(sos);
		} catch (Exception e) {
			String errorMessage = "error::"+e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
		
		return wrapReadableMiddleObjectList.get(0);
	}
	
	
	@Test 
	public void testMock1() {
		InputMessageReaderIF inputMessageReaderOfOwnerSC = mock(InputMessageReaderIF.class);
	}
	
	
	@Test 
	public void testMock2() {
		/*class InputMessageReaderMock implements  InputMessageReaderIF {

			@Override
			public void addNewSocket(SocketChannel newSC) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public int getNumberOfSocket() {
				// TODO Auto-generated method stub
				return 0;
			}
			
		}
		
		InputMessageReaderIF inputMessageReaderOfOwnerSC = mock(InputMessageReaderMock.class);*/
		
		List mockedList = mock(List.class);
	}
	
	@Test
	public void testExecute() {
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		
		
		ClassLoader currentClassLoader = this.getClass().getClassLoader();
		final String ClassLoaderClassPackagePrefixName = "kr.pe.sinnori.impl.";
		
		
		int index = 0;
		String projectName = "sample_test";
		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}
		
		DataPacketBufferPoolIF dataPacketBufferPoolManager = getDataPacketBufferPool();
		
		class InputMessageReaderMock implements  InputMessageReaderIF {

			@Override
			public void addNewSocket(SocketChannel newSC) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public int getNumberOfSocket() {
				// TODO Auto-generated method stub
				return 0;
			}
			
		}
		
		InputMessageReaderIF inputMessageReaderOfOwnerSC = new InputMessageReaderMock(); 
		
		
		
		ExecutorIF executorOfOwnerSC = mock(ExecutorIF.class);
		OutputMessageWriterIF outputMessageWriterOfOwnerSC = mock(OutputMessageWriterIF.class);
		// putIntoQueue, ToLetter toLetter
		// when(outputMessageWriterOfOwnerSC.putIntoQueue(null))
		
		SocketOutputStream socketOutputStreamOfOwnerSC = null;
		try {
			socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder, 
					dataPacketBufferMaxCntPerMessage, 
					dataPacketBufferPoolManager);
		} catch (NoMoreDataPacketBufferException e1) {
			fail("fail to get a WrapBuffer from DataPacketBufferPool");
		}
		
		PersonalLoginManagerIF personalLoginManagerOfOwnerSC = mock(PersonalLoginManagerIF.class);
		SocketResource socketResourceOfFromSC = new SocketResource(fromSC, 
				inputMessageReaderOfOwnerSC, 
				executorOfOwnerSC, 
				outputMessageWriterOfOwnerSC,
				socketOutputStreamOfOwnerSC, 
				personalLoginManagerOfOwnerSC);		
		
		MessageProtocolIF messageProtocol = getMessageProtocol(
				streamCharsetEncoder,
				streamCharsetDecoder,
				dataPacketBufferPoolManager);
		
		Echo echo = new Echo();
		echo.setStartTime(new Date().getTime());;
		echo.setRandomInt(new Random().nextInt());
		echo.messageHeaderInfo.mailboxID = 1;
		echo.messageHeaderInfo.mailID = 3;
		
		EchoEncoder echoEncoder = new EchoEncoder();

		WrapReadableMiddleObject wrapReadableMiddleObject = 
				getWrapReadableMiddleObject(echo, 
						echoEncoder,
						streamCharsetDecoder, 
						dataPacketBufferMaxCntPerMessage,
						messageProtocol, dataPacketBufferPoolManager);
		
		
		/*try {
			doAnswer(new Answer<Void>() {
			    public Void answer(InvocationOnMock invocation) {
			      Object[] args = invocation.getArguments();
			      if (1 != args.length) {
			    	  fail("the argument length is not one");
			      }
			      
			      ToLetter actucalToLetter = (ToLetter)args[0];
			      
	    		  log.info("actual toLetter={}", actucalToLetter.toString());
			      // System.out.println("called with arguments: " + Arrays.toString(args));
			      return null;
			    }
			}).when(outputMessageWriterOfOwnerSC).putIntoQueue(any(ToLetter.class));
		} catch (InterruptedException e2) {
			fail("InterruptedException");
		}*/
		
		ServerObjectCacheManagerIF serverObjectCacheManager = mock(ServerObjectCacheManagerIF.class);
		
		try {
			String messageID = echo.getMessageID();
			String classFullName = new StringBuilder(
					ClassLoaderClassPackagePrefixName).append("message.")
					.append(messageID).append(".").append(messageID)
					.append("ServerCodec").toString();
			String errorMessage = String
					.format("fail to get cached object::ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]",
							currentClassLoader.hashCode(), messageID,
							classFullName);
			when(serverObjectCacheManager.getServerMessageCodec(currentClassLoader, anyString())).thenThrow(new DynamicClassCallException(errorMessage));
		} catch (DynamicClassCallException e) {
			fail("DynamicClassCallException");
		}
		
		AbstractServerTask serverTaskMock = mock(AbstractServerTask.class);
		
		/*try {
			doAnswer(new Answer<Void>() {
			    public Void answer(InvocationOnMock invocation) {
			      Object[] args = invocation.getArguments();
			      if (4 == args.length) {
			    	  if (args[2]  instanceof ToLetterCarrier) {
			    		  ToLetterCarrier toLetterCarrier  = (ToLetterCarrier)args[2];
			    		  log.info("toLetter={}", actucalToLetter.toString());
			    	  }
			    	  
			      }
			      // System.out.println("called with arguments: " + Arrays.toString(args));
			      return null;
			    }
			}).when(serverTaskMock).doTask(anyString(), 
					any(PersonalLoginManagerIF.class), any(ToLetterCarrier.class), any(AbstractMessage.class));
		
		} catch (Exception e) {
			fail("Exception");
		}*/
		
		try {
			serverTaskMock.execute(index, 
					projectName, 
					fromSC, 
					socketResourceOfFromSC, 
					wrapReadableMiddleObject, 
					messageProtocol, serverObjectCacheManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
	}
}
