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

public class TestMesssage implements CommonRootIF {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			/**
			 * 2013.07.03
			 * [20:39:44.203][INFO ][main](AbstractClientExecutor.java::105) - [1000]회 실행 평균 수행 시간=[0.641000] ms
			 * [20:40:23.599][INFO ][main](AbstractClientExecutor.java::105) - [1000]회 실행 평균 수행 시간=[0.580000] ms
			 */
			// SinnoriWorker.getInstance().start("sample_simple_chat", "TestLocalAllDataType", 1000);
			
			/**
			 * 2013.07.09 TestNetEco
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[2.405000] ms
			 */
			// SinnoriWorker.getInstance().start("sample_simple_chat", "TestNetEco", 1000);
			
			/**
			 * 2013.07.09 TestNetAllDataType
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:105) - [1000]회 실행 평균 수행 시간=[2.597000] ms
			 */
			SinnoriWorker.getInstance().start("sample_simple_chat", "TestNetAllDataType", 1);
			
			
			// SinnoriWorker.getInstance().start("sample_simple_chat", "TestBigSizeMessages");
			
			
			// SinnoriWorker.getInstance().start("sample_simple_chat", "TestMessageHeader");
			
			// SinnoriWorker.getInstance().start("sample_simple_chat", "SpeedTestV001", 10000);
			
			/**
			 * 2013.07.09 TestVirtualInputStream
			 * INFO  kr.pe.sinnori.util.AbstractClientExecutor.execute(AbstractClientExecutor.java:67) - 수행 시간=[13.000000] ms
			 */
			// SinnoriWorker.getInstance().start("sample_simple_chat", "TestVirtualInputStream");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
		// System.gc();
	}
}
