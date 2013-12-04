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


package kr.pe.sinnori.client;

import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.AbstractConnectionPool;
import kr.pe.sinnori.client.connection.asyn.noshare.NoShareAsynConnectionPool;
import kr.pe.sinnori.client.connection.asyn.share.ShareAsynConnectionPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPool;
import kr.pe.sinnori.client.connection.sync.noshare.NoShareSyncConnectionPool;
import kr.pe.sinnori.client.io.LetterFromServer;
import kr.pe.sinnori.client.io.LetterToServer;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.lib.AbstractProject;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.lib.OutputMessageQueueQueueMangerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.lib.WrapOutputMessageQueue;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * <pre>
 * 클라리언트 프로젝트 클래스. 프로젝트 소속 클라이언트용 서버접속 API 동작 환경 조성과 자원을 전담하는 클래스.
 * -  자원 목록 -
 * (1) 데이터 패킷 버퍼 큐
 * (2) 바디 버퍼 큐
 * (3) 비동기 입출력 자원
 *     (3-1) 입력 메시지 큐
 *     (3-2) 출력 메시지 큐를 원소로 하는 큐
 *     (3-3) 서버에서 보내는 불특정 다수 메시지를 받는 큐
 *     (3-4) 입력 메시지 쓰기 담당 쓰레드 폴
 *     (3-5) 출력 메시지 쓰기 담당 쓰레드 폴
 * (4) 메시지 정보 해쉬
 * </pre>
 * 
 * @author Jonghoon Won
 *
 */
public class ClientProject extends AbstractProject implements ClientProjectIF, OutputMessageQueueQueueMangerIF {
	/** 모니터 객체 */
	private final Object outputMessageQueuerQueueMonitor = new Object();

	/** 비동기 방식에서 사용되는 입력 메시지 큐 */
	private LinkedBlockingQueue<LetterToServer> inputMessageQueue = null;
	
	/** 비동기 방식에서 사용되는 출력 메시지 큐를 원소로 가지는 큐 */
	private LinkedBlockingQueue<WrapOutputMessageQueue> outputMessageQueueQueue = null;
	
	/** 서버에서 보내는 불특정 다수 메시지를 받는 큐 */
	private LinkedBlockingQueue<OutputMessage> serverOutputMessageQueue = null;

	/** 비동기 방식에서 사용되는 입력 메시지 쓰기 쓰레드 */
	private InputMessageWriterPool inputMessageWriterPool = null;
	
	/** 비동기 방식에서 사용되는 출력 메시지 읽기 쓰레드 */
	private OutputMessageReaderPool outputMessageReaderPool = null;
	
	/** 프로젝트의 연결 클래스 폴 */
	private AbstractConnectionPool connectionPool = null;
	
