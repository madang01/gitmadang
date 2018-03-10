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

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.client.connection.ConnectionPoolIF;
import kr.pe.sinnori.client.connection.ConnectionPoolSupporterIF;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceFactoryIF;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceIF;
import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

public class AsynPrivateConnectionPool implements ConnectionPoolIF {
	private Logger log = LoggerFactory.getLogger(AsynPrivateConnectionPool.class);
	private final Object monitor = new Object();

	private String projectName = null;
	private String host = null;
	private int port;
	private long socketTimeOut;
	private ClientMessageUtilityIF clientMessageUtility = null;

	private transient int size = 0;
	private int max;

	private AsynSocketResourceFactoryIF asynSocketResourceFactory = null;

	private ArrayDeque<AsynPrivateConnection> connectionQueue = null;
	private transient int numberOfConnection = 0;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;

	public AsynPrivateConnectionPool(String projectName, String host, int port, long socketTimeOut,
			ClientMessageUtilityIF clientMessageUtility, int size, int max,
			AsynSocketResourceFactoryIF asynSocketResourceFactory)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException, ConnectionPoolException {
		if (size <= 0) {
			String errorMessage = String.format("the parameter size[%d] is less than or equal to zero", size); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (max <= 0) {
			String errorMessage = String.format("the parameter max[%d] is less than or equal to zero", max); 
			throw new IllegalArgumentException(errorMessage);
		}

		if (size > max) {
			String errorMessage = String.format("the parameter size[%d] is greater than the parameter max[%d]", size, max); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		this.projectName = projectName;
		this.host = host;
		this.port = port;
		this.socketTimeOut = socketTimeOut;
		this.clientMessageUtility = clientMessageUtility;
		this.size = size;
		this.max = max;
		this.asynSocketResourceFactory = asynSocketResourceFactory;

		this.connectionQueue = new ArrayDeque<AsynPrivateConnection>(max);
		
		/**
		 * 비동기 비 공유 연결 클래스는 입력 메시지 큐 1개와 출력 메시지큐 1개를 할당 받는다. 입력 메시지 큐는 모든 연결 클래스간에 공유하며,
		 * 출력 메시지 큐는 연결 클래스 각각 존재한다.
		 */
		try {
			for (int i = 0; i < size; i++) {
				addConnection();

			}
		} catch (IOException e) {

			while (!connectionQueue.isEmpty()) {

				try {
					connectionQueue.removeFirst().close();
				} catch (IOException e1) {
				}
			}
			throw e;
		}

	}

	private void addConnection()
			throws InterruptedException, NoMoreDataPacketBufferException, IOException, ConnectionPoolException {

		if (numberOfConnection >= max) {
			throw new ConnectionPoolException("fail to add a connection because this connection pool is full");
		}

		AsynSocketResourceIF asynSocketResource = asynSocketResourceFactory.makeNewAsynSocketResource();

		AsynPrivateConnection serverConnection = new AsynPrivateConnection(projectName, host, port, socketTimeOut,
				clientMessageUtility, asynSocketResource);

		synchronized (monitor) {
			connectionQueue.addLast(serverConnection);
			numberOfConnection++;
			size = Math.max(numberOfConnection, size);
		}
	}

	private boolean whetherConnectionIsMissing() {
		return (numberOfConnection != size);
	}

	public void addAllLostConnections() throws InterruptedException {

		while (whetherConnectionIsMissing()) {
			try {
				addConnection();
				log.info("결손된 비동기 비공유 연결 추가 작업 완료");
			} catch (InterruptedException e) {
				throw e;
			} catch (Exception e) {
				log.warn("에러 발생에 따른 결손된 비동기 비공유 연결 추가 작업 잠시 중지 ", e);
				break;
			}
		}
		// }
	}

	public AbstractConnection getConnection()
			throws InterruptedException, SocketTimeoutException, ConnectionPoolException {

		AsynPrivateConnection asynPrivateConnection = null;
		boolean loop = false;

		synchronized (monitor) {
			do {
				// log.info("111111111");

				if (0 == numberOfConnection) {
					throw new ConnectionPoolException("check server alive");
				}

				if (connectionQueue.isEmpty()) {
					monitor.wait(socketTimeOut);

					if (connectionQueue.isEmpty()) {
						throw new SocketTimeoutException("asynchronized private connection pool timeout");
					}
				}

				asynPrivateConnection = connectionQueue.removeFirst();

				if (asynPrivateConnection.isConnected()) {
					asynPrivateConnection.queueOut();
					loop = false;
				} else {
					loop = true;

					/**
					 * Warning! 큐에 반환 되지 않고 가비지 대상이 될 경우 그 원인을 추적해야 하므로 반듯이 큐 안이라는 상태에서 연결을 폐기해야 한다
					 */

					String reasonForLoss = new StringBuilder("폴에서 꺼낸 비동기 비공유 연결[")
							.append(asynPrivateConnection.hashCode()).append("]이 닫혀있어 폐기").toString();

					numberOfConnection--;

					log.warn("{}, 총 연결수[{}]", reasonForLoss, numberOfConnection);

					connectionPoolSupporter.notice(reasonForLoss);
				}

			} while (loop);
		}

		return asynPrivateConnection;

	}

	public void release(AbstractConnection conn) throws ConnectionPoolException {

		if (null == conn) {
			String errorMessage = "the parameter conn is null";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		if (!(conn instanceof AsynPrivateConnection)) {
			String errorMessage = "the parameter conn is not instace of NoShareAsynConnection class";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		AsynPrivateConnection asynPrivateConnection = (AsynPrivateConnection) conn;

		synchronized (monitor) {
			/**
			 * 연속 2회 큐 입력 방지
			 */
			if (asynPrivateConnection.isInQueue()) {
				String errorMessage = String.format("the paramter conn[%d] allready was in connection queue",
						conn.hashCode());
				log.warn(errorMessage, new Throwable());
				throw new ConnectionPoolException(errorMessage);
			}

			asynPrivateConnection.queueIn();

			if (! asynPrivateConnection.isConnected()) {
				/**
				 * Warning! 큐에 반환 되지 않고 가비지 대상이 될 경우 그 원인을 추적해야 하므로 반듯이 큐 안이라는 상태에서 연결을 폐기해야 한다
				 */

				numberOfConnection--;

				String reasonForLoss = new StringBuilder("반환된 비동기 비공유 연결[").append(asynPrivateConnection.hashCode())
						.append("]이 닫혀있어 폐기").toString();

				log.warn("{}, 총 연결수[{}]", reasonForLoss, numberOfConnection);

				connectionPoolSupporter.notice(reasonForLoss);
				return;
			}

			connectionQueue.addLast(asynPrivateConnection);
			monitor.notify();
		}

	}

	@Override
	public void registerConnectionPoolSupporter(ConnectionPoolSupporterIF connectionPoolSupporter) {
		this.connectionPoolSupporter = connectionPoolSupporter;
	}

}
