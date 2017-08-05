package kr.pe.sinnori.common.config;

import static org.junit.Assert.fail;

import java.util.List;

import kr.pe.sinnori.common.config.itemvalue.AllDBCPPartConfiguration;
import kr.pe.sinnori.common.config.itemvalue.AllSubProjectPartConfiguration;
import kr.pe.sinnori.common.config.itemvalue.CommonPartConfiguration;
import kr.pe.sinnori.common.config.itemvalue.DBCPParConfiguration;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
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
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				"sample_base");
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				"D:\\gitsinnori\\sinnori");
		
		SinnoriLogbackManger.getInstance().setup();
		
	}
	
	@Test
	public void testGetInstance() {
		SinnoriConfiguration sinnoriRunningProjectConfiguration = 
				SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		AllDBCPPartConfiguration allDBCPPart = sinnoriRunningProjectConfiguration.getAllDBCPPartConfiguration();
		CommonPartConfiguration commonPart = sinnoriRunningProjectConfiguration.getCommonPartConfiguration();
		ProjectPartConfiguration mainProjectPart = sinnoriRunningProjectConfiguration.getMainProjectPartConfiguration();
		AllSubProjectPartConfiguration allSubProjectPart = sinnoriRunningProjectConfiguration.getAllSubProjectPartConfiguration();
		
		
		List<String> dbcpNameList = allDBCPPart.getDBCPNameList();
		for (String dbcpName : dbcpNameList) {
			DBCPParConfiguration dbcpPart = allDBCPPart.getDBCPPartConfiguration(dbcpName);
			
			if (null == dbcpPart) {
				log.info("dbcpPart[{}] is null", dbcpName);
				fail("dbcpPart is null");
			}
			
			
			if (dbcpPart.toString().indexOf("null") >= 0) {
				fail("Maybe dbcp part's one more variables are null");
			}
			
			// log.info(dbcpPart.toString());
		}
		
		log.info(commonPart.toString());
		
		if (commonPart.toString().indexOf("null") >= 0) {
			/** if RSA Keypair source is API, then rsaKeyPairPathOfSessionKey is null. so first null no problem. */
			if (!commonPart.getRsaKeypairSourceOfSessionKey().equals(CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.SERVER)) {
				fail("Maybe common part's one more variables are null");
			}
			
			/** second null check */
			if (commonPart.toString().indexOf("null", commonPart.toString().indexOf("rsaKeySizeOfSessionKey")) >= 0) {
				fail("Maybe common part's three more variables are null");
			}
		}
		
		if (mainProjectPart.toString().indexOf("null") >= 0) {
			fail("Maybe main project part's one more variables are null");
		}
		
		List<String> subProjectNameList = allSubProjectPart.getSubProjectNamelist();
		for (String subProjectName : subProjectNameList) {
			ProjectPartConfiguration projectPart = allSubProjectPart.getSubProjectPartConfiguration(subProjectName);
			
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
