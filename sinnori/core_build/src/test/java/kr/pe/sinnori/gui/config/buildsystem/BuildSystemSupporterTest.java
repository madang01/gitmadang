package kr.pe.sinnori.gui.config.buildsystem;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.buildsystem.BuildSystemSupporter;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.exception.BuildSystemException;

public class BuildSystemSupporterTest {
	
	private static Logger log = LoggerFactory
			.getLogger(BuildSystemSupporterTest.class);
	
	private final String projectNameForTest = "sample_test";
	private final String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
	
	
		
	@Before
	public void setup() {
		// FIXME! 실제 배포시에는 막아야 한다.
		// fail("this test is system dependent test, you need to install Sinnori Software");
		
		SinnoriLogbackManger.getInstance().setup();
		
		File sinnoriInstalledPath = new File(sinnoriInstalledPathString);
		if (!sinnoriInstalledPath.exists()) {
			String errorMessage = new StringBuilder("Sinnori software installed path[")
			.append(sinnoriInstalledPathString).append("] does not exist").toString();
			fail(errorMessage);
		}
		
		
		if (!sinnoriInstalledPath.isDirectory()) {
			String errorMessage = new StringBuilder("Sinnori software installed path[")
			.append(sinnoriInstalledPathString).append("] is not a directory").toString();
			fail(errorMessage);
		}
		
		
	}
	
	@After
	public void end() {
		/*if (null != tempSinnoriInstalledPath) {
			try {
				FileUtils.forceDelete(tempSinnoriInstalledPath);
			} catch (IOException e) {
				e.printStackTrace();
				fail("fail to delete the temp sinnori installed path");
			}
		}*/
	}
	
	@Test
	public void testCreateNewMainProjectBuildSystem() {		
		try {
			BuildSystemSupporter.removeProjectDirectory(projectNameForTest, sinnoriInstalledPathString);
		} catch (BuildSystemException e) {
			log.info("fail to delete project path", e);
			fail(e.getMessage());
		}
		
		
		boolean isServer = true;
		String jvmOptionsOfServer = "";
		
		boolean isAppClient = true;
		String jvmOptionsOfAppClient = "";
		
		boolean isWebClient = true;
		String servletSystemLibrayPathString = "D:\\apache-tomcat-8.5.11\\lib";
		
		try {
			BuildSystemSupporter.createNewMainProjectBuildSystem(projectNameForTest, 
					sinnoriInstalledPathString,
					isServer, jvmOptionsOfServer,
					isAppClient, jvmOptionsOfAppClient,
					isWebClient, servletSystemLibrayPathString);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (BuildSystemException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		/*MainProject mainProjectInformation = null;
		try {
			mainProjectInformation = 
					new MainProject(projectNameForTest, sinnoriInstalledPathString, null);
		} catch (BuildSystemException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}*/
		
		//mainProjectInformation.loadSinnoriConfigSequencedProperties();
		
		/*mainProjectInformation.addNewDBCPName("tw_sinnoridb");
		mainProjectInformation.addNewSubProjectName("sample_test_sub1");*/
	}
	
	@Test
	public void testGetProjectBasePathString() {
		/*java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd:HHmmss");
		sdf.format(new java.util.Date());*/
		
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectBasePathString(sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetProjectPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectPathString(mainProjectName, sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetSinnoriConfigPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\config";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getSinnoriConfigPathString(mainProjectName, sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetSinnoriConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\config\\sinnori.properties";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getSinnoriConfigFilePathString(mainProjectName, sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetLogbackConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\config\\logback.xml";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getLogbackConfigFilePathString(mainProjectName, sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetMessageInfoPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\impl\\message\\info";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getMessageInfoPathString(mainProjectName, sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetAntPropertiesFilePath() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\ant.properties";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getAntBuiltInPropertiesFilePath(mainProjectName, sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	
	@Test
	public void testGetSessionKeyRSAKeypairPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\rsa_keypair";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getSessionKeyRSAKeypairPathString(mainProjectName, sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetServerBuildPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\server_build";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerBuildPathString(mainProjectName, sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetServerBuildConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\server_build\\build.xml";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerAntBuildFilePathString(mainProjectName, sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetAPPINFPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\server_build\\APP-INF";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getAPPINFPathString(mainProjectName, sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetDBCPConnPoolConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\server_build\\APP-INF\\resources\\kr\\pe\\sinnori\\impl\\mybatis\\tw_sinnoridb.properties";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getDBCPConfigFilePathString(mainProjectName, 
						sinnoriInstalledPathString,
						"tw_sinnoridb");
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetClientBuildBasePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getClientBuildBasePathString(mainProjectName, 
						sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetAppClientBuildPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build\\app_build";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getAppClientBuildPathString(mainProjectName, 
						sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetAppClientBuildConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build\\app_build\\build.xml";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getAppClientAntBuildFilePathString(mainProjectName, 
						sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetWebClientBuildPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build\\web_build";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		// FIXME!
		log.info(expectedValue);
		
		
		String returnedValue = BuildSystemPathSupporter
				.getWebClientBuildPathString(mainProjectName, 
						sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
		
	@Test
	public void testGetWebClientBuildConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build\\web_build\\build.xml";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getWebClientAntBuildFilePathString(mainProjectName, 
						sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
}
