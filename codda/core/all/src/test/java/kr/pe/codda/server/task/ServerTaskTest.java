package kr.pe.codda.server.task;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayDeque;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.client.connection.ClientMessageUtility;
import kr.pe.codda.client.connection.ClientObjectCacheManager;
import kr.pe.codda.client.connection.ClientObjectCacheManagerIF;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.classloader.ServerSimpleClassLoaderIF;
import kr.pe.codda.common.etc.CharsetUtil;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.DataPacketBufferPool;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;
import kr.pe.codda.common.protocol.ReceivedMessageBlockingQueueIF;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;
import kr.pe.codda.common.protocol.thb.THBMessageProtocol;
import kr.pe.codda.common.type.SelfExn.ErrorType;
import kr.pe.codda.impl.message.Empty.Empty;
import kr.pe.codda.impl.message.Empty.EmptyDecoder;
import kr.pe.codda.impl.message.Empty.EmptyEncoder;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnRes;
import kr.pe.codda.server.AcceptedConnection;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.ProjectLoginManagerIF;
import kr.pe.codda.server.ServerIOEvenetControllerIF;
import kr.pe.codda.server.ServerObjectCacheManagerIF;

public class ServerTaskTest extends AbstractJunitTest {
	
	class ReceivedMessageBlockingQueueMock implements ReceivedMessageBlockingQueueIF {
		private ReadableMiddleObjectWrapper readableMiddleObjectWrapper = null;

		@Override
		public void putReceivedMessage(ReadableMiddleObjectWrapper readableMiddleObjectWrapper)
				throws InterruptedException {
			if (null == readableMiddleObjectWrapper) {
				fail("the parameter readableMiddleObjectWrapper is null");
			}
			
			if (null != this.readableMiddleObjectWrapper) {
				fail("메시지 추출은 1번만이야하지만  2번째 호출함");
			}
			this.readableMiddleObjectWrapper = readableMiddleObjectWrapper;
			
		}
		
		
		public ReadableMiddleObjectWrapper getReadableMiddleObjectWrapper() {
			return readableMiddleObjectWrapper;
		}
	}

