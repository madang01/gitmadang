package kr.pe.codda.server.classloader;

import java.io.File;

import kr.pe.codda.common.classloader.ExcludedDynamicClassManager;
import kr.pe.codda.common.classloader.ExcludedDynamicClassManagerIF;
import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.common.exception.CoddaConfigurationException;

public class ServerClassLoaderFactory {
	private String serverAPPINFClassPathString = null;
	private String projectResourcesPathString = null;
	private ExcludedDynamicClassManagerIF excludedDynamicClassManager = new ExcludedDynamicClassManager();
	
	public ServerClassLoaderFactory(String serverAPPINFClassPathString,
			String projectResourcesPathString) throws CoddaConfigurationException {
		if (null == serverAPPINFClassPathString) {
			throw new IllegalArgumentException("the parameter serverAPPINFClassPathString is null");
		}
		
		File serverAPPINFClassPath = new File(serverAPPINFClassPathString);
		
		if (!serverAPPINFClassPath.exists()) {
			String errorMessage = String.format("the server APP-INF class path[%s] doesn't exist", serverAPPINFClassPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (!serverAPPINFClassPath.isDirectory()) {
			String errorMessage = String.format("the server APP-INF class path[%s] isn't a directory", serverAPPINFClassPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (null == projectResourcesPathString) {
			throw new IllegalArgumentException("the parameter projectResourcesPathString is null");
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
		
		this.serverAPPINFClassPathString = serverAPPINFClassPathString;
		this.projectResourcesPathString = projectResourcesPathString;
	}
	
	public SimpleClassLoader createServerClassLoader() {
		return new SimpleClassLoader(serverAPPINFClassPathString, projectResourcesPathString, excludedDynamicClassManager);
	}
}
