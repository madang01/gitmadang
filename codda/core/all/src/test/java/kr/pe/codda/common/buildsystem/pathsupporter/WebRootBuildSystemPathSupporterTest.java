package kr.pe.codda.common.buildsystem.pathsupporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import junitlib.AbstractJunitTest;

public class WebRootBuildSystemPathSupporterTest extends AbstractJunitTest {
	@Test
	public void testGetWebRootBasePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectPathString(installedPathString, mainProjectName))
				.append(File.separator).append("web_app_base").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		
		String returnedValue = WebRootBuildSystemPathSupporter
				.getWebRootBasePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebUploadPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(WebRootBuildSystemPathSupporter
				.getWebRootBasePathString(installedPathString, mainProjectName))
				.append(File.separator).append("upload").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		
		String returnedValue = WebRootBuildSystemPathSupporter
				.getWebUploadPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetWebTempPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(WebRootBuildSystemPathSupporter
				.getWebRootBasePathString(installedPathString, mainProjectName))
				.append(File.separator).append("temp").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		/*if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	*/
		
		String returnedValue = WebRootBuildSystemPathSupporter
				.getWebTempPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
}
