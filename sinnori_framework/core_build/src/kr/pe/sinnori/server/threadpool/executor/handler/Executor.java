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

package kr.pe.sinnori.server.threadpool.executor.handler;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.io.LetterFromClient;
import kr.pe.sinnori.server.io.LetterToClient;

/**
 * 서버 비지니스 로직 수행자 쓰레드<br/>
 * 입력 메시지 대응 비지니스 로직 수행후 결과로 얻은 출력 메시지가 담긴 편지 묶음을 출력 메시지 큐에 넣는다.
 * 
 * @author Jonghoon Won
 */
public class Executor extends Thread implements CommonRootIF {
	private int index;
	private ServerProjectConfig serverProjectConfig = null;
	private LinkedBlockingQueue<LetterFromClient> inputMessageQueue;
	private LinkedBlockingQueue<LetterToClient> ouputMessageQueue;
	private MessageProtocolIF messageProtocol = null;
	
	private ServerObjectCacheManagerIF serverObjectCacheManager = null;
	
	
	/**
	 * 생성자
	 * @param index 순번
	 * @param serverProjectConfig 프로젝트의 공통 포함한 서버 환경 변수 접근 인터페이스
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param ouputMessageQueue 출력 메시지 큐
	 * 
	 */
	public Executor(int index, 
			ServerProjectConfig serverProjectConfig,
			LinkedBlockingQueue<LetterFromClient> inputMessageQueue,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue,
			MessageProtocolIF messageProtocol, ServerObjectCacheManagerIF serverObjectCacheManager) {
		this.index = index;
		this.serverProjectConfig = serverProjectConfig;		
		this.inputMessageQueue = inputMessageQueue;
		this.ouputMessageQueue = ouputMessageQueue;
		this.messageProtocol = messageProtocol;
		this.serverObjectCacheManager = serverObjectCacheManager;
	}
	

	@Override
	public void run() {
		log.info(String.format("%s ExecutorProcessor[%d] start", serverProjectConfig.getProjectName(), index));
		
		try {
			while (!Thread.currentThread().isInterrupted()) {
				LetterFromClient letterFromClient = inputMessageQueue.take();

				SocketChannel clientSC = letterFromClient.getFromSC();
				ClientResource clientResource = letterFromClient.getClientResource();
				ReceivedLetter receivedLetter = letterFromClient.getWrapMiddleReadObj();
				String messageID = receivedLetter.getMessageID();
				// ServerSession serverSession = serverSessionManager.openServerSessionID(messageID, inObj);
				// serverSession.setAttribute("ouputMessageQueue", ouputMessageQueue);
				AbstractServerTask  serverTask = serverObjectCacheManager.getServerTask(messageID);
				
				serverTask.execute(index, serverProjectConfig, serverProjectConfig.getCharset(), ouputMessageQueue, messageProtocol,
						clientSC, clientResource, receivedLetter, serverObjectCacheManager);
				
			}
			log.warn(String.format("%s ExecutorProcessor[%d] loop exit", serverProjectConfig.getProjectName(), index));
		} catch (InterruptedException e) {
			log.warn(String.format("%s ExecutorProcessor[%d] stop", serverProjectConfig.getProjectName(), index), e);
		} catch (Exception e) {
			log.warn(String.format("%s ExecutorProcessor[%d] unknown error", serverProjectConfig.getProjectName(), index), e);
		}

	}
}
