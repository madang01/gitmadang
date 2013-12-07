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
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.AbstractServerExecutor;
import kr.pe.sinnori.server.executor.SererExecutorClassLoaderManagerIF;
import kr.pe.sinnori.server.io.LetterFromClient;
import kr.pe.sinnori.server.io.LetterToClient;

/**
 * 서버 비지니스 로직 수행자 쓰레드<br/>
 * 입력 메시지 대응 비지니스 로직 수행후 결과로 얻은 출력 메시지가 담긴 편지 묶음을 출력 메시지 큐에 넣는다.
 * 
 * @author Jonghoon Won
 */
public class ExecutorProcessor extends Thread implements CommonRootIF {
	private int index;
	private CommonProjectInfo commonProjectInfo;
	private LinkedBlockingQueue<LetterFromClient> inputMessageQueue;
	private LinkedBlockingQueue<LetterToClient> ouputMessageQueue;
	private MessageMangerIF messageManger;
	private SererExecutorClassLoaderManagerIF sererExecutorClassLoaderManager;
	private ClientResourceManagerIF clientResourceManager;
	
	

	
	/**
	 * 생성자
	 * @param index 순번
	 * @param commonProjectInfo 연결 공통 데이터
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param ouputMessageQueue 출력 메시지 큐
	 * @param messageManger 메시지 관리자
	 * @param sererExecutorClassLoaderManager 서버 비지니스 로직 클래스 로더 관리자
	 * @param clientResourceManager 클라이언트 자원 관리자
	 * 
	 */
	public ExecutorProcessor(int index,
			CommonProjectInfo commonProjectInfo, 
			LinkedBlockingQueue<LetterFromClient> inputMessageQueue,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue,
			MessageMangerIF messageManger,
			SererExecutorClassLoaderManagerIF sererExecutorClassLoaderManager,
			ClientResourceManagerIF clientResourceManager) {
		this.index = index;
		this.commonProjectInfo = commonProjectInfo;
		this.inputMessageQueue = inputMessageQueue;
		this.ouputMessageQueue = ouputMessageQueue;
		this.messageManger = messageManger;
		this.sererExecutorClassLoaderManager = sererExecutorClassLoaderManager;
		// this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		this.clientResourceManager = clientResourceManager;
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				LetterFromClient fromLetter = inputMessageQueue.take();

				InputMessage inObj = fromLetter.getInputMessage();
				SocketChannel fromSC = fromLetter.getFromSC();
				
				String messageID = inObj.getMessageID();
				try {
					AbstractServerExecutor executor = sererExecutorClassLoaderManager.getServerExecutorObject(messageID);
					if (executor instanceof AbstractAuthServerExecutor) {
						/** 로그인 요구 서비스인 경우 로그인 여부 검사 */
						if (! clientResourceManager.getClientResource(fromSC).isLogin()) {
							OutputMessage errorOutObj = messageManger.createOutputMessage("SelfExn");

							errorOutObj.messageHeaderInfo = inObj.messageHeaderInfo;
							errorOutObj.setAttribute("whereError", "S");
							errorOutObj.setAttribute("errorGubun", "A");
							errorOutObj.setAttribute("errorMessageID", messageID);
							errorOutObj.setAttribute("errorMessage", "this input message demand login");

							LetterToClient toLetter = new LetterToClient(fromSC, errorOutObj);
							ouputMessageQueue.put(toLetter);
							return;
						}
					}
					
					ArrayList<LetterToClient> toList = null;
					
					if (messageID.equals("DownFileInfo") || messageID.equals("UpFileInfo")) {
						toList = executor.executeInputMessage(fromSC, inObj, commonProjectInfo, ouputMessageQueue, messageManger, clientResourceManager);
					} else {
						toList = executor.executeInputMessage(fromSC, inObj, commonProjectInfo, messageManger, clientResourceManager); 
					}
					

					// if (null == toList) continue;
					
					int size = toList.size();
					for (int i = 0; i < size; i++) {
						LetterToClient toLetter = toList.get(i);
						ouputMessageQueue.put(toLetter);
					}
				} catch (DynamicClassCallException e) {
					log.warn(String.format("fromSC=[%d], inObj[%s], %s", fromSC.hashCode(), inObj.toString(), e.getMessage()), e);
					
					OutputMessage errorOutObj = null;
					try {
						errorOutObj = messageManger.createOutputMessage("SelfExn");
					} catch (MessageInfoNotFoundException e1) {
						log.fatal("시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
						System.exit(1);
					}
					
					errorOutObj.messageHeaderInfo = inObj.messageHeaderInfo;
					errorOutObj.setAttribute("whereError", "S");
					errorOutObj.setAttribute("errorGubun", "D");
					errorOutObj.setAttribute("errorMessageID", messageID);
					errorOutObj.setAttribute("errorMessage", e.getMessage());

					LetterToClient toLetter = new LetterToClient(fromSC, errorOutObj);
					ouputMessageQueue.put(toLetter);
				} catch (MessageInfoNotFoundException e) {
					log.warn(String.format("fromSC=[%d], inObj[%s], %s", fromSC.hashCode(), inObj.toString(), e.getMessage()), e);
					
					OutputMessage errorOutObj = null;
					try {
						errorOutObj = messageManger.createOutputMessage("SelfExn");
					} catch (MessageInfoNotFoundException e1) {
						log.fatal("시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
						System.exit(1);
					}
					
					errorOutObj.messageHeaderInfo = inObj.messageHeaderInfo;
					errorOutObj.setAttribute("whereError", "S");
					errorOutObj.setAttribute("errorGubun", "M");
					errorOutObj.setAttribute("errorMessageID", inObj.getMessageID());
					errorOutObj.setAttribute("errorMessage", e.getMessage());

					LetterToClient toLetter = new LetterToClient(fromSC, errorOutObj);
					ouputMessageQueue.put(toLetter);
				} catch(MessageItemException e) {
					log.warn(String.format("fromSC=[%d], inObj[%s], %s", fromSC.hashCode(), inObj.toString(), e.getMessage()), e);
					
					OutputMessage errorOutObj = null;
					try {
						errorOutObj = messageManger.createOutputMessage("SelfExn");
					} catch (MessageInfoNotFoundException e1) {
						log.fatal("시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
						System.exit(1);
					}
					
					errorOutObj.messageHeaderInfo = inObj.messageHeaderInfo;
					errorOutObj.setAttribute("whereError", "S");
					errorOutObj.setAttribute("errorGubun", "M");
					errorOutObj.setAttribute("errorMessageID", inObj.getMessageID());
					errorOutObj.setAttribute("errorMessage", e.getMessage());

					LetterToClient toLetter = new LetterToClient(fromSC, errorOutObj);
					ouputMessageQueue.put(toLetter);
				} catch (Exception e) {
					
					String errorMessgae = e.getMessage();
					if (null == errorMessgae) {
						errorMessgae = "Unknown Exception";
					} else {
						errorMessgae = new StringBuilder("Unknown Exception::").append(errorMessgae).toString();
						
					}
					
					log.warn(String.format("fromSC=[%d], inObj[%s], %s", fromSC.hashCode(), inObj.toString(), errorMessgae), e);
					
					
					OutputMessage errorOutObj = null;
					try {
						errorOutObj = messageManger.createOutputMessage("SelfExn");
					} catch (MessageInfoNotFoundException e1) {
						log.fatal("시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
						System.exit(1);
					}
					
					errorOutObj.messageHeaderInfo = inObj.messageHeaderInfo;
					errorOutObj.setAttribute("whereError", "S");
					errorOutObj.setAttribute("errorGubun", "U");
					errorOutObj.setAttribute("errorMessageID", inObj.getMessageID());
					errorOutObj.setAttribute("errorMessage", e.getMessage());

					LetterToClient toLetter = new LetterToClient(fromSC, errorOutObj);
					ouputMessageQueue.put(toLetter);
				}
			}
			log.warn("Thread loop exit");
		} catch (InterruptedException e) {
			log.warn(String.format("Index[%d] stop", index), e);
		} catch (Exception e) {
			log.warn(String.format("Index[%d] error", index), e);
		}

	}
}
