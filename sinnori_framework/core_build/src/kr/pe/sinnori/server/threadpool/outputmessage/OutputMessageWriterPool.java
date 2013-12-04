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

import kr.pe.sinnori.common.io.MessageExchangeProtocolIF;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.io.LetterToClient;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriter;

/**
 * 서버 출력 메시지 소켓 쓰기 담당 쓰레드 폴
 * 
 * @author Jonghoon Won
 */
public class OutputMessageWriterPool extends AbstractThreadPool {
	private int maxHandler;
	private CommonProjectInfo commonProjectInfo;
	private MessageExchangeProtocolIF messageProtocol;
	private MessageMangerIF messageManger;
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManger;
	private ClientResourceManagerIF clientResourceManager;
	private LinkedBlockingQueue<LetterToClient> outputMessageQueue = null;
	
	/**
	 * 생성자
	 * @param size 출력 메시지 쓰기 쓰레드 갯수
	 * @param max 출력 메시지 쓰기 쓰레드 최대 갯수
	 * @param commonProjectInfo 연결 공통 데이터
	 * @param outputMessageQueue 출력 메시지 큐
	 * @param messageProtocol 메시지 교환 프로토콜
	 * @param messageManger 메시지 관리자
	 * @param dataPacketBufferQueueManger 데이터 패킷 버퍼 큐 관리자
	 * @param clientResourceManager 클라이언트 자원 관리자
	 */
	public OutputMessageWriterPool(int size, int max,
			CommonProjectInfo commonProjectInfo,
			LinkedBlockingQueue<LetterToClient> outputMessageQueue,
			MessageExchangeProtocolIF messageProtocol,
			MessageMangerIF messageManger,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManger,
			ClientResourceManagerIF clientResourceManager) {
		if (size <= 0) {
			throw new IllegalArgumentException("파라미터 초기 핸들러 갯수는 0보다 커야 합니다.");
		}
		if (max <= 0) {
			throw new IllegalArgumentException("파라미터 최대 핸들러 갯수는 0보다 커야 합니다.");
		}

		if (size > max) {
			throw new IllegalArgumentException(String.format(
					"파라미터 초기 핸들러 갯수[%d]는 최대 핸들러 갯수[%d]보다 작거나 같아야 합니다.", size,
					max));
		}

		this.maxHandler = max;
		this.commonProjectInfo = commonProjectInfo;
		this.outputMessageQueue = outputMessageQueue;
		this.messageProtocol = messageProtocol;
		this.messageManger = messageManger;
		this.dataPacketBufferQueueManger = dataPacketBufferQueueManger;
		this.clientResourceManager = clientResourceManager;

		for (int i = 0; i < size; i++) {
			addHandler();
		}
	}

	@Override
	public void addHandler() {
		synchronized (monitor) {
			int size = pool.size();
			if (size > maxHandler) {
				log.warn(String.format("더 이상 출력 메시지 쓰기 쓰레드 생성할 수 없습니다. 최대 생성수[%d] 도달",
						maxHandler));
				return;
			}

			try {
				Thread handler = new OutputMessageWriter(size, commonProjectInfo, 
						outputMessageQueue, messageProtocol, messageManger, 
						dataPacketBufferQueueManger, clientResourceManager);
				
				pool.add(handler);
			} catch (Exception e) {
				log.warn("handler 등록 실패", e);
			}
		}
	}
}
