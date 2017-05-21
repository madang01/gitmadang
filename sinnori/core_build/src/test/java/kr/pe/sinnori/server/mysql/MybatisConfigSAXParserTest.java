package kr.pe.sinnori.server.mysql;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.vo.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.mysql.FileTypeResourceManager;
import kr.pe.sinnori.common.mysql.MybatisConfigXMLFileSAXParser;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.common.util.SequencedProperties;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class MybatisConfigSAXParserTest {
	private String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
	private String mainProjectName = "sample_test";
	
	@Before
	public void setup() {
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		
		SinnoriLogbackManger.getInstance().setup();
		
		SinnoriConfiguration sinnoriConfiguration = 
				SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		
		SequencedProperties sinnoriConfigSequencedProperties = sinnoriConfiguration.getSinnoriConfigurationSequencedPropties();
				
		sinnoriConfigSequencedProperties.setProperty("dbcp.name_list.value", "sample_base_db");
		sinnoriConfigSequencedProperties.setProperty("dbcp.sample_base_db.dbcp_confige_file.value", "D:\\gitsinnori\\sinnori\\project\\sample_test\\resources\\dbcp\\dbcp.sample_base_db.properties");
	}
	
	@Test
	public void testParse() {
		Logger log = LoggerFactory.getLogger(MybatisConfigSAXParserTest.class);
		
		MybatisConfigXMLFileSAXParser mybatisConfigSAXParser = null;
		try {
			mybatisConfigSAXParser = new MybatisConfigXMLFileSAXParser();
		} catch (SAXException e) {
			log.warn("1.SAXException", e);
			fail(e.getMessage());
		}		
		
		String serverMybatisConfigFileRelativePathString = null;
		{
			SinnoriConfiguration sinnoriConfiguration = 
					SinnoriConfigurationManager.getInstance()
					.getSinnoriRunningProjectConfiguration();
			
			ProjectPartConfiguration mainProjetPart = sinnoriConfiguration
					.getMainProjectPartConfiguration();
			
			serverMybatisConfigFileRelativePathString = mainProjetPart
					.getServerMybatisConfigFileRelativePathString();
		}
		
		String mainProjectResorucesPathString = BuildSystemPathSupporter
				.getProjectResourcesPathString(sinnoriInstalledPathString, mainProjectName);
		
		String mybatisConfigeFilePathString = CommonStaticUtil
				.getFilePathStringFromResourcePathAndRelativePathOfFile(
						mainProjectResorucesPathString,
						serverMybatisConfigFileRelativePathString);
		
		/*log.info("serverAPPINFResorucePathString={}", serverAPPINFResorucePathString);
		log.info("serverClassloaderMybatisConfigFileRelativePathString={}", serverClassloaderMybatisConfigFileRelativePathString);
		log.info("mybatisConfigeFilePathString={}", mybatisConfigeFilePathString);*/
		
		
		File mybatisConfigeFile = new File(mybatisConfigeFilePathString);

		FileTypeResourceManager fileTypeResourceManger = null;
		try {
			fileTypeResourceManger = mybatisConfigSAXParser.parse(mybatisConfigeFile);
		} catch (IllegalArgumentException | SAXException | IOException e) {
			log.warn("2.SAXException", e);
			
			fail(e.getMessage());
		}
		
		// log.info(fileTypeResourceManger.toString());
		
		fileTypeResourceManger.isModified();
		
		log.info("isModified={}", fileTypeResourceManger.isModified());
	}
}
