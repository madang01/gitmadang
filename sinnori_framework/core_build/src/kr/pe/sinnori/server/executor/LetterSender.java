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

package kr.pe.sinnori.server.executor;

import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.io.LetterToClient;

/**
 * 클라이언트로 보내는 편지 배달부. 서버 비지니스 로직 호출할때 마다 할당 된다. 
 * @author Jonghoon won
 *
 */
public class LetterSender implements CommonRootIF {
	private InputMessage inObj;
	private ClientResource inObjClientResource  = null;
	private LinkedBlockingQueue<LetterToClient> ouputMessageQueue = null;
	
	/**
	 * 생성자
	 * @param inObjClientResource 입력 메시지 보낸 클라이언트의 자원
	 * @param inObj 입력 메시지, 입력 메시지를 보낸 클라이언트 당사자한테 출력 메시지를 보낼때 입력 메시지의 메일박스 식별자와 메일 식별자가 필요하다.
	 * @param ouputMessageQueue 출력 메시지를 담아 보낼 큐
	 */
	public LetterSender(ClientResource inObjClientResource, InputMessage inObj, LinkedBlockingQueue<LetterToClient> ouputMessageQueue) {
		this.inObjClientResource = inObjClientResource;
		this.inObj = inObj;
		this.ouputMessageQueue = ouputMessageQueue;
	}
	
	/**
	 * 출력 메시지를 비 익명으로 입력 메시지 보낸 클라이언트로 보낸다.
	 * @param outObj 출력 메시지
	 */
	public void sendSync(OutputMessage outObj) {
		outObj.messageHeaderInfo = inObj.messageHeaderInfo;
		
		try {
			ouputMessageQueue.put(inObjClientResource.getLetterToClient(outObj));
		} catch (InterruptedException e) {
			
			try {
				ouputMessageQueue.put(inObjClientResource.getLetterToClient(outObj));
			} catch (InterruptedException e1) {
				log.fatal("two InterruptedException", e1);
				System.exit(1);
			}
			Thread.currentThread().isInterrupted();
		}
	}
	
	/**
	 * 출력 메시지를 익명으로 입력 메시지 보낸 클라이언트로 보낸다.
	 * @param outObj
	 */
	public void sendAsyn(OutputMessage outObj) {
		outObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;
		outObj.messageHeaderInfo.mailID = inObjClientResource.getServerMailID();
		
		try {
			ouputMessageQueue.put(inObjClientResource.getLetterToClient(outObj));
		} catch (InterruptedException e) {
			
			try {
				ouputMessageQueue.put(inObjClientResource.getLetterToClient(outObj));
			} catch (InterruptedException e1) {
				log.fatal("two InterruptedException", e1);
				System.exit(1);
			}
			Thread.currentThread().isInterrupted();
		}
	}
	
	/**
	 * 출력 메시지를 익명으로 지정하는 클라이언트로 보낸다.
	 * @param outObjClientResource 출력 메시지를 받고자 하는 클라이언트 자원
	 * @param outObj 출력 메시지
	 */
	public void sendAsyn(ClientResource outObjClientResource, OutputMessage outObj) {
		outObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;
		outObj.messageHeaderInfo.mailID = outObjClientResource.getServerMailID();
		
		try {
			ouputMessageQueue.put(outObjClientResource.getLetterToClient(outObj));
		} catch (InterruptedException e) {
			
			try {
				ouputMessageQueue.put(outObjClientResource.getLetterToClient(outObj));
			} catch (InterruptedException e1) {
				log.fatal("two InterruptedException", e1);
				System.exit(1);
			}
			Thread.currentThread().isInterrupted();
		}
	}

	/**
	 * @return 입력 메시지를 보낸 클라이언트의 자원
	 */
	public ClientResource getInObjClientResource() {
		return inObjClientResource;
	}
}
