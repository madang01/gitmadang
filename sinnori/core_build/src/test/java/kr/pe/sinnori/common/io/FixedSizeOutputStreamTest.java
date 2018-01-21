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

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;
import kr.pe.sinnori.common.exception.SinnoriBufferOverflowException;
import kr.pe.sinnori.common.util.HexUtil;

public class FixedSizeOutputStreamTest {
	Logger log = null;

	@Before
	public void setup() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_base";
		LOG_TYPE logType = LOG_TYPE.SERVER;
		String logbackConfigFilePathString = BuildSystemPathSupporter.getLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		String sinnoriLogPathString = BuildSystemPathSupporter.getLogPathString(sinnoriInstalledPathString, mainProjectName, logType);
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);		
		
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH,
				sinnoriLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);

		// SinnoriLogbackManger.getInstance().setup(sinnoriInstalledPathString, mainProjectName, logType);
		
		log = LoggerFactory.getLogger(FixedSizeOutputStreamTest.class);

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
	public void testPutUnsignedByte_shortType_lessThanZero() {
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
	public void testPutUnsignedByte_shortType_greaterThanMax() {
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
	public void testPutUnsignedByte_minMaxMiddle() {
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		ByteBuffer shortBuffer = ByteBuffer.allocate(2);
		
		short shortTypeExpectedValueList[] = { 0, CommonStaticFinalVars.UNSIGNED_BYTE_MAX,
				CommonStaticFinalVars.UNSIGNED_BYTE_MAX / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			streambuffer.order(streamByteOrder);
			shortBuffer.order(streamByteOrder);
			
			for (short expectedValue : shortTypeExpectedValueList) {
				streambuffer.clear();
				Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);

				try {
					FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

					fsos.putUnsignedByte(expectedValue);
					fsos.flipOutputStreamBuffer();

					FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
					short actualValue = fsis.getUnsignedByte();

					assertEquals(expectedValue, actualValue);

					if (streambuffer.hasRemaining()) {
						fail("buffer has a remaing data");
					}

					streambuffer.position(0);
					shortBuffer.clear();
					Arrays.fill(shortBuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
					if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {						
						shortBuffer.position(1);
						shortBuffer.put(streambuffer.get());
					} else {
						shortBuffer.put(streambuffer.get());
					}

					shortBuffer.clear();
					actualValue = shortBuffer.getShort();
					
					assertEquals(expectedValue, actualValue);

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
	public void testPutUnsignedByte_integerType_lessThanZero() {
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
	public void testPutUnsignedByte_integerType_greaterThanMax() {
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
	public void testPutUnsignedByte_longType_greaterThanMax() {
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
	public void testPutUnsignedShort_integerType_lessThanZero() {
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
	public void testPutUnsignedShort_integerType_greaterThanMax() {
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
	public void testPutUnsignedShort_longType_lessThanZero() {
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
	public void testPutUnsignedShort_longType_greaterThanMax() {
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
	public void testPutUnsignedShort_minMaxMiddle() {
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		int integerTypeExpectedValueList[] = { 0, CommonStaticFinalVars.UNSIGNED_SHORT_MAX,
				CommonStaticFinalVars.UNSIGNED_SHORT_MAX / 3 };
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		ByteBuffer integerBuffer = ByteBuffer.allocate(4);

		for (ByteOrder streamByteOrder : streamByteOrderList) {

			streambuffer.order(streamByteOrder);
			integerBuffer.order(streamByteOrder);

			for (int expectedValue : integerTypeExpectedValueList) {
				streambuffer.clear();
				Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);

				try {
					FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

					fsos.putUnsignedShort(expectedValue);
					fsos.flipOutputStreamBuffer();

					FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
					int actualValue = fsis.getUnsignedShort();

					assertEquals(expectedValue, actualValue);

					if (streambuffer.hasRemaining()) {
						fail("buffer has a remaing data");
					}

					streambuffer.position(0);
					integerBuffer.clear();
					Arrays.fill(integerBuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
					if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {
						integerBuffer.position(2);
						integerBuffer.put(streambuffer.get());
						integerBuffer.put(streambuffer.get());
					} else {
						integerBuffer.put(streambuffer.get());
						integerBuffer.put(streambuffer.get());
					}

					integerBuffer.clear();
					actualValue = integerBuffer.getInt();
					assertEquals(expectedValue, actualValue);

				} catch (IllegalArgumentException e) {
					fail(e.getMessage());
				} catch (Exception e) {
					fail(e.getMessage());
				}
			}
		}
	}

	@Test
	public void testPutUnsignedInt_lessThanZero() {
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
	public void testPutUnsignedInt_greaterThanMax() {
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
	public void testPutUnsignedInt_minMaxMiddle() {
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		long longTypeExpectedValueList[] = {CommonStaticFinalVars.UNSIGNED_INTEGER_MAX,
				CommonStaticFinalVars.UNSIGNED_INTEGER_MAX / 14, 0 };
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		ByteBuffer longBuffer = ByteBuffer.allocate(8);

		for (ByteOrder streamByteOrder : streamByteOrderList) {

			streambuffer.order(streamByteOrder);
			longBuffer.order(streamByteOrder);

			for (long expectedValue : longTypeExpectedValueList) {
				streambuffer.clear();
				Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);

				try {
					FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

					fsos.putUnsignedInt(expectedValue);
					fsos.flipOutputStreamBuffer();

					FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
					long actualValue = fsis.getUnsignedInt();

					assertEquals(expectedValue, actualValue);

					if (streambuffer.hasRemaining()) {
						fail("buffer has a remaing data");
					}

					streambuffer.position(0);
					longBuffer.clear();
					Arrays.fill(longBuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
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
					actualValue = longBuffer.getLong();
					assertEquals(expectedValue, actualValue);
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
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
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
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
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
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
		byte[] src = {0x10, 0x20, 0x30, 0x40};
		int offset = src.length;
		int length = 1;
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			
			fsos.putBytes(src, offset, length);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter offset[%d] is greater than or equal to array.length[%d]", offset, src.length);

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
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
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
	public void testPutBytes_sumOfTheParameterOffsetAndtheParameterLength_isGreaterThanArrayLength() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
		byte[] src = {0x10, 0x20, 0x30, 0x40};
		int offset = src.length-1;
		int length = 2;
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			
			fsos.putBytes(src, offset, length);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			long sumOfOffsetAndLength = (long)offset + length;
			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the sum[%d] of the parameter offset[%d] and the parameter length[%d] is greater than array.length[%d]", 
					sumOfOffsetAndLength, offset, length, src.length);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPutBytes_minMaxMiddle() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);

		
		int oldPosition = -1;
		int newPosition = -2;
		
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
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
	public void testPutBytes_greaterThanNumberOfBytesRemaining() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
				
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
		byte[] src = {0x10, 0x20, 0x30, 0x40};
		int offset = 0;
		int length = src.length;
		
		int numberOfBytesRequired = length;		
		int numberOfBytesSkipping = streambuffer.remaining() - 1;
		long numberOfBytesRemaining = streambuffer.remaining() - numberOfBytesSkipping;
		
		// streambuffer.clear();
		// streambuffer.position(streambuffer.limit() - src.length + 1);
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);	
			
			fsos.skip(numberOfBytesSkipping);
			
			fsos.putBytes(src, offset, length);
			
			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			
			String expectedMessage = String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);

			assertEquals(expectedMessage, errorMessage);
		
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
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
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
		
		int length = -1;

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			String sourceValue = "한글a똠방각하";
			
			

			fsos.putFixedLengthString(length, sourceValue, streamCharsetEncoder);
			
			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			
			String expectedMessage = String.format("the parameter fixedLength[%d] is less than zero", length);

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
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);

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
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);

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
	public void testPutFixedLengthString_greaterThanNumberOfBytesRemaining() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		
		Charset wantedCharset = Charset.forName("UTF8");
		CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);;

		int length = 5;
		String src = "똠방각하";
		
		/*streambuffer.clear();
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
		streambuffer.position(streambuffer.limit() - length + 1);*/
		
		int numberOfBytesRequired = length;		
		int numberOfBytesSkipping = streambuffer.remaining() - 1;
		long numberOfBytesRemaining = streambuffer.remaining() - numberOfBytesSkipping;
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			
			fsos.skip(numberOfBytesSkipping);

			fsos.putFixedLengthString(length, src, wantedCharsetEncoder);
			fsos.flipOutputStreamBuffer();

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			
			String expectedMessage = String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);

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
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
				
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			fsos.putFixedLengthString(length, src, wantedCharsetEncoder);
			if (length != streambuffer.position() ) {
				fail(String.format("1.the parameter length[%d] is different from the the length[%d] written in stream buffer", length, streambuffer.position()));
			}
			
			streambuffer.clear();

			FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
			String actualValue = fsis.getFixedLengthString(length, wantedCharsetDecoder);

			/** 원본 문자열 "똠방각하" 에서 스트림 문자셋  ecu-kr 는 '똠' 자를 표현할 수 없지만 UTF-8 문자셋은 표현할 수 있다. */
			assertEquals(expectedValue, actualValue);
			
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
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
				
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			fsos.putFixedLengthString(length, src, wantedCharsetEncoder);
			if (length != streambuffer.position() ) {
				fail(String.format("1.the parameter length[%d] is different from the the length[%d] written in stream buffer", length, streambuffer.position()));
			}
			
			streambuffer.clear();
			fsos.skip(3);

			FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
			String actualValue = fsis.getFixedLengthString(length, wantedCharsetDecoder);

			assertEquals(expectedValue, actualValue.trim());
			
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