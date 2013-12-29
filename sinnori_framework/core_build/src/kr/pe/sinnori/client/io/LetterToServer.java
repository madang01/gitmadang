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

package kr.pe.sinnori.client.io;

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.common.message.InputMessage;

/**
 * 서버로 보내는 입력 메시지와 연결 클래스를 담은 클래스.
 * 
 * @author Jonghoon Won
 * 
 */
public class LetterToServer {
	private AbstractAsynConnection serverConnection;
	private InputMessage intputMessage;

	/**
	 * 생성자
	 * 
	 * @param serverConnection
	 *            비동기 방식의 소켓 채널을 갖는 연결 클래스
	 * @param intputMessage
	 *            입력 메시지
	 */
	public LetterToServer(AbstractAsynConnection serverConnection,
			InputMessage intputMessage) {
		this.serverConnection = serverConnection;
		this.intputMessage = intputMessage;
	}

	/**
	 * 비동기 방식의 소켓 채널을 갖는 연결 클래스를 반환한다.
	 * 
	 * @return 비동기 방식의 소켓 채널을 갖는 연결 클래스
	 */
	public AbstractAsynConnection getServerConnection() {
		return serverConnection;
	}

	/**
	 * 입력 메시지를 반환한다.
	 * 
	 * @return 입력 메시지
	 */
	public InputMessage getInputMessage() {
		return intputMessage;
	}

	@Override
	public String toString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("SocketChannel hash[");
		if (serverConnection != null) {
			strBuff.append(serverConnection.toString());
		} else {
			strBuff.append("serverConnection is empty");
		}
		strBuff.append("], out=");
		strBuff.append(intputMessage.toString());
		return strBuff.toString();
	}

}
