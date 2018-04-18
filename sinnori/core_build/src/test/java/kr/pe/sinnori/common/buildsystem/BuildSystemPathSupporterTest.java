package kr.pe.sinnori.common.buildsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;

public class BuildSystemPathSupporterTest extends AbstractJunitTest {
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	

	@Test
	public void testGetSinnoriTempPathString() {
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(sinnoriInstalledPathString)
				.append(File.separator).append("temp").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = BuildSystemPathSupporter.getSinnoriTempPathString(sinnoriInstalledPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetSinnoriLogPathString() {
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(sinnoriInstalledPathString)
				.append(File.separator).append("log").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = BuildSystemPathSupporter.getSinnoriLogPathString(sinnoriInstalledPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetSinnoriResourcesPathString() {
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(sinnoriInstalledPathString)
				.append(File.separator).append("resources").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = BuildSystemPathSupporter.getSinnoriResourcesPathString(sinnoriInstalledPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetMessageInfoDirectoryPathStringFromSinnoriResources() {
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter.getSinnoriResourcesPathString(sinnoriInstalledPathString))
				.append(File.separator).append("message_info").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = BuildSystemPathSupporter.getMessageInfoDirectoryPathStringFromSinnoriResources(sinnoriInstalledPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetMessageInfoFilePathStringFromSinnoriResources() {
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String messageID = "SelfExnRes";
		String expectedValue = new StringBuilder(BuildSystemPathSupporter.getMessageInfoDirectoryPathStringFromSinnoriResources(sinnoriInstalledPathString))
				.append(File.separator).append(messageID)
				.append(".xml").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = BuildSystemPathSupporter.getMessageInfoFilePathStringFromSinnoriResources(sinnoriInstalledPathString, messageID);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetProjectBasePathString() {
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(sinnoriInstalledPathString)
				.append(File.separator).append("project").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = BuildSystemPathSupporter
				.getProjectBasePathString(sinnoriInstalledPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetProjectPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectBasePathString(sinnoriInstalledPathString))
				.append(File.separator).append(mainProjectName).toString();
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectPathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectConfigDirectoryPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("config").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectConfigDirectoryPathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectConfigDirectoryPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.SINNORI_CONFIG_FILE_NAME).toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getProejctConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectResourcesDirectoryPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("resources").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectResourcesDirectoryPathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	
	@Test
	public void testGetProjectLogbackConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectResourcesDirectoryPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.SINNORI_LOGBACK_LOG_FILE_NAME).toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	
	
	@Test
	public void testGetProjectDBCPConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectResourcesDirectoryPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("dbcp")
				.append(File.separator).append("dbcp.sample_base_db.properties")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectDBCPConfigFilePathString(sinnoriInstalledPathString, mainProjectName, "sample_base_db");
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectMessageInfoDirectoryPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectResourcesDirectoryPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("message_info").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}		
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectMessageInfoDirectoryPathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectMessageInfoFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String messageID = "Echo";
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectMessageInfoDirectoryPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append(messageID).append(".xml")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}		
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectMessageInfoFilePathString(sinnoriInstalledPathString, mainProjectName, messageID);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetSessionKeyRSAKeypairPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectResourcesDirectoryPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("rsa_keypair")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getSessionKeyRSAKeypairPathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetSessionKeyRSAPublickeyFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getSessionKeyRSAKeypairPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.PUBLIC_KEY_FILE_NAME)
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}		
		
		String returnedValue = BuildSystemPathSupporter
				.getSessionKeyRSAPublickeyFilePathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetSessionKeyRSAPrivatekeyFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getSessionKeyRSAKeypairPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.PRIVATE_KEY_FILE_NAME)
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}		
		
		String returnedValue = BuildSystemPathSupporter
				.getSessionKeyRSAPrivatekeyFilePathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerBuildPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("server_build")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		// String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\server_build";
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerAntBuildXMLFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("build.xml")
				.toString();
		
		// String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\server_build\\build.xml";
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerAPPINFPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("APP-INF")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerAPPINFPathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerAPPINFClassPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getServerAPPINFPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("classes")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerAPPINFClassPathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	private static String getRelativePathWhereMessageIOSourceFilesAreLocated() {
		return new StringBuilder("src")
				.append(File.separator).append("main")
				.append(File.separator).append("java")
				.append(File.separator).append("kr")
				.append(File.separator).append("pe")
				.append(File.separator).append("sinnori")
				.append(File.separator).append("impl")
				.append(File.separator).append("message")
				.toString();
	}
	
	@Test
	public void testGetServerIOSourcePath() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append(getRelativePathWhereMessageIOSourceFilesAreLocated())
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerIOSourcePath(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetClientBuildBasePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("client_build")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		// String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build";
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getClientBuildBasePathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetAppClientBuildPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getClientBuildBasePathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("app_build")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		// String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build";
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetAppClientBuildConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("build.xml")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		String returnedValue = BuildSystemPathSupporter
				.getAppClientAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetAppClientIOSourcePath() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append(getRelativePathWhereMessageIOSourceFilesAreLocated())
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		String returnedValue = BuildSystemPathSupporter
				.getAppClientIOSourcePath(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebClientBuildPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getClientBuildBasePathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("web_build")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		// String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build";
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebClientBuildConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("build.xml")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		
		// String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build\\web_build\\build.xml";
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getWebClientAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebClientAntPropertiesFilePath() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.WEBCLIENT_ANT_PROPRTEIS_FILE_NAME_VALUE).toString();
		
		log.info("expectedValue={}", expectedValue);
		
		// String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build\\web_build\\webAnt.properties";
		
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		
		String returnedValue = BuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	
	@Test
	public void testGetWebClinetIOSourcePath() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append(getRelativePathWhereMessageIOSourceFilesAreLocated()).toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		
		String returnedValue = BuildSystemPathSupporter
				.getWebClinetIOSourcePath(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebRootBasePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("web_app_base").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		
		String returnedValue = BuildSystemPathSupporter
				.getWebRootBasePathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebUploadPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getWebRootBasePathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("upload").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		
		String returnedValue = BuildSystemPathSupporter
				.getWebUploadPathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebTempPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getWebRootBasePathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator).append("temp").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		
		String returnedValue = BuildSystemPathSupporter
				.getWebTempPathString(sinnoriInstalledPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
}
