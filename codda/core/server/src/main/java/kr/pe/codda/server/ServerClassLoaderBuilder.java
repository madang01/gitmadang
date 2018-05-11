package kr.pe.codda.server;

import java.io.File;

import kr.pe.codda.common.buildsystem.BuildSystemPathSupporter;
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
		
		CoddaConfiguration sinnoriRunningProjectConfiguration =  CoddaConfigurationManager.getInstance().getRunningProjectConfiguration();
		
		String mainProjectName = sinnoriRunningProjectConfiguration.getMainProjectName();
		String sinnoriInstalledPathString = sinnoriRunningProjectConfiguration.getSinnoriInstalledPathString();
		
		
		serverAPPINFClassPathString = BuildSystemPathSupporter
				.getServerAPPINFClassPathString(sinnoriInstalledPathString, mainProjectName);
		
		File serverAPPINFClassPath = new File(serverAPPINFClassPathString);
		
		if (!serverAPPINFClassPath.exists()) {
			String errorMessage = String.format("the server APP-INF class path[%s] doesn't exist", serverAPPINFClassPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (!serverAPPINFClassPath.isDirectory()) {
			String errorMessage = String.format("the server APP-INF class path[%s] isn't a directory", serverAPPINFClassPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		projectResourcesPathString = BuildSystemPathSupporter.getProjectResourcesDirectoryPathString(sinnoriInstalledPathString, mainProjectName);
		
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
