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
import kr.pe.sinnori.util.SinnoriWorker;

public class SinnoriAppClientMain implements CommonRootIF {
	
	public static void main(String[] args) {
		String projectName = System.getenv("SINNORI_PROJECT_NAME");
		if (null == projectName) {
			log.fatal("환경변수 SINNORI_PROJECT_NAME 가 정의되지 않았습니다.");
			System.exit(1);
		}
		
		if (projectName.trim().length() == 0) {
			log.fatal("환경변수 SINNORI_PROJECT_NAME 값이 지정되지 않았습니다. 환경변수 SINNORI_PROJECT_NAME 에 프로젝트 이름을 정해주세요.");
			System.exit(1);
		}
		
		/** 강제적인 클라이언트 모드로 변경 */
		try {
			/**
			 * 2013.07.03
			 * [20:39:44.203][INFO ][main](AbstractClientExecutor.java::105) - [1000]회 실행 평균 수행 시간=[0.641000] ms
			 * [20:40:23.599][INFO ][main](AbstractClientExecutor.java::105) - [1000]회 실행 평균 수행 시간=[0.580000] ms
			 * 
			 * 2013.07.23
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[0.985000] ms
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[1.056000] ms
			 * -------- 아래는 commonProjectInfo 에 messageExchangeProtocol 를 넣은 버전, 
			 * -------- 7월 3일자 보다 속도가 더 느린 이유는 아마도 헤더 검증때문으로 추정됨
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[0.890000] ms
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[0.912000] ms
			 */
			// SinnoriWorker.getInstance().start(projectName, "TestLocalAllDataType", 1);
			//SinnoriWorker.getInstance().start(projectName, "TestLocalAllDataType", 10000);
			
			/**
			 * 2013.07.09 TestNetEco
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[2.405000] ms
			 * 
			 * 2013.07.11 TestNetEco
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[2.135000] ms
			 * 
			 * 2013.07.24 TestNetEco
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[2.208000] ms
			 */
			SinnoriWorker.getInstance().start(projectName, "TestNetEco", 10);
			
			/**
			 * 2013.07.09 TestNetAllDataType
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[2.597000] ms
			 * 
			 * 2013.07.11 TestNetAllDataType, bytesVar2 size 9000 byte
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[5.546000] ms
			 * 
			 * 2013.07.24 TestNetAllDataType, bytesVar2 size 30000 byte
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[8.654000] ms
			 * 
			 * 2013.07.24 TestNetAllDataType, bytesVar2 size 9000 byte
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[4.479000] ms
			 * 
			 * 2013.07.24 TestNetAllDataType, bytesVar2 size 30000 byte : GatheringByteChannel 포기 버전
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[5.213000] ms
			 * 
			 * 2013.08.14 TestNetAllDataType, bytesVar2 size 30000 byte, LinkedHashMap
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[12.224000] ms
			 * 
			 * 2013.08.14 TestNetAllDataType, bytesVar2 size 9000 byte, LinkedHashMap
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[5.956000] ms
			 * 
			 * 2013.08.14 TestNetAllDataType, bytesVar2 size 30000 byte, ArrayList+HashMap, NoShare + Asyn
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[13.079000] ms
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[12.992000] ms
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[13.185000] ms
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[11.501000] ms
			 * 2013.08.14 TestNetAllDataType, bytesVar2 size 30000 byte, ArrayList+c, NoShare + Syn
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[12.552000] ms
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[12.219000] ms
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[12.506000] ms
			 * 
			 * 2013.08.19 bytesVar2 size 30000 byte, DJSON, NoShare + Asyn
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[26.768999] ms
			 * 
			 * 2013.12.12 bytesVar2 size 30000 byte, DHB, 4Kbyte Buffer, NoShare + Asyn, ArrayList+c
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:115) - [1000]회 실행 평균 수행 시간=[11.395000] ms
			 * 
			 */
			// SinnoriWorker.getInstance().start(projectName, "TestNetAllDataType", 10000);
			//SinnoriWorker.getInstance().start(projectName, "TestNetAllDataType", 1000);
			// SinnoriWorker.getInstance().start(projectName, "TestNetAllDataType2", 10000);
			
			
			//SinnoriWorker.getInstance().start(projectName, "TestBigSizeMessages");
			
			
			// SinnoriWorker.getInstance().start(projectName, "TestMessageHeader");
			/**
			 * 2013.08.14 bytesVar2 size 8000 byte
			 * :: LinkedHashmap 사용하는 경우
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [10000]회 실행 평균 수행 시간=[0.065300] ms
			 * :: ArrayList + HashMap 사용하는 경우
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [10000]회 실행 평균 수행 시간=[0.062400] ms
			 */
			// SinnoriWorker.getInstance().start(projectName, "SpeedTestV001", 10000);
			
			/**
			 * 2013.07.09 TestVirtualInputStream
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:67) - 수행 시간=[13.000000] ms
			 */
			//SinnoriWorker.getInstance().start(projectName, "TestVirtualInputStream");
			
			
			// SinnoriWorker.getInstance().start(projectName, "FileUpDownClientV1");
			// SinnoriWorker.getInstance().start(projectName, "FileUpDownClientV2");
			//SinnoriWorker.getInstance().start(projectName, "TestLoginService");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
