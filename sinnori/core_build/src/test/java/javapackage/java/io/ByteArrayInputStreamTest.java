package javapackage.java.io;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;

public class ByteArrayInputStreamTest {
private Logger log = LoggerFactory.getLogger(ByteArrayInputStreamTest.class);
	
	@Before
	public void setup() {
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				"sample_base");
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				"D:\\gitsinnori\\sinnori");

		SinnoriLogbackManger.getInstance().setup();

	}
	
	
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
}
