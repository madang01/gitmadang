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

package kr.pe.codda.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.server.threadpool.executor.ServerExecutorIF;

/**
 * 서버에 접속하는 클라이언트 자원 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public class SocketResource implements InterestedResoruceIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(SocketResource.class);

	private SocketChannel ownerSC = null;
	private long socketTimeOut=5000;
	private int outputMessageQueueSize=5;
	private MessageProtocolIF messageProtocol = null;
	private ServerExecutorIF serverExecutor = null;
	private SocketOutputStream socketOutputStreamOfOwnerSC = null;
	private PersonalLoginManagerIF personalLoginManagerOfOwnerSC = null;	
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ServerIOEvenetControllerIF serverIOEvenetController = null;
	
	private ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = new ArrayDeque<ArrayDeque<WrapBuffer>>();
	
	
	/** 최종 읽기를 수행한 시간. 초기값은 클라이언트(=SocketChannel) 생성시간이다 */
	private java.util.Date finalReadTime = null;
	
	private final Object monitorOfServerMailID = new Object();
	/** 클라이언트에 할당되는 서버 편지 식별자 */
	private int serverMailID = Integer.MIN_VALUE;
		
	public SocketResource(SocketChannel ownerSC,
			long socketTimeOut,
			int outputMessageQueueSize,
			MessageProtocolIF messageProtocol,
			ServerExecutorIF serverExecutor,
			SocketOutputStream socketOutputStreamOfOwnerSC,
			PersonalLoginManagerIF personalLoginManagerOfOwnerSC,
			DataPacketBufferPoolIF dataPacketBufferPool,
			ServerIOEvenetControllerIF serverIOEvenetController) {
		if (null == ownerSC) {
			throw new IllegalArgumentException("the parameter ownerSC is null");
		}
		
		if (socketTimeOut < 0) {
			throw new IllegalArgumentException("the parameter socketTimeOut is less than zero");
		}
		
		if (outputMessageQueueSize <= 0) {
			throw new IllegalArgumentException("the parameter outputMessageQueueSize is less than or equal to zero");
		}
		
		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}

		if (null == serverExecutor) {
			throw new IllegalArgumentException("the parameter serverExecutor is null");
		}
		
		
		if (null == socketOutputStreamOfOwnerSC) {
			throw new IllegalArgumentException("the parameter socketOutputStreamOfOwnerSC is null");
		}
		
		if (null == personalLoginManagerOfOwnerSC) {
			throw new IllegalArgumentException("the parameter personalLoginManagerOfOwnerSC is null");
		}
		
		if (null == dataPacketBufferPool) {
			throw new IllegalArgumentException("the parameter dataPacketBufferPool is null");
		}
		
		if (null == serverIOEvenetController) {
			throw new IllegalArgumentException("the parameter serverIOEvenetController is null");
		}
		
		this.ownerSC = ownerSC;
		this.socketTimeOut = socketTimeOut;
		this.outputMessageQueueSize = outputMessageQueueSize;
		this.messageProtocol = messageProtocol;
		this.serverExecutor = serverExecutor;
		this.socketOutputStreamOfOwnerSC = socketOutputStreamOfOwnerSC;
		this.personalLoginManagerOfOwnerSC = personalLoginManagerOfOwnerSC;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.serverIOEvenetController = serverIOEvenetController;
		
		finalReadTime = new java.util.Date();
	}
	
	public ServerExecutorIF getServerExecutor() {
		return serverExecutor;
	}
	
	public SocketChannel getOwnerSC() {
		return ownerSC;
	}
	
	
	/**
	 * 마지막으로 읽은 시간을 반환한다.
	 * 
	 * @return 마지막으로 읽은 시간
	 */
	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}

	/**
	 * 마지막으로 읽은 시간을 새롭게 설정한다.
	 */
	private void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}	

	/**
	 * 메일 식별자를 반환한다. 메일 식별자는 자동 증가된다.
	 */
	public int getServerMailID() {
		synchronized (monitorOfServerMailID) {
			if (Integer.MAX_VALUE == serverMailID) {
				serverMailID = Integer.MIN_VALUE;
			} else {
				serverMailID++;
			}
			return serverMailID;
		}
	}
	
	
	
	
	public PersonalLoginManagerIF getPersonalLoginManager() {
		return personalLoginManagerOfOwnerSC;
	}
	
	public void close() throws IOException {
		ownerSC.close();
	}
	
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientResource [");		
		builder.append("ownerSC=");
		builder.append(ownerSC.hashCode());
		builder.append(", finalReadTime=");
		builder.append(finalReadTime);		
		builder.append(", serverMailID=");
		builder.append(serverMailID);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void onRead(SelectionKey selectedKey) throws InterruptedException {
		try {
			int numberOfReadBytes = 0;			
			do {
				numberOfReadBytes = socketOutputStreamOfOwnerSC.read(ownerSC);
			} while (numberOfReadBytes > 0);

			if (numberOfReadBytes == -1) {
				String errorMessage = new StringBuilder("this socket channel[")
						.append(ownerSC.hashCode())
						.append("] has reached end-of-stream").toString();

				log.warn(errorMessage);
				close();
				releaseResources();
				selectedKey.channel();
				return;
			}

			setFinalReadTime();
			
			messageProtocol.S2MList(ownerSC, socketOutputStreamOfOwnerSC, serverExecutor.getWrapMessageBlockingQueue());		
				
		} catch (NoMoreDataPacketBufferException e) {
			String errorMessage = new StringBuilder()
					.append("the no more data packet buffer error occurred while reading the socket[")
					.append(ownerSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			try {
				close();
			} catch (IOException e1) {
				log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
						hashCode(), e1.getMessage());
			}
			releaseResources();
			
			selectedKey.channel();
			return;
		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("the io error occurred while reading the socket[")
					.append(ownerSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			try {
				close();
			} catch (IOException e1) {
				log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
						hashCode(), e1.getMessage());
			}
			releaseResources();
			selectedKey.channel();
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("the unknown error occurred while reading the socket[")
					.append(ownerSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			try {
				close();
			} catch (IOException e1) {
				log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
						hashCode(), e1.getMessage());
			}
			releaseResources();
			selectedKey.channel();
			return;
		}
		
	}

	@Override
	public void onWrite(SelectionKey selectedKey) throws InterruptedException {
		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = outputMessageQueue.peek();
		WrapBuffer currentWorkingWrapBuffer = inputMessageWrapBufferQueue.peek();
		ByteBuffer currentWorkingByteBuffer = currentWorkingWrapBuffer.getByteBuffer();
		boolean loop = true;
		while (loop) {
			int numberOfBytesWritten  = 0;
			try {
				numberOfBytesWritten = ownerSC.write(currentWorkingByteBuffer);
			} catch(IOException e) {
				String errorMessage = new StringBuilder()
						.append("fail to write a sequence of bytes to this channel[")
						.append(ownerSC.hashCode())
						.append("] because error occured, errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);
				try {
					close();
				} catch (IOException e1) {
					log.warn("fail to close the socket channel[{}], errmsg={}", 
							hashCode(), e1.getMessage());
				}
				
				releaseResources();
				selectedKey.channel();
				return;
			}	
			
			
			if (0 == numberOfBytesWritten) {
				loop = false;
				return;
			}
			
			if (! currentWorkingByteBuffer.hasRemaining()) {
				inputMessageWrapBufferQueue.removeFirst();
				dataPacketBufferPool.putDataPacketBuffer(currentWorkingWrapBuffer);
				
				if (inputMessageWrapBufferQueue.isEmpty()) {					
					synchronized (outputMessageQueue) {
						outputMessageQueue.removeFirst();
						try {
							if (outputMessageQueue.isEmpty()) {
								serverIOEvenetController.endWrite(this);
								loop = false;
								return;
							}
						} finally {
							outputMessageQueue.notify();
						}
					}
					
					
					inputMessageWrapBufferQueue = outputMessageQueue.peek();
				}
				
				currentWorkingWrapBuffer = inputMessageWrapBufferQueue.peek();
				currentWorkingByteBuffer = currentWorkingWrapBuffer.getByteBuffer();
			}
		}
	}
	
	 

	@Override
	public void releaseResources() {
		socketOutputStreamOfOwnerSC.close();
		personalLoginManagerOfOwnerSC.releaseLoginUserResource();

		/**
		 * 참고 : '메시지 입력 담당 쓰레드'(=InputMessageReader) 는 소켓이 닫히면 자동적으로 selector 에서 인지하여 제거되므로 따로 작업할 필요 없음
		 */
		serverExecutor.removeSocket(ownerSC);
	}

	@Override
	public SelectionKey keyFor(Selector ioEventSelector) {
		return ownerSC.keyFor(ioEventSelector);
	}
	
	public void addOutputMessage(AbstractMessage outputMessage, ArrayDeque<WrapBuffer> outputMessageWrapBufferQueue) throws InterruptedException {
		synchronized (outputMessageQueue) {
			if (outputMessageQueue.size() == outputMessageQueueSize) {
				outputMessageQueue.wait(socketTimeOut);
				
				if (outputMessageQueue.size() == outputMessageQueueSize) {						
					log.warn("drop the outgoing letter[sc={}][{}] because the output message could not be inserted into the output message queue during the socket timeout period.", 
							ownerSC.hashCode(), outputMessage.toString());
					return;
				}
			}
			
			outputMessageQueue.addLast(outputMessageWrapBufferQueue);			
			serverIOEvenetController.startWrite(this);
		}
	}
	
	/** only Junit test */
	public ArrayDeque<ArrayDeque<WrapBuffer>> getOutputMessageQueue() {
		return outputMessageQueue;
	}
}
