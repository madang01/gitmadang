package kr.pe.sinnori.common.config.iteminfo;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;
import kr.pe.sinnori.common.util.SequencedProperties;

public class SinnoriItemIDInfoMangerTest {
	Logger log = LoggerFactory
			.getLogger(SinnoriItemIDInfoMangerTest.class);
	
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
	public void testGetInstance() {
		SinnoriItemIDInfoManger.getInstance();
	}
	
	@Test
	public void testGetNewSinnoriConfigSequencedProperties() {
		String mainProjectName = "sample_test";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		SequencedProperties newSinnoriProperties = SinnoriItemIDInfoManger.getInstance()
				.getNewSinnoriConfigSequencedProperties(sinnoriInstalledPathString, mainProjectName);
		log.info(newSinnoriProperties.toString());
		
		/*try {
			SequencedPropertiesUtil.createNewSequencedPropertiesFile(newSinnoriProperties, "this file is the project "+mainProjectName+"'s configuration properties file", 
					"D:\\temp.properties", CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			e.printStackTrace();
			org.junit.Assert.fail(e.getMessage());
		}*/
	}
	
	
}
