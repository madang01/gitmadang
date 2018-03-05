package kr.pe.sinnori.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SinnoriBufferOverflowException;

public class FreeSizeOutputStreamTest extends AbstractJunitTest {
	

	private void checkValidFlippedWrapBufferList(List<WrapBuffer> flippedWrapBufferList) {
		if (null == flippedWrapBufferList) {
			fail("the parameter flippedWrapBufferList is null");
		}
		if (0 == flippedWrapBufferList.size()) {
			fail("the parameter flippedWrapBufferList is empty");
		}

		int flippedWrapBufferListSize = flippedWrapBufferList.size();

		for (int i = 0; i < flippedWrapBufferListSize; i++) {
			WrapBuffer flippedWrapBuffer = flippedWrapBufferList.get(i);

			if (!flippedWrapBuffer.getByteBuffer().hasRemaining()) {
				String errorMessage = String.format("the flippedWrapBufferList index[%d]' buffer has no data", i);
				fail(errorMessage);
			}
		}
	}

	private void checkNumberOfWrittenBytes(final int expectedNumberOfWrittenBytes, FreeSizeOutputStream fsos) {
		long outputStreamSize = fsos.size();
		long numberOfWrittenBytes = fsos.getNumberOfWrittenBytes();

		if (numberOfWrittenBytes != outputStreamSize) {
			String errorMessage = String.format(
					"the var outputStreamSize[%d] is different from the var expectedNumberOfWrittenBytes[%d]",
					outputStreamSize, numberOfWrittenBytes);
			fail(errorMessage);
		}

		if (outputStreamSize != expectedNumberOfWrittenBytes) {
			String errorMessage = String.format(
					"numberOfWrittenBytes[%d] is different from expectedNumberOfWrittenBytes[%d]", numberOfWrittenBytes,
					expectedNumberOfWrittenBytes);
			fail(errorMessage);
		}
	}

	@Test
	public void testConstructor_theParameterDataPacketBufferMaxCount_lessThanOrEqualToZero() {
		int dataPacketBufferMaxCount = 0;
		CharsetEncoder streamCharsetEncoder = null;
		DataPacketBufferPool dataPacketBufferPool = null;

		FreeSizeOutputStream fsos = null;
		try {

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = String.format(
					"the parameter dataPacketBufferMaxCount[%d] is less than or equal to zero",
					dataPacketBufferMaxCount);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		} finally {
			if (null != fsos) {
				fsos.close();
			}
		}

		dataPacketBufferMaxCount = -1;

		try {

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = String.format(
					"the parameter dataPacketBufferMaxCount[%d] is less than or equal to zero",
					dataPacketBufferMaxCount);

			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testConstructor_theParameterStreamCharsetEncoder_null() {
		int dataPacketBufferMaxCount = 10;
		CharsetEncoder streamCharsetEncoder = null;
		DataPacketBufferPool dataPacketBufferPool = null;

		FreeSizeOutputStream fsos = null;
		try {

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = "the parameter streamCharsetEncoder is null";

			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testConstructor_theParameterDataPacketBufferQueueManager_null() {
		int dataPacketBufferMaxCount = 10;

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolIF dataPacketBufferPool = null;

		FreeSizeOutputStream fsos = null;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = "the parameter dataPacketBufferPool is null";

			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testPutByte_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 3;

		byte actualValue = (byte) 0;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		byte byteTypeExpectedValueList[] = { Byte.MIN_VALUE, Byte.MAX_VALUE, 0, Byte.MAX_VALUE / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, 
						streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("error");
			}

			for (byte expectedValue : byteTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPool);

					fsos.putByte(expectedValue);

					checkNumberOfWrittenBytes(1, fsos);

					List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

					checkValidFlippedWrapBufferList(flippedWrapBufferList);

					long outputStreamSize = fsos.size();
					if (outputStreamSize <= dataPacketBufferSize) {
						WrapBuffer workingWrapBuffer = flippedWrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** warning! the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = dupBuffer.get();

						assertEquals(expectedValue, actualValue);

						log.info(
								"test case::public void putByte(byte value)::streamByteOrder={}, expectedValue={}::하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료",
								streamByteOrder, expectedValue);
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList,
							streamCharsetDecoder, dataPacketBufferPool);

					actualValue = fsis.getByte();

					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format(
								"numberOfReadBytes[%d] is different from outputStreamSize[%d]", numberOfReadBytes,
								outputStreamSize);
						fail(errorMessage);
					}

					long numberOfRemaingBytes = fsis.available();
					if (0 != numberOfRemaingBytes) {
						fail("the input stream is not available");
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

	}

	@Test
	public void testPutByte_greaterThanNumberOfBytesRemaining() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;

		byte expectedValue = (byte) 0x31;

		FreeSizeOutputStream fsos = null;

		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		int numberOfBytesRequired = 1;
		int numberOfBytesSkipping = dataPacketBufferSize * dataPacketBufferMaxCount;
		long numberOfBytesRemaining = dataPacketBufferSize * dataPacketBufferMaxCount - numberOfBytesSkipping;

		try {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("error");
			}

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.skip(numberOfBytesSkipping);

			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);

			fsos.putByte(expectedValue);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);

			assertEquals(expectedMessage, errorMessage);

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

	@Test
	public void testPutUnsignedByte_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 1;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		short actualValue = 0;

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		short shortTypeExpectedValueList[] = { 0, CommonStaticFinalVars.UNSIGNED_BYTE_MAX,
				CommonStaticFinalVars.UNSIGNED_BYTE_MAX / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			for (short expectedValue : shortTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPool);

					fsos.putUnsignedByte(expectedValue);

					checkNumberOfWrittenBytes(1, fsos);

					List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

					checkValidFlippedWrapBufferList(flippedWrapBufferList);

					long outputStreamSize = fsos.size();
					if (outputStreamSize <= dataPacketBufferSize) {
						WrapBuffer workingWrapBuffer = flippedWrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = (short) (0xff & dupBuffer.get());

						/*
						 * log.info("1.expectedValue=0x{}, actualValue=0x{}",
						 * HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
						 */

						assertEquals(expectedValue, actualValue);

						log.info(
								"test case::public void putUnsignedByte(short value)::streamByteOrder={}, expectedValue={}::하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료",
								streamByteOrder, expectedValue);
					}

					// workingWrapBuffer.getByteBuffer().rewind();

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList,
							streamCharsetDecoder, dataPacketBufferPool);

					actualValue = fsis.getUnsignedByte();

					/*
					 * log.info("2.expectedValue=0x{}, actualValue=0x{}",
					 * HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					 */

					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format(
								"numberOfReadBytes[%d] is different from outputStreamSize[%d]", numberOfReadBytes,
								outputStreamSize);
						fail(errorMessage);
					}

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
	}

