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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

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
	private ArrayBlockingQueue<ToLetter> outputMessageQueue;	
	
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
			ArrayBlockingQueue<ToLetter> outputMessageQueue,
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
				
				//log.info("toLetter={}", toLetter.toString());

				SocketChannel toSC = toLetter.getToSocketChannel();
				List<WrapBuffer> warpBufferList = toLetter.getWrapBufferList();
				int indexOfWorkingBuffer = 0;
				int warpBufferListSize = warpBufferList.size();
				
				Selector writeEventOnlySelector = Selector.open();
				
				try {
					toSC.register(writeEventOnlySelector, SelectionKey.OP_WRITE);
					
					WrapBuffer workingWrapBuffer = warpBufferList.get(indexOfWorkingBuffer);
					ByteBuffer workingByteBuffer = workingWrapBuffer.getByteBuffer();
					boolean loop = true;
					do {
						@SuppressWarnings("unused")
						int numberOfKeys =  writeEventOnlySelector.select();
						
						writeEventOnlySelector.selectedKeys().clear();
						
						toSC.write(workingByteBuffer);
						
						if (! workingByteBuffer.hasRemaining()) {
							if ((indexOfWorkingBuffer+1) == warpBufferListSize) {
								loop = false;
								break;
							}
							indexOfWorkingBuffer++;
							workingWrapBuffer = warpBufferList.get(indexOfWorkingBuffer);
							workingByteBuffer = workingWrapBuffer.getByteBuffer();
						}
					} while (loop);
					
				} catch (Exception e) {
					String errorMessage = new StringBuilder("fail to write a output message, ")
							.append(projectName)
							.append(" OutputMessageWriter[")
							.append(index)
							.append("] toSC[")
							.append(toSC.hashCode())
							.append("], errmsg=")
							.append(e.getMessage()).toString();
					log.warn(errorMessage, e);
					
					try {
						toSC.close();
					} catch (IOException e1) {
						
					}
				} finally {
					for (WrapBuffer wrapBuffer : warpBufferList) {
						dataPacketBufferQueueManager.putDataPacketBuffer(wrapBuffer);
					}
					try {
						writeEventOnlySelector.close();
					} catch(IOException e) {
					}
				}
			}
		
			log.warn(String.format("%s OutputMessageWriter[%d] loop exit", projectName, index));	
		
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
