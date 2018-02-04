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

package kr.pe.sinnori.server.threadpool.outputmessage.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.WrapBuffer;

/**
 * 서버 출력 메시지 소켓 쓰기 담당 쓰레드
 * 
 * @author Won Jonghoon
 * 
 */
public class OutputMessageWriter extends Thread implements OutputMessageWriterIF {
	private Logger log = LoggerFactory.getLogger(OutputMessageWriter.class);
	
	private String projectName;
	private int index;	
	private DataPacketBufferPoolIF dataPacketBufferQueueManager;
	private LinkedBlockingQueue<ToLetter> outputMessageQueue;	
	
	private final Set<SocketChannel> socketChannelSet = Collections.synchronizedSet(new HashSet<SocketChannel>());

	/**
	 * 생성자
	 * @param index 순번
	 * @param serverProjectConfig 프로젝트의 공통 포함한 서버 환경 변수 접근 인터페이스 
	 * @param outputMessageQueue 출력 메시지 큐
	 * @param messageProtocol 메시지 교환 프로토콜
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 */
	public OutputMessageWriter(String projectName, int index, 
			LinkedBlockingQueue<ToLetter> outputMessageQueue,
			DataPacketBufferPoolIF dataPacketBufferQueueManager) {
		this.index = index;
		this.projectName = projectName;
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		this.outputMessageQueue = outputMessageQueue;
	}

	/**
	 * <b>출력메세지 queue</b>로부터 출력 메세지들을 얻어와서 socket channel 쓰기 작업을 수행한다.
	 */
	@Override
	public void run() {
		log.info(String.format("%s OutputMessageWriter[%d] start", projectName, index));
		
		try {
			
			while (!Thread.currentThread().isInterrupted()) {
				ToLetter toLetter = null;
				try {
					toLetter = outputMessageQueue.take();
				} catch (InterruptedException e) {
					log.warn("project[{}] index[{}] InterruptedException", projectName, index);
					break;
				}

				SocketChannel toSC = toLetter.getToSocketChannel();	
				List<WrapBuffer> outObjWrapBufferList = toLetter.getWrapBufferList();
				
				try {
					// int outObjWrapBufferListSize = outObjWrapBufferList.size();
					
					// long startTime = System.currentTimeMillis();
					synchronized (toSC) {
						/**
						 * 2013.07.24 잔존 데이타 발생하므로 GatheringByteChannel 를 이용하는 바이트 버퍼 배열 쓰기 방식 포기.
						 */
						/*for (int i=0; i < outObjWrapBufferListSize; i++) {
							WrapBuffer wrapBuffer = outObjWrapBufferList.get(i);
							ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();

							do {
								toSC.write(byteBuffer);
							} while(byteBuffer.hasRemaining());
						}*/
						
						for (WrapBuffer wrapBuffer : outObjWrapBufferList) {
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
					//long endTime = System.currentTimeMillis();
					//log.info(String.format("elapsed time=[%s]", endTime - startTime));
				} catch (NotYetConnectedException e) {
					// ClosedChannelException
					log.warn(String.format("%s OutputMessageWriter[%d] toSC[%d] NotYetConnectedException",
							projectName, index, toSC.hashCode()), e);
					try {
						toSC.close();
					} catch (IOException e1) {
					}
				} catch(ClosedByInterruptException e) {
					/** ClosedByInterruptException 는 IOException 상속 받기때문에 따로 처리  */
					log.warn(String.format("%s OutputMessageWriter[%d] toSC[%d] ClosedByInterruptException",
							projectName, index, toSC.hashCode()), e);
					try {
						toSC.close();
					} catch (IOException e1) {
					}
					
					throw e;
				} catch (IOException e) {
					log.warn(String.format("%s OutputMessageWriter[%d] toSC[%d] IOException",
							projectName, index, toSC.hashCode()), e);
					
					try {
						toSC.close();
					} catch (IOException e1) {
						
					}
				} finally {
					if (null != outObjWrapBufferList) {
						/*int bodyWrapBufferListSiz = outObjWrapBufferList.size();
						for (int i=0; i < bodyWrapBufferListSiz; i++) {
							WrapBuffer wrapBuffer = outObjWrapBufferList.get(0);
							outObjWrapBufferList.remove(0);
							dataPacketBufferQueueManager.putDataPacketBuffer(wrapBuffer);
						}*/
						
						for (WrapBuffer wrapBuffer : outObjWrapBufferList) {
							dataPacketBufferQueueManager.putDataPacketBuffer(wrapBuffer);
						}
					}
				}
			}
		
			log.warn(String.format("%s OutputMessageWriter[%d] loop exit", projectName, index));		
		} catch(ClosedByInterruptException e) {
			/** 이미 로그를 찍은 상태로 nothing */
		} catch (Exception e) {
			log.warn(String.format("%s OutputMessageWriter[%d] unknown error", projectName, index), e);
		}
		
		// log.warn(String.format("%s OutputMessageWriter[%d] thread end", commonProjectInfo.getProjectName(), index));
	}
	
	@Override
	public void addNewSocket(SocketChannel newSC) {
		socketChannelSet.add(newSC);
	}
	
	public void removeSocket(SocketChannel sc) {
		socketChannelSet.remove(sc);
	}


	@Override
	public int getNumberOfSocket() {
		return socketChannelSet.size();
	}
	
	@Override
	public void putIntoQueue(ToLetter toLetter) throws InterruptedException {
		outputMessageQueue.put(toLetter);
	}
}
