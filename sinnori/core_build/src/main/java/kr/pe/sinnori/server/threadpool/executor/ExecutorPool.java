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

import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.threadpool.IEOThreadPoolSetManagerIF;
import kr.pe.sinnori.server.threadpool.executor.handler.Executor;
import kr.pe.sinnori.server.threadpool.executor.handler.ExecutorIF;

/**
 * 서버 비지니스 로직 수행자 쓰레드 폴
 * 
 * @author Won Jonghoon
 */
public class ExecutorPool extends AbstractThreadPool implements ExecutorPoolIF {
	// execuate_processor_pool_max_size
	
	private int maxHandler;
	// private LinkedBlockingQueue<FromLetter> inputMessageQueue;
	private int inputMessageQueueSize;
	private MessageProtocolIF messageProtocol= null;
	private SocketResourceManagerIF socketResourceManager;
	private ServerObjectCacheManagerIF serverObjectCacheManager = null;
	
	private String projectName = null;
	
	public ExecutorPool(String projectName, 
			int size, 
			int max,
			// LinkedBlockingQueue<FromLetter> inputMessageQueue,
			int inputMessageQueueSize,
			MessageProtocolIF messageProtocol,
			SocketResourceManagerIF socketResourceManager,
			ServerObjectCacheManagerIF serverObjectCacheManager,
			IEOThreadPoolSetManagerIF ieoThreadPoolManager) {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("%s 파라미터 size 는 0보다 커야 합니다.", projectName));
		}
		if (max <= 0) {
			throw new IllegalArgumentException(String.format("%s 파라미터 max 는 0보다 커야 합니다.", projectName));
		}

		if (size > max) {
			throw new IllegalArgumentException(String.format(
					"%s 파라미터 size[%d]는 파라미터 max[%d]보다 작거나 같아야 합니다.", projectName, size, max));
		}
		
		this.maxHandler = max;
		this.projectName = projectName;		
		this.inputMessageQueueSize = inputMessageQueueSize;
		this.messageProtocol = messageProtocol;
		this.socketResourceManager = socketResourceManager;
		this.serverObjectCacheManager = serverObjectCacheManager;
		
		ieoThreadPoolManager.setExecutorPool(this);

		for (int i = 0; i < size; i++) {
			addHandler();
		}
	}

	@Override
	public void addHandler() {
		LinkedBlockingQueue<FromLetter> inputMessageQueue = new
				LinkedBlockingQueue<FromLetter>(inputMessageQueueSize);
		
		synchronized (monitor) {
			int size = pool.size();

			if (size > maxHandler) {
				String errorMessage = String.format("%s ExecutorProcessor 최대 갯수[%d]를 넘을 수 없습니다.", projectName, maxHandler); 
				log.warn(errorMessage);
				throw new RuntimeException(errorMessage);
			}
			
			try {
				Thread handler = new Executor(size, 
						projectName, 
						inputMessageQueue, 
						messageProtocol, 
						socketResourceManager, 
						serverObjectCacheManager);
				pool.add(handler);
			} catch (Exception e) {
				String errorMessage = String.format("%s ExecutorProcessor[%d] 등록 실패", projectName, size); 
				log.warn(errorMessage, e);
				throw new RuntimeException(errorMessage);
			}
		}
	}

	@Override
	public ExecutorIF getExecutorWithMinimumMumberOfSockets() {
		ExecutorIF executorWithMinimumMumberOfSockets = null;
		int minimumMumberOfSockets = Integer.MAX_VALUE;

		int size = pool.size();
		for (int i = 0; i < size; i++) {
			ExecutorIF handler = (ExecutorIF) pool.get(i);
			int numberOfSocket = handler.getNumberOfSocket();
			if (numberOfSocket < minimumMumberOfSockets) {
				minimumMumberOfSockets = numberOfSocket;
				executorWithMinimumMumberOfSockets = handler;
			}
		}
		
		return executorWithMinimumMumberOfSockets;
	}
}
