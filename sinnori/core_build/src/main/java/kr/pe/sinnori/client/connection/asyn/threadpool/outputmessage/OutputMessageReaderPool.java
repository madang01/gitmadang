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

package kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.threadpool.ThreadPoolIF;

/**
 * 클라이언트 출력 메시지 소켓 읽기 담당 쓰레드 폴
 * 
 * @see OutputMessageReader
 * @author Won Jonghoon
 */
public class OutputMessageReaderPool implements ThreadPoolIF, OutputMessageReaderPoolIF {
	private Logger log = LoggerFactory.getLogger(OutputMessageReaderPool.class);
	private final List<OutputMessageReaderIF> pool = new ArrayList<OutputMessageReaderIF>();
	
	
	private String projectName = null;
	private long wakeupIntervalOfSelectorForReadEventOnly;
	private MessageProtocolIF messageProtocol = null;

	public OutputMessageReaderPool(int poolSize, 
			String projectName, long wakeupIntervalOfSelectorForReadEventOnly,
			MessageProtocolIF messageProtocol) {
		if (poolSize <= 0) {
			String errorMessage = String.format("the parameter poolSize[%d] is less than or equal to zero", poolSize); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == projectName) {
			throw new IllegalArgumentException("the parameter projectName is null");
		}
		
		if (wakeupIntervalOfSelectorForReadEventOnly < 0) {
			String errorMessage = String.format("the parameter wakeupIntervalOfSelectorForReadEventOnly[%d] is less than zero", wakeupIntervalOfSelectorForReadEventOnly); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}

		this.projectName = projectName;
		this.wakeupIntervalOfSelectorForReadEventOnly = wakeupIntervalOfSelectorForReadEventOnly;
		this.messageProtocol = messageProtocol;

		for (int i = 0; i < poolSize; i++) {
			try {
				innerAddTask();
			} catch (IllegalStateException e) {
				log.error(e.getMessage(), e);
				System.exit(1);
			}
		}
	}

	private void innerAddTask() throws IllegalStateException {
		int size = pool.size();

		try {
			OutputMessageReaderIF handler = new OutputMessageReader(projectName, size, wakeupIntervalOfSelectorForReadEventOnly, messageProtocol);
			pool.add(handler);
		} catch (Exception e) {
			String errorMessage = String.format("%s OutputMessageReader[%d] 등록 실패", projectName, size);
			log.warn(errorMessage, e);
			throw new IllegalStateException(errorMessage);
		}
	}

	@Override
	public OutputMessageReaderIF getOutputMessageReaderWithMinimumNumberOfConnetion() {
		
		if (pool.isEmpty()) {
			throw new NoSuchElementException("OutputMessageReaderPool empty");
		}

		int min = Integer.MAX_VALUE;
		OutputMessageReaderIF minOutputMessageReader = null;	
		

		for (OutputMessageReaderIF handler : pool) {
			int numberOfAsynConnection = handler.getNumberOfConnection();
			if (numberOfAsynConnection < min) {
				minOutputMessageReader = handler;
				min = numberOfAsynConnection;
			}
		}

		return minOutputMessageReader;
	}

	@Override
	public void addTask() throws IllegalStateException, NotSupportedException {
		String errorMessage = "this OutputMessageReaderPool dosn't support this addTask method";
		throw new NotSupportedException(errorMessage);
	}
	
	@Override
	public int getPoolSize() {
		return pool.size();
	}

	@Override
	public void startAll() {
		for (OutputMessageReaderIF handler: pool) {			
			if (handler.isAlive()) continue;
			handler.start();
		}
	}

	@Override
	public void stopAll() {
		for (OutputMessageReaderIF handler: pool) {			
			if (handler.isAlive()) continue;
			handler.interrupt();
		}
	}
}
