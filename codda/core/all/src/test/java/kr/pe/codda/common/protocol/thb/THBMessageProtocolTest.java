package kr.pe.codda.common.protocol.thb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayDeque;
import java.util.Date;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.client.connection.sync.SyncOutputMessageReceiver;
import kr.pe.codda.common.etc.CharsetUtil;
import kr.pe.codda.common.io.DataPacketBufferPool;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.ReceivedDataStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnRes;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnResEncoder;

public class THBMessageProtocolTest extends AbstractJunitTest {
	
	
	@Test
	public void testM2S_basic() {
		int dataPacketBufferMaxCntPerMessage = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
		DataPacketBufferPoolIF dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 4096;
		int dataPacketBufferPoolSize = 100;
		
		
		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		}
		
		THBMessageProtocol thbMessageProtocol = 
				new THBMessageProtocol(dataPacketBufferMaxCntPerMessage,
						streamCharsetEncoder,
						streamCharsetDecoder,
						dataPacketBufferPool);
		
		SelfExnResEncoder selfExnEncoder = new SelfExnResEncoder();
		
		// log.info("1");		
		long beforeTime = 0;
		long afterTime = 0;
		
		
		int retryCount = 1000000;
		
		int firstIndex = -1;
		int differentCount = 0;
		
		SyncOutputMessageReceiver syncOutputMessageReceiver = new SyncOutputMessageReceiver(thbMessageProtocol);
		
		
		StringBuilder testStringBuilder = new StringBuilder();
		
		for (int i=0; i < 2500; i++) {
			testStringBuilder.append("한글");
		}
		
		SelfExnRes expectedSelfExnRes = new SelfExnRes();
		expectedSelfExnRes.setErrorPlace(SelfExn.ErrorPlace.SERVER);
		expectedSelfExnRes.setErrorType(SelfExn.ErrorType.BodyFormatException);
		expectedSelfExnRes.setErrorMessageID("Echo");
		expectedSelfExnRes.setErrorReason(testStringBuilder.toString());
		
		expectedSelfExnRes.messageHeaderInfo.mailboxID = 1;
		expectedSelfExnRes.messageHeaderInfo.mailID = 3;
		
		beforeTime= System.currentTimeMillis();
		
		for (int i=0; i < retryCount; i++) {			
			long beforeLocalTime= new Date().getTime();			
			
			ArrayDeque<WrapBuffer> wrapBufferListOfInputMessage = null;
			try {
				wrapBufferListOfInputMessage = thbMessageProtocol.M2S(expectedSelfExnRes, selfExnEncoder);
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
			
			ReceivedDataStream rds = null;
			try {
				rds = new ReceivedDataStream(wrapBufferListOfInputMessage, streamCharsetDecoder, dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
			} catch (Exception e) {
				String errorMessage = "error::"+e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			syncOutputMessageReceiver.ready(ClientMessageCodecManger.getInstance());
			
			try {
				thbMessageProtocol.S2MList(rds, syncOutputMessageReceiver);
			} catch (Exception e) {
				String errorMessage = "error::"+e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			if (syncOutputMessageReceiver.isError()) {
				fail("1 개 이상 출력 메시지 추출되었습니다");
			}
			
			//log.info("5");
			if (! syncOutputMessageReceiver.isReceivedMessage()) {
				fail("추출한 출력 메시지가 없습니다");
			}				
				
			try {
				AbstractMessage resObj = syncOutputMessageReceiver.getReceiveMessage();
				
				if (! (resObj instanceof SelfExnRes)) {
					fail("resObj is not a instance of SelfExnRes class");
				}
				
				SelfExnRes acutalSelfExnRes = (SelfExnRes)resObj;
				
				assertEquals("SelfExn 입력과 출력 메시지 비교", expectedSelfExnRes.toString(), acutalSelfExnRes.toString());
			} catch (Exception e) {
				String errorMessage = "error::"+e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
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
		
		afterTime= System.currentTimeMillis();
		
		log.info("{} 번 시간차={} ms, 평균={} ms, firstIndex={}, differentCount={}", retryCount, (afterTime-beforeTime), (double)(afterTime-beforeTime)/retryCount, firstIndex, differentCount);
	}
}
