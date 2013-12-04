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

import kr.pe.sinnori.common.message.InputMessage;

/**
 * 클라이언트로부터 받은 편지(=수신편지). 클라이언트로부터 받은 편지는 송신자와 송신자가 보낸 입력 메세지로 구성된다.
 * 
 * @author Jonghoon Won
 * 
 */
public class LetterFromClient {
	private SocketChannel sc;
	private InputMessage inputMessage;

	/**
	 * 메세지를 반환한다.
	 * 
	 * @return 메세지
	 */
	public InputMessage getInputMessage() {
		return inputMessage;
	}

	@Override
	public String toString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("SocketChannel hash[");
		if (sc != null) {
			strBuff.append(sc.hashCode());
		} else {
			strBuff.append("sc is empty");
		}
		strBuff.append("], out=");
		strBuff.append(inputMessage.toString());
		return strBuff.toString();
	}

	public LetterFromClient(SocketChannel sc, InputMessage inputMessage) {
		this.sc = sc;
		this.inputMessage = inputMessage;
	}

	/**
	 * 송신할 client, 즉 송신자를 반환한다.
	 * 
	 * @return 송신할 client, 즉 송신자
	 */
	public SocketChannel getFromSC() {
		return sc;
	}

}
