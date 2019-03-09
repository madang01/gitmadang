package kr.pe.codda.common.buildsystem.pathsupporter;

import java.io.File;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.type.LogType;

public abstract class ProjectBuildSytemPathSupporter {
	/** [project base path] : [installed path]/project */
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
	
	/** [project path] : [project base path]/[main project name]  */
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
	
	/** log path : [project path]/log/[log type name] */
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

	/** project config path : [proejct path]/config */
	public static String getProjectConfigDirectoryPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("config");
		return strBuilder.toString();
	}

	/** project config file path : [project config path]/[project config short file name] */
	public static String getProejctConfigFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(
				getProjectConfigDirectoryPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.CONFIG_FILE_NAME);
		return strBuilder.toString();
	}

	/** project resources path : [project path]/resources */
	public static String getProjectResourcesDirectoryPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(
				getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("resources");
		return strBuilder.toString();
	}
	
	
	
	
	/** logback config file path : [project resources path]/[logack log short file name] */
	public static String getProjectLogbackConfigFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.LOGBACK_LOG_FILE_NAME);
		return strBuilder.toString();
	}
	
	/** DBCP configuration file path : [project path]/resources/dbcp/dbcp.[dbcp name].properties  */
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

	/** message info path : [project path]/resources/message_info */
	public static String getProjectMessageInfoDirectoryPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator)
				.append("message_info");
		return strBuilder.toString();
	}
	
	

	/** message info path : [project path]/resources/message_info/[message id].xml */
	public static String getProjectMessageInfoFilePathString(String installedPathString, String mainProjectName, String messageID) {
		StringBuilder strBuilder = new StringBuilder(getProjectMessageInfoDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator)
				.append(messageID)
				.append(".xml");
		return strBuilder.toString();
	}
	

	/** RSA keypair path : [project path]/resources/rsa_keypair */
	public static String getSessionKeyRSAKeypairPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("rsa_keypair");
		return strBuilder.toString();
	}
	
	/** RSA Publickey file : [RSA keypair path]/[publickey short file name] */
	public static String getSessionKeyRSAPublickeyFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getSessionKeyRSAKeypairPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.PUBLIC_KEY_FILE_NAME);
		return strBuilder.toString();
	}
	
	/** RSA Publickey file : [RSA keypair path]/[privatekey short file name] */
	public static String getSessionKeyRSAPrivatekeyFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getSessionKeyRSAKeypairPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(CommonStaticFinalVars.PRIVATE_KEY_FILE_NAME);
		return strBuilder.toString();
	}
	
	/**  db initialization path : [project path]/resources/db_initialization  */
	public static String getDBInitializationDirecotryPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectResourcesDirectoryPathString(installedPathString, mainProjectName))
				.append(File.separator)
				.append("db_initialization");
		return strBuilder.toString();
	}
	
	/** client build base path : [project path]/client_build */
	public static String getClientBuildBasePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("client_build");

		return strBuilder.toString();
	}
	
	/** [java main source directory relative path] : src[separator]main[separator]java */
	public static String getJavaMainSourceDirectoryRelativePath(String separator) {
		StringBuilder relativePathStringBuilder = new StringBuilder("src")
			.append(separator).append("main")
			.append(separator).append("java"); 
		return relativePathStringBuilder.toString();
	}
	
	/** [java test source directory relative path] : src[separator]main[separator]java */
	public static String getJavaTestSourceDirectoryRelativePath(String separator) {
		StringBuilder relativePathStringBuilder = new StringBuilder("src")
			.append(separator).append("main")
			.append(separator).append("test")
			.append(separator); 
		return relativePathStringBuilder.toString();
	}
	
	
	public static String getJavaSourceBaseDirectoryRelativePath(String separator) {
		StringBuilder relativePathStringBuilder = new StringBuilder(getJavaMainSourceDirectoryRelativePath(separator))
			.append(separator);
		
		// kr.pe.codda.message package to relative path
		for (char ch : CommonStaticFinalVars.BASE_PACKAGE_NAME.toCharArray()) {
			if (ch == '.') {
				relativePathStringBuilder.append(separator);
			} else {
				relativePathStringBuilder.append(ch);
			}
		};
		
		return relativePathStringBuilder.toString();
	}
	
	
	/** [message io source directory relative path] : [java main source directory relative path][separator] */
	public static String getMessageIOSourceBaseDirectoryRelativePath(String separator) {
		StringBuilder relativePathStringBuilder = new StringBuilder(getJavaMainSourceDirectoryRelativePath(separator))
			.append(separator);
		
		// kr.pe.codda.message package to relative path
		for (char ch : CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME.toCharArray()) {
			if (ch == '.') {
				relativePathStringBuilder.append(separator);
			} else {
				relativePathStringBuilder.append(ch);
			}
		};
		
		return relativePathStringBuilder.toString();
	}
	
	public static String getServerTaskDirectoryRelativePath(String separator) {
		StringBuilder relativePathStringBuilder = new StringBuilder(getJavaMainSourceDirectoryRelativePath(separator))
			.append(separator);

		for (char ch : CommonStaticFinalVars.BASE_SERVER_TASK_CLASS_FULL_NAME.toCharArray()) {
			if (ch == '.') {
				relativePathStringBuilder.append(separator);
			} else {
				relativePathStringBuilder.append(ch);
			}
		};
		
		return relativePathStringBuilder.toString();
	}
	
}
