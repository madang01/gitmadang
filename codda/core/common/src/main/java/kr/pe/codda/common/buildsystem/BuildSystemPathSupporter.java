package kr.pe.codda.common.buildsystem;

import java.io.File;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.type.LogType;

public abstract class BuildSystemPathSupporter {
	
	/** <installed path>/temp */
	public static String getRootTempPathString(
			String installedPathString) {
		if (null == installedPathString) {
			throw new IllegalArgumentException(
					"the parameter installedPathString is null");
		}

		if (installedPathString.equals("")) {
			throw new IllegalArgumentException(
					"the parameter InstalledPathString is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(installedPathString);
		strBuilder.append(File.separator);
		strBuilder.append("temp");
		return strBuilder.toString();
	}
	
	
	/** <installed path>/log */
	public static String getRootLogPathString(
			String installedPathString) {
		if (null == installedPathString) {
			throw new IllegalArgumentException(
					"the parameter installedPathString is null");
		}

		if (installedPathString.equals("")) {
			throw new IllegalArgumentException(
					"the parameter installedPathString is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(installedPathString);
		strBuilder.append(File.separator);
		strBuilder.append("log");
		return strBuilder.toString();
	}

	/** root resources path : <installed path>/resources */
	public static String getRootResourcesPathString(
			String installedPathString) {
		if (null == installedPathString) {
			throw new IllegalArgumentException(
					"the parameter InstalledPathString is null");
		}

		if (installedPathString.equals("")) {
			throw new IllegalArgumentException(
					"the parameter InstalledPathString is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(installedPathString);
		strBuilder.append(File.separator);
		strBuilder.append("resources");
		return strBuilder.toString();
	}
	
	/** message info path : <root resource path>/message_info */
	public static String getMessageInfoDirectoryPathStringFromRootResources(String installedPathString) {
		StringBuilder strBuilder = new StringBuilder(getRootResourcesPathString(installedPathString))
				.append(File.separator)
				.append("message_info");
		return strBuilder.toString();
	}

	/** message info path : <root resource path>/message_info/<message id>.xml */
	public static String getMessageInfoFilePathStringFromRootResources(String installedPathString, String messageID) {
		StringBuilder strBuilder = new StringBuilder(getMessageInfoDirectoryPathStringFromRootResources(installedPathString))
				.append(File.separator)
				.append(messageID)
				.append(".xml");
		return strBuilder.toString();
	}
	
	/** project base path : <installed path>/project */
	public static String getProjectBasePathString(
			String installedPathString) {
		if (null == installedPathString) {
			throw new IllegalArgumentException(
					"the parameter installedPathString is null");
		}

		if (installedPathString.equals("")) {
			throw new IllegalArgumentException(
					"the parameter installedPathString is a empty string");
		}

		StringBuilder strBuilder = new StringBuilder(installedPathString);
		strBuilder.append(File.separator);
		strBuilder.append("project");
		return strBuilder.toString();
	}

	/** project path : <project base path>/<main project name>  */
	public static String getProjectPathString(String installedPathString,  String mainProjectName) {
		if (null == mainProjectName) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName is null");
		}
		
		if (mainProjectName.equals("")) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName is a empty string");
		}
		

		StringBuilder strBuilder = new StringBuilder(
				getProjectBasePathString(installedPathString));
		strBuilder.append(File.separator);
		strBuilder.append(mainProjectName);
		return strBuilder.toString();
	}
	
	/** log path : <project path>/log/<log type name> */
	public static String getProjectLogPathString(String installedPathString, String mainProjectName, LogType logType) {		
		if (null == logType) {
			throw new IllegalArgumentException(
					"the parameter logType is null");
		}
		

		StringBuilder strBuilder = new StringBuilder(
				getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("log");
		strBuilder.append(File.separator);		
		strBuilder.append(logType.toString().toLowerCase());		
		return strBuilder.toString();
	}

	/** project config path : <proejct path>/config */
	public static String getProjectConfigDirectoryPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("config");
		return strBuilder.toString();
	}

	/** project config file path : <project config path>/<project config short file name> */
	public static String getProejctConfigFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(
				getProjectConfigDirectoryPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.CONFIG_FILE_NAME);
		return strBuilder.toString();
	}

	/** project resources path : <project path>/resources */
	public static String getProjectResourcesDirectoryPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(
				getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("resources");
		return strBuilder.toString();
	}
	
	
	/** logback config file path : <project resources path>/<logack log short file name> */
	public static String getProjectLogbackConfigFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.LOGBACK_LOG_FILE_NAME);
		return strBuilder.toString();
	}
	
	/** DBCP configuration file path : <project path>/resources/dbcp/dbcp.<dbcp name>.properties  */
	public static String getProjectDBCPConfigFilePathString(String installedPathString, String mainProjectName,
			 String dbcpName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("dbcp");
		strBuilder.append(File.separator);		
		strBuilder.append("dbcp.");
		strBuilder.append(dbcpName);
		strBuilder.append(".properties");

		return strBuilder.toString();
	}

	/** message info path : <project path>/resources/message_info */
	public static String getProjectMessageInfoDirectoryPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator)
				.append("message_info");
		return strBuilder.toString();
	}
	
	

	/** message info path : <project path>/resources/message_info/<message id>.xml */
	public static String getProjectMessageInfoFilePathString(String installedPathString, String mainProjectName, String messageID) {
		StringBuilder strBuilder = new StringBuilder(getProjectMessageInfoDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator)
				.append(messageID)
				.append(".xml");
		return strBuilder.toString();
	}
	

	/** RSA keypair path : <project path>/resources/rsa_keypair */
	public static String getSessionKeyRSAKeypairPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("rsa_keypair");
		return strBuilder.toString();
	}
	
	/** RSA Publickey file : <RSA keypair path>/<publickey short file name> */
	public static String getSessionKeyRSAPublickeyFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getSessionKeyRSAKeypairPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.PUBLIC_KEY_FILE_NAME);
		return strBuilder.toString();
	}
	
	/** RSA Publickey file : <RSA keypair path>/<privatekey short file name> */
	public static String getSessionKeyRSAPrivatekeyFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getSessionKeyRSAKeypairPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.PRIVATE_KEY_FILE_NAME);
		return strBuilder.toString();
	}

	/** server build path : <project path>/server_build */
	public static String getServerBuildPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("server_build");
		return strBuilder.toString();
	}

	/** server build.xml : <project path>/server_build/build.xml */
	public static String getServerAntBuildXMLFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}

	/** APP-INF path : <server build path>/APP-INF */
	public static String getServerAPPINFPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("APP-INF");
		return strBuilder.toString();
	}
	
	/** APP-INF class path : <APP-INF path>/classes */
	public static String getServerAPPINFClassPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerAPPINFPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("classes");		
		return strBuilder.toString();
	}
	
	
	/**
	 * 
	 * @return <message source file's relative path>, the relative path where message I / O files are located based on 'Ant build path'
	 */
	private static String getRelativePathWhereMessageIOSourceFilesAreLocated() {
		StringBuilder relativePathStringBuilder = new StringBuilder("src")
			.append(File.separator).append("main")
			.append(File.separator).append("java")
			.append(File.separator);
		
		for (char ch : CommonStaticFinalVars.FIRST_PREFIX_OF_DYNAMIC_CLASS_FULL_NAME.toCharArray()) {
			if (ch == '.') {
				relativePathStringBuilder.append(File.separator);
			} else {
				relativePathStringBuilder.append(ch);
			}
		};
		 
		return relativePathStringBuilder.append("message")
				.toString();
	}

	/** <server build path>/<message source file's relative path> */
	public static String getServerIOSourcePath(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(getRelativePathWhereMessageIOSourceFilesAreLocated());
		
		return strBuilder.toString();
	}

	/** client build base path : <project path>/client_build */
	public static String getClientBuildBasePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("client_build");

		return strBuilder.toString();
	}
	
	/** application client build path : <project path>/client_build/app_build */
	public static String getAppClientBuildPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getClientBuildBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("app_build");
		return strBuilder.toString();
	}

	/** application client build.xml : <application client build path>/build.xml */
	public static String getAppClientAntBuildXMLFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getAppClientBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}
	
	/** <app client build path>/<message source file's relative path> */
	public static String getAppClientIOSourcePath(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getAppClientBuildPathString(installedPathString, 	mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(getRelativePathWhereMessageIOSourceFilesAreLocated());
		
		return strBuilder.toString();
	}
	

	/** web client build path : <project path>/client_build/web_build */
	public static String getWebClientBuildPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getClientBuildBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web_build");
		return strBuilder.toString();
	}
	
	/** web client build.xml : <web client build path>/build.xml */
	public static String getWebClientAntBuildXMLFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");

		return strBuilder.toString();
	}
	
	// FIXME!
	/** ant.properties : <web client build path>/webAnt.properties */
	public static String getWebClientAntPropertiesFilePath(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("webAnt.properties");
		return strBuilder.toString();
	}
	
	/** <web client build path>/<message source file's relative path> */
	public static String getWebClinetIOSourcePath(String installedPathString, String mainProjectName) {
		// FIXME!
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(getRelativePathWhereMessageIOSourceFilesAreLocated());
		
		return strBuilder.toString();
	}
	

	/** <project path>/web_app_base */
	public static String getWebRootBasePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web_app_base");
		return strBuilder.toString();
	}
	
	/** <project path>/web_app_base/upload */
	public static String getWebUploadPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("upload");
		return strBuilder.toString();
	}	
	
	/** <project path>/web_app_base/temp */
	public static String getWebTempPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("temp");
		return strBuilder.toString();
	}	

	/**
	 * <project path>/web_app_base/ROOT
	 */
	public static String getWebRootPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("ROOT");
		return strBuilder.toString();
	}
	
	/**
	 * <project path>/web_app_base/ROOT/WEB-INF
	 */
	public static String getWEBINFPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("WEB-INF");
		return strBuilder.toString();
	}
	
	/**
	 * <project path>/web_app_base/ROOT/WEB-INF/web.xml
	 */
	public static String getWebRootXMLFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWEBINFPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web.xml");
		return strBuilder.toString();
	}	
}
