package kr.pe.codda.common.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.config.fileorpathstringgetter.AbstractFileOrPathStringGetter;
import kr.pe.codda.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfo;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfoManger;
import kr.pe.codda.common.config.subset.AllDBCPPartConfiguration;
import kr.pe.codda.common.config.subset.AllSubProjectPartConfiguration;
import kr.pe.codda.common.config.subset.CommonPartConfiguration;
import kr.pe.codda.common.config.subset.DBCPParConfiguration;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.type.ProjectType;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.SequencedProperties;
import kr.pe.codda.common.util.SequencedPropertiesUtil;

public class CoddaConfiguration {
	private InternalLogger log = InternalLoggerFactory.getInstance(CoddaConfiguration.class);

	private String mainProjectName = null;
	private String installedPathString = null;

	private String configFilePathString = null;

	private AllDBCPPartConfiguration allDBCPPartConfiguration = null;
	private CommonPartConfiguration commonPartConfiguration = null;
	private ProjectPartConfiguration mainProjectPartConfiguration = null;
	private AllSubProjectPartConfiguration allSubProjectPartConfiguration = null;

	private SequencedProperties configSequencedProperties = null;

	public CoddaConfiguration(String installedPathString, String mainProjectName)
			throws IllegalArgumentException, CoddaConfigurationException, FileNotFoundException, IOException {
		if (null == installedPathString) {
			throw new IllegalArgumentException("the parameter installedPathString is null");
		}

		if (installedPathString.equals("")) {
			throw new IllegalArgumentException("the parameter installedPathString is a empty string");
		}
		
		// FIXME!
		// System.out.printf("installedPathString=%s, mainProjectName=%s", installedPathString, mainProjectName);
		// System.out.println("");
				

		File installedPath = new File(installedPathString);

		if (!installedPath.exists()) {
			String errorMessage = new StringBuilder(
					"the installed path(=the parameter installedPathString[")
							.append(installedPathString).append("]) doesn't exist").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!installedPath.isDirectory()) {
			String errorMessage = new StringBuilder(
					"the installed path(=the parameter installedPathString[")
							.append(installedPathString).append("]) is not a directory").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == mainProjectName) {
			throw new IllegalArgumentException("the parameter mainProjectName is null");
		}

		if (mainProjectName.equals("")) {
			throw new IllegalArgumentException("the parameter mainProjectName is a empty string");
		}

		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(mainProjectName)) {
			throw new IllegalArgumentException("the parameter mainProjectName has leading or tailing white space");
		}

		this.mainProjectName = mainProjectName;
		this.installedPathString = installedPathString;
		this.configFilePathString = ProjectBuildSytemPathSupporter
				.getProejctConfigFilePathString(installedPathString, mainProjectName);
		
		// FIXME!
		// System.out.printf("configFilePathString=%s", configFilePathString);
		// System.out.println("");

		this.configSequencedProperties = SequencedPropertiesUtil
				.loadSequencedPropertiesFile(configFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);

