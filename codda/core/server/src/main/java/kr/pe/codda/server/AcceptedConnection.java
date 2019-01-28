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
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;
import kr.pe.codda.common.protocol.ReceivedMessageBlockingQueueIF;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.codda.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.codda.server.classloader.ServerDynamicObjectMangerIF;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * 서버에 접속하는 클라이언트 자원 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public class AcceptedConnection implements ServerIOEventHandlerIF, ReceivedMessageBlockingQueueIF, PersonalLoginManagerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AcceptedConnection.class);

	private SelectionKey personalSelectionKey = null;
	private SocketChannel acceptedSocketChannel = null;
	private String projectName = null;
	@SuppressWarnings("unused")
	private long socketTimeout = 5000;
	private int serverOutputMessageQueueCapacity = 5;
	private MessageProtocolIF messageProtocol = null;
	private SocketOutputStream socketOutputStream = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ServerIOEvenetControllerIF serverIOEvenetController = null;
	private ServerDynamicObjectMangerIF serverObjectCacheManager = null;

	private transient ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = null;
	private ProjectLoginManagerIF projectLoginManager = null;
	private String personalLoginID = null;

	/** 최종 읽기를 수행한 시간. 초기값은 클라이언트(=SocketChannel) 생성시간이다 */
	private java.util.Date finalReadTime = null;

	//private final Object monitorOfServerMailID = new Object();
	/** 클라이언트에 할당되는 서버 편지 식별자 */
	private int serverMailID = Integer.MIN_VALUE;
	
	public AcceptedConnection(SelectionKey personalSelectionKey, SocketChannel acceptedSocketChannel, 
			String projectName,	long socketTimeOut, int serverOutputMessageQueueCapacity, 
			SocketOutputStream socketOutputStreamOfAcceptedSC,
			ProjectLoginManagerIF projectLoginManager, MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferPool, ServerIOEvenetControllerIF serverIOEvenetController,
			ServerDynamicObjectMangerIF serverObjectCacheManager) {

		if (null == personalSelectionKey) {
			throw new IllegalArgumentException("the parameter personalSelectionKey is null");
		}
		
		if (null == acceptedSocketChannel) {
			throw new IllegalArgumentException("the parameter acceptedSocketChannel is null");
		}

		if (socketTimeOut < 0) {
			throw new IllegalArgumentException("the parameter socketTimeOut is less than zero");
		}

		if (serverOutputMessageQueueCapacity <= 0) {
			throw new IllegalArgumentException("the parameter serverOutputMessageQueueCapacity is less than or equal to zero");
		}

		if (null == socketOutputStreamOfAcceptedSC) {
			throw new IllegalArgumentException("the parameter socketOutputStreamOfAcceptedSC is null");
		}

		if (null == projectLoginManager) {
			throw new IllegalArgumentException("the parameter projectLoginManager is null");
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

		this.personalSelectionKey = personalSelectionKey;
		this.acceptedSocketChannel = acceptedSocketChannel;
		this.projectName = projectName;
		this.socketTimeout = socketTimeOut;
		this.serverOutputMessageQueueCapacity = serverOutputMessageQueueCapacity;
		this.socketOutputStream = socketOutputStreamOfAcceptedSC;
		this.projectLoginManager = projectLoginManager;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.serverIOEvenetController = serverIOEvenetController;
		this.serverObjectCacheManager = serverObjectCacheManager;

		finalReadTime = new java.util.Date();

		outputMessageQueue = new ArrayDeque<ArrayDeque<WrapBuffer>>(serverOutputMessageQueueCapacity);
	}

	public SocketChannel getOwnerSC() {
		return acceptedSocketChannel;
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
		//synchronized (monitorOfServerMailID) {
			if (Integer.MAX_VALUE == serverMailID) {
				serverMailID = Integer.MIN_VALUE;
			} else {
				serverMailID++;
			}
			return serverMailID;
		//}
	}

	public void close() {
		try {
			acceptedSocketChannel.shutdownOutput();
		} catch (Exception e) {
			log.warn("fail to shutdown output of the socket channel[{}], errmsg={}", acceptedSocketChannel.hashCode(),
					e.getMessage());
		}

		try {
			acceptedSocketChannel.close();
		} catch (Exception e) {
			log.warn("fail to close the socket channel[{}], errmsg={}", acceptedSocketChannel.hashCode(),
					e.getMessage());
		}
		
		serverIOEvenetController.cancel(personalSelectionKey);

		releaseResources();
	}

	private void releaseResources() {
		socketOutputStream.close();
		releaseLoginUserResource();

		serverIOEvenetController.cancel(personalSelectionKey);

		log.info("this accepted socket channel[hashcode={}, selection key={}]'s resources has been released",
				acceptedSocketChannel.hashCode(), personalSelectionKey.hashCode());
	}

	@Override
	public void onRead(SelectionKey personalSelectionKey) throws InterruptedException {
		try {
			int numberOfReadBytes = socketOutputStream.read(acceptedSocketChannel);

			if (numberOfReadBytes == -1) {
				String errorMessage = new StringBuilder("this socket channel[").append(acceptedSocketChannel.hashCode())
						.append("] has reached end-of-stream").toString();

				log.warn(errorMessage);
				close();
				return;
			} 
			
			setFinalReadTime();
			messageProtocol.S2MList(socketOutputStream, this);

		} catch (NoMoreDataPacketBufferException e) {
			String errorMessage = new StringBuilder()
					.append("the no more data packet buffer error occurred while reading the socket[")
					.append(acceptedSocketChannel.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);

			close();
			return;
		} catch (IOException e) {
			String errorMessage = new StringBuilder().append("the io error occurred while reading the socket[")
					.append(acceptedSocketChannel.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);

			
			close();
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("the unknown error occurred while reading the socket[")
					.append(acceptedSocketChannel.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);

			close();
			return;
		}
	}

	@Override
	public void onWrite(SelectionKey personalSelectionKey) throws InterruptedException {
		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = outputMessageQueue.peek();
		WrapBuffer currentWorkingWrapBuffer = inputMessageWrapBufferQueue.peek();
		ByteBuffer currentWorkingByteBuffer = currentWorkingWrapBuffer.getByteBuffer();
		boolean loop = true;
		while (loop) {
			int numberOfBytesWritten = 0;
			try {
				numberOfBytesWritten = acceptedSocketChannel.write(currentWorkingByteBuffer);
			} catch (IOException e) {
				String errorMessage = new StringBuilder().append("fail to write a sequence of bytes to this channel[")
						.append(acceptedSocketChannel.hashCode()).append("] because io error occured, errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);

				close();
				return;
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("fail to write a sequence of bytes to this channel[")
						.append(acceptedSocketChannel.hashCode()).append("] because unknow error occured, errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);

				close();
				return;
			}

			if (0 == numberOfBytesWritten) {
				loop = false;
				return;
			}

			if (!currentWorkingByteBuffer.hasRemaining()) {
				inputMessageWrapBufferQueue.removeFirst();
				dataPacketBufferPool.putDataPacketBuffer(currentWorkingWrapBuffer);

				if (inputMessageWrapBufferQueue.isEmpty()) {
					outputMessageQueue.removeFirst();
					if (outputMessageQueue.isEmpty()) {
						personalSelectionKey.interestOps(personalSelectionKey.interestOps() & ~SelectionKey.OP_WRITE);
						loop = false;
						return;
					}
					inputMessageWrapBufferQueue = outputMessageQueue.peek();

				}

				currentWorkingWrapBuffer = inputMessageWrapBufferQueue.peek();
				currentWorkingByteBuffer = currentWorkingWrapBuffer.getByteBuffer();
			}
		}

	}

	public void addOutputMessage(AbstractMessage outputMessage, ArrayDeque<WrapBuffer> outputMessageWrapBufferQueue)
			throws InterruptedException {		
		
		// log.info("outputMessageQueue.size={}, inputMessageCount={}", outputMessageQueue.size(), inputMessageCount);
		
		outputMessageQueue.offer(outputMessageWrapBufferQueue);
		personalSelectionKey.interestOps(personalSelectionKey.interestOps() | SelectionKey.OP_WRITE);
	}

	/** only Junit test */
	public ArrayDeque<ArrayDeque<WrapBuffer>> getOutputMessageQueue() {
		return outputMessageQueue;
	}

	public int hashCode() {
		return acceptedSocketChannel.hashCode();
	}

	

	@Override
	public void putReceivedMessage(ReadableMiddleObjectWrapper readableMiddleObjectWrapper)
			throws InterruptedException {
		String messageID = readableMiddleObjectWrapper.getMessageID();
		try {
			AbstractServerTask serverTask = null;
			try {
				serverTask = serverObjectCacheManager.getServerTask(messageID);
			} catch (DynamicClassCallException e) {
				log.warn(e.getMessage());

				SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
				String errorReason = e.getMessage();
				ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(errorType, errorReason,
						readableMiddleObjectWrapper, this, messageProtocol);

				return;
			} catch (Exception | Error e) {
				log.warn("unknown error::fail to get a input message server task", e);

				SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
				String errorReason = "fail to get a input message server task::" + e.getMessage();
				ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(errorType, errorReason,
						readableMiddleObjectWrapper, this, messageProtocol);
				return;
			}

			try {
				serverTask.execute(projectName, this, projectLoginManager, readableMiddleObjectWrapper,
						messageProtocol, this);
			} catch (InterruptedException e) {
				throw e;
			} catch (Exception | Error e) {
				log.warn("unknwon error::fail to execute a input message server task", e);

				SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(ServerTaskException.class);
				String errorReason = "fail to execute a input message server task::" + e.getMessage();
				ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(errorType, errorReason,
						readableMiddleObjectWrapper, this, messageProtocol);
				return;
			}

		} finally {
			/**
			 * <pre>
			 * MiddleReadableObject 가 가진 자원 반환을 하는 장소는  2군데이다.
			 * 첫번째 장소는 메시지 추출 후 쓰임이 다해서 호출하는 AbstractMessageDecoder#decode 이며
			 * 두번째 장소는 2번 연속 호출해도 무방하기때문에 안전하게 자원 반환을 보장하기위한 Executor#run 이다.
			 * </pre>
			 */
			readableMiddleObjectWrapper.closeReadableMiddleObject();
		}
	}

	@Override
	public boolean isLogin() {
		if (null == personalLoginID) {
			return false;
		}		
		
		boolean isConnected = acceptedSocketChannel.isConnected();
		
		return isConnected;
	}

	@Override
	public void registerLoginUser(String loginID) {
		if (null == loginID) {
			throw new IllegalArgumentException("the parameter loginID is null");
		}
		this.personalLoginID = loginID;
		projectLoginManager.registerloginUser(personalSelectionKey, loginID);
	}

	@Override
	public String getLoginID() {
		return personalLoginID;
	}
	
	/** 로그 아웃시 할당 받은 자원을 해제한다. */
	private void releaseLoginUserResource() {
		if (null != personalLoginID) {
			projectLoginManager.removeLoginUser(personalSelectionKey);
			LocalSourceFileResourceManager.getInstance().removeUsingUserIDWithUnlockFile(personalLoginID);
			LocalTargetFileResourceManager.getInstance().removeUsingUserIDWithUnlockFile(personalLoginID);
		}
	}
	
	public boolean canRead() {
		return  (outputMessageQueue.size() < serverOutputMessageQueueCapacity);
	}
	
	
	public String toSimpleInfomation() {
		StringBuilder builder = new StringBuilder();
		builder.append("personalSelectionKey=");
		builder.append(personalSelectionKey);
		builder.append(", acceptedSocketChannel=");
		builder.append(acceptedSocketChannel);
		return builder.toString();
	}
	
	public boolean isConnected() {
		return acceptedSocketChannel.isConnected();
	}
	
}
