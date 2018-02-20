package kr.pe.sinnori.server.task;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.DataPacketBufferPool;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.FreeSizeInputStream;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.protocol.thb.THBMessageProtocol;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.impl.message.Echo.Echo;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.threadpool.executor.handler.ServerExecutorIF;
import kr.pe.sinnori.server.threadpool.inputmessage.handler.InputMessageReaderIF;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

public class ServerTaskTest extends AbstractJunitTest {
	
	/*@Test
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
		
	*/
	
	@Test
	public void testExecute_failToGetServerMessageCodec_통제된에러() {
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		
		
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil("kr.pe.sinnori.impl.");
		
		/*ClassLoader currentClassLoader = this.getClass().getClassLoader();
		final String ClassLoaderClassPackagePrefixName = "kr.pe.sinnori.impl.";*/
		
		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}
		
		DataPacketBufferPoolIF dataPacketBufferPoolManager = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;
			
			try {
				dataPacketBufferPoolManager = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}
		
		MessageProtocolIF messageProtocol = null;
		{
			int messageIDFixedSize = 25;
			messageProtocol = 
					new THBMessageProtocol(messageIDFixedSize, 
							dataPacketBufferMaxCntPerMessage,
							streamCharsetEncoder,
							streamCharsetDecoder,
							dataPacketBufferPoolManager);
		}		
		
		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);
		
		SocketResource socketResourceOfFromSC = null;
		{	
			InputMessageReaderIF inputMessageReaderOfOwnerSC = mock(InputMessageReaderIF.class);
			ServerExecutorIF executorOfOwnerSC = mock(ServerExecutorIF.class);
			
			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;
				private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage,
						MessageProtocolIF messageProtocol, 
						DataPacketBufferPoolIF dataPacketBufferQueueManager,
						IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
					this.ioPartDynamicClassNameUtil = ioPartDynamicClassNameUtil;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfSocket() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {
				}

				@Override
				public void putIntoQueue(ToLetter toLetter) throws InterruptedException {
					if (null == toLetter) {
						fail("the parameter toLetter is null");
					}
					
					log.info("toLetter={}", toLetter.toString());
					
					
					String messageID = toLetter.getMessageID();
					
					SingleItemDecoderIF dhbSingleItemDecoder = messageProtocol.getSingleItemDecoder();
					
					FreeSizeInputStream fsis = new FreeSizeInputStream(dataPacketBufferMaxCntPerMessage, 
							toLetter.getWrapBufferList(), 
							streamCharsetDecoder, dataPacketBufferQueueManager);
					
					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						
						AbstractMessageDecoder messageDecoder  = (AbstractMessageDecoder)Class.forName(ioPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID)).newInstance();
					
						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);
						
						if (! (resObj instanceof SelfExnRes)) {
							String errorMessage = String.format("this output message[%s] is not a expected SelfExnRes message", 
									resObj.toString());
							fail(errorMessage);
						}
						
						SelfExnRes selfExnRes = (SelfExnRes)resObj;
						if (! selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}
						
						if (! selfExnRes.getErrorType().equals(SelfExn.ErrorType.DynamicClassCallException)) {
							fail("에러 종류가 동적 클래스가 아님");
						}
						
						log.info("성공 :: {}", selfExnRes.toString());
						
					} catch (Error | Exception e) {
						log.warn("error", e);
						fail("unknow error::OutputMessageWriterMock::#putIntoQueue");
					} finally {
						fsis.close();
					}
				} 			
			}
			
			OutputMessageWriterIF outputMessageWriterOfOwnerSC = 
					new OutputMessageWriterMock(streamCharsetDecoder, 
							dataPacketBufferMaxCntPerMessage, 
							messageProtocol, dataPacketBufferPoolManager,
							ioPartDynamicClassNameUtil);
			
			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder, 
						dataPacketBufferMaxCntPerMessage, 
						dataPacketBufferPoolManager);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}
			
			socketResourceOfFromSC = new SocketResource(fromSC, 
					inputMessageReaderOfOwnerSC, 
					executorOfOwnerSC, 
					outputMessageWriterOfOwnerSC,
					socketOutputStreamOfOwnerSC, 
					personalLoginManagerOfFromSC);
		}
		
		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;
			
			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
			}

			@Override
			public void remove(SocketChannel sc) {
			}

			@Override
			public SocketResource getSocketResource(SocketChannel sc) {
				return socketResourceOfFromSC;
			}

			@Override
			public int getNumberOfSocketResources() {
				return 0;
			}
		}
		
		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);
				

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Echo echoReq = new Echo();
			echoReq.setStartTime(new Date().getTime());;
			echoReq.setRandomInt(new Random().nextInt());
			echoReq.messageHeaderInfo.mailboxID = 1;
			echoReq.messageHeaderInfo.mailID = 3;
			
			AbstractMessage message = echoReq;
			
			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder)Class.forName(ioPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID())).newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}
			
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
			
			if (1 != wrapReadableMiddleObjectList.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}
			
			wrapReadableMiddleObject = wrapReadableMiddleObjectList.get(0);
		}
		
		class ServerObjectCacheManagerMock implements ServerObjectCacheManagerIF {

			@Override
			public MessageCodecIF getServerMessageCodec(ClassLoader classLoader, String messageID)
					throws DynamicClassCallException {
				String errorMessage = String.format("fail to get the paramter messageID[%s]'s server message codec", messageID);
				throw new DynamicClassCallException(errorMessage);
			}

			@Override
			public AbstractServerTask getServerTask(String messageID) throws DynamicClassCallException {
				return null;
			}
		}
		
		
		ServerObjectCacheManagerIF serverObjectCacheManager = new ServerObjectCacheManagerMock();

		class AbstractServerTaskMock extends AbstractServerTask {

			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}
			
		}
		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();
		
	    
	    
		try {
			int index = 0;
			String projectName = "sample_test";
			
			serverTaskMock.execute(index, 
					projectName, 
					fromSC, 
					socketResourceManager, 
					socketResourceOfFromSC,
					personalLoginManagerOfFromSC,
					wrapReadableMiddleObject, 
					messageProtocol, serverObjectCacheManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
		
		
		wrapReadableMiddleObject.closeReadableMiddleObject();
		socketResourceOfFromSC.close();
	}
	
	@Test
	public void testExecute_failToGetServerMessageCodec_통제벗어난에러() {
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		
		
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil("kr.pe.sinnori.impl.");
		
		/*ClassLoader currentClassLoader = this.getClass().getClassLoader();
		final String ClassLoaderClassPackagePrefixName = "kr.pe.sinnori.impl.";*/
		
		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}
		
		DataPacketBufferPoolIF dataPacketBufferPoolManager = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;
			
			try {
				dataPacketBufferPoolManager = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}
		
		MessageProtocolIF messageProtocol = null;
		{
			int messageIDFixedSize = 25;
			messageProtocol = 
					new THBMessageProtocol(messageIDFixedSize, 
							dataPacketBufferMaxCntPerMessage,
							streamCharsetEncoder,
							streamCharsetDecoder,
							dataPacketBufferPoolManager);
		}		
		
		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);
		
		SocketResource socketResourceOfFromSC = null;
		{	
			InputMessageReaderIF inputMessageReaderOfOwnerSC = mock(InputMessageReaderIF.class);
			ServerExecutorIF executorOfOwnerSC = mock(ServerExecutorIF.class);
			
			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;
				private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage,
						MessageProtocolIF messageProtocol, 
						DataPacketBufferPoolIF dataPacketBufferQueueManager,
						IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
					this.ioPartDynamicClassNameUtil = ioPartDynamicClassNameUtil;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfSocket() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {
				}

				@Override
				public void putIntoQueue(ToLetter toLetter) throws InterruptedException {
					if (null == toLetter) {
						fail("the parameter toLetter is null");
					}
					
					log.info("toLetter={}", toLetter.toString());
					
					
					String messageID = toLetter.getMessageID();
					
					SingleItemDecoderIF dhbSingleItemDecoder = messageProtocol.getSingleItemDecoder();
					
					FreeSizeInputStream fsis = new FreeSizeInputStream(dataPacketBufferMaxCntPerMessage, 
							toLetter.getWrapBufferList(), 
							streamCharsetDecoder, dataPacketBufferQueueManager);
					
					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						
						AbstractMessageDecoder messageDecoder  = (AbstractMessageDecoder)Class.forName(ioPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID)).newInstance();
					
						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);
						
						if (! (resObj instanceof SelfExnRes)) {
							String errorMessage = String.format("this output message[%s] is not a expected SelfExnRes message", 
									resObj.toString());
							fail(errorMessage);
						}
						
						SelfExnRes selfExnRes = (SelfExnRes)resObj;
						if (! selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}
						
						if (! selfExnRes.getErrorType().equals(SelfExn.ErrorType.DynamicClassCallException)) {
							fail("에러 종류가 동적 클래스가 아님");
						}
						
						log.info("성공 :: {}", selfExnRes.toString());
						
					} catch (Error | Exception e) {
						log.warn("error", e);
						fail("unknow error::OutputMessageWriterMock::#putIntoQueue");
					} finally {
						fsis.close();
					}
				} 			
			}
			
			OutputMessageWriterIF outputMessageWriterOfOwnerSC = 
					new OutputMessageWriterMock(streamCharsetDecoder, 
							dataPacketBufferMaxCntPerMessage, 
							messageProtocol, dataPacketBufferPoolManager,
							ioPartDynamicClassNameUtil);
			
			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder, 
						dataPacketBufferMaxCntPerMessage, 
						dataPacketBufferPoolManager);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}
			
			
			
			socketResourceOfFromSC = new SocketResource(fromSC, 
					inputMessageReaderOfOwnerSC, 
					executorOfOwnerSC, 
					outputMessageWriterOfOwnerSC,
					socketOutputStreamOfOwnerSC, 
					personalLoginManagerOfFromSC);
		}
		
		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;
			
			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
			}

			@Override
			public void remove(SocketChannel sc) {
			}

			@Override
			public SocketResource getSocketResource(SocketChannel sc) {
				return socketResourceOfFromSC;
			}

			@Override
			public int getNumberOfSocketResources() {
				return 0;
			}
		}
		
		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Echo echoReq = new Echo();
			echoReq.setStartTime(new Date().getTime());;
			echoReq.setRandomInt(new Random().nextInt());
			echoReq.messageHeaderInfo.mailboxID = 1;
			echoReq.messageHeaderInfo.mailID = 3;
			
			AbstractMessage message = echoReq;
			
			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder)Class.forName(ioPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID())).newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}
			
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
			
			if (1 != wrapReadableMiddleObjectList.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}
			
			wrapReadableMiddleObject = wrapReadableMiddleObjectList.get(0);
		}
		
		ServerObjectCacheManagerIF serverObjectCacheManager = mock(ServerObjectCacheManagerIF.class);
		
		Answer<MessageCodecIF> answerGetServerMessageCodec = new Answer<MessageCodecIF>() {
	        public MessageCodecIF answer(InvocationOnMock invocation) throws Throwable {	        	
				throw new NullPointerException();
	        }
	    };
	    
	    try {
			doAnswer(answerGetServerMessageCodec).when(serverObjectCacheManager)
				.getServerMessageCodec(any(ClassLoader.class), anyString());
		} catch (Exception e) {
			fail("fail to create the AbstractServerTask class mock");
		}

	    class AbstractServerTaskMock extends AbstractServerTask {

			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}
			
		}
		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();
	    
		try {
			int index = 0;
			String projectName = "sample_test";
			
			serverTaskMock.execute(index, 
					projectName, 
					fromSC, 
					socketResourceManager,
					socketResourceOfFromSC,
					personalLoginManagerOfFromSC,
					wrapReadableMiddleObject, 
					messageProtocol, serverObjectCacheManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
		
		
		wrapReadableMiddleObject.closeReadableMiddleObject();
		socketResourceOfFromSC.close();
	}
	
	
	@Test
	public void testExecute_failToGetMessageDecoder_통제된에러() {
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		
		
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil("kr.pe.sinnori.impl.");
		
		
		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}
		
		DataPacketBufferPoolIF dataPacketBufferPoolManager = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;
			
			try {
				dataPacketBufferPoolManager = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}
		
		MessageProtocolIF messageProtocol = null;
		{
			int messageIDFixedSize = 25;
			messageProtocol = 
					new THBMessageProtocol(messageIDFixedSize, 
							dataPacketBufferMaxCntPerMessage,
							streamCharsetEncoder,
							streamCharsetDecoder,
							dataPacketBufferPoolManager);
		}		
		
		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);
		
		SocketResource socketResourceOfFromSC = null;
		{	
			InputMessageReaderIF inputMessageReaderOfOwnerSC = mock(InputMessageReaderIF.class);
			ServerExecutorIF executorOfOwnerSC = mock(ServerExecutorIF.class);
			
			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;
				private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage,
						MessageProtocolIF messageProtocol, 
						DataPacketBufferPoolIF dataPacketBufferQueueManager,
						IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
					this.ioPartDynamicClassNameUtil = ioPartDynamicClassNameUtil;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfSocket() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {
				}

				@Override
				public void putIntoQueue(ToLetter toLetter) throws InterruptedException {
					if (null == toLetter) {
						fail("the parameter toLetter is null");
					}
					
					log.info("toLetter={}", toLetter.toString());
					
					
					String messageID = toLetter.getMessageID();
					
					SingleItemDecoderIF dhbSingleItemDecoder = messageProtocol.getSingleItemDecoder();
					
					FreeSizeInputStream fsis = new FreeSizeInputStream(dataPacketBufferMaxCntPerMessage, 
							toLetter.getWrapBufferList(), 
							streamCharsetDecoder, dataPacketBufferQueueManager);
					
					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						
						AbstractMessageDecoder messageDecoder  = (AbstractMessageDecoder)Class.forName(ioPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID)).newInstance();
					
						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);
						
						if (! (resObj instanceof SelfExnRes)) {
							String errorMessage = String.format("this output message[%s] is not a expected SelfExnRes message", 
									resObj.toString());
							fail(errorMessage);
						}
						
						SelfExnRes selfExnRes = (SelfExnRes)resObj;
						if (! selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}
						
						if (! selfExnRes.getErrorType().equals(SelfExn.ErrorType.DynamicClassCallException)) {
							fail("에러 종류가 동적 클래스가 아님");
						}
						
						log.info("성공 :: {}", selfExnRes.toString());
						
					} catch (Error | Exception e) {
						log.warn("error", e);
						fail("unknow error::OutputMessageWriterMock::#putIntoQueue");
					} finally {
						fsis.close();
					}
				} 			
			}
			
			OutputMessageWriterIF outputMessageWriterOfOwnerSC = 
					new OutputMessageWriterMock(streamCharsetDecoder, 
							dataPacketBufferMaxCntPerMessage, 
							messageProtocol, dataPacketBufferPoolManager,
							ioPartDynamicClassNameUtil);
			
			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder, 
						dataPacketBufferMaxCntPerMessage, 
						dataPacketBufferPoolManager);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}
			
			
			
			socketResourceOfFromSC = new SocketResource(fromSC, 
					inputMessageReaderOfOwnerSC, 
					executorOfOwnerSC, 
					outputMessageWriterOfOwnerSC,
					socketOutputStreamOfOwnerSC, 
					personalLoginManagerOfFromSC);
		}
		
		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;
			
			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
			}

			@Override
			public void remove(SocketChannel sc) {
			}

			@Override
			public SocketResource getSocketResource(SocketChannel sc) {
				return socketResourceOfFromSC;
			}

			@Override
			public int getNumberOfSocketResources() {
				return 0;
			}
		}
		
		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);
				

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Echo echoReq = new Echo();
			echoReq.setStartTime(new Date().getTime());;
			echoReq.setRandomInt(new Random().nextInt());
			echoReq.messageHeaderInfo.mailboxID = 1;
			echoReq.messageHeaderInfo.mailID = 3;
			
			AbstractMessage message = echoReq;
			
			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder)Class.forName(ioPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID())).newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}
			
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
			
			if (1 != wrapReadableMiddleObjectList.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}
			
			wrapReadableMiddleObject = wrapReadableMiddleObjectList.get(0);
		}
		
		ServerObjectCacheManagerIF serverObjectCacheManager = mock(ServerObjectCacheManagerIF.class);
		
		Answer<MessageCodecIF> answerGetServerMessageCodec = new Answer<MessageCodecIF>() {
	        public MessageCodecIF answer(InvocationOnMock invocation) throws Throwable {
	        	class MessageCodecMock implements MessageCodecIF {

					@Override
					public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {
						throw new DynamicClassCallException("fail to get getMessageDecoder");
					}

					@Override
					public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {
						return null;
					}
	        		
	        	}
	        	
				return new MessageCodecMock();
	        }
	    };
	    
	    try {
			doAnswer(answerGetServerMessageCodec).when(serverObjectCacheManager)
				.getServerMessageCodec(any(ClassLoader.class), anyString());
		} catch (Exception e) {
			fail("fail to create the AbstractServerTask class mock");
		}

	    class AbstractServerTaskMock extends AbstractServerTask {

			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}
			
		}
		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();
	    
		try {
			int index = 0;
			String projectName = "sample_test";
			
			serverTaskMock.execute(index, 
					projectName, 
					fromSC, 
					socketResourceManager, 
					socketResourceOfFromSC,
					personalLoginManagerOfFromSC,
					wrapReadableMiddleObject, 
					messageProtocol, serverObjectCacheManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
		
		
		wrapReadableMiddleObject.closeReadableMiddleObject();
		socketResourceOfFromSC.close();
	}
	
	@Test
	public void testExecute_failToGetMessageDecoder_통제벗어난에러() {
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		
		
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil("kr.pe.sinnori.impl.");
		
		
		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}
		
		DataPacketBufferPoolIF dataPacketBufferPoolManager = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;
			
			try {
				dataPacketBufferPoolManager = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}
		
		MessageProtocolIF messageProtocol = null;
		{
			int messageIDFixedSize = 25;
			messageProtocol = 
					new THBMessageProtocol(messageIDFixedSize, 
							dataPacketBufferMaxCntPerMessage,
							streamCharsetEncoder,
							streamCharsetDecoder,
							dataPacketBufferPoolManager);
		}		
		
		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);
		
		SocketResource socketResourceOfFromSC = null;
		{	
			InputMessageReaderIF inputMessageReaderOfOwnerSC = mock(InputMessageReaderIF.class);
			ServerExecutorIF executorOfOwnerSC = mock(ServerExecutorIF.class);
			
			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;
				private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage,
						MessageProtocolIF messageProtocol, 
						DataPacketBufferPoolIF dataPacketBufferQueueManager,
						IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
					this.ioPartDynamicClassNameUtil = ioPartDynamicClassNameUtil;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfSocket() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {
				}

				@Override
				public void putIntoQueue(ToLetter toLetter) throws InterruptedException {
					if (null == toLetter) {
						fail("the parameter toLetter is null");
					}
					
					log.info("toLetter={}", toLetter.toString());
					
					
					String messageID = toLetter.getMessageID();
					
					SingleItemDecoderIF dhbSingleItemDecoder = messageProtocol.getSingleItemDecoder();
					
					FreeSizeInputStream fsis = new FreeSizeInputStream(dataPacketBufferMaxCntPerMessage, 
							toLetter.getWrapBufferList(), 
							streamCharsetDecoder, dataPacketBufferQueueManager);
					
					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						
						AbstractMessageDecoder messageDecoder  = (AbstractMessageDecoder)Class.forName(ioPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID)).newInstance();
					
						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);
						
						if (! (resObj instanceof SelfExnRes)) {
							String errorMessage = String.format("this output message[%s] is not a expected SelfExnRes message", 
									resObj.toString());
							fail(errorMessage);
						}
						
						SelfExnRes selfExnRes = (SelfExnRes)resObj;
						if (! selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}
						
						if (! selfExnRes.getErrorType().equals(SelfExn.ErrorType.DynamicClassCallException)) {
							fail("에러 종류가 동적 클래스가 아님");
						}
						
						log.info("성공 :: {}", selfExnRes.toString());
						
					} catch (Error | Exception e) {
						log.warn("error", e);
						fail("unknow error::OutputMessageWriterMock::#putIntoQueue");
					} finally {
						fsis.close();
					}
				} 			
			}
			
			OutputMessageWriterIF outputMessageWriterOfOwnerSC = 
					new OutputMessageWriterMock(streamCharsetDecoder, 
							dataPacketBufferMaxCntPerMessage, 
							messageProtocol, dataPacketBufferPoolManager,
							ioPartDynamicClassNameUtil);
			
			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder, 
						dataPacketBufferMaxCntPerMessage, 
						dataPacketBufferPoolManager);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}
			
			
			
			socketResourceOfFromSC = new SocketResource(fromSC, 
					inputMessageReaderOfOwnerSC, 
					executorOfOwnerSC, 
					outputMessageWriterOfOwnerSC,
					socketOutputStreamOfOwnerSC, 
					personalLoginManagerOfFromSC);
		}
		
		
		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;
			
			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
			}

			@Override
			public void remove(SocketChannel sc) {
			}

			@Override
			public SocketResource getSocketResource(SocketChannel sc) {
				return socketResourceOfFromSC;
			}

			@Override
			public int getNumberOfSocketResources() {
				return 0;
			}
		}
		
		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Echo echoReq = new Echo();
			echoReq.setStartTime(new Date().getTime());;
			echoReq.setRandomInt(new Random().nextInt());
			echoReq.messageHeaderInfo.mailboxID = 1;
			echoReq.messageHeaderInfo.mailID = 3;
			
			AbstractMessage message = echoReq;
			
			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder)Class.forName(ioPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID())).newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}
			
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
			
			if (1 != wrapReadableMiddleObjectList.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}
			
			wrapReadableMiddleObject = wrapReadableMiddleObjectList.get(0);
		}
		
		ServerObjectCacheManagerIF serverObjectCacheManager = mock(ServerObjectCacheManagerIF.class);
		
		Answer<MessageCodecIF> answerGetServerMessageCodec = new Answer<MessageCodecIF>() {
	        public MessageCodecIF answer(InvocationOnMock invocation) throws Throwable {
	        	class MessageCodecMock implements MessageCodecIF {

					@Override
					public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {
						throw new NullPointerException();
					}

					@Override
					public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {
						return null;
					}
	        		
	        	}
	        	
				return new MessageCodecMock();
	        }
	    };
	    
	    try {
			doAnswer(answerGetServerMessageCodec).when(serverObjectCacheManager)
				.getServerMessageCodec(any(ClassLoader.class), anyString());
		} catch (Exception e) {
			fail("fail to create the AbstractServerTask class mock");
		}

	    class AbstractServerTaskMock extends AbstractServerTask {

			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}
			
		}
		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();
	    
		try {
			int index = 0;
			String projectName = "sample_test";
			
			serverTaskMock.execute(index, 
					projectName, 
					fromSC, 
					socketResourceManager,
					socketResourceOfFromSC,
					personalLoginManagerOfFromSC,
					wrapReadableMiddleObject, 
					messageProtocol, serverObjectCacheManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
		
		
		wrapReadableMiddleObject.closeReadableMiddleObject();
		socketResourceOfFromSC.close();
	}
	
	@Test
	public void testExecute_failToDecodeBody_통제된에러() {
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		
		
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil("kr.pe.sinnori.impl.");
		
		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}
		
		DataPacketBufferPoolIF dataPacketBufferPoolManager = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;
			
			try {
				dataPacketBufferPoolManager = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}
		
		MessageProtocolIF messageProtocol = null;
		{
			int messageIDFixedSize = 25;
			messageProtocol = 
					new THBMessageProtocol(messageIDFixedSize, 
							dataPacketBufferMaxCntPerMessage,
							streamCharsetEncoder,
							streamCharsetDecoder,
							dataPacketBufferPoolManager);
		}		
		
		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);
		
		SocketResource socketResourceOfFromSC = null;
		{	
			InputMessageReaderIF inputMessageReaderOfOwnerSC = mock(InputMessageReaderIF.class);
			ServerExecutorIF executorOfOwnerSC = mock(ServerExecutorIF.class);
			
			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;
				private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage,
						MessageProtocolIF messageProtocol, 
						DataPacketBufferPoolIF dataPacketBufferQueueManager,
						IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
					this.ioPartDynamicClassNameUtil = ioPartDynamicClassNameUtil;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfSocket() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {
				}

				@Override
				public void putIntoQueue(ToLetter toLetter) throws InterruptedException {
					if (null == toLetter) {
						fail("the parameter toLetter is null");
					}
					
					log.info("toLetter={}", toLetter.toString());
					
					
					String messageID = toLetter.getMessageID();
					
					SingleItemDecoderIF dhbSingleItemDecoder = messageProtocol.getSingleItemDecoder();
					
					FreeSizeInputStream fsis = new FreeSizeInputStream(dataPacketBufferMaxCntPerMessage, 
							toLetter.getWrapBufferList(), 
							streamCharsetDecoder, dataPacketBufferQueueManager);
					
					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						
						AbstractMessageDecoder messageDecoder  = (AbstractMessageDecoder)Class.forName(ioPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID)).newInstance();
					
						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);
						
						if (! (resObj instanceof SelfExnRes)) {
							String errorMessage = String.format("this output message[%s] is not a expected SelfExnRes message", 
									resObj.toString());
							fail(errorMessage);
						}
						
						SelfExnRes selfExnRes = (SelfExnRes)resObj;
						if (! selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}
						
						if (! selfExnRes.getErrorType().equals(SelfExn.ErrorType.BodyFormatException)) {
							fail("에러 종류가 바디 포맷 클래스가 아님");
						}
						
						log.info("성공 :: {}", selfExnRes.toString());
						
					} catch (Error | Exception e) {
						log.warn("error", e);
						fail("unknow error::OutputMessageWriterMock::#putIntoQueue");
					} finally {
						fsis.close();
					}
				} 			
			}
			
			OutputMessageWriterIF outputMessageWriterOfOwnerSC = 
					new OutputMessageWriterMock(streamCharsetDecoder, 
							dataPacketBufferMaxCntPerMessage, 
							messageProtocol, dataPacketBufferPoolManager,
							ioPartDynamicClassNameUtil);
			
			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder, 
						dataPacketBufferMaxCntPerMessage, 
						dataPacketBufferPoolManager);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}
			
			
			
			socketResourceOfFromSC = new SocketResource(fromSC, 
					inputMessageReaderOfOwnerSC, 
					executorOfOwnerSC, 
					outputMessageWriterOfOwnerSC,
					socketOutputStreamOfOwnerSC, 
					personalLoginManagerOfFromSC);
		}
		
		
		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;
			
			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
			}

			@Override
			public void remove(SocketChannel sc) {
			}

			@Override
			public SocketResource getSocketResource(SocketChannel sc) {
				return socketResourceOfFromSC;
			}

			@Override
			public int getNumberOfSocketResources() {
				return 0;
			}
		}
		
		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Echo echoReq = new Echo();
			echoReq.setStartTime(new Date().getTime());;
			echoReq.setRandomInt(new Random().nextInt());
			echoReq.messageHeaderInfo.mailboxID = 1;
			echoReq.messageHeaderInfo.mailID = 3;
			
			AbstractMessage message = echoReq;
			
			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder)Class.forName(ioPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID())).newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}
			
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
			
			if (1 != wrapReadableMiddleObjectList.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}
			
			wrapReadableMiddleObject = wrapReadableMiddleObjectList.get(0);
		}
		
		// IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil
		class ServerObjectCacheManagerMock implements ServerObjectCacheManagerIF {
			@Override
			public MessageCodecIF getServerMessageCodec(ClassLoader classLoader, String messageID)
					throws DynamicClassCallException {
				
				class MessageCodecMock implements MessageCodecIF {

					@Override
					public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {
						class MessageDecoderMock extends AbstractMessageDecoder {
							
							@Override
							protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder,
									Object middleReadObj) throws OutOfMemoryError, BodyFormatException {
								throw new BodyFormatException("test BodyFormatException");
							}
							
						}
						
						return new MessageDecoderMock();
					}

					@Override
					public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {
						return null;
					}
					
				}
	        	
				return new MessageCodecMock();
			}

			@Override
			public AbstractServerTask getServerTask(String messageID) throws DynamicClassCallException {
				return null;
			}
			
		}
		
		ServerObjectCacheManagerIF serverObjectCacheManager = new ServerObjectCacheManagerMock();
		

	    class AbstractServerTaskMock extends AbstractServerTask {

			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}
			
		}
		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();
	    
		try {
			int index = 0;
			String projectName = "sample_test";
			
			serverTaskMock.execute(index, 
					projectName, 
					fromSC, 
					socketResourceManager, 
					socketResourceOfFromSC,
					personalLoginManagerOfFromSC,
					wrapReadableMiddleObject, 
					messageProtocol, serverObjectCacheManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
		
		
		wrapReadableMiddleObject.closeReadableMiddleObject();
		socketResourceOfFromSC.close();
	}
	
	@Test
	public void testExecute_failToDecodeBody_통제벗어난에러() {
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		
		
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil("kr.pe.sinnori.impl.");
		
		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}
		
		DataPacketBufferPoolIF dataPacketBufferPoolManager = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;
			
			try {
				dataPacketBufferPoolManager = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}
		
		MessageProtocolIF messageProtocol = null;
		{
			int messageIDFixedSize = 25;
			messageProtocol = 
					new THBMessageProtocol(messageIDFixedSize, 
							dataPacketBufferMaxCntPerMessage,
							streamCharsetEncoder,
							streamCharsetDecoder,
							dataPacketBufferPoolManager);
		}		
		
		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);
		
		SocketResource socketResourceOfFromSC = null;
		{	
			InputMessageReaderIF inputMessageReaderOfOwnerSC = mock(InputMessageReaderIF.class);
			ServerExecutorIF executorOfOwnerSC = mock(ServerExecutorIF.class);
			
			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;
				private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage,
						MessageProtocolIF messageProtocol, 
						DataPacketBufferPoolIF dataPacketBufferQueueManager,
						IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
					this.ioPartDynamicClassNameUtil = ioPartDynamicClassNameUtil;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfSocket() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {
				}

				@Override
				public void putIntoQueue(ToLetter toLetter) throws InterruptedException {
					if (null == toLetter) {
						fail("the parameter toLetter is null");
					}
					
					log.info("toLetter={}", toLetter.toString());
					
					
					String messageID = toLetter.getMessageID();
					
					SingleItemDecoderIF dhbSingleItemDecoder = messageProtocol.getSingleItemDecoder();
					
					FreeSizeInputStream fsis = new FreeSizeInputStream(dataPacketBufferMaxCntPerMessage, 
							toLetter.getWrapBufferList(), 
							streamCharsetDecoder, dataPacketBufferQueueManager);
					
					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						
						AbstractMessageDecoder messageDecoder  = (AbstractMessageDecoder)Class.forName(ioPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID)).newInstance();
					
						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);
						
						if (! (resObj instanceof SelfExnRes)) {
							String errorMessage = String.format("this output message[%s] is not a expected SelfExnRes message", 
									resObj.toString());
							fail(errorMessage);
						}
						
						SelfExnRes selfExnRes = (SelfExnRes)resObj;
						if (! selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}
						
						if (! selfExnRes.getErrorType().equals(SelfExn.ErrorType.BodyFormatException)) {
							fail("에러 종류가 바디 포맷 클래스가 아님");
						}
						
						log.info("성공 :: {}", selfExnRes.toString());
						
					} catch (Error | Exception e) {
						log.warn("error", e);
						fail("unknow error::OutputMessageWriterMock::#putIntoQueue");
					} finally {
						fsis.close();
					}
				} 			
			}
			
			OutputMessageWriterIF outputMessageWriterOfOwnerSC = 
					new OutputMessageWriterMock(streamCharsetDecoder, 
							dataPacketBufferMaxCntPerMessage, 
							messageProtocol, dataPacketBufferPoolManager,
							ioPartDynamicClassNameUtil);
			
			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder, 
						dataPacketBufferMaxCntPerMessage, 
						dataPacketBufferPoolManager);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}
			
			
			
			socketResourceOfFromSC = new SocketResource(fromSC, 
					inputMessageReaderOfOwnerSC, 
					executorOfOwnerSC, 
					outputMessageWriterOfOwnerSC,
					socketOutputStreamOfOwnerSC, 
					personalLoginManagerOfFromSC);
		}
		
		
		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;
			
			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
			}

			@Override
			public void remove(SocketChannel sc) {
			}

			@Override
			public SocketResource getSocketResource(SocketChannel sc) {
				return socketResourceOfFromSC;
			}

			@Override
			public int getNumberOfSocketResources() {
				return 0;
			}
		}
		
		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Echo echoReq = new Echo();
			echoReq.setStartTime(new Date().getTime());;
			echoReq.setRandomInt(new Random().nextInt());
			echoReq.messageHeaderInfo.mailboxID = 1;
			echoReq.messageHeaderInfo.mailID = 3;
			
			AbstractMessage message = echoReq;
			
			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder)Class.forName(ioPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID())).newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}
			
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
			
			if (1 != wrapReadableMiddleObjectList.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}
			
			wrapReadableMiddleObject = wrapReadableMiddleObjectList.get(0);
		}
		
		class ServerObjectCacheManagerMock implements ServerObjectCacheManagerIF {
			@Override
			public MessageCodecIF getServerMessageCodec(ClassLoader classLoader, String messageID)
					throws DynamicClassCallException {
				
				class MessageCodecMock implements MessageCodecIF {

					@Override
					public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {
						class MessageDecoderMock extends AbstractMessageDecoder {
							
							@Override
							protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder,
									Object middleReadObj) throws OutOfMemoryError, BodyFormatException {
								throw new NullPointerException();
							}
							
						}
						
						return new MessageDecoderMock();
					}

					@Override
					public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {
						return null;
					}
					
				}
	        	
				return new MessageCodecMock();
			}

			@Override
			public AbstractServerTask getServerTask(String messageID) throws DynamicClassCallException {
				return null;
			}
			
		}
		
		ServerObjectCacheManagerIF serverObjectCacheManager = new ServerObjectCacheManagerMock();
		

	    class AbstractServerTaskMock extends AbstractServerTask {

			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}
			
		}
		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();
	    
		try {
			int index = 0;
			String projectName = "sample_test";
			
			serverTaskMock.execute(index, 
					projectName, 
					fromSC, 
					socketResourceManager, 
					socketResourceOfFromSC,
					personalLoginManagerOfFromSC,
					wrapReadableMiddleObject, 
					messageProtocol, serverObjectCacheManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
		
		
		wrapReadableMiddleObject.closeReadableMiddleObject();
		socketResourceOfFromSC.close();
	}
	
	@Test
	public void testDoTask_2번이상동기출력메시지추가() {
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		
		
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil("kr.pe.sinnori.impl.");
				
		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}
		
		DataPacketBufferPoolIF dataPacketBufferPoolManager = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;
			
			try {
				dataPacketBufferPoolManager = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}
		
		MessageProtocolIF messageProtocol = null;
		{
			int messageIDFixedSize = 25;
			messageProtocol = 
					new THBMessageProtocol(messageIDFixedSize, 
							dataPacketBufferMaxCntPerMessage,
							streamCharsetEncoder,
							streamCharsetDecoder,
							dataPacketBufferPoolManager);
		}		
		
		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);
		
		SocketResource socketResourceOfFromSC = null;
		{	
			InputMessageReaderIF inputMessageReaderOfOwnerSC = mock(InputMessageReaderIF.class);
			ServerExecutorIF executorOfOwnerSC = mock(ServerExecutorIF.class);
			
			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;
				private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage,
						MessageProtocolIF messageProtocol, 
						DataPacketBufferPoolIF dataPacketBufferQueueManager,
						IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
					this.ioPartDynamicClassNameUtil = ioPartDynamicClassNameUtil;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfSocket() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {
				}

				@Override
				public void putIntoQueue(ToLetter toLetter) throws InterruptedException {
					if (null == toLetter) {
						fail("the parameter toLetter is null");
					}
					
					log.info("toLetter={}", toLetter.toString());
					
					
					String messageID = toLetter.getMessageID();
					
					SingleItemDecoderIF dhbSingleItemDecoder = messageProtocol.getSingleItemDecoder();
					
					FreeSizeInputStream fsis = new FreeSizeInputStream(dataPacketBufferMaxCntPerMessage, 
							toLetter.getWrapBufferList(), 
							streamCharsetDecoder, dataPacketBufferQueueManager);
					
					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						
						AbstractMessageDecoder messageDecoder  = (AbstractMessageDecoder)Class.forName(ioPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID)).newInstance();
					
						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);
						
						if (! (resObj instanceof SelfExnRes)) {
							String errorMessage = String.format("this output message[%s] is not a expected SelfExnRes message", 
									resObj.toString());
							fail(errorMessage);
						}
						
						SelfExnRes selfExnRes = (SelfExnRes)resObj;
						if (! selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}
						
						if (! selfExnRes.getErrorType().equals(SelfExn.ErrorType.ServerTaskException)) {
							fail("에러 종류가 서버 타스크 클래스가 아님");
						}
						
						if (selfExnRes.getErrorReason().indexOf("the synchronous output message can't be added becase another synchronous message is already registered in the toLetter list") < 0) {
							fail("2번째 동기 출력 메시지를 추가할 수 없다는 에러가 아님");
						}
						
						log.info("성공 :: {}", selfExnRes.toString());
						
					} catch (Error | Exception e) {
						log.warn("error", e);
						fail("unknow error::OutputMessageWriterMock::#putIntoQueue");
					} finally {
						fsis.close();
					}
				} 			
			}
			
			OutputMessageWriterIF outputMessageWriterOfOwnerSC = 
					new OutputMessageWriterMock(streamCharsetDecoder, 
							dataPacketBufferMaxCntPerMessage, 
							messageProtocol, dataPacketBufferPoolManager,
							ioPartDynamicClassNameUtil);
			
			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder, 
						dataPacketBufferMaxCntPerMessage, 
						dataPacketBufferPoolManager);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}
			
			
			
			socketResourceOfFromSC = new SocketResource(fromSC, 
					inputMessageReaderOfOwnerSC, 
					executorOfOwnerSC, 
					outputMessageWriterOfOwnerSC,
					socketOutputStreamOfOwnerSC, 
					personalLoginManagerOfFromSC);
		}
		
		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;
			
			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
			}

			@Override
			public void remove(SocketChannel sc) {
			}

			@Override
			public SocketResource getSocketResource(SocketChannel sc) {
				return socketResourceOfFromSC;
			}

			@Override
			public int getNumberOfSocketResources() {
				return 0;
			}
		}
		
		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);
				

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Echo echoReq = new Echo();
			echoReq.setStartTime(new Date().getTime());;
			echoReq.setRandomInt(new Random().nextInt());
			echoReq.messageHeaderInfo.mailboxID = 1;
			echoReq.messageHeaderInfo.mailID = 3;
			
			AbstractMessage message = echoReq;
			
			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder)Class.forName(ioPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID())).newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}
			
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
			
			if (1 != wrapReadableMiddleObjectList.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}
			
			wrapReadableMiddleObject = wrapReadableMiddleObjectList.get(0);
		}
		
		class ServerObjectCacheManagerMock implements ServerObjectCacheManagerIF {
			private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;
			
			public ServerObjectCacheManagerMock(IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil) {
				this.ioPartDynamicClassNameUtil = ioPartDynamicClassNameUtil;
			}
			
			@Override
			public MessageCodecIF getServerMessageCodec(ClassLoader classLoader, String messageID)
					throws DynamicClassCallException {
				
				MessageCodecIF messageCodec = null;
				try {
					messageCodec = (MessageCodecIF) Class.forName(ioPartDynamicClassNameUtil
							.getServerMessageCodecClassFullName(messageID)).newInstance();
				} catch (Exception e) {
					fail("fail to get messageCodec");
				} 
	        	
				return messageCodec;
			}

			@Override
			public AbstractServerTask getServerTask(String messageID) throws DynamicClassCallException {
				return null;
			}
			
		}
		
		ServerObjectCacheManagerIF serverObjectCacheManager = new ServerObjectCacheManagerMock(ioPartDynamicClassNameUtil);
		

	    class AbstractServerTaskMock extends AbstractServerTask {

			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				log.info(inputMessage.toString());
				
				// throw new NullPointerException();
				
				toLetterCarrier.addSyncOutputMessage(inputMessage);
				toLetterCarrier.addSyncOutputMessage(inputMessage);
			}
			
		}
		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();
	    
		try {
			int index = 0;
			String projectName = "sample_test";
			
			serverTaskMock.execute(index, 
					projectName, 
					fromSC, 
					socketResourceManager, 
					socketResourceOfFromSC,
					personalLoginManagerOfFromSC,
					wrapReadableMiddleObject, 
					messageProtocol, serverObjectCacheManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
		
		
		wrapReadableMiddleObject.closeReadableMiddleObject();
		socketResourceOfFromSC.close();
	}
	
	@Test
	public void testDoTask_비동기입력메시지에동기출력메시지추가() {
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		
		
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil("kr.pe.sinnori.impl.");
				
		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}
		
		DataPacketBufferPoolIF dataPacketBufferPoolManager = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;
			
			try {
				dataPacketBufferPoolManager = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}
		
		MessageProtocolIF messageProtocol = null;
		{
			int messageIDFixedSize = 25;
			messageProtocol = 
					new THBMessageProtocol(messageIDFixedSize, 
							dataPacketBufferMaxCntPerMessage,
							streamCharsetEncoder,
							streamCharsetDecoder,
							dataPacketBufferPoolManager);
		}		
		
		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);
		
		SocketResource socketResourceOfFromSC = null;
		{	
			InputMessageReaderIF inputMessageReaderOfOwnerSC = mock(InputMessageReaderIF.class);
			ServerExecutorIF executorOfOwnerSC = mock(ServerExecutorIF.class);
			
			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;
				private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage,
						MessageProtocolIF messageProtocol, 
						DataPacketBufferPoolIF dataPacketBufferQueueManager,
						IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
					this.ioPartDynamicClassNameUtil = ioPartDynamicClassNameUtil;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfSocket() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {
				}

				@Override
				public void putIntoQueue(ToLetter toLetter) throws InterruptedException {
					if (null == toLetter) {
						fail("the parameter toLetter is null");
					}
					
					log.info("toLetter={}", toLetter.toString());
					
					
					String messageID = toLetter.getMessageID();
					
					SingleItemDecoderIF dhbSingleItemDecoder = messageProtocol.getSingleItemDecoder();
					
					FreeSizeInputStream fsis = new FreeSizeInputStream(dataPacketBufferMaxCntPerMessage, 
							toLetter.getWrapBufferList(), 
							streamCharsetDecoder, dataPacketBufferQueueManager);
					
					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						
						AbstractMessageDecoder messageDecoder  = (AbstractMessageDecoder)Class.forName(ioPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID)).newInstance();
					
						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);
						
						if (! (resObj instanceof SelfExnRes)) {
							String errorMessage = String.format("this output message[%s] is not a expected SelfExnRes message", 
									resObj.toString());
							fail(errorMessage);
						}
						
						SelfExnRes selfExnRes = (SelfExnRes)resObj;
						if (! selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}
						
						if (! selfExnRes.getErrorType().equals(SelfExn.ErrorType.ServerTaskException)) {
							fail("에러 종류가 서버 타스크 클래스가 아님");
						}
						
						if (selfExnRes.getErrorReason().indexOf("the synchronous output message can't be added becase the inputMessage is a asynchronous message") < 0) {
							fail("비동기 입력 메시지에 동기 출력 메시지를 보냈다는 에러가 아님");
						}
						
						// the synchronous output message can't be added becase the inputMessage is a asynchronous message
						log.info("성공 :: {}", selfExnRes.toString());
						
					} catch (Error | Exception e) {
						log.warn("error", e);
						fail("unknow error::OutputMessageWriterMock::#putIntoQueue");
					} finally {
						fsis.close();
					}
				} 			
			}
			
			OutputMessageWriterIF outputMessageWriterOfOwnerSC = 
					new OutputMessageWriterMock(streamCharsetDecoder, 
							dataPacketBufferMaxCntPerMessage, 
							messageProtocol, dataPacketBufferPoolManager,
							ioPartDynamicClassNameUtil);
			
			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder, 
						dataPacketBufferMaxCntPerMessage, 
						dataPacketBufferPoolManager);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}
			
			
			
			socketResourceOfFromSC = new SocketResource(fromSC, 
					inputMessageReaderOfOwnerSC, 
					executorOfOwnerSC, 
					outputMessageWriterOfOwnerSC,
					socketOutputStreamOfOwnerSC, 
					personalLoginManagerOfFromSC);
		}
		
		
		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;
			
			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
			}

			@Override
			public void remove(SocketChannel sc) {
			}

			@Override
			public SocketResource getSocketResource(SocketChannel sc) {
				return socketResourceOfFromSC;
			}

			@Override
			public int getNumberOfSocketResources() {
				return 0;
			}
		}
		
		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Echo echoReq = new Echo();
			echoReq.setStartTime(new Date().getTime());;
			echoReq.setRandomInt(new Random().nextInt());
			echoReq.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
			echoReq.messageHeaderInfo.mailID = 3;
			
			AbstractMessage message = echoReq;
			
			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder)Class.forName(ioPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID())).newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}
			
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
			
			if (1 != wrapReadableMiddleObjectList.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}
			
			wrapReadableMiddleObject = wrapReadableMiddleObjectList.get(0);
		}
		
		class ServerObjectCacheManagerMock implements ServerObjectCacheManagerIF {
			private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;
			
			public ServerObjectCacheManagerMock(IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil) {
				this.ioPartDynamicClassNameUtil = ioPartDynamicClassNameUtil;
			}
			
			@Override
			public MessageCodecIF getServerMessageCodec(ClassLoader classLoader, String messageID)
					throws DynamicClassCallException {
				
				MessageCodecIF messageCodec = null;
				try {
					messageCodec = (MessageCodecIF) Class.forName(ioPartDynamicClassNameUtil
							.getServerMessageCodecClassFullName(messageID)).newInstance();
				} catch (Exception e) {
					fail("fail to get messageCodec");
				} 
	        	
				return messageCodec;
			}

			@Override
			public AbstractServerTask getServerTask(String messageID) throws DynamicClassCallException {
				return null;
			}
			
		}
		
		ServerObjectCacheManagerIF serverObjectCacheManager = new ServerObjectCacheManagerMock(ioPartDynamicClassNameUtil);
		

	    class AbstractServerTaskMock extends AbstractServerTask {

			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				log.info(inputMessage.toString());
				
				// throw new NullPointerException();
				
				toLetterCarrier.addSyncOutputMessage(inputMessage);
			}
			
		}
		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();
	    
		try {
			int index = 0;
			String projectName = "sample_test";
			
			serverTaskMock.execute(index, 
					projectName, 
					fromSC, 
					socketResourceManager, 
					socketResourceOfFromSC,
					personalLoginManagerOfFromSC,
					wrapReadableMiddleObject, 
					messageProtocol, serverObjectCacheManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
		
		
		wrapReadableMiddleObject.closeReadableMiddleObject();
		socketResourceOfFromSC.close();
	}
}
