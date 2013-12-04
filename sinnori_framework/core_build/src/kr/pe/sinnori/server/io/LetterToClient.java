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

import kr.pe.sinnori.common.message.OutputMessage;

/**
 * 클라이언트에게 보내는 편지<br/>
 * 클라이언트에게 보내는 편지는 받을 대상(=소켓 채널)과 출력 메시지로 구성된다.
 * 
 * @author Jonghoon Won
 * 
 */
public class LetterToClient {
	private SocketChannel sc;
	private OutputMessage outputMessage;

	/**
	 * 
	 * @param sc
	 *            수신할 client, 즉 수신자
	 * @param outputMessage
	 *            수신자가 받아 보는 메시지
	 */
	public LetterToClient(SocketChannel sc, OutputMessage outputMessage) {
		this.sc = sc;
		this.outputMessage = outputMessage;
	}

	/**
	 * 수신할 client, 즉 수신자를 반환한다.
	 * 
	 * @return 수신할 client, 즉 수신자
	 */
	public SocketChannel getToSC() {
		return sc;
	}

	/**
	 * 메시지를 반환한다.
	 * 
	 * @return 메시지
	 */
	public OutputMessage getOutputMessage() {
		return outputMessage;
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
		strBuff.append(outputMessage.toString());
		return strBuff.toString();
	}

}
