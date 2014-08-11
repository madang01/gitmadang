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

package kr.pe.sinnori.common.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.util.LogManager;
import kr.pe.sinnori.common.util.SequencedProperties;

import org.slf4j.Logger;

/**
 * 신놀이 환경 변수에 대응하는 값에 접근하기 위한 클래스
 * 
 * @author Jonghoon Won
 * 
 */
public final class SinnoriConfig {
	private final String SINNORI_PROJECT_CONFIG_FILE_CHARSET = "UTF-8";
	private Logger log = LogManager.getInstance().getLogger();
	private Map<String, Object> resourceHash = null;
	
	private SequencedProperties configFileProperties = new SequencedProperties();

	/** 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스 */
	private static final class SinnoriConfigHolder {
		static final SinnoriConfig singleton = new SinnoriConfig();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * @return 싱글턴 객체
	 */
	public static SinnoriConfig getInstance() {
		return SinnoriConfigHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자
	 */
	private SinnoriConfig() {
		resourceHash = new HashMap<String, Object>();

		String propSinnoriConfigFile = System.getenv("SINNORI_PROJECT_CONFIG_FILE");
		// System.out.printf("=sinnori_config_file[{}]",
		// prop_sinnori_config_file);
		// System.out.println();
		log.info("1.SINNORI_PROJECT_CONFIG_FILE[{}]", propSinnoriConfigFile);

		if (null == propSinnoriConfigFile) {
			propSinnoriConfigFile = "config.xml";

			log.info("2.prop_sinnori_config_file[{}]", propSinnoriConfigFile);
		}

		
		File sinnoriConfigFile = new File(propSinnoriConfigFile);
		
		if (!sinnoriConfigFile.exists()) {
			log.error("sinnori config file[{}] not exist", propSinnoriConfigFile);
			System.exit(1);
		}
		
		if (!sinnoriConfigFile.isFile()) {
			log.error("sinnori config file[{}] not file", sinnoriConfigFile.getAbsolutePath());
			System.exit(1);
		}
		
		if (!sinnoriConfigFile.canRead()) {
			log.error("sinnori config file[{}] can't read", sinnoriConfigFile.getAbsolutePath());
			System.exit(1);
		}

		
		FileInputStream sinnoriConfigFIS = null;
		InputStreamReader sinnoriConfigISR = null;
		try {

			sinnoriConfigFIS = new FileInputStream(sinnoriConfigFile);

			sinnoriConfigISR = new InputStreamReader(
					sinnoriConfigFIS, SINNORI_PROJECT_CONFIG_FILE_CHARSET);

			configFileProperties.load(sinnoriConfigISR);

			// System.out.println(this.toString());
		} catch (Exception e) {
			System.out.println("설정파일 읽기 실패");
			System.out.println(e.toString());
			System.exit(1);
		} finally {
			try {
				if (sinnoriConfigISR != null)
					sinnoriConfigISR.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		String propKey = null;
		String propValue = null;		
		
		/******** sessionkey 시작 **********/
		propKey = "sessionkey.rsa_keysize.value";
		propValue = configFileProperties.getProperty(propKey);
		
		int rsaKeySize = 1024;
		
		if (null == propValue) {
			log.info("세션키에서 사용하는 공개키 크기[{}][{}] 미지정으로 기본값[{}] 로 설정합니다.", propKey, propValue, rsaKeySize);
		} else {
			try {
				rsaKeySize = Integer.parseInt(propValue);
				
				if (rsaKeySize < 1024) {
					log.error("warning:: key[{}] minimum value 1024 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		
		resourceHash.put(propKey, rsaKeySize);
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		
		// sessionkey.rsa_keysize.value
		
		
		propKey = "sessionkey.rsa_keypair_source.value";
		propValue = configFileProperties.getProperty(propKey);
		
		String rsaKeyPairSource = "API";
		
		if (null == propValue) {
			log.info("세션키 공개키쌍 생성 방법[{}][{}] 미지정으로 기본값인 API 로 설정합니다.", propKey, propValue);			
		} else if (!rsaKeyPairSource.equals("File") && !rsaKeyPairSource.equals("API")){
			log.error("warning:: key[{}] set API, File but value[{}]", propKey, propValue);
			System.exit(1);
		} else {
			rsaKeyPairSource =  propValue;
		}
		
		resourceHash.put(propKey, rsaKeyPairSource);
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		
		if (rsaKeyPairSource.equals("File")) {
			propKey = "sessionkey.rsa_keypair_path.value";
			propValue = configFileProperties.getProperty(propKey);
			File rsaKeyPairPath = null;
			if (null == propValue) {
				log.error("warning:: 세션키 파일 경로[{}][{}]를 지정해 주세요", propKey, propValue);
				System.exit(1);
			} else {
				rsaKeyPairPath = new File(propValue);	
			}
			if (!rsaKeyPairPath.exists()) {
				log.error("warning:: 세션키 파일 경로[{}][{}]가 존재 하지 않습니다.", propKey, propValue);
				System.exit(1);
			}
			if (!rsaKeyPairPath.isDirectory() || !rsaKeyPairPath.canRead()) {
				log.error("warning:: 세션키 파일 경로[{}][{}][{}]가 잘못 되었습니다.", propKey, propValue, rsaKeyPairPath.getAbsolutePath());
				System.exit(1);
			}		
			resourceHash.put(propKey, rsaKeyPairPath);		
			log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		}
		
		
		propKey = "sessionkey.symmetric_key_algorithm.value";
		propValue = configFileProperties.getProperty(propKey);
		String symmetricKeyAlgorithm = null;
		if (null == propValue) {
			symmetricKeyAlgorithm="AES";
		} else {
			if (propValue.equals("AES") || propValue.equals("DESede") || propValue.equals("DES")) {
				symmetricKeyAlgorithm = propValue;
			} else {
				log.error("warning:: key[{}] set AES, DESede, DES but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}		
		resourceHash.put(propKey, symmetricKeyAlgorithm);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		
		propKey = "sessionkey.private_key.encoding.value";
		propValue = configFileProperties.getProperty(propKey);
		CommonType.SymmetricKeyEncoding privateKeyEncoding = null;
		// String privateKeyEncoding = null;
		if (null == propValue) {
			privateKeyEncoding = CommonType.SymmetricKeyEncoding.NONE;
		} else {
			if (propValue.equals("NONE")) {
				privateKeyEncoding = CommonType.SymmetricKeyEncoding.NONE;
			} else if (propValue.equals("BASE64")) {
				privateKeyEncoding = CommonType.SymmetricKeyEncoding.BASE64;
			} else {
				log.error("warning:: key[{}] set NONE, BASE64 but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}		
		resourceHash.put(propKey, privateKeyEncoding);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, privateKeyEncoding.toString());
		
		propKey = "sessionkey.symmetric_key_size.value";
		propValue = configFileProperties.getProperty(propKey);
		int symmetricKeySize=16;
		if (null != propValue) {
			try {
				symmetricKeySize = Integer.parseInt(propValue);
				
				if (symmetricKeySize < 8) {
					log.error("warning:: key[{}] minimum value 8 but value[{}]", propKey, propValue);
					System.exit(1);
				}				
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}		
		resourceHash.put(propKey, symmetricKeySize);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		
		
		propKey = "sessionkey.iv_size.value";
		propValue = configFileProperties.getProperty(propKey);
		int symmetricIVSize=16;
		if (null != propValue) {
			try {
				symmetricIVSize = Integer.parseInt(propValue);
				
				if (symmetricIVSize < 8) {
					log.error("warning:: key[{}] minimum value 8 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}		
		resourceHash.put(propKey, symmetricIVSize);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		/******** sessionkey 종료 **********/
		
		/******** servlet_jsp 시작 **********/
		propKey = "servlet_jsp.jdf_servlet_trace.value";
		propValue = configFileProperties.getProperty(propKey);
		boolean jdfServletTrace = true;
		if (null != propValue) {
			if (propValue.equals("true")) {				
				jdfServletTrace=true;
			} else if (propValue.equals("false")) {
				jdfServletTrace=false;
			} else {
				log.error("warning:: key[{}] set true, false but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}		
		resourceHash.put(propKey, jdfServletTrace);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		
		propKey = "servlet_jsp.jdf_error_message_page.value";
		propValue = configFileProperties.getProperty(propKey);
		String jdfErrorMessagePage = "/errorMessagePage.jsp";
		if (null != propValue) {			
			jdfErrorMessagePage=propValue;
		}		
		resourceHash.put(propKey, jdfErrorMessagePage);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		
		propKey = "servlet_jsp.jdf_login_page.value";
		propValue = configFileProperties.getProperty(propKey);
		String jdfLoginPage = "/login.jsp";
		if (null != propValue) {			
			jdfLoginPage=propValue;
		}		
		resourceHash.put(propKey, jdfLoginPage);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		
		propKey = "servlet_jsp.web_layout_control_page.value";
		propValue = configFileProperties.getProperty(propKey);
		String webLayoutControlPage = "/PageJump.jsp";
		if (null != propValue) {			
			webLayoutControlPage=propValue;
		}		
		resourceHash.put(propKey, webLayoutControlPage);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		/******** servlet_jsp 종료 **********/
		
		
		
		
		/******** 신놀이 작업자 시작 **********/
		propKey = "sinnori_worker.running_mode.value";
		propValue = configFileProperties.getProperty(propKey);		
		String sinnori_worker_running_mode = "client";
		if (propValue.toLowerCase().equals("server")) {
			sinnori_worker_running_mode = "server";
		} else if (propValue.toLowerCase().equals("client")) {
			sinnori_worker_running_mode = "client";
		} else if (propValue.toLowerCase().equals("all")) {
			sinnori_worker_running_mode = "all";
		} 		
		resourceHash.put(propKey, sinnori_worker_running_mode);
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		
		propKey = "sinnori_worker.client.executor.impl.source.path.value";
		propValue = configFileProperties.getProperty(propKey);		
		File sinnori_worker_client_executor_impl_source_path = new File(propValue);
		if (!sinnori_worker_client_executor_impl_source_path.exists()) {
			log.error("key[{}]::value[{}] 신놀이 프레임 워크 클라이언트 비지니스 로직 소스 경로가 존재하지 않습니다.", propKey, propValue);
			System.exit(1);
		}
		if (!sinnori_worker_client_executor_impl_source_path.isDirectory() || !sinnori_worker_client_executor_impl_source_path.canRead()) {
			log.error("key[{}]::value[{}] 신놀이 프레임 워크 클라이언트 비지니스 로직 소스 경로[{}]가 잘못되었습니다.", propKey, propValue, sinnori_worker_client_executor_impl_source_path.getAbsolutePath());
			System.exit(1);
		}
		resourceHash.put(propKey, sinnori_worker_client_executor_impl_source_path);
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		
		propKey = "sinnori_worker.client.executor.impl.binary.path.value";
		propValue = configFileProperties.getProperty(propKey);		
		File sinnori_worker_client_executor_impl_bianry_path = new File(propValue);
		if (!sinnori_worker_client_executor_impl_bianry_path.exists()) {
			log.error("key[{}]::value[{}] 신놀이 프레임 워크 클라이언트 비지니스 로직 이진 파일 경로가 존재하지 않습니다.", propKey, propValue);
			System.exit(1);
		}
		if (!sinnori_worker_client_executor_impl_bianry_path.isDirectory() || !sinnori_worker_client_executor_impl_bianry_path.canRead()) {
			log.error("key[{}]::value[{}] 신놀이 프레임 워크 클라이언트 비지니스 로직 이진 파일 경로[{}]가 잘못되었습니다.", propKey, propValue, sinnori_worker_client_executor_impl_bianry_path.getAbsolutePath());
			System.exit(1);
		}
		resourceHash.put(propKey, sinnori_worker_client_executor_impl_bianry_path);
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		
		propKey = "sinnori_worker.client.executor.prefix.value";
		propValue = configFileProperties.getProperty(propKey);
		String sinnori_worker_client_executor_prefix = null;
		if (null == propValue || 0 == propValue.trim().length()) {
			sinnori_worker_client_executor_prefix = "impl.executor.client.";
		} else {
			sinnori_worker_client_executor_prefix = propValue;
		}
		resourceHash.put(propKey, sinnori_worker_client_executor_prefix);
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		
		propKey = "sinnori_worker.client.executor.suffix.value";
		propValue = configFileProperties.getProperty(propKey);
		String sinnori_worker_client_executor_suffix = null;
		if (null == propValue || 0 == propValue.trim().length()) {
			sinnori_worker_client_executor_suffix = "CExtor";
		} else {
			sinnori_worker_client_executor_suffix = propValue;
		}
		resourceHash.put(propKey, sinnori_worker_client_executor_suffix);
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		/******** 신놀이 작업자 종료 **********/
		
		/******** 파일 송수신 시작 **********/
		propKey = "common.updownfile.local_source_file_resource_cnt.value";
		propValue = configFileProperties.getProperty(propKey);
		int localSourceFileResourceCnt=10;
		if (null != propValue) {
			try {
				localSourceFileResourceCnt = Integer.parseInt(propValue);
				
				if (localSourceFileResourceCnt < 5) {
					log.error("warning:: key[{}] minimum value 5 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		resourceHash.put(propKey, localSourceFileResourceCnt);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, (Integer)resourceHash.get(propKey));
		
		
		propKey = "common.updownfile.local_target_file_resource_cnt.value";
		propValue = configFileProperties.getProperty(propKey);
		int localTargetFileResourceCnt=10;
		if (null != propValue) {
			try {
				localTargetFileResourceCnt = Integer.parseInt(propValue);
				
				if (localTargetFileResourceCnt < 5) {
					log.error("warning:: key[{}] minimum value 5 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		resourceHash.put(propKey, localTargetFileResourceCnt);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, (Integer)resourceHash.get(propKey));
		
		
		propKey = "common.updownfile.file_block_max_size.value";
		propValue = configFileProperties.getProperty(propKey);
		int fileBlockMaxSize=1024*1024;
		if (null != propValue) {
			try {
				fileBlockMaxSize = Integer.parseInt(propValue);
				
				if (fileBlockMaxSize < 1024) {
					log.error("warning:: key[{}] minimum value 1024 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				
				if (0 != (fileBlockMaxSize % 1024)) {
					log.error("warning:: key[{}]'s value[{}] is not a multiple of 1024", propKey, propValue);
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		resourceHash.put(propKey, fileBlockMaxSize);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, (Integer)resourceHash.get(propKey));
		/******** 파일 송수신 종료 **********/

		/******** 프로젝트 모니터 시작 **********/
		propKey = "common.client.monitor.interval.value";
		propValue = configFileProperties.getProperty(propKey);
		long clientMonitorInterval=1000;
		if (null != propValue) {
			try {
				clientMonitorInterval = Long.parseLong(propValue);
				
				if (clientMonitorInterval < 1000) {
					log.error("warning:: key[{}] minimum value 1000 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		resourceHash.put(propKey, clientMonitorInterval);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, (Long)resourceHash.get(propKey));
		
		
		propKey = "common.server.monitor.interval.value";
		propValue = configFileProperties.getProperty(propKey);
		long serverMonitorInterval=1000;
		if (null != propValue) {
			try {
				serverMonitorInterval = Long.parseLong(propValue);
				
				if (serverMonitorInterval < 1000) {
					log.error("warning:: key[{}] minimum value 1000 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		resourceHash.put(propKey, serverMonitorInterval);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, (Long)resourceHash.get(propKey));
		/******** 프로젝트 모니터 종료 **********/
		
		/******** 싱글턴 클래스 객체 캐쉬 관리자 시작 **********/
		propKey = "common.cached_object.max_size.value";
		propValue = configFileProperties.getProperty(propKey);
		int cachedObjectMaxSize=10;
		if (null != propValue) {
			try {
				cachedObjectMaxSize = Integer.parseInt(propValue);
				
				if (cachedObjectMaxSize < 0) {
					log.error("warning:: key[{}] minimum value 0 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		resourceHash.put(propKey, cachedObjectMaxSize);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, (Integer)resourceHash.get(propKey));
		
		propKey = "common.cached_object.max_update_seq_interval.value";
		propValue = configFileProperties.getProperty(propKey);
		long maxUpdateSeqInterva=5000;
		if (null != propValue) {
			try {
				maxUpdateSeqInterva = Long.parseLong(propValue);
				
				if (maxUpdateSeqInterva < 1000) {
					log.error("warning:: key[{}] minimum value 1000 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		resourceHash.put(propKey, maxUpdateSeqInterva);		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, (Long)resourceHash.get(propKey));
		/******** 싱글턴 클래스 객체 캐쉬 관리자 종료 **********/
		
		
		propKey = "common.projectlist.value";
		propValue = configFileProperties.getProperty(propKey);
		String projectlist = null;
		if (null == propValue || 0 == propValue.trim().length()) {
			// projectlist = "sample_simple_chat";
			log.error("필수 항목 프로젝트목록[{}]의 값이 설정되지 않았습니다.", propKey);
			System.exit(1);
		} else {
			projectlist = propValue;
		}
		resourceHash.put(propKey, projectlist);
		
		StringTokenizer tokenProject = new StringTokenizer(projectlist, ",");
		while(tokenProject.hasMoreElements()) {
			String projectName = tokenProject.nextToken().trim();
			Hashtable<String, Object> projectHash = new Hashtable<String, Object>();
			
			resourceHash.put(projectName, projectHash);
			// int inputMessageWriterMaxSize = clientAsynIOThreadPoolInfo.getInputMessageWriterMaxSize();
			/*ProjectConfig projectInfo = new ProjectConfig(projectName, configFileProperties, log);			
			resourceHash.put(projectName, projectInfo);
			log.info("[{}] 프로젝트 정보를 신놀이 환경 변수에 저장합니다.", projectName));
			log.info(projectInfo.toString());*/
		}		
		
		//resourceHash.put(propKey, projectSet);
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
	}
	
	public CommonProjectConfig getCommonProjectConfig(String projectName) {
		if (null == projectName) {
			String errorMessage = String.format("parameter projectName is null", projectName);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
 		
		@SuppressWarnings("unchecked")
		Hashtable<String, Object> projectHash = (Hashtable<String, Object>)resourceHash.get(projectName);
		if (null  == projectHash) {
			log.warn("projectName[{}] not exist", projectName);
			return null;
		}
		
		CommonProjectConfig commonProjectConfig = (CommonProjectConfig)projectHash.get("CommonProjectConfig");
		if (null == commonProjectConfig) {
			commonProjectConfig = new CommonProjectConfig(projectName, configFileProperties, log);
			projectHash.put("ClientProjectConfig", commonProjectConfig);
		}
		return commonProjectConfig;
	}

	public ClientProjectConfig getClientProjectConfig(String projectName) {
		if (null == projectName) {
			String errorMessage = String.format("parameter projectName is null", projectName);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
 		
		
		@SuppressWarnings("unchecked")
		Hashtable<String, Object> projectHash = (Hashtable<String, Object>)resourceHash.get(projectName);
		if (null  == projectHash) {
			log.warn("projectName[{}] not exist", projectName);
			return null;
		}
		
		ClientProjectConfig clientProjectConfig = (ClientProjectConfig)projectHash.get("ClientProjectConfig");
		if (null == clientProjectConfig) {
			clientProjectConfig = new ClientProjectConfig(projectName, configFileProperties, log);
			projectHash.put("ClientProjectConfig", clientProjectConfig);
		}
		return clientProjectConfig;
	}
	
	public ServerProjectConfig getServerProjectConfig(String projectName) {
		if (null == projectName) {
			String errorMessage = String.format("parameter projectName is null", projectName);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
 		
		@SuppressWarnings("unchecked")
		Hashtable<String, Object> projectHash = (Hashtable<String, Object>)resourceHash.get(projectName);
		if (null  == projectHash) {
			log.warn("projectName[{}] not exist", projectName);
			return null;
		}
		
		ServerProjectConfig serverProjectConfig = (ServerProjectConfig)projectHash.get("ClientProjectConfig");
		if (null == serverProjectConfig) {
			serverProjectConfig = new ServerProjectConfig(projectName, configFileProperties, log);
			projectHash.put("ClientProjectConfig", serverProjectConfig);
		}
		return serverProjectConfig;
	}
	
	/*
	public String getServerHost(String projectName) {
		ProjectConfig projectInfo = (ProjectConfig)resourceHash.get(projectName);
		return projectInfo.getServerHost();
	}
	
	public int getServerPort(String projectName) {
		ProjectConfig projectInfo = (ProjectConfig)resourceHash.get(projectName);
		return projectInfo.getServerPort();
	}
	*/

	
	/**
	 * 신놀이 환경 변수 이름을 갖는 값을 반환한다. 
	 * @param key 환경 변수 이름
	 * @return 환경 변수 값
	 */
	public Object getResource(String key) {
		Object value = resourceHash.get(key);
		if (null == value) {
			String errorMessage = String.format("key[{}] not found", key);
			log.warn(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		return value;
	}
	
	/**
	 * 설정파일 저장
	 */
	public void save() {
		String propSinnoriConfigFile = System.getenv("SINNORI_PROJECT_CONFIG_FILE");
		String projectName = System.getenv("SINNORI_PROJECT_NAME");
		// System.out.printf("=sinnori_config_file[{}]",
		// prop_sinnori_config_file);
		// System.out.println();
		log.info("1.Project[{}] SINNORI_PROJECT_CONFIG_FILE[{}]", projectName, propSinnoriConfigFile);

		if (null == propSinnoriConfigFile) {
			propSinnoriConfigFile = "config.xml";

			log.info("2.Project[{}] prop_sinnori_config_file[{}]", projectName, propSinnoriConfigFile);
		}

		
		File sinnoriConfigFile = new File(propSinnoriConfigFile);
		
		if (!sinnoriConfigFile.exists()) {
			log.error("Project[{}] sinnori config file[{}] not exist", projectName, propSinnoriConfigFile);
			System.exit(1);
		}
		
		if (!sinnoriConfigFile.isFile()) {
			log.error("Project[{}] sinnori config file[{}] not file", projectName, sinnoriConfigFile.getAbsolutePath());
			System.exit(1);
		}
		
		if (!sinnoriConfigFile.canWrite()) {
			log.error("Project[{}] sinnori config file[{}] can't write", projectName, sinnoriConfigFile.getAbsolutePath());
			System.exit(1);
		}
		
		FileOutputStream fos_sinnoriConfig_file = null;
		OutputStreamWriter osw_sinnoriConfig_file = null;
		try {
			fos_sinnoriConfig_file = new FileOutputStream(sinnoriConfigFile);
			osw_sinnoriConfig_file = new OutputStreamWriter(fos_sinnoriConfig_file, SINNORI_PROJECT_CONFIG_FILE_CHARSET);
			configFileProperties.store(osw_sinnoriConfig_file, String.format("# Sinnori Project[{}] Config File", projectName));
			// System.out.println(this.toString());
		} catch (Exception e) {
			log.error("Project[{}] sinnori config file[{}] unknown error", projectName, sinnoriConfigFile.getAbsolutePath(), e);
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
}
