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

import kr.pe.sinnori.common.lib.CommonStaticFinalVars;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 로그, 표준출력, 표준에러출력에 관해서 설정파일에 명시된 경로에 고정된 이름으로 일자별로 생성하도록 처리해 주는 class<br/>
 * 
 * note : 로그는 {@link java.util.logging.Logger } 를 기반으로한다.
 * 
 * @author Jonghoon Won
 */
public final class LogManager {
	// private final String SINNORI_CONFIG_FILE_CHARSET = "UTF-8";
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자.
	 */
	private LogManager() {
		System.out.println("call LogManager::LogManager");

		/*String propSinnoriConfigFile = System.getenv("SINNORI_PROJECT_CONFIG_FILE");
		System.out.printf("SINNORI_PROJECT_CONFIG_FILE[%s]%s", propSinnoriConfigFile, CommonStaticFinal.NEWLINE);
		
		Properties sinnoriConfigFileProp = new Properties();
		

		if (null == propSinnoriConfigFile) {
			propSinnoriConfigFile = "sinnori_config.properties";
		}
		
		File sinnoriConfigFile = new File(propSinnoriConfigFile);
		
		if (!sinnoriConfigFile.exists()) {
			System.out.printf("sinnori config file[%s] not exist%s", propSinnoriConfigFile, CommonStaticFinal.NEWLINE);
			System.exit(1);
		}
		
		if (!sinnoriConfigFile.isFile()) {
			System.out.printf("sinnori config file[%s] not file%s", sinnoriConfigFile.getAbsolutePath(), CommonStaticFinal.NEWLINE);
			System.exit(1);
		}
		
		if (!sinnoriConfigFile.canRead()) {
			System.out.printf("sinnori config file[%s] can't read%s", sinnoriConfigFile.getAbsolutePath(), CommonStaticFinal.NEWLINE);
			System.exit(1);
		}
		
		FileInputStream fis_sinnoriConfig_file = null;
		InputStreamReader isr_sinnoriConfig_file = null;
		try {

			fis_sinnoriConfig_file = new FileInputStream(sinnoriConfigFile);

			isr_sinnoriConfig_file = new InputStreamReader(
					fis_sinnoriConfig_file, SINNORI_CONFIG_FILE_CHARSET);

			sinnoriConfigFileProp.load(isr_sinnoriConfig_file);

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
		}*/
		
		String propSinnoriLogPath = System.getenv("SINNORI_PROJECT_LOG_PATH");
		
		
		System.out.printf("SINNORI_PROJECT_LOG_PATH[%s]%s", propSinnoriLogPath, CommonStaticFinalVars.NEWLINE);
		
		if (null == propSinnoriLogPath) {
			System.out.printf("SINNORI_PROJECT_LOG_PATH is null%s", CommonStaticFinalVars.NEWLINE);
			System.exit(1);
		}
		
		File sinnoriLogPath = new File(propSinnoriLogPath);
			
		if (!sinnoriLogPath.exists()) {
			System.out.printf("SINNORI_PROJECT_LOG_PATH[%s] not exist%s", propSinnoriLogPath, CommonStaticFinalVars.NEWLINE);
			System.exit(1);
		}
		
		if (!sinnoriLogPath.isDirectory()) {
			System.out.printf("SINNORI_PROJECT_LOG_PATH[%s] not directory%s", sinnoriLogPath.getAbsolutePath(), CommonStaticFinalVars.NEWLINE);
			System.exit(1);
		}
		
		if (!sinnoriLogPath.canRead() ) {
			System.out.printf("SINNORI_PROJECT_LOG_PATH[%s] can't read%s", sinnoriLogPath.getAbsolutePath(), CommonStaticFinalVars.NEWLINE);
			System.exit(1);
		}
		
		if (!sinnoriLogPath.canWrite()) {
			System.out.printf("SINNORI_PROJECT_LOG_PATH[%s] can't write%s", sinnoriLogPath.getAbsolutePath(), CommonStaticFinalVars.NEWLINE);
			System.exit(1);
		}
		
		// sinnoriConfigFileProp.setProperty("log4j.appender.logfile.File", sinnoriLogPath.getAbsolutePath()+File.separator+"logger.log");
		// sinnoriConfigFileProp.setProperty("log4j.appender.logfile.File", new StringBuilder(sinnoriLogPath.getAbsolutePath()).append(File.separator).append("logger.log").toString());
		
		
		// PropertyConfigurator.configure(sinnoriConfigFileProp);
		
		System.setProperty("SINNORI_PROJECT_LOG_PATH", propSinnoriLogPath);
	}

	/**
	 * 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스
	 */
	private static final class LogManagerHolder {
		static final LogManager singleton = new LogManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static LogManager getInstance() {
		return LogManagerHolder.singleton;
	}

	/**
	 * @return 신놀이 프레임 워크 전용 로거
	 */
	public Logger getLogger () {
		return LoggerFactory.getLogger("kr.pe.sinnori");
	}
}
