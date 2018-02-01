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

package kr.pe.sinnori.common.message.builder.info;


import java.io.File;

import kr.pe.sinnori.common.type.MessageTransferDirectionType;



/**
 * 메시지 정보 클래스.<br/>
 * 
 * <pre>
 * 참고) 메시지 표현 정규식
 * 메시지 = 메시지 식별자, 항목 그룹
 * 항목 그룹 = (항목)*
 * 항목 = (단일 항목 | 배열)
 * 단일 항목 = 이름, 타입, 타입 부가 정보{0..1}, 값
 * 타입 부가 정보 = 크기 | 문자셋
 * 배열 = 이름, 반복 횟수, (항목 그룹)
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public class MessageInfo {
	private String messageID = null;
	private String firstLowerMessageID = null;
	private MessageTransferDirectionType messageTransferDirectionType = null;
	
	private OrderedItemSet messageOrderedItemSet = new OrderedItemSet();
	
	private File messageInfoXMLFile = null;
		

	public File getMessageInfoXMLFile() {
		return messageInfoXMLFile;
	}

	public MessageInfo(String messageID, File messageInfoXMLFile) {		
		this.messageID = messageID;
		this.messageInfoXMLFile = messageInfoXMLFile;
		this.firstLowerMessageID = messageID.substring(0,1).toLowerCase()+messageID.substring(1);
	}

	public String getMessageID() {
		return messageID;
	}
	
	public java.util.Date getLastModified() {
		return new java.util.Date(messageInfoXMLFile.lastModified());
	}
	
	public void setDirection(MessageTransferDirectionType messageTransferDirectionType) {
		this.messageTransferDirectionType = messageTransferDirectionType;
	}
	
	public MessageTransferDirectionType getDirection() {
		return messageTransferDirectionType;
	}
	
	public String getFirstLowerMessageID() {
		return firstLowerMessageID;
	}
	
	public OrderedItemSet getOrderedItemSet() {
		return messageOrderedItemSet;
	}	
	
	public String toString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("{ messageID=");
		strBuff.append(messageID);
		strBuff.append(", direction=");
		strBuff.append(messageTransferDirectionType.toString());
		strBuff.append(", {");
		strBuff.append(messageOrderedItemSet.toString());
		strBuff.append("}}");

		return strBuff.toString();
	}
}