	@Test
	public void testExecute_failToGetServerMessageCodec_통제된에러() {
		String projectName = "sample_test";
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		long socketTimeOut = 5000;
		int serverOutputMessageQueueCapacity = 5;		

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
		ProjectLoginManagerIF  projectLoginManagerMock = mock(ProjectLoginManagerIF.class);

		AcceptedConnection fromAcceptedConnection = null;
		{
			

			SocketOutputStream socketOutputStreamOfAcceptedSC = null;
			try {
				socketOutputStreamOfAcceptedSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}			
			SelectionKey personalSelectionKey = mock(SelectionKey.class);
			
			ServerIOEvenetControllerIF serverIOEvenetController = mock(ServerIOEvenetControllerIF.class);
			ServerObjectCacheManagerIF serverObjectCacheManager = mock(ServerObjectCacheManagerIF.class);
			
			fromAcceptedConnection = new AcceptedConnection(personalSelectionKey, fromSC, 
					projectName, socketTimeOut, serverOutputMessageQueueCapacity, 
					socketOutputStreamOfAcceptedSC,
					projectLoginManagerMock, messageProtocol,
					dataPacketBufferPool, serverIOEvenetController,
					serverObjectCacheManager);
		}
		
		

		ReadableMiddleObjectWrapper inputMessageWrapReadableMiddleObject = null;
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
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			try {
				messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}


			inputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			
			if (null == inputMessageWrapReadableMiddleObject) {
				fail("추출된 입력 메시지 없습니다");
			}
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
		
		PersonalLoginManagerIF fromPersonalLoginManager = mock(PersonalLoginManagerIF.class);

		try {
			serverTaskMock.execute(projectName,
					fromAcceptedConnection,			
					projectLoginManagerMock,						
					inputMessageWrapReadableMiddleObject,
					messageProtocol, fromPersonalLoginManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
		
		// log.info(readableMiddleObjectWrapperQueue.poll().toString());
		
		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = fromAcceptedConnection.getOutputMessageQueue();
		if (outputMessageQueue.size() != 1) {
			fail("예상 출력 메시지 갯수가 1개가 아님");
		}
		
		ArrayDeque<WrapBuffer> wrapBufferQueue = outputMessageQueue.poll();		
		
		for (WrapBuffer wrapBuffer : wrapBufferQueue) {
			ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
			byteBuffer.position(byteBuffer.remaining());
		}		
		
		SocketOutputStream sos = null;
		try {
			sos = new SocketOutputStream(wrapBufferQueue, streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
		} catch (NoMoreDataPacketBufferException e) {
			fail("fail to create a instance of SocketOutputStream");
		}
		
		
		ReadableMiddleObjectWrapper outputMessageWrapReadableMiddleObject = null;
		try {
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);			
			outputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == outputMessageWrapReadableMiddleObject) {
				fail("추출된 출력 메시지가 없습니다");
			}
		} catch (Exception e) {
			fail("fail to close");
		}
		
		
		AbstractMessage receviedOutputMessage = null;
		try {
			receviedOutputMessage = ClientMessageUtility.buildOutputMessage(messageProtocol,
					clientObjectCacheManager, this.getClass().getClassLoader(), outputMessageWrapReadableMiddleObject);
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
		
		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();

		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		// fromAcceptedConnection.close();
		fromAcceptedConnection.close();
	}

	@Test
	public void testExecute_failToGetServerMessageCodec_통제벗어난에러() {
		String projectName = "sample_test";
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;		
		long socketTimeOut = 5000;
		int serverOutputMessageQueueCapacity = 5; 
		/*
		 * ClassLoader currentClassLoader = this.getClass().getClassLoader(); final
		 * String ClassLoaderClassPackagePrefixName = "kr.pe.sinnori.impl.";
		 */
		
		// MailboxIF inputMessageMailbox = new SimpleMailbox(readableMiddleObjectWrapperQueue);

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
		ProjectLoginManagerIF  projectLoginManagerMock = mock(ProjectLoginManagerIF.class);
		
		

		AcceptedConnection fromAcceptedConnection = null;
		{	

			SocketOutputStream socketOutputStreamOfAcceptedSC = null;
			try {
				socketOutputStreamOfAcceptedSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}			
			SelectionKey personalSelectionKey = mock(SelectionKey.class);
			
			ServerIOEvenetControllerIF serverIOEvenetController = mock(ServerIOEvenetControllerIF.class);
			ServerObjectCacheManagerIF serverObjectCacheManagerMock = mock(ServerObjectCacheManagerIF.class);
			
			fromAcceptedConnection = new AcceptedConnection(personalSelectionKey, 
					fromSC, 
					projectName,	socketTimeOut, serverOutputMessageQueueCapacity, 
					socketOutputStreamOfAcceptedSC,
					projectLoginManagerMock, messageProtocol,
					dataPacketBufferPool, serverIOEvenetController,
					serverObjectCacheManagerMock);
		}
		

		ReadableMiddleObjectWrapper inputMessageWrapReadableMiddleObject = null;
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
			
			
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			
			try {
				messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			inputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == inputMessageWrapReadableMiddleObject) {
				fail("추출된 입력 메시지 없습니다");
			}
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
		PersonalLoginManagerIF fromPersonalLoginManager = mock(PersonalLoginManagerIF.class);

		try {
			serverTaskMock.execute( 
					projectName,
					fromAcceptedConnection,			
					projectLoginManagerMock,						
					inputMessageWrapReadableMiddleObject,
					messageProtocol,
					fromPersonalLoginManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
		
		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = fromAcceptedConnection.getOutputMessageQueue();
		if (outputMessageQueue.size() != 1) {
			fail("예상 출력 메시지 갯수가 1개가 아님");
		}
		
		ArrayDeque<WrapBuffer> wrapBufferQueue = outputMessageQueue.poll();		
		
		for (WrapBuffer wrapBuffer : wrapBufferQueue) {
			ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
			byteBuffer.position(byteBuffer.remaining());
		}		
		
		SocketOutputStream sos = null;
		try {
			sos = new SocketOutputStream(wrapBufferQueue, streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
		} catch (NoMoreDataPacketBufferException e) {
			fail("fail to create a instance of SocketOutputStream");
		}
		
		ReadableMiddleObjectWrapper outputMessageWrapReadableMiddleObject = null;
		try {
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);			
			outputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == outputMessageWrapReadableMiddleObject) {
				fail("추출된 출력 메시지가 없습니다");
			}
		} catch (Exception e) {
			fail("fail to close");
		}
		
		AbstractMessage receviedOutputMessage = null;
		try {
			receviedOutputMessage = ClientMessageUtility.buildOutputMessage(messageProtocol,
					clientObjectCacheManager, this.getClass().getClassLoader(), outputMessageWrapReadableMiddleObject);
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
		
		if (selfExnRes.getErrorReason().indexOf("unknown error::fail to get the server message codec of the input message") < 0) {
			fail("서버 메시지 코덱 얻기 실패 실험 실패::강제적으로 서버 메시지 코덱 얻기 실패 예외를 던졌지만 그 원인이 입력 메시지 코덱 얻기 실패가 아님");
		}
		
		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();

		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		fromAcceptedConnection.close();
	}

	@Test
	public void testExecute_failToGetMessageDecoder_통제된에러() {
		String projectName = "sample_test";
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		long socketTimeOut = 5000;
		int serverOutputMessageQueueCapacity = 5; 
		
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
		ProjectLoginManagerIF  projectLoginManagerMock = mock(ProjectLoginManagerIF.class);
		
		

		AcceptedConnection fromAcceptedConnection = null;
		{
			SocketOutputStream socketOutputStreamOfAcceptedSC = null;
			try {
				socketOutputStreamOfAcceptedSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}			
			SelectionKey personalSelectionKey = mock(SelectionKey.class);
			
			ServerIOEvenetControllerIF serverIOEvenetController = mock(ServerIOEvenetControllerIF.class);
			ServerObjectCacheManagerIF serverObjectCacheManagerMock = mock(ServerObjectCacheManagerIF.class);
			
			fromAcceptedConnection = new AcceptedConnection(personalSelectionKey, 
					fromSC, 
					projectName,	socketTimeOut, serverOutputMessageQueueCapacity, 
					socketOutputStreamOfAcceptedSC,
					projectLoginManagerMock, messageProtocol,
					dataPacketBufferPool, serverIOEvenetController,
					serverObjectCacheManagerMock);
		}

		ReadableMiddleObjectWrapper inputMessageWrapReadableMiddleObject = null;
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
			
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			
			try {
				messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			inputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == inputMessageWrapReadableMiddleObject) {
				fail("추출된 입력 메시지 없습니다");
			}
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
		PersonalLoginManagerIF fromPersonalLoginManager = mock(PersonalLoginManagerIF.class);

		try {
			serverTaskMock.execute( 
					projectName,
					fromAcceptedConnection,			
					projectLoginManagerMock,						
					inputMessageWrapReadableMiddleObject,
					messageProtocol,
					fromPersonalLoginManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}

		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = fromAcceptedConnection.getOutputMessageQueue();
		if (outputMessageQueue.size() != 1) {
			fail("예상 출력 메시지 갯수가 1개가 아님");
		}
		
		ArrayDeque<WrapBuffer> wrapBufferQueue = outputMessageQueue.poll();		
		
		for (WrapBuffer wrapBuffer : wrapBufferQueue) {
			ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
			byteBuffer.position(byteBuffer.remaining());
		}		
		
		SocketOutputStream sos = null;
		try {
			sos = new SocketOutputStream(wrapBufferQueue, streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
		} catch (NoMoreDataPacketBufferException e) {
			fail("fail to create a instance of SocketOutputStream");
		}
		
		ReadableMiddleObjectWrapper outputMessageWrapReadableMiddleObject = null;
		try {
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);			
			outputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == outputMessageWrapReadableMiddleObject) {
				fail("추출된 출력 메시지가 없습니다");
			}
		} catch (Exception e) {
			fail("fail to close");
		}
		
		AbstractMessage receviedOutputMessage = null;
		try {
			receviedOutputMessage = ClientMessageUtility.buildOutputMessage(messageProtocol,
					clientObjectCacheManager, this.getClass().getClassLoader(), outputMessageWrapReadableMiddleObject);
		} catch (DynamicClassCallException | BodyFormatException e) {
			fail("fail to build a output message");
		}
		
		if (! (receviedOutputMessage instanceof SelfExnRes)) {
			fail("입력 메시지 디코더 얻기 실패 실험 실패::강제적으로 입력 메시지 디코더 얻기 실패 예외를 던졌지만 그에 대한 처리 결과 메시지인 SelfExnRes 미 발생");
		}
		
		SelfExnRes selfExnRes = (SelfExnRes)receviedOutputMessage;
		
		if (! selfExnRes.getErrorType().equals(ErrorType.DynamicClassCallException)) {
			fail("입력 메시지 디코더 얻기 실패 실험 실패::강제적으로 입력 메시지 디코더 얻기 실패 예외를 던졌지만 그에 대한 에러 종류가 DynamicClassCallException 가 아님");
		}
		
		if (selfExnRes.getErrorReason().indexOf("fail to get a input message decoder") < 0) {
			fail("입력 메시지 디코더 얻기 실패 실험 실패::강제적으로 입력 메시지 디코더 얻기 실패 예외를 던졌지만 그 원인이 입력 메시지 디코더 얻기 실패가 아님");
		}
		
		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		fromAcceptedConnection.close();
	}

	@Test
	public void testExecute_failToGetMessageDecoder_통제벗어난에러() {
		String projectName = "sample_test";
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		long socketTimeOut = 5000;
		int serverOutputMessageQueueCapacity = 5; 
		
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
		ProjectLoginManagerIF  projectLoginManagerMock = mock(ProjectLoginManagerIF.class);
		
		

		AcceptedConnection fromAcceptedConnection = null;
		{
			

			SocketOutputStream socketOutputStreamOfAcceptedSC = null;
			try {
				socketOutputStreamOfAcceptedSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}			
			SelectionKey personalSelectionKey = mock(SelectionKey.class);
			
			ServerIOEvenetControllerIF serverIOEvenetController = mock(ServerIOEvenetControllerIF.class);
			ServerObjectCacheManagerIF serverObjectCacheManager = mock(ServerObjectCacheManagerIF.class);
			
			fromAcceptedConnection = new AcceptedConnection(personalSelectionKey, fromSC, 
					projectName, socketTimeOut, serverOutputMessageQueueCapacity, 
					socketOutputStreamOfAcceptedSC,
					projectLoginManagerMock, messageProtocol,
					dataPacketBufferPool, serverIOEvenetController,
					serverObjectCacheManager);
		}

		ReadableMiddleObjectWrapper inputMessageWrapReadableMiddleObject = null;
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
			
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			
			try {
				messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			inputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == inputMessageWrapReadableMiddleObject) {
				fail("추출된 입력 메시지 없습니다");
			}
		}

		class AbstractServerTaskMock extends AbstractServerTask {
			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}

		}

		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();
		PersonalLoginManagerIF fromPersonalLoginManager = mock(PersonalLoginManagerIF.class);

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
			serverTaskMock.execute( 
					projectName,
					fromAcceptedConnection,			
					projectLoginManagerMock,						
					inputMessageWrapReadableMiddleObject,
					messageProtocol,
					fromPersonalLoginManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}

		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = fromAcceptedConnection.getOutputMessageQueue();
		if (outputMessageQueue.size() != 1) {
			fail("예상 출력 메시지 갯수가 1개가 아님");
		}
		
		ArrayDeque<WrapBuffer> wrapBufferQueue = outputMessageQueue.poll();		
		
		for (WrapBuffer wrapBuffer : wrapBufferQueue) {
			ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
			byteBuffer.position(byteBuffer.remaining());
		}		
		
		SocketOutputStream sos = null;
		try {
			sos = new SocketOutputStream(wrapBufferQueue, streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
		} catch (NoMoreDataPacketBufferException e) {
			fail("fail to create a instance of SocketOutputStream");
		}
				
		ReadableMiddleObjectWrapper outputMessageWrapReadableMiddleObject = null;
		try {
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);			
			outputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == outputMessageWrapReadableMiddleObject) {
				fail("추출된 출력 메시지가 없습니다");
			}
		} catch (Exception e) {
			fail("fail to close");
		}
		
		AbstractMessage receviedOutputMessage = null;
		try {
			receviedOutputMessage = ClientMessageUtility.buildOutputMessage(messageProtocol,
					clientObjectCacheManager, this.getClass().getClassLoader(), outputMessageWrapReadableMiddleObject);
		} catch (DynamicClassCallException | BodyFormatException e) {
			fail("fail to build a output message");
		}
		
		if (! (receviedOutputMessage instanceof SelfExnRes)) {
			fail("입력 메시지 디코더 얻기 실패 실험 실패::강제적으로 입력 메시지 디코더 얻기 실패 예외를 던졌지만 그에 대한 처리 결과 메시지인 SelfExnRes 미 발생");
		}
		
		SelfExnRes selfExnRes = (SelfExnRes)receviedOutputMessage;
		
		if (! selfExnRes.getErrorType().equals(ErrorType.DynamicClassCallException)) {
			fail("입력 메시지 디코더 얻기 실패 실험 실패::강제적으로 입력 메시지 디코더 얻기 실패 예외를 던졌지만 그에 대한 에러 종류가 DynamicClassCallException 가 아님");
		}
		
		if (selfExnRes.getErrorReason().indexOf("unknown error::fail to get a input message decoder") < 0) {
			fail("입력 메시지 디코더 얻기 실패 실험 실패::강제적으로 입력 메시지 디코더 얻기 실패 예외를 던졌지만 그 원인이 입력 메시지 디코더 얻기 실패가 아님");
		}
		
		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		fromAcceptedConnection.close();
	}

	@Test
	public void testExecute_failToDecodeBody_통제된에러() {
		String projectName = "sample_test";
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		long socketTimeOut = 5000;
		int serverOutputMessageQueueCapacity = 5; 
		
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
		ProjectLoginManagerIF  projectLoginManagerMock = mock(ProjectLoginManagerIF.class);
		
		
		AcceptedConnection fromAcceptedConnection = null;
		{
			SocketOutputStream socketOutputStreamOfAcceptedSC = null;
			try {
				socketOutputStreamOfAcceptedSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}			
			SelectionKey personalSelectionKey = mock(SelectionKey.class);
			
			ServerIOEvenetControllerIF serverIOEvenetController = mock(ServerIOEvenetControllerIF.class);
			ServerObjectCacheManagerIF serverObjectCacheManager = mock(ServerObjectCacheManagerIF.class);
			
			fromAcceptedConnection = new AcceptedConnection(personalSelectionKey, fromSC, 
					projectName, socketTimeOut, serverOutputMessageQueueCapacity, 
					socketOutputStreamOfAcceptedSC,
					projectLoginManagerMock, messageProtocol,
					dataPacketBufferPool, serverIOEvenetController,
					serverObjectCacheManager);
		}

		ReadableMiddleObjectWrapper inputMessageWrapReadableMiddleObject = null;
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
			
			
			
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			
			try {
				messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			inputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == inputMessageWrapReadableMiddleObject) {
				fail("추출된 입력 메시지 없습니다");
			}
		}

		class AbstractServerTaskMock extends AbstractServerTask {

			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}

		}

		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();
		PersonalLoginManagerIF fromPersonalLoginManager = mock(PersonalLoginManagerIF.class);

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
			serverTaskMock.execute(projectName,
					fromAcceptedConnection,			
					projectLoginManagerMock,						
					inputMessageWrapReadableMiddleObject,
					messageProtocol, fromPersonalLoginManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
		
		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = fromAcceptedConnection.getOutputMessageQueue();
		if (outputMessageQueue.size() != 1) {
			fail("예상 출력 메시지 갯수가 1개가 아님");
		}
		
		ArrayDeque<WrapBuffer> wrapBufferQueue = outputMessageQueue.poll();		
		
		for (WrapBuffer wrapBuffer : wrapBufferQueue) {
			ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
			byteBuffer.position(byteBuffer.remaining());
		}		
		
		SocketOutputStream sos = null;
		try {
			sos = new SocketOutputStream(wrapBufferQueue, streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
		} catch (NoMoreDataPacketBufferException e) {
			fail("fail to create a instance of SocketOutputStream");
		}
				
		ReadableMiddleObjectWrapper outputMessageWrapReadableMiddleObject = null;
		try {
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);			
			outputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == outputMessageWrapReadableMiddleObject) {
				fail("추출된 출력 메시지가 없습니다");
			}
		} catch (Exception e) {
			fail("fail to close");
		}
		
