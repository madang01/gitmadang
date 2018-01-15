package kr.pe.sinnori.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;

import org.junit.Assert;
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

public class FreeSizeOutputStreamTest {
	Logger log = null;

	@Before
	public void setup() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_base";
		LOG_TYPE logType = LOG_TYPE.SERVER;
		String logbackConfigFilePathString = BuildSystemPathSupporter
				.getLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		String sinnoriLogPathString = BuildSystemPathSupporter.getLogPathString(sinnoriInstalledPathString,
				mainProjectName, logType);

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH, sinnoriLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);

		// SinnoriLogbackManger.getInstance().setup(sinnoriInstalledPathString,
		// mainProjectName, logType);

		log = LoggerFactory.getLogger(FreeSizeOutputStreamTest.class);
	}

	@Test
	public void testConstructor_theParameterDataPacketBufferMaxCount_lessThanOrEqualToZero() {
		int dataPacketBufferMaxCount = 0;
		CharsetEncoder streamCharsetEncoder = null;
		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;

		FreeSizeOutputStream fsos = null;
		try {

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = String.format(
					"the parameter dataPacketBufferMaxCount[%d] is less than or equal to zero",
					dataPacketBufferMaxCount);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != fsos) {
				fsos.close();
			}
		}

		dataPacketBufferMaxCount = -1;

		try {

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = String.format(
					"the parameter dataPacketBufferMaxCount[%d] is less than or equal to zero",
					dataPacketBufferMaxCount);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != fsos) {
				fsos.close();
			}
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void testConstructor_theParameterDataPacketBufferMaxCount_지정한최대버퍼수만큼만데이터를저장할수있는지점검() {
		int dataPacketBufferMaxCount = 2;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		// FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}

		byte[] expectedValue = { 0x11, 0x22, 0x33 };
		byte actualValue[] = new byte[expectedValue.length];

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.putBytes(expectedValue);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format(
					"this output stream is full. maximum number of data packet buffers=[%d]", dataPacketBufferMaxCount);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			log.warn("", e);
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != fsos) {
				fsos.close();
			}

			dataPacketBufferPoolManager.checkMissingWrapBufferExist();
		}
	}

	@Test
	public void testConstructor_theParameterStreamCharsetEncoder_null() {
		int dataPacketBufferMaxCount = 10;
		CharsetEncoder streamCharsetEncoder = null;
		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;

		FreeSizeOutputStream fsos = null;
		try {

			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = "the parameter streamCharsetEncoder is null";

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;

		FreeSizeOutputStream fsos = null;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = "the parameter dataPacketBufferQueueManager is null";

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			log.warn("", e);
			fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 4096;
		int dataPacketBufferPoolSize = 1000;

		byte actualValue = (byte) 0;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		byte byteTypeExpectedValueList[] = { Byte.MIN_VALUE, Byte.MAX_VALUE, 0, Byte.MAX_VALUE / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				fail("error");
			}

			for (byte expectedValue : byteTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putByte(expectedValue);

					if (fsos.getOutputStreamSize() != 1L) {
						String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not one",
								fsos.getOutputStreamSize());
						fail(errorMessage);
					}

					ArrayList<WrapBuffer> wrapBufferList = fsos.getFlippedWrapBufferList();

					WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
					ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
					/** warning! the duplicate method doesn't copy the byte order attribute */
					dupBuffer.order(streamByteOrder);

					actualValue = dupBuffer.get();

					assertEquals(expectedValue, actualValue);

					// workingWrapBuffer.getByteBuffer().rewind();

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getByte();

					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

				} catch (Exception e) {
					fail("unknown error::" + e.getMessage());
				} finally {
					if (null != fsos) {
						fsos.close();
					}

					dataPacketBufferPoolManager.checkMissingWrapBufferExist();
				}
			}

		}

	}

	@Test
	public void testPutUnsignedByte_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		short actualValue = 0;

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		short shortTypeExpectedValueList[] = { 0, CommonStaticFinalVars.UNSIGNED_BYTE_MAX,
				CommonStaticFinalVars.UNSIGNED_BYTE_MAX / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				fail("error");
			}

			for (short expectedValue : shortTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putUnsignedByte(expectedValue);

					if (fsos.getOutputStreamSize() != 1) {
						String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not one",
								fsos.getOutputStreamSize());
						fail(errorMessage);
					}

					ArrayList<WrapBuffer> wrapBufferList = fsos.getFlippedWrapBufferList();

					WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
					ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
					/** warning! the duplicate method doesn't copy the byte order attribute */
					dupBuffer.order(streamByteOrder);

					actualValue = (short) (0xff & dupBuffer.get());

					log.info("1.expectedValue=0x{}, actualValue=0x{}", HexUtil.getHexString(expectedValue),
							HexUtil.getHexString(actualValue));

					assertEquals(expectedValue, actualValue);

					// workingWrapBuffer.getByteBuffer().rewind();

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getUnsignedByte();

					log.info("2.expectedValue=0x{}, actualValue=0x{}", HexUtil.getHexString(expectedValue),
							HexUtil.getHexString(actualValue));

					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

				} catch (Exception e) {
					fail("unknown error::" + e.getMessage());
				} finally {
					if (null != fsos) {
						fsos.close();
					}

					dataPacketBufferPoolManager.checkMissingWrapBufferExist();
				}
			}
		}
	}

	@Test
	public void testPutShort_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		short actualValue = 0;

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		short shortTypeExpectedValueList[] = { Short.MIN_VALUE, Short.MAX_VALUE, 0, Short.MAX_VALUE / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				fail("error");
			}

			for (short expectedValue : shortTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putShort(expectedValue);

					if (fsos.getOutputStreamSize() != 2) {
						String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not 2 bytes",
								fsos.getOutputStreamSize());
						fail(errorMessage);
					}

					ArrayList<WrapBuffer> wrapBufferList = fsos.getFlippedWrapBufferList();

					if (fsos.getOutputStreamSize() <= dataPacketBufferSize) {
						/**
						 * 하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증
						 */

						WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** warning! the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = dupBuffer.getShort();

						// log.info("1.expectedValue=0x{}, actualValue=0x{}",
						// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));

						assertEquals(expectedValue, actualValue);
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getShort();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

				} catch (Exception e) {
					log.warn("", e);
					fail("unknown error::" + e.getMessage());
				} finally {
					if (null != fsos) {
						fsos.close();
					}

					dataPacketBufferPoolManager.checkMissingWrapBufferExist();
				}
			}

		}

	}

	@Test
	public void testPutUnsignedShort_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		int actualValue = 0;

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		int integerTypeExpectedValueList[] = { 0, CommonStaticFinalVars.UNSIGNED_SHORT_MAX,
				CommonStaticFinalVars.UNSIGNED_SHORT_MAX / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				fail("error");
			}

			for (int expectedValue : integerTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putUnsignedShort(expectedValue);

					if (fsos.getOutputStreamSize() != 2) {
						String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not 2 bytes",
								fsos.getOutputStreamSize());
						fail(errorMessage);
					}

					ArrayList<WrapBuffer> wrapBufferList = fsos.getFlippedWrapBufferList();

					if (fsos.getOutputStreamSize() <= dataPacketBufferSize) {
						/**
						 * 하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증
						 */

						WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** warning! the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = dupBuffer.getShort() & 0xffff;

						// log.info("1.expectedValue=0x{}, actualValue=0x{}",
						// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));

						assertEquals(expectedValue, actualValue);
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getUnsignedShort();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

				} catch (Exception e) {
					log.warn("", e);
					fail("unknown error::" + e.getMessage());
				} finally {
					if (null != fsos) {
						fsos.close();
					}

					dataPacketBufferPoolManager.checkMissingWrapBufferExist();
				}
			}
		}

	}

	@Test
	public void testPutInt_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		int actualValue = 0;

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		int integerTypeExpectedValueList[] = { Integer.MIN_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				fail("error");
			}

			for (int expectedValue : integerTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putInt(expectedValue);

					if (fsos.getOutputStreamSize() != 4) {
						String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not 4 bytes",
								fsos.getOutputStreamSize());
						fail(errorMessage);
					}

					ArrayList<WrapBuffer> wrapBufferList = fsos.getFlippedWrapBufferList();

					if (fsos.getOutputStreamSize() <= dataPacketBufferSize) {
						/**
						 * 하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증
						 */

						WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** warning! the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = dupBuffer.getInt();

						// log.info("1.expectedValue=0x{}, actualValue=0x{}",
						// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));

						assertEquals(expectedValue, actualValue);
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getInt();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

				} catch (Exception e) {
					log.warn("", e);
					fail("unknown error::" + e.getMessage());
				} finally {
					if (null != fsos) {
						fsos.close();
					}

					dataPacketBufferPoolManager.checkMissingWrapBufferExist();
				}
			}
		}
	}

	@Test
	public void testPutUnsignedInt_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		long actualValue = 0;

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		long longTypeExpectedValueList[] = { 0, CommonStaticFinalVars.UNSIGNED_INTEGER_MAX,
				CommonStaticFinalVars.UNSIGNED_INTEGER_MAX / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				fail("error");
			}

			for (long expectedValue : longTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putUnsignedInt(expectedValue);

					if (fsos.getOutputStreamSize() != 4) {
						String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not 4 bytes",
								fsos.getOutputStreamSize());
						fail(errorMessage);
					}

					ArrayList<WrapBuffer> wrapBufferList = fsos.getFlippedWrapBufferList();

					if (fsos.getOutputStreamSize() <= dataPacketBufferSize) {
						/**
						 * 하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증
						 */

						WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** warning! the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = dupBuffer.getInt() & 0xffffffffL;

						// log.info("1.expectedValue=0x{}, actualValue=0x{}",
						// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));

						assertEquals(expectedValue, actualValue);
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getUnsignedInt();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

				} catch (Exception e) {
					log.warn("", e);
					fail("unknown error::" + e.getMessage());
				} finally {
					if (null != fsos) {
						fsos.close();
					}

					dataPacketBufferPoolManager.checkMissingWrapBufferExist();
				}
			}
		}
	}

	@Test
	public void testPutLong_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;

		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		CharsetUtil.createCharsetDecoder(streamCharset);

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;
		long actualValue = 0;

		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		long longTypeExpectedValueList[] = { Long.MIN_VALUE, Long.MAX_VALUE, 0, Long.MAX_VALUE / 2 };

		for (ByteOrder streamByteOrder : streamByteOrderList) {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				fail("error");
			}

			for (long expectedValue : longTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putLong(expectedValue);

					if (fsos.getOutputStreamSize() != 8) {
						String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not 8 bytes",
								fsos.getOutputStreamSize());
						fail(errorMessage);
					}

					ArrayList<WrapBuffer> wrapBufferList = fsos.getFlippedWrapBufferList();

					if (fsos.getOutputStreamSize() <= dataPacketBufferSize) {
						/**
						 * 하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증
						 */

						WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** warning! the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = dupBuffer.getLong();

						// log.info("1.expectedValue=0x{}, actualValue=0x{}",
						// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));

						assertEquals(expectedValue, actualValue);
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getLong();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);

				} catch (Exception e) {
					log.warn("", e);
					fail("unknown error::" + e.getMessage());
				} finally {
					if (null != fsos) {
						fsos.close();
					}

					dataPacketBufferPoolManager.checkMissingWrapBufferExist();
				}
			}
		}

		System.gc();
	}

	@Test
	public void testPutBytes_theParameterSrc_null() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 2;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		// FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}

		byte[] sourceBytes = null;
		int sourceOffset = -1;
		int sourceLength = -1;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.putBytes(sourceBytes, sourceOffset, sourceLength);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = "the parameter src is null";

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			log.warn("", e);
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != fsos) {
				fsos.close();
			}

			dataPacketBufferPoolManager.checkMissingWrapBufferExist();
		}
	}

	@Test
	public void testPutBytes_theParameterOffset_lessThanZero() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 2;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		// FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}

		byte[] sourceBytes = { 0x11, 0x22, 0x33 };
		// byte actualValue[] = new byte[expectedValue.length];
		int sourceOffset = -1;
		int sourceLength = -1;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.putBytes(sourceBytes, sourceOffset, sourceLength);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = String.format("the parameter offset[%d] is less than zero", sourceOffset);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			log.warn("", e);
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != fsos) {
				fsos.close();
			}

			dataPacketBufferPoolManager.checkMissingWrapBufferExist();
		}
	}

	@Test
	public void testPutBytes_theParameterLength_lessThanZero() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 2;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		// FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}

		byte[] sourceBytes = { 0x11, 0x22, 0x33 };
		// byte actualValue[] = new byte[expectedValue.length];
		int sourceOffset = 0;
		int sourceLength = -1;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.putBytes(sourceBytes, sourceOffset, sourceLength);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = String.format("the parameter length[%d] is less than zero", sourceLength);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			log.warn("", e);
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != fsos) {
				fsos.close();
			}

			dataPacketBufferPoolManager.checkMissingWrapBufferExist();
		}
	}

	@Test
	public void testPutBytes_basic() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 2;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}

		byte[] sourceBytes = { 0x11, 0x22, 0x33 };
		byte destinationBytes[] = new byte[sourceBytes.length];

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.putBytes(sourceBytes);

			long outputStreamSize = fsos.getOutputStreamSize();

			if (outputStreamSize != sourceBytes.length) {
				String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not [%d] bytes",
						outputStreamSize, sourceBytes.length);
				fail(errorMessage);
			}

			ArrayList<WrapBuffer> wrapBufferList = fsos.getFlippedWrapBufferList();

			if (outputStreamSize <= dataPacketBufferSize) {
				/**
				 * 하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증
				 */

				log.info("fsos.size={}, dataPacketBufferSize={}", outputStreamSize, dataPacketBufferSize);

				WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
				ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
				/** warning! the duplicate method doesn't copy the byte order attribute */
				dupBuffer.order(streamByteOrder);

				dupBuffer.get(destinationBytes);

				log.info("1.expectedValue=0x{}, actualValue=0x{}", HexUtil.getHexStringFromByteArray(sourceBytes),
						HexUtil.getHexStringFromByteArray(destinationBytes));

				Assert.assertArrayEquals(sourceBytes, destinationBytes);
			}

			fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
					dataPacketBufferPoolManager);
			// fsis = fsos.getFreeSizeInputStream(streamCharsetDecoder);

			destinationBytes = fsis.getBytes(sourceBytes.length);

			log.info("2.expectedValue=0x{}, actualValue=0x{}", HexUtil.getHexStringFromByteArray(sourceBytes),
					HexUtil.getHexStringFromByteArray(destinationBytes));
			// log.info("FreeSizeInputStream size={}", fsis.position());

			Assert.assertArrayEquals(sourceBytes, destinationBytes);

		} catch (Exception e) {
			log.warn("", e);
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != fsos) {
				fsos.close();
			}

			dataPacketBufferPoolManager.checkMissingWrapBufferExist();
		}
	}

	@Test
	public void testPutBytes_complex() {
		fail("미구현");
	}

	// FIXME!
	@Test
	public void testPutFixedLengthString_theParameterLen_lessThanZero() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		// FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}

		String strStr = "똠방각하";
		int fixedLength = -1;

		Charset wantedCharset = Charset.forName("EUC-KR");
		CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.putFixedLengthString(fixedLength, strStr, wantedCharsetEncoder);	
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = String.format("the parameter fixedLength[%d] is less than zero", fixedLength);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			log.warn("", e);
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != fsos) {
				fsos.close();
			}

			dataPacketBufferPoolManager.checkMissingWrapBufferExist();
		}
	}

	// FIXME!
	@Test
	public void testPutFixedLengthString_theParameterStr_null() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}

		String strStr = "똠방각하";
		String excpectedValue = "?";
		String acualValue = null;
		;

		Charset wantedCharset = Charset.forName("EUC-KR");
		CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.putFixedLengthString(1, strStr, wantedCharsetEncoder);

			excpectedValue = "?";

			long outputStreamSize = fsos.getOutputStreamSize();
			long writtenBytes = fsos.getWrittenBytes();

			if (writtenBytes != outputStreamSize) {
				String errorMessage = String.format("the var outputStreamSize[%d] is different from the var writtenBytes[%d]",
						outputStreamSize, writtenBytes);
				fail(errorMessage);
			}

			if (outputStreamSize != 1) {
				String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not one byte",
						outputStreamSize);
				fail(errorMessage);
			}

			ArrayList<WrapBuffer> wrapBufferList = fsos.getFlippedWrapBufferList();

			if (outputStreamSize <= dataPacketBufferSize) {
				/**
				 * 하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증
				 */

				// log.info("fsos.size={}, dataPacketBufferSize={}", outputStreamSize,
				// dataPacketBufferSize);

				WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
				ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
				/** warning! the duplicate method doesn't copy the byte order attribute */
				dupBuffer.order(streamByteOrder);

				byte temp[] = new byte[(int) outputStreamSize];
				dupBuffer.get(temp);
				acualValue = new String(temp, wantedCharset);

				Assert.assertEquals(excpectedValue, acualValue.trim());

				log.info("ByteBuffer 이용한 데이터 비교 검증 완료");
			}

			fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
					dataPacketBufferPoolManager);
			// fsis = fsos.getFreeSizeInputStream(streamCharsetDecoder);

			byte temp[] = new byte[(int) outputStreamSize];
			fsis.getBytes(temp);
			acualValue = new String(temp, wantedCharset);

			Assert.assertEquals(excpectedValue, acualValue.trim());
			
			long readBytes = fsis.getReadBytes();
			if (readBytes != 1) {
				String errorMessage = String.format("the readBytes[%d] of FreeSizeOutputStream is not one byte",
						readBytes);
				fail(errorMessage);
			}			

			long remainingBytesOfInputStream = fsis.available();
			if (0 != remainingBytesOfInputStream) {
				fail("the input stream is available");
			}
		} catch (Exception e) {
			log.warn("", e);
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != fsos) {
				fsos.close();
			}

			dataPacketBufferPoolManager.checkMissingWrapBufferExist();
		}
	}
}
