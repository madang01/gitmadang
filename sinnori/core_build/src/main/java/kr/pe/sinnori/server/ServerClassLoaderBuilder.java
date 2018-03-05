package kr.pe.sinnori.server;

import java.io.File;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.sinnori.common.classloader.SimpleClassLoader;
import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;

public class ServerClassLoaderBuilder {
	private String serverAPPINFClassPathString = null;
	private String projectResourcesPathString = null;
	private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;
	
	public ServerClassLoaderBuilder(IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil) throws SinnoriConfigurationException {
		this.ioPartDynamicClassNameUtil = ioPartDynamicClassNameUtil;		
		
		SinnoriConfiguration sinnoriRunningProjectConfiguration =  SinnoriConfigurationManager.getInstance().getSinnoriRunningProjectConfiguration();
		
		String mainProjectName = sinnoriRunningProjectConfiguration.getMainProjectName();
		String sinnoriInstalledPathString = sinnoriRunningProjectConfiguration.getSinnoriInstalledPathString();
		
		
		serverAPPINFClassPathString = BuildSystemPathSupporter
				.getServerAPPINFClassPathString(sinnoriInstalledPathString, mainProjectName);
		
		File serverAPPINFClassPath = new File(serverAPPINFClassPathString);
		
		if (!serverAPPINFClassPath.exists()) {
			String errorMessage = String.format("the server APP-INF class path[%s] doesn't exist", serverAPPINFClassPathString);
		 	throw new SinnoriConfigurationException(errorMessage);
		}
		
		if (!serverAPPINFClassPath.isDirectory()) {
			String errorMessage = String.format("the server APP-INF class path[%s] isn't a directory", serverAPPINFClassPathString);
		 	throw new SinnoriConfigurationException(errorMessage);
		}
		
		projectResourcesPathString = BuildSystemPathSupporter.getProjectResourcesPathString(sinnoriInstalledPathString, mainProjectName);
		
		File projectResourcesPath = new File(projectResourcesPathString);
		
		if (! projectResourcesPath.exists()) {
			String errorMessage = String.format("the project resources path[%s] doesn't exist", projectResourcesPathString);
		 	throw new SinnoriConfigurationException(errorMessage);
		}
		
		if (! projectResourcesPath.isDirectory()) {
			String errorMessage = String.format("the project resources path[%s] isn't a directory", projectResourcesPathString);
		 	throw new SinnoriConfigurationException(errorMessage);
		}
		
	}
	
	public SimpleClassLoader build() {
		return new SimpleClassLoader(serverAPPINFClassPathString, projectResourcesPathString, ioPartDynamicClassNameUtil);
	}
}
