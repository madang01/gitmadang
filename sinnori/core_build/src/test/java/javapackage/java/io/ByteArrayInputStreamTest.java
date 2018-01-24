package javapackage.java.io;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;

public class ByteArrayInputStreamTest {
private Logger log = LoggerFactory.getLogger(ByteArrayInputStreamTest.class);
	
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
