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
	 * @param projectName
	 *            프로젝트 이름
	 * @param sinnoriInstalledPathString
	 *            신놀이 설치 경로
	 * @return "project path" that is "{@link #getProjectBasePathString(String)}
	 *         /[project name]"
	 */
	public static String getProjectPathString(String projectName,
			String sinnoriInstalledPathString) {
		if (null == projectName) {
			throw new IllegalArgumentException(
					"the parameter projectName is null");
		}

		StringBuilder strBuilder = new StringBuilder(
				getProjectBasePathString(sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append(projectName);
		return strBuilder.toString();
	}

	/**
	 * @param projectName
	 *            프로젝트 이름
	 * @param sinnoriInstalledPathString
	 *            신놀이 설치 경로
	 * @return "sinnori config path" that is "
	 *         {@link #getProjectPathString(String, String)}/config"
	 */
	public static String getSinnoriConfigPathString(String projectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				projectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("config");
		return strBuilder.toString();
	}

	/**
	 * @param projectName
	 *            프로젝트 이름
	 * @param sinnoriInstalledPathString
	 *            신놀이 설치 경로
	 * @return "sinnori config file path" that is "
	 *         {@link #getSinnoriConfigPathString(String, String)}
	 *         /sinnori.properties"
	 */
	public static String getSinnoriConfigFilePathString(String projectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getSinnoriConfigPathString(projectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.SINNORI_CONFIG_FILE_NAME);
		return strBuilder.toString();
	}

	/**
	 * @param projectName
	 *            프로젝트 이름
	 * @param sinnoriInstalledPathString
	 *            신놀이 설치 경로
	 * @return "logback config file path" that is "
	 *         {@link #getSinnoriConfigPathString(String, String)}
	 *         /locakback.xml"
	 */
	public static String getLogbackConfigFilePathString(String projectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getSinnoriConfigPathString(projectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.SINNORI_LOGBACK_LOG_FILE_NAME);
		return strBuilder.toString();
	}

	/** message info path : <project path>/impl/message/info */
	public static String getMessageInfoPathString(String projectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				projectName, sinnoriInstalledPathString))
				.append(File.separator).append("impl").append(File.separator)
				.append("message").append(File.separator).append("info");
		return strBuilder.toString();
	}

	/** ant.properties : <project path>/ant.properties */
	public static String getAntPropertiesFilePath(String projectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				projectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("ant.properties");
		return strBuilder.toString();
	}

	/** RSA keypair path : <project path>/rsa_keypair */
	public static String getSessionKeyRSAKeypairPathString(String projectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				projectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("rsa_keypair");
		return strBuilder.toString();
	}

	/** server build path : <project path>/server_build */
	public static String getServerBuildPathString(String projectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				projectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("server_build");
		return strBuilder.toString();
	}

	/** server build.xml : <project path>/server_build/build.xml */
	public static String getServerBuildSystemConfigFilePathString(
			String projectName, String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(
				projectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}

	/** APP-INF path : <server build path>/APP-INF */
	public static String getAPPINFPathString(String projectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(
				projectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("APP-INF");
		return strBuilder.toString();
	}

	/**
	 * DBCP config file path : <server build
	 * path>/APP-INF/resources/kr/pe/sinnori/mybatis/<dbcp connection pool
	 * name>.properties
	 */
	public static String getDBCPConfigFilePathString(String projectName,
			String sinnoriInstalledPathString, String dbcpName) {
		StringBuilder strBuilder = new StringBuilder(getAPPINFPathString(
				projectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("resources");
		strBuilder.append(File.separator);
		strBuilder.append("kr");
		strBuilder.append(File.separator);
		strBuilder.append("pe");
		strBuilder.append(File.separator);
		strBuilder.append("sinnori");
		strBuilder.append(File.separator);
		strBuilder.append("mybatis");
		strBuilder.append(File.separator);
		strBuilder.append(dbcpName);
		strBuilder.append(".properties");

		return strBuilder.toString();
	}

	/** client build base path : <project path>/client_build */
	public static String getClientBuildBasePathString(String projectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				projectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("client_build");

		return strBuilder.toString();
	}

	/** application client build path : <project path>/client_build/app_build */
	public static String getAppClientBuildPathString(String projectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getClientBuildBasePathString(projectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("app_build");
		return strBuilder.toString();
	}

	/** application client build.xml : <application client build path>/build.xml */
	public static String getAppClientBuildSystemConfigFilePathString(
			String projectName, String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getAppClientBuildPathString(projectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}

	/** web client build path : <project path>/client_build/web_build */
	public static String getWebClientBuildPathString(String projectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getClientBuildBasePathString(projectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("web_build");
		return strBuilder.toString();
	}

	/** <project path>/web_app_base */
	public static String getWebRootBasePathString(String projectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(
				projectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("web_app_base");
		return strBuilder.toString();
	}

	public static String getWebRootPathString(String projectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getWebRootBasePathString(
				projectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("ROOT");
		return strBuilder.toString();
	}

	/** web client build.xml : <web client build path>/build.xml */
	public static String getWebClientBuildSystemConfigFilePathString(
			String projectName, String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getWebClientBuildPathString(projectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");

		return strBuilder.toString();
	}
}
