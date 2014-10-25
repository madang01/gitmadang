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

package main;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.util.SinnoriWorker;

public class SinnoriAppClientMain implements CommonRootIF {
	
	public static void main(String[] args) {
		String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);
		if (null == projectName) {
			log.error("자바 시스템 환경 변수[{}] 가 정의되지 않았습니다.", CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);
			System.exit(1);
		}
		
		if (projectName.trim().length() == 0) {
			log.error("자바 시스템 환경 변수[{}] 값[{}]이 빈 문자열 있습니다.", CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME, projectName);
			System.exit(1);
		}

		/** 강제적인 클라이언트 모드로 변경 */
		try {
			SinnoriWorker.getInstance().start(projectName, "SyncFileUpDownClient");
			//SinnoriWorker.getInstance().start(projectName, "ASynFileUpDownClient");
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
}
