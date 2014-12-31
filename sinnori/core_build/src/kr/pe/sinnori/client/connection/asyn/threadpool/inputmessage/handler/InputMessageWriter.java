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

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.client.io.LetterToServer;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;

/**
 * 클라이언트 입력 메시지 소켓 쓰기 담당 쓰레드(=핸들러)
 * 
 * @author Won Jonghoon
 * 
 */
public class InputMessageWriter extends Thread implements CommonRootIF {
	/** 입력 메시지 쓰기 쓰레드 번호 */
	private int index;
	private ClientProjectConfig clientProjectConfig = null;
	/** 입력 메시지 큐 */
	private LinkedBlockingQueue<LetterToServer> inputMessageQueue = null;
	
	// private MessageProtocolIF messageProtocol = null;
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = null;
	
	

	/**
	 * 생성자
	 * @param index 순번
	 * @param clientProjectConfig 프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param messageProtocol 메시지 교환 프로프로콜
	 * @param messageManger 메시지 관리자
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @throws NoMoreDataPacketBufferException 
	 */
	public InputMessageWriter(int index,
			ClientProjectConfig clientProjectConfig,
			LinkedBlockingQueue<LetterToServer> inputMessageQueue,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) throws NoMoreDataPacketBufferException {
		this.index = index;
		this.clientProjectConfig = clientProjectConfig;
		this.inputMessageQueue = inputMessageQueue;
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		
	}

	@Override
	public void run() {
		log.info(String.format("InputMessageWriter[%d] start", index));

		// ByteBuffer inputMessageWriteBuffer = inputMessageWrapBuffer.getByteBuffer();
		try {
			while (!Thread.currentThread().isInterrupted()) {
				LetterToServer letterToServer = null;
				try {
					letterToServer = inputMessageQueue.take();
				} catch (InterruptedException e) {
					log.warn(String.format("%s index[%d] stop", clientProjectConfig.getProjectName(), index), e);
					break;
				}
	
				// log.info("1. In InputMessageWriter, letter=[%s]",
				// letterToServer.toString());
	
				AbstractAsynConnection asynConnection = letterToServer
						.getServerConnection();
				// AbstractMessage inObj = letterToServer.getInputMessage();
	
				// SocketChannel toSC = noneBlockConnection.getSocketChannel();
				
				// Charset clientCharset = clientProjectConfig.getCharset();
	
				
				
				// inputMessageWriteBuffer.clear();
				// inputMessageWriteBuffer.order(connByteOrder);
				
				ArrayList<WrapBuffer> inObjWrapBufferList = null;
				
				try {
					inObjWrapBufferList = letterToServer.getWrapBufferList();
					
					asynConnection.write(inObjWrapBufferList);
				} catch (NotYetConnectedException e) {
					log.warn(String.format("%s InputMessageWriter[%d] NotYetConnectedException::%s, letterToServer=[%s]", asynConnection.getSimpleConnectionInfo(), index, e.getMessage(), letterToServer.toString()), e);
					
					asynConnection.serverClose();
					
				} catch(ClosedByInterruptException e) {
					log.warn(String.format("%s InputMessageWriter[%d] ClosedByInterruptException::%s, letterToServer=[%s]", asynConnection.getSimpleConnectionInfo(), index, e.getMessage(), letterToServer.toString()), e);
					
					asynConnection.serverClose();
				} catch (IOException e) {
					log.warn(String.format("%s InputMessageWriter[%d] IOException::%s, letterToServer=[%s]", asynConnection.getSimpleConnectionInfo(), index, e.getMessage(), letterToServer.toString()), e);
					
					asynConnection.serverClose();
				} finally {
					if (null != inObjWrapBufferList) {
						/*int bodyWrapBufferListSiz = inObjWrapBufferList.size();
						for (int i=0; i < bodyWrapBufferListSiz; i++) {
							WrapBuffer wrapBuffer = inObjWrapBufferList.get(0);
							inObjWrapBufferList.remove(0);
							dataPacketBufferQueueManager.putDataPacketBuffer(wrapBuffer);
						}*/
						
						for (WrapBuffer wrapBuffer : inObjWrapBufferList) {
							dataPacketBufferQueueManager.putDataPacketBuffer(wrapBuffer);
						}
					}
				}			
			}
			
			log.warn(String.format("%s InputMessageWriter[%d] loop exit", clientProjectConfig.getProjectName(), index));
		} catch (Exception e) {
			log.warn(String.format("%s InputMessageWriter[%d] unknown error::%s", clientProjectConfig.getProjectName(), index, e.getMessage()), e);
		}

		log.warn(String.format("%s InputMessageWriter[%d] thread end", clientProjectConfig.getProjectName(), index));
	}

	public void finalize() {
		log.warn(String.format("%s InputMessageWriter[%d] 소멸::[%s]", clientProjectConfig.getProjectName(), index, toString()));
	}
}