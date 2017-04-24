package kr.pe.sinnori.common.config.buildsystem;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.OS;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.buildsystem.MainProjectBuildSystemState;
import kr.pe.sinnori.common.buildsystem.ProjectBuilder;
import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.util.SequencedProperties;

public class ProjectBuilderTest {
	private Logger log = LoggerFactory.getLogger(BuildSystemSupporterTest.class);

	final int EXIT_SUCCESS = 0;

	@SuppressWarnings("unused")
	@Test
	public void testConstructor_badSinnoriInstalledPath_notExist() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori2";
		String mainProjectName = "sample_test";
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			fail("this test must throw BuildSystemException");
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			if (!errorMessage.equals(
					String.format("the Sinnori installed path[%s] does not exist", sinnoriInstalledPathString))) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testConstructor_badSinnoriInstalledPath_notDirecotry() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\.gitignore";
		String mainProjectName = "sample_test";

		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			fail("this test must throw BuildSystemException");
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			if (!errorMessage.equals(
					String.format("the Sinnori installed path[%s] isn't a directory", sinnoriInstalledPathString))) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void testConstructor_badProjectBasePath_notExist() {
		String sinnoriInstalledPathString = "D:\\gitsinnori";
		String mainProjectName = "sample_test";
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);

			fail("this test must throw BuildSystemException");
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			if (!errorMessage.equals(String.format("the project base path[%s] does not exist",
					BuildSystemPathSupporter.getProjectBasePathString(sinnoriInstalledPathString)))) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void testConstructor_badProjectBasePath_notDirectory() {
		String sinnoriInstalledPathStringForTest = "D:\\gitsinnori\\testsinnori2";
		String mainProjectName = "sample_test";
		
		File sinnoriInstalledPathForTest = new File(sinnoriInstalledPathStringForTest);
		boolean resultCreatingSinnoriInstalledPathForTest = sinnoriInstalledPathForTest.mkdir();
		if (!resultCreatingSinnoriInstalledPathForTest) {
			fail("fail to create Sinnori installed path For Test");
		}
		sinnoriInstalledPathForTest.deleteOnExit();

		String proejctBasePathString = BuildSystemPathSupporter
				.getProjectBasePathString(sinnoriInstalledPathStringForTest);
		File proejctBasePath = new File(proejctBasePathString);

		try {
			proejctBasePath.createNewFile();
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			fail("fail to create file having project base path[" + proejctBasePathString + "]");
		}

		proejctBasePath.deleteOnExit();

		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathStringForTest, mainProjectName);

			fail("this test must throw BuildSystemException");
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			if (!errorMessage.equals(String.format("the project base path[%s] isn't a directory",
					BuildSystemPathSupporter.getProjectBasePathString(sinnoriInstalledPathStringForTest)))) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void testConstructor_badProjectName_notDirectory() {
		String sinnoriInstalledPathStringForTest = "D:\\gitsinnori\\testsinnori";
		String badMainProjectName = "sample_notdir";

		File sinnoriInstalledPathForTest = new File(sinnoriInstalledPathStringForTest);
		boolean resultCreatingSinnoriInstalledPathForTest = sinnoriInstalledPathForTest.mkdirs();
		if (!resultCreatingSinnoriInstalledPathForTest) {
			fail("fail to create Sinnori installed path For Test");
		}
		sinnoriInstalledPathForTest.deleteOnExit();

		String proejctBasePathString = BuildSystemPathSupporter
				.getProjectBasePathString(sinnoriInstalledPathStringForTest);
		File proejctBasePath = new File(proejctBasePathString);

		boolean isSuccess = proejctBasePath.mkdirs();
		if (!isSuccess) {
			fail("fail to create file having project base path[" + proejctBasePathString + "]");
		}
		
		proejctBasePath.deleteOnExit();

		String projectPathString = BuildSystemPathSupporter.getProjectPathString(sinnoriInstalledPathStringForTest,
				badMainProjectName);

		File projectPath = new File(projectPathString);
		try {
			projectPath.createNewFile();
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			fail("fail to create file having project path[" + projectPathString + "]");
		}

		projectPath.deleteOnExit();

		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathStringForTest, badMainProjectName);

