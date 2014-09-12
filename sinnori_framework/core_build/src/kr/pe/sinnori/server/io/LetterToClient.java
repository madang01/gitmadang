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

package kr.pe.sinnori.server.io;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * 클라이언트에게 보내는 편지<br/>
 * 클라이언트에게 보내는 편지는 받을 대상(=소켓 채널)과 출력 메시지로 구성된다.
 * 
 * @author Jonghoon Won
 * 
 */
public class LetterToClient {
	private SocketChannel toSC;
	private AbstractMessage messageToClient = null;
	private String messageID = null;
	int mailboxID;
	int mailID; 
	private ArrayList<WrapBuffer> wrapBufferList = null;
	 

	/**
	 * 생성자
	 * @param toSC
	 * @param messageToClient
	 */
	public LetterToClient(SocketChannel toSC, AbstractMessage messageToClient, ArrayList<WrapBuffer> wrapBufferList) {
		this.toSC = toSC;
		this.messageToClient = messageToClient;
		this.messageID = messageToClient.getMessageID();
		this.mailboxID = messageToClient.messageHeaderInfo.mailboxID;
		this.mailID = messageToClient.messageHeaderInfo.mailID;
		this.wrapBufferList = wrapBufferList;
	}
	
	/**
	 * 
	 * @param sc
	 *            수신할 client, 즉 수신자
	 * @param outObj
	 *            수신자가 받아 보는 메시지
	 */
	/*public LetterToClient(SocketChannel toSC, AbstractMessage messageToClient, 
			String messageID, int mailboxID, int mailID, ArrayList<WrapBuffer> wrapBufferList) {
		this.toSC = toSC;
		this.messageToClient = messageToClient;
		this.messageID = messageID;
		this.mailboxID = mailboxID;
		this.mailID = mailID;
		this.wrapBufferList = wrapBufferList;
	}*/

	/**
	 * 수신할 client, 즉 수신자를 반환한다.
	 * 
	 * @return 수신할 client, 즉 수신자
	 */
	public SocketChannel getToSC() {
		return toSC;
	}

	public AbstractMessage getMessageToClient() {
		return messageToClient;
	}
	
	public String getMessageID() {
		return messageID;
	}
	
	public int getMailboxID() {
		return mailboxID;
	}
	
	public int getMailID() {
		return mailID;
	}

	/** 
	 * @return 메시지 내용이 담긴 스트림 버퍼
	 */
	public ArrayList<WrapBuffer> getWrapBufferList() {
		return wrapBufferList;
	}
	
	/**
	 * 메시지 내용이 담긴 스트림 버퍼를 저장한다.
	 * @param wrapBufferList 메시지 내용이 담긴 스트림 버퍼
	 */
	/*public void setWrapBufferList(ArrayList<WrapBuffer> wrapBufferList) {
		this.wrapBufferList =wrapBufferList;
	}*/

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LetterToClient [toSC=");
		builder.append(toSC.hashCode());
		builder.append(", messageToClient=");
		builder.append(messageToClient.toString());
		builder.append(", messageID=");
		builder.append(messageID);
		builder.append(", mailboxID=");
		builder.append(mailboxID);
		builder.append(", mailID=");
		builder.append(mailID);
		builder.append(", wrapBufferList size=");
		builder.append(wrapBufferList.size());
		builder.append("]");
		return builder.toString();
	}
}
