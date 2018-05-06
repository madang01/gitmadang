package kr.pe.codda.common.protocol.thb;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.io.BinaryInputStreamIF;
import kr.pe.codda.common.io.FixedSizeInputStream;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;
import kr.pe.codda.common.type.SingleItemType;

public class THBSingleItemDecoderTest extends AbstractJunitTest {
	
	@Test
	public void test_잘못된문자셋() {
		Charset streamCharset = Charset.forName("utf8");
		// CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = 
				new THBSingleItemDecoderMatcher(streamCharsetDecoder);
		
		SingleItemDecoderIF thbSingleItemDecoder = new THBSingleItemDecoder(thbSingleItemDecoderMatcher);
		
		String path = "Empty";
		String itemName = "strVar1";
		SingleItemType singleItemType = SingleItemType.UB_PASCAL_STRING;
		@SuppressWarnings("unused")
		Object nativeItemValue = null;
		int itemSize = -1;
		String nativeItemCharset = "utf888";
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
		
		try {
			nativeItemValue = thbSingleItemDecoder.getValueFromReadableMiddleObject(path, itemName, singleItemType, itemSize, nativeItemCharset, binaryInputStream);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.lastIndexOf("is a bad charset name") < 0) {
				log.warn("unknown error", e);
				fail("unknow error");
			}
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("unknown error");
		}
		
		// String itemValue = (String)nativeItemValue;
	}
	
	@Test
	public void test_잘못된크기지정() {
		Charset streamCharset = Charset.forName("utf8");
		// CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = 
				new THBSingleItemDecoderMatcher(streamCharsetDecoder);
		
		SingleItemDecoderIF thbSingleItemDecoder = new THBSingleItemDecoder(thbSingleItemDecoderMatcher);
		
		String path = "Empty";
		String itemName = "strVar1";
		SingleItemType singleItemType = SingleItemType.FIXED_LENGTH_STRING;
		@SuppressWarnings("unused")
		Object nativeItemValue = null;
		int itemSize = -10;
		String nativeItemCharset = null;
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
		
		try {
			nativeItemValue = thbSingleItemDecoder.getValueFromReadableMiddleObject(path, itemName, singleItemType, itemSize, nativeItemCharset, binaryInputStream);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.lastIndexOf("is less than zero") < 0) {
				log.warn("unknown error", e);
				fail("unknow error");
			}
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("unknown error");
		}
		
		// String itemValue = (String)nativeItemValue;
	}
	
	@Test
	public void test_WhenBodyFormatExceptionWhyNullPointerException() {
		Charset streamCharset = Charset.forName("utf8");
		// CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		
		class THBSingleItemDecoderMatcherMock implements  THBSingleItemDecoderMatcherIF {
			
			class THBSingleItemDecoderMock extends AbstractTHBSingleItemDecoder {

				@Override
				public Object getValue(int itemTypeID, String itemName, int itemSize, String nativeItemCharset,
						BinaryInputStreamIF binaryInputStream) throws Exception {
					throw new NullPointerException();
				}

				@Override
				public SingleItemType getSingleItemType() {
					return SingleItemType.FIXED_LENGTH_STRING;
				}
				
			}
			
			private AbstractTHBSingleItemDecoder thbSingleItemDecoder = new THBSingleItemDecoderMock(); 

			@Override
			public AbstractTHBSingleItemDecoder get(int itemTypeID) {
				return thbSingleItemDecoder;
			}
			
		}
		
		THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = 
				new THBSingleItemDecoderMatcherMock();
		
		SingleItemDecoderIF thbSingleItemDecoder = new THBSingleItemDecoder(thbSingleItemDecoderMatcher);
		
		String path = "Empty";
		String itemName = "strVar1";
		SingleItemType singleItemType = SingleItemType.FIXED_LENGTH_STRING;
		@SuppressWarnings("unused")
		Object nativeItemValue = null;
		int itemSize = -10;
		String nativeItemCharset = null;
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
		
		try {
			nativeItemValue = thbSingleItemDecoder.getValueFromReadableMiddleObject(path, itemName, singleItemType, itemSize, nativeItemCharset, binaryInputStream);
			
			fail("no BodyFormatException");
		} catch (BodyFormatException e) {
			String errorMessage = e.getMessage();
			log.info(errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("unknown error");
		}
		
		// String itemValue = (String)nativeItemValue;
	}
	
	@Test
	public void test_WhenBodyFormatExceptionWhyOutOfMemoryError() {
		Charset streamCharset = Charset.forName("utf8");
		// CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		
		class THBSingleItemDecoderMatcherMock implements  THBSingleItemDecoderMatcherIF {
			
			class THBSingleItemDecoderMock extends AbstractTHBSingleItemDecoder {

				@Override
				public Object getValue(int itemTypeID, String itemName, int itemSize, String nativeItemCharset,
						BinaryInputStreamIF binaryInputStream) throws Exception {
					
					@SuppressWarnings("unused")
					byte t[] = new byte[Integer.MAX_VALUE];
					
					return null;
				}

				@Override
				public SingleItemType getSingleItemType() {
					return SingleItemType.FIXED_LENGTH_STRING;
				}
				
			}
			
			private AbstractTHBSingleItemDecoder thbSingleItemDecoder = new THBSingleItemDecoderMock(); 

			@Override
			public AbstractTHBSingleItemDecoder get(int itemTypeID) {
				return thbSingleItemDecoder;
			}
			
		}
		
		THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = 
				new THBSingleItemDecoderMatcherMock();
		
		SingleItemDecoderIF thbSingleItemDecoder = new THBSingleItemDecoder(thbSingleItemDecoderMatcher);
		
		String path = "Empty";
		String itemName = "strVar1";
		SingleItemType singleItemType = SingleItemType.FIXED_LENGTH_STRING;
		@SuppressWarnings("unused")
		Object nativeItemValue = null;
		int itemSize = -10;
		String nativeItemCharset = null;
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
		
		try {
			nativeItemValue = thbSingleItemDecoder.getValueFromReadableMiddleObject(path, itemName, singleItemType, itemSize, nativeItemCharset, binaryInputStream);
			
			fail("no BodyFormatException");
		} catch (BodyFormatException e) {
			String errorMessage = e.getMessage();
			log.info(errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("unknown error");
		}
		
		// String itemValue = (String)nativeItemValue;
	}
}
