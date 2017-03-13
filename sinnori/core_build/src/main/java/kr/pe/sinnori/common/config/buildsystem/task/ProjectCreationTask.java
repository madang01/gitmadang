package kr.pe.sinnori.common.config.buildsystem.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import kr.pe.sinnori.common.config.buildsystem.BuildSystemFileContents;
import kr.pe.sinnori.common.config.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.exception.MessageInfoSAXParserException;
import kr.pe.sinnori.common.message.builder.IOFileSetContentsBuilderManager;
import kr.pe.sinnori.common.message.builder.info.MessageInfo;
import kr.pe.sinnori.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.common.util.SequencedPropertiesUtil;

public class ProjectCreationTask {
	private Logger log = LoggerFactory
			.getLogger(ProjectCreationTask.class);
	
	private final String MESSAGE_SOURCE_FILE_RELATIVE_PATH = "src/main/java/kr/pe/sinnori/impl/message";
	final String ECHO_MESSAGE_ID = "Echo";
	final String AUTHOR = "Won Jonghoon";
	
	private String mainProjectName;
	private String sinnoriInstalledPathString;
	private boolean isServer;
	/** jvmOptionsOfServer : -server -Xmx1024m -Xms1024m */
	private String jvmOptionsOfServer;
	private boolean isAppClient;
	private String jvmOptionsOfAppClient;
	private boolean isWebClient;
	private String servletSystemLibraryPathString;
	
	private String projectPathString;
	
	public ProjectCreationTask(String mainProjectName, String sinnoriInstalledPathString,
			boolean isServer, String jvmOptionsOfServer, 
			boolean isAppClient, String jvmOptionsOfAppClient, 
			boolean isWebClient,
			String servletSystemLibraryPathString) {
		this.mainProjectName = mainProjectName;
		this.sinnoriInstalledPathString = sinnoriInstalledPathString;
		this.isServer = isServer;
		this.jvmOptionsOfServer = jvmOptionsOfServer;
		this.isAppClient = isAppClient;
		this.jvmOptionsOfAppClient = jvmOptionsOfAppClient;
		this.isWebClient = isWebClient;
		this.servletSystemLibraryPathString = servletSystemLibraryPathString;
		
		this.projectPathString = BuildSystemPathSupporter
				.getProjectPathString(mainProjectName,
						sinnoriInstalledPathString);
	}
	
