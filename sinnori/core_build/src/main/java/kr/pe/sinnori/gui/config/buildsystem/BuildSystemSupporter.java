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
import kr.pe.sinnori.common.exception.ConfigErrorException;
import kr.pe.sinnori.common.message.info.MessageInfo;
import kr.pe.sinnori.common.message.info.MessageInfoSAXParser;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.common.util.SequencedPropertiesUtil;
import kr.pe.sinnori.gui.message.builder.MessageProcessFileContentsManager;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public abstract class BuildSystemSupporter {
	private static Logger log = LoggerFactory
			.getLogger(BuildSystemSupporter.class);

	public static void applyAppClientStatus(String mainProjectName,
			String sinnoriInstalledPathString, boolean isAppClient, MessageInfoSAXParser messageInfoSAXParser)
			throws ConfigErrorException {
		String appClientBuildPathString = BuildSystemPathSupporter.getAppClientBuildPathString(
				mainProjectName, sinnoriInstalledPathString);
		File appClientBuildPath = new File(appClientBuildPathString);

		if (isAppClient) {
			if (appClientBuildPath.exists()) {
				/** nothing */
				return;
			}
			createAppClientBuildSystem(mainProjectName,
					sinnoriInstalledPathString, messageInfoSAXParser);
		} else {
			if (!appClientBuildPath.exists()) {
				/** nothing */
				return;
			}
			
			try {
				FileUtils.forceDelete(appClientBuildPath);
			} catch (IOException e) {
				String errorMessage = String
						.format("fail to delete app client build path[%s], errormessage=%s",
								appClientBuildPathString, e.getMessage());
				log.warn(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			log.info("app client build path[{}] was deleted successfully",
					appClientBuildPathString);
		}
	}

	public static void applyWebClientStatus(String mainProjectName,
			String sinnoriInstalledPathString, boolean isWebClient) throws ConfigErrorException {
		String webClientBuildPathString = BuildSystemPathSupporter.getWebClientBuildPathString(
				mainProjectName, sinnoriInstalledPathString);
		File webClientBuildPath = new File(webClientBuildPathString);
		
		String webRootPathString = BuildSystemPathSupporter.getWebRootPathString(mainProjectName, sinnoriInstalledPathString);
		File webRootPath = new File(webRootPathString);

		if (isWebClient) {
			if (!webClientBuildPath.exists()) {
				createWebClientBuildSystem(mainProjectName,
						sinnoriInstalledPathString);
			}
			
			if (!webRootPath.exists()) {
				createWebRootEnvironment(mainProjectName,
						sinnoriInstalledPathString);
			}

		} else {
			if (webClientBuildPath.exists()) {
				try {
					FileUtils.forceDelete(webClientBuildPath);
				} catch (IOException e) {
					String errorMessage = String
							.format("fail to delete web client build path[%s], errormessage=%s",
									webClientBuildPathString, e.getMessage());
					log.warn(errorMessage);
					throw new ConfigErrorException(errorMessage);
				}

				log.info("web client build path[{}] was deleted successfully",
						webClientBuildPathString);
			}

			if (webRootPath.exists()) {
				try {
					FileUtils.forceDelete(webRootPath);
				} catch (IOException e) {
					String errorMessage = String
							.format("fail to delete web root path[%s], errormessage=%s",
									webRootPathString, e.getMessage());
					log.warn(errorMessage);
					throw new ConfigErrorException(errorMessage);
				}

				log.info("web root path[{}] was deleted successfully",
						webRootPathString);
			}			
		}
	}

	public static List<String> getSubProjectNameListFromSinnoriConfigSequencedProperties(
			String mainProjectName, String sinnoriInstalledPathString,
			SequencedProperties sinnoriConfigSequencedProperties)
			throws ConfigErrorException {

		
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
			throw new ConfigErrorException(errorMessage);
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
				throw new ConfigErrorException(errorMessage);
			}
			
			subProjectNameList.add(subProjectName);
			subProjectNameHashSet.add(subProjectName);
		}

		

		return subProjectNameList;
	}

	public static List<String> getDBCPNameListFromSinnoriConfigSequencedProperties(
			String projectName, String sinnoriInstalledPathString,
			SequencedProperties sinnoriConfigSequencedProperties)
			throws ConfigErrorException {
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
			throw new ConfigErrorException(errorMessage);
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

			throw new ConfigErrorException(errorMessage);
		}
		return dbcpNameList;
	}

	public static SequencedProperties getAntSequencedPropertiesAfterValidCheck(String antPropertiesFilePathString) throws ConfigErrorException {
		SequencedProperties antProperties= null;
		
		try {
			antProperties = SequencedPropertiesUtil.getSequencedPropertiesFromFile(antPropertiesFilePathString);
		} catch(IOException e) {
			String errorMessage = new StringBuilder("fail to read ant properties file[")
			.append(antPropertiesFilePathString).append("]").toString();
			log.warn(errorMessage, e);
			throw new ConfigErrorException(new StringBuilder(errorMessage)
			.append(", errormessage=").append(e.getMessage()).toString());
		}
		
		
		final String[] keyArray = {CommonStaticFinalVars.IS_WEB_CLIENT_KEY,
				CommonStaticFinalVars.SERVLET_SYSTEM_LIBIARY_PATH_KEY
		};
		
		
		for (int i=0; i < keyArray.length; i++) {
			String itemKey = keyArray[i];
			String itemValue = antProperties.getProperty(itemKey);
			
			if (null == itemValue) {
				String errorMessage = String.format(
						"ant.properties[%s]'s key[%s] is not found",
						antPropertiesFilePathString, itemKey);
				// log.warn(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			itemValue = itemValue.trim();

			if (itemValue.equals("")) {
				String errorMessage = String.format(
						"ant.properties[%s]'s key[%s] is a empty string",
						antPropertiesFilePathString, itemValue);
				// log.warn(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}
		}		
		
		return antProperties;
	}

	public static void checkSeverAntEnvironment(String mainProjectName,
			String sinnoriInstalledPathString) throws ConfigErrorException {
		
		String serverBuildFilePathString = BuildSystemPathSupporter.getServerBuildSystemConfigFilePathString(
				mainProjectName, sinnoriInstalledPathString);

		File serverBuildFile = new File(serverBuildFilePathString);
		if (!serverBuildFile.exists()) {
			String errorMessage = String.format(
					"server build.xml[%s] is not found",
					serverBuildFilePathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!serverBuildFile.isFile()) {
			String errorMessage = String.format(
					"server build.xml[%s]  is not a normal file",
					serverBuildFilePathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!serverBuildFile.canRead()) {
			String errorMessage = String.format(
					"server build.xml[%s]  doesn't hava permission to read",
					serverBuildFilePathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!serverBuildFile.canWrite()) {
			String errorMessage = String.format(
					"server build.xml[%s]  doesn't hava permission to write",
					serverBuildFilePathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

	}

	public static void checkBaseClientAntEnvironment(String mainProjectName,
			String sinnoriInstalledPathString) throws ConfigErrorException {
		String clientBuildBasePathString = BuildSystemPathSupporter.getClientBuildBasePathString(
				mainProjectName, sinnoriInstalledPathString);

		File clientBuildBasePath = new File(clientBuildBasePathString);
		if (!clientBuildBasePath.exists()) {
			String errorMessage = String.format(
					"client build base path[%s] is not found",
					clientBuildBasePathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!clientBuildBasePath.isDirectory()) {
			String errorMessage = String.format(
					"client build base path[%s] is not directory",
					clientBuildBasePathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!clientBuildBasePath.canRead()) {
			String errorMessage = String.format(
					"client build base path[%s] doesn't hava permission to read",
					clientBuildBasePathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!clientBuildBasePath.canWrite()) {
			String errorMessage = String.format(
					"client build base path[%s] doesn't hava permission to write",
					clientBuildBasePathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}
	}

	public static boolean getIsAppClient(String mainProjectName,
			String sinnoriInstalledPathString) throws ConfigErrorException {

		boolean isAppClient;

		String appClientBuildSystemConfigFilePathString = BuildSystemPathSupporter.getAppClientBuildSystemConfigFilePathString(
				mainProjectName, sinnoriInstalledPathString);
		File appClientBuildFile = new File(appClientBuildSystemConfigFilePathString);
		if (appClientBuildFile.exists()) {
			if (!appClientBuildFile.canRead()) {
				String errorMessage = String.format(
						"app client build.xml[%s]  doesn't hava permission to read",
						appClientBuildSystemConfigFilePathString);
				// log.warn(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			if (!appClientBuildFile.canWrite()) {
				String errorMessage = String.format(
						"app client build.xml[%s]  doesn't hava permission to write",
						appClientBuildSystemConfigFilePathString);
				// log.warn(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			isAppClient = true;
		} else {
			isAppClient = false;
		}

		return isAppClient;
	}

	public static boolean getIsWebClient(String mainProjectName,
			String sinnoriInstalledPathString) throws ConfigErrorException {

		boolean isWebClient;

		String webClientBuildXMLFilePathString = BuildSystemPathSupporter.getWebClientBuildSystemConfigFilePathString(
				mainProjectName, sinnoriInstalledPathString);
		File webClientBuildFile = new File(webClientBuildXMLFilePathString);
		if (webClientBuildFile.exists()) {
			if (!webClientBuildFile.canRead()) {
				String errorMessage = String.format(
						"web client build.xml[%s]  doesn't hava permission to read",
						webClientBuildXMLFilePathString);
				// log.warn(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			if (!webClientBuildFile.canWrite()) {
				String errorMessage = String.format(
						"web client build.xml[%s]  doesn't hava permission to write",
						webClientBuildXMLFilePathString);
				// log.warn(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			isWebClient = true;
		} else {
			isWebClient = false;
		}

		return isWebClient;
	}

	public static void createNewMainProjectBuildSystem(
			String newMainProjectName, String sinnoriInstalledPathString, MessageInfoSAXParser messageInfoSAXParser)
			throws IllegalArgumentException, ConfigErrorException {		
		boolean isServer = true;
		boolean isAppClient = true;
		boolean isWebClient = false;
		String servletSystemLibrayPathString = "";
		
		createNewMainProjectBuildSystem(newMainProjectName, sinnoriInstalledPathString,
				isServer, isAppClient, isWebClient, servletSystemLibrayPathString, messageInfoSAXParser);
	}
	
	public static void createNewMainProjectBuildSystem(
			String newMainProjectName, String sinnoriInstalledPathString,
			boolean isServer,
			boolean isAppClient, 
			boolean isWebClient, String servletSystemLibrayPathString, MessageInfoSAXParser messageInfoSAXParser)
			throws IllegalArgumentException, ConfigErrorException {
		
		String childDirectories[] = { "config", "impl/message/info",
				"rsa_keypair", "log/apache", "log/client", "log/server",
				"log/servlet" };

		String projectPathString = BuildSystemPathSupporter.getProjectPathString(newMainProjectName, sinnoriInstalledPathString);
		
		createChildDirectoriesOfBasePath(projectPathString,
				childDirectories);

		/** <project home>/ant.properties */
		SequencedProperties antProperties = AntPropertiesUtilOfBuildSystem
				.getNewBuildSystemProperties(isWebClient, 
						servletSystemLibrayPathString);
				

		String antPropertiesFilePathString = BuildSystemPathSupporter.getAntPropertiesFilePath(
				newMainProjectName, sinnoriInstalledPathString);
		try {
			SequencedPropertiesUtil.saveSequencedPropertiesToFile(
					antProperties, getAntPropertiesTitle(newMainProjectName),
					antPropertiesFilePathString,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder("fail to save the main project[")
			.append(newMainProjectName)
			.append("]'s ant.properties file[")
			.append(antPropertiesFilePathString)
			.append("]").toString();
			
			log.warn(errorMessage, e);
			
			throw new ConfigErrorException(errorMessage);			
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to save the main project[")
			.append(newMainProjectName)
			.append("]'s ant.properties file[")
			.append(antPropertiesFilePathString)
			.append("]").toString();
			
			log.warn(errorMessage, e);
			
			throw new ConfigErrorException(errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
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
			String errorMessage = String
					.format("the main project[%s]'s sinnori.properties file[%s] not exist",
							newMainProjectName, antPropertiesFilePathString);
			throw new ConfigErrorException(errorMessage);
		} catch (IOException e) {
			String errorMessage = String
					.format("fail to save the main project[%s]'s sinnori.properties file[%s]",
							newMainProjectName, antPropertiesFilePathString);
			log.warn(
					new StringBuilder("fail to save the main project[")
							.append(newMainProjectName)
							.append("]'s sinnori.properties file").toString(),
					e);
			throw new ConfigErrorException(errorMessage);
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
			String[] childDirectories) throws ConfigErrorException {
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
					throw new ConfigErrorException(errorMessage);
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
				throw new ConfigErrorException(errorMessage);
			}

			if (!childPath.canRead()) {
				String errorMessage = String.format("path[%s] doesn't hava permission to read",
						childPathString);
				// log.warn(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			if (!childPath.canWrite()) {
				String errorMessage = String.format(
						"path[%s] doesn't hava permission to write", childPathString);
				// log.warn(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

		}
	}

	private static void createUTF8File(String title, String contents,
			String filePathString) throws ConfigErrorException {
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

			throw new ConfigErrorException(errorMessage);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("서버 build.xml 파일[")
					.append(filePathString).append("] 생성중 IO 에러::")
					.append(e.getMessage()).toString();

			// log.warn(errorMessage);

			throw new ConfigErrorException(errorMessage);
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

	private static void createSeverBuildSystem(String newProjectName,
			String sinnoriInstalledPathString, MessageInfoSAXParser messageInfoSAXParser) throws ConfigErrorException {
		
		String serverBuildPathString = BuildSystemPathSupporter.getServerBuildPathString(newProjectName,
				sinnoriInstalledPathString);
		File serverBuildPath = new File(serverBuildPathString);

		if (!serverBuildPath.exists()) {
			boolean isSuccess = serverBuildPath.mkdir();
			if (!isSuccess) {
				String errorMessage = String.format(
						"fail to make a new server build path[%s]",
						serverBuildPathString);
				// log.warn(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}
		}

		if (!serverBuildPath.isDirectory()) {
			String errorMessage = String.format(
					"server build path[%s] is not directory",
					serverBuildPathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!serverBuildPath.canRead()) {
			String errorMessage = String.format(
					"server build path[%s] doesn't hava permission to read",
					serverBuildPathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!serverBuildPath.canWrite()) {
			String errorMessage = String.format(
					"server build path[%s] doesn't hava permission to write",
					serverBuildPathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
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
				newProjectName, sinnoriInstalledPathString);
		createUTF8File(
				"server build.xml",
				BuildSystemFileContents
						.getServerAntBuildXMLFileContent(
								newProjectName,
								CommonStaticFinalVars.SERVER_MAIN_CLASS_FULL_NAME_VALUE,
								CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE),
				serverBuildXMLFilePathString);

		String relativeExecutabeJarFileName = new StringBuilder("dist")
				.append(File.separator)
				.append(CommonStaticFinalVars.SERVER_EXECUTABLE_JAR_SHORT_FILE_NAME_VALUE)
				.toString();

		/** Server.bat */
		String dosShellFilePathString = new StringBuilder(serverBuildPathString)
				.append(File.separator).append(newProjectName)
				.append("Server.bat").toString();

		createUTF8File("dos shell of server",
				BuildSystemFileContents.getDosShellContents(
						newProjectName,
						BuildSystemPathSupporter.getProjectPathString(newProjectName,
								sinnoriInstalledPathString),

						"-Xmx1024m -Xms1024m", "server", serverBuildPathString,
						relativeExecutabeJarFileName,

						CommonStaticFinalVars.SINNORI_LOGBACK_LOG_FILE_NAME,
						CommonStaticFinalVars.SINNORI_CONFIG_FILE_NAME),
				dosShellFilePathString);

		/** Server.sh */
		String unixShellFilePathString = new StringBuilder(
				serverBuildPathString).append(File.separator)
				.append(newProjectName).append("Server.sh").toString();
		createUTF8File("unix shell of server",
				BuildSystemFileContents.getUnixShellContents(
						newProjectName,
						BuildSystemPathSupporter.getProjectPathString(newProjectName,
								sinnoriInstalledPathString),

						"-Xmx1024m -Xms1024m", "server", serverBuildPathString,
						relativeExecutabeJarFileName,

						CommonStaticFinalVars.SINNORI_LOGBACK_LOG_FILE_NAME,
						CommonStaticFinalVars.SINNORI_CONFIG_FILE_NAME),
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
				BuildSystemPathSupporter.getMessageInfoPathString(newProjectName,
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
			throw new ConfigErrorException(new StringBuilder(errorMessage)
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
	private static void createWebClientBuildSystem(String newMainProjectName,
			String sinnoriInstalledPathString) throws ConfigErrorException {
		String webClientBuildPathString = BuildSystemPathSupporter.getWebClientBuildPathString(
				newMainProjectName, sinnoriInstalledPathString);
		File webClientBuildPath = new File(webClientBuildPathString);
		
		boolean isSuccess = webClientBuildPath.mkdir();
		if (!isSuccess) {
			String errorMessage = String.format(
					"fail to create a new web client build path[%s]",
					webClientBuildPathString);
			log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!webClientBuildPath.isDirectory()) {
			String errorMessage = String.format(
					"web client build path[%s] is not directory",
					webClientBuildPathString);
			log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!webClientBuildPath.canRead()) {
			String errorMessage = String.format(
					"web client build path[%s] doesn't hava permission to read",
					webClientBuildPathString);
			log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!webClientBuildPath.canWrite()) {
			String errorMessage = String.format(
					"web client build path[%s] doesn't hava permission to write",
					webClientBuildPathString);
			log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
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
	
	private static void createWebRootEnvironment(String newMainProjectName,
			String sinnoriInstalledPathString) throws ConfigErrorException {
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
			throw new ConfigErrorException(errorMessage);
		}
		
		// FIXME!		
		if (!webRootPath.isDirectory()) {
			String errorMessage = String.format(
					"the new web root path[%s] is not directory", webRootPathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!webRootPath.canRead()) {
			String errorMessage = String.format("the new web root path[%s] doesn't hava permission to read",
					webRootPathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!webRootPath.canWrite()) {
			String errorMessage = String.format(
					"the new web root path[%s] doesn't hava permission to write", webRootPathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}
		
		String childDirectories[] = { "WEB-INF" };
		createChildDirectoriesOfBasePath(webRootPathString,
				childDirectories);
	}
	

	private static void createAppClientBuildSystem(String newMainProjectName,
			String sinnoriInstalledPathString, MessageInfoSAXParser messageInfoSAXParser) throws ConfigErrorException {
		
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
				throw new ConfigErrorException(errorMessage);
			}
		}

		if (!appClientBuildPath.isDirectory()) {
			String errorMessage = String.format(
					"app client build path[%s] is not directory",
					appClientBuildPath);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!appClientBuildPath.canRead()) {
			String errorMessage = String.format(
					"app client build path[%s] doesn't hava permission to read",
					appClientBuildPathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		if (!appClientBuildPath.canWrite()) {
			String errorMessage = String.format(
					"app client build path[%s] doesn't hava permission to write",
					appClientBuildPathString);
			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
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
						BuildSystemPathSupporter.getProjectPathString(newMainProjectName,
								sinnoriInstalledPathString),

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
						BuildSystemPathSupporter.getProjectPathString(newMainProjectName,
								sinnoriInstalledPathString),

						"-Xmx1024m -Xms1024m", "client", appClientBuildPathString,
						relativeExecutabeJarFileName,

						CommonStaticFinalVars.SINNORI_LOGBACK_LOG_FILE_NAME,
						CommonStaticFinalVars.SINNORI_CONFIG_FILE_NAME),
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
			throw new ConfigErrorException(new StringBuilder(errorMessage)
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
			String sinnoriInstalledPathString) throws ConfigErrorException {
		String projectPathString = BuildSystemPathSupporter.getProjectPathString(projectName,
				sinnoriInstalledPathString);
		File projectPath = new File(projectPathString);
		
		if (!projectPath.exists()) {
			String errorMessage = new StringBuilder("the project path[")
			.append(projectPathString).append("] does not exist").toString();
			throw new ConfigErrorException(errorMessage);
		}
		
		if (!projectPath.isDirectory()) {
			String errorMessage = new StringBuilder("the project path[")
			.append(projectPathString).append("] is not a directory").toString();
			throw new ConfigErrorException(errorMessage);
		}
		
		try {
			FileUtils.forceDelete(projectPath);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to delete the project path[")
			.append(projectPathString).append("]").toString();
			/** 상세 에러 추적용 */
			log.warn(errorMessage, e);
			throw new ConfigErrorException(new StringBuilder(errorMessage)
			.append(", errormessage=")
			.append(e.getMessage()).toString());
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
