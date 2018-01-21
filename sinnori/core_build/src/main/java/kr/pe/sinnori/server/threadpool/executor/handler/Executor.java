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
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.io.LetterFromClient;
import kr.pe.sinnori.server.io.LetterToClient;

/**
 * 서버 비지니스 로직 수행자 쓰레드<br/>
 * 입력 메시지 대응 비지니스 로직 수행후 결과로 얻은 출력 메시지가 담긴 편지 묶음을 출력 메시지 큐에 넣는다.
 * 
 * @author Won Jonghoon
 */
public class Executor extends Thread {
	private Logger log = LoggerFactory.getLogger(Executor.class);
	
	private int index;
	private LoginManagerIF loginManager = null;
	private LinkedBlockingQueue<LetterFromClient> inputMessageQueue;
	private LinkedBlockingQueue<LetterToClient> ouputMessageQueue;
	private MessageProtocolIF messageProtocol = null;
	
	private ServerObjectCacheManagerIF serverObjectCacheManager = null;
	
	private String projectName = null;
	private Charset charsetOfProject = null;
	// private CharsetDecoder charsetDecoderOfProject = null;
	// private CharsetEncoder charsetEncoderOfProject = null;
	
	
	/**
	 * 생성자
	 * @param index 순번
	 * @param serverProjectConfig 프로젝트의 공통 포함한 서버 환경 변수
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param ouputMessageQueue 출력 메시지 큐
	 * @param messageProtocol 서버 프로젝트의 메시지 프로토콜
	 * @param loginManager 로그인 관리자
	 * @param serverObjectCacheManager 서버 객체 캐쉬 관리자
	 * @param sqlSessionFactory 서버 프로젝트의 Mybatis SqlSessionFactory
	 */
	public Executor(int index,
			String projectName,	
			Charset charsetOfProject,
			LinkedBlockingQueue<LetterFromClient> inputMessageQueue,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue,
			MessageProtocolIF messageProtocol, LoginManagerIF loginManager,
			ServerObjectCacheManagerIF serverObjectCacheManager) {
		this.index = index;	
		this.loginManager = loginManager;
		this.inputMessageQueue = inputMessageQueue;
		this.ouputMessageQueue = ouputMessageQueue;
		this.messageProtocol = messageProtocol;
		this.serverObjectCacheManager = serverObjectCacheManager;
		
		this.projectName = projectName;
		this.charsetOfProject = charsetOfProject;
		// charsetDecoderOfProject = CharsetUtil.createCharsetDecoder(charsetOfProject);
		// charsetEncoderOfProject = CharsetUtil.createCharsetEncoder(charsetOfProject);
	}
	

