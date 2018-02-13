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
package kr.pe.sinnori.client.connection.asyn.mailbox;

import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class AsynPrivateMailbox implements AsynMailboxIF {
	private Logger log = LoggerFactory.getLogger(AsynPrivateMailbox.class);

	private final Object monitor = new Object();

	private int mailboxID;
	private SynchronousQueue<WrapReadableMiddleObject> outputMessageQueue = 
			new SynchronousQueue<WrapReadableMiddleObject>();
	private long socketTimeOut;

	private int mailID = Integer.MIN_VALUE;

	public AsynPrivateMailbox(int mailboxID, long socketTimeOut) {
		if (0 == mailboxID) {
			String errorMessage = String.format("the parameter mailboxID[%d] is equal to zero that is a public mail box's id", mailboxID);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (mailboxID < 0) {
			String errorMessage = String.format("the parameter mailboxID[%d] is less than zero", mailboxID);
			throw new IllegalArgumentException(errorMessage);
		}

		if (mailboxID > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			String errorMessage = String.format("the parameter mailboxID[%d] is greater than unsinged short max[%d]",
					mailboxID, CommonStaticFinalVars.UNSIGNED_SHORT_MAX);
			throw new IllegalArgumentException(errorMessage);
		}		

		if (socketTimeOut < 0) {
			String errorMessage = String.format("the parameter socketTimeOut[%d] is less than zero", socketTimeOut);
			throw new IllegalArgumentException(errorMessage);
		}

		this.mailboxID = mailboxID;
		this.socketTimeOut = socketTimeOut;
	}

	
	/*private int getMailboxID() {
		return mailboxID;
	}*/

	private void nextMailID() {
		//synchronized (monitor) {
			if (Integer.MAX_VALUE == mailID) {
				mailID = Integer.MIN_VALUE;
			} else {
				mailID++;
			}
			// return mailID;
		//}
	}
	
	public int getMailboxID () {
		return mailboxID;
	}
	
	public int getMailID() {
		return mailID;
	}
	
	public void putToSyncOutputMessageQueue(WrapReadableMiddleObject wrapReadableMiddleObject)
			throws InterruptedException {
		//log.info("putToSyncOutputMessageQueue::{}", wrapReadableMiddleObject.toString());
		//synchronized (monitor) {
			int fromMailboxID = wrapReadableMiddleObject.getMailboxID();
			if (mailboxID != fromMailboxID) {
				log.error("the mailbox id of the received message[{}] is not same to this mail box id[%{}]", 
						wrapReadableMiddleObject.toString(), mailboxID);
				System.exit(1);
			}

			int fromMailID = wrapReadableMiddleObject.getMailID();

			if (mailID != fromMailID) {
				log.warn("drop the received message[{}] because it's mail id is different form this mailbox's mail id[{}]", 
						wrapReadableMiddleObject.toString(), mailID);
				return;
			}
			
			// boolean result = false;

			// log.info("1.putToSyncOutputMessageQueue::outputMessageQueue.offer::isEmpty={}", outputMessageQueue.isEmpty());
			if (! outputMessageQueue.isEmpty()) {
				log.info("putToSyncOutputMessageQueue::큐가 꽉 차 있어 큐를 비움::outputMessageQueue.poll");
				WrapReadableMiddleObject timeoutWrapReadableMiddleObject = outputMessageQueue.poll();
				if (null == timeoutWrapReadableMiddleObject) {
					log.error("큐가 가득찬 상태라서 비우기 위해 큐에서 가져온 원소가 널임, 원인 파악하여 제거 필요함, 입력 메시지[{}]", wrapReadableMiddleObject.toString());
					System.exit(1);
				}
			}
			
			// log.info("putToSyncOutputMessageQueue::outputMessageQueue.offer");			
			boolean result = outputMessageQueue.offer(wrapReadableMiddleObject);
			if (! result) {
				log.warn("drop the received message[{}] because it was failed to insert the received message into outputmessage-queue[isEmpty={}]", wrapReadableMiddleObject.toString(), outputMessageQueue.isEmpty());
			}
		//}
	}

	public WrapReadableMiddleObject getSyncOutputMessage() throws SocketTimeoutException, InterruptedException {
		//synchronized (monitor) {
		try {
			long startTime = new Date().getTime();
			long elapsedTime =  0;
			
			//log.info("1.getSyncOutputMessage::outputMessageQueue.poll");
			WrapReadableMiddleObject wrapReadableMiddleObject = outputMessageQueue.poll(socketTimeOut, TimeUnit.MILLISECONDS);;
			if (null == wrapReadableMiddleObject) {
				String errorMessage = String.format("1.서버 응답 시간[%d]이 초과되었습니다. mailboxID=[%d], mailID=[%d]", socketTimeOut,
						mailboxID, mailID);
				// log.warn(errorMessage);
				
				/*// FIXME!
				Thread.sleep(1000);*/
				
				throw new SocketTimeoutException(errorMessage);
			}
			
			while (wrapReadableMiddleObject.getMailID() != mailID) {
				log.info("큐로 부터 얻어온 출력 메시지[{}]의 메시지 식별자가 이 메일 박스의 메일 식별자[{}]와 달라 폐기", 
						wrapReadableMiddleObject.toString(), mailID);
				
				elapsedTime = startTime - new Date().getTime();
				
				if (elapsedTime > socketTimeOut) {
					String errorMessage = String.format("2.서버 응답 시간[%d]이 초과되었습니다. mailboxID=[%d], mailID=[%d]", socketTimeOut,
							mailboxID, mailID);
					log.warn(errorMessage);
					throw new SocketTimeoutException(errorMessage);
				}
				
				//log.info("2.getSyncOutputMessage::outputMessageQueue.poll");
				wrapReadableMiddleObject = 
						outputMessageQueue.poll(socketTimeOut - elapsedTime, TimeUnit.MILLISECONDS);
				
				if (null == wrapReadableMiddleObject) {
					String errorMessage = String.format("3.서버 응답 시간[%d]이 초과되었습니다. mailboxID=[%d], mailID=[%d]", socketTimeOut,
							mailboxID, mailID);
					log.warn(errorMessage);
					throw new SocketTimeoutException(errorMessage);
				}
			} 

			
			// log.info("getSyncOutputMessage::{}", wrapReadableMiddleObject.toString());
			return wrapReadableMiddleObject;
		} finally {
			nextMailID();
		}
		//}
	}	

	public int hashCode() {
		return monitor.hashCode();
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AsynPrivateMailbox [mailboxID=");
		builder.append(mailboxID);
		builder.append(", mailID=");
		builder.append(mailID);
		builder.append(", socketTimeOut=");
		builder.append(socketTimeOut);
		builder.append("]");
		return builder.toString();
	}
	
}
