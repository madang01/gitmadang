package kr.pe.codda.common.buildsystem.pathsupporter;

import java.io.File;

public class AppClientBuildSystemPathSupporter {
	/** application client build path : [project path]/client_build/app_build */
	public static String getAppClientBuildPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(ProjectBuildSytemPathSupporter.getClientBuildBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("app_build");
		return strBuilder.toString();
	}

	/** application client build.xml : [application client build path]/build.xml */
	public static String getAppClientAntBuildXMLFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getAppClientBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");
		return strBuilder.toString();
	}
	
	/** [app client build path]/[message source file's relative path] */
	public static String getAppClientIOSourcePath(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getAppClientBuildPathString(installedPathString, 	mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath(File.separator));
		
		return strBuilder.toString();
	}
}
