package kr.pe.sinnori.common.config.buildsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.exception.BuildSystemException;

public abstract class BuildSystemSupporter {
	private static Logger log = LoggerFactory
			.getLogger(BuildSystemSupporter.class);
	
	/*public static List<String> getSubProjectNameListFromSinnoriConfigSequencedProperties(
			String mainProjectName, String sinnoriInstalledPathString,
			SequencedProperties sinnoriConfigSequencedProperties)
			throws SinnoriConfigurationException {

		final String sinnoriConfigFilePathString = BuildSystemPathSupporter
				.getSinnoriConfigFilePathString(mainProjectName,
						sinnoriInstalledPathString);
		String subProjectNameListValue = sinnoriConfigSequencedProperties
				.getProperty(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING);

		List<String> subProjectNameList = new ArrayList<>();
		if (null == subProjectNameListValue) {
			*//** 프로젝트 목록을 지정하는 키가 없을 경우 *//*
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

			*//**
			 * Waraning! 러시아 페이트공 문제를 피하기 위해서 중복 검사를 위해서 List 가 아닌 HashSet 이용.
			 *//*
			if (subProjectNameHashSet.contains(subProjectName)) {
				String errorMessage = new StringBuilder(
						"the project config file[")
						.append(sinnoriConfigFilePathString)
						.append("]'s sub project name list has a duplicate project name[")
						.append(subProjectName).append("]").toString();

				// log.warn(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			subProjectNameList.add(subProjectName);
			subProjectNameHashSet.add(subProjectName);
		}

		return subProjectNameList;
	}*/

	/*public static List<String> getDBCPNameListFromSinnoriConfigSequencedProperties(
			String projectName, String sinnoriInstalledPathString,
			SequencedProperties sinnoriConfigSequencedProperties)
			throws SinnoriConfigurationException {
		List<String> dbcpNameList = new ArrayList<>();
		final String sinnoriConfigFilePathString = BuildSystemPathSupporter
				.getSinnoriConfigFilePathString(projectName,
						sinnoriInstalledPathString);

		String dbcpNameListValue = sinnoriConfigSequencedProperties
				.getProperty(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING);

		if (null == dbcpNameListValue) {
			*//** DBCP 연결 폴 이름 목록을 지정하는 키가 없을 경우 *//*
			String errorMessage = new StringBuilder("project config file[")
					.append(sinnoriConfigFilePathString)
					.append("] has no a dbcp connection pool name list")
					.toString();
			throw new SinnoriConfigurationException(errorMessage);
		}

		String[] dbcpConnectionPoolNameArrray = dbcpNameListValue.split(",");

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
			*//** DBCP 연결 폴 이름 목록의 이름들중 중복된 것이 있는 경우 *//*
			String errorMessage = new StringBuilder("project config file[")
					.append(sinnoriConfigFilePathString)
					.append("]::dbcp connection pool name list has one more same thing")
					.toString();

			// log.warn(errorMessage);

			throw new SinnoriConfigurationException(errorMessage);
		}
		return dbcpNameList;
	}
*/
	/*public static SequencedProperties loadAntSequencedPropertiesFile(String mainProjectName,
			String sinnoriInstalledPathString) throws BuildSystemException {

		String antPropertiesFilePathString = BuildSystemPathSupporter
				.getAntBuiltInPropertiesFilePath(mainProjectName,
						sinnoriInstalledPathString);

		SequencedProperties antBuiltInProperties = null;

		try {
			antBuiltInProperties = SequencedPropertiesUtil
					.loadSequencedPropertiesFile(
							antPropertiesFilePathString,
							CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to read ant built-in properties file[")
					.append(antPropertiesFilePathString).append("]").toString();
			log.warn(errorMessage, e);
			throw new BuildSystemException(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.getMessage())
					.toString());
		}
		
		String isTomcatString = antBuiltInProperties.getProperty(CommonStaticFinalVars.IS_TOMCAT_KEY);
		
		if (null == isTomcatString) {
			String errorMessage = new StringBuilder(
					"ant built-in properties file[")
					.append(antPropertiesFilePathString)
					.append("]'s key[")
					.append(CommonStaticFinalVars.IS_TOMCAT_KEY)
					.append("] is not found").toString();
			throw new BuildSystemException(errorMessage);
		}
		
		isTomcatString = isTomcatString.toLowerCase();
		boolean isTomcat = false;
		
		if (isTomcatString.equals("true")) {
			isTomcat = true;
		} else if (isTomcatString.equals("false")) {
			isTomcat = false;
		} else {
			String errorMessage = new StringBuilder(
					"ant built-in properties file[")
					.append(antPropertiesFilePathString)
					.append("]'s key[")
					.append(CommonStaticFinalVars.IS_TOMCAT_KEY)
					.append("]' value is bad, the value must be one of true or false").toString();
			throw new BuildSystemException(errorMessage);
		}
				
		if (isTomcat) {
			String servletSystemLibiaryPathString = antBuiltInProperties
					.getProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBIARY_PATH_KEY);
			if (null == servletSystemLibiaryPathString) {
				String errorMessage = new StringBuilder(
						"ant built-in properties file[")
						.append(antPropertiesFilePathString)
						.append("]'s key[")
						.append(CommonStaticFinalVars.SERVLET_SYSTEM_LIBIARY_PATH_KEY)
						.append("] is not found").toString();
				throw new BuildSystemException(errorMessage);
			}
		}
		
		return antBuiltInProperties;
	}*/

	/*public static void checkServerBuildSystemConfigFile(String mainProjectName,
			String sinnoriInstalledPathString) throws BuildSystemException {

		String serverBuildSystemConfigFilePathString = BuildSystemPathSupporter
				.getServerAntBuildFilePathString(mainProjectName,
						sinnoriInstalledPathString);

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

	}*/