	/**
	 * 생성자
	 * @param projectName 프로젝트 이름
	 * @throws NoMoreDataPacketBufferException 프로젝트의 연결 클래스을 만들때 데이터 패킷 버퍼 부족시 던지는 예외
	 * @throws NoMoreOutputMessageQueueException 프로젝트의 연결 클래스을 만들때 바디 버퍼 부족시 던지는 예외
	 * @throws InterruptedException 쓰레드 인터럽트
	 */
	public ClientProject(String projectName) throws NoMoreDataPacketBufferException, NoMoreOutputMessageQueueException, InterruptedException {		
		super(projectName);
		
		ClientProjectConfig clientProjectInfo = projectInfo.getClientProjectInfo();
		
		int dataPacketBufferCnt = clientProjectInfo.getDataPacketBufferCnt();
		int connectionCount = clientProjectInfo.getConnectionCount();		
		long socketTimeOut = clientProjectInfo.getSocketTimeout();
		boolean whetherToAutoConnect = clientProjectInfo.getWhetherToAutoConnect();		

		dataPacketBufferQueue = new LinkedBlockingQueue<WrapBuffer>(dataPacketBufferCnt);
		try {
			for (int i = 0; i < dataPacketBufferCnt; i++) {
				WrapBuffer buffer = new WrapBuffer(commonProjectInfo.dataPacketBufferSize);
				dataPacketBufferQueue.add(buffer);
				buffer.getByteBuffer().order(commonProjectInfo.byteOrderOfProject);
			}
		} catch (OutOfMemoryError e) {
			String errorMessage = "OutOfMemoryError";
			log.fatal(errorMessage, e);
			throw new NoMoreDataPacketBufferException(errorMessage);
		}
		
		
		boolean channelBlockingMode = clientProjectInfo.getChannelBlockingMode();
		CommonType.THREAD_SHARE_MODE threadShareMode = clientProjectInfo.getThreadShareMode();
		
		
		if (channelBlockingMode) {			
			connectionPool = new NoShareSyncConnectionPool(connectionCount, 
					socketTimeOut, whetherToAutoConnect, commonProjectInfo, messageExchangeProtocol, this, this);
		} else {
			int inputMessageQueueSize = clientProjectInfo.getInputMessageQueueSize();
			int OutputMessageQueueSize = clientProjectInfo.getOutputMessageQueueSize();
			int finishConnectMaxCall = clientProjectInfo.getFinishConnectMaxCall();
			long finishConnectWaittingTime = clientProjectInfo.getReadSelectorWakeupInterval();
			long readSelectorWakeupInterval = clientProjectInfo.getReadSelectorWakeupInterval();
		
			serverOutputMessageQueue  = new LinkedBlockingQueue<OutputMessage>(OutputMessageQueueSize);
			
			inputMessageQueue = new LinkedBlockingQueue<LetterToServer>(inputMessageQueueSize);
			
			inputMessageWriterPool = new InputMessageWriterPool(
					clientProjectInfo.getInputMessageWriterSize(),
					clientProjectInfo.getInputMessageWriterMaxSize(),
					commonProjectInfo,
					inputMessageQueue, messageExchangeProtocol, this, this);
			
			outputMessageReaderPool = new OutputMessageReaderPool(
					clientProjectInfo.getOutputMessageReaderSize(),
					clientProjectInfo.getOutputMessageReaderMaxSize(), 
					readSelectorWakeupInterval, commonProjectInfo, messageExchangeProtocol, this);
			
			inputMessageWriterPool.startAll();
			outputMessageReaderPool.startAll();
			
			
			
			if (CommonType.THREAD_SHARE_MODE.Multi == threadShareMode) {
				int mailBoxCnt = clientProjectInfo.getMultiMailboxCnt();
				
				int  outputMessageQueueQueueSize = mailBoxCnt * connectionCount;
				outputMessageQueueQueue = new LinkedBlockingQueue<WrapOutputMessageQueue>(outputMessageQueueQueueSize);
				
				
				for (int i=0; i < connectionCount; i++) {
					for (int j=0; j < mailBoxCnt; j++) {
						LinkedBlockingQueue<OutputMessage> outputMessageQueue = new LinkedBlockingQueue<OutputMessage>(OutputMessageQueueSize);
						WrapOutputMessageQueue wrapOutputMessageQeuue = new WrapOutputMessageQueue(outputMessageQueue);
						outputMessageQueueQueue.add(wrapOutputMessageQeuue);
					}
				}

				connectionPool = new ShareAsynConnectionPool(connectionCount,
						socketTimeOut, whetherToAutoConnect,
						finishConnectMaxCall, finishConnectWaittingTime, 
						mailBoxCnt,
						commonProjectInfo, 
						serverOutputMessageQueue, 
						inputMessageQueue, this, 
						outputMessageReaderPool, this, this);
			} else {
				outputMessageQueueQueue = new LinkedBlockingQueue<WrapOutputMessageQueue>(connectionCount);
				for (int i=0; i < connectionCount; i++) {
					LinkedBlockingQueue<OutputMessage> outputMessageQueue = new LinkedBlockingQueue<OutputMessage>(OutputMessageQueueSize);
					WrapOutputMessageQueue wrapOutputMessageQeuue = new WrapOutputMessageQueue(outputMessageQueue);
					outputMessageQueueQueue.add(wrapOutputMessageQeuue);
				}
				
				connectionPool = new NoShareAsynConnectionPool(connectionCount, 
						socketTimeOut, whetherToAutoConnect,
						finishConnectMaxCall, finishConnectWaittingTime, 
						commonProjectInfo, 
						serverOutputMessageQueue, 
						inputMessageQueue, this, 
						outputMessageReaderPool, this, this);
			}
		}
	}
	
	
	@Override
	public LetterFromServer sendInputMessage(
			InputMessage inputMessage) throws ServerNotReadyException,
			SocketTimeoutException, NoMoreDataPacketBufferException,
			BodyFormatException, MessageInfoNotFoundException {
		return connectionPool.sendInputMessage(inputMessage);
	}
	
