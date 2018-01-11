package kr.pe.sinnori.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.util.HexUtil;

public class FixedSizeOutputStreamTest {
	Logger log = LoggerFactory.getLogger(FixedSizeOutputStreamTest.class);

	@Before
	public void setup() {
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				"sample_base");
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				"D:\\gitsinnori\\sinnori");

		SinnoriLogbackManger.getInstance().setup();

	}

	@Test
	public void test빅엔디안과리틀엔디안차이점() {
		long tValue = 0x1122334455667788L;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		streambuffer.order(ByteOrder.BIG_ENDIAN);
		streambuffer.putLong(tValue);

		log.info("big endian::streamBuffer=0x{}", HexUtil.getAllHexStringFromByteBuffer(streambuffer));

		streambuffer.clear();
		streambuffer.order(ByteOrder.LITTLE_ENDIAN);
		streambuffer.putLong(tValue);

		log.info("little endian::streamBuffer=0x{}", HexUtil.getAllHexStringFromByteBuffer(streambuffer));

		streambuffer.clear();
		streambuffer.order(ByteOrder.BIG_ENDIAN);
		streambuffer.putLong(0L);
		streambuffer.clear();
	}

	@Test
	public void testConstructor_theParameterOutputStreamBuffer_null() {
		try {
			@SuppressWarnings("unused")
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(null, Charset.forName("utf-8").newEncoder());

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = "the parameter outputStreamBuffer is null";

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testConstructor_theParameterStreamCharsetEncoder_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			@SuppressWarnings("unused")
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, null);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = "the parameter streamCharsetEncoder is null";

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testPutUnsignedByte_theParameterValue_shortType_lessThanZero() {
		short value = -1;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putUnsignedByte(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is less than zero", value);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testPutUnsignedByte_theParameterValue_shortType_greaterThanMax() {
		short value = CommonStaticFinalVars.UNSIGNED_BYTE_MAX + 1;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putUnsignedByte(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is greater than the unsigned byte max[%d]",
					value, CommonStaticFinalVars.UNSIGNED_BYTE_MAX);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testPutUnsignedByte_theParameterValue_minMaxMiddle() {
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		ByteBuffer shortBuffer = ByteBuffer.allocate(2);
		
		short shortTypeExpectedValueList[] = { 0, CommonStaticFinalVars.UNSIGNED_BYTE_MAX,
				CommonStaticFinalVars.UNSIGNED_BYTE_MAX / 2 };

		for (int j = 0; j < streamByteOrderList.length; j++) {
			ByteOrder streamByteOrder = streamByteOrderList[j];
			streambuffer.order(streamByteOrder);
			shortBuffer.order(streamByteOrder);
			
			for (int i = 0; i < shortTypeExpectedValueList.length; i++) {
				short expectedValue = shortTypeExpectedValueList[i];
				streambuffer.clear();
				Arrays.fill(streambuffer.array(), (byte) 0);

				try {
					FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

					fsos.putUnsignedByte(expectedValue);
					fsos.flipOutputStreamBuffer();

					FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
					short actucalValue = fsis.getUnsignedByte();

					assertEquals(expectedValue, actucalValue);

					if (streambuffer.hasRemaining()) {
						fail("buffer has a remaing data");
					}

					streambuffer.position(0);
					shortBuffer.clear();
					Arrays.fill(shortBuffer.array(), (byte) 0);
					if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {						
						shortBuffer.position(1);
						shortBuffer.put(streambuffer.get());
					} else {
						shortBuffer.put(streambuffer.get());
					}

					shortBuffer.clear();
					actucalValue = shortBuffer.getShort();
					
					assertEquals(expectedValue, actucalValue);

				} catch (IllegalArgumentException e) {
					fail(e.getMessage());
				} catch (Exception e) {
					log.info(e.getMessage(), e);
					fail(e.getMessage());
				}
			}
		}
	}

	@Test
	public void testPutUnsignedByte_theParameterValue_integerType_lessThanZero() {
		int value = -1;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putUnsignedByte(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is less than zero", value);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testPutUnsignedByte_theParameterValue_integerType_greaterThanMax() {
		int value = CommonStaticFinalVars.UNSIGNED_BYTE_MAX + 1;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putUnsignedByte(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is greater than the unsigned byte max[%d]",
					value, CommonStaticFinalVars.UNSIGNED_BYTE_MAX);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPutUnsignedByte_theParameterValue_longType_greaterThanMax() {
		long value = CommonStaticFinalVars.UNSIGNED_BYTE_MAX + 1;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putUnsignedByte(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is greater than the unsigned byte max[%d]",
					value, CommonStaticFinalVars.UNSIGNED_BYTE_MAX);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testPutUnsignedShort_theParameterValue_integerType_lessThanZero() {
		int value = -1;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putUnsignedShort(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is less than zero", value);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testPutUnsignedShort_theParameterValue_integerType_greaterThanMax() {
		int value = CommonStaticFinalVars.UNSIGNED_SHORT_MAX + 10;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putUnsignedShort(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is greater than the unsigned short max[%d]",
					value, CommonStaticFinalVars.UNSIGNED_SHORT_MAX);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testPutUnsignedShort_theParameterValue_longType_lessThanZero() {
		long value = -1;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putUnsignedShort(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is less than zero", value);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testPutUnsignedShort_theParameterValue_longType_greaterThanMax() {
		long value = CommonStaticFinalVars.UNSIGNED_SHORT_MAX + 10;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putUnsignedShort(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is greater than the unsigned short max[%d]",
					value, CommonStaticFinalVars.UNSIGNED_SHORT_MAX);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPutUnsignedShort_theParameterValue_minMaxMiddle() {
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		int integerTypeExpectedValueList[] = { 0, CommonStaticFinalVars.UNSIGNED_SHORT_MAX,
				CommonStaticFinalVars.UNSIGNED_SHORT_MAX / 3 };
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		ByteBuffer integerBuffer = ByteBuffer.allocate(4);

		for (int j = 0; j < streamByteOrderList.length; j++) {
			ByteOrder streamByteOrder = streamByteOrderList[j];

			streambuffer.order(streamByteOrder);
			integerBuffer.order(streamByteOrder);

			for (int i = 0; i < integerTypeExpectedValueList.length; i++) {
				int expectedValue = integerTypeExpectedValueList[i];
				streambuffer.clear();
				Arrays.fill(streambuffer.array(), (byte) 0);

				try {
					FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

					fsos.putUnsignedShort(expectedValue);
					fsos.flipOutputStreamBuffer();

					FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
					int actucalValue = fsis.getUnsignedShort();

					assertEquals(expectedValue, actucalValue);

					if (streambuffer.hasRemaining()) {
						fail("buffer has a remaing data");
					}

					streambuffer.position(0);
					integerBuffer.clear();
					Arrays.fill(integerBuffer.array(), (byte) 0);
					if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {
						integerBuffer.position(2);
						integerBuffer.put(streambuffer.get());
						integerBuffer.put(streambuffer.get());
					} else {
						integerBuffer.put(streambuffer.get());
						integerBuffer.put(streambuffer.get());
					}

					integerBuffer.clear();
					actucalValue = integerBuffer.getInt();
					assertEquals(expectedValue, actucalValue);

				} catch (IllegalArgumentException e) {
					fail(e.getMessage());
				} catch (Exception e) {
					fail(e.getMessage());
				}
			}
		}
	}

	@Test
	public void testPutUnsignedInt_theParameterValue_lessThanZero() {
		long value = -1;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putUnsignedInt(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is less than zero", value);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testPutUnsignedInt_theParameterValue_greaterThanMax() {
		long value = CommonStaticFinalVars.UNSIGNED_INTEGER_MAX + 10;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putUnsignedInt(value);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the parameter value[%d] is greater than the unsigned integer max[%d]", value,
					CommonStaticFinalVars.UNSIGNED_INTEGER_MAX);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	

	@Test
	public void testPutUnsignedInt_theParameterValue_minMaxMiddle() {
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		long longTypeExpectedValueList[] = { 0, CommonStaticFinalVars.UNSIGNED_INTEGER_MAX,
				CommonStaticFinalVars.UNSIGNED_INTEGER_MAX / 14 };
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		ByteBuffer longBuffer = ByteBuffer.allocate(8);

		for (int j = 0; j < streamByteOrderList.length; j++) {
			ByteOrder streamByteOrder = streamByteOrderList[j];

			streambuffer.order(streamByteOrder);
			longBuffer.order(streamByteOrder);

			for (int i = 0; i < longTypeExpectedValueList.length; i++) {
				long expectedValue = longTypeExpectedValueList[i];
				streambuffer.clear();
				Arrays.fill(streambuffer.array(), (byte) 0);

				try {
					FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

					fsos.putUnsignedInt(expectedValue);
					fsos.flipOutputStreamBuffer();

					FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
					long actucalValue = fsis.getUnsignedInt();

					assertEquals(expectedValue, actucalValue);

					if (streambuffer.hasRemaining()) {
						fail("buffer has a remaing data");
					}

					streambuffer.position(0);
					longBuffer.clear();
					Arrays.fill(longBuffer.array(), (byte) 0);
					if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {
						longBuffer.position(4);
						longBuffer.put(streambuffer.get());
						longBuffer.put(streambuffer.get());
						longBuffer.put(streambuffer.get());
						longBuffer.put(streambuffer.get());
					} else {
						longBuffer.put(streambuffer.get());
						longBuffer.put(streambuffer.get());
						longBuffer.put(streambuffer.get());
						longBuffer.put(streambuffer.get());
					}

					longBuffer.clear();
					actucalValue = longBuffer.getLong();
					assertEquals(expectedValue, actucalValue);
				} catch (IllegalArgumentException e) {
					fail(e.getMessage());
				} catch (Exception e) {
					fail(e.getMessage());
				}
			}
		}
	}

	 
	@Test
	public void testPutBytes_theParameterSrc_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), (byte) 0);
		byte[] src = null;
		int offset = 0;
		int length = 1;
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			
			fsos.putBytes(src, offset, length);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = "the parameter src is null";

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPutBytes_theParameterOffset_lessThanZero() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), (byte) 0);
		byte[] src = {0x10, 0x20, 0x30, 0x40};
		int offset = -1;
		int length = 1;
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			
			fsos.putBytes(src, offset, length);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter offset[%d] is less than zero", offset);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testPutBytes_theParameterOffset_greaterThanOrEqualToSourceByteArrayLength() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), (byte) 0);
		byte[] src = {0x10, 0x20, 0x30, 0x40};
		int offset = src.length;
		int length = 1;
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			
			fsos.putBytes(src, offset, length);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter offset[%d] is greater than or equal to the length[%d] of the parameter src that is a byte array", offset, src.length);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPutBytes_theParameterLength_lessThanZero() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), (byte) 0);
		byte[] src = {0x10, 0x20, 0x30, 0x40};
		int offset = 0;
		int length = -1;
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			
			fsos.putBytes(src, offset, length);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter length[%d] is less than zero", length);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testPutBytes_theParameterOffset_theParameterLength_sumIsGreaterThanSourceByteArrayLength() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), (byte) 0);
		byte[] src = {0x10, 0x20, 0x30, 0x40};
		int offset = 1;
		int length = src.length;
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			
			fsos.putBytes(src, offset, length);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			long sumOfOffsetAndLength = (long)offset + length;
			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the sum[%d] of the parameter offset[%d] and the parameter length[%d] is greater than the length[%d] of the parameter src that is a byte array", 
					sumOfOffsetAndLength, offset, length, src.length);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPutBytes_theParameterOffset_theParameterLength_minMaxMiddle() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);

		
		int oldPosition = -1;
		int newPosition = -2;
		
		Arrays.fill(streambuffer.array(), (byte) 0);
		byte[] src = {0x10, 0x20, 0x30, 0x40};
		int offset = 0;
		int length = 0;
		
		streambuffer.clear();
		oldPosition = streambuffer.position();
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			
			fsos.putBytes(src, offset, length);
		
		} catch (Exception e) {
			fail(e.getMessage());
		}
		newPosition = streambuffer.position();
		assertEquals(oldPosition, newPosition);
		
		
		streambuffer.clear();
		oldPosition = streambuffer.position();
		offset = 0;
		length = src.length;
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			
			fsos.putBytes(src, offset, length);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		newPosition = streambuffer.position();
		assertEquals(oldPosition+length, newPosition);
		
		streambuffer.clear();
		oldPosition = streambuffer.position();
		offset = 0;
		length = 2;
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			
			fsos.putBytes(src, offset, length);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		newPosition = streambuffer.position();
		assertEquals(oldPosition+length, newPosition);
	}
	
	
	@Test
	public void testPutBytes_theParameterLength_greaterThanRemaingBytes() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);

		
				
		Arrays.fill(streambuffer.array(), (byte) 0);
		byte[] src = {0x10, 0x20, 0x30, 0x40};
		int offset = 0;
		int length = src.length;
		
		streambuffer.clear();
		streambuffer.position(streambuffer.limit() - src.length + 1);
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			
			fsos.putBytes(src, offset, length);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			
			String expectedMessage = String.format(
					"the parameter length[%d] is greater than the remaining bytes[%d]", length,
					streambuffer.remaining());

			assertEquals(expectedMessage, errorMessage);
		
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	
	
	
	@Test
	public void testPutFixedLengthString_theParameterLength_lessThanZero() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), (byte) 0);
		
		int length = -1;

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			String sourceValue = "한글a똠방각하";
			
			

			fsos.putFixedLengthString(length, sourceValue, streamCharsetEncoder);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			
			String expectedMessage = String.format("the parameter length[%d] is less than zero", length);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	
	@Test
	public void testPutFixedLengthString_theParameterSrc_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		
		Charset wantedCharset = Charset.forName("UTF8");
		CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), (byte) 0);

		int length = 5;
		String src = null;
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			fsos.putFixedLengthString(length, src, wantedCharsetEncoder);
			fsos.flipOutputStreamBuffer();

			fail("no IllegalArgumentException");

		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			// log.info(errorMessage, e);
			
			String expectedMessage = "the parameter src is null";

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testPutFixedLengthString_theParameterWantedCharsetEncoder_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		
		// Charset wantedCharset = Charset.forName("UTF8");
		CharsetEncoder wantedCharsetEncoder = null;

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), (byte) 0);

		int length = 5;
		String src = "똠방각하";
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			fsos.putFixedLengthString(length, src, wantedCharsetEncoder);
			fsos.flipOutputStreamBuffer();

			fail("no IllegalArgumentException");

		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			// log.info(errorMessage, e);
			
			String expectedMessage = "the parameter wantedCharsetEncoder is null";

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	
	@Test
	public void testPutFixedLengthString_theParameterLength_greaterThanRemaingBytes() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		
		Charset wantedCharset = Charset.forName("UTF8");
		CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);;

		int length = 5;
		String src = "똠방각하";
		
		streambuffer.clear();
		Arrays.fill(streambuffer.array(), (byte) 0);
		streambuffer.position(streambuffer.limit() - length + 1);
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			fsos.putFixedLengthString(length, src, wantedCharsetEncoder);
			fsos.flipOutputStreamBuffer();

			fail("no IllegalArgumentException");

		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			// log.info(errorMessage, e);
			
			String expectedMessage = String.format("the parameter length[%d] is greater than the remaining bytes[%d]", length, streambuffer.remaining());

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testPutFixedLengthString_differentStreamCharsetAndWantedCharset() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		
		
		Charset wantedCharset = Charset.forName("UTF8");
		CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);
		CharsetDecoder wantedCharsetDecoder = CharsetUtil.createCharsetDecoder(wantedCharset);

		/** 참고 : "똠방각하" 문자열은 UTF-8  기준 12 bytes 길이를 갖는다 */
		int length = 12;
		String src = "똠방각하";
		String expectedValue = src;
		
		streambuffer.clear();
		Arrays.fill(streambuffer.array(), (byte) 0);
				
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			fsos.putFixedLengthString(length, src, wantedCharsetEncoder);
			if (length != streambuffer.position() ) {
				fail(String.format("1.the parameter length[%d] is different from the the length[%d] written in stream buffer", length, streambuffer.position()));
			}
			
			streambuffer.clear();

			FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
			String actucalValue = fsis.getFixedLengthString(length, wantedCharsetDecoder);

			/** 원본 문자열 "똠방각하" 에서 스트림 문자셋  ecu-kr 는 '똠' 자를 표현할 수 없지만 UTF-8 문자셋은 표현할 수 있다. */
			assertEquals(expectedValue, actucalValue);
			
			if (length != streambuffer.position() ) {
				fail(String.format("2.the parameter length[%d] is different from the the length[%d] read in stream buffer", length, streambuffer.position()));
			}
		
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testSkip_normal() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		
		
		Charset wantedCharset = Charset.forName("UTF8");
		CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);
		CharsetDecoder wantedCharsetDecoder = CharsetUtil.createCharsetDecoder(wantedCharset);

		/** 참고 : "똠방각하" 문자열은 UTF-8  기준 12 bytes 길이를 갖는다 */
		int length = 12;
		String src = "똠방각하";
		String expectedValue = "방각하";
		
		streambuffer.clear();
		Arrays.fill(streambuffer.array(), (byte) 0);
				
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			fsos.putFixedLengthString(length, src, wantedCharsetEncoder);
			if (length != streambuffer.position() ) {
				fail(String.format("1.the parameter length[%d] is different from the the length[%d] written in stream buffer", length, streambuffer.position()));
			}
			
			streambuffer.clear();
			fsos.skip(3);

			FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
			String actucalValue = fsis.getFixedLengthString(length, wantedCharsetDecoder);

			assertEquals(expectedValue, actucalValue.trim());
			
			if (length != (streambuffer.position()-3) ) {
				fail(String.format("2.the parameter length[%d] is different from the the length[%d] read in stream buffer", length, streambuffer.position()-3));
			}
		
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPutFixedLengthString_complex() {
		fail("FIXME!, 복합테스트 미진행");
	}	
}
