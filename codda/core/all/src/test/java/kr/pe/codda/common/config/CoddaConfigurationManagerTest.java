package kr.pe.codda.common.config;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.config.subset.AllDBCPPartConfiguration;
import kr.pe.codda.common.config.subset.AllSubProjectPartConfiguration;
import kr.pe.codda.common.config.subset.CommonPartConfiguration;
import kr.pe.codda.common.config.subset.DBCPParConfiguration;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.type.SessionKey;

public class CoddaConfigurationManagerTest extends AbstractJunitTest {

	@Test
	public void testGetInstance() {
		CoddaConfiguration runningProjectConfiguration = 
				CoddaConfigurationManager.getInstance()
				
				.getRunningProjectConfiguration();
		AllDBCPPartConfiguration allDBCPPart = runningProjectConfiguration.getAllDBCPPartConfiguration();
		CommonPartConfiguration commonPart = runningProjectConfiguration.getCommonPartConfiguration();
		ProjectPartConfiguration mainProjectPart = runningProjectConfiguration.getMainProjectPartConfiguration();
		AllSubProjectPartConfiguration allSubProjectPart = runningProjectConfiguration.getAllSubProjectPartConfiguration();
		
		
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
			if (!commonPart.getRsaKeypairSourceOfSessionKey().equals(SessionKey.RSAKeypairSourceType.SERVER)) {
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
