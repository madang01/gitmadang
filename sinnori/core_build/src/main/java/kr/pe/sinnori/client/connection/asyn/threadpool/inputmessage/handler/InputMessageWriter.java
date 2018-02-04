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
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.WrapBuffer;

/**
 * 클라이언트 입력 메시지 소켓 쓰기 담당 쓰레드(=핸들러)
 * 
 * @author Won Jonghoon
 * 
 */
public class InputMessageWriter extends Thread {
	private Logger log = LoggerFactory.getLogger(InputMessageWriter.class);
	
	private String projectName = null;
	/** 입력 메시지 쓰기 쓰레드 번호 */
	private int index;
	
	/** 입력 메시지 큐 */
	private LinkedBlockingQueue<ToLetter> inputMessageQueue = null;
	
	// private MessageProtocolIF messageProtocol = null;
	private DataPacketBufferPoolIF dataPacketBufferQueueManager = null;
	
	

	/**
	 * 생성자
	 * @param index 순번
	 * @param projectPart 프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param messageProtocol 메시지 교환 프로프로콜
	 * @param messageManger 메시지 관리자
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @throws NoMoreDataPacketBufferException 
	 */
	public InputMessageWriter(String projectName, int index,
			LinkedBlockingQueue<ToLetter> inputMessageQueue,
			DataPacketBufferPoolIF dataPacketBufferQueueManager) throws NoMoreDataPacketBufferException {
		this.projectName = projectName;
		this.index = index;		
		this.inputMessageQueue = inputMessageQueue;
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		
	}

	@Override
	public void run() {
		log.info(String.format("InputMessageWriter[%d] start", index));

		// ByteBuffer inputMessageWriteBuffer = inputMessageWrapBuffer.getByteBuffer();
		try {
			while (!Thread.currentThread().isInterrupted()) {
				ToLetter toLetter = null;
				try {
					toLetter = inputMessageQueue.take();
				} catch (InterruptedException e) {
					log.warn(String.format("%s index[%d] stop", projectName, index), e);
					break;
				}
				
				SocketChannel toSC = toLetter.getToSocketChannel();
				
				List<WrapBuffer> inObjWrapBufferList = null;
				
				try {
					inObjWrapBufferList = toLetter.getWrapBufferList();
					
					synchronized (toSC) {
						/**
						 * 2013.07.24 잔존 데이타 발생하므로 GatheringByteChannel 를 이용하는 바이트 버퍼 배열 쓰기 방식 포기.
						 */			
						/*for (int i=0; i < inObjWrapBufferListSize; i++) {
							WrapBuffer wrapBuffer = inObjWrapBufferList.get(i);
							ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
							
							do {
								serverSC.write(byteBuffer);
							} while(byteBuffer.hasRemaining());
						}*/
						// try {
						for (WrapBuffer wrapBuffer : inObjWrapBufferList) {
							ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
							do {
								int numberOfBytesWritten = toSC.write(byteBuffer);
								if (0 == numberOfBytesWritten) {
									try {
										Thread.sleep(10);
									} catch (InterruptedException e) {
										log.warn("when the number of bytes written is zero,  this thread must sleep. but it fails.", e);
									}
								}
							} while(byteBuffer.hasRemaining());
						}
					}
				} catch (NotYetConnectedException e) {
					log.warn(String.format("InputMessageWriter[%d] NotYetConnectedException::%s, letterToServer=[%s]", index, e.getMessage(), toLetter.toString()), e);
					
					// asynConnection.serverClose();
					
				} catch(ClosedChannelException e) {
					log.warn(String.format("InputMessageWriter[%d] ClosedChannelException::%s, letterToServer=[%s]", index, e.getMessage(), toLetter.toString()), e);
				} catch (IOException e) {
					log.warn(String.format("InputMessageWriter[%d] IOException::%s, letterToServer=[%s]",index, e.getMessage(), toLetter.toString()), e);
					
					// asynConnection.serverClose();
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
			
			log.warn(String.format("%s InputMessageWriter[%d] loop exit", projectName, index));
		} catch (Exception e) {
			log.warn(String.format("%s InputMessageWriter[%d] unknown error::%s", projectName, index, e.getMessage()), e);
		}

		log.warn(String.format("%s InputMessageWriter[%d] thread end", projectName, index));
	}

	public void finalize() {
		log.warn(String.format("%s InputMessageWriter[%d] 소멸::[%s]", projectName, index, toString()));
	}
	
	
}
