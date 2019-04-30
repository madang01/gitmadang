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

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.client.connection.ClientMessageUtility;
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;

public final class SyncMessageMailbox {
	private InternalLogger log = InternalLoggerFactory.getInstance(SyncMessageMailbox.class);
	private final Object monitor = new Object();
	
	private ConnectionIF conn;	

	private long socketTimeOut;

	private int mailboxID;
	private transient int mailID = Integer.MIN_VALUE;
	private MessageProtocolIF messageProtocol = null;
	
	
	private String receviedMessageID = null;
	private Object receviedReadableMiddleObject = null;

	private MessageCodecMangerIF messageCodecManger = null;

	public SyncMessageMailbox(ConnectionIF conn, int mailboxID, long socketTimeOut, MessageProtocolIF messageProtocol) {
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
		this.messageProtocol = messageProtocol;
	}

	public void setMessageCodecManger(MessageCodecMangerIF messageCodecManger) {
		this.messageCodecManger = messageCodecManger;
	}
	
	/*
	 * private int getMailboxID() { return mailboxID; }
	 */

	

	public int getMailboxID() {
		return mailboxID;
	}

	public int getMailID() {
		return mailID;
	}
	

	public void putSyncOutputMessage(int fromMailboxID, int fromMailID, String messageID, Object readableMiddleObject)
			throws InterruptedException {
		if (null == readableMiddleObject) {
			throw new IllegalArgumentException("the parameter readableMiddleObject is null");
		}
		
		synchronized (monitor) {
			if (mailboxID != fromMailboxID) {
				AbstractMessage outputMessage = ClientMessageUtility.buildOutputMessage("discarded", messageCodecManger, messageProtocol, fromMailboxID, fromMailID, messageID, readableMiddleObject);
				
				log.warn("drop the received letter[{}][{}] because it's mailbox id is different form this mailbox id[{}]",
						outputMessage.toString(), mailboxID);				
				return;
			}
			
			if (mailID != fromMailID) {
				AbstractMessage outputMessage = ClientMessageUtility.buildOutputMessage("discarded", messageCodecManger, messageProtocol, fromMailboxID, fromMailID, messageID, readableMiddleObject);
				
				log.warn("drop the received letter[{}] because it's mail id is different form this mailbox's mail id[{}]",
						outputMessage.toString(), mailID);

				// readableMiddleObjectWrapper.closeReadableMiddleObject();
				return;
			}			
			
			// outputMessageReadableMiddleObjectWrapper = readableMiddleObjectWrapper;
			this.receviedMessageID = messageID;
			this.receviedReadableMiddleObject = readableMiddleObject;
			monitor.notify();
		}
	}

	public AbstractMessage getSyncOutputMessage() throws IOException, InterruptedException {
		AbstractMessage returnedObject = null;
		synchronized (monitor) {
			if (null == receviedReadableMiddleObject) {
				monitor.wait(socketTimeOut);				
				if (null == receviedReadableMiddleObject) {
					
					if (! conn.isConnected()) {
						log.warn(
								"this connection[{}] disconnected so the input message's mail[mailboxID={}, mailID={}] lost",
								conn.hashCode(), mailboxID, mailID);
						log.info("연결 끊어져서 mailID 증가, mailID={}", mailID);
						
						if (Integer.MAX_VALUE == mailID) {
							mailID = Integer.MIN_VALUE;
						} else {
							mailID++;
						}
						throw new IOException("the connection has been disconnected");
					}
					
					log.warn("this connection[{}] timeout occurred so the request mail[mailboxID={}, mailID={}] lost",
							conn.hashCode(), mailboxID, mailID);
					
					log.info("소켓 타임아웃으로 인한 mailID 증가, mailID={}", mailID);
					if (Integer.MAX_VALUE == mailID) {
						mailID = Integer.MIN_VALUE;
					} else {
						mailID++;
					}					
					throw new SocketTimeoutException("socket timeout occurred");
				}				
			}
			returnedObject = ClientMessageUtility.buildOutputMessage("received", messageCodecManger, messageProtocol, mailboxID, mailID, receviedMessageID, receviedReadableMiddleObject);
			
			if (Integer.MAX_VALUE == mailID) {
				mailID = Integer.MIN_VALUE;
			} else {
				mailID++;
			}
			//receviedMessageID = null;
			receviedReadableMiddleObject = null;
		}
		
		return returnedObject;
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
