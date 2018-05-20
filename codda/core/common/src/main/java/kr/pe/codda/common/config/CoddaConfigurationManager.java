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

package kr.pe.codda.common.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * 환경 변수에 대응하는 값에 접근하기 위한 클래스
 * 
 * @author Won Jonghoon
 * 
 */
public final class CoddaConfigurationManager {
	private InternalLogger log = InternalLoggerFactory.getInstance(CoddaConfigurationManager.class);

	private CoddaConfiguration runningProjectConfiguration = null;

	/** 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스 */
	private static final class ConfigurationManagerHolder {
		static final CoddaConfigurationManager singleton = new CoddaConfigurationManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static CoddaConfigurationManager getInstance() {
		return ConfigurationManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자
	 */
	private CoddaConfigurationManager() {
		String runningProjectName = System
				.getProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME);
		String installedPathString = System
				.getProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH);
		
		

		if (null == runningProjectName) {
			log.error(
					"configuration needs java system properties variable '{}', ex) java -D{}=sample_base -D{}=[installed path]",
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME,
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME,
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH);
			System.exit(1);
		}

		if (runningProjectName.equals("")) {
			log.error("java system properties variable '{}' is a empty string",
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME);
			System.exit(1);
		}

		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(runningProjectName)) {
			log.error("java system properties variable '{}' has leading or tailing white space",
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME);
			System.exit(1);
		}

		if (null == installedPathString) {
			log.error(
					"configuration needs java system properties variable '{}', ex) java -D{}=sample_base -D{}=[installed path]",
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH,
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME,
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH);
			System.exit(1);
		}

		if (installedPathString.equals("")) {
			log.error("java system properties variable '{}' is a empty string",
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH);
			System.exit(1);
		}

		File installedPath = new File(installedPathString);

		if (!installedPath.exists()) {
			String errorMessage = new StringBuilder("the installed path(=java system properties variable '")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH).append("'s value[")
					.append(installedPathString).append("]) doesn't exist").toString();
			log.error(errorMessage);
			System.exit(1);
		}

		if (!installedPath.isDirectory()) {
			String errorMessage = new StringBuilder("the installed path(=java system properties variable '")
					.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH).append("'s value[")
					.append(installedPathString).append("]) is not a directory").toString();
			log.error(errorMessage);
			System.exit(1);
		}

		String configFilePathString = ProjectBuildSytemPathSupporter
				.getProejctConfigFilePathString(installedPathString, runningProjectName);
		
		try {
			runningProjectConfiguration = new CoddaConfiguration(installedPathString, runningProjectName);
		} catch (IllegalArgumentException e) {
			log.error("check java system proprties -D{}={} -D{}={}, errormessage={}",
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME, runningProjectName,
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH, installedPathString,
					e.getMessage());

			System.exit(1);
		} catch (FileNotFoundException e) {
			String errorMessage = new StringBuilder("the configuration file[")
					.append(configFilePathString).append("] doesn't exist").toString();
			log.error(errorMessage, e);
			System.exit(1);
		} catch (IOException e) {
			String errorMessage = new StringBuilder("fail to read the configuration file[")
					.append(configFilePathString).append("]").toString();
			log.error(errorMessage, e);
			System.exit(1);
		} catch (CoddaConfigurationException e) {
			String errorMessage = new StringBuilder("the configuration file[")
					.append(configFilePathString).append("] has bad format").toString();
			log.error(errorMessage, e);
			System.exit(1);
		}
	}

	public CoddaConfiguration getRunningProjectConfiguration() {
		return runningProjectConfiguration;
	}

}
