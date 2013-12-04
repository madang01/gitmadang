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

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReader;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderIF;
import kr.pe.sinnori.common.io.MessageExchangeProtocolIF;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;

/**
 * 클라이언트 출력 메시지 소켓 읽기 담당 쓰레드 폴
 * 
 * @see OutputMessageReader
 * @author Jonghoon Won
 */
public class OutputMessageReaderPool extends AbstractThreadPool implements
		OutputMessageReaderPoolIF {
	private int maxHandler;
	private long readSelectorWakeupInterval;
	private CommonProjectInfo commonProjectInfo = null;
	private MessageExchangeProtocolIF messageProtocol = null;
	private MessageMangerIF messageManger = null;
	
	/**
	 * 생성자
	 * @param size 클라이언트 출력 메시지 소켓 읽기 담당 쓰레드 초기 갯수
	 * @param max 클라이언트 출력 메시지 소켓 읽기 담당 쓰레드 최대 갯수
	 * @param readSelectorWakeupInterval 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기
	 * @param commonProjectInfo 공통 연결 데이터
	 * @param messageProtocol 메시지 교환 프로토콜
	 * @param messageManger 메시지 관리자
	 */
	public OutputMessageReaderPool(int size, int max, long readSelectorWakeupInterval, 
			CommonProjectInfo commonProjectInfo,
			MessageExchangeProtocolIF messageProtocol,
			MessageMangerIF messageManger) {
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
		this.readSelectorWakeupInterval = readSelectorWakeupInterval;
		this.commonProjectInfo = commonProjectInfo;
		this.messageProtocol = messageProtocol;
		this.messageManger = messageManger;
		

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
					Thread handler = new OutputMessageReader(size, readSelectorWakeupInterval, commonProjectInfo, messageProtocol, messageManger);
					pool.add(handler);
				} catch (Exception e) {
					log.warn("handler 등록 실패", e);
				}
			}
		}
	}

	@Override
	public void addNewServer(AbstractAsynConnection serverConnection) throws InterruptedException {
		OutputMessageReaderIF minHandler = null;
		int MIN_COUNT = Integer.MAX_VALUE;

		int size = pool.size();
		for (int i = 0; i < size; i++) {
			OutputMessageReaderIF handler = (OutputMessageReaderIF) pool.get(i);
			int cnt_of_clients = handler.getCntOfClients();
			if (cnt_of_clients < MIN_COUNT) {
				MIN_COUNT = cnt_of_clients;
				minHandler = handler;
			}
		}
		minHandler.addNewServer(serverConnection); // 마지막으로 ReqeustHandler에 등록
	}
}
