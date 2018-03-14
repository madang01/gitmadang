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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;

import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.threadpool.IEOServerThreadPoolSetManagerIF;

/**
 * 서버 비지니스 로직 수행자 쓰레드 폴
 * 
 * @author Won Jonghoon
 */
public class ServerExecutorPool extends AbstractThreadPool implements ServerExecutorPoolIF {
	// execuate_processor_pool_max_size
	
	private int poolMaxSize;
	private String projectName = null;
	private int inputMessageQueueSize;
	private MessageProtocolIF messageProtocol= null;
	private SocketResourceManagerIF socketResourceManager;
	private ServerObjectCacheManagerIF serverObjectCacheManager = null;
	
	
	
	public ServerExecutorPool( 
			int poolSize, 
			int poolMaxSize,
			String projectName,
			int inputMessageQueueSize,			
			MessageProtocolIF messageProtocol,
			SocketResourceManagerIF socketResourceManager,
			ServerObjectCacheManagerIF serverObjectCacheManager,
			IEOServerThreadPoolSetManagerIF ieoThreadPoolManager) throws SinnoriConfigurationException {
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
		
		if (null == socketResourceManager) {
			throw new IllegalArgumentException("the parameter socketResourceManager is null");
		}
		
		if (null == serverObjectCacheManager) {
			throw new IllegalArgumentException("the parameter serverObjectCacheManager is null");
		}
		
		if (null == ieoThreadPoolManager) {
			throw new IllegalArgumentException("the parameter ieoThreadPoolManager is null");
		}
		
		this.poolMaxSize = poolMaxSize;
		this.projectName = projectName;		
		this.inputMessageQueueSize = inputMessageQueueSize;
		this.messageProtocol = messageProtocol;
		this.socketResourceManager = socketResourceManager;
		this.serverObjectCacheManager =  serverObjectCacheManager;
		
		ieoThreadPoolManager.setExecutorPool(this);		
		
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
		ArrayBlockingQueue<FromLetter> inputMessageQueue = new
				ArrayBlockingQueue<FromLetter>(inputMessageQueueSize);
		
		synchronized (monitor) {
			int size = pool.size();

			if (size >= poolMaxSize) {
				String errorMessage = String.format("can't add any more tasks becase the number of %s ServerExecutorPool's tasks reached the maximum[%d] number", projectName, poolMaxSize); 
				log.warn(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			try {
				Thread task = new ServerExecutor(size, 
						projectName, 
						inputMessageQueue, 
						messageProtocol, 
						socketResourceManager, 
						serverObjectCacheManager);
				pool.add(task);
			} catch (Exception e) {
				String errorMessage = String.format("failed to add a %s ServerExecutorPool's task becase error occured::errmsg={}", projectName, e.getMessage()); 
				log.warn(errorMessage, e);
				throw new IllegalStateException(errorMessage);
			}
		}
	}

	@Override
	public ServerExecutorIF getExecutorWithMinimumNumberOfSockets() {
		Iterator<Thread> poolIter = pool.iterator();
		
		if (! poolIter.hasNext()) {
			throw new NoSuchElementException("ServerExecutorPool empty");
		}
		
		ServerExecutorIF minServerExecutor = (ServerExecutorIF)poolIter.next();
		int min = minServerExecutor.getNumberOfSocket();
		
		while (poolIter.hasNext()) {
			ServerExecutorIF serverExecutor = (ServerExecutorIF) poolIter.next();
			int numberOfSocket = serverExecutor.getNumberOfSocket();
			if (numberOfSocket < min) {
				min = numberOfSocket;
				minServerExecutor = serverExecutor;
			}
		}

		return minServerExecutor;
	}
}
