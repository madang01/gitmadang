package javapackage.java.io;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitSupporter;

public class ByteArrayInputStreamTest extends AbstractJunitSupporter {
	
	
	@Test
	public void testClose_닫은후동작을보기위한테스트() {
		byte src[] = {0x30, 0x31, 0x32};
		ByteArrayInputStream bais = new ByteArrayInputStream(src);
		bais.read();
		try {
			bais.close();
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			fail("io error::"+e.getMessage());
		}
		
		log.info("1. pos={}", bais.available());
		
		bais.read();
		
		log.info("2. pos={}", bais.available());
	}
	
	@Test
	public void testMark_theParameterReadAheadLimit_greaterThanRemaingBytes() {
		byte src[] = {0x30, 0x31, 0x32};
		ByteArrayInputStream bais = new ByteArrayInputStream(src);
		
		if (!bais.markSupported()) {
			fail("ByteArrayInputStream not mark supported");
		}
		
		int value = bais.read();
		log.info("1. value={}", value);
		
		/*try {
			bais.mark(src.length);
		} catch(Exception e) {
			log.warn("", e);
			fail("mark() 호출시 에러 발생");
		}
		*/
		try {
			bais.mark(1);
		} catch(Exception e) {
			log.warn("", e);
			fail("mark() 호출시 에러 발생");
		}
		
		value = bais.read();	
		log.info("2. value={}", value);
		value = bais.read();
		log.info("3. value={}", value);
		
		bais.reset();
		
		value = bais.read();
		log.info("4. value={}", value);
		
		value = bais.read();
		log.info("5. value={}", value);
		
		value = bais.read();
		log.info("6. value={}", value);
	}
}
