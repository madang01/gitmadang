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

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SinnoriSqlSessionFactoryIF;
import kr.pe.sinnori.server.io.LetterFromClient;
import kr.pe.sinnori.server.io.LetterToClient;
import kr.pe.sinnori.server.threadpool.executor.handler.Executor;

/**
 * 서버 비지니스 로직 수행자 쓰레드 폴
 * 
 * @author Jonghoon Won
 */
public class ExecutorPool extends AbstractThreadPool {
	// execuate_processor_pool_max_size
	
	private int maxHandler;
	private ServerProjectConfig serverProjectConfig;
	private LoginManagerIF loginManager;
	private LinkedBlockingQueue<LetterFromClient> inputMessageQueue;
	private LinkedBlockingQueue<LetterToClient> ouputMessageQueue;
	private MessageProtocolIF messageProtocol= null;
	private ServerObjectCacheManagerIF serverObjectCacheManager = null;
	private SinnoriSqlSessionFactoryIF sqlSessionFactory = null;
	
	/**
	 * 생성자
	 * @param size 서버 비지니스 로직 수행자 쓰레드 갯수
	 * @param max 서버 비지니스 로직 수행자 쓰레드 최대 갯수
	 * @param serverProjectConfig 프로젝트의 공통 포함한 서버 환경 변수
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param ouputMessageQueue 출력 메시지 큐
	 * @param messageProtocol 서버 프로젝트의 메시지 프로토콜
	 * @param loginManager 로그인 관리자
	 * @param serverObjectCacheManager 서버 객체 캐쉬 관리자
	 * @param sqlSessionFactory 서버 프로젝트의 Mybatis SqlSessionFactory
	 */
	public ExecutorPool(int size, int max,
			ServerProjectConfig serverProjectConfig,			
			LinkedBlockingQueue<LetterFromClient> inputMessageQueue,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue,
			MessageProtocolIF messageProtocol,
			LoginManagerIF loginManager,
			ServerObjectCacheManagerIF serverObjectCacheManager,
			SinnoriSqlSessionFactoryIF sqlSessionFactory) {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("%s 파라미터 size 는 0보다 커야 합니다.", serverProjectConfig.getProjectName()));
		}
		if (max <= 0) {
			throw new IllegalArgumentException(String.format("%s 파라미터 max 는 0보다 커야 합니다.", serverProjectConfig.getProjectName()));
		}

		if (size > max) {
			throw new IllegalArgumentException(String.format(
					"%s 파라미터 size[%d]는 파라미터 max[%d]보다 작거나 같아야 합니다.", serverProjectConfig.getProjectName(), size, max));
		}
		
		this.maxHandler = max;
		this.serverProjectConfig = serverProjectConfig;
		this.loginManager = loginManager;
		this.inputMessageQueue = inputMessageQueue;
		this.ouputMessageQueue = ouputMessageQueue;
		this.messageProtocol = messageProtocol;
		this.serverObjectCacheManager = serverObjectCacheManager;
		this.sqlSessionFactory = sqlSessionFactory;

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
					Thread handler = new Executor(size, serverProjectConfig, 
							inputMessageQueue, ouputMessageQueue, messageProtocol, 
							loginManager, serverObjectCacheManager, sqlSessionFactory);
					pool.add(handler);
				} catch (Exception e) {
					String errorMessage = String.format("%s ExecutorProcessor[%d] 등록 실패", serverProjectConfig.getProjectName(), size); 
					log.warn(errorMessage, e);
					throw new RuntimeException(errorMessage);
				}
			} else {
				String errorMessage = String.format("%s ExecutorProcessor 최대 갯수[%d]를 넘을 수 없습니다.", serverProjectConfig.getProjectName(), maxHandler); 
				log.warn(errorMessage);
				throw new RuntimeException(errorMessage);
			}
		}
	}
}
