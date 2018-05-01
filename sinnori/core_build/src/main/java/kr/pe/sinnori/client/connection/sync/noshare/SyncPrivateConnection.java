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
package kr.pe.sinnori.client.connection.sync.noshare;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayDeque;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.ConnectionFixedParameter;
import kr.pe.sinnori.client.connection.SocketResoruceIF;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;


public class SyncPrivateConnection extends AbstractConnection {
	private SocketResoruceIF syncPrivateSocketResoruce = null;
	
	private SocketOutputStream socketOutputStream = null;
	
	
	private static final int MAILBOX_ID = 1;
	
	/** 메일 식별자 */
	private int mailID = 0;	
	
	/** 큐 등록 상태 */
	private boolean isQueueIn = true;	
	
	public SyncPrivateConnection(ConnectionFixedParameter connectionFixedParameter,
			SyncPrivateSocketResource syncPrivateSocketResource) throws InterruptedException, NoMoreDataPacketBufferException, IOException {
		super(connectionFixedParameter);
		
		if (null == syncPrivateSocketResource) {
			String errorMessage = "the parameter syncPrivateSocketResource is null"; 
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		this.syncPrivateSocketResoruce = syncPrivateSocketResource;
		socketOutputStream = syncPrivateSocketResoruce.getSocketOutputStream();
		//log.info(String.format("project[%s] NoShareSyncConnection[%d] 생성자 end", projectName, serverSC.hashCode()));
	}
	
	
	
	
	public boolean isInQueue() {
		// return lastCaller.equals("");
		return isQueueIn;
	}

	/**
	 * 큐 속에 들어갈때 상태 변경 메소드
	 */
	protected void queueIn() {
		isQueueIn = true;
		// log.info("put NoShareSyncConnection[{}] in the connection queue", monitor.hashCode());
	}

	/**
	 * 큐 밖으로 나갈때 상태 변경 메소드
	 */
	protected void queueOut() {
		isQueueIn = false;
		// log.info("get NoShareSyncConnection[{}] from the connection queue", monitor.hashCode());
	}
	
	public AbstractMessage sendSyncInputMessage(AbstractMessage inObj)
			throws InterruptedException, NoMoreDataPacketBufferException,  
			DynamicClassCallException, ServerTaskException, AccessDeniedException, BodyFormatException, IOException {
		
		
		// String messageID = inObj.getMessageID();
		// LetterFromServer letterFromServer = null;
		

		ClassLoader classLoader = inObj.getClass().getClassLoader();
		inObj.messageHeaderInfo.mailboxID = MAILBOX_ID;
		inObj.messageHeaderInfo.mailID = this.mailID;
		
		
		AbstractMessage outObj = null;
		
		Selector ioEventOnlySelector = Selector.open();
		
		try {
			serverSC.register(ioEventOnlySelector, SelectionKey.OP_WRITE);
			
			ArrayDeque<WrapBuffer> warpBufferQueue = null;
			WrapBuffer workingWrapBuffer = null;
			try {
				warpBufferQueue = clientMessageUtility.buildReadableWrapBufferList(classLoader, inObj);
				
				while (! warpBufferQueue.isEmpty()) {
					workingWrapBuffer = warpBufferQueue.removeFirst();
					ByteBuffer workingByteBuffer = workingWrapBuffer.getByteBuffer();
					
					boolean loop = true;
					do {
						int numberOfKeys =  ioEventOnlySelector.select(socketTimeOut);
						if (0 == numberOfKeys) {
							String errorMessage = new StringBuilder("this sync private connection[")
									.append(serverSC.hashCode())
									.append("] timeout so closed").toString();
							
							log.info(errorMessage);
							
							close();
							
							throw new SocketTimeoutException(errorMessage);
						}
						
						ioEventOnlySelector.selectedKeys().clear();
						
						serverSC.write(workingByteBuffer);
						
						if (! workingByteBuffer.hasRemaining()) {
							clientMessageUtility.releaseWrapBuffer(workingWrapBuffer);
							
							if (warpBufferQueue.isEmpty()) {
								workingWrapBuffer = null;
								break;
							}
							
							workingWrapBuffer = warpBufferQueue.removeFirst();
							workingByteBuffer = workingWrapBuffer.getByteBuffer();
						}
					} while (loop);
				}
				
			} catch (Exception e) {
				String errorMessage = new StringBuilder("fail to write a input message in this sync private connection[")
						.append(serverSC.hashCode())
						.append("], errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);
				log.warn("this input message[sc={}][{}] has dropped becase of write failure", 
						serverSC.hashCode(), inObj.toString());
				
				throw new IOException(errorMessage);
			} finally {
				if (null != workingWrapBuffer) {
					clientMessageUtility.releaseWrapBuffer(workingWrapBuffer);
				}
				while (! warpBufferQueue.isEmpty()) {
					workingWrapBuffer = warpBufferQueue.removeFirst();
					clientMessageUtility.releaseWrapBuffer(workingWrapBuffer);
				}
			}
			
			if (Integer.MAX_VALUE == mailID) {
				mailID = Integer.MIN_VALUE;
			} else {
				mailID++;
			}
			
			
			serverSC.keyFor(ioEventOnlySelector).interestOps(SelectionKey.OP_READ);
			
			do {
				int numberOfKeys =  ioEventOnlySelector.select(socketTimeOut);
				if (0 == numberOfKeys) {
					String errorMessage = new StringBuilder("this sync private connection[")
							.append(serverSC.hashCode())
							.append("] timeout so closed").toString();
					
					log.info(errorMessage);
					
					close();
					
					throw new SocketTimeoutException(errorMessage);
				}
				
				ioEventOnlySelector.selectedKeys().clear();
				
				ArrayDeque<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue = new ArrayDeque<WrapReadableMiddleObject>();
				
				try {
					int numRead = socketOutputStream.read(serverSC);
					
					if (numRead == -1) {
						String errorMessage = new StringBuilder("this socket channel[")
								.append(serverSC.hashCode())
								.append("] has reached end-of-stream").toString();
						throw new IOException(errorMessage);
					}
					
					setFinalReadTime();					
					
					clientMessageUtility
							.S2MList(socketOutputStream, wrapReadableMiddleObjectQueue);
										
					int wrapReadableMiddleObjectListSize = wrapReadableMiddleObjectQueue.size();
					
					if (1 == wrapReadableMiddleObjectListSize) {
						WrapReadableMiddleObject wrapReadableMiddleObject = wrapReadableMiddleObjectQueue.pollFirst();						
						outObj = clientMessageUtility.buildOutputMessage(classLoader, wrapReadableMiddleObject);
						break;						
					} else if (wrapReadableMiddleObjectListSize > 1) {
						String errorMessage = new StringBuilder("this sync private connection[")
								.append(serverSC.hashCode())
								.append("] has recevied one more messages in this sendSyncInputMessage method").toString();
						
						for (WrapReadableMiddleObject wrapReadableMiddleObject : wrapReadableMiddleObjectQueue) {							
							log.warn("drop the output message[{}] becase {}",
									wrapReadableMiddleObject.toString(), errorMessage);
							
							wrapReadableMiddleObject.closeReadableMiddleObject();
						}
						
						throw new SocketException(errorMessage);
					}
					
				} catch (Exception e) {
					String errorMessage = new StringBuilder("fail to read a output message")							
							.append(" in this sync private connection[")
							.append(serverSC.hashCode())
							.append("], errmsg=")
							.append(e.getMessage()).toString();
					log.warn(errorMessage, e);
					log.warn("fail to read a output message for the input message[sc={}][{}]", 
							serverSC.hashCode(), inObj.toString());
					
					if (null != wrapReadableMiddleObjectQueue) {
						for (WrapReadableMiddleObject wrapReadableMiddleObject : wrapReadableMiddleObjectQueue) {							
							log.warn("drop the output message[{}] becase {}",
									wrapReadableMiddleObject.toString(), errorMessage);
							
							wrapReadableMiddleObject.closeReadableMiddleObject();
						}
					}
					
					throw new IOException(errorMessage);
				}
			} while (true);		
			
		} finally {
			try {
				ioEventOnlySelector.close();
			} catch(IOException e) {
			}
		}		
		
		if (outObj instanceof SelfExnRes) {
			SelfExnRes selfExnRes = (SelfExnRes) outObj;
			log.warn(selfExnRes.toString());
			SelfExn.ErrorType.throwSelfExnException(selfExnRes);
		}
		
		return outObj;
	}	
	
	public void sendAsynInputMessage(AbstractMessage inObj) 
			throws NotSupportedException {		
		throw new NotSupportedException("this synchronous connection doesn't support this method 'sendAsynInputMessage'");
	}
	
	@Override
	public void finalize() {
		try {
			close();
		} catch (IOException e) {
			
		}
		
		if (! isQueueIn) {
			log.warn("큐로 복귀 못한 동기 비공유 연결[{}]", hashCode());
		}
	}


	protected void doReleaseSocketResources() {
		syncPrivateSocketResoruce.releaseSocketResources();
	}

	

}
