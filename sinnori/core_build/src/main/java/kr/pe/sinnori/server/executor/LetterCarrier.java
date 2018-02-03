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

package kr.pe.sinnori.server.executor;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResource;

/**
 * 클라이언트로 보내는 편지 배달부. 서버 비지니스 로직 호출할때 마다 할당 된다. 
 * @author Won Jonghoon
 *
 */
public class LetterCarrier {
	private Logger log = LoggerFactory.getLogger(LetterCarrier.class);
	
	private AbstractServerTask serverTask = null;
	private AbstractMessage messageFromClient;
	private String messageIDFromClient = null;
	private SocketResource clientResource  = null;
	private LinkedBlockingQueue<ToLetter> ouputMessageQueue = null;
	private MessageProtocolIF messageProtocol= null;
	private ServerObjectCacheManagerIF serverObjectCacheManager = null;
	private ArrayList<ToLetter> letterToClientList = new ArrayList<ToLetter>();
	
	private SocketChannel fromSC = null;
	
	/**
	 * 생성자
	 * @param inObjClientResource 입력 메시지 보낸 클라이언트의 자원
	 * @param inObj 입력 메시지, 입력 메시지를 보낸 클라이언트 당사자한테 출력 메시지를 보낼때 입력 메시지의 메일박스 식별자와 메일 식별자가 필요하다.
	 */
	public LetterCarrier(AbstractServerTask serverTask, 
			SocketChannel fromSC, 
			AbstractMessage messageFromClient,
			LinkedBlockingQueue<ToLetter> ouputMessageQueue,
			MessageProtocolIF messageProtocol,
			ServerObjectCacheManagerIF serverObjectCacheManager) {
		this.serverTask = serverTask;
		this.fromSC = fromSC;
		this.messageFromClient = messageFromClient;
		this.messageIDFromClient = messageFromClient.getMessageID();
		this.ouputMessageQueue = ouputMessageQueue;
		this.messageProtocol = messageProtocol;
		this.serverObjectCacheManager = serverObjectCacheManager;
		
		
	}
	
	/**
	 * 출력 메시지를 비 익명으로 입력 메시지 보낸 클라이언트로 보낸다.
	 * @param outObj 출력 메시지
	 */
	public void addSyncMessage(AbstractMessage messageToClient) {
		messageToClient.messageHeaderInfo = messageFromClient.messageHeaderInfo;
		
		if (letterToClientList.size() > 0) {
			log.warn("동기 메시지는 1개만 가질 수 있습니다.  추가 취소된 메시지={}", messageToClient.toString());
			return;
		}
		
		List<WrapBuffer> wrapBufferList = getMessageStream(serverTask, messageIDFromClient, messageToClient, messageProtocol, serverObjectCacheManager);
		if (null != wrapBufferList) {
			ToLetter letterToClient = getToLetter(messageToClient, wrapBufferList);
			letterToClientList.add(letterToClient);
		}
	}
	
	private ToLetter getToLetter(AbstractMessage messageToClient, List<WrapBuffer> wrapBufferList) {		
		ToLetter letterToClient = new ToLetter(fromSC, messageToClient.getMessageID(), 
				messageToClient.messageHeaderInfo.mailboxID,
				messageToClient.messageHeaderInfo.mailID,
				wrapBufferList);		
		return letterToClient;
	}
	
	private List<WrapBuffer> getMessageStream(AbstractServerTask serverTask, String messageIDFromClient, 
			AbstractMessage  messageToClient,
			MessageProtocolIF messageProtocol,			
			ServerObjectCacheManagerIF serverObjectCacheManager) {
		return serverTask.getMessageStream(messageIDFromClient, fromSC, messageToClient, messageProtocol, serverObjectCacheManager);
	}
	
	
	/**
	 * 출력 메시지를 익명으로 입력 메시지 보낸 클라이언트로 보낸다.
	 * @param outObj
	 */
	public void addAsynMessage(AbstractMessage messageToClient) {
		messageToClient.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		messageToClient.messageHeaderInfo.mailID = clientResource.getServerMailID();
		
		List<WrapBuffer> wrapBufferList = getMessageStream(serverTask, messageIDFromClient, messageToClient, messageProtocol, serverObjectCacheManager);
		if (null != wrapBufferList) {
			ToLetter letterToClient = getToLetter(messageToClient, wrapBufferList);
			letterToClientList.add(letterToClient);
		}
	}
	
