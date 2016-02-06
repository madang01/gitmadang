package kr.pe.sinnori.common.config;

import java.io.File;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;

public abstract class BuildSystemPathSupporter {
	/**
	 * @param sinnoriInstalledPathString
	 *            신놀이 설치 경로
	 * @return "project base path" that is "[sinnori installed path]/project"
	 */
	public static String getProjectBasePathString(
			String sinnoriInstalledPathString) {
		if (null == sinnoriInstalledPathString) {
			throw new IllegalArgumentException(
					"the parameter sinnoriInstalledPathString is null");
		}

		if (sinnoriInstalledPathString.equals("")) {
			throw new IllegalArgumentException(
					"the parameter sinnoriInstalledPathString is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(sinnoriInstalledPathString);
		strBuilder.append(File.separator);
		strBuilder.append("project");
		return strBuilder.toString();
	}

	/**
	 * @param mainProjectName
	 *            프로젝트 이름
	 * @param sinnoriInstalledPathString
	 *            신놀이 설치 경로
	 * @return "project path" that is "{@link #getProjectBasePathString(String)}
	 *         /[project name]"
	 */
	public static String getProjectPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		if (null == mainProjectName) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName is null");
		}
		
		if (mainProjectName.equals("")) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName is a empty string");
		}
		

		StringBuilder strBuilder = new StringBuilder(
				getProjectBasePathString(sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append(mainProjectName);
		return strBuilder.toString();
	}
	
	/**
	 * 지정한 로그 이름을 가진 로그 경로를 반환한다.
	 * @param mainProjectName
	 * @param sinnoriInstalledPathString
	 * @param logName
	 * @return [project path]/log
	 */
	public static String getLogPathString(String mainProjectName,
			String sinnoriInstalledPathString, String logName) {		
		if (null == logName) {
			throw new IllegalArgumentException(
					"the parameter logName is null");
		}
		
		if (logName.equals("")) {
			throw new IllegalArgumentException(
					"the parameter logName is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(
				getProjectPathString(mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("log");
		strBuilder.append(File.separator);
		strBuilder.append(logName);
		return strBuilder.toString();
	}

	/**
	 * @param mainProjectName
	 *            프로젝트 이름
	 * @param sinnoriInstalledPathString
	 *            신놀이 설치 경로
	 * @return "sinnori config path" that is "
	 *         {@link #getProjectPathString(String, String)}/config"
	 */
	public static String getSinnoriConfigPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("config");
		return strBuilder.toString();
	}

	/**
	 * @param mainProjectName
	 *            프로젝트 이름
	 * @param sinnoriInstalledPathString
	 *            신놀이 설치 경로
	 * @return "sinnori config file path" that is "
	 *         {@link #getSinnoriConfigPathString(String, String)}
	 *         /sinnori.properties"
	 */
	public static String getSinnoriConfigFilePathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getSinnoriConfigPathString(mainProjectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.SINNORI_CONFIG_FILE_NAME);
		return strBuilder.toString();
	}

	/**
	 * @param mainProjectName
	 *            프로젝트 이름
	 * @param sinnoriInstalledPathString
	 *            신놀이 설치 경로
	 * @return "logback config file path" that is "
	 *         {@link #getSinnoriConfigPathString(String, String)}
	 *         /locakback.xml"
	 */
	public static String getLogbackConfigFilePathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getSinnoriConfigPathString(mainProjectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.SINNORI_LOGBACK_LOG_FILE_NAME);
		return strBuilder.toString();
	}
	
	/**
	 * DBCP configuration file path : <project path>/dbcp.<dbcp name>.properties
	 */
	public static String getDBCPConfigFilePathString(String mainProjectName,
			String sinnoriInstalledPathString, String dbcpName) {
		StringBuilder strBuilder = new StringBuilder(getSinnoriConfigPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("dbcp.");
		strBuilder.append(dbcpName);
		strBuilder.append(".properties");

		return strBuilder.toString();
	}

	/** message info path : <project path>/impl/message/info */
	public static String getMessageInfoPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				mainProjectName, sinnoriInstalledPathString))
				.append(File.separator).append("impl").append(File.separator)
				.append("message").append(File.separator).append("info");
		return strBuilder.toString();
	}

	/** ant.properties : <project path>/ant.properties */
	public static String getAntBuiltInPropertiesFilePath(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("ant.properties");
		return strBuilder.toString();
	}

	/** RSA keypair path : <project path>/rsa_keypair */
	public static String getSessionKeyRSAKeypairPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("rsa_keypair");
		return strBuilder.toString();
	}

	/** server build path : <project path>/server_build */
	public static String getServerBuildPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("server_build");
		return strBuilder.toString();
	}

	/** server build.xml : <project path>/server_build/build.xml */
	public static String getServerBuildSystemConfigFilePathString(
			String mainProjectName, String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}

	/** APP-INF path : <server build path>/APP-INF */
	public static String getAPPINFPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("APP-INF");
		return strBuilder.toString();
	}

	

	/** client build base path : <project path>/client_build */
	public static String getClientBuildBasePathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("client_build");

		return strBuilder.toString();
	}

	/** application client build path : <project path>/client_build/app_build */
	public static String getAppClientBuildPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getClientBuildBasePathString(mainProjectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("app_build");
		return strBuilder.toString();
	}

	/** application client build.xml : <application client build path>/build.xml */
	public static String getAppClientBuildSystemConfigFilePathString(
			String mainProjectName, String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getAppClientBuildPathString(mainProjectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}

	/** web client build path : <project path>/client_build/web_build */
	public static String getWebClientBuildPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getClientBuildBasePathString(mainProjectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("web_build");
		return strBuilder.toString();
	}

	/** <project path>/web_app_base */
	public static String getWebRootBasePathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("web_app_base");
		return strBuilder.toString();
	}

	/**
	 * <project path>/web_app_base/ROOT
	 */
	public static String getWebRootPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getWebRootBasePathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("ROOT");
		return strBuilder.toString();
	}
	
	/**
	 * <project path>/web_app_base/ROOT/WEB-INF
	 */
	public static String getWEBINFPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getWebRootPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("WEB-INF");
		return strBuilder.toString();
	}
	
	/**
	 * <project path>/web_app_base/ROOT/WEB-INF/web.xml
	 */
	public static String getWebXmlFilePathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getWEBINFPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("web.xml");
		return strBuilder.toString();
	}


	/** web client build.xml : <web client build path>/build.xml */
	public static String getWebClientBuildSystemConfigFilePathString(
			String mainProjectName, String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getWebClientBuildPathString(mainProjectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");

		return strBuilder.toString();
	}
}