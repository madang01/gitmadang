package kr.pe.codda.common.buildsystem.pathsupporter;

import java.io.File;

public abstract class ServerBuildSytemPathSupporter {
	/** server build path : [project path]/server_build */
	public static String getServerBuildPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(ProjectBuildSytemPathSupporter.getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("server_build");
		return strBuilder.toString();
	}

	/** server build.xml : [project path]/server_build/build.xml */
	public static String getServerAntBuildXMLFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}

	/** APP-INF path : [server build path]/APP-INF */
	public static String getServerAPPINFPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("APP-INF");
		return strBuilder.toString();
	}
	
	/** APP-INF class path : [APP-INF path]/classes */
	public static String getServerAPPINFClassPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerAPPINFPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("classes");		
		return strBuilder.toString();
	}
	
	/** [server build path]/[message source file's relative path] */
	public static String getServerIOSourcePath(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getServerBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath(File.separator));
		
		return strBuilder.toString();
	}
}
