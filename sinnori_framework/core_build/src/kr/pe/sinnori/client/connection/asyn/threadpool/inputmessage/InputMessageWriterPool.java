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

import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler.InputMessageWriter;
import kr.pe.sinnori.client.io.LetterToServer;
import kr.pe.sinnori.common.io.MessageExchangeProtocolIF;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;

/**
 * 클라이언트 입력 메시지 소켓 쓰기 담당 쓰레드 폴.
 * 
 * @author Jonghoon Won
 */
public class InputMessageWriterPool extends AbstractThreadPool {
	private int maxHandler;
	private CommonProjectInfo commonProjectInfo = null;
	private LinkedBlockingQueue<LetterToServer> inputMessageQueue;
	private MessageMangerIF messageManger = null;
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager;
	// private ArrayList<WrapBuffer> ioWrapBufferList = new ArrayList<WrapBuffer>();
	private MessageExchangeProtocolIF messageProtocol = null;
	
	
	/**
	 * 생성자
	 * @param size 클라이언트 입력 메시지 소켓 쓰기 담당 쓰레드 초기 갯수
	 * @param max 클라이언트 입력 메시지 소켓 쓰기 담당 쓰레드 최대 갯수
	 * @param commonProjectInfo 공통 연결 데이터
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param messageProtocol 메시지 교환 프로프로콜
	 * @param messageManger 메시지 관리자
	 * @param dataPacketBufferQueueManager 데이터 패킷 큐 관리자
	 */
	public InputMessageWriterPool(int size, int max,
			CommonProjectInfo commonProjectInfo, 
			LinkedBlockingQueue<LetterToServer> inputMessageQueue, 
			MessageExchangeProtocolIF messageProtocol, 
			MessageMangerIF messageManger,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) {
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
		this.inputMessageQueue = inputMessageQueue;
		this.messageProtocol = messageProtocol;
		this.messageManger = messageManger;
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		
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
					/*
					WrapBuffer wrapBuffer = dataPacketBufferQueueManager.pollDataPacketBuffer();
					if (null == wrapBuffer) {
						log.severe("데이터 패킷 버퍼 확보 실패");
						return;
					}
					*/
					// ioWrapBufferList.add(wrapBuffer);

					Thread handler = new InputMessageWriter(size, commonProjectInfo,
							inputMessageQueue, messageProtocol, messageManger, dataPacketBufferQueueManager);
					pool.add(handler);

					// log.info("InputMessageWriter[%d] wrapBuffer=[%d]", size, wrapBuffer.hashCode());
				} catch (Exception e) {
					log.warn("handler 등록 실패", e);
				}
			}
		}
	}
	
}
