package kr.pe.codda.server.task;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayDeque;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.client.connection.ClientMessageUtility;
import kr.pe.codda.client.connection.ClientMessageUtilityIF;
import kr.pe.codda.client.connection.ClientObjectCacheManager;
import kr.pe.codda.client.connection.ClientObjectCacheManagerIF;
import kr.pe.codda.common.asyn.ToLetter;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.classloader.ServerSimpleClassLoaderIF;
import kr.pe.codda.common.etc.CharsetUtil;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.DataPacketBufferPool;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.FreeSizeInputStream;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMessageBlockingQueueIF;
import kr.pe.codda.common.protocol.SimpleReceivedMessageBlockingQueue;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;
import kr.pe.codda.common.protocol.WrapReadableMiddleObject;
import kr.pe.codda.common.protocol.thb.THBMessageProtocol;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.common.type.SelfExn.ErrorType;
import kr.pe.codda.impl.message.Empty.Empty;
import kr.pe.codda.impl.message.Empty.EmptyDecoder;
import kr.pe.codda.impl.message.Empty.EmptyEncoder;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.ServerIOEvenetControllerIF;
import kr.pe.codda.server.SocketResource;
import kr.pe.codda.server.SocketResourceManagerIF;
import kr.pe.codda.server.threadpool.executor.ServerExecutorIF;
import kr.pe.codda.server.threadpool.executor.ServerExecutorPoolIF;
import kr.pe.codda.server.threadpool.outputmessage.OutputMessageWriterIF;

public class ServerTaskTest extends AbstractJunitTest {

	@Test
	public void testExecute_failToGetServerMessageCodec_통제된에러() {
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		long socketTimeOut = 5000;
		int outputMessageQueueSize = 5; 

		/*
		 * ClassLoader currentClassLoader = this.getClass().getClassLoader(); final
		 * String ClassLoaderClassPackagePrefixName = "kr.pe.sinnori.impl.";
		 */
		ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue = 
				new ArrayBlockingQueue<WrapReadableMiddleObject>(10);

		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}

