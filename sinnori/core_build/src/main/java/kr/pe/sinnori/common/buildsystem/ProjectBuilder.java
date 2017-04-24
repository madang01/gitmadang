package kr.pe.sinnori.common.buildsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.config.vo.AllDBCPPartConfiguration;
import kr.pe.sinnori.common.config.vo.AllSubProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;
import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.exception.MessageInfoSAXParserException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.message.builder.IOFileSetContentsBuilderManager;
import kr.pe.sinnori.common.message.builder.info.MessageInfo;
import kr.pe.sinnori.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.common.util.SequencedPropertiesUtil;

public class ProjectBuilder {
	private Logger log = LoggerFactory.getLogger(ProjectBuilder.class);

	private static final String MESSAGE_SOURCE_FILE_RELATIVE_PATH = "src/main/java/kr/pe/sinnori/impl/message";
	public static final String ECHO_MESSAGE_ID = "Echo";
	public static final String AUTHOR = "Won Jonghoon";
	public static final String JVM_OPTIONS_OF_SERVER = "-server -Xmx2048m -Xms1024m";
	public static final String JVM_OPTIONS_OF_APP_CLIENT = "-Xmx2048m -Xms1024m";

	private String mainProjectName;
	private String sinnoriInstalledPathString;

	private String projectPathString;
	
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

	public ProjectBuilder(String sinnoriInstalledPathString, String mainProjectName) throws BuildSystemException {
		if (null == sinnoriInstalledPathString) {
			throw new IllegalArgumentException("the parameter sinnoriInstalledPathString is null");
		}
		
		if (null == mainProjectName) {
			throw new IllegalArgumentException("the parameter mainProjectName is null");
		}
		
		checkValidPath("the Sinnori installed path", sinnoriInstalledPathString);
		
		
		String projectBasePathString = BuildSystemPathSupporter.getProjectBasePathString(sinnoriInstalledPathString);
		
		checkValidPath("the project base path", projectBasePathString);
				
		
		String projectPathString= BuildSystemPathSupporter.getProjectPathString(sinnoriInstalledPathString, mainProjectName);
		
		File projectPath = new File(projectPathString);
		if (projectPath.exists()) {
			if (!projectPath.isDirectory()) {
				String errorMessage = new StringBuilder("the project path[")
						.append(projectPathString).append("] isn't a directory")
						.toString();
				throw new BuildSystemException(errorMessage);
			}
		}		
		
		this.sinnoriInstalledPathString = sinnoriInstalledPathString;
		this.mainProjectName = mainProjectName;
		this.projectPathString = projectPathString;	
	}
	
	
	
	/**
	 * @return only whether the project path exists. Warning! this method does not care whether the project path is a directory. 
	 */
	public boolean whetherOnlyProjectPathExists() {
		String projectPathString= BuildSystemPathSupporter.getProjectPathString(sinnoriInstalledPathString, mainProjectName);
		
		File projectPath = new File(projectPathString);
		
		return projectPath.exists();
	}
	
