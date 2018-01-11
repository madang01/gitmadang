package kr.pe.sinnori.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;

public class FixedSizeInputStreamTest {
	private Logger log = LoggerFactory.getLogger(FixedSizeInputStreamTest.class);
	
	@Before
	public void setup() {
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				"sample_base");
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				"D:\\gitsinnori\\sinnori");

		SinnoriLogbackManger.getInstance().setup();

	}
	
	@Test
	public void testGetByte_BufferUnderflowException() {
		Charset streamCharset = Charset.forName("utf-8");
		// CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		ByteBuffer streambuffer = ByteBuffer.allocate(8);
		streambuffer.position(streambuffer.limit());
		
		FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
		try {
			fsis.getByte();
			
			fail("no SinnoriBufferUnderflowException");
		} catch (SinnoriBufferUnderflowException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage, e);
			
			String expectedMessage = "the remaining bytes is zero";

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail("unknown error::"+e.getMessage());
		}
		
	}
	
	@Test
	public void testGetUnsignedByte_BufferUnderflowException() {
		Charset streamCharset = Charset.forName("utf-8");
		// CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		ByteBuffer streambuffer = ByteBuffer.allocate(8);
		streambuffer.position(streambuffer.limit());
		
		FixedSizeInputStream fsis = new FixedSizeInputStream(streambuffer, streamCharsetDecoder);
		try {
			fsis.getUnsignedByte();
			
			fail("no SinnoriBufferUnderflowException");
		} catch (SinnoriBufferUnderflowException e) {
			String errorMessage = e.getMessage();
			
			log.info(errorMessage, e);
			
			String expectedMessage = "the remaining bytes is zero";

			assertEquals(expectedMessage, errorMessage);
		} catch (Exception e) {
			fail("unknown error::"+e.getMessage());
		}
	}
}