	public void createChildDirectories() throws BuildSystemException {
		log.info("main project[{}]'s child direcotry creation task start", mainProjectName);
		
		
		List<String> childRelativeDirectoryList = new ArrayList<String>();
		
		/**
		 * common directory
		 */
		childRelativeDirectoryList.add("config");
		childRelativeDirectoryList.add("resources/message_info");
		childRelativeDirectoryList.add("resources/rsa_keypair");
		childRelativeDirectoryList.add("log/apache");
		childRelativeDirectoryList.add("log/client");
		childRelativeDirectoryList.add("log/server");
		childRelativeDirectoryList.add("log/servlet");
		
		
		if (isServer) {
			childRelativeDirectoryList.add("server_build/APP-INF/classes");
			childRelativeDirectoryList.add("server_build/APP-INF/lib");
			childRelativeDirectoryList.add("server_build/APP-INF/resources");			
			childRelativeDirectoryList.add("server_build/corelib/ex");
			childRelativeDirectoryList.add("server_build/corelib/in");
			childRelativeDirectoryList.add("server_build/lib/ex");
			childRelativeDirectoryList.add("server_build/lib/in");
			childRelativeDirectoryList.add(new StringBuilder("server_build/")
					.append(MESSAGE_SOURCE_FILE_RELATIVE_PATH).toString());
			
			childRelativeDirectoryList.add("server_build/src/main/java/kr/pe/sinnori/impl/server/mybatis");
			childRelativeDirectoryList.add("server_build/src/main/java/kr/pe/sinnori/impl/servertask");
			childRelativeDirectoryList.add("server_build/src/main/java/kr/pe/sinnori/impl/message");
			childRelativeDirectoryList.add("server_build/src/main/java/main");
			childRelativeDirectoryList.add("server_build/src/test/java");
			childRelativeDirectoryList.add("server_build/build");
			childRelativeDirectoryList.add("server_build/dist");
			
		}
		
		if (isAppClient) {			
			childRelativeDirectoryList.add("client_build/app_build/corelib/ex");
			childRelativeDirectoryList.add("client_build/app_build/corelib/in");			
			childRelativeDirectoryList.add("client_build/app_build/lib/ex");
			childRelativeDirectoryList.add("client_build/app_build/lib/in");
			childRelativeDirectoryList.add(new StringBuilder("client_build/app_build/")
					.append(MESSAGE_SOURCE_FILE_RELATIVE_PATH).toString());
			childRelativeDirectoryList.add("client_build/app_build/src/main/java/main");
			childRelativeDirectoryList.add("client_build/app_build/src/test/java");
			childRelativeDirectoryList.add("client_build/app_build/build");			
			childRelativeDirectoryList.add("client_build/app_build/dist");
		}
		
		if (isWebClient) {
			childRelativeDirectoryList.add("client_build/web_build/corelib/ex");
			childRelativeDirectoryList.add("client_build/web_build/corelib/in");			
			childRelativeDirectoryList.add("client_build/web_build/lib/ex");
			childRelativeDirectoryList.add("client_build/web_build/lib/in");
			childRelativeDirectoryList.add(new StringBuilder("client_build/web_build/")
					.append(MESSAGE_SOURCE_FILE_RELATIVE_PATH).toString());
			childRelativeDirectoryList.add("client_build/web_build/src/main/java//kr/pe/sinnori/servlet");
			childRelativeDirectoryList.add("client_build/web_build/src/main/java/kr/pe/sinnori/weblib/common");
			childRelativeDirectoryList.add("client_build/web_build/src/main/java/kr/pe/sinnori/weblib/htmlstring");
			childRelativeDirectoryList.add("client_build/web_build/src/main/java/kr/pe/sinnori/weblib/jdf");
			childRelativeDirectoryList.add("client_build/web_build/src/test/java");
			childRelativeDirectoryList.add("client_build/web_build/build");
			childRelativeDirectoryList.add("client_build/web_build/dist");
			
			
			childRelativeDirectoryList.add("web_app_base/upload");
			childRelativeDirectoryList.add("web_app_base/ROOT/WEB-INF/classes");
			childRelativeDirectoryList.add("web_app_base/ROOT/WEB-INF/lib");
		}
		
		CommonStaticUtil.createChildDirectoriesOfBasePath(projectPathString, childRelativeDirectoryList);
		
		log.info("main project[{}]'s child direcotry creation task end", mainProjectName);
	}
	
	public void createFiles() throws BuildSystemException {
		log.info("main project[{}]'s file creation task start", mainProjectName);
		saveAntPropertiesFile();
		saveSinnoriConfigFile();
		saveLogbackConfigFile();
		saveEchoMessageInfomationFile();
		
		
		if (isServer) {
			createSeverBuildSystemFiles();
		}
		
		if (isAppClient) {
			createAppClientBuildSystemFiles();
		}
		
		if (isWebClient) {
			createWebClientBuildSystemFiles();
			createWebRootSampleFiles();
		}
		log.info("main project[{}]'s file creation task end", mainProjectName);
	}
	
	public void createWebClientBuildSystemFiles() throws BuildSystemException {
		log.info("web client build system files creation task start");
		
		saveWebClientAntBuildFile();
		createWebClientEchoIOFileSet();
		copyWebClientSampleSourceFiles();
		
		log.info("web client build system files creation task end");
	}
	
	// FIXME!
	public void createWebRootSampleFiles() 	 throws BuildSystemException {
		log.info("web root sample files creation task start");
		
		
		log.info("web root sample files creation task end");
	}
	
