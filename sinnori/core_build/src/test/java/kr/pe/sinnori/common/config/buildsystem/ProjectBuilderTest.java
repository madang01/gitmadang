package kr.pe.sinnori.common.config.buildsystem;


import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.util.SequencedProperties;

public class ProjectBuilderTest {
	private Logger log = LoggerFactory
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
	public void testConstructor_badProjectBasePath_notExist() {
		String sinnoriInstalledPathString= "D:\\gitsinnori";
		String mainProjectName = "sample_test";		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			fail("this test must throw BuildSystemException");
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			if (!errorMessage.equals(String.format("the project base path[%s] does not exist", 
					BuildSystemPathSupporter.getProjectBasePathString(	sinnoriInstalledPathString)))) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}	
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testConstructor_badProjectBasePath_notDirectory() {		
		String sinnoriInstalledPathStringForTest= "D:\\gitsinnori\\testsinnori";
		File sinnoriInstalledPathForTest = new File(sinnoriInstalledPathStringForTest);
		boolean resultCreatingSinnoriInstalledPathForTest  = sinnoriInstalledPathForTest.mkdir();
		if (!resultCreatingSinnoriInstalledPathForTest) {
			fail("fail to create Sinnori installed path For Test");
		}
		sinnoriInstalledPathForTest.deleteOnExit();
		
		
		String proejctBasePathString = BuildSystemPathSupporter.getProjectBasePathString(sinnoriInstalledPathStringForTest);
		File proejctBasePath = new File(proejctBasePathString);
		
		try {
			proejctBasePath.createNewFile();
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			fail("fail to create file having project base path["+proejctBasePathString+"]");
		}
		
		proejctBasePath.deleteOnExit();
		
		
		String mainProjectName = "sample_test";		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathStringForTest, mainProjectName);
			
