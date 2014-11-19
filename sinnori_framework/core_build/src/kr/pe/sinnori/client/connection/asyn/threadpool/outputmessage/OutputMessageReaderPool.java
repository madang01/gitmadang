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
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;

/**
 * 클라이언트 출력 메시지 소켓 읽기 담당 쓰레드 폴
 * 
 * @see OutputMessageReader
 * @author Won Jonghoon
 */
public class OutputMessageReaderPool extends AbstractThreadPool implements
		OutputMessageReaderPoolIF {
	private int maxHandler;
	private long readSelectorWakeupInterval;
	private ClientProjectConfig clientProjectConfig = null;
	private MessageProtocolIF messageProtocol = null;
	
	/**
	 * 생성자
	 * @param size 클라이언트 출력 메시지 소켓 읽기 담당 쓰레드 초기 갯수
	 * @param max 클라이언트 출력 메시지 소켓 읽기 담당 쓰레드 최대 갯수
	 * @param readSelectorWakeupInterval 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기
	 * @param clientProjectConfig 프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스
	 * @param messageProtocol 메시지 교환 프로토콜
	 */
	public OutputMessageReaderPool(int size, int max, long readSelectorWakeupInterval, 
			ClientProjectConfig clientProjectConfig,
			MessageProtocolIF messageProtocol) {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("%s 파라미터 size 는 0보다 커야 합니다.", clientProjectConfig.getProjectName()));
		}
		if (max <= 0) {
			throw new IllegalArgumentException(String.format("%s 파라미터 max 는 0보다 커야 합니다.", clientProjectConfig.getProjectName()));
		}

		if (size > max) {
			throw new IllegalArgumentException(String.format(
					"%s 파라미터 size[%d]는 파라미터 max[%d]보다 작거나 같아야 합니다.", clientProjectConfig.getProjectName(), size, max));
		}

		this.maxHandler = max;
		this.readSelectorWakeupInterval = readSelectorWakeupInterval;
		this.clientProjectConfig = clientProjectConfig;
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
					Thread handler = new OutputMessageReader(size, readSelectorWakeupInterval, clientProjectConfig, messageProtocol);
					pool.add(handler);
				} catch (Exception e) {
					String errorMessage = String.format("%s OutputMessageReader[%d] 등록 실패", clientProjectConfig.getProjectName(), size); 
					log.warn(errorMessage, e);
					throw new RuntimeException(errorMessage);
				}
			} else {
				String errorMessage = String.format("%s OutputMessageReader 최대 갯수[%d]를 넘을 수 없습니다.", clientProjectConfig.getProjectName(), maxHandler); 
				log.warn(errorMessage);
				throw new RuntimeException(errorMessage);
			}
		}
	}

	@Override
	public void addNewServer(AbstractAsynConnection serverConnection) {
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
		// 읽기 전용 selector 에 최소로 등록된 소켓 채널을 가지고 있는 출력 메시지 읽기 처리 쓰레드에 연결 객체 등록 
		minHandler.addNewServer(serverConnection);
	}
}