	public boolean whetherOnlyServerBuildPathExists() {
		String serverBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName);
		File serverBuildPath = new File(serverBuildPathString);
		return serverBuildPath.exists();
	}
	
	public boolean whetherOnlyAppClientBuildPathExists() {
		String appClientBuildPathString = BuildSystemPathSupporter.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName);
		File appClientBuildPath = new File(appClientBuildPathString);
		return appClientBuildPath.exists();
	}
	
	public boolean whetherOnlyWebClientBuildPathExists() {
		String webClientBuildPathString = BuildSystemPathSupporter.getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName);
		File webClientBuildPath = new File(webClientBuildPathString);
		return webClientBuildPath.exists();
	}
	
	public boolean whetherOnlyWebRootPathExists() {
		String webRootPathString = BuildSystemPathSupporter.getWebRootPathString(sinnoriInstalledPathString, mainProjectName);
		File webRootPath = new File(webRootPathString);
		return webRootPath.exists();
	}
	
	
			
	
	public boolean isValidServerAntBuildXMLFile() {
		String serverAntBuildXMLFilePathString = BuildSystemPathSupporter.getServerAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
		File serverAntBuildXMLFile = new File(serverAntBuildXMLFilePathString);
		
		if (serverAntBuildXMLFile.exists() && serverAntBuildXMLFile.isFile()) {
			return true;
		}
		
		log.info("the server ant build.xml file[{}] is bad :: whetherExist[{}] isFile[{}]", 
				serverAntBuildXMLFilePathString, serverAntBuildXMLFile.exists(), serverAntBuildXMLFile.isFile());
		
		return false;
	}
	
	public boolean isValidAppClientAntBuildXMLFile() {
		String appClientAntBuildFilePathString = BuildSystemPathSupporter
				.getAppClientAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
		File appClientAntBuildXMLFile = new File(appClientAntBuildFilePathString);
		
		if (appClientAntBuildXMLFile.exists() && appClientAntBuildXMLFile.isFile()) {
			return true;
		}
		
		log.info("the app client ant build.xml file[{}] is bad :: whetherExist[{}] isFile[{}]", 
				appClientAntBuildFilePathString, appClientAntBuildXMLFile.exists(), appClientAntBuildXMLFile.isFile());
		
		return false;
	}
	
	public boolean isValidWebClientAntBuildXMLFile() {
		String webClientAntBuildFilePathString = BuildSystemPathSupporter
				.getWebClientAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
		File webClientAntBuildXMLFile = new File(webClientAntBuildFilePathString);
		
		if (webClientAntBuildXMLFile.exists() && webClientAntBuildXMLFile.isFile()) {
			return true;
		}
		
		log.info("the web client ant build.xml file[{}] is bad :: whetherExist[{}] isFile[{}]", 
				webClientAntBuildFilePathString, webClientAntBuildXMLFile.exists(), webClientAntBuildXMLFile.isFile());
		
		return false;
	}

	public boolean isValidWebRootXMLFile() {
		String webRootXMLFilePathString = BuildSystemPathSupporter.getWebRootXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
		File webRootXMLFile = new File(webRootXMLFilePathString);
		
		if (webRootXMLFile.exists() && webRootXMLFile.isFile()) {
			return true;
		}
		
		log.info("the web.xml file[{}] located at web root directory is bad :: whetherExist[{}] isFile[{}]", 
				webRootXMLFilePathString, webRootXMLFile.exists(), webRootXMLFile.isFile());
		
		return false;
	}
	
	
	public MainProjectBuildSystemState getNewInstanceOfMainProjectBuildSystemState() throws BuildSystemException {
		String projectPathString= BuildSystemPathSupporter.getProjectPathString(sinnoriInstalledPathString, mainProjectName);
		
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
		SequencedProperties sinnoriConfigurationSequencedPropties = null;
		
		isServer = isValidServerAntBuildXMLFile();
		isAppClient = isValidAppClientAntBuildXMLFile();
		isWebClient = isValidWebClientAntBuildXMLFile();
		
		if (isWebClient) {
			if (!isValidWebRootXMLFile()) {
				String webXMLFilePathString = BuildSystemPathSupporter.getWebRootXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
				
				String errorMessage = String.format(
						"the project's WEB-INF/web.xml[%s] file doesn't exist",
						webXMLFilePathString);
				throw new BuildSystemException(errorMessage);
			}
			
			Properties webClientAntProperties = loadValidWebClientAntPropertiesFile();		
			
			servletSystemLibrayPathString = webClientAntProperties.getProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY);
		}
		
		if (null == servletSystemLibrayPathString) servletSystemLibrayPathString = "";		
		
		
		SinnoriConfiguration sinnoriConfiguration = null;
		
		try {
			sinnoriConfiguration = new SinnoriConfiguration(sinnoriInstalledPathString, mainProjectName);
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (SinnoriConfigurationException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		}
		
		AllDBCPPartConfiguration allDBCPPartItems = sinnoriConfiguration.getAllDBCPPartConfiguration();
		AllSubProjectPartConfiguration allSubProjectPartItems = sinnoriConfiguration.getAllSubProjectPartConfiguration();
		
		dbcpNameList = allDBCPPartItems.getDBCPNameList();
		subProjectNameList = allSubProjectPartItems.getSubProjectNamelist();	
		
		sinnoriConfigurationSequencedPropties =
		sinnoriConfiguration.getSinnoriConfigurationSequencedPropties();
		
		return new MainProjectBuildSystemState(sinnoriInstalledPathString, mainProjectName,
				isServer, isAppClient, isWebClient, servletSystemLibrayPathString,
				dbcpNameList, subProjectNameList, sinnoriConfigurationSequencedPropties);
	}

	
	public void createProject(boolean isServer, boolean isAppClient, boolean isWebClient, 
			String servletSystemLibraryPathString) throws BuildSystemException {
		// FIXME!
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
	
	public void createCommonChildDirectories() throws BuildSystemException {
		log.info("main project[{}]'s common child direcotry creation task start", mainProjectName);

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("config");
		childRelativeDirectoryList.add("resources/message_info");
		childRelativeDirectoryList.add("resources/rsa_keypair");
		childRelativeDirectoryList.add("log/apache");
		childRelativeDirectoryList.add("log/client");
		childRelativeDirectoryList.add("log/server");
		childRelativeDirectoryList.add("log/servlet");
		
		CommonStaticUtil.createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[{}]'s common child direcotry creation task end", mainProjectName);
	}
	
	public void createServerBuildChildDirectories() throws BuildSystemException {
		log.info("main project[{}]'s server child direcotry creation task start", mainProjectName);

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("server_build/APP-INF/classes");
		childRelativeDirectoryList.add("server_build/APP-INF/lib");
		childRelativeDirectoryList.add("server_build/APP-INF/resources");
		childRelativeDirectoryList.add("server_build/corelib/ex");
		childRelativeDirectoryList.add("server_build/corelib/in");
		childRelativeDirectoryList.add("server_build/lib/main/ex");
		childRelativeDirectoryList.add("server_build/lib/main/in");
		childRelativeDirectoryList.add("server_build/lib/test");

		childRelativeDirectoryList.add("server_build/src/main/java/kr/pe/sinnori/impl/server/mybatis");
		childRelativeDirectoryList.add("server_build/src/main/java/kr/pe/sinnori/impl/servertask");
		childRelativeDirectoryList.add(new StringBuilder("server_build/").append(MESSAGE_SOURCE_FILE_RELATIVE_PATH).toString());		
		childRelativeDirectoryList.add("server_build/src/main/java/main");
		childRelativeDirectoryList.add("server_build/src/test/java");
		childRelativeDirectoryList.add("server_build/build");
		childRelativeDirectoryList.add("server_build/dist");
		
		CommonStaticUtil.createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[{}]'s server child direcotry creation task end", mainProjectName);
	}
	
	public void createAppBuildChildDirectories() throws BuildSystemException {
		log.info("main project[{}]'s application build child direcotry creation task start", mainProjectName);

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("client_build/app_build/corelib/ex");
		childRelativeDirectoryList.add("client_build/app_build/corelib/in");
		childRelativeDirectoryList.add("client_build/app_build/lib/main/ex");
		childRelativeDirectoryList.add("client_build/app_build/lib/main/in");
		childRelativeDirectoryList.add("client_build/app_build/lib/test");
		
		childRelativeDirectoryList.add(
				new StringBuilder("client_build/app_build/").append(MESSAGE_SOURCE_FILE_RELATIVE_PATH).toString());
		childRelativeDirectoryList.add("client_build/app_build/src/main/java/main");
		childRelativeDirectoryList.add("client_build/app_build/src/test/java");
		childRelativeDirectoryList.add("client_build/app_build/build");
		childRelativeDirectoryList.add("client_build/app_build/dist");
		
		CommonStaticUtil.createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[{}]'s application build child direcotry creation task end", mainProjectName);
	}
	
	public void createWebBuildChildDirectories() throws BuildSystemException {
		log.info("main project[{}]'s web build child direcotry creation task start", mainProjectName);

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("client_build/web_build/corelib/ex");
		childRelativeDirectoryList.add("client_build/web_build/corelib/in");
		childRelativeDirectoryList.add("client_build/web_build/lib/ex");
		childRelativeDirectoryList.add("client_build/web_build/lib/in");
		childRelativeDirectoryList.add(
				new StringBuilder("client_build/web_build/").append(MESSAGE_SOURCE_FILE_RELATIVE_PATH).toString());
		childRelativeDirectoryList.add("client_build/web_build/src/main/java//kr/pe/sinnori/servlet");
		childRelativeDirectoryList.add("client_build/web_build/src/main/java/kr/pe/sinnori/weblib/common");
		childRelativeDirectoryList.add("client_build/web_build/src/main/java/kr/pe/sinnori/weblib/htmlstring");
		childRelativeDirectoryList.add("client_build/web_build/src/main/java/kr/pe/sinnori/weblib/jdf");
		childRelativeDirectoryList.add("client_build/web_build/src/test/java");
		childRelativeDirectoryList.add("client_build/web_build/build");
		childRelativeDirectoryList.add("client_build/web_build/dist");
		
		CommonStaticUtil.createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[{}]'s web build child direcotry creation task end", mainProjectName);
	}
	
	public void createWebRootChildDirectories() throws BuildSystemException {
		log.info("main project[{}]'s web root child direcotry creation task start", mainProjectName);

		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		childRelativeDirectoryList.add("web_app_base/upload");
		childRelativeDirectoryList.add("web_app_base/ROOT/WEB-INF/classes");
		childRelativeDirectoryList.add("web_app_base/ROOT/WEB-INF/lib");
		
		CommonStaticUtil.createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);

		log.info("main project[{}]'s web root child direcotry creation task end", mainProjectName);
	}
	
	public void createChildDirectories(boolean isServer, boolean isAppClient, boolean isWebClient)
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

	public void createFiles(boolean isServer, boolean isAppClient, boolean isWebClient, 
			String servletSystemLibraryPathString) throws BuildSystemException {
		log.info("main project[{}]'s file creation task start", mainProjectName);
		
		if(null == servletSystemLibraryPathString) {
			throw new IllegalArgumentException("the paramet servletSystemLibraryPathString is null");
		}
		
		createNewSinnoriConfigFile();
		createNewLogbackConfigFile();
		createNewEchoMessageInfomationFile();

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

	public void createWebClientBuildSystemFiles(String servletSystemLibraryPathString) throws BuildSystemException {
		log.info("main project[{}]'s web client build system files creation task start", mainProjectName);
		
		if(null == servletSystemLibraryPathString) {
			throw new IllegalArgumentException("the paramet servletSystemLibraryPathString is null");
		}

		createNewWebClientAntBuildFile();
		createNewWebClientAntPropertiesFile(servletSystemLibraryPathString);
		createNewWebClientEchoIOFileSet();
		copyWebClientSampleFiles();

		log.info("main project[{}]'s web client build system files creation task end", mainProjectName);
	}
	
	public void deleteWebCientBuildPath() throws BuildSystemException {
		log.info("main project[{}]'s web client build path deletion task start", mainProjectName);
		
		String webClientBuildPathString = BuildSystemPathSupporter
				.getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName);
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

	
	public void createWebRootSampleFiles() throws BuildSystemException {
		log.info("main project[{}]'s web root sample files creation task start", mainProjectName);

		copyWebRootSampleFiles();

		log.info("main project[{}]'s web root sample files creation task end", mainProjectName);
	}
	
	public void deleteWebRoot() throws BuildSystemException {
		log.info("main project[{}]'s web root path deletion task start", mainProjectName);
		
		String webRootPathString = BuildSystemPathSupporter
				.getWebRootPathString(sinnoriInstalledPathString, mainProjectName);
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

	public void copyWebRootSampleFiles() throws BuildSystemException {
		log.info("main project[{}]'s web root sample files copy task start", mainProjectName);

		String sinnoriResourcePathString = BuildSystemPathSupporter
				.getSinnoriResourcesPathString(sinnoriInstalledPathString);
		String targetWebRootPathString = BuildSystemPathSupporter.getWebRootPathString(sinnoriInstalledPathString, mainProjectName);

		String sourceWebRootPathString = new StringBuilder(sinnoriResourcePathString).append(File.separator)
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

	public void createNewWebClientAntBuildFile() throws BuildSystemException {
		log.info("main project[{}]'s web client ant build.xml file creation task start", mainProjectName);

		String webClientAntBuildXMLFileContents = BuildSystemFileContents
				.getWebClientAntBuildXMLFileContents(mainProjectName);

		String webClientAntBuildXMLFilePahtString = BuildSystemPathSupporter
				.getWebClientAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);

		File webClientAntBuildXMLFile = new File(webClientAntBuildXMLFilePahtString);

		try {
			CommonStaticUtil.createNewFile(webClientAntBuildXMLFile, webClientAntBuildXMLFileContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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

	public void createNewWebClientEchoIOFileSet() throws BuildSystemException {
		log.info("main project[{}]'s web client message[{}] io file set creation task start", mainProjectName,
				ECHO_MESSAGE_ID);

		String messageIOSetBasedirectoryPathString = null;

		{
			final String webClientBuildPathString = BuildSystemPathSupporter
					.getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName);

			messageIOSetBasedirectoryPathString = getMessageIOSetBasedirectoryPathString(webClientBuildPathString);
			createNewMessageIDDirectory(messageIOSetBasedirectoryPathString, ECHO_MESSAGE_ID);
		}

		createNewMessageSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createNewDecoderSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createNewEncoderSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createNewServerCodecSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createNewClientCodecSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);

		log.info("main project[{}]'s web client message[{}] io file set creation task end", mainProjectName,
				ECHO_MESSAGE_ID);
	}

	public void copyWebClientSampleFiles() throws BuildSystemException {
		log.info("mainproject[{}]'s web client sample source files copy task start", mainProjectName);

		String sinnoriResourcePathString = BuildSystemPathSupporter
				.getSinnoriResourcesPathString(sinnoriInstalledPathString);
		String webClientBuildPathString = BuildSystemPathSupporter
				.getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName);

		String sourceDirectoryPathString = new StringBuilder(sinnoriResourcePathString).append(File.separator)
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

	public void createAppClientBuildSystemFiles() throws BuildSystemException {
		log.info("mainproject[{}]'s application client build system files creation task start", mainProjectName);

		createNewAppClientAntBuildXMLFile();
		createNewAppClientDosShellFile();
		createNewAppClientUnixShellFile();
		copyAppClientSampleSourceFiles();
		createNewAppClientEchoIOFileSet();

		log.info("mainproject[{}]'s application client build system files creation task end", mainProjectName);
	}
	
	public void deleteAppClientBuildPath() throws BuildSystemException {
		log.info("mainproject[{}]'s application client build path deletion task start", mainProjectName);
		
		String appClientBuildPathString = BuildSystemPathSupporter
				.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName);
		
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

	public void createNewAppClientAntBuildXMLFile() throws BuildSystemException {
		log.info("main project[{}]'s application client ant build.xml file creation task start", mainProjectName);

		String appClientAntBuildXMLFileContents = BuildSystemFileContents.getAppClientAntBuildXMLFileContents(mainProjectName,
				CommonStaticFinalVars.APPCLIENT_MAIN_CLASS_FULL_NAME_VALUE,
				CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE);

		String appClientAntBuildXMLFilePahtString = BuildSystemPathSupporter
				.getAppClientAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);

		File appClientAntBuildXMLFile = new File(appClientAntBuildXMLFilePahtString);

		try {
			CommonStaticUtil.createNewFile(appClientAntBuildXMLFile, appClientAntBuildXMLFileContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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

	public void createNewAppClientDosShellFile() throws BuildSystemException {
		log.info("main project[{}]'s application client dos shell file creation task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String appClientBuildPathString = BuildSystemPathSupporter.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName);

		String appClientDosShellFileContents = BuildSystemFileContents.getDosShellContents(sinnoriInstalledPathString,
				mainProjectName, JVM_OPTIONS_OF_APP_CLIENT, LOG_TYPE.APPCLIENT, appClientBuildPathString,
				relativeExecutabeJarFileName);

		/** AppClient.bat */
		String appClientDosShellFilePathString = new StringBuilder(appClientBuildPathString).append(File.separator)
				.append(mainProjectName).append("AppClient.bat").toString();
		File appClientDosShellFile = new File(appClientDosShellFilePathString);

		try {
			CommonStaticUtil.createNewFile(appClientDosShellFile, appClientDosShellFileContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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

	public void overwriteAppClientDosShellFile() throws BuildSystemException {
		log.info("main project[{}]'s application client dos shell file  overwrite task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String appClientBuildPathString = BuildSystemPathSupporter.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName);

		String appClientDosShellFileContents = BuildSystemFileContents.getDosShellContents(sinnoriInstalledPathString, mainProjectName,
				JVM_OPTIONS_OF_APP_CLIENT, LOG_TYPE.APPCLIENT, appClientBuildPathString,
				relativeExecutabeJarFileName);

		/** AppClient.bat */
		String appClientDosShellFilePathString = new StringBuilder(appClientBuildPathString).append(File.separator)
				.append(mainProjectName).append("AppClient.bat").toString();
		File appClientDosShellFile = new File(appClientDosShellFilePathString);

		try {
			CommonStaticUtil.overwriteFile(appClientDosShellFile, appClientDosShellFileContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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

	public void createNewAppClientUnixShellFile() throws BuildSystemException {
		log.info("main project[{}]'s application client unix shell file creation task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String appClientBuildPathString = BuildSystemPathSupporter.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName);

		String appClientUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(sinnoriInstalledPathString, mainProjectName,
				JVM_OPTIONS_OF_APP_CLIENT, LOG_TYPE.APPCLIENT, appClientBuildPathString, relativeExecutabeJarFileName);

		/** AppClient.bat */
		String appClientUnixShellFilePathString = new StringBuilder(appClientBuildPathString).append(File.separator)
				.append(mainProjectName).append("AppClient.sh").toString();
		File appClientUnixShellFile = new File(appClientUnixShellFilePathString);

		try {
			CommonStaticUtil.createNewFile(appClientUnixShellFile, appClientUnixShellFileContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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

	public void overwriteAppClientUnixShellFile() throws BuildSystemException {
		log.info("main project[{}]'s application client unix shell file overwrite task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String appClientBuildPathString = BuildSystemPathSupporter.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName);

		String appClientUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(sinnoriInstalledPathString, mainProjectName,
				JVM_OPTIONS_OF_APP_CLIENT, LOG_TYPE.APPCLIENT, appClientBuildPathString, relativeExecutabeJarFileName);

		/** AppClient.bat */
		String appClientUnixShellFilePathString = new StringBuilder(appClientBuildPathString).append(File.separator)
				.append(mainProjectName).append("AppClient.sh").toString();
		File appClientUnixShellFile = new File(appClientUnixShellFilePathString);

		try {
			CommonStaticUtil.overwriteFile(appClientUnixShellFile, appClientUnixShellFileContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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

	public void copyAppClientSampleSourceFiles() throws BuildSystemException {
		log.info("application client sample source files copy task start");

		String sinnoriResourcePathString = BuildSystemPathSupporter
				.getSinnoriResourcesPathString(sinnoriInstalledPathString);
		String applicationClientBuildPathString = BuildSystemPathSupporter.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName);

		String sourceDirectoryPathString = new StringBuilder(sinnoriResourcePathString).append(File.separator)
				.append("newproject").append(File.separator).append("app_build").append(File.separator).append("src")
				.toString();

		String targetDirectoryPathString = new StringBuilder(applicationClientBuildPathString).append(File.separator)
				.append("src").toString();

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

		log.info("application client sample source files copy task end");
	}

	public void createNewAppClientEchoIOFileSet() throws BuildSystemException {
		log.info("main project[{}]'s application client message[{}] io file set creation task start", mainProjectName,
				ECHO_MESSAGE_ID);

		String messageIOSetBasedirectoryPathString = null;

		{
			final String appClientBuildPathString = BuildSystemPathSupporter
					.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName);

			messageIOSetBasedirectoryPathString = getMessageIOSetBasedirectoryPathString(appClientBuildPathString);
			createNewMessageIDDirectory(messageIOSetBasedirectoryPathString, ECHO_MESSAGE_ID);
		}

		createNewMessageSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createNewDecoderSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createNewEncoderSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createNewServerCodecSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createNewClientCodecSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);

		log.info("main project[{}]'s application client message[{}] io file set creation task end", mainProjectName,
				ECHO_MESSAGE_ID);
	}

	public void createServerBuildSystemFiles() throws BuildSystemException {
		log.info("server build system files creation task start");

		createNewServerAntBuildXMLFile();
		createNewServerDosShellFile();
		createNewServerUnixShellFile();
		copyServerSampleSourceFiles();
		createNewServerEchoTaskFile();
		createServerEchoIOFileSet();

		log.info("server build system files creation task end");
	}
	
	public void deleteServerBuildPath() throws BuildSystemException {
		log.info("mainproject[{}]'s server build path deletion task start", mainProjectName);
		
		String serverBuildPathString = BuildSystemPathSupporter
				.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName);
		
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

	private String getMessageIOSetBasedirectoryPathString(String buildPathString) {
		String messageIOSetBasedirectoryPathString = null;
		if (File.separator.equals("/")) {
			messageIOSetBasedirectoryPathString = new StringBuilder(buildPathString).append(File.separator)
					.append(MESSAGE_SOURCE_FILE_RELATIVE_PATH).toString();

		} else {
			messageIOSetBasedirectoryPathString = new StringBuilder(buildPathString).append(File.separator)
					.append(MESSAGE_SOURCE_FILE_RELATIVE_PATH.replaceAll("/", "\\\\")).toString();

		}

		return messageIOSetBasedirectoryPathString;
	}

	private void createNewMessageIDDirectory(String messageIOSetBasedirectoryPathString, String messageID)
			throws BuildSystemException {
		List<String> childRelativeDirectoryList = new ArrayList<String>();
		childRelativeDirectoryList.add(messageID);
		CommonStaticUtil.createChildDirectoriesOfBasePath(messageIOSetBasedirectoryPathString,
				childRelativeDirectoryList);
	}

	public void createServerEchoIOFileSet() throws BuildSystemException {
		log.info("main project[{}]'s server {} message io file set creation task start", mainProjectName,
				ECHO_MESSAGE_ID);

		String messageIOSetBasedirectoryPathString = null;

		{
			final String serverBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName);
			messageIOSetBasedirectoryPathString = getMessageIOSetBasedirectoryPathString(serverBuildPathString);
			createNewMessageIDDirectory(messageIOSetBasedirectoryPathString, ECHO_MESSAGE_ID);
		}

		createNewMessageSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createNewDecoderSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createNewEncoderSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createNewServerCodecSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createNewClientCodecSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);

		log.info("main project[{}]'s server {} message io file set creation task end", mainProjectName,
				ECHO_MESSAGE_ID);
	}

	public MessageInfo getMessageInfo(String messageID) throws BuildSystemException {

		String echoMessageInfoFilePathString = new StringBuilder(
				BuildSystemPathSupporter.getMessageInfoPathString(sinnoriInstalledPathString, mainProjectName))
						.append(File.separator).append(messageID).append(".xml").toString();
		File echoMessageInfoFile = new File(echoMessageInfoFilePathString);

		MessageInfoSAXParser messageInfoSAXParser = null;
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (MessageInfoSAXParserException e) {
			System.exit(1);
		}
		MessageInfo echoMessageInfo = null;
		try {
			echoMessageInfo = messageInfoSAXParser.parse(echoMessageInfoFile, true);
		} catch (IllegalArgumentException | SAXException | IOException e) {
			String errorMessage = new StringBuilder("fail to parse sinnori message information xml file[")
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

		IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager.getInstance();

		String messageFileContnets = ioFileSetContentsBuilderManager.getMessageSourceFileContents(messageID, author,
				messageInfo);

		/**
		 * <build path>/src/main/java/kr/pe/sinnori/impl/message/<Message
		 * ID>/<Message ID>.java
		 */
		String messageFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append(".java").toString();

		File messageFile = new File(messageFilePathString);

		try {
			CommonStaticUtil.createNewFile(messageFile, messageFileContnets,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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

		IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager.getInstance();

		String decoderFileContnets = ioFileSetContentsBuilderManager.getDecoderSourceFileContents(messageID, author,
				messageInfo);

		/**
		 * <build path>/src/main/java/kr/pe/sinnori/impl/message/<Message
		 * ID>/<Message ID>Decoder.java
		 */
		String decoderFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append("Decoder.java").toString();

		File decoderFile = new File(decoderFilePathString);

		try {
			CommonStaticUtil.createNewFile(decoderFile, decoderFileContnets,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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

		IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager.getInstance();

		String encoderFileContnets = ioFileSetContentsBuilderManager.getEncoderSourceFileContents(messageID, author,
				messageInfo);

		/**
		 * <build path>/src/main/java/kr/pe/sinnori/impl/message/<Message
		 * ID>/<Message ID>Decoder.java
		 */
		String encoderFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append("Encoder.java").toString();

		File encoderFile = new File(encoderFilePathString);

		try {
			CommonStaticUtil.createNewFile(encoderFile, encoderFileContnets,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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

		IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager.getInstance();

		String serverCodecFileContnets = ioFileSetContentsBuilderManager.getServerCodecSourceFileContents(
				CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL, messageID, author);

		/**
		 * <build path>/src/main/java/kr/pe/sinnori/impl/message/<Message
		 * ID>/<Message ID>ServerCodec.java
		 */
		String serverCodecFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append("ServerCodec.java").toString();

		File serverCodecFile = new File(serverCodecFilePathString);

		try {
			CommonStaticUtil.createNewFile(serverCodecFile, serverCodecFileContnets,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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

		IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager.getInstance();

		String clientCodecFileContnets = ioFileSetContentsBuilderManager.getClientCodecSourceFileContents(
				CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL, messageID, author);

		/**
		 * <build path>/src/main/java/kr/pe/sinnori/impl/message/<Message
		 * ID>/<Message ID>ClientCodec.java
		 */
		String clientCodecFilePathString = new StringBuilder(messageIOSetBasedirectoryPathString).append(File.separator)
				.append(messageID).append(File.separator).append(messageID).append("ClientCodec.java").toString();

		File clientCodecFile = new File(clientCodecFilePathString);

		try {
			CommonStaticUtil.createNewFile(clientCodecFile, clientCodecFileContnets,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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

	public void createNewServerEchoTaskFile() throws BuildSystemException {
		log.info("main project[{}]'s server echo task file creation task start", mainProjectName);

		// final String messageID = "Echo";
		String echoServerTaskContents = BuildSystemFileContents.getEchoServerTaskContents();

		String serverBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName);

		/** server_build/src/main/java/kr/pe/sinnori/impl/servertask */
		String serverEchoTaskFilePathString = new StringBuilder(serverBuildPathString).append(File.separator)
				.append("src").append(File.separator).append("main").append(File.separator).append("java")
				.append(File.separator).append("kr").append(File.separator).append("pe").append(File.separator)
				.append("sinnori").append(File.separator).append("impl").append(File.separator).append("servertask")
				.append(File.separator).append(ECHO_MESSAGE_ID).append("ServerTask.java").toString();

		File serverEchoTaskFile = new File(serverEchoTaskFilePathString);

		try {
			CommonStaticUtil.createNewFile(serverEchoTaskFile, echoServerTaskContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server dos shell file[").append(serverEchoTaskFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s server echo task file creation task end", mainProjectName);
	}

	public void copyServerSampleSourceFiles() throws BuildSystemException {
		log.info("server sample source files copy task start");

		String sinnoriResourcePathString = BuildSystemPathSupporter
				.getSinnoriResourcesPathString(sinnoriInstalledPathString);
		String serverBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName);

		String sourceDirectoryPathString = new StringBuilder(sinnoriResourcePathString).append(File.separator)
				.append("newproject").append(File.separator).append("server_build").append(File.separator).append("src")
				.toString();

		String targetDirectoryPathString = new StringBuilder(serverBuildPathString).append(File.separator).append("src")
				.toString();

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

		log.info("server sample source files copy task end");
	}

	public void createNewServerDosShellFile() throws BuildSystemException {
		log.info("main project[{}]'s server dos shell file creation task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String serverBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName);

		String serverDosShellFileContents = BuildSystemFileContents.getDosShellContents(sinnoriInstalledPathString, mainProjectName,
				JVM_OPTIONS_OF_SERVER, LOG_TYPE.SERVER, serverBuildPathString, relativeExecutabeJarFileName);

		/** Server.bat */
		String serverDosShellFilePathString = new StringBuilder(serverBuildPathString).append(File.separator)
				.append(mainProjectName).append("Server.bat").toString();
		File serverDosShellFile = new File(serverDosShellFilePathString);

		try {
			CommonStaticUtil.createNewFile(serverDosShellFile, serverDosShellFileContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server dos shell file[").append(serverDosShellFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s server dos shell file creation task end", mainProjectName);
	}

	public void overwriteServerDosShellFile() throws BuildSystemException {
		log.info("main project[{}]'s server dos shell file overwrite task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String serverBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName);

		String serverDosShellFileContents = BuildSystemFileContents.getDosShellContents(sinnoriInstalledPathString, mainProjectName,
				JVM_OPTIONS_OF_SERVER, LOG_TYPE.SERVER, serverBuildPathString, relativeExecutabeJarFileName);

		/** Server.bat */
		String serverDosShellFilePathString = new StringBuilder(serverBuildPathString).append(File.separator)
				.append(mainProjectName).append("Server.bat").toString();
		File serverDosShellFile = new File(serverDosShellFilePathString);

		try {
			CommonStaticUtil.overwriteFile(serverDosShellFile, serverDosShellFileContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server dos shell file[").append(serverDosShellFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s server dos shell file overwrite task end", mainProjectName);
	}

	public void createNewServerUnixShellFile() throws BuildSystemException {
		log.info("main project[{}]'s server unix shell file creation task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String serverBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName);

		String serverUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(sinnoriInstalledPathString, mainProjectName,
				JVM_OPTIONS_OF_SERVER, LOG_TYPE.SERVER, serverBuildPathString, relativeExecutabeJarFileName);

		String serverUnixShellFilePathString = new StringBuilder(serverBuildPathString).append(File.separator)
				.append(mainProjectName).append("Server.sh").toString();

		File serverUnixShellFile = new File(serverUnixShellFilePathString);

		try {
			CommonStaticUtil.createNewFile(serverUnixShellFile, serverUnixShellFileContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server unix shell file[").append(serverUnixShellFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s server unix shell file creation task end", mainProjectName);
	}

	public void overwriteServerUnixShellFile() throws BuildSystemException {
		log.info("main project[{}]'s server unix shell file overwrite task start", mainProjectName);

		String relativeExecutabeJarFileName = new StringBuilder("dist").append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE).toString();

		String serverBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName);

		String serverUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(sinnoriInstalledPathString, mainProjectName,
				JVM_OPTIONS_OF_SERVER, LOG_TYPE.SERVER, serverBuildPathString, relativeExecutabeJarFileName);

		String serverUnixShellFilePathString = new StringBuilder(serverBuildPathString).append(File.separator)
				.append(mainProjectName).append("Server.sh").toString();

		File serverUnixShellFile = new File(serverUnixShellFilePathString);

		try {
			CommonStaticUtil.overwriteFile(serverUnixShellFile, serverUnixShellFileContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server unix shell file[").append(serverUnixShellFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s server unix shell file overwrite task end", mainProjectName);
	}

	public void createNewServerAntBuildXMLFile() throws BuildSystemException {
		log.info("main project[{}]'s server ant build.xml file creation task start", mainProjectName);

		String sererAntBuildXMLFileContents = BuildSystemFileContents.getServerAntBuildXMLFileContent(mainProjectName,
				CommonStaticFinalVars.SERVER_MAIN_CLASS_FULL_NAME_VALUE,
				CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE);

		String serverAntBuildXMLFilePahtString = BuildSystemPathSupporter.getServerAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);

		File serverAntBuildXMLFile = new File(serverAntBuildXMLFilePahtString);

		try {
			CommonStaticUtil.createNewFile(serverAntBuildXMLFile, sererAntBuildXMLFileContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s server ant build.xml file[").append(serverAntBuildXMLFilePahtString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s server ant build.xml file creation task end", mainProjectName);
	}

	public void createNewEchoMessageInfomationFile() throws BuildSystemException {
		log.info("main project[{}]'s echo message information file creation task start", mainProjectName);

		String echoMessageInformationFileContents = BuildSystemFileContents.getEchoMessageInfoContents();

		String echoMessageXMLFilePathString = new StringBuilder(
				BuildSystemPathSupporter.getMessageInfoPathString(sinnoriInstalledPathString, mainProjectName))
						.append(File.separator).append("Echo.xml").toString();

		File logbackConfigFile = new File(echoMessageXMLFilePathString);

		try {
			CommonStaticUtil.createNewFile(logbackConfigFile, echoMessageInformationFileContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s echo message information file[").append(echoMessageXMLFilePathString).append("]")
					.toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s echo message information file creation task end", mainProjectName);
	}

	public void createNewLogbackConfigFile() throws BuildSystemException {
		log.info("main project[{}]'s logback config file creation task start", mainProjectName);

		String logbackConfigFileContents = BuildSystemFileContents.getContentsOfLogback();

		String logbackConfigFilePathString = BuildSystemPathSupporter.getLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);

		File logbackConfigFile = new File(logbackConfigFilePathString);

		try {
			CommonStaticUtil.createNewFile(logbackConfigFile, logbackConfigFileContents,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s logback config file[").append(logbackConfigFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s logback config file creation task end", mainProjectName);
	}

	public void createNewSinnoriConfigFile() throws BuildSystemException {
		log.info("main project[{}]'s config file creation task start", mainProjectName);
		
		String sinnoriConfigFilePathString = BuildSystemPathSupporter.getSinnoriConfigFilePathString(sinnoriInstalledPathString, mainProjectName);

		SinnoriItemIDInfoManger mainProjectItemIDInfo = SinnoriItemIDInfoManger.getInstance();

		SequencedProperties newSinnoriConfigSequencedProperties = mainProjectItemIDInfo
				.getNewSinnoriConfigSequencedProperties(sinnoriInstalledPathString, mainProjectName);
		
		SinnoriConfiguration sinnoriConfiguration = null;
		try {
			sinnoriConfiguration = new SinnoriConfiguration(sinnoriInstalledPathString, mainProjectName, newSinnoriConfigSequencedProperties);
			sinnoriConfiguration.createNewFile();
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("fail to create the main project[").append(mainProjectName)
					.append("]'s sinnori configuration file").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to create the main project's sinnori configuration file[").append(sinnoriConfigFilePathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (SinnoriConfigurationException e) {
			String errorMessage = new StringBuilder("fail to create the main project's sinnori configuration file[").append(sinnoriConfigFilePathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s config file creation task end", mainProjectName);
	}
	
	public void overwriteSinnoriConfigFile(SequencedProperties modifiedSinnoriConfigSequencedProperties) throws BuildSystemException {
		log.info("main project[{}]'s config file overwrite task start", mainProjectName);
		
		String sinnoriConfigFilePathString = BuildSystemPathSupporter.getSinnoriConfigFilePathString(sinnoriInstalledPathString, mainProjectName);

		
		SinnoriConfiguration sinnoriConfiguration = null;
		try {
			sinnoriConfiguration = new SinnoriConfiguration(sinnoriInstalledPathString, mainProjectName, modifiedSinnoriConfigSequencedProperties);
			sinnoriConfiguration.overwriteFile();
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("fail to save the main project[").append(mainProjectName)
					.append("]'s sinnori configuration file").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (FileNotFoundException e) {			
			String errorMessage = new StringBuilder("fail to overwrite the main project's sinnori configuration file").append(sinnoriConfigFilePathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (IOException e) {			
			String errorMessage = new StringBuilder("fail to overwrite the main project's sinnori configuration file").append(sinnoriConfigFilePathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (SinnoriConfigurationException e) {			
			String errorMessage = new StringBuilder("fail to overwrite the main project's sinnori configuration file").append(sinnoriConfigFilePathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s config file overwrite task end", mainProjectName);
	}
	
	public SequencedProperties loadSinnoriConfigPropertiesFile() throws BuildSystemException {
		SinnoriConfiguration sinnoriConfiguration = null;
		
		try {
			sinnoriConfiguration = new SinnoriConfiguration(sinnoriInstalledPathString, mainProjectName);
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		} catch (SinnoriConfigurationException e) {
			log.warn(e.getMessage(), e);
			throw new BuildSystemException(e.getMessage());
		}
		
		return sinnoriConfiguration.getSinnoriConfigurationSequencedPropties();
		
	}
	
	public void createNewWebClientAntPropertiesFile(String servletSystemLibraryPathString)
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
		
		String webClientAntPropertiesFilePathString = BuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(sinnoriInstalledPathString, mainProjectName);
		
		SequencedProperties antBuiltInProperties = new SequencedProperties();

		// antBuiltInProperties.setProperty(CommonStaticFinalVars.IS_TOMCAT_KEY, isTomcat ? "true" : "false");

		antBuiltInProperties.setProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY,
				servletSystemLibraryPathString);

		
		try {
			SequencedPropertiesUtil.createNewSequencedPropertiesFile(antBuiltInProperties, getWebClientAntPropertiesTitle(),
					webClientAntPropertiesFilePathString, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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
	
	public void overwriteWebClientAntPropertiesFile(String servletSystemLibraryPathString)
			throws BuildSystemException {
		log.info("main project[{}]'s web client ant properties file overwrite task start", mainProjectName);
		
		if(null == servletSystemLibraryPathString) {
			throw new IllegalArgumentException("the paramet servletSystemLibraryPathString is null");
		}
		
		String webClientAntPropertiesFilePathString = BuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(sinnoriInstalledPathString, mainProjectName);
		
		File servletSystemLibraryPath = new File(servletSystemLibraryPathString);
		if (servletSystemLibraryPath.exists()) {
			String errorMessage = new StringBuilder("the web client's servlet system library path[")
					.append(servletSystemLibraryPathString)
					.append("] doesn't exist").toString();

			throw new BuildSystemException(errorMessage);
		}

		SequencedProperties antBuiltInProperties = new SequencedProperties();
		antBuiltInProperties.setProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY,
				servletSystemLibraryPathString);

		
		try {
			SequencedPropertiesUtil.overwriteSequencedPropertiesFile(antBuiltInProperties, getWebClientAntPropertiesTitle(),
					webClientAntPropertiesFilePathString, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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
			String errorMessage = new StringBuilder("fail to overwrite the main project[").append(mainProjectName)
					.append("]'s web client ant properties file[").append(webClientAntPropertiesFilePathString).append("]")
					.toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}

		log.info("main project[{}]'s web client ant properties file overwrite task end", mainProjectName);
	}

	private String getWebClientAntPropertiesTitle() {
		return new StringBuilder("project[").append(mainProjectName).append("]'s web client ant properteis file").toString();
	}
		
	public Properties loadValidWebClientAntPropertiesFile() throws BuildSystemException {		
		String webClientAntPropertiesFilePathString = BuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(sinnoriInstalledPathString, mainProjectName);
		
		SequencedProperties webClientAntProperties = null;
		try {
			webClientAntProperties = SequencedPropertiesUtil.loadSequencedPropertiesFile(webClientAntPropertiesFilePathString, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
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
	
	public void applySinnoriInstalledPath() throws BuildSystemException {
		applySinnoriInstalledPathToConfigFile();
		
		if (File.separator.equals("/")) {				
			/** unix shell */
			if (isValidServerAntBuildXMLFile()) {
				overwriteServerUnixShellFile();
			}
			if (isValidAppClientAntBuildXMLFile()) {
				overwriteAppClientUnixShellFile();
			}
		} else {
			/** dos shell */
			if (isValidServerAntBuildXMLFile()) {
				overwriteServerDosShellFile();
			}
			if (isValidAppClientAntBuildXMLFile()) {
				overwriteAppClientDosShellFile();
			}
		}
	}

	public void applySinnoriInstalledPathToConfigFile() throws BuildSystemException {
		String sinnoriConfigFilePathString = BuildSystemPathSupporter.getSinnoriConfigFilePathString(sinnoriInstalledPathString, mainProjectName);

		SinnoriConfiguration sinnoriConfiguration = null;
		try {
			sinnoriConfiguration = new SinnoriConfiguration(sinnoriInstalledPathString, mainProjectName);
			sinnoriConfiguration.applySinnoriInstalledPath();
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("fail to apply Sinnori installed path to the main project[")
					.append(mainProjectName).append("] config file").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder(
					"fail to apply Sinnori installed path to the main project config file[")
							.append(sinnoriConfigFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to apply Sinnori installed path to the main project config file[")
							.append(sinnoriConfigFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		} catch (SinnoriConfigurationException e) {
			String errorMessage = new StringBuilder(
					"fail to apply Sinnori installed path to the main project config file[")
							.append(sinnoriConfigFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(
					new StringBuilder(errorMessage).append(", errormessage=").append(e.getMessage()).toString());
		}
	}
		

	public void changeProjectState(boolean isServer, boolean isAppClient, 
			boolean isWebClient, String servletSystemLibraryPathString, 
			SequencedProperties modifiedSinnoriConfigSequencedProperties) throws BuildSystemException {
		log.info("main project[{}] changeProjectState method start", mainProjectName);
		
		if (!isServer && !isAppClient && !isWebClient) {
			throw new IllegalArgumentException("You must choose one more build system type but isSerer=false, isAppClient=false, isWebClient=false");
		}
		
		if (isServer) {
			String serverBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName);
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
				String serverAntBuildFilePathString = BuildSystemPathSupporter
						.getServerAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
				
				log.warn("the server buid.xml file[{}] is bad beacse it doesn't exist or is not a file, so skip deletion of server build system", 
						serverAntBuildFilePathString);
			}
		}
		
		if (isAppClient) {
			String appClientBuildPathString = BuildSystemPathSupporter.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName);
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
				String appClientAntBuildFilePathString = BuildSystemPathSupporter
						.getAppClientAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
				
				log.warn("the app client buid.xml file[{}] is bad beacse it doesn't exist or is not a file, so skip deletion of app client build system", 
						appClientAntBuildFilePathString);
			}
			
		}
		
		if (isWebClient) {
			{
				String webClientBuildPathString = BuildSystemPathSupporter.getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName);
				File webClientBuildPath = new File(webClientBuildPathString);
				
				if (!webClientBuildPath.exists()) {
					createWebBuildChildDirectories();
					createWebClientBuildSystemFiles(servletSystemLibraryPathString);
				} else {				
					log.warn("the web client build path[{}] exists, so skip creation of web client build system", webClientBuildPathString);
				}		
			}
			{
				String webRootPathString = BuildSystemPathSupporter.getWebRootPathString(sinnoriInstalledPathString, mainProjectName);
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
				String webClientAntBuildFilePathString = BuildSystemPathSupporter
						.getWebClientAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
				log.warn("the web client buid.xml file[{}] is bad beacse it doesn't exist or is not a file, so skip deletion of web client build system",
						webClientAntBuildFilePathString);
			}			

			if (isValidWebRootXMLFile()) {
				deleteWebRoot();
			} else {
				String webRootXMLFilePathString = BuildSystemPathSupporter.getWebRootXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
				log.warn("the web.xml file[{}] located at web root direcotry is bad beacse it doesn't exist or is not a file, so skip deletion of web root system", 
						webRootXMLFilePathString);
			}
		}
		
		overwriteSinnoriConfigFile(modifiedSinnoriConfigSequencedProperties);
		
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
