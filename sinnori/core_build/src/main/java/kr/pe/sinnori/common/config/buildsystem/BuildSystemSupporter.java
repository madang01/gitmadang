package kr.pe.sinnori.common.config.buildsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.exception.BuildSystemException;

public abstract class BuildSystemSupporter {
	private static Logger log = LoggerFactory
			.getLogger(BuildSystemSupporter.class);
	
	

	public static void createNewMainProjectBuildSystem(
			String sinnoriInstalledPathString, String newMainProjectName, 
			boolean isServer, boolean isAppClient,
			boolean isWebClient,
			String servletSystemLibraryPathString)
			throws IllegalArgumentException, BuildSystemException {
		log.info("new main project creation task stat");
		
		ProjectBuilder projectBuilder = new ProjectBuilder(
				sinnoriInstalledPathString, newMainProjectName);
		
		projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
		
		log.info("new main project creation task end");
	}

	public static void dropProject(String sinnoriInstalledPathString, String targetProjectName) throws BuildSystemException {
		ProjectBuilder projectBuilder = new ProjectBuilder(
				sinnoriInstalledPathString, targetProjectName);
		
		projectBuilder.dropProject();
	}

}