	@Override
	public CommonProjectInfo getCommonProjectInfo() {
		return commonProjectInfo;
	}
	
	/**
	 * 서버에서 보낸 불특정 다수로 출력 메시지를 얻는다. 단 서버에서 보낸 불특정 다수로 출력 메시지가 들어올 때까지 블락 된다.
	 * @return 서버에서 보낸 불특정 다수로 출력 메시지
	 * @throws InterruptedException 쓰레드 인터럽트
	 */
	public OutputMessage takeServerOutputMessageQueue()
			throws InterruptedException {
		OutputMessage outputMessage = null;
		outputMessage = serverOutputMessageQueue.take();
		return outputMessage;
	}
	/*
	public void startAsynPool() {
		if (null != inputMessageWriterPool) {
			inputMessageWriterPool.startAll();
		}
		if (null != outputMessageReaderPool) {
			outputMessageReaderPool.startAll();
		}
	}
	*/
	/**
	 * 비동기 입출력용 소켓 읽기/쓰기 쓰레드들을 중지한다. 동기 모드인 경우에 호출할 경우 아무 동작 없다.
	 */
	public void stopAsynPool() {
		if (null != inputMessageWriterPool) {
			inputMessageWriterPool.stopAll();
		}
		
		if (null != outputMessageReaderPool) {
			outputMessageReaderPool.stopAll();
		}
	}

	@Override
	public WrapOutputMessageQueue pollOutputMessageQueue()
			throws NoMoreOutputMessageQueueException {
		WrapOutputMessageQueue wrapOutputMessageQueue = outputMessageQueueQueue.poll();
		if (null == wrapOutputMessageQueue) {
			String errorMessage = String.format("클라이언트 프로젝트[%s]에서 랩 출력 메시지큐가 부족합니다.", commonProjectInfo.projectName);
			throw new NoMoreOutputMessageQueueException(errorMessage);
		}
		
		wrapOutputMessageQueue.queueOut();
		
		return wrapOutputMessageQueue;
	}

	@Override
	public void putOutputMessageQueue(
			WrapOutputMessageQueue wrapOutputMessageQueue) {
		if (null == wrapOutputMessageQueue)
			return;

		/**
		 * 2번 연속 반환 막기
		 */
		synchronized (outputMessageQueuerQueueMonitor) {
			if (wrapOutputMessageQueue.isInQueue()) {
				log.warn(String.format("출력 메시지 큐 2번 연속 반환 시도"));
				return;
			}
			wrapOutputMessageQueue.queueIn();
		}

		outputMessageQueueQueue.add(wrapOutputMessageQueue);
		
	}	
	
	public MonitorClientProjectInfo getInfo() {
		MonitorClientProjectInfo clientProjectInfo = new MonitorClientProjectInfo();
		clientProjectInfo.projectName = commonProjectInfo.projectName;
		clientProjectInfo.dataPacketBufferQueueSize = dataPacketBufferQueue.size();
		
		clientProjectInfo.usedMailboxCnt = connectionPool.getUsedMailboxCnt();
		clientProjectInfo.totalMailbox = connectionPool.getTotalMailbox();
		
		if (connectionPool instanceof NoShareSyncConnectionPool) {
			clientProjectInfo.inputMessageQueueSize = -1;
			clientProjectInfo.outputMessageQueueQueueSize = -1;
			clientProjectInfo.serverOutputMessageQueueSize = -1;
		} else {
			clientProjectInfo.inputMessageQueueSize = inputMessageQueue.size();
			clientProjectInfo.outputMessageQueueQueueSize = outputMessageQueueQueue.size();
			clientProjectInfo.serverOutputMessageQueueSize = serverOutputMessageQueue.size();
		}
		
		return clientProjectInfo;
	}
}

