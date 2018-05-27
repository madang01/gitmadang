package kr.pe.codda.common.buildsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.buildsystem.pathsupporter.AppClientBuildSystemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.CommonBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.WebClientBuildSystemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfoManger;
import kr.pe.codda.common.config.subset.AllDBCPPartConfiguration;
import kr.pe.codda.common.config.subset.AllSubProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BuildSystemException;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.message.builder.IOPartDynamicClassFileContentsBuilderManager;
import kr.pe.codda.common.message.builder.info.MessageInfo;
import kr.pe.codda.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.codda.common.type.LogType;
import kr.pe.codda.common.type.MessageTransferDirectionType;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.SequencedProperties;
import kr.pe.codda.common.util.SequencedPropertiesUtil;

public class ProjectBuilder {
	private InternalLogger log = InternalLoggerFactory.getInstance(ProjectBuilder.class);
	
	public static final String AUTHOR = "Won Jonghoon";
	public static final String JVM_OPTIONS_OF_SERVER = "-server -Xmx2048m -Xms1024m";
	public static final String JVM_OPTIONS_OF_APP_CLIENT = "-Xmx2048m -Xms1024m";
	
	private final String[] messageIDList = {"Echo", "PublicKeyReq",  "PublicKeyRes"};

	private String mainProjectName;
	private String installedPathString;

	private String projectPathString;

	public ProjectBuilder(String installedPathString, String mainProjectName) throws BuildSystemException {
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter installedPathString is null");
		}
		
		if (null == mainProjectName) {
			throw new IllegalArgumentException("the parameter mainProjectName is null");
		}
		
		checkValidPath("the installed path", installedPathString);
		
		
		String projectBasePathString = ProjectBuildSytemPathSupporter.getProjectBasePathString(installedPathString);
		
		checkValidPath("the project base path", projectBasePathString);
				
		
		String projectPathString= ProjectBuildSytemPathSupporter.getProjectPathString(installedPathString, mainProjectName);
		
		File projectPath = new File(projectPathString);
		if (projectPath.exists()) {
			if (!projectPath.isDirectory()) {
				String errorMessage = new StringBuilder("the project path[")
						.append(projectPathString).append("] isn't a directory")
						.toString();
				throw new BuildSystemException(errorMessage);
			}
		}		
		
