package kr.pe.sinnori.common.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.fileorpathstringgetter.AbstractFileOrPathStringGetter;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.config.vo.AllDBCPPartConfiguration;
import kr.pe.sinnori.common.config.vo.AllSubProjectPartConfiguration;
import kr.pe.sinnori.common.config.vo.CommonPartConfiguration;
import kr.pe.sinnori.common.config.vo.DBCPParConfiguration;
import kr.pe.sinnori.common.config.vo.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.common.util.SequencedPropertiesUtil;

public class SinnoriConfiguration {
	private Logger log = LoggerFactory.getLogger(SinnoriConfiguration.class);

	private String mainProjectName = null;
	private String sinnoriInstalledPathString = null;

	private String sinnoriConfigFilePathString = null;
	
	private AllDBCPPartConfiguration allDBCPPartConfiguration = null;
	private CommonPartConfiguration commonPartConfiguration = null;
	private ProjectPartConfiguration mainProjectPartConfiguration = null;
	private AllSubProjectPartConfiguration allSubProjectPartConfiguration = null;	
	
	private SequencedProperties sinnoriConfigSequencedProperties = null;

	public SinnoriConfiguration(String sinnoriInstalledPathString,
			String mainProjectName) throws IllegalArgumentException,
			FileNotFoundException, IOException, SinnoriConfigurationException {
		if (null == sinnoriInstalledPathString) {
			throw new IllegalArgumentException(
					"the parameter sinnoriInstalledPathString is null");
		}

		if (sinnoriInstalledPathString.equals("")) {
			throw new IllegalArgumentException(
					"the parameter sinnoriInstalledPathString is a empty string");
		}

		File sinnoriInstalledPath = new File(sinnoriInstalledPathString);

		if (!sinnoriInstalledPath.exists()) {
			String errorMessage = new StringBuilder(
					"the sinnori installed path(=the parameter sinnoriInstalledPathString[")
					.append(sinnoriInstalledPathString)
					.append("]) doesn't exist").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (!sinnoriInstalledPath.isDirectory()) {
			String errorMessage = new StringBuilder(
					"the sinnori installed path(=the parameter sinnoriInstalledPathString[")
					.append(sinnoriInstalledPathString)
					.append("]) is not a directory").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == mainProjectName) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName is null");
		}
		
		if (mainProjectName.equals("")) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName is a empty string");
		}

		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(mainProjectName)) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName has leading or tailing white space");
		}		

		this.mainProjectName = mainProjectName;
		this.sinnoriInstalledPathString = sinnoriInstalledPathString;
		this.sinnoriConfigFilePathString = BuildSystemPathSupporter
				.getSinnoriConfigFilePathString(sinnoriInstalledPathString, mainProjectName);

		this.allDBCPPartConfiguration = new AllDBCPPartConfiguration();
		this.commonPartConfiguration = new CommonPartConfiguration();
		this.mainProjectPartConfiguration = new ProjectPartConfiguration(
				CommonType.PROJECT_GUBUN.MAIN_PROJECT, mainProjectName);
		this.allSubProjectPartConfiguration = new AllSubProjectPartConfiguration();

		
		this.sinnoriConfigSequencedProperties = SequencedPropertiesUtil
				.loadSequencedPropertiesFile(sinnoriConfigFilePathString, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		
		moveSinnoriConfigSequencedPropertiesToAllPartItemsWhilePerformingValidation();
	}
	
	public SinnoriConfiguration(String sinnoriInstalledPathString,
			String mainProjectName, SequencedProperties modifiedSinnoriConfigSequencedProperties) throws IllegalArgumentException,
			IOException, SinnoriConfigurationException {
		if (null == sinnoriInstalledPathString) {
			throw new IllegalArgumentException(
					"the parameter sinnoriInstalledPathString is null");
		}

		if (sinnoriInstalledPathString.equals("")) {
			throw new IllegalArgumentException(
					"the parameter sinnoriInstalledPathString is a empty string");
		}
		
		File sinnoriInstalledPath = new File(sinnoriInstalledPathString);

		if (!sinnoriInstalledPath.exists()) {
			String errorMessage = new StringBuilder(
					"the sinnori installed path(=the parameter sinnoriInstalledPathString[")
					.append(sinnoriInstalledPathString)
					.append("]) doesn't exist").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (!sinnoriInstalledPath.isDirectory()) {
			String errorMessage = new StringBuilder(
					"the sinnori installed path(=the parameter sinnoriInstalledPathString[")
					.append(sinnoriInstalledPathString)
					.append("]) is not a directory").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == mainProjectName) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName is null");
		}
		
		if (mainProjectName.equals("")) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName is a empty string");
		}

		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(mainProjectName)) {
			throw new IllegalArgumentException(
					"the parameter mainProjectName has leading or tailing white space");
		}		

		this.mainProjectName = mainProjectName;
		this.sinnoriInstalledPathString = sinnoriInstalledPathString;
		
		this.sinnoriConfigFilePathString = BuildSystemPathSupporter
				.getSinnoriConfigFilePathString(sinnoriInstalledPathString, mainProjectName);

		this.allDBCPPartConfiguration = new AllDBCPPartConfiguration();
		this.commonPartConfiguration = new CommonPartConfiguration();
		this.mainProjectPartConfiguration = new ProjectPartConfiguration(
				CommonType.PROJECT_GUBUN.MAIN_PROJECT, mainProjectName);
		this.allSubProjectPartConfiguration = new AllSubProjectPartConfiguration();
		this.sinnoriConfigSequencedProperties = modifiedSinnoriConfigSequencedProperties;
				
		moveSinnoriConfigSequencedPropertiesToAllPartItemsWhilePerformingValidation();
	}

	private void moveSinnoriConfigSequencedPropertiesToAllPartItemsWhilePerformingValidation() throws FileNotFoundException, IOException,
			SinnoriConfigurationException {
		/*
		 * allDBCPPart.clear(); allSubProjectPart.clear();
		 */
		

		List<String> subProjectNameList = new ArrayList<>();
		Set<String> subProjectNameSet = new HashSet<>();

		List<String> dbcpNameList = new ArrayList<>();
		Set<String> dbcpNameSet = new HashSet<>();

		{
			String itemValue = sinnoriConfigSequencedProperties
					.getProperty(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING);
			if (null == itemValue) {
				String errorMessage = new StringBuilder(
						"the sub project name list key(=")
						.append(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)
						.append(") was not found in the sinnori conifg file[")
						.append(sinnoriConfigFilePathString).append("]")
						.toString();
				throw new SinnoriConfigurationException(errorMessage);
			}

			itemValue = itemValue.trim();

			if (!itemValue.equals("")) {
				StringTokenizer tokens = new StringTokenizer(itemValue, ",");
				while (tokens.hasMoreTokens()) {
					String token = tokens.nextToken();
					String subProjectName = token.trim();
					if (subProjectNameSet.contains(subProjectName)) {
						String errorMessage = new StringBuilder(
								"sub project name[")
								.append(subProjectName)
								.append("] over at the project name list of the sinnori conifg file[")
								.append(sinnoriConfigFilePathString)
								.append("]").toString();
						throw new SinnoriConfigurationException(errorMessage);
					}

					subProjectNameList.add(subProjectName);
					subProjectNameSet.add(subProjectName);
				}
			}
		}
		{
			String itemValue = sinnoriConfigSequencedProperties
					.getProperty(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING);
			if (null == itemValue) {
				String errorMessage = new StringBuilder(
						"the dbcp name list key(=")
						.append(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING)
						.append(") was not found in the sinnori conifg file[")
						.append(sinnoriConfigFilePathString).append("]")
						.toString();
				throw new SinnoriConfigurationException(errorMessage);
			}

			itemValue = itemValue.trim();

			if (!itemValue.equals("")) {
				StringTokenizer tokens = new StringTokenizer(itemValue, ",");

				while (tokens.hasMoreTokens()) {
					String token = tokens.nextToken();
					String dbcpName = token.trim();
					if (dbcpNameSet.contains(dbcpName)) {
						String errorMessage = new StringBuilder("dbcp name[")
								.append(dbcpName)
								.append("] over at the dbcp name list of the sinnori conifg file[")
								.append(sinnoriConfigFilePathString)
								.append("]").toString();
						throw new SinnoriConfigurationException(errorMessage);
					}

					dbcpNameList.add(dbcpName);
					dbcpNameSet.add(dbcpName);
				}
			}
		}

		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger
				.getInstance();
		/**
		 * 설정 프로퍼티 파일의 항목 키 전체가 올바른지 검사하기 check item key invalidation by item in
		 * the config file
		 */
		@SuppressWarnings("unchecked")
		Enumeration<String> itemKeys = sinnoriConfigSequencedProperties.keys();
		while (itemKeys.hasMoreElements()) {
			String itemKey = itemKeys.nextElement();

			if (itemKey
					.equals(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)
					|| itemKey
							.equals(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)) {
				continue;
			}

			@SuppressWarnings("unused")
			ItemIDInfo<?> itemIDInfo = null;
			try {
				itemIDInfo = sinnoriItemIDInfoManger.getItemIDInfoFromKey(
						itemKey, dbcpNameSet, subProjectNameSet);
			} catch (IllegalArgumentException e) {
				String errorMessage = new StringBuilder("the item key[")
						.append(itemKey)
						.append("] of the sinnori conifg file[")
						.append(sinnoriConfigFilePathString)
						.append("] is wrong").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
		}

		List<ItemIDInfo<?>> dbcpItemIDInfoList = sinnoriItemIDInfoManger
				.getUnmodifiableDBCPPartItemIDInfoList();
		for (String dbcpName : dbcpNameList) {
			String prefixOfItemID = new StringBuilder("dbcp.").append(dbcpName)
					.append(".").toString();

			DBCPParConfiguration dbcpPartItems = new DBCPParConfiguration(
					dbcpName);
			for (ItemIDInfo<?> itemIDInfo : dbcpItemIDInfoList) {
				String itemID = itemIDInfo.getItemID();
				String itemKey = new StringBuilder(prefixOfItemID).append(
						itemID).toString();
				String itemValue = sinnoriConfigSequencedProperties
						.getProperty(itemKey);
				if (null == itemValue) {
					String errorMessage = new StringBuilder("the item key[")
							.append(itemKey)
							.append("] was not found in the Sinnori conifg file[")
							.append(sinnoriConfigFilePathString).append("]")
							.toString();
					throw new SinnoriConfigurationException(errorMessage);
				}

				

				boolean isInactive = sinnoriItemIDInfoManger.isInactive(itemID,
						prefixOfItemID, sinnoriConfigSequencedProperties);
				if (!isInactive) {
					Object nativeValue = sinnoriItemIDInfoManger
							.getNativeValueAfterValidChecker(itemKey,
									sinnoriConfigSequencedProperties);
					
					try {
						dbcpPartItems.mapping(itemKey, nativeValue);
					} catch (IllegalArgumentException | ClassCastException
							| SinnoriConfigurationException e) {
						String errorMessage = new StringBuilder(
								"fail to map item key[")
								.append(itemKey)
								.append("]'s value[")
								.append(itemValue)
								.append("] to the dbcp part value object's variable in the sinnori conifg file[")
								.append(sinnoriConfigFilePathString)
								.append("]").toString();
						throw new SinnoriConfigurationException(errorMessage);
					}
				} else {
					log.info(
							"item key[{}]'s value is null becase its status is inactive",
							itemKey);
				}

			}
			allDBCPPartConfiguration.addDBCPPartValueObject(dbcpPartItems);
		}

		List<ItemIDInfo<?>> commonItemIDInfoList = sinnoriItemIDInfoManger
				.getUnmodifiableCommonPartItemIDInfoList();
		for (ItemIDInfo<?> itemIDInfo : commonItemIDInfoList) {
			String itemID = itemIDInfo.getItemID();
			String itemKey = itemID;
			String itemValue = sinnoriConfigSequencedProperties
					.getProperty(itemKey);
			if (null == itemValue) {
				String errorMessage = new StringBuilder("the item key[")
						.append(itemKey)
						.append("] was not found in the sinnori conifg file[")
						.append(sinnoriConfigFilePathString).append("]")
						.toString();
				throw new SinnoriConfigurationException(errorMessage);
			}

			

			boolean isInactive = sinnoriItemIDInfoManger.isInactive(itemID, "",
					sinnoriConfigSequencedProperties);
			if (!isInactive) {
				Object nativeValue = sinnoriItemIDInfoManger
							.getNativeValueAfterValidChecker(itemKey,
									sinnoriConfigSequencedProperties);				
				
				try {
					commonPartConfiguration.mapping(itemKey, nativeValue);
				} catch (IllegalArgumentException | ClassCastException
						| SinnoriConfigurationException e) {
					String errorMessage = new StringBuilder(
							"fail to map item key[")
							.append(itemKey)
							.append("]'s value[")
							.append(itemValue)
							.append("] to the common part value object's variable in the sinnori conifg file[")
							.append(sinnoriConfigFilePathString).append("]")
							.toString();
					throw new SinnoriConfigurationException(errorMessage);
				}
			} else {
				log.info(
						"item key[{}]'s value is null because its status is inactive",
						itemKey);
			}

		}

		List<ItemIDInfo<?>> projectItemIDInfoList = sinnoriItemIDInfoManger
				.getUnmodifiableProjectPartItemIDInfoList();

		/** main project part */
		for (ItemIDInfo<?> itemIDInfo : projectItemIDInfoList) {
			String itemID = itemIDInfo.getItemID();
			String itemKey = new StringBuilder("mainproject.").append(itemID)
					.toString();
			String itemValue = sinnoriConfigSequencedProperties
					.getProperty(itemKey);
			if (null == itemValue) {
				String errorMessage = new StringBuilder("the item key[")
						.append(itemKey)
						.append("] was not found in the sinnori conifg file[")
						.append(sinnoriConfigFilePathString).append("]")
						.toString();
				throw new SinnoriConfigurationException(errorMessage);
			}

			

			boolean isInactive = sinnoriItemIDInfoManger.isInactive(itemID,
					"mainproject.", sinnoriConfigSequencedProperties);
			if (!isInactive) {
				Object nativeValue = sinnoriItemIDInfoManger
						.getNativeValueAfterValidChecker(itemKey,
								sinnoriConfigSequencedProperties);
				
				try {
					mainProjectPartConfiguration.mapping(itemKey, nativeValue);
				} catch (IllegalArgumentException | ClassCastException
						| SinnoriConfigurationException e) {
					String errorMessage = new StringBuilder(
							"fail to map item key[")
							.append(itemKey)
							.append("]'s value[")
							.append(itemValue)
							.append("] to the project part value object's variable in the sinnori conifg file[")
							.append(sinnoriConfigFilePathString).append("]")
							.toString();
					throw new SinnoriConfigurationException(errorMessage);
				}
			} else {
				log.info(
						"item key[{}]'s value is null becase its status is inactive",
						itemKey);
			}
		}

		for (String subProjectName : subProjectNameList) {
			String prefixOfItemID = new StringBuilder("subproject.")
					.append(subProjectName).append(".").toString();

			ProjectPartConfiguration subProjectPartItems = new ProjectPartConfiguration(
					CommonType.PROJECT_GUBUN.SUB_PROJECT, subProjectName);
			for (ItemIDInfo<?> itemIDInfo : projectItemIDInfoList) {
				String itemID = itemIDInfo.getItemID();
				String itemKey = new StringBuilder(prefixOfItemID).append(
						itemID).toString();
				String itemValue = sinnoriConfigSequencedProperties
						.getProperty(itemKey);
				if (null == itemValue) {
					String errorMessage = new StringBuilder("the item key[")
							.append(itemKey)
							.append("] was not found in the sinnori conifg file[")
							.append(sinnoriConfigFilePathString).append("]")
							.toString();
					throw new SinnoriConfigurationException(errorMessage);
				}				

				boolean isInactive = sinnoriItemIDInfoManger.isInactive(itemID,
						prefixOfItemID, sinnoriConfigSequencedProperties);
				if (!isInactive) {
					Object nativeValue = sinnoriItemIDInfoManger
							.getNativeValueAfterValidChecker(itemKey,
									sinnoriConfigSequencedProperties);
					
					try {
						subProjectPartItems.mapping(itemKey, nativeValue);
					} catch (IllegalArgumentException | ClassCastException
							| SinnoriConfigurationException e) {
						String errorMessage = new StringBuilder(
								"fail to map item key[")
								.append(itemKey)
								.append("]'s value[")
								.append(itemValue)
								.append("] to the project part value object's variable in the sinnori conifg file[")
								.append(sinnoriConfigFilePathString)
								.append("]").toString();
						throw new SinnoriConfigurationException(errorMessage);
					}
				} else {
					log.info(
							"item key[{}]'s value is null becase its status is inactive",
							itemKey);
				}
			}

			allSubProjectPartConfiguration
					.addSubProjectPartValueObject(subProjectPartItems);
		}
	}

	
	public void changeServerAddress(String newServerHost, int newServerPort) {
		mainProjectPartConfiguration.changeServerAddress(newServerHost, newServerPort);
	}
	
	
	public void createNewFile() throws IOException {
		SequencedPropertiesUtil.createNewSequencedPropertiesFile(sinnoriConfigSequencedProperties,
				getSinnoriConfigPropertiesTitle(), sinnoriConfigFilePathString, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
	}
	
	public void overwriteFile() throws IOException {		
		SequencedPropertiesUtil.overwriteSequencedPropertiesFile(sinnoriConfigSequencedProperties,
				getSinnoriConfigPropertiesTitle(), sinnoriConfigFilePathString, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
	}
	
	// FIXME!
	public void applySinnoriInstalledPath() throws IOException {		
		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger
				.getInstance();
		
		/** common */
		List<ItemIDInfo<?>> commonPartItemIDInfoList = sinnoriItemIDInfoManger
				.getUnmodifiableCommonPartItemIDInfoList();
		{
			for (ItemIDInfo<?> itemIDConfigInfo : commonPartItemIDInfoList) {
				String itemID = itemIDConfigInfo.getItemID();
				String itemKey = itemID;

				AbstractFileOrPathStringGetter fileOrPathStringGetter = sinnoriItemIDInfoManger
						.getFileOrPathStringGetter(itemID);

				if (null != fileOrPathStringGetter) {
					String itemValue = fileOrPathStringGetter
							.getFileOrPathStringDependingOnSinnoriInstalledPath(
									mainProjectName, sinnoriInstalledPathString);
					sinnoriConfigSequencedProperties.put(itemKey, itemValue);
				}
			}
		}

		/** dbcp */
		List<ItemIDInfo<?>> dbcpPartItemIDInfoList = sinnoriItemIDInfoManger
				.getUnmodifiableDBCPPartItemIDInfoList();
		{
			for (String dbcpName : allDBCPPartConfiguration.getDBCPNameList()) {
				String prefixOfItemID = new StringBuilder("dbcp.")
						.append(dbcpName).append(".").toString();
				for (ItemIDInfo<?> itemIDConfigInfo : dbcpPartItemIDInfoList) {
					String itemID = itemIDConfigInfo.getItemID();
					String itemKey = new StringBuilder(prefixOfItemID).append(
							itemID).toString();

					AbstractFileOrPathStringGetter fileOrPathStringGetter = sinnoriItemIDInfoManger
							.getFileOrPathStringGetter(itemID);

					if (null != fileOrPathStringGetter) {
						String itemValue = fileOrPathStringGetter
								.getFileOrPathStringDependingOnSinnoriInstalledPath(
										mainProjectName,
										sinnoriInstalledPathString, dbcpName);
						sinnoriConfigSequencedProperties
								.put(itemKey, itemValue);
					}
				}
			}

		}

		/** main project */
		List<ItemIDInfo<?>> projectPartItemIDInfoList = sinnoriItemIDInfoManger
				.getUnmodifiableProjectPartItemIDInfoList();
		{
			String prefixOfItemID = new StringBuilder("mainproject.")
					.toString();
			for (ItemIDInfo<?> itemIDConfigInfo : projectPartItemIDInfoList) {

				String itemID = itemIDConfigInfo.getItemID();
				String itemKey = new StringBuilder(prefixOfItemID).append(
						itemID).toString();

				AbstractFileOrPathStringGetter fileOrPathStringGetter = sinnoriItemIDInfoManger
						.getFileOrPathStringGetter(itemID);

				if (null != fileOrPathStringGetter) {
					String itemValue = fileOrPathStringGetter
							.getFileOrPathStringDependingOnSinnoriInstalledPath(
									mainProjectName, sinnoriInstalledPathString);

					sinnoriConfigSequencedProperties.put(itemKey, itemValue);
				}
			}
		}

		/** sub project */
		{
			for (String subProjectName : allSubProjectPartConfiguration.getSubProjectNamelist()) {
				String prefixOfItemID = new StringBuilder("subproject.")
						.append(subProjectName).append(".").toString();
				for (ItemIDInfo<?> itemIDConfigInfo : projectPartItemIDInfoList) {

					String itemID = itemIDConfigInfo.getItemID();
					String itemKey = new StringBuilder(prefixOfItemID).append(
							itemID).toString();

					AbstractFileOrPathStringGetter fileOrPathStringGetter = sinnoriItemIDInfoManger
							.getFileOrPathStringGetter(itemID);

					if (null != fileOrPathStringGetter) {
						String itemValue = fileOrPathStringGetter
								.getFileOrPathStringDependingOnSinnoriInstalledPath(
										mainProjectName,
										sinnoriInstalledPathString);

						sinnoriConfigSequencedProperties
								.put(itemKey, itemValue);
					}
				}
			}
		}	

		overwriteFile();
	}
	
	public CommonPartConfiguration getCommonPartConfiguration() {
		return commonPartConfiguration;
	}

	public ProjectPartConfiguration getMainProjectPartConfiguration() {
		return mainProjectPartConfiguration;
	}

	public AllSubProjectPartConfiguration getAllSubProjectPartConfiguration() {
		return allSubProjectPartConfiguration;
	}

	public AllDBCPPartConfiguration getAllDBCPPartConfiguration() {
		return allDBCPPartConfiguration;
	}

	public String getMainProjectName() {
		return mainProjectName;
	}

	public String getSinnoriInstalledPathString() {
		return sinnoriInstalledPathString;
	}

	public SequencedProperties getSinnoriConfigurationSequencedPropties() {
		return sinnoriConfigSequencedProperties;
	}
	
	public String getSinnoriConfigPropertiesTitle() {
		return new StringBuilder("project[").append(mainProjectName).append("]'s sinnori config file").toString();
	}
}
