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
import java.util.NoSuchElementException;

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

public class AsynPrivateConnectionPool implements ConnectionPoolIF {
	private Logger log = LoggerFactory.getLogger(AsynPrivateConnectionPool.class);
	private final Object monitor = new Object();
	
	private transient int poolSize = 0;
	private int poolMaxSize;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;
	private AsynSocketResourceFactoryIF asynSocketResourceFactory = null;	
	
	private String projectName = null;
	private long socketTimeOut;
		
	private ConnectionFixedParameter connectionFixedParameter = null;	
	
	private ArrayDeque<AsynPrivateConnection> connectionQueue = null;
	private transient int numberOfConnection = 0;

	public AsynPrivateConnectionPool(AsynPrivateConnectionPoolParameter asynPrivateConnectionPoolParameter,
			ConnectionFixedParameter connectionFixedParameter)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException, ConnectionPoolException {
		if (null == asynPrivateConnectionPoolParameter) {
			throw new IllegalArgumentException("the parameter asynPrivateConnectionPoolParameter is null");
		}
		
		if (null == connectionFixedParameter) {
			throw new IllegalArgumentException("the parameter connectionFixedParameter is null");
		}
		
		this.connectionFixedParameter = connectionFixedParameter;
		
		poolSize = asynPrivateConnectionPoolParameter.getPoolSize();
		poolMaxSize = asynPrivateConnectionPoolParameter.getPoolMaxSize();
		connectionPoolSupporter = asynPrivateConnectionPoolParameter.getConnectionPoolSupporter();
		asynSocketResourceFactory = asynPrivateConnectionPoolParameter.getAsynSocketResourceFactory();
		
		projectName = connectionFixedParameter.getProjectName();
		socketTimeOut = connectionFixedParameter.getSocketTimeOut();		
		
		connectionQueue = new ArrayDeque<AsynPrivateConnection>(poolMaxSize);
		
		/**
		 * 비동기 비 공유 연결 클래스는 입력 메시지 큐 1개와 출력 메시지큐 1개를 할당 받는다. 입력 메시지 큐는 모든 연결 클래스간에 공유하며,
		 * 출력 메시지 큐는 연결 클래스 각각 존재한다.
		 */
		try {
			for (int i = 0; i < poolSize; i++) {
				addConnection();
			}
		} catch (IOException e) {			
			while (! connectionQueue.isEmpty()) {
				try {
					connectionQueue.removeFirst().close();
				} catch(NoSuchElementException e1) {
					log.error("dead code enter");
					System.exit(1);
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

		
		AsynSocketResourceIF asynSocketResource = asynSocketResourceFactory.makeNewAsynSocketResource();

		AsynPrivateConnection conn = 
				new AsynPrivateConnection(connectionFixedParameter,
						asynSocketResource);

		synchronized (monitor) {
			connectionQueue.addLast(conn);
			numberOfConnection++;
			poolSize = Math.max(numberOfConnection, poolSize);
		}
		
		log.debug("{} new AsynPrivateConnection[{}] added", projectName, conn.hashCode());
	}

	private boolean whetherConnectionIsMissing() {
		return (numberOfConnection != poolSize);
	}

	public void addAllLostConnections() throws InterruptedException {
		log.debug("{} missing connection refill work start", projectName);

		while (whetherConnectionIsMissing()) {
			try {
				addConnection();				
			} catch (InterruptedException e) {
				String errorMessage = new StringBuilder(projectName)
						.append(" 인터럽트 발생에 따른 결손된 연결 충원 작업 중지").toString();
				log.warn(errorMessage, e);
				
				throw e;
			} catch (Exception e) {
				String errorMessage = new StringBuilder(projectName)
						.append(" 에러 발생에 따른 결손된 연결 충원 작업 중지, errmsg={}")
						.append(e.getMessage()).toString();
				
				log.warn(errorMessage, e);
				break;
			}
		}
		// }
		log.debug("{} missing connection refill work end", projectName);
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
					 * <pre>
					 * 폴에서 꺼낸 연결이 닫힌 경우 폐기한다. 단  이러한 작업은 큐에 넣어진 상태에서 이루어 져야 한다.
					 * 왜냐하면 연결은 참조 포인트가 없어 gc 될때 큐에 넣어진 상태가 아니면 로그를 남기는데
					 * 사용자 한테 넘기기전 폐기이므로 gc 될때 로그를 남기지 말아야 하기때문이다.  
					 * </pre>   
					 */
					String reasonForLoss = new StringBuilder("폴에서 꺼낸 연결[")							
							.append(asynPrivateConnection.hashCode()).append("]이 닫혀있어 폐기").toString();

					numberOfConnection--;

					log.warn("{} {}, numberOfConnection[{}]", projectName, reasonForLoss, numberOfConnection);

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

			/**
			 * 큐에 넣어진 상태로 변경
			 */
			asynPrivateConnection.queueIn();

			if (! asynPrivateConnection.isConnected()) {
				/**
				 * <pre>
				 * 반환된 연결이 닫힌 경우 폐기한다. 단  이러한 작업은 큐에 넣어진 상태에서 이루어 져야 한다.
				 * 왜냐하면 연결은 참조 포인트가 없어 gc 될때 큐에 넣어진 상태가 아니면 로그를 남기는데
				 * 정상적인 반환이므로 gc 될때 로그를 남기지 말아야 하기때문이다.  
				 * </pre>   
				 */
				numberOfConnection--;

				String reasonForLoss = new StringBuilder("반환된 연결[")
						.append(asynPrivateConnection.hashCode())
						.append("]이 닫혀있어 폐기").toString();

				log.warn("{} {}, numberOfConnection[{}]", projectName, reasonForLoss, numberOfConnection);

				connectionPoolSupporter.notice(reasonForLoss);
				return;
			}

			connectionQueue.addLast(asynPrivateConnection);
			monitor.notify();
		}
	}	

	public int size() {
		return numberOfConnection;
	}
	
	public int getQueueSize() {
		return connectionQueue.size();
	}
}
