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

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.ConnectionPoolIF;
import kr.pe.sinnori.client.connection.ConnectionPoolManagerIF;
import kr.pe.sinnori.client.connection.SocketResoruceIF;
import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;

/**
 * 클라이언트 비공유 방식의 동기 연결 클래스 {@link NoShareSyncConnection} 를 원소로 가지는 폴 관리자
 * 클래스<br/>
 * 다른 쓰레드간에 연결 클래스를 공유 시키지 않기 위해서 폴 원소는 큐로 관리한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class NoShareSyncConnectionPool implements ConnectionPoolIF {
	private Logger log = LoggerFactory.getLogger(NoShareSyncConnectionPool.class);
	private final Object monitor = new Object();

	private String projectName = null;
	private String host = null;
	private int port;
	private int connectionPoolSize;
	private int connectionPoolMaxSize;
	private long socketTimeOut;
	private int dataPacketBufferMaxCntPerMessage;
	private CharsetDecoder streamCharsetDecoder = null;
	private MessageProtocolIF messageProtocol = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ClientObjectCacheManagerIF clientObjectCacheManager = null;

	private LinkedBlockingQueue<NoShareSyncConnection> connectionQueue = null;
	private transient int numberOfConnection = 0;
	private ConnectionPoolManagerIF poolManager = null;

	public NoShareSyncConnectionPool(String projectName, String host, int port, int connectionPoolSize,
			int connectionPoolMaxSize, long socketTimeOut, int dataPacketBufferMaxCntPerMessage,
			CharsetDecoder streamCharsetDecoder, MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferPool, ClientObjectCacheManagerIF clientObjectCacheManager)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException, ConnectionPoolException {
		super();

		// log.info("create new SingleBlockConnectionPool");

		this.projectName = projectName;
		this.host = host;
		this.port = port;
		this.connectionPoolSize = connectionPoolSize;
		this.connectionPoolMaxSize = connectionPoolMaxSize;
		this.socketTimeOut = socketTimeOut;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.clientObjectCacheManager = clientObjectCacheManager;

		connectionQueue = new LinkedBlockingQueue<NoShareSyncConnection>(connectionPoolMaxSize);

		try {
			for (int i = 0; i < connectionPoolSize; i++) {
				addConnection();
			}
		} catch (IOException e) {
			while (!connectionQueue.isEmpty()) {
				NoShareSyncConnection conn = connectionQueue.poll();
				try {
					conn.close();
				} catch (IOException e1) {
				}
				conn.doReleaseSocketResources();

			}
			throw e;
		}
	}

	public void addConnection()
			throws InterruptedException, NoMoreDataPacketBufferException, IOException, ConnectionPoolException {
		synchronized (monitor) {
			if (numberOfConnection >= connectionPoolMaxSize) {
				throw new ConnectionPoolException("fail to add a connection because this connection pool is full");
			}

			SocketOutputStream socketOutputStream = new SocketOutputStream(streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);

			SocketResoruceIF syncPrivateSocketResoruce = new SyncPrivateSocketResource(socketOutputStream);

			NoShareSyncConnection conn = new NoShareSyncConnection(projectName, host, port, socketTimeOut,
					syncPrivateSocketResoruce, dataPacketBufferPool, messageProtocol, clientObjectCacheManager);

			connectionQueue.add(conn);
			numberOfConnection++;
			
			connectionPoolSize = Math.max(numberOfConnection, connectionPoolSize);
		}
	}

	/*
	 * public int getNumberOfConnection() { return numberOfConnection; }
	 */

	/*
	 * public int getConnectionPoolSize() { return connectionPoolSize; }
	 */
	/*
	 * public int getConnectionPoolMaxSize() { return connectionPoolMaxSize; }
	 */

	public boolean whetherConnectionIsMissing() {
		return (numberOfConnection != connectionPoolSize);
	}

	public AbstractConnection getConnection() throws InterruptedException, SocketTimeoutException, ConnectionPoolException {

		NoShareSyncConnection syncPrivateConnection = null;
		boolean loop = false;
		
		synchronized (monitor) {			
			do {
				if (0 == numberOfConnection) {
					throw new ConnectionPoolException("check server alive");
				}
				
				syncPrivateConnection = connectionQueue.poll(socketTimeOut, TimeUnit.MILLISECONDS);
				if (null == syncPrivateConnection) {
					throw new SocketTimeoutException("no share synchronized connection pool timeout");
				}
				syncPrivateConnection.queueOut();

				if (syncPrivateConnection.isConnected()) {
					loop = false;
				} else {
					loop = true;
					
					String reasonForLoss = new StringBuilder("폴에서 꺼낸 동기 비공유 연결[")
							.append(syncPrivateConnection.hashCode()).append("]이 닫혀있어 폐기").toString();

					numberOfConnection--;

					log.warn("{}, 총 연결수[{}]", reasonForLoss, numberOfConnection);

					poolManager.notice(reasonForLoss);		
				}
			} while (loop);

			return syncPrivateConnection;
		}
	}

	public void release(AbstractConnection conn) throws ConnectionPoolException {
		if (null == conn) {
			String errorMessage = "the parameter conn is null";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		if (!(conn instanceof NoShareSyncConnection)) {
			String errorMessage = "the parameter conn is not instace of NoShareSyncConnection class";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		NoShareSyncConnection syncPrivateConnection = (NoShareSyncConnection) conn;
		synchronized (monitor) {
			/**
			 * 연속 2회 큐 입력 방지
			 */
			if (syncPrivateConnection.isInQueue()) {
				String errorMessage = String.format(
						"the paramter conn[%d] that is NoShareSyncConnection  allready was in connection queue",
						conn.hashCode());
				log.warn(errorMessage, new Throwable());
				throw new ConnectionPoolException(errorMessage);
			}

			if (!syncPrivateConnection.isConnected()) {
				numberOfConnection--;

				String reasonForLoss = new StringBuilder("반환된 동기 비공유 연결[").append(syncPrivateConnection.hashCode())
						.append("]이 닫혀있어 폐기").toString();

				log.warn("{}, 총 연결수[{}]", reasonForLoss, numberOfConnection);

				poolManager.notice(reasonForLoss);
				return;
			}

			syncPrivateConnection.queueIn();
			boolean isSuccess = connectionQueue.offer(syncPrivateConnection);
			if (!isSuccess) {
				log.error("fail to offer NoShareSyncConnection[{}] to queue, you need to check and fix bug",
						conn.hashCode());
				System.exit(1);
			}
		}
	}

	@Override
	public void registerPoolManager(ConnectionPoolManagerIF poolManager) {
		this.poolManager = poolManager;
	}

}
