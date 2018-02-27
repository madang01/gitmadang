package kr.pe.sinnori.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitSupporter;
import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SinnoriBufferOverflowException;
import kr.pe.sinnori.common.util.HexUtil;

public class FixedSizeOutputStreamTest extends AbstractJunitSupporter {
	

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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutByte_basic() {
		byte expectedValue = -123;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putByte(expectedValue);

			streambuffer.flip();
			byte actualValue = streambuffer.get();

			assertEquals(expectedValue, actualValue);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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

				} catch (Exception e) {
					String errorMessage = "error::" + e.getMessage();
					log.warn(errorMessage, e);
					fail(errorMessage);
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutShort_basic() {
		short expectedValue = 577;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putShort(expectedValue);

			streambuffer.flip();
			short actualValue = streambuffer.getShort();

			assertEquals(expectedValue, actualValue);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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

				} catch (Exception e) {
					String errorMessage = "error::" + e.getMessage();
					log.warn(errorMessage, e);
					fail(errorMessage);
				}
			}
		}
	}

	@Test
	public void testPutInt_basic() {
		int expectedValue = Short.MAX_VALUE + 10000;
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());

			fsos.putInt(expectedValue);

			streambuffer.flip();
			int actualValue = streambuffer.getInt();

			assertEquals(expectedValue, actualValue);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutUnsignedInt_minMaxMiddle() {
		ByteBuffer streambuffer = ByteBuffer.allocate(8);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		long longTypeExpectedValueList[] = { CommonStaticFinalVars.UNSIGNED_INTEGER_MAX,
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
				} catch (Exception e) {
					String errorMessage = "error::" + e.getMessage();
					log.warn(errorMessage, e);
					fail(errorMessage);
				}
			}
		}
	}

	@Test
	public void testPutBytes_theParameterSrc_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(streamCharset);

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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutBytes_theParameterOffset_lessThanZero() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(streamCharset);

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
		byte[] src = { 0x10, 0x20, 0x30, 0x40 };
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutBytes_theParameterOffset_greaterThanOrEqualToSourceByteArrayLength() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(streamCharset);

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
		byte[] src = { 0x10, 0x20, 0x30, 0x40 };
		int offset = src.length;
		int length = 1;

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			fsos.putBytes(src, offset, length);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the parameter offset[%d] is greater than or equal to array.length[%d]", offset, src.length);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutBytes_theParameterLength_lessThanZero() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(streamCharset);

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
		byte[] src = { 0x10, 0x20, 0x30, 0x40 };
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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutBytes_sumOfTheParameterOffsetAndtheParameterLength_isGreaterThanArrayLength() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(streamCharset);

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
		byte[] src = { 0x10, 0x20, 0x30, 0x40 };
		int offset = src.length - 1;
		int length = 2;

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			fsos.putBytes(src, offset, length);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			long sumOfOffsetAndLength = (long) offset + length;
			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the sum[%d] of the parameter offset[%d] and the parameter length[%d] is greater than array.length[%d]",
					sumOfOffsetAndLength, offset, length, src.length);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutBytes_basic() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(streamCharset);

		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
		byte[] src = { 0x10, 0x20, 0x30, 0x40 };

		// int length = 0;

		for (int offset = 0; offset < src.length; offset++) {
			int maxLength = src.length - offset;

			for (int length = 0; length <= maxLength; length++) {
				streambuffer.clear();

				log.info(
						"test case::public void putBytes(byte[] src, int offset, int length)::offset={}, legnth={}::쓰여진 바이트수 점검",
						offset, length);

				try {
					FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

					fsos.putBytes(src, offset, length);

				} catch (Exception e) {
					String errorMessage = "error::" + e.getMessage();
					log.warn(errorMessage, e);
					fail(errorMessage);
				}

				streambuffer.flip();

				assertEquals(length, streambuffer.remaining());
			}
		}
	}

	@Test
	public void testPutBytes_greaterThanNumberOfBytesRemaining() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(streamCharset);

		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);
		byte[] src = { 0x10, 0x20, 0x30, 0x40 };
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

			String expectedMessage = String.format(
					"the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);

			assertEquals(expectedMessage, errorMessage);

		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutFixedLengthString_theParameterLength_lessThanZero() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(streamCharset);

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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutFixedLengthString_theParameterSrc_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(streamCharset);

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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutFixedLengthString_theParameterWantedCharsetEncoder_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(streamCharset);

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
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutFixedLengthString_greaterThanNumberOfBytesRemaining() {
		ByteBuffer streambuffer = ByteBuffer.allocate(1024);

		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		// CharsetDecoder streamCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(streamCharset);

		Charset wantedCharset = Charset.forName("UTF8");
		CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);
		;

		int length = 5;
		String src = "똠방각하";

		/*
		 * streambuffer.clear(); Arrays.fill(streambuffer.array(),
		 * CommonStaticFinalVars.ZERO_BYTE); streambuffer.position(streambuffer.limit()
		 * - length + 1);
		 */

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

			String expectedMessage = String.format(
					"the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
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

		/** 참고 : "똠방각하" 문자열은 UTF-8 기준 12 bytes 길이를 갖는다 */
		int length = 12;
		String src = "똠방각하";
		String expectedValue = src;

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			fsos.putFixedLengthString(length, src, wantedCharsetEncoder);
			if (length != streambuffer.position()) {
				fail(String.format(
						"1.the parameter length[%d] is different from the the length[%d] written in stream buffer",
						length, streambuffer.position()));
			}

			streambuffer.clear();

			FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
			String actualValue = fsis.getFixedLengthString(length, wantedCharsetDecoder);

			/** 원본 문자열 "똠방각하" 에서 스트림 문자셋 ecu-kr 는 '똠' 자를 표현할 수 없지만 UTF-8 문자셋은 표현할 수 있다. */
			assertEquals(expectedValue, actualValue);

			if (length != streambuffer.position()) {
				fail(String.format(
						"2.the parameter length[%d] is different from the the length[%d] read in stream buffer", length,
						streambuffer.position()));
			}

		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutFixedLengthString_basic() {
		ByteBuffer streambuffer = ByteBuffer.allocate(100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		Charset wantedCharset = Charset.forName("EUC-KR");
		CharsetEncoder wantedCharsetEncoder = wantedCharset.newEncoder();
		CharsetDecoder wantedCharsetDecoder = wantedCharset.newDecoder();

		int fixedLength = 20;
		String src = "한글";

		{
			log.info(
					"test case::public void putFixedLengthString(int fixedLength, String src)::default charset verification");

			try {
				byte[] expectedValues = src.getBytes(streamCharset);

				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

				fsos.putFixedLengthString(fixedLength, src);

				streambuffer.flip();
				byte[] actualsValues = new byte[expectedValues.length];
				streambuffer.get(actualsValues);

				Assert.assertArrayEquals(expectedValues, actualsValues);

				streambuffer.rewind();
				FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
				String dst = fsis.getFixedLengthString(fixedLength).trim();

				assertEquals(src, dst);

			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			streambuffer.clear();

			log.info(
					"test case::public void putFixedLengthString(int fixedLength, String src, CharsetEncoder wantedCharsetEncoder)::custom charset verification");

			try {
				byte[] expectedValues = src.getBytes(wantedCharset);

				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

				fsos.putFixedLengthString(fixedLength, src, wantedCharsetEncoder);

				streambuffer.flip();
				byte[] actualsValues = new byte[expectedValues.length];
				streambuffer.get(actualsValues);

				Assert.assertArrayEquals(expectedValues, actualsValues);

				streambuffer.rewind();
				FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
				String dst = fsis.getFixedLengthString(fixedLength, wantedCharsetDecoder).trim();

				assertEquals(src, dst);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

	}

	@Test
	public void testPutStringAll_theParameterStr_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		/*
		 * CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		 * 
		 * Charset wantedCharset = Charset.forName("EUC-KR"); CharsetEncoder
		 * wantedCharsetEncoder = wantedCharset.newEncoder(); CharsetDecoder
		 * wantedCharsetDecoder = wantedCharset.newDecoder();
		 */

		String src = null;

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			fsos.putStringAll(src);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			String expectedMessage = "the parameter src is null";
			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutStringAll_greaterThanNumberOfBytesRemaining() {
		ByteBuffer streambuffer = ByteBuffer.allocate(100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		/*
		 * CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		 * 
		 * Charset wantedCharset = Charset.forName("EUC-KR"); CharsetEncoder
		 * wantedCharsetEncoder = wantedCharset.newEncoder(); CharsetDecoder
		 * wantedCharsetDecoder = wantedCharset.newDecoder();
		 */

		String src = "한글";

		int numberOfBytesRemaining = 0;
		int numberOfBytesRequired = src.getBytes(streamCharset).length;

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			fsos.skip(streambuffer.remaining() - 1);
			numberOfBytesRemaining = streambuffer.remaining();
			fsos.putStringAll(src);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			String expectedMessage = String.format(
					"the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

	@Test
	public void testPutStringAll_charsetVerification() {
		ByteBuffer streambuffer = ByteBuffer.allocate(100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		Charset wantedCharset = Charset.forName("EUC-KR");
		// CharsetEncoder wantedCharsetEncoder = wantedCharset.newEncoder();
		// CharsetDecoder wantedCharsetDecoder = wantedCharset.newDecoder();

		{
			log.info("test case::public void putStringAll(String src)::default charset verification");

			streambuffer.clear();
			String src = "가나다라";

			try {
				byte[] expectedValues = src.getBytes(streamCharset);

				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putStringAll(src);

				assertEquals(expectedValues.length, fsos.size());

				streambuffer.flip();
				byte[] actualsValues = new byte[expectedValues.length];
				streambuffer.get(actualsValues);

				Assert.assertArrayEquals(expectedValues, actualsValues);

			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			log.info(
					"test case::public void putStringAll(String src, Charset wantedCharset)::custom charset verification");

			streambuffer.clear();
			String src = "가나다라";

			try {
				byte[] expectedValues = src.getBytes(wantedCharset);

				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putStringAll(src, wantedCharset);

				assertEquals(expectedValues.length, fsos.size());

				streambuffer.flip();
				byte[] actualsValues = new byte[expectedValues.length];
				streambuffer.get(actualsValues);

				Assert.assertArrayEquals(expectedValues, actualsValues);

			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
	}

	
	@Test
	public void testPutUBPascalString_theParameterSrc_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		Charset wantedCharset = Charset.forName("EUC-KR");

		String src = null;

		{
			log.info("test case::public void putUBPascalString(String src)::the parameter src is null");

			streambuffer.clear();

			try {

				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putUBPascalString(src);
				
				fail("no IllegalArgumentException");
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter src is null";
				assertEquals(expectedMessage, errorMessage);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			log.info(
					"test case::public void putUBPascalString(String src, Charset wantedCharset)::the parameter src is null");

			streambuffer.clear();			

			try {
				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putUBPascalString(src, wantedCharset);
				
				fail("no IllegalArgumentException");
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter src is null";
				assertEquals(expectedMessage, errorMessage);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
	}
	
	@Test
	public void testPutUBPascalString_theParameterWantedCharset_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		Charset wantedCharset = null;

		String src = "한글";		

		{
			streambuffer.clear();			

			try {
				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putUBPascalString(src, wantedCharset);
				
				fail("no IllegalArgumentException");
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter wantedCharset is null";
				assertEquals(expectedMessage, errorMessage);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
	}
	
	@Test
	public void testPutUBPascalString_charsetVerification() {
		ByteBuffer streambuffer = ByteBuffer.allocate(100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		Charset wantedCharset = Charset.forName("EUC-KR");

		final int numberOfBytesLength = 1;

		{
			log.info("test case::public void putUBPascalString(String src)::default charset verification");

			streambuffer.clear();
			String src = "가나다라";

			try {
				byte[] expectedValues = src.getBytes(streamCharset);

				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putUBPascalString(src);

				assertEquals(expectedValues.length + numberOfBytesLength, fsos.size());

				streambuffer.flip();
				streambuffer.position(streambuffer.position() + numberOfBytesLength);

				byte[] actualsValues = new byte[expectedValues.length];
				streambuffer.get(actualsValues);

				Assert.assertArrayEquals(expectedValues, actualsValues);

			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			log.info(
					"test case::public void putUBPascalString(String src, Charset wantedCharset)::custom charset verification");

			streambuffer.clear();
			String src = "가나다라";

			try {
				byte[] expectedValues = src.getBytes(wantedCharset);

				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putUBPascalString(src, wantedCharset);

				assertEquals(expectedValues.length + numberOfBytesLength, fsos.size());

				streambuffer.flip();
				streambuffer.position(streambuffer.position() + numberOfBytesLength);
				byte[] actualsValues = new byte[expectedValues.length];
				streambuffer.get(actualsValues);

				Assert.assertArrayEquals(expectedValues, actualsValues);

			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
	}
	
	@Test
	public void testPutUBPascalString_theParameterSrc_theLengthOfSrcBytesIsGreaterThanMaxOfUnsignedByte() {
		ByteBuffer streambuffer = ByteBuffer.allocate(CommonStaticFinalVars.UNSIGNED_SHORT_MAX+100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();		
			
		StringBuilder srcStringBuilder = new StringBuilder();
		
		for (int i=0; i < CommonStaticFinalVars.UNSIGNED_BYTE_MAX; i++) {
			srcStringBuilder.append("a");
		}
		
		String src = srcStringBuilder.toString(); 

		try {

			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			fsos.putUBPascalString(src);
			
			srcStringBuilder.append("a");
			src = srcStringBuilder.toString(); 
			
			streambuffer.clear();
			fsos.putUBPascalString(src);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the length[%d] of bytes encoding the parameter src as a charset[%s] is greater than the unsigned byte max[%d]", 
					src.getBytes(streamCharset).length, streamCharset.name(),
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX);
			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}		
	}
	
	@Test
	public void testPutUBPascalString_theParameterSrc_theLengthOfSrcBytesIsGreaterThanNumberOfBytesRemaing() {
		ByteBuffer streambuffer = ByteBuffer.allocate(20);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		
		long numberOfBytesRemaining=0;
		int numberOfBytesRequired=0;
		final int numberOfBytesLength = 1;
		
		StringBuilder srcStringBuilder = new StringBuilder();
		
		for (int i=0; i < (streambuffer.remaining() - numberOfBytesLength); i++) {
			srcStringBuilder.append("a");
		}
		
		String src = srcStringBuilder.toString();

		try {

			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			fsos.putUBPascalString(src);
			
			srcStringBuilder.append("a");
			src = srcStringBuilder.toString(); 
			
			streambuffer.clear();
			numberOfBytesRequired = src.getBytes(streamCharset).length+numberOfBytesLength;
			numberOfBytesRemaining = fsos.remaining();
			fsos.putUBPascalString(src);
			
			fail("no SinnoriBufferOverflowException");
		} catch(SinnoriBufferOverflowException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage);
			String expectedMessage = String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}	
	}
	
	@Test
	public void testPutUBPascalString_basic() {
		Charset streamCharset = Charset.forName("EUC-KR");
		Charset wantedCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		ByteBuffer streambuffer = ByteBuffer.allocate(100);
		
		String src = "한글";
		String actualValue = null;
		
		FixedSizeOutputStream fsos = null;
		FixedSizeInputStream fsis = null;
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		
		for (ByteOrder streamByteOrder : streamByteOrderList) {
			log.info("test case::public void putUBPascalString(String src)::streamByteOrder={}::basic", streamByteOrder);
			
			streambuffer.clear();
			try {
				fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				
				fsos.putUBPascalString(src);
				
				
				streambuffer.flip();
				fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);

				//log.info("fsis.available={}", fsis.available());
				
				actualValue = fsis.getUBPascalString();				

				assertEquals(src, actualValue);
				
				long numberOfRemaingBytes = fsis.available();
				if (0 != numberOfRemaingBytes) {
					fail("the input stream has one more byte");
				}
				
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			} finally {
				if (null != fsos) {
					fsos.close();
				}
			}
		}
		
		for (ByteOrder streamByteOrder : streamByteOrderList) {
			log.info("test case::public void putUBPascalString(String src, Charset wantedCharset)::streamByteOrder={}::basic", streamByteOrder);
			
			streambuffer.clear();
			
			try {
				fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				
				fsos.putUBPascalString(src, wantedCharset);
				
				
				streambuffer.flip();
				fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);

				//log.info("fsis.available={}", fsis.available());
				
				actualValue = fsis.getUBPascalString(wantedCharset);				

				assertEquals(src, actualValue);
				
				long numberOfRemaingBytes = fsis.available();
				if (0 != numberOfRemaingBytes) {
					fail("the input stream has one more byte");
				}
				
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			} finally {
				if (null != fsos) {
					fsos.close();
				}
			}
		}
	}
	
	@Test
	public void testPutUSPascalString_theParameterSrc_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		Charset wantedCharset = Charset.forName("EUC-KR");

		String src = null;

		{
			log.info("test case::public void putUSPascalString(String src)::the parameter src is null");

			streambuffer.clear();

			try {

				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putUSPascalString(src);
				
				fail("no IllegalArgumentException");
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter src is null";
				assertEquals(expectedMessage, errorMessage);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			log.info(
					"test case::public void putUSPascalString(String src, Charset wantedCharset)::the parameter src is null");

			streambuffer.clear();			

			try {
				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putUSPascalString(src, wantedCharset);
				
				fail("no IllegalArgumentException");
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter src is null";
				assertEquals(expectedMessage, errorMessage);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
	}
	
	@Test
	public void testPutUSPascalString_theParameterWantedCharset_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		Charset wantedCharset = null;

		String src = "한글";		

		{
			streambuffer.clear();			

			try {
				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putUSPascalString(src, wantedCharset);
				
				fail("no IllegalArgumentException");
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter wantedCharset is null";
				assertEquals(expectedMessage, errorMessage);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
	}

	@Test
	public void testPutUSPascalString_charsetVerification() {
		ByteBuffer streambuffer = ByteBuffer.allocate(100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		Charset wantedCharset = Charset.forName("EUC-KR");

		final int numberOfBytesLength = 2;

		{
			log.info("test case::public void putUSPascalString(String src)::default charset verification");

			streambuffer.clear();
			String src = "가나다라";

			try {
				byte[] expectedValues = src.getBytes(streamCharset);

				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putUSPascalString(src);

				assertEquals(expectedValues.length + numberOfBytesLength, fsos.size());

				streambuffer.flip();
				streambuffer.position(streambuffer.position() + numberOfBytesLength);

				byte[] actualsValues = new byte[expectedValues.length];
				streambuffer.get(actualsValues);

				Assert.assertArrayEquals(expectedValues, actualsValues);

			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			log.info(
					"test case::public void putUSPascalString(String src, Charset wantedCharset)::custom charset verification");

			streambuffer.clear();
			String src = "가나다라";

			try {
				byte[] expectedValues = src.getBytes(wantedCharset);

				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putUSPascalString(src, wantedCharset);

				assertEquals(expectedValues.length + numberOfBytesLength, fsos.size());

				streambuffer.flip();
				streambuffer.position(streambuffer.position() + numberOfBytesLength);
				byte[] actualsValues = new byte[expectedValues.length];
				streambuffer.get(actualsValues);

				Assert.assertArrayEquals(expectedValues, actualsValues);

			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
	}
	
	@Test
	public void testPutUSPascalString_theParameterSrc_theLengthOfSrcBytesIsGreaterThanMaxOfUnsignedByte() {
		ByteBuffer streambuffer = ByteBuffer.allocate(CommonStaticFinalVars.UNSIGNED_SHORT_MAX+100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();		
			
		StringBuilder srcStringBuilder = new StringBuilder();
		
		for (int i=0; i < CommonStaticFinalVars.UNSIGNED_SHORT_MAX; i++) {
			srcStringBuilder.append("a");
		}
		
		String src = srcStringBuilder.toString(); 

		try {

			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			fsos.putUSPascalString(src);
			
			srcStringBuilder.append("a");
			src = srcStringBuilder.toString(); 
			
			streambuffer.clear();
			fsos.putUSPascalString(src);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the length[%d] of bytes encoding the parameter src as a charset[%s] is greater than the unsigned short max[%d]", 
					src.getBytes(streamCharset).length, streamCharset.name(),
					CommonStaticFinalVars.UNSIGNED_SHORT_MAX);
			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}
	
	@Test
	public void testPutUSPascalString_theParameterSrc_theLengthOfSrcBytesIsGreaterThanNumberOfBytesRemaing() {
		ByteBuffer streambuffer = ByteBuffer.allocate(20);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		
		long numberOfBytesRemaining=0;
		int numberOfBytesRequired=0;
		final int numberOfBytesLength = 2;
		
		StringBuilder srcStringBuilder = new StringBuilder();
		
		for (int i=0; i < (streambuffer.remaining() - numberOfBytesLength); i++) {
			srcStringBuilder.append("a");
		}
		
		String src = srcStringBuilder.toString();

		try {

			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			fsos.putUSPascalString(src);
			
			srcStringBuilder.append("a");
			src = srcStringBuilder.toString(); 
			
			streambuffer.clear();
			numberOfBytesRequired = src.getBytes(streamCharset).length+numberOfBytesLength;
			numberOfBytesRemaining = fsos.remaining();
			fsos.putUSPascalString(src);
			
			fail("no SinnoriBufferOverflowException");
		} catch(SinnoriBufferOverflowException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage);
			String expectedMessage = String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}	
	}
	
	@Test
	public void testPutUSPascalString_basic() {
		Charset streamCharset = Charset.forName("EUC-KR");
		Charset wantedCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		ByteBuffer streambuffer = ByteBuffer.allocate(100);
		
		String src = "한글";
		String actualValue = null;
		
		FixedSizeOutputStream fsos = null;
		FixedSizeInputStream fsis = null;
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		
		for (ByteOrder streamByteOrder : streamByteOrderList) {
			log.info("test case::public void putUSPascalString(String src)::streamByteOrder={}::basic", streamByteOrder);
			
			streambuffer.clear();
			try {
				fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				
				fsos.putUSPascalString(src);
				
				
				streambuffer.flip();
				fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);

				//log.info("fsis.available={}", fsis.available());
				
				actualValue = fsis.getUSPascalString();				

				assertEquals(src, actualValue);
				
				long numberOfRemaingBytes = fsis.available();
				if (0 != numberOfRemaingBytes) {
					fail("the input stream has one more byte");
				}
				
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			} finally {
				if (null != fsos) {
					fsos.close();
				}
			}
		}
		
		for (ByteOrder streamByteOrder : streamByteOrderList) {
			log.info("test case::public void putUSPascalString(String src, Charset wantedCharset)::streamByteOrder={}::basic", streamByteOrder);
			
			streambuffer.clear();
			
			try {
				fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				
				fsos.putUSPascalString(src, wantedCharset);
				
				
				streambuffer.flip();
				fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);

				//log.info("fsis.available={}", fsis.available());
				
				actualValue = fsis.getUSPascalString(wantedCharset);				

				assertEquals(src, actualValue);
				
				long numberOfRemaingBytes = fsis.available();
				if (0 != numberOfRemaingBytes) {
					fail("the input stream has one more byte");
				}
				
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			} finally {
				if (null != fsos) {
					fsos.close();
				}
			}
		}
	}
	
	
	
	@Test
	public void testPutSIPascalString_theParameterSrc_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		Charset wantedCharset = Charset.forName("EUC-KR");

		String src = null;

		{
			log.info("test case::public void putSIPascalString(String src)::the parameter src is null");

			streambuffer.clear();

			try {

				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putSIPascalString(src);
				
				fail("no IllegalArgumentException");
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter src is null";
				assertEquals(expectedMessage, errorMessage);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			log.info(
					"test case::public void putSIPascalString(String src, Charset wantedCharset)::the parameter src is null");

			streambuffer.clear();			

			try {
				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putSIPascalString(src, wantedCharset);
				
				fail("no IllegalArgumentException");
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter src is null";
				assertEquals(expectedMessage, errorMessage);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
	}
	
	@Test
	public void testPutSIPascalString_theParameterWantedCharset_null() {
		ByteBuffer streambuffer = ByteBuffer.allocate(100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		Charset wantedCharset = null;

		String src = "한글";		

		{
			streambuffer.clear();			

			try {
				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putSIPascalString(src, wantedCharset);
				
				fail("no IllegalArgumentException");
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter wantedCharset is null";
				assertEquals(expectedMessage, errorMessage);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
	}
	
	@Test
	public void testPutSIPascalString_charsetVerification() {
		ByteBuffer streambuffer = ByteBuffer.allocate(100);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		Charset wantedCharset = Charset.forName("EUC-KR");

		final int numberOfBytesLength = 4;

		{
			log.info("test case::public void putSIPascalString(String src)::default charset verification");

			streambuffer.clear();
			String src = "가나다라";

			try {
				byte[] expectedValues = src.getBytes(streamCharset);

				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putSIPascalString(src);

				assertEquals(expectedValues.length + numberOfBytesLength, fsos.size());

				streambuffer.flip();
				streambuffer.position(streambuffer.position() + numberOfBytesLength);

				byte[] actualsValues = new byte[expectedValues.length];
				streambuffer.get(actualsValues);

				Assert.assertArrayEquals(expectedValues, actualsValues);

			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			log.info(
					"test case::public void putSIPascalString(String src, Charset wantedCharset)::custom charset verification");

			streambuffer.clear();
			String src = "가나다라";

			try {
				byte[] expectedValues = src.getBytes(wantedCharset);

				FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				fsos.putSIPascalString(src, wantedCharset);

				assertEquals(expectedValues.length + numberOfBytesLength, fsos.size());

				streambuffer.flip();
				streambuffer.position(streambuffer.position() + numberOfBytesLength);
				byte[] actualsValues = new byte[expectedValues.length];
				streambuffer.get(actualsValues);

				Assert.assertArrayEquals(expectedValues, actualsValues);

			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
	}
	
	
	@Test
	public void testPutSIPascalString_theParameterSrc_theLengthOfSrcBytesIsGreaterThanNumberOfBytesRemaing() {
		ByteBuffer streambuffer = ByteBuffer.allocate(20);

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		
		long numberOfBytesRemaining=0;
		int numberOfBytesRequired=0;
		final int numberOfBytesLength = 4;
		
		StringBuilder srcStringBuilder = new StringBuilder();
		
		for (int i=0; i < (streambuffer.remaining() - numberOfBytesLength); i++) {
			srcStringBuilder.append("a");
		}
		
		String src = srcStringBuilder.toString();

		try {

			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
			fsos.putSIPascalString(src);
			
			srcStringBuilder.append("a");
			src = srcStringBuilder.toString(); 
			
			streambuffer.clear();
			numberOfBytesRequired = src.getBytes(streamCharset).length+numberOfBytesLength;
			numberOfBytesRemaining = fsos.remaining();
			fsos.putSIPascalString(src);
			
			fail("no SinnoriBufferOverflowException");
		} catch(SinnoriBufferOverflowException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage);
			String expectedMessage = String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}	
	}
	
	@Test
	public void testPutSIPascalString_basic() {
		Charset streamCharset = Charset.forName("EUC-KR");
		Charset wantedCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		ByteBuffer streambuffer = ByteBuffer.allocate(100);
		
		String src = "한글";
		String actualValue = null;
		
		FixedSizeOutputStream fsos = null;
		FixedSizeInputStream fsis = null;
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		
		for (ByteOrder streamByteOrder : streamByteOrderList) {
			log.info("test case::public void putSIPascalString(String src)::streamByteOrder={}::basic", streamByteOrder);
			
			streambuffer.clear();
			try {
				fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				
				fsos.putSIPascalString(src);
				
				
				streambuffer.flip();
				fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);

				//log.info("fsis.available={}", fsis.available());
				
				actualValue = fsis.getSIPascalString();				

				assertEquals(src, actualValue);
				
				long numberOfRemaingBytes = fsis.available();
				if (0 != numberOfRemaingBytes) {
					fail("the input stream has one more byte");
				}
				
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			} finally {
				if (null != fsos) {
					fsos.close();
				}
			}
		}
		
		for (ByteOrder streamByteOrder : streamByteOrderList) {
			log.info("test case::public void putSIPascalString(String src, Charset wantedCharset)::streamByteOrder={}::basic", streamByteOrder);
			
			streambuffer.clear();
			
			try {
				fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
				
				fsos.putSIPascalString(src, wantedCharset);
				
				
				streambuffer.flip();
				fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);

				//log.info("fsis.available={}", fsis.available());
				
				actualValue = fsis.getSIPascalString(wantedCharset);				

				assertEquals(src, actualValue);
				
				long numberOfRemaingBytes = fsis.available();
				if (0 != numberOfRemaingBytes) {
					fail("the input stream has one more byte");
				}
				
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			} finally {
				if (null != fsos) {
					fsos.close();
				}
			}
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

		/** 참고 : "똠방각하" 문자열은 UTF-8 기준 12 bytes 길이를 갖는다 */
		int length = 12;
		String src = "똠방각하";
		String expectedValue = "방각하";

		streambuffer.clear();
		Arrays.fill(streambuffer.array(), CommonStaticFinalVars.ZERO_BYTE);

		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);

			fsos.putFixedLengthString(length, src, wantedCharsetEncoder);
			if (length != streambuffer.position()) {
				fail(String.format(
						"1.the parameter length[%d] is different from the the length[%d] written in stream buffer",
						length, streambuffer.position()));
			}

			streambuffer.clear();
			fsos.skip(3);

			FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
			String actualValue = fsis.getFixedLengthString(length, wantedCharsetDecoder);

			assertEquals(expectedValue, actualValue.trim());

			if (length != (streambuffer.position() - 3)) {
				fail(String.format(
						"2.the parameter length[%d] is different from the the length[%d] read in stream buffer", length,
						streambuffer.position() - 3));
			}

		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
	}

}
