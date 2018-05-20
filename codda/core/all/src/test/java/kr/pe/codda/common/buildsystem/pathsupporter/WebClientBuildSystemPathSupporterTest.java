package kr.pe.codda.common.buildsystem.pathsupporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;

public class WebClientBuildSystemPathSupporterTest extends AbstractJunitTest {
	@Test
	public void testGetWebClientBuildPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getClientBuildBasePathString(installedPathString, mainProjectName))
				.append(File.separator).append("web_build")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebClientBuildConfigFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append("build.xml")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = WebClientBuildSystemPathSupporter
				.getWebClientAntBuildXMLFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebClientAntPropertiesFilePath() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append(CommonStaticFinalVars.WEBCLIENT_ANT_PROPRTEIS_FILE_NAME_VALUE).toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		
		String returnedValue = WebClientBuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	
	@Test
	public void testGetWebClinetIOSourcePath() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath(File.separator)).toString();
		
		log.info("expectedValue={}", expectedValue);
		
		/*if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		*/
		String returnedValue = WebClientBuildSystemPathSupporter
				.getWebClinetIOSourcePath(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
}
