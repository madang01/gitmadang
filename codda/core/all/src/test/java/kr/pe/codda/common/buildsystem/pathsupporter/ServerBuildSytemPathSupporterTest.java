package kr.pe.codda.common.buildsystem.pathsupporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import junitlib.AbstractJunitTest;

public class ServerBuildSytemPathSupporterTest extends AbstractJunitTest {

	
	@Test
	public void testGetServerBuildPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(ProjectBuildSytemPathSupporter
				.getProjectPathString(installedPathString, mainProjectName))
				.append(File.separator).append("server_build")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = ServerBuildSytemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerAntBuildXMLFilePathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(ServerBuildSytemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append("build.xml")
				.toString();
		
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = ServerBuildSytemPathSupporter
				.getServerAntBuildXMLFilePathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerAPPINFPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(ServerBuildSytemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append("APP-INF")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = ServerBuildSytemPathSupporter
				.getServerAPPINFPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerAPPINFClassPathString() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(ServerBuildSytemPathSupporter
				.getServerAPPINFPathString(installedPathString, mainProjectName))
				.append(File.separator).append("classes")
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		/*if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}*/
		
		
		String returnedValue = ServerBuildSytemPathSupporter
				.getServerAPPINFClassPathString(installedPathString, mainProjectName);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
	
	@Test
	public void testGetServerIOSourcePath() {
		String mainProjectName = "sample_base";
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(ServerBuildSytemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName))
				.append(File.separator).append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath(File.separator))
				.toString();
		
		log.info("expectedValue={}", expectedValue);
		
		/*if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}*/
		
		
		String returnedValue = ServerBuildSytemPathSupporter
				.getServerIOSourcePath(installedPathString, mainProjectName);
		
		log.info("returnedValue={}", returnedValue);
		
		assertEquals("the expected value comparison", expectedValue, returnedValue);
	}
}
