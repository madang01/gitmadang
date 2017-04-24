package kr.pe.sinnori.common.buildsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.util.SequencedProperties;

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
		log.info("new main project drop task stat");
		
		ProjectBuilder projectBuilder = new ProjectBuilder(
				sinnoriInstalledPathString, targetProjectName);
		
		projectBuilder.dropProject();
		
		log.info("new main project drop task end");
	}
	
	public static MainProjectBuildSystemState getMainProjectBuildSystemState(String sinnoriInstalledPathString, String mainProjectName) throws BuildSystemException {		
		ProjectBuilder projectBuilder = new ProjectBuilder(	sinnoriInstalledPathString, mainProjectName);
		return projectBuilder.getNewInstanceOfMainProjectBuildSystemState();
	}
	
	public static void applySinnoriInstalledPath(String sinnoriInstalledPathString, String mainProjectName) throws BuildSystemException {
		log.info("project sinnori installed path apply task stat");
		
		ProjectBuilder projectBuilder = new ProjectBuilder(
				sinnoriInstalledPathString, mainProjectName);
		
		projectBuilder.applySinnoriInstalledPath();
		
		log.info("project sinnori installed path apply task end");
	}
	
	public static void changeProjectState(String sinnoriInstalledPathString, String mainProjectName,
			boolean isServer,
			boolean isAppClient, 
			boolean isWebClient, String servletSystemLibraryPathString, 
			SequencedProperties modifiedSinnoriConfigSequencedProperties) throws BuildSystemException {
		log.info("project state change task stat");
		ProjectBuilder projectBuilder = new ProjectBuilder(
				sinnoriInstalledPathString, mainProjectName);
		
		projectBuilder.changeProjectState(isServer, isAppClient, isWebClient, servletSystemLibraryPathString, modifiedSinnoriConfigSequencedProperties);
		log.info("project state change task end");
	}	
}
