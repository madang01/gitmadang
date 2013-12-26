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

package kr.pe.sinnori.main;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.util.SinnoriWorker;

public class TestMesssageMain implements CommonRootIF {
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		
		/**
		 * 주의점) SinnoriWorker 는 OS 환경 설정 변수 CLASSPATH 에 호출을 원하는 클래스가 있어야 한다.
		 * 이클립스의 경우 외부 클래스 경로를 추가해 주어야 한다. 
		 * 예를 들면 /home/madang01/gitsinnori/ 경로에 신놀이를 설치했다면 아래와 같은 경로
		 * /home/madang01/gitsinnori/sinnori_framework/project/sample_simple_ftp/client_build/app_build/build
		 * 를 외부 클래스 경로로 잡아야하며 동시에 clinet_build 에서 ant all 를 수행하여 
		 * build 경로에 동적으로 로딩할 클래스를 미리 컴파일 시켜 놓아야 한다.
		 */
		/**
		 * 2013.07.03
		 * [20:39:44.203][INFO ][main](AbstractClientExecutor.java::105) - [1000]회 실행 평균 수행 시간=[0.641000] ms
		 * [20:40:23.599][INFO ][main](AbstractClientExecutor.java::105) - [1000]회 실행 평균 수행 시간=[0.580000] ms
		 */
		// SinnoriWorker.getInstance().start("sample_simple_ftp", "TestLocalAllDataType", 1000);
		
		/**
		 * 2013.07.09 TestNetEco
		 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[2.405000] ms
		 */
		// SinnoriWorker.getInstance().start("sample_simple_ftp", "TestNetEco", 1000);
		
		/**
		 * 2013.07.09 TestNetAllDataType
		 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[2.597000] ms
		 */
		SinnoriWorker.getInstance().start("sample_simple_ftp", "TestNetAllDataType", 1);
		
		
		// SinnoriWorker.getInstance().start("sample_simple_ftp", "TestBigSizeMessages");
		
		
		// SinnoriWorker.getInstance().start("sample_simple_ftp", "TestMessageHeader");
		
		// SinnoriWorker.getInstance().start("sample_simple_ftp", "SpeedTestV001", 10000);
		
		/**
		 * 2013.07.09 TestVirtualInputStream
		 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:67) - 수행 시간=[13.000000] ms
		 */
		// SinnoriWorker.getInstance().start("sample_simple_ftp", "TestVirtualInputStream");
		
		// System.gc();
		
	}
}
