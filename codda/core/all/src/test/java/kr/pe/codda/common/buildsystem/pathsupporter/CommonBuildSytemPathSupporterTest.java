package kr.pe.codda.common.buildsystem.pathsupporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import junitlib.AbstractJunitTest;

public class CommonBuildSytemPathSupporterTest extends AbstractJunitTest {
	@Test
	public void testGetCommonTempPathString() {
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedCommonTempPathString = new StringBuilder(installedPathString)
				.append(File.separator).append("temp").toString();
		
		log.info("expectedCommonTempPathString={}", expectedCommonTempPathString);
		
		if (!(new File(expectedCommonTempPathString)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualCommonTempPathString = CommonBuildSytemPathSupporter.getCommonTempPathString(installedPathString);
		
		assertEquals("Codda temp directory path validation", expectedCommonTempPathString, actualCommonTempPathString);
	}
	
	@Test
	public void testGetCommonLogPathString() {
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(installedPathString)
				.append(File.separator).append("log").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = CommonBuildSytemPathSupporter.getCommonLogPathString(installedPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetCommonResourcesPathString() {
		String installedPathString = installedPath.getAbsolutePath();
		
		String expectedValue = new StringBuilder(installedPathString)
				.append(File.separator).append("resources").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = CommonBuildSytemPathSupporter.getCommonResourcesPathString(installedPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetCommonMessageInfoDirectoryPathString() {
		String installedPathString = installedPath.getAbsolutePath();
		String expectedValue = new StringBuilder(CommonBuildSytemPathSupporter.getCommonResourcesPathString(installedPathString))
				.append(File.separator).append("message_info").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String actualValue = CommonBuildSytemPathSupporter.getCommonMessageInfoDirectoryPathString(installedPathString);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
	
	@Test
	public void testGetMessageInfoFilePathStringFromRootResources() {
		String installedPathString = installedPath.getAbsolutePath();
		String messageID = "SelfExnRes";
		String expectedValue = new StringBuilder(CommonBuildSytemPathSupporter.getCommonMessageInfoDirectoryPathString(installedPathString))
				.append(File.separator).append(messageID)
				.append(".xml").toString();
		
		log.info("expectedValue={}", expectedValue);
		
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}		
		
		String actualValue = CommonBuildSytemPathSupporter.getCommonMessageInfoFilePathString(installedPathString, messageID);
		
		assertEquals("the expected value comparison", expectedValue, actualValue);
	}
}