	/*
	 * public static void checkBaseClientAntEnvironment(String mainProjectName,
	 * String sinnoriInstalledPathString) throws SinnoriConfigurationException {
	 * String clientBuildBasePathString =
	 * BuildSystemPathSupporter.getClientBuildBasePathString( mainProjectName,
	 * sinnoriInstalledPathString);
	 * 
	 * File clientBuildBasePath = new File(clientBuildBasePathString); if
	 * (!clientBuildBasePath.exists()) { String errorMessage = String.format(
	 * "client build base path[%s] is not found", clientBuildBasePathString); //
	 * log.warn(errorMessage); throw new
	 * SinnoriConfigurationException(errorMessage); }
	 * 
	 * if (!clientBuildBasePath.isDirectory()) { String errorMessage =
	 * String.format( "client build base path[%s] is not directory",
	 * clientBuildBasePathString); // log.warn(errorMessage); throw new
	 * SinnoriConfigurationException(errorMessage); }
	 * 
	 * if (!clientBuildBasePath.canRead()) { String errorMessage =
	 * String.format(
	 * "client build base path[%s] doesn't hava permission to read",
	 * clientBuildBasePathString); // log.warn(errorMessage); throw new
	 * SinnoriConfigurationException(errorMessage); }
	 * 
	 * if (!clientBuildBasePath.canWrite()) { String errorMessage =
	 * String.format(
	 * "client build base path[%s] doesn't hava permission to write",
	 * clientBuildBasePathString); // log.warn(errorMessage); throw new
	 * SinnoriConfigurationException(errorMessage); } }
	 */

	/**
	 * 클라이언트용 응용 프로그램을 만들기 위한 읽기/쓰기 가능한 build.xml 존재여부를 반환한다.
	 * 
	 * @param mainProjectName
	 * @param sinnoriInstalledPathString
	 * @return
	 * @throws BuildSystemException
	 */
	/*public static boolean getIsAppClientAfterCheckingAppClientBuildSystemConfigFile(
			String mainProjectName, String sinnoriInstalledPathString)
			throws BuildSystemException {

		boolean isAppClient;

		String appClientBuildSystemConfigFilePathString = BuildSystemPathSupporter
				.getAppClientAntBuildFilePathString(mainProjectName,
						sinnoriInstalledPathString);
		File appClientBuildFile = new File(
				appClientBuildSystemConfigFilePathString);
		if (appClientBuildFile.exists()) {
			if (!appClientBuildFile.isFile()) {
				String errorMessage = String.format(
						"app client build.xml[%s]  is not a normal file",
						appClientBuildSystemConfigFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			if (!appClientBuildFile.canRead()) {
				String errorMessage = String
						.format("app client build.xml[%s]  doesn't hava permission to read",
								appClientBuildSystemConfigFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			if (!appClientBuildFile.canWrite()) {
				String errorMessage = String
						.format("app client build.xml[%s]  doesn't hava permission to write",
								appClientBuildSystemConfigFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			isAppClient = true;
		} else {
			isAppClient = false;
		}

		return isAppClient;
	}*/

	/*public static boolean getIsWebClientAfterCheckingWebClientBuildSystemConfigFile(
			String mainProjectName, String sinnoriInstalledPathString)
			throws BuildSystemException {

		boolean isWebClient;

		String webClientBuildXMLFilePathString = BuildSystemPathSupporter
				.getWebClientAntBuildFilePathString(mainProjectName,
						sinnoriInstalledPathString);
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
				String errorMessage = String
						.format("web client build.xml[%s]  doesn't hava permission to read",
								webClientBuildXMLFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			if (!webClientBuildFile.canWrite()) {
				String errorMessage = String
						.format("web client build.xml[%s]  doesn't hava permission to write",
								webClientBuildXMLFilePathString);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			isWebClient = true;
		} else {
			isWebClient = false;
		}

		return isWebClient;
	}*/

	/**
	 * 웹 루트 존재 여부는 읽기/쓰기 가능한 web.xml 파일 여부로 판단
	 * 
	 * @param mainProjectName
	 * @param sinnoriInstalledPathString
	 * @return
	 * @throws BuildSystemException
	 */
	/*public static boolean getIsWebRootAfterCheckingWebClientBuildSystemConfigFile(
			String mainProjectName, String sinnoriInstalledPathString)
			throws BuildSystemException {

		boolean isWebRoot;

		String webXmlFilePathString = BuildSystemPathSupporter
				.getWebXMLFilePathString(mainProjectName,
						sinnoriInstalledPathString);

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
	}*/

	public static void createNewMainProjectBuildSystem(
			String newMainProjectName, String sinnoriInstalledPathString,
			boolean isServer, boolean isAppClient,
			boolean isWebClient,
			String servletSystemLibraryPathString)
			throws IllegalArgumentException, BuildSystemException {
		log.info("new main project creation task stat");
		
		ProjectBuilder projectBuilder = new ProjectBuilder(
				sinnoriInstalledPathString, newMainProjectName);
		
		projectBuilder.create(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
		
		log.info("new main project creation task end");
	}

	public static void removeProjectDirectory(String targetProjectName, String sinnoriInstalledPathString) throws BuildSystemException {
		ProjectBuilder projectBuilder = new ProjectBuilder(
				sinnoriInstalledPathString, targetProjectName);
		
		projectBuilder.destory();
	}

	/*public static String getAntPropertiesTitle(String mainProjectName) {
		return new StringBuilder("project[").append(mainProjectName)
				.append("]'s ant properteis file").toString();
	}

	public static String getSinnoriConfigPropertiesTitle(String mainProjectName) {
		return new StringBuilder("project[").append(mainProjectName)
				.append("]'s sinnori config file").toString();
	}*/

}
