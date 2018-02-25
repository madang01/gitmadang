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
import java.util.LinkedList;

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

/**
 * 클라이언트 비공유 방식의 비동기 연결 클래스 {@link NoShareAsynConnection} 를 원소로 가지는 폴 관리자
 * 클래스<br/>
 * 다른 쓰레드간에 연결 클래스를 공유 시키지 않기 위해서 폴 원소는 큐로 관리한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class NoShareAsynConnectionPool implements ConnectionPoolIF {
	private Logger log = LoggerFactory.getLogger(NoShareAsynConnectionPool.class);
	private final Object monitor = new Object();

	private String projectName = null;
	private String host = null;
	private int port;
	private long socketTimeOut;
	private ClientMessageUtilityIF clientMessageUtility = null;
	
	
	private transient int connectionPoolSize = 0;
	private int connectionPoolMaxSize;	
	
	// private IEOClientThreadPoolSetManagerIF ieoClientThreadPoolSetManager = null;	
	// private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	private AsynSocketResourceFactoryIF asynSocketResourceFactory = null;
	

	private LinkedList<NoShareAsynConnection> connectionList = null;
	private transient int numberOfConnection = 0;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;

	public NoShareAsynConnectionPool(String projectName, 
			String host, int port, 
			long socketTimeOut,
			ClientMessageUtilityIF clientMessageUtility,			
			int connectionPoolSize,			
			int connectionPoolMaxSize,
			AsynSocketResourceFactoryIF asynSocketResourceFactory)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException, ConnectionPoolException {
		this.projectName = projectName;
		this.host = host;
		this.port = port;
		this.socketTimeOut = socketTimeOut;		
		this.clientMessageUtility = clientMessageUtility;		
		this.connectionPoolSize = connectionPoolSize;
		this.connectionPoolMaxSize = connectionPoolMaxSize;
		this.asynSocketResourceFactory = asynSocketResourceFactory;
		

		this.connectionList = new LinkedList<NoShareAsynConnection>();

		/**
		 * 비동기 비 공유 연결 클래스는 입력 메시지 큐 1개와 출력 메시지큐 1개를 할당 받는다. 입력 메시지 큐는 모든 연결 클래스간에 공유하며,
		 * 출력 메시지 큐는 연결 클래스 각각 존재한다.
		 */
		try {
			for (int i = 0; i < connectionPoolSize; i++) {
				addConnection();

			}
		} catch (IOException e) {

			while (!connectionList.isEmpty()) {

				try {
					connectionList.removeFirst().close();
				} catch (IOException e1) {
				}
			}
			throw e;
		}

	}

	private void addConnection()
			throws InterruptedException, NoMoreDataPacketBufferException, IOException, ConnectionPoolException {

		if (numberOfConnection >= connectionPoolMaxSize) {
			throw new ConnectionPoolException("fail to add a connection because this connection pool is full");
		}

		AsynSocketResourceIF asynSocketResource = asynSocketResourceFactory.makeNewAsynSocketResource();

		NoShareAsynConnection serverConnection = new NoShareAsynConnection(projectName, host, port, socketTimeOut,
				clientMessageUtility,
				asynSocketResource);
		
		synchronized (monitor) {
			connectionList.addLast(serverConnection);
			numberOfConnection++;
			connectionPoolSize = Math.max(numberOfConnection, connectionPoolSize);
		}
	}

	private boolean whetherConnectionIsMissing() {
		return (numberOfConnection != connectionPoolSize);
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

		NoShareAsynConnection asynPrivateConnection = null;
		boolean loop = false;

		synchronized (monitor) {
			do {
				if (0 == numberOfConnection) {
					throw new ConnectionPoolException("check server alive");
				}

				if (connectionList.isEmpty()) {
					monitor.wait(socketTimeOut);

					if (connectionList.isEmpty()) {
						throw new SocketTimeoutException("asynchronized private connection pool timeout");
					}
				}

				asynPrivateConnection = connectionList.removeFirst();

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

			return asynPrivateConnection;
		}
	}

	public void release(AbstractConnection conn) throws ConnectionPoolException {
		if (null == conn) {
			String errorMessage = "the parameter conn is null";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		if (!(conn instanceof NoShareAsynConnection)) {
			String errorMessage = "the parameter conn is not instace of NoShareAsynConnection class";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		NoShareAsynConnection asynPrivateConnection = (NoShareAsynConnection) conn;

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

			if (!asynPrivateConnection.isConnected()) {
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

			connectionList.addLast(asynPrivateConnection);
			monitor.notify();
		}
	}

	@Override
	public void registerConnectionPoolSupporter(ConnectionPoolSupporterIF connectionPoolSupporter) {
		this.connectionPoolSupporter = connectionPoolSupporter;
	}

}
