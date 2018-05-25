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

package kr.pe.codda.server.threadpool.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.WrapReadableMiddleObject;
import kr.pe.codda.common.threadpool.ThreadPoolIF;
import kr.pe.codda.server.ServerObjectCacheManagerIF;
import kr.pe.codda.server.AcceptedConnectionManagerIF;
import kr.pe.codda.server.ProjectLoginManagerIF;

/**
 * 서버 비지니스 로직 수행자 쓰레드 폴
 * 
 * @author Won Jonghoon
 */
public class ServerExecutorPool implements ThreadPoolIF, ServerExecutorPoolIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ServerExecutorPool.class);
	private final Object monitor = new Object();
	private final List<ServerExecutorIF> pool = new ArrayList<ServerExecutorIF>();
	
	private int poolMaxSize;
	private String projectName = null;
	private int inputMessageQueueSize;
	private ProjectLoginManagerIF projectLoginManager = null;
	private MessageProtocolIF messageProtocol= null;
	private AcceptedConnectionManagerIF socketResourceManager;
	private ServerObjectCacheManagerIF serverObjectCacheManager = null;

	public ServerExecutorPool( 
			int poolSize, 
			int poolMaxSize,
			String projectName,
			int inputMessageQueueSize,
			ProjectLoginManagerIF projectLoginManager,
			MessageProtocolIF messageProtocol,
			AcceptedConnectionManagerIF socketResourceManager,
			ServerObjectCacheManagerIF serverObjectCacheManager) throws CoddaConfigurationException {
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
		
		if (inputMessageQueueSize <= 0) {
			String errorMessage = String.format("the parameter inputMessageQueueSize[%d] is less than or equal to zero", inputMessageQueueSize); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}
		
		if (null == projectLoginManager) {
			throw new IllegalArgumentException("the parameter projectLoginManager is null");
		}
		
		if (null == socketResourceManager) {
			throw new IllegalArgumentException("the parameter socketResourceManager is null");
		}
		
		if (null == serverObjectCacheManager) {
			throw new IllegalArgumentException("the parameter serverObjectCacheManager is null");
		}
		
		this.poolMaxSize = poolMaxSize;
		this.projectName = projectName;		
		this.inputMessageQueueSize = inputMessageQueueSize;
		this.projectLoginManager = projectLoginManager;
		this.messageProtocol = messageProtocol;
		this.socketResourceManager = socketResourceManager;
		this.serverObjectCacheManager =  serverObjectCacheManager;
		
		socketResourceManager.setServerExecutorPool(this);
		
		
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
		ArrayBlockingQueue<WrapReadableMiddleObject> inputMessageQueue = new
				ArrayBlockingQueue<WrapReadableMiddleObject>(inputMessageQueueSize);
		
		synchronized (monitor) {
			int size = pool.size();

			if (size >= poolMaxSize) {
				String errorMessage = new StringBuilder("can't add a ServerExecutor in the project[")
						.append(projectName)
						.append("] becase the number of ServerExecutor is maximum[")
						.append(poolMaxSize)
						.append("]").toString();
				
				log.warn(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			try {
				ServerExecutorIF handler = new ServerExecutor(size, 
						projectName, 
						inputMessageQueue, 
						projectLoginManager,
						messageProtocol, 
						socketResourceManager, 
						serverObjectCacheManager);
				pool.add(handler);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("failed to add a ServerExecutor in the project[")
						.append(projectName)
						.append("], errmsg=")
						.append(e.getMessage()).toString();  
				log.warn(errorMessage, e);
				throw new IllegalStateException(errorMessage);
			}
		}
	}

	
	public ServerExecutorIF getExecutorWithMinimumNumberOfSockets() {
		if (pool.isEmpty()) {
			throw new NoSuchElementException("ServerExecutorPool empty");
		}
		
		int min = Integer.MAX_VALUE;
		ServerExecutorIF minServerExecutor = null;

		for (ServerExecutorIF serverExecutor : pool) {
			int numberOfSocket = serverExecutor.getNumberOfConnection();
			if (numberOfSocket < min) {
				min = numberOfSocket;
				minServerExecutor = serverExecutor;
			}
		}

		return minServerExecutor;
	}
	
	@Override
	public int getPoolSize() {
		return pool.size();
	}

	@Override
	public void startAll() {
		for (ServerExecutorIF handler: pool) {			
			if (handler.isAlive()) continue;
			handler.start();
		}
	}

	@Override
	public void stopAll() {
		for (ServerExecutorIF handler: pool) {			
			if (handler.isAlive()) continue;
			handler.interrupt();
		}
	}
}
