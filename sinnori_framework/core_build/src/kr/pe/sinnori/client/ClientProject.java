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
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.AbstractConnectionPool;
import kr.pe.sinnori.client.connection.asyn.noshare.NoShareAsynConnectionPool;
import kr.pe.sinnori.client.connection.asyn.share.ShareAsynConnectionPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPool;
import kr.pe.sinnori.client.connection.sync.noshare.NoShareSyncConnectionPool;
import kr.pe.sinnori.client.io.LetterFromServer;
import kr.pe.sinnori.client.io.LetterToServer;
import kr.pe.sinnori.common.configuration.ClientProjectConfigIF;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.io.MessageProtocolIF;
import kr.pe.sinnori.common.lib.AbstractProject;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonType.CONNECTION_TYPE;
import kr.pe.sinnori.common.lib.SyncOutputMessageQueueQueueMangerIF;
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
public class ClientProject extends AbstractProject implements ClientProjectIF, SyncOutputMessageQueueQueueMangerIF {
	/** 모니터 객체 */
	private final Object outputMessageQueuerQueueMonitor = new Object();
	
	// private final Object anonymousServerMessageTaskMonitor = new Object();

	/** 비동기 방식에서 사용되는 입력 메시지 큐 */
	private LinkedBlockingQueue<LetterToServer> inputMessageQueue = null;
	
	/** 비동기 방식에서 사용되는 출력 메시지 큐를 원소로 가지는 큐 */
	private LinkedBlockingQueue<WrapOutputMessageQueue> syncOutputMessageQueueQueue = null;
	
	/** 서버에서 보내는 불특정 다수 메시지를 받는 큐 */
	private LinkedBlockingQueue<OutputMessage> asynOutputMessageQueue = null;

	/** 비동기 방식에서 사용되는 입력 메시지 쓰기 쓰레드 */
	private InputMessageWriterPool inputMessageWriterPool = null;
	
	/** 비동기 방식에서 사용되는 출력 메시지 읽기 쓰레드 */
	private OutputMessageReaderPool outputMessageReaderPool = null;
	
	/** 프로젝트의 연결 클래스 폴 */
	private AbstractConnectionPool connectionPool = null;
	
	private AsynOutputMessageExecutorThread[] asynOutputMessageExecutorThreadList = null;
	// private AsynOutputMessageExecutorThread asynOutputMessageExecutorThread = null;
	
	private ClientProjectMonitor clientProjectMonitor = null;
	
