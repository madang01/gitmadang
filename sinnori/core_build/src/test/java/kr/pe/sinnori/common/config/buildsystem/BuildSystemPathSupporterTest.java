package kr.pe.sinnori.common.config.buildsystem;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.buildsystem.BuildSystemPathSupporter;

public class BuildSystemPathSupporterTest {
	private Logger log = LoggerFactory
			.getLogger(BuildSystemPathSupporterTest.class);
	
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
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\resources\\logback.xml";
		
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
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\resources\\message_info";
		
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
	public void testGetWebClientAntPropertiesFilePath() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build\\web_build\\webAnt.properties";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(mainProjectName, sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	
	@Test
	public void testGetSessionKeyRSAKeypairPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\resources\\rsa_keypair";
		
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
				.getServerAntBuildXMLFilePathString(mainProjectName, sinnoriInstalledPathString);
		
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
				.getServerAPPINFPathString(mainProjectName, sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetDBCPConnPoolConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\resources\\dbcp\\dbcp.sample_base_db.properties";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getDBCPConfigFilePathString(mainProjectName, 
						sinnoriInstalledPathString,
						"sample_base_db");
		
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
				.getAppClientAntBuildXMLFilePathString(mainProjectName, 
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
				.getWebClientAntBuildXMLFilePathString(mainProjectName, 
						sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
}
