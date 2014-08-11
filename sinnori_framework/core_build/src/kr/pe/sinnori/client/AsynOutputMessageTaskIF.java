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

package kr.pe.sinnori.client;

import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.protocol.ReceivedLetter;

/**
 * 익명 서버 메시지 처리 작업 인터페이스
 * @author Jonghoon won
 *
 */
public interface AsynOutputMessageTaskIF {
	/**
	 * 익명 서버 메시지 처리 작업
	 * @param clientProjectConfig 프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스  
	 * @param outObj 출력 메시지
	 */
	public void doTask(ClientProjectConfig clientProjectConfig, ReceivedLetter receivedLetter);
}
