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
package kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.type.SelfExn.ErrorPlace;
import kr.pe.sinnori.common.type.SelfExn.ErrorType;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;

/**
 * 클라이언트 입력 메시지 소켓 쓰기 담당 쓰레드(=핸들러)
 * 
 * @author Won Jonghoon
 * 
 */
public class InputMessageWriter extends Thread implements InputMessageWriterIF {
	private Logger log = LoggerFactory.getLogger(InputMessageWriter.class);

	// private final Object monitor = new Object();

	private String projectName = null;
	/** 입력 메시지 쓰기 쓰레드 번호 */
	private int index;

	/** 입력 메시지 큐 */
	private ArrayBlockingQueue<ToLetter> inputMessageQueue = null;

	// private MessageProtocolIF messageProtocol = null;
	private ClientMessageUtilityIF clientMessageUtility = null;
	// private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;

	private Hashtable<SocketChannel, AbstractAsynConnection> sc2AsynConnectionHash = new Hashtable<SocketChannel, AbstractAsynConnection>();

	public InputMessageWriter(String projectName, int index, ArrayBlockingQueue<ToLetter> inputMessageQueue,
			ClientMessageUtilityIF clientMessageUtility) throws NoMoreDataPacketBufferException {
		this.projectName = projectName;
		this.index = index;
		this.inputMessageQueue = inputMessageQueue;
		this.clientMessageUtility = clientMessageUtility;
	}

	@Override
	public void run() {
		log.info(String.format("InputMessageWriter[%d] start", index));
		
		
		try {
			while (!Thread.currentThread().isInterrupted()) {
				ToLetter toLetter = inputMessageQueue.take();
				
				SocketChannel toSC = toLetter.getToSocketChannel();
				
				List<WrapBuffer> warpBufferList = toLetter.getWrapBufferList();
				int indexOfWorkingBuffer = 0;
				int warpBufferListSize = warpBufferList.size();
				
				Selector writeEventOnlySelector = Selector.open();
				
				try {
					toSC.register(writeEventOnlySelector, SelectionKey.OP_WRITE);
						
					WrapBuffer workingWrapBuffer = null;
					ByteBuffer workingByteBuffer = null;
					boolean loop = true;
					while (loop) {
						@SuppressWarnings("unused")
						int numberOfKeys =  writeEventOnlySelector.select();
						 
						workingWrapBuffer = warpBufferList.get(indexOfWorkingBuffer);
						workingByteBuffer = workingWrapBuffer.getByteBuffer();
							
						if (! workingByteBuffer.hasRemaining()) {
							if ((indexOfWorkingBuffer+1) == warpBufferListSize) {
								loop = false;
								break;
							}
							indexOfWorkingBuffer++;
							workingWrapBuffer = warpBufferList.get(indexOfWorkingBuffer);
							workingByteBuffer = workingWrapBuffer.getByteBuffer();
						}
						toSC.write(workingByteBuffer);
					}
				} catch(Exception e) {
					String errorMessage = String.format("InputMessageWriter[%d] Exception::%s, letterToServer=[%s]",
							index, e.getMessage(), toLetter.toString());
					log.warn(errorMessage, e);
					
					AbstractAsynConnection asynConnection = sc2AsynConnectionHash.get(toSC);
					
					if (toLetter.getMailboxID() != CommonStaticFinalVars.ASYN_MAILBOX_ID) {
						SelfExnRes selfExnRes = new SelfExnRes();
						selfExnRes.messageHeaderInfo.mailboxID = toLetter.getMailboxID();
						selfExnRes.messageHeaderInfo.mailID = toLetter.getMailID();
						selfExnRes.setErrorMessageID(toLetter.getMessageID());
						selfExnRes.setErrorPlace(ErrorPlace.CLIENT);
						selfExnRes.setErrorType(ErrorType.ClientIOException);
						selfExnRes.setErrorReason("IOException::"+e.getMessage());						
						
						WrapReadableMiddleObject wrapReadableMiddleObject 
						= new WrapReadableMiddleObject(toLetter.getMessageID(), 
								toLetter.getMailboxID(), toLetter.getMailID(), selfExnRes);
								
						
						FromLetter fromLetter = new FromLetter(toLetter.getToSocketChannel(), wrapReadableMiddleObject);
						asynConnection.putToOutputMessageQueue(fromLetter);
					} else {
						/** 비동기일때에는 그냥 닫는다 */
						asynConnection.close();
					}
				} finally {
					writeEventOnlySelector.close();
					clientMessageUtility.releaseWrapBufferList(warpBufferList);
				}
			}

			log.warn(String.format("%s InputMessageWriter[%d] loop exit", projectName, index));
		} catch(InterruptedException e) {
			log.warn(String.format("%s InputMessageWriter[%d] stop", projectName, index));
		} catch (Exception e) {
			log.warn(String.format("%s InputMessageWriter[%d] unknown error::%s", projectName, index, e.getMessage()),
					e);
		}
	}

	public void registerAsynConnection(AbstractAsynConnection asynConnection) {
		sc2AsynConnectionHash.put(asynConnection.getSocketChannel(), asynConnection);
	}

	public int getNumberOfAsynConnection() {
		return sc2AsynConnectionHash.size();
	}

	public void removeAsynConnection(AbstractAsynConnection asynConnection) {
		sc2AsynConnectionHash.remove(asynConnection.getSocketChannel());
	}

	public void putIntoQueue(ToLetter toLetter) throws InterruptedException {
		inputMessageQueue.put(toLetter);
	}

	public void finalize() {
		log.warn(String.format("%s InputMessageWriter[%d] 소멸::[%s]", projectName, index, toString()));
	}
}
