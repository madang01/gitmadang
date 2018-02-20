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
import kr.pe.sinnori.client.connection.SocketResoruceIF;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;

/**
 * 클라이언트 비공유 방식의 동기 연결 클래스 {@link NoShareSyncConnection} 를 원소로 가지는 폴 관리자 클래스<br/>
 * 다른 쓰레드간에 연결 클래스를 공유 시키지 않기 위해서 폴 원소는 큐로 관리한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class NoShareSyncConnectionPool implements ConnectionPoolIF {
	private Logger log = LoggerFactory.getLogger(NoShareSyncConnectionPool.class);	
	private final Object monitor = new Object();
	
	
	/** Connection Pool 운영을 위한 변수 */
	private LinkedBlockingQueue<NoShareSyncConnection> connectionQueue = null;
	// private int connectionPoolSize;
	private long socketTimeOut;
	
	public NoShareSyncConnectionPool(String projectName,
			String host,
			int port,
			int connectionPoolSize, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			int dataPacketBufferMaxCntPerMessage,
			CharsetDecoder streamCharsetDecoder,
			MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException {
		super();

		// log.info("create new SingleBlockConnectionPool");

		this.socketTimeOut = socketTimeOut;
		// this.connectionPoolSize = connectionPoolSize;

		connectionQueue = new LinkedBlockingQueue<NoShareSyncConnection>(
				connectionPoolSize);

		try {
			for (int i = 0; i < connectionPoolSize; i++) {
				SocketOutputStream socketOutputStream = new SocketOutputStream(streamCharsetDecoder, 
						dataPacketBufferMaxCntPerMessage, dataPacketBufferQueueManager);
				
				SocketResoruceIF syncPrivateSocketResoruce = new SyncPrivateSocketResource(socketOutputStream);
				
				NoShareSyncConnection serverConnection = new NoShareSyncConnection(
						projectName,  
						host, port,
						socketTimeOut,
						whetherToAutoConnect, 
						syncPrivateSocketResoruce,
						dataPacketBufferQueueManager,
						messageProtocol,
						clientObjectCacheManager);
				connectionQueue.add(serverConnection);
			}
		} catch(IOException e) {
			while(! connectionQueue.isEmpty()) {
				NoShareSyncConnection serverConnection = connectionQueue.poll(); 
				try {
					serverConnection.closeSocket();
				} catch (IOException e1) {
				}
				serverConnection.releaseSocketResources();
				
			}
			throw e;
		}
	}
		
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws IOException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, AccessDeniedException, InterruptedException {
		NoShareSyncConnection conn = null;
		
		conn = connectionQueue.poll(socketTimeOut, TimeUnit.MILLISECONDS);
		if (null == conn) {
			throw new SocketTimeoutException("no share synchronized connection pool timeout");
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
	
	
	public AbstractConnection getConnection() throws InterruptedException, NotSupportedException, SocketTimeoutException {
		
		
		synchronized (monitor) {
			NoShareSyncConnection conn = connectionQueue.poll(socketTimeOut, TimeUnit.MILLISECONDS);
			if (null == conn) {
				throw new SocketTimeoutException("no share synchronized connection pool timeout");
			}
			conn.queueOut();
			return conn;
		}
	}
	
	
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
	
}
