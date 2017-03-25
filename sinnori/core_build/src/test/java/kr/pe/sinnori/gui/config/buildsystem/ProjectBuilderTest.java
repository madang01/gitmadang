package kr.pe.sinnori.gui.config.buildsystem;


import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.buildsystem.ProjectBuilder;
import kr.pe.sinnori.common.exception.BuildSystemException;

public class ProjectBuilderTest {
	private static Logger log = LoggerFactory
			.getLogger(BuildSystemSupporterTest.class);
	
	
	
	@SuppressWarnings("unused")
	@Test
	public void testConstructor_badSinnoriInstalledPath() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori2";
		String mainProjectName = "sample_test";
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			if (!errorMessage.equals(String.format("the Sinnori installed path[%s] does not exist", sinnoriInstalledPathString))) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}	
		}
		
		
		sinnoriInstalledPathString= "D:\\gitsinnori\\.gitignore";
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			if (!errorMessage.equals(String.format("the Sinnori installed path[%s] isn't a directory", sinnoriInstalledPathString))) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}	
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testConstructor_badProjectName() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test2";
		
		mainProjectName = "sample_file";
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			if (!errorMessage.equals(String.format("the project path[%s] isn't a directory", BuildSystemPathSupporter.getProjectPathString(mainProjectName,
					sinnoriInstalledPathString)))) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}	
		}
	}
	
	
	@SuppressWarnings("unused")
	@Test
	public void testConstructor_ok_oldProjectPath() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
		} catch (BuildSystemException e) {
			fail(e.getMessage());
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testConstructor_ok_newProjectPath() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test2";
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
		} catch (BuildSystemException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testWhetherProjectPathExists_true() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";
		boolean expectedWhetherProjectPathExists = true;
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			
			boolean whetherProjectPathExists = projectBuilder.whetherProjectPathExists();
			
			if (expectedWhetherProjectPathExists != whetherProjectPathExists) {
				fail("isValidSeverAntBuildXMLFile is false");
			}
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testWhetherProjectPathExists_false() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test2";
		boolean expectedWhetherProjectPathExists = false;
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			
			boolean whetherProjectPathExists = projectBuilder.whetherProjectPathExists();
			
			if (expectedWhetherProjectPathExists != whetherProjectPathExists) {
				fail("isValidSeverAntBuildXMLFile is false");
			}
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testCreate() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";
		
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			if (projectBuilder.whetherProjectPathExists()) {
				projectBuilder.destory();
			}
			
			boolean isServer = true;
			boolean isAppClient = true;			
			boolean isWebClient = true;
			String servletSystemLibraryPathString = "D:\\apache-tomcat-8.5.11\\lib";
			
			projectBuilder.create(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	
	
	@Test
	public void testisValidSeverAntBuildXMLFile() {	
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			
			boolean isValidSeverAntBuildXMLFile = projectBuilder.isValidSeverAntBuildXMLFile();
			
			if (!isValidSeverAntBuildXMLFile) {
				fail("isValidSeverAntBuildXMLFile is false");
			}
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
}