	@Override
	public void run() {
		log.info(String.format("%s ExecutorProcessor[%d] start", projectName, index));
		
		try {
			while (!Thread.currentThread().isInterrupted()) {
				LetterFromClient letterFromClient = inputMessageQueue.take();

				SocketChannel clientSC = letterFromClient.getFromSC();
				ClientResource clientResource = letterFromClient.getClientResource();
				ReceivedLetter receivedLetter = letterFromClient.getReceivedLetter();
				String messageID = receivedLetter.getMessageID();
				AbstractServerTask  serverTask = null;
				
				try {
					serverTask = serverObjectCacheManager.getServerTask(messageID);
				} catch (DynamicClassCallException e) {
					log.warn("DynamicClassCallException", e);
					
					String errorMessage = new StringBuilder("1.fail to load server task class that is dynamic class::").append(e.toString()).toString();
					
					SelfExn selfExnOutObj = new SelfExn();
					selfExnOutObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
					selfExnOutObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
					
					selfExnOutObj.setErrorPlace("S");
					selfExnOutObj.setErrorGubun(DynamicClassCallException.class);
					selfExnOutObj.setErrorMessageID(messageID);
					selfExnOutObj.setErrorMessage(errorMessage);
					
					List<WrapBuffer> wrapBufferList = null;
					try {
						wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
					} catch(Throwable e1) {
						log.error("fail to convert 'SelfExn' message to stream", e1);
						System.exit(1);
					}
					
					putToOutputMessageQueue(clientSC, receivedLetter, selfExnOutObj, wrapBufferList, ouputMessageQueue);
					continue;
				} catch(Exception e) {
					log.warn("unknown error", e);
					
					String errorMessage = new StringBuilder("2.fail to load server task class that is dynamic class::").append(e.toString()).toString();
					
					SelfExn selfExnOutObj = new SelfExn();
					selfExnOutObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
					selfExnOutObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
					// selfExnOutObj.setError("S", messageID, new DynamicClassCallException("알수 없는 에러 발생::"+e.getMessage()));
					selfExnOutObj.setErrorPlace("S");
					selfExnOutObj.setErrorGubun(DynamicClassCallException.class);
					selfExnOutObj.setErrorMessageID(messageID);
					selfExnOutObj.setErrorMessage(errorMessage);
					
					List<WrapBuffer> wrapBufferList = null;
					try {
						wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
						
						putToOutputMessageQueue(clientSC, receivedLetter, selfExnOutObj, wrapBufferList, ouputMessageQueue);
					} catch(Throwable e1) {
						log.error("fail to convert 'SelfExn' message to stream", e1);
						System.exit(1);
					}
					continue;
				} catch (Error e) {
					log.warn("unknown error", e);
					
					String errorMessage = new StringBuilder("3.fail to load server task class that is dynamic class::").append(e.toString()).toString();
					
					SelfExn selfExnOutObj = new SelfExn();
					selfExnOutObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
					selfExnOutObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
					// selfExnOutObj.setError("S", messageID, new DynamicClassCallException("알수 없는 에러 발생::"+e.getMessage()));
					selfExnOutObj.setErrorPlace("S");
					selfExnOutObj.setErrorGubun(DynamicClassCallException.class);
					selfExnOutObj.setErrorMessageID(messageID);
					selfExnOutObj.setErrorMessage(errorMessage);
					
					List<WrapBuffer> wrapBufferList = null;
					try {
						wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
						
						putToOutputMessageQueue(clientSC, receivedLetter, selfExnOutObj, wrapBufferList, ouputMessageQueue);
					} catch(Throwable e1) {
						log.error("fail to convert 'SelfExn' message to stream", e1);
						System.exit(1);
					}
					continue;
				}
				
				serverTask.execute(index, projectName, charsetOfProject, ouputMessageQueue, messageProtocol,
						clientSC, clientResource, receivedLetter, loginManager, serverObjectCacheManager);
				
			}
			log.warn(String.format("%s ExecutorProcessor[%d] loop exit", projectName, index));
		} catch (InterruptedException e) {
			log.warn(String.format("%s ExecutorProcessor[%d] stop", projectName, index), e);
		} catch (Exception e) {
			log.warn(String.format("%s ExecutorProcessor[%d] unknown error", projectName, index), e);
		}

	}
	
	private void putToOutputMessageQueue(SocketChannel clientSC, 
			ReceivedLetter  receivedLetter,
			AbstractMessage wrapBufferMessage, List<WrapBuffer> wrapBufferList, 
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue) {
		
		LetterToClient letterToClient = new LetterToClient(clientSC,
				wrapBufferMessage,
				wrapBufferList);  
		try {
			ouputMessageQueue.put(letterToClient);
		} catch (InterruptedException e) {
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e1) {
				log.error("재시도 과정에서 인터럽트 발생하여 종료, clientSC hashCode=[{}], 입력 메시지[{}] 추출 실패, 전달 못한 송신 메시지=[{}]", 
					clientSC.hashCode(), receivedLetter.toString(), wrapBufferMessage.toString());
				Thread.interrupted();
			}
		}
	}
}
