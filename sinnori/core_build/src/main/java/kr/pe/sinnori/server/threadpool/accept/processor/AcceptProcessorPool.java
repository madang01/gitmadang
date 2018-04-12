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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.threadpool.ThreadPoolIF;
import kr.pe.sinnori.server.SocketResourceManagerIF;

/**
 * 서버에 접속 승인된 클라이언트(=소켓 채널) 등록 처리 쓰레드 폴.
 * 
 * @author Won Jonghoon
 * 
 */
public class AcceptProcessorPool implements ThreadPoolIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AcceptProcessorPool.class);
	private final Object monitor = new Object();		
	private final List<AcceptProcessor> pool = new ArrayList<AcceptProcessor>();
	
	
	
	private int poolMaxSize;
	private String projectName = null;
	private ArrayBlockingQueue<SocketChannel> acceptQueue;
	private SocketResourceManagerIF socketResourceManager = null;
	
	public AcceptProcessorPool(int poolSize, int poolMaxSize,
			String projectName, 
			ArrayBlockingQueue<SocketChannel> acceptQueue,
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
				String errorMessage = new StringBuilder("can't add a AcceptProcessor in the project[")
						.append(projectName)
						.append("] becase the number of AcceptProcessor is maximum[")
						.append(poolMaxSize)
						.append("]").toString();
				log.warn(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			try {
				AcceptProcessor acceptProcessor = new AcceptProcessor(size, projectName,
						acceptQueue, socketResourceManager);
				pool.add(acceptProcessor);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("failed to add a AcceptProcessor in the project[")
						.append(projectName)
						.append("], errmsg=")
						.append(e.getMessage()).toString(); 
				log.warn(errorMessage, e);
				throw new IllegalStateException(errorMessage);
			}
		}
	}
	
	@Override
	public int getPoolSize() {
		return pool.size();
	}

	@Override
	public void startAll() {
		for (AcceptProcessor handler: pool) {			
			if (handler.isAlive()) continue;
			handler.start();
		}
	}

	@Override
	public void stopAll() {
		for (AcceptProcessor handler: pool) {			
			if (handler.isAlive()) continue;
			handler.interrupt();
		}
	}
}
