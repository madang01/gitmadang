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
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.etc.SelfExnUtil;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.ToLetterCarrier;

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
		log.warn("{} ExecutorProcessor[{}] start", projectName, index);
		
		try {
			while (!Thread.currentThread().isInterrupted()) {
				FromLetter letterFromClient = inputMessageQueue.take();

				SocketChannel fromSC = letterFromClient.getFromSocketChannel();
				
				WrapReadableMiddleObject wrapReadableMiddleObject = letterFromClient.getWrapReadableMiddleObject();
				String messageID = wrapReadableMiddleObject.getMessageID();
				
				SocketResource socketResourceOfFromSC = socketResourceManager.getSocketResource(fromSC);
				
				
				if (messageID.equals(SelfExn.class.getSimpleName())) {
					fromSC.close();
					log.warn("this message id[{}] is a system reserved message id. so the socket channel[hashCode={}] was closed", messageID, fromSC.hashCode());					
					continue;
				}
				
				AbstractServerTask  serverTask = null;
				try {
					
					try {
						serverTask = serverObjectCacheManager.getServerTask(messageID);	
					} catch (DynamicClassCallException e) {
						log.warn(e.getMessage());

						String errorType = SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class);
						String errorReason = e.getMessage();			
						ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(fromSC, 
								errorType,
								errorReason,
								wrapReadableMiddleObject, socketResourceOfFromSC, messageProtocol);
						
						continue;
					} catch(Exception | Error e) {
						log.warn("unknown error::fail to get a input message server task", e);
						
						String errorType = SelfExnUtil.getSelfExnErrorGubun(DynamicClassCallException.class);
						String errorReason = "fail to get a input message server task::"+e.getMessage();			
						ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(fromSC, 
								errorType,
								errorReason,
								wrapReadableMiddleObject, socketResourceOfFromSC, messageProtocol);
						continue;
					}
					
					try {
						serverTask.execute(index, projectName, 
								fromSC,
								socketResourceOfFromSC, 
								wrapReadableMiddleObject, 
								messageProtocol, 
								serverObjectCacheManager);
					} catch (InterruptedException e) {
						throw e;
					} catch(Exception | Error e) {
						log.warn("unknwon error::fail to execute a input message server task", e);						
						
						String errorType = SelfExnUtil.getSelfExnErrorGubun(ServerTaskException.class);
						String errorReason = "fail to execute a input message server task::"+e.getMessage();			
						ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(fromSC, 
								errorType,
								errorReason,
								wrapReadableMiddleObject, socketResourceOfFromSC, messageProtocol);
						continue;
					}
				
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
			log.warn("{} ExecutorProcessor[{}] loop exit", projectName, index);
		} catch (InterruptedException e) {
			log.warn("{} ExecutorProcessor[{}] stop", projectName, index);
		} catch (Exception e) {
			log.warn("unknown error", e);
			log.warn("{} ExecutorProcessor[{}] unknown error", projectName, index);
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
