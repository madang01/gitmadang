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
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.AbstractConnectionPool;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPublicMailbox;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterPoolIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPoolIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderIF;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;

/**
 * 클라이언트 비공유 방식의 비동기 연결 클래스 {@link NoShareAsynConnection} 를 원소로 가지는 폴 관리자 클래스<br/>
 * 다른 쓰레드간에 연결 클래스를 공유 시키지 않기 위해서 폴 원소는 큐로 관리한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class NoShareAsynConnectionPool extends AbstractConnectionPool {
	
	private LinkedBlockingQueue<NoShareAsynConnection> connectionQueue = null;
	private ArrayList<NoShareAsynConnection> connectionList = new ArrayList<NoShareAsynConnection>();
	// private int connectionPoolSize;
	private long socketTimeOut;

	public NoShareAsynConnectionPool(
			String projectName,
			String host,
			int port,
			int connectionPoolSize,			
			long socketTimeOut,
			boolean whetherToAutoConnect,
			AsynPublicMailbox asynPublicMailbox,
			int dataPacketBufferMaxCntPerMessage,
			CharsetDecoder streamCharsetDecoder,
			MessageProtocolIF messageProtocol,
			InputMessageWriterPoolIF inputMessageWriterPool,
			OutputMessageReaderPoolIF outputMessageReaderPool,
			DataPacketBufferPoolIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager)
			throws NoMoreDataPacketBufferException, InterruptedException, NoMoreOutputMessageQueueException {		
		// this.connectionPoolSize = connectionPoolSize;
		this.socketTimeOut = socketTimeOut;
		
		connectionQueue = new LinkedBlockingQueue<NoShareAsynConnection>(
				connectionPoolSize);

		/**
		 * 비동기 비 공유 연결 클래스는 입력 메시지 큐 1개와 출력 메시지큐 1개를 할당 받는다.
		 * 입력 메시지 큐는 모든 연결 클래스간에 공유하며, 출력 메시지 큐는 연결 클래스 각각 존재한다.
		 */
		for (int i = 0; i < connectionPoolSize; i++) {
			OutputMessageReaderIF outputMessageReader = outputMessageReaderPool.getNextOutputMessageReader();
			InputMessageWriterIF inputMessageWriter = inputMessageWriterPool.getNextInputMessageWriter();
			
			SocketOutputStream socketOutputStream = new SocketOutputStream(streamCharsetDecoder, 
					dataPacketBufferMaxCntPerMessage, dataPacketBufferQueueManager);
			
			NoShareAsynConnection serverConnection = new NoShareAsynConnection(
					projectName,
					i, 
					host, 
					port, 
					socketTimeOut, 
					whetherToAutoConnect,   
					asynPublicMailbox,
					inputMessageWriter,
					outputMessageReader,
					socketOutputStream,
					messageProtocol,
					dataPacketBufferQueueManager, 
					clientObjectCacheManager);
			connectionQueue.add(serverConnection);
			connectionList.add(serverConnection);
		}
	}
	

	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws IOException, SocketTimeoutException,
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, AccessDeniedException, InterruptedException {
		NoShareAsynConnection conn = null;

		//synchronized (monitor) {
			/** 쓰레드 간에 공유를 막기 위해 queueOut 사용*/		
			conn = connectionQueue.poll(socketTimeOut, TimeUnit.MICROSECONDS);		

			if (null == conn) {
				throw new SocketTimeoutException("no share synchronized connection pool timeout");
			}
			
			conn.queueOut();
		//}
		
		
		AbstractMessage outObj = null;
		try {
			outObj = conn.sendSyncInputMessage(inputMessage);
		} finally {
			conn.queueIn();
			boolean isSuccess = connectionQueue.offer(conn);
			if (!isSuccess) {
				log.error("fail to offer connection[{}] to connection queue becase of bug, you need to check and fix bug", conn.hashCode());
				System.exit(1);
			}
		}

		return outObj;
	}	
	
	@Override
	public AbstractConnection getConnection() throws InterruptedException, NotSupportedException, SocketTimeoutException {
		
		//synchronized (monitor) {
			NoShareAsynConnection conn = connectionQueue.poll(socketTimeOut, TimeUnit.MILLISECONDS);
			if (null == conn) {
				throw new SocketTimeoutException("no share synchronized connection pool timeout");
			}
			conn.queueOut();
			return conn;
		//}
	}
	
	@Override
	public void release(AbstractConnection conn) throws NotSupportedException {
		if (null == conn) {
			String errorMessage = "the parameter conn is null";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! (conn instanceof NoShareAsynConnection)) {
			String errorMessage = "the parameter conn is not instace of NoShareAsynConnection class";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}

		NoShareAsynConnection serverConnection = (NoShareAsynConnection)conn;
		
		synchronized (monitor) {
			/**
			 * 연속 2회 큐 입력 방지
			 */
			if (serverConnection.isInQueue()) {
				String errorMessage = String.format("the paramter conn[%d] allready was in connection queue", conn.hashCode());
				log.warn(errorMessage, new Throwable());
				return;
			}
			serverConnection.queueIn();
			boolean isSuccess = connectionQueue.offer(serverConnection);
			if (!isSuccess) {
				log.error("fail to offer NoShareAsynConnection[{}] to connection queue becase of bug, you need to check and fix bug", conn.hashCode());
				System.exit(1);
			}
		}
	}

}
