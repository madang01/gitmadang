package kr.pe.sinnori.gui.config.buildsystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import kr.pe.sinnori.common.config.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.ConfigErrorException;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.common.util.SequencedPropertiesUtil;
import kr.pe.sinnori.gui.message.builder.info.MessageInfoSAXParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainProjectInformation {
	private Logger log = LoggerFactory.getLogger(MainProjectInformation.class);

	private String mainProjectName;
	private String sinnoriInstalledPathString;

	private boolean isAppClient = false;
	private boolean isWebClient = false;

	private List<String> subProjectNameList = null;
	private List<String> dbcpNameList = null;

	private SequencedProperties antProperties = null;
	private SequencedProperties sinnoriConfigSequencedProperties = null;

	private MessageInfoSAXParser messageInfoSAXParser = null;
	private SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger
			.getInstance();

	public MainProjectInformation(boolean isCreation, String mainProjectName,
			String sinnoriInstalledPathString, MessageInfoSAXParser messageInfoSAXParser) throws ConfigErrorException {
		this.mainProjectName = mainProjectName;
		this.sinnoriInstalledPathString = sinnoriInstalledPathString;
		this.messageInfoSAXParser = messageInfoSAXParser;
		
		if (isCreation) {
			BuildSystemSupporter.createNewMainProjectBuildSystem(mainProjectName, sinnoriInstalledPathString, messageInfoSAXParser);
		} else {
			updateInformationBasedOnBuildSystem();
		}		
	}

	public void updateInformationBasedOnBuildSystem()
			throws ConfigErrorException {
		String antPropertiesFilePathString = BuildSystemPathSupporter
				.getAntPropertiesFilePath(mainProjectName,
						sinnoriInstalledPathString);		

		antProperties = BuildSystemSupporter.getAntSequencedPropertiesAfterValidCheck(antPropertiesFilePathString);

		BuildSystemSupporter.checkSeverAntEnvironment(mainProjectName,
				sinnoriInstalledPathString);
		BuildSystemSupporter.checkBaseClientAntEnvironment(mainProjectName,
				sinnoriInstalledPathString);
		isAppClient = BuildSystemSupporter.getIsAppClient(
				mainProjectName, sinnoriInstalledPathString);
		isWebClient = BuildSystemSupporter.getIsWebClient(
				mainProjectName, sinnoriInstalledPathString);

		if (!isAppClient && !isWebClient) {
			String errorMessage = String
					.format("the main project %s[%s] must hava at least one more client between app client and web client",
							mainProjectName, BuildSystemPathSupporter
									.getProjectPathString(mainProjectName,
											sinnoriInstalledPathString));

			// log.warn(errorMessage);
			throw new ConfigErrorException(errorMessage);
		}

		String sinnoriConfigFilePathString = BuildSystemPathSupporter
				.getSinnoriConfigFilePathString(mainProjectName,
						sinnoriInstalledPathString);

		try {
			sinnoriConfigSequencedProperties = SequencedPropertiesUtil
					.getSequencedPropertiesFromFile(sinnoriConfigFilePathString);
		} catch (FileNotFoundException e) {
			String errorMessage = String
					.format("the main project[%s]'s sinnori.properties file[%s] not exist",
							mainProjectName, antPropertiesFilePathString);
			throw new ConfigErrorException(errorMessage);
		} catch (IOException e) {
			String errorMessage = String
					.format("fail to load the main project[%s]'s sinnori.properties file[%s]",
							mainProjectName, antPropertiesFilePathString);
			log.warn(
					new StringBuilder("fail to load the main project[")
							.append(mainProjectName)
							.append("]'s sinnori.properties file").toString(),
					e);
			throw new ConfigErrorException(errorMessage);
		}

		subProjectNameList = BuildSystemSupporter
				.getSubProjectNameListFromSinnoriConfigSequencedProperties(
						mainProjectName, sinnoriInstalledPathString,
						sinnoriConfigSequencedProperties);
		dbcpNameList = BuildSystemSupporter
				.getDBCPNameListFromSinnoriConfigSequencedProperties(
						mainProjectName, sinnoriInstalledPathString,
						sinnoriConfigSequencedProperties);
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

	/**
	 * @return the unmodifiable sub project name-list
	 */
	public final List<String> getUnmodifiableSubProjectNameList() {
		return Collections.unmodifiableList(subProjectNameList);
	}

	public void addNewSubProjectName(String newSubProjectName) {
		if (subProjectNameList.contains(newSubProjectName)) {
			String errorMesssage = new StringBuilder(
					"the parameter newSubProjectName[")
					.append(newSubProjectName)
					.append("] exist in the sub project name list").toString();
			throw new IllegalArgumentException(errorMesssage);
		}
		subProjectNameList.add(newSubProjectName);

		String subProjectNameListValue = sinnoriConfigSequencedProperties
				.getProperty(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING);
		subProjectNameListValue = new StringBuilder(subProjectNameListValue)
				.append(",").append(newSubProjectName).toString();

		sinnoriConfigSequencedProperties.put(
				CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING,
				subProjectNameListValue);

		List<ItemIDInfo<?>> projectPartItemIDInfoList = sinnoriItemIDInfoManger
				.getUnmodifiableProjectPartItemIDInfoList();

		String prefixOfItemID = new StringBuilder("subproject.")
				.append(newSubProjectName).append(".").toString();

		for (ItemIDInfo<?> itemIDInfo : projectPartItemIDInfoList) {
			String itemKey = new StringBuilder(prefixOfItemID).append(
					itemIDInfo.getItemID()).toString();

			String itemDescKey = itemIDInfo.getItemDescKey(prefixOfItemID);

			sinnoriConfigSequencedProperties.put(itemDescKey,
					itemIDInfo.getDescription());
			sinnoriConfigSequencedProperties.put(itemKey,
					itemIDInfo.getDefaultValue());
		}

		sinnoriItemIDInfoManger
				.updateAllOfDefalutValueDependingOnSinnoriInstalledPath(
						mainProjectName, sinnoriInstalledPathString,
						sinnoriConfigSequencedProperties, prefixOfItemID);
	}

	public void removeSubProjectName(String selectedSubProjectName) {
		if (!subProjectNameList.contains(selectedSubProjectName)) {
			String errorMesssage = new StringBuilder(
					"the parameter selectedSubProjectName[")
					.append(selectedSubProjectName)
					.append("] doesn't exist in the sub project name list")
					.toString();
			throw new IllegalArgumentException(errorMesssage);
		}

		subProjectNameList.remove(selectedSubProjectName);
		String subProjectNameListValue = "";
		int subProjectNameListSize = subProjectNameList.size();
		for (int i = 0; i < subProjectNameListSize; i++) {
			String subProjectName = subProjectNameList.get(i);
			if (0 == i) {
				subProjectNameListValue = subProjectName;
			} else {
				subProjectNameListValue = new StringBuilder(
						subProjectNameListValue).append(",")
						.append(subProjectName).toString();
			}
		}

		sinnoriConfigSequencedProperties.put(
				CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING,
				subProjectNameListValue);

		List<ItemIDInfo<?>> projectPartItemIDInfoList = sinnoriItemIDInfoManger
				.getUnmodifiableProjectPartItemIDInfoList();

		String prefixOfItemID = new StringBuilder("subproject.")
				.append(selectedSubProjectName).append(".").toString();

		for (ItemIDInfo<?> itemIDInfo : projectPartItemIDInfoList) {
			String itemKey = new StringBuilder(prefixOfItemID).append(
					itemIDInfo.getItemID()).toString();

			String itemDescKey = itemIDInfo.getItemDescKey(prefixOfItemID);
			sinnoriConfigSequencedProperties.remove(itemDescKey);
			sinnoriConfigSequencedProperties.remove(itemKey);
		}
	}

	/**
	 * @return the unmodifiable dbcp connection pool name list
	 */
	public final List<String> getUnmodifiableDBCPNameList() {
		return Collections.unmodifiableList(dbcpNameList);
	}

	public void addNewDBCPName(String newDBCPName) {
		if (dbcpNameList.contains(newDBCPName)) {
			String errorMesssage = new StringBuilder(
					"the parameter newDBCPName[").append(newDBCPName)
					.append("] exist in the dbcp connection pool name list")
					.toString();
			throw new IllegalArgumentException(errorMesssage);
		}

		dbcpNameList.add(newDBCPName);

		String dbcpNameListValue = sinnoriConfigSequencedProperties
				.getProperty(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING);
		dbcpNameListValue = new StringBuilder(dbcpNameListValue).append(",")
				.append(newDBCPName).toString();

		sinnoriConfigSequencedProperties.put(
				CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING,
				dbcpNameListValue);

		List<ItemIDInfo<?>> dbcpPartItemIDInfoList = sinnoriItemIDInfoManger
				.getUnmodifiableDBCPPartItemIDInfoList();

		String prefixOfItemID = new StringBuilder("dbcp.").append(newDBCPName)
				.append(".").toString();

		for (ItemIDInfo<?> itemIDInfo : dbcpPartItemIDInfoList) {
			String itemKey = new StringBuilder(prefixOfItemID).append(
					itemIDInfo.getItemID()).toString();

			String itemDescKey = itemIDInfo.getItemDescKey(prefixOfItemID);

			sinnoriConfigSequencedProperties.put(itemDescKey,
					itemIDInfo.getDescription());
			sinnoriConfigSequencedProperties.put(itemKey,
					itemIDInfo.getDefaultValue());
		}

		sinnoriItemIDInfoManger
				.updateAllOfDefalutValueDependingOnSinnoriInstalledPath(
						mainProjectName, sinnoriInstalledPathString,
						sinnoriConfigSequencedProperties, prefixOfItemID);
	}

	public void removeDBCPName(String selectedDBCPName) {
		if (!dbcpNameList.contains(selectedDBCPName)) {
			String errorMesssage = new StringBuilder(
					"the parameter selectedDBCPName[")
					.append(selectedDBCPName)
					.append("] doesn't exist in the dbcp connection pool name list")
					.toString();
			throw new IllegalArgumentException(errorMesssage);
		}

		dbcpNameList.remove(selectedDBCPName);

		String dbcpNameListValue = "";
		int dbcpNameListSize = dbcpNameList.size();
		for (int i = 0; i < dbcpNameListSize; i++) {
			String dbcpName = dbcpNameList.get(i);
			if (0 == i) {
				dbcpNameListValue = dbcpName;
			} else {
				dbcpNameListValue = new StringBuilder(dbcpNameListValue)
						.append(",").append(dbcpName).toString();
			}
		}

		sinnoriConfigSequencedProperties.put(
				CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING,
				dbcpNameListValue);

		List<ItemIDInfo<?>> dbcpPartItemIDInfoList = sinnoriItemIDInfoManger
				.getUnmodifiableDBCPPartItemIDInfoList();

		String prefixOfItemID = new StringBuilder("dbcp.")
				.append(selectedDBCPName).append(".").toString();

		for (ItemIDInfo<?> itemIDInfo : dbcpPartItemIDInfoList) {
			String itemKey = new StringBuilder(prefixOfItemID).append(
					itemIDInfo.getItemID()).toString();

			String itemDescKey = itemIDInfo.getItemDescKey(prefixOfItemID);

			sinnoriConfigSequencedProperties.remove(itemDescKey);
			sinnoriConfigSequencedProperties.remove(itemKey);
		}
	}

	public String getServletSystemLibrayPathString() {
		return antProperties
				.getProperty(CommonStaticFinalVars.SERVLET_SYSTEM_LIBIARY_PATH_KEY);
	}

	public void setAppClient(boolean isAppClient) throws ConfigErrorException {
		if (this.isAppClient == isAppClient)
			return;
		this.isAppClient = isAppClient;
	}

	public void setWebClient(boolean isWebClient) throws ConfigErrorException {
		if (this.isWebClient == isWebClient)
			return;
		this.isWebClient = isWebClient;
		antProperties.setProperty(CommonStaticFinalVars.IS_WEB_CLIENT_KEY,
				String.valueOf(isWebClient));
	}

	public void setServletSystemLibrayPathString(
			String servletSystemLibrayPathString)
			throws IllegalArgumentException {
		antProperties.setProperty(
				CommonStaticFinalVars.SERVLET_SYSTEM_LIBIARY_PATH_KEY,
				servletSystemLibrayPathString);
	}

	public void setSinnoriConfigProperties(String itemKey, String itemValue)
			throws IllegalArgumentException, ConfigErrorException {
		// FIXME!
		log.info("main project[{}] key[{}]=value[{}]", mainProjectName,
				itemKey, itemValue);
		sinnoriConfigSequencedProperties.put(itemKey, itemValue);

		boolean isInactive = sinnoriItemIDInfoManger.isInactive(itemKey,
				sinnoriConfigSequencedProperties);

		if (!isInactive) {
			sinnoriItemIDInfoManger.getNativeValueAfterValidChecker(itemKey,
					sinnoriConfigSequencedProperties);
		}

	}

	public SequencedProperties getSinnoriConfigSequencedProperties() {
		return sinnoriConfigSequencedProperties;
	}

	/*
	 * public boolean isValidConfigFile() { String sinnoriConfigFilePathString =
	 * BuildSystemSupporter.getSinnoriConfigFilePathString(mainProjectName,
	 * sinnoriInstalledPathString); boolean isValid =
	 * SinnoriConfigurationManager
	 * .getInstance().isValidSinnoriConfigurationFile(
	 * sinnoriConfigFilePathString); return isValid; }
	 */

	public void save() throws ConfigErrorException {
		try {
			saveBuildSystemProperteis();
		} catch (IOException e) {
			log.warn("fail to save main project build system properties file",
					e);
			throw new ConfigErrorException(e.getMessage());
		}
		try {
			saveSinnoriConfigProperties();
		} catch (IOException e) {
			log.warn("fail to save main project config properties file", e);
			throw new ConfigErrorException(e.getMessage());
		}

		BuildSystemSupporter.applyAppClientStatus(mainProjectName,
				sinnoriInstalledPathString, isAppClient, messageInfoSAXParser);
		BuildSystemSupporter.applyWebClientStatus(mainProjectName,
				sinnoriInstalledPathString, isWebClient);
	}

	private void saveBuildSystemProperteis() throws FileNotFoundException,
			IOException {
		SequencedPropertiesUtil.saveSequencedPropertiesToFile(antProperties,
				BuildSystemSupporter.getAntPropertiesTitle(mainProjectName),
				BuildSystemPathSupporter.getAntPropertiesFilePath(mainProjectName,
						sinnoriInstalledPathString),
				CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
	}

	private void saveSinnoriConfigProperties() throws FileNotFoundException,
			IOException {
		SequencedPropertiesUtil.saveSequencedPropertiesToFile(
				sinnoriConfigSequencedProperties, BuildSystemSupporter
						.getSinnoriConfigPropertiesTitle(mainProjectName),
						BuildSystemPathSupporter.getSinnoriConfigFilePathString(
						mainProjectName, sinnoriInstalledPathString),
				CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
	}
}
