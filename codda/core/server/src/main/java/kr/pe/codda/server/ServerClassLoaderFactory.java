package kr.pe.codda.server;

import java.io.File;

import kr.pe.codda.common.classloader.ServerSystemClassLoaderClassManager;
import kr.pe.codda.common.classloader.ServerSystemClassLoaderClassManagerIF;
import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.common.exception.CoddaConfigurationException;

public class ServerClassLoaderFactory {
	private String serverAPPINFClassPathString = null;
	private String projectResourcesPathString = null;
	private ServerSystemClassLoaderClassManagerIF serverSystemClassLoaderClassManager = new ServerSystemClassLoaderClassManager();
	
	public ServerClassLoaderFactory(String serverAPPINFClassPathString,
			String projectResourcesPathString) throws CoddaConfigurationException {
		this.serverAPPINFClassPathString = serverAPPINFClassPathString;
		this.projectResourcesPathString = projectResourcesPathString;
		
		File serverAPPINFClassPath = new File(serverAPPINFClassPathString);
		
		if (!serverAPPINFClassPath.exists()) {
			String errorMessage = String.format("the server APP-INF class path[%s] doesn't exist", serverAPPINFClassPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (!serverAPPINFClassPath.isDirectory()) {
			String errorMessage = String.format("the server APP-INF class path[%s] isn't a directory", serverAPPINFClassPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
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
	
	public SimpleClassLoader createServerClassLoader() {
		return new SimpleClassLoader(serverAPPINFClassPathString, projectResourcesPathString, serverSystemClassLoaderClassManager);
	}
}
