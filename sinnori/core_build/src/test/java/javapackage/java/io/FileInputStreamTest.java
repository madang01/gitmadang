package javapackage.java.io;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;

public class FileInputStreamTest {
	private Logger log = LoggerFactory.getLogger(FileInputStreamTest.class);
	
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
		File tempFile = null;
		String prefix = "Test";
		String suffix = ".temp";
		try {
			tempFile = File.createTempFile(prefix, suffix);
			
			
			
		} catch (IOException e) {
			fail("can't create tempfile becase of io error::"+e.getMessage());
		}
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(tempFile);
			fis.close();
			
			
		} catch (FileNotFoundException e) {
			fail("temp file not found error::"+e.getMessage());
		} catch (IOException e) {
			fail("fail to close temp file::"+e.getMessage());
		}
		
		
		try {
			fis.read();
		} catch (IOException e) {
			/** result :: java.io.IOException: Stream Closed */
			log.warn("IOException", e);
		}
		
		
	}
}
