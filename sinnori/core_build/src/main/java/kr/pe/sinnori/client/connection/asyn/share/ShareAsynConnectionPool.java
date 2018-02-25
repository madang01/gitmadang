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
package kr.pe.sinnori.client.connection.asyn.share;

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
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxPool;
import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

/**
 * 클라이언트 공유 방식의 비동기 연결 클래스 {@link ShareAsynConnection} 를 원소로 가지는 폴 관리자 클래스<br/>
 * 다른 쓰레드간에 연결 클래스를 공유하기 위해서 목록으로 관리되며 순차적으로 순환 할당한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class ShareAsynConnectionPool implements ConnectionPoolIF {
	private Logger log = LoggerFactory.getLogger(ShareAsynConnectionPool.class);

	private final Object monitor = new Object();

	private String projectName = null;
	private String host = null;
	private int port;
	private long socketTimeOut;
	private ClientMessageUtilityIF clientMessageUtility = null;	
	private transient int connectionPoolSize;
	private int connectionPoolMaxSize;	
	// private int numberOfAsynPrivateMailboxPerConnection;
	private AsynPrivateMailboxPoolFactoryIF asynPrivateMailboxPoolFactory = null;
	private AsynSocketResourceFactoryIF asynSocketResourceFactory = null;
	
	/**
	 * 공유방식으로 비동기 방식의 소켓 채널을 소유한 연결 클래스 목록. 쓰레드 공유하기 위해서 순차적으로 할당한다.
	 */
	private LinkedList<ShareAsynConnection> connectionList = null;
	private transient int numberOfConnection = 0;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;
	private int currentWorkingIndex = -1;

	public ShareAsynConnectionPool(String projectName, String host, int port, 
			long socketTimeOut,
			ClientMessageUtilityIF clientMessageUtility,			
			int connectionPoolSize,
			int connectionPoolMaxSize,
			// int numberOfAsynPrivateMailboxPerConnection,
			AsynPrivateMailboxPoolFactoryIF asynPrivateMailboxPoolFactory,
			AsynSocketResourceFactoryIF asynSocketResourceFactory)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException, ConnectionPoolException {
		this.projectName = projectName;
		this.host = host;
		this.port = port;
		this.connectionPoolSize = connectionPoolSize;
		this.connectionPoolMaxSize = connectionPoolMaxSize;
		this.socketTimeOut = socketTimeOut;
		this.clientMessageUtility = clientMessageUtility;		
		// this.numberOfAsynPrivateMailboxPerConnection = numberOfAsynPrivateMailboxPerConnection;
		this.asynPrivateMailboxPoolFactory = asynPrivateMailboxPoolFactory;
		this.asynSocketResourceFactory = asynSocketResourceFactory;

		this.connectionList = new LinkedList<ShareAsynConnection>();
		try {
			for (int i = 0; i < connectionPoolSize; i++) {
				addConnection();
			}

		} catch (IOException e) {
			while (! connectionList.isEmpty()) {
				try {
					connectionList.removeFirst().close();
				} catch (IOException e1) {
				}
			}
			throw e;
		}

		// log.info("connectionList size=[%d]", connectionList.size());
	}

	private void addConnection()
			throws InterruptedException, NoMoreDataPacketBufferException, IOException, ConnectionPoolException {

		if (numberOfConnection >= connectionPoolMaxSize) {
			throw new ConnectionPoolException("fail to add a connection because this connection pool is full");
		}
		
		AsynSocketResourceIF asynSocketResource = asynSocketResourceFactory.makeNewAsynSocketResource();
		
		AsynPrivateMailboxPool asynPrivateMailboxPool =asynPrivateMailboxPoolFactory.makeNewAsynPrivateMailboxPool();

		ShareAsynConnection serverConnection = new ShareAsynConnection(projectName, host, port, socketTimeOut, 
				clientMessageUtility,
				asynPrivateMailboxPool,
				asynSocketResource);

		synchronized (monitor) {
			connectionList.add(serverConnection);

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
				log.info("결손된 비동기 공유 연결 추가 작업 완료");
			} catch (InterruptedException e) {
				throw e;
			} catch (Exception e) {
				log.warn("에러 발생에 따른 결손된 비동기 공유 연결 추가 작업 잠시 중지 ", e);
				break;
			}
		}

	}

	public AbstractConnection getConnection()
			throws InterruptedException, SocketTimeoutException, ConnectionPoolException {
		boolean loop = false;
		ShareAsynConnection conn = null;

		synchronized (monitor) {
			do {
				if (connectionList.isEmpty()) {
					throw new ConnectionPoolException("check server alive");
				}

				currentWorkingIndex = (currentWorkingIndex + 1) % connectionList.size();

				conn = connectionList.get(currentWorkingIndex);

				if (conn.isConnected()) {
					loop = false;
				} else {
					loop = true;

					String reasonForLoss = new StringBuilder("다음 차례의 비동기 공유 연결[").append(conn.hashCode())
							.append("]이 닫혀있어 폐기").toString();

					numberOfConnection--;

					log.warn("{}, 총 연결수[{}]", reasonForLoss, numberOfConnection);

					connectionPoolSupporter.notice(reasonForLoss);

					connectionList.remove(conn);
				}
			} while (loop);

			return conn;
		}
	}

	public void release(AbstractConnection conn) {

		if (null == conn) {
			String errorMessage = "the parameter conn is null";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		if (!(conn instanceof ShareAsynConnection)) {
			String errorMessage = "the parameter conn is not instace of ShareAsynConnection class";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		synchronized (monitor) {
			if (!conn.isConnected()) {
				String reasonForLoss = new StringBuilder("반환된 비동기 공유 연결[").append(conn.hashCode()).append("]이 닫혀있어 폐기")
						.toString();

				numberOfConnection--;

				log.warn("{}, 총 연결수[{}]", reasonForLoss, numberOfConnection);

				connectionPoolSupporter.notice(reasonForLoss);

				connectionList.remove(conn);
			}
		}
	}

	@Override
	public void registerConnectionPoolSupporter(ConnectionPoolSupporterIF connectionPoolSupporter) {
		this.connectionPoolSupporter = connectionPoolSupporter;
	}

}