			fail("this test must throw BuildSystemException");
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			if (!errorMessage.equals(String.format("the project path[%s] isn't a directory", BuildSystemPathSupporter
					.getProjectPathString(sinnoriInstalledPathStringForTest, badMainProjectName)))) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}
		}
	}

	@Test
	public void testWhetherOnlyProjectPathExists_projectPathExsitCase() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
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
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_noproject";
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
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";

		boolean isServer = true;
		boolean isAppClient = true;
		boolean isWebClient = true;
		String servletSystemLibraryPathString = "D:\\apache-tomcat-8.5.11\\lib";

		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);

			if (projectBuilder.whetherOnlyProjectPathExists()) {
				projectBuilder.dropProject();
			}

			File servletSystemLibraryPath = new File(servletSystemLibraryPathString);
			if (!servletSystemLibraryPath.exists()) {
				fail(new StringBuilder("the servelt system library path[").append(servletSystemLibraryPathString)
						.append("] for web-client build doesn't exist, this test needs Tomcat").toString());
			}

			if (!servletSystemLibraryPath.isDirectory()) {
				fail(new StringBuilder("the servelt system library path[").append(servletSystemLibraryPathString)
						.append("] for web-client build isn't a directory, this test needs Tomcat").toString());
			}

			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		/** check each build type making using Ant */
		if (isServer) {
			checkAntBuildForServer(sinnoriInstalledPathString, mainProjectName);
		}
		if (isAppClient) {
			checkAntBuildForAppClient(sinnoriInstalledPathString, mainProjectName);
		}
		if (isWebClient) {
			checkAntBuildForWebClient(sinnoriInstalledPathString, mainProjectName);
		}
	}

	

	private void checkAntBuildForServer(String sinnoriInstalledPathString, String mainProjectName) {
		org.apache.commons.exec.CommandLine antCommandLine = null;
		if (OS.isFamilyUnix()) {
			antCommandLine = new CommandLine("ant");
		} else if (OS.isFamilyWindows()) {
			antCommandLine = new CommandLine("ant.bat");
		} else {
			fail("unknown OS");
		}

		antCommandLine.addArgument("all");

		Executor antExecutor = new DefaultExecutor();
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);

		antExecutor.setExitValue(EXIT_SUCCESS);
		antExecutor.setWatchdog(watchdog);

		String severBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(sinnoriInstalledPathString,
				mainProjectName);
		File severBuildPath = new File(severBuildPathString);
		antExecutor.setWorkingDirectory(severBuildPath);

		try {
			antExecutor.execute(antCommandLine, resultHandler);
		} catch (ExecuteException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		try {
			resultHandler.waitFor();
		} catch (InterruptedException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		int exitCode = resultHandler.getExitValue();

		if (EXIT_SUCCESS != exitCode) {
			fail("ant exist code[" + exitCode + "] is not a expected exit code 1");
		}

		log.info("success server ant build");
	}

	private void checkAntBuildForAppClient(String sinnoriInstalledPathString, String mainProjectName) {
		org.apache.commons.exec.CommandLine antCommandLine = null;
		if (OS.isFamilyUnix()) {
			antCommandLine = new CommandLine("ant");
		} else if (OS.isFamilyWindows()) {
			antCommandLine = new CommandLine("ant.bat");
		} else {
			fail("unknown OS");
		}

		antCommandLine.addArgument("all");

		Executor antExecutor = new DefaultExecutor();
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);

		antExecutor.setExitValue(EXIT_SUCCESS);
		antExecutor.setWatchdog(watchdog);

		String appClientBuildPathString = BuildSystemPathSupporter
				.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName);
		File appClientBuildPath = new File(appClientBuildPathString);
		antExecutor.setWorkingDirectory(appClientBuildPath);

		try {
			antExecutor.execute(antCommandLine, resultHandler);
		} catch (ExecuteException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		try {
			resultHandler.waitFor();
		} catch (InterruptedException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		int exitCode = resultHandler.getExitValue();

		if (EXIT_SUCCESS != exitCode) {
			fail("ant exist code[" + exitCode + "] is not a expected exit code 1");
		}

		log.info("success app-client ant build");
	}

	private void checkAntBuildForWebClient(String sinnoriInstalledPathString, String mainProjectName) {
		org.apache.commons.exec.CommandLine antCommandLine = null;
		if (OS.isFamilyUnix()) {
			antCommandLine = new CommandLine("ant");
		} else if (OS.isFamilyWindows()) {
			antCommandLine = new CommandLine("ant.bat");
		} else {
			fail("unknown OS");
		}

		antCommandLine.addArgument("all");

		Executor antExecutor = new DefaultExecutor();
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);

		antExecutor.setExitValue(EXIT_SUCCESS);
		antExecutor.setWatchdog(watchdog);

		String webClientBuildPathString = BuildSystemPathSupporter
				.getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName);
		File webClientBuildPath = new File(webClientBuildPathString);
		antExecutor.setWorkingDirectory(webClientBuildPath);

		try {
			antExecutor.execute(antCommandLine, resultHandler);
		} catch (ExecuteException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		try {
			resultHandler.waitFor();
		} catch (InterruptedException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		int exitCode = resultHandler.getExitValue();

		if (EXIT_SUCCESS != exitCode) {
			fail("ant exist code[" + exitCode + "] is not a expected exit code 1");
		}

		log.info("success web-client ant build");
	}

	/*@Test
	public void checkAntBuildForSever() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";

		checkAntBuildForServer(sinnoriInstalledPathString, mainProjectName);
	}

	@Test
	public void checkAntBuildForAppClient() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";

		checkAntBuildForAppClient(sinnoriInstalledPathString, mainProjectName);
	}

	@Test
	public void checkAntBuildForWebClient() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";

		checkAntBuildForWebClient(sinnoriInstalledPathString, mainProjectName);
	}*/

	@Test
	public void testCreateProject_onlyServerBuild() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
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
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
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
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
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
	public void testisValidSeverAntBuildXMLFile() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
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

			boolean isValidSeverAntBuildXMLFile = projectBuilder.isValidServerAntBuildXMLFile();

			if (!isValidSeverAntBuildXMLFile) {
				fail("isValidSeverAntBuildXMLFile is false");
			}
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetMainProjectBuildSystemState() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";

		boolean isServer = true;
		boolean isAppClient = true;
		boolean isWebClient = true;
		String servletSystemLibraryPathString = "D:\\apache-tomcat-8.5.11\\lib";

		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);

			boolean[] isServerBooleanSet = { true, false };
			boolean[] isAppClientBooleanSet = { true, false };
			boolean[] isWebClientBooleanSet = { true, false };

			for (int i = 0; i < isServerBooleanSet.length; i++) {
				for (int j = 0; j < isAppClientBooleanSet.length; j++) {
					for (int k = 0; k < isWebClientBooleanSet.length; k++) {

						isServer = isServerBooleanSet[i];
						isAppClient = isAppClientBooleanSet[j];
						isWebClient = isWebClientBooleanSet[k];
						
						/** 전체 빌드 시스템 없는 경우는 생략 */
						if (!isServer && !isAppClient && !isWebClient) continue;

						if (projectBuilder.whetherOnlyProjectPathExists()) {
							projectBuilder.dropProject();
						}

						projectBuilder.createProject(isServer, isAppClient, isWebClient,
								servletSystemLibraryPathString);

						MainProjectBuildSystemState mainProjectBuildSystemState = projectBuilder
								.getNewInstanceOfMainProjectBuildSystemState();

						if (isServer != mainProjectBuildSystemState.isServer()) {
							fail(String.format(
									"the value of isServer[%s] isn't same to mainProjectBuildSystemState.isServer[%s]",
									isServer, mainProjectBuildSystemState.isServer()));
						}

						if (isAppClient != mainProjectBuildSystemState.isAppClient()) {
							fail(String.format(
									"the value of isAppClient[%s] isn't same to mainProjectBuildSystemState.isAppClient[%s]",
									isAppClient, mainProjectBuildSystemState.isAppClient()));
						}

						if (isWebClient != mainProjectBuildSystemState.isWebClient()) {
							fail(String.format(
									"the value of isWebClient[%s] isn't same to mainProjectBuildSystemState.isWebClient[%s]",
									isWebClient, mainProjectBuildSystemState.isWebClient()));
						}

						if (isWebClient) {
							if (null != servletSystemLibraryPathString) {
								if (!servletSystemLibraryPathString
										.equals(mainProjectBuildSystemState.getServletSystemLibrayPathString())) {
									fail(String.format(
											"servletSystemLibraryPathString[%s] isn't same to mainProjectBuildSystemState.getServletSystemLibrayPathString()[%s]",
											servletSystemLibraryPathString,
											mainProjectBuildSystemState.getServletSystemLibrayPathString()));
								}
							}
						}

					}
				}
			}

		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testChangeProjectState() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test2";

		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);

			boolean isServer = true;
			boolean isAppClient = true;
			boolean isWebClient = true;
			String servletSystemLibraryPathString = "D:\\apache-tomcat-8.5.11\\lib";
			boolean[] isServerBooleanSet = { true, false };
			boolean[] isAppClientBooleanSet = { true, false };
			boolean[] isWebClientBooleanSet = { true, false };

			for (int i = 0; i < isServerBooleanSet.length; i++) {
				for (int j = 0; j < isAppClientBooleanSet.length; j++) {
					for (int k = 0; k < isWebClientBooleanSet.length; k++) {
						isServer = isServerBooleanSet[i];
						isAppClient = isAppClientBooleanSet[j];
						isWebClient = isWebClientBooleanSet[k];
						
						/** 전체 빌드 시스템 없는 경우는 생략 */
						if (!isServer && !isAppClient && !isWebClient) continue;
						
						if (projectBuilder.whetherOnlyProjectPathExists()) {
							projectBuilder.dropProject();
						}

						projectBuilder.createProject(isServer, isAppClient, isWebClient,
								servletSystemLibraryPathString);

						SinnoriConfiguration sinnoriConfiguration = new SinnoriConfiguration(sinnoriInstalledPathString,
								mainProjectName);
						SequencedProperties modifiedSinnoriConfigSequencedProperties = sinnoriConfiguration
								.getSinnoriConfigurationSequencedPropties();

						for (int ii = 0; ii < isServerBooleanSet.length; ii++) {
							for (int jj = 0; jj < isAppClientBooleanSet.length; jj++) {
								for (int kk = 0; kk < isWebClientBooleanSet.length; kk++) {

									isServer = !isServerBooleanSet[ii];
									isAppClient = !isAppClientBooleanSet[jj];
									isWebClient = !isWebClientBooleanSet[kk];
									
									/** 전체 빌드 시스템 없는 경우는 생략 */
									if (!isServer && !isAppClient && !isWebClient) continue;

									projectBuilder.changeProjectState(isServer, isAppClient, isWebClient,
											servletSystemLibraryPathString, modifiedSinnoriConfigSequencedProperties);

									if (isServer) {
										if (!projectBuilder.isValidServerAntBuildXMLFile()) {
											fail("This test expected that there is server build, but it doesn't exist");
										}
									} else {
										if (projectBuilder.isValidServerAntBuildXMLFile()) {
											fail("This test expected that there is no server build, but it exists");
										}
									}

									if (isAppClient) {
										if (!projectBuilder.isValidAppClientAntBuildXMLFile()) {
											fail("This test expected that there is application build, but it doesn't exist");
										}
									} else {
										if (projectBuilder.isValidAppClientAntBuildXMLFile()) {
											fail("This test expected that there is no application build, but it exists");
										}
									}

									if (isWebClient) {
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
	public void testApplySinnoriInstalledPath() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_test";
		boolean isServer = true;
		boolean isAppClient = true;
		boolean isWebClient = true;
		String servletSystemLibraryPathString = "D:\\apache-tomcat-8.5.11\\lib";
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			if (projectBuilder.whetherOnlyProjectPathExists()) {
				projectBuilder.dropProject();
			}			

			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
			
			SequencedProperties sequencedPropertiesForBackup = projectBuilder.loadSinnoriConfigPropertiesFile();
			
			SequencedProperties sequencedPropertiesForModify = projectBuilder.loadSinnoriConfigPropertiesFile();
			
			// FIXME!
			sequencedPropertiesForModify.getProperty(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_PATH_ITEMID);
			sequencedPropertiesForModify.setProperty(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_PATH_ITEMID, "aaaa");
			
			// log.info("sequencedPropertiesForModify={}", sequencedPropertiesForModify.toString());
			
			projectBuilder.overwriteSinnoriConfigFile(sequencedPropertiesForModify);
			
			
			projectBuilder.applySinnoriInstalledPath();
			
			SequencedProperties sequencedPropertiesForResult = projectBuilder.loadSinnoriConfigPropertiesFile();
			
			
			if (!sequencedPropertiesForBackup.equals(sequencedPropertiesForResult)) {
				// log.info("sequencedPropertiesForResult={}", sequencedPropertiesForResult.toString());
				fail(String.format("the backup seq properties is not same to the modified seq properties applying Sinnori installed path"));
			}
			
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
}
