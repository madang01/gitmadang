package kr.pe.sinnori.common.protocol.thb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.io.DataPacketBufferPool;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.impl.message.SelfExn.SelfExnDecoder;
import kr.pe.sinnori.impl.message.SelfExn.SelfExnEncoder;

public class THBMessageProtocolTest extends AbstractJunitTest {
	
	
	@Test
	public void testM2S_basic() {
		int messageIDFixedSize = 25;
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
			dataPacketBufferPoolManager = DataPacketBufferPool.Builder
					.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		}
		
		THBMessageProtocol thbMessageProtocol = 
				new THBMessageProtocol(messageIDFixedSize, 
						dataPacketBufferMaxCntPerMessage,
						streamCharsetEncoder,
						streamCharsetDecoder,
						dataPacketBufferPoolManager);
		
		SelfExnEncoder selfExnEncoder = new SelfExnEncoder();
		SelfExnDecoder selfExnDecoder = new SelfExnDecoder();
		THBSingleItemDecoder dhbSingleItemDecoder = new THBSingleItemDecoder(streamCharsetDecoder);
		
		
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
		
		SelfExn selfExnReq = new SelfExn();
		selfExnReq.setErrorPlace("sever");
		selfExnReq.setErrorGubun("B");
		selfExnReq.setErrorMessageID("Echo");
		selfExnReq.setErrorMessage(testStringBuilder.toString());
		
		selfExnReq.messageHeaderInfo.mailboxID = 1;
		selfExnReq.messageHeaderInfo.mailID = 3;
		
		beforeTime= new Date().getTime();
		
		for (int i=0; i < retryCount; i++) {			
			long beforeLocalTime= new Date().getTime();			
			
			List<WrapBuffer> wrapBufferListOfInputMessage = null;
			try {
				wrapBufferListOfInputMessage = thbMessageProtocol.M2S(selfExnReq, selfExnEncoder);
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
				wrapReadableMiddleObjectList = thbMessageProtocol.S2MList(sos);
			} catch (Exception e) {
				String errorMessage = "error::"+e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			//log.info("5");
			
			for (WrapReadableMiddleObject wrapReadableMiddleObject :  wrapReadableMiddleObjectList) {
				Object readablemiddleObj = wrapReadableMiddleObject.getReadableMiddleObject();				
				
				try {
					AbstractMessage resObj = selfExnDecoder.decode(dhbSingleItemDecoder, readablemiddleObj);
					resObj.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
					resObj.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
					
					/*if (! (resObj instanceof SelfExn)) {
						fail("resObj is not a instance of SelfExn class");
					}*/
					
					SelfExn selfExnRes = (SelfExn)resObj;
					
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
