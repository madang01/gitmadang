package kr.pe.codda.common.buildsystem;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.type.LogType;

public abstract class BuildSystemFileContents {
	
	/** server_build/build.xml */
	public static String getServerAntBuildXMLFileContent(String mainProjectName) {
		return ServerAntBuildXMLFileContenetsBuilder.build(mainProjectName);
	}

	/** client_build/app_build/build.xml */
	public static String getAppClientAntBuildXMLFileContents(String mainProjectName) {		
		return AppClientAntBuildXMLFileContenetsBuilder.build(mainProjectName);
	}

	/** client_build/web_build/build.xml */
	public static String getWebClientAntBuildXMLFileContents(String mainProjectName) {
		return WebClientAntBuildXMLFileContenetsBuilder.build(mainProjectName);
	}

	/**
	 * server_build/<main project name>.sh or client_build/app_build/<main
	 * project name>Client.sh
	 */
	public static String getDosShellContents(String installedPathString, String mainProjectName, 

			String jvmOptions, LogType logType, String workingPathString, String relativeExecutabeJarFileName) {
		final String dosShellLineSeparator = "^";
		
		String commonPartOfShellContents = getCommonPartOfShellContents(installedPathString, mainProjectName, 

				jvmOptions, dosShellLineSeparator, logType, relativeExecutabeJarFileName);

		StringBuilder shellContentsBuilder = new StringBuilder();
		shellContentsBuilder.append("set OLDPWD=%CD%");
		shellContentsBuilder.append(System.getProperty("line.separator"));

		shellContentsBuilder.append("cd /D ");
		shellContentsBuilder.append(workingPathString);
		shellContentsBuilder.append(System.getProperty("line.separator"));

		shellContentsBuilder.append(commonPartOfShellContents);
		shellContentsBuilder.append(System.getProperty("line.separator"));

		shellContentsBuilder.append("cd /D %OLDPWD%");
		return shellContentsBuilder.toString();
	}

	/**
	 * server_build/<main project name>Server.bat or
	 * client_build/app_build/<main project name>Client.bat
	 */
	public static String getUnixShellContents(String installedPathString, String mainProjectName, 

			String jvmOptions, LogType logType, String workingPathString, String relativeExecutabeJarFileName) {

		final String unixShellLineSeparator = "\\";
		
		String commonPartOfShellContents = getCommonPartOfShellContents(installedPathString, mainProjectName, 
				jvmOptions, unixShellLineSeparator, logType, relativeExecutabeJarFileName);

		StringBuilder shellContentsBuilder = new StringBuilder();
		shellContentsBuilder.append("cd ");
		shellContentsBuilder.append(workingPathString);
		shellContentsBuilder.append(System.getProperty("line.separator"));

		shellContentsBuilder.append(commonPartOfShellContents);
		shellContentsBuilder.append(System.getProperty("line.separator"));

		shellContentsBuilder.append("cd -");
		return shellContentsBuilder.toString();
	}

	// FIXME!
	private static String getCommonPartOfShellContents(String installedPathString, String mainProjectName, 
			String jvmOptions, String shellLineSeparator, LogType logType, String relativeExecutabeJarFileName) {
		StringBuilder commandPartBuilder = new StringBuilder();

		commandPartBuilder.append("java ");
		commandPartBuilder.append(jvmOptions);
		commandPartBuilder.append(" ").append(shellLineSeparator).append(System.getProperty("line.separator"));

		commandPartBuilder.append("-D");
		commandPartBuilder.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE);
		commandPartBuilder.append("=").append(
				ProjectBuildSytemPathSupporter.getProjectLogbackConfigFilePathString(installedPathString, mainProjectName));

		commandPartBuilder.append(" ").append(shellLineSeparator).append(System.getProperty("line.separator"));

		commandPartBuilder.append("-D");
		commandPartBuilder.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOG_PATH);
		commandPartBuilder.append("=").append(
				ProjectBuildSytemPathSupporter.getProjectLogPathString(installedPathString, mainProjectName, logType));

		commandPartBuilder.append(" ").append(shellLineSeparator).append(System.getProperty("line.separator"));

		commandPartBuilder.append("-D");
		commandPartBuilder.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH);
		commandPartBuilder.append("=").append(installedPathString);

		commandPartBuilder.append(" ").append(shellLineSeparator).append(System.getProperty("line.separator"));

		commandPartBuilder.append("-D");
		commandPartBuilder.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME);
		commandPartBuilder.append("=").append(mainProjectName);
		commandPartBuilder.append(" ").append(shellLineSeparator).append(System.getProperty("line.separator"));

		commandPartBuilder.append("-jar ").append(relativeExecutabeJarFileName);

		return commandPartBuilder.toString();
	}
}
