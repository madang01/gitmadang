package kr.pe.sinnori.common.etc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junitlib.JunitUtil;
import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;

public class SinnoriSecurityMangerTest {
	Logger log = null;

	@Before
	public void setup() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_base";
		LOG_TYPE logType = LOG_TYPE.SERVER;
		String logbackConfigFilePathString = BuildSystemPathSupporter.getLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		String sinnoriLogPathString = BuildSystemPathSupporter.getLogPathString(sinnoriInstalledPathString, mainProjectName, logType);
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);		
		
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH,
				sinnoriLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);

		// SinnoriLogbackManger.getInstance().setup(sinnoriInstalledPathString, mainProjectName, logType);
		
		log = LoggerFactory.getLogger(SinnoriSecurityMangerTest.class);

	}
	
	/**
	 * this method test for the private method 'LimitedLongBitSet#checkIndexOutOfBoundsException' using Java reflection.
	 * if this test can't run because of java reflection permission, then check java reflection permission
	 * (ex vm argument -Djava.security.manager, the Sinnori project set vm argument 'java.security.manager' to 'kr.pe.sinnori.common.etc.SinnoriSecurityManger' for preventing java reflection)  
	 */
	@Test
	public void testCheckIndexOutOfBoundsException_usnigJavaRelection_badParameter_bitIndexIsLessThanZero() {		
		long maxBitNumber = 10;
		LimitedLongBitSet limitedLongBitSet = null;
		try {			
			limitedLongBitSet = new LimitedLongBitSet(maxBitNumber);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		long bitIndex = -1;
		@SuppressWarnings("unused")
		Object returnedValueObject = null;
		try {
			returnedValueObject = JunitUtil.genericInvokMethod(limitedLongBitSet, "throwExceptionIfIndexOutOfBound", 1, bitIndex);
		} catch(InvocationTargetException e) {
			Throwable causedException = e.getCause();
			if (null != causedException && causedException instanceof IndexOutOfBoundsException) {
				String errorMessage = causedException.getMessage();
				log.info("errorMessage=[{}]", errorMessage);
				
				final String exepectedErrorMessage = String.format("the parameter bitIndex[%d] is less than zero", bitIndex);
				
				assertEquals(errorMessage, exepectedErrorMessage);
			} else {
				log.warn(e.getMessage(), e);
				fail("InvocationTargetException error::"+e.getMessage());
			}
		
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("unknown error::"+e.getMessage());
		}
		
		/*if (null == returnedValueObject) {
			fail("the returned value object of LimitedLongBitSet class::checkIndexOutOfBoundsException method is null");
		}*/		
	}
}