		DataPacketBufferPoolIF dataPacketBufferPool = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;

			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize,
						dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}

		MessageProtocolIF messageProtocol = null;
		{

			messageProtocol = new THBMessageProtocol(dataPacketBufferMaxCntPerMessage, streamCharsetEncoder,
					streamCharsetDecoder, dataPacketBufferPool);
		}
		
		ClientObjectCacheManagerIF clientObjectCacheManager = new ClientObjectCacheManager();
		ClientMessageUtilityIF clientMessageUtility = new ClientMessageUtility(messageProtocol, clientObjectCacheManager,
				dataPacketBufferPool);

		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);
		
		ServerIOEvenetControllerIF serverIOEvenetController = mock(ServerIOEvenetControllerIF.class);

		SocketResource socketResourceOfFromSC = null;
		{
			
			class ServerExecutorMock implements ServerExecutorIF {
				private ReceivedMessageBlockingQueueIF wrapMessageBlockingQueue  = null;
				
				public ServerExecutorMock(ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue) {					
					wrapMessageBlockingQueue  = new SimpleReceivedMessageBlockingQueue(wrapReadableMiddleObjectQueue);
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {					
				}

				@Override
				public int getNumberOfConnection() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {					
				}

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
					
				}

				@Override
				public ReceivedMessageBlockingQueueIF getWrapMessageBlockingQueue() {
					return wrapMessageBlockingQueue;
				}
				
			}
			
			ServerExecutorIF executorOfOwnerSC = new ServerExecutorMock(wrapReadableMiddleObjectQueue);

			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}

			socketResourceOfFromSC = new SocketResource(fromSC,  socketTimeOut, outputMessageQueueSize,
					messageProtocol,
					executorOfOwnerSC,
					socketOutputStreamOfOwnerSC, 
					personalLoginManagerOfFromSC,
					dataPacketBufferPool, serverIOEvenetController);
		}

		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;

			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewAcceptedSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
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

			@Override
			public void setServerExecutorPool(ServerExecutorPoolIF serverExecutorPool) {
				// TODO Auto-generated method stub
				
			}
		}

		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);

		WrapReadableMiddleObject inputMessageWrapReadableMiddleObject = null;
		{
			Empty emptyReq = new Empty();
			emptyReq.messageHeaderInfo.mailboxID = 1;
			emptyReq.messageHeaderInfo.mailID = 3;

			AbstractMessage message = emptyReq;

			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder) Class
						.forName(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID()))
						.newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}

			ArrayDeque<WrapBuffer> wrapBufferListOfInputMessage = null;
			try {
				wrapBufferListOfInputMessage = messageProtocol.M2S(message, messageEncoder);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("2");

			for (WrapBuffer inputMessageWrapBuffer : wrapBufferListOfInputMessage) {
				if (inputMessageWrapBuffer.isInQueue()) {
					fail("bad wrap buffer where is in of queue");
				}

				ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
				inputMessageByteBuffer.position(inputMessageByteBuffer.limit());
			}

			// log.info("3");

			SocketOutputStream sos = null;
			try {
				sos = new SocketOutputStream(wrapBufferListOfInputMessage, streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("sos.size={}", sos.size());

			// log.info("4");
			
			try {
				messageProtocol.S2MList(fromSC, sos, socketResourceOfFromSC.getServerExecutor().getWrapMessageBlockingQueue());
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			if (1 != wrapReadableMiddleObjectQueue.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}

			inputMessageWrapReadableMiddleObject = wrapReadableMiddleObjectQueue.poll();
		}

		class AbstractServerTaskMock extends AbstractServerTask {
			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}
		}

		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();

		class ServerSimpleClassLoaderMock implements ServerSimpleClassLoaderIF {
			@Override
			public MessageCodecIF getMessageCodec(String messageID) throws DynamicClassCallException {

				throw new DynamicClassCallException("the server message codec was not found");
			}
		}

		serverTaskMock.setServerSimpleClassloader(new ServerSimpleClassLoaderMock());

		try {
			int index = 0;
			String projectName = "sample_test";

			serverTaskMock.execute(index, projectName, fromSC, socketResourceOfFromSC,
					socketResourceManager, socketResourceOfFromSC,
					personalLoginManagerOfFromSC, inputMessageWrapReadableMiddleObject, messageProtocol);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
		
		// log.info(wrapReadableMiddleObjectQueue.poll().toString());
		
		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = socketResourceOfFromSC.getOutputMessageQueue();
		if (outputMessageQueue.size() != 1) {
			fail("예상 출력 메시지 갯수가 1개가 아님");
		}
		
		ArrayDeque<WrapBuffer> wrapBufferQueue = outputMessageQueue.poll();
		SocketOutputStream sos = null;
		try {
			sos = new SocketOutputStream(wrapBufferQueue, streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
		} catch (NoMoreDataPacketBufferException e) {
			fail("fail to create a instance of SocketOutputStream");
		}
		
		
		try {
			messageProtocol.S2MList(fromSC, sos, new SimpleReceivedMessageBlockingQueue(wrapReadableMiddleObjectQueue));
		} catch (Exception e) {
			fail("fail to close");
		}
		
		if (wrapReadableMiddleObjectQueue.size() != 1) {
			fail("예상 출력 메시지 갯수가 1개가 아님");
		}
		
		
		WrapReadableMiddleObject outputMessageWrapReadableMiddleObject = wrapReadableMiddleObjectQueue.poll();
		
		AbstractMessage receviedOutputMessage = null;
		try {
			receviedOutputMessage = clientMessageUtility.buildOutputMessage(this.getClass().getClassLoader(), outputMessageWrapReadableMiddleObject);
		} catch (DynamicClassCallException | BodyFormatException e) {
			fail("fail to build a output message");
		}
		
		if (! (receviedOutputMessage instanceof SelfExnRes)) {
			fail("서버 메시지 코덱 얻기 실패 실험 실패::강제적으로 서버 메시지 코덱 얻기 실패 예외를 던졌지만 그에 대한 처리 결과 메시지인 SelfExnRes 미 발생");
		}
		
		SelfExnRes selfExnRes = (SelfExnRes)receviedOutputMessage;
		
		if (! selfExnRes.getErrorType().equals(ErrorType.DynamicClassCallException)) {
			fail("서버 메시지 코덱 얻기 실패 실험 실패::강제적으로 서버 메시지 코덱 얻기 실패 예외를 던졌지만 그에 대한 에러 종류가 DynamicClassCallException 가 아님");
		}
		
		if (selfExnRes.getErrorReason().indexOf("fail to get the server message codec of the input message") < 0) {
			fail("서버 메시지 코덱 얻기 실패 실험 실패::강제적으로 서버 메시지 코덱 얻기 실패 예외를 던졌지만 그 원인이 입력 메시지 코덱 얻기 실패가 아님");
		}
		
		// "fail to get the server message codec of the input message";

		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();

		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		// socketResourceOfFromSC.close();
		socketResourceOfFromSC.releaseResources();
	}

	@Test
	public void testExecute_failToGetServerMessageCodec_통제벗어난에러() {
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		/*
		 * ClassLoader currentClassLoader = this.getClass().getClassLoader(); final
		 * String ClassLoaderClassPackagePrefixName = "kr.pe.sinnori.impl.";
		 */
		
		ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue = 
				new ArrayBlockingQueue<WrapReadableMiddleObject>(10);
		
		// MailboxIF inputMessageMailbox = new SimpleMailbox(wrapReadableMiddleObjectQueue);

		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}

		DataPacketBufferPoolIF dataPacketBufferPool = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;

			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize,
						dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}

		MessageProtocolIF messageProtocol = null;
		{
			messageProtocol = new THBMessageProtocol(dataPacketBufferMaxCntPerMessage, streamCharsetEncoder,
					streamCharsetDecoder, dataPacketBufferPool);
		}

		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);

		SocketResource socketResourceOfFromSC = null;
		{
			class ServerExecutorMock implements ServerExecutorIF {
				private ReceivedMessageBlockingQueueIF wrapMessageBlockingQueue  = null;
				
				public ServerExecutorMock(ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue) {					
					wrapMessageBlockingQueue  = new SimpleReceivedMessageBlockingQueue(wrapReadableMiddleObjectQueue);
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {					
				}

				@Override
				public int getNumberOfConnection() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {					
				}

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
					
				}

				@Override
				public ReceivedMessageBlockingQueueIF getWrapMessageBlockingQueue() {
					return wrapMessageBlockingQueue;
				}
				
			}
			
			ServerExecutorIF executorOfOwnerSC = new ServerExecutorMock(wrapReadableMiddleObjectQueue);

			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage, MessageProtocolIF messageProtocol,
						DataPacketBufferPoolIF dataPacketBufferQueueManager) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfConnection() {
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
							toLetter.getWrapBufferList(), streamCharsetDecoder, dataPacketBufferQueueManager);

					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						fsis.getUBPascalString();
						fsis.getUnsignedShort();
						fsis.getInt();

						AbstractMessageDecoder messageDecoder = (AbstractMessageDecoder) Class
								.forName(IOPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID))
								.newInstance();

						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);

						if (!(resObj instanceof SelfExnRes)) {
							String errorMessage = String.format(
									"this output message[%s] is not a expected SelfExnRes message", resObj.toString());
							fail(errorMessage);
						}

						SelfExnRes selfExnRes = (SelfExnRes) resObj;
						if (!selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}

						if (!selfExnRes.getErrorType().equals(SelfExn.ErrorType.DynamicClassCallException)) {
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

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
				}
			}

			OutputMessageWriterIF outputMessageWriterOfOwnerSC = new OutputMessageWriterMock(streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, messageProtocol, dataPacketBufferPool);

			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}

			socketResourceOfFromSC = new SocketResource(fromSC, executorOfOwnerSC,
					outputMessageWriterOfOwnerSC, socketOutputStreamOfOwnerSC, personalLoginManagerOfFromSC);
		}

		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;

			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewAcceptedSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
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

			@Override
			public void setServerExecutorPool(ServerExecutorPoolIF serverExecutorPool) {
				// TODO Auto-generated method stub
				
			}
		}

		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Empty emptyReq = new Empty();
			emptyReq.messageHeaderInfo.mailboxID = 1;
			emptyReq.messageHeaderInfo.mailID = 3;

			AbstractMessage message = emptyReq;

			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder) Class
						.forName(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID()))
						.newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}

			ArrayDeque<WrapBuffer> wrapBufferListOfInputMessage = null;
			try {
				wrapBufferListOfInputMessage = messageProtocol.M2S(message, messageEncoder);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("2");

			for (WrapBuffer inputMessageWrapBuffer : wrapBufferListOfInputMessage) {
				if (inputMessageWrapBuffer.isInQueue()) {
					fail("bad wrap buffer where is in of queue");
				}

				ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
				inputMessageByteBuffer.position(inputMessageByteBuffer.limit());
			}

			// log.info("3");

			SocketOutputStream sos = null;
			try {
				sos = new SocketOutputStream(wrapBufferListOfInputMessage, streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("sos.size={}", sos.size());

			// log.info("4");
			
			try {
				messageProtocol.S2MList(fromSC, sos, socketResourceOfFromSC.getServerExecutor().getWrapMessageBlockingQueue());
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			if (1 != wrapReadableMiddleObjectQueue.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}

			wrapReadableMiddleObject = wrapReadableMiddleObjectQueue.poll();
		}

		class AbstractServerTaskMock extends AbstractServerTask {

			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}

		}
		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();

		class ServerSimpleClassLoaderMock implements ServerSimpleClassLoaderIF {
			@Override
			public MessageCodecIF getMessageCodec(String messageID) throws DynamicClassCallException {

				throw new NullPointerException();
			}
		}
		serverTaskMock.setServerSimpleClassloader(new ServerSimpleClassLoaderMock());

		try {
			int index = 0;
			String projectName = "sample_test";

			serverTaskMock.execute(index, projectName, fromSC, socketResourceManager, socketResourceOfFromSC,
					personalLoginManagerOfFromSC, wrapReadableMiddleObject, messageProtocol);
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

		ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue = new ArrayBlockingQueue<WrapReadableMiddleObject>(10);
		
		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}

		DataPacketBufferPoolIF dataPacketBufferPool = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;

			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize,
						dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}

		MessageProtocolIF messageProtocol = null;
		{

			messageProtocol = new THBMessageProtocol(dataPacketBufferMaxCntPerMessage, streamCharsetEncoder,
					streamCharsetDecoder, dataPacketBufferPool);
		}

		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);

		SocketResource socketResourceOfFromSC = null;
		{
			class ServerExecutorMock implements ServerExecutorIF {
				private ReceivedMessageBlockingQueueIF wrapMessageBlockingQueue  = null;
				
				public ServerExecutorMock(ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue) {					
					wrapMessageBlockingQueue  = new SimpleReceivedMessageBlockingQueue(wrapReadableMiddleObjectQueue);
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {					
				}

				@Override
				public int getNumberOfConnection() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {					
				}

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
					
				}

				@Override
				public ReceivedMessageBlockingQueueIF getWrapMessageBlockingQueue() {
					return wrapMessageBlockingQueue;
				}
				
			}
			
			ServerExecutorIF executorOfOwnerSC = new ServerExecutorMock(wrapReadableMiddleObjectQueue);

			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage, MessageProtocolIF messageProtocol,
						DataPacketBufferPoolIF dataPacketBufferQueueManager) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfConnection() {
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
							toLetter.getWrapBufferList(), streamCharsetDecoder, dataPacketBufferQueueManager);

					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						fsis.getUBPascalString();
						fsis.getUnsignedShort();
						fsis.getInt();

						AbstractMessageDecoder messageDecoder = (AbstractMessageDecoder) Class
								.forName(IOPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID))
								.newInstance();

						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);

						if (!(resObj instanceof SelfExnRes)) {
							String errorMessage = String.format(
									"this output message[%s] is not a expected SelfExnRes message", resObj.toString());
							fail(errorMessage);
						}

						SelfExnRes selfExnRes = (SelfExnRes) resObj;
						if (!selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}

						if (!selfExnRes.getErrorType().equals(SelfExn.ErrorType.DynamicClassCallException)) {
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

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
				}
			}

			OutputMessageWriterIF outputMessageWriterOfOwnerSC = new OutputMessageWriterMock(streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, messageProtocol, dataPacketBufferPool);

			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}

			socketResourceOfFromSC = new SocketResource(fromSC, executorOfOwnerSC,
					outputMessageWriterOfOwnerSC, socketOutputStreamOfOwnerSC, personalLoginManagerOfFromSC);
		}

		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;

			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewAcceptedSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
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

			@Override
			public void setServerExecutorPool(ServerExecutorPoolIF serverExecutorPool) {
				// TODO Auto-generated method stub
				
			}
		}

		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Empty emptyReq = new Empty();
			emptyReq.messageHeaderInfo.mailboxID = 1;
			emptyReq.messageHeaderInfo.mailID = 3;

			AbstractMessage message = emptyReq;

			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder) Class
						.forName(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID()))
						.newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}

			ArrayDeque<WrapBuffer> wrapBufferListOfInputMessage = null;
			try {
				wrapBufferListOfInputMessage = messageProtocol.M2S(message, messageEncoder);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("2");

			for (WrapBuffer inputMessageWrapBuffer : wrapBufferListOfInputMessage) {
				if (inputMessageWrapBuffer.isInQueue()) {
					fail("bad wrap buffer where is in of queue");
				}

				ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
				inputMessageByteBuffer.position(inputMessageByteBuffer.limit());
			}

			// log.info("3");

			SocketOutputStream sos = null;
			try {
				sos = new SocketOutputStream(wrapBufferListOfInputMessage, streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("sos.size={}", sos.size());

			// log.info("4");

			
			try {
				messageProtocol.S2MList(fromSC, sos, socketResourceOfFromSC.getServerExecutor().getWrapMessageBlockingQueue());
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			if (1 != wrapReadableMiddleObjectQueue.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}

			wrapReadableMiddleObject = wrapReadableMiddleObjectQueue.poll();
		}

		class AbstractServerTaskMock extends AbstractServerTask {

			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}

		}

		class ServerSimpleClassLoaderMock implements ServerSimpleClassLoaderIF {
			@Override
			public MessageCodecIF getMessageCodec(String messageID) throws DynamicClassCallException {
				class MessageCodecMock implements MessageCodecIF {

					@Override
					public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {
						throw new DynamicClassCallException("메시지 디코더 통제된 에러");
					}

					@Override
					public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {
						return null;
					}

				}

				return new MessageCodecMock();
			}
		}

		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();
		serverTaskMock.setServerSimpleClassloader(new ServerSimpleClassLoaderMock());

		try {
			int index = 0;
			String projectName = "sample_test";

			serverTaskMock.execute(index, projectName, fromSC, socketResourceManager, socketResourceOfFromSC,
					personalLoginManagerOfFromSC, wrapReadableMiddleObject, messageProtocol);
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
		
		ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue = new ArrayBlockingQueue<WrapReadableMiddleObject>(10);

		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}

		DataPacketBufferPoolIF dataPacketBufferPool = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;

			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize,
						dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}

		MessageProtocolIF messageProtocol = null;
		{
			messageProtocol = new THBMessageProtocol(dataPacketBufferMaxCntPerMessage, streamCharsetEncoder,
					streamCharsetDecoder, dataPacketBufferPool);
		}

		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);

		SocketResource socketResourceOfFromSC = null;
		{
			class ServerExecutorMock implements ServerExecutorIF {
				private ReceivedMessageBlockingQueueIF wrapMessageBlockingQueue  = null;
				
				public ServerExecutorMock(ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue) {					
					wrapMessageBlockingQueue  = new SimpleReceivedMessageBlockingQueue(wrapReadableMiddleObjectQueue);
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {					
				}

				@Override
				public int getNumberOfConnection() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {					
				}

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
					
				}

				@Override
				public ReceivedMessageBlockingQueueIF getWrapMessageBlockingQueue() {
					return wrapMessageBlockingQueue;
				}
				
			}
			
			ServerExecutorIF executorOfOwnerSC = new ServerExecutorMock(wrapReadableMiddleObjectQueue);

			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage, MessageProtocolIF messageProtocol,
						DataPacketBufferPoolIF dataPacketBufferQueueManager) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfConnection() {
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
							toLetter.getWrapBufferList(), streamCharsetDecoder, dataPacketBufferQueueManager);

					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						fsis.getUBPascalString();
						fsis.getUnsignedShort();
						fsis.getInt();

						AbstractMessageDecoder messageDecoder = (AbstractMessageDecoder) Class
								.forName(IOPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID))
								.newInstance();

						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);

						if (!(resObj instanceof SelfExnRes)) {
							String errorMessage = String.format(
									"this output message[%s] is not a expected SelfExnRes message", resObj.toString());
							fail(errorMessage);
						}

						SelfExnRes selfExnRes = (SelfExnRes) resObj;
						if (!selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}

						if (!selfExnRes.getErrorType().equals(SelfExn.ErrorType.DynamicClassCallException)) {
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

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
				}
			}

			OutputMessageWriterIF outputMessageWriterOfOwnerSC = new OutputMessageWriterMock(streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, messageProtocol, dataPacketBufferPool);

			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}

			socketResourceOfFromSC = new SocketResource(fromSC, executorOfOwnerSC,
					outputMessageWriterOfOwnerSC, socketOutputStreamOfOwnerSC, personalLoginManagerOfFromSC);
		}

		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;

			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewAcceptedSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
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

			@Override
			public void setServerExecutorPool(ServerExecutorPoolIF serverExecutorPool) {
				// TODO Auto-generated method stub
				
			}
		}

		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Empty emptyReq = new Empty();
			emptyReq.messageHeaderInfo.mailboxID = 1;
			emptyReq.messageHeaderInfo.mailID = 3;

			AbstractMessage message = emptyReq;

			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder) Class
						.forName(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID()))
						.newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}

			ArrayDeque<WrapBuffer> wrapBufferListOfInputMessage = null;
			try {
				wrapBufferListOfInputMessage = messageProtocol.M2S(message, messageEncoder);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("2");

			for (WrapBuffer inputMessageWrapBuffer : wrapBufferListOfInputMessage) {
				if (inputMessageWrapBuffer.isInQueue()) {
					fail("bad wrap buffer where is in of queue");
				}

				ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
				inputMessageByteBuffer.position(inputMessageByteBuffer.limit());
			}

			// log.info("3");

			SocketOutputStream sos = null;
			try {
				sos = new SocketOutputStream(wrapBufferListOfInputMessage, streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("sos.size={}", sos.size());

			// log.info("4");

			
			try {
				messageProtocol.S2MList(fromSC, sos, socketResourceOfFromSC.getServerExecutor().getWrapMessageBlockingQueue());
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			if (1 != wrapReadableMiddleObjectQueue.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}

			wrapReadableMiddleObject = wrapReadableMiddleObjectQueue.poll();
		}

		class AbstractServerTaskMock extends AbstractServerTask {
			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}

		}

		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();

		class ServerSimpleClassLoaderMock implements ServerSimpleClassLoaderIF {
			@Override
			public MessageCodecIF getMessageCodec(String messageID) throws DynamicClassCallException {
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
		}
		serverTaskMock.setServerSimpleClassloader(new ServerSimpleClassLoaderMock());

		try {
			int index = 0;
			String projectName = "sample_test";

			serverTaskMock.execute(index, projectName, fromSC, socketResourceManager, socketResourceOfFromSC,
					personalLoginManagerOfFromSC, wrapReadableMiddleObject, messageProtocol);
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
		
		ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue = new ArrayBlockingQueue<WrapReadableMiddleObject>(10);

		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}

		DataPacketBufferPoolIF dataPacketBufferPool = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;

			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize,
						dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}

		MessageProtocolIF messageProtocol = null;
		{
			messageProtocol = new THBMessageProtocol(dataPacketBufferMaxCntPerMessage, streamCharsetEncoder,
					streamCharsetDecoder, dataPacketBufferPool);
		}

		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);

		SocketResource socketResourceOfFromSC = null;
		{
			class ServerExecutorMock implements ServerExecutorIF {
				private ReceivedMessageBlockingQueueIF wrapMessageBlockingQueue  = null;
				
				public ServerExecutorMock(ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue) {					
					wrapMessageBlockingQueue  = new SimpleReceivedMessageBlockingQueue(wrapReadableMiddleObjectQueue);
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {					
				}

				@Override
				public int getNumberOfConnection() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {					
				}

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
					
				}

				@Override
				public ReceivedMessageBlockingQueueIF getWrapMessageBlockingQueue() {
					return wrapMessageBlockingQueue;
				}
				
			}
			
			ServerExecutorIF executorOfOwnerSC = new ServerExecutorMock(wrapReadableMiddleObjectQueue);

			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage, MessageProtocolIF messageProtocol,
						DataPacketBufferPoolIF dataPacketBufferQueueManager) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfConnection() {
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
							toLetter.getWrapBufferList(), streamCharsetDecoder, dataPacketBufferQueueManager);

					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						fsis.getUBPascalString();
						fsis.getUnsignedShort();
						fsis.getInt();

						AbstractMessageDecoder messageDecoder = (AbstractMessageDecoder) Class
								.forName(IOPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID))
								.newInstance();

						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);

						if (!(resObj instanceof SelfExnRes)) {
							String errorMessage = String.format(
									"this output message[%s] is not a expected SelfExnRes message", resObj.toString());
							fail(errorMessage);
						}

						SelfExnRes selfExnRes = (SelfExnRes) resObj;
						if (!selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}

						if (!selfExnRes.getErrorType().equals(SelfExn.ErrorType.BodyFormatException)) {
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

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
				}
			}

			OutputMessageWriterIF outputMessageWriterOfOwnerSC = new OutputMessageWriterMock(streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, messageProtocol, dataPacketBufferPool);

			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}

			socketResourceOfFromSC = new SocketResource(fromSC, executorOfOwnerSC,
					outputMessageWriterOfOwnerSC, socketOutputStreamOfOwnerSC, personalLoginManagerOfFromSC);
		}

		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;

			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewAcceptedSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
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

			@Override
			public void setServerExecutorPool(ServerExecutorPoolIF serverExecutorPool) {
				// TODO Auto-generated method stub
				
			}
		}

		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Empty emptyReq = new Empty();
			emptyReq.messageHeaderInfo.mailboxID = 1;
			emptyReq.messageHeaderInfo.mailID = 3;

			AbstractMessage message = emptyReq;

			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder) Class
						.forName(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID()))
						.newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}

			ArrayDeque<WrapBuffer> wrapBufferListOfInputMessage = null;
			try {
				wrapBufferListOfInputMessage = messageProtocol.M2S(message, messageEncoder);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("2");

			for (WrapBuffer inputMessageWrapBuffer : wrapBufferListOfInputMessage) {
				if (inputMessageWrapBuffer.isInQueue()) {
					fail("bad wrap buffer where is in of queue");
				}

				ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
				inputMessageByteBuffer.position(inputMessageByteBuffer.limit());
			}

			// log.info("3");

			SocketOutputStream sos = null;
			try {
				sos = new SocketOutputStream(wrapBufferListOfInputMessage, streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("sos.size={}", sos.size());

			// log.info("4");

			
			try {
				messageProtocol.S2MList(fromSC, sos, socketResourceOfFromSC.getServerExecutor().getWrapMessageBlockingQueue());
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			if (1 != wrapReadableMiddleObjectQueue.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}

			wrapReadableMiddleObject = wrapReadableMiddleObjectQueue.poll();
		}

		class AbstractServerTaskMock extends AbstractServerTask {

			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}

		}

		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();

		class ServerSimpleClassLoaderMock implements ServerSimpleClassLoaderIF {
			@Override
			public MessageCodecIF getMessageCodec(String messageID) throws DynamicClassCallException {
				class MessageCodecMock implements MessageCodecIF {

					@Override
					public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {
						class MessageDcoderMock extends AbstractMessageDecoder {

							@Override
							protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder,
									Object middleReadObj) throws BodyFormatException {
								throw new BodyFormatException("항목 xxx 항목의 타입(expected=Long, acutal=Integer)이 다릅니다");
							}

						}
						return new MessageDcoderMock();
					}

					@Override
					public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {
						return null;
					}

				}

				return new MessageCodecMock();
			}
		}
		serverTaskMock.setServerSimpleClassloader(new ServerSimpleClassLoaderMock());

		try {
			int index = 0;
			String projectName = "sample_test";

			serverTaskMock.execute(index, projectName, fromSC, socketResourceManager, socketResourceOfFromSC,
					personalLoginManagerOfFromSC, wrapReadableMiddleObject, messageProtocol);
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
		
		ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue = new ArrayBlockingQueue<WrapReadableMiddleObject>(10);

		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}

		DataPacketBufferPoolIF dataPacketBufferPool = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;

			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize,
						dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}

		MessageProtocolIF messageProtocol = null;
		{
			messageProtocol = new THBMessageProtocol(dataPacketBufferMaxCntPerMessage, streamCharsetEncoder,
					streamCharsetDecoder, dataPacketBufferPool);
		}

		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);

		SocketResource socketResourceOfFromSC = null;
		{
			class ServerExecutorMock implements ServerExecutorIF {
				private ReceivedMessageBlockingQueueIF wrapMessageBlockingQueue  = null;
				
				public ServerExecutorMock(ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue) {					
					wrapMessageBlockingQueue  = new SimpleReceivedMessageBlockingQueue(wrapReadableMiddleObjectQueue);
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {					
				}

				@Override
				public int getNumberOfConnection() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {					
				}

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
					
				}

				@Override
				public ReceivedMessageBlockingQueueIF getWrapMessageBlockingQueue() {
					return wrapMessageBlockingQueue;
				}
				
			}
			
			ServerExecutorIF executorOfOwnerSC = new ServerExecutorMock(wrapReadableMiddleObjectQueue);

			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage, MessageProtocolIF messageProtocol,
						DataPacketBufferPoolIF dataPacketBufferQueueManager) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfConnection() {
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
							toLetter.getWrapBufferList(), streamCharsetDecoder, dataPacketBufferQueueManager);

					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						fsis.getUBPascalString();
						fsis.getUnsignedShort();
						fsis.getInt();

						AbstractMessageDecoder messageDecoder = (AbstractMessageDecoder) Class
								.forName(IOPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID))
								.newInstance();

						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);

						if (!(resObj instanceof SelfExnRes)) {
							String errorMessage = String.format(
									"this output message[%s] is not a expected SelfExnRes message", resObj.toString());
							fail(errorMessage);
						}

						SelfExnRes selfExnRes = (SelfExnRes) resObj;
						if (!selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}

						if (!selfExnRes.getErrorType().equals(SelfExn.ErrorType.BodyFormatException)) {
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

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
				}
			}

			OutputMessageWriterIF outputMessageWriterOfOwnerSC = new OutputMessageWriterMock(streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, messageProtocol, dataPacketBufferPool);

			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}

			socketResourceOfFromSC = new SocketResource(fromSC, executorOfOwnerSC,
					outputMessageWriterOfOwnerSC, socketOutputStreamOfOwnerSC, personalLoginManagerOfFromSC);
		}

		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;

			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewAcceptedSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
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

			@Override
			public void setServerExecutorPool(ServerExecutorPoolIF serverExecutorPool) {
				// TODO Auto-generated method stub
				
			}
		}

		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Empty emptyReq = new Empty();
			emptyReq.messageHeaderInfo.mailboxID = 1;
			emptyReq.messageHeaderInfo.mailID = 3;

			AbstractMessage message = emptyReq;

			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder) Class
						.forName(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID()))
						.newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}

			ArrayDeque<WrapBuffer> wrapBufferListOfInputMessage = null;
			try {
				wrapBufferListOfInputMessage = messageProtocol.M2S(message, messageEncoder);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("2");

			for (WrapBuffer inputMessageWrapBuffer : wrapBufferListOfInputMessage) {
				if (inputMessageWrapBuffer.isInQueue()) {
					fail("bad wrap buffer where is in of queue");
				}

				ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
				inputMessageByteBuffer.position(inputMessageByteBuffer.limit());
			}

			// log.info("3");

			SocketOutputStream sos = null;
			try {
				sos = new SocketOutputStream(wrapBufferListOfInputMessage, streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("sos.size={}", sos.size());

			// log.info("4");

			
			try {
				messageProtocol.S2MList(fromSC, sos, socketResourceOfFromSC.getServerExecutor().getWrapMessageBlockingQueue());
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			if (1 != wrapReadableMiddleObjectQueue.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}

			wrapReadableMiddleObject = wrapReadableMiddleObjectQueue.poll();
		}

		class AbstractServerTaskMock extends AbstractServerTask {
			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}

		}

		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();
		class ServerSimpleClassLoaderMock implements ServerSimpleClassLoaderIF {
			@Override
			public MessageCodecIF getMessageCodec(String messageID) throws DynamicClassCallException {
				class MessageCodecMock implements MessageCodecIF {

					@Override
					public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {
						class MessageDcoderMock extends AbstractMessageDecoder {

							@Override
							protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder,
									Object middleReadObj) throws BodyFormatException {
								throw new NullPointerException();
							}

						}
						return new MessageDcoderMock();
					}

					@Override
					public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {
						return null;
					}

				}

				return new MessageCodecMock();
			}
		}

		serverTaskMock.setServerSimpleClassloader(new ServerSimpleClassLoaderMock());

		try {
			int index = 0;
			String projectName = "sample_test";

			serverTaskMock.execute(index, projectName, fromSC, socketResourceManager, socketResourceOfFromSC,
					personalLoginManagerOfFromSC, wrapReadableMiddleObject, messageProtocol);
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

		ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue = new ArrayBlockingQueue<WrapReadableMiddleObject>(10);
		
		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}

		DataPacketBufferPoolIF dataPacketBufferPool = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;

			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize,
						dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}

		MessageProtocolIF messageProtocol = null;
		{
			messageProtocol = new THBMessageProtocol(dataPacketBufferMaxCntPerMessage, streamCharsetEncoder,
					streamCharsetDecoder, dataPacketBufferPool);
		}

		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);

		SocketResource socketResourceOfFromSC = null;
		{
			class ServerExecutorMock implements ServerExecutorIF {
				private ReceivedMessageBlockingQueueIF wrapMessageBlockingQueue  = null;
				
				public ServerExecutorMock(ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue) {					
					wrapMessageBlockingQueue  = new SimpleReceivedMessageBlockingQueue(wrapReadableMiddleObjectQueue);
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {					
				}

				@Override
				public int getNumberOfConnection() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {					
				}

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
					
				}

				@Override
				public ReceivedMessageBlockingQueueIF getWrapMessageBlockingQueue() {
					return wrapMessageBlockingQueue;
				}
				
			}
			
			ServerExecutorIF executorOfOwnerSC = new ServerExecutorMock(wrapReadableMiddleObjectQueue);

			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage, MessageProtocolIF messageProtocol,
						DataPacketBufferPoolIF dataPacketBufferQueueManager) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfConnection() {
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
							toLetter.getWrapBufferList(), streamCharsetDecoder, dataPacketBufferQueueManager);

					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						fsis.getUBPascalString();
						fsis.getUnsignedShort();
						fsis.getInt();

						AbstractMessageDecoder messageDecoder = (AbstractMessageDecoder) Class
								.forName(IOPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID))
								.newInstance();

						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);

						if (!(resObj instanceof SelfExnRes)) {
							String errorMessage = String.format(
									"this output message[%s] is not a expected SelfExnRes message", resObj.toString());
							fail(errorMessage);
						}

						SelfExnRes selfExnRes = (SelfExnRes) resObj;
						if (!selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}

						if (!selfExnRes.getErrorType().equals(SelfExn.ErrorType.ServerTaskException)) {
							fail("에러 종류가 서버 타스크 클래스가 아님");
						}

						if (selfExnRes.getErrorReason().indexOf(
								"the synchronous output message can't be added becase another synchronous message is already registered in the toLetter list") < 0) {
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

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
				}
			}

			OutputMessageWriterIF outputMessageWriterOfOwnerSC = new OutputMessageWriterMock(streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, messageProtocol, dataPacketBufferPool);

			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}

			socketResourceOfFromSC = new SocketResource(fromSC, executorOfOwnerSC,
					outputMessageWriterOfOwnerSC, socketOutputStreamOfOwnerSC, personalLoginManagerOfFromSC);
		}

		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;

			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewAcceptedSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
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

			@Override
			public void setServerExecutorPool(ServerExecutorPoolIF serverExecutorPool) {
				// TODO Auto-generated method stub
				
			}
		}

		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Empty emptyReq = new Empty();
			emptyReq.messageHeaderInfo.mailboxID = 1;
			emptyReq.messageHeaderInfo.mailID = 3;

			AbstractMessage message = emptyReq;

			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder) Class
						.forName(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID()))
						.newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}

			ArrayDeque<WrapBuffer> wrapBufferListOfInputMessage = null;
			try {
				wrapBufferListOfInputMessage = messageProtocol.M2S(message, messageEncoder);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("2");

			for (WrapBuffer inputMessageWrapBuffer : wrapBufferListOfInputMessage) {
				if (inputMessageWrapBuffer.isInQueue()) {
					fail("bad wrap buffer where is in of queue");
				}

				ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
				inputMessageByteBuffer.position(inputMessageByteBuffer.limit());
			}

			// log.info("3");

			SocketOutputStream sos = null;
			try {
				sos = new SocketOutputStream(wrapBufferListOfInputMessage, streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("sos.size={}", sos.size());

			// log.info("4");

			
			try {
				messageProtocol.S2MList(fromSC, sos, socketResourceOfFromSC.getServerExecutor().getWrapMessageBlockingQueue());
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			if (1 != wrapReadableMiddleObjectQueue.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}

			wrapReadableMiddleObject = wrapReadableMiddleObjectQueue.poll();
		}

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
		class ServerSimpleClassLoaderMock implements ServerSimpleClassLoaderIF {
			@Override
			public MessageCodecIF getMessageCodec(String messageID) throws DynamicClassCallException {
				class MessageCodecMock implements MessageCodecIF {

					@Override
					public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {

						return new EmptyDecoder();
					}

					@Override
					public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {
						return new EmptyEncoder();
					}

				}

				return new MessageCodecMock();
			}
		}

		serverTaskMock.setServerSimpleClassloader(new ServerSimpleClassLoaderMock());

		try {
			int index = 0;
			String projectName = "sample_test";

			serverTaskMock.execute(index, projectName, fromSC, socketResourceManager, socketResourceOfFromSC,
					personalLoginManagerOfFromSC, wrapReadableMiddleObject, messageProtocol);
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
		
		ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue = new ArrayBlockingQueue<WrapReadableMiddleObject>(10);

		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			fail("fail to open new socket channel");
		}

		DataPacketBufferPoolIF dataPacketBufferPool = null;
		{
			ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
			boolean isDirect = false;
			int dataPacketBufferSize = 4096;
			int dataPacketBufferPoolSize = 100;

			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize,
						dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			}
		}

		MessageProtocolIF messageProtocol = null;
		{
			messageProtocol = new THBMessageProtocol(dataPacketBufferMaxCntPerMessage, streamCharsetEncoder,
					streamCharsetDecoder, dataPacketBufferPool);
		}

		PersonalLoginManagerIF personalLoginManagerOfFromSC = mock(PersonalLoginManagerIF.class);

		SocketResource socketResourceOfFromSC = null;
		{
			class ServerExecutorMock implements ServerExecutorIF {
				private ReceivedMessageBlockingQueueIF wrapMessageBlockingQueue  = null;
				
				public ServerExecutorMock(ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue) {					
					wrapMessageBlockingQueue  = new SimpleReceivedMessageBlockingQueue(wrapReadableMiddleObjectQueue);
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {					
				}

				@Override
				public int getNumberOfConnection() {
					return 0;
				}

				@Override
				public void removeSocket(SocketChannel sc) {					
				}

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
					
				}

				@Override
				public ReceivedMessageBlockingQueueIF getWrapMessageBlockingQueue() {
					return wrapMessageBlockingQueue;
				}
				
			}
			
			ServerExecutorIF executorOfOwnerSC = new ServerExecutorMock(wrapReadableMiddleObjectQueue);

			class OutputMessageWriterMock implements OutputMessageWriterIF {
				private CharsetDecoder streamCharsetDecoder = null;
				private int dataPacketBufferMaxCntPerMessage;
				private MessageProtocolIF messageProtocol = null;
				private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;

				public OutputMessageWriterMock(CharsetDecoder streamCharsetDecoder,
						int dataPacketBufferMaxCntPerMessage, MessageProtocolIF messageProtocol,
						DataPacketBufferPoolIF dataPacketBufferQueueManager) {
					this.streamCharsetDecoder = streamCharsetDecoder;
					this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
					this.messageProtocol = messageProtocol;
					this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
				}

				@Override
				public void addNewSocket(SocketChannel newSC) {
				}

				@Override
				public int getNumberOfConnection() {
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
							toLetter.getWrapBufferList(), streamCharsetDecoder, dataPacketBufferQueueManager);

					try {
						fsis.skip(messageProtocol.getMessageHeaderSize());
						fsis.getUBPascalString();
						fsis.getUnsignedShort();
						fsis.getInt();

						AbstractMessageDecoder messageDecoder = (AbstractMessageDecoder) Class
								.forName(IOPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID))
								.newInstance();

						AbstractMessage resObj = messageDecoder.decode(dhbSingleItemDecoder, fsis);

						if (!(resObj instanceof SelfExnRes)) {
							String errorMessage = String.format(
									"this output message[%s] is not a expected SelfExnRes message", resObj.toString());
							fail(errorMessage);
						}

						SelfExnRes selfExnRes = (SelfExnRes) resObj;
						if (!selfExnRes.getErrorPlace().equals(SelfExn.ErrorPlace.SERVER)) {
							fail("에러 장소가 서버가 아님");
						}

						if (!selfExnRes.getErrorType().equals(SelfExn.ErrorType.ServerTaskException)) {
							fail("에러 종류가 서버 타스크 클래스가 아님");
						}

						if (selfExnRes.getErrorReason().indexOf(
								"the synchronous output message can't be added becase the inputMessage is a asynchronous message") < 0) {
							fail("비동기 입력 메시지에 동기 출력 메시지를 보냈다는 에러가 아님");
						}

						// the synchronous output message can't be added becase the inputMessage is a
						// asynchronous message
						log.info("성공 :: {}", selfExnRes.toString());

					} catch (Error | Exception e) {
						log.warn("error", e);
						fail("unknow error::OutputMessageWriterMock::#putIntoQueue");
					} finally {
						fsis.close();
					}
				}

				@Override
				public boolean isAlive() {
					return false;
				}

				@Override
				public void start() {
				}

				@Override
				public void interrupt() {
				}
			}

			OutputMessageWriterIF outputMessageWriterOfOwnerSC = new OutputMessageWriterMock(streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, messageProtocol, dataPacketBufferPool);

			SocketOutputStream socketOutputStreamOfOwnerSC = null;
			try {
				socketOutputStreamOfOwnerSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}

			socketResourceOfFromSC = new SocketResource(fromSC, executorOfOwnerSC,
					outputMessageWriterOfOwnerSC, socketOutputStreamOfOwnerSC, personalLoginManagerOfFromSC);
		}

		class SocketResourceManagerMock implements SocketResourceManagerIF {
			private SocketResource socketResourceOfFromSC = null;

			public SocketResourceManagerMock(SocketResource socketResourceOfFromSC) {
				this.socketResourceOfFromSC = socketResourceOfFromSC;
			}

			@Override
			public void addNewAcceptedSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException {
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

			@Override
			public void setServerExecutorPool(ServerExecutorPoolIF serverExecutorPool) {
				// TODO Auto-generated method stub
				
			}
		}

		SocketResourceManagerIF socketResourceManager = new SocketResourceManagerMock(socketResourceOfFromSC);

		WrapReadableMiddleObject wrapReadableMiddleObject = null;
		{
			Empty emptyReq = new Empty();
			emptyReq.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
			emptyReq.messageHeaderInfo.mailID = 3;

			AbstractMessage message = emptyReq;

			AbstractMessageEncoder messageEncoder = null;
			try {
				messageEncoder = (AbstractMessageEncoder) Class
						.forName(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(message.getMessageID()))
						.newInstance();
			} catch (Exception e) {
				fail(message.getMessageID() + " 메시지 인코더 인스턴스 생성 실패");
			}

			ArrayDeque<WrapBuffer> wrapBufferListOfInputMessage = null;
			try {
				wrapBufferListOfInputMessage = messageProtocol.M2S(message, messageEncoder);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("2");

			for (WrapBuffer inputMessageWrapBuffer : wrapBufferListOfInputMessage) {
				if (inputMessageWrapBuffer.isInQueue()) {
					fail("bad wrap buffer where is in of queue");
				}

				ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
				inputMessageByteBuffer.position(inputMessageByteBuffer.limit());
			}

			// log.info("3");

			SocketOutputStream sos = null;
			try {
				sos = new SocketOutputStream(wrapBufferListOfInputMessage, streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			// log.info("sos.size={}", sos.size());

			// log.info("4");

			
			try {
				messageProtocol.S2MList(fromSC, sos, socketResourceOfFromSC.getServerExecutor().getWrapMessageBlockingQueue());
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			if (1 != wrapReadableMiddleObjectQueue.size()) {
				fail("출력 메시지 갯수가 1이 아닙니다.");
			}

			wrapReadableMiddleObject = wrapReadableMiddleObjectQueue.poll();
		}

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
		class ServerSimpleClassLoaderMock implements ServerSimpleClassLoaderIF {
			@Override
			public MessageCodecIF getMessageCodec(String messageID) throws DynamicClassCallException {
				class MessageCodecMock implements MessageCodecIF {

					@Override
					public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException {

						return new EmptyDecoder();
					}

					@Override
					public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException {
						return new EmptyEncoder();
					}

				}

				return new MessageCodecMock();
			}
		}
		
		serverTaskMock.setServerSimpleClassloader(new ServerSimpleClassLoaderMock());
		

		try {
			int index = 0;
			String projectName = "sample_test";

			serverTaskMock.execute(index, projectName, fromSC, socketResourceManager, socketResourceOfFromSC,
					personalLoginManagerOfFromSC, wrapReadableMiddleObject, messageProtocol);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}

		wrapReadableMiddleObject.closeReadableMiddleObject();
		socketResourceOfFromSC.close();
	}
}
