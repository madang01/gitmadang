/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.sinnori.common.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.config.valueobject.AllDBCPPart;
import kr.pe.sinnori.common.config.valueobject.AllSubProjectPart;
import kr.pe.sinnori.common.config.valueobject.CommonPart;
import kr.pe.sinnori.common.config.valueobject.DBCPPart;
import kr.pe.sinnori.common.config.valueobject.ProjectPart;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.ConfigErrorException;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.common.util.SequencedPropertiesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 신놀이 환경 변수에 대응하는 값에 접근하기 위한 클래스
 * 
 * @author Won Jonghoon
 * 
 */
public final class SinnoriConfigurationManager {
	private Logger log = LoggerFactory
			.getLogger(SinnoriConfigurationManager.class);

	private String sinnoriRunningProjectName = null;
	private String sinnoriInstalledPathString = null;

	private AllDBCPPart allDBCPPart = null;
	private CommonPart commonPart = null;
	private ProjectPart mainProjectPart = null;
	private AllSubProjectPart allSubProjectPart = null;
	

	/** 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스 */
	private static final class SinnoriConfigHolder {
		static final SinnoriConfigurationManager singleton = new SinnoriConfigurationManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static SinnoriConfigurationManager getInstance() {
		return SinnoriConfigHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자
	 */
	private SinnoriConfigurationManager() {
		String sinnoriRunningProjectName = System
				.getProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME);
		String sinnoriInstalledPathString = System
				.getProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH);

		log.info(
				"java system proprties -D[{}]=[{}], -D[{}]=[{}]",
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				sinnoriRunningProjectName,
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);

		if (null == sinnoriRunningProjectName) {
			log.error("the java system proprties key[{}] was not defined", CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME);
			System.exit(1);
		}
		
		if (sinnoriRunningProjectName.equals("")) {
			log.error("the java system proprties key[{}]'s value is a empty string", 
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME);
			System.exit(1);
		}
		
		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(sinnoriRunningProjectName)) {
			log.error("the java system proprties key[{}]'s value has leading or tailing white space(s)", 
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME);
			System.exit(1);
		}

		if (null == sinnoriInstalledPathString) {
			log.error(
					"the java system properties variable '{}' was not defined",
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH);

			System.exit(1);
		}
		
		this.sinnoriRunningProjectName = sinnoriRunningProjectName;
		this.sinnoriInstalledPathString = sinnoriInstalledPathString;
		this.commonPart = new CommonPart();
		this.mainProjectPart = new ProjectPart(sinnoriRunningProjectName);
		