		this.installedPathString = installedPathString;
		this.mainProjectName = mainProjectName;
		this.projectPathString = projectPathString;	
	}
	
	private void checkValidPath(String title, String targetPathString) throws BuildSystemException {
		File targetPath = new File(targetPathString);
		if (!targetPath.exists()) {
			String errorMessage = new StringBuilder(title).append("[")
					.append(targetPathString).append("] does not exist")
					.toString();
			throw new BuildSystemException(errorMessage);
		}

		if (!targetPath.isDirectory()) {
			String errorMessage = new StringBuilder(title).append("[")
					.append(targetPathString).append("] isn't a directory")
					.toString();
			throw new BuildSystemException(errorMessage);
		}
	}
	
	
	/**
	 * @return only whether the project path exists. Warning! this method does not care whether the project path is a directory. 
	 */
	public boolean whetherOnlyProjectPathExists() {
		String projectPathString= ProjectBuildSytemPathSupporter.getProjectPathString(installedPathString, mainProjectName);
		
		File projectPath = new File(projectPathString);
		
		return projectPath.exists();
	}
	
	public boolean whetherOnlyServerBuildPathExists() {
		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);
		File serverBuildPath = new File(serverBuildPathString);
		return serverBuildPath.exists();
	}
	
	public boolean whetherOnlyAppClientBuildPathExists() {
		String appClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);
		File appClientBuildPath = new File(appClientBuildPathString);
		return appClientBuildPath.exists();
	}
	
	public boolean whetherOnlyWebClientBuildPathExists() {
		String webClientBuildPathString = WebClientBuildSystemPathSupporter.getWebClientBuildPathString(installedPathString, mainProjectName);
		File webClientBuildPath = new File(webClientBuildPathString);
		return webClientBuildPath.exists();
	}
	
	public boolean whetherOnlyWebRootPathExists() {
		String webRootPathString = WebRootBuildSystemPathSupporter.getWebRootPathString(installedPathString, mainProjectName);
		File webRootPath = new File(webRootPathString);
		return webRootPath.exists();
	}
	
	
			
	
	public boolean isValidServerAntBuildXMLFile() {
		String serverAntBuildXMLFilePathString = ServerBuildSytemPathSupporter.getServerAntBuildXMLFilePathString(installedPathString, mainProjectName);
		File serverAntBuildXMLFile = new File(serverAntBuildXMLFilePathString);
		
		if (serverAntBuildXMLFile.exists() && serverAntBuildXMLFile.isFile()) {
			return true;
		}
		
		log.info("the server ant build.xml file[{}] is bad :: whetherExist[{}] isFile[{}]", 
				serverAntBuildXMLFilePathString, serverAntBuildXMLFile.exists(), serverAntBuildXMLFile.isFile());
		
		return false;
	}
	
	public boolean isValidAppClientAntBuildXMLFile() {
		String appClientAntBuildFilePathString = AppClientBuildSystemPathSupporter
				.getAppClientAntBuildXMLFilePathString(installedPathString, mainProjectName);
		File appClientAntBuildXMLFile = new File(appClientAntBuildFilePathString);
		
		if (appClientAntBuildXMLFile.exists() && appClientAntBuildXMLFile.isFile()) {
			return true;
		}
		
		log.info("the app client ant build.xml file[{}] is bad :: whetherExist[{}] isFile[{}]", 
				appClientAntBuildFilePathString, appClientAntBuildXMLFile.exists(), appClientAntBuildXMLFile.isFile());
		
		return false;
	}
	
	public boolean isValidWebClientAntBuildXMLFile() {
		String webClientAntBuildFilePathString = WebClientBuildSystemPathSupporter
				.getWebClientAntBuildXMLFilePathString(installedPathString, mainProjectName);
		File webClientAntBuildXMLFile = new File(webClientAntBuildFilePathString);
		
		if (webClientAntBuildXMLFile.exists() && webClientAntBuildXMLFile.isFile()) {
			return true;
		}
		
		log.info("the web client ant build.xml file[{}] is bad :: whetherExist[{}] isFile[{}]", 
				webClientAntBuildFilePathString, webClientAntBuildXMLFile.exists(), webClientAntBuildXMLFile.isFile());
		
		return false;
	}

	public boolean isValidWebRootXMLFile() {
		String webRootXMLFilePathString = WebRootBuildSystemPathSupporter.getWebRootXMLFilePathString(installedPathString, mainProjectName);
		File webRootXMLFile = new File(webRootXMLFilePathString);
		
		if (webRootXMLFile.exists() && webRootXMLFile.isFile()) {
			return true;
		}
		
		log.info("the web.xml file[{}] located at web root directory is bad :: whetherExist[{}] isFile[{}]", 
				webRootXMLFilePathString, webRootXMLFile.exists(), webRootXMLFile.isFile());
		
		return false;
	}
	
	
	public MainProjectBuildSystemState getNewInstanceOfMainProjectBuildSystemState() throws BuildSystemException {
		String projectPathString= ProjectBuildSytemPathSupporter.getProjectPathString(installedPathString, mainProjectName);
		
		File projectPath = new File(projectPathString);
		if (!projectPath.exists()) {
			String errorMessage = new StringBuilder("the project path[")
					.append(projectPathString).append("] does not exist")
					.toString();
			throw new BuildSystemException(errorMessage);
		}

		if (!projectPath.isDirectory()) {
			String errorMessage = new StringBuilder("the project path[")
					.append(projectPathString).append("] isn't a directory")
					.toString();
			throw new BuildSystemException(errorMessage);
		}
		
		String servletSystemLibrayPathString = null;
		boolean isServer = false;
		boolean isAppClient = false;
		boolean isWebClient = false;
		List<String> subProjectNameList = null;
		List<String> dbcpNameList = null;
		SequencedProperties configurationSequencedPropties = null;
		
		isServer = isValidServerAntBuildXMLFile();
		isAppClient = isValidAppClientAntBuildXMLFile();
		isWebClient = isValidWebClientAntBuildXMLFile();
		
		if (isWebClient) {
			if (!isValidWebRootXMLFile()) {
				String webXMLFilePathString = WebRootBuildSystemPathSupporter.getWebRootXMLFilePathString(installedPathString, mainProjectName);
				
				String errorMessage = String.format(
						"the project's WEB-INF/web.xml[%s] file doesn't exist",
						webXMLFilePathString);
				throw new BuildSystemException(errorMessage);
			}
			
			Properties webClientAntProperties = loadValidWebClientAntPropertiesFile();		
			
			servletSystemLibrayPathString = webClientAntProperties.getProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY);
		}
		
		if (null == servletSystemLibrayPathString) servletSystemLibrayPathString = "";		
		
		
		CoddaConfiguration configuration = null;
		
		try {
			configuration = new CoddaConfiguration(installedPathString, mainProjectName);
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (CoddaConfigurationException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		}
		
		AllDBCPPartConfiguration allDBCPPartConfiguration = configuration.getAllDBCPPartConfiguration();
		AllSubProjectPartConfiguration allSubProjectPartConfiguration = configuration.getAllSubProjectPartConfiguration();
		
		dbcpNameList = allDBCPPartConfiguration.getDBCPNameList();
		subProjectNameList = allSubProjectPartConfiguration.getSubProjectNamelist();	
				
		configurationSequencedPropties =
		configuration.getConfigurationSequencedPropties();
		
		return new MainProjectBuildSystemState(installedPathString, mainProjectName,
				isServer, isAppClient, isWebClient, servletSystemLibrayPathString,
				dbcpNameList, subProjectNameList, configurationSequencedPropties);
	}

	
	public void createProject(boolean isServer, boolean isAppClient, boolean isWebClient, 
			String servletSystemLibraryPathString) throws BuildSystemException {
		if (!isServer && !isAppClient && !isWebClient) {
			throw new IllegalArgumentException("You must choose one more build system type but isSerer=false, isAppClient=false, isWebClient=false");
		}
		
		if (null == servletSystemLibraryPathString) {
			throw new IllegalArgumentException("the paramet servletSystemLibraryPathString is null");
		}
		
		if (whetherOnlyProjectPathExists()) {
			String errorMessage = String.format("Warning! this project can't be created because the project path[%s] exists", projectPathString);
			throw new BuildSystemException(errorMessage);
		}
		
		createChildDirectories(isServer, isAppClient, isWebClient);
		createFiles(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
	}
	
	private void createCommonChildDirectories() throws BuildSystemException {
		log.info("main project[{}]'s common child direcotry creation task start", mainProjectName);

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("config");
		childRelativeDirectoryList.add("resources/dbcp");
		childRelativeDirectoryList.add("resources/message_info");
		childRelativeDirectoryList.add("resources/rsa_keypair");
		childRelativeDirectoryList.add("log/apache");
		childRelativeDirectoryList.add("log/client");
		childRelativeDirectoryList.add("log/server");
		childRelativeDirectoryList.add("log/servlet");
		
		CommonStaticUtil.createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[{}]'s common child direcotry creation task end", mainProjectName);
	}
	
	private void createServerBuildChildDirectories() throws BuildSystemException {
		log.info("main project[{}]'s server child direcotry creation task start", mainProjectName);

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("server_build/APP-INF/classes");
		// childRelativeDirectoryList.add("server_build/APP-INF/resources");
		childRelativeDirectoryList.add("server_build/corelib/ex");
		childRelativeDirectoryList.add("server_build/corelib/in");
		childRelativeDirectoryList.add("server_build/lib/main/ex");
		childRelativeDirectoryList.add("server_build/lib/main/in");
		childRelativeDirectoryList.add("server_build/lib/test");
		
		childRelativeDirectoryList.add(new StringBuilder("server_build/")
				.append(ProjectBuildSytemPathSupporter.getServerTaskDirectoryRelativePath("/")).toString());
		childRelativeDirectoryList.add(new StringBuilder("server_build/")
				.append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath("/")).toString());		
		childRelativeDirectoryList.add("server_build/src/main/java/main");
		childRelativeDirectoryList.add("server_build/src/test/java");
		childRelativeDirectoryList.add("server_build/build");
		childRelativeDirectoryList.add("server_build/dist");
		
		CommonStaticUtil.createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[{}]'s server child direcotry creation task end", mainProjectName);
	}
	
	private void createAppBuildChildDirectories() throws BuildSystemException {
		log.info("main project[{}]'s application build child direcotry creation task start", mainProjectName);

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("client_build/app_build/corelib/ex");
		childRelativeDirectoryList.add("client_build/app_build/corelib/in");
		childRelativeDirectoryList.add("client_build/app_build/lib/main/ex");
		childRelativeDirectoryList.add("client_build/app_build/lib/main/in");
		childRelativeDirectoryList.add("client_build/app_build/lib/test");
		
		childRelativeDirectoryList.add(
				new StringBuilder("client_build/app_build/").append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath("/")).toString());
		childRelativeDirectoryList.add("client_build/app_build/src/main/java/main");
		childRelativeDirectoryList.add("client_build/app_build/src/test/java");
		childRelativeDirectoryList.add("client_build/app_build/build");
		childRelativeDirectoryList.add("client_build/app_build/dist");
		
		CommonStaticUtil.createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[{}]'s application build child direcotry creation task end", mainProjectName);
	}
	
	private void createWebBuildChildDirectories() throws BuildSystemException {
		log.info("main project[{}]'s web build child direcotry creation task start", mainProjectName);

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("client_build/web_build/corelib/ex");
		childRelativeDirectoryList.add("client_build/web_build/corelib/in");
		childRelativeDirectoryList.add("client_build/web_build/lib/main/ex");
		childRelativeDirectoryList.add("client_build/web_build/lib/main/in");
		childRelativeDirectoryList.add("client_build/web_build/lib/test");
		childRelativeDirectoryList.add(
				new StringBuilder("client_build/web_build/").append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath("/")).toString());
		childRelativeDirectoryList.add(new StringBuilder("client_build/web_build/")
				.append(ProjectBuildSytemPathSupporter.getJavaSourceBaseDirectoryRelativePath("/"))
				.append("/servlet").toString());
		childRelativeDirectoryList.add(new StringBuilder("client_build/web_build/")
				.append(ProjectBuildSytemPathSupporter.getJavaSourceBaseDirectoryRelativePath("/"))
				.append("/weblib/common").toString());		
		childRelativeDirectoryList.add(new StringBuilder("client_build/web_build/")
				.append(ProjectBuildSytemPathSupporter.getJavaSourceBaseDirectoryRelativePath("/"))
				.append("/weblib/htmlstring").toString());
		childRelativeDirectoryList.add(new StringBuilder("client_build/web_build/")
				.append(ProjectBuildSytemPathSupporter.getJavaSourceBaseDirectoryRelativePath("/"))
				.append("/weblib/jdf").toString());
		childRelativeDirectoryList.add("client_build/web_build/src/test/java");
		childRelativeDirectoryList.add("client_build/web_build/build");
		childRelativeDirectoryList.add("client_build/web_build/dist");
		
		CommonStaticUtil.createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[{}]'s web build child direcotry creation task end", mainProjectName);
	}
	
	private void createWebRootChildDirectories() throws BuildSystemException {
		log.info("main project[{}]'s web root child direcotry creation task start", mainProjectName);

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("web_app_base/upload");
		childRelativeDirectoryList.add("web_app_base/ROOT/WEB-INF/classes");
		childRelativeDirectoryList.add("web_app_base/ROOT/WEB-INF/lib");
		
		CommonStaticUtil.createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[{}]'s web root child direcotry creation task end", mainProjectName);
	}
	
	private void createChildDirectories(boolean isServer, boolean isAppClient, boolean isWebClient)
			throws BuildSystemException {
		log.info("main project[{}]'s child direcotry creation task start", mainProjectName);
		createCommonChildDirectories();

		if (isServer) {
			createServerBuildChildDirectories();

		}

		if (isAppClient) {
			createAppBuildChildDirectories();
		}

		if (isWebClient) {
			createWebBuildChildDirectories();
			createWebRootChildDirectories();
		}

		

		log.info("main project[{}]'s child direcotry creation task end", mainProjectName);
	}

	private void createFiles(boolean isServer, boolean isAppClient, boolean isWebClient, 
			String servletSystemLibraryPathString) throws BuildSystemException {
		log.info("main project[{}]'s file creation task start", mainProjectName);
		
		if(null == servletSystemLibraryPathString) {
			throw new IllegalArgumentException("the paramet servletSystemLibraryPathString is null");
		}
		
		createNewConfigFile();
		copySampleProjectResorucesToProjectResources();

		if (isServer) {
			createServerBuildSystemFiles();
		}

		if (isAppClient) {
			createAppClientBuildSystemFiles();
		}

		if (isWebClient) {
			createWebClientBuildSystemFiles(servletSystemLibraryPathString);
			createWebRootSampleFiles();
		}
		log.info("main project[{}]'s file creation task end", mainProjectName);
	}
	
	private void copySampleProjectResorucesToProjectResources() throws BuildSystemException {
		log.info("the mainproject[{}]'s resources directory copy task start", mainProjectName);

		String commonResourcesPathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);
		String projectResorucesPathString = ProjectBuildSytemPathSupporter
				.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);

		String sampleProjectResourcesPathString = new StringBuilder(commonResourcesPathString).append(File.separator)
				.append("newproject").append(File.separator).append("resources").toString();

		String projectResourcesPathString = new StringBuilder(projectResorucesPathString).toString();

		File sampleProjectResourcesPath = new File(sampleProjectResourcesPathString);
		File projectResouresPath = new File(projectResourcesPathString);

		try {
			FileUtils.copyDirectory(sampleProjectResourcesPath, projectResouresPath);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to copy the sample resoruces directory[")
					.append(sampleProjectResourcesPathString)
					.append("]  to the main project[").append(mainProjectName)
					.append("]'s the resources directory[").append(projectResourcesPathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("the mainproject[{}]'s resources directory copy task end", mainProjectName);
	}

	private void createWebClientBuildSystemFiles(String servletSystemLibraryPathString) throws BuildSystemException {
		log.info("main project[{}]'s web client build system files creation task start", mainProjectName);
		
		if(null == servletSystemLibraryPathString) {
			throw new IllegalArgumentException("the paramet servletSystemLibraryPathString is null");
		}

		createNewWebClientAntBuildFile();
		createNewWebClientAntPropertiesFile(servletSystemLibraryPathString);
		createNewWebClientMessageIOFileSet();
		copyWebClientSampleFiles();

		log.info("main project[{}]'s web client build system files creation task end", mainProjectName);
	}
	
	private void deleteWebCientBuildPath() throws BuildSystemException {
		log.info("main project[{}]'s web client build path deletion task start", mainProjectName);
		
		String webClientBuildPathString = WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName);
		File webClientBuildPath = new File(webClientBuildPathString);
		
		try {
			FileUtils.forceDelete(webClientBuildPath);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to delete the web client build path[").append(webClientBuildPathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s web client build path deletion task end", mainProjectName);
	}

	
	private void createWebRootSampleFiles() throws BuildSystemException {
		log.info("main project[{}]'s web root sample files creation task start", mainProjectName);

		copyWebRootSampleFiles();

		log.info("main project[{}]'s web root sample files creation task end", mainProjectName);
	}
	
	private void deleteWebRoot() throws BuildSystemException {
		log.info("main project[{}]'s web root path deletion task start", mainProjectName);
		
		String webRootPathString = WebRootBuildSystemPathSupporter
				.getWebRootPathString(installedPathString, mainProjectName);
		File webRootPath = new File(webRootPathString);
		
		try {
			FileUtils.forceDelete(webRootPath);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to delete the web root path[").append(webRootPathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s web root path deletion task end", mainProjectName);
	}

	private void copyWebRootSampleFiles() throws BuildSystemException {
		log.info("main project[{}]'s web root sample files copy task start", mainProjectName);

		String commonResourcePathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);
		String targetWebRootPathString = WebRootBuildSystemPathSupporter.getWebRootPathString(installedPathString, mainProjectName);

		String sourceWebRootPathString = new StringBuilder(commonResourcePathString).append(File.separator)
				.append("newproject").append(File.separator).append("web_root").toString();

		File sourceWebRootPath = new File(sourceWebRootPathString);
		File targetWebRootPath = new File(targetWebRootPathString);

		try {
			FileUtils.copyDirectory(sourceWebRootPath, targetWebRootPath);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to copy the main project[").append(mainProjectName)
					.append("]'s the source directory[").append(sourceWebRootPathString)
					.append("]  having sample source files to the target directory[").append(targetWebRootPathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s web root sample files copy task end", mainProjectName);
	}

	private void createNewWebClientAntBuildFile() throws BuildSystemException {
		log.info("main project[{}]'s web client ant build.xml file creation task start", mainProjectName);

		String webClientAntBuildXMLFileContents = BuildSystemFileContents
				.getWebClientAntBuildXMLFileContents(mainProjectName);

		String webClientAntBuildXMLFilePahtString = WebClientBuildSystemPathSupporter
				.getWebClientAntBuildXMLFilePathString(installedPathString, mainProjectName);

		File webClientAntBuildXMLFile = new File(webClientAntBuildXMLFilePahtString);

		try {
			CommonStaticUtil.createNewFile(webClientAntBuildXMLFile, webClientAntBuildXMLFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s web client ant build.xml file[").append(webClientAntBuildXMLFilePahtString).append("]")
					.toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s web client ant build.xml file creation task end", mainProjectName);
	}

	private void createNewWebClientMessageIOFileSet() throws BuildSystemException {
		log.info("main project[{}]'s web client message io file set creation task start", mainProjectName);

		String webClientBuildPathString = WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName);
		
		String messageIOSetBasedirectoryPathString = CommonStaticUtil
					.getFilePathStringFromResourcePathAndRelativePathOfFile
					(webClientBuildPathString, ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath("/"));
		
		
		for (String messageID : messageIDList) {
			createNewMessageIOSet(messageID, AUTHOR, messageIOSetBasedirectoryPathString);
		}

		log.info("main project[{}]'s web client message io file set creation task end", mainProjectName);
	}

	private void copyWebClientSampleFiles() throws BuildSystemException {
		log.info("mainproject[{}]'s web client sample source files copy task start", mainProjectName);

		String commonResourcePathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);
		String webClientBuildPathString = WebClientBuildSystemPathSupporter
				.getWebClientBuildPathString(installedPathString, mainProjectName);

		String sourceDirectoryPathString = new StringBuilder(commonResourcePathString).append(File.separator)
				.append("newproject").append(File.separator).append("web_build").toString();

		String targetDirectoryPathString = new StringBuilder(webClientBuildPathString).toString();

		File sourceDirectory = new File(sourceDirectoryPathString);
		File targetDirectory = new File(targetDirectoryPathString);

		try {
			FileUtils.copyDirectory(sourceDirectory, targetDirectory);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to copy the main project[").append(mainProjectName)
					.append("]'s the source directory[").append(sourceDirectoryPathString)
					.append("]  having sample source files to the target directory[").append(targetDirectoryPathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("mainproject[{}]'s web client sample source files copy task end", mainProjectName);
	}

	private void createAppClientBuildSystemFiles() throws BuildSystemException {
		log.info("mainproject[{}]'s application client build system files creation task start", mainProjectName);

		createNewAppClientAntBuildXMLFile();
		createNewAppClientDosShellFile();
		createNewAppClientUnixShellFile();
		copyAppClientSampleFiles();
		createNewAppClientAllMessageIOFileSet();

		log.info("mainproject[{}]'s application client build system files creation task end", mainProjectName);
	}
	
	private void deleteAppClientBuildPath() throws BuildSystemException {
		log.info("mainproject[{}]'s application client build path deletion task start", mainProjectName);
		
		String appClientBuildPathString = AppClientBuildSystemPathSupporter
				.getAppClientBuildPathString(installedPathString, mainProjectName);
		
		File appClientBuildPath = new File(appClientBuildPathString);
		
		try {
			FileUtils.forceDelete(appClientBuildPath);
		} catch (IOException e) {
			String errorMessage = String.format("fail to delete app client build path[%s]", appClientBuildPathString);
			log.warn(errorMessage, e);
			

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("mainproject[{}]'s application client build path deletion task end", mainProjectName);
	}

	private void createNewAppClientAntBuildXMLFile() throws BuildSystemException {
		log.info("main project[{}]'s application client ant build.xml file creation task start", mainProjectName);

		String appClientAntBuildXMLFileContents = BuildSystemFileContents.getAppClientAntBuildXMLFileContents(mainProjectName);

		String appClientAntBuildXMLFilePahtString = AppClientBuildSystemPathSupporter
				.getAppClientAntBuildXMLFilePathString(installedPathString, mainProjectName);

		File appClientAntBuildXMLFile = new File(appClientAntBuildXMLFilePahtString);

		try {
			CommonStaticUtil.createNewFile(appClientAntBuildXMLFile, appClientAntBuildXMLFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s app client ant build.xml file[").append(appClientAntBuildXMLFilePahtString).append("]")
					.toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s application client ant build.xml file creation task end", mainProjectName);
	}

	private void createNewAppClientDosShellFile() throws BuildSystemException {
		log.info("main project[{}]'s application client dos shell file creation task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String appClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);

		String appClientDosShellFileContents = BuildSystemFileContents.getDosShellContents(installedPathString,
				mainProjectName, JVM_OPTIONS_OF_APP_CLIENT, LogType.APPCLIENT, appClientBuildPathString,
				relativeExecutabeJarFileName);

		/** AppClient.bat */
		String appClientDosShellFilePathString = new StringBuilder(appClientBuildPathString).append(File.separator)
				.append(mainProjectName).append("AppClient.bat").toString();
		File appClientDosShellFile = new File(appClientDosShellFilePathString);

		try {
			CommonStaticUtil.createNewFile(appClientDosShellFile, appClientDosShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s application client dos shell file[").append(appClientDosShellFilePathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s application client dos shell file creation task end", mainProjectName);
	}

	private void overwriteAppClientDosShellFile() throws BuildSystemException {
		log.info("main project[{}]'s application client dos shell file  overwrite task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String appClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);

		String appClientDosShellFileContents = BuildSystemFileContents.getDosShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_APP_CLIENT, LogType.APPCLIENT, appClientBuildPathString,
				relativeExecutabeJarFileName);

		/** AppClient.bat */
		String appClientDosShellFilePathString = new StringBuilder(appClientBuildPathString).append(File.separator)
				.append(mainProjectName).append("AppClient.bat").toString();
		File appClientDosShellFile = new File(appClientDosShellFilePathString);

		try {
			CommonStaticUtil.overwriteFile(appClientDosShellFile, appClientDosShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s application client dos shell file[").append(appClientDosShellFilePathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s application client dos shell file overwrite task end", mainProjectName);
	}

	private void createNewAppClientUnixShellFile() throws BuildSystemException {
		log.info("main project[{}]'s application client unix shell file creation task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String appClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);

		String appClientUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_APP_CLIENT, LogType.APPCLIENT, appClientBuildPathString, relativeExecutabeJarFileName);

		/** AppClient.bat */
		String appClientUnixShellFilePathString = new StringBuilder(appClientBuildPathString).append(File.separator)
				.append(mainProjectName).append("AppClient.sh").toString();
		File appClientUnixShellFile = new File(appClientUnixShellFilePathString);

		try {
			CommonStaticUtil.createNewFile(appClientUnixShellFile, appClientUnixShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s application client unix shell file[").append(appClientUnixShellFilePathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s application client unix shell file creation task end", mainProjectName);
	}

	private void overwriteAppClientUnixShellFile() throws BuildSystemException {
		log.info("main project[{}]'s application client unix shell file overwrite task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String appClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);

		String appClientUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_APP_CLIENT, LogType.APPCLIENT, appClientBuildPathString, relativeExecutabeJarFileName);

		/** AppClient.bat */
		String appClientUnixShellFilePathString = new StringBuilder(appClientBuildPathString).append(File.separator)
				.append(mainProjectName).append("AppClient.sh").toString();
		File appClientUnixShellFile = new File(appClientUnixShellFilePathString);

		try {
			CommonStaticUtil.overwriteFile(appClientUnixShellFile, appClientUnixShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s application client unix shell file[").append(appClientUnixShellFilePathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s application client unix shell file overwrite task end", mainProjectName);
	}

	private void copyAppClientSampleFiles() throws BuildSystemException {
		log.info("application client sample source files copy task start");

		String commonResourcePathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);
		String applicationClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);

		String sourceDirectoryPathString = new StringBuilder(commonResourcePathString).append(File.separator)
				.append("newproject").append(File.separator).append("app_build").toString();

		String targetDirectoryPathString = applicationClientBuildPathString;

		File sourceDirectory = new File(sourceDirectoryPathString);
		File targetDirectory = new File(targetDirectoryPathString);

		try {
			FileUtils.copyDirectory(sourceDirectory, targetDirectory);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to copy the main project[").append(mainProjectName)
					.append("]'s the build directory[").append(sourceDirectoryPathString)
					.append("]  having sample source files to the target directory[").append(targetDirectoryPathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("application client sample source files copy task end");
	}

	private void createNewAppClientAllMessageIOFileSet() throws BuildSystemException {
		log.info("main project[{}]'s application client message io file set creation task start", mainProjectName);

		String appClientBuildPathString = AppClientBuildSystemPathSupporter
				.getAppClientBuildPathString(installedPathString, mainProjectName);
		
		String messageIOSetBasedirectoryPathString = CommonStaticUtil
					.getFilePathStringFromResourcePathAndRelativePathOfFile
					(appClientBuildPathString, ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath("/"));
			
		for (String messageID : messageIDList) {
			createNewMessageIOSet(messageID, AUTHOR, messageIOSetBasedirectoryPathString);
		}		

		log.info("main project[{}]'s application client message io file set creation task end", mainProjectName);
	}

	private void createNewMessageIOSet(String messageID, String author, String messageIOSetBasedirectoryPathString)
			throws BuildSystemException {
		createNewMessageIDDirectory(messageIOSetBasedirectoryPathString, messageID);
		createNewMessageSourceFile(messageID, author, messageIOSetBasedirectoryPathString);
		createNewDecoderSourceFile(messageID, author, messageIOSetBasedirectoryPathString);
		createNewEncoderSourceFile(messageID, author, messageIOSetBasedirectoryPathString);
		createNewServerCodecSourceFile(messageID, author, messageIOSetBasedirectoryPathString);
		createNewClientCodecSourceFile(messageID, author, messageIOSetBasedirectoryPathString);
	}

	private void createServerBuildSystemFiles() throws BuildSystemException {
		log.info("server build system files creation task start");

		createNewServerAntBuildXMLFile();
		createNewServerDosShellFile();
		createNewServerUnixShellFile();
		copyServerSampleFiles();
		createServerMessageIOFileSet();

		log.info("server build system files creation task end");
	}
	
	private void deleteServerBuildPath() throws BuildSystemException {
		log.info("mainproject[{}]'s server build path deletion task start", mainProjectName);
		
		String serverBuildPathString = ServerBuildSytemPathSupporter
				.getServerBuildPathString(installedPathString, mainProjectName);
		
		File serverBuildPath = new File(serverBuildPathString);
		
		try {
			FileUtils.forceDelete(serverBuildPath);
		} catch (IOException e) {
			String errorMessage = String.format("fail to delete server build path[%s]", serverBuildPathString);
			log.warn(errorMessage, e);
			

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("mainproject[{}]'s server build path deletion task end", mainProjectName);
	}

	private void createNewMessageIDDirectory(String messageIOSetBasedirectoryPathString, String messageID)
			throws BuildSystemException {
		List<String> childRelativeDirectoryList = new ArrayList<String>();
		childRelativeDirectoryList.add(messageID);
		CommonStaticUtil.createChildDirectoriesOfBasePath(messageIOSetBasedirectoryPathString,
				childRelativeDirectoryList);
	}

	private void createServerMessageIOFileSet() throws BuildSystemException {
		log.info("main project[{}]'s server message io file set creation task start", mainProjectName);

		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);
		String messageIOSetBasedirectoryPathString = CommonStaticUtil
					.getFilePathStringFromResourcePathAndRelativePathOfFile
					(serverBuildPathString, ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath("/"));
		

		
		
		for (String messageID : messageIDList) {
			createNewMessageIOSet(messageID, AUTHOR, messageIOSetBasedirectoryPathString);
		}
		
		log.info("main project[{}]'s server message io file set creation task end", mainProjectName);
	}

	private MessageInfo getMessageInfo(String messageID) throws BuildSystemException {

		String echoMessageInfoFilePathString = new StringBuilder(
				ProjectBuildSytemPathSupporter.getProjectMessageInfoDirectoryPathString(installedPathString, mainProjectName))
						.append(File.separator).append(messageID).append(".xml").toString();
		File echoMessageInfoFile = new File(echoMessageInfoFilePathString);

		MessageInfoSAXParser messageInfoSAXParser = null;
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (SAXException e) {
			System.exit(1);
		}
		MessageInfo echoMessageInfo = null;
		try {
			echoMessageInfo = messageInfoSAXParser.parse(echoMessageInfoFile, true);
		} catch (IllegalArgumentException | SAXException | IOException e) {
			String errorMessage = new StringBuilder("fail to parse message information xml file[")
					.append(echoMessageInfoFile.getAbsolutePath()).append("]").toString();
			log.warn(errorMessage, e);
			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		return echoMessageInfo;
	}

	private void createNewMessageSourceFile(String messageID, String author, String messageIOSetBasedirectoryPathString)
			throws BuildSystemException {
		log.info("main project[{}]'s message file[{}][{}] creation task start", mainProjectName, messageID,
				messageIOSetBasedirectoryPathString);

		MessageInfo messageInfo = getMessageInfo(messageID);

		IOPartDynamicClassFileContentsBuilderManager ioFileSetContentsBuilderManager = IOPartDynamicClassFileContentsBuilderManager.getInstance();

		String messageFileContnets = ioFileSetContentsBuilderManager.getMessageSourceFileContents(author,
				messageInfo);
		
		String messageFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append(".java").toString();

		File messageFile = new File(messageFilePathString);

		try {
			CommonStaticUtil.createNewFile(messageFile, messageFileContnets,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[").append(messageFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s message file[{}][{}] creation task end", mainProjectName, messageID,
				messageIOSetBasedirectoryPathString);
	}

	private void createNewDecoderSourceFile(String messageID, String author, String messageIOSetBasedirectoryPathString)
			throws BuildSystemException {
		log.info("main project[{}]'s message decoder file[{}][{}] creation task start", mainProjectName, messageID,
				messageIOSetBasedirectoryPathString);

		MessageInfo messageInfo = getMessageInfo(messageID);

		IOPartDynamicClassFileContentsBuilderManager ioFileSetContentsBuilderManager = IOPartDynamicClassFileContentsBuilderManager.getInstance();

		String decoderFileContnets = ioFileSetContentsBuilderManager.getDecoderSourceFileContents(author,
				messageInfo);
		
		String decoderFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append("Decoder.java").toString();

		File decoderFile = new File(decoderFilePathString);

		try {
			CommonStaticUtil.createNewFile(decoderFile, decoderFileContnets,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[").append(decoderFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s message decoder file[{}][{}] creation task end", mainProjectName, messageID,
				messageIOSetBasedirectoryPathString);
	}

	private void createNewEncoderSourceFile(String messageID, String author, String messageIOSetBasedirectoryPathString)
			throws BuildSystemException {
		log.info("main project[{}]'s message encoder file[{}][{}] creation task start", mainProjectName, messageID,
				messageIOSetBasedirectoryPathString);

		MessageInfo messageInfo = getMessageInfo(messageID);

		IOPartDynamicClassFileContentsBuilderManager ioFileSetContentsBuilderManager = IOPartDynamicClassFileContentsBuilderManager.getInstance();

		String encoderFileContnets = ioFileSetContentsBuilderManager.getEncoderSourceFileContents(author,
				messageInfo);
		
		String encoderFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append("Encoder.java").toString();

		File encoderFile = new File(encoderFilePathString);

		try {
			CommonStaticUtil.createNewFile(encoderFile, encoderFileContnets,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[").append(encoderFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s message encoder file[{}][{}] creation task end", mainProjectName, messageID,
				messageIOSetBasedirectoryPathString);
	}

	private void createNewServerCodecSourceFile(String messageID, String author,
			String messageIOSetBasedirectoryPathString) throws BuildSystemException {
		log.info("main project[{}]'s message server codec file[{}][{}] creation task start", mainProjectName, messageID,
				messageIOSetBasedirectoryPathString);
		// MessageInfo messageInfo = getMessageInfo(messageID);

		IOPartDynamicClassFileContentsBuilderManager ioFileSetContentsBuilderManager = IOPartDynamicClassFileContentsBuilderManager.getInstance();

		String serverCodecFileContnets = ioFileSetContentsBuilderManager.getServerCodecSourceFileContents(
				MessageTransferDirectionType.FROM_ALL_TO_ALL, messageID, author);

		String serverCodecFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append("ServerCodec.java").toString();

		File serverCodecFile = new File(serverCodecFilePathString);

		try {
			CommonStaticUtil.createNewFile(serverCodecFile, serverCodecFileContnets,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[").append(serverCodecFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s message server codec file[{}][{}] creation task end", mainProjectName, messageID,
				messageIOSetBasedirectoryPathString);
	}

	private void createNewClientCodecSourceFile(String messageID, String author,
			String messageIOSetBasedirectoryPathString) throws BuildSystemException {
		log.info("main project[{}]'s message client codec file[{}][{}] creation task start", mainProjectName, messageID,
				messageIOSetBasedirectoryPathString);
		// MessageInfo messageInfo = getMessageInfo(messageID);

		IOPartDynamicClassFileContentsBuilderManager ioFileSetContentsBuilderManager = IOPartDynamicClassFileContentsBuilderManager.getInstance();

		String clientCodecFileContnets = ioFileSetContentsBuilderManager.getClientCodecSourceFileContents(
				MessageTransferDirectionType.FROM_ALL_TO_ALL, messageID, author);

		String clientCodecFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append("ClientCodec.java").toString();

		File clientCodecFile = new File(clientCodecFilePathString);

		try {
			CommonStaticUtil.createNewFile(clientCodecFile, clientCodecFileContnets,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[").append(clientCodecFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s message client codec file[{}][{}] creation task end", mainProjectName, messageID,
				messageIOSetBasedirectoryPathString);
	}

	

	private void copyServerSampleFiles() throws BuildSystemException {
		log.info("server sample source files copy task start");

		
		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);

		String commonResourcePathString = CommonBuildSytemPathSupporter
				.getCommonResourcesPathString(installedPathString);
		
		String sourceDirectoryPathString = new StringBuilder(commonResourcePathString).append(File.separator)
				.append("newproject").append(File.separator).append("server_build").toString();

		String targetDirectoryPathString = new StringBuilder(serverBuildPathString).toString();

		File sourceDirectory = new File(sourceDirectoryPathString);
		File targetDirectory = new File(targetDirectoryPathString);

		try {
			FileUtils.copyDirectory(sourceDirectory, targetDirectory);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to copy the main project[").append(mainProjectName)
					.append("]'s the build directory[").append(sourceDirectoryPathString)
					.append("]  having sample source files to the target directory[").append(targetDirectoryPathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("server sample source files copy task end");
	}

	private void createNewServerDosShellFile() throws BuildSystemException {
		log.info("main project[{}]'s server dos shell file creation task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);

		String serverDosShellFileContents = BuildSystemFileContents.getDosShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_SERVER, LogType.SERVER, serverBuildPathString, relativeExecutabeJarFileName);

		/** Server.bat */
		String serverDosShellFilePathString = new StringBuilder(serverBuildPathString).append(File.separator)
				.append(mainProjectName).append("Server.bat").toString();
		File serverDosShellFile = new File(serverDosShellFilePathString);

		try {
			CommonStaticUtil.createNewFile(serverDosShellFile, serverDosShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server dos shell file[").append(serverDosShellFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s server dos shell file creation task end", mainProjectName);
	}

	private void overwriteServerDosShellFile() throws BuildSystemException {
		log.info("main project[{}]'s server dos shell file overwrite task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);

		String serverDosShellFileContents = BuildSystemFileContents.getDosShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_SERVER, LogType.SERVER, serverBuildPathString, relativeExecutabeJarFileName);

		/** Server.bat */
		String serverDosShellFilePathString = new StringBuilder(serverBuildPathString).append(File.separator)
				.append(mainProjectName).append("Server.bat").toString();
		File serverDosShellFile = new File(serverDosShellFilePathString);

		try {
			CommonStaticUtil.overwriteFile(serverDosShellFile, serverDosShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server dos shell file[").append(serverDosShellFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s server dos shell file overwrite task end", mainProjectName);
	}

	private void createNewServerUnixShellFile() throws BuildSystemException {
		log.info("main project[{}]'s server unix shell file creation task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);

		String serverUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_SERVER, LogType.SERVER, serverBuildPathString, relativeExecutabeJarFileName);

		String serverUnixShellFilePathString = new StringBuilder(serverBuildPathString).append(File.separator)
				.append(mainProjectName).append("Server.sh").toString();

		File serverUnixShellFile = new File(serverUnixShellFilePathString);

		try {
			CommonStaticUtil.createNewFile(serverUnixShellFile, serverUnixShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server unix shell file[").append(serverUnixShellFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s server unix shell file creation task end", mainProjectName);
	}

	private void overwriteServerUnixShellFile() throws BuildSystemException {
		log.info("main project[{}]'s server unix shell file overwrite task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);

		String serverUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(installedPathString, mainProjectName,
				JVM_OPTIONS_OF_SERVER, LogType.SERVER, serverBuildPathString, relativeExecutabeJarFileName);

		String serverUnixShellFilePathString = new StringBuilder(serverBuildPathString).append(File.separator)
				.append(mainProjectName).append("Server.sh").toString();

		File serverUnixShellFile = new File(serverUnixShellFilePathString);

		try {
			CommonStaticUtil.overwriteFile(serverUnixShellFile, serverUnixShellFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server unix shell file[").append(serverUnixShellFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s server unix shell file overwrite task end", mainProjectName);
	}

	private void createNewServerAntBuildXMLFile() throws BuildSystemException {
		log.info("main project[{}]'s server ant build.xml file creation task start", mainProjectName);

		String sererAntBuildXMLFileContents = BuildSystemFileContents.getServerAntBuildXMLFileContent(mainProjectName);

		String serverAntBuildXMLFilePahtString = ServerBuildSytemPathSupporter.getServerAntBuildXMLFilePathString(installedPathString, mainProjectName);

		File serverAntBuildXMLFile = new File(serverAntBuildXMLFilePahtString);

		try {
			CommonStaticUtil.createNewFile(serverAntBuildXMLFile, sererAntBuildXMLFileContents,
					CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server ant build.xml file[").append(serverAntBuildXMLFilePahtString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s server ant build.xml file creation task end", mainProjectName);
	}

	private void createNewConfigFile() throws BuildSystemException {
		log.info("main project[{}]'s config file creation task start", mainProjectName);
		
		String projectConfigFilePathString = ProjectBuildSytemPathSupporter.getProejctConfigFilePathString(installedPathString, mainProjectName);

		ItemIDInfoManger mainProjectItemIDInfo = ItemIDInfoManger.getInstance();

		SequencedProperties newConfigSequencedProperties = mainProjectItemIDInfo
				.getNewConfigSequencedProperties(installedPathString, mainProjectName);
		
		try {
			SequencedPropertiesUtil.createNewSequencedPropertiesFile(newConfigSequencedProperties,
					CoddaConfiguration.getConfigPropertiesTitle(mainProjectName), projectConfigFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to create the main project's configuration file[").append(projectConfigFilePathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s config file creation task end", mainProjectName);
	}
	
	private void createNewWebClientAntPropertiesFile(String servletSystemLibraryPathString)
			throws BuildSystemException {
		log.info("main project[{}]'s web client ant properties file creation task start", mainProjectName);
		
		if(null == servletSystemLibraryPathString) {
			throw new IllegalArgumentException("the paramet servletSystemLibraryPathString is null");
		}
		
		File servletSystemLibraryPath = new File(servletSystemLibraryPathString);
		if (!servletSystemLibraryPath.exists()) {
			String errorMessage = new StringBuilder("the web client's servlet system library path[")
					.append(servletSystemLibraryPathString)
					.append("] doesn't exist").toString();

			throw new BuildSystemException(errorMessage);
		}
		
		String webClientAntPropertiesFilePathString = WebClientBuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(installedPathString, mainProjectName);
		
		SequencedProperties antBuiltInProperties = new SequencedProperties();

		// antBuiltInProperties.setProperty(CommonStaticFinalVars.IS_TOMCAT_KEY, isTomcat ? "true" : "false");

		antBuiltInProperties.setProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY,
				servletSystemLibraryPathString);

		
		try {
			SequencedPropertiesUtil.createNewSequencedPropertiesFile(antBuiltInProperties, getWebClientAntPropertiesTitle(),
					webClientAntPropertiesFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder("the main project[")
					.append(mainProjectName)
					.append("]'s web client ant properties file[")
					.append(webClientAntPropertiesFilePathString)
					.append("] doesn't exist").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to create the main project[").append(mainProjectName)
					.append("]'s web client ant properties file[").append(webClientAntPropertiesFilePathString).append("]")
					.toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s web client ant properties file creation task end", mainProjectName);
	}
	
	// FIXME!
	private void modifyWebClientAntPropertiesFile(String servletSystemLibraryPathString) throws BuildSystemException {
		log.info("main project[{}]'s web client ant properties file modification task start", mainProjectName);
		
		if(null == servletSystemLibraryPathString) {
			throw new IllegalArgumentException("the paramet servletSystemLibraryPathString is null");
		}
		
		File servletSystemLibraryPath = new File(servletSystemLibraryPathString);
		if (!servletSystemLibraryPath.exists()) {
			String errorMessage = new StringBuilder("the web client's servlet system library path[")
					.append(servletSystemLibraryPathString)
					.append("] doesn't exist").toString();

			throw new BuildSystemException(errorMessage);
		}
		
		String webClientAntPropertiesFilePathString = WebClientBuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(installedPathString, mainProjectName);
		
		SequencedProperties antBuiltInProperties = new SequencedProperties();

		antBuiltInProperties.setProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY,
				servletSystemLibraryPathString);
		
		try {
			SequencedPropertiesUtil.overwriteSequencedPropertiesFile(antBuiltInProperties, getWebClientAntPropertiesTitle(),
					webClientAntPropertiesFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder("the main project[")
					.append(mainProjectName)
					.append("]'s web client ant properties file[")
					.append(webClientAntPropertiesFilePathString)
					.append("] doesn't exist").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to modify the main project[").append(mainProjectName)
					.append("]'s web client ant properties file[").append(webClientAntPropertiesFilePathString).append("]")
					.toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s web client ant properties file modification task end", mainProjectName);
	}
	private String getWebClientAntPropertiesTitle() {
		return new StringBuilder("project[").append(mainProjectName).append("]'s web client ant properteis file").toString();
	}
		
	private Properties loadValidWebClientAntPropertiesFile() throws BuildSystemException {		
		String webClientAntPropertiesFilePathString = WebClientBuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(installedPathString, mainProjectName);
		
		SequencedProperties webClientAntProperties = null;
		try {
			webClientAntProperties = SequencedPropertiesUtil.loadSequencedPropertiesFile(webClientAntPropertiesFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder("the main project[")
					.append(mainProjectName)
					.append("]'s web client ant properties file[")
					.append(webClientAntPropertiesFilePathString)
					.append("] doesn't exist").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to load the main project[").append(mainProjectName)
					.append("]'s web client ant properties file[").append(webClientAntPropertiesFilePathString).append("]")
					.toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		if (!webClientAntProperties.containsKey(CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY)) {			
			String errorMessage = String.format(
					"the web client ant properties file[%s] is bad because the key[%s] that means servlet system library path is not found",
					webClientAntPropertiesFilePathString, CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY);
			throw new BuildSystemException(errorMessage);
		}		
		
		return webClientAntProperties;
	}
	

	private void applyInstalledPathToConfigFile() throws BuildSystemException {
		String mainProejctConfigFilePathString = ProjectBuildSytemPathSupporter.getProejctConfigFilePathString(installedPathString, mainProjectName);
		try {		
			CoddaConfiguration.applyInstalledPath(installedPathString, mainProjectName);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("fail to apply installed path to the main project[")
					.append(mainProjectName).append("] config file").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (Exception e) {
			String errorMessage = new StringBuilder(
					"fail to apply installed path to the main project config file[")
							.append(mainProejctConfigFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
	}
		

	public void applyInstalledPath() throws BuildSystemException {
		applyInstalledPathToConfigFile();
		
		if (File.separator.equals("/")) {				
			/** unix shell */
			if (isValidServerAntBuildXMLFile()) {
				overwriteServerUnixShellFile();
			} else {
				createNewServerAntBuildXMLFile();
			}
			if (isValidAppClientAntBuildXMLFile()) {
				overwriteAppClientUnixShellFile();
			} else {
				createNewAppClientUnixShellFile();
			}
		} else {
			/** dos shell */
			if (isValidServerAntBuildXMLFile()) {
				overwriteServerDosShellFile();
			} else {
				createNewServerDosShellFile();
			}
			if (isValidAppClientAntBuildXMLFile()) {
				overwriteAppClientDosShellFile();
			} else {
				createNewAppClientDosShellFile();
			}
		}
	}

	public void overwriteConfigFile(SequencedProperties modifiedConfigSequencedProperties) throws BuildSystemException {
		log.info("main project[{}]'s config file overwrite task start", mainProjectName);
		
		String mainProjectConfigFilePathString = ProjectBuildSytemPathSupporter.getProejctConfigFilePathString(installedPathString, mainProjectName);
		try {
			
			
			SequencedPropertiesUtil.overwriteSequencedPropertiesFile(modifiedConfigSequencedProperties,
					CoddaConfiguration.getConfigPropertiesTitle(mainProjectName), 
					mainProjectConfigFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s configuration file").toString();
	
			log.warn(errorMessage, e);
	
			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (IOException e) {			
			String errorMessage = new StringBuilder("fail to overwrite the main project's configuration file").append(mainProjectConfigFilePathString)
					.append("]").toString();
	
			log.warn(errorMessage, e);
	
			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s config file overwrite task end", mainProjectName);
	}

	public SequencedProperties loadConfigPropertiesFile() throws BuildSystemException {
		CoddaConfiguration mainProjectConfiguration = null;
		
		try {
			mainProjectConfiguration = new CoddaConfiguration(installedPathString, mainProjectName);
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (CoddaConfigurationException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		}
		
		return mainProjectConfiguration.getConfigurationSequencedPropties();
		
	}

	public void changeProjectState(boolean isServer, boolean isAppClient, 
			boolean isWebClient, String servletSystemLibraryPathString, 
			SequencedProperties modifiedConfigSequencedProperties) throws BuildSystemException {
		log.info("main project[{}] changeProjectState method start", mainProjectName);
		
		if (!isServer && !isAppClient && !isWebClient) {
			throw new IllegalArgumentException("You must choose one more build system type but isSerer=false, isAppClient=false, isWebClient=false");
		}
		
		if (isServer) {
			String serverBuildPathString = ServerBuildSytemPathSupporter.getServerBuildPathString(installedPathString, mainProjectName);
			File serverBuildPath = new File(serverBuildPathString);
			
			if (!serverBuildPath.exists()) {
				createServerBuildChildDirectories();
				createServerBuildSystemFiles();				
			} else {
				log.warn("the server build path[{}] exist, so skip creation of server build system", serverBuildPathString);
			}	
		} else {
			if (isValidServerAntBuildXMLFile()) {
				deleteServerBuildPath();				
			} else {
				String serverAntBuildFilePathString = ServerBuildSytemPathSupporter
						.getServerAntBuildXMLFilePathString(installedPathString, mainProjectName);
				
				log.warn("the server buid.xml file[{}] is bad beacse it doesn't exist or is not a file, so skip deletion of server build system", 
						serverAntBuildFilePathString);
			}
		}
		
		if (isAppClient) {
			String appClientBuildPathString = AppClientBuildSystemPathSupporter.getAppClientBuildPathString(installedPathString, mainProjectName);
			File appClientBuildPath = new File(appClientBuildPathString);
			
			if (!appClientBuildPath.exists()) {
				createAppBuildChildDirectories();
				createAppClientBuildSystemFiles();				
			} else {				
				log.warn("the app client build path[{}] exist, so skip creation of app client build system", appClientBuildPathString);
			}			
		} else {			
			
			if (isValidAppClientAntBuildXMLFile()) {
				deleteAppClientBuildPath();				
			} else {
				String appClientAntBuildFilePathString = AppClientBuildSystemPathSupporter
						.getAppClientAntBuildXMLFilePathString(installedPathString, mainProjectName);
				
				log.warn("the app client buid.xml file[{}] is bad beacse it doesn't exist or is not a file, so skip deletion of app client build system", 
						appClientAntBuildFilePathString);
			}
			
		}
		
		if (isWebClient) {
			{
				String webClientBuildPathString = WebClientBuildSystemPathSupporter.getWebClientBuildPathString(installedPathString, mainProjectName);
				File webClientBuildPath = new File(webClientBuildPathString);
				
				if (!webClientBuildPath.exists()) {
					createWebBuildChildDirectories();
					createWebClientBuildSystemFiles(servletSystemLibraryPathString);
				} else {
					modifyWebClientAntPropertiesFile(servletSystemLibraryPathString);
					log.warn("the web client build path[{}] exists, so only the web client's ant properties including servlet system library path  was updated for new servletSystemLibraryPathString[{}]", 
							webClientBuildPathString, servletSystemLibraryPathString);
				}		
			}
			{
				String webRootPathString = WebRootBuildSystemPathSupporter.getWebRootPathString(installedPathString, mainProjectName);
				File webRootPath = new File(webRootPathString);
				if (!webRootPath.exists()) {
					createWebRootChildDirectories();
					createWebRootSampleFiles();
				} else {
					log.warn("the web root path[{}] exists, so skip creation of web root", webRootPathString);
				}
			}
		} else {
			if (isValidWebClientAntBuildXMLFile()) {
				deleteWebCientBuildPath();
			} else {
				String webClientAntBuildFilePathString = WebClientBuildSystemPathSupporter
						.getWebClientAntBuildXMLFilePathString(installedPathString, mainProjectName);
				log.warn("the web client buid.xml file[{}] is bad beacse it doesn't exist or is not a file, so skip deletion of web client build system",
						webClientAntBuildFilePathString);
			}			

			if (isValidWebRootXMLFile()) {
				deleteWebRoot();
			} else {
				String webRootXMLFilePathString = WebRootBuildSystemPathSupporter.getWebRootXMLFilePathString(installedPathString, mainProjectName);
				log.warn("the web.xml file[{}] located at web root direcotry is bad beacse it doesn't exist or is not a file, so skip deletion of web root system", 
						webRootXMLFilePathString);
			}
		}
		
		overwriteConfigFile(modifiedConfigSequencedProperties);
		
		log.info("main project[{}] changeProjectState method end", mainProjectName);
	}
	
	
	/**
	 * if the project path exists and is a directory, then force-delete it
	 * @throws BuildSystemException This exception is thrown if the project path doesn't exit or is not a directory.
	 */
	public void dropProject() throws BuildSystemException {
		File projectPath = new File(projectPathString);

		if (!projectPath.exists()) {
			String errorMessage = new StringBuilder("the main project path[")
					.append(projectPathString).append("] does not exist")
					.toString();
			throw new BuildSystemException(errorMessage);
		}

		if (!projectPath.isDirectory()) {
			String errorMessage = new StringBuilder("the main project path[")
					.append(projectPathString).append("] is not a directory")
					.toString();
			throw new BuildSystemException(errorMessage);
		}

		try {
			FileUtils.forceDelete(projectPath);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to delete the main project path[")
					.append(projectPathString).append("]").toString();
			/** 상세 에러 추적용 */
			log.warn(errorMessage, e);
			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
	}
}
