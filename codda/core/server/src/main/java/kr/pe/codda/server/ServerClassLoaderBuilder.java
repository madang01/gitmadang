package kr.pe.codda.server;

import java.io.File;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.classloader.ServerSystemClassLoaderClassManager;
import kr.pe.codda.common.classloader.ServerSystemClassLoaderClassManagerIF;
import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.exception.CoddaConfigurationException;

public class ServerClassLoaderBuilder {
	private String serverAPPINFClassPathString = null;
	private String projectResourcesPathString = null;
	private ServerSystemClassLoaderClassManagerIF serverSystemClassLoaderClassManager = null;
	
	public ServerClassLoaderBuilder() throws CoddaConfigurationException {
		serverSystemClassLoaderClassManager = new ServerSystemClassLoaderClassManager();		
		
		CoddaConfiguration runningProjectConfiguration =  CoddaConfigurationManager.getInstance().getRunningProjectConfiguration();
		
		String mainProjectName = runningProjectConfiguration.getMainProjectName();
		String installedPathString = runningProjectConfiguration.getInstalledPathString();
		
		
		serverAPPINFClassPathString = ServerBuildSytemPathSupporter
				.getServerAPPINFClassPathString(installedPathString, mainProjectName);
		
		File serverAPPINFClassPath = new File(serverAPPINFClassPathString);
		
		if (!serverAPPINFClassPath.exists()) {
			String errorMessage = String.format("the server APP-INF class path[%s] doesn't exist", serverAPPINFClassPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (!serverAPPINFClassPath.isDirectory()) {
			String errorMessage = String.format("the server APP-INF class path[%s] isn't a directory", serverAPPINFClassPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);
		
		File projectResourcesPath = new File(projectResourcesPathString);
		
		if (! projectResourcesPath.exists()) {
			String errorMessage = String.format("the project resources path[%s] doesn't exist", projectResourcesPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (! projectResourcesPath.isDirectory()) {
			String errorMessage = String.format("the project resources path[%s] isn't a directory", projectResourcesPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
	}
	
	public SimpleClassLoader build() {
		return new SimpleClassLoader(serverAPPINFClassPathString, projectResourcesPathString, serverSystemClassLoaderClassManager);
	}
}
