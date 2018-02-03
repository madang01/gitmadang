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

//import java.util.logging.Level;

import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.io.DataPacketBufferPoolManagerIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriter;

/**
 * 서버 출력 메시지 소켓 쓰기 담당 쓰레드 폴
 * 
 * @author Won Jonghoon
 */
public class OutputMessageWriterPool extends AbstractThreadPool {
	private String projectName = null;
	private int maxHandler;
	private DataPacketBufferPoolManagerIF dataPacketBufferQueueManger;
	private LinkedBlockingQueue<ToLetter> outputMessageQueue = null;
	
	/**
	 * 생성자
	 * @param size 출력 메시지 쓰기 쓰레드 갯수
	 * @param max 출력 메시지 쓰기 쓰레드 최대 갯수
	 * @param projectPart 프로젝트의 공통 포함한 서버 환경 변수 접근 인터페이스
	 * @param outputMessageQueue 출력 메시지 큐
	 * @param dataPacketBufferQueueManger 데이터 패킷 버퍼 큐 관리자
	 */
	public OutputMessageWriterPool(String projectName, int size, int max,
			LinkedBlockingQueue<ToLetter> outputMessageQueue,
			DataPacketBufferPoolManagerIF dataPacketBufferQueueManger) {
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
		this.outputMessageQueue = outputMessageQueue;
		this.dataPacketBufferQueueManger = dataPacketBufferQueueManger;

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
					Thread handler = new OutputMessageWriter(projectName, size,
							outputMessageQueue,   
							dataPacketBufferQueueManger);
					
					pool.add(handler);
				} catch (Exception e) {
					String errorMessage = String.format("%s OutputMessageWriter[%d] 등록 실패", projectName, size); 
					log.warn(errorMessage, e);
					throw new RuntimeException(errorMessage);
				}
			} else {
				String errorMessage = String.format("%s OutputMessageWriter 최대 갯수[%d]를 넘을 수 없습니다.", projectName, maxHandler); 
				log.warn(errorMessage);
				throw new RuntimeException(errorMessage);
			}
		}
	}
}
