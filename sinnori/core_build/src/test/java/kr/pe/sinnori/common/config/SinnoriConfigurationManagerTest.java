package kr.pe.sinnori.common.config;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.config.itemvalue.AllDBCPPartConfiguration;
import kr.pe.sinnori.common.config.itemvalue.AllSubProjectPartConfiguration;
import kr.pe.sinnori.common.config.itemvalue.CommonPartConfiguration;
import kr.pe.sinnori.common.config.itemvalue.DBCPParConfiguration;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.type.SessionKey;

public class SinnoriConfigurationManagerTest extends AbstractJunitTest {

	
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
