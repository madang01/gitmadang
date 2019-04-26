package kr.pe.codda.common.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;

public class ReceivedDataStreamTest extends AbstractJunitTest {
	
	@Test
	public void testCutMessageInputStreamFromStartingPosition_basic() {
		int dataPacketBufferMaxCount = 15;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
	
		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 15;
		
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		}
		
		
		ReceivedDataStream sos = null;
		FreeSizeOutputStream fsos = null;
		try {
			{
				long expectedSize = dataPacketBufferSize*3+42;
				{
					
					ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = null;
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPool);
					
					fsos.skip(expectedSize);
					emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
					sos = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);
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
	
		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 6;
		int dataPacketBufferPoolSize = 15;
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
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
		 
		
		ReceivedDataStream sos = null;
		
		FreeSizeOutputStream fsos = null;
		ArrayDeque<WrapBuffer> outputStreamWrapBufferListForTest = null;
		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPool);
			
			for (int expectedValue : integerTypeExpectedValueList) {
				fsos.putInt(expectedValue);
			}
	
			
			fsos.putByte(eofByte);
			
			outputStreamWrapBufferListForTest = fsos.getOutputStreamWrapBufferList();
			
			sos = 
					new ReceivedDataStream(outputStreamWrapBufferListForTest, streamCharsetDecoder,
							dataPacketBufferMaxCount, dataPacketBufferPool);
			
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
			
			if (1 != sos.getCircleQueueSize()) {
				fail(String.format("StreamWrapBufferQueue size[%d] is not equal to one", 
						sos.getCircleQueueSize()));
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
			
			if (0L != sos.getReceviedBytes()) {
				fail("1. the number of socket read bytes is not zero");
			}
			
			if (0L != sos.getSreamSizeUsingStreamWrapBufferQueue()) {
				fail("2. the number of socket read bytes is not zero");
			}
			
			if (0 != sos.getCircleQueueSize()) {
				fail(String.format("StreamWrapBufferQueue size[%d] is not equal to zero", 
						sos.getCircleQueueSize()));
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


	@Test
	public void testGetByte_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		// int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 10;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3};
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		byte[] byteTypeExpectedValueList = { -1, 0, 1, Byte.MIN_VALUE/2, Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE/2};
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
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
					
					for (byte expectedValue : byteTypeExpectedValueList) {
						fsos.putByte(expectedValue);
					}
					
					ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
					rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);

					for (byte expectedValue : byteTypeExpectedValueList) {
						byte actualValue = rds.getByte();
						
						assertEquals(expectedValue, actualValue);
					}
					
					assertEquals(0, rds.available());
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
	public void testGetUnsignedByte_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		// int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 10;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3};
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		short[] unsignedByteTypeExpectedValueList = { 0, 1, Byte.MAX_VALUE, Byte.MAX_VALUE+1, CommonStaticFinalVars.UNSIGNED_BYTE_MAX};
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
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
					
					for (short expectedValue : unsignedByteTypeExpectedValueList) {
						fsos.putUnsignedByte(expectedValue);
					}
					
					ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
					rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);

					for (short expectedValue : unsignedByteTypeExpectedValueList) {
						short actualValue = rds.getUnsignedByte();
						
						assertEquals(expectedValue, actualValue);
					}
					
					assertEquals(0, rds.available());
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
	public void testGetShort_minMaxMiddle() {
		int dataPacketBufferMaxCount = 100;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		// int dataPacketBufferSize = 1;
		int dataPacketBufferPoolSize = 200;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3};
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		short[] shortTypeExpectedValueList = { -1, 0, 1, Short.MIN_VALUE, Short.MAX_VALUE, 0, Short.MAX_VALUE / 2 };
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
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
					