	@Test
	public void testPutUnsignedByte_greaterThanNumberOfBytesRemaining() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;

		short expectedValue = 0x87;

		FreeSizeOutputStream fsos = null;

		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		int numberOfBytesRequired = 1;
		int numberOfBytesSkipping = dataPacketBufferSize * dataPacketBufferMaxCount;
		long numberOfBytesRemaining = dataPacketBufferSize * dataPacketBufferMaxCount - numberOfBytesSkipping;

		try {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("error");
			}

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.skip(numberOfBytesSkipping);

			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);

			fsos.putUnsignedByte(expectedValue);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);
			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);

			assertEquals(expectedMessage, errorMessage);

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

	@Test
	public void testPutShort_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 2;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		short actualValue = 0;

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		short shortTypeExpectedValueList[] = { Short.MIN_VALUE, Short.MAX_VALUE, 0, Short.MAX_VALUE / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			for (short expectedValue : shortTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPool);

					fsos.putShort(expectedValue);

					checkNumberOfWrittenBytes(2, fsos);

					List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

					checkValidFlippedWrapBufferList(flippedWrapBufferList);

					long outputStreamSize = fsos.size();
					if (outputStreamSize <= dataPacketBufferSize) {
						WrapBuffer workingWrapBuffer = flippedWrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = dupBuffer.getShort();

						// log.info("1.expectedValue=0x{}, actualValue=0x{}",
						// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));

						assertEquals(expectedValue, actualValue);

						log.info(
								"test case::public void putShort(short value)::하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList,
							streamCharsetDecoder, dataPacketBufferPool);

					actualValue = fsis.getShort();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format(
								"numberOfReadBytes[%d] is different from outputStreamSize[%d]", numberOfReadBytes,
								outputStreamSize);
						fail(errorMessage);
					}

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

	}

	@Test
	public void testPutShort_greaterThanNumberOfBytesRemaining() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;

		short expectedValue = (short) 0x8713;

		FreeSizeOutputStream fsos = null;

		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		int numberOfBytesRequired = 2;
		int numberOfBytesSkipping = dataPacketBufferSize * dataPacketBufferMaxCount;
		long numberOfBytesRemaining = dataPacketBufferSize * dataPacketBufferMaxCount - numberOfBytesSkipping;

		try {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("error");
			}

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.skip(numberOfBytesSkipping);

			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);

			fsos.putShort(expectedValue);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);

			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);

			assertEquals(expectedMessage, errorMessage);

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

	@Test
	public void testPutUnsignedShort_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 2;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		int actualValue = 0;

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		int integerTypeExpectedValueList[] = { 0, CommonStaticFinalVars.UNSIGNED_SHORT_MAX,
				CommonStaticFinalVars.UNSIGNED_SHORT_MAX / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("error");
			}

			for (int expectedValue : integerTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPool);

					fsos.putUnsignedShort(expectedValue);

					checkNumberOfWrittenBytes(2, fsos);

					List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

					checkValidFlippedWrapBufferList(flippedWrapBufferList);

					long outputStreamSize = fsos.size();
					if (outputStreamSize <= dataPacketBufferSize) {
						WrapBuffer workingWrapBuffer = flippedWrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = dupBuffer.getShort() & 0xffff;

						// log.info("1.expectedValue=0x{}, actualValue=0x{}",
						// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));

						assertEquals(expectedValue, actualValue);

						log.info(
								"test case::public void putUnsignedShort(int value)::하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList,
							streamCharsetDecoder, dataPacketBufferPool);

					actualValue = fsis.getUnsignedShort();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format(
								"numberOfReadBytes[%d] is different from outputStreamSize[%d]", numberOfReadBytes,
								outputStreamSize);
						fail(errorMessage);
					}

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

	}

	@Test
	public void testPutUnsignedShort_greaterThanNumberOfBytesRemaining() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;

		int expectedValue = 0x8713;

		FreeSizeOutputStream fsos = null;

		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		int numberOfBytesRequired = 2;
		int numberOfBytesSkipping = dataPacketBufferSize * dataPacketBufferMaxCount;
		long numberOfBytesRemaining = dataPacketBufferSize * dataPacketBufferMaxCount - numberOfBytesSkipping;

		try {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("error");
			}

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.skip(numberOfBytesSkipping);

			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);

			fsos.putUnsignedShort(expectedValue);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);

			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);

			assertEquals(expectedMessage, errorMessage);

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

	@Test
	public void testPutInt_minMaxMiddle() {
		int dataPacketBufferMaxCount = 4;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 4;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		int actualValue = 0;

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		int integerTypeExpectedValueList[] = { Integer.MIN_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("error");
			}

			for (int expectedValue : integerTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPool);

					fsos.putInt(expectedValue);

					checkNumberOfWrittenBytes(4, fsos);

					List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

					checkValidFlippedWrapBufferList(flippedWrapBufferList);

					long outputStreamSize = fsos.size();
					if (outputStreamSize <= dataPacketBufferSize) {
						WrapBuffer workingWrapBuffer = flippedWrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** warning! the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = dupBuffer.getInt();

						// log.info("1.expectedValue=0x{}, actualValue=0x{}",
						// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));

						assertEquals(expectedValue, actualValue);

						log.info(
								"test case::public void putInt(int value)::하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList,
							streamCharsetDecoder, dataPacketBufferPool);

					actualValue = fsis.getInt();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format(
								"numberOfReadBytes[%d] is different from outputStreamSize[%d]", numberOfReadBytes,
								outputStreamSize);
						fail(errorMessage);
					}

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
	}

	@Test
	public void testPutInt_SinnoriBufferOverflowException() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;

		int expectedValue = 0x76135249;

		FreeSizeOutputStream fsos = null;

		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		int numberOfBytesRequired = 4;
		int numberOfBytesSkipping = dataPacketBufferSize * dataPacketBufferMaxCount - 3;
		long numberOfBytesRemaining = dataPacketBufferSize * dataPacketBufferMaxCount - numberOfBytesSkipping;

		try {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("error");
			}

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.skip(numberOfBytesSkipping);

			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);

			fsos.putInt(expectedValue);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);

			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);

			assertEquals(expectedMessage, errorMessage);

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

	@Test
	public void testPutUnsignedInt_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 4;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		long actualValue = 0;

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		long longTypeExpectedValueList[] = { 0, CommonStaticFinalVars.UNSIGNED_INTEGER_MAX,
				CommonStaticFinalVars.UNSIGNED_INTEGER_MAX / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			for (long expectedValue : longTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPool);

					fsos.putUnsignedInt(expectedValue);

					checkNumberOfWrittenBytes(4, fsos);

					List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

					checkValidFlippedWrapBufferList(flippedWrapBufferList);

					long outputStreamSize = fsos.size();
					if (outputStreamSize <= dataPacketBufferSize) {
						WrapBuffer workingWrapBuffer = flippedWrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** warning! the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = dupBuffer.getInt() & 0xffffffffL;

						// log.info("1.expectedValue=0x{}, actualValue=0x{}",
						// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));

						assertEquals(expectedValue, actualValue);

						log.info(
								"test case::public void putUnsignedInt(long value)::하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList,
							streamCharsetDecoder, dataPacketBufferPool);

					actualValue = fsis.getUnsignedInt();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format(
								"numberOfReadBytes[%d] is different from outputStreamSize[%d]", numberOfReadBytes,
								outputStreamSize);
						fail(errorMessage);
					}

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
	}

	@Test
	public void testPutUnsignedInt_greaterThanNumberOfBytesRemaining() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;

		long expectedValue = 0x76135249L;

		FreeSizeOutputStream fsos = null;

		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		int numberOfBytesRequired = 4;
		int numberOfBytesSkipping = dataPacketBufferSize * dataPacketBufferMaxCount - 3;
		long numberOfBytesRemaining = dataPacketBufferSize * dataPacketBufferMaxCount - numberOfBytesSkipping;

		try {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("error");
			}

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.skip(numberOfBytesSkipping);

			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);

			fsos.putUnsignedInt(expectedValue);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);

			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);

			assertEquals(expectedMessage, errorMessage);

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

	@Test
	public void testPutLong_minMaxMiddle() {
		int dataPacketBufferMaxCount = 8;

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		CharsetUtil.createCharsetDecoder(streamCharset);

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 8;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		long actualValue = 0;

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		long longTypeExpectedValueList[] = { Long.MIN_VALUE, Long.MAX_VALUE, 0, Long.MAX_VALUE / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			for (long expectedValue : longTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPool);

					fsos.putLong(expectedValue);

					checkNumberOfWrittenBytes(8, fsos);

					List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

					checkValidFlippedWrapBufferList(flippedWrapBufferList);

					long outputStreamSize = fsos.size();
					if (outputStreamSize <= dataPacketBufferSize) {
						WrapBuffer workingWrapBuffer = flippedWrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** warning! the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = dupBuffer.getLong();

						// log.info("1.expectedValue=0x{}, actualValue=0x{}",
						// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));

						assertEquals(expectedValue, actualValue);

						log.info(
								"test case::public void putLong(long value)::하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList,
							streamCharsetDecoder, dataPacketBufferPool);

					actualValue = fsis.getLong();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format(
								"numberOfReadBytes[%d] is different from outputStreamSize[%d]", numberOfReadBytes,
								outputStreamSize);
						fail(errorMessage);
					}

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

	}

	@Test
	public void testPutLong_greaterThanNumberOfBytesRemaining() {
		int dataPacketBufferMaxCount = 8;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 15;

		long expectedValue = 0x4762176135249038L;

		FreeSizeOutputStream fsos = null;

		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		int numberOfBytesRequired = 8;
		int numberOfBytesSkipping = dataPacketBufferSize * dataPacketBufferMaxCount - 1;
		long numberOfBytesRemaining = dataPacketBufferSize * dataPacketBufferMaxCount - numberOfBytesSkipping;

		try {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("error");
			}

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.skip(numberOfBytesSkipping);

			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);

			fsos.putLong(expectedValue);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);

			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);

			assertEquals(expectedMessage, errorMessage);

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

	@Test
	public void testPutBytes_theParameterSrc_null() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		// FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		{
			log.info("test case::public void putBytes(byte[] src)::the 'src' parameter is null");

			byte[] sourceBytes = null;
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putBytes(sourceBytes);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();

				String expectedMessage = "the parameter src is null";

				assertEquals(expectedMessage, errorMessage);
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

		{
			log.info(
					"test case::public void putBytes(byte[] src, int offset, int length)::the 'src' parameter is null");

			byte[] sourceBytes = null;
			int sourceOffset = -1;
			int sourceLength = -1;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putBytes(sourceBytes, sourceOffset, sourceLength);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();

				String expectedMessage = "the parameter src is null";

				assertEquals(expectedMessage, errorMessage);
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

		{
			log.info("test case::public void putBytes(ByteBuffer src)::the 'src' parameter is null");

			ByteBuffer sourceByteBuffer = null;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putBytes(sourceByteBuffer);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();

				String expectedMessage = "the parameter src is null";

				assertEquals(expectedMessage, errorMessage);
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
	public void testPutBytes_theParameterOffset_lessThanZero() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		// FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		byte[] sourceBytes = { 0x11, 0x22, 0x33 };
		int sourceOffset = -1;
		int sourceLength = -1;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putBytes(sourceBytes, sourceOffset, sourceLength);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = String.format("the parameter offset[%d] is less than zero", sourceOffset);

			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testPutBytes_theParameterOffset_greaterThanOrEqualToArrayLength() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		// FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		byte[] sourceBytes = { 0x11, 0x22, 0x33 };
		// byte actualValue[] = new byte[expectedValue.length];
		int sourceOffset = sourceBytes.length;
		int sourceLength = 1;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putBytes(sourceBytes, sourceOffset, sourceLength);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = String.format(
					"the parameter offset[%d] is greater than or equal to array.length[%d]", sourceOffset,
					sourceBytes.length);

			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testPutBytes_theParameterLength_lessThanZero() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 2;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		// FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		byte[] sourceBytes = { 0x11, 0x22, 0x33 };
		// byte actualValue[] = new byte[expectedValue.length];
		int sourceOffset = 0;
		int sourceLength = -1;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putBytes(sourceBytes, sourceOffset, sourceLength);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = String.format("the parameter length[%d] is less than zero", sourceLength);

			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testPutBytes_sumOftheParameterOffsetAndtheParameterLength_greaterThanArrayLength() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 2;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		// FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		byte[] sourceBytes = { 0x11, 0x22, 0x33 };
		// byte actualValue[] = new byte[expectedValue.length];
		int sourceOffset = sourceBytes.length - 1;
		int sourceLength = 2;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putBytes(sourceBytes, sourceOffset, sourceLength);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// log.warn(""+e.getMessage(), e);

			String errorMessage = e.getMessage();

			String expectedMessage = String.format(
					"the sum[%d] of the parameter offset[%d] and the parameter length[%d] is greater than array.length[%d]",
					sourceOffset + sourceLength, sourceOffset, sourceLength, sourceBytes.length);

			assertEquals(expectedMessage, errorMessage);
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

	@SuppressWarnings("unused")
	@Test
	public void testPutBytes_greaterThanNumberOfBytesRemaining() {
		int dataPacketBufferMaxCount = 2;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		// FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		byte[] expectedValue = { 0x11, 0x22, 0x33 };
		byte actualValue[] = new byte[expectedValue.length];

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putBytes(expectedValue);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					dataPacketBufferMaxCount * dataPacketBufferSize, expectedValue.length);

			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testPutBytes_basic() {
		int dataPacketBufferMaxCount = 4;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 4;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn("" + e.getMessage(), e);
			fail("error");
		}
		{
			log.info("test case::public void putBytes(byte[] src)::ok");

			byte[] sourceBytes = { 0x11, 0x22, 0x33, 0x44 };

			byte destinationBytes[] = new byte[sourceBytes.length];

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putBytes(sourceBytes);

				long outputStreamSize = fsos.size();

				if (outputStreamSize != sourceBytes.length) {
					String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not [%d] bytes",
							outputStreamSize, sourceBytes.length);
					fail(errorMessage);
				}

				List<WrapBuffer> wrapBufferList = fsos.getReadableWrapBufferList();

				if (outputStreamSize <= dataPacketBufferSize) {
					WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
					ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
					/** warning! the duplicate method doesn't copy the byte order attribute */
					dupBuffer.order(streamByteOrder);

					dupBuffer.get(destinationBytes);

					/*
					 * log.info("1.expectedValue=0x{}, actualValue=0x{}",
					 * HexUtil.getHexStringFromByteArray(sourceBytes),
					 * HexUtil.getHexStringFromByteArray(destinationBytes));
					 */

					Assert.assertArrayEquals(sourceBytes, destinationBytes);

					log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
				}

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);
				// fsis = fsos.getFreeSizeInputStream(streamCharsetDecoder);

				destinationBytes = fsis.getBytes(sourceBytes.length);

				Assert.assertArrayEquals(sourceBytes, destinationBytes);

				long numberOfReadBytes = fsis.getNumberOfReadBytes();
				if (numberOfReadBytes != outputStreamSize) {
					String errorMessage = String.format("numberOfReadBytes[%d] is different from outputStreamSize[%d]",
							numberOfReadBytes, outputStreamSize);
					fail(errorMessage);
				}

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

		{
			log.info("test case::public void putBytes(byte[] src, int offset, int length)::ok");

			byte[] sourceBytes = { 0x11, 0x22, 0x33, 0x44 };
			int sourceOffset = 1;
			int sourceLength = 2;
			byte[] expectedBytes = { 0x22, 0x33 };

			byte destinationBytes[] = new byte[sourceLength];

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putBytes(sourceBytes, sourceOffset, sourceLength);

				long outputStreamSize = fsos.size();

				if (outputStreamSize != sourceLength) {
					String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not [%d] bytes",
							outputStreamSize, sourceLength);
					fail(errorMessage);
				}

				List<WrapBuffer> wrapBufferList = fsos.getReadableWrapBufferList();

				if (outputStreamSize <= dataPacketBufferSize) {
					WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
					ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
					/** warning! the duplicate method doesn't copy the byte order attribute */
					dupBuffer.order(streamByteOrder);

					dupBuffer.get(destinationBytes);

					/*
					 * log.info("1.expectedValue=0x{}, actualValue=0x{}",
					 * HexUtil.getHexStringFromByteArray(sourceBytes),
					 * HexUtil.getHexStringFromByteArray(destinationBytes));
					 */

					Assert.assertArrayEquals(expectedBytes, destinationBytes);

					log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
				}

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);
				// fsis = fsos.getFreeSizeInputStream(streamCharsetDecoder);

				destinationBytes = fsis.getBytes(sourceLength);

				Assert.assertArrayEquals(expectedBytes, destinationBytes);

				long numberOfReadBytes = fsis.getNumberOfReadBytes();
				if (numberOfReadBytes != outputStreamSize) {
					String errorMessage = String.format("numberOfReadBytes[%d] is different from outputStreamSize[%d]",
							numberOfReadBytes, outputStreamSize);
					fail(errorMessage);
				}

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

		{
			log.info("test case::public void putBytes(ByteBuffer src)::ok");

			byte[] sourceBytes = { 0x11, 0x22, 0x33, 0x44 };
			ByteBuffer sourceByteBuffer = ByteBuffer.wrap(sourceBytes);

			byte destinationBytes[] = new byte[sourceBytes.length];

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putBytes(sourceByteBuffer);

				long outputStreamSize = fsos.size();

				if (outputStreamSize != sourceBytes.length) {
					String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not [%d] bytes",
							outputStreamSize, sourceBytes.length);
					fail(errorMessage);
				}

				List<WrapBuffer> wrapBufferList = fsos.getReadableWrapBufferList();

				if (outputStreamSize <= dataPacketBufferSize) {
					WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
					ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
					/** warning! the duplicate method doesn't copy the byte order attribute */
					dupBuffer.order(streamByteOrder);

					dupBuffer.get(destinationBytes);

					/*
					 * log.info("1.expectedValue=0x{}, actualValue=0x{}",
					 * HexUtil.getHexStringFromByteArray(sourceBytes),
					 * HexUtil.getHexStringFromByteArray(destinationBytes));
					 */

					Assert.assertArrayEquals(sourceBytes, destinationBytes);

					log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
				}

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);
				// fsis = fsos.getFreeSizeInputStream(streamCharsetDecoder);

				destinationBytes = fsis.getBytes(sourceBytes.length);

				Assert.assertArrayEquals(sourceBytes, destinationBytes);

				long numberOfReadBytes = fsis.getNumberOfReadBytes();
				if (numberOfReadBytes != outputStreamSize) {
					String errorMessage = String.format("numberOfReadBytes[%d] is different from outputStreamSize[%d]",
							numberOfReadBytes, outputStreamSize);
					fail(errorMessage);
				}

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
	public void testPutFixedLengthString_theParameterLen_lessThanZero() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		String strStr = "똠방각하";
		int fixedLength = -1;

		Charset wantedCharset = Charset.forName("EUC-KR");
		CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putFixedLengthString(fixedLength, strStr, wantedCharsetEncoder);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = String.format("the parameter fixedLength[%d] is less than zero", fixedLength);

			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testPutFixedLengthString_theParameterStr_null() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		{
			log.info("test case::public void putFixedLengthString(int fixedLength, "
					+ "String src, CharsetEncoder wantedCharsetEncoder)::the 'src' parameter is null");

			String strStr = null;
			int fixedLength = 1;

			Charset wantedCharset = Charset.forName("EUC-KR");
			CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putFixedLengthString(fixedLength, strStr, wantedCharsetEncoder);
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();

				String expectedMessage = "the parameter src is null";

				assertEquals(expectedMessage, errorMessage);
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

		{
			log.info(
					"test case::public void putFixedLengthString(int fixedLength, String src)::the 'src' parameter is null");

			String strStr = null;
			int fixedLength = 1;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putFixedLengthString(fixedLength, strStr);
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();

				String expectedMessage = "the parameter src is null";

				assertEquals(expectedMessage, errorMessage);
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
	public void testPutFixedLengthString_theParameterWantedCharsetEncoder_null() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		String strStr = "똠방각하";
		int fixedLength = 1;
		CharsetEncoder wantedCharsetEncoder = null;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putFixedLengthString(fixedLength, strStr, wantedCharsetEncoder);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = "the parameter wantedCharsetEncoder is null";

			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testPutFixedLengthString_charsetVerification() {
		int dataPacketBufferMaxCount = 4;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 20;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		Charset wantedCharset = Charset.forName("utf-8");
		CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);
		// CharsetDecoder wantedCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(wantedCharset);

		{
			log.info(
					"test case::public void putFixedLengthString(int fixedLength, String src)::default charset verification");

			String src = "가나다라";
			byte[] excpectedBytes = src.getBytes(streamCharset);
			byte[] actualBytes = null;
			int fixedLength = excpectedBytes.length;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putFixedLengthString(fixedLength, src);

				// log.info("fsos.size={}", fsos.size());

				checkNumberOfWrittenBytes(fixedLength, fsos);

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				// log.info("fsis.available={}", fsis.available());

				actualBytes = fsis.getBytes((int) fsis.available());

				Assert.assertArrayEquals(excpectedBytes, actualBytes);

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

		{
			log.info(
					"test case::public void putFixedLengthString(int fixedLength, String src, CharsetEncoder wantedCharsetEncoder)::custom charset verification");

			String src = "가나다라";
			byte[] excpectedBytes = src.getBytes(wantedCharset);
			byte[] actualBytes = null;
			int fixedLength = excpectedBytes.length;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putFixedLengthString(fixedLength, src, wantedCharsetEncoder);

				// log.info("fsos.size={}", fsos.size());

				checkNumberOfWrittenBytes(fixedLength, fsos);

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				// log.info("fsis.available={}", fsis.available());

				actualBytes = fsis.getBytes((int) fsis.available());

				Assert.assertArrayEquals(excpectedBytes, actualBytes);

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
	public void testPutFixedLengthString_basic() {
		int dataPacketBufferMaxCount = 6;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 6;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn("" + e.getMessage(), e);
			fail("error");
		}

		Charset wantedCharset = Charset.forName("utf-8");
		CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);
		CharsetDecoder wantedCharsetDecoder = CharsetUtil.createCharsetDecoder(wantedCharset);

		{
			log.info("test case::public void putFixedLengthString(int fixedLength, String src)::ok");

			String strStr = "가나다라";
			String excpectedValue = "가나다";
			String actualValue = null;
			int fixedLength = 6;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putFixedLengthString(fixedLength, strStr);

				checkNumberOfWrittenBytes(fixedLength, fsos);

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				long outputStreamSize = fsos.size();

				if (outputStreamSize <= dataPacketBufferSize) {
					WrapBuffer workingWrapBuffer = flippedWrapBufferList.get(0);
					ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
					/** warning! the duplicate method doesn't copy the byte order attribute */
					dupBuffer.order(streamByteOrder);

					byte temp[] = new byte[(int) outputStreamSize];
					dupBuffer.get(temp);
					actualValue = new String(temp, wantedCharset);

					Assert.assertEquals(excpectedValue, actualValue.trim());

					log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
				}

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);
				// fsis = fsos.getFreeSizeInputStream(streamCharsetDecoder);

				/*
				 * byte temp[] = new byte[(int) outputStreamSize]; fsis.getBytes(temp);
				 * actualValue = new String(temp, wantedCharset);
				 * 
				 * Assert.assertEquals(excpectedValue, actualValue.trim());
				 */

				actualValue = fsis.getFixedLengthString(fixedLength);

				Assert.assertEquals(excpectedValue, actualValue.trim());

				long numberOfReadBytes = fsis.getNumberOfReadBytes();
				if (numberOfReadBytes != outputStreamSize) {
					String errorMessage = String.format("numberOfReadBytes[%d] is different from outputStreamSize[%d]",
							numberOfReadBytes, outputStreamSize);
					fail(errorMessage);
				}

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

		{
			log.info(
					"test case::public void putFixedLengthString(int fixedLength, String src, CharsetEncoder wantedCharsetEncoder)::ok");

			String strStr = "가나다라";
			String excpectedValue = "가나";
			String actualValue = null;
			int fixedLength = 6;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putFixedLengthString(fixedLength, strStr, wantedCharsetEncoder);

				checkNumberOfWrittenBytes(fixedLength, fsos);

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				long outputStreamSize = fsos.size();

				if (outputStreamSize <= dataPacketBufferSize) {
					WrapBuffer workingWrapBuffer = flippedWrapBufferList.get(0);
					ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
					/** warning! the duplicate method doesn't copy the byte order attribute */
					dupBuffer.order(streamByteOrder);

					byte temp[] = new byte[(int) outputStreamSize];
					dupBuffer.get(temp);
					actualValue = new String(temp, wantedCharset);

					Assert.assertEquals(excpectedValue, actualValue.trim());

					log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
				}

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);
				// fsis = fsos.getFreeSizeInputStream(streamCharsetDecoder);

				/*
				 * byte temp[] = new byte[(int) outputStreamSize]; fsis.getBytes(temp);
				 * actualValue = new String(temp, wantedCharset);
				 * 
				 * Assert.assertEquals(excpectedValue, actualValue.trim());
				 */

				actualValue = fsis.getFixedLengthString(fixedLength, wantedCharsetDecoder);

				Assert.assertEquals(excpectedValue, actualValue.trim());

				long numberOfReadBytes = fsis.getNumberOfReadBytes();
				if (numberOfReadBytes != outputStreamSize) {
					String errorMessage = String.format("numberOfReadBytes[%d] is different from outputStreamSize[%d]",
							numberOfReadBytes, outputStreamSize);
					fail(errorMessage);
				}

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
	public void testPutStringAll_theParameterStr_null() {
		int dataPacketBufferMaxCount = 6;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 6;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn("" + e.getMessage(), e);
			fail("error");
		}

		String strStr = null;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putStringAll(strStr);

			fail("no Exception");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = "the parameter src is null";

			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testPutStringAll_greaterThanNumberOfBytesRemaining() {
		int dataPacketBufferMaxCount = 6;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 6;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		String strStr = "한글";

		int numberOfBytesRequired = strStr.getBytes(streamCharset).length;
		int numberOfBytesSkipping = dataPacketBufferSize * dataPacketBufferMaxCount - 1;
		long numberOfBytesRemaining = dataPacketBufferSize * dataPacketBufferMaxCount - numberOfBytesSkipping;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.skip(numberOfBytesSkipping);

			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);

			fsos.putStringAll(strStr);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);
			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);

			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testPutStringAll_charsetVerification() {
		int dataPacketBufferMaxCount = 20;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		Charset wantedCharset = Charset.forName("utf-8");
		// CharsetEncoder wantedCharsetEncoder =
		// CharsetUtil.createCharsetEncoder(wantedCharset);
		// CharsetDecoder wantedCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(wantedCharset);

		{
			log.info("test case::public void putStringAll(String src)::default charset verification");

			String src = "가나다라";
			byte[] excpectedBytes = src.getBytes(streamCharset);
			byte[] actualBytes = null;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putStringAll(src);

				// log.info("fsos.size={}", fsos.size());

				checkNumberOfWrittenBytes(excpectedBytes.length, fsos);

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				// log.info("fsis.available={}", fsis.available());

				actualBytes = fsis.getBytes((int) fsis.available());

				Assert.assertArrayEquals(excpectedBytes, actualBytes);

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

		{
			log.info(
					"test case::public void putStringAll(String src, Charset wantedCharset)::custom charset verification");

			String src = "가나다라";
			byte[] excpectedBytes = src.getBytes(wantedCharset);
			byte[] actualBytes = null;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putStringAll(src, wantedCharset);

				// log.info("fsos.size={}", fsos.size());

				checkNumberOfWrittenBytes(excpectedBytes.length, fsos);

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				// log.info("fsis.available={}", fsis.available());

				actualBytes = fsis.getBytes((int) fsis.available());

				Assert.assertArrayEquals(excpectedBytes, actualBytes);

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
	public void testPutUBPascalString_theParameterSrc_null() {
		int dataPacketBufferMaxCount = 20;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		Charset wantedCharset = Charset.forName("utf-8");
		// CharsetEncoder wantedCharsetEncoder =
		// CharsetUtil.createCharsetEncoder(wantedCharset);
		// CharsetDecoder wantedCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(wantedCharset);

		String src = null;

		{
			log.info("test case::public void putUBPascalString(String src)::the parameter src is null");

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUBPascalString(src);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter src is null";
				assertEquals(expectedMessage, errorMessage);
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

		{
			log.info(
					"test case::public void putUBPascalString(String src, Charset wantedCharset)::the parameter src is null");

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUBPascalString(src, wantedCharset);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter src is null";
				assertEquals(expectedMessage, errorMessage);
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
	public void testPutUBPascalString_theParameterWantedCharset_null() {
		int dataPacketBufferMaxCount = 20;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		Charset wantedCharset = null;
		// CharsetEncoder wantedCharsetEncoder =
		// CharsetUtil.createCharsetEncoder(wantedCharset);
		// CharsetDecoder wantedCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(wantedCharset);

		String src = "한글";

		{
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUBPascalString(src, wantedCharset);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter wantedCharset is null";
				assertEquals(expectedMessage, errorMessage);
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
	public void testPutUBPascalString_charsetVerification() {
		int dataPacketBufferMaxCount = 20;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		final int numberOfBytesLength = 1;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		Charset wantedCharset = Charset.forName("utf-8");
		// CharsetEncoder wantedCharsetEncoder =
		// CharsetUtil.createCharsetEncoder(wantedCharset);
		// CharsetDecoder wantedCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(wantedCharset);

		{
			log.info("test case::public void putUBPascalString(String src)::default charset verification");

			String src = "가나다라";
			byte[] excpectedBytes = src.getBytes(streamCharset);
			byte[] actualBytes = null;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUBPascalString(src);

				// log.info("fsos.size={}", fsos.size());

				checkNumberOfWrittenBytes(excpectedBytes.length + numberOfBytesLength, fsos);

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				fsis.skip(numberOfBytesLength);

				// log.info("fsis.available={}", fsis.available());

				actualBytes = fsis.getBytes((int) fsis.available());

				Assert.assertArrayEquals(excpectedBytes, actualBytes);

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

		{
			log.info(
					"test case::public void putUBPascalString(String src, Charset wantedCharset)::custom charset verification");

			String src = "가나다라";
			byte[] excpectedBytes = src.getBytes(wantedCharset);
			byte[] actualBytes = null;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUBPascalString(src, wantedCharset);

				// log.info("fsos.size={}", fsos.size());

				checkNumberOfWrittenBytes(excpectedBytes.length + numberOfBytesLength, fsos);

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				// log.info("fsis.available={}", fsis.available());
				fsis.skip(numberOfBytesLength);

				actualBytes = fsis.getBytes((int) fsis.available());

				Assert.assertArrayEquals(excpectedBytes, actualBytes);

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
	public void testPutUBPascalString_theParameterSrc_theLengthOfSrcBytesIsGreaterThanMaxOfUnsignedByte() {
		int dataPacketBufferMaxCount = 20;
		Charset streamCharset = Charset.forName("UTF-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 4096;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
		
		StringBuilder srcStringBuilder = new StringBuilder();
		
		for (int i=0; i < CommonStaticFinalVars.UNSIGNED_BYTE_MAX; i++) {
			srcStringBuilder.append("a");
		}
		
		String src = srcStringBuilder.toString(); 

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putUBPascalString(src);
			
			fsos.close();
			fsos = null;
			
			srcStringBuilder.append("a");
			src = srcStringBuilder.toString(); 
			
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putUBPascalString(src);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
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
		} finally {
			if (null != fsos) {
				fsos.close();
			}
		}
	}
	
	
	@Test
	public void testPutUBPascalString_theParameterSrc_theLengthOfSrcBytesIsGreaterThanNumberOfBytesRemaing() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("UTF-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		final int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;
		
		long numberOfBytesRemaining=0;
		int numberOfBytesRequired=0;
		final int numberOfBytesLength = 1;
		
		StringBuilder srcStringBuilder = new StringBuilder();
		
		for (int i=0; i < (dataPacketBufferMaxCount - numberOfBytesLength); i++) {
			srcStringBuilder.append("a");
		}
		
		String src = srcStringBuilder.toString();

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putUBPascalString(src);
			
			fsos.close();
			fsos = null;
			
			srcStringBuilder.append("a");
			src = srcStringBuilder.toString(); 
			
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);
			
			
			numberOfBytesRequired = src.getBytes(streamCharset).length+numberOfBytesLength;
			numberOfBytesRemaining = fsos.remaining();

			fsos.putUBPascalString(src);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testPutUBPascalString_basic() {
		int dataPacketBufferMaxCount = 20;
		Charset streamCharset = Charset.forName("EUC-KR");
		Charset wantedCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		String src = "한글";
		String actualValue = null;

		/*
		 * int numberOfBytesRequired = strStr.getBytes(streamCharset).length; int
		 * numberOfBytesSkipping = dataPacketBufferSize*dataPacketBufferMaxCount-1; long
		 * numberOfBytesRemaining = dataPacketBufferSize*dataPacketBufferMaxCount -
		 * numberOfBytesSkipping;
		 */

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			log.info("test case::public void putUBPascalString(String src)::streamByteOrder={}::basic",
					streamByteOrder);

			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUBPascalString(src);

				// log.info("fsos.size={}", fsos.size());

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				// log.info("fsis.available={}", fsis.available());

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
			log.info(
					"test case::public void putUBPascalString(String src, Charset wantedCharset)::streamByteOrder={}::basic",
					streamByteOrder);

			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUBPascalString(src, wantedCharset);

				// log.info("fsos.size={}", fsos.size());

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				// log.info("fsis.available={}", fsis.available());

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
		int dataPacketBufferMaxCount = 20;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		Charset wantedCharset = Charset.forName("utf-8");

		String src = null;

		{
			log.info("test case::public void putUSPascalString(String src)::the parameter src is null");

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUSPascalString(src);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter src is null";
				assertEquals(expectedMessage, errorMessage);
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

		{
			log.info(
					"test case::public void putUSPascalString(String src, Charset wantedCharset)::the parameter src is null");

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUSPascalString(src, wantedCharset);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter src is null";
				assertEquals(expectedMessage, errorMessage);
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
	public void testPutUSPascalString_theParameterWantedCharset_null() {
		int dataPacketBufferMaxCount = 20;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		Charset wantedCharset = null;

		String src = "한글";

		{
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUSPascalString(src, wantedCharset);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter wantedCharset is null";
				assertEquals(expectedMessage, errorMessage);
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
	public void testPutUSPascalString_charsetVerification() {
		int dataPacketBufferMaxCount = 20;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		final int numberOfBytesLength = 2;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		Charset wantedCharset = Charset.forName("utf-8");
		// CharsetEncoder wantedCharsetEncoder =
		// CharsetUtil.createCharsetEncoder(wantedCharset);
		// CharsetDecoder wantedCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(wantedCharset);

		{
			log.info("test case::public void putUSPascalString(String src)::default charset verification");

			String src = "가나다라";
			byte[] excpectedBytes = src.getBytes(streamCharset);
			byte[] actualBytes = null;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUSPascalString(src);

				// log.info("fsos.size={}", fsos.size());

				checkNumberOfWrittenBytes(excpectedBytes.length + numberOfBytesLength, fsos);

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				fsis.skip(numberOfBytesLength);

				// log.info("fsis.available={}", fsis.available());

				actualBytes = fsis.getBytes((int) fsis.available());

				Assert.assertArrayEquals(excpectedBytes, actualBytes);

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

		{
			log.info(
					"test case::public void putUSPascalString(String src, Charset wantedCharset)::custom charset verification");

			String src = "가나다라";
			byte[] excpectedBytes = src.getBytes(wantedCharset);
			byte[] actualBytes = null;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUSPascalString(src, wantedCharset);

				// log.info("fsos.size={}", fsos.size());

				checkNumberOfWrittenBytes(excpectedBytes.length + numberOfBytesLength, fsos);

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				// log.info("fsis.available={}", fsis.available());
				fsis.skip(numberOfBytesLength);

				actualBytes = fsis.getBytes((int) fsis.available());

				Assert.assertArrayEquals(excpectedBytes, actualBytes);

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
	public void testPutUSPascalString_theParameterSrc_theLengthOfSrcBytesIsGreaterThanMaxOfUnsignedShort() {
		int dataPacketBufferMaxCount = 100;
		Charset streamCharset = Charset.forName("UTF-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 4096;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
		
		StringBuilder srcStringBuilder = new StringBuilder();
		
		for (int i=0; i < CommonStaticFinalVars.UNSIGNED_SHORT_MAX; i++) {
			srcStringBuilder.append("a");
		}
		
		String src = srcStringBuilder.toString(); 

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putUSPascalString(src);
			
			fsos.close();
			fsos = null;
			
			srcStringBuilder.append("a");
			src = srcStringBuilder.toString(); 
			
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putUSPascalString(src);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
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
		} finally {
			if (null != fsos) {
				fsos.close();
			}
		}

	}
	
	
	@Test
	public void testPutUSPascalString_theParameterSrc_theLengthOfSrcBytesIsGreaterThanNumberOfBytesRemaing() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("UTF-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		final int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;
		
		long numberOfBytesRemaining=0;
		int numberOfBytesRequired=0;
		final int numberOfBytesLength = 2;
		
		StringBuilder srcStringBuilder = new StringBuilder();
		
		for (int i=0; i < (dataPacketBufferMaxCount - numberOfBytesLength); i++) {
			srcStringBuilder.append("a");
		}
		
		String src = srcStringBuilder.toString();

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putUSPascalString(src);
			
			fsos.close();
			fsos = null;
			
			srcStringBuilder.append("a");
			src = srcStringBuilder.toString(); 
			
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);
			
			
			numberOfBytesRequired = src.getBytes(streamCharset).length+numberOfBytesLength;
			numberOfBytesRemaining = fsos.remaining();

			fsos.putUSPascalString(src);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testPutUSPascalString_basic() {
		int dataPacketBufferMaxCount = 50;
		Charset streamCharset = Charset.forName("EUC-KR");
		Charset wantedCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 4096;
		int dataPacketBufferPoolSize = 100;

		StringBuilder testStringBuilder = new StringBuilder();

		for (int i = 0; i < 10000; i++) {
			testStringBuilder.append("한글");
		}

		String src = testStringBuilder.toString();
		String actualValue = null;

		/*
		 * int numberOfBytesRequired = strStr.getBytes(streamCharset).length; int
		 * numberOfBytesSkipping = dataPacketBufferSize*dataPacketBufferMaxCount-1; long
		 * numberOfBytesRemaining = dataPacketBufferSize*dataPacketBufferMaxCount -
		 * numberOfBytesSkipping;
		 */

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUSPascalString(src);

				// log.info("fsos.size={}", fsos.size());

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				// log.info("fsis.available={}", fsis.available());

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
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putUSPascalString(src, wantedCharset);

				// log.info("fsos.size={}", fsos.size());

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				// log.info("fsis.available={}", fsis.available());

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
		int dataPacketBufferMaxCount = 20;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		Charset wantedCharset = Charset.forName("utf-8");

		String src = null;

		{
			log.info("test case::public void putSIPascalString(String src)::the parameter src is null");

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putSIPascalString(src);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter src is null";
				assertEquals(expectedMessage, errorMessage);
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

		{
			log.info(
					"test case::public void putSIPascalString(String src, Charset wantedCharset)::the parameter src is null");

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putSIPascalString(src, wantedCharset);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter src is null";
				assertEquals(expectedMessage, errorMessage);
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
	public void testPutSIPascalString_theParameterWantedCharset_null() {
		int dataPacketBufferMaxCount = 20;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		Charset wantedCharset = null;

		String src = "한글";

		{
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putSIPascalString(src, wantedCharset);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String expectedMessage = "the parameter wantedCharset is null";
				assertEquals(expectedMessage, errorMessage);
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
	public void testPutSIPascalString_charsetVerification() {
		int dataPacketBufferMaxCount = 20;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		final int numberOfBytesLength = 4;

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		Charset wantedCharset = Charset.forName("utf-8");
		// CharsetEncoder wantedCharsetEncoder =
		// CharsetUtil.createCharsetEncoder(wantedCharset);
		// CharsetDecoder wantedCharsetDecoder =
		// CharsetUtil.createCharsetDecoder(wantedCharset);

		{
			log.info("test case::public void putSIPascalString(String src)::default charset verification");

			String src = "가나다라";
			byte[] excpectedBytes = src.getBytes(streamCharset);
			byte[] actualBytes = null;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putSIPascalString(src);

				// log.info("fsos.size={}", fsos.size());

				checkNumberOfWrittenBytes(excpectedBytes.length + numberOfBytesLength, fsos);

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				fsis.skip(numberOfBytesLength);

				// log.info("fsis.available={}", fsis.available());

				actualBytes = fsis.getBytes((int) fsis.available());

				Assert.assertArrayEquals(excpectedBytes, actualBytes);

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

		{
			log.info(
					"test case::public void putSIPascalString(String src, Charset wantedCharset)::custom charset verification");

			String src = "가나다라";
			byte[] excpectedBytes = src.getBytes(wantedCharset);
			byte[] actualBytes = null;

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putSIPascalString(src, wantedCharset);

				// log.info("fsos.size={}", fsos.size());

				checkNumberOfWrittenBytes(excpectedBytes.length + numberOfBytesLength, fsos);

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				// log.info("fsis.available={}", fsis.available());
				fsis.skip(numberOfBytesLength);

				actualBytes = fsis.getBytes((int) fsis.available());

				Assert.assertArrayEquals(excpectedBytes, actualBytes);

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
	public void testPutSIPascalString_theParameterSrc_theLengthOfSrcBytesIsGreaterThanNumberOfBytesRemaing() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("UTF-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		final int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 50;

		FreeSizeOutputStream fsos = null;
		
		long numberOfBytesRemaining=0;
		int numberOfBytesRequired=0;
		final int numberOfBytesLength = 4;
		
		StringBuilder srcStringBuilder = new StringBuilder();
		
		for (int i=0; i < (dataPacketBufferMaxCount - numberOfBytesLength); i++) {
			srcStringBuilder.append("a");
		}
		
		String src = srcStringBuilder.toString();

		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			String errorMessage = "error::" + e.getMessage();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.putSIPascalString(src);
			
			fsos.close();
			fsos = null;
			
			srcStringBuilder.append("a");
			src = srcStringBuilder.toString(); 
			
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);
			
			
			numberOfBytesRequired = src.getBytes(streamCharset).length+numberOfBytesLength;
			numberOfBytesRemaining = fsos.remaining();

			fsos.putSIPascalString(src);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage);			
			String expectedMessage = String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			assertEquals(expectedMessage, errorMessage);
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

	@Test
	public void testPutSIPascalString_basic() {
		int dataPacketBufferMaxCount = 50;
		Charset streamCharset = Charset.forName("EUC-KR");
		Charset wantedCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 4096;
		int dataPacketBufferPoolSize = 100;

		StringBuilder testStringBuilder = new StringBuilder();

		for (int i = 0; i < 20000; i++) {
			testStringBuilder.append("한글");
		}

		String src = testStringBuilder.toString();
		String actualValue = null;

		/*
		 * int numberOfBytesRequired = strStr.getBytes(streamCharset).length; int
		 * numberOfBytesSkipping = dataPacketBufferSize*dataPacketBufferMaxCount-1; long
		 * numberOfBytesRemaining = dataPacketBufferSize*dataPacketBufferMaxCount -
		 * numberOfBytesSkipping;
		 */

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putSIPascalString(src);

				// log.info("fsos.size={}", fsos.size());

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				// log.info("fsis.available={}", fsis.available());

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
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);

				fsos.putSIPascalString(src, wantedCharset);

				// log.info("fsos.size={}", fsos.size());

				List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

				checkValidFlippedWrapBufferList(flippedWrapBufferList);

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
						dataPacketBufferPool);

				// log.info("fsis.available={}", fsis.available());

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
	public void testSkip_basic() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;

		FreeSizeOutputStream fsos = null;

		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		try {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn("" + e.getMessage(), e);
				fail("error");
			}

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);

			fsos.skip(dataPacketBufferSize * dataPacketBufferMaxCount);

			checkNumberOfWrittenBytes(dataPacketBufferSize * dataPacketBufferMaxCount, fsos);

			List<WrapBuffer> flippedWrapBufferList = fsos.getReadableWrapBufferList();

			checkValidFlippedWrapBufferList(flippedWrapBufferList);
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
