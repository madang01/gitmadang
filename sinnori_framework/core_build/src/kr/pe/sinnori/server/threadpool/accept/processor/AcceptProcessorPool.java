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

package kr.pe.sinnori.server.threadpool.accept.processor;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;
import kr.pe.sinnori.server.threadpool.accept.processor.handler.AcceptProcessor;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderPoolIF;

/**
 * 서버에 접속 승인된 클라이언트(=소켓 채널) 등록 처리 쓰레드 폴.
 * 
 * @author Won Jonghoon
 * 
 */
public class AcceptProcessorPool extends AbstractThreadPool {
	private LinkedBlockingQueue<SocketChannel> acceptQueue;
	private int maxHandler;
	private ServerProjectConfig serverProjectConfig = null;
	private InputMessageReaderPoolIF inputMessageReaderPoolIF = null;
	
	/**
	 * 생성자
	 * @param size 소켓 채널의 신규 등록 처리를 수행하는 쓰레드 갯수
	 * @param max 소켓 채널의 신규 등록 처리를 수행하는 쓰레드 최대 갯수
	 * @param serverProjectConfig 프로젝트의 공통 포함한 서버 환경 변수 접근 인터페이스
	 * @param acceptQueue 신규 등록할 소켓 채널을 받는 큐
	 * @param inputMessageReaderPoolIF 서버에 접속 승인된 클라이언트(=소켓 채널) 등록 처리 쓰레드가 바라보는 입력 메시지 소켓 읽기 담당 쓰레드 폴 인터페이스
	 */
	public AcceptProcessorPool(int size, int max,
			ServerProjectConfig serverProjectConfig,
			LinkedBlockingQueue<SocketChannel> acceptQueue,			
			InputMessageReaderPoolIF inputMessageReaderPoolIF) {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("%s 파라미터 size 는 0보다 커야 합니다.", serverProjectConfig.getProjectName()));
		}
		if (max <= 0) {
			throw new IllegalArgumentException(String.format("%s 파라미터 max 는 0보다 커야 합니다.", serverProjectConfig.getProjectName()));
		}

		if (size > max) {
			throw new IllegalArgumentException(String.format(
					"%s 파라미터 size[%d]는 파라미터 max[%d]보다 작거나 같아야 합니다.", serverProjectConfig.getProjectName(), size, max));
		}

		this.acceptQueue = acceptQueue;
		this.maxHandler = max;
		this.serverProjectConfig = serverProjectConfig;
		this.inputMessageReaderPoolIF = inputMessageReaderPoolIF;

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
					Thread handler = new AcceptProcessor(size, 
							serverProjectConfig, 
							acceptQueue,
							inputMessageReaderPoolIF);
					pool.add(handler);
				} catch (Exception e) {
					String errorMessage = String.format("%s AcceptProcessor[%d] 등록 실패", serverProjectConfig.getProjectName(), size); 
					log.warn(errorMessage, e);
					throw new RuntimeException(errorMessage);
				}
			} else {
				String errorMessage = String.format("%s AcceptProcessor 최대 갯수[%d]를 넘을 수 없습니다.", serverProjectConfig.getProjectName(), maxHandler); 
				log.warn(errorMessage);
				throw new RuntimeException(errorMessage);
			}
		}
	}
}
