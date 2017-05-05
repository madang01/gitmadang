package kr.pe.sinnori.common.buildsystem;

import java.util.List;

import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.util.SequencedProperties;

public class MainProjectBuildSystemState {
	// private Logger log = LoggerFactory.getLogger(MainProjectBuildSystemState.class);
	
	private String mainProjectName;
	private String sinnoriInstalledPathString;
	// private Frame mainFrame = null;

	private boolean isServer = false;
	private boolean isAppClient = false;
	private boolean isWebClient = false;
	private String servletSystemLibrayPathString = "";	
	private List<String> subProjectNameList = null;
	private List<String> dbcpNameList = null;	
	private SequencedProperties sinnoriConfigurationSequencedPropties = null;
	
	public MainProjectBuildSystemState(String sinnoriInstalledPathString,
			String mainProjectName,
			boolean isServer, boolean isAppClient, boolean isWebClient, 
			String servletSystemLibrayPathString,
			List<String> dbcpNameList, List<String> subProjectNameList,  
			SequencedProperties sinnoriConfigurationSequencedPropties)
			throws BuildSystemException {
		if (null == sinnoriInstalledPathString) {
			throw new IllegalArgumentException("the parameter sinnoriInstalledPathString is null");
		}
		if (null == mainProjectName) {
			throw new IllegalArgumentException("the parameter mainProjectName is null");
		}
		if (null == servletSystemLibrayPathString) {
			throw new IllegalArgumentException("the parameter servletSystemLibrayPathString is null");
		}
		if (null == subProjectNameList) {
			throw new IllegalArgumentException("the parameter subProjectNameList is null");
		}
		if (null == dbcpNameList) {
			throw new IllegalArgumentException("the parameter dbcpNameList is null");
		}
		if (null == sinnoriConfigurationSequencedPropties) {
			throw new IllegalArgumentException("the parameter sinnoriConfigurationSequencedPropties is null");
		}
		
		this.mainProjectName = mainProjectName;
		this.sinnoriInstalledPathString = sinnoriInstalledPathString;

		this.isServer = isServer;
		this.isAppClient = isAppClient;
		this.isWebClient = isWebClient;
		this.servletSystemLibrayPathString = servletSystemLibrayPathString;
		
		this.subProjectNameList = subProjectNameList;
		this.dbcpNameList = dbcpNameList;		
		this.sinnoriConfigurationSequencedPropties = sinnoriConfigurationSequencedPropties;
	}

	

	public String getMainProjectName() {
		return mainProjectName;
	}

	public String getSinnoriInstalledPathString() {
		return sinnoriInstalledPathString;
	}
	
	public boolean isServer() {
		return isServer;
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



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MainProjectBuildSystemState [mainProjectName=");
		builder.append(mainProjectName);
		builder.append(", sinnoriInstalledPathString=");
		builder.append(sinnoriInstalledPathString);
		builder.append(", isServer=");
		builder.append(isServer);
		builder.append(", isAppClient=");
		builder.append(isAppClient);
		builder.append(", isWebClient=");
		builder.append(isWebClient);
		builder.append(", servletSystemLibrayPathString=");
		builder.append(servletSystemLibrayPathString);
		builder.append(", subProjectNameList=");
		builder.append(subProjectNameList);
		builder.append(", dbcpNameList=");
		builder.append(dbcpNameList);		
		builder.append(", sinnoriConfigurationSequencedPropties=");
		builder.append(sinnoriConfigurationSequencedPropties);
		builder.append("]");
		return builder.toString();
	}
	
	
}
