package kr.pe.sinnori.common.message.builder.info;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;

public class SingleItemTypeMangerTest {
	Logger log = null;

	final String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
	final String mainProjectName = "sample_base";

	@Before
	public void setup() {
		LOG_TYPE logType = LOG_TYPE.SERVER;
		String logbackConfigFilePathString = BuildSystemPathSupporter
				.getLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		String sinnoriLogPathString = BuildSystemPathSupporter.getLogPathString(sinnoriInstalledPathString,
				mainProjectName, logType);

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH, sinnoriLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);

		log = LoggerFactory.getLogger(SingleItemTypeMangerTest.class);
	}
	
	@Test
	public void testGetSingleItemType_sigleItemID를통해얻은SigleItemType맞는지검사() {
		for (SingleItemType expectedSingleItemType : SingleItemType.values()) {
			SingleItemType actualSingleItemType = SingleItemTypeManger.getInstance().getSingleItemType(expectedSingleItemType.getItemTypeID());
			
			assertEquals("sigleItemID 를 통해 얻은 SigleItemType 맞는지 검사", expectedSingleItemType, actualSingleItemType);
		}
	}
}
