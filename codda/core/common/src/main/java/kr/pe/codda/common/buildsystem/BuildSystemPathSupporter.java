package kr.pe.codda.common.buildsystem;

import java.io.File;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.type.LogType;

public abstract class BuildSystemPathSupporter {
	
	/** <installed path>/temp */
	public static String getRootTempPathString(
			String InstalledPathString) {
		if (null == InstalledPathString) {
			throw new IllegalArgumentException(
					"the parameter InstalledPathString is null");
		}

		if (InstalledPathString.equals("")) {
			throw new IllegalArgumentException(
					"the parameter InstalledPathString is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(InstalledPathString);
		strBuilder.append(File.separator);
		strBuilder.append("temp");
		return strBuilder.toString();
	}
	
	
	/** <installed path>/log */
	public static String getRootLogPathString(
			String InstalledPathString) {
		if (null == InstalledPathString) {
			throw new IllegalArgumentException(
					"the parameter InstalledPathString is null");
		}

		if (InstalledPathString.equals("")) {
			throw new IllegalArgumentException(
					"the parameter InstalledPathString is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(InstalledPathString);
		strBuilder.append(File.separator);
		strBuilder.append("log");
		return strBuilder.toString();
	}

	/** root resources path : <installed path>/resources */
	public static String getRootResourcesPathString(
			String InstalledPathString) {
		if (null == InstalledPathString) {
			throw new IllegalArgumentException(
					"the parameter InstalledPathString is null");
		}

		if (InstalledPathString.equals("")) {
			throw new IllegalArgumentException(
					"the parameter InstalledPathString is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(InstalledPathString);
		strBuilder.append(File.separator);
		strBuilder.append("resources");
		return strBuilder.toString();
	}
	
	/** message info path : <root resource path>/message_info */
	public static String getMessageInfoDirectoryPathStringFromRootResources(String InstalledPathString) {
		StringBuilder strBuilder = new StringBuilder(getRootResourcesPathString(InstalledPathString))
				.append(File.separator)
				.append("message_info");
		return strBuilder.toString();
	}

	/** message info path : <root resource path>/message_info/<message id>.xml */
	public static String getMessageInfoFilePathStringFromRootResources(String InstalledPathString, String messageID) {
		StringBuilder strBuilder = new StringBuilder(getMessageInfoDirectoryPathStringFromRootResources(InstalledPathString))
				.append(File.separator)
				.append(messageID)
				.append(".xml");
		return strBuilder.toString();
	}
	
	/** project base path : <installed path>/project */
	public static String getProjectBasePathString(
			String InstalledPathString) {
		if (null == InstalledPathString) {
			throw new IllegalArgumentException(
					"the parameter InstalledPathString is null");
		}

		if (InstalledPathString.equals("")) {
			throw new IllegalArgumentException(
					"the parameter InstalledPathString is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(InstalledPathString);
		strBuilder.append(File.separator);
		strBuilder.append("project");
		return strBuilder.toString();
	}

	/** project path : <project base path>/<main project name>  */
	public static String getProjectPathString(String InstalledPathString,  String mainProjectName) {
		if (null == mainProjectName) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName is null");
		}
		
		if (mainProjectName.equals("")) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName is a empty string");
		}
		

		StringBuilder strBuilder = new StringBuilder(
				getProjectBasePathString(InstalledPathString));
		strBuilder.append(File.separator);
		strBuilder.append(mainProjectName);
		return strBuilder.toString();
	}
	
	/** log path : <project path>/log/<log type name> */
	public static String getProjectLogPathString(String InstalledPathString, String mainProjectName, LogType logType) {		
		if (null == logType) {
			throw new IllegalArgumentException(
					"the parameter logType is null");
		}
		

		StringBuilder strBuilder = new StringBuilder(
				getProjectPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("log");
		strBuilder.append(File.separator);		
		strBuilder.append(logType.toString().toLowerCase());		
		return strBuilder.toString();
	}

	/** project config path : <proejct path>/config */
	public static String getProjectConfigDirectoryPathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("config");
		return strBuilder.toString();
	}

	/** project config file path : <project config path>/<project config short file name> */
	public static String getProejctConfigFilePathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(
				getProjectConfigDirectoryPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.CONFIG_FILE_NAME);
		return strBuilder.toString();
	}

	/** project resources path : <project path>/resources */
	public static String getProjectResourcesDirectoryPathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(
				getProjectPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("resources");
		return strBuilder.toString();
	}
	
	
	/** logback config file path : <project resources path>/<logack log short file name> */
	public static String getProjectLogbackConfigFilePathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.LOGBACK_LOG_SHORT_FILE_NAME);
		return strBuilder.toString();
	}
	
	/** DBCP configuration file path : <project path>/resources/dbcp/dbcp.<dbcp name>.properties  */
	public static String getProjectDBCPConfigFilePathString(String InstalledPathString, String mainProjectName,
			 String dbcpName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("dbcp");
		strBuilder.append(File.separator);		
		strBuilder.append("dbcp.");
		strBuilder.append(dbcpName);
		strBuilder.append(".properties");

		return strBuilder.toString();
	}

	/** message info path : <project path>/resources/message_info */
	public static String getProjectMessageInfoDirectoryPathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(InstalledPathString, mainProjectName))
				.append(File.separator)
				.append("message_info");
		return strBuilder.toString();
	}
	
	

	/** message info path : <project path>/resources/message_info/<message id>.xml */
	public static String getProjectMessageInfoFilePathString(String InstalledPathString, String mainProjectName, String messageID) {
		StringBuilder strBuilder = new StringBuilder(getProjectMessageInfoDirectoryPathString(InstalledPathString, mainProjectName))
				.append(File.separator)
				.append(messageID)
				.append(".xml");
		return strBuilder.toString();
	}
	

	/** RSA keypair path : <project path>/resources/rsa_keypair */
	public static String getSessionKeyRSAKeypairPathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("rsa_keypair");
		return strBuilder.toString();
	}
	
