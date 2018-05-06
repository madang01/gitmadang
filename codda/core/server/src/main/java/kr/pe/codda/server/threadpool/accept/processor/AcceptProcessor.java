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

package kr.pe.codda.server.threadpool.accept.processor;

import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.server.SocketResourceManagerIF;

/**
 * 서버에 접속 승인된 클라이언트(=소켓 채널) 등록 처리 쓰레드
 * 
 * @author Won Jonghoon
 * 
 */
public class AcceptProcessor extends Thread {
	private InternalLogger log = InternalLoggerFactory.getInstance(AcceptProcessor.class);
	
	private int index;
	private String projectName;
	private ArrayBlockingQueue<SocketChannel> acceptQueue;
	private SocketResourceManagerIF socketResourceManager = null;


	public AcceptProcessor(int index,
			String projectName,
			ArrayBlockingQueue<SocketChannel> acceptQueue,
			SocketResourceManagerIF socketResourceManager) {
		this.index = index;
		this.projectName = projectName;
		this.acceptQueue = acceptQueue;
		this.socketResourceManager = socketResourceManager;
	}

	
	@Override
	public void run() {
		log.info(String.format("%s AcceptProcessor[%d] start", projectName, index));

		try {
			while (!Thread.currentThread().isInterrupted()) {
				SocketChannel clientSC = acceptQueue.take();
				
				clientSC.configureBlocking(false);
				clientSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
				clientSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
				clientSC.setOption(StandardSocketOptions.SO_LINGER, 0);
				clientSC.setOption(StandardSocketOptions.SO_REUSEADDR, true);
				//clientSC.setOption(StandardSocketOptions.SO_SNDBUF, 65536);
				
				socketResourceManager.addNewAcceptedSocketChannel(clientSC);
			}
			log.warn(String.format("%s AcceptProcessor[%d] loop exit", projectName, index));
		} catch (InterruptedException e) {
			log.warn(String.format("%s AcceptProcessor[%d] stop", projectName, index), e);
		} catch (Exception e) {
			log.warn(String.format("%s AcceptProcessor[%d] unknown error", projectName, index), e);
		}
	}
}
