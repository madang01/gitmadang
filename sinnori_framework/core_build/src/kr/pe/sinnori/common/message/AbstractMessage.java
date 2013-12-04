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

package kr.pe.sinnori.common.message;

import org.json.simple.JSONObject;


/**
 * 입출력 메시지 클래스의 부모 추상화 클래스.<br/> 
 * 메시지 구현 추상화 클래스를 상속 받으며 메시지 운용에 필요한  메시지 헤더 정보를 갖고 있다.
 * 
 * @author Jonghoon Won
 * 
 */
public abstract class AbstractMessage extends AbstractItemGroupDataOfMessage {
	
	/** 메시지 운용에 필요한 메시지 헤더 정보 */
	public MessageHeaderInfo messageHeaderInfo = new MessageHeaderInfo();

	/**
	 * 생성자
	 * @param messageID 메시지 생성을 원하는 메시지 식별자
	 * @param messageInfo 메시지 식별자에 대응하는 메시지 정보
	 */
	public AbstractMessage(String messageID, MessageInfo messageInfo){
		super(messageID, messageInfo);
	}
	
	public String toJSONString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("{ \"messageID\":\"");
		strBuilder.append(JSONObject.escape(messageID));
		strBuilder.append("\", \"mailboxID\":");
		strBuilder.append(messageHeaderInfo.mailboxID);
		strBuilder.append(", \"mailID\":");
		strBuilder.append(messageHeaderInfo.mailID);
		strBuilder.append(", ");
		strBuilder.append(super.toJSONString());
		strBuilder.append(" }");
		
		return strBuilder.toString(); 
	}
	
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(messageHeaderInfo.toString());
		strBuilder.append(", ");
		strBuilder.append(super.toString());
		return strBuilder.toString();
	}
}