	/**
	 * 생성자
	 * @param projectName 프로젝트 이름
	 * @throws NoMoreDataPacketBufferException 프로젝트의 연결 클래스을 만들때 데이터 패킷 버퍼 부족시 던지는 예외
	 * @throws NoMoreOutputMessageQueueException 프로젝트의 연결 클래스을 만들때 바디 버퍼 부족시 던지는 예외
	 * @throws InterruptedException 쓰레드 인터럽트
	 */
	public ClientProject(String projectName) throws NoMoreDataPacketBufferException, NoMoreOutputMessageQueueException, InterruptedException {		
		super(projectName);
		 
		ClientProjectConfigIF clientProjectConfig = (ClientProjectConfigIF)projectConfig;
		
		int dataPacketBufferCnt = clientProjectConfig.getClientDataPacketBufferCnt();
		int connectionCount = clientProjectConfig.getClientConnectionCount();		
		long socketTimeOut = clientProjectConfig.getClientSocketTimeout();
		boolean whetherToAutoConnect = clientProjectConfig.getClientWhetherToAutoConnect();		

		dataPacketBufferQueue = new LinkedBlockingQueue<WrapBuffer>(dataPacketBufferCnt);
		try {
			for (int i = 0; i < dataPacketBufferCnt; i++) {
				WrapBuffer buffer = new WrapBuffer(clientProjectConfig.getDataPacketBufferSize());
				dataPacketBufferQueue.add(buffer);
				buffer.getByteBuffer().order(clientProjectConfig.getByteOrder());
			}
		} catch (OutOfMemoryError e) {
			String errorMessage = "OutOfMemoryError";
			log.fatal(errorMessage, e);
			throw new NoMoreDataPacketBufferException(errorMessage);
		}
		
		CONNECTION_TYPE connectionType = clientProjectConfig.getConnectionType();
		
		
		
		if (CONNECTION_TYPE.NoShareSync == connectionType) {			
			connectionPool = new NoShareSyncConnectionPool(connectionCount, 
					socketTimeOut, whetherToAutoConnect, clientProjectConfig, messageExchangeProtocol, this, this);
		} else {
			int inputMessageQueueSize = clientProjectConfig.getClientInputMessageQueueSize();
			int OutputMessageQueueSize = clientProjectConfig.getClientOutputMessageQueueSize();
			int finishConnectMaxCall = clientProjectConfig.getClientFinishConnectMaxCall();
			long finishConnectWaittingTime = clientProjectConfig.getClientReadSelectorWakeupInterval();
			long readSelectorWakeupInterval = clientProjectConfig.getClientReadSelectorWakeupInterval();
		
			asynOutputMessageQueue  = new LinkedBlockingQueue<OutputMessage>(OutputMessageQueueSize);
			
			inputMessageQueue = new LinkedBlockingQueue<LetterToServer>(inputMessageQueueSize);
			
			inputMessageWriterPool = new InputMessageWriterPool(
					clientProjectConfig.getClientInputMessageWriterSize(),
					clientProjectConfig.getClientInputMessageWriterMaxSize(),
					clientProjectConfig,
					inputMessageQueue, messageExchangeProtocol, this, this);
			
			outputMessageReaderPool = new OutputMessageReaderPool(
					clientProjectConfig.getClientOutputMessageReaderSize(),
					clientProjectConfig.getClientOutputMessageReaderMaxSize(), 
					readSelectorWakeupInterval, clientProjectConfig, messageExchangeProtocol, this);
			
			inputMessageWriterPool.startAll();
			outputMessageReaderPool.startAll();
			
			
			if (CONNECTION_TYPE.ShareAsyn == connectionType) {
				int mailBoxCnt = clientProjectConfig.getClientShareAsynConnMailboxCnt();
				
				int  outputMessageQueueQueueSize = mailBoxCnt * connectionCount;
				syncOutputMessageQueueQueue = new LinkedBlockingQueue<WrapOutputMessageQueue>(outputMessageQueueQueueSize);
				
				
				for (int i=0; i < connectionCount; i++) {
					for (int j=0; j < mailBoxCnt; j++) {
						LinkedBlockingQueue<OutputMessage> outputMessageQueue = new LinkedBlockingQueue<OutputMessage>(OutputMessageQueueSize);
						WrapOutputMessageQueue wrapOutputMessageQeuue = new WrapOutputMessageQueue(outputMessageQueue);
						syncOutputMessageQueueQueue.add(wrapOutputMessageQeuue);
					}
				}

				connectionPool = new ShareAsynConnectionPool(connectionCount,
						socketTimeOut, whetherToAutoConnect,
						finishConnectMaxCall, finishConnectWaittingTime, 
						mailBoxCnt,
						clientProjectConfig, 
						asynOutputMessageQueue, 
						inputMessageQueue, this, 
						outputMessageReaderPool, this, this);
			} else {
				syncOutputMessageQueueQueue = new LinkedBlockingQueue<WrapOutputMessageQueue>(connectionCount);
				for (int i=0; i < connectionCount; i++) {
					LinkedBlockingQueue<OutputMessage> outputMessageQueue = new LinkedBlockingQueue<OutputMessage>(OutputMessageQueueSize);
					WrapOutputMessageQueue wrapOutputMessageQeuue = new WrapOutputMessageQueue(outputMessageQueue);
					syncOutputMessageQueueQueue.add(wrapOutputMessageQeuue);
				}
				
				connectionPool = new NoShareAsynConnectionPool(connectionCount, 
						socketTimeOut, whetherToAutoConnect,
						finishConnectMaxCall, finishConnectWaittingTime, 
						clientProjectConfig, 
						asynOutputMessageQueue, 
						inputMessageQueue, this, 
						outputMessageReaderPool, this, this);
			}			

			asynOutputMessageExecutorThreadList = new AsynOutputMessageExecutorThread[clientProjectConfig.getClientAsynOutputMessageExecutorThreadCnt()];
			
			for (int i=0; i < asynOutputMessageExecutorThreadList.length; i++) {
				asynOutputMessageExecutorThreadList[i] = new AsynOutputMessageExecutorThread();
				asynOutputMessageExecutorThreadList[i].start();
			}
			
		}
		
		clientProjectMonitor = new ClientProjectMonitor(clientProjectConfig.getClientMonitorTimeInterval(), clientProjectConfig.getClientRequestTimeout());
		clientProjectMonitor.start();
	}
	
	
	@Override
	public LetterFromServer sendSyncInputMessage(
			InputMessage inputMessage) throws ServerNotReadyException,
			SocketTimeoutException, NoMoreDataPacketBufferException,
			BodyFormatException, MessageInfoNotFoundException {
		return connectionPool.sendSyncInputMessage(inputMessage);
	}
	
	
	@Override
	public AbstractConnection getConnection() throws InterruptedException, NotSupportedException {
		return connectionPool.getConnection();
	}
	
