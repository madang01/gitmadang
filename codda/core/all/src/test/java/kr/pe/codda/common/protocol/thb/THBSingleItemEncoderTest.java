package kr.pe.codda.common.protocol.thb;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.io.BinaryOutputStreamIF;
import kr.pe.codda.common.io.FixedSizeOutputStream;
import kr.pe.codda.common.type.SingleItemType;

public class THBSingleItemEncoderTest extends AbstractJunitTest {
	
	@Test
	public void testPutValueToWritableMiddleObject_잘못된문자셋이름() {
		Charset streamCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher = 
				new THBSingleItemEncoderMatcher(streamCharsetEncoder);
		
		THBSingleItemEncoder thbSingleItemEncoder = new THBSingleItemEncoder(thbSingleItemEncoderMatcher);
		
		
		String path = "Empty";
		String itemName = "strVar1";
		SingleItemType singleItemType = SingleItemType.UB_PASCAL_STRING;
		Object nativeItemValue = "한글";
		int itemSize = -1;
		String nativeItemCharset = "utf888";
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder); 
		
		try {
			thbSingleItemEncoder.putValueToWritableMiddleObject(path, itemName, singleItemType, nativeItemValue, itemSize, nativeItemCharset, binaryOutputStream);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.lastIndexOf("is a bad charset name") < 0) {
				log.warn("unknown error", e);
				fail("unknow error");
			}
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("unknown error");
		}
	}
	
	@Test
	public void testPutValueToWritableMiddleObject_잘못된크기지정() {
		Charset streamCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher = 
				new THBSingleItemEncoderMatcher(streamCharsetEncoder);
		
		THBSingleItemEncoder thbSingleItemEncoder = new THBSingleItemEncoder(thbSingleItemEncoderMatcher);
		
		
		String path = "Empty";
		String itemName = "strVar1";
		SingleItemType singleItemType = SingleItemType.FIXED_LENGTH_STRING;
		Object nativeItemValue = "한글";
		int itemSize = -1;
		String nativeItemCharset = "utf888";
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder); 
		
		try {
			thbSingleItemEncoder.putValueToWritableMiddleObject(path, itemName, singleItemType, nativeItemValue, itemSize, nativeItemCharset, binaryOutputStream);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.lastIndexOf("is less than zero") < 0) {
				log.warn("unknown error", e);
				fail("unknow error");
			}
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("unknown error");
		}
	}
	
	@Test
	public void testPutValueToWritableMiddleObject_WhenBodyFormatExceptionWhyNullPointerException() {
		Charset streamCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		class THBSingleItemEncoderMatcherMock implements THBSingleItemEncoderMatcherIF {
			class THBSingleItemEncoderMock extends AbstractTHBSingleItemEncoder {

				@Override
				public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
						String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream) throws Exception {
					throw new NullPointerException();
				}

				@Override
				public SingleItemType getSingleItemType() {
					return SingleItemType.FIXED_LENGTH_STRING;
				}
				
			}
			
			private THBSingleItemEncoderMock thbSingleItemEncoderMock = new THBSingleItemEncoderMock();
			
			public THBSingleItemEncoderMatcherMock() {
				
			}

			@Override
			public AbstractTHBSingleItemEncoder get(int itemTypeID) {
				return thbSingleItemEncoderMock;
			}
		}
		
		THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher = 
				new THBSingleItemEncoderMatcherMock();
		
		THBSingleItemEncoder thbSingleItemEncoder = new THBSingleItemEncoder(thbSingleItemEncoderMatcher);
		
		
		String path = "Empty";
		String itemName = "strVar1";
		SingleItemType singleItemType = SingleItemType.FIXED_LENGTH_STRING;
		Object nativeItemValue = "한글";
		int itemSize = -1;
		String nativeItemCharset = "utf888";
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder); 
		
		try {
			thbSingleItemEncoder.putValueToWritableMiddleObject(path, itemName, singleItemType, nativeItemValue, itemSize, nativeItemCharset, binaryOutputStream);
			
			fail("no BodyFormatException");
		} catch(BodyFormatException e) {
			String errorMessage = e.getMessage();
			log.info(errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("unknown error");
		}
	}
	
	@Test
	public void testPutValueToWritableMiddleObject_WhenBodyFormatExceptionWhyOutOfMemoryError() {
		Charset streamCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		class THBSingleItemEncoderMatcherMock implements THBSingleItemEncoderMatcherIF {
			class THBSingleItemEncoderMock extends AbstractTHBSingleItemEncoder {

				@Override
				public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
						String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream) throws Exception {
					
					byte t[] = new byte[Integer.MAX_VALUE];
					
					log.info("temp buffer size={}", t.length);
				}

				@Override
				public SingleItemType getSingleItemType() {
					return SingleItemType.FIXED_LENGTH_STRING;
				}
				
			}
			
			private THBSingleItemEncoderMock thbSingleItemEncoderMock = new THBSingleItemEncoderMock();
			
			public THBSingleItemEncoderMatcherMock() {
				
			}

			@Override
			public AbstractTHBSingleItemEncoder get(int itemTypeID) {
				return thbSingleItemEncoderMock;
			}
		}
		
		THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher = 
				new THBSingleItemEncoderMatcherMock();
		
		THBSingleItemEncoder thbSingleItemEncoder = new THBSingleItemEncoder(thbSingleItemEncoderMatcher);
		
		
		String path = "Empty";
		String itemName = "strVar1";
		SingleItemType singleItemType = SingleItemType.FIXED_LENGTH_STRING;
		Object nativeItemValue = "한글";
		int itemSize = -1;
		String nativeItemCharset = "utf888";
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder); 
		
		try {
			thbSingleItemEncoder.putValueToWritableMiddleObject(path, itemName, singleItemType, nativeItemValue, itemSize, nativeItemCharset, binaryOutputStream);
			
			fail("no BodyFormatException");
		} catch(BodyFormatException e) {
			String errorMessage = e.getMessage();
			log.info(errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("unknown error");
		}
	}
}
