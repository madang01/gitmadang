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

package kr.pe.sinnori.server.threadpool.inputmessage;

import java.util.Iterator;
import java.util.NoSuchElementException;

import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.threadpool.IEOServerThreadPoolSetManagerIF;

/**
 * 입력 메시지 소켓 읽기 담당 쓰레드 폴
 * 
 * @author Won Jonghoon
 * 
 */
public class InputMessageReaderPool extends AbstractThreadPool implements
		InputMessageReaderPoolIF {
	private int poolMaxSize;
	private String projectName = null;	
	private long wakeupIntervalOfSelectorFoReadEventOnly;
	private MessageProtocolIF messageProtocol;
	private SocketResourceManagerIF socketResourceManager;
	
	public InputMessageReaderPool( 
			int poolSize, 
			int poolMaxSize, 
			String projectName,
			long wakeupIntervalOfSelectorFoReadEventOnly,
			MessageProtocolIF messageProtocol,
			SocketResourceManagerIF socketResourceManager,
			IEOServerThreadPoolSetManagerIF ieoThreadPoolManager) {
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
		
		if (wakeupIntervalOfSelectorFoReadEventOnly < 0) {
			String errorMessage = String.format("the parameter wakeupIntervalOfSelectorFoReadEventOnly[%d] is less than zero", wakeupIntervalOfSelectorFoReadEventOnly); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}
		
		
		if (null == socketResourceManager) {
			throw new IllegalArgumentException("the parameter socketResourceManager is null");
		}
		
		if (null == ieoThreadPoolManager) {
			throw new IllegalArgumentException("the parameter ieoThreadPoolManager is null");
		}
		
		this.poolMaxSize = poolMaxSize;
		this.projectName = projectName;
		this.wakeupIntervalOfSelectorFoReadEventOnly = wakeupIntervalOfSelectorFoReadEventOnly;
		this.messageProtocol = messageProtocol;
		this.socketResourceManager = socketResourceManager;	
		
		ieoThreadPoolManager.setInputMessageReaderPool(this);

		for (int i = 0; i < poolSize; i++) {
			addTask();
		}
	}

	@Override
	public void addTask() throws IllegalStateException {
		synchronized (monitor) {
			int size = pool.size();
			
			if (size >= poolMaxSize) {
				String errorMessage = String.format("can't add any more tasks becase the number of %s InputMessageReaderPool's tasks reached the maximum[%d] number", projectName, poolMaxSize); 
				log.warn(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			try {
				Thread inputMessageReader = new InputMessageReader(projectName, 
						size, 
						wakeupIntervalOfSelectorFoReadEventOnly, 
						messageProtocol,  
						socketResourceManager);
				pool.add(inputMessageReader);
			} catch (Exception e) {
				String errorMessage = String.format("failed to add a %s InputMessageReaderPool's task becase error occured::errmsg={}", projectName, e.getMessage()); 
				log.warn(errorMessage, e);
				throw new IllegalStateException(errorMessage);
			}
		}
	}	
	
	@Override
	public InputMessageReaderIF getInputMessageReaderWithMinimumNumberOfSockets() {
		Iterator<Thread> poolIter = pool.iterator();
		
		if (! poolIter.hasNext()) {
			throw new NoSuchElementException("InputMessageReaderPool empty");
		}
		
		InputMessageReaderIF minInputMessageReader = (InputMessageReaderIF)poolIter.next();
		int min = minInputMessageReader.getNumberOfSocket();
		
		while (poolIter.hasNext()) {
			InputMessageReaderIF inputMessageReader = (InputMessageReaderIF) poolIter.next();
			int numberOfSocket = inputMessageReader.getNumberOfSocket();
			if (numberOfSocket < min) {
				min = numberOfSocket;
				minInputMessageReader = inputMessageReader;
			}
		}
		
		return minInputMessageReader;
	}
}
