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
package kr.pe.codda.client.connection.asyn.mainbox;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;

public final class SyncMessageMailbox {
	private InternalLogger log = InternalLoggerFactory.getInstance(SyncMessageMailbox.class);
	private ArrayBlockingQueue<ReadableMiddleObjectWrapper> outputMessageQueue = new ArrayBlockingQueue<ReadableMiddleObjectWrapper>(1);

	//private final Object monitor = new Object();

	private ConnectionIF conn;
	private int mailboxID;	
	
	private long socketTimeOut;

	private transient int mailID = Integer.MIN_VALUE;
	
	
	public SyncMessageMailbox(ConnectionIF conn, int mailboxID, long socketTimeOut) {
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
		

		this.conn = conn;
		this.mailboxID = mailboxID;
		this.socketTimeOut = socketTimeOut;
	}

	/*
	 * private int getMailboxID() { return mailboxID; }
	 */

	private void nextMailID() {
		//synchronized (monitor) {
			if (Integer.MAX_VALUE == mailID) {
				mailID = Integer.MIN_VALUE;
			} else {
				mailID++;
			}
		//}
	}

	public int getMailboxID() {
		return mailboxID;
	}

	public int getMailID() {
		return mailID;
	}

	public void putSyncOutputMessage(ReadableMiddleObjectWrapper readableMiddleObjectWrapper) throws InterruptedException {
		if (null == readableMiddleObjectWrapper) {
			throw new IllegalArgumentException("the parameter readableMiddleObjectWrapper is null");
		}
		

		int fromMailboxID = readableMiddleObjectWrapper.getMailboxID();
		if (mailboxID != fromMailboxID) {
			log.warn("drop the received letter[{}][{}] because it's mailbox id is different form this mailbox id[{}]",
					readableMiddleObjectWrapper.toString(), mailboxID);
			
			readableMiddleObjectWrapper.closeReadableMiddleObject();
			return;
		}

		int fromMailID = readableMiddleObjectWrapper.getMailID();		

		//synchronized (monitor) {
			if (mailID != fromMailID) {
				log.warn("drop the received letter[{}] because it's mail id is different form this mailbox's mail id[{}]",
						readableMiddleObjectWrapper.toString(), mailID);
				
				readableMiddleObjectWrapper.closeReadableMiddleObject();
				return;
			}
		//}		

		// Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		if (! outputMessageQueue.isEmpty()) {
			ReadableMiddleObjectWrapper oldWrapReadableMiddleObject  = outputMessageQueue.poll();
			if (null != oldWrapReadableMiddleObject) {
				log.warn(
						"clear the old received message[{}] from the ouputmessage queue of this mailbox[mailID={}] becase new message recevied",
						readableMiddleObjectWrapper.toString(), mailID);
				
				oldWrapReadableMiddleObject.closeReadableMiddleObject();
			}
		}
		
		boolean result = outputMessageQueue.offer(readableMiddleObjectWrapper);		
		if (!result) {
			log.warn("drop the received letter[{}] because it was failed to insert the received letter into the output message queue of this mailbox"
		  , readableMiddleObjectWrapper.toString()); 
			
			readableMiddleObjectWrapper.closeReadableMiddleObject();
		}
		 
	}

	public ReadableMiddleObjectWrapper getSyncOutputMessage() throws IOException, InterruptedException {
		// synchronized (monitor) {

		ReadableMiddleObjectWrapper readableMiddleObjectWrapper = null;
		boolean loop = false;

		long currentWorkingSocketTimeOut = socketTimeOut;
		long startTime = System.currentTimeMillis();
		try {
			 do {
				 readableMiddleObjectWrapper = outputMessageQueue.poll(currentWorkingSocketTimeOut, TimeUnit.MILLISECONDS);
				
				if (null == readableMiddleObjectWrapper) {
					if (! conn.isConnected()) {						
						log.warn("this connection[{}] disconnected so the input message's mail[mailboxID={}, mailID={}] lost", 
								conn.hashCode(), mailboxID, mailID);
						throw new IOException("the connection has been disconnected");
					}
					
					log.warn("this connection[{}] timeout occurred so the request mail[mailboxID={}, mailID={}] lost", 
							conn.hashCode(), mailboxID, mailID);
					throw new SocketTimeoutException("socket timeout occurred");
				}				
				
				loop = (readableMiddleObjectWrapper.getMailID() != mailID);
				if (loop) {
					log.warn(
							"drop the received message[{}] because it's mail id is different form this mailbox's mail id[{}]",
							readableMiddleObjectWrapper.toString(), mailID);
					readableMiddleObjectWrapper.closeReadableMiddleObject();

					currentWorkingSocketTimeOut = socketTimeOut - (startTime - System.currentTimeMillis());
					if (currentWorkingSocketTimeOut <= 0) {
						log.warn("this connection[{}] timeout occurred so the request mail[mailboxID={}, mailID={}] lost", 
								conn.hashCode(), mailboxID, mailID);
						throw new SocketTimeoutException("socket timeout occurred");
					}
				}
			} while (loop);
		} finally {
			nextMailID();
		}

		return readableMiddleObjectWrapper;
	}
	
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SyncMailboxForAsynPrivate [connection=");
		builder.append(conn.hashCode());		
		builder.append(", mailboxID=");
		builder.append(mailboxID);
		builder.append(", mailID=");
		builder.append(mailID);
		builder.append(", socketTimeOut=");
		builder.append(socketTimeOut);
		builder.append("]");
		return builder.toString();
	}
}
