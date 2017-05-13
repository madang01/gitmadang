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

import kr.pe.sinnori.client.io.ClientOutputMessageQueueWrapper;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;

/**
 * 비동기 입출력 지원용 랩 출력 메시지 큐를 원소로 하는 큐 관리자 
 * @author Won Jonghoon
 *
 */
public interface ClientOutputMessageQueueQueueMangerIF {
	/**
	 * 랩 출력 메시지 큐를 원소로 하는 큐에서 랩 출력 메시지 큐를 얻어온다.
	 * @return 데이터 패킷 버퍼
	 */
	public ClientOutputMessageQueueWrapper pollOutputMessageQueue() throws NoMoreOutputMessageQueueException;

	/**
	 * 랩 출력 메시지 큐를 원소로 하는 큐에 랩 출력 메시지 큐를 반환한다.
	 * @param outputMessageQueueWrapper 큐에 반환하고자 하는 랩 출력 메시지 큐
	 */
	public void putOutputMessageQueue(ClientOutputMessageQueueWrapper outputMessageQueueWrapper);
}
