package kr.pe.sinnori.common.config.buildsystem;

import java.io.File;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;

public abstract class BuildSystemPathSupporter {
	
	public static String getSinnoriResourcesPathString(
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
		strBuilder.append("resouces");
		return strBuilder.toString();
	}
	
	
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

	public static String getResourcesPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getProjectPathString(mainProjectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("resources");
		return strBuilder.toString();
	}
	
	
	/**
	 * @param mainProjectName 프로젝트 이름
	 * @param sinnoriInstalledPathString 신놀이 설치 경로
	 * @return <project path>/resources/logback.xml, <project path> reference  {@link #getProjectPathString(String, String)}
	 */
	public static String getLogbackConfigFilePathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getResourcesPathString(mainProjectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.SINNORI_LOGBACK_LOG_FILE_NAME);
		return strBuilder.toString();
	}
	
	/**
	 * DBCP configuration file path : <project path>/resources/dbcp/dbcp.<dbcp name>.properties
	 */
	public static String getDBCPConfigFilePathString(String mainProjectName,
			String sinnoriInstalledPathString, String dbcpName) {
		StringBuilder strBuilder = new StringBuilder(getResourcesPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("dbcp");
		strBuilder.append(File.separator);		
		strBuilder.append("dbcp.");
		strBuilder.append(dbcpName);
		strBuilder.append(".properties");

		return strBuilder.toString();
	}

	/** message info path : <project path>/resources/message_info */
	public static String getMessageInfoPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getResourcesPathString(
				mainProjectName, sinnoriInstalledPathString)).append(File.separator)
				.append("message_info");
		return strBuilder.toString();
	}

	

	/** RSA keypair path : <project path>/resources/rsa_keypair */
	public static String getSessionKeyRSAKeypairPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getResourcesPathString(
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
	public static String getServerAntBuildXMLFilePathString(
			String mainProjectName, String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}

	/** APP-INF path : <server build path>/APP-INF */
	public static String getServerAPPINFPathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("APP-INF");
		return strBuilder.toString();
	}
	
	/**
	 * @return src/main/java/kr/pe/sinnori/impl/message, the relative path where message I / O files are located based on 'Ant build path'
	 */
	private static String getRelativePathWhereMessageIOSourceFilesAreLocated() {
		return new StringBuilder("src")
				.append(File.separator).append("main")
				.append(File.separator).append("java")
				.append(File.separator).append("kr")
				.append(File.separator).append("pe")
				.append(File.separator).append("sinnori")
				.append(File.separator).append("impl")
				.append(File.separator).append("message")
				.toString();
	}

	/** <server build path>/src/main/java/kr/pe/sinnori/impl/message */
	public static String getServerIOSourcePath(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append(getRelativePathWhereMessageIOSourceFilesAreLocated());
		
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

	// getAppClientAntBuildFilePathString
	
	/** application client build.xml : <application client build path>/build.xml */
	public static String getAppClientAntBuildXMLFilePathString(
			String mainProjectName, String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getAppClientBuildPathString(mainProjectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}
	
	/** <app client build path>/src/main/java/kr/pe/sinnori/impl/message */
	public static String getAppClientIOSourcePath(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getAppClientBuildPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append(getRelativePathWhereMessageIOSourceFilesAreLocated());
		
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
	
	/** web client build.xml : <web client build path>/build.xml */
	public static String getWebClientAntBuildXMLFilePathString(
			String mainProjectName, String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(
				getWebClientBuildPathString(mainProjectName,
						sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");

		return strBuilder.toString();
	}
	
	// FIXME!
	/** ant.properties : <web client build path>/webAnt.properties */
	public static String getWebClientAntPropertiesFilePath(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("webAnt.properties");
		return strBuilder.toString();
	}
	
	/** <web client build path>/src/main/java/kr/pe/sinnori/impl/message */
	public static String getWebIOSourcePath(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append(getRelativePathWhereMessageIOSourceFilesAreLocated());
		
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
	public static String getWebRootXMLFilePathString(String mainProjectName,
			String sinnoriInstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getWEBINFPathString(
				mainProjectName, sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("web.xml");
		return strBuilder.toString();
	}


	
}
