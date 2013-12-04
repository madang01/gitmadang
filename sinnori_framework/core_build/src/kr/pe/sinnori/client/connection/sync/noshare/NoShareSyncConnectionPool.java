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
package kr.pe.sinnori.client.connection.sync.noshare;

import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.AbstractConnectionPool;
import kr.pe.sinnori.client.io.LetterFromServer;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.io.MessageExchangeProtocolIF;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;

/**
 * 클라이언트 비공유 방식의 동기 연결 클래스 {@link NoShareSyncConnection} 를 원소로 가지는 폴 관리자 클래스<br/>
 * 다른 쓰레드간에 연결 클래스를 공유 시키지 않기 위해서 폴 원소는 큐로 관리한다.
 * 
 * @author Jonghoon Won
 * 
 */
public class NoShareSyncConnectionPool extends AbstractConnectionPool {
	/** Connection Pool 운영을 위한 변수 */
	private LinkedBlockingQueue<NoShareSyncConnection> connectionQueue = null;
	private int connectionPoolSize;
	
	/** 소켓 쓰기 랩 버퍼 목록 */
	// private ArrayList<WrapBuffer> inputMessageWriteBufferList = new ArrayList<WrapBuffer>();

	/**
	 * 생성자
	 * @param connectionPoolSize 연결 폴 크기
	 * @param socketTimeOut 소켓 타임 아웃
	 * @param whetherToAutoConnect 자동 연결 여부
	 * @param commonProjectInfo 공통 프로젝트 정보
	 * @Param messageProtocol 메시지 교환 프로토콜
	 * @param messageManger 메시지 관리자
	 * @param dataPacketBufferQueueManager 데이터 패킷 큐 관리자
	 * @throws NoMoreDataPacketBufferException 데이터 패킷을 할당 받지 못할을 경우 던지는 예외
	 * @throws InterruptedException 쓰레드 인터럽트
	 */
	public NoShareSyncConnectionPool(int connectionPoolSize, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			CommonProjectInfo commonProjectInfo,
			MessageExchangeProtocolIF messageProtocol,
			MessageMangerIF messageManger, 
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager)
			throws NoMoreDataPacketBufferException, InterruptedException {
		super(commonProjectInfo, null);

		// log.info("create new SingleBlockConnectionPool");

		this.connectionPoolSize = connectionPoolSize;

		connectionQueue = new LinkedBlockingQueue<NoShareSyncConnection>(
				connectionPoolSize);

		
		for (int i = 0; i < connectionPoolSize; i++) {
			NoShareSyncConnection serverConnection = new NoShareSyncConnection(
					i, socketTimeOut, whetherToAutoConnect, commonProjectInfo, 
					serverOutputMessageQueue, messageProtocol, messageManger, 
					dataPacketBufferQueueManager);
			connectionQueue.add(serverConnection);
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
		NoShareSyncConnection serverConnection = null;
		// synchronized (monitor) {
		try {
			serverConnection = connectionQueue.take();
			serverConnection.queueOut();
		} catch (InterruptedException e) {
			try {
				serverConnection = connectionQueue.take();
				serverConnection.queueOut();
			} catch (InterruptedException e1) {
				log.fatal("인터럽트 받아 후속 처리중 발생", e1);
				System.exit(1);
			}
		}
		// }

		LetterFromServer retLetterList = null;
		try {
			retLetterList = serverConnection.sendInputMessage(inputMessage);
		} finally {
			// synchronized (monitor) {
			try {
				serverConnection.queueIn();
				connectionQueue.put(serverConnection);
			} catch (InterruptedException e) {
				log.fatal("발생할 이유 없음 원인 제거 필요함", e);
			}
			// }
		}
		return retLetterList;
	}
	
}
