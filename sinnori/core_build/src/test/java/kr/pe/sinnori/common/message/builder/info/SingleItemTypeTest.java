package kr.pe.sinnori.common.message.builder.info;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;

public class SingleItemTypeTest {
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
	public void test_ItemTypeID가정말로키가맞는지그리고0부터순차적으로할당되었는지에대한테스트() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		int[] arrayOfSingleItemTypeID = new int[singleItemTypes.length];
		Arrays.fill(arrayOfSingleItemTypeID, -1);
		for (SingleItemType singleItemType : singleItemTypes) {
			int singleItemTypeID = singleItemType.getItemTypeID();
			try {
				arrayOfSingleItemTypeID[singleItemTypeID]=singleItemTypeID;
			} catch(IndexOutOfBoundsException e) {
				fail(String.format("singleItemType[%s] is bad, singleItemTypeID[%d] is out of the range[0 ~ %d]", singleItemType.toString(), singleItemTypeID, singleItemTypes.length-1));
			}
		}
		for (int i=0; i < arrayOfSingleItemTypeID.length; i++) {
			int singleItemTypeID = arrayOfSingleItemTypeID[i];
			if (-1 == singleItemTypeID) {
				fail(String.format("the singleItemTypeID[%d] is not found", i));
			}
		}
	}
}
