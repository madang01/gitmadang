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

//import java.util.logging.Level;

import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;
import kr.pe.sinnori.server.threadpool.IEOServerThreadPoolSetManagerIF;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriter;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

/**
 * 서버 출력 메시지 소켓 쓰기 담당 쓰레드 폴
 * 
 * @author Won Jonghoon
 */
public class OutputMessageWriterPool extends AbstractThreadPool implements OutputMessageWriterPoolIF {
	private String projectName = null;
	private int maxHandler;
	private DataPacketBufferPoolIF dataPacketBufferQueueManger;
	// private LinkedBlockingQueue<ToLetter> outputMessageQueue = null;
	private int outputMessageQueueSize;
	
	
	public OutputMessageWriterPool(String projectName, int size, int max,
			// LinkedBlockingQueue<ToLetter> outputMessageQueue,
			int outputMessageQueueSize,
			DataPacketBufferPoolIF dataPacketBufferQueueManger,
			IEOServerThreadPoolSetManagerIF ieoThreadPoolManager) {
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

		this.projectName = projectName;
		this.maxHandler = max;
		this.outputMessageQueueSize = outputMessageQueueSize;
		this.dataPacketBufferQueueManger = dataPacketBufferQueueManger;
		
		for (int i = 0; i < size; i++) {
			addHandler();
		}
	}

	@Override
	public void addHandler() throws IllegalStateException {
		LinkedBlockingQueue<ToLetter> outputMessageQueue = new LinkedBlockingQueue<ToLetter>(outputMessageQueueSize);
		synchronized (monitor) {
			int size = pool.size();
			
			if (size > maxHandler) {
				String errorMessage = String.format("%s OutputMessageWriter 최대 갯수[%d]를 넘을 수 없습니다.", projectName, maxHandler); 
				log.warn(errorMessage);
				throw new IllegalStateException(errorMessage);
				
			}
			try {
				Thread handler = new OutputMessageWriter(projectName, size,
						outputMessageQueue,   
						dataPacketBufferQueueManger);
				
				pool.add(handler);
			} catch (Exception e) {
				String errorMessage = String.format("%s OutputMessageWriter[%d] 등록 실패", projectName, size); 
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