	@Override
	public void freeConnection(AbstractConnection conn) throws NotSupportedException {
		connectionPool.freeConnection(conn);;
	}
	
	
	/**
	 * 서버에서 보낸 불특정 다수로 출력 메시지를 얻는다. 단 서버에서 보낸 불특정 다수로 출력 메시지가 들어올 때까지 블락 된다.
	 * @return 서버에서 보낸 불특정 다수로 출력 메시지
	 * @throws InterruptedException 쓰레드 인터럽트
	 */
	public OutputMessage takeServerOutputMessageQueue()
			throws InterruptedException {
		OutputMessage outputMessage = null;
		outputMessage = asynOutputMessageQueue.take();
		return outputMessage;
	}
	
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
		WrapOutputMessageQueue wrapOutputMessageQueue = syncOutputMessageQueueQueue.poll();
		if (null == wrapOutputMessageQueue) {
			String errorMessage = String.format("클라이언트 프로젝트[%s]에서 랩 출력 메시지큐가 부족합니다.", projectConfig.getProjectName());
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

		syncOutputMessageQueueQueue.add(wrapOutputMessageQueue);
		
	}	
	
	/**
	 * @return 클라이언트 프로젝트 정보
	 */
	public ClientProjectMonitorInfo getInfo() {
		ClientProjectMonitorInfo clientProjectMonitorInfo = new ClientProjectMonitorInfo();
		clientProjectMonitorInfo.projectName = projectConfig.getProjectName();
		clientProjectMonitorInfo.dataPacketBufferQueueSize = dataPacketBufferQueue.size();
		
		clientProjectMonitorInfo.usedMailboxCnt = connectionPool.getUsedMailboxCnt();
		clientProjectMonitorInfo.totalMailbox = connectionPool.getTotalMailbox();
		
		if (connectionPool instanceof NoShareSyncConnectionPool) {
			clientProjectMonitorInfo.inputMessageQueueSize = -1;
			clientProjectMonitorInfo.syncOutputMessageQueueQueueSize = -1;
			clientProjectMonitorInfo.AsynOutputMessageQueueSize = -1;
		} else {
			clientProjectMonitorInfo.inputMessageQueueSize = inputMessageQueue.size();
			clientProjectMonitorInfo.syncOutputMessageQueueQueueSize = syncOutputMessageQueueQueue.size();
			clientProjectMonitorInfo.AsynOutputMessageQueueSize = asynOutputMessageQueue.size();
		}
		
		return clientProjectMonitorInfo;
	}
	
	/**
	 * <pre>
	 * 서버에서 보내는 익명 메시지 처리 쓰레드.
	 * 처음 지정되는 익명 메시지 처리자는 디폴트 처리자({@link DefaultAsynOutputMessageTask }) 로 익명 메시지 로그만 찍는다. 
	 * 주) 비동기에서만 동작한다.
	 * </pre>
	 * @author Jonghoon Won
	 *
	 */
	private class AsynOutputMessageExecutorThread extends Thread {
		//private final Object monitor = new Object();
		
		// private LinkedBlockingQueue<OutputMessage> serverOutputMessageQueue = null;
		private AsynOutputMessageTaskIF asynOutputMessageTask = null;
		
		
		/**
		 * 생성자
		 * @param projectName 프로젝트 이름
		 * @param asynOutputMessageQueue 서버 익명 출력 메시지 큐
		 */
		public AsynOutputMessageExecutorThread() {
			// this.serverOutputMessageQueue= serverOutputMessageQueue;			
			this.asynOutputMessageTask = new DefaultAsynOutputMessageTask();
		}

		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					OutputMessage outObj = asynOutputMessageQueue.take();
					//synchronized (monitor) {
						asynOutputMessageTask.doTask(projectConfig, outObj);
					//}
				}
				
