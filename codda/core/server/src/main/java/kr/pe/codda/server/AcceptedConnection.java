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

import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.ReceivedDataStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ProtocolUtil;
import kr.pe.codda.common.protocol.ReceivedMessageReceiverIF;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.codda.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.codda.server.classloader.ServerTaskMangerIF;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * 서버에 접속하는 클라이언트 자원 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public class AcceptedConnection
		implements ServerIOEventHandlerIF, ReceivedMessageReceiverIF, PersonalLoginManagerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AcceptedConnection.class);

	private SelectionKey personalSelectionKey = null;
	private SocketChannel acceptedSocketChannel = null;
	private String projectName = null;
	@SuppressWarnings("unused")
	private long socketTimeout = 5000;
	private int serverOutputMessageQueueCapacity = 5;
	private MessageProtocolIF messageProtocol = null;
	private ReceivedDataStream receivedDataOnlyStream = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ServerIOEvenetControllerIF serverIOEvenetController = null;
	private ServerTaskMangerIF serverTaskManager = null;

	private transient ArrayDeque<ArrayDeque<WrapBuffer>> outputMessageQueue = null;
	private ProjectLoginManagerIF projectLoginManager = null;
	private String personalLoginID = null;

	/** 최종 읽기를 수행한 시간. 초기값은 클라이언트(=SocketChannel) 생성시간이다 */
	private java.util.Date finalReadTime = null;

	// private final Object monitorOfServerMailID = new Object();
	/** 클라이언트에 할당되는 서버 편지 식별자 */
	private int serverMailID = Integer.MIN_VALUE;

	public AcceptedConnection(SelectionKey personalSelectionKey, SocketChannel acceptedSocketChannel,
			String projectName, long socketTimeOut, int serverOutputMessageQueueCapacity,
			ReceivedDataStream receivedDataOnlyStream, ProjectLoginManagerIF projectLoginManager,
			MessageProtocolIF messageProtocol, DataPacketBufferPoolIF dataPacketBufferPool,
			ServerIOEvenetControllerIF serverIOEvenetController, ServerTaskMangerIF serverTaskManager) {

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
			throw new IllegalArgumentException(
					"the parameter serverOutputMessageQueueCapacity is less than or equal to zero");
		}

		if (null == receivedDataOnlyStream) {
			throw new IllegalArgumentException("the parameter receivedDataOnlyStream is null");
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
		this.receivedDataOnlyStream = receivedDataOnlyStream;
		this.projectLoginManager = projectLoginManager;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.serverIOEvenetController = serverIOEvenetController;
		this.serverTaskManager = serverTaskManager;

		finalReadTime = new java.util.Date();

		outputMessageQueue = new ArrayDeque<ArrayDeque<WrapBuffer>>(serverOutputMessageQueueCapacity);
	}

	/*
	 * public SocketChannel getOwnerSC() { return acceptedSocketChannel; }
	 */

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

	private void turnOnSocketWriteMode() throws CancelledKeyException {
		personalSelectionKey.interestOps(personalSelectionKey.interestOps() | SelectionKey.OP_WRITE);

		// log.info("call turn on OP_WRITE[{}]",
		// acceptedSocketChannel.hashCode());
	}

	private void turnOffSocketWriteMode() throws CancelledKeyException {
		personalSelectionKey.interestOps(personalSelectionKey.interestOps() & ~SelectionKey.OP_WRITE);

		// log.info("call turn off OP_WRITE[{}]",
		// acceptedSocketChannel.hashCode());
	}

	private void turnOnSocketReadMode() throws CancelledKeyException {
		personalSelectionKey.interestOps(personalSelectionKey.interestOps() | SelectionKey.OP_READ);

		// log.info("call turn on OP_READ[{}]",
		// acceptedSocketChannel.hashCode());
	}

	private void turnOffSocketReadMode() throws CancelledKeyException {
		personalSelectionKey.interestOps(personalSelectionKey.interestOps() & ~SelectionKey.OP_READ);

		// log.info("call turn off OP_READ[{}]",
		// acceptedSocketChannel.hashCode());
	}

	/**
	 * 메일 식별자를 반환한다. 메일 식별자는 자동 증가된다.
	 */
	public int getServerMailID() {
		// synchronized (monitorOfServerMailID) {
		if (Integer.MAX_VALUE == serverMailID) {
			serverMailID = Integer.MIN_VALUE;
		} else {
			serverMailID++;
		}
		return serverMailID;
		// }
	}

	@Override
	public void onRead(SelectionKey personalSelectionKey) throws Exception {

		int numberOfReadBytes = receivedDataOnlyStream.read(acceptedSocketChannel);

		if (numberOfReadBytes == -1) {
			String errorMessage = new StringBuilder("this socket channel[").append(acceptedSocketChannel.hashCode())
					.append("] has reached end-of-stream").toString();

			log.warn(errorMessage);
			close();
			return;
		}

		setFinalReadTime();
		messageProtocol.S2MList(receivedDataOnlyStream, this);
		/**
		 * 추출된 메시지에 1:1 대응하는 서버 비지니스 로직 수행후 출력 메시지 큐 크기가 수용 가능 용량의 50% 보다 클 경우 소켓 읽기 이벤트
		 * 끄기
		 */
		if (outputMessageQueue.size() > serverOutputMessageQueueCapacity / 2) {
			turnOffSocketReadMode();
		}

	}

	@Override
	public void onWrite(SelectionKey personalSelectionKey) throws Exception {
		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = outputMessageQueue.peek();
		if (null == inputMessageWrapBufferQueue) {
			log.warn("the var inputMessageWrapBufferQueue is null");
			return;
		}

		WrapBuffer currentWorkingWrapBuffer = inputMessageWrapBufferQueue.peek();
		ByteBuffer currentWorkingByteBuffer = currentWorkingWrapBuffer.getByteBuffer();
		boolean loop = true;

		while (loop) {
			int numberOfBytesWritten = acceptedSocketChannel.write(currentWorkingByteBuffer);

			if (0 == numberOfBytesWritten) {
				loop = false;
				break;
			}

			if (!currentWorkingByteBuffer.hasRemaining()) {
				inputMessageWrapBufferQueue.removeFirst();
				dataPacketBufferPool.putDataPacketBuffer(currentWorkingWrapBuffer);

				if (inputMessageWrapBufferQueue.isEmpty()) {
					outputMessageQueue.removeFirst();
					if (outputMessageQueue.isEmpty()) {
						try {
							turnOffSocketWriteMode();
						} catch (CancelledKeyException e) {
							log.warn("더 이상의 출력 메시지가 없어 OP_WRITE 스위치를 끄려고 할때 CancelledKeyException 발생");
							close();
							return;
						}
						loop = false;
						break;
					}
					inputMessageWrapBufferQueue = outputMessageQueue.peek();

				}

				currentWorkingWrapBuffer = inputMessageWrapBufferQueue.peek();
				currentWorkingByteBuffer = currentWorkingWrapBuffer.getByteBuffer();
			}
		}

		turnOnSocketReadModeIfValid();
	}

	/**
	 * 소켓 읽기 이벤트가 꺼져 있는 상태라면 더 이상 전송할 데이터 없는 상태가 되어 소켓 쓰기 이벤트가 꺼져 있거나 혹은 출력 메시지 큐가
	 * 최대 용량 25% 보다 작은 경우 소켓 읽기 이벤트 켜기
	 */
	private void turnOnSocketReadModeIfValid() {
		int interestOps = personalSelectionKey.interestOps();

		if (((interestOps & SelectionKey.OP_READ) == 0)) {
			if ((interestOps & SelectionKey.OP_WRITE) == 0
					|| outputMessageQueue.size() < serverOutputMessageQueueCapacity / 4) {
				turnOnSocketReadMode();
			}
		}
	}

	/** only JUnit test */
	public ArrayDeque<ArrayDeque<WrapBuffer>> getOutputMessageQueue() {
		return outputMessageQueue;
	}

	public int hashCode() {
		return acceptedSocketChannel.hashCode();
	}

	/**
	 * 서버 비지니스 로직에서 출력 메시지가 생겼을때 호출하는 메소드로 출력 메시지를 출력 메시지 큐에 넣고
	 * 
	 * @param outputMessage
	 * @param outputMessageWrapBufferQueue
	 * @throws InterruptedException
	 */
	public void addOutputMessage(AbstractMessage outputMessage, ArrayDeque<WrapBuffer> outputMessageWrapBufferQueue)
			throws InterruptedException {

		// log.info("outputMessageQueue.size={}, inputMessageCount={}",
		// outputMessageQueue.size(), inputMessageCount);

		outputMessageQueue.offer(outputMessageWrapBufferQueue);
		try {
			/**
			 * 소켓 쓰기 이벤트를 켠다
			 */
			turnOnSocketWriteMode();
		} catch (CancelledKeyException e) {
			log.warn("this client[{}]'s selection key has been cancelled", acceptedSocketChannel.hashCode());

			close();
		}
	}

	@Override
	public void putReceivedMessage(int mailboxID, int mailID, String messageID, Object readableMiddleObject)
			throws InterruptedException {

		AbstractServerTask serverTask = null;
		try {
			serverTask = serverTaskManager.getServerTask(messageID);
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());

			ProtocolUtil.closeReadableMiddleObject(mailboxID, mailID, messageID, readableMiddleObject);

			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = e.getMessage();
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(errorType, errorReason, mailboxID, mailID,
					messageID, this, messageProtocol);

			return;
		} catch (Exception | Error e) {
			String errorMessage = new StringBuilder().append("unknown error::fail to get a input message[")
					.append(messageID).append("] server task").toString();
			log.warn(errorMessage, e);

			ProtocolUtil.closeReadableMiddleObject(mailboxID, mailID, messageID, readableMiddleObject);

			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(DynamicClassCallException.class);
			String errorReason = "fail to get a input message server task::" + e.getMessage();
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(errorType, errorReason, mailboxID, mailID,
					messageID, this, messageProtocol);
			return;
		}

		try {
			serverTask.execute(projectName, this, projectLoginManager, mailboxID, mailID, messageID,
					readableMiddleObject, messageProtocol, this);
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception | Error e) {
			log.warn("unknwon error::fail to execute a input message server task", e);

			SelfExn.ErrorType errorType = SelfExn.ErrorType.valueOf(ServerTaskException.class);
			String errorReason = "fail to execute a input message server task::" + e.getMessage();
			ToLetterCarrier.putInputErrorMessageToOutputMessageQueue(errorType, errorReason, mailboxID, mailID,
					messageID, this, messageProtocol);
			return;
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

	private void releaseResources() {
		while (!outputMessageQueue.isEmpty()) {
			ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = outputMessageQueue.removeFirst();

			while (!inputMessageWrapBufferQueue.isEmpty()) {
				WrapBuffer buffer = inputMessageWrapBufferQueue.removeFirst();
				dataPacketBufferPool.putDataPacketBuffer(buffer);
			}
		}

		receivedDataOnlyStream.close();
		releaseLoginUserResource();

		log.info("this accepted socket channel[hashcode={}, selection key={}]'s resources has been released",
				acceptedSocketChannel.hashCode(), personalSelectionKey.hashCode());
	}

	/** 로그 아웃시 할당 받은 자원을 해제한다. */
	private void releaseLoginUserResource() {
		if (null != personalLoginID) {
			projectLoginManager.removeLoginUser(personalSelectionKey);
			LocalSourceFileResourceManager.getInstance().removeUsingUserIDWithUnlockFile(personalLoginID);
			LocalTargetFileResourceManager.getInstance().removeUsingUserIDWithUnlockFile(personalLoginID);
		}
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

}
