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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.threadpool.ThreadPoolIF;

/**
 * 클라이언트 입력 메시지 소켓 쓰기 담당 쓰레드 폴.
 * 
 * @author Won Jonghoon
 */
public class InputMessageWriterPool implements ThreadPoolIF, InputMessageWriterPoolIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(InputMessageWriterPool.class);
	private final List<InputMessageWriterIF> pool = new ArrayList<InputMessageWriterIF>();
	
	
	private String projectName = null;
	private int inputMessageQueueSize;
	private ClientMessageUtilityIF clientMessageUtility = null;

	public InputMessageWriterPool(int poolSize,
			String projectName, 
			int inputMessageQueueSize, 
			ClientMessageUtilityIF clientMessageUtility) {
		if (poolSize <= 0) {
			String errorMessage = String.format("the parameter poolSize[%d] is less than or equal to zero", poolSize); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == projectName) {
			throw new IllegalArgumentException("the parameter projectName is null");
		}
		
		if (inputMessageQueueSize <= 0) {
			String errorMessage = String.format("the parameter inputMessageQueueSize[%d] is less than or equal to zero", inputMessageQueueSize); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == clientMessageUtility) {
			throw new IllegalArgumentException("the parameter clientMessageUtility is null");
		}
		

		this.projectName = projectName;
		this.inputMessageQueueSize = inputMessageQueueSize;
		this.clientMessageUtility = clientMessageUtility;

		for (int i = 0; i < poolSize; i++) {
			try {
				innserAddTask();
			} catch (IllegalStateException e) {
				log.error(e.getMessage(), e);
				System.exit(1);
			}
		}
	}
	
	private void innserAddTask() throws IllegalStateException {
		ArrayBlockingQueue<ToLetter> inputMessageQueue = new ArrayBlockingQueue<ToLetter>(inputMessageQueueSize);
		
		int size = pool.size();

		try {
			InputMessageWriterIF handler = new InputMessageWriter(projectName, size, inputMessageQueue,
					clientMessageUtility);

			pool.add(handler);
		} catch (Exception e) {
			String errorMessage = String.format("%s InputMessageWriter[%d] 등록 실패", projectName, size);
			log.warn(errorMessage, e);
			throw new IllegalStateException(errorMessage);
		}
	}

	@Override
	public InputMessageWriterIF getInputMessageWriterWithMinimumNumberOfConnetion() {
		if (pool.isEmpty()) {
			throw new NoSuchElementException("InputMessageWriterPool empty");
		}
		
		int min = Integer.MAX_VALUE;
		InputMessageWriterIF minInputMessageWriter = null;

		for (InputMessageWriterIF handler : pool) {
			int numberOfAsynConnection = handler.getNumberOfConnection();
			if (numberOfAsynConnection < min) {
				minInputMessageWriter = handler;
				min = numberOfAsynConnection;
			}
		}

		return minInputMessageWriter;
	}


	@Override
	public void addTask() throws IllegalStateException, NotSupportedException {
		String errorMessage = "this InputMessageWriterPool dosn't support this addTask method";
		throw new NotSupportedException(errorMessage);
	}

	@Override
	public int getPoolSize() {
		return pool.size();
	}

	@Override
	public void startAll() {
		for (InputMessageWriterIF handler: pool) {			
			if (handler.isAlive()) continue;
			handler.start();
		}
	}

	@Override
	public void stopAll() {
		for (InputMessageWriterIF handler: pool) {			
			if (handler.isAlive()) continue;
			handler.interrupt();
		}
	}
}
