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

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.util.HexUtil;


public class FixedSizeOutputStreamTest {
	Logger log = LoggerFactory
			.getLogger(FixedSizeOutputStreamTest.class);
	
	@Before
	public void setup() {
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				"sample_base");
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				"D:\\gitsinnori\\sinnori");
		
		SinnoriLogbackManger.getInstance().setup();
		
	}
	
	@Test
	public void test빅엔디안과리틀엔디안차이점() {
		long tValue = 0x1122334455667788L;
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
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
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = "the parameter outputStreamBuffer is null";
			
			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testConstructor_theParameterStreamCharsetEncoder_null() {
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
		try {
			@SuppressWarnings("unused")
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, null);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = "the parameter streamCharsetEncoder is null";
			
			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPutUnsignedByte_theParameterValue_shortType_lessThanZero() {
		short value = -1;
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());
			
			fsos.putUnsignedByte(value);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is less than zero", value);
			
			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPutUnsignedByte_theParameterValue_shortType_greaterThanMax() {
		short value = CommonStaticFinalVars.UNSIGNED_BYTE_MAX+1;
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());
			
			fsos.putUnsignedByte(value);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is greater than the unsigned byte max[%d]", value, CommonStaticFinalVars.UNSIGNED_BYTE_MAX);
			
			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPutUnsignedByte_theParameterValue_minMaxMiddle() {
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		ByteOrder[] streamByteOrderList = {ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN};
		for (int j=0; j < streamByteOrderList.length; j++) {
			ByteOrder streamByteOrder = streamByteOrderList[j];
			streambuffer.order(streamByteOrder);
			
			{
				short shortTypeExpectedValueList[]={0, CommonStaticFinalVars.UNSIGNED_BYTE_MAX, CommonStaticFinalVars.UNSIGNED_BYTE_MAX/2};
				for (int i=0; i < shortTypeExpectedValueList.length; i++) {
					short expectedValue = shortTypeExpectedValueList[i];
					streambuffer.clear();
					Arrays.fill(streambuffer.array(), (byte)0);
					
					try {
						FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
						
						fsos.putUnsignedByte(expectedValue);
						fsos.flipOutputStreamBuffer();
						
						FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
						short actucalValue = fsis.getUnsignedByte();
						
						assertEquals(expectedValue, actucalValue);
					} catch(IllegalArgumentException e) {
						fail(e.getMessage());
					} catch(Exception e) {
						fail(e.getMessage());
					}
				}
			}
			
			{
				int integerTypeExpectedValueList[]={0, CommonStaticFinalVars.UNSIGNED_BYTE_MAX, CommonStaticFinalVars.UNSIGNED_BYTE_MAX/2};				
				for (int i=0; i < integerTypeExpectedValueList.length; i++) {
					int expectedValue = integerTypeExpectedValueList[i];
					streambuffer.clear();
					Arrays.fill(streambuffer.array(), (byte)0);
					
					try {
						FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
						
						fsos.putUnsignedByte(expectedValue);
						fsos.flipOutputStreamBuffer();
						
						FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
						short actucalValue = fsis.getUnsignedByte();
						
						assertEquals(expectedValue, actucalValue);
					} catch(IllegalArgumentException e) {
						fail(e.getMessage());
					} catch(Exception e) {
						fail(e.getMessage());
					}
				}
			}
		}
	}
	
	@Test
	public void testPutUnsignedByte_theParameterValue_integerType_lessThanZero() {
		int value = -1;
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());
			
			fsos.putUnsignedByte(value);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is less than zero", value);
			
			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPutUnsignedByte_theParameterValue_integerType_greaterThanMax() {
		int value = CommonStaticFinalVars.UNSIGNED_BYTE_MAX+1;
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());
			
			fsos.putUnsignedByte(value);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is greater than the unsigned byte max[%d]", value, CommonStaticFinalVars.UNSIGNED_BYTE_MAX);
			
			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	
	@Test
	public void testPutUnsignedShort_theParameterValue_lessThanZero() {
		int value = -1;
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());
			
			fsos.putUnsignedShort(value);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is less than zero", value);
			
			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testPutUnsignedShort_theParameterValue_greaterThanMax() {
		int value = CommonStaticFinalVars.UNSIGNED_SHORT_MAX+10;
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());
			
			fsos.putUnsignedShort(value);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is greater than the unsigned short max[%d]", value, CommonStaticFinalVars.UNSIGNED_SHORT_MAX);
			
			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testPutUnsignedShort_theParameterValue_minMaxMiddle() {
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
		
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
	
		int integerTypeExpectedValueList[]={0, CommonStaticFinalVars.UNSIGNED_SHORT_MAX, CommonStaticFinalVars.UNSIGNED_SHORT_MAX/3};
		ByteOrder[] streamByteOrderList = {ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN};
		
		for (int j=0; j < streamByteOrderList.length; j++) {
			ByteOrder streamByteOrder = streamByteOrderList[j];
			
			streambuffer.order(streamByteOrder);
			
			for (int i=0; i < integerTypeExpectedValueList.length; i++) {
				int expectedValue = integerTypeExpectedValueList[i];
				streambuffer.clear();
				Arrays.fill(streambuffer.array(), (byte)0);
				
				try {
					FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
					
					fsos.putUnsignedShort(expectedValue);
					fsos.flipOutputStreamBuffer();
					
					FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
					int actucalValue = fsis.getUnsignedShort();
					
					assertEquals(expectedValue, actucalValue);
				} catch(IllegalArgumentException e) {
					fail(e.getMessage());
				} catch(Exception e) {
					fail(e.getMessage());
				}
			}
		}
	}
	
	
	@Test
	public void testPutUnsignedInt_theParameterValue_lessThanZero() {
		long value = -1;
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());
			
			fsos.putUnsignedInt(value);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is less than zero", value);
			
			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPutUnsignedInt_theParameterValue_greaterThanMax() {
		long value = CommonStaticFinalVars.UNSIGNED_INTEGER_MAX+10;
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
		try {
			FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());
			
			fsos.putUnsignedInt(value);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = String.format("the parameter value[%d] is greater than the unsigned integer max[%d]", value, CommonStaticFinalVars.UNSIGNED_INTEGER_MAX);
			
			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	// FIXME!, putUnsignedInt, _minMaxMiddle
	@Test
	public void testPutUnsignedInt_theParameterValue_minMaxMiddle() {
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
		
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
	
		long longTypeExpectedValueList[]={0, CommonStaticFinalVars.UNSIGNED_INTEGER_MAX, CommonStaticFinalVars.UNSIGNED_INTEGER_MAX/14};
		ByteOrder[] streamByteOrderList = {ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN};
		
		for (int j=0; j < streamByteOrderList.length; j++) {
			ByteOrder streamByteOrder = streamByteOrderList[j];
			
			streambuffer.order(streamByteOrder);
			
			for (int i=0; i < longTypeExpectedValueList.length; i++) {
				long expectedValue = longTypeExpectedValueList[i];
				streambuffer.clear();
				Arrays.fill(streambuffer.array(), (byte)0);
				
				try {
					FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, streamCharsetEncoder);
					
					fsos.putUnsignedInt(expectedValue);
					fsos.flipOutputStreamBuffer();
					
					FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
					long actucalValue = fsis.getUnsignedInt();
					
					assertEquals(expectedValue, actucalValue);
				} catch(IllegalArgumentException e) {
					fail(e.getMessage());
				} catch(Exception e) {
					fail(e.getMessage());
				}
			}
		}
	}
	
	@Test
	public void testConstructor() {
		long tValue = 0x1122334455667788L;
		ByteBuffer streambuffer =  ByteBuffer.allocate(8);
		
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
		
		FixedSizeOutputStream fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());
		short expectedValue01 = (short)200;
		int expectedValue02 =  CommonStaticFinalVars.UNSIGNED_SHORT_MAX-1;
		
		fsos.putUnsignedByte(expectedValue01);
		fsos.putUnsignedShort(expectedValue02);
		streambuffer.flip();
		
		log.info("#1::big endian::streamBuffer=0x{}", HexUtil.getAllHexStringFromByteBuffer(streambuffer));
		log.info("#1::streamBuffer=[{}], byteorder={}", streambuffer.toString(), streambuffer.order().toString());
		
		
		FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, Charset.forName("utf-8").newDecoder());
		try {
			short actucalValue01 = fsis.getUnsignedByte();
			
			assertEquals(expectedValue01, actucalValue01);
			
		} catch (SinnoriBufferUnderflowException e) {
			fail(e.getMessage());
		}
		try {
			int actucalValue02 = fsis.getUnsignedShort();
			
			assertEquals(expectedValue02, actucalValue02);
		} catch (SinnoriBufferUnderflowException e) {
			fail(e.getMessage());
		}
		
		
		streambuffer.clear();
		streambuffer.order(ByteOrder.LITTLE_ENDIAN);
		streambuffer.putLong(0L);
		streambuffer.clear();
		
		fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());
		expectedValue01 = (short)200;
		expectedValue02 =  CommonStaticFinalVars.UNSIGNED_SHORT_MAX-1;
		
		fsos.putUnsignedByte(expectedValue01);
		fsos.putUnsignedShort(expectedValue02);
		streambuffer.flip();
		
		log.info("#2::little endian::streamBuffer=0x{}", HexUtil.getAllHexStringFromByteBuffer(streambuffer));
		log.info("#2::streamBuffer=[{}], byteorder={}", streambuffer.toString(), streambuffer.order().toString());
		
		
		fsis = new FixedSizeInputStream(streambuffer, Charset.forName("utf-8").newDecoder());
		try {
			short actucalValue01 = fsis.getUnsignedByte();
			
			assertEquals(expectedValue01, actucalValue01);
			
		} catch (SinnoriBufferUnderflowException e) {
			fail(e.getMessage());
		}
		try {
			int actucalValue02 = fsis.getUnsignedShort();
			
			assertEquals(expectedValue02, actucalValue02);
		} catch (SinnoriBufferUnderflowException e) {
			fail(e.getMessage());
		}
		
		
		streambuffer.clear();
		streambuffer.order(ByteOrder.BIG_ENDIAN);
		streambuffer.putLong(0L);
		streambuffer.clear();
		
		fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());
		expectedValue01 = (short)200;
		long expectedValue03 =  CommonStaticFinalVars.UNSIGNED_INTEGER_MAX - 1;
		
		fsos.putUnsignedByte(expectedValue01);
		fsos.putUnsignedInt(expectedValue03);
		streambuffer.flip();
		
		log.info("#3::big endian::expectedValue03={}, streamBuffer=0x{}", expectedValue03, HexUtil.getAllHexStringFromByteBuffer(streambuffer));
		log.info("#3::streamBuffer=[{}], byteorder={}", streambuffer.toString(), streambuffer.order().toString());
		
		
		fsis = new FixedSizeInputStream(streambuffer, Charset.forName("utf-8").newDecoder());
		try {
			short actucalValue01 = fsis.getUnsignedByte();
			
			assertEquals(expectedValue01, actucalValue01);
			
		} catch (SinnoriBufferUnderflowException e) {
			fail(e.getMessage());
		}
		try {
			long actucalValue03 = fsis.getUnsignedInt();
			
			assertEquals(expectedValue03, actucalValue03);
		} catch (SinnoriBufferUnderflowException e) {
			fail(e.getMessage());
		}
		
		
		streambuffer.clear();
		streambuffer.order(ByteOrder.LITTLE_ENDIAN);
		streambuffer.putLong(0L);
		streambuffer.clear();
		
		fsos = new FixedSizeOutputStream(streambuffer, Charset.forName("utf-8").newEncoder());
		expectedValue01 = (short)200;
		expectedValue03 =  CommonStaticFinalVars.UNSIGNED_INTEGER_MAX - 1;
		
		fsos.putUnsignedByte(expectedValue01);
		fsos.putUnsignedInt(expectedValue03);
		streambuffer.flip();
		
		log.info("#4::little endian::expectedValue03={}, streamBuffer=0x{}", expectedValue03, HexUtil.getAllHexStringFromByteBuffer(streambuffer));
		log.info("#4::streamBuffer=[{}], byteorder={}", streambuffer.toString(), streambuffer.order().toString());
		
		
		fsis = new FixedSizeInputStream(streambuffer, Charset.forName("utf-8").newDecoder());
		try {
			short actucalValue01 = fsis.getUnsignedByte();
			
			assertEquals(expectedValue01, actucalValue01);
			
		} catch (SinnoriBufferUnderflowException e) {
			fail(e.getMessage());
		}
		try {
			long actucalValue03 = fsis.getUnsignedInt();
			
			assertEquals(expectedValue03, actucalValue03);
		} catch (SinnoriBufferUnderflowException e) {
			fail(e.getMessage());
		}
	}
}
