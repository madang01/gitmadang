package kr.pe.codda.common.protocol.dhb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CharsetUtil;
import kr.pe.codda.common.io.DataPacketBufferPool;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.SimpleReceivedMessageBlockingQueue;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;
import kr.pe.codda.common.protocol.thb.THBSingleItemDecoder;
import kr.pe.codda.common.protocol.thb.THBSingleItemDecoderMatcher;
import kr.pe.codda.common.protocol.thb.THBSingleItemDecoderMatcherIF;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnRes;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnResDecoder;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnResEncoder;

public class DHBMessageProtocolTest extends AbstractJunitTest {	
	
	
	@Test
	public void testM2S_basic() {
		int dataPacketBufferMaxCntPerMessage = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
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
		
		SocketChannel fromSC = null;
		try {
			fromSC = SocketChannel.open();
		} catch (IOException e) {
			log.warn(""+e.getMessage(), e);
			fail("fail to open a new socket channel");
		}
		
		DHBMessageProtocol dhbMessageProtocol = 
				new DHBMessageProtocol( 
						dataPacketBufferMaxCntPerMessage,
						streamCharsetEncoder,
						streamCharsetDecoder,
						dataPacketBufferPoolManager);
		
		SelfExnResEncoder selfExnEncoder = new SelfExnResEncoder();
		SelfExnResDecoder selfExnDecoder = new SelfExnResDecoder();
		
		THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = new THBSingleItemDecoderMatcher(streamCharsetDecoder);
		THBSingleItemDecoder thbSingleItemDecoder = new THBSingleItemDecoder(thbSingleItemDecoderMatcher);

		// log.info("1");		
		long beforeTime = 0;
		long afterTime = 0;
		
		
		int retryCount = 1;
		
		int firstIndex = -1;
		int differentCount = 0;
		
		StringBuilder testStringBuilder = new StringBuilder();
		
		for (int i=0; i < 2500; i++) {
			testStringBuilder.append("한글");
		}
		
		SelfExnRes selfExnReq = new SelfExnRes();
		selfExnReq.setErrorPlace(SelfExn.ErrorPlace.SERVER);
		selfExnReq.setErrorType(SelfExn.ErrorType.BodyFormatException);
		selfExnReq.setErrorMessageID("Echo");
		selfExnReq.setErrorReason(testStringBuilder.toString());	
		
		selfExnReq.messageHeaderInfo.mailboxID = 1;
		selfExnReq.messageHeaderInfo.mailID = 3;
		
		beforeTime= new Date().getTime();
		ArrayBlockingQueue<ReadableMiddleObjectWrapper> readableMiddleObjectWrapperQueue = new ArrayBlockingQueue<ReadableMiddleObjectWrapper>(10);
		SimpleReceivedMessageBlockingQueue simpleWrapMessageBlockingQueue = new SimpleReceivedMessageBlockingQueue(readableMiddleObjectWrapperQueue);
		
		for (int i=0; i < retryCount; i++) {			
			long beforeLocalTime= new Date().getTime();			
			
			ArrayDeque<WrapBuffer> wrapBufferListOfInputMessage = null;
			try {
				wrapBufferListOfInputMessage = dhbMessageProtocol.M2S(selfExnReq, selfExnEncoder);
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
			
			
			try {
				dhbMessageProtocol.S2MList(fromSC, sos, simpleWrapMessageBlockingQueue);
			} catch (Exception e) {
				String errorMessage = "error::"+e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			//log.info("5");
			
			while (! readableMiddleObjectWrapperQueue.isEmpty()) {
				ReadableMiddleObjectWrapper readableMiddleObjectWrapper = readableMiddleObjectWrapperQueue.poll();
				
				Object readablemiddleObj = readableMiddleObjectWrapper.getReadableMiddleObject();				
				
				try {
					AbstractMessage resObj = selfExnDecoder.decode(thbSingleItemDecoder, readablemiddleObj);
					resObj.messageHeaderInfo.mailboxID = readableMiddleObjectWrapper.getMailboxID();
					resObj.messageHeaderInfo.mailID = readableMiddleObjectWrapper.getMailID();
					
					/*if (! (resObj instanceof SelfExn)) {
						fail("resObj is not a instance of SelfExn class");
					}*/
					
					SelfExnRes selfExnRes = (SelfExnRes)resObj;
					
					assertEquals("SelfExn 입력과 출력 메시지 비교", selfExnReq.toString(), selfExnRes.toString());
				} catch (Exception e) {
					String errorMessage = "error::"+e.getMessage();
					log.warn(errorMessage, e);
					fail(errorMessage);
				}
			}
			
			long afterLocalTime= new Date().getTime();
			if ((-1 == firstIndex) && (afterLocalTime == beforeLocalTime)) {
				firstIndex = i;
			}
			
			if (afterLocalTime != beforeLocalTime) {
				// log.info("case[{}]::afterLocalTime != beforeLocalTime", i);
				differentCount++;
			}
		}
		
		afterTime= new Date().getTime();
		
		log.info("{} 번 시간차={} ms, 평균={} ms, firstIndex={}, differentCount={}", retryCount, (afterTime-beforeTime), (double)(afterTime-beforeTime)/retryCount, firstIndex, differentCount);
	}
}

