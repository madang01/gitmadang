package kr.pe.sinnori.common.config;

import static org.junit.Assert.fail;

import java.util.List;

import kr.pe.sinnori.common.config.valueobject.AllDBCPPart;
import kr.pe.sinnori.common.config.valueobject.AllSubProjectPart;
import kr.pe.sinnori.common.config.valueobject.CommonPart;
import kr.pe.sinnori.common.config.valueobject.DBCPPart;
import kr.pe.sinnori.common.config.valueobject.ProjectPart;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinnoriConfigurationManagerTest {
	Logger log = LoggerFactory
			.getLogger(SinnoriConfigurationManagerTest.class);
	
	@Before
	public void setup() {
		SinnoriLogbackManger.getInstance().setup();
		
		
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				"sample_test");
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				"D:\\gitsinnori\\sinnori");
		
		
	}
	
	@Test
	public void testGetInstance() {
		SinnoriConfigurationManager sinnoriConfigurationManager = SinnoriConfigurationManager.getInstance();
		AllDBCPPart allDBCPPart = sinnoriConfigurationManager.getAllDBCPPart();
		CommonPart commonPart = sinnoriConfigurationManager.getCommonPart();
		ProjectPart mainProjectPart = sinnoriConfigurationManager.getMainProjectPart();
		AllSubProjectPart allSubProjectPart = sinnoriConfigurationManager.getAllSubProjectPart();
		
		List<String> dbcpNameList = allDBCPPart.getDBCPNameList();
		for (String dbcpName : dbcpNameList) {
			DBCPPart dbcpPart = allDBCPPart.getDBCPPart(dbcpName);
			
			if (null == dbcpPart) {
				log.info("dbcpPart[{}] is null", dbcpName);
				fail("dbcpPart is null");
			}
			
			
			if (dbcpPart.toString().indexOf("null") >= 0) {
				fail("Maybe dbcp part's one more variables are null");
			}
			
			// log.info(dbcpPart.toString());
		}
		
		// log.info(commonPart.toString());
		if (commonPart.toString().indexOf("null") >= 0) {
			fail("Maybe common part's one more variables are null");
		}
		
		if (mainProjectPart.toString().indexOf("null") >= 0) {
			fail("Maybe main project part's one more variables are null");
		}
		
		List<String> subProjectNameList = allSubProjectPart.getSubProjectNamelist();
		for (String subProjectName : subProjectNameList) {
			ProjectPart projectPart = allSubProjectPart.getSubProjectPart(subProjectName);
			
			if (null == projectPart) {
				log.info("sub projectPart[{}] is null", subProjectName);
				fail("projectPart is null");
			}
			
			// log.info(projectPart.toString());
			if (projectPart.toString().indexOf("null") >= 0) {
				fail("Maybe sub project part's one more variables are null");
			}
		}
	}
}
