package kr.pe.sinnori.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;

import org.junit.After;
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
	
	
	@After
	public void finish() {
		System.gc();
	}
	
	private void checkValidFlippedWrapBufferList(ArrayList<WrapBuffer> flippedWrapBufferList) {
		if (null == flippedWrapBufferList) {
			fail("the parameter flippedWrapBufferList is null");
		}
		if (0 == flippedWrapBufferList.size()) {
			fail("the parameter flippedWrapBufferList is empty");
		}
		
		int flippedWrapBufferListSize = flippedWrapBufferList.size();
		
		for (int i=0; i < flippedWrapBufferListSize; i++) {
			WrapBuffer flippedWrapBuffer = flippedWrapBufferList.get(i);
			
			if (! flippedWrapBuffer.getByteBuffer().hasRemaining()) {
				String errorMessage = String.format("the flippedWrapBufferList index[%d]' buffer has no data", i);
				fail(errorMessage);
			}
		}
	}
	
	private void checkNumberOfWrittenBytes(final int expectedNumberOfWrittenBytes, FreeSizeOutputStream fsos) {
		long outputStreamSize = fsos.size();
		long numberOfWrittenBytes = fsos.getNumberOfWrittenBytes();

		if (numberOfWrittenBytes != outputStreamSize) {
			String errorMessage = String.format("the var outputStreamSize[%d] is different from the var expectedNumberOfWrittenBytes[%d]",
					outputStreamSize, numberOfWrittenBytes);
			fail(errorMessage);
		}

		if (outputStreamSize != expectedNumberOfWrittenBytes) {
			String errorMessage = String.format("numberOfWrittenBytes[%d] is different from expectedNumberOfWrittenBytes[%d]",
					numberOfWrittenBytes, expectedNumberOfWrittenBytes);
			fail(errorMessage);
		}
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
			log.warn(""+e.getMessage(), e);
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
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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
			log.warn(""+e.getMessage(), e);
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
			log.warn(""+e.getMessage(), e);
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
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 3;

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
				log.warn(""+e.getMessage(), e);
				fail("error");
			}

			for (byte expectedValue : byteTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putByte(expectedValue);
					
					checkNumberOfWrittenBytes(1, fsos);					
					
					ArrayList<WrapBuffer> flippedWrapBufferList = fsos.getFlippedWrapBufferList();
					
					checkValidFlippedWrapBufferList(flippedWrapBufferList);
					
					long outputStreamSize = fsos.size();
					if (outputStreamSize <= dataPacketBufferSize) {
						WrapBuffer workingWrapBuffer = flippedWrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** warning! the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = dupBuffer.get();

						assertEquals(expectedValue, actualValue);
						
						log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getByte();

					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);
					
					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format("numberOfReadBytes[%d] is different from outputStreamSize[%d]",
								numberOfReadBytes, outputStreamSize);
						fail(errorMessage);
					}			

					long numberOfRemaingBytes = fsis.available();
					if (0 != numberOfRemaingBytes) {
						fail("the input stream is available");
					}

				} catch (Exception e) {
					log.warn(""+e.getMessage(), e);
					fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;

		byte expectedValue = (byte) 0x31;

		FreeSizeOutputStream fsos = null;
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		int numberOfBytesRequired = 1;
		int numberOfBytesSkipping = dataPacketBufferSize*dataPacketBufferMaxCount;
		long numberOfBytesRemaining = dataPacketBufferSize*dataPacketBufferMaxCount - numberOfBytesSkipping;
		
		try {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}
			
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.skip(numberOfBytesSkipping);
			
			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);
			
			fsos.putByte(expectedValue);
			
			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			String errorMessage = e.getMessage();
			String expectedMessage =
			String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			
			assertEquals(expectedMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
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
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}

			for (short expectedValue : shortTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putUnsignedByte(expectedValue);

					checkNumberOfWrittenBytes(1, fsos);

					ArrayList<WrapBuffer> flippedWrapBufferList = fsos.getFlippedWrapBufferList();
					
					checkValidFlippedWrapBufferList(flippedWrapBufferList);
					
					long outputStreamSize = fsos.size();
					if (outputStreamSize <= dataPacketBufferSize) {						
						WrapBuffer workingWrapBuffer = flippedWrapBufferList.get(0);
						ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
						/** the duplicate method doesn't copy the byte order attribute */
						dupBuffer.order(streamByteOrder);

						actualValue = (short) (0xff & dupBuffer.get());

						log.info("1.expectedValue=0x{}, actualValue=0x{}", HexUtil.getHexString(expectedValue),
								HexUtil.getHexString(actualValue));

						assertEquals(expectedValue, actualValue);
						
						log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
					}

					

					// workingWrapBuffer.getByteBuffer().rewind();

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getUnsignedByte();

					/*log.info("2.expectedValue=0x{}, actualValue=0x{}", HexUtil.getHexString(expectedValue),
							HexUtil.getHexString(actualValue));*/

					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);
					
					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format("numberOfReadBytes[%d] is different from outputStreamSize[%d]",
								numberOfReadBytes, outputStreamSize);
						fail(errorMessage);
					}			

					long numberOfRemaingBytes = fsis.available();
					if (0 != numberOfRemaingBytes) {
						fail("the input stream is available");
					}

				} catch (Exception e) {
					log.warn(""+e.getMessage(), e);
					fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;

		short expectedValue =  0x87;

		FreeSizeOutputStream fsos = null;
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		int numberOfBytesRequired = 1;
		int numberOfBytesSkipping = dataPacketBufferSize*dataPacketBufferMaxCount;
		long numberOfBytesRemaining = dataPacketBufferSize*dataPacketBufferMaxCount - numberOfBytesSkipping;
		
		try {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}
			
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.skip(numberOfBytesSkipping);
			
			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);
			
			fsos.putUnsignedByte(expectedValue);
			
			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);			
			String errorMessage = e.getMessage();
			String expectedMessage =
			String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			
			assertEquals(expectedMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
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
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}

			for (short expectedValue : shortTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putShort(expectedValue);

					checkNumberOfWrittenBytes(2, fsos);

					ArrayList<WrapBuffer> flippedWrapBufferList = fsos.getFlippedWrapBufferList();
					
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
						
						log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getShort();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);
					
					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format("numberOfReadBytes[%d] is different from outputStreamSize[%d]",
								numberOfReadBytes, outputStreamSize);
						fail(errorMessage);
					}			

					long numberOfRemaingBytes = fsis.available();
					if (0 != numberOfRemaingBytes) {
						fail("the input stream is available");
					}

				} catch (Exception e) {
					log.warn(""+e.getMessage(), e);
					fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;

		short expectedValue =  (short)0x8713;

		FreeSizeOutputStream fsos = null;
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		int numberOfBytesRequired = 2;
		int numberOfBytesSkipping = dataPacketBufferSize*dataPacketBufferMaxCount;
		long numberOfBytesRemaining = dataPacketBufferSize*dataPacketBufferMaxCount - numberOfBytesSkipping;
		
		try {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}
			
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.skip(numberOfBytesSkipping);
			
			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);
			
			fsos.putShort(expectedValue);
			
			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);
			
			String errorMessage = e.getMessage();
			String expectedMessage =
			String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			
			assertEquals(expectedMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
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
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}

			for (int expectedValue : integerTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putUnsignedShort(expectedValue);

					checkNumberOfWrittenBytes(2, fsos);

					ArrayList<WrapBuffer> flippedWrapBufferList = fsos.getFlippedWrapBufferList();
					
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
						
						log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getUnsignedShort();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);
					
					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format("numberOfReadBytes[%d] is different from outputStreamSize[%d]",
								numberOfReadBytes, outputStreamSize);
						fail(errorMessage);
					}			

					long numberOfRemaingBytes = fsis.available();
					if (0 != numberOfRemaingBytes) {
						fail("the input stream is available");
					}

				} catch (Exception e) {
					log.warn(""+e.getMessage(), e);
					fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;

		int expectedValue =  0x8713;

		FreeSizeOutputStream fsos = null;
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		int numberOfBytesRequired = 2;		
		int numberOfBytesSkipping = dataPacketBufferSize*dataPacketBufferMaxCount;
		long numberOfBytesRemaining = dataPacketBufferSize*dataPacketBufferMaxCount - numberOfBytesSkipping;
		
		try {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}
			
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.skip(numberOfBytesSkipping);
			
			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);
			
			fsos.putUnsignedShort(expectedValue);
			
			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);
			
			String errorMessage = e.getMessage();
			String expectedMessage =
			String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			
			assertEquals(expectedMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
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
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}

			for (int expectedValue : integerTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putInt(expectedValue);

					checkNumberOfWrittenBytes(4, fsos);
					
					
					ArrayList<WrapBuffer> flippedWrapBufferList = fsos.getFlippedWrapBufferList();
					
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
						
						log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getInt();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);
					
					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format("numberOfReadBytes[%d] is different from outputStreamSize[%d]",
								numberOfReadBytes, outputStreamSize);
						fail(errorMessage);
					}			

					long numberOfRemaingBytes = fsis.available();
					if (0 != numberOfRemaingBytes) {
						fail("the input stream is available");
					}

				} catch (Exception e) {
					log.warn(""+e.getMessage(), e);
					fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;

		int expectedValue =  0x76135249;

		FreeSizeOutputStream fsos = null;
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		int numberOfBytesRequired = 4;		
		int numberOfBytesSkipping = dataPacketBufferSize*dataPacketBufferMaxCount-3;
		long numberOfBytesRemaining = dataPacketBufferSize*dataPacketBufferMaxCount - numberOfBytesSkipping;
		
		try {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}
			
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.skip(numberOfBytesSkipping);
			
			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);
			
			fsos.putInt(expectedValue);
			
			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);
			
			String errorMessage = e.getMessage();
			String expectedMessage =
			String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			
			assertEquals(expectedMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
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
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}

			for (long expectedValue : longTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putUnsignedInt(expectedValue);

					checkNumberOfWrittenBytes(4, fsos);

					ArrayList<WrapBuffer> flippedWrapBufferList = fsos.getFlippedWrapBufferList();
					
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
						
						log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getUnsignedInt();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);
					
					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format("numberOfReadBytes[%d] is different from outputStreamSize[%d]",
								numberOfReadBytes, outputStreamSize);
						fail(errorMessage);
					}			

					long numberOfRemaingBytes = fsis.available();
					if (0 != numberOfRemaingBytes) {
						fail("the input stream is available");
					}

				} catch (Exception e) {
					log.warn(""+e.getMessage(), e);
					fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;

		long expectedValue =  0x76135249L;

		FreeSizeOutputStream fsos = null;
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		int numberOfBytesRequired = 4;		
		int numberOfBytesSkipping = dataPacketBufferSize*dataPacketBufferMaxCount-3;
		long numberOfBytesRemaining = dataPacketBufferSize*dataPacketBufferMaxCount - numberOfBytesSkipping;
		
		try {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}
			
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.skip(numberOfBytesSkipping);
			
			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);
			
			fsos.putUnsignedInt(expectedValue);
			
			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);
			
			String errorMessage = e.getMessage();
			String expectedMessage =
			String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			
			assertEquals(expectedMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
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
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}

			for (long expectedValue : longTypeExpectedValueList) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);

					fsos.putLong(expectedValue);

					checkNumberOfWrittenBytes(8, fsos);

					ArrayList<WrapBuffer> flippedWrapBufferList = fsos.getFlippedWrapBufferList();
					
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
						
						log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
					}

					fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, flippedWrapBufferList, streamCharsetDecoder,
							dataPacketBufferPoolManager);

					actualValue = fsis.getLong();

					// log.info("2.expectedValue=0x{}, actualValue=0x{}",
					// HexUtil.getHexString(expectedValue), HexUtil.getHexString(actualValue));
					// log.info("FreeSizeInputStream size={}", fsis.position());

					assertEquals(expectedValue, actualValue);
					
					long numberOfReadBytes = fsis.getNumberOfReadBytes();
					if (numberOfReadBytes != outputStreamSize) {
						String errorMessage = String.format("numberOfReadBytes[%d] is different from outputStreamSize[%d]",
								numberOfReadBytes, outputStreamSize);
						fail(errorMessage);
					}			

					long numberOfRemaingBytes = fsis.available();
					if (0 != numberOfRemaingBytes) {
						fail("the input stream is available");
					}

				} catch (Exception e) {
					log.warn(""+e.getMessage(), e);
					fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 15;

		long expectedValue =  0x4762176135249038L;

		FreeSizeOutputStream fsos = null;
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		int numberOfBytesRequired = 8;		
		int numberOfBytesSkipping = dataPacketBufferSize*dataPacketBufferMaxCount-1;
		long numberOfBytesRemaining = dataPacketBufferSize*dataPacketBufferMaxCount - numberOfBytesSkipping;
		
		try {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}
			
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.skip(numberOfBytesSkipping);
			
			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);
			
			fsos.putLong(expectedValue);
			
			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);
			
			String errorMessage = e.getMessage();
			String expectedMessage =
			String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			
			assertEquals(expectedMessage, errorMessage);
			
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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
			log.warn(""+e.getMessage(), e);
			fail("error");
		}

		{
			log.info("test case::the 'src' parameter is null in the method \"public void putBytes(byte[] src)\"");
			
			byte[] sourceBytes = null;
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPoolManager);

				fsos.putBytes(sourceBytes);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();

				String expectedMessage = "the parameter src is null";

				assertEquals(expectedMessage, errorMessage);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			} finally {
				if (null != fsos) {
					fsos.close();
				}
			}
		}
		
		{
			log.info("test case::the 'src' parameter is null in the method \"public void putBytes(byte[] src, int offset, int length)\"");
			
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
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			} finally {
				if (null != fsos) {
					fsos.close();
				}
			}
		}
		
		{
			log.info("test case::the 'src' parameter is null in the method \"public void putBytes(ByteBuffer src)\"");
			
			ByteBuffer sourceByteBuffer = null;
			
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPoolManager);

				fsos.putBytes(sourceByteBuffer);

				fail("no IllegalArgumentException");
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();

				String expectedMessage = "the parameter src is null";

				assertEquals(expectedMessage, errorMessage);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
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
			log.warn(""+e.getMessage(), e);
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
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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
			log.warn(""+e.getMessage(), e);
			fail("error");
		}

		byte[] sourceBytes = { 0x11, 0x22, 0x33 };
		// byte actualValue[] = new byte[expectedValue.length];
		int sourceOffset = sourceBytes.length;
		int sourceLength = 1;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.putBytes(sourceBytes, sourceOffset, sourceLength);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = String.format("the parameter offset[%d] is greater than or equal to array.length[%d]", sourceOffset, sourceBytes.length);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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
			log.warn(""+e.getMessage(), e);
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
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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
			log.warn(""+e.getMessage(), e);
			fail("error");
		}

		byte[] sourceBytes = { 0x11, 0x22, 0x33 };
		// byte actualValue[] = new byte[expectedValue.length];
		int sourceOffset = sourceBytes.length-1;
		int sourceLength = 2;
		

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.putBytes(sourceBytes, sourceOffset, sourceLength);

			fail("no IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// log.warn(""+e.getMessage(), e);
			
			String errorMessage = e.getMessage();

			String expectedMessage = String.format(
					"the sum[%d] of the parameter offset[%d] and the parameter length[%d] is greater than array.length[%d]", 
					sourceOffset+sourceLength, sourceOffset, sourceLength, sourceBytes.length);

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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
			log.warn(""+e.getMessage(), e);
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
			String expectedMessage = String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					dataPacketBufferMaxCount*dataPacketBufferSize, expectedValue.length);
	
			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 4;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("error");
		}
		{
			log.info("test case::ok operation in the method \"public void putBytes(byte[] src)\"");
			
			byte[] sourceBytes = { 0x11, 0x22, 0x33, 0x44 };
			
			byte destinationBytes[] = new byte[sourceBytes.length];

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPoolManager);

				fsos.putBytes(sourceBytes);

				long outputStreamSize = fsos.size();

				if (outputStreamSize != sourceBytes.length) {
					String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not [%d] bytes",
							outputStreamSize, sourceBytes.length);
					fail(errorMessage);
				}

				ArrayList<WrapBuffer> wrapBufferList = fsos.getFlippedWrapBufferList();

				if (outputStreamSize <= dataPacketBufferSize) {
					WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
					ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
					/** warning! the duplicate method doesn't copy the byte order attribute */
					dupBuffer.order(streamByteOrder);

					dupBuffer.get(destinationBytes);

					/*log.info("1.expectedValue=0x{}, actualValue=0x{}", HexUtil.getHexStringFromByteArray(sourceBytes),
							HexUtil.getHexStringFromByteArray(destinationBytes));*/

					Assert.assertArrayEquals(sourceBytes, destinationBytes);
					
					log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
				}

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
						dataPacketBufferPoolManager);
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
					fail("the input stream is available");
				}

			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			} finally {
				if (null != fsos) {
					fsos.close();
				}
			}
		}
		
		{
			log.info("test case::ok operation in the method \"public void putBytes(byte[] src, int offset, int length)\"");
			
			byte[] sourceBytes = { 0x11, 0x22, 0x33, 0x44 };		
			int sourceOffset = 1;
			int sourceLength = 2;
			byte[] expectedBytes = { 0x22, 0x33 };
			
			byte destinationBytes[] = new byte[sourceLength];

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPoolManager);

				fsos.putBytes(sourceBytes, sourceOffset, sourceLength);

				long outputStreamSize = fsos.size();

				if (outputStreamSize != sourceLength) {
					String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not [%d] bytes",
							outputStreamSize, sourceLength);
					fail(errorMessage);
				}

				ArrayList<WrapBuffer> wrapBufferList = fsos.getFlippedWrapBufferList();

				if (outputStreamSize <= dataPacketBufferSize) {
					WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
					ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
					/** warning! the duplicate method doesn't copy the byte order attribute */
					dupBuffer.order(streamByteOrder);

					dupBuffer.get(destinationBytes);

					/*log.info("1.expectedValue=0x{}, actualValue=0x{}", HexUtil.getHexStringFromByteArray(sourceBytes),
							HexUtil.getHexStringFromByteArray(destinationBytes));*/

					Assert.assertArrayEquals(expectedBytes, destinationBytes);
					
					log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
				}

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
						dataPacketBufferPoolManager);
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
					fail("the input stream is available");
				}

			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			} finally {
				if (null != fsos) {
					fsos.close();
				}
			}
		}

		{
			log.info("test case::ok operation in the method \"public void putBytes(ByteBuffer src)\"");
			
			byte[] sourceBytes = { 0x11, 0x22, 0x33, 0x44 };
			ByteBuffer sourceByteBuffer = ByteBuffer.wrap(sourceBytes);
			
			byte destinationBytes[] = new byte[sourceBytes.length];

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPoolManager);

				fsos.putBytes(sourceByteBuffer);

				long outputStreamSize = fsos.size();

				if (outputStreamSize != sourceBytes.length) {
					String errorMessage = String.format("the size[%d] of FreeSizeOutputStream is not [%d] bytes",
							outputStreamSize, sourceBytes.length);
					fail(errorMessage);
				}

				ArrayList<WrapBuffer> wrapBufferList = fsos.getFlippedWrapBufferList();

				if (outputStreamSize <= dataPacketBufferSize) {
					WrapBuffer workingWrapBuffer = wrapBufferList.get(0);
					ByteBuffer dupBuffer = workingWrapBuffer.getByteBuffer().duplicate();
					/** warning! the duplicate method doesn't copy the byte order attribute */
					dupBuffer.order(streamByteOrder);

					dupBuffer.get(destinationBytes);

					/*log.info("1.expectedValue=0x{}, actualValue=0x{}", HexUtil.getHexStringFromByteArray(sourceBytes),
							HexUtil.getHexStringFromByteArray(destinationBytes));*/

					Assert.assertArrayEquals(sourceBytes, destinationBytes);
					
					log.info("하나의 버퍼에 쓴 내용이 모두 담겨있을 경우 ByteBuffer 를 이용한 데이터 검증 완료");
				}

				fsis = new FreeSizeInputStream(dataPacketBufferMaxCount, wrapBufferList, streamCharsetDecoder,
						dataPacketBufferPoolManager);
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
					fail("the input stream is available");
				}

			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			} finally {
				if (null != fsos) {
					fsos.close();
				}
			}
		}
		
	}

	@Test
	public void testPutBytes_complex() {
		fail("미구현");
	}

	@Test
	public void testPutFixedLengthString_theParameterLen_lessThanZero() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
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
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("error");
		}
		
		{
			log.info("test case::the 'src' parameter is null in the method \"public void putFixedLengthString(int fixedLength, "
					+ "String src, CharsetEncoder wantedCharsetEncoder)\"");
			
			String strStr = null;
			int fixedLength = 1;

			Charset wantedCharset = Charset.forName("EUC-KR");
			CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPoolManager);

				fsos.putFixedLengthString(fixedLength, strStr, wantedCharsetEncoder);	
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();

				String expectedMessage = "the parameter src is null";

				assertEquals(expectedMessage, errorMessage);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			} finally {
				if (null != fsos) {
					fsos.close();
				}
			}
		}
		
		{
			log.info("test case::the 'src' parameter is null in the method \"public void putFixedLengthString(int fixedLength, String src)\"");
			
			String strStr = null;
			int fixedLength = 1;			

			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPoolManager);

				fsos.putFixedLengthString(fixedLength, strStr);	
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();

				String expectedMessage = "the parameter src is null";

				assertEquals(expectedMessage, errorMessage);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1024;
		int dataPacketBufferPoolSize = 5;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("error");
		}

		String strStr = "똠방각하";
		int fixedLength = 1;
		CharsetEncoder wantedCharsetEncoder = null;

		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.putFixedLengthString(fixedLength, strStr, wantedCharsetEncoder);	
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = "the parameter wantedCharsetEncoder is null";

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != fsos) {
				fsos.close();
			}
		}
	}

	@Test
	public void testPutFixedLengthString_basic() {
		int dataPacketBufferMaxCount = 6;
		Charset streamCharset = Charset.forName("EUC-KR");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 6;

		FreeSizeOutputStream fsos = null;
		FreeSizeInputStream fsis = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("error");
		}		

		Charset wantedCharset = Charset.forName("utf-8");
		CharsetEncoder wantedCharsetEncoder = CharsetUtil.createCharsetEncoder(wantedCharset);
		CharsetDecoder wantedCharsetDecoder = CharsetUtil.createCharsetDecoder(wantedCharset);

		{
			log.info("test case::ok operation in the method \"public void putFixedLengthString(int fixedLength, String src)\"");
			
			String strStr = "가나다라";
			String excpectedValue = "가나";
			String actualValue = null;
			int fixedLength = 6;
			
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPoolManager);

				fsos.putFixedLengthString(fixedLength, strStr, wantedCharsetEncoder);

				checkNumberOfWrittenBytes(fixedLength, fsos);

				ArrayList<WrapBuffer> flippedWrapBufferList = fsos.getFlippedWrapBufferList();
				
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
						dataPacketBufferPoolManager);
				// fsis = fsos.getFreeSizeInputStream(streamCharsetDecoder);

				/*byte temp[] = new byte[(int) outputStreamSize];
				fsis.getBytes(temp);
				actualValue = new String(temp, wantedCharset);

				Assert.assertEquals(excpectedValue, actualValue.trim());*/
				
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
					fail("the input stream is available");
				}
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
			} finally {
				if (null != fsos) {
					fsos.close();
				}
			}
		}
		
		{
			log.info("test case::ok operation in the method \"public void putFixedLengthString(int fixedLength, String src, CharsetEncoder wantedCharsetEncoder)\"");
			
			String strStr = "가나다라";
			String excpectedValue = "가나다";
			String actualValue = null;
			int fixedLength = 6;
			
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPoolManager);

				fsos.putFixedLengthString(fixedLength, strStr);

				checkNumberOfWrittenBytes(fixedLength, fsos);

				ArrayList<WrapBuffer> flippedWrapBufferList = fsos.getFlippedWrapBufferList();
				
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
						dataPacketBufferPoolManager);
				// fsis = fsos.getFreeSizeInputStream(streamCharsetDecoder);

				/*byte temp[] = new byte[(int) outputStreamSize];
				fsis.getBytes(temp);
				actualValue = new String(temp, wantedCharset);

				Assert.assertEquals(excpectedValue, actualValue.trim());*/
				
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
					fail("the input stream is available");
				}
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 6;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("error");
		}
		
		String strStr = null;
		
		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.putStringAll(strStr);

			fail("no Exception");
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();

			String expectedMessage = "the parameter src is null";

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
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

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 6;

		FreeSizeOutputStream fsos = null;

		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder.build(isDirect,
					streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("error");
		}
		
		
		String strStr = "한글";
		
		int numberOfBytesRequired = strStr.getBytes(streamCharset).length;		
		int numberOfBytesSkipping = dataPacketBufferSize*dataPacketBufferMaxCount-1;
		long numberOfBytesRemaining = dataPacketBufferSize*dataPacketBufferMaxCount - numberOfBytesSkipping;
		
		
		
		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);
			
			fsos.skip(numberOfBytesSkipping);
			
			checkNumberOfWrittenBytes(numberOfBytesSkipping, fsos);

			fsos.putStringAll(strStr);

			fail("no SinnoriBufferOverflowException");
		} catch (SinnoriBufferOverflowException e) {
			// log.warn(""+e.getMessage(), e);			
			String errorMessage = e.getMessage();
			String expectedMessage =
			String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired);
			
			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != fsos) {
				fsos.close();
			}
		}
	}
	
	@Test
	public void testSkip_basic() {
		int dataPacketBufferMaxCount = 5;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 10;		

		FreeSizeOutputStream fsos = null;
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		
		try {
			try {
				dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
						.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				log.warn(""+e.getMessage(), e);
				fail("error");
			}
			
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);

			fsos.skip(dataPacketBufferSize*dataPacketBufferMaxCount);
			
			checkNumberOfWrittenBytes(dataPacketBufferSize*dataPacketBufferMaxCount, fsos);
			
			ArrayList<WrapBuffer> flippedWrapBufferList = fsos.getFlippedWrapBufferList();
			
			checkValidFlippedWrapBufferList(flippedWrapBufferList);	
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != fsos) {
				fsos.close();
			}
		}
	}
}
