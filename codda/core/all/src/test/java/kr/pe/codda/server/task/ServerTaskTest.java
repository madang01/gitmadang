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
import org.mockito.Mockito;

import junitlib.AbstractJunitTest;
import kr.pe.codda.client.connection.ClientMessageUtility;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
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
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;
import kr.pe.codda.common.protocol.ReceivedMessageBlockingQueueIF;
import kr.pe.codda.common.protocol.thb.THBMessageProtocol;
import kr.pe.codda.common.protocol.thb.THBSingleItemDecoder;
import kr.pe.codda.common.protocol.thb.THBSingleItemDecoderMatcher;
import kr.pe.codda.common.protocol.thb.THBSingleItemDecoderMatcherIF;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.common.type.SelfExn.ErrorType;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.Empty.Empty;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnRes;
import kr.pe.codda.impl.task.server.EmptyServerTask;
import kr.pe.codda.server.AcceptedConnection;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.ProjectLoginManagerIF;
import kr.pe.codda.server.ServerIOEvenetControllerIF;
import kr.pe.codda.server.classloader.ServerTaskMangerIF;

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
	public void testConstructor_DynamicClassCallException예외던지기() {
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
			
			ServerTaskMangerIF serverTaskMangerMock = mock(ServerTaskMangerIF.class);
			try {
				Mockito.when(serverTaskMangerMock.getServerTask(Mockito.anyString()))
				.thenThrow(new DynamicClassCallException("모의로 DynamicClassCallException 예외 던지기"));			
			} catch (DynamicClassCallException e) {
				fail("모키토 설정 실패, errmsg=" + e.getMessage());
			}
			
			fromAcceptedConnection = new AcceptedConnection(personalSelectionKey, fromSC, 
					projectName, socketTimeOut, serverOutputMessageQueueCapacity, 
					socketOutputStreamOfAcceptedSC,
					projectLoginManagerMock, messageProtocol,
					dataPacketBufferPool, serverIOEvenetController,
					serverTaskMangerMock);
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
				
		try {
			fromAcceptedConnection.putReceivedMessage(inputMessageWrapReadableMiddleObject);
		} catch (InterruptedException e) {
			fail("인터럽트 발생");
		}
		
		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = fromAcceptedConnection.getOutputMessageQueue();
		
		if (1 != outputMessageQueue.size()) {
			fail("메시지 갯수가 1개가 아님");
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
			receviedOutputMessage = ClientMessageUtility.buildOutputMessage(ClientMessageCodecManger.getInstance(),
					messageProtocol,
					outputMessageWrapReadableMiddleObject);
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
		
		if (selfExnRes.getErrorReason().indexOf("모의로 DynamicClassCallException 예외 던지기") < 0) {
			fail("서버 메시지 코덱 얻기 실패 실험 실패::강제적으로 서버 메시지 코덱 얻기 실패 예외를 던졌지만 그 원인이 입력 메시지 코덱 얻기 실패가 아님");
		}
		
		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();

		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		// fromAcceptedConnection.close();
		fromAcceptedConnection.close();
		
	}
	
	@Test
	public void testConstructor_NullPointerException예외던지기() {
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
			
			ServerTaskMangerIF serverTaskMangerMock = mock(ServerTaskMangerIF.class);
			try {
				Mockito.when(serverTaskMangerMock.getServerTask(Mockito.anyString()))
				.thenThrow(new NullPointerException("모의로 NullPointerException 예외 던지기"));			
			} catch (DynamicClassCallException e) {
				fail("모키토 설정 실패, errmsg=" + e.getMessage());
			}
			
			fromAcceptedConnection = new AcceptedConnection(personalSelectionKey, fromSC, 
					projectName, socketTimeOut, serverOutputMessageQueueCapacity, 
					socketOutputStreamOfAcceptedSC,
					projectLoginManagerMock, messageProtocol,
					dataPacketBufferPool, serverIOEvenetController,
					serverTaskMangerMock);
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
				
		try {
			fromAcceptedConnection.putReceivedMessage(inputMessageWrapReadableMiddleObject);
		} catch (InterruptedException e) {
			fail("인터럽트 발생");
		}
		
		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = fromAcceptedConnection.getOutputMessageQueue();
		
		if (1 != outputMessageQueue.size()) {
			fail("메시지 갯수가 1개가 아님");
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
			receviedOutputMessage = ClientMessageUtility.buildOutputMessage(ClientMessageCodecManger.getInstance(),
					messageProtocol,
					outputMessageWrapReadableMiddleObject);
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
		
		if (selfExnRes.getErrorReason().indexOf("fail to get a input message server task") < 0) {
			fail("서버 메시지 코덱 얻기 실패 실험 실패::강제적으로 서버 메시지 코덱 얻기 실패 예외를 던졌지만 그 원인이 입력 메시지 코덱 얻기 실패가 아님");
		}
		
		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();

		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		// fromAcceptedConnection.close();
		fromAcceptedConnection.close();
	}

	@Test
	public void testExecute_출력메시지인코더얻기_통제된에러() {
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
			
			class EmptyServerTask extends AbstractServerTask {

				public EmptyServerTask() throws DynamicClassCallException {
					super();
				}

				@Override
				public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
						ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
					toLetterCarrier.addBypassOutputMessage(inputMessage);
				}
				
				public AbstractMessageEncoder getMessageEncoder(String messageID) throws DynamicClassCallException {
					throw new DynamicClassCallException("모의 DynamicClassCallException 예외 던지기");
				}
			}
			
			
			ServerTaskMangerIF serverTaskMangerMock = mock(ServerTaskMangerIF.class);
			try {
				Mockito.when(serverTaskMangerMock.getServerTask(Mockito.anyString()))
				.thenReturn(new EmptyServerTask());			
			} catch (DynamicClassCallException e) {
				fail("모키토 설정 실패, errmsg=" + e.getMessage());
			}
			
			fromAcceptedConnection = new AcceptedConnection(personalSelectionKey, fromSC, 
					projectName, socketTimeOut, serverOutputMessageQueueCapacity, 
					socketOutputStreamOfAcceptedSC,
					projectLoginManagerMock, messageProtocol,
					dataPacketBufferPool, serverIOEvenetController,
					serverTaskMangerMock);
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
				
		try {
			fromAcceptedConnection.putReceivedMessage(inputMessageWrapReadableMiddleObject);
		} catch (InterruptedException e) {
			fail("인터럽트 발생");
		}
		
		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = fromAcceptedConnection.getOutputMessageQueue();
		
		if (1 != outputMessageQueue.size()) {
			fail("메시지 갯수가 1개가 아님");
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
			receviedOutputMessage = ClientMessageUtility.buildOutputMessage(ClientMessageCodecManger.getInstance(),
					messageProtocol,
					outputMessageWrapReadableMiddleObject);
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
		
		if (selfExnRes.getErrorReason().indexOf("모의 DynamicClassCallException 예외 던지기") < 0) {
			fail("서버 메시지 코덱 얻기 실패 실험 실패::강제적으로 서버 메시지 코덱 얻기 실패 예외를 던졌지만 그 원인이 입력 메시지 코덱 얻기 실패가 아님");
		}
		
		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();

		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		// fromAcceptedConnection.close();
		fromAcceptedConnection.close();
	}
	
	@Test
	public void testExecute_출력메시지인코더얻기_통제벗어난에러() {
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
			
			class EmptyServerTask extends AbstractServerTask {

				public EmptyServerTask() throws DynamicClassCallException {
					super();
				}

				@Override
				public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager,
						ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws Exception {
					toLetterCarrier.addBypassOutputMessage(inputMessage);
				}
				
				public AbstractMessageEncoder getMessageEncoder(String messageID) throws DynamicClassCallException {
					throw new NullPointerException("모의 NullPointerException 예외 던지기");
				}
			}
			
			
			ServerTaskMangerIF serverTaskMangerMock = mock(ServerTaskMangerIF.class);
			try {
				Mockito.when(serverTaskMangerMock.getServerTask(Mockito.anyString()))
				.thenReturn(new EmptyServerTask());			
			} catch (DynamicClassCallException e) {
				fail("모키토 설정 실패, errmsg=" + e.getMessage());
			}
			
			fromAcceptedConnection = new AcceptedConnection(personalSelectionKey, fromSC, 
					projectName, socketTimeOut, serverOutputMessageQueueCapacity, 
					socketOutputStreamOfAcceptedSC,
					projectLoginManagerMock, messageProtocol,
					dataPacketBufferPool, serverIOEvenetController,
					serverTaskMangerMock);
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
				
		try {
			fromAcceptedConnection.putReceivedMessage(inputMessageWrapReadableMiddleObject);
		} catch (InterruptedException e) {
			fail("인터럽트 발생");
		}
		
		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = fromAcceptedConnection.getOutputMessageQueue();
		
		if (1 != outputMessageQueue.size()) {
			fail("메시지 갯수가 1개가 아님");
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
			receviedOutputMessage = ClientMessageUtility.buildOutputMessage(ClientMessageCodecManger.getInstance(),
					messageProtocol,
					outputMessageWrapReadableMiddleObject);
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
		
		if (selfExnRes.getErrorReason().indexOf("모의 NullPointerException 예외 던지기") < 0) {
			fail("서버 메시지 코덱 얻기 실패 실험 실패::강제적으로 서버 메시지 코덱 얻기 실패 예외를 던졌지만 그 원인이 입력 메시지 코덱 얻기 실패가 아님");
		}
		
		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();

		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		fromAcceptedConnection.close();
	}
	
	
	@Test
	public void testExecute_출력메시지인코딩_바디포맷에러() {
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
			
			
			ServerTaskMangerIF serverTaskMangerMock = mock(ServerTaskMangerIF.class);
			try {
				Mockito.when(serverTaskMangerMock.getServerTask(Mockito.anyString()))
				.thenReturn(new EmptyServerTask());			
			} catch (DynamicClassCallException e) {
				fail("모키토 설정 실패, errmsg=" + e.getMessage());
			}
			// AbstractMessageEncoder
			MessageProtocolIF messageProtocolMock = mock(MessageProtocolIF.class);
			try {
				Mockito.when(messageProtocolMock.M2S(Mockito.any(Empty.class), Mockito.any(AbstractMessageEncoder.class)))
				.thenThrow(new BodyFormatException("모의 BodyFormatException 예외 던지기"));
			} catch (Exception e) {
				fail("dead code");
			}
			
			Empty emptyRes = new Empty();
			emptyRes.messageHeaderInfo.mailboxID = 1;
			emptyRes.messageHeaderInfo.mailID = 3;
			
			SelfExnRes selfExnRes = new SelfExnRes();
			selfExnRes.messageHeaderInfo.mailboxID = 1;
			selfExnRes.messageHeaderInfo.mailID = 1;
			selfExnRes.setErrorPlace(SelfExn.ErrorPlace.SERVER);
			selfExnRes.setErrorType(SelfExn.ErrorType.valueOf(BodyFormatException.class));
		
			selfExnRes.setErrorMessageID(emptyRes.getMessageID());
			selfExnRes.setErrorReason("fail to build a output message stream");			
			
			ArrayDeque<WrapBuffer> wrapBufferListOfSelfExn = null;			
			try {
				wrapBufferListOfSelfExn = messageProtocol.M2S(selfExnRes, CommonStaticFinalVars.SELFEXN_ENCODER);
			} catch(Exception e) {
				fail("dead code");
			}
			
			try {
				Mockito.when(messageProtocolMock.M2S(Mockito.any(SelfExnRes.class), Mockito.any(AbstractMessageEncoder.class)))
				.thenReturn(wrapBufferListOfSelfExn);
			} catch (Exception e) {
				fail("dead code");
			}
			
			THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = new THBSingleItemDecoderMatcher(streamCharsetDecoder);
			Mockito.when(messageProtocolMock.getSingleItemDecoder()).thenReturn(new THBSingleItemDecoder(thbSingleItemDecoderMatcher));
			
			fromAcceptedConnection = new AcceptedConnection(personalSelectionKey, fromSC, 
					projectName, socketTimeOut, serverOutputMessageQueueCapacity, 
					socketOutputStreamOfAcceptedSC,
					projectLoginManagerMock, messageProtocolMock,
					dataPacketBufferPool, serverIOEvenetController,
					serverTaskMangerMock);
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
				
		try {
			fromAcceptedConnection.putReceivedMessage(inputMessageWrapReadableMiddleObject);
		} catch (InterruptedException e) {
			fail("인터럽트 발생");
		}
		
		ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = fromAcceptedConnection.getOutputMessageQueue();
		
		if (1 != outputMessageQueue.size()) {
			fail("메시지 갯수가 1개가 아님");
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
			receviedOutputMessage = ClientMessageUtility.buildOutputMessage(ClientMessageCodecManger.getInstance(),
					messageProtocol,
					outputMessageWrapReadableMiddleObject);
		} catch (DynamicClassCallException | BodyFormatException e) {
			fail("fail to build a output message");
		}
		
		if (! (receviedOutputMessage instanceof SelfExnRes)) {
			fail("서버 메시지 코덱 얻기 실패 실험 실패::강제적으로 서버 메시지 코덱 얻기 실패 예외를 던졌지만 그에 대한 처리 결과 메시지인 SelfExnRes 미 발생");
		}
		
		SelfExnRes selfExnRes = (SelfExnRes)receviedOutputMessage;	
		
		if (! selfExnRes.getErrorType().equals(ErrorType.BodyFormatException)) {
			fail("서버 메시지 코덱 얻기 실패 실험 실패::강제적으로 서버 메시지 코덱 얻기 실패 예외를 던졌지만 그에 대한 에러 종류가 DynamicClassCallException 가 아님");
		}
		
		if (selfExnRes.getErrorReason().indexOf("fail to build a output message stream") < 0) {
			fail("서버 메시지 코덱 얻기 실패 실험 실패::강제적으로 서버 메시지 코덱 얻기 실패 예외를 던졌지만 그 원인이 입력 메시지 코덱 얻기 실패가 아님");
		}
		
		outputMessageWrapReadableMiddleObject.closeReadableMiddleObject();

		inputMessageWrapReadableMiddleObject.closeReadableMiddleObject();
		fromAcceptedConnection.close();
	}
}
