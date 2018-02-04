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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.AbstractConnectionPool;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.ConnectionPoolTimeoutException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;

/**
 * 클라이언트 비공유 방식의 동기 연결 클래스 {@link NoShareSyncConnection} 를 원소로 가지는 폴 관리자 클래스<br/>
 * 다른 쓰레드간에 연결 클래스를 공유 시키지 않기 위해서 폴 원소는 큐로 관리한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class NoShareSyncConnectionPool extends AbstractConnectionPool {
	/** Connection Pool 운영을 위한 변수 */
	private LinkedBlockingQueue<NoShareSyncConnection> connectionQueue = null;
	private ArrayList<NoShareSyncConnection> connectionList = new ArrayList<NoShareSyncConnection>();
	private int connectionPoolSize;
	private long connectionTimeout;
	
	/**
	 * 생성자
	 * @param connectionPoolSize 연결 폴 크기
	 * @param connectionTimeout 비공유 연결 타임 아웃
	 * @param socketTimeOut 소켓 타임 아웃
	 * @param whetherToAutoConnect 자동 연결 여부
	 * @param projectPart 프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스
	 * @param messageProtocol 메시지 교환 프로토콜
	 * @param dataPacketBufferQueueManager 데이터 패킷 큐 관리자
	 * @throws NoMoreDataPacketBufferException 데이터 패킷을 할당 받지 못할을 경우 던지는 예외
	 * @throws InterruptedException 쓰레드 인터럽트
	 */
	public NoShareSyncConnectionPool(String projectName,
			String hostOfProject,
			int portOfProject,
			Charset charsetOfProject, int connectionPoolSize, long connectionTimeout, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			MessageProtocolIF messageProtocol,
			int dataPacketBufferMaxCntPerMessage,
			DataPacketBufferPoolIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager)
			throws NoMoreDataPacketBufferException, InterruptedException {
		super(null);

		// log.info("create new SingleBlockConnectionPool");

		this.connectionPoolSize = connectionPoolSize;
		this.connectionTimeout = connectionTimeout;

		connectionQueue = new LinkedBlockingQueue<NoShareSyncConnection>(
				connectionPoolSize);

		
		for (int i = 0; i < connectionPoolSize; i++) {
			NoShareSyncConnection serverConnection = new NoShareSyncConnection(
					projectName,
					i, hostOfProject, portOfProject, charsetOfProject, 
					socketTimeOut, whetherToAutoConnect, 
					serverOutputMessageQueue, messageProtocol, 
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
			DynamicClassCallException, ServerTaskException, NotLoginException, ConnectionPoolTimeoutException, InterruptedException {
		NoShareSyncConnection conn = null;
		
		conn = connectionQueue.poll(connectionTimeout, TimeUnit.MILLISECONDS);
		if (null == conn) {
			throw new ConnectionPoolTimeoutException("no share synchronized connection pool timeout");
		}
		
		conn.queueOut();

		AbstractMessage retMessage = null;
		try {
			retMessage = conn.sendSyncInputMessage(inputMessage);
		} finally {
			
			conn.queueIn();
			boolean isSuccess = connectionQueue.offer(conn);
			if (!isSuccess) {
				log.error("fail to put NoShareSyncConnection[{}] in the connection queue becase of bug, you need to check and fix bug", conn.hashCode());
				System.exit(1);
			}
			
		}
		return retMessage;
	}
	
	@Override
	public AbstractConnection getConnection() throws InterruptedException, NotSupportedException, ConnectionPoolTimeoutException {
		
		
		synchronized (monitor) {
			NoShareSyncConnection conn = connectionQueue.poll(connectionTimeout, TimeUnit.MILLISECONDS);
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
		
		if (! (conn instanceof NoShareSyncConnection)) {
			String errorMessage = "the parameter conn is not instace of NoShareSyncConnection class";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}
		
		NoShareSyncConnection serverConnection = (NoShareSyncConnection)conn;
		synchronized (monitor) {
			/**
			 * 연속 2회 큐 입력 방지
			 */
			if (serverConnection.isInQueue()) {
				String errorMessage = String.format("the paramter conn[%d] that is NoShareSyncConnection  allready was in connection queue", conn.hashCode());
				log.warn(errorMessage, new Throwable());
				return;
			}
			serverConnection.queueIn();
			boolean isSuccess = connectionQueue.offer(serverConnection);
			if (!isSuccess) {
				log.error("fail to offer NoShareSyncConnection[{}] to connection queue becase of bug, you need to check and fix bug", conn.hashCode());
				System.exit(1);
			}
		}
	}
	
	@Override
	public List<AbstractConnection> getConnectionList() {
		List<AbstractConnection> dupList = new ArrayList<AbstractConnection>();
		for (NoShareSyncConnection conn : connectionList) {
			dupList.add(conn);
		}
		return dupList;
	}
}
