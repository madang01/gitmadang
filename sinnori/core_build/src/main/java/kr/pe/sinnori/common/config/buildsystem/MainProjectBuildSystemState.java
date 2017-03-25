package kr.pe.sinnori.common.config.buildsystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.vo.AllDBCPPartItems;
import kr.pe.sinnori.common.config.vo.AllSubProjectPartItems;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.util.SequencedProperties;

public class MainProjectBuildSystemState {
	private Logger log = LoggerFactory.getLogger(MainProjectBuildSystemState.class);
	
	private String mainProjectName;
	private String sinnoriInstalledPathString;
	// private Frame mainFrame = null;

	private boolean isAppClient = false;
	private boolean isWebClient = false;
	private List<String> subProjectNameList = null;
	private List<String> dbcpNameList = null;
	private boolean isTomcat = false;
	private String servletSystemLibrayPathString = "";
	private SequencedProperties sinnoriConfigurationSequencedPropties = null;
	
	public MainProjectBuildSystemState(String mainProjectName,
			String sinnoriInstalledPathString)
			throws BuildSystemException {
		this.mainProjectName = mainProjectName;
		this.sinnoriInstalledPathString = sinnoriInstalledPathString;

		
		ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);
		
		if (projectBuilder.isValidSeverAntBuildXMLFile()) {
			String serverAntBuildFilePathString = BuildSystemPathSupporter.getServerAntBuildXMLFilePathString(mainProjectName, sinnoriInstalledPathString);
			
			String errorMessage = String.format(
					"the project's server build.xml[%s] file doesn't exist",
					serverAntBuildFilePathString);
			throw new BuildSystemException(errorMessage);
		}
		
		if (projectBuilder.isValidWebClientAntBuildXMLFile()) {
			if (!projectBuilder.isValidWebRootXMLFile()) {
				String webXMLFilePathString = BuildSystemPathSupporter.getWebRootXMLFilePathString(mainProjectName, sinnoriInstalledPathString);
				
				String errorMessage = String.format(
						"the project's WEB-INF/web.xml[%s] file doesn't exist",
						webXMLFilePathString);
				throw new BuildSystemException(errorMessage);
			}
			
			Properties webClientAntProperties = projectBuilder.loadValidWebClientAntPropertiesFile();		
			
			servletSystemLibrayPathString = webClientAntProperties.getProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBRARY_PATH_KEY);
		}
		
		
		
		isAppClient = projectBuilder.isValidAppClientAntBuildXMLFile();
		isWebClient = projectBuilder.isValidWebClientAntBuildXMLFile();
		
		SinnoriConfiguration sinnoriConfiguration = null;
		
		try {
			sinnoriConfiguration = new SinnoriConfiguration(mainProjectName, sinnoriInstalledPathString);
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			throw new BuildSystemException(e.getMessage());
		} catch (FileNotFoundException e) {
			log.warn("FileNotFoundException", e);
			throw new BuildSystemException(e.getMessage());
		} catch (IOException e) {
			log.warn("IOException", e);
			throw new BuildSystemException(e.getMessage());
		} catch (SinnoriConfigurationException e) {
			log.warn("SinnoriConfigurationException", e);
			throw new BuildSystemException(e.getMessage());
		}
		
		AllDBCPPartItems allDBCPPartItems = sinnoriConfiguration.getAllDBCPPart();
		AllSubProjectPartItems allSubProjectPartItems = sinnoriConfiguration.getAllSubProjectPart();
		
		dbcpNameList = allDBCPPartItems.getDBCPNameList();
		subProjectNameList = allSubProjectPartItems.getSubProjectNamelist();	
		
		sinnoriConfigurationSequencedPropties =
		sinnoriConfiguration.getSinnoriConfigurationSequencedPropties();	
	}

	

	public String getMainProjectName() {
		return mainProjectName;
	}

	public String getSinnoriInstalledPathString() {
		return sinnoriInstalledPathString;
	}

	public boolean isAppClient() {
		return isAppClient;
	}

	public boolean isWebClient() {
		return isWebClient;
	}

	
	public final List<String> getSubProjectNameList() {
		return subProjectNameList;
	}
	
	public final List<String> getDBCPNameList() {
		return dbcpNameList;
	}

	public final String getServletSystemLibrayPathString() {
		return servletSystemLibrayPathString;
	}
	
	public final boolean isTomcat() {
		return isTomcat;
	}

	public SequencedProperties getSinnoriConfigurationSequencedPropties() {
		return sinnoriConfigurationSequencedPropties;
	}
}
