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

import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.threadpool.inputmessage.handler.InputMessageReader;
import kr.pe.sinnori.server.threadpool.inputmessage.handler.InputMessageReaderIF;

/**
 * 입력 메시지 소켓 읽기 담당 쓰레드 폴
 * 
 * @author Won Jonghoon
 * 
 */
public class InputMessageReaderPool extends AbstractThreadPool implements
		InputMessageReaderPoolIF {
	private String projectName = null;
	private int max;
	private long readSelectorWakeupInterval;
	private MessageProtocolIF messageProtocol;
	private DataPacketBufferPoolIF dataPacketBufferQueueManager;
	private SocketResourceManagerIF clientResourceManager;
	
	
	
	public InputMessageReaderPool(String projectName, 
			int size, 
			int max, 
			long readSelectorWakeupInterval,
			MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferQueueManager,
			SocketResourceManagerIF clientResourceManager) {
		if (size <= 0) {
			String errorMessage = String.format("%s 파라미터 size 는 0보다 커야 합니다.", projectName);
			log.warn(errorMessage);
			
			throw new IllegalArgumentException(errorMessage);
		}

		if (max <= 0) {
			String errorMessage = String.format("%s 파라미터 max 는 0보다 커야 합니다.", projectName);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (size > max) {
			String errorMessage = String.format("%s 파라미터 size[%d]는 파라미터 max[%d]보다 작거나 같아야 합니다.", 
					projectName, size, max);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		this.projectName = projectName;
		this.max = max;
		this.readSelectorWakeupInterval = readSelectorWakeupInterval;
		this.projectName = projectName;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		this.clientResourceManager = clientResourceManager;		

		for (int i = 0; i < size; i++) {
			addHandler();
		}
	}

	@Override
	public void addHandler() {
		synchronized (monitor) {
			int size = pool.size();
			
			if (size > max) {
				String errorMessage = String.format("%s InputMessageReader 최대 갯수[%d]를 넘을 수 없습니다.", projectName, max); 
				log.warn(errorMessage);
				throw new RuntimeException(errorMessage);
			}
			
			try {
				Thread inputMessageReader = new InputMessageReader(projectName, 
						size, 
						readSelectorWakeupInterval, 
						messageProtocol, 
						dataPacketBufferQueueManager, 
						clientResourceManager);
				pool.add(inputMessageReader);
			} catch (Exception e) {
				String errorMessage = String.format("%s InputMessageReader[%d] 등록 실패", projectName, size); 
				log.warn(errorMessage, e);
				throw new RuntimeException(errorMessage);
			}
		}
	}	
	
	@Override
	public InputMessageReaderIF getInputMessageReaderWithMinimumMumberOfSockets() {
		InputMessageReaderIF inputMessageReaderWithMinimumMumberOfSockets = null;
		int minimumMumberOfSockets = Integer.MAX_VALUE;

		int size = pool.size();
		for (int i = 0; i < size; i++) {
			InputMessageReaderIF handler = (InputMessageReaderIF) pool.get(i);
			int numberOfSocket = handler.getNumberOfSocket();
			if (numberOfSocket < minimumMumberOfSockets) {
				minimumMumberOfSockets = numberOfSocket;
				inputMessageReaderWithMinimumMumberOfSockets = handler;
			}
		}
		
		return inputMessageReaderWithMinimumMumberOfSockets;
	}
}