			fail("this test must throw BuildSystemException");
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			if (!errorMessage.equals(String.format("the project base path[%s] isn't a directory", 
					BuildSystemPathSupporter.getProjectBasePathString(	sinnoriInstalledPathStringForTest)))) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}	
		}
	}
	
	
	@SuppressWarnings("unused")
	@Test
	public void testConstructor_badProjectName_notDirectory() {
		String sinnoriInstalledPathStringForTest= "D:\\gitsinnori\\testsinnori";
		String mainProjectName = "sample_test";	
		
		
		File sinnoriInstalledPathForTest = new File(sinnoriInstalledPathStringForTest);
		boolean resultCreatingSinnoriInstalledPathForTest  = sinnoriInstalledPathForTest.mkdir();
		if (!resultCreatingSinnoriInstalledPathForTest) {
			fail("fail to create Sinnori installed path For Test");
		}
		sinnoriInstalledPathForTest.deleteOnExit();
		
		
		String proejctBasePathString = BuildSystemPathSupporter.getProjectBasePathString(sinnoriInstalledPathStringForTest);
		File proejctBasePath = new File(proejctBasePathString);
		
		try {
			proejctBasePath.createNewFile();
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			fail("fail to create file having project base path["+proejctBasePathString+"]");
		}
		
		proejctBasePath.deleteOnExit();
		
		
		String projectPathString= BuildSystemPathSupporter.getProjectPathString(mainProjectName,
				sinnoriInstalledPathStringForTest);
		
		File projectPath = new File(projectPathString);
		try {
			projectPath.createNewFile();
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			fail("fail to create file having project path["+projectPathString+"]");
		}
		
		projectPath.deleteOnExit();
		
			
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathStringForTest, mainProjectName);
			
			fail("this test must throw BuildSystemException");
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			if (!errorMessage.equals(String.format("the project path[%s] isn't a directory", 
					BuildSystemPathSupporter.getProjectPathString(	mainProjectName, sinnoriInstalledPathStringForTest)))) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}	
		}
	}
	
	
	@Test
	public void testWhetherOnlyProjectPathExists_projectPathExsitCase() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_base";
		boolean expectedWhetherProjectPathExists = true;
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			
			boolean whetherProjectPathExists = projectBuilder.whetherOnlyProjectPathExists();
			
			if (expectedWhetherProjectPathExists != whetherProjectPathExists) {
				fail("isValidSeverAntBuildXMLFile is false");
			}
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testWhetherOnlyProjectPathExists_projectPathNotExsitCase() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test2";
		boolean expectedWhetherOnlyProjectPathExists = false;
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			
			boolean whetherOnlyProjectPathExists = projectBuilder.whetherOnlyProjectPathExists();
			
			if (expectedWhetherOnlyProjectPathExists != whetherOnlyProjectPathExists) {
				fail("isValidSeverAntBuildXMLFile is false");
			}
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testCreateProject_AllTypeBuild() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";
		
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			if (projectBuilder.whetherOnlyProjectPathExists()) {
				projectBuilder.dropProject();
			}
			
			boolean isServer = true;
			boolean isAppClient = true;			
			boolean isWebClient = true;
			String servletSystemLibraryPathString = "D:\\apache-tomcat-8.5.11\\lib";
			File servletSystemLibraryPath = new File(servletSystemLibraryPathString);
			if (!servletSystemLibraryPath.exists()) {
				fail(new StringBuilder("the servelt system library path[")
						.append(servletSystemLibraryPathString)
						.append("] for web-client build doesn't exist, this test needs Tomcat").toString());
			}
			
			if (!servletSystemLibraryPath.isDirectory()) {
				fail(new StringBuilder("the servelt system library path[")
						.append(servletSystemLibraryPathString)
						.append("] for web-client build isn't a directory, this test needs Tomcat").toString());
			}			
			
			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testCreateProject_onlyServerBuild() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";
		
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			if (projectBuilder.whetherOnlyProjectPathExists()) {
				projectBuilder.dropProject();
			}
			
			boolean isServer = true;
			boolean isAppClient = false;			
			boolean isWebClient = false;
			String servletSystemLibraryPathString = "D:\\apache-tomcat-8.5.11\\lib";
			
			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCreateProject_onlyAppClientBuild() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";
		
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			if (projectBuilder.whetherOnlyProjectPathExists()) {
				projectBuilder.dropProject();
			}
			
			boolean isServer = false;
			boolean isAppClient = true;			
			boolean isWebClient = false;
			String servletSystemLibraryPathString = "D:\\apache-tomcat-8.5.11\\lib";
			
			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}	
	
	
	@Test
	public void testCreateProject_onlyWebClientBuild() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";
		
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			if (projectBuilder.whetherOnlyProjectPathExists()) {
				projectBuilder.dropProject();
			}
			
			boolean isServer = false;
			boolean isAppClient = false;			
			boolean isWebClient = true;
			String servletSystemLibraryPathString = "D:\\apache-tomcat-8.5.11\\lib";
			
			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}	
	
	@Test
	public void testChangeProjectState() {
		String sinnoriInstalledPathString= "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";
		
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			if (projectBuilder.whetherOnlyProjectPathExists()) {
				projectBuilder.dropProject();
			}
			
			boolean isServer = true;
			boolean isAppClient = true;			
			boolean isWebClient = true;
			String servletSystemLibraryPathString = "D:\\apache-tomcat-8.5.11\\lib";
			
			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
			
			boolean[] arraySelectedIsAppClient = {true, false};			
			boolean[] arrySelectedIsWebClient = {true, false};
			
			SinnoriConfiguration sinnoriConfiguration = new SinnoriConfiguration(sinnoriInstalledPathString, mainProjectName);
			SequencedProperties modifiedSinnoriConfigSequencedProperties = sinnoriConfiguration.getSinnoriConfigurationSequencedPropties();
			
			for (int i=0; i < arraySelectedIsAppClient.length; i++) {
				for (int j=0; j < arrySelectedIsWebClient.length; j++) {
					
					
					projectBuilder.changeProjectState(arraySelectedIsAppClient[i], arrySelectedIsWebClient[j], servletSystemLibraryPathString, modifiedSinnoriConfigSequencedProperties);
					
					if (arraySelectedIsAppClient[i]) {
						if (!projectBuilder.isValidAppClientAntBuildXMLFile()) {
							fail("This test expected that there is application build, but it doesn't exist");
						}
					} else {
						if (projectBuilder.isValidAppClientAntBuildXMLFile()) {
							fail("This test expected that there is no application build, but it exists");
						}
					}
					
					if (arrySelectedIsWebClient[j]) {
						if (!projectBuilder.isValidWebClientAntBuildXMLFile()) {
							fail("This test expected that there is web build, but it doesn't exist");
						}
						
						if (!projectBuilder.isValidWebRootXMLFile()) {
							fail("This test expected that there is web root, but it doesn't exist");
						}
					} else {
						if (projectBuilder.isValidWebClientAntBuildXMLFile()) {
							fail("This test expected that there is no web build, but it exists");
						}
						
						if (projectBuilder.isValidWebRootXMLFile()) {
							fail("This test expected that there is no web root, but it exists");
						}
					}
					
				}
			}			
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		} catch (SinnoriConfigurationException e) {
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
			
			boolean whetherOnlyProjectPathExists = projectBuilder.whetherOnlyProjectPathExists();
			if (whetherOnlyProjectPathExists) {
				projectBuilder.dropProject();
			}
			
			boolean isServer = true;
			boolean isAppClient = false;			
			boolean isWebClient = false;
			String servletSystemLibraryPathString = "D:\\apache-tomcat-8.5.11\\lib";
			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
			
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
