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

import java.util.ArrayList;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.server.ClientResource;

/**
 * 클라이언트로 보내는 편지 배달부. 서버 비지니스 로직 호출할때 마다 할당 된다. 
 * @author Jonghoon won
 *
 */
public class LetterSender implements CommonRootIF {
	private AbstractMessage messageFromClient;
	private ClientResource clientResource  = null; 
	private ArrayList<AbstractMessage> messageToClientList = new ArrayList<AbstractMessage>();
	
	/**
	 * 생성자
	 * @param inObjClientResource 입력 메시지 보낸 클라이언트의 자원
	 * @param inObj 입력 메시지, 입력 메시지를 보낸 클라이언트 당사자한테 출력 메시지를 보낼때 입력 메시지의 메일박스 식별자와 메일 식별자가 필요하다.
	 */
	public LetterSender(ClientResource clientResource, AbstractMessage messageFromClient) {
		this.clientResource = clientResource;
		this.messageFromClient = messageFromClient;
	}
	
	/**
	 * 출력 메시지를 비 익명으로 입력 메시지 보낸 클라이언트로 보낸다.
	 * @param outObj 출력 메시지
	 */
	public void addSyncMessage(AbstractMessage messageToClient) {
		messageToClient.messageHeaderInfo = messageFromClient.messageHeaderInfo;
		
		messageToClientList.add(messageToClient);
	}
	
	/**
	 * 출력 메시지를 익명으로 입력 메시지 보낸 클라이언트로 보낸다.
	 * @param outObj
	 */
	public void addAsynMessage(AbstractMessage messageToClient) {
		messageToClient.messageHeaderInfo.mailboxID = CommonStaticFinalVars.ASYN_MAILBOX_ID;
		messageToClient.messageHeaderInfo.mailID = clientResource.getServerMailID();
		
		messageToClientList.add(messageToClient);
	}
	
	public ArrayList<AbstractMessage> getMessageToClientList() {
		return messageToClientList;
	}
	
	/**
	 * 전체 목록 삭제. 주의점) 로그 없다. 만약 로그 필요시 {@link #writeLog(String) } 호출할것
	 */
	public void clearMessageToClientList() {
		messageToClientList.clear();
	}

	/**
	 * @return 입력 메시지를 보낸 클라이언트의 자원
	 */
	public ClientResource getClientResource() {
		return clientResource;
	}
	
	public void writeLogAll(String title) {
		int i=0;
		for (AbstractMessage messageToClient : messageToClientList) {
			log.info("%::전체삭제-잔존 메시지[{}]=[{}]", i++, messageToClient.toString());
		}
	}
}
