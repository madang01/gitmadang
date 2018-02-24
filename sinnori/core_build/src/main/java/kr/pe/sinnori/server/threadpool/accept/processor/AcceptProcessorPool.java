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

package kr.pe.sinnori.server.threadpool.accept.processor;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.threadpool.AbstractThreadPool;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.threadpool.accept.processor.handler.AcceptProcessor;

/**
 * 서버에 접속 승인된 클라이언트(=소켓 채널) 등록 처리 쓰레드 폴.
 * 
 * @author Won Jonghoon
 * 
 */
public class AcceptProcessorPool extends AbstractThreadPool {
	private LinkedBlockingQueue<SocketChannel> acceptQueue;
	private int max;
	private SocketResourceManagerIF socketResourceManager = null;

	private String projectName = null;

	
	public AcceptProcessorPool(String projectName, int size, int max,
			LinkedBlockingQueue<SocketChannel> acceptQueue,
			SocketResourceManagerIF socketResourceManager) {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format(
					"%s 파라미터 size 는 0보다 커야 합니다.", projectName));
		}
		if (max <= 0) {
			throw new IllegalArgumentException(String.format(
					"%s 파라미터 max 는 0보다 커야 합니다.", projectName));
		}

		if (size > max) {
			throw new IllegalArgumentException(String.format(
					"%s 파라미터 size[%d]는 파라미터 max[%d]보다 작거나 같아야 합니다.",
					projectName, size, max));
		}

		this.acceptQueue = acceptQueue;
		this.max = max;
		this.projectName = projectName;
		this.socketResourceManager = socketResourceManager;

		for (int i = 0; i < size; i++) {
			addHandler();
		}
	}

	@Override
	public void addHandler() throws IllegalStateException{
		synchronized (monitor) {
			int size = pool.size();
			
			if (size > max) {
				String errorMessage = String.format(
						"%s AcceptProcessor 최대 갯수[%d]를 넘을 수 없습니다.",
						projectName, max);
				log.warn(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			try {
				Thread acceptProcessor = new AcceptProcessor(size, projectName,
						acceptQueue, socketResourceManager);
				pool.add(acceptProcessor);
			} catch (Exception e) {
				String errorMessage = String.format(
						"%s AcceptProcessor[%d] 등록 실패", projectName, size);
				log.warn(errorMessage, e);
				throw new IllegalStateException(errorMessage);
			}
		}
	}
}
