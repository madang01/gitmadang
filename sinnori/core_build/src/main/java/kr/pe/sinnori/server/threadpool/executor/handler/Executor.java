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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SelfExnUtil;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

/**
 * 서버 비지니스 로직 수행자 쓰레드<br/>
 * 입력 메시지 대응 비지니스 로직 수행후 결과로 얻은 출력 메시지가 담긴 편지 묶음을 출력 메시지 큐에 넣는다.
 * 
 * @author Won Jonghoon
 */
public class Executor extends Thread implements ExecutorIF {
	private Logger log = LoggerFactory.getLogger(Executor.class);
	
	// private final Object monitor = new Object();
	
	private int index;
	private LinkedBlockingQueue<FromLetter> inputMessageQueue;
	private MessageProtocolIF messageProtocol = null;
	
	private SocketResourceManagerIF socketResourceManager = null;
	private ServerObjectCacheManagerIF serverObjectCacheManager = null;
	
	private String projectName = null;
	
	private final Set<SocketChannel> socketChannelSet = Collections.synchronizedSet(new HashSet<SocketChannel>());

	public Executor(int index,
			String projectName,
			LinkedBlockingQueue<FromLetter> inputMessageQueue,
			MessageProtocolIF messageProtocol, 
			SocketResourceManagerIF socketResourceManager,
			ServerObjectCacheManagerIF serverObjectCacheManager) {
		this.index = index;		
		this.projectName = projectName;
		this.inputMessageQueue = inputMessageQueue;
		this.messageProtocol = messageProtocol;
		this.socketResourceManager = socketResourceManager;
		this.serverObjectCacheManager = serverObjectCacheManager;
	}
	

	@Override
	public void run() {
		log.info(String.format("%s ExecutorProcessor[%d] start", projectName, index));
		
		try {
			while (!Thread.currentThread().isInterrupted()) {
				FromLetter letterFromClient = inputMessageQueue.take();

				SocketChannel fromSC = letterFromClient.getFromSocketChannel();
				
				WrapReadableMiddleObject wrapReadableMiddleObject = letterFromClient.getWrapReadableMiddleObject();
				String messageID = wrapReadableMiddleObject.getMessageID();				
				
				SocketResource fromSocketResource = socketResourceManager.getSocketResource(fromSC);				
				PersonalLoginManagerIF fromPersonalLoginManager = fromSocketResource.getPersonalLoginManager();
				OutputMessageWriterIF fromOutputMessageWriter = fromSocketResource.getOutputMessageWriterWithMinimumMumberOfSockets();
				
				AbstractServerTask  serverTask = null;
				try {
					serverTask = serverObjectCacheManager.getServerTask(messageID);
					
					serverTask.execute(index, projectName, fromOutputMessageWriter, messageProtocol,
							fromSC, wrapReadableMiddleObject, fromPersonalLoginManager, serverObjectCacheManager);
				} catch (DynamicClassCallException e) {
					log.warn("DynamicClassCallException", e);
					
					String errorMessage = new StringBuilder("1.fail to load server task class that is dynamic class::").append(e.toString()).toString();
					
					SelfExn selfExnOutObj = new SelfExn();
					selfExnOutObj.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
					selfExnOutObj.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
					
					selfExnOutObj.setErrorPlace("S");
					selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class));
					selfExnOutObj.setErrorMessageID(messageID);
					selfExnOutObj.setErrorMessage(errorMessage);
					
					List<WrapBuffer> wrapBufferList = null;
					try {
						wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
					} catch(Throwable e1) {
						log.error("fail to convert 'SelfExn' message to stream", e1);
						System.exit(1);
					}
					
					putToOutputMessageQueue(fromSC, wrapReadableMiddleObject, selfExnOutObj, wrapBufferList, fromOutputMessageWriter);
					continue;
				} catch(Exception e) {
					log.warn("unknown error", e);
					
					String errorMessage = new StringBuilder("2.fail to load server task class that is dynamic class::").append(e.toString()).toString();
					
					SelfExn selfExnOutObj = new SelfExn();
					selfExnOutObj.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
					selfExnOutObj.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
					// selfExnOutObj.setError("S", messageID, new DynamicClassCallException("알수 없는 에러 발생::"+e.getMessage()));
					selfExnOutObj.setErrorPlace("S");
					selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class));
					selfExnOutObj.setErrorMessageID(messageID);
					selfExnOutObj.setErrorMessage(errorMessage);
					
