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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import kr.pe.sinnori.common.config.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.util.CommonStaticUtil;

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

	private SinnoriConfiguration sinnoriRunningProjectConfiguration = null;
	

	/** 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스 */
	private static final class SinnoriConfigurationManagerHolder {
		static final SinnoriConfigurationManager singleton = new SinnoriConfigurationManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static SinnoriConfigurationManager getInstance() {
		return SinnoriConfigurationManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자
	 */
	private SinnoriConfigurationManager() {
		String sinnoriRunningProjectName = System
				.getProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME);
		String sinnoriInstalledPathString = System
				.getProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH);

		
		/*log.info(
				"java system proprties -D[{}]=[{}], -D[{}]=[{}]",
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				sinnoriRunningProjectName,
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);*/
		
		if (null == sinnoriRunningProjectName) {
			log.error("sinnori configuration needs java system properties variable '{}', ex) java -D{}=sample_base -D{}=~/gitsinnori/sinnori", 
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME, 
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH);
			System.exit(1);
		}
		
		if (sinnoriRunningProjectName.equals("")) {
			log.error("java system properties variable '{}' is a empty string", 
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME);
			System.exit(1);
		}
		
		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(sinnoriRunningProjectName)) {
			log.error("java system properties variable '{}' has leading or tailing white space", CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME);
			System.exit(1);
		}
		
		if (null == sinnoriInstalledPathString) {
			log.error("sinnori configuration needs java system properties variable '{}', ex) java -D{}=sample_base -D{}=~/gitsinnori/sinnori", 
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH, 
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH);
			System.exit(1);
		}
		
		if (sinnoriInstalledPathString.equals("")) {
			log.error("java system properties variable '{}' is a empty string", 
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH);
			System.exit(1);
		}
		
		File sinnoriInstalledPath = new File(sinnoriInstalledPathString);
		
		if (! sinnoriInstalledPath.exists()) {
			String errorMessage = new StringBuilder("the sinnori installed path(=java system properties variable '")
			.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH)
			.append("'s value[")
			.append(sinnoriInstalledPathString)
			.append("]) doesn't exist").toString();
			log.error(errorMessage);
			System.exit(1);
		}
		
		if (! sinnoriInstalledPath.isDirectory()) {
			String errorMessage = new StringBuilder("the sinnori installed path(=java system properties variable '")
			.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH)
			.append("'s value[")
			.append(sinnoriInstalledPathString)
			.append("]) is not a directory").toString();
			log.error(errorMessage);
			System.exit(1);
		}
		
		String sinnoriConfigFilePathString = BuildSystemPathSupporter
				.getSinnoriConfigFilePathString(sinnoriRunningProjectName,
						sinnoriInstalledPathString);
		
		try {
			sinnoriRunningProjectConfiguration = new SinnoriConfiguration(sinnoriInstalledPathString, sinnoriRunningProjectName);
		} catch (IllegalArgumentException e) {
			log.error(
					"check java system proprties -D{}={} -D{}={}, errormessage={}",
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
					sinnoriRunningProjectName,
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
					sinnoriInstalledPathString,
					e.getMessage());

			System.exit(1);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder("the Sinnori configuration file[")
			.append(sinnoriConfigFilePathString).append("] doesn't exist").toString();
			log.error(errorMessage, e);
			System.exit(1);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to read the Sinnori configuration file[")
			.append(sinnoriConfigFilePathString).append("]").toString();
			log.error(errorMessage, e);
			System.exit(1);
		} catch (SinnoriConfigurationException e) {
			String errorMessage = new StringBuilder("the Sinnori configuration file[")
			.append(sinnoriConfigFilePathString).append("] has bad format").toString();
			log.error(errorMessage, e);
			System.exit(1);
		}
	}
	
	public SinnoriConfiguration getSinnoriRunningProjectConfiguration() {
		return sinnoriRunningProjectConfiguration;
	}
}
