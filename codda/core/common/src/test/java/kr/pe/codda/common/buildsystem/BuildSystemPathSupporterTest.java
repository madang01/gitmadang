package kr.pe.codda.common.buildsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;

public class BuildSystemPathSupporterTest extends AbstractJunitTest {
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	

	@Test
	public void testGetRootTempPathString() {
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(installedPathString)
				.append(File.separator).append("temp").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = BuildSystemPathSupporter.getTempPathString(installedPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetRootLogPathString() {
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(installedPathString)
				.append(File.separator).append("log").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = BuildSystemPathSupporter.getLogPathString(installedPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetRootResourcesPathString() {
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(installedPathString)
				.append(File.separator).append("resources").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = BuildSystemPathSupporter.getRootResourcesPathString(installedPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetMessageInfoDirectoryPathStringFromSinnoriResources() {
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter.getRootResourcesPathString(installedPathString))
				.append(File.separator).append("message_info").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = BuildSystemPathSupporter.getMessageInfoDirectoryPathStringFromRootResources(installedPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetMessageInfoFilePathStringFromRootResources() {
		String installedPathString = installedPath.getAbsolutePath();
		String messageID = "SelfExnRes";
		String expectedValue = new StringBuilder(BuildSystemPathSupporter.getMessageInfoDirectoryPathStringFromRootResources(installedPathString))
				.append(File.separator).append(messageID)
				.append(".xml").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}		
		
		String actualValue = BuildSystemPathSupporter.getMessageInfoFilePathStringFromRootResources(installedPathString, messageID);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetProjectBasePathString() {
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(installedPathString)
				.append(File.separator).append("project").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = BuildSystemPathSupporter
				.getProjectBasePathString(installedPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetProjectPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectBasePathString(installedPathString))
				.append(File.separator).append(mainProjectName).toString();
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectConfigDirectoryPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectPathString(installedPathString, mainProjectName))
				.append(File.separator).append("config").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectConfigDirectoryPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectConfigFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectConfigDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.CONFIG_FILE_NAME).toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getProejctConfigFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectResourcesDirectoryPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectPathString(installedPathString, mainProjectName))
				.append(File.separator).append("resources").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	
	@Test
	public void testGetProjectLogbackConfigFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.LOGBACK_LOG_FILE_NAME).toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectLogbackConfigFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	
	
	@Test
	public void testGetProjectDBCPConfigFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator).append("dbcp")
				.append(File.separator).append("dbcp.sample_base_db.properties")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectDBCPConfigFilePathString(installedPathString, mainProjectName, "sample_base_db");
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectMessageInfoDirectoryPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator).append("message_info").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}		
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectMessageInfoDirectoryPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetProjectMessageInfoFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String messageID = "Echo";
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectMessageInfoDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator).append(messageID).append(".xml")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}		
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectMessageInfoFilePathString(installedPathString, mainProjectName, messageID);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetSessionKeyRSAKeypairPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator).append("rsa_keypair")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getSessionKeyRSAKeypairPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetSessionKeyRSAPublickeyFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getSessionKeyRSAKeypairPathString(installedPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.PUBLIC_KEY_FILE_NAME)
				.toString();
		
		/*log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}		*/
		
		String returnedValue = BuildSystemPathSupporter
				.getSessionKeyRSAPublickeyFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetSessionKeyRSAPrivatekeyFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getSessionKeyRSAKeypairPathString(installedPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.PRIVATE_KEY_FILE_NAME)
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		/*if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	*/	
		
		String returnedValue = BuildSystemPathSupporter
				.getSessionKeyRSAPrivatekeyFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerBuildPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectPathString(installedPathString, mainProjectName))
				.append(File.separator).append("server_build")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerAntBuildXMLFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append("build.xml")
				.toString();
		
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerAntBuildXMLFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerAPPINFPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append("APP-INF")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerAPPINFPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerAPPINFClassPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getServerAPPINFPathString(installedPathString, mainProjectName))
				.append(File.separator).append("classes")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		/*if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}*/
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerAPPINFClassPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	private static String getRelativePathWhereMessageIOSourceFilesAreLocated() {
		
		String temp = new StringBuilder("src.main.java.")
		.append(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME).toString();
		
		
		String relativePathWhereMessageIOSourceFilesAreLocated = null;
		if (File.separator.equals("/")) {
			relativePathWhereMessageIOSourceFilesAreLocated = temp.replaceAll("\\.", "/");
		} else {
			relativePathWhereMessageIOSourceFilesAreLocated = temp.replaceAll("\\.", "\\\\");
		}
		
		
		return relativePathWhereMessageIOSourceFilesAreLocated;
	}
	
	@Test
	public void testGetServerIOSourcePath() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append(getRelativePathWhereMessageIOSourceFilesAreLocated())
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		/*if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}*/
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerIOSourcePath(installedPathString, mainProjectName);
		
		log.info("returnedValue={}", returnedValue);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetClientBuildBasePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectPathString(installedPathString, mainProjectName))
				.append(File.separator).append("client_build")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getClientBuildBasePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetAppClientBuildPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getClientBuildBasePathString(installedPathString, mainProjectName))
				.append(File.separator).append("app_build")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getAppClientBuildPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetAppClientBuildConfigFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getAppClientBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append("build.xml")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		String returnedValue = BuildSystemPathSupporter
				.getAppClientAntBuildXMLFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetAppClientIOSourcePath() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getAppClientBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append(getRelativePathWhereMessageIOSourceFilesAreLocated())
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		String returnedValue = BuildSystemPathSupporter
				.getAppClientIOSourcePath(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebClientBuildPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getClientBuildBasePathString(installedPathString, mainProjectName))
				.append(File.separator).append("web_build")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebClientBuildConfigFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append("build.xml")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getWebClientAntBuildXMLFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebClientAntPropertiesFilePath() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.WEBCLIENT_ANT_PROPRTEIS_FILE_NAME_VALUE).toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		
		String returnedValue = BuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	
	@Test
	public void testGetWebClinetIOSourcePath() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append(getRelativePathWhereMessageIOSourceFilesAreLocated()).toString();
		
		log.info("expectedValue={}", expectedValue);
		
		/*if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		*/
		String returnedValue = BuildSystemPathSupporter
				.getWebClinetIOSourcePath(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebRootBasePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getProjectPathString(installedPathString, mainProjectName))
				.append(File.separator).append("web_app_base").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		
		String returnedValue = BuildSystemPathSupporter
				.getWebRootBasePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebUploadPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getWebRootBasePathString(installedPathString, mainProjectName))
				.append(File.separator).append("upload").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		
		String returnedValue = BuildSystemPathSupporter
				.getWebUploadPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebTempPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(BuildSystemPathSupporter
				.getWebRootBasePathString(installedPathString, mainProjectName))
				.append(File.separator).append("temp").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		/*if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	*/
		
		String returnedValue = BuildSystemPathSupporter
				.getWebTempPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
}
