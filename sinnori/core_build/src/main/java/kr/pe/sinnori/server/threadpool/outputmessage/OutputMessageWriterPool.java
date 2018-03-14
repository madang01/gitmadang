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

package kr.pe.sinnori.server.threadpool.outputmessage;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;

import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;
import kr.pe.sinnori.server.threadpool.IEOServerThreadPoolSetManagerIF;

/**
 * 서버 출력 메시지 소켓 쓰기 담당 쓰레드 폴
 * 
 * @author Won Jonghoon
 */
public class OutputMessageWriterPool extends AbstractThreadPool implements OutputMessageWriterPoolIF {
	
	private int poolMaxSize;
	private String projectName = null;
	private int outputMessageQueueSize;
	private DataPacketBufferPoolIF dataPacketBufferPool;

	public OutputMessageWriterPool(int poolSize, int poolMaxSize,
			String projectName, 
			int outputMessageQueueSize,
			DataPacketBufferPoolIF dataPacketBufferPool,
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
		
		if (outputMessageQueueSize <= 0) {
			String errorMessage = String.format("the parameter outputMessageQueueSize[%d] is less than or equal to zero", outputMessageQueueSize); 
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == dataPacketBufferPool) {
			throw new IllegalArgumentException("the parameter dataPacketBufferPool is null");
		}
		
		if (null == ieoThreadPoolManager) {
			throw new IllegalArgumentException("the parameter ieoThreadPoolManager is null");
		}		

		this.poolMaxSize = poolMaxSize;
		this.projectName = projectName;		
		this.outputMessageQueueSize = outputMessageQueueSize;
		this.dataPacketBufferPool = dataPacketBufferPool;
		
		ieoThreadPoolManager.setOutputMessageWriterPool(this);
		
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
		ArrayBlockingQueue<ToLetter> outputMessageQueue = new ArrayBlockingQueue<ToLetter>(outputMessageQueueSize);
		synchronized (monitor) {
			int size = pool.size();
			
			if (size >= poolMaxSize) {
				if (size >= poolMaxSize) {
					String errorMessage = String.format("can't add any more tasks becase the number of %s OutputMessageWriterPool's tasks reached the maximum[%d] number", projectName, poolMaxSize); 
					log.warn(errorMessage);
					throw new IllegalStateException(errorMessage);
				}
				
			}
			try {
				Thread handler = new OutputMessageWriter(projectName, size,
						outputMessageQueue,   
						dataPacketBufferPool);
				
				pool.add(handler);
			} catch (Exception e) {
				String errorMessage = String.format("failed to add a %s OutputMessageWriterPool's task becase error occured::errmsg={}", projectName, e.getMessage()); 
				log.warn(errorMessage, e);
				throw new IllegalStateException(errorMessage);
			}
		}
	}

	@Override
	public OutputMessageWriterIF getOutputMessageWriterWithMinimumNumberOfSockets() {
		Iterator<Thread> poolIter = pool.iterator();
		
		if (! poolIter.hasNext()) {
			throw new NoSuchElementException("OutputMessageWriterPool empty");
		}
		
		OutputMessageWriterIF minOutputMessageWriter = (OutputMessageWriterIF)poolIter.next();
		int min = minOutputMessageWriter.getNumberOfSocket();
	
		while (poolIter.hasNext()) {
			OutputMessageWriterIF outputMessageWriter = (OutputMessageWriterIF) poolIter.next();
			int numberOfSocket = outputMessageWriter.getNumberOfSocket();
			if (numberOfSocket < min) {
				min = numberOfSocket;
				minOutputMessageWriter = outputMessageWriter;
			}
		}
		
		return minOutputMessageWriter;
	}
}