	public void saveWebClientAntBuildFile() throws BuildSystemException {
		log.info("main project[{}]'s web client ant build file creation task start", mainProjectName);
		
		String webClientAntBuildFileContents = BuildSystemFileContents.getWebClientAntBuildFileContents(mainProjectName);
		
		String webClientAntBuildFilePahtString = BuildSystemPathSupporter
				.getWebClientAntBuildFilePathString(mainProjectName,
						sinnoriInstalledPathString);		

		File webClientAntBuildFile = new File(webClientAntBuildFilePahtString);	
				
		try {
			CommonStaticUtil.overwriteFile(webClientAntBuildFile, 
					webClientAntBuildFileContents, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s server ant build file[")
					.append(webClientAntBuildFilePahtString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
	
		log.info("main project[{}]'s web client ant build file creation task end", mainProjectName);
	}
	
	public void createWebClientEchoIOFileSet() throws BuildSystemException {
		log.info("main project[{}]'s web client message[{}] io file set creation task start", mainProjectName, ECHO_MESSAGE_ID);
		
		
		
		// FIXME!
		String messageIOSetBasedirectoryPathString = null;
		
		{
			final String webClientBuildPathString = BuildSystemPathSupporter
					.getWebClientBuildPathString(mainProjectName,
							sinnoriInstalledPathString);
			
			messageIOSetBasedirectoryPathString = getMessageIOSetBasedirectoryPathString(webClientBuildPathString);
			createMessageIOSetDirectory(messageIOSetBasedirectoryPathString, ECHO_MESSAGE_ID);
		}
		
		createMessageSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createDecoderSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createEncoderSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createServerCodecSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createClientCodecSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		
		log.info("main project[{}]'s web client message[{}] io file set creation task end", mainProjectName, ECHO_MESSAGE_ID);
	}
	
	
	public void copyWebClientSampleSourceFiles() throws BuildSystemException {	
		log.info("web client sample source files copy task start");
		
		
		String sinnoriResourcePathString = BuildSystemPathSupporter.getSinnoriResourcesPathString(sinnoriInstalledPathString);
		String webClientBuildPathString = BuildSystemPathSupporter.getWebClientBuildPathString(mainProjectName, sinnoriInstalledPathString);
		
		String sourceDirectoryPathString = new StringBuilder(sinnoriResourcePathString)
				.append(File.separator).append("newproject")
				.append(File.separator).append("web_build")
				.append(File.separator).append("src").toString();
		
		String targetDirectoryPathString = new StringBuilder(webClientBuildPathString)				
				.append(File.separator).append("src").toString();
		
		File sourceDirectory = new File(sourceDirectoryPathString);
		File targetDirectory = new File(targetDirectoryPathString);
		
		try {
			FileUtils.copyDirectory(sourceDirectory, targetDirectory);			
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to copy the main project[").append(mainProjectName)
					.append("]'s the source directory[")
					.append(sourceDirectoryPathString).append("]  having sample source files to the target directory[")
					.append(targetDirectoryPathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("web client sample source files copy task end");
	}
	
	public void createAppClientBuildSystemFiles() throws BuildSystemException {
		log.info("application client build system files creation task start");
		
		saveAppClientAntBuildFile();
		createAppClientDosShellFile();
		createAppClientUnixShellFile();
		copyAppClientSampleSourceFiles();
		createAppClientEchoIOFileSet();
		
		log.info("application client build system files creation task end");
	}
	
	public void saveAppClientAntBuildFile() throws BuildSystemException {
		log.info("main project[{}]'s application client ant build file creation task start", mainProjectName);
		
		String appClientAntBuildFileContents = BuildSystemFileContents.getAppClientAntBuildFileContents(mainProjectName,
				CommonStaticFinalVars.APPCLIENT_MAIN_CLASS_FULL_NAME_VALUE,
				CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE);
		
		String appClientAntBuildFilePahtString = BuildSystemPathSupporter
				.getAppClientAntBuildFilePathString(mainProjectName,
						sinnoriInstalledPathString);		

		File appClientAntBuildFile = new File(appClientAntBuildFilePahtString);	
				
		try {
			CommonStaticUtil.overwriteFile(appClientAntBuildFile, 
					appClientAntBuildFileContents, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s server ant build file[")
					.append(appClientAntBuildFilePahtString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
	
		log.info("main project[{}]'s application client ant build file creation task end", mainProjectName);
	}
		
	public void createAppClientDosShellFile() throws BuildSystemException {
		log.info("main project[{}]'s application client dos shell file creation task start", mainProjectName);
		
		String relativeExecutabeJarFileName = new StringBuilder("dist")
				.append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE)
				.toString();
		
		
		String appClientBuildPathString = BuildSystemPathSupporter
				.getAppClientBuildPathString(mainProjectName,
						sinnoriInstalledPathString);
		
		String appClientDosShellFileContents = BuildSystemFileContents.getDosShellContents(mainProjectName,
				sinnoriInstalledPathString,
				jvmOptionsOfAppClient, "client", appClientBuildPathString,
				relativeExecutabeJarFileName);
		

		/** AppClient.bat */
		String appClientDosShellFilePathString = new StringBuilder(appClientBuildPathString)
				.append(File.separator).append(mainProjectName)
				.append("AppClient.bat").toString();
		File appClientDosShellFile = new File(appClientDosShellFilePathString);

		try {
			CommonStaticUtil.overwriteFile(appClientDosShellFile, 
					appClientDosShellFileContents, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s application client dos shell file[")
					.append(appClientDosShellFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s application client dos shell file creation task end", mainProjectName);
	}
	
	public void createAppClientUnixShellFile() throws BuildSystemException {
		log.info("main project[{}]'s application client unix shell file creation task start", mainProjectName);
				
		String relativeExecutabeJarFileName = new StringBuilder("dist")
				.append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE)
				.toString();
		
		String appClientBuildPathString = BuildSystemPathSupporter
				.getAppClientBuildPathString(mainProjectName,
						sinnoriInstalledPathString);
		
		String appClientUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(mainProjectName,
				sinnoriInstalledPathString,

				jvmOptionsOfAppClient, "client", appClientBuildPathString,
				relativeExecutabeJarFileName);

		/** AppClient.bat */
		String appClientUnixShellFilePathString = new StringBuilder(appClientBuildPathString)
				.append(File.separator).append(mainProjectName)
				.append("AppClient.sh").toString();
		File appClientUnixShellFile = new File(appClientUnixShellFilePathString);

		try {
			CommonStaticUtil.overwriteFile(appClientUnixShellFile, 
					appClientUnixShellFileContents, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s application client unix shell file[")
					.append(appClientUnixShellFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s application client unix shell file creation task end", mainProjectName);
	}
		
	public void copyAppClientSampleSourceFiles() throws BuildSystemException{
		log.info("application client sample source files copy task start");
		
		
		String sinnoriResourcePathString = BuildSystemPathSupporter.getSinnoriResourcesPathString(sinnoriInstalledPathString);
		String applicationClientBuildPathString = BuildSystemPathSupporter.getAppClientBuildPathString(mainProjectName, sinnoriInstalledPathString);
		
		String sourceDirectoryPathString = new StringBuilder(sinnoriResourcePathString)
				.append(File.separator).append("newproject")
				.append(File.separator).append("app_build")
				.append(File.separator).append("src").toString();
		
		String targetDirectoryPathString = new StringBuilder(applicationClientBuildPathString)				
				.append(File.separator).append("src").toString();
		
		File sourceDirectory = new File(sourceDirectoryPathString);
		File targetDirectory = new File(targetDirectoryPathString);
		
		try {
			FileUtils.copyDirectory(sourceDirectory, targetDirectory);			
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to copy the main project[").append(mainProjectName)
					.append("]'s the source directory[")
					.append(sourceDirectoryPathString).append("]  having sample source files to the target directory[")
					.append(targetDirectoryPathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("application client sample source files copy task end");
	}
	
	public void createAppClientEchoIOFileSet() throws BuildSystemException {
		log.info("main project[{}]'s application client message[{}] io file set creation task start", mainProjectName, ECHO_MESSAGE_ID);
				
		String messageIOSetBasedirectoryPathString = null;
		
		{
			final String appClientBuildPathString = BuildSystemPathSupporter
					.getAppClientBuildPathString(mainProjectName,
							sinnoriInstalledPathString);
			
			messageIOSetBasedirectoryPathString = getMessageIOSetBasedirectoryPathString(appClientBuildPathString);
			createMessageIOSetDirectory(messageIOSetBasedirectoryPathString, ECHO_MESSAGE_ID);
		}
		
		createMessageSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createDecoderSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createEncoderSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createServerCodecSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createClientCodecSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		
		log.info("main project[{}]'s application client message[{}] io file set creation task end", mainProjectName, ECHO_MESSAGE_ID);
	}
	
	public void createSeverBuildSystemFiles()
			throws BuildSystemException {
		log.info("server build system files creation task start");

		saveSeverAntBuildFile();
		createServerDosShellFile();
		createServerUnixShellFile();
		copyServerSampleSourceFiles();
		createServerEchoTaskFile();
		createServerEchoIOFileSet();
		
		log.info("server build system files creation task end");
	}
	
	private String getMessageIOSetBasedirectoryPathString(String buildPathString) {
		String messageIOSetBasedirectoryPathString = null;
		if (File.separator.equals("/")) {			
			messageIOSetBasedirectoryPathString = new StringBuilder(
					buildPathString).append(File.separator).append(MESSAGE_SOURCE_FILE_RELATIVE_PATH).toString();
			
		} else {			
			messageIOSetBasedirectoryPathString = new StringBuilder(
					buildPathString).append(File.separator).append(MESSAGE_SOURCE_FILE_RELATIVE_PATH.replaceAll("/", "\\\\")).toString();
			
		}
		
		return messageIOSetBasedirectoryPathString;
	}
	
	private void createMessageIOSetDirectory(String messageIOSetBasedirectoryPathString, String messageID) throws BuildSystemException {
		List<String> childRelativeDirectoryList = new ArrayList<String>();
		childRelativeDirectoryList.add(messageID);
		CommonStaticUtil.createChildDirectoriesOfBasePath(messageIOSetBasedirectoryPathString, childRelativeDirectoryList);
	}
	
	public void createServerEchoIOFileSet() throws BuildSystemException {
		log.info("main project[{}]'s server message[{}] io file set creation task start", mainProjectName, ECHO_MESSAGE_ID);		
		
		
		String messageIOSetBasedirectoryPathString = null;
		
		{
			final String serverBuildPathString = BuildSystemPathSupporter
					.getServerBuildPathString(mainProjectName,
							sinnoriInstalledPathString);
			messageIOSetBasedirectoryPathString = getMessageIOSetBasedirectoryPathString(serverBuildPathString);
			createMessageIOSetDirectory(messageIOSetBasedirectoryPathString, ECHO_MESSAGE_ID);
		}
		
		
		
		createMessageSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createDecoderSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createEncoderSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createServerCodecSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		createClientCodecSourceFile(ECHO_MESSAGE_ID, AUTHOR, messageIOSetBasedirectoryPathString);
		
		
		log.info("main project[{}]'s server message[{}] io file set creation task end", mainProjectName, ECHO_MESSAGE_ID);
	}
	
	public MessageInfo getMessageInfo(String messageID)
			throws BuildSystemException {

		String echoMessageInfoFilePathString = new StringBuilder(
				BuildSystemPathSupporter.getMessageInfoPathString(
						mainProjectName, sinnoriInstalledPathString))
				.append(File.separator).append(messageID).append(".xml")
				.toString();
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
			String errorMessage = new StringBuilder(
					"fail to parse sinnori message information xml file[")
					.append(echoMessageInfoFile.getAbsolutePath()).append("]")
					.toString();
			log.warn(errorMessage, e);
			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage())
					.toString());
		}
		return echoMessageInfo;
	}
	
	private void createMessageSourceFile(String messageID, String author, String messageIOSetBasedirectoryPathString) throws BuildSystemException {
		log.info("main project[{}]'s message file[{}][{}] creation task start", mainProjectName, messageID, messageIOSetBasedirectoryPathString);
		
		MessageInfo messageInfo = getMessageInfo(messageID);
		
		IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager
				.getInstance();
		
		String messageFileContnets = ioFileSetContentsBuilderManager
				.getMessageSourceFileContents(messageID, author,
						messageInfo);
		
		/** <build path>/src/main/java/kr/pe/sinnori/impl/message/<Message ID>/<Message ID>.java */
		String messageFilePathString = new StringBuilder(
				messageIOSetBasedirectoryPathString)					
				.append(File.separator).append(messageID)
				.append(File.separator).append(messageID)
				.append(".java")
				.toString();
		
		File messageFile = new File(messageFilePathString);
		
		try {
			CommonStaticUtil.overwriteFile(messageFile, 
					messageFileContnets, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[")
					.append(messageFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s message file[{}][{}] creation task end", mainProjectName, messageID, messageIOSetBasedirectoryPathString);
	}
	
	private void createDecoderSourceFile(String messageID, String author, String messageIOSetBasedirectoryPathString) throws BuildSystemException {
		log.info("main project[{}]'s message decoder file[{}][{}] creation task start", mainProjectName, messageID, messageIOSetBasedirectoryPathString);
		
		MessageInfo messageInfo = getMessageInfo(messageID);
		
		IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager
				.getInstance();
		
		String decoderFileContnets = ioFileSetContentsBuilderManager
				.getDecoderSourceFileContents(messageID, author,
						messageInfo);
		
		/** <build path>/src/main/java/kr/pe/sinnori/impl/message/<Message ID>/<Message ID>Decoder.java */
		String decoderFilePathString = new StringBuilder(
				messageIOSetBasedirectoryPathString)					
				.append(File.separator).append(messageID)
				.append(File.separator).append(messageID)
				.append("Decoder.java")
				.toString();
		
		File decoderFile = new File(decoderFilePathString);
		
		try {
			CommonStaticUtil.overwriteFile(decoderFile, 
					decoderFileContnets, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[")
					.append(decoderFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s message decoder file[{}][{}] creation task end", mainProjectName, messageID, messageIOSetBasedirectoryPathString);
	}
	
	private void createEncoderSourceFile(String messageID, String author, String messageIOSetBasedirectoryPathString) throws BuildSystemException {
		log.info("main project[{}]'s message encoder file[{}][{}] creation task start", mainProjectName, messageID, messageIOSetBasedirectoryPathString);
		
		MessageInfo messageInfo = getMessageInfo(messageID);
		
		IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager
				.getInstance();
		
		String encoderFileContnets = ioFileSetContentsBuilderManager
				.getEncoderSourceFileContents(messageID, author,
						messageInfo);
		
		/** <build path>/src/main/java/kr/pe/sinnori/impl/message/<Message ID>/<Message ID>Decoder.java */
		String encoderFilePathString = new StringBuilder(
				messageIOSetBasedirectoryPathString)					
				.append(File.separator).append(messageID)
				.append(File.separator).append(messageID)
				.append("Encoder.java")
				.toString();		
		
		File encoderFile = new File(encoderFilePathString);
		
		try {
			CommonStaticUtil.overwriteFile(encoderFile, 
					encoderFileContnets, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[")
					.append(encoderFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s message encoder file[{}][{}] creation task end", mainProjectName, messageID, messageIOSetBasedirectoryPathString);
	}
	
	private void createServerCodecSourceFile(String messageID, String author, String messageIOSetBasedirectoryPathString) throws BuildSystemException {
		log.info("main project[{}]'s message server codec file[{}][{}] creation task start", mainProjectName, messageID, messageIOSetBasedirectoryPathString);
		// MessageInfo messageInfo = getMessageInfo(messageID);
		
		IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager
				.getInstance();
		
		String serverCodecFileContnets = ioFileSetContentsBuilderManager
				.getServerCodecSourceFileContents(
						CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL,
						messageID, author);
		
		/** <build path>/src/main/java/kr/pe/sinnori/impl/message/<Message ID>/<Message ID>ServerCodec.java */
		String serverCodecFilePathString = new StringBuilder(
				messageIOSetBasedirectoryPathString)					
				.append(File.separator).append(messageID)
				.append(File.separator).append(messageID)
				.append("ServerCodec.java")
				.toString();		
		
		File serverCodecFile = new File(serverCodecFilePathString);
		
		try {
			CommonStaticUtil.overwriteFile(serverCodecFile, 
					serverCodecFileContnets, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[")
					.append(serverCodecFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s message server codec file[{}][{}] creation task end", mainProjectName, messageID, messageIOSetBasedirectoryPathString);
	}
		
	private void createClientCodecSourceFile(String messageID, String author, String messageIOSetBasedirectoryPathString) throws BuildSystemException {
		log.info("main project[{}]'s message client codec file[{}][{}] creation task start", mainProjectName, messageID, messageIOSetBasedirectoryPathString);
		// MessageInfo messageInfo = getMessageInfo(messageID);
		
		IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager
				.getInstance();
		
		String clientCodecFileContnets = ioFileSetContentsBuilderManager
				.getClientCodecSourceFileContents(
						CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL,
						messageID, author);
		
		/** <build path>/src/main/java/kr/pe/sinnori/impl/message/<Message ID>/<Message ID>ClientCodec.java */
		String clientCodecFilePathString = new StringBuilder(
				messageIOSetBasedirectoryPathString)					
				.append(File.separator).append(messageID)
				.append(File.separator).append(messageID)
				.append("ClientCodec.java")
				.toString();
		
		File clientCodecFile = new File(clientCodecFilePathString);
		
		try {
			CommonStaticUtil.overwriteFile(clientCodecFile, 
					clientCodecFileContnets, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s message source file[")
					.append(clientCodecFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s message client codec file[{}][{}] creation task end", mainProjectName, messageID, messageIOSetBasedirectoryPathString);
	}
	
	public void createServerEchoTaskFile() throws BuildSystemException {		
		log.info("main project[{}]'s server echo task file creation task start", mainProjectName);
		
		// final String messageID = "Echo";
		String echoServerTaskContents = BuildSystemFileContents.getEchoServerTaskContents();
		
		String serverBuildPathString = BuildSystemPathSupporter
				.getServerBuildPathString(mainProjectName,
						sinnoriInstalledPathString);
		
		/** server_build/src/main/java/kr/pe/sinnori/impl/servertask */
		String serverEchoTaskFilePathString = new StringBuilder(
				serverBuildPathString).append(File.separator).append("src")
				.append(File.separator).append("main")
				.append(File.separator).append("java")
				.append(File.separator).append("kr")
				.append(File.separator).append("pe")
				.append(File.separator).append("sinnori")
				.append(File.separator).append("impl")
				.append(File.separator).append("servertask")
				.append(File.separator).append(ECHO_MESSAGE_ID).append("ServerTask.java")
				.toString();		
		
		File serverEchoTaskFile = new File(serverEchoTaskFilePathString);
		
		try {
			CommonStaticUtil.overwriteFile(serverEchoTaskFile, 
					echoServerTaskContents, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s server dos shell file[")
					.append(serverEchoTaskFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s server echo task file creation task end", mainProjectName);
	}
	
	public void copyServerSampleSourceFiles() throws BuildSystemException {
		log.info("server sample source files copy task start");		
		
		String sinnoriResourcePathString = BuildSystemPathSupporter.getSinnoriResourcesPathString(sinnoriInstalledPathString);
		String serverBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(mainProjectName, sinnoriInstalledPathString);
		
		String sourceDirectoryPathString = new StringBuilder(sinnoriResourcePathString)
				.append(File.separator).append("newproject")
				.append(File.separator).append("server_build")
				.append(File.separator).append("src").toString();
		
		String targetDirectoryPathString = new StringBuilder(serverBuildPathString)				
				.append(File.separator).append("src").toString();
		
		File sourceDirectory = new File(sourceDirectoryPathString);
		File targetDirectory = new File(targetDirectoryPathString);
		
		try {
			FileUtils.copyDirectory(sourceDirectory, targetDirectory);			
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to copy the main project[").append(mainProjectName)
					.append("]'s the source directory[")
					.append(sourceDirectoryPathString).append("]  having sample source files to the target directory[")
					.append(targetDirectoryPathString)
					.append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("server sample source files copy task end");
	}
	
	
	
	public void createServerDosShellFile() throws BuildSystemException {
		log.info("main project[{}]'s server dos shell file creation task start", mainProjectName);
		
		String relativeExecutabeJarFileName = new StringBuilder("dist")
				.append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE)
				.toString();
		
		String serverBuildPathString = BuildSystemPathSupporter
				.getServerBuildPathString(mainProjectName,
						sinnoriInstalledPathString);
		
		String serverDosShellFileContents = BuildSystemFileContents.getDosShellContents(mainProjectName,
				sinnoriInstalledPathString,

				jvmOptionsOfServer, "server", serverBuildPathString,
				relativeExecutabeJarFileName);

		/** Server.bat */
		String serverDosShellFilePathString = new StringBuilder(serverBuildPathString)
				.append(File.separator).append(mainProjectName)
				.append("Server.bat").toString();
		File serverDosShellFile = new File(serverDosShellFilePathString);

		try {
			CommonStaticUtil.overwriteFile(serverDosShellFile, 
					serverDosShellFileContents, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s server dos shell file[")
					.append(serverDosShellFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		
		
		log.info("main project[{}]'s server dos shell file creation task end", mainProjectName);
	}
	
	public void createServerUnixShellFile() throws BuildSystemException {
		log.info("main project[{}]'s server unix shell file creation task start", mainProjectName);
		
		
		String relativeExecutabeJarFileName = new StringBuilder("dist")
				.append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE)
				.toString();
		
		String serverBuildPathString = BuildSystemPathSupporter
				.getServerBuildPathString(mainProjectName,
						sinnoriInstalledPathString);
		
		String serverUnixShellFileContents = BuildSystemFileContents.getUnixShellContents(mainProjectName,
				sinnoriInstalledPathString,

				jvmOptionsOfServer, "server", serverBuildPathString,
				relativeExecutabeJarFileName);		
		
		String serverUnixShellFilePathString = new StringBuilder(
				serverBuildPathString).append(File.separator)
				.append(mainProjectName).append("Server.sh").toString();
		
		File serverUnixShellFile = new File(serverUnixShellFilePathString);
		
		
		try {
			CommonStaticUtil.overwriteFile(serverUnixShellFile, 
					serverUnixShellFileContents, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s server unix shell file[")
					.append(serverUnixShellFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s server unix shell file creation task end", mainProjectName);
	}
	
	public void saveSeverAntBuildFile() throws BuildSystemException {
		log.info("main project[{}]'s server ant build file creation task start", mainProjectName);
		
		String sererAntBuildFileContents = BuildSystemFileContents.getServerAntBuildFileContent(mainProjectName,
				CommonStaticFinalVars.SERVER_MAIN_CLASS_FULL_NAME_VALUE,
				CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE);
		
		String serverAntBuildFilePahtString = BuildSystemPathSupporter
				.getServerAntBuildFilePathString(mainProjectName,
						sinnoriInstalledPathString);		

		File serverAntBuildFile = new File(serverAntBuildFilePahtString);	
				
		try {
			CommonStaticUtil.overwriteFile(serverAntBuildFile, 
					sererAntBuildFileContents, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s server ant build file[")
					.append(serverAntBuildFilePahtString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
	
		log.info("main project[{}]'s server ant build file creation task end", mainProjectName);
	}
	
	public void saveEchoMessageInfomationFile() throws BuildSystemException {
		log.info("main project[{}]'s echo message information file creation task start", mainProjectName);
		
		String echoMessageInformationFileContents = BuildSystemFileContents.getEchoMessageInfoContents();
		
		String echoMessageXMLFilePathString = new StringBuilder(
				BuildSystemPathSupporter.getMessageInfoPathString(
						mainProjectName, sinnoriInstalledPathString))
				.append(File.separator).append("Echo.xml").toString();

		File logbackConfigFile = new File(echoMessageXMLFilePathString);
				
		try {
			CommonStaticUtil.overwriteFile(logbackConfigFile, 
					echoMessageInformationFileContents, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s echo message information file[")
					.append(echoMessageXMLFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
	
		log.info("main project[{}]'s echo message information file creation task end", mainProjectName);
	}
	
	public void saveLogbackConfigFile() throws BuildSystemException {
		log.info("main project[{}]'s logback config file creation task start", mainProjectName);
		
		String logbackConfigFileContents = BuildSystemFileContents.getContentsOfLogback();
		
		String logbackConfigFilePathString = BuildSystemPathSupporter.getLogbackConfigFilePathString(
				mainProjectName, sinnoriInstalledPathString);
		
		File logbackConfigFile = new File(logbackConfigFilePathString);		
		
		try {
			CommonStaticUtil.overwriteFile(logbackConfigFile, 
					logbackConfigFileContents, 
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s logback config file[")
					.append(logbackConfigFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s logback config file creation task end", mainProjectName);
	}
	
	public void saveSinnoriConfigFile() throws BuildSystemException {
		log.info("main project[{}]'s config file creation task start", mainProjectName);
		
		
		SinnoriItemIDInfoManger mainProjectItemIDInfo = SinnoriItemIDInfoManger
				.getInstance();

		SequencedProperties newSinnoriConfigSequencedProperties = mainProjectItemIDInfo
				.getNewSinnoriConfigSequencedProperties(mainProjectName,
						sinnoriInstalledPathString);

		String sinnoriConfigFilePathString = BuildSystemPathSupporter
				.getSinnoriConfigFilePathString(mainProjectName,
						sinnoriInstalledPathString);
		try {
			SequencedPropertiesUtil.saveSequencedPropertiesToFile(
					newSinnoriConfigSequencedProperties,
					getSinnoriConfigPropertiesTitle(mainProjectName),
					sinnoriConfigFilePathString,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[")
					.append(mainProjectName)
					.append("]'s sinnori configuration file").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[")
					.append(mainProjectName)
					.append("]'s sinnori configuration file[")
					.append(sinnoriConfigFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s config file creation task end", mainProjectName);
	}
	
	public void saveAntPropertiesFile() throws BuildSystemException {
		log.info("main project[{}]'s ant properties file creation task start", mainProjectName);
		
		SequencedProperties antBuiltInProperties = new SequencedProperties();
		
		antBuiltInProperties.setProperty(
				CommonStaticFinalVars.IS_TOMCAT_KEY,
				String.valueOf(isWebClient));
		
		antBuiltInProperties.setProperty(
				CommonStaticFinalVars.SERVLET_SYSTEM_LIBIARY_PATH_KEY,
				servletSystemLibraryPathString);

		String antPropertiesFilePathString = BuildSystemPathSupporter
				.getAntBuiltInPropertiesFilePath(mainProjectName,
						sinnoriInstalledPathString);
		try {
			SequencedPropertiesUtil.saveSequencedPropertiesToFile(
					antBuiltInProperties,
					getAntPropertiesTitle(mainProjectName),
					antPropertiesFilePathString,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s ant built-in properties file").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s ant built-in properties file[")
					.append(antPropertiesFilePathString).append("]").toString();

			log.warn(errorMessage, e);

			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		log.info("main project[{}]'s ant properties file creation task end", mainProjectName);
	}
	
	public String getAntPropertiesTitle(String mainProjectName) {
		return new StringBuilder("project[").append(mainProjectName)
				.append("]'s ant properteis file").toString();
	}

	public String getSinnoriConfigPropertiesTitle(String mainProjectName) {
		return new StringBuilder("project[").append(mainProjectName)
				.append("]'s sinnori config file").toString();
	}
}
