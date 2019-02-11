package kr.pe.codda.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayDeque;

import org.junit.Test;

import junitlib.AbstractJunitTest;

public class ReceivedDataOnlyStreamTest extends AbstractJunitTest {
	
	
	
	@Test
	public void testCutMessageInputStreamFromStartingPosition_basic() {
		int dataPacketBufferMaxCount = 15;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 15;
		
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		try {
			dataPacketBufferPoolManager = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		}
		
		
		ReceivedDataOnlyStream sos = null;
		FreeSizeOutputStream fsos = null;
		try {
			{
				long expectedSize = dataPacketBufferSize*3+42;
				{
					
					ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = null;
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);
					
					fsos.skip(expectedSize);
					emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
					sos = new ReceivedDataOnlyStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPoolManager);
				}
				
				long actualSize = sos.getSreamSizeUsingStreamWrapBufferQueue();
				
				assertEquals(expectedSize, actualSize);
			}			
			
			FreeSizeInputStream fsis = null;
			try {
				long oldSize = sos.getSreamSizeUsingStreamWrapBufferQueue();
				
				long expectedSize = oldSize - dataPacketBufferSize - 24;
				fsis = sos.cutMessageInputStreamFromStartingPosition(expectedSize);
				
				//log.info("fsis size={}", fsis.available());
				// log.info("sos size={}", sos.size());
				
				long actualSize = sos.getSreamSizeUsingStreamWrapBufferQueue();
				
				assertEquals(oldSize - expectedSize, actualSize);
			} finally {
				if (null != fsis) {
					fsis.close();
				}				
			}			
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::"+e.getMessage());
		} finally {
			if (null != sos) {
				sos.close();
			}
		}
	}
	
	@Test
	public void testCutMessageInputStreamFromStartingPosition_complex() {
		int dataPacketBufferMaxCount = 15;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 6;
		int dataPacketBufferPoolSize = 15;
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		try {
			dataPacketBufferPoolManager = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		}
		
		final int integerTypeExpectedValueList[] = { 
				0x11223344, 
				0x55667788,
				0x99aabbcc,
				0xddeeff00
			};	
		
		final byte eofByte = (byte) 0xc1;
		 
		
		ReceivedDataOnlyStream sos = null;
		
		FreeSizeOutputStream fsos = null;
		ArrayDeque<WrapBuffer> outputStreamWrapBufferListForTest = null;
		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);
			
			for (int expectedValue : integerTypeExpectedValueList) {
				fsos.putInt(expectedValue);
			}

			
			fsos.putByte(eofByte);
			
			outputStreamWrapBufferListForTest = fsos.getOutputStreamWrapBufferList();
			
			sos = 
					new ReceivedDataOnlyStream(outputStreamWrapBufferListForTest, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPoolManager);
			
			// log.info("sos.size={}", sos.size());
			FreeSizeInputStream fsis = null;
			for (int i=0; i < integerTypeExpectedValueList.length; i++) {
				int expectedValue = integerTypeExpectedValueList[i];
				try {
					fsis = sos.cutMessageInputStreamFromStartingPosition(4);
					int actualValue = fsis.getInt();
					
					assertEquals(String.format("integerTypeExpectedValueList's index[%d]'s value[%d] vs  actualValue[%d] 비교", i, expectedValue, actualValue), expectedValue, actualValue);				
				} finally {
					if (null != fsis) {
						fsis.close();
					}				
				}
			}
			
			if (1 != sos.getStreamWrapBufferQueue().size()) {
				fail(String.format("StreamWrapBufferQueue size[%d] is not equal to one", 
						sos.getStreamWrapBufferQueue().size()));
			}
			
			try {
				fsis = sos.cutMessageInputStreamFromStartingPosition(1);
				byte actualValue = fsis.getByte();
				
				assertEquals(eofByte, actualValue);				
			} finally {
				if (null != fsis) {
					fsis.close();
				}				
			}
			
			if (0L != sos.getStreamSize()) {
				fail("1. the number of socket read bytes is not zero");
			}
			
			if (0L != sos.getSreamSizeUsingStreamWrapBufferQueue()) {
				fail("2. the number of socket read bytes is not zero");
			}
			
			if (0 != sos.getStreamWrapBufferQueue().size()) {
				fail(String.format("StreamWrapBufferQueue size[%d] is not equal to zero", 
						sos.getStreamWrapBufferQueue().size()));
			}
			
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != sos) {
				sos.close();
			}
		}
	}
}