				log.info(String.format("client project[%s] AnonymousServerMessageProcessorThread loop exit", projectConfig.getProjectName()));
				log.warn("Thread loop exit");
			} catch (InterruptedException e) {
				log.warn(String.format("client project[%s] AnonymousServerMessageProcessorThread interrupt", projectConfig.getProjectName()), e);
			} catch (Exception e) {
				log.warn(String.format("client project[%s] AnonymousServerMessageProcessorThread unknow error", projectConfig.getProjectName()), e);
			}
        }
		
		/**
		 * 새로운 서버 익명 메시지 비지니스 로직으로 교체를 한다.
		 * @param newAnonymousServerMessageTask 새로운 서버 익명 메시지 비지니스 로직
		 */
		public void changeAsynOutputMessageTask(AsynOutputMessageTaskIF newAsynOutputMessageTask) {
			if (null == newAsynOutputMessageTask) {
				String errorMessage = "parameter newAsynOutputMessageTask is null";
				IllegalArgumentException e = new IllegalArgumentException(errorMessage);
				log.warn("IllegalArgumentException", e);
				throw e;
			}
			//synchronized (monitor) {
				asynOutputMessageTask = newAsynOutputMessageTask;
			//}
		}
	}
	
	
	@Override
	public void changeAsynOutputMessageTask(AsynOutputMessageTaskIF newAsynOutputMessageTask) {
		/**
		 * asynOutputMessageExecutorThread 는 비동기일때만 초기화 되므로 동기일때에는 null 값이다.
		 * 따라서 null 값이면 비동기이므로 무시한다.
		 */
		if (null == asynOutputMessageExecutorThreadList) return;
		
		for (int i=0; i < asynOutputMessageExecutorThreadList.length; i++) {
			asynOutputMessageExecutorThreadList[i].changeAsynOutputMessageTask(newAsynOutputMessageTask);
		}
	}
	
	/**
	 * 서버 비동기 출력 메시지 처리자 쓰레드 종료
	 */
	public void stopAsynOutputMessageExecutorThread() {
		if (null != asynOutputMessageExecutorThreadList) {
			for (int i=0; i < asynOutputMessageExecutorThreadList.length; i++) {
				asynOutputMessageExecutorThreadList[i].interrupt();
			}
		}
	}
	
	// FIXME!
	private class ClientProjectMonitor extends Thread implements CommonRootIF {
		private long monitorInterval;
		private long requestTimeout;
		
		public ClientProjectMonitor(long monitorInterval, long requestTimeout) {
			this.monitorInterval = monitorInterval;
			this.requestTimeout = requestTimeout;
		}
		
		@Override
		public void run() {
			ArrayList<AbstractConnection>  list = connectionPool.getConnectionList();
			int listSize = list.size();
			try {
				while (!Thread.currentThread().isInterrupted()) {	
					log.info(getInfo().toString());
					
					long currentTime = System.currentTimeMillis();
					
					for (int i=0; i < listSize; i++) {
						AbstractConnection conn = list.get(i);
						
						java.util.Date finalReadTime = conn.getFinalReadTime();
						if (null == finalReadTime) continue;
						
						if ((finalReadTime.getTime() - currentTime) > requestTimeout) {
							log.info(String.format("project[%s] requestTimeout[%d] over so socket close, conn[%s]", projectConfig.getProjectName(), requestTimeout, conn.toString()));
							conn.serverClose();
						}
					}
					
					Thread.sleep(monitorInterval);
				}
				
				log.warn(String.format("client project[%s] ClientProjectMonitor loop exit", projectConfig.getProjectName()));
			} catch (InterruptedException e) {
				log.warn(String.format("client project[%s] ClientProjectMonitor interrupt", projectConfig.getProjectName()), e);
			} catch (Exception e) {
				log.warn(String.format("client project[%s] ClientProjectMonitor unknow error", projectConfig.getProjectName()), e);
			}
		}
	}
	
	public void stopMonitor() {
		if (null != clientProjectMonitor) clientProjectMonitor.interrupt();
	}
	
	public void stop() {
		// FIXME!
		log.info(String.format("project[%s] client project stop", projectConfig.getProjectName()));
		
		stopAsynPool();
		stopAsynOutputMessageExecutorThread();
		stopMonitor();
	}
	
	@Override
	public MessageProtocolIF getMessageExchangeProtocol() {
		return messageExchangeProtocol;
	}
}

