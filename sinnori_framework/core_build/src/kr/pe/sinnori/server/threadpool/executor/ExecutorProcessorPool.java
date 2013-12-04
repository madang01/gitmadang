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

package kr.pe.sinnori.server.threadpool.executor;

import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.SererExecutorClassLoaderManagerIF;
import kr.pe.sinnori.server.io.LetterFromClient;
import kr.pe.sinnori.server.io.LetterToClient;
import kr.pe.sinnori.server.threadpool.executor.handler.ExecutorProcessor;

/**
 * 서버 비지니스 로직 수행자 쓰레드 폴
 * 
 * @author Jonghoon Won
 */
public class ExecutorProcessorPool extends AbstractThreadPool {
	// execuate_processor_pool_max_size
	private int maxHandler;
	private CommonProjectInfo commonProjectInfo;
	private MessageMangerIF messageManger;
	private SererExecutorClassLoaderManagerIF sererExecutorClassLoaderManager;
	
	private ClientResourceManagerIF clientResourceManager;
	private LinkedBlockingQueue<LetterFromClient> inputMessageQueue;
	private LinkedBlockingQueue<LetterToClient> ouputMessageQueue;
	
	/**
	 * 생성자
	 * @param size 서버 비지니스 로직 수행자 쓰레드 갯수
	 * @param max 서버 비지니스 로직 수행자 쓰레드 최대 갯수
	 * @param commonProjectInfo 연결 공통 데이터
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param ouputMessageQueue 출력 메시지 큐
	 * @param messageManger 메시지 관리자
	 * @param sererExecutorClassLoaderManager 서버 비지니스 로직 클래스 로더 관리자
	 * @param clientResourceManager 클라이언트 자원 관리자
	 */
	public ExecutorProcessorPool(int size, int max,
			CommonProjectInfo commonProjectInfo, 
			LinkedBlockingQueue<LetterFromClient> inputMessageQueue,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue,
			MessageMangerIF messageManger,
			SererExecutorClassLoaderManagerIF sererExecutorClassLoaderManager,
			ClientResourceManagerIF clientResourceManager) {
		if (size <= 0) {
			throw new IllegalArgumentException("파라미터 초기 핸들러 갯수는 0보다 커야 합니다.");
		}
		if (max <= 0) {
			throw new IllegalArgumentException("파라미터 최대 핸들러 갯수는 0보다 커야 합니다.");
		}

		if (size > max) {
			throw new IllegalArgumentException(String.format(
					"파라미터 초기 핸들러 갯수[%d]는 최대 핸들러 갯수[%d]보다 작거나 같아야 합니다.", size,
					max));
		}

		this.maxHandler = max;
		this.commonProjectInfo = commonProjectInfo;
		this.inputMessageQueue = inputMessageQueue;
		this.ouputMessageQueue = ouputMessageQueue;
		this.messageManger = messageManger;
		this.sererExecutorClassLoaderManager = sererExecutorClassLoaderManager;
		// this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		this.clientResourceManager = clientResourceManager;

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
					Thread handler = new ExecutorProcessor(size,
							commonProjectInfo, inputMessageQueue, ouputMessageQueue,
							messageManger, sererExecutorClassLoaderManager, clientResourceManager);
					pool.add(handler);
				} catch (Exception e) {
					log.warn("MesgProcessor handler 등록 실패", e);
				}
			}
		}
	}
}
