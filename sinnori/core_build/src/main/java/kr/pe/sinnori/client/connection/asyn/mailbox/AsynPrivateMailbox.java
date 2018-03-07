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
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class AsynPrivateMailbox implements AsynPrivateMailboxIF {
	private Logger log = LoggerFactory.getLogger(AsynPrivateMailbox.class);

	// private final Object monitor = new Object();

	private int mailboxID;
	// FromLetter fromLetter
	private SynchronousQueue<FromLetter> outputMessageQueue = new SynchronousQueue<FromLetter>();
	private long socketTimeOut;

	private transient int mailID = Integer.MIN_VALUE;

	public AsynPrivateMailbox(int mailboxID, long socketTimeOut) {
		if (0 == mailboxID) {
			String errorMessage = String
					.format("the parameter mailboxID[%d] is equal to zero that is a public mail box's id", mailboxID);
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

	/*
	 * private int getMailboxID() { return mailboxID; }
	 */

	private void nextMailID() {
		// synchronized (monitor) {
		if (Integer.MAX_VALUE == mailID) {
			mailID = Integer.MIN_VALUE;
		} else {
			mailID++;
		}
		// }
	}

	public int getMailboxID() {
		return mailboxID;
	}

	public int getMailID() {
		return mailID;
	}

	public void putToSyncOutputMessageQueue(FromLetter fromLetter) throws InterruptedException {
		if (null == fromLetter) {
			throw new IllegalArgumentException("the parameter fromLetter is null");
		}
		// log.info("putToSyncOutputMessageQueue::{}",
		// wrapReadableMiddleObject.toString());
		// synchronized (monitor) {
		WrapReadableMiddleObject wrapReadableMiddleObject = fromLetter.getWrapReadableMiddleObject();

		int fromMailboxID = wrapReadableMiddleObject.getMailboxID();
		if (mailboxID != fromMailboxID) {
			log.warn("drop the received message[{}] because it's mailbox id is different form this mailbox id[{}]",
					fromLetter.toString(), mailboxID);
			return;
		}

		int fromMailID = wrapReadableMiddleObject.getMailID();

		if (mailID != fromMailID) {
			log.warn("drop the received message[{}] because it's mail id is different form this mailbox's mail id[{}]",
					fromLetter.toString(), mailID);
			return;
		}

		// log.info("putToSyncOutputMessageQueue::outputMessageQueue.offer");
		boolean result = outputMessageQueue.offer(fromLetter, 500L, TimeUnit.MICROSECONDS);
		if (!result) {
			log.warn(
					"drop the received message[{}] because it was failed to insert the received message into outputmessage-queue[isEmpty={}]",
					fromLetter.toString(), outputMessageQueue.isEmpty());
		}
		// }
	}

	public WrapReadableMiddleObject getSyncOutputMessage() throws SocketTimeoutException, InterruptedException {
		// synchronized (monitor) {

		WrapReadableMiddleObject wrapReadableMiddleObject = null;

		boolean loop = false;
		try {
			do {				
				FromLetter fromLetter = outputMessageQueue.poll(socketTimeOut, TimeUnit.MILLISECONDS);
				if (null == fromLetter) {
					String errorMessage = new StringBuilder("mailboxID=").append(mailboxID).append(", mailID=")
							.append(mailID).toString();
					throw new SocketTimeoutException(errorMessage);
				}

				wrapReadableMiddleObject = fromLetter.getWrapReadableMiddleObject();

				loop = (wrapReadableMiddleObject.getMailID() != mailID);
				if (loop) {
					log.warn(
							"drop the received message[{}] because it's mail id is different form this mailbox's mail id[{}]",
							fromLetter.toString(), mailID);
				}
			} while (loop);
		} finally {
			nextMailID();
		}

		return wrapReadableMiddleObject;

		// }
	}

	/*
	 * public int hashCode() { return monitor.hashCode(); }
	 */

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
