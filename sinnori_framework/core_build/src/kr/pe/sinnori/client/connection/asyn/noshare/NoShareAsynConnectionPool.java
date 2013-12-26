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
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.AbstractConnectionPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPoolIF;
import kr.pe.sinnori.client.io.LetterFromServer;
import kr.pe.sinnori.client.io.LetterToServer;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.OutputMessageQueueQueueMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * 클라이언트 비공유 방식의 비동기 연결 클래스 {@link NoShareAsynConnection} 를 원소로 가지는 폴 관리자 클래스<br/>
 * 다른 쓰레드간에 연결 클래스를 공유 시키지 않기 위해서 폴 원소는 큐로 관리한다.
 * 
 * @author Jonghoon Won
 * 
 */
public class NoShareAsynConnectionPool extends AbstractConnectionPool {
	/** Connection Pool 운영을 위한 변수 */
	private LinkedBlockingQueue<NoShareAsynConnection> connectionQueue = null;
	private ArrayList<NoShareAsynConnection> connectionList = new ArrayList<NoShareAsynConnection>();
	private int connectionPoolSize;
	private boolean isFailToGetConnection = false;

	
	/**
	 * 
	 * @param connectionPoolSize 연결 공통 데이터
	 * @param socketTimeOut 소켓 타임 아웃 시간
	 * @param whetherToAutoConnect 자동 접속 여부
	 * @param finishConnectMaxCall 비동기 방식에서 연결 확립 시도 최대 호출 횟수
	 * @param finishConnectWaittingTime 비동기 연결 확립 시도 간격
	 * @param commonProjectInfo 공통 프로젝트 정보
	 * @param serverOutputMessageQueue 서버에서 보내는 불특정 출력 메시지를 받는 큐
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param outputMessageQueueQueueManger 출력 메시지 큐를 원소로 가지는 큐 관리자
	 * @param outputMessageReaderPool 서버에 접속한 소켓 채널을 균등하게 소켓 읽기 담당 쓰레드에 등록하기 위한 인터페이스
	 * @param messageManger 메시지 관리자
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 할당 받지 못했을 경우 던지는 예외
	 * @throws InterruptedException 쓰레드 인터럽트
	 * @throws NoMoreOutputMessageQueueException 출력 메시지 큐 부족시 실패시 던지는 예외
	 */
	public NoShareAsynConnectionPool(
			int connectionPoolSize, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			int finishConnectMaxCall,
			long finishConnectWaittingTime,
			CommonProjectInfo commonProjectInfo,
			LinkedBlockingQueue<OutputMessage> serverOutputMessageQueue,
			LinkedBlockingQueue<LetterToServer> inputMessageQueue,
			OutputMessageQueueQueueMangerIF outputMessageQueueQueueManger, 
			OutputMessageReaderPoolIF outputMessageReaderPool,
			MessageMangerIF messageManger,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager)
			throws NoMoreDataPacketBufferException, InterruptedException, NoMoreOutputMessageQueueException {
		super(commonProjectInfo, serverOutputMessageQueue);
		
		this.connectionPoolSize = connectionPoolSize;
		
		connectionQueue = new LinkedBlockingQueue<NoShareAsynConnection>(
				connectionPoolSize);

		/**
		 * 비동기 비 공유 연결 클래스는 입력 메시지 큐 1개와 출력 메시지큐 1개를 할당 받는다.
		 * 입력 메시지 큐는 모든 연결 클래스간에 공유하며, 출력 메시지 큐는 연결 클래스 각각 존재한다.
		 */
		for (int i = 0; i < connectionPoolSize; i++) {
			NoShareAsynConnection serverConnection = new NoShareAsynConnection(
					i, socketTimeOut, whetherToAutoConnect, 
					finishConnectMaxCall, finishConnectWaittingTime, commonProjectInfo, 
					serverOutputMessageQueue, inputMessageQueue, outputMessageQueueQueueManger,
					outputMessageReaderPool, messageManger, 
					dataPacketBufferQueueManager);
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
	public LetterFromServer sendInputMessage(InputMessage inputMessage)
			throws ServerNotReadyException, SocketTimeoutException,
			NoMoreDataPacketBufferException, BodyFormatException, MessageInfoNotFoundException {
		NoShareAsynConnection conn = null;

		/** 쓰레드 간에 공유를 막기 위해 queueOut 사용*/
		try {
			conn = connectionQueue.poll();
			if (null == conn) {
				if (!isFailToGetConnection) {
					isFailToGetConnection = true;
					log.warn("WARNING::connection queue empty");
				}
				conn = connectionQueue.take();
			}
			
			conn.queueOut();
		} catch (InterruptedException e) {
			try {
				conn = connectionQueue.take();
				conn.queueOut();
			} catch (InterruptedException e1) {
				log.fatal("인터럽트 받아 후속 처리중 발생", e1);
				System.exit(1);
			}
		}

		LetterFromServer retLetterList = null;
		try {
			retLetterList = conn.sendInputMessage(inputMessage);
		} finally {
			conn.queueIn();
			connectionQueue.offer(conn);
		}

		return retLetterList;
	}	
	
	@Override
	public AbstractConnection getConnection() throws InterruptedException, NotSupportedException {
		NoShareAsynConnection conn = connectionQueue.poll();
		if (null == conn) {
			if (!isFailToGetConnection) {
				isFailToGetConnection = true;
				log.warn("WARNING::connection queue empty");
			}
			conn = connectionQueue.take();
		}
		
		synchronized (monitor) {
			conn.queueOut();
			return conn;
		}
	}
	
	@Override
	public void freeConnection(AbstractConnection conn) throws NotSupportedException {
		if (null == conn) return;

		NoShareAsynConnection serverConnection = (NoShareAsynConnection)conn;
		
		synchronized (monitor) {
			if (serverConnection.isInQueue()) return;
			serverConnection.queueIn();
		}
		
		connectionQueue.offer(serverConnection);
	}

	@Override
	public final ArrayList<AbstractConnection> getConnectionList() {
		ArrayList<AbstractConnection>  list = new ArrayList<AbstractConnection>();
		int connectionListSize = connectionList.size();
		
		for (int i = 0; i < connectionListSize; i++) {
			AbstractConnection conn = connectionList.get(i);
			list.add(conn);
		}
		
		return list;
	}
}
