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

package kr.pe.sinnori.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 서버 설정파일을 담는 Properties
 * 
 * @author Jonghoon Won
 */
public final class ConfigManager {
	private final String SINNORI_CONFIG_FILE_CHARSET = "UTF-8";
	private Properties sinnoriConfig = null;

	private static final class ConfigManagerHolder {
		static final ConfigManager singleton = new ConfigManager();
	}

	public static ConfigManager getInstance() {
		return ConfigManagerHolder.singleton;
	}

	private ConfigManager() {
		super();

		System.out.println("call ConfigManager::ConfigManager");

		String str_sinnori_config_file = System.getenv("SINNORI_PROJECT_CONFIG_FILE");

		System.out.printf("System.getenv::sinnori_config_file[%s]\n",
				str_sinnori_config_file);

		// String sinnoriConfig_file =
		// System.getProperty("sinnori_config_file"); // 설정파일경로
		if (str_sinnori_config_file == null) {
			str_sinnori_config_file = System.getProperty("sinnori_config_file");
			System.out.printf("System.getProperty::sinnori_config_file[%s]\n",
					str_sinnori_config_file);
		}

		if (str_sinnori_config_file == null) {
			sinnoriConfig = System.getProperties();
		} else {
			File sinnori_config_file = new File(str_sinnori_config_file);
			if (sinnori_config_file.exists() && sinnori_config_file.canRead()) {
				sinnoriConfig = loadsinnoriConfig(sinnori_config_file);
			} else {
				System.out.printf(
						"sinnori_config_file[%s] not exist or cann't read\n",
						str_sinnori_config_file);
				sinnoriConfig = System.getProperties();
			}
		}

	}

	/**
	 * 
	 * 지정한 파일및, 파일의 문자셋에 맞쳐 설정 파일을 로드한다. 설정 파일을 위한 환경 변수를 검사한다. 값 지정이 안되었다면 시스템
	 * 종료한다.
	 * 
	 * 1. 환경변수 1.1 설정파일문자셋 : sinnoriConfig_file_charset 예제)
	 * -DsinnoriConfig_file_charset="EUC-KR" 1.2 설정파일경로 : sinnoriConfig_file 예제)
	 * -DsinnoriConfig_file=
	 * "E:\prj\PlayingYut\sinnoriConfig\sinnoriConfig.properties"
	 * 
	 */
	public void savesinnoriConfig(String comments) {
		// 설정파일에 관한 환경 변수를 읽어 온다.
		String sinnoriConfig_file_charset = System
				.getProperty("sinnoriConfig_file_charset"); // 설정파일문자셋
		String sinnoriConfig_file = System.getProperty("sinnoriConfig_file"); // 설정파일경로

		// 서버 설정 파일 시작
		if (sinnoriConfig_file_charset == null) {
			sinnoriConfig_file_charset = "UTF-8";
		}

		// 설정파일 load
		File f_sinnoriConfig_file = null;
		FileOutputStream fos_sinnoriConfig_file = null;
		try {
			f_sinnoriConfig_file = new File(sinnoriConfig_file);

			if (!f_sinnoriConfig_file.exists()
					|| !f_sinnoriConfig_file.isFile()) {
				System.out.println(String.format(
						"sinnoriConfig file exist=%s, is file=%s",
						f_sinnoriConfig_file.exists(),
						f_sinnoriConfig_file.isFile()));
				System.out.println(String.format(
						"Wrong path, check sinnoriConfig_file=%s",
						sinnoriConfig_file));
				new Throwable(String.format(
						"Wrong path, check sinnoriConfig_file=%s",
						sinnoriConfig_file)).printStackTrace();
				System.exit(1);
			}
			fos_sinnoriConfig_file = new FileOutputStream(f_sinnoriConfig_file);

			sinnoriConfig.store(fos_sinnoriConfig_file, comments);
		} catch (Exception e) {
			System.out.println("설정파일 쓰기 실패");
			System.out.println(e.toString());
			System.exit(1);
		} finally {
			try {
				if (fos_sinnoriConfig_file != null)
					fos_sinnoriConfig_file.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * 지정한 파일및, 파일의 문자셋에 맞쳐 설정 파일을 로드한다. 설정 파일을 위한 환경 변수를 검사한다. 값 지정이 안되었다면 시스템
	 * 종료한다.
	 * 
	 * 1. 환경변수 1.1 설정파일문자셋 : sinnoriConfig_file_charset 예제)
	 * -DsinnoriConfig_file_charset="EUC-KR" 1.2 설정파일경로 : sinnoriConfig_file 예제)
	 * -DsinnoriConfig_file=
	 * "E:\prj\PlayingYut\sinnoriConfig\sinnoriConfig.properties"
	 * 
	 */
	public Properties loadsinnoriConfig(File sinnori_config_file) {
		Properties theProperties = new Properties();

		// 설정파일 load

		FileInputStream fis_sinnoriConfig_file = null;
		InputStreamReader isr_sinnoriConfig_file = null;
		try {

			fis_sinnoriConfig_file = new FileInputStream(sinnori_config_file);

			isr_sinnoriConfig_file = new InputStreamReader(
					fis_sinnoriConfig_file, SINNORI_CONFIG_FILE_CHARSET);

			theProperties.load(isr_sinnoriConfig_file);

			// System.out.println(this.toString());
		} catch (Exception e) {
			System.out.println("설정파일 읽기 실패");
			System.out.println(e.toString());
			System.exit(1);
		} finally {
			try {
				if (isr_sinnoriConfig_file != null)
					isr_sinnoriConfig_file.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		return theProperties;
	}

	/**
	 * 환경 변수의 값을 얻는다.
	 * @param key 환경 변수명
	 * @return 환경 변수 값
	 */
	public String getProperty(String key) {
		return sinnoriConfig.getProperty(key);
	}

	/*
	public void setProperty(String key, String value) {
		sinnoriConfig.setProperty(key, value);
	}
	*/
}
