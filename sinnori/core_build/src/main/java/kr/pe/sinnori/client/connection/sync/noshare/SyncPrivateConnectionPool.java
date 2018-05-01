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
import java.util.ArrayDeque;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.ConnectionFixedParameter;
import kr.pe.sinnori.client.connection.ConnectionPoolIF;
import kr.pe.sinnori.client.connection.ConnectionPoolSupporterIF;
import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

public class SyncPrivateConnectionPool implements ConnectionPoolIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(SyncPrivateConnectionPool.class);
	private final Object monitor = new Object();

	private transient int poolSize;
	private int poolMaxSize;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;
	private SyncPrivateSocketResourceFactoryIF syncPrivateSocketResourceFactory = null;

	private String projectName = null;
	private long socketTimeOut;

	private ConnectionFixedParameter connectionFixedParameter = null;

	private ArrayDeque<SyncPrivateConnection> connectionQueue = null;
	private transient int numberOfConnection = 0;

	public SyncPrivateConnectionPool(SyncPrivateConnectionPoolParameter syncPrivateConnectionPoolParameter,
			ConnectionFixedParameter connectionFixedParameter)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException, ConnectionPoolException {
		if (null == syncPrivateConnectionPoolParameter) {
			String errorMessage = "the parameter syncPrivateConnectionPoolParameter is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == connectionFixedParameter) {
			throw new IllegalArgumentException("the parameter connectionFixedParameter is null");
		}

		this.connectionFixedParameter = connectionFixedParameter;

		poolSize = syncPrivateConnectionPoolParameter.getPoolSize();
		poolMaxSize = syncPrivateConnectionPoolParameter.getPoolMaxSize();
		connectionPoolSupporter = syncPrivateConnectionPoolParameter.getConnectionPoolSupporter();
		syncPrivateSocketResourceFactory = syncPrivateConnectionPoolParameter.getSyncPrivateSocketResourceFactory();

		projectName = connectionFixedParameter.getProjectName();
		socketTimeOut = connectionFixedParameter.getSocketTimeOut();

		connectionQueue = new ArrayDeque<SyncPrivateConnection>(poolMaxSize);

		try {
			for (int i = 0; i < poolSize; i++) {
				addConnection();
			}
		} catch (Exception e) {
			while (!connectionQueue.isEmpty()) {
				try {
					connectionQueue.removeFirst().close();
				} catch (IOException e1) {
				}
			}
			throw e;
		}

		connectionPoolSupporter.registerPool(this);
		connectionPoolSupporter.start();
	}

	private void addConnection()
			throws InterruptedException, NoMoreDataPacketBufferException, IOException, ConnectionPoolException {

		if (numberOfConnection >= poolMaxSize) {
			throw new ConnectionPoolException("fail to add a connection because this connection pool is full");
		}

		SyncPrivateSocketResource syncPrivateSocketResource = syncPrivateSocketResourceFactory
				.makeNewSyncPrivateSocketResource();

		SyncPrivateConnection conn = null;
		
		try {
			conn = new SyncPrivateConnection(connectionFixedParameter, syncPrivateSocketResource);
		} catch(Exception e) {
			syncPrivateSocketResource.releaseSocketResources();
			throw e;
		}

		// synchronized (monitor) {
		connectionQueue.addLast(conn);
		numberOfConnection++;
		poolSize = Math.max(numberOfConnection, poolSize);
		// }

		log.debug("{} new SyncPrivateConnection[{}] added", projectName, conn.hashCode());
	}

	private boolean whetherConnectionIsMissing() {
		return (numberOfConnection != poolSize);
	}

	public void addAllLostConnections() throws InterruptedException {
		synchronized (monitor) {
			while (whetherConnectionIsMissing()) {
				try {
					addConnection();
				} catch (InterruptedException e) {
					String errorMessage = new StringBuilder(projectName).append(" 인터럽트 발생에 따른 결손된 연결 추가 작업 중지")
							.toString();
					log.warn(errorMessage, e);
					throw e;
				} catch (Exception e) {
					String errorMessage = new StringBuilder(projectName).append(" 에러 발생에 따른 결손된 연결 추가 작업 중지, errmsg={}")
							.append(e.getMessage()).toString();
					log.warn(errorMessage, e);
					break;
				}
			}
		}
	}

	public AbstractConnection getConnection()
			throws InterruptedException, SocketTimeoutException, ConnectionPoolException {

		SyncPrivateConnection syncPrivateConnection = null;
		boolean loop = false;

		long currentSocketTimeOut = socketTimeOut;
		long startTime = System.currentTimeMillis();
		
		synchronized (monitor) {
			do {
				if (0 == numberOfConnection) {
					connectionPoolSupporter.notice("no more connection");
					throw new ConnectionPoolException("check server is alive or something is bad");
				}				

				syncPrivateConnection = connectionQueue.peekFirst();
				if (null == syncPrivateConnection) {
					monitor.wait(currentSocketTimeOut);
					syncPrivateConnection = connectionQueue.peekFirst();
					if (null == syncPrivateConnection) {
						throw new SocketTimeoutException("1.synchronized private connection pool timeout");
					}
				}

				if (syncPrivateConnection.isConnected()) {
					syncPrivateConnection.queueOut();
					loop = false;
				} else {
					loop = true;

					/**
					 * Warning! 큐에 반환 되지 않고 가비지 대상이 될 경우 그 원인을 추적해야 하므로 반듯이 큐 안이라는 상태에서 연결을 폐기해야 한다
					 */

					String reasonForLoss = new StringBuilder("폴에서 꺼낸 연결[").append(syncPrivateConnection.hashCode())
							.append("]이 닫혀있어 폐기").toString();

					numberOfConnection--;

					log.warn("{} {}, 총 연결수[{}]", projectName, reasonForLoss, numberOfConnection);

					connectionPoolSupporter.notice(reasonForLoss);
					
					currentSocketTimeOut -= (System.currentTimeMillis() - startTime);
					if (currentSocketTimeOut <= 0) {
						throw new SocketTimeoutException("2.synchronized private connection pool timeout");
					}
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

		if (!(conn instanceof SyncPrivateConnection)) {
			String errorMessage = "the parameter conn is not instace of SyncPrivateConnection class";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		SyncPrivateConnection syncPrivateConnection = (SyncPrivateConnection) conn;
		synchronized (monitor) {
			/**
			 * 연속 2회 큐 입력 방지
			 */
			if (syncPrivateConnection.isInQueue()) {
				String errorMessage = String.format("the paramter conn[%d] allready was in connection queue",
						conn.hashCode());
				log.warn(errorMessage, new Throwable());
				throw new ConnectionPoolException(errorMessage);
			}

			syncPrivateConnection.queueIn();

			if (!syncPrivateConnection.isConnected()) {
				/**
				 * Warning! 큐에 반환 되지 않고 가비지 대상이 될 경우 그 원인을 추적해야 하므로 반듯이 큐 안이라는 상태에서 연결을 폐기해야 한다
				 */
				numberOfConnection--;

				String reasonForLoss = new StringBuilder("반환된 연결[").append(syncPrivateConnection.hashCode())
						.append("]이 닫혀있어 폐기").toString();

				log.warn("{} {}, 총 연결수[{}]", projectName, reasonForLoss, numberOfConnection);

				connectionPoolSupporter.notice(reasonForLoss);
				return;
			}

			connectionQueue.addLast(syncPrivateConnection);
			monitor.notify();
		}
	}

	public int getNumberOfConnection() {
		return numberOfConnection;
	}
	
	public String getPoolState() {
		return new StringBuilder()
				.append("numberOfConnection=")
				.append(numberOfConnection)
				.append(", connectionQueue.size=")
				.append(connectionQueue.size()).toString();
	}
	

	public int getPoolSize() {
		return connectionQueue.size();
	}
}
