package kr.pe.sinnori.common.config.itemidinfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import kr.pe.sinnori.common.config.AbstractDependOnInactiveChecker;
import kr.pe.sinnori.common.config.AbstractDependOnValidChecker;
import kr.pe.sinnori.common.config.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.dependoninactivechecker.RSAKeypairPathDependOnSourceInActiveChecker;
import kr.pe.sinnori.common.config.dependonvalidchecker.MinDependOnMaxValidChecker;
import kr.pe.sinnori.common.config.dependonvalidchecker.MybatisConfigFileRelativePathDependOnClassLoaderResourceAbsolutePathValidChecker;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo.ConfigurationPart;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.ConfigErrorException;
import kr.pe.sinnori.common.util.SequencedProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 신놀이 환경 설정 정보 클래스. 언어 종속적인 타입으로 변환할 정보, 특정 항목의 값에 영향을 받는 의존 관계 정보, 특정 항목의 특정
 * 값들에 의해서 비활성화 되는 정보를 구축한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class SinnoriItemIDInfoManger implements DBCPPartItemIDInfoMangerIF,
		CommonPartItemIDInfoMangerIF, ProjectPartItemIDInfoMangerIF {
	private Logger log = LoggerFactory.getLogger(SinnoriItemIDInfoManger.class);
	

	private List<ItemIDInfo<?>> itemIDInfoList = new ArrayList<ItemIDInfo<?>>();

	private Map<String, ItemIDInfo<?>> itemIDInfoHash = new HashMap<String, ItemIDInfo<?>>();

	private Map<String, AbstractDependOnInactiveChecker> inactiveCheckerHash = new HashMap<String, AbstractDependOnInactiveChecker>();
	private Map<String, AbstractDependOnValidChecker> validCheckerHash = new HashMap<String, AbstractDependOnValidChecker>();

	private List<ItemIDInfo<?>> dbcpPartItemIDInfoList = new ArrayList<ItemIDInfo<?>>();
	private List<ItemIDInfo<?>> commonPartItemIDInfoList = new ArrayList<ItemIDInfo<?>>();
	private List<ItemIDInfo<?>> projectPartItemIDInfoList = new ArrayList<ItemIDInfo<?>>();

	/** 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스 */
	private static final class SinnoriConfigItemIDInfoMangerHolder {
		static final SinnoriItemIDInfoManger singleton = new SinnoriItemIDInfoManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static SinnoriItemIDInfoManger getInstance() {
		return SinnoriConfigItemIDInfoMangerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자
	 */
	private SinnoriItemIDInfoManger() {
		try {
			DBCPPartItemIDInfoAdder.addAllDBCPPartItemIDInfo(this);
		} catch (IllegalArgumentException | ConfigErrorException e) {
			log.error(
					"fail to add all of dbcp part item identification informtion",
					e);
			System.exit(1);
		}
		try {
			CommonPartItemIDInfoAdder.addAllCommonPartItemIDInfo(this);
		} catch (IllegalArgumentException | ConfigErrorException e) {
			log.error(
					"fail to add all of common part item identification informtion",
					e);
			System.exit(1);
		}
		try {
			ProjectPartItemIDInfoAdder.addAllProjectPartItemIDInfo(this);
		} catch (IllegalArgumentException | ConfigErrorException e) {
			log.error(
					"fail to add all of project part item identification informtion",
					e);
			System.exit(1);
		}

		try {
			addValidChecker();
		} catch (IllegalArgumentException | ConfigErrorException e) {
			log.error("fail to add valid checker", e);
			System.exit(1);
		}
		try {
			addInactiveChecker();
		} catch (IllegalArgumentException | ConfigErrorException e) {
			log.error("fail to add inactive checker", e);
			System.exit(1);
		}
		
		itemIDInfoList = Collections
				.unmodifiableList(itemIDInfoList);		
		itemIDInfoHash = Collections.unmodifiableMap(itemIDInfoHash);
		
		inactiveCheckerHash = Collections.unmodifiableMap(inactiveCheckerHash);
		validCheckerHash = Collections.unmodifiableMap(validCheckerHash);
		
		dbcpPartItemIDInfoList = Collections
				.unmodifiableList(dbcpPartItemIDInfoList);
		commonPartItemIDInfoList = Collections
				.unmodifiableList(commonPartItemIDInfoList);
		projectPartItemIDInfoList = Collections
				.unmodifiableList(projectPartItemIDInfoList);
	}

	public ItemIDInfo<?> getItemIDInfo(String itemID) {
		return itemIDInfoHash.get(itemID);
	}

	private void addItemIDInfo(ItemIDInfo<?> itemIDInfo)
			throws IllegalArgumentException, UnsupportedOperationException {
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder(
					"the paramter itemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		ItemIDInfo<?> olditemIDConfigInfo = itemIDInfoHash.get(itemIDInfo
				.getItemID());
		if (null != olditemIDConfigInfo) {
			String errorMessage = new StringBuilder("the item id[")
					.append(itemIDInfo.getItemID()).append("] is registed")
					.toString();

			// log.warn(errorMessage);

			throw new IllegalArgumentException(errorMessage);
		}

		itemIDInfoHash.put(itemIDInfo.getItemID(), itemIDInfo);
		itemIDInfoList.add(itemIDInfo);

		ConfigurationPart itemConfigPart = itemIDInfo.getConfigurationPart();

		if (ItemIDInfo.ConfigurationPart.DBCP == itemConfigPart) {
			dbcpPartItemIDInfoList.add(itemIDInfo);
		} else if (ItemIDInfo.ConfigurationPart.COMMON == itemConfigPart) {
			commonPartItemIDInfoList.add(itemIDInfo);
		} else {
			projectPartItemIDInfoList.add(itemIDInfo);
		}
	}

	@Override
	public void addDBCPPartItemIDInfo(ItemIDInfo<?> itemIDInfo)
			throws IllegalArgumentException, UnsupportedOperationException {
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder(
					"the paramter itemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (!itemIDInfo.getConfigurationPart().equals(
				ItemIDInfo.ConfigurationPart.DBCP)) {
			String errorMessage = new StringBuilder("the parameter itemIDInfo[")
					.append(itemIDInfo.getItemID())
					.append("] is not a dbcp part item id").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		addItemIDInfo(itemIDInfo);
	}

	@Override
	public void addCommonPartItemIDInfo(ItemIDInfo<?> itemIDInfo)
			throws IllegalArgumentException, UnsupportedOperationException {
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder(
					"the paramter itemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!itemIDInfo.getConfigurationPart().equals(
				ItemIDInfo.ConfigurationPart.COMMON)) {
			String errorMessage = new StringBuilder("the parameter itemIDInfo[")
					.append(itemIDInfo.getItemID())
					.append("] is not a common part item id").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		addItemIDInfo(itemIDInfo);
	}

	@Override
	public void addProjectPartItemIDInfo(ItemIDInfo<?> itemIDInfo)
			throws IllegalArgumentException, UnsupportedOperationException {
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder(
					"the paramter itemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!itemIDInfo.getConfigurationPart().equals(
				ItemIDInfo.ConfigurationPart.PROJECT)) {
			String errorMessage = new StringBuilder("the parameter itemIDInfo[")
					.append(itemIDInfo.getItemID())
					.append("] is not a project common part item id")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		addItemIDInfo(itemIDInfo);
	}

	@SuppressWarnings("unchecked")
	private void addValidChecker() throws IllegalArgumentException,
			ConfigErrorException {
		{
			String dependentTargetItemID = ItemID.ProjectPartItemID.CLIENT_MONITOR_RECEPTION_TIMEOUT_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			String dependentSourceItemID = ItemID.ProjectPartItemID.CLIENT_CONNECTION_SOCKET_TIMEOUT_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			
			validCheckerHash.put(dependentSourceItemID,
					new MinDependOnMaxValidChecker<Long>(
							(ItemIDInfo<Long>) dependentSourceitemIDConfigInfo,
							(ItemIDInfo<Long>) dependentTargetItemIDInfo,
							
							Long.class));
		}
		{
			String dependentTargetItemID = ItemID.ProjectPartItemID.CLIENT_ASYN_OUTPUT_MESSAGE_READER_MAX_SIZE_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			String dependentSourceItemID = ItemID.ProjectPartItemID.CLIENT_ASYN_OUTPUT_MESSAGE_READER_SIZE_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			validCheckerHash
					.put(dependentSourceItemID,
							new MinDependOnMaxValidChecker<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}
		{
			String dependentTargetItemID = ItemID.ProjectPartItemID.CLIENT_ASYN_INPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			String dependentSourceItemID = ItemID.ProjectPartItemID.CLIENT_ASYN_INPUT_MESSAGE_WRITER_SIZE_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			validCheckerHash
					.put(dependentSourceItemID,
							new MinDependOnMaxValidChecker<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}

		{
			String dependentTargetItemID = ItemID.ProjectPartItemID.SERVER_POOL_ACCEPT_PROCESSOR_MAX_SIZE_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			String dependentSourceItemID = ItemID.ProjectPartItemID.SERVER_POOL_ACCEPT_PROCESSOR_SIZE_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			validCheckerHash
					.put(dependentSourceItemID,
							new MinDependOnMaxValidChecker<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}
		{
			String dependentTargetItemID = ItemID.ProjectPartItemID.SERVER_POOL_INPUT_MESSAGE_READER_MAX_SIZE_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			String dependentSourceItemID = ItemID.ProjectPartItemID.SERVER_POOL_INPUT_MESSAGE_READER_SIZE_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			validCheckerHash
					.put(dependentSourceItemID,
							new MinDependOnMaxValidChecker<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}
		{
			String dependentTargetItemID = ItemID.ProjectPartItemID.SERVER_POOL_EXECUTOR_PROCESSOR_MAX_SIZE_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			String dependentSourceItemID = ItemID.ProjectPartItemID.SERVER_POOL_EXECUTOR_PROCESSOR_SIZE_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			validCheckerHash
					.put(dependentSourceItemID,
							new MinDependOnMaxValidChecker<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}
		{
			String dependentTargetItemID = ItemID.ProjectPartItemID.SERVER_POOL_OUTPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			String dependentSourceItemID = ItemID.ProjectPartItemID.SERVER_POOL_OUTPUT_MESSAGE_WRITER_SIZE_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			validCheckerHash
					.put(dependentSourceItemID,
							new MinDependOnMaxValidChecker<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}
		{
			String dependentTargetItemID = ItemID.ProjectPartItemID.SERVER_CLASSLOADER_APPINF_PATH_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			String dependentSourceItemID = ItemID.ProjectPartItemID.SERVER_CLASSLOADER_MYBATIS_CONFIG_FILE_RELATIVE_PATH_STRING_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentSourceItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			validCheckerHash
					.put(dependentSourceItemID,
							new MybatisConfigFileRelativePathDependOnClassLoaderResourceAbsolutePathValidChecker(
									(ItemIDInfo<String>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<File>) dependentTargetItemIDInfo));
		}
	}

	private void addInactiveChecker() throws IllegalArgumentException,
			ConfigErrorException {
		{
			String dependentSourceItemID = ItemID.CommonPartItemID.SESSIONKEY_RSA_KEYPAIR_PATH_ITEMID;
			String dependentTargetItemID = ItemID.CommonPartItemID.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID;

			ItemIDInfo<?> dependentSourceItemIDInfo = getItemIDInfo(dependentSourceItemID);
			if (null == dependentSourceItemIDInfo) {
				String errorMessage = new StringBuilder(
						"the dependent source item identification[")
						.append(dependentSourceItemID)
						.append("] information is not ready").toString();
				throw new ConfigErrorException(errorMessage);
			}

			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"the dependent target item identification[")
						.append(dependentTargetItemID)
						.append("] information is not ready").toString();
				// log.error(errorMessage);
				throw new ConfigErrorException(errorMessage);
			}

			inactiveCheckerHash
					.put(dependentSourceItemID,
							new RSAKeypairPathDependOnSourceInActiveChecker(
									dependentSourceItemIDInfo,
									dependentTargetItemIDInfo,
									new String[] { CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.API
											.toString() }));
		}

	}

	/**
	 * 구축한 항목 식별자 정보를 바탕으로 주어진 메인 프로젝트 이름과 설치 경로에 맞도록 신규 생성된 신놀이 설정 시퀀스 프로퍼티를
	 * 반환한다.
	 * 
	 * @param mainProjectName
	 *            메인 프로젝트 이름
	 * @param sinnoriInstalledPathString
	 *            신놀이 설치 경로
	 * @return 구축한 항목 식별자 정보를 바탕으로 주어진 메인 프로젝트 이름과 설치 경로에 맞도록 신규 생성된 신놀이 설정 시퀀스
	 *         프로퍼티
	 */
	public SequencedProperties getNewSinnoriConfigSequencedProperties(
			String mainProjectName, String sinnoriInstalledPathString) {
		SequencedProperties sinnoriConfigSequencedProperties = getNewSinnoriConfigSequencedProperties(mainProjectName);

		updateAllOfDefalutValueDependingOnSinnoriInstalledPath(mainProjectName,
				sinnoriInstalledPathString, sinnoriConfigSequencedProperties, null);

		return sinnoriConfigSequencedProperties;
	}

	private SequencedProperties getNewSinnoriConfigSequencedProperties(
			String mainProjectName) {
		SequencedProperties sinnoriConfigSequencedProperties = new SequencedProperties();
		/** DBCP */
		{
			sinnoriConfigSequencedProperties.setProperty(
					CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING,
					"");
		}

		/** common */
		{
			String prefixOfItemID = "";
			for (ItemIDInfo<?> itemIDConfigInfo : commonPartItemIDInfoList) {				
				String itemKey = itemIDConfigInfo.getItemID();

				String itemDescKey = itemIDConfigInfo
						.getItemDescKey(prefixOfItemID);

				// FIXME!
				// log.info("itemKey=[{}], itemDescKey=[{}]", itemKey, itemDescKey);

				sinnoriConfigSequencedProperties.put(itemDescKey,
						itemIDConfigInfo.getDescription());
				sinnoriConfigSequencedProperties.put(itemKey,
						itemIDConfigInfo.getDefaultValue());
			}
		}
		

		/** main project */
		{
			String prefixOfItemID = new StringBuilder("mainproject.").toString();
			for (ItemIDInfo<?> itemIDConfigInfo : projectPartItemIDInfoList) {
				

				String itemKey = new StringBuilder(prefixOfItemID).append(
						itemIDConfigInfo.getItemID()).toString();

				String itemDescKey = itemIDConfigInfo
						.getItemDescKey(prefixOfItemID);

				// FIXME!
				// log.info("itemKey=[{}], itemDescKey=[{}]", itemKey,
				// itemDescKey);

				sinnoriConfigSequencedProperties.put(itemDescKey,
						itemIDConfigInfo.getDescription());
				sinnoriConfigSequencedProperties.put(itemKey,
						itemIDConfigInfo.getDefaultValue());
			}
		}
		
		
		/** sub project */
		{
			sinnoriConfigSequencedProperties.setProperty(
					CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING,
					"");

			
		}

		return sinnoriConfigSequencedProperties;
	}

	public void updateAllOfDefalutValueDependingOnSinnoriInstalledPath(
			String mainProjectName, String sinnoriInstalledPathString,
			SequencedProperties sinnoriConfigSequencedProperties, String prefixOfItemID)
			throws IllegalArgumentException {

		@SuppressWarnings("unchecked")
		Enumeration<String> itemKeys = sinnoriConfigSequencedProperties.keys();
		while (itemKeys.hasMoreElements()) {
			String itemKey = itemKeys.nextElement();
			String oldItemValue = sinnoriConfigSequencedProperties
					.getProperty(itemKey);
			/**
			 * 신규 추가된 DBCP 혹은 부 프로젝트 파트로 한정하기 위한 로직
			 */
			if (null != prefixOfItemID) {
				if (itemKey.indexOf(prefixOfItemID) != 0) continue;
			}
			
			ItemIDInfo<?> itemIDInfo = getItemIDInfoFromKey(itemKey, null, null);

			if (null != itemIDInfo) {
				String itemID = itemIDInfo.getItemID();

				if (itemID
						.equals(ItemID.ProjectPartItemID.COMMON_MESSAGE_INFO_XMLPATH_ITEMID)) {
					String newItemValue = BuildSystemPathSupporter
							.getMessageInfoPathString(mainProjectName,
									sinnoriInstalledPathString);
					log.info("itemKey[{}] old value[{}] to new value[{}]",
							itemKey, oldItemValue, newItemValue);
					sinnoriConfigSequencedProperties.put(itemKey, newItemValue);
				} else if (itemID
						.equals(ItemID.ProjectPartItemID.SERVER_CLASSLOADER_APPINF_PATH_ITEMID)) {
					String newItemValue = BuildSystemPathSupporter
							.getAPPINFPathString(mainProjectName,
									sinnoriInstalledPathString);
					log.info("itemKey[{}] old value[{}] to new value[{}]",
							itemKey, oldItemValue, newItemValue);
					sinnoriConfigSequencedProperties.put(itemKey, newItemValue);
				} else if (itemID
						.equals(ItemID.DBCPPartItemID.DBCP_CONFIGE_FILE_ITEMID)) {
					int startIndex = itemKey.indexOf("dbcp.");
					int endIndex = itemKey.lastIndexOf(itemID);
					String dbcpName = null;

					try {
						dbcpName = itemKey.substring(startIndex, endIndex - 1);
					} catch (Exception e) {
						log.error("itemKey=[{}], startIndex={}, endIndex={}");
						System.exit(1);
					}

					String newItemValue = BuildSystemPathSupporter
							.getDBCPConfigFilePathString(mainProjectName,
									sinnoriInstalledPathString, dbcpName);
					log.info("itemKey[{}] old value[{}] to new value[{}]",
							itemKey, oldItemValue, newItemValue);
					sinnoriConfigSequencedProperties.put(itemKey, newItemValue);
				} else if (itemID
						.equals(ItemID.CommonPartItemID.SESSIONKEY_RSA_KEYPAIR_PATH_ITEMID)) {

					String newItemValue = BuildSystemPathSupporter
							.getSessionKeyRSAKeypairPathString(mainProjectName,
									sinnoriInstalledPathString);
					log.info("itemKey[{}] old value[{}] to new value[{}]",
							itemKey, oldItemValue, newItemValue);
					sinnoriConfigSequencedProperties.put(itemKey, newItemValue);
				}
			}
		}
	}

	public boolean isInactive(String dependentSourceItemKey,
			Properties sourceProperties) throws IllegalArgumentException {
		boolean isInactive = false;
		ItemIDInfo<?> itemIDInfo = getItemIDInfoFromKey(dependentSourceItemKey,
				null, null);

		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder(
					"fail to get itemID from itemKey because parameter dependentSourceItemKey[")
					.append(dependentSourceItemKey).append("] is bad")
					.toString();

			// log.warn(errorMessage);

			throw new IllegalArgumentException(errorMessage);
		}

		String itemID = itemIDInfo.getItemID();
		int inx = dependentSourceItemKey.indexOf(itemID);
		String prefixOfItemID = dependentSourceItemKey.substring(0, inx);

		AbstractDependOnInactiveChecker inactiveChecker = inactiveCheckerHash
				.get(itemID);

		if (null != inactiveChecker) {
			isInactive = inactiveChecker.isInactive(sourceProperties,
					prefixOfItemID);
		}
		return isInactive;
	}

	public ItemIDInfo<?> getItemIDInfoFromKey(String itemKey,
			Set<String> dbcpNameList, Set<String> projectNameList)
			throws IllegalArgumentException {
		if (null == itemKey) {
			String errorMessage = "the parameter itemKey is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemKey.equals(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING)
				|| itemKey
						.equals(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)) {
			return null;
		}

		if (itemKey.lastIndexOf(".desc") + ".desc".length() == itemKey.length()) {
			return null;
		}

		ItemIDInfo<?> itemIDInfo = null;
		String itemID = null;

		StringTokenizer stringTokenizer = new StringTokenizer(itemKey, ".");

		if (!stringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder(
					"first token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		String firstToken = stringTokenizer.nextToken();

		if (firstToken.equals("mainproject")) {
			if (!stringTokenizer.hasMoreTokens()) {
				String errorMessage = new StringBuilder(
						"second token does not exist in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			String subPartName = stringTokenizer.nextToken();
			if (subPartName.equals("")) {
				String errorMessage = new StringBuilder(
						"subPartName is a empty string in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (!subPartName.equals("common") && !subPartName.equals("client")
					&& !subPartName.equals("server")) {
				String errorMessage = new StringBuilder("the sub part[")
						.append(subPartName)
						.append("] of the parameter itemKey[")
						.append(itemKey)
						.append("]'s itemID is bad, it must have one element of set {'common', 'client', 'server'}")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (!stringTokenizer.hasMoreTokens()) {
				String errorMessage = new StringBuilder(
						"third token does not exist in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			String thirdToken = stringTokenizer.nextToken();
			if (thirdToken.equals("")) {
				String errorMessage = new StringBuilder(
						"third Token is a empty string in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			String prefixOfItemID = firstToken + ".";
			itemID = itemKey.substring(prefixOfItemID.length());

			itemIDInfo = itemIDInfoHash.get(itemID);
			if (null == itemIDInfo) {
				String errorMessage = new StringBuilder(
						"the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not registed, check it")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			ItemIDInfo.ConfigurationPart configPart = itemIDInfo
					.getConfigurationPart();
			if (!configPart.equals(ItemIDInfo.ConfigurationPart.PROJECT)) {
				String errorMessage = new StringBuilder(
						"the configuration part[")
						.append(configPart.toString())
						.append("] of the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not a project part").toString();
				throw new IllegalArgumentException(errorMessage);
			}			
		} else if (firstToken.equals("subproject")) {
			/** project part */
			if (!stringTokenizer.hasMoreTokens()) {
				String errorMessage = new StringBuilder(
						"second token does not exist in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			String projectName = stringTokenizer.nextToken();
			if (projectName.equals("")) {
				String errorMessage = "project name is an empty string at the project part of the parameter sinnoriConfigSequencedProperties";
				throw new IllegalArgumentException(errorMessage);
			}

			if (null != projectNameList) {
				if (!projectNameList.contains(projectName)) {
					String errorMessage = new StringBuilder("the item key[")
							.append(itemKey)
							.append("] has a wrong project name not existing in the parameter projectNameList[")
							.append(projectNameList.toString()).append("]")
							.toString();
					throw new IllegalArgumentException(errorMessage);
				}
			}

			if (!stringTokenizer.hasMoreTokens()) {
				String errorMessage = new StringBuilder(
						"third token does not exist in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			String subPartName = stringTokenizer.nextToken();
			if (subPartName.equals("")) {
				String errorMessage = new StringBuilder(
						"subPartName is a empty string in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (!subPartName.equals("common") && !subPartName.equals("client")
					&& !subPartName.equals("server")) {
				String errorMessage = new StringBuilder("the sub part[")
						.append(subPartName)
						.append("] of the parameter itemKey[")
						.append(itemKey)
						.append("]'s itemID is bad, it must have one element of set {'common', 'client', 'server'}")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (!stringTokenizer.hasMoreTokens()) {
				String errorMessage = new StringBuilder(
						"fourth token does not exist in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			String fourthToken = stringTokenizer.nextToken();
			if (fourthToken.equals("")) {
				String errorMessage = new StringBuilder(
						"fourth Token is a empty string in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			String prefixOfItemID = firstToken + "." + projectName + ".";
			itemID = itemKey.substring(prefixOfItemID.length());

			itemIDInfo = itemIDInfoHash.get(itemID);
			if (null == itemIDInfo) {
				String errorMessage = new StringBuilder(
						"the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not registed, check it")
						.toString();

				/*
				 * log.info(
				 * "item id is not registed, itemKey=[{}], prefixOfItemID=[{}], itemI=[{}]"
				 * , itemKey, prefixOfItemID, itemID);
				 */

				throw new IllegalArgumentException(errorMessage);
			}

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			ItemIDInfo.ConfigurationPart configPart = itemIDInfo
					.getConfigurationPart();
			if (!configPart.equals(ItemIDInfo.ConfigurationPart.PROJECT)) {
				String errorMessage = new StringBuilder(
						"the configuration part[")
						.append(configPart.toString())
						.append("] of the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not a project part").toString();
				throw new IllegalArgumentException(errorMessage);
			}

		} else if (firstToken.equals("dbcp")) {
			/** dbcp part */
			if (!stringTokenizer.hasMoreTokens()) {
				String errorMessage = new StringBuilder(
						"second token does not exist in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			String dbcpName = stringTokenizer.nextToken();

			if (null != dbcpNameList) {
				if (!dbcpNameList.contains(dbcpName)) {
					String errorMessage = new StringBuilder("the item key[")
							.append(itemKey)
							.append("] has a wrong dbcp name not existing in the parameter dbcpNameList[")
							.append(dbcpNameList.toString()).append("]")
							.toString();
					throw new IllegalArgumentException(errorMessage);
				}
			}

			if (!stringTokenizer.hasMoreTokens()) {
				String errorMessage = new StringBuilder(
						"third token does not exist in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			String thirdToken = stringTokenizer.nextToken();
			if (thirdToken.equals("")) {
				String errorMessage = new StringBuilder(
						"third token is a empty string in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			String prefixOfItemID = firstToken + "." + dbcpName + ".";
			itemID = itemKey.substring(prefixOfItemID.length());

			itemIDInfo = itemIDInfoHash.get(itemID);
			if (null == itemIDInfo) {
				String errorMessage = new StringBuilder(
						"the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not registed, check it")
						.toString();

				/*
				 * log.info(
				 * "item id is not registed, itemKey=[{}], prefixOfItemID=[{}], itemI=[{}]"
				 * , itemKey, prefixOfItemID, itemID);
				 */

				throw new IllegalArgumentException(errorMessage);
			}

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			ItemIDInfo.ConfigurationPart configPart = itemIDInfo
					.getConfigurationPart();
			if (!configPart.equals(ItemIDInfo.ConfigurationPart.DBCP)) {
				String errorMessage = new StringBuilder(
						"the configuration part[")
						.append(configPart.toString())
						.append("] of the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not a dbcp part").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		} else {
			/** common part */
			if (!stringTokenizer.hasMoreTokens()) {
				String errorMessage = new StringBuilder(
						"second token does not exist in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			String secondToken = stringTokenizer.nextToken();
			if (secondToken.equals("")) {
				String errorMessage = new StringBuilder(
						"second token is a empty string in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			itemID = itemKey;

			itemIDInfo = itemIDInfoHash.get(itemID);
			if (null == itemIDInfo) {
				String errorMessage = new StringBuilder(
						"the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not registed, check it")
						.toString();

				/*
				 * log.info(
				 * "item id is not registed, itemKey=[{}], prefixOfItemID=[{}], itemI=[{}]"
				 * , itemKey, "", itemID);
				 */

				throw new IllegalArgumentException(errorMessage);
			}

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			ItemIDInfo.ConfigurationPart configPart = itemIDInfo
					.getConfigurationPart();
			if (!configPart.equals(ItemIDInfo.ConfigurationPart.COMMON)) {
				String errorMessage = new StringBuilder(
						"the configuration part[")
						.append(configPart.toString())
						.append("] of the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not a common part").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		return itemIDInfo;
	}

	public Object getNativeValueAfterValidChecker(String itemValueKey,
			Properties sourceProperties) throws IllegalArgumentException,
			ConfigErrorException {
		if (null == itemValueKey) {
			throw new IllegalArgumentException("parameter itemValueKey is null");
		}
		if (null == sourceProperties) {
			throw new IllegalArgumentException(
					"parameter sourceProperties is null");
		}

		ItemIDInfo<?> itemIDInfo = getItemIDInfoFromKey(itemValueKey, null,
				null);
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder(
					"parameter dependentSourceKey[").append(itemValueKey)
					.append("] is bad, itemID is null").toString();

			log.warn(errorMessage);

			throw new ConfigErrorException(errorMessage);
		}

		String itemID = itemIDInfo.getItemID();
		int inx = itemValueKey.indexOf(itemID);
		String prefixOfItemID = itemValueKey.substring(0, inx);

		AbstractDependOnValidChecker dependOnValidCheck = validCheckerHash
				.get(itemID);

		if (null != dependOnValidCheck) {

			try {
				boolean isValid = dependOnValidCheck.isValid(sourceProperties,
						prefixOfItemID);
				if (!isValid) {
					String errorMessage = new StringBuilder(
							"the dependent source item")
							.append(prefixOfItemID)
							.append(dependOnValidCheck
									.getDependentSourceItemID())
							.append("] doesn't depend on the dependent target item[")
							.append(prefixOfItemID)
							.append(dependOnValidCheck
									.getDependentTargetItemID()).append("]")
							.toString();

					// log.warn(errorMessage);

					throw new ConfigErrorException(errorMessage);
				}
			} catch (IllegalArgumentException e) {
				String errorMessage = new StringBuilder(
						"the parameter itemValueKey[").append(itemValueKey)
						.append("]'s invalid check fails errrorMessage=")
						.append(e.getMessage()).toString();
				/** 다른 예외로 변환 되므로 이력 남긴다. */
				log.debug(errorMessage, e);

				throw new ConfigErrorException(errorMessage);
			}
		}

		Object itemNativeValue = null;
		String itemValue = sourceProperties.getProperty(itemValueKey);
		try {
			itemNativeValue = itemIDInfo.getItemValueConverter().valueOf(
					itemValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"fail to convert the parameter itemValueKey[")
					.append(itemValueKey).append("]'s value[")
					.append(sourceProperties.getProperty(itemValueKey))
					.append("] to a native value errrorMessage=")
					.append(e.getMessage()).toString();
			/** 다른 예외로 변환 되므로 이력 남긴다. */
			log.debug(errorMessage, e);

			throw new ConfigErrorException(e.getMessage());
		}

		return itemNativeValue;
	}

	public List<ItemIDInfo<?>> getUnmodifiableDBCPPartItemIDInfoList() {		
		return dbcpPartItemIDInfoList;
	}

	public List<ItemIDInfo<?>> getUnmodifiableCommonPartItemIDInfoList() {		
		return commonPartItemIDInfoList;
	}

	public List<ItemIDInfo<?>> getUnmodifiableProjectPartItemIDInfoList() {		
		return projectPartItemIDInfoList;
	}
}
