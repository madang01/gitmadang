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
 * @author Jonghoon won
 *
 */
public class LetterSender implements CommonRootIF {
	private InputMessage inObj;
	private ClientResource inObjClientResource  = null;
	private LinkedBlockingQueue<LetterToClient> ouputMessageQueue = null;
	
	
	public LetterSender(ClientResource inObjClientResource, InputMessage inObj, LinkedBlockingQueue<LetterToClient> ouputMessageQueue) {
		this.inObjClientResource = inObjClientResource;
		this.inObj = inObj;
		this.ouputMessageQueue = ouputMessageQueue;
	}
	
	public void sendSelf(OutputMessage outObj) {
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
	
	public void sendAnonymous(OutputMessage outObj) {
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
	
	public void sendAnonymous(ClientResource outObjClientResource, OutputMessage outObj) {
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
	 * @return the inObjClientResource
	 */
	public ClientResource getInObjClientResource() {
		return inObjClientResource;
	}
}
