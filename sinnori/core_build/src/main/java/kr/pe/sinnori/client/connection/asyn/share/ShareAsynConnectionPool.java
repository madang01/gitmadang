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
import java.util.ArrayList;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.AbstractConnectionPool;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxMapper;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPublicMailbox;
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
 * 클라이언트 공유 방식의 비동기 연결 클래스 {@link ShareAsynConnection} 를 원소로 가지는 폴 관리자 클래스<br/>
 * 다른 쓰레드간에 연결 클래스를 공유하기 위해서 목록으로 관리되며 순차적으로 순환 할당한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class ShareAsynConnectionPool extends AbstractConnectionPool {
	/**
	 * 공유방식으로 비동기 방식의 소켓 채널을 소유한 연결 클래스 목록. 쓰레드 공유하기 위해서 순차적으로 할당한다.
	 */
	private ArrayList<ShareAsynConnection> connectionList = null;
	/** 순차적으로 할당하기 위해서 목록내에 반환할 연결 클래스를 가르키는 인덱스 */
	private int indexOfConnection = 0;
	

	
	public ShareAsynConnectionPool(String projectName, 
			String host, int port,
			int connectionPoolSize, 
			long socketTimeOut, 
			boolean whetherToAutoConnect,
			int numberOfAsynPrivateMailboxPerConnection,
			AsynPublicMailbox asynPublicMailbox,			
			int dataPacketBufferMaxCntPerMessage,
			CharsetDecoder streamCharsetDecoder,
			MessageProtocolIF messageProtocol,
			OutputMessageReaderPoolIF outputMessageReaderPool,
			DataPacketBufferPoolIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager)
			throws NoMoreDataPacketBufferException, InterruptedException, NoMoreOutputMessageQueueException {
		super();
		// log.info("create new MultiNoneBlockConnectionPool");

		

		connectionList = new ArrayList<ShareAsynConnection>(connectionPoolSize);

		for (int i = 0; i < connectionPoolSize; i++) {
			OutputMessageReaderIF outputMessageReader = outputMessageReaderPool.getNextOutputMessageReader();
			SocketOutputStream socketOutputStream = new SocketOutputStream(streamCharsetDecoder, 
					dataPacketBufferMaxCntPerMessage, dataPacketBufferQueueManager);
			
			ShareAsynConnection serverConnection = null;
			
			AsynPrivateMailboxMapper asynPrivateMailboxMapper = 
					new AsynPrivateMailboxMapper(numberOfAsynPrivateMailboxPerConnection,
							socketTimeOut);

			serverConnection = new ShareAsynConnection(projectName, i, 
					host, port, 
					socketTimeOut, 
					whetherToAutoConnect, 
					asynPublicMailbox,
					asynPrivateMailboxMapper,
					socketOutputStream,
					messageProtocol, 
					outputMessageReader,
					dataPacketBufferQueueManager, 
					clientObjectCacheManager);

			connectionList.add(serverConnection);

		}

		// log.info("connectionList size=[%d]", connectionList.size());

	}

	

	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws IOException, NoMoreDataPacketBufferException,
			BodyFormatException, DynamicClassCallException, ServerTaskException, AccessDeniedException, 
			InterruptedException {
		ShareAsynConnection conn = null;

		synchronized (monitor) {
			conn = connectionList.get(indexOfConnection);
			indexOfConnection = (indexOfConnection + 1) % connectionList.size();			
			
			return conn.sendSyncInputMessage(inputMessage);
		}
	}

	@Override
	public AbstractConnection getConnection() throws InterruptedException, NotSupportedException, SocketTimeoutException {
		throw new NotSupportedException("공유+비동기 연결 객체는 직접적으로 받을 수 없습니다.");
	}

	@Override
	public void release(AbstractConnection conn) throws NotSupportedException {
		throw new NotSupportedException("공유+비동기 연결 객체를 직접 받지 않으므로 반환 기능도 없습니다.");
	}

	
}
