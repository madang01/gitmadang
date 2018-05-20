package kr.pe.codda.common.buildsystem.pathsupporter;

import java.io.File;

public class WebRootBuildSystemPathSupporter {
	/** [project path]/web_app_base */
	public static String getWebRootBasePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(ProjectBuildSytemPathSupporter.getProjectPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web_app_base");
		return strBuilder.toString();
	}
	
	/** [project path]/web_app_base/upload */
	public static String getWebUploadPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("upload");
		return strBuilder.toString();
	}	
	
	/** [project path]/web_app_base/temp */
	public static String getWebTempPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("temp");
		return strBuilder.toString();
	}	

	/**
	 * [project path]/web_app_base/ROOT
	 */
	public static String getWebRootPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootBasePathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("ROOT");
		return strBuilder.toString();
	}
	
	/**
	 * [project path]/web_app_base/ROOT/WEB-INF
	 */
	public static String getWEBINFPathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWebRootPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("WEB-INF");
		return strBuilder.toString();
	}
	
	/**
	 * [project path]/web_app_base/ROOT/WEB-INF/web.xml
	 */
	public static String getWebRootXMLFilePathString(String installedPathString, String mainProjectName) {
		StringBuilder strBuilder = new StringBuilder(getWEBINFPathString(installedPathString, mainProjectName));
		strBuilder.append(File.separator);
		strBuilder.append("web.xml");
		return strBuilder.toString();
	}	
}
