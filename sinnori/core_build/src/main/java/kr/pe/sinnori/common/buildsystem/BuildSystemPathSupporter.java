package kr.pe.sinnori.common.buildsystem;

import java.io.File;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;

public abstract class BuildSystemPathSupporter {

	/** sinnori resources path : <sinnnori installed path>/resources */
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
	
	public static String getMybatisConfigDTDFilePathString(String sinnoriInstalledPathString) {		
		StringBuilder strBuilder = new StringBuilder(getSinnoriResourcesPathString(sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("mybatis");
		strBuilder.append(File.separator);
		strBuilder.append("mybatis-3-config.dtd");
		return strBuilder.toString();
	}
	
	public static String getMybatisMapperDTDFilePathString(String sinnoriInstalledPathString) {		
		StringBuilder strBuilder = new StringBuilder(getSinnoriResourcesPathString(sinnoriInstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append("mybatis");
		strBuilder.append(File.separator);
		strBuilder.append("mybatis-3-mapper.dtd");
		return strBuilder.toString();
	}
	
	/** project base path : <sinnori installed path>/project */
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

	/** project path : <project base path>/<main project name>  */
	public static String getProjectPathString(String sinnoriInstalledPathString,  String mainProjectName) {
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
	
	/** log path : <project path>/log/<log type name> */
	public static String getLogPathString(String sinnoriInstalledPathString, String mainProjectName, LOG_TYPE logType) {		
		if (null == logType) {
			throw new IllegalArgumentException(
					"the parameter logType is null");
		}
		

		StringBuilder strBuilder = new StringBuilder(
				getProjectPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("log");
		strBuilder.append(File.separator);		
		strBuilder.append(logType.toString().toLowerCase());		
		return strBuilder.toString();
	}

	/** sinnori config path : <proejct path>/config */
	public static String getSinnoriConfigPathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("config");
		return strBuilder.toString();
	}

	/** sinnori config file path : <sinnori config path>/<sinnori config short file name> */
	public static String getSinnoriConfigFilePathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(
				getSinnoriConfigPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.SINNORI_CONFIG_FILE_NAME);
		return strBuilder.toString();
	}

	/** project resources path : <project path>/resources */
	public static String getProjectResourcesPathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(
				getProjectPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("resources");
		return strBuilder.toString();
	}
	
	
	/** logback config file path : <project resources path>/<sinnori logack log file short name> */
	public static String getLogbackConfigFilePathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.SINNORI_LOGBACK_LOG_FILE_NAME);
		return strBuilder.toString();
	}
	
	/** DBCP configuration file path : <project path>/resources/dbcp/dbcp.<dbcp name>.properties  */
	public static String getDBCPConfigFilePathString(String sinnoriInstalledPathString, String mainProjectName,
			 String dbcpName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("dbcp");
		strBuilder.append(File.separator);		
		strBuilder.append("dbcp.");
		strBuilder.append(dbcpName);
		strBuilder.append(".properties");

		return strBuilder.toString();
	}

	/** message info path : <project path>/resources/message_info */
	public static String getMessageInfoPathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesPathString(sinnoriInstalledPathString, mainProjectName))
				.append(File.separator)
				.append("message_info");
		return strBuilder.toString();
	}

	

	/** RSA keypair path : <project path>/resources/rsa_keypair */
	public static String getSessionKeyRSAKeypairPathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("rsa_keypair");
		return strBuilder.toString();
	}
	
	/** RSA Publickey file : <RSA keypair path>/sinnori.publickey */
	public static String getSessionKeyRSAPublickeyFilePathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getSessionKeyRSAKeypairPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.PUBLIC_KEY_FILE_NAME);
		return strBuilder.toString();
	}
	
	/** RSA Publickey file : <RSA keypair path>/sinnori.privatekey */
	public static String getSessionKeyRSAPrivatekeyFilePathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getSessionKeyRSAKeypairPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.PRIVATE_KEY_FILE_NAME);
		return strBuilder.toString();
	}

	/** server build path : <project path>/server_build */
	public static String getServerBuildPathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("server_build");
		return strBuilder.toString();
	}

	/** server build.xml : <project path>/server_build/build.xml */
	public static String getServerAntBuildXMLFilePathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}

	/** APP-INF path : <server build path>/APP-INF */
	public static String getServerAPPINFPathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("APP-INF");
		return strBuilder.toString();
	}
	
	/*public static String getServerAPPINFResourcesPathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerAPPINFPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("resources");
		return strBuilder.toString();
	}*/
	
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
	public static String getServerIOSourcePath(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(getRelativePathWhereMessageIOSourceFilesAreLocated());
		
		return strBuilder.toString();
	}

	/** client build base path : <project path>/client_build */
	public static String getClientBuildBasePathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("client_build");

		return strBuilder.toString();
	}
	
	/** application client build path : <project path>/client_build/app_build */
	public static String getAppClientBuildPathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getClientBuildBasePathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("app_build");
		return strBuilder.toString();
	}

	/** application client build.xml : <application client build path>/build.xml */
	public static String getAppClientAntBuildXMLFilePathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}
	
	/** <app client build path>/src/main/java/kr/pe/sinnori/impl/message */
	public static String getAppClientIOSourcePath(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getAppClientBuildPathString(sinnoriInstalledPathString, 	mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(getRelativePathWhereMessageIOSourceFilesAreLocated());
		
		return strBuilder.toString();
	}
	

	/** web client build path : <project path>/client_build/web_build */
	public static String getWebClientBuildPathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getClientBuildBasePathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web_build");
		return strBuilder.toString();
	}
	
	/** web client build.xml : <web client build path>/build.xml */
	public static String getWebClientAntBuildXMLFilePathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");

		return strBuilder.toString();
	}
	
	// FIXME!
	/** ant.properties : <web client build path>/webAnt.properties */
	public static String getWebClientAntPropertiesFilePath(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("webAnt.properties");
		return strBuilder.toString();
	}
	
	/** <web client build path>/src/main/java/kr/pe/sinnori/impl/message */
	public static String getWebClinetIOSourcePath(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(getRelativePathWhereMessageIOSourceFilesAreLocated());
		
		return strBuilder.toString();
	}
	

	/** <project path>/web_app_base */
	public static String getWebRootBasePathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web_app_base");
		return strBuilder.toString();
	}

	/**
	 * <project path>/web_app_base/ROOT
	 */
	public static String getWebRootPathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootBasePathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("ROOT");
		return strBuilder.toString();
	}
	
	/**
	 * <project path>/web_app_base/ROOT/WEB-INF
	 */
	public static String getWEBINFPathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("WEB-INF");
		return strBuilder.toString();
	}
	
	/**
	 * <project path>/web_app_base/ROOT/WEB-INF/web.xml
	 */
	public static String getWebRootXMLFilePathString(String sinnoriInstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWEBINFPathString(sinnoriInstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web.xml");
		return strBuilder.toString();
	}	
}
