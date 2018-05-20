package kr.pe.codda.common.protocol.thb;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.io.BinaryInputStreamIF;
import kr.pe.codda.common.io.FixedSizeInputStream;
import kr.pe.codda.common.type.SingleItemType;

public class THBSingleItemDecoderMatcherTest extends AbstractJunitTest {
	@Test
	public void test_잘못된크기기정() {
		Charset streamCharset = Charset.forName("utf8");
		// CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = 
				new THBSingleItemDecoderMatcher(streamCharsetDecoder);
		
		SingleItemType[] testSingleItemTypes = {
				SingleItemType.FIXED_LENGTH_STRING,
				SingleItemType.FIXED_LENGTH_BYTES
		};
		
		
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		String itemName = "문자열 변수";
		@SuppressWarnings("unused")
		Object naitveItemValue = null;
		int itemSize = -1;
		String nativeItemCharset = null;
		
		BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
		
		for (SingleItemType testSingleItemType : testSingleItemTypes) {
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			try {
				naitveItemValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch(IllegalArgumentException e) {				
				String errorMessage = e.getMessage();
				if (errorMessage.lastIndexOf("is less than zero") < 0) {
					log.warn("unknown error", e);
					fail("unknown error");
				}
			} catch (Exception e) {
				log.warn("unkwnon error", e);
				fail("unknwon error");
			}
		}
	}
	
	@Test
	public void test_잘못된문자셋() {
		Charset streamCharset = Charset.forName("utf8");
		// CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = 
				new THBSingleItemDecoderMatcher(streamCharsetDecoder);
		
		SingleItemType[] testSingleItemTypes = {
				SingleItemType.UB_PASCAL_STRING,
				SingleItemType.US_PASCAL_STRING,
				SingleItemType.SI_PASCAL_STRING,
				SingleItemType.FIXED_LENGTH_STRING
		};
		
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		String itemName = "문자열 변수";
		@SuppressWarnings("unused")
		Object naitveItemValue = null;
		int itemSize = 20;
		String nativeItemCharset = "utf883";
		
		BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
		
		for (SingleItemType testSingleItemType : testSingleItemTypes) {
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			try {
				naitveItemValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch(IllegalArgumentException e) {				
				String errorMessage = e.getMessage();
				if (errorMessage.lastIndexOf("is a bad charset name") < 0) {
					log.warn("unknown error", e);
					fail("unknown error");
				}
			} catch (Exception e) {
				log.warn("unkwnon error", e);
				fail("unknwon error");
			}
		}
	}

}