					for (short expectedValue : shortTypeExpectedValueList) {
						fsos.putShort(expectedValue);
					}
					
					ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
					rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);

					for (short expectedValue : shortTypeExpectedValueList) {
						short actualValue = rds.getShort();
						
						log.info("dataPacketBufferSizeList={}, actualValue={}", dataPacketBufferSizeList, actualValue);
						
						assertEquals(expectedValue, actualValue);
					}
					
					assertEquals(0, rds.available());
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
	public void testGetUnsignedShort_minMaxMiddle() {
		int dataPacketBufferMaxCount = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferPoolSize = 10;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3, 4, 5};
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		int[] unsignedShortTypeExpectedValueList = { 1, 0, CommonStaticFinalVars.UNSIGNED_SHORT_MAX,  CommonStaticFinalVars.UNSIGNED_SHORT_MAX/2};
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
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
					
					for (int expectedValue : unsignedShortTypeExpectedValueList) {
						fsos.putUnsignedShort(expectedValue);
					}
					
					ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
					rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);

					for (int expectedValue : unsignedShortTypeExpectedValueList) {
						int actualValue = rds.getUnsignedShort();
						
						assertEquals(expectedValue, actualValue);
					}
					
					assertEquals(0, rds.available());
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
	public void testGetInt_minMaxMiddle() {
		int dataPacketBufferMaxCount = 24;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferPoolSize = 100;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3, 4, 5};
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		int[] intTypeExpectedValueList = { 1, 0, Integer.MIN_VALUE,  Integer.MAX_VALUE, CommonStaticFinalVars.UNSIGNED_SHORT_MAX, CommonStaticFinalVars.UNSIGNED_SHORT_MAX+1};
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
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
					
					for (int expectedValue : intTypeExpectedValueList) {
						fsos.putInt(expectedValue);
					}
					
					ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
					rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);

					for (int expectedValue : intTypeExpectedValueList) {
						int actualValue = rds.getInt();
						
						assertEquals(expectedValue, actualValue);
					}
					
					assertEquals(0, rds.available());
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
	public void testGetUnsignedInt_minMaxMiddle() {
		int dataPacketBufferMaxCount = 100;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferPoolSize = 200;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3, 4, 5};
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		long[] unsignedIntTypeExpectedValueList = { 0, 1, Integer.MAX_VALUE, Integer.MAX_VALUE+1L, CommonStaticFinalVars.UNSIGNED_INTEGER_MAX};
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
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
					
					for (long expectedValue : unsignedIntTypeExpectedValueList) {
						fsos.putUnsignedInt(expectedValue);
					}
					
					ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
					rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);

					for (long expectedValue : unsignedIntTypeExpectedValueList) {
						long actualValue = rds.getUnsignedInt();
						
						assertEquals(expectedValue, actualValue);
					}
					
					assertEquals(0, rds.available());
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
	public void testGetLong_minMaxMiddle() {
		int dataPacketBufferMaxCount = 100;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferPoolSize = 200;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3, 4, 5, 6, 7, 8, 9};
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		long[] longTypeExpectedValueList = { -1, 0, 1, CommonStaticFinalVars.UNSIGNED_INTEGER_MAX, CommonStaticFinalVars.UNSIGNED_INTEGER_MAX+1, Long.MIN_VALUE, Long.MAX_VALUE};
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
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
					
					for (long expectedValue : longTypeExpectedValueList) {
						fsos.putLong(expectedValue);
					}
					
					ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
					rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);

					for (long expectedValue : longTypeExpectedValueList) {
						long actualValue = rds.getLong();
						
						assertEquals(expectedValue, actualValue);
					}
					
					assertEquals(0, rds.available());
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
	public void testGetBytes_단순값비교() {
		int dataPacketBufferMaxCount = 100;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3, 4};
		byte[][] expectedByteArrayList = new byte[10][];
		
		Random random = new Random();
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			try {
				/** build expectedByteArrayList having random data */				
				for (int i=0; i < expectedByteArrayList.length; i++) {
					int size = random.nextInt(10) + 1;					
					expectedByteArrayList[i] = new byte[size];
					random.nextBytes(expectedByteArrayList[i]);
				}
				
				
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);
				
				
				for (byte[] expectedByteArray : expectedByteArrayList) {
					fsos.putBytes(expectedByteArray);
				}
				
				long expectedOutputStreamSize = fsos.size();
				
				ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
				rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);
				
				assertEquals(expectedOutputStreamSize, rds.available());
				
				for (byte[] expectedByteArray : expectedByteArrayList) {					
					byte[] actualByteArray = new byte[expectedByteArray.length];
					rds.getBytes(actualByteArray);
					
					assertArrayEquals(expectedByteArray, actualByteArray);
				}
				
				assertEquals(0, rds.available());
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
	public void testGetBytes_수동비교() {
		int dataPacketBufferMaxCount = 100;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3, 4};
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			try {
				
				byte[] testByteArray = {1, 2, 3, 4, 5, 6, 7, 8, 9};
				byte[] expectedByteArray = {9, 8, 7, 4, 5,  6, 2, 3, 1};
				
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);
				
				
				fsos.putBytes(testByteArray);
				
				long expectedOutputStreamSize = fsos.size();
				
				ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
				rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);
				
				assertEquals(expectedOutputStreamSize, rds.available());
				
				byte[] actualByteArray = new byte[testByteArray.length];
				Arrays.fill(actualByteArray, CommonStaticFinalVars.ZERO_BYTE);
				
				rds.getBytes(actualByteArray, 8, 1);
				rds.getBytes(actualByteArray, 6, 2);				
				rds.getBytes(actualByteArray, 3, 3);				
				rds.getBytes(actualByteArray, 2, 1);
				rds.getBytes(actualByteArray, 1, 1);
				rds.getBytes(actualByteArray, 0, 1);

				
				
				assertArrayEquals(expectedByteArray, actualByteArray);
					
				assertEquals(0, rds.available());
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
	public void testSkip() {
		int dataPacketBufferMaxCount = 100;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3, 4, 11};
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			byte[] testByteArray = {1, 2, 3, 4, 5, 6, 7, 8, 9};
			
			for (int i=1; i < testByteArray.length; i++) {
				try {
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPool);
					
					
					fsos.putBytes(testByteArray);
					
					long expectedOutputStreamSize = fsos.size();
					
					ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
					rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);
					
					assertEquals(expectedOutputStreamSize, rds.available());
					rds.skip(i);
					
					assertEquals(testByteArray[i], rds.getByte());
					
					assertEquals(testByteArray.length - i - 1, rds.available());
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
			
			
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);				
				
				fsos.putBytes(testByteArray);
				
				long expectedOutputStreamSize = fsos.size();
				
				ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
				rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);
				
				assertEquals(expectedOutputStreamSize, rds.available());
				rds.skip(testByteArray.length);
				
				assertEquals(0, rds.available());
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
	public void testMark_처음() {
		int dataPacketBufferMaxCount = 100;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3, 4, 11};
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			byte[] testByteArray = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
			
			
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);
				
				fsos.putBytes(testByteArray);
				
				long expectedOutputStreamSize = fsos.size();
				
				ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
				rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);
				
				assertEquals(expectedOutputStreamSize, rds.available());
				rds.mark();
				rds.skip(5);
				byte actualValue = rds.getByte();
				
				assertEquals(5, actualValue);
				
				rds.reset();
				
				actualValue = rds.getByte();
				
				assertEquals(0, actualValue);
				
				actualValue = rds.getByte();
				
				assertEquals(1, actualValue);
				
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
	public void testMark_중간() {
		int dataPacketBufferMaxCount = 100;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3, 4, 512};
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			byte[] testByteArray = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
			
			
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);
				
				fsos.putBytes(testByteArray);
				
				long expectedOutputStreamSize = fsos.size();
				
				ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
				rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);
				
				assertEquals(expectedOutputStreamSize, rds.available());				
				rds.skip(5);
				rds.mark();
				
				byte actualValue = rds.getByte();				
				assertEquals(5, actualValue);
				
				rds.skip(rds.available());

				rds.reset();
				
				actualValue = rds.getByte();
				
				assertEquals(5, actualValue);
				
				actualValue = rds.getByte();
				
				assertEquals(6, actualValue);
				
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
	public void testGetMD5_전체() {
		int dataPacketBufferMaxCount = 21;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3, 4, 512};
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			byte[] testByteArray = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
			
			
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);
				
				fsos.putBytes(testByteArray);
				
				long expectedOutputStreamSize = fsos.size();
				
				ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
				rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);
				
				assertEquals(expectedOutputStreamSize, rds.available());
				
				rds.mark();
				byte[] actuals = rds.getMD5(rds.available());
				
				java.security.MessageDigest md5 = null;
				try {
					md5 = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
					System.exit(1);
				}
				
				md5.update(testByteArray);
				byte[] expecteds = md5.digest();
				
				assertArrayEquals(expecteds, actuals);
				
				assertEquals(0, rds.available());
				
				rds.reset();
				
				byte actualValue = rds.getByte();
				
				assertEquals(0, actualValue);
				
				actualValue = rds.getByte();
				
				assertEquals(1, actualValue);
				
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
	public void testGetMD5WithoutChange_전체() {
		int dataPacketBufferMaxCount = 100;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3, 4, 512};
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			byte[] testByteArray = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
			
			
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);
				
				fsos.putBytes(testByteArray);
				
				long expectedOutputStreamSize = fsos.size();
				
				ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
				rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);
				
				assertEquals(expectedOutputStreamSize, rds.available());
				
				byte[] actuals = rds.getMD5WithoutChange(rds.available());
				
				java.security.MessageDigest md5 = null;
				try {
					md5 = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
					System.exit(1);
				}
				
				md5.update(testByteArray);
				byte[] expecteds = md5.digest();
				
				assertArrayEquals(expecteds, actuals);
				
				byte actualValue = rds.getByte();
				
				assertEquals(0, actualValue);
				
				actualValue = rds.getByte();
				
				assertEquals(1, actualValue);
				
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
	public void testGetMD5WithoutChange_getMD5_속도비교() {
		int dataPacketBufferMaxCount = 21;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;

		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		int dataPacketBufferPoolSize = 1000;

		FreeSizeOutputStream fsos = null;
		ReceivedDataStream rds = null;
		
		int[] dataPacketBufferSizeList = { 1, 2, 3, 4, 512};
		
		for (int dataPacketBufferSize : dataPacketBufferSizeList) {
			try {
				dataPacketBufferPool = new DataPacketBufferPool(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			} catch (Exception e) {
				String errorMessage = "error::" + e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			byte[] testByteArray = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
			
			try {
				fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
						dataPacketBufferPool);
				
				fsos.putBytes(testByteArray);
				
				long expectedOutputStreamSize = fsos.size();
				
				ArrayDeque<WrapBuffer> emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
				rds = new ReceivedDataStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPool);
				
				assertEquals(expectedOutputStreamSize, rds.available());
				
				final int retryCount = 1000000;
				
				long startTime = System.nanoTime();
				
				for (int i=0; i < retryCount; i++) {
					rds.getMD5WithoutChange(rds.available());
				}
				
				long endTime = System.nanoTime();
				
				log.info("{}::getMD5WithoutChange::평균시간:{}", dataPacketBufferSize, (endTime - startTime)/retryCount);
				
				
				startTime = System.nanoTime();
				
				for (int i=0; i < retryCount; i++) {
					// log.info("dataPacketBufferSize={}::{} 번째 시도", dataPacketBufferSize, i);
					rds.mark();
					rds.getMD5(rds.available());
					rds.reset();
				}
				
				endTime = System.nanoTime();
				
				log.info("{}::getMD5::평균시간:{}", dataPacketBufferSize, (endTime - startTime)/retryCount);
				
				byte actualValue = rds.getByte();
				
				assertEquals(0, actualValue);
				
				actualValue = rds.getByte();
				
				assertEquals(1, actualValue);
				
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
