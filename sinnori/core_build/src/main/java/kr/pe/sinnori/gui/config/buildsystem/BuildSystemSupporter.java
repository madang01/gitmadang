package kr.pe.sinnori.gui.config.buildsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.pe.sinnori.common.config.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.common.util.SequencedPropertiesUtil;
import kr.pe.sinnori.gui.message.builder.MessageProcessFileContentsManager;
import kr.pe.sinnori.gui.message.builder.info.MessageInfo;
import kr.pe.sinnori.gui.message.builder.info.MessageInfoSAXParser;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public abstract class BuildSystemSupporter {
	private static Logger log = LoggerFactory
			.getLogger(BuildSystemSupporter.class);

	public static List<String> getSubProjectNameListFromSinnoriConfigSequencedProperties(
			String mainProjectName, String sinnoriInstalledPathString,
			SequencedProperties sinnoriConfigSequencedProperties)
			throws SinnoriConfigurationException {

		
		final String sinnoriConfigFilePathString = BuildSystemPathSupporter.getSinnoriConfigFilePathString(
				mainProjectName, sinnoriInstalledPathString);
		String subProjectNameListValue = sinnoriConfigSequencedProperties
				.getProperty(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING);

		List<String> subProjectNameList = new ArrayList<>();
		if (null == subProjectNameListValue) {
			/** 프로젝트 목록을 지정하는 키가 없을 경우 */
			String errorMessage = new StringBuilder("project config file[")
					.append(sinnoriConfigFilePathString)
					.append("] has no a sub project list key").toString();
			throw new SinnoriConfigurationException(errorMessage);
		}
		
		subProjectNameListValue = subProjectNameListValue.trim();

		String[] subPprojectNameArrray = subProjectNameListValue.split(",");
		if (0 == subPprojectNameArrray.length) {
			return subProjectNameList;
		}

		HashSet<String> subProjectNameHashSet = new HashSet<>();
		

		for (String subProjectName : subPprojectNameArrray) {
			subProjectName = subProjectName.trim();

			if (subProjectName.equals(""))
				continue;

			/**
			 * Waraning! 러시아 페이트공 문제를 피하기 위해서 중복 검사를 위해서 List 가 아닌 HashSet 이용. 
			 */
			if (subProjectNameHashSet.contains(subProjectName)) {
				String errorMessage = new StringBuilder("the project config file[")
						.append(sinnoriConfigFilePathString)
						.append("]'s sub project name list has a duplicate project name[")
						.append(subProjectName)
						.append("]")
						.toString();

				// log.warn(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			subProjectNameList.add(subProjectName);
			subProjectNameHashSet.add(subProjectName);
		}

		

		return subProjectNameList;
	}

	public static List<String> getDBCPNameListFromSinnoriConfigSequencedProperties(
			String projectName, String sinnoriInstalledPathString,
			SequencedProperties sinnoriConfigSequencedProperties)
			throws SinnoriConfigurationException {
		List<String> dbcpNameList = new ArrayList<>();
		final String sinnoriConfigFilePathString = BuildSystemPathSupporter.getSinnoriConfigFilePathString(
				projectName, sinnoriInstalledPathString);

		String dbcpNameListValue = sinnoriConfigSequencedProperties
				.getProperty(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING);

		if (null == dbcpNameListValue) {
			/** DBCP 연결 폴 이름 목록을 지정하는 키가 없을 경우 */
			String errorMessage = new StringBuilder("project config file[")
					.append(sinnoriConfigFilePathString)
					.append("] has no a dbcp connection pool name list")
					.toString();
			throw new SinnoriConfigurationException(errorMessage);
		}

		String[] dbcpConnectionPoolNameArrray = dbcpNameListValue
				.split(",");

		dbcpNameList.clear();

		Set<String> tempNameSet = new HashSet<String>();

		for (String dbcpConnectionPoolNameOfList : dbcpConnectionPoolNameArrray) {
			dbcpConnectionPoolNameOfList = dbcpConnectionPoolNameOfList.trim();

			if (dbcpConnectionPoolNameOfList.equals(""))
				continue;

			tempNameSet.add(dbcpConnectionPoolNameOfList);
			dbcpNameList.add(dbcpConnectionPoolNameOfList);
		}

		if (tempNameSet.size() != dbcpNameList.size()) {
			/** DBCP 연결 폴 이름 목록의 이름들중 중복된 것이 있는 경우 */
			String errorMessage = new StringBuilder("project config file[")
					.append(sinnoriConfigFilePathString)
					.append("]::dbcp connection pool name list has one more same thing")
					.toString();

			// log.warn(errorMessage);

			throw new SinnoriConfigurationException(errorMessage);
		}
		return dbcpNameList;
	}

	public static void checkAntBuiltInPropertiesFile(String mainProjectName, String sinnoriInstalledPathString) throws BuildSystemException {
		
		String antPropertiesFilePathString = BuildSystemPathSupporter
				.getAntBuiltInPropertiesFilePath(mainProjectName,
						sinnoriInstalledPathString);

		SequencedProperties antBuiltInProperties = null;
		
		try {
			antBuiltInProperties = SequencedPropertiesUtil.getSequencedPropertiesFromFile(antPropertiesFilePathString);
		} catch(IOException e) {
			String errorMessage = new StringBuilder("fail to read ant built-in properties file[")
			.append(antPropertiesFilePathString).append("]").toString();
			log.warn(errorMessage, e);
			throw new BuildSystemException(new StringBuilder(errorMessage)
			.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		String servletSystemLibiaryPathString = antBuiltInProperties.getProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBIARY_PATH_KEY);
		if (null == servletSystemLibiaryPathString) {
			String errorMessage = new StringBuilder("ant built-in properties file[")
			.append(antPropertiesFilePathString).append("]'s key[")
			.append(CommonStaticFinalVars.SERVLET_SYSTEM_LIBIARY_PATH_KEY)
			.append("] is not found").toString();
			throw new BuildSystemException(errorMessage);
		}
	}

	public static void checkServerBuildSystemConfigFile(String mainProjectName,
			String sinnoriInstalledPathString) throws BuildSystemException {
		
		String serverBuildSystemConfigFilePathString = BuildSystemPathSupporter.getServerBuildSystemConfigFilePathString(
				mainProjectName, sinnoriInstalledPathString);

		File serverBuildFile = new File(serverBuildSystemConfigFilePathString);
		if (!serverBuildFile.exists()) {
			String errorMessage = String.format(
					"server build.xml[%s] is not found",
					serverBuildSystemConfigFilePathString);
			// log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

		if (!serverBuildFile.isFile()) {
			String errorMessage = String.format(
					"server build.xml[%s]  is not a normal file",
					serverBuildSystemConfigFilePathString);
			// log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

		if (!serverBuildFile.canRead()) {
			String errorMessage = String.format(
					"server build.xml[%s]  doesn't hava permission to read",
					serverBuildSystemConfigFilePathString);
			// log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

		if (!serverBuildFile.canWrite()) {
			String errorMessage = String.format(
					"server build.xml[%s]  doesn't hava permission to write",
					serverBuildSystemConfigFilePathString);
			// log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

	}
	/*
	public static void checkBaseClientAntEnvironment(String mainProjectName,
			String sinnoriInstalledPathString) throws SinnoriConfigurationException {
		String clientBuildBasePathString = BuildSystemPathSupporter.getClientBuildBasePathString(
				mainProjectName, sinnoriInstalledPathString);

		File clientBuildBasePath = new File(clientBuildBasePathString);
		if (!clientBuildBasePath.exists()) {
			String errorMessage = String.format(
					"client build base path[%s] is not found",
					clientBuildBasePathString);
			// log.warn(errorMessage);
			throw new SinnoriConfigurationException(errorMessage);
		}

		if (!clientBuildBasePath.isDirectory()) {
			String errorMessage = String.format(
					"client build base path[%s] is not directory",
					clientBuildBasePathString);
			// log.warn(errorMessage);
			throw new SinnoriConfigurationException(errorMessage);
		}

		if (!clientBuildBasePath.canRead()) {
			String errorMessage = String.format(
					"client build base path[%s] doesn't hava permission to read",
					clientBuildBasePathString);
			// log.warn(errorMessage);
			throw new SinnoriConfigurationException(errorMessage);
		}

		if (!clientBuildBasePath.canWrite()) {
			String errorMessage = String.format(
					"client build base path[%s] doesn't hava permission to write",
					clientBuildBasePathString);
			// log.warn(errorMessage);
			throw new SinnoriConfigurationException(errorMessage);
		}
	}
	*/

	/**
	 * 클라이언트용 응용 프로그램을 만들기 위한 읽기/쓰기 가능한 build.xml 존재여부를 반환한다.
	 * 
	 * @param mainProjectName
	 * @param sinnoriInstalledPathString
	 * @return
	 * @throws BuildSystemException
	 */
	public static boolean getIsAppClientAfterCheckingAppClientBuildSystemConfigFile(String mainProjectName,
			String sinnoriInstalledPathString) throws BuildSystemException {

		boolean isAppClient;

		String appClientBuildSystemConfigFilePathString = BuildSystemPathSupporter.getAppClientBuildSystemConfigFilePathString(
				mainProjectName, sinnoriInstalledPathString);
		File appClientBuildFile = new File(appClientBuildSystemConfigFilePathString);
		if (appClientBuildFile.exists()) {
			if (!appClientBuildFile.isFile()) {
				String errorMessage = String.format(
						"app client build.xml[%s]  is not a normal file",
						appClientBuildSystemConfigFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}
			
			if (!appClientBuildFile.canRead()) {
				String errorMessage = String.format(
						"app client build.xml[%s]  doesn't hava permission to read",
						appClientBuildSystemConfigFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			if (!appClientBuildFile.canWrite()) {
				String errorMessage = String.format(
						"app client build.xml[%s]  doesn't hava permission to write",
						appClientBuildSystemConfigFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			isAppClient = true;
		} else {
			isAppClient = false;
		}

		return isAppClient;
	}

	public static boolean getIsWebClientAfterCheckingWebClientBuildSystemConfigFile(String mainProjectName,
			String sinnoriInstalledPathString) throws BuildSystemException {

		boolean isWebClient;

		String webClientBuildXMLFilePathString = BuildSystemPathSupporter.getWebClientBuildSystemConfigFilePathString(
				mainProjectName, sinnoriInstalledPathString);
		File webClientBuildFile = new File(webClientBuildXMLFilePathString);
		if (webClientBuildFile.exists()) {
			if (!webClientBuildFile.isFile()) {
				String errorMessage = String.format(
						"web client build.xml[%s]  is not a normal file",
						webClientBuildXMLFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}
			
			if (!webClientBuildFile.canRead()) {
				String errorMessage = String.format(
						"web client build.xml[%s]  doesn't hava permission to read",
						webClientBuildXMLFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			if (!webClientBuildFile.canWrite()) {
				String errorMessage = String.format(
						"web client build.xml[%s]  doesn't hava permission to write",
						webClientBuildXMLFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			isWebClient = true;
		} else {
			isWebClient = false;
		}

		return isWebClient;
	}

	
	/**
	 * 웹 루트 존재 여부는 읽기/쓰기 가능한 web.xml 파일 여부로 판단
	 * 
	 * @param mainProjectName
	 * @param sinnoriInstalledPathString
	 * @return
	 * @throws BuildSystemException
	 */
	public static boolean getIsWebRootAfterCheckingWebClientBuildSystemConfigFile(String mainProjectName,
			String sinnoriInstalledPathString) throws BuildSystemException {

		boolean isWebRoot;

		String webXmlFilePathString = BuildSystemPathSupporter.getWebXmlFilePathString(
				mainProjectName, sinnoriInstalledPathString);
		
		File webXmlFile = new File(webXmlFilePathString);
		if (webXmlFile.exists()) {
			if (!webXmlFile.isFile()) {
				String errorMessage = String.format(
						"web.xml[%s] is not a normal file",
						webXmlFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}
			
			if (!webXmlFile.canRead()) {
				String errorMessage = String.format(
						"web.xml[%s]  doesn't hava permission to read",
						webXmlFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			if (!webXmlFile.canWrite()) {
				String errorMessage = String.format(
						"web.xml[%s]  doesn't hava permission to write",
						webXmlFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			isWebRoot = true;
		} else {
			isWebRoot = false;
		}

		return isWebRoot;
	}
	
	public static void saveAntBuiltInProperties(String mainProjectName, 
			String sinnoriInstalledPathString, String servletSystemLibraryPathString) throws BuildSystemException {
		SequencedProperties antBuiltInProperties = new SequencedProperties();
		antBuiltInProperties.setProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBIARY_PATH_KEY, 
				servletSystemLibraryPathString);
		
		String antPropertiesFilePathString = BuildSystemPathSupporter.getAntBuiltInPropertiesFilePath(
				mainProjectName, sinnoriInstalledPathString);
		try {
			SequencedPropertiesUtil.saveSequencedPropertiesToFile(
					antBuiltInProperties, getAntPropertiesTitle(mainProjectName),
					antPropertiesFilePathString,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder("fail to save the main project[")
			.append(mainProjectName)
			.append("]'s ant built-in properties file").toString();
			
			log.warn(errorMessage, e);
			
			throw new BuildSystemException(new StringBuilder(errorMessage)
			.append(", errormessage=").append(e.toString()).toString());	
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[")
			.append(mainProjectName)
			.append("]'s ant built-in properties file[")
			.append(antPropertiesFilePathString)
			.append("]").toString();
			
			log.warn(errorMessage, e);
			
			throw new BuildSystemException(new StringBuilder(errorMessage)
			.append(", errormessage=").append(e.toString()).toString());
		}
	}
	public static void createNewMainProjectBuildSystem(
			String newMainProjectName, String sinnoriInstalledPathString,
			boolean isServer,
			boolean isAppClient, 
			boolean isWebClient, String servletSystemLibraryPathString, MessageInfoSAXParser messageInfoSAXParser)
			throws IllegalArgumentException, BuildSystemException {
		
		String childDirectories[] = { "config", "impl/message/info",
				"rsa_keypair", "log/apache", "log/client", "log/server",
				"log/servlet" };

		String projectPathString = BuildSystemPathSupporter.getProjectPathString(newMainProjectName, sinnoriInstalledPathString);
		
		createChildDirectoriesOfBasePath(projectPathString,
				childDirectories);

		/** <project home>/ant.properties */
		saveAntBuiltInProperties(newMainProjectName, sinnoriInstalledPathString, servletSystemLibraryPathString);
		
		/** <project home>/config/sinnori.properties */
		SinnoriItemIDInfoManger mainProjectItemIDInfo = SinnoriItemIDInfoManger.getInstance();
		
		SequencedProperties newSinnoriConfigSequencedProperties = mainProjectItemIDInfo
				.getNewSinnoriConfigSequencedProperties(newMainProjectName, sinnoriInstalledPathString);

		String sinnoriConfigFilePathString = BuildSystemPathSupporter.getSinnoriConfigFilePathString(
				newMainProjectName, sinnoriInstalledPathString);

		try {
			SequencedPropertiesUtil.saveSequencedPropertiesToFile(
					newSinnoriConfigSequencedProperties,
					getSinnoriConfigPropertiesTitle(newMainProjectName),
					sinnoriConfigFilePathString,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder("fail to save the main project[")
			.append(newMainProjectName)
			.append("]'s sinnori configuration file").toString();
			
			log.warn(errorMessage, e);
			
			throw new BuildSystemException(new StringBuilder(errorMessage)
			.append(", errormessage=").append(e.toString()).toString());
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[")
			.append(newMainProjectName)
			.append("]'s sinnori configuration file[")
			.append(sinnoriConfigFilePathString)
			.append("]").toString();
			
			log.warn(errorMessage, e);
			
			throw new BuildSystemException(new StringBuilder(errorMessage)
			.append(", errormessage=").append(e.toString()).toString());
		}

		/** <project home>/config/logback.xml */
		createUTF8File(
				"logback config file",
				BuildSystemFileContents.getContentsOfLogback(),
				BuildSystemPathSupporter.getLogbackConfigFilePathString(newMainProjectName,
						sinnoriInstalledPathString));

		/** <project home>/impl/message/info/Echo.xml */
		String echoMessageXMLFilePathString = new StringBuilder(
				BuildSystemPathSupporter.getMessageInfoPathString(newMainProjectName,
						sinnoriInstalledPathString))
		.append(File.separator).append("Echo.xml").toString();

		createUTF8File("echo message",
				BuildSystemFileContents.getEchoMessageInfoContents(),
				echoMessageXMLFilePathString);

		if (isServer) {
			createSeverBuildSystem(newMainProjectName,
					sinnoriInstalledPathString, messageInfoSAXParser);
		}
		if (isAppClient) {
			createAppClientBuildSystem(newMainProjectName,
					sinnoriInstalledPathString, messageInfoSAXParser);
		}
		
		if (isWebClient) {
			createWebClientBuildSystem(newMainProjectName,
					sinnoriInstalledPathString);
			
			createWebRootEnvironment(newMainProjectName,
					sinnoriInstalledPathString);
			
		}
	}

	private static void createChildDirectoriesOfBasePath(String basePathStrig,
			String[] childDirectories) throws BuildSystemException {
		for (int i = 0; i < childDirectories.length; i++) {
			String relativeDir = childDirectories[i];

			// log.info("relativeDir[{}]=[{}]", i, relativeDir);

			String subDir = null;
			if (File.separator.equals("/")) {
				subDir = relativeDir;
			} else {
				subDir = relativeDir.replaceAll("/", "\\\\");
			}

			String childPathString = new StringBuilder(basePathStrig)
					.append(File.separator).append(subDir).toString();

			File childPath = new File(childPathString);
			if (!childPath.exists()) {
				try {
					FileUtils.forceMkdir(childPath);
				} catch (IOException e) {
					String errorMessage = String.format(
							"fail to create a new path[%s]", childPathString);
					log.info(errorMessage, e);
					throw new BuildSystemException(errorMessage);
				}

				log.info("child direcotry[{}] was created successfully",
						childPathString);
			} else {
				log.info("child direcotry[{}] exist", childPathString);
			}

			if (!childPath.isDirectory()) {
				String errorMessage = String.format(
						"path[%s] is not directory", childPathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			if (!childPath.canRead()) {
				String errorMessage = String.format("path[%s] doesn't hava permission to read",
						childPathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			if (!childPath.canWrite()) {
				String errorMessage = String.format(
						"path[%s] doesn't hava permission to write", childPathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

		}
	}

	private static void createUTF8File(String title, String contents,
			String filePathString) throws BuildSystemException {
		File serverBuildXMLFile = new File(filePathString);
		FileOutputStream fos = null;
		try {
			fos = FileUtils.openOutputStream(serverBuildXMLFile);

			fos.write(contents.getBytes("UTF-8"));

			// log.info("title={}, filePathString={} UTF8 file creation success",
			// title, filePathString);

		} catch (UnsupportedEncodingException e) {
			String errorMessage = new StringBuilder(title).append("[")
					.append(filePathString).append("] 생성중 문자셋 에러::")
					.append(e.getMessage()).toString();

			// log.warn(errorMessage);

			throw new BuildSystemException(errorMessage);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("서버 build.xml 파일[")
					.append(filePathString).append("] 생성중 IO 에러::")
					.append(e.getMessage()).toString();

			// log.warn(errorMessage);

			throw new BuildSystemException(errorMessage);
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (Exception e) {
					/*
					 * String errorMessage = new
					 * StringBuilder("서버 build.xml 파일[") .append(filePathString)
					 * .append("] 의 쓰기 쓰트림 닫기 에러::")
					 * .append(e.getMessage()).toString();
					 */
					// log.warn(errorMessage);
				}
			}
		}
		
		log.info("{} file[{}] was created successfully", title, filePathString);
	}

	private static void createSeverBuildSystem(String newMainProjectName,
			String sinnoriInstalledPathString, MessageInfoSAXParser messageInfoSAXParser) throws BuildSystemException {
		
		String serverBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(newMainProjectName,
				sinnoriInstalledPathString);
		File serverBuildPath = new File(serverBuildPathString);

		if (!serverBuildPath.exists()) {
			boolean isSuccess = serverBuildPath.mkdir();
			if (!isSuccess) {
				String errorMessage = String.format(
						"fail to make a new server build path[%s]",
						serverBuildPathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}
		}

		if (!serverBuildPath.isDirectory()) {
			String errorMessage = String.format(
					"server build path[%s] is not directory",
					serverBuildPathString);
			// log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

		if (!serverBuildPath.canRead()) {
			String errorMessage = String.format(
					"server build path[%s] doesn't hava permission to read",
					serverBuildPathString);
			// log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

		if (!serverBuildPath.canWrite()) {
			String errorMessage = String.format(
					"server build path[%s] doesn't hava permission to write",
					serverBuildPathString);
			// log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}
		
		String childDirectories[] = { "src", "src/main",
				"src/kr/pe/sinnori/common/serverlib",
				"src/kr/pe/sinnori/impl/message",
				"src/kr/pe/sinnori/impl/server/mybatis",
				"src/kr/pe/sinnori/impl/servertask", "APP-INF/lib",
				"APP-INF/resources", "APP-INF/classes" };
		createChildDirectoriesOfBasePath(serverBuildPathString,
				childDirectories);

		/** build.xml */
		String serverBuildXMLFilePathString = BuildSystemPathSupporter.getServerBuildSystemConfigFilePathString(
				newMainProjectName, sinnoriInstalledPathString);
		createUTF8File(
				"server build.xml",
				BuildSystemFileContents
						.getServerAntBuildXMLFileContent(
								newMainProjectName,
								CommonStaticFinalVars.SERVER_MAIN_CLASS_FULL_NAME_VALUE,
								CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE),
				serverBuildXMLFilePathString);

		String relativeExecutabeJarFileName = new StringBuilder("dist")
				.append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE)
				.toString();

		/** Server.bat */
		String dosShellFilePathString = new StringBuilder(serverBuildPathString)
				.append(File.separator).append(newMainProjectName)
				.append("Server.bat").toString();

		createUTF8File("dos shell of server",
				BuildSystemFileContents.getDosShellContents(
						newMainProjectName,
						sinnoriInstalledPathString,

						"-Xmx1024m -Xms1024m", "server", serverBuildPathString,
						relativeExecutabeJarFileName,

						CommonStaticFinalVars.SINNORI_LOGBACK_LOG_FILE_NAME,
						CommonStaticFinalVars.SINNORI_CONFIG_FILE_NAME),
				dosShellFilePathString);

		/** Server.sh */
		String unixShellFilePathString = new StringBuilder(
				serverBuildPathString).append(File.separator)
				.append(newMainProjectName).append("Server.sh").toString();
		createUTF8File("unix shell of server",
				BuildSystemFileContents.getUnixShellContents(
						newMainProjectName,
						sinnoriInstalledPathString,

						"-Xmx1024m -Xms1024m", "server", serverBuildPathString,
						relativeExecutabeJarFileName),
				unixShellFilePathString);

		

		/**
		 * create source file having DEFAULT_SERVER_MAIN_CLASS_NAME ex)
		 * server_build/src/main/SinnoriServerMain.java
		 */
		String serverMainSrcFilePathString = null;
		if (File.separator.equals("/")) {
			String subStr = CommonStaticFinalVars.SERVER_MAIN_CLASS_FULL_NAME_VALUE
					.replaceAll("\\.", "/");
			serverMainSrcFilePathString = new StringBuilder(
					serverBuildPathString).append(File.separator).append("src")
					.append(File.separator).append(subStr).append(".java")
					.toString();

			// log.info("subStr=[{}], serverMainSrcFilePathString=[{}]", subStr,
			// serverMainSrcFilePathString);
		} else {
			String subStr = CommonStaticFinalVars.SERVER_MAIN_CLASS_FULL_NAME_VALUE
					.replaceAll("\\.", "\\\\");

			serverMainSrcFilePathString = new StringBuilder(
					serverBuildPathString).append(File.separator).append("src")
					.append(File.separator).append(subStr).append(".java")
					.toString();

			// log.info("subStr=[{}], serverMainSrcFilePathString=[{}]", subStr,
			// serverMainSrcFilePathString);
		}

		/** server_build/src/main/SinnoriServerMain.java */
		createUTF8File(
				"main class source of server",
				BuildSystemFileContents
						.getDefaultServerMainClassContents(CommonStaticFinalVars.SERVER_MAIN_CLASS_FULL_NAME_VALUE),
				serverMainSrcFilePathString);

		String messageID = "Echo";
		String author = "Won Jonghoon";
		String echoMessageInfoFilePathString = new StringBuilder(
				BuildSystemPathSupporter.getMessageInfoPathString(newMainProjectName,
						sinnoriInstalledPathString))
		.append(File.separator)
		.append(messageID).append(".xml").toString();
		File echoMessageInfoFile = new File(echoMessageInfoFilePathString);
		
		
		MessageInfo echoMessageInfo = null;
		try {
			echoMessageInfo = messageInfoSAXParser.parse(echoMessageInfoFile, true);
		} catch (IllegalArgumentException | SAXException | IOException e) {
			String errorMessage = new StringBuilder("fail to parse sinnori message information xml file[")
			.append(echoMessageInfoFile.getAbsolutePath()).append("]").toString();
			log.warn(errorMessage, e);
			throw new BuildSystemException(new StringBuilder(errorMessage)
			.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		String serverImplBasePath = new StringBuilder(
				serverBuildPathString).append(File.separator).append("src")
				.append(File.separator).append("kr").append(File.separator)
				.append("pe").append(File.separator).append("sinnori")
				.append(File.separator).append("impl").toString();
		
		String serverMessagePathString = new StringBuilder(serverImplBasePath).append(File.separator)
				.append("message").toString();
		
		MessageProcessFileContentsManager messageProcessFileContentsManager 
			= MessageProcessFileContentsManager.getInstance();		
		
		/** server_build/src/kr/pe/sinnori/impl/message/Echo/Echo.java */		
		String echoMessageFileContnets = messageProcessFileContentsManager.getMessageSourceFileContents(messageID, author, echoMessageInfo);
		
		String echoMessageFilePathString = new StringBuilder(serverMessagePathString)		
				.append(File.separator).append(messageID).append(File.separator)
				.append(messageID)
				.append(".java").toString();
		
		createUTF8File("echo message file",
				echoMessageFileContnets,
				echoMessageFilePathString);
		
		/** server_build/src/kr/pe/sinnori/impl/message/Echo/EchoDecoder.java */
		String decoderFileContnets = messageProcessFileContentsManager.getDecoderSourceFileContents(messageID, author, echoMessageInfo);
		
		String decoderFilePathString = new StringBuilder(serverMessagePathString)		
				.append(File.separator).append(messageID).append(File.separator)
				.append(messageID)
				.append("Decoder.java").toString();
		
		createUTF8File("echo decoder file",
				decoderFileContnets,
				decoderFilePathString);
		
		/** server_build/src/kr/pe/sinnori/impl/message/Echo/EchoEncoder.java */
		String encoderFileContnets = messageProcessFileContentsManager.getEncoderSourceFileContents(messageID, author, echoMessageInfo);
		
		String encoderFilePathString = new StringBuilder(serverMessagePathString)		
				.append(File.separator).append(messageID).append(File.separator)
				.append(messageID)
				.append("Encoder.java").toString();
		
		createUTF8File("echo encoder file",
				encoderFileContnets,
				encoderFilePathString);
		
		/** server_build/src/kr/pe/sinnori/impl/message/Echo/EchoServerCodec.java */
		String serverCodecFileContnets = messageProcessFileContentsManager
				.getServerCodecSourceFileContents(CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL
						, messageID, author);
		
		String serverCodecFilePathString = new StringBuilder(serverMessagePathString)
		.append(File.separator).append(messageID).append(File.separator)
				.append(messageID)
				.append("ServerCodec.java").toString();
		
		createUTF8File("echo encoder file",
				serverCodecFileContnets,
				serverCodecFilePathString);
		
		/** server_build/src/kr/pe/sinnori/impl/message/Echo/EchoClientCodec.java */
		String clientCodecFileContnets = messageProcessFileContentsManager
				.getClientCodecSourceFileContents(CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL
						, messageID, author);
		
		String clientCodecFilePathString = new StringBuilder(serverMessagePathString)
		.append(File.separator).append(messageID).append(File.separator)				
				.append(messageID)
				.append("ClientCodec.java").toString();
		
		createUTF8File("echo encoder file",
				clientCodecFileContnets,
				clientCodecFilePathString);
		
		/** server_build/src/kr/pe/sinnori/impl/servertask/EchoServerTask.java */
		String echoServerTaskSrcFilePathString = new StringBuilder(serverImplBasePath)
			.append(File.separator)
				.append("servertask").append(File.separator)
				.append(messageID)
				.append("ServerTask.java").toString();

		createUTF8File("echo server task source file",
				BuildSystemFileContents.getEchoServerTaskContents(),
				echoServerTaskSrcFilePathString);
	}

	// FIXME!
	public static void createWebClientBuildSystem(String newMainProjectName,
			String sinnoriInstalledPathString) throws BuildSystemException {
		String webClientBuildPathString = BuildSystemPathSupporter.getWebClientBuildPathString(
				newMainProjectName, sinnoriInstalledPathString);
		File webClientBuildPath = new File(webClientBuildPathString);
		
		boolean isSuccess = webClientBuildPath.mkdir();
		if (!isSuccess) {
			String errorMessage = String.format(
					"fail to create a new web client build path[%s]",
					webClientBuildPathString);
			log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

		if (!webClientBuildPath.isDirectory()) {
			String errorMessage = String.format(
					"web client build path[%s] is not directory",
					webClientBuildPathString);
			log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

		if (!webClientBuildPath.canRead()) {
			String errorMessage = String.format(
					"web client build path[%s] doesn't hava permission to read",
					webClientBuildPathString);
			log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

		if (!webClientBuildPath.canWrite()) {
			String errorMessage = String.format(
					"web client build path[%s] doesn't hava permission to write",
					webClientBuildPathString);
			log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

		String webClientBuildSystemFilePathString = BuildSystemPathSupporter.getWebClientBuildSystemConfigFilePathString(
				newMainProjectName, sinnoriInstalledPathString);

		String webClientBuildXMLFileConents = BuildSystemFileContents
				.getWebClientAntBuildXMLFileContents(newMainProjectName);
		createUTF8File("web client build.xml",
				webClientBuildXMLFileConents,
				webClientBuildSystemFilePathString);

		String childDirectories[] = { "src" };
		createChildDirectoriesOfBasePath(webClientBuildPathString,
				childDirectories);
	}
	
	public static void createWebRootEnvironment(String newMainProjectName,
			String sinnoriInstalledPathString) throws BuildSystemException {
		// getWebAppBasePathString
		String webRootPathString = BuildSystemPathSupporter.getWebRootPathString(
				newMainProjectName, sinnoriInstalledPathString);
		File webRootPath = new File(webRootPathString);
		boolean isSuccess = webRootPath.mkdirs();
		if (!isSuccess) {
			String errorMessage = String.format(
					"fail to create a new web root path[%s]",
					webRootPathString);
			log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}
		
		// FIXME!		
		if (!webRootPath.isDirectory()) {
			String errorMessage = String.format(
					"the new web root path[%s] is not directory", webRootPathString);
			// log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

		if (!webRootPath.canRead()) {
			String errorMessage = String.format("the new web root path[%s] doesn't hava permission to read",
					webRootPathString);
			// log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

		if (!webRootPath.canWrite()) {
			String errorMessage = String.format(
					"the new web root path[%s] doesn't hava permission to write", webRootPathString);
			// log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}
		
		String childDirectories[] = { "WEB-INF" };
		createChildDirectoriesOfBasePath(webRootPathString,
				childDirectories);
	}
	

	public static void createAppClientBuildSystem(String newMainProjectName,
			String sinnoriInstalledPathString, MessageInfoSAXParser messageInfoSAXParser) throws BuildSystemException {
		
		String appClientBuildPathString = BuildSystemPathSupporter.getAppClientBuildPathString(
				newMainProjectName, sinnoriInstalledPathString);

		File appClientBuildPath = new File(appClientBuildPathString);
		if (!appClientBuildPath.exists()) {
			boolean isSuccess = appClientBuildPath.mkdirs();
			if (!isSuccess) {
				String errorMessage = String.format(
						"fail to make a new app client build path[%s]",
						appClientBuildPathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}
		}

		if (!appClientBuildPath.isDirectory()) {
			String errorMessage = String.format(
					"app client build path[%s] is not directory",
					appClientBuildPath);
			// log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

		if (!appClientBuildPath.canRead()) {
			String errorMessage = String.format(
					"app client build path[%s] doesn't hava permission to read",
					appClientBuildPathString);
			// log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}

		if (!appClientBuildPath.canWrite()) {
			String errorMessage = String.format(
					"app client build path[%s] doesn't hava permission to write",
					appClientBuildPathString);
			// log.warn(errorMessage);
			throw new BuildSystemException(errorMessage);
		}	
		
		////////////////////////////////////////////////
		String childDirectories[] = { "src", "src/main",
				"src/kr/pe/sinnori/common/clientlib",
				"src/kr/pe/sinnori/impl/message" };
		createChildDirectoriesOfBasePath(appClientBuildPathString,
				childDirectories);

		/** build.xml */
		String appClientBuildXMLFilePathString = BuildSystemPathSupporter.getAppClientBuildSystemConfigFilePathString(
				newMainProjectName, sinnoriInstalledPathString);
		createUTF8File(
				"client build.xml",
				BuildSystemFileContents
						.getServerAntBuildXMLFileContent(
								newMainProjectName,
								CommonStaticFinalVars.APPCLIENT_MAIN_CLASS_FULL_NAME_VALUE,
								CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE),
								appClientBuildXMLFilePathString);

		String relativeExecutabeJarFileName = new StringBuilder("dist")
				.append(File.separator)
				.append(CommonStaticFinalVars.APPCLIENT_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE)
				.toString();

		/** AppClient.bat */
		String dosShellFilePathString = new StringBuilder(appClientBuildPathString)
				.append(File.separator).append(newMainProjectName)
				.append("AppClient.bat").toString();

		createUTF8File("dos shell of client",
				BuildSystemFileContents.getDosShellContents(
						newMainProjectName,
						sinnoriInstalledPathString,

						"-Xmx1024m -Xms1024m", "client", appClientBuildPathString,
						relativeExecutabeJarFileName,

						CommonStaticFinalVars.SINNORI_LOGBACK_LOG_FILE_NAME,
						CommonStaticFinalVars.SINNORI_CONFIG_FILE_NAME),
				dosShellFilePathString);

		/** AppClient.sh */
		String unixShellFilePathString = new StringBuilder(
				appClientBuildPathString).append(File.separator)
				.append(newMainProjectName).append("AppClient.sh").toString();
		createUTF8File("unix shell of client",
				BuildSystemFileContents.getUnixShellContents(
						newMainProjectName,
						sinnoriInstalledPathString,

						"-Xmx1024m -Xms1024m", "client", appClientBuildPathString,
						relativeExecutabeJarFileName),
				unixShellFilePathString);

		

		/**
		 * create source file having DEFAULT_APPCLIENT_MAIN_CLASS_NAME ex)
		 * clinet_build/src/main/SinnoriAppClientMain.java
		 */
		String appClientMainSrcFilePathString = null;
		if (File.separator.equals("/")) {
			String subStr = CommonStaticFinalVars.APPCLIENT_MAIN_CLASS_FULL_NAME_VALUE
					.replaceAll("\\.", "/");
			appClientMainSrcFilePathString = new StringBuilder(
					appClientBuildPathString).append(File.separator).append("src")
					.append(File.separator).append(subStr).append(".java")
					.toString();

			// log.info("subStr=[{}], serverMainSrcFilePathString=[{}]", subStr,
			// serverMainSrcFilePathString);
		} else {
			String subStr = CommonStaticFinalVars.APPCLIENT_MAIN_CLASS_FULL_NAME_VALUE
					.replaceAll("\\.", "\\\\");

			appClientMainSrcFilePathString = new StringBuilder(
					appClientBuildPathString).append(File.separator).append("src")
					.append(File.separator).append(subStr).append(".java")
					.toString();

			// log.info("subStr=[{}], serverMainSrcFilePathString=[{}]", subStr,
			// serverMainSrcFilePathString);
		}

		/** clinet_build/src/main/SinnoriAppClientMain.java */
		createUTF8File(
				"main class source of client",
				BuildSystemFileContents
						.getDefaultServerMainClassContents(CommonStaticFinalVars.APPCLIENT_MAIN_CLASS_FULL_NAME_VALUE),
				appClientMainSrcFilePathString);
		
		/////////////////////////////////////////////////
		String messageID = "Echo";
		String author = "Won Jonghoon";
		String echoMessageInfoFilePathString = new StringBuilder(
				BuildSystemPathSupporter.getMessageInfoPathString(newMainProjectName,
						sinnoriInstalledPathString))
		.append(File.separator)
		.append(messageID).append(".xml").toString();
		File echoMessageInfoFile = new File(echoMessageInfoFilePathString);
		
		
		MessageInfo echoMessageInfo = null;
		try {
			echoMessageInfo = messageInfoSAXParser.parse(echoMessageInfoFile, true);
		} catch (IllegalArgumentException | SAXException | IOException e) {
			String errorMessage = new StringBuilder("fail to parse sinnori message information xml file[")
			.append(echoMessageInfoFile.getAbsolutePath()).append("]").toString();
			log.warn(errorMessage, e);
			throw new BuildSystemException(new StringBuilder(errorMessage)
			.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		String appClientImplBasePath = new StringBuilder(
				appClientBuildPathString).append(File.separator).append("src")
				.append(File.separator).append("kr").append(File.separator)
				.append("pe").append(File.separator).append("sinnori")
				.append(File.separator).append("impl").toString();
		
		String appClientMessagePath = new StringBuilder(appClientImplBasePath).append(File.separator)
				.append("message").toString();
			
		MessageProcessFileContentsManager messageProcessFileContentsManager 
			= MessageProcessFileContentsManager.getInstance();		
		
		/** app_build/src/kr/pe/sinnori/impl/message/Echo/Echo.java */		
		String echoMessageFileContnets = messageProcessFileContentsManager.getMessageSourceFileContents(messageID, author, echoMessageInfo);
		
		String echoMessageFilePathString = new StringBuilder(appClientMessagePath)
		.append(File.separator).append(messageID).append(File.separator)
				.append(messageID)
				.append(".java").toString();
		
		createUTF8File("echo message file",
				echoMessageFileContnets,
				echoMessageFilePathString);
		
		/** app_build/src/kr/pe/sinnori/impl/message/Echo/EchoDecoder.java */
		String decoderFileContnets = messageProcessFileContentsManager.getDecoderSourceFileContents(messageID, author, echoMessageInfo);
		
		String decoderFilePathString = new StringBuilder(appClientMessagePath)
		.append(File.separator).append(messageID).append(File.separator)
				.append(messageID)
				.append("Decoder.java").toString();
		
		createUTF8File("echo decoder file",
				decoderFileContnets,
				decoderFilePathString);
		
		/** app_build/src/kr/pe/sinnori/impl/message/Echo/EchoEncoder.java */
		String encoderFileContnets = messageProcessFileContentsManager.getEncoderSourceFileContents(messageID, author, echoMessageInfo);
		
		String encoderFilePathString = new StringBuilder(appClientMessagePath)
		.append(File.separator).append(messageID).append(File.separator)
				.append(messageID)
				.append("Encoder.java").toString();
		
		createUTF8File("echo encoder file",
				encoderFileContnets,
				encoderFilePathString);
		
		/** app_build/src/kr/pe/sinnori/impl/message/Echo/EchoServerCodec.java */
		String serverCodecFileContnets = messageProcessFileContentsManager
				.getServerCodecSourceFileContents(CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL
						, messageID, author);
		
		String serverCodecFilePathString = new StringBuilder(appClientMessagePath)
		.append(File.separator).append(messageID).append(File.separator)
				.append(messageID)
				.append("ServerCodec.java").toString();
		
		createUTF8File("echo encoder file",
				serverCodecFileContnets,
				serverCodecFilePathString);
		
		/** app_build/src/kr/pe/sinnori/impl/message/Echo/EchoClientCodec.java */
		String clientCodecFileContnets = messageProcessFileContentsManager
				.getClientCodecSourceFileContents(CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL
						, messageID, author);
		
		String clientCodecFilePathString = new StringBuilder(appClientMessagePath)
		.append(File.separator).append(messageID).append(File.separator)
				.append(messageID)
				.append("ClientCodec.java").toString();
		
		createUTF8File("echo encoder file",
				clientCodecFileContnets,
				clientCodecFilePathString);
	}

	public static void removeProjectDirectory(String projectName,
			String sinnoriInstalledPathString) throws BuildSystemException {
		String projectPathString = BuildSystemPathSupporter.getProjectPathString(projectName,
				sinnoriInstalledPathString);
		File projectPath = new File(projectPathString);
		
		if (!projectPath.exists()) {
			String errorMessage = new StringBuilder("the project path[")
			.append(projectPathString).append("] does not exist").toString();
			throw new BuildSystemException(errorMessage);
		}
		
		if (!projectPath.isDirectory()) {
			String errorMessage = new StringBuilder("the project path[")
			.append(projectPathString).append("] is not a directory").toString();
			throw new BuildSystemException(errorMessage);
		}
		
		try {
			FileUtils.forceDelete(projectPath);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to delete the project path[")
			.append(projectPathString).append("]").toString();
			/** 상세 에러 추적용 */
			log.warn(errorMessage, e);
			throw new BuildSystemException(new StringBuilder(errorMessage)
			.append(", errormessage=")
			.append(e.toString()).toString());
		}
	}
	
	public static String getAntPropertiesTitle(String mainProjectName) {
		return new StringBuilder("project[").append(mainProjectName)
				.append("]'s ant config file").toString();
	}

	public static String getSinnoriConfigPropertiesTitle(String mainProjectName) {
		return new StringBuilder("project[").append(mainProjectName)
				.append("]'s sinnori config file").toString();
	}

	
}
