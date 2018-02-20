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
import java.nio.charset.CharsetDecoder;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.ConnectionPoolIF;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResource;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceIF;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxMapper;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.ClientExecutorPoolIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.handler.ClientExecutorIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterPoolIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPoolIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderIF;
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
 * 클라이언트 공유 방식의 비동기 연결 클래스 {@link ShareAsynConnection} 를 원소로 가지는 폴 관리자 클래스<br/>
 * 다른 쓰레드간에 연결 클래스를 공유하기 위해서 목록으로 관리되며 순차적으로 순환 할당한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class ShareAsynConnectionPool implements ConnectionPoolIF {
	// private Logger log = LoggerFactory.getLogger(NoShareAsynConnectionPool.class);
	// private final Object monitor = new Object();

	/**
	 * 공유방식으로 비동기 방식의 소켓 채널을 소유한 연결 클래스 목록. 쓰레드 공유하기 위해서 순차적으로 할당한다.
	 */
	private LinkedList<ShareAsynConnection> connectionList = null;
	

	public ShareAsynConnectionPool(String projectName, String host, int port, int connectionPoolSize,
			long socketTimeOut, boolean whetherToAutoConnect, InputMessageWriterPoolIF inputMessageWriterPool,
			OutputMessageReaderPoolIF outputMessageReaderPool, ClientExecutorPoolIF clientExecutorPool,
			int numberOfAsynPrivateMailboxPerConnection, int dataPacketBufferMaxCntPerMessage,
			CharsetDecoder streamCharsetDecoder, MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferQueueManager, ClientObjectCacheManagerIF clientObjectCacheManager)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException {
		super();
		// log.info("create new MultiNoneBlockConnectionPool");

		connectionList = new LinkedList<ShareAsynConnection>();
		try {
			for (int i = 0; i < connectionPoolSize; i++) {
				OutputMessageReaderIF outputMessageReader = outputMessageReaderPool.getNextOutputMessageReader();
				InputMessageWriterIF inputMessageWriter = inputMessageWriterPool.getNextInputMessageWriter();
				ClientExecutorIF clientExecutor = clientExecutorPool.getNextClientExecutor();
	
				SocketOutputStream socketOutputStream = new SocketOutputStream(streamCharsetDecoder,
						dataPacketBufferMaxCntPerMessage, dataPacketBufferQueueManager);
	
				ShareAsynConnection serverConnection = null;
	
				AsynPrivateMailboxMapper asynPrivateMailboxMapper = new AsynPrivateMailboxMapper(
						numberOfAsynPrivateMailboxPerConnection, socketTimeOut);
	
				AsynSocketResourceIF asynSocketResource = new AsynSocketResource(socketOutputStream, inputMessageWriter,
						outputMessageReader, clientExecutor);
	
				
					serverConnection = new ShareAsynConnection(projectName, host, port, socketTimeOut,
							whetherToAutoConnect, asynPrivateMailboxMapper, asynSocketResource, messageProtocol,
							clientObjectCacheManager);
	
					connectionList.add(serverConnection);
				
			}
			
		} catch (IOException e) {			
			while (! connectionList.isEmpty()) {
				try {
					connectionList.removeFirst().closeSocket();
				} catch (IOException e1) {
				}
			}
			throw e;
		}

		// log.info("connectionList size=[%d]", connectionList.size());
	}

	public synchronized AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws IOException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException,
			ServerTaskException, AccessDeniedException, InterruptedException {
		if (connectionList.isEmpty()) {
			throw new NoSuchElementException("the asyn public connection list is empty");
		}
		
		ShareAsynConnection conn = null;
		do {
			conn = connectionList.removeFirst();	
			
			if (conn.isConnected()) {
				connectionList.addLast(conn);
				break;
			} else {
				if (connectionList.isEmpty()) {
					throw new NoSuchElementException("no more valid asyn public connection");
				}
			}
		} while(true);
		

		return conn.sendSyncInputMessage(inputMessage);
	}

	public AbstractConnection getConnection()
			throws InterruptedException, NotSupportedException, SocketTimeoutException {
		throw new NotSupportedException("공유+비동기 연결 객체는 직접적으로 받을 수 없습니다.");
	}

	public void release(AbstractConnection conn) throws NotSupportedException {
		throw new NotSupportedException("공유+비동기 연결 객체를 직접 받지 않으므로 반환 기능도 없습니다.");
	}

}
