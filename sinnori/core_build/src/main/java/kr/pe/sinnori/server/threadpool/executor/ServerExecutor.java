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

package kr.pe.sinnori.server.threadpool.executor;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

/**
 * 서버 비지니스 로직 수행자 쓰레드<br/>
 * 입력 메시지 대응 비지니스 로직 수행후 결과로 얻은 출력 메시지가 담긴 편지 묶음을 출력 메시지 큐에 넣는다.
 * 
 * @author Won Jonghoon
 */
public class ServerExecutor extends Thread implements ServerExecutorIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ServerExecutor.class);
	
	// private final Object monitor = new Object();
	
	private int index;
	private String projectName = null;
	
	private ArrayBlockingQueue<FromLetter> inputMessageQueue;
	private MessageProtocolIF messageProtocol = null;
	
	private SocketResourceManagerIF socketResourceManager = null;
	private ServerObjectCacheManagerIF serverObjectCacheManager = null;	
	
	private final ConcurrentHashMap<SocketChannel, SocketChannel> socketChannelHash 
		= new ConcurrentHashMap<SocketChannel, SocketChannel>();

	public ServerExecutor(int index,
			String projectName,
			ArrayBlockingQueue<FromLetter> inputMessageQueue,
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
		log.info("{} ServerExecutor[{}] start", projectName, index);
		
		try {
			while (!Thread.currentThread().isInterrupted()) {
				FromLetter letterFromClient = inputMessageQueue.take();
				
				// FIXME!
				// log.info("{} ServerExecutor[{}] letterFromClient=[{}]", projectName, index, letterFromClient.toString());
				
				SocketChannel fromSC = letterFromClient.getFromSocketChannel();
				
				WrapReadableMiddleObject wrapReadableMiddleObject = letterFromClient.getWrapReadableMiddleObject();
				String messageID = wrapReadableMiddleObject.getMessageID();
				
				SocketResource socketResourceOfFromSC = socketResourceManager.getSocketResource(fromSC);
				if (null == socketResourceOfFromSC) {
					log.warn("the socket channel[{}] sending a input message[{}] failed to get socket resources, so stop to do task",
							fromSC.hashCode(), wrapReadableMiddleObject.toSimpleInformation());
					continue;
				}
				
				PersonalLoginManagerIF personalLoginManagerOfFromSC = socketResourceOfFromSC.getPersonalLoginManager();
				
				
				try {
					AbstractServerTask  serverTask = null;
					try {
						serverTask = serverObjectCacheManager.getServerTask(messageID);	
					} catch (DynamicClassCallException e) {
						log.warn(e.getMessage());

						SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);						
						String errorReason = e.getMessage();			
						ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(fromSC, 
								errorType,
								errorReason,
								wrapReadableMiddleObject, 
								socketResourceOfFromSC, messageProtocol);
						
						continue;
					} catch(Exception | Error e) {
						log.warn("unknown error::fail to get a input message server task", e);
						
						SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
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
								socketResourceManager,
								socketResourceOfFromSC,
								personalLoginManagerOfFromSC,
								wrapReadableMiddleObject, 
								messageProtocol);
					} catch (InterruptedException e) {
						throw e;
					} catch(Exception | Error e) {
						log.warn("unknwon error::fail to execute a input message server task", e);						
						
						SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(ServerTaskException.class);
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
			
			log.warn("{} ServerExecutor[{}] loop exit", projectName, index);
		} catch (InterruptedException e) {
			log.warn("{} ServerExecutor[{}] stop", projectName, index);
		} catch (Exception e) {
			String errorMessage = String.format("%s ServerExecutor[%d] unknown error", projectName, index); 
			log.warn(errorMessage, e);
		}
	}	

	@Override
	public void addNewSocket(SocketChannel newSC) {
		socketChannelHash.put(newSC, newSC);
	}
	
	public void removeSocket(SocketChannel sc) {
		socketChannelHash.remove(sc);
	}


	@Override
	public int getNumberOfConnection() {
		return socketChannelHash.size();
	}
	
	@Override
	public void putIntoQueue(FromLetter fromLetter) throws InterruptedException {
		try {
		inputMessageQueue.put(fromLetter);
		} catch(InterruptedException e) {
			WrapReadableMiddleObject wrapReadableMiddleObject = fromLetter.getWrapReadableMiddleObject();
			log.info("drop the input message[{}] becase of InterruptedException", wrapReadableMiddleObject.toString());
			throw e;
		}
	}
	
	public void finalize() {
		log.warn("{} ServerExecutor[{}] finalize", projectName, index);
	}
}
