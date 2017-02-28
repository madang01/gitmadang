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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.AbstractConnectionPool;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.project.DataPacketBufferQueueManagerIF;
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
	private boolean isFailToGetConnection = false;
	
	/**
	 * 생성자
	 * @param connectionPoolSize 연결 폴 크기
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
			Charset charsetOfProject, int connectionPoolSize, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			MessageProtocolIF messageProtocol,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager)
			throws NoMoreDataPacketBufferException, InterruptedException {
		super(null);

		// log.info("create new SingleBlockConnectionPool");

		this.connectionPoolSize = connectionPoolSize;

		connectionQueue = new LinkedBlockingQueue<NoShareSyncConnection>(
				connectionPoolSize);

		
		for (int i = 0; i < connectionPoolSize; i++) {
			NoShareSyncConnection serverConnection = new NoShareSyncConnection(
					projectName,
					i, hostOfProject, portOfProject, charsetOfProject, 
					socketTimeOut, whetherToAutoConnect, 
					serverOutputMessageQueue, messageProtocol, 
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
			DynamicClassCallException, ServerTaskException, NotLoginException {
		NoShareSyncConnection conn = null;
		// synchronized (monitor) {
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
				log.error("인터럽트 받아 후속 처리중 발생", e1);
				System.exit(1);
			}
		}
		// }

		AbstractMessage retMessage = null;
		try {
			retMessage = conn.sendSyncInputMessage(inputMessage);
		} finally {
			// synchronized (monitor) {
			try {
				conn.queueIn();
				connectionQueue.put(conn);
			} catch (InterruptedException e) {
				log.error("발생할 이유 없음 원인 제거 필요함", e);
			}
			// }
		}
		return retMessage;
	}
	
	@Override
	public AbstractConnection getConnection() throws InterruptedException, NotSupportedException {
		NoShareSyncConnection conn = connectionQueue.poll();
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
		
		NoShareSyncConnection serverConnection = (NoShareSyncConnection)conn;
		synchronized (monitor) {
			if (serverConnection.isInQueue()) return;
			serverConnection.queueIn();
		}
		connectionQueue.offer(serverConnection);
	}
	
	@Override
	public List<AbstractConnection> getConnectionList() {
		return Collections.unmodifiableList(connectionList);
		/*ArrayList<AbstractConnection>  list = new ArrayList<AbstractConnection>();
		
		int connectionListSize = connectionList.size();
		for (int i = 0; i < connectionListSize; i++) {
			AbstractConnection conn = connectionList.get(i);
			list.add(conn);
		}
		
		return list;*/
	}
}