		AbstractMessage receviedOutputMessage = null;
		try {
			receviedOutputMessage = ClientMessageUtility.buildOutputMessage(messageProtocol,
					clientObjectCacheManager, this.getClass().getClassLoader(), outputMessageWrapReadableMiddleObject);
		} catch (DynamicClassCallException | BodyFormatException e) {
			fail("fail to build a output message");
		}
		
		if (! (receviedOutputMessage instanceof SelfExnRes)) {
			fail("입력 메시지 얻기 실험 실패::강제적으로 서버 메시지 코덱 얻기 실패 예외를 던졌지만 그에 대한 처리 결과 메시지인 SelfExnRes 미 발생");
		}
		
		SelfExnRes selfExnRes = (SelfExnRes)receviedOutputMessage;
				
		if (! selfExnRes.getErrorType().equals(ErrorType.BodyFormatException)) {
			fail("입력 메시지 얻기 실험 실패::강제적으로 바디 포맷 예외를 던졌지만 그에 대한 에러 종류가 BodyFormatException 이 아님");
		}
		
		if (selfExnRes.getErrorReason().indexOf("fail to get a input message from readable middle object") < 0) {
			fail("입력 메시지 얻기 실험 실패::강제적으로 바디 포맷 예외를 던졌지만 그 원인이 입력 메시지 얻기 실패가 아님");
		}
		
		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();

		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		fromAcceptedConnection.close();
	}

	@Test
	public void testExecute_failToDecodeBody_통제벗어난에러() {
		String projectName = "sample_test";
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		long socketTimeOut = 5000;
		int serverOutputMessageQueueCapacity = 5; 
		
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
		ProjectLoginManagerIF  projectLoginManagerMock = mock(ProjectLoginManagerIF.class);
		
		AcceptedConnection fromAcceptedConnection = null;
		{
			SocketOutputStream socketOutputStreamOfAcceptedSC = null;
			try {
				socketOutputStreamOfAcceptedSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}			
			SelectionKey personalSelectionKey = mock(SelectionKey.class);			
			ServerIOEvenetControllerIF serverIOEvenetController = mock(ServerIOEvenetControllerIF.class);
			ServerObjectCacheManagerIF serverObjectCacheManager = mock(ServerObjectCacheManagerIF.class);
			
			fromAcceptedConnection = new AcceptedConnection(personalSelectionKey, fromSC, 
					projectName, socketTimeOut, serverOutputMessageQueueCapacity, 
					socketOutputStreamOfAcceptedSC,
					projectLoginManagerMock, messageProtocol,
					dataPacketBufferPool, serverIOEvenetController,
					serverObjectCacheManager);
		}

		ReadableMiddleObjectWrapper inputMessageWrapReadableMiddleObject = null;
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

			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			try {
				messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			inputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == inputMessageWrapReadableMiddleObject) {
				fail("추출된 입력 메시지 없습니다");
			}
		}

		class AbstractServerTaskMock extends AbstractServerTask {
			@Override
			public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
					ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
				fail("이 테스트는 동적 클래스 호출시 에러가 날 경우 예외 처리를 잘하는가에 대한 테스트로 이 메소드는 호출되어서는 안된다");
			}

		}

		AbstractServerTask serverTaskMock = new AbstractServerTaskMock();
		PersonalLoginManagerIF fromPersonalLoginManager = mock(PersonalLoginManagerIF.class);
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
			serverTaskMock.execute(projectName,
					fromAcceptedConnection,			
					projectLoginManagerMock,						
					inputMessageWrapReadableMiddleObject,
					messageProtocol, fromPersonalLoginManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}

		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = fromAcceptedConnection.getOutputMessageQueue();
		if (outputMessageQueue.size() != 1) {
			fail("예상 출력 메시지 갯수가 1개가 아님");
		}
		
		ArrayDeque<WrapBuffer> wrapBufferQueue = outputMessageQueue.poll();		
		
		for (WrapBuffer wrapBuffer : wrapBufferQueue) {
			ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
			byteBuffer.position(byteBuffer.remaining());
		}		
		
		SocketOutputStream sos = null;
		try {
			sos = new SocketOutputStream(wrapBufferQueue, streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
		} catch (NoMoreDataPacketBufferException e) {
			fail("fail to create a instance of SocketOutputStream");
		}
				
		ReadableMiddleObjectWrapper outputMessageWrapReadableMiddleObject = null;
		try {
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);			
			outputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == outputMessageWrapReadableMiddleObject) {
				fail("추출된 출력 메시지가 없습니다");
			}
		} catch (Exception e) {
			fail("fail to close");
		}
		
		AbstractMessage receviedOutputMessage = null;
		try {
			receviedOutputMessage = ClientMessageUtility.buildOutputMessage(messageProtocol,
					clientObjectCacheManager, this.getClass().getClassLoader(), outputMessageWrapReadableMiddleObject);
		} catch (DynamicClassCallException | BodyFormatException e) {
			fail("fail to build a output message");
		}
		
		if (! (receviedOutputMessage instanceof SelfExnRes)) {
			fail("입력 메시지 얻기 실험 실패::강제적으로 서버 메시지 코덱 얻기 실패 예외를 던졌지만 그에 대한 처리 결과 메시지인 SelfExnRes 미 발생");
		}
		
		SelfExnRes selfExnRes = (SelfExnRes)receviedOutputMessage;
				
		if (! selfExnRes.getErrorType().equals(ErrorType.BodyFormatException)) {
			fail("입력 메시지 얻기 실험 실패::강제적으로 바디 포맷 예외를 던졌지만 그에 대한 에러 종류가 BodyFormatException 이 아님");
		}
		
		if (selfExnRes.getErrorReason().indexOf("unknown error::fail to get a input message from readable middle object") < 0) {
			fail("입력 메시지 얻기 실험 실패::강제적으로 바디 포맷 예외를 던졌지만 그 원인이 입력 메시지 얻기 실패가 아님");
		}
		
		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		
		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		fromAcceptedConnection.close();
	}

	@Test
	public void testDoTask_2번이상동기출력메시지추가() {
		String projectName = "sample_test";
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		long socketTimeOut = 5000;
		int serverOutputMessageQueueCapacity = 5; 

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
		ProjectLoginManagerIF  projectLoginManagerMock = mock(ProjectLoginManagerIF.class);

		AcceptedConnection fromAcceptedConnection = null;
		{

			SocketOutputStream socketOutputStreamOfAcceptedSC = null;
			try {
				socketOutputStreamOfAcceptedSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}			
			SelectionKey personalSelectionKey = mock(SelectionKey.class);			
			ServerIOEvenetControllerIF serverIOEvenetController = mock(ServerIOEvenetControllerIF.class);
			ServerObjectCacheManagerIF serverObjectCacheManager = mock(ServerObjectCacheManagerIF.class);
			
			fromAcceptedConnection = new AcceptedConnection(personalSelectionKey, fromSC, 
					projectName, socketTimeOut, serverOutputMessageQueueCapacity, 
					socketOutputStreamOfAcceptedSC,
					projectLoginManagerMock, messageProtocol,
					dataPacketBufferPool, serverIOEvenetController,
					serverObjectCacheManager);
		}

		ReadableMiddleObjectWrapper inputMessageWrapReadableMiddleObject = null;
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

			
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			try {
				messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			inputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == inputMessageWrapReadableMiddleObject) {
				fail("추출된 입력 메시지 없습니다");
			}
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
		PersonalLoginManagerIF fromPersonalLoginManager = mock(PersonalLoginManagerIF.class);
		
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
			serverTaskMock.execute(projectName,
					fromAcceptedConnection,			
					projectLoginManagerMock,						
					inputMessageWrapReadableMiddleObject,
					messageProtocol, fromPersonalLoginManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}
		
		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = fromAcceptedConnection.getOutputMessageQueue();
		if (outputMessageQueue.size() != 2) {
			fail("예상 출력 메시지 갯수가 2개가 아님");
		}
		
		/** 동기 메시지 2개를 넣을려다 실패한것이므로 두번째에 에러 내용이 담겨 있음 */
		outputMessageQueue.poll();
		ArrayDeque<WrapBuffer> wrapBufferQueue = outputMessageQueue.poll();		
		
		for (WrapBuffer wrapBuffer : wrapBufferQueue) {
			ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
			byteBuffer.position(byteBuffer.remaining());
		}		
		
		SocketOutputStream sos = null;
		try {
			sos = new SocketOutputStream(wrapBufferQueue, streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
		} catch (NoMoreDataPacketBufferException e) {
			fail("fail to create a instance of SocketOutputStream");
		}
				
		ReadableMiddleObjectWrapper outputMessageWrapReadableMiddleObject = null;
		try {
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);			
			outputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == outputMessageWrapReadableMiddleObject) {
				fail("추출된 출력 메시지가 없습니다");
			}
		} catch (Exception e) {
			fail("fail to close");
		}
		
		AbstractMessage receviedOutputMessage = null;
		try {
			receviedOutputMessage = ClientMessageUtility.buildOutputMessage(messageProtocol,
					clientObjectCacheManager, this.getClass().getClassLoader(), outputMessageWrapReadableMiddleObject);
		} catch (DynamicClassCallException | BodyFormatException e) {
			fail("fail to build a output message");
		}
		
		if (! (receviedOutputMessage instanceof SelfExnRes)) {
			fail("서버 타스크 실험 실패::2번 이상 동기 출력 메시지를 추가하여  에러를 발생하였지만 그에 대한 처리 결과 메시지인 SelfExnRes 미 발생");
		}
		
		SelfExnRes selfExnRes = (SelfExnRes)receviedOutputMessage;
				
		if (! selfExnRes.getErrorType().equals(ErrorType.ServerTaskException)) {
			fail("서버 타스크 실험 실패::2번 이상 동기 출력 메시지를 추가하여  에러를 발생하였지만 그에 대한 에러 종류가 ServerTaskException 이 아님");
		}
		
		if (selfExnRes.getErrorReason().indexOf("unknown error::fail to execuate the input message's task") < 0) {
			fail("서버 타스크 실험 실패::2번 이상 동기 출력 메시지를 추가하여  에러를 발생하였지만 그 원인이 입력 메시지 타스크 수행 실패가 아님");
		}
		
		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();

		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		fromAcceptedConnection.close();
	}

	@Test
	public void testDoTask_비동기입력메시지에도불구하고동기출력메시지를보내려고함() {
		String projectName = "sample_test";
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		int dataPacketBufferMaxCntPerMessage = 10;
		long socketTimeOut = 5000;
		int serverOutputMessageQueueCapacity = 5; 
		
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
		ProjectLoginManagerIF  projectLoginManagerMock = mock(ProjectLoginManagerIF.class);
		
		

		AcceptedConnection fromAcceptedConnection = null;
		{	

			SocketOutputStream socketOutputStreamOfAcceptedSC = null;
			try {
				socketOutputStreamOfAcceptedSC = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (NoMoreDataPacketBufferException e1) {
				fail("fail to build the instance of SocketOutputStream class becase there is no more buffer in the dataPacketBufferPool");
			}			
			SelectionKey personalSelectionKey = mock(SelectionKey.class);			
			ServerIOEvenetControllerIF serverIOEvenetController = mock(ServerIOEvenetControllerIF.class);			
			ServerObjectCacheManagerIF serverObjectCacheManager = mock(ServerObjectCacheManagerIF.class);
			
			fromAcceptedConnection = new AcceptedConnection(personalSelectionKey, fromSC, 
					projectName, socketTimeOut, serverOutputMessageQueueCapacity, 
					socketOutputStreamOfAcceptedSC,
					projectLoginManagerMock, messageProtocol,
					dataPacketBufferPool, serverIOEvenetController,
					serverObjectCacheManager);
		}

		ReadableMiddleObjectWrapper inputMessageWrapReadableMiddleObject = null;
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

			
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			try {
				messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			inputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == inputMessageWrapReadableMiddleObject) {
				fail("추출된 입력 메시지 없습니다");
			}
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
		PersonalLoginManagerIF fromPersonalLoginManager = mock(PersonalLoginManagerIF.class);
		
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
			serverTaskMock.execute(projectName,
					fromAcceptedConnection,			
					projectLoginManagerMock,						
					inputMessageWrapReadableMiddleObject,
					messageProtocol, fromPersonalLoginManager);
		} catch (InterruptedException e) {
			fail("InterruptedException");
		}

		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = fromAcceptedConnection.getOutputMessageQueue();
		if (outputMessageQueue.size() != 1) {
			fail("예상 출력 메시지 갯수가 1개가 아님");
		}
				
		ArrayDeque<WrapBuffer> wrapBufferQueue = outputMessageQueue.poll();		
		
		for (WrapBuffer wrapBuffer : wrapBufferQueue) {
			ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
			byteBuffer.position(byteBuffer.remaining());
		}		
		
		SocketOutputStream sos = null;
		try {
			sos = new SocketOutputStream(wrapBufferQueue, streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
		} catch (NoMoreDataPacketBufferException e) {
			fail("fail to create a instance of SocketOutputStream");
		}
				
		ReadableMiddleObjectWrapper outputMessageWrapReadableMiddleObject = null;
		try {
			ReceivedMessageBlockingQueueMock receivedMessageBlockingQueueMock = new ReceivedMessageBlockingQueueMock();
			messageProtocol.S2MList(sos, receivedMessageBlockingQueueMock);			
			outputMessageWrapReadableMiddleObject = receivedMessageBlockingQueueMock.getReadableMiddleObjectWrapper();
			if (null == outputMessageWrapReadableMiddleObject) {
				fail("추출된 출력 메시지가 없습니다");
			}
		} catch (Exception e) {
			fail("fail to close");
		}
		
		AbstractMessage receviedOutputMessage = null;
		try {
			receviedOutputMessage = ClientMessageUtility.buildOutputMessage(messageProtocol,
					clientObjectCacheManager, this.getClass().getClassLoader(), outputMessageWrapReadableMiddleObject);
		} catch (DynamicClassCallException | BodyFormatException e) {
			fail("fail to build a output message");
		}
		
		if (! (receviedOutputMessage instanceof SelfExnRes)) {
			fail("서버 타스크 실험 실패::2번 이상 동기 출력 메시지를 추가하여  에러를 발생하였지만 그에 대한 처리 결과 메시지인 SelfExnRes 미 발생");
		}
		
		SelfExnRes selfExnRes = (SelfExnRes)receviedOutputMessage;
				
		if (! selfExnRes.getErrorType().equals(ErrorType.ServerTaskException)) {
			fail("서버 타스크 실험 실패::2번 이상 동기 출력 메시지를 추가하여  에러를 발생하였지만 그에 대한 에러 종류가 ServerTaskException 이 아님");
		}
		
		if (selfExnRes.getErrorReason().indexOf("unknown error::fail to execuate the input message's task") < 0) {
			fail("서버 타스크 실험 실패::2번 이상 동기 출력 메시지를 추가하여  에러를 발생하였지만 그 원인이 입력 메시지 타스크 수행 실패가 아님");
		}
		
		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		
		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		fromAcceptedConnection.close();
	}
}
