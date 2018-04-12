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

package kr.pe.sinnori.server.threadpool.outputmessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
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
	private InternalLogger log = InternalLoggerFactory.getInstance(OutputMessageWriter.class);
	
	private String projectName;
	private int index;	
	private DataPacketBufferPoolIF dataPacketBufferPool;
	private ArrayBlockingQueue<ToLetter> outputMessageQueue;	
	
	private final ConcurrentHashMap<SocketChannel, SocketChannel> socketChannelHash = new ConcurrentHashMap<SocketChannel, SocketChannel>();

	
	public OutputMessageWriter(String projectName, int index, 
			ArrayBlockingQueue<ToLetter> outputMessageQueue,
			DataPacketBufferPoolIF dataPacketBufferPool) {
		this.index = index;
		this.projectName = projectName;
		this.dataPacketBufferPool = dataPacketBufferPool;
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
				
				toLetter = outputMessageQueue.take();
				
				
				//log.info("toLetter={}", toLetter.toString());

				SocketChannel toSC = toLetter.getToSC();
				List<WrapBuffer> warpBufferList = toLetter.getWrapBufferList();
				Selector writeEventOnlySelector = null;

				try {
					writeEventOnlySelector = Selector.open();
					
					int indexOfWorkingBuffer = 0;
					int warpBufferListSize = warpBufferList.size();
					
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
					
				} catch (IOException e) {
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
						dataPacketBufferPool.putDataPacketBuffer(wrapBuffer);
					}
					
					if (null != writeEventOnlySelector) {
						try {
							writeEventOnlySelector.close();
						} catch(IOException e) {
						}
					}
				}
			}
		
			
			log.warn("{} OutputMessageWriter[{}] loop exit", projectName, index);
		} catch(InterruptedException e) {
			log.warn("{} OutputMessageWriter[{}] stop", projectName, index);
		} catch (Exception e) {
			String errorMessage = String.format("%s OutputMessageWriter[%d] unknown error", projectName, index);
			log.warn(errorMessage, e);
		}
		
		// log.warn(String.format("%s OutputMessageWriter[%d] thread end", commonProjectInfo.getProjectName(), index));
	}
	
	@Override
	public void addNewSocket(SocketChannel newSC) {
		socketChannelHash.put(newSC, newSC);
	}
	
	public void removeSocket(SocketChannel sc) {
		socketChannelHash.remove(sc);
	}


	@Override
	public int getNumberOfConnection() {
		return socketChannelHash.size();
	}
	
	@Override
	public void putIntoQueue(ToLetter toLetter) throws InterruptedException {
		try {
			outputMessageQueue.put(toLetter);
		} catch(InterruptedException e) {
			log.info("drop the output message[{}] becase of InterruptedException", toLetter.toString());
			for (WrapBuffer wrapBuffer : toLetter.getWrapBufferList()) {
				dataPacketBufferPool.putDataPacketBuffer(wrapBuffer);
			}
		}
		
	}
	
	public void finalize() {
		log.warn("{} OutputMessageWriter[{}] finalize", projectName, index);
	}
}