					List<WrapBuffer> wrapBufferList = null;
					try {
						wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
						
						putToOutputMessageQueue(fromSC, wrapReadableMiddleObject, selfExnOutObj, wrapBufferList, fromOutputMessageWriter);
					} catch(Throwable e1) {
						log.error("fail to convert 'SelfExn' message to stream", e1);
						System.exit(1);
					}
					continue;
				} catch (Error e) {
					log.warn("unknown error", e);
					
					String errorMessage = new StringBuilder("3.fail to load server task class that is dynamic class::").append(e.toString()).toString();
					
					SelfExn selfExnOutObj = new SelfExn();
					selfExnOutObj.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
					selfExnOutObj.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
					// selfExnOutObj.setError("S", messageID, new DynamicClassCallException("알수 없는 에러 발생::"+e.getMessage()));
					selfExnOutObj.setErrorPlace("S");
					selfExnOutObj.setErrorGubun(SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class));
					selfExnOutObj.setErrorMessageID(messageID);
					selfExnOutObj.setErrorMessage(errorMessage);
					
					List<WrapBuffer> wrapBufferList = null;
					try {
						wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER);
						
						putToOutputMessageQueue(fromSC, wrapReadableMiddleObject, selfExnOutObj, wrapBufferList, fromOutputMessageWriter);
					} catch(Throwable e1) {
						log.error("fail to convert 'SelfExn' message to stream", e1);
						System.exit(1);
					}
					continue;
				} finally {
					/**
					 * <pre>
					 * MiddleReadableObject 가 가진 자원 반환을 하는 장소는  2군데이다.
					 * 첫번째 장소는 메시지 추출 후 쓰임이 다해서 호출하는 AbstractMessageDecoder#decode 이며
					 * 두번째 장소는 2번 연속 호출해도 무방하기때문에 안전하게 자원 반환을 보장하기위한 Executor#run 이다.
					 * </pre>
					 */
					wrapReadableMiddleObject.closeReadableMiddleObject();
				}
			}
			log.warn(String.format("%s ExecutorProcessor[%d] loop exit", projectName, index));
		} catch (InterruptedException e) {
			log.warn(String.format("%s ExecutorProcessor[%d] stop", projectName, index), e);
		} catch (Exception e) {
			log.warn(String.format("%s ExecutorProcessor[%d] unknown error", projectName, index), e);
		}

	}
	
	private void putToOutputMessageQueue(SocketChannel clientSC, 
			WrapReadableMiddleObject  receivedLetter,
			AbstractMessage wrapBufferMessage, List<WrapBuffer> wrapBufferList, 
			OutputMessageWriterIF outputMessageWriter) {
		
		ToLetter letterToClient = new ToLetter(clientSC,
				wrapBufferMessage.getMessageID(),
				wrapBufferMessage.messageHeaderInfo.mailboxID,
				wrapBufferMessage.messageHeaderInfo.mailID,
				wrapBufferList);
		try {
			outputMessageWriter.putIntoQueue(letterToClient);
		} catch (InterruptedException e) {
			try {
				outputMessageWriter.putIntoQueue(letterToClient);
			} catch (InterruptedException e1) {
				log.error("재시도 과정에서 인터럽트 발생하여 종료, clientSC hashCode=[{}], 입력 메시지[{}] 추출 실패, 전달 못한 송신 메시지=[{}]", 
					clientSC.hashCode(), receivedLetter.toString(), wrapBufferMessage.toString());
				Thread.interrupted();
			}
		}
	}


	@Override
	public void addNewSocket(SocketChannel newSC) {
		socketChannelSet.add(newSC);
	}
	
	public void removeSocket(SocketChannel sc) {
		socketChannelSet.remove(sc);
	}


	@Override
	public int getNumberOfSocket() {
		return socketChannelSet.size();
	}
	
	@Override
	public void putIntoQueue(FromLetter fromLetter) throws InterruptedException {
		inputMessageQueue.put(fromLetter);
	}
}
