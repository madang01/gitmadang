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

package kr.pe.sinnori.common.asyn;

import java.nio.channels.SocketChannel;
import java.util.List;

import kr.pe.sinnori.common.io.WrapBuffer;

public class ToLetter {
	private SocketChannel toSC = null;
	private String messageID = null;
	private int mailboxID;
	private int mailID;
	private List<WrapBuffer> wrapBufferList = null;


	public ToLetter(SocketChannel toSC, String messageID, int mailboxID, int mailID, List<WrapBuffer> wrapBufferList) {
		if (null == toSC) {
			throw new IllegalArgumentException("the parameter toSC is null");
		}
		
		if (null == messageID) {
			throw new IllegalArgumentException("the parameter messageID is null");
		}
		
		if (null == wrapBufferList) {
			throw new IllegalArgumentException("the parameter wrapBufferList is null");
		}
		
		this.toSC = toSC;
		this.messageID = messageID;
		this.mailboxID = mailboxID;
		this.mailID = mailID;
		this.wrapBufferList = wrapBufferList;
	}
	
	public int getMailboxID() {
		return mailboxID;
	}
	
	public int getMailID() {
		return mailID;
	}

	public String getMessageID() {
		return messageID;
	}
	
	public SocketChannel getToSC() {
		return toSC;
	}
	
	public List<WrapBuffer> getWrapBufferList() {
		return wrapBufferList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LetterToServer [toSC=");
		builder.append(toSC.hashCode());
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