	/** RSA Publickey file : <RSA keypair path>/<publickey short file name> */
	public static String getSessionKeyRSAPublickeyFilePathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getSessionKeyRSAKeypairPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.PUBLIC_KEY_FILE_NAME);
		return strBuilder.toString();
	}
	
	/** RSA Publickey file : <RSA keypair path>/<privatekey short file name> */
	public static String getSessionKeyRSAPrivatekeyFilePathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getSessionKeyRSAKeypairPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.PRIVATE_KEY_FILE_NAME);
		return strBuilder.toString();
	}

	/** server build path : <project path>/server_build */
	public static String getServerBuildPathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("server_build");
		return strBuilder.toString();
	}

	/** server build.xml : <project path>/server_build/build.xml */
	public static String getServerAntBuildXMLFilePathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}

	/** APP-INF path : <server build path>/APP-INF */
	public static String getServerAPPINFPathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("APP-INF");
		return strBuilder.toString();
	}
	
	/** APP-INF class path : <APP-INF path>/classes */
	public static String getServerAPPINFClassPathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerAPPINFPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("classes");		
		return strBuilder.toString();
	}
	
	
	/**
	 * 
	 * @return <message source file's relative path>, the relative path where message I / O files are located based on 'Ant build path'
	 */
	private static String getRelativePathWhereMessageIOSourceFilesAreLocated() {
		return new StringBuilder("src")
				.append(File.separator).append("main")
				.append(File.separator).append("java")
				.append(File.separator).append("kr")
				.append(File.separator).append("pe")
				.append(File.separator).append("codda")
				.append(File.separator).append("impl")
				.append(File.separator).append("message")
				.toString();
	}

	/** <server build path>/<message source file's relative path> */
	public static String getServerIOSourcePath(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(getRelativePathWhereMessageIOSourceFilesAreLocated());
		
		return strBuilder.toString();
	}

	/** client build base path : <project path>/client_build */
	public static String getClientBuildBasePathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("client_build");

		return strBuilder.toString();
	}
	
	/** application client build path : <project path>/client_build/app_build */
	public static String getAppClientBuildPathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getClientBuildBasePathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("app_build");
		return strBuilder.toString();
	}

	/** application client build.xml : <application client build path>/build.xml */
	public static String getAppClientAntBuildXMLFilePathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getAppClientBuildPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}
	
	/** <app client build path>/<message source file's relative path> */
	public static String getAppClientIOSourcePath(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getAppClientBuildPathString(InstalledPathString, 	mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(getRelativePathWhereMessageIOSourceFilesAreLocated());
		
		return strBuilder.toString();
	}
	

	/** web client build path : <project path>/client_build/web_build */
	public static String getWebClientBuildPathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getClientBuildBasePathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web_build");
		return strBuilder.toString();
	}
	
	/** web client build.xml : <web client build path>/build.xml */
	public static String getWebClientAntBuildXMLFilePathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");

		return strBuilder.toString();
	}
	
	// FIXME!
	/** ant.properties : <web client build path>/webAnt.properties */
	public static String getWebClientAntPropertiesFilePath(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("webAnt.properties");
		return strBuilder.toString();
	}
	
	/** <web client build path>/<message source file's relative path> */
	public static String getWebClinetIOSourcePath(String InstalledPathString, String mainProjectName) {
		// FIXME!
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(getRelativePathWhereMessageIOSourceFilesAreLocated());
		
		return strBuilder.toString();
	}
	

	/** <project path>/web_app_base */
	public static String getWebRootBasePathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web_app_base");
		return strBuilder.toString();
	}
	
	/** <project path>/web_app_base/upload */
	public static String getWebUploadPathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootBasePathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("upload");
		return strBuilder.toString();
	}	
	
	/** <project path>/web_app_base/temp */
	public static String getWebTempPathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootBasePathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("temp");
		return strBuilder.toString();
	}	

	/**
	 * <project path>/web_app_base/ROOT
	 */
	public static String getWebRootPathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootBasePathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("ROOT");
		return strBuilder.toString();
	}
	
	/**
	 * <project path>/web_app_base/ROOT/WEB-INF
	 */
	public static String getWEBINFPathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("WEB-INF");
		return strBuilder.toString();
	}
	
	/**
	 * <project path>/web_app_base/ROOT/WEB-INF/web.xml
	 */
	public static String getWebRootXMLFilePathString(String InstalledPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWEBINFPathString(InstalledPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web.xml");
		return strBuilder.toString();
	}	
}
