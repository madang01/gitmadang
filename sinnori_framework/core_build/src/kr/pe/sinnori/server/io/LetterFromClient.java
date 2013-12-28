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
import kr.pe.sinnori.server.ClientResource;

/**
 * 클라이언트로부터 받은 편지(=수신편지). 클라이언트로부터 받은 편지는 송신자와 송신자가 보낸 입력 메세지로 구성된다.
 * 
 * @author Jonghoon Won
 * 
 */
public class LetterFromClient {
	private SocketChannel clientSC;
	private InputMessage inObj;
	private ClientResource clientResource  = null;

	public LetterFromClient(SocketChannel clientSC, InputMessage inObj, ClientResource clientResource) {
		this.clientSC = clientSC;
		this.inObj = inObj;
		this.clientResource = clientResource;
	}
	
	/**
	 * 메세지를 반환한다.
	 * 
	 * @return 메세지
	 */
	public InputMessage getInputMessage() {
		return inObj;
	}
	
	/**
	 * 송신할 client, 즉 송신자를 반환한다.
	 * 
	 * @return 송신할 client, 즉 송신자
	 */
	/*
	public SocketChannel getFromSC() {
		return clientSC;
	}
	*/
	
	public ClientResource getClientResource() {
		return clientResource;
	}

	@Override
	public String toString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("clientSC=[");
		if (clientSC != null) {
			strBuff.append(clientSC.hashCode());
		} else {
			strBuff.append("clientSC is empty");
		}
		strBuff.append("], inObj=");
		strBuff.append(inObj.toString());
		return strBuff.toString();
	}	
}
