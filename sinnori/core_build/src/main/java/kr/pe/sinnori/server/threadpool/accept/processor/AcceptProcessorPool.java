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

import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;
import kr.pe.sinnori.server.SocketResourceManagerIF;

/**
 * 서버에 접속 승인된 클라이언트(=소켓 채널) 등록 처리 쓰레드 폴.
 * 
 * @author Won Jonghoon
 * 
 */
public class AcceptProcessorPool extends AbstractThreadPool {
	
	private int poolMaxSize;
	private String projectName = null;
	private LinkedBlockingQueue<SocketChannel> acceptQueue;
	private SocketResourceManagerIF socketResourceManager = null;
	
	public AcceptProcessorPool(int poolSize, int poolMaxSize,
			String projectName, 
			LinkedBlockingQueue<SocketChannel> acceptQueue,
			SocketResourceManagerIF socketResourceManager) {
		if (poolSize <= 0) {
			String errorMessage = String.format("the parameter poolSize[%d] is less than or equal to zero", poolSize); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (poolMaxSize <= 0) {
			String errorMessage = String.format("the parameter poolMaxSize[%d] is less than or equal to zero", poolMaxSize); 
			throw new IllegalArgumentException(errorMessage);
		}

		if (poolSize > poolMaxSize) {
			String errorMessage = String.format("the parameter poolSize[%d] is greater than the parameter poolMaxSize[%d]", poolSize, poolMaxSize); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == projectName) {
			throw new IllegalArgumentException("the parameter projectName is null");
		}
		
		
		if (null == acceptQueue) {
			throw new IllegalArgumentException("the parameter acceptQueue is null");
		}
		
		if (null == socketResourceManager) {
			throw new IllegalArgumentException("the parameter socketResourceManager is null");
		}
		
		this.poolMaxSize = poolMaxSize;
		this.projectName = projectName;
		this.acceptQueue = acceptQueue;
		this.socketResourceManager = socketResourceManager;

		for (int i = 0; i < poolSize; i++) {
			try {
				addTask();
			} catch (IllegalStateException | NotSupportedException e) {
				log.error(e.getMessage(), e);
				System.exit(1);
			}
		}
	}

	@Override
	public void addTask() throws IllegalStateException, NotSupportedException {
		synchronized (monitor) {
			int size = pool.size();
			
			if (size >= poolMaxSize) {
				String errorMessage = String.format("can't add any more tasks becase the number of %s AcceptProcessorPool's tasks reached the maximum[%d] number", projectName, poolMaxSize); 
				log.warn(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			try {
				Thread acceptProcessor = new AcceptProcessor(size, projectName,
						acceptQueue, socketResourceManager);
				pool.add(acceptProcessor);
			} catch (Exception e) {
				String errorMessage = String.format("failed to add a %s AcceptProcessorPool's task becase error occured::errmsg={}", projectName, e.getMessage()); 
				log.warn(errorMessage, e);
				throw new IllegalStateException(errorMessage);
			}
		}
	}
}
