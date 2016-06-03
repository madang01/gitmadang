package kr.pe.sinnori.common.config.buildsystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import kr.pe.sinnori.common.config.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.vo.AllDBCPPartValueObject;
import kr.pe.sinnori.common.config.vo.AllSubProjectPartValueObject;
import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.util.SequencedProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainProjectBuildSystemState {
	private Logger log = LoggerFactory.getLogger(MainProjectBuildSystemState.class);
	
	private String mainProjectName;
	private String sinnoriInstalledPathString;
	// private Frame mainFrame = null;

	private boolean isAppClient = false;
	private boolean isWebClient = false;
	private List<String> subProjectNameList = null;
	private List<String> dbcpNameList = null;
	private String servletSystemLibrayPathString = null;
	private SequencedProperties sinnoriConfigurationSequencedPropties = null;
	
	public MainProjectBuildSystemState(String mainProjectName,
			String sinnoriInstalledPathString)
			throws BuildSystemException {
		this.mainProjectName = mainProjectName;
		this.sinnoriInstalledPathString = sinnoriInstalledPathString;

		updateInformationBasedOnBuildSystem();
	}

	private void updateInformationBasedOnBuildSystem()
			throws BuildSystemException {
		/** Is 'ant built-in properties'(=ant.properties) file valid? */
		BuildSystemSupporter.checkAntBuiltInPropertiesFile(mainProjectName, sinnoriInstalledPathString);

		/** check validation of server build system */
		BuildSystemSupporter.checkServerBuildSystemConfigFile(mainProjectName,
				sinnoriInstalledPathString);

		/** if application client exist then check validation of application client build system */
		boolean isAppClientOfProjectBuildSystem = BuildSystemSupporter
				.getIsAppClientAfterCheckingAppClientBuildSystemConfigFile(
						mainProjectName, sinnoriInstalledPathString);
		
		/** if web application client exist then check validation of web application client build system */
		boolean isWebClientOfProjectBuildSystem = BuildSystemSupporter
				.getIsWebClientAfterCheckingWebClientBuildSystemConfigFile(
						mainProjectName, sinnoriInstalledPathString);
		
		/** if web root exist then check validation of web root */
		boolean isWebRootOfProjectBuildSystem = BuildSystemSupporter
				.getIsWebRootAfterCheckingWebClientBuildSystemConfigFile(mainProjectName, sinnoriInstalledPathString);

		if (isWebRootOfProjectBuildSystem != isWebClientOfProjectBuildSystem) {
			String errorMessage = new StringBuilder("client build system presence[")
			.append(isWebClientOfProjectBuildSystem).append("] is different from web root presence[")
			.append(isWebRootOfProjectBuildSystem).append("]").toString();
					
			throw new BuildSystemException(errorMessage);
		}		
		
		if (!isAppClientOfProjectBuildSystem && !isWebClientOfProjectBuildSystem) {
			String errorMessage = String
					.format("the main project %s[%s] must hava at least one more client between app client and web client",
							mainProjectName, BuildSystemPathSupporter
									.getProjectPathString(mainProjectName,
											sinnoriInstalledPathString));
			throw new BuildSystemException(errorMessage);
		}
		
		this.isAppClient = isAppClientOfProjectBuildSystem;

		SinnoriConfiguration sinnoriConfiguration = null;
		
		try {
			sinnoriConfiguration = new SinnoriConfiguration(mainProjectName, sinnoriInstalledPathString);
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			throw new BuildSystemException(e.getMessage());
		} catch (FileNotFoundException e) {
			log.warn("FileNotFoundException", e);
			throw new BuildSystemException(e.toString());
		} catch (IOException e) {
			log.warn("IOException", e);
			throw new BuildSystemException(e.toString());
		} catch (SinnoriConfigurationException e) {
			log.warn("SinnoriConfigurationException", e);
			throw new BuildSystemException(e.getMessage());
		}
		
		AllDBCPPartValueObject allDBCPPartConfiguration = sinnoriConfiguration.getAllDBCPPart();
		AllSubProjectPartValueObject allSubProjectPartConfiguration = sinnoriConfiguration.getAllSubProjectPart();
		
		dbcpNameList = allDBCPPartConfiguration.getDBCPNameList();
		subProjectNameList = allSubProjectPartConfiguration.getSubProjectNamelist();	
		
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

	public SequencedProperties getSinnoriConfigurationSequencedPropties() {
		return sinnoriConfigurationSequencedPropties;
	}
}
