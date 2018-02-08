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
package kr.pe.sinnori.client.connection.asyn.noshare;

import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.ClientOutputMessageQueueQueueMangerIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.AbstractConnectionPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.AsynServerAdderIF;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.ConnectionPoolTimeoutException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

/**
 * 클라이언트 비공유 방식의 비동기 연결 클래스 {@link NoShareAsynConnection} 를 원소로 가지는 폴 관리자 클래스<br/>
 * 다른 쓰레드간에 연결 클래스를 공유 시키지 않기 위해서 폴 원소는 큐로 관리한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class NoShareAsynConnectionPool extends AbstractConnectionPool {
	
	
	/** Connection Pool 운영을 위한 변수 */
	private LinkedBlockingQueue<NoShareAsynConnection> connectionQueue = null;
	private ArrayList<NoShareAsynConnection> connectionList = new ArrayList<NoShareAsynConnection>();
	private int connectionPoolSize;
	private long connectionTimeout;
	

	
	/**
	 * 
	 * @param connectionPoolSize 연결 공통 데이터
	 * @param socketTimeOut 소켓 타임 아웃 시간
	 * @param whetherToAutoConnect 자동 접속 여부
	 * @param finishConnectMaxCall 비동기 방식에서 연결 확립 시도 최대 호출 횟수
	 * @param finishConnectWaittingTime 비동기 연결 확립 시도 간격
	 * @param clientProjectConfig 프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스
	 * @param asynOutputMessageQueue 서버에서 보내는 불특정 출력 메시지를 받는 큐
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param syncOutputMessageQueueQueueManger 출력 메시지 큐를 원소로 가지는 큐 관리자
	 * @param outputMessageReaderPool 서버에 접속한 소켓 채널을 균등하게 소켓 읽기 담당 쓰레드에 등록하기 위한 인터페이스
	 * @param messageManger 메시지 관리자
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 할당 받지 못했을 경우 던지는 예외
	 * @throws InterruptedException 쓰레드 인터럽트
	 * @throws NoMoreOutputMessageQueueException 출력 메시지 큐 부족시 실패시 던지는 예외
	 */
	public NoShareAsynConnectionPool(
			String projectName,
			String hostOfProject,
			int portOfProject,
			Charset charsetOfProject,
			int connectionPoolSize, long connectionTimeout,			
			long socketTimeOut,
			boolean whetherToAutoConnect,
			int finishConnectMaxCall,
			long finishConnectWaittingTime,
			LinkedBlockingQueue<WrapReadableMiddleObject> asynOutputMessageQueue,
			LinkedBlockingQueue<ToLetter> inputMessageQueue,
			MessageProtocolIF messageProtocol,
			AsynServerAdderIF outputMessageReaderPool,
			ClientOutputMessageQueueQueueMangerIF syncOutputMessageQueueQueueManger,
			int dataPacketBufferMaxCntPerMessage,
			DataPacketBufferPoolIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager)
			throws NoMoreDataPacketBufferException, InterruptedException, NoMoreOutputMessageQueueException {
		super(asynOutputMessageQueue);
		
		this.connectionPoolSize = connectionPoolSize;
		this.connectionTimeout = connectionTimeout;
		
		connectionQueue = new LinkedBlockingQueue<NoShareAsynConnection>(
				connectionPoolSize);

		/**
		 * 비동기 비 공유 연결 클래스는 입력 메시지 큐 1개와 출력 메시지큐 1개를 할당 받는다.
		 * 입력 메시지 큐는 모든 연결 클래스간에 공유하며, 출력 메시지 큐는 연결 클래스 각각 존재한다.
		 */
		for (int i = 0; i < connectionPoolSize; i++) {
			NoShareAsynConnection serverConnection = new NoShareAsynConnection(
					projectName,
					i, hostOfProject, portOfProject, charsetOfProject, socketTimeOut, whetherToAutoConnect, 
					finishConnectMaxCall, finishConnectWaittingTime,  
					asynOutputMessageQueue, inputMessageQueue,
					messageProtocol,
					outputMessageReaderPool,
					syncOutputMessageQueueQueueManger,
					dataPacketBufferMaxCntPerMessage,
					dataPacketBufferQueueManager, clientObjectCacheManager);
			connectionQueue.add(serverConnection);
			connectionList.add(serverConnection);
		}
	}
	
	@Override
	public int getUsedMailboxCnt() {
		return connectionQueue.remainingCapacity();
	}

	@Override
	public int getTotalMailbox() {
		return connectionPoolSize;
	}
	

	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws ServerNotReadyException, SocketTimeoutException,
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, AccessDeniedException, ConnectionPoolTimeoutException, InterruptedException {
		NoShareAsynConnection conn = null;

		/** 쓰레드 간에 공유를 막기 위해 queueOut 사용*/		
		conn = connectionQueue.poll(connectionTimeout, TimeUnit.MICROSECONDS);		

		if (null == conn) {
			throw new ConnectionPoolTimeoutException("no share synchronized connection pool timeout");
		}
		
		conn.queueOut();
		
		AbstractMessage outObj = null;
		try {
			outObj = conn.sendSyncInputMessage(inputMessage);
		} finally {
			conn.queueIn();
			boolean isSuccess = connectionQueue.offer(conn);
			if (!isSuccess) {
				log.error("fail to offer connection[{}] to connection queue becase of bug, you need to check and fix bug", conn.hashCode());
				System.exit(1);
			}
		}

		return outObj;
	}	
	
	@Override
	public AbstractConnection getConnection() throws InterruptedException, NotSupportedException, ConnectionPoolTimeoutException {
		
		synchronized (monitor) {
			NoShareAsynConnection conn = connectionQueue.poll(connectionTimeout, TimeUnit.MILLISECONDS);
			if (null == conn) {
				throw new ConnectionPoolTimeoutException("no share synchronized connection pool timeout");
			}
			conn.queueOut();
			return conn;
		}
	}
	
	@Override
	public void release(AbstractConnection conn) throws NotSupportedException {
		if (null == conn) {
			String errorMessage = "the parameter conn is null";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! (conn instanceof NoShareAsynConnection)) {
			String errorMessage = "the parameter conn is not instace of NoShareAsynConnection class";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		NoShareAsynConnection serverConnection = (NoShareAsynConnection)conn;
		
		synchronized (monitor) {
			/**
			 * 연속 2회 큐 입력 방지
			 */
			if (serverConnection.isInQueue()) {
				String errorMessage = String.format("the paramter conn[%d] allready was in connection queue", conn.hashCode());
				log.warn(errorMessage, new Throwable());
				return;
			}
			serverConnection.queueIn();
			boolean isSuccess = connectionQueue.offer(serverConnection);
			if (!isSuccess) {
				log.error("fail to offer NoShareAsynConnection[{}] to connection queue becase of bug, you need to check and fix bug", conn.hashCode());
				System.exit(1);
			}
		}
	}

	@Override
	public final List<AbstractConnection> getConnectionList() {
		List<AbstractConnection> dupList = new ArrayList<AbstractConnection>();
		for (NoShareAsynConnection conn : connectionList) {
			dupList.add(conn);
		}
		return dupList;
	}
}
