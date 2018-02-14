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

import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderThread;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;

/**
 * 클라이언트 출력 메시지 소켓 읽기 담당 쓰레드 폴
 * 
 * @see OutputMessageReaderThread
 * @author Won Jonghoon
 */
public class OutputMessageReaderPool extends AbstractThreadPool implements OutputMessageReaderPoolIF {
	private String projectName = null;
	private int maxHandler;
	private long readSelectorWakeupInterval;
	private MessageProtocolIF messageProtocol = null;
	
	
	// private int poolSize;
	private int nextIndex=-1;
	
	
	public OutputMessageReaderPool(String projectName, int size, int max,
			long readSelectorWakeupInterval, 			
			MessageProtocolIF messageProtocol) {
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
		this.readSelectorWakeupInterval = readSelectorWakeupInterval;		
		this.messageProtocol = messageProtocol;
		
		
		

		for (int i = 0; i < size; i++) {
			addHandler();
		}
	}

	@Override
	public void addHandler() {
		synchronized (monitor) {
			int size = pool.size();

			if (size < maxHandler) {
				try {
					Thread handler = new OutputMessageReaderThread(projectName, size, readSelectorWakeupInterval, messageProtocol);
					pool.add(handler);
				} catch (Exception e) {
					String errorMessage = String.format("%s OutputMessageReader[%d] 등록 실패", projectName, size); 
					log.warn(errorMessage, e);
					throw new RuntimeException(errorMessage);
				}
			} else {
				String errorMessage = String.format("%s OutputMessageReader 최대 갯수[%d]를 넘을 수 없습니다.", projectName, maxHandler); 
				log.warn(errorMessage);
				throw new RuntimeException(errorMessage);
			}
		}
	}

	@Override
	public OutputMessageReaderIF getNextOutputMessageReader() {
		nextIndex = (nextIndex + 1) % pool.size();
		return (OutputMessageReaderIF)pool.get(nextIndex);
	}

	// @Override
	/*public void addNewServer(AbstractAsynConnection serverAsynConnection) {
		AsynReadOnlySelectorManagerIF minHandler = null;
		int MIN_COUNT = Integer.MAX_VALUE;

		int size = pool.size();
		for (int i = 0; i < size; i++) {
			AsynReadOnlySelectorManagerIF handler = (AsynReadOnlySelectorManagerIF) pool.get(i);
			int cnt_of_clients = handler.getCntOfServers();
			if (cnt_of_clients < MIN_COUNT) {
				MIN_COUNT = cnt_of_clients;
				minHandler = handler;
			}
		}
		// 읽기 전용 selector 에 최소로 등록된 소켓 채널을 가지고 있는 출력 메시지 읽기 처리 쓰레드에 연결 객체 등록 
		minHandler.addNewServer(serverAsynConnection);
	}*/
}