		boolean reloadResult = reload();
		if (!reloadResult) {
			String errorMessage = new StringBuilder(
					"fail to (re)load the sinnori conifg file, check log").toString();
			log.error(errorMessage);
			System.exit(1);
		}
	}
	
	public boolean isValidSinnoriConfigurationFile(String sinnoriConfigFilePathString) {
		SequencedProperties sinnoriConfigSequencedProperties = null;
		try {
			sinnoriConfigSequencedProperties = SequencedPropertiesUtil
					.getSequencedPropertiesFromFile(sinnoriConfigFilePathString);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder(
					"fail to load the sinnori conifg file[")
					.append(sinnoriConfigFilePathString).append("]").toString();
			log.warn(errorMessage, e);
			return false;
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to load the sinnori conifg file[")
					.append(sinnoriConfigFilePathString).append("]").toString();
			log.warn(errorMessage, e);
			return false;
		}
		
		
		List<String> subProjectNameList = new ArrayList<>();
		Set<String> subProjectNameSet = new HashSet<>();
		
		List<String> dbcpNameList = new ArrayList<>();
		Set<String> dbcpNameSet = new HashSet<>();
		
		{
			String itemValue = sinnoriConfigSequencedProperties
					.getProperty(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING);
			if (null == itemValue) {
				String errorMessage = new StringBuilder("the sub project name list key(=")
				.append(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)
				.append(") was not found in the sinnori conifg file[")
					.append(sinnoriConfigFilePathString).append("]").toString();
				log.warn(errorMessage);
				return false;
			}
			
			itemValue = itemValue.trim();
			
			if (!itemValue.equals("")) {
				StringTokenizer tokens = new StringTokenizer(itemValue, ",");
				while (tokens.hasMoreTokens()) {
					String token = tokens.nextToken();
					String subProjectName = token.trim();
					if (subProjectNameSet.contains(subProjectName)) {
						String errorMessage = new StringBuilder("sub project name[")
								.append(subProjectName)
								.append("] over at the sub project name list of the sinnori conifg file[")
								.append(sinnoriConfigFilePathString).append("]").toString();
						log.warn(errorMessage);
						return false;
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
				String errorMessage = new StringBuilder("the dbcp name list key(=")
				.append(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING)
				.append(") was not found in the sinnori conifg file[")
					.append(sinnoriConfigFilePathString).append("]").toString();
				log.warn(errorMessage);
				return false;
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
								.append(sinnoriConfigFilePathString).append("]").toString();
						log.warn(errorMessage);
						return false;
					}
					
					dbcpNameList.add(dbcpName);
					dbcpNameSet.add(dbcpName);
				}
			}
		}
		
		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger
				.getInstance();
		/**
		 * 설정 프로퍼티 파일의 항목 키 전체가 올바른지 검사하기
		 * check item key invalidation by item in the config file
		 */
		@SuppressWarnings("unchecked")
		Enumeration<String> itemKeys = sinnoriConfigSequencedProperties.keys();
		while (itemKeys.hasMoreElements()) {
			String itemKey = itemKeys.nextElement();
			
			if (itemKey.equals(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)
					|| itemKey.equals(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)) {
				continue;
			}
			
			@SuppressWarnings("unused")
			ItemIDInfo<?> itemIDInfo = null;
			try {
				itemIDInfo = sinnoriItemIDInfoManger.getItemIDInfoFromKey(itemKey, dbcpNameSet, subProjectNameSet);
			} catch(IllegalArgumentException e) {
				String errorMessage = new StringBuilder("the item key[")
				.append(itemKey)
				.append("] of the sinnori conifg file[")
				.append(sinnoriConfigFilePathString).append("] is wrong").toString();
				log.warn(errorMessage, e);
				return false;
			}
		}		
				
		List<ItemIDInfo<?>> dbcpItemIDInfoList = sinnoriItemIDInfoManger.getUnmodifiableDBCPPartItemIDInfoList();
		for (String dbcpName : dbcpNameList) {
			for (ItemIDInfo<?> itemIDInfo : dbcpItemIDInfoList) {
				String itemKey = new StringBuilder("dbcp.").append(dbcpName)
						.append(".").append(itemIDInfo.getItemID()).toString();
				String itemValue = sinnoriConfigSequencedProperties.getProperty(itemKey);
				if (null == itemValue) {
					String errorMessage = new StringBuilder(
							"the item key[")
							.append(itemKey)
							.append("] was not found in the sinnori conifg file[")
							.append(sinnoriConfigFilePathString).append("]")
							.toString();
					log.warn(errorMessage);
					return false;
				}
			}
		}	
				
		List<ItemIDInfo<?>> commonItemIDInfoList = sinnoriItemIDInfoManger.getUnmodifiableCommonPartItemIDInfoList();
		for (ItemIDInfo<?> itemIDInfo : commonItemIDInfoList) {
			String itemKey = itemIDInfo.getItemID();
			String itemValue = sinnoriConfigSequencedProperties.getProperty(itemKey);
			if (null == itemValue) {
				String errorMessage = new StringBuilder(
						"the item key[")
						.append(itemKey)
						.append("] was not found in the sinnori conifg file[")
						.append(sinnoriConfigFilePathString).append("]")
						.toString();
				log.warn(errorMessage);
				return false;
			}
			
		}
		
		
		List<ItemIDInfo<?>> projectItemIDInfoList = sinnoriItemIDInfoManger.getUnmodifiableProjectPartItemIDInfoList();
		
		/** main project part */
		for (ItemIDInfo<?> itemIDInfo : projectItemIDInfoList) {
			String itemKey = new StringBuilder("mainproject.")
			.append(itemIDInfo.getItemID()).toString();
			String itemValue = sinnoriConfigSequencedProperties.getProperty(itemKey);
			if (null == itemValue) {
				String errorMessage = new StringBuilder(
						"the item key[")
						.append(itemKey)
						.append("] was not found in the sinnori conifg file[")
						.append(sinnoriConfigFilePathString).append("]")
						.toString();
				log.warn(errorMessage);
				return false;
			}
		}		
		
		for (String subProjectName : subProjectNameList) {
			for (ItemIDInfo<?> itemIDInfo : projectItemIDInfoList) {
				String itemKey = new StringBuilder("subproject.").append(subProjectName)
						.append(".").append(itemIDInfo.getItemID()).toString();
				String itemValue = sinnoriConfigSequencedProperties.getProperty(itemKey);
				if (null == itemValue) {
					String errorMessage = new StringBuilder(
							"the item key[")
							.append(itemKey)
							.append("] was not found in the sinnori conifg file[")
							.append(sinnoriConfigFilePathString).append("]")
							.toString();
					log.warn(errorMessage);
					return false;
				}
			}
		}
		
		
		return true;
	}

	public boolean reload() {
		String sinnoriConfigFilePathString = BuildSystemPathSupporter
				.getSinnoriConfigFilePathString(sinnoriRunningProjectName,
						sinnoriInstalledPathString);

		SequencedProperties sinnoriConfigSequencedProperties = null;
		try {
			sinnoriConfigSequencedProperties = SequencedPropertiesUtil
					.getSequencedPropertiesFromFile(sinnoriConfigFilePathString);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder(
					"fail to load the sinnori conifg file[")
					.append(sinnoriConfigFilePathString).append("]").toString();
			log.warn(errorMessage, e);
			return false;
		} catch (IOException e) {
			String errorMessage = new StringBuilder(
					"fail to load the sinnori conifg file[")
					.append(sinnoriConfigFilePathString).append("]").toString();
			log.warn(errorMessage, e);
			return false;
		}

		List<String> subProjectNameList = new ArrayList<>();
		Set<String> subProjectNameSet = new HashSet<>();
		
		List<String> dbcpNameList = new ArrayList<>();
		Set<String> dbcpNameSet = new HashSet<>();
		
		{
			String itemValue = sinnoriConfigSequencedProperties
					.getProperty(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING);
			if (null == itemValue) {
				String errorMessage = new StringBuilder("the sub project name list key(=")
				.append(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)
				.append(") was not found in the sinnori conifg file[")
					.append(sinnoriConfigFilePathString).append("]").toString();
				log.warn(errorMessage);
				return false;
			}
			
			itemValue = itemValue.trim();
			
			if (!itemValue.equals("")) {
				StringTokenizer tokens = new StringTokenizer(itemValue, ",");
				while (tokens.hasMoreTokens()) {
					String token = tokens.nextToken();
					String subProjectName = token.trim();
					if (subProjectNameSet.contains(subProjectName)) {
						String errorMessage = new StringBuilder("sub project name[")
								.append(subProjectName)
								.append("] over at the project name list of the sinnori conifg file[")
								.append(sinnoriConfigFilePathString).append("]").toString();
						log.warn(errorMessage);
						return false;
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
				String errorMessage = new StringBuilder("the dbcp name list key(=")
				.append(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING)
				.append(") was not found in the sinnori conifg file[")
					.append(sinnoriConfigFilePathString).append("]").toString();
				log.warn(errorMessage);
				return false;
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
								.append(sinnoriConfigFilePathString).append("]").toString();
						log.warn(errorMessage);
						return false;
					}
					
					dbcpNameList.add(dbcpName);
					dbcpNameSet.add(dbcpName);
				}
			}
		}
		
		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger
				.getInstance();
		/**
		 * 설정 프로퍼티 파일의 항목 키 전체가 올바른지 검사하기
		 * check item key invalidation by item in the config file
		 */
		@SuppressWarnings("unchecked")
		Enumeration<String> itemKeys = sinnoriConfigSequencedProperties.keys();
		while (itemKeys.hasMoreElements()) {
			String itemKey = itemKeys.nextElement();
			
			if (itemKey.equals(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)
					|| itemKey.equals(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)) {
				continue;
			}
			
			@SuppressWarnings("unused")
			ItemIDInfo<?> itemIDInfo = null;
			try {
				itemIDInfo = sinnoriItemIDInfoManger.getItemIDInfoFromKey(itemKey, dbcpNameSet, subProjectNameSet);
			} catch(IllegalArgumentException e) {
				String errorMessage = new StringBuilder("the item key[")
				.append(itemKey)
				.append("] of the sinnori conifg file[")
				.append(sinnoriConfigFilePathString).append("] is wrong").toString();
				log.warn(errorMessage, e);
				return false;
			}
		}		
				
		List<ItemIDInfo<?>> dbcpItemIDInfoList = sinnoriItemIDInfoManger.getUnmodifiableDBCPPartItemIDInfoList();
		HashMap<String, DBCPPart> dbcpPartHash = new HashMap<String, DBCPPart>();
		for (String dbcpName : dbcpNameList) {
			DBCPPart dbcpPart = new DBCPPart(dbcpName);
			for (ItemIDInfo<?> itemIDInfo : dbcpItemIDInfoList) {
				String itemKey = new StringBuilder("dbcp.").append(dbcpName)
						.append(".").append(itemIDInfo.getItemID()).toString();
				String itemValue = sinnoriConfigSequencedProperties.getProperty(itemKey);
				if (null == itemValue) {
					String errorMessage = new StringBuilder("the item key[")
							.append(itemKey)
							.append("] was not found in the sinnori conifg file[")
							.append(sinnoriConfigFilePathString).append("]")
							.toString();
					log.warn(errorMessage);
					return false;
				}
				
				boolean isInactive = sinnoriItemIDInfoManger.isInactive(itemKey, sinnoriConfigSequencedProperties);
				if (!isInactive) {
					try {
						dbcpPart.mapping(itemIDInfo, itemValue);
					} catch (IllegalArgumentException | ClassCastException
							| ConfigErrorException e) {
						String errorMessage = new StringBuilder(
								"fail to map item key[")
								.append(itemKey)
								.append("]'s value[")
								.append(itemValue)
								.append("] to the dbcp part value object's variable in the sinnori conifg file[")
								.append(sinnoriConfigFilePathString).append("]")
								.toString();
						log.warn(errorMessage, e);
						return false;
					}
				} else {
					log.info("item key[{}] is inactive status so that value is null", 
							itemKey);
				}
				
			}
			dbcpPartHash.put(dbcpName, dbcpPart);
		}
		
		allDBCPPart = new AllDBCPPart(dbcpNameList, dbcpPartHash);		
				
		List<ItemIDInfo<?>> commonItemIDInfoList = sinnoriItemIDInfoManger.getUnmodifiableCommonPartItemIDInfoList();
		for (ItemIDInfo<?> itemIDInfo : commonItemIDInfoList) {
			String itemKey = itemIDInfo.getItemID();
			String itemValue = sinnoriConfigSequencedProperties.getProperty(itemKey);
			if (null == itemValue) {
				String errorMessage = new StringBuilder(
						"the item key[")
						.append(itemKey)
						.append("] was not found in the sinnori conifg file[")
						.append(sinnoriConfigFilePathString).append("]")
						.toString();
				log.warn(errorMessage);
				return false;
			}
			
			boolean isInactive = sinnoriItemIDInfoManger.isInactive(itemKey, sinnoriConfigSequencedProperties);
			if (!isInactive) {
				try {
					commonPart.mapping(itemIDInfo, itemValue);
				} catch (IllegalArgumentException | ClassCastException
						| ConfigErrorException e) {
					String errorMessage = new StringBuilder(
							"fail to map item key[")
							.append(itemKey)
							.append("]'s value[")
							.append(itemValue)
							.append("] to the common part value object's variable in the sinnori conifg file[")
							.append(sinnoriConfigFilePathString).append("]")
							.toString();
					log.warn(errorMessage, e);
					return false;
				}
			} else {
				log.info("item key[{}] is inactive status so that item value is null", 
						itemKey);
			}
			
			
		}		
		
		List<ItemIDInfo<?>> projectItemIDInfoList = sinnoriItemIDInfoManger.getUnmodifiableProjectPartItemIDInfoList();
		
		/** main project part */
		for (ItemIDInfo<?> itemIDInfo : projectItemIDInfoList) {
			String itemKey = new StringBuilder("mainproject.")
			.append(itemIDInfo.getItemID()).toString();
			String itemValue = sinnoriConfigSequencedProperties.getProperty(itemKey);
			if (null == itemValue) {
				String errorMessage = new StringBuilder(
						"the item key[")
						.append(itemKey)
						.append("] was not found in the sinnori conifg file[")
						.append(sinnoriConfigFilePathString).append("]")
						.toString();
				log.warn(errorMessage);
				return false;
			}
			boolean isInactive = sinnoriItemIDInfoManger.isInactive(itemKey, sinnoriConfigSequencedProperties);
			if (!isInactive) {
				try {
					mainProjectPart.mapping(itemIDInfo, itemValue);
				} catch (IllegalArgumentException | ClassCastException
						| ConfigErrorException e) {
					String errorMessage = new StringBuilder(
							"fail to map item key[")
							.append(itemKey)
							.append("]'s value[")
							.append(itemValue)
							.append("] to the project part value object's variable in the sinnori conifg file[")
							.append(sinnoriConfigFilePathString).append("]")
							.toString();
					log.warn(errorMessage, e);
					return false;
				}
			} else {
				log.info("item key[{}] is inactive status so that value is null", 
						itemKey);
			}
		}		
		
		HashMap<String, ProjectPart> subProjectPartHash = new HashMap<String, ProjectPart>();
		for (String subProjectName : subProjectNameList) {
			ProjectPart subProjectPart = new ProjectPart(subProjectName);
			for (ItemIDInfo<?> itemIDInfo : projectItemIDInfoList) {
				String itemKey = new StringBuilder("subproject.").append(subProjectName)
						.append(".").append(itemIDInfo.getItemID()).toString();
				String itemValue = sinnoriConfigSequencedProperties.getProperty(itemKey);
				if (null == itemValue) {
					String errorMessage = new StringBuilder(
							"the item key[")
							.append(itemKey)
							.append("] was not found in the sinnori conifg file[")
							.append(sinnoriConfigFilePathString).append("]")
							.toString();
					log.warn(errorMessage);
					return false;
				}
				boolean isInactive = sinnoriItemIDInfoManger.isInactive(itemKey, sinnoriConfigSequencedProperties);
				if (!isInactive) {
					try {
						subProjectPart.mapping(itemIDInfo, itemValue);
					} catch (IllegalArgumentException | ClassCastException
							| ConfigErrorException e) {
						String errorMessage = new StringBuilder(
								"fail to map item key[")
								.append(itemKey)
								.append("]'s value[")
								.append(itemValue)
								.append("] to the project part value object's variable in the sinnori conifg file[")
								.append(sinnoriConfigFilePathString).append("]")
								.toString();
						log.warn(errorMessage, e);
						return false;
					}
				} else {
					log.info("item key[{}] is inactive status so that value is null", 
							itemKey);
				}							
			}
			
			subProjectPartHash.put(subProjectName, subProjectPart);
		}
		
		allSubProjectPart = new AllSubProjectPart(subProjectNameList, subProjectPartHash);
		
		return true;
	}

	public CommonPart getCommonPart() {
		return commonPart;
	}
	
	public ProjectPart getMainProjectPart() {
		return mainProjectPart;
	}

	public AllSubProjectPart getAllSubProjectPart() {
		return allSubProjectPart;
	}

	public AllDBCPPart getAllDBCPPart() {
		return allDBCPPart;
	}

	public String getSinnoriRunningProjectName() {
		return sinnoriRunningProjectName;
	}

	public String getSinnoriInstalledPathString() {
		return sinnoriInstalledPathString;
	}

}
