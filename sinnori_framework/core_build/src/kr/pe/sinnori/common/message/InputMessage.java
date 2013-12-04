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


/**
 * 입력 메시지 구현 클래스.
 * 
 * @author Jonghoon Won
 * 
 */
public class InputMessage extends AbstractMessage {

	/**
	 *생성자
	 * @param messageID 메시지 생성을 원하는 메시지 식별자
	 * @param messageInfo 메시지 식별자에 대응하는 메시지 정보
	 */
	public InputMessage(String messageID, MessageInfo messageInfo) {
		super(messageID, messageInfo);
	}
}
