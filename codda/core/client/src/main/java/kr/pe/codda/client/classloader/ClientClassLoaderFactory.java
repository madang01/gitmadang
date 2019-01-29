package kr.pe.codda.client.classloader;

import java.io.File;

import kr.pe.codda.common.classloader.ExcludedDynamicClassManager;
import kr.pe.codda.common.classloader.ExcludedDynamicClassManagerIF;
import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.common.exception.CoddaConfigurationException;

public class ClientClassLoaderFactory {
	private String clientClassloaderClassPathString = null;
	private String clientClassloaderReousrcesPathString = null;
	private ExcludedDynamicClassManagerIF excludedDynamicClassManager = new ExcludedDynamicClassManager();
	
	public ClientClassLoaderFactory(String clientClassloaderClassPathString,
			String clientClassloaderReousrcesPathString) throws CoddaConfigurationException {
		if (null == clientClassloaderClassPathString) {
			throw new IllegalArgumentException("the parameter clientClassloaderClassPathString is null");
		}

		File clientAPPINFClassPath = new File(clientClassloaderClassPathString);
		
		if (!clientAPPINFClassPath.exists()) {
			String errorMessage = String.format("the client APP-INF class path[%s] doesn't exist", clientClassloaderClassPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (!clientAPPINFClassPath.isDirectory()) {
			String errorMessage = String.format("the client APP-INF class path[%s] isn't a directory", clientClassloaderClassPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (null == clientClassloaderReousrcesPathString) {
			throw new IllegalArgumentException("the parameter clientClassloaderReousrcesPathString is null");
		}
		
		File projectResourcesPath = new File(clientClassloaderReousrcesPathString);
		
		if (! projectResourcesPath.exists()) {
			String errorMessage = String.format("the project resources path[%s] doesn't exist", clientClassloaderReousrcesPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (! projectResourcesPath.isDirectory()) {
			String errorMessage = String.format("the project resources path[%s] isn't a directory", clientClassloaderReousrcesPathString);
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		this.clientClassloaderClassPathString = clientClassloaderClassPathString;
		this.clientClassloaderReousrcesPathString = clientClassloaderReousrcesPathString;		
	}
	
	public SimpleClassLoader createClientClassLoader() {
		return new SimpleClassLoader(clientClassloaderClassPathString, clientClassloaderReousrcesPathString, excludedDynamicClassManager);
	}
}
