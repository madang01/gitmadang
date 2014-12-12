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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.util.SequencedProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 신놀이 환경 변수에 대응하는 값에 접근하기 위한 클래스
 * 
 * @author Won Jonghoon
 * 
 */
public final class SinnoriConfig {
	private Logger log = LoggerFactory.getLogger(CommonStaticFinalVars.SINNORI_ROOT_LOGGER_NAME);
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
		
		String sinnoriConfigurationFileName = System.getProperty(CommonStaticFinalVars.SINNORI_CONFIG_FILE_JAVA_SYSTEM_VAR_NAME);
		log.info("자바 시스템 환경 변수 '신놀이 환경 설정 파일'[{}]의 값[{}]", CommonStaticFinalVars.SINNORI_CONFIG_FILE_JAVA_SYSTEM_VAR_NAME, sinnoriConfigurationFileName);
		
		
		if (sinnoriConfigurationFileName == null) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("#No Project Config File");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("#Mon Aug 18 00:07:34 KST 2014");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("servlet_jsp.jdf_error_message_page.desc=JDF framework에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp, 미 지정시 /errorMessagePage.jsp.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("servlet_jsp.jdf_error_message_page.value=/errorMessagePage.jsp");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("servlet_jsp.jdf_login_page.desc=로그인 전용 처리 jsp, 미 지정시 /login.jsp.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("servlet_jsp.jdf_login_page.value=/login.jsp");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("servlet_jsp.jdf_servlet_trace.desc=JDF framework에서 서블릿 경과시간 추적 여부, 미 지정시 true.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("servlet_jsp.jdf_servlet_trace.set=true, false");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("servlet_jsp.jdf_servlet_trace.value=true");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("servlet_jsp.web_layout_control_page.desc=신놀이 웹의 레이아웃 컨트롤러 jsp, /PageJump.jsp.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("servlet_jsp.web_layout_control_page.value=/PageJump.jsp");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.rsa_keypair_source.desc=세션키에 사용되는 공개키 키쌍 생성 방법(\\=원천)로써 2가지가 있다. 미지정시 API, (1) API \\: 자체 암호 lib 이용하여 RSA 키쌍 생성, (2) File \\: 외부 파일를 읽어와서 RSA  키쌍을 생성");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.rsa_keypair_source.set=API, File");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.rsa_keypair_source.value=API");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.rsa_keypair_path.desc=세션키에 사용되는 공개키 키쌍 파일 경로,  세션키에 사용되는 공개키 키쌍 생성 방법이 File 일 경우에는 필수 항목.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.rsa_keypair_path.value=./");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.rsa_keysize.desc=세션키에 사용하는 공개키 크기, 단위 byte. 디폴트 1024");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.rsa_keysize.value=1024");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.symmetric_key_algorithm.desc=세션키에 사용되는 대칭키 알고리즘, 미 지정시 AES.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.symmetric_key_algorithm.set=ASE, DESede, DES");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.symmetric_key_algorithm.value=AES");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.symmetric_key_size.desc=세션키에 사용되는 대칭키 크기, 단위 byte, 암호 강도 때문에 최소 8 byte 이상 요구, 미 지정시 16.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.symmetric_key_size.value=16");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.iv_size.desc=세션키에 사용되는 대칭키와 같이 사용되는 IV 크기, 단위 byte, 최소 8 byte 이상 갖도록 함. 미 지정시 16.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.iv_size.value=16");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.private_key.encoding.desc=개인키를 인코딩 방법, 미 지정시 NONE. 웹의 경우 이진데이터는 폼 전송이 불가하므로 base64 인코딩하여 전송한다.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.private_key.encoding.set=NONE, BASE64");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sessionkey.private_key.encoding.value=BASE64");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sinnori_worker.running_mode.desc=신놀이 작업자 동작 모드");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sinnori_worker.running_mode.set=client, server, all");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sinnori_worker.running_mode.value=client");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sinnori_worker.client.executor.prefix.value=impl.executor.client.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("sinnori_worker.client.executor.suffix.value=CExtor");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("common.updownfile.local_source_file_resource_cnt.desc=로컬 원본 파일 자원 갯수, 미 지정시 10, 최소 5");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("common.updownfile.local_source_file_resource_cnt.value=10");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("common.updownfile.local_target_file_resource_cnt.desc=로컬 목적지 파일 자원 갯수, 미 지정시 10, 최소 5");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("common.updownfile.local_target_file_resource_cnt.value=10");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("common.updownfile.file_block_max_size.desc=파일 송수신 파일 블락 최대 크기, 최소값 1024, 1024의 배수, 기본값 1 Mbytes \\= 1024*1024(\\=1048576), 단위 byte.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("common.updownfile.file_block_max_size.value=1048576");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("common.cached_object.max_size.desc=싱글턴 클래스 '객체 캐쉬 관리자'(LoaderAndName2ObjectManager) 에서 캐쉬로 관리할 객체의 최대 갯수. 주로 캐쉬되는 대상 객체는 xxxServerCodec, xxxClientCodec 이다.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("common.cached_object.max_size.value=1");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("common.cached_object.max_update_seq_interval.desc=시간 개념의 객체 생성 순서를 갱신하는 최소 간격, 단위 ms.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("common.cached_object.max_update_seq_interval.value=5000");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("common.projectlist.desc=프로젝트와 프로젝트 구분은 공백 없이 콤마로 한다.");
			stringBuilder.append(System.getProperty("line.separator"));
			stringBuilder.append("common.projectlist.value=");
			stringBuilder.append(System.getProperty("line.separator"));
			ByteArrayInputStream bais = null;
			InputStreamReader sinnoriConfigISR = null;
			try {
				bais = new ByteArrayInputStream(stringBuilder.toString().getBytes(CommonStaticFinalVars.SINNORI_CONFIG_FILE_CHARSET));
				
				sinnoriConfigISR = new InputStreamReader(bais, CommonStaticFinalVars.SINNORI_CONFIG_FILE_CHARSET);

				configFileProperties.load(sinnoriConfigISR);
				
			} catch (UnsupportedEncodingException e) {
				String errorMessage = String.format("신놀이 환경 설정 실패::설정파일의 문자셋[%s] 이름이 잘못되었습니다.", CommonStaticFinalVars.SINNORI_CONFIG_FILE_CHARSET);
				log.error(errorMessage, e);
				throw new RuntimeException(errorMessage);
			} catch (IOException e) {
				String errorMessage = String.format("신놀이 환경 설정 실패::설정 파일[%s] 읽을때 에러 발생", sinnoriConfigurationFileName);
				log.error(errorMessage, e);
				throw new RuntimeException(errorMessage);
			} catch (Exception e) {
				String errorMessage = String.format("신놀이 환경 설정 실패::알 수 없는 에러::%s", e.getMessage());
				log.error(errorMessage, e);
				throw new RuntimeException(errorMessage);
			} finally {
				try {
					if (sinnoriConfigISR != null)
						sinnoriConfigISR.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				try {
					if (bais != null)
						bais.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} else {
			File sinnoriConfigFile = new File(sinnoriConfigurationFileName);
			
			if (!sinnoriConfigFile.exists()) {
				String errorMessage = String.format("신놀이 환경 설정 실패::설정 파일[%s]이 존재하지 않습니다.", sinnoriConfigurationFileName);
				log.error(errorMessage);
				throw new RuntimeException(errorMessage);
			}
			
			if (!sinnoriConfigFile.isFile()) {
				String errorMessage = String.format("신놀이 환경 설정 실패::설정 파일[%s]이 일반 파일이 아닙니다.", sinnoriConfigurationFileName);
				log.error(errorMessage);
				throw new RuntimeException(errorMessage);
			}
			
			if (!sinnoriConfigFile.canRead()) {
				String errorMessage = String.format("신놀이 환경 설정 실패::설정 파일[%s]을 읽을 수 없습니다.", sinnoriConfigurationFileName);
				log.error(errorMessage);
				throw new RuntimeException(errorMessage);
			}
			
			FileInputStream sinnoriConfigFIS = null;
			InputStreamReader sinnoriConfigISR = null;
			try {

				sinnoriConfigFIS = new FileInputStream(sinnoriConfigFile);

				sinnoriConfigISR = new InputStreamReader(
						sinnoriConfigFIS, CommonStaticFinalVars.SINNORI_CONFIG_FILE_CHARSET);

				configFileProperties.load(sinnoriConfigISR);

			} catch(FileNotFoundException  e) {
				String errorMessage = String.format("신놀이 환경 설정 실패::설정 파일[%s]이 존재하지 않습니다.", sinnoriConfigurationFileName);
				log.error(errorMessage, e);
				throw new RuntimeException(errorMessage);
			} catch (IOException e) {
				String errorMessage = String.format("신놀이 환경 설정 실패::설정 파일[%s] 읽을때 에러 발생", sinnoriConfigurationFileName);
				log.error(errorMessage, e);
				throw new RuntimeException(errorMessage);
			} catch (Exception e) {
				String errorMessage = String.format("신놀이 환경 설정 실패::알 수 없는 에러::%s", e.getMessage());
				log.error(errorMessage, e);
				throw new RuntimeException(errorMessage);
			} finally {
				try {
					if (sinnoriConfigISR != null)
						sinnoriConfigISR.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				try {
					if (sinnoriConfigFIS != null)
						sinnoriConfigFIS.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

		String propKey = null;
		String propValue = null;
		
		/******** dbcp 시작 **********/
		// dbcp.connection_pool_name_list.value
		propKey = "dbcp.connection_pool_name_list.value";
		propValue = configFileProperties.getProperty(propKey);
		
		List<String> dbcpConnectionPoolNameList = new ArrayList<String>();
		StringTokenizer dbcpConnectionPoolNameTokens = new StringTokenizer(propValue, ",");
		while (dbcpConnectionPoolNameTokens.hasMoreElements()) {
			String dbcpConnectionPoolName = dbcpConnectionPoolNameTokens.nextToken().trim();
			dbcpConnectionPoolNameList.add(dbcpConnectionPoolName);
		}
		
		resourceHash.put(propKey, dbcpConnectionPoolNameList);
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		
		for (String dbcpConnectionPoolName : dbcpConnectionPoolNameList) {
			propKey = new StringBuilder("dbcp.")
			.append(dbcpConnectionPoolName).append(".confige_file.value").toString();
			
			String configeFilePathName = configFileProperties.getProperty(propKey);
			
			File configeFile = new File(configeFilePathName);
			if (! configeFile.exists()) {
				log.warn("key[{}] dbcp connection pool name[{}]'s config file[{}] is not found", propKey, dbcpConnectionPoolName, configeFilePathName);
				continue;
			}
			if (! configeFile.canRead()) {
				log.warn("key[{}] dbcp connection pool name[{}]'s config file[{}] cannot be read", propKey, dbcpConnectionPoolName, configeFilePathName);
				continue;
			}
			
			if (! configeFile.canWrite()) {
				log.warn("key[{}] dbcp connection pool name[{}]'s config file[{}] cannot be written", propKey, dbcpConnectionPoolName, configeFilePathName);
				continue;
			}
			
			resourceHash.put(propKey, configeFile);
			log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));			
		}
		
		/******** dbcp 종료 **********/
		
		/******** mybatis 시작 **********/
		propKey = "mybatis.config_file.value";
		propValue = configFileProperties.getProperty(propKey);		
		
		for (int i=0; i < 1; i++) {
			if (null == propValue) {
				log.warn("key[{}] is not found", propKey);
				continue;
			}
			
			String trimPropValue = propValue.trim();
			
			if (trimPropValue.equals("")) {
				log.warn("key[{}]'s value is empty", propKey);
				continue;
			}
			
			if (!trimPropValue.equals(propValue)) {
				log.warn("key[{}]'s value is not same to the copy of the string, with leading and trailing whitespace omitted", propKey);
				continue;
			}
			
			// String mybatisConfigeFilePathName = propValue;
			
			/*File mybatisConfigeFile = new File(propValue);
			if (! mybatisConfigeFile.exists()) {
				log.warn("key[{}] mybatis config file[{}] is not found", propKey, propValue);
				continue;
			}
			if (! mybatisConfigeFile.canRead()) {
				log.warn("key[{}] mybatis config file[{}] cannot be read", propKey, propValue);
				continue;
			}
			
			if (! mybatisConfigeFile.canWrite()) {
				log.warn("key[{}] mybatis config file[{}] cannot be written", propKey, propValue);
				continue;
			}*/
			
			resourceHash.put(propKey, propValue);
			log.info("{}::prop value[{}], new value[{}]", propKey, propValue, resourceHash.get(propKey));
		}
		
		/******** mybatis 종료 **********/
		
		
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
		List<String> projectNamelist = new ArrayList<String>();
		if (null == propValue || 0 == propValue.trim().length()) {
			// projectlist = "sample_simple_chat";
			log.error("필수 항목 프로젝트목록[{}]의 값이 설정되지 않았습니다.", propKey);
			System.exit(1);
		}
		resourceHash.put(propKey, projectNamelist);
		
		StringTokenizer tokenProject = new StringTokenizer(propValue, ",");
		while(tokenProject.hasMoreElements()) {			
			String projectName = tokenProject.nextToken().trim();
			projectNamelist.add(projectName);
			Hashtable<String, Object> projectHash = new Hashtable<String, Object>();
			
			resourceHash.put(projectName, projectHash);			
		}
		
		if (0 == projectNamelist.size()) {
			log.error("필수 항목 프로젝트목록[{}]의 값이 설정되지 않았습니다.", propKey);
			System.exit(1);
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
		
		ServerProjectConfig serverProjectConfig = (ServerProjectConfig)projectHash.get("ServerProjectConfig");
		if (null == serverProjectConfig) {
			serverProjectConfig = new ServerProjectConfig(projectName, configFileProperties, log);
			projectHash.put("ServerProjectConfig", serverProjectConfig);
		}
		return serverProjectConfig;
	}
	
	
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
		String propSinnoriConfigFile = System.getProperty(CommonStaticFinalVars.SINNORI_CONFIG_FILE_JAVA_SYSTEM_VAR_NAME);
		String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);		
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
			osw_sinnoriConfig_file = new OutputStreamWriter(fos_sinnoriConfig_file, CommonStaticFinalVars.SINNORI_CONFIG_FILE_CHARSET);
			configFileProperties.store(osw_sinnoriConfig_file, String.format("# Sinnori Project[{}] Config File", projectName));
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
