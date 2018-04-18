package kr.pe.sinnori.common.buildsystem;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.OS;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.common.util.SequencedPropertiesUtil;

public class ProjectBuilderTest extends AbstractJunitTest {
	final int EXIT_SUCCESS = 0;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	/*public void testGetNewSinnoriConfigSequencedProperties() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori2";
		String mainProjectName = "sample_test";
		
		SinnoriItemIDInfoManger mainProjectItemIDInfo = SinnoriItemIDInfoManger.getInstance();

		SequencedProperties newSinnoriConfigSequencedProperties = mainProjectItemIDInfo
				.getNewSinnoriConfigSequencedProperties(sinnoriInstalledPathString, mainProjectName);
		
		log.info(newSinnoriConfigSequencedProperties.toString());
	}*/

	
	@Test
	public void testConstructor_badSinnoriInstalledPath_notExist() {
		Random r = new Random();
		
		String sinnoriInstalledPathString = new StringBuilder("temp_directory_")
				.append(r.nextInt())
				.append("_")
				.append(r.nextInt())
				.append("_that_does_not_exit").toString();
		String mainProjectName = "sample_test";
		try {
			new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
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
	
	
	@Test
	public void testConstructor_badSinnoriInstalledPath_notDirecotry() {
		File tempFile = null;		
		try {
			tempFile = File.createTempFile("temp", ".tmp");
		} catch (IOException e) {
			fail("fail to create a temp file");
		}
		
		tempFile.deleteOnExit();
		
		String sinnoriInstalledPathString = tempFile.getAbsolutePath();;
		String mainProjectName = "sample_test";

		try {			
			new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
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

	@Test
	public void testConstructor_badProjectBasePath_notExist() {
		Random r = new Random();
		
		String sinnoriInstalledBasePathString = sinnoriInstalledBasePath.getAbsolutePath();
		String mainProjectName = new StringBuilder("sample_test_")
				.append(r.nextInt()).toString();
		try {
			new ProjectBuilder(sinnoriInstalledBasePathString, mainProjectName);

			fail("this test must throw BuildSystemException");
		} catch (BuildSystemException e) {
			String errorMessage = e.getMessage();
			// log.info(errorMessage, e);
			if (!errorMessage.equals(String.format("the project base path[%s] does not exist",
					BuildSystemPathSupporter.getProjectBasePathString(sinnoriInstalledBasePathString)))) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}
		}
	}

	@Test
	public void testConstructor_badProjectBasePath_notDirectory() {
		String sinnoriInstalledBasePathString = sinnoriInstalledBasePath.getAbsolutePath();
		
		String sinnoriInstalledPathStringForTest = new StringBuilder(sinnoriInstalledBasePathString)
				.append(File.separator)
				.append("testsinnori2").toString();
		
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
			new ProjectBuilder(sinnoriInstalledPathStringForTest, mainProjectName);

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

	@Test
	public void testConstructor_badProjectName_notDirectory() {
		String sinnoriInstalledBasePathString = sinnoriInstalledBasePath.getAbsolutePath();
		
		String sinnoriInstalledPathStringForTest = new StringBuilder(sinnoriInstalledBasePathString)
				.append(File.separator)
				.append("testsinnori2").toString();
		
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
			new ProjectBuilder(sinnoriInstalledPathStringForTest, badMainProjectName);

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
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
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
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
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
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String mainProjectName = "sample_test";

		boolean isServer = true;
		boolean isAppClient = true;
		boolean isWebClient = true;
		String servletSystemLibraryPathString = wasLibPath.getAbsolutePath();

		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);

			if (projectBuilder.whetherOnlyProjectPathExists()) {
				projectBuilder.dropProject();
			}

			/*File servletSystemLibraryPath = new File(servletSystemLibraryPathString);
			if (!servletSystemLibraryPath.exists()) {
				fail(new StringBuilder("the servelt system library path[").append(servletSystemLibraryPathString)
						.append("] for web-client build doesn't exist, this test needs Tomcat").toString());
			}

			if (!servletSystemLibraryPath.isDirectory()) {
				fail(new StringBuilder("the servelt system library path[").append(servletSystemLibraryPathString)
						.append("] for web-client build isn't a directory, this test needs Tomcat").toString());
			}*/

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

	

	@Test
	public void testCreateProject_onlyServerBuild() {
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String mainProjectName = "sample_test";

		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);

			if (projectBuilder.whetherOnlyProjectPathExists()) {
				projectBuilder.dropProject();
			}

			boolean isServer = true;
			boolean isAppClient = false;
			boolean isWebClient = false;
			String servletSystemLibraryPathString = wasLibPath.getAbsolutePath();

			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateProject_onlyAppClientBuild() {
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String mainProjectName = "sample_test";

		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);

			if (projectBuilder.whetherOnlyProjectPathExists()) {
				projectBuilder.dropProject();
			}

			boolean isServer = false;
			boolean isAppClient = true;
			boolean isWebClient = false;
			String servletSystemLibraryPathString = wasLibPath.getAbsolutePath();

			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateProject_onlyWebClientBuild() {
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String mainProjectName = "sample_test";

		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);

			if (projectBuilder.whetherOnlyProjectPathExists()) {
				projectBuilder.dropProject();
			}

			boolean isServer = false;
			boolean isAppClient = false;
			boolean isWebClient = true;
			String servletSystemLibraryPathString = wasLibPath.getAbsolutePath();

			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testIsValidSeverAntBuildXMLFile() {
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
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
			String servletSystemLibraryPathString = wasLibPath.getAbsolutePath();
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
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String mainProjectName = "sample_test";

		boolean isServer = true;
		boolean isAppClient = true;
		boolean isWebClient = true;
		String servletSystemLibraryPathString = wasLibPath.getAbsolutePath();

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
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String mainProjectName = "sample_test";

		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);

			boolean isServer = true;
			boolean isAppClient = true;
			boolean isWebClient = true;
			String servletSystemLibraryPathString = wasLibPath.getAbsolutePath();
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
		String sinnoriInstalledPathString = sinnoriInstalledPath.getAbsolutePath();
		String mainProjectName = "sample_test";
		String sampleBaseDBCPName = "sample_base_db";
		boolean isServer = true;
		boolean isAppClient = true;
		boolean isWebClient = true;
		String servletSystemLibraryPathString = wasLibPath.getAbsolutePath();
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
			
			if (projectBuilder.whetherOnlyProjectPathExists()) {
				projectBuilder.dropProject();
			}

			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
			
			String projectResourcesDirectoryPathString = BuildSystemPathSupporter.getProjectResourcesDirectoryPathString(sinnoriInstalledPathString, mainProjectName);
			String dbcpFilePathStringOfSampeTest = new StringBuilder(projectResourcesDirectoryPathString)
					.append(File.separator)
					.append("dbcp")
					.append(File.separator)
					.append("dbcp.")
					.append(sampleBaseDBCPName)
					.append(".properties")
					.toString();
			
			// log.info("dbcpFilePathStringOfSampeTest={}", dbcpFilePathStringOfSampeTest);
			
			SequencedPropertiesUtil.createNewSequencedPropertiesFile(new SequencedProperties(), "the sampe_test's sample dbcp", 
					dbcpFilePathStringOfSampeTest, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			
			SequencedProperties sequencedPropertiesForModify = projectBuilder.loadSinnoriConfigPropertiesFile();
			
			sequencedPropertiesForModify.setProperty(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID, "aaaa");
			sequencedPropertiesForModify.setProperty(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID, "bbbb");
			
			
			sequencedPropertiesForModify.setProperty("dbcp.name_list.value", sampleBaseDBCPName);
			sequencedPropertiesForModify.setProperty(new StringBuilder("dbcp.")
					.append(sampleBaseDBCPName)
					.append(".")
					.append(ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID).toString(), "fjfjieifjf");
			
			
			// log.info("sequencedPropertiesForModify={}", sequencedPropertiesForModify.toString());
			
			projectBuilder.overwriteSinnoriConfigFile(sequencedPropertiesForModify);
			
			
			projectBuilder.applySinnoriInstalledPath();
			
			SequencedProperties sequencedPropertiesForResult = projectBuilder.loadSinnoriConfigPropertiesFile();
			
			
			if (! BuildSystemPathSupporter
					.getSessionKeyRSAPrivatekeyFilePathString(sinnoriInstalledPathString, mainProjectName)
					.equals(sequencedPropertiesForResult
							.getProperty(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID)) || 
					! BuildSystemPathSupporter
					.getSessionKeyRSAPublickeyFilePathString(sinnoriInstalledPathString, mainProjectName)
					.equals(sequencedPropertiesForResult
							.getProperty(ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID))) {				
				
				fail(String.format("the backup seq properties is not same to the modified seq properties applying Sinnori installed path"));
			}
			
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
}
