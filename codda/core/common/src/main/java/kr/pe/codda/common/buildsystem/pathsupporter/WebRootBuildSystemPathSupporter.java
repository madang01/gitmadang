package kr.pe.codda.common.buildsystem.pathsupporter;

import java.io.File;

public class WebRootBuildSystemPathSupporter {
	/** [project path]/user_web_app_base */
	public static String getUserWebRootBasePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(ProjectBuildSytemPathSupporter.getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("user_web_app_base");
		return strBuilder.toString();
	}
	
	/** [project path]/user_web_app_base/upload */
	public static String getUserWebUploadPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getUserWebRootBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("upload");
		return strBuilder.toString();
	}	
	
	/** [project path]/user_web_app_base/temp */
	public static String getUserWebTempPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getUserWebRootBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("temp");
		return strBuilder.toString();
	}	

	/**
	 * [project path]/user_web_app_base/ROOT
	 */
	public static String getUserWebRootPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getUserWebRootBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("ROOT");
		return strBuilder.toString();
	}
	
	/**
	 * [project path]/user_web_app_base/ROOT/WEB-INF
	 */
	public static String getUserWebINFPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getUserWebRootPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("WEB-INF");
		return strBuilder.toString();
	}
	
	/**
	 * [project path]/user_web_app_base/ROOT/WEB-INF/web.xml
	 */
	public static String getUserWebRootXMLFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getUserWebINFPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web.xml");
		return strBuilder.toString();
	}	
}
