package kr.pe.codda.common.buildsystem.pathsupporter;

import java.io.File;

public class WebClientBuildSystemPathSupporter {
	/** web client build path : [project path]/client_build/web_build */
	public static String getWebClientBuildPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(ProjectBuildSytemPathSupporter.getClientBuildBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web_build");
		return strBuilder.toString();
	}
	
	/** web client build.xml : [web client build path]/build.xml */
	public static String getWebClientAntBuildXMLFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("build.xml");

		return strBuilder.toString();
	}
	
	// FIXME!
	/** ant.properties : [web client build path]/webAnt.properties */
	public static String getWebClientAntPropertiesFilePath(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("webAnt.properties");
		return strBuilder.toString();
	}
	
	/** [web client build path]/[message source file's relative path] */
	public static String getWebClinetIOSourcePath(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebClientBuildPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append(ProjectBuildSytemPathSupporter.getMessageIOSourceBaseDirectoryRelativePath(File.separator));
		
		return strBuilder.toString();
	}
}
