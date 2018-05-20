package kr.pe.codda.common.buildsystem.pathsupporter;

import java.io.File;

public abstract class CommonBuildSytemPathSupporter {
	
	/** [installed path]/temp */
	public static String getCommonTempPathString(
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
	
	
	/** [installed path]/log */
	public static String getCommonLogPathString(
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

	/** [common resource path] : [installed path]/resources */
	public static String getCommonResourcesPathString(
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
	
	/** [message info path] : [common resource path]/message_info */
	public static String getCommonMessageInfoDirectoryPathString(String installedPathString) {
		StringBuilder strBuilder = new StringBuilder(getCommonResourcesPathString(installedPathString))
				.append(File.separator)
				.append("message_info");
		return strBuilder.toString();
	}

	/** [message info file path] : [message info path]/[message id].xml */
	public static String getCommonMessageInfoFilePathString(String installedPathString, String messageID) {
		StringBuilder strBuilder = new StringBuilder(getCommonMessageInfoDirectoryPathString(installedPathString))
				.append(File.separator)
				.append(messageID)
				.append(".xml");
		return strBuilder.toString();
	}
	
	
}
