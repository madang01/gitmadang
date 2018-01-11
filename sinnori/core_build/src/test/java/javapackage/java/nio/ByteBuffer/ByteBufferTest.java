package javapackage.java.nio.ByteBuffer;

import static org.junit.Assert.fail;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;

public class ByteBufferTest {
	Logger log = LoggerFactory
			.getLogger(ByteBufferTest.class);
	
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
	
}
