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

package kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage;

import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler.InputMessageWriter;
import kr.pe.sinnori.client.io.LetterToServer;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;

/**
 * 클라이언트 입력 메시지 소켓 쓰기 담당 쓰레드 폴.
 * 
 * @author Won Jonghoon
 */
public class InputMessageWriterPool extends AbstractThreadPool {
	private int maxHandler;
	private ClientProjectConfig clientProjectConfig = null;
	private LinkedBlockingQueue<LetterToServer> inputMessageQueue;
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager;
	
	
	/**
	 * 생성자
	 * @param size 클라이언트 입력 메시지 소켓 쓰기 담당 쓰레드 초기 갯수
	 * @param max 클라이언트 입력 메시지 소켓 쓰기 담당 쓰레드 최대 갯수
	 * @param clientProjectConfig 프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param messageProtocol 메시지 교환 프로프로콜
	 * @param dataPacketBufferQueueManager 데이터 패킷 큐 관리자
	 */
	public InputMessageWriterPool(int size, int max,
			ClientProjectConfig clientProjectConfig, 
			LinkedBlockingQueue<LetterToServer> inputMessageQueue,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("%s 파라미터 size 는 0보다 커야 합니다.", clientProjectConfig.getProjectName()));
		}
		if (max <= 0) {
			throw new IllegalArgumentException(String.format("%s 파라미터 max 는 0보다 커야 합니다.", clientProjectConfig.getProjectName()));
		}

		if (size > max) {
			throw new IllegalArgumentException(String.format(
					"%s 파라미터 size[%d]는 파라미터 max[%d]보다 작거나 같아야 합니다.", clientProjectConfig.getProjectName(), size, max));
		}
		
		this.maxHandler = max;
		this.clientProjectConfig = clientProjectConfig;
		this.inputMessageQueue = inputMessageQueue;
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		
		for (int i = 0; i < size; i++) {
			addHandler();
		}
	}

	@Override
	public void addHandler() {
		synchronized (monitor) {
			int size = pool.size();

			if (size < maxHandler) {
				try {
					Thread handler = new InputMessageWriter(size, clientProjectConfig,
							inputMessageQueue, dataPacketBufferQueueManager);
					
					pool.add(handler);
				} catch (Exception e) {
					String errorMessage = String.format("%s InputMessageWriter[%d] 등록 실패", clientProjectConfig.getProjectName(), size); 
					log.warn(errorMessage, e);
					throw new RuntimeException(errorMessage);
				}
			} else {
				String errorMessage = String.format("%s InputMessageWriter 최대 갯수[%d]를 넘을 수 없습니다.", clientProjectConfig.getProjectName(), maxHandler); 
				log.warn(errorMessage);
				throw new RuntimeException(errorMessage);
			}
		}
	}
	
}
