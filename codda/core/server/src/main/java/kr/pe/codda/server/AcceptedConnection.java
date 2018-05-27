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
public class AcceptedConnection implements ServerInterestedConnectionIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AcceptedConnection.class);
	
	private SelectionKey selectedKey = null;
	private SocketChannel acceptedSocketChannel = null;
	private long socketTimeOut=5000;
	private int outputMessageQueueSize=5;
	private MessageProtocolIF messageProtocol = null;
	private ServerExecutorIF serverExecutor = null;
	private SocketOutputStream socketOutputStream = null;
	private PersonalLoginManager personalLoginManager = null;	
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ServerIOEvenetControllerIF serverIOEvenetController = null;
	
	private ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = new ArrayDeque<ArrayDeque<WrapBuffer>>();
	
	
	/** 최종 읽기를 수행한 시간. 초기값은 클라이언트(=SocketChannel) 생성시간이다 */
	private java.util.Date finalReadTime = null;
	
	private final Object monitorOfServerMailID = new Object();
	/** 클라이언트에 할당되는 서버 편지 식별자 */
	private int serverMailID = Integer.MIN_VALUE;
			
	public AcceptedConnection(SelectionKey selectedKey,
			SocketChannel acceptedSocketChannel,
			long socketTimeOut,
			int outputMessageQueueSize,
			SocketOutputStream socketOutputStreamOfAcceptedSC,
			PersonalLoginManager personalLoginManagerOfAcceptedSC,
			ServerExecutorIF serverExecutorOfAcceptedSC,
			MessageProtocolIF messageProtocol,						
			DataPacketBufferPoolIF dataPacketBufferPool,
			ServerIOEvenetControllerIF serverIOEvenetController) {
				
		if (null == acceptedSocketChannel) {
			throw new IllegalArgumentException("the parameter acceptedSocketChannel is null");
		}
		
		if (socketTimeOut < 0) {
			throw new IllegalArgumentException("the parameter socketTimeOut is less than zero");
		}
		
		if (outputMessageQueueSize <= 0) {
			throw new IllegalArgumentException("the parameter outputMessageQueueSize is less than or equal to zero");
		}
		
		if (null == socketOutputStreamOfAcceptedSC) {
			throw new IllegalArgumentException("the parameter socketOutputStreamOfAcceptedSC is null");
		}
		
		if (null == personalLoginManagerOfAcceptedSC) {
			throw new IllegalArgumentException("the parameter personalLoginManagerOfAcceptedSC is null");
		}		
		
		if (null == serverExecutorOfAcceptedSC) {
			throw new IllegalArgumentException("the parameter serverExecutorOfAcceptedSC is null");
		}	
		
		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}		
		
		if (null == dataPacketBufferPool) {
			throw new IllegalArgumentException("the parameter dataPacketBufferPool is null");
		}
		
		if (null == serverIOEvenetController) {
			throw new IllegalArgumentException("the parameter serverIOEvenetController is null");
		}
		
		this.selectedKey = selectedKey;
		this.acceptedSocketChannel = acceptedSocketChannel;
		this.socketTimeOut = socketTimeOut;
		this.outputMessageQueueSize = outputMessageQueueSize;
		this.socketOutputStream = socketOutputStreamOfAcceptedSC;
		this.personalLoginManager = personalLoginManagerOfAcceptedSC;
		this.serverExecutor = serverExecutorOfAcceptedSC;		
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.serverIOEvenetController = serverIOEvenetController;
		
		finalReadTime = new java.util.Date();
	}
	
	public ServerExecutorIF getServerExecutor() {
		return serverExecutor;
	}
	
	public SocketChannel getOwnerSC() {
		return acceptedSocketChannel;
	}
	
	public SelectionKey getSelectionKey() {
		return selectedKey;
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
		return personalLoginManager;
	}
	
	
	public void close() {
		try {
			acceptedSocketChannel.shutdownOutput();
		} catch(Exception e) {
			log.warn("fail to shutdown output of the socket channel[{}], errmsg={}", acceptedSocketChannel.hashCode(), e.getMessage());
		}
		
		try {
			acceptedSocketChannel.close();
		} catch(Exception e) {
			log.warn("fail to close the socket channel[{}], errmsg={}", acceptedSocketChannel.hashCode(), e.getMessage());
		}
	}	

	@Override
	public void onRead(SelectionKey selectedKey) throws InterruptedException {
		try {
			int numberOfReadBytes = 0;			
			do {
				numberOfReadBytes = socketOutputStream.read(acceptedSocketChannel);
			} while (numberOfReadBytes > 0);

			if (numberOfReadBytes == -1) {
				String errorMessage = new StringBuilder("this socket channel[")
						.append(acceptedSocketChannel.hashCode())
						.append("] has reached end-of-stream").toString();

				log.warn(errorMessage);
				close();
				releaseResources();
				return;
			}

			setFinalReadTime();
			
			messageProtocol.S2MList(this, socketOutputStream, serverExecutor);		
				
		} catch (NoMoreDataPacketBufferException e) {
			String errorMessage = new StringBuilder()
					.append("the no more data packet buffer error occurred while reading the socket[")
					.append(acceptedSocketChannel.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			
			close();
			
			releaseResources();
			return;
		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("the io error occurred while reading the socket[")
					.append(acceptedSocketChannel.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			
			close();
			
			releaseResources();
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("the unknown error occurred while reading the socket[")
					.append(acceptedSocketChannel.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			
			close();
			
			releaseResources();
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
				numberOfBytesWritten = acceptedSocketChannel.write(currentWorkingByteBuffer);
			} catch(IOException e) {
				String errorMessage = new StringBuilder()
						.append("fail to write a sequence of bytes to this channel[")
						.append(acceptedSocketChannel.hashCode())
						.append("] because io error occured, errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);
				
				close();				
				releaseResources();
				return;
			} catch(Exception e) {
				String errorMessage = new StringBuilder()
						.append("fail to write a sequence of bytes to this channel[")
						.append(acceptedSocketChannel.hashCode())
						.append("] because unknow error occured, errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);
				
				close();				
				releaseResources();
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
	
	public void releaseResources() {
		socketOutputStream.close();
		personalLoginManager.releaseLoginUserResource();

		/**
		 * 참고 : '메시지 입력 담당 쓰레드'(=InputMessageReader) 는 소켓이 닫히면 자동적으로 selector 에서 인지하여 제거되므로 따로 작업할 필요 없음
		 */
		serverExecutor.removeSocket(acceptedSocketChannel);
		serverIOEvenetController.cancel(selectedKey);
	}

	@Override
	public SelectionKey keyFor(Selector ioEventSelector) {
		return acceptedSocketChannel.keyFor(ioEventSelector);
	}
	
	public void addOutputMessage(AbstractMessage outputMessage, ArrayDeque<WrapBuffer> outputMessageWrapBufferQueue) throws InterruptedException {
		synchronized (outputMessageQueue) {
			if (outputMessageQueue.size() == outputMessageQueueSize) {
				outputMessageQueue.wait(socketTimeOut);
				
				if (outputMessageQueue.size() == outputMessageQueueSize) {						
					log.warn("drop the outgoing letter[sc={}][{}] because the output message could not be inserted into the output message queue during the socket timeout period.", 
							acceptedSocketChannel.hashCode(), outputMessage.toString());
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
	
	public int hashCode() {
		return acceptedSocketChannel.hashCode();	
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AcceptedConnection [");		
		builder.append("acceptedSocketChannel=");
		builder.append(acceptedSocketChannel.hashCode());
		builder.append(", finalReadTime=");
		builder.append(finalReadTime);		
		builder.append(", serverMailID=");
		builder.append(serverMailID);
		builder.append("]");
		return builder.toString();
	}
}
