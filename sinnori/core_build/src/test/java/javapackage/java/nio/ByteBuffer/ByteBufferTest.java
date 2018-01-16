package javapackage.java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;

public class ByteBufferTest {
	Logger log = LoggerFactory
			.getLogger(ByteBufferTest.class);
	
	@Before
	public void setup() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_base";
		LOG_TYPE logType = LOG_TYPE.SERVER;
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);		
		

		SinnoriLogbackManger.getInstance().setup(sinnoriInstalledPathString, mainProjectName, logType);
		
	}
	
	@Test
	public void testPut_파라미터length가파라미터src로지정되는바이트배열의크기보다큰경우() {
		ByteBuffer streambuffer =  ByteBuffer.allocate(10);
		
		byte src[] = {0x32, 0x33, 0x34, 0x35};
		int offset = 0;
		int length = 7;
		try {
			streambuffer.put(src, offset, length);
			
			fail("no IndexOutOfBoundsException");
		} catch(IndexOutOfBoundsException e) {
			//log.warn(e.toString(), e);
		} catch(Exception e) {
			log.warn(e.toString(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPut_버퍼크기가파라미터src로지정되는바이트배열의크기보다작은경우() {
		ByteBuffer streambuffer =  ByteBuffer.allocate(2);
		
		byte src[] = {0x32, 0x33, 0x34, 0x35};
		int offset = 0;
		int length = 3;
		try {
			streambuffer.put(src, offset, length);
			
			fail("no BufferOverflowException");
		} catch(BufferOverflowException e) {
			//log.warn(e.toString(), e);
		} catch(Exception e) {
			log.warn(e.toString(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPut_길이를0으로지정할경우() {
		ByteBuffer streambuffer =  ByteBuffer.allocate(2);
		
		byte src[] = {0x32, 0x33, 0x34, 0x35};
		int offset = 1;
		int length = 3;
		try {
			streambuffer.put(src, offset, length);
		
		} catch(Exception e) {
			log.warn(e.toString(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDuplicate_바이트오더도복제하는지여부() {
		// ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
		
		ByteBuffer streambuffer =  ByteBuffer.allocate(2);
		
		
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		
		for (ByteOrder byteOrder : streamByteOrderList) {
			streambuffer.order(byteOrder);
			
			ByteBuffer dupBuffer = streambuffer.duplicate();
			
			assertEquals(byteOrder, dupBuffer.order());
		}
	}
	
	@Test
	public void testArray_유효한범위인지전체범위인지테스트() {
		byte src[] = {0x32, 0x33, 0x34, 0x35};
		ByteBuffer streambuffer =  ByteBuffer.wrap(src);
		streambuffer.get();
		streambuffer.get();
		
		byte result[] = streambuffer.array();
		
		if (src.length == result.length) {
			log.info("결과:전체, streambuffer={}", streambuffer.toString());
		} else {
			log.info("결과:유효한부분만, streambuffer={}", streambuffer.toString());
		}
	}
}