		initAllPartItems(mainProjectName);
		convertConfigSequencedPropertiesToAllPartItemsWithValidation(this.configSequencedProperties);
	}

	private void initAllPartItems(String mainProjectName) {
		this.allDBCPPartConfiguration = new AllDBCPPartConfiguration();
		this.commonPartConfiguration = new CommonPartConfiguration();
		this.mainProjectPartConfiguration = new ProjectPartConfiguration(ProjectType.MAIN, mainProjectName);
		this.allSubProjectPartConfiguration = new AllSubProjectPartConfiguration();
	}

	public void convertConfigSequencedPropertiesToAllPartItemsWithValidation(
			SequencedProperties configSequencedProperties) throws CoddaConfigurationException {
		List<String> subProjectNameList = new ArrayList<>();
		Set<String> subProjectNameSet = new HashSet<>();
		{
			String itemValue = configSequencedProperties
					.getProperty(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING);
			if (null == itemValue) {
				String errorMessage = new StringBuilder("the sub project name list key(=")
						.append(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)
						.append(") was not found in the conifg file[").append(configFilePathString)
						.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}

			itemValue = itemValue.trim();

			if (!itemValue.equals("")) {
				StringTokenizer tokens = new StringTokenizer(itemValue, ",");
				while (tokens.hasMoreTokens()) {
					String token = tokens.nextToken();
					String subProjectName = token.trim();
					if (subProjectNameSet.contains(subProjectName)) {
						String errorMessage = new StringBuilder("sub project name[").append(subProjectName)
								.append("] over at the project name list of the conifg file[")
								.append(configFilePathString).append("]").toString();
						throw new CoddaConfigurationException(errorMessage);
					}

					subProjectNameList.add(subProjectName);
					subProjectNameSet.add(subProjectName);
				}
			}
		}

		List<String> dbcpNameList = new ArrayList<>();
		Set<String> dbcpNameSet = new HashSet<>();
		{
			String itemValue = configSequencedProperties
					.getProperty(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING);
			if (null == itemValue) {
				String errorMessage = new StringBuilder("the dbcp name list key(=")
						.append(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING)
						.append(") was not found in the conifg file[").append(configFilePathString)
						.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}

			itemValue = itemValue.trim();

			if (!itemValue.equals("")) {
				StringTokenizer tokens = new StringTokenizer(itemValue, ",");

				while (tokens.hasMoreTokens()) {
					String token = tokens.nextToken();
					String dbcpName = token.trim();
					if (dbcpNameSet.contains(dbcpName)) {
						String errorMessage = new StringBuilder("dbcp name[").append(dbcpName)
								.append("] over at the dbcp name list of the conifg file[")
								.append(configFilePathString).append("]").toString();
						throw new CoddaConfigurationException(errorMessage);
					}

					dbcpNameList.add(dbcpName);
					dbcpNameSet.add(dbcpName);
				}
			}
		}

		ItemIDInfoManger itemIDInfoManger = ItemIDInfoManger.getInstance();
		/**
		 * 설정 프로퍼티 파일의 항목 키 전체가 올바른지 검사하기 check item key invalidation by item in the
		 * config file
		 */
		@SuppressWarnings("unchecked")
		Enumeration<String> itemKeys = configSequencedProperties.keys();
		while (itemKeys.hasMoreElements()) {
			String itemKey = itemKeys.nextElement();

			if (itemKey.equals(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)
					|| itemKey.equals(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)) {
				continue;
			}

			@SuppressWarnings("unused")
			ItemIDInfo<?> itemIDInfo = null;
			try {
				itemIDInfo = itemIDInfoManger.getItemIDInfoFromKey(itemKey, dbcpNameSet, subProjectNameSet);
			} catch (IllegalArgumentException e) {
				// log.warn("", e);

				String errorMessage = new StringBuilder("error message=[").append(e.getMessage())
						.append("], the conifg file[").append(configFilePathString).append("]")
						.toString();
				throw new CoddaConfigurationException(errorMessage);
			}
		}

		List<ItemIDInfo<?>> dbcpItemIDInfoList = itemIDInfoManger.getUnmodifiableDBCPPartItemIDInfoList();
		for (String dbcpName : dbcpNameList) {
			String prefixOfItemID = new StringBuilder("dbcp.").append(dbcpName).append(".").toString();

			DBCPParConfiguration dbcpPartItems = new DBCPParConfiguration(dbcpName);
			for (ItemIDInfo<?> itemIDInfo : dbcpItemIDInfoList) {
				String itemID = itemIDInfo.getItemID();
				String itemKey = new StringBuilder(prefixOfItemID).append(itemID).toString();
				String itemValue = configSequencedProperties.getProperty(itemKey);
				if (null == itemValue) {
					String errorMessage = new StringBuilder("the item key[").append(itemKey)
							.append("] was not found in the conifg file[").append(configFilePathString)
							.append("]").toString();
					throw new CoddaConfigurationException(errorMessage);
				}

				boolean isInactive = itemIDInfoManger.isDisabled(itemID, prefixOfItemID,
						configSequencedProperties);
				if (!isInactive) {
					Object nativeValue = itemIDInfoManger.getNativeValueAfterValidChecker(itemKey,
							configSequencedProperties);

					try {
						dbcpPartItems.mapping(itemKey, nativeValue);
					} catch (IllegalArgumentException | ClassCastException | CoddaConfigurationException e) {
						String errorMessage = new StringBuilder("fail to map item key[").append(itemKey)
								.append("]'s value[").append(itemValue)
								.append("] to the dbcp part value object's variable in the conifg file[")
								.append(configFilePathString).append("]").toString();
						throw new CoddaConfigurationException(errorMessage);
					}
				} else {
					log.info("item key[{}]'s value is null becase its status is inactive", itemKey);
				}

			}
			allDBCPPartConfiguration.addDBCPPartValueObject(dbcpPartItems);
		}

		List<ItemIDInfo<?>> commonItemIDInfoList = itemIDInfoManger.getUnmodifiableCommonPartItemIDInfoList();
		for (ItemIDInfo<?> itemIDInfo : commonItemIDInfoList) {
			String itemID = itemIDInfo.getItemID();
			String itemKey = itemID;
			String itemValue = configSequencedProperties.getProperty(itemKey);
			if (null == itemValue) {
				String errorMessage = new StringBuilder("the item key[").append(itemKey)
						.append("] was not found in the conifg file[").append(configFilePathString)
						.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}

			boolean isInactive = itemIDInfoManger.isDisabled(itemID, "", configSequencedProperties);
			if (!isInactive) {
				Object nativeValue = itemIDInfoManger.getNativeValueAfterValidChecker(itemKey,
						configSequencedProperties);

				try {
					commonPartConfiguration.mapping(itemKey, nativeValue);
				} catch (IllegalArgumentException | ClassCastException | CoddaConfigurationException e) {
					String errorMessage = new StringBuilder("fail to map item key[").append(itemKey)
							.append("]'s value[").append(itemValue)
							.append("] to the common part value object's variable in the conifg file[")
							.append(configFilePathString).append("]").toString();
					throw new CoddaConfigurationException(errorMessage);
				}
			} else {
				log.info("item key[{}]'s value is null because its status is inactive", itemKey);
			}

		}

		List<ItemIDInfo<?>> projectItemIDInfoList = itemIDInfoManger.getUnmodifiableProjectPartItemIDInfoList();

		/** main project part */
		for (ItemIDInfo<?> itemIDInfo : projectItemIDInfoList) {
			String itemID = itemIDInfo.getItemID();
			String itemKey = new StringBuilder("mainproject.").append(itemID).toString();
			String itemValue = configSequencedProperties.getProperty(itemKey);
			if (null == itemValue) {
				String errorMessage = new StringBuilder("the item key[").append(itemKey)
						.append("] was not found in the conifg file[").append(configFilePathString)
						.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}

			boolean isInactive = itemIDInfoManger.isDisabled(itemID, "mainproject.",
					configSequencedProperties);
			if (!isInactive) {
				Object nativeValue = itemIDInfoManger.getNativeValueAfterValidChecker(itemKey,
						configSequencedProperties);

				try {
					mainProjectPartConfiguration.mapping(itemKey, nativeValue);
				} catch (IllegalArgumentException | ClassCastException | CoddaConfigurationException e) {
					String errorMessage = new StringBuilder("fail to map item key[").append(itemKey)
							.append("]'s value[").append(itemValue)
							.append("] to the project part value object's variable in the conifg file[")
							.append(configFilePathString).append("]").toString();
					throw new CoddaConfigurationException(errorMessage);
				}
			} else {
				log.info("item key[{}]'s value is null becase its status is inactive", itemKey);
			}
		}

		for (String subProjectName : subProjectNameList) {
			String prefixOfItemID = new StringBuilder("subproject.").append(subProjectName).append(".").toString();

			ProjectPartConfiguration subProjectPartItems = new ProjectPartConfiguration(ProjectType.SUB,
					subProjectName);
			for (ItemIDInfo<?> itemIDInfo : projectItemIDInfoList) {
				String itemID = itemIDInfo.getItemID();
				String itemKey = new StringBuilder(prefixOfItemID).append(itemID).toString();
				String itemValue = configSequencedProperties.getProperty(itemKey);
				if (null == itemValue) {
					String errorMessage = new StringBuilder("the item key[").append(itemKey)
							.append("] was not found in the conifg file[").append(configFilePathString)
							.append("]").toString();
					throw new CoddaConfigurationException(errorMessage);
				}

				boolean isInactive = itemIDInfoManger.isDisabled(itemID, prefixOfItemID,
						configSequencedProperties);
				if (!isInactive) {
					Object nativeValue = itemIDInfoManger.getNativeValueAfterValidChecker(itemKey,
							configSequencedProperties);

					try {
						subProjectPartItems.mapping(itemKey, nativeValue);
					} catch (IllegalArgumentException | ClassCastException | CoddaConfigurationException e) {
						String errorMessage = new StringBuilder("fail to map item key[").append(itemKey)
								.append("]'s value[").append(itemValue)
								.append("] to the project part value object's variable in the conifg file[")
								.append(configFilePathString).append("]").toString();
						throw new CoddaConfigurationException(errorMessage);
					}
				} else {
					log.info("item key[{}]'s value is null becase its status is inactive", itemKey);
				}
			}

			allSubProjectPartConfiguration.addSubProjectPartValueObject(subProjectPartItems);
		}
	}

	/**
	 * 만약 서버 주소가 다르다면 새로운 서버 주소로 교체후 저장한다.
	 * 
	 * @param newServerHost
	 *            새로운 서버 호스트 주소
	 * @param newServerPort
	 *            새로운 서버 포트
	 * @throws IOException
	 *             저장시 에러 발생시 던지는 예외
	 */
	public void changeServerAddressIfDifferent(String newServerHost, int newServerPort) throws IOException {
		if (null == newServerHost) {
			throw new IllegalArgumentException("the parameter newServerHost is null");
		}

		if (newServerHost.equals("")) {
			throw new IllegalArgumentException("the parameter newServerHost is a empty string");
		}

		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(newServerHost)) {
			throw new IllegalArgumentException("the parameter newServerHost has any leading or tailing white space");
		}

		String oldSeverHost = mainProjectPartConfiguration.getServerHost();
		int oldServerPort = mainProjectPartConfiguration.getServerPort();

		if (newServerHost.equals(oldSeverHost) && newServerPort == oldServerPort) {
			return;
		}

		mainProjectPartConfiguration.changeServerAddress(newServerHost, newServerPort);

		String serverHostKey = new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_HOST_ITEMID).toString();
		configSequencedProperties.setProperty(serverHostKey, newServerHost);

		String serverPortKey = new StringBuilder("mainproject.")
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_PORT_ITEMID).toString();
		configSequencedProperties.setProperty(serverPortKey, String.valueOf(newServerPort));

		overwriteFile();
	}


	private void overwriteFile() throws IOException {
		SequencedPropertiesUtil.overwriteSequencedPropertiesFile(configSequencedProperties,
				getConfigPropertiesTitle(), configFilePathString,
				CommonStaticFinalVars.SOURCE_FILE_CHARSET);
	}

	public void applyModifiedConfigSequencedProperties() throws IOException, CoddaConfigurationException {
		initAllPartItems(mainProjectName);
		convertConfigSequencedPropertiesToAllPartItemsWithValidation(configSequencedProperties);
		overwriteFile();
	}

	// FIXME!
	public static void applyInstalledPath(String installedPathString, String mainProjectName)
			throws IOException, CoddaConfigurationException {
		String configFilePathString = ProjectBuildSytemPathSupporter
				.getProejctConfigFilePathString(installedPathString, mainProjectName);

		SequencedProperties configSequencedProperties = SequencedPropertiesUtil
				.loadSequencedPropertiesFile(configFilePathString, CommonStaticFinalVars.SOURCE_FILE_CHARSET);

		List<String> subProjectNameList = buildSubProjectNameList(configFilePathString,
				configSequencedProperties);

		List<String> dbcpNameList = buildDBCPNameList(configFilePathString, configSequencedProperties);

		ItemIDInfoManger itemIDInfoManger = ItemIDInfoManger.getInstance();

		/** common */
		List<ItemIDInfo<?>> commonPartItemIDInfoList = itemIDInfoManger
				.getUnmodifiableCommonPartItemIDInfoList();
		{
			for (ItemIDInfo<?> itemIDConfigInfo : commonPartItemIDInfoList) {
				String itemID = itemIDConfigInfo.getItemID();
				String itemKey = itemID;

				AbstractFileOrPathStringGetter fileOrPathStringGetter = itemIDInfoManger
						.getFileOrPathStringGetter(itemID);

				if (null != fileOrPathStringGetter) {
					String itemValue = fileOrPathStringGetter
							.getFileOrPathStringDependingOnInstalledPath(installedPathString, mainProjectName);

					configSequencedProperties.put(itemKey, itemValue);
				}
			}
		}

		/** dbcp */
		List<ItemIDInfo<?>> dbcpPartItemIDInfoList = itemIDInfoManger.getUnmodifiableDBCPPartItemIDInfoList();
		{
			for (String dbcpName : dbcpNameList) {
				String prefixOfItemID = new StringBuilder("dbcp.").append(dbcpName).append(".").toString();
				for (ItemIDInfo<?> itemIDConfigInfo : dbcpPartItemIDInfoList) {
					String itemID = itemIDConfigInfo.getItemID();
					String itemKey = new StringBuilder(prefixOfItemID).append(itemID).toString();

					AbstractFileOrPathStringGetter fileOrPathStringGetter = itemIDInfoManger
							.getFileOrPathStringGetter(itemID);

					if (null != fileOrPathStringGetter) {
						String itemValue = fileOrPathStringGetter.getFileOrPathStringDependingOnInstalledPath(
								installedPathString, mainProjectName, dbcpName);
						configSequencedProperties.put(itemKey, itemValue);
					}
				}
			}

		}

		/** main project */
		List<ItemIDInfo<?>> projectPartItemIDInfoList = itemIDInfoManger
				.getUnmodifiableProjectPartItemIDInfoList();
		{
			String prefixOfItemID = new StringBuilder("mainproject.").toString();
			for (ItemIDInfo<?> itemIDConfigInfo : projectPartItemIDInfoList) {

				String itemID = itemIDConfigInfo.getItemID();
				String itemKey = new StringBuilder(prefixOfItemID).append(itemID).toString();

				AbstractFileOrPathStringGetter fileOrPathStringGetter = itemIDInfoManger
						.getFileOrPathStringGetter(itemID);

				if (null != fileOrPathStringGetter) {
					String itemValue = fileOrPathStringGetter
							.getFileOrPathStringDependingOnInstalledPath(installedPathString, mainProjectName);

					configSequencedProperties.put(itemKey, itemValue);
				}
			}
		}

		/** sub project */
		{
			for (String subProjectName : subProjectNameList) {
				String prefixOfItemID = new StringBuilder("subproject.").append(subProjectName).append(".").toString();
				for (ItemIDInfo<?> itemIDConfigInfo : projectPartItemIDInfoList) {

					String itemID = itemIDConfigInfo.getItemID();
					String itemKey = new StringBuilder(prefixOfItemID).append(itemID).toString();

					AbstractFileOrPathStringGetter fileOrPathStringGetter = itemIDInfoManger
							.getFileOrPathStringGetter(itemID);

					if (null != fileOrPathStringGetter) {
						String itemValue = fileOrPathStringGetter.getFileOrPathStringDependingOnInstalledPath(
								installedPathString, mainProjectName);

						configSequencedProperties.put(itemKey, itemValue);
					}
				}
			}
		}

		SequencedPropertiesUtil.overwriteSequencedPropertiesFile(configSequencedProperties,
				getConfigPropertiesTitle(mainProjectName), configFilePathString,
				CommonStaticFinalVars.SOURCE_FILE_CHARSET);

	}

	private static List<String> buildDBCPNameList(String configFilePathString,
			SequencedProperties configSequencedProperties) throws CoddaConfigurationException {
		List<String> dbcpNameList = new ArrayList<>();
		Set<String> dbcpNameSet = new HashSet<>();
		{
			String itemValue = configSequencedProperties
					.getProperty(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING);
			if (null == itemValue) {
				String errorMessage = new StringBuilder("the dbcp name list key(=")
						.append(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING)
						.append(") was not found in the conifg file[").append(configFilePathString)
						.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}

			itemValue = itemValue.trim();

			if (!itemValue.equals("")) {
				StringTokenizer tokens = new StringTokenizer(itemValue, ",");

				while (tokens.hasMoreTokens()) {
					String token = tokens.nextToken();
					String dbcpName = token.trim();
					if (dbcpNameSet.contains(dbcpName)) {
						String errorMessage = new StringBuilder("dbcp name[").append(dbcpName)
								.append("] over at the dbcp name list of the conifg file[")
								.append(configFilePathString).append("]").toString();
						throw new CoddaConfigurationException(errorMessage);
					}

					dbcpNameList.add(dbcpName);
					dbcpNameSet.add(dbcpName);
				}
			}
		}
		return dbcpNameList;
	}

	private static List<String> buildSubProjectNameList(String configFilePathString,
			SequencedProperties configSequencedProperties) throws CoddaConfigurationException {
		List<String> subProjectNameList = new ArrayList<>();
		Set<String> subProjectNameSet = new HashSet<>();
		{
			String itemValue = configSequencedProperties
					.getProperty(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING);
			if (null == itemValue) {
				String errorMessage = new StringBuilder("the sub project name list key(=")
						.append(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)
						.append(") was not found in the conifg file[").append(configFilePathString)
						.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}

			itemValue = itemValue.trim();

			if (!itemValue.equals("")) {
				StringTokenizer tokens = new StringTokenizer(itemValue, ",");
				while (tokens.hasMoreTokens()) {
					String token = tokens.nextToken();
					String subProjectName = token.trim();
					if (subProjectNameSet.contains(subProjectName)) {
						String errorMessage = new StringBuilder("sub project name[").append(subProjectName)
								.append("] over at the project name list of the conifg file[")
								.append(configFilePathString).append("]").toString();
						throw new CoddaConfigurationException(errorMessage);
					}

					subProjectNameList.add(subProjectName);
					subProjectNameSet.add(subProjectName);
				}
			}
		}
		return subProjectNameList;
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

	public String getInstalledPathString() {
		return installedPathString;
	}

	public SequencedProperties getConfigurationSequencedPropties() {
		return configSequencedProperties;
	}

	public String getConfigPropertiesTitle() {
		return getConfigPropertiesTitle(mainProjectName);
	}

	public static String getConfigPropertiesTitle(String mainProjectName) {
		return new StringBuilder("project[").append(mainProjectName).append("]'s config file").toString();
	}
}
