package kr.pe.codda.common.buildsystem.pathsupporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import junitlib.AbstractJunitTest;

public class AppClientBuildSystemPathSupporterTest extends AbstractJunitTest {
	@Test
	public void testGetAppClientBuildPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getClientBuildBasePathString(installedPathString, mainProjectName))
				.append(File.separator).append("app_build")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = AppClientBuildSystemPathSupporter
				.getAppClientBuildPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetAppClientBuildConfigFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(AppClientBuildSystemPathSupporter
				.getAppClientBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append("build.xml")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		String returnedValue = AppClientBuildSystemPathSupporter
				.getAppClientAntBuildXMLFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetAppClientIOSourcePath() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(AppClientBuildSystemPathSupporter
				.getAppClientBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath(File.separator))
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		String returnedValue = AppClientBuildSystemPathSupporter
				.getAppClientIOSourcePath(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	
}
