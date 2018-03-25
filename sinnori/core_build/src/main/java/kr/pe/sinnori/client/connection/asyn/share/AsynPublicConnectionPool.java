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
import kr.pe.sinnori.client.connection.ConnectionFixedParameter;
import kr.pe.sinnori.client.connection.ConnectionPoolIF;
import kr.pe.sinnori.client.connection.ConnectionPoolSupporterIF;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceFactoryIF;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceIF;
import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

 
public class AsynPublicConnectionPool implements ConnectionPoolIF {
	private Logger log = LoggerFactory.getLogger(AsynPublicConnectionPool.class);

	private final Object monitor = new Object();

	private transient int poolSize;
	private int poolMaxSize;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;	
	private AsynSocketResourceFactoryIF asynSocketResourceFactory = null;
	private AsynPrivateMailboxPoolFactoryIF asynPrivateMailboxPoolFactory = null;
	
	private String projectName = null;
	
	private ConnectionFixedParameter connectionFixedParameter = null;
	
	
	/**
	 * 공유방식으로 비동기 방식의 소켓 채널을 소유한 연결 클래스 목록. 쓰레드 공유하기 위해서 순차적으로 할당한다.
	 */
	private LinkedList<AsynPublicConnection> connectionList = null;
	private transient int numberOfConnection = 0;	
	private int currentWorkingIndex = -1;

	public AsynPublicConnectionPool(
			AsynPublicConnectionPoolParameter asynPublicConnectionPoolParameter,
			ConnectionFixedParameter connectionFixedParameter)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException, ConnectionPoolException {
		if (null == asynPublicConnectionPoolParameter) {
			String errorMessage = "the parameter asynPublicConnectionPoolParameter is null"; 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == connectionFixedParameter) {
			String errorMessage = "the parameter connectionFixedParameter is null"; 
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.connectionFixedParameter = connectionFixedParameter;
		
		poolSize = asynPublicConnectionPoolParameter.getPoolSize();
		poolMaxSize = asynPublicConnectionPoolParameter.getPoolMaxSize();
		connectionPoolSupporter = asynPublicConnectionPoolParameter.getConnectionPoolSupporter();
		asynSocketResourceFactory = asynPublicConnectionPoolParameter.getAsynSocketResourceFactory();
		asynPrivateMailboxPoolFactory = asynPublicConnectionPoolParameter.getAsynPrivateMailboxPoolFactory();
		
		projectName = connectionFixedParameter.getProjectName();
		

		connectionList = new LinkedList<AsynPublicConnection>();
		try {
			for (int i = 0; i < poolSize; i++) {
				addConnection();
			}

		} catch (Exception e) {
			while (! connectionList.isEmpty()) {
				try {
					connectionList.removeFirst().close();
				} catch (IOException e1) {
				}
			}
			throw e;
		}

		connectionPoolSupporter.registerPool(this);
		connectionPoolSupporter.start();
		// log.info("connectionList size=[%d]", connectionList.size());
	}

	private void addConnection()
			throws InterruptedException, NoMoreDataPacketBufferException, IOException, ConnectionPoolException {

		if (numberOfConnection >= poolMaxSize) {
			throw new ConnectionPoolException("fail to add a connection because this connection pool is full");
		}
		
		AsynPrivateMailboxPoolIF asynPrivateMailboxPool
			=	asynPrivateMailboxPoolFactory.makeNewAsynPrivateMailboxPool();		

		AsynSocketResourceIF asynSocketResource = asynSocketResourceFactory.makeNewAsynSocketResource();
		AsynPublicConnection conn = null;
		
		try {
		conn = new AsynPublicConnection(connectionFixedParameter,
				asynSocketResource, asynPrivateMailboxPool);
		} catch(Exception e) {
			asynSocketResource.releaseSocketResources();
			throw e;
		}

		// synchronized (monitor) {
			
			connectionList.add(conn);

			numberOfConnection++;
			poolSize = Math.max(numberOfConnection, poolSize);
		//}
		
		log.debug("{} new AsynPublicConnection[{}] added", projectName, conn.hashCode());
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
					String errorMessage = new StringBuilder(projectName)
							.append(" 인터럽트 발생에 따른 결손된 연결 추가 작업 중지").toString();
					log.warn(errorMessage, e);
					
					throw e;
				} catch (Exception e) {
					String errorMessage = new StringBuilder(projectName)
							.append(" 에러 발생에 따른 결손된 연결 추가 작업 중지, errmsg=")
							.append(e.getMessage()).toString();
					
					log.warn(errorMessage, e);
					break;
				}
			}
		}
	}

	public AbstractConnection getConnection()
			throws InterruptedException, SocketTimeoutException, ConnectionPoolException {
		boolean loop = false;
		AsynPublicConnection conn = null;

		synchronized (monitor) {
			do {
				if (connectionList.isEmpty()) {
					connectionPoolSupporter.notice("no more connection");
					throw new ConnectionPoolException("check server alive");
				}

				currentWorkingIndex = (currentWorkingIndex + 1) % connectionList.size();

				conn = connectionList.get(currentWorkingIndex);

				if (conn.isConnected()) {
					loop = false;
				} else {
					loop = true;

					String reasonForLoss = new StringBuilder("폴에서 꺼낸 연결[")
							.append(conn.hashCode())
							.append("]이 닫혀있어 폐기").toString();

					numberOfConnection--;

					log.warn("{} {}, 총 연결수[{}]", projectName, reasonForLoss, numberOfConnection);

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

		if (!(conn instanceof AsynPublicConnection)) {
			String errorMessage = "the parameter conn is not instace of ShareAsynConnection class";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}
		AsynPublicConnection asynPublicConnection = (AsynPublicConnection) conn;

		synchronized (monitor) {
			if (asynPublicConnection.isDropped()) return;
			
			if (! asynPublicConnection.isConnected()) {
				String reasonForLoss = new StringBuilder("반환된 연결[")
						.append(conn.hashCode()).append("]이 닫혀있어 폐기")
						.toString();				

				numberOfConnection--;

				log.warn("{} {}, 총 연결수[{}]", projectName, reasonForLoss, numberOfConnection);

				connectionPoolSupporter.notice(reasonForLoss);

				connectionList.remove(conn);
				
				asynPublicConnection.drop();
			}
		}
	}

	public int size() {
		return numberOfConnection;
	}
	
	public int getListSize() {
		return connectionList.size();
	}

}
