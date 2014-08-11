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
package kr.pe.sinnori.common.protocol;


/**
 * 프로토콜 단에서 추출된 메시지 내용을 담는 클래스. 메시지 식별자, 메일 박스 식별자, 메일 식별자, 중간 다리 역활 읽기 객체를 담고 있다.
 * @author "Jonghoon Won"
 *
 */
public class ReceivedLetter {
	private String messageID = null;
	private int mailboxID;
	private int mailID;
	private Object middleReadObj = null;
	
	public ReceivedLetter(String messageID, int mailboxID, int mailID, Object middleReadObj) {
		this.messageID = messageID;
		this.mailboxID = mailboxID;
		this.mailID = mailID;
		this.middleReadObj = middleReadObj;
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

	public Object getMiddleReadObj() {
		return middleReadObj;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WrapMiddleReadObj [messageID=");
		builder.append(messageID);
		builder.append(", mailboxID=");
		builder.append(mailboxID);
		builder.append(", mailID=");
		builder.append(mailID);
		builder.append(", middleReadObj hashCode=");
		builder.append(middleReadObj.hashCode());
		builder.append("]");
		return builder.toString();
	}

	
	
}
