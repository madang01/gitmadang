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

package kr.pe.sinnori.server.threadpool.accept.processor.handler;

// import java.util.logging.Level;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderPoolIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 서버에 접속 승인된 클라이언트(=소켓 채널) 등록 처리 쓰레드
 * 
 * @author Won Jonghoon
 * 
 */
public class AcceptProcessor extends Thread {
	private Logger log = LoggerFactory.getLogger(AcceptProcessor.class);
	
	private int index; // AcceptSelectorPool에서 생성한 순서
	private String projectName;
	private LinkedBlockingQueue<SocketChannel> acceptQueue;
	private InputMessageReaderPoolIF inputMessageReaderPoolIF = null;

	/**
	 * 생성자
	 * @param index 쓰레드 순번
	 * @param serverProjectConfig 프로젝트의 공통 포함한 서버 환경 변수 접근 인터페이스
	 * @param acceptQueue 접속 요청 소켓 채널을 입력 받는 큐
	 * @param inputMessageReaderPoolIF 접속 요청 소켓 채널 등록 처리할 입력 메시지 읽기 폴 인터페이스
	 */
	public AcceptProcessor(int index,
			String projectName,
			LinkedBlockingQueue<SocketChannel> acceptQueue,
			InputMessageReaderPoolIF inputMessageReaderPoolIF) {
		this.index = index;
		this.projectName = projectName;
		this.acceptQueue = acceptQueue;
		this.inputMessageReaderPoolIF = inputMessageReaderPoolIF;
	}

	/**
	 * <b>Accept Qeueue</b> 에서 OP_ACCEPT 이벤트 발생한 socket channel을 꺼내와서 <br/>
	 * 비동기로 세팅후
	 * {@link InputMessageReaderPoolIF#addNewClient(java.nio.channels.SocketChannel) }
	 * 에게 전달한다.
	 * 
	 * @see InputMessageReaderPoolIF#addNewClient(java.nio.channels.SocketChannel)
	 */
	@Override
	public void run() {
		log.info(String.format("%s AcceptProcessor[%d] start", projectName, index));

		try {
			while (!Thread.currentThread().isInterrupted()) {
				SocketChannel clientSC = acceptQueue.take();
				clientSC.configureBlocking(false);

				/*Socket sc = clientSC.socket();
				sc.setKeepAlive(true);
				sc.setTcpNoDelay(true);*/
				// sc.setSendBufferSize(io_buffer_size);
				// sc.setReceiveBufferSize(io_buffer_size);
				
				clientSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
				clientSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
				clientSC.setOption(StandardSocketOptions.SO_LINGER, 0);
				clientSC.setOption(StandardSocketOptions.SO_SNDBUF, 65536);
				// clientSC.setOption(StandardSocketOptions.SO_RCVBUF, serverProjectConfig.getDataPacketBufferSize());

				try {
					inputMessageReaderPoolIF.addNewClient(clientSC);
				} catch (NoMoreDataPacketBufferException e) {
					log.warn("NoMoreDataPacketBufferException", e);
					clientSC.close();
				}
			}
			log.warn(String.format("%s AcceptProcessor[%d] loop exit", projectName, index));
		} catch (InterruptedException e) {
			log.warn(String.format("%s AcceptProcessor[%d] stop", projectName, index), e);
		} catch (Exception e) {
			log.warn(String.format("%s AcceptProcessor[%d] unknown error", projectName, index), e);
		}
	}
}