	public void addAsynMessage(AbstractMessage messageToClient, SocketChannel toSC) {
		messageToClient.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		messageToClient.messageHeaderInfo.mailID = clientResource.getServerMailID();
		
		List<WrapBuffer> wrapBufferList = serverTask.getMessageStream(messageIDFromClient, toSC, messageToClient,  messageProtocol, serverObjectCacheManager);
		if (null != wrapBufferList) {
			ToLetter letterToClient = getToLetter(messageToClient, wrapBufferList);
			letterToClientList.add(letterToClient);
		}
	}
	
	public void directSendAsynMessage(AbstractMessage messageToClient, SocketChannel toSC) {
		messageToClient.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		messageToClient.messageHeaderInfo.mailID = clientResource.getServerMailID();
		
		List<WrapBuffer> wrapBufferList = serverTask.getMessageStream(messageIDFromClient, toSC, messageToClient, messageProtocol, serverObjectCacheManager);
		if (null != wrapBufferList) {
			ToLetter letterToClient = getToLetter(messageToClient, wrapBufferList);
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e) {
				try {
					ouputMessageQueue.put(letterToClient);
				} catch (InterruptedException e1) {
					log.error("재시도 과정에서 인터럽트 발생하여 종료, clientResource=[{}], messageFromClient=[{}], 전달 못한 송신 메시지=[{}]", 
							clientResource.toString(), messageFromClient.toString(), letterToClient.toString());
					Thread.interrupted();
				}
			}
		}
	}
	
	public void directSendAsynMessage(AbstractMessage messageToClient) {
		messageToClient.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		messageToClient.messageHeaderInfo.mailID = clientResource.getServerMailID();
		
		List<WrapBuffer> wrapBufferList = getMessageStream(serverTask, messageIDFromClient, messageToClient, messageProtocol, serverObjectCacheManager);
		if (null != wrapBufferList) {
			ToLetter letterToClient = getToLetter(messageToClient, wrapBufferList);
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e) {
				try {
					ouputMessageQueue.put(letterToClient);
				} catch (InterruptedException e1) {
					log.error("재시도 과정에서 인터럽트 발생하여 종료, clientResource=[{}], messageFromClient=[{}], 전달 못한 송신 메시지=[{}]", 
							clientResource.toString(), messageFromClient.toString(), letterToClient.toString());
					Thread.interrupted();
				}
			}
		}
	}
	
	public void directSendLetterToClientList() {
		for (ToLetter letterToClient : letterToClientList) {
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e) {
				try {
					ouputMessageQueue.put(letterToClient);
				} catch (InterruptedException e1) {
					log.error("재시도 과정에서 인터럽트 발생하여 종료, clientResource=[{}], messageFromClient=[{}], 전달 못한 송신 메시지=[{}]", 
							clientResource.toString(), messageFromClient.toString(), letterToClient.toString());
					Thread.interrupted();
				}
			}
		}
	}
	
	
	/*public ArrayList<LetterToClient> getMessageToClientList() {
		return letterToClientList;
	}*/
	
	/**
	 * 전체 목록 삭제. 주의점) 로그 없다. 만약 로그 필요시 {@link #writeLog(String) } 호출할것
	 */
	public void clearMessageToClientList() {
		letterToClientList.clear();
	}

	/**
	 * @return 입력 메시지를 보낸 클라이언트의 자원
	 */
	public SocketResource getClientResource() {
		return clientResource;
	}
	
	public void writeLogAll(String title) {
		int i=0;
		for (ToLetter letterToClient : letterToClientList) {
			log.info("%::전체삭제-잔존 메시지[{}]=[{}]", i++, letterToClient.toString());
		}
	}
	
	
}
