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

package kr.pe.sinnori.client;

import java.io.IOException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.ConnectionPoolIF;
import kr.pe.sinnori.client.connection.ConnectionPoolManager;
import kr.pe.sinnori.client.connection.SocketResoruceIF;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResource;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceIF;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxMapper;
import kr.pe.sinnori.client.connection.asyn.noshare.NoShareAsynConnection;
import kr.pe.sinnori.client.connection.asyn.noshare.NoShareAsynConnectionPool;
import kr.pe.sinnori.client.connection.asyn.share.ShareAsynConnection;
import kr.pe.sinnori.client.connection.asyn.share.ShareAsynConnectionPool;
import kr.pe.sinnori.client.connection.asyn.task.AbstractClientTask;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.ClientExecutorPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.handler.ClientExecutorIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderIF;
import kr.pe.sinnori.client.connection.sync.noshare.NoShareSyncConnection;
import kr.pe.sinnori.client.connection.sync.noshare.NoShareSyncConnectionPool;
import kr.pe.sinnori.client.connection.sync.noshare.SyncPrivateSocketResource;
import kr.pe.sinnori.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.etc.ObjectCacheManager;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.DataPacketBufferPool;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.dhb.DHBMessageProtocol;
import kr.pe.sinnori.common.protocol.djson.DJSONMessageProtocol;
import kr.pe.sinnori.common.protocol.thb.THBMessageProtocol;
import kr.pe.sinnori.common.type.ConnectionType;

/**
 * <pre>
 * 클라리언트 프로젝트 클래스. 프로젝트 소속 클라이언트용 서버접속 API 동작 환경 조성과 자원을 전담하는 클래스.
 * -  자원 목록 -
 * (1) 데이터 패킷 버퍼 큐
 * (2) 바디 버퍼 큐
 * (3) 비동기 입출력 자원
 *     (3-1) 입력 메시지 큐
 *     (3-2) 출력 메시지 큐를 원소로 하는 큐
 *     (3-3) 서버에서 보내는 불특정 다수 메시지를 받는 큐
 *     (3-4) 입력 메시지 쓰기 담당 쓰레드 폴
 *     (3-5) 출력 메시지 쓰기 담당 쓰레드 폴
 * (4) 메시지 정보 해쉬
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public class AnyProjectConnectionPool implements ClientProjectIF, ClientObjectCacheManagerIF {
	private Logger log = LoggerFactory.getLogger(AnyProjectConnectionPool.class);
	
	private ProjectPartConfiguration projectPartConfiguration = null;
	
	private ObjectCacheManager objectCacheManager = ObjectCacheManager
			.getInstance();	
	
	private MessageProtocolIF messageProtocol = null;
	
	
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	
	private CharsetEncoder charsetEncoderOfProject = null;
	
	private CharsetDecoder charsetDecoderOfProject = null;
	
	private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;
	

	/** 비동기 방식에서 사용되는 출력 메시지 읽기 쓰레드 */
	private OutputMessageReaderPool outputMessageReaderPool = null;

	private InputMessageWriterPool inputMessageWriterPool = null;

	private ClientExecutorPool clientExecutorPool = null;

	/** 프로젝트의 연결 클래스 폴 */
	private ConnectionPoolIF connectionPool = null;

	private ConnectionPoolManager connectionPoolManager = null;
	

	public AnyProjectConnectionPool(ProjectPartConfiguration projectPartConfiguration)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException, ConnectionPoolException {
		this.projectPartConfiguration = projectPartConfiguration;		
		
		charsetEncoderOfProject = CharsetUtil.createCharsetEncoder(projectPartConfiguration.getCharset());
		charsetDecoderOfProject = CharsetUtil.createCharsetDecoder(projectPartConfiguration.getCharset());
		
		boolean isDirect = false;
		this.dataPacketBufferPool = new DataPacketBufferPool(isDirect, 
				projectPartConfiguration.getByteOrder()
						, projectPartConfiguration.getDataPacketBufferSize()
						, projectPartConfiguration.getDataPacketBufferPoolSize());
		

		switch (projectPartConfiguration.getMessageProtocolType()) {
			case DHB: {
				messageProtocol = new DHBMessageProtocol(
						projectPartConfiguration.getMessageIDFixedSize(), 
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
						charsetEncoderOfProject, charsetDecoderOfProject, 
						dataPacketBufferPool);
	
				break;
			}
			case DJSON: {
				messageProtocol = new DJSONMessageProtocol(
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), 
						charsetEncoderOfProject, charsetDecoderOfProject, 
						dataPacketBufferPool);
				break;
			}
			case THB: {
				messageProtocol = new THBMessageProtocol(
						projectPartConfiguration.getMessageIDFixedSize(), 
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), 
						charsetEncoderOfProject, charsetDecoderOfProject, dataPacketBufferPool);
				break;
			}
			default: {
				log.error(String.format("project[%s] 지원하지 않는 메시지 프로토콜[%s] 입니다.",
						projectPartConfiguration.getProjectName(), projectPartConfiguration
								.getMessageProtocolType().toString()));
				System.exit(1);
			}
		}
		
		ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil(projectPartConfiguration
				.getFirstPrefixDynamicClassFullName());
		
		

		if (projectPartConfiguration.getConnectionType().equals(ConnectionType.SYNC_PRIVATE)) {
			connectionPool = new NoShareSyncConnectionPool(projectPartConfiguration.getProjectName(),
					projectPartConfiguration.getServerHost(), projectPartConfiguration.getServerPort(),
					projectPartConfiguration.getClientConnectionCount(),
					projectPartConfiguration.getClientConnectionMaxCount(),
					projectPartConfiguration.getClientSocketTimeout(),
					projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), charsetDecoderOfProject,
					messageProtocol, dataPacketBufferPool, this);
		} else {
			inputMessageWriterPool = new InputMessageWriterPool(projectPartConfiguration.getProjectName(),
					projectPartConfiguration.getClientAsynInputMessageWriterPoolSize(),
					projectPartConfiguration.getClientAsynInputMessageQueueSize(), dataPacketBufferPool);

			outputMessageReaderPool = new OutputMessageReaderPool(projectPartConfiguration.getProjectName(),
					projectPartConfiguration.getClientAsynOutputMessageReaderPoolSize(),
					projectPartConfiguration.getClientReadSelectorWakeupInterval(), messageProtocol);

			clientExecutorPool = new ClientExecutorPool(projectPartConfiguration.getProjectName(),
					projectPartConfiguration.getClientAsynExecutorPoolSize(),
					projectPartConfiguration.getClientAsynOutputMessageQueueSize(), messageProtocol, this);

			inputMessageWriterPool.startAll();
			clientExecutorPool.startAll();
			outputMessageReaderPool.startAll();

			if (projectPartConfiguration.getConnectionType().equals(ConnectionType.ASYN_SHARE)) {

				connectionPool = new ShareAsynConnectionPool(projectPartConfiguration.getProjectName(),
						projectPartConfiguration.getServerHost(), projectPartConfiguration.getServerPort(),
						projectPartConfiguration.getClientConnectionCount(),
						projectPartConfiguration.getClientConnectionMaxCount(),
						projectPartConfiguration.getClientSocketTimeout(), inputMessageWriterPool,
						outputMessageReaderPool, clientExecutorPool,
						projectPartConfiguration.getClientAsynPirvateMailboxCntPerPublicConnection(),
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), charsetDecoderOfProject,
						messageProtocol, dataPacketBufferPool, this);
			} else {
				connectionPool = new NoShareAsynConnectionPool(projectPartConfiguration.getProjectName(),
						projectPartConfiguration.getServerHost(), projectPartConfiguration.getServerPort(),
						projectPartConfiguration.getClientConnectionCount(),
						projectPartConfiguration.getClientConnectionMaxCount(),
						projectPartConfiguration.getClientSocketTimeout(), inputMessageWriterPool,
						outputMessageReaderPool, clientExecutorPool,
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), charsetDecoderOfProject,
						messageProtocol,

						dataPacketBufferPool, this);
			}

		}

		/** FIXME! 신놀이 환경 변수 '연결 폴 관리자 수행 간격' 설정 필요함 */
		connectionPoolManager = new ConnectionPoolManager(connectionPool, 1000L * 60 * 10);

		connectionPoolManager.start();

		/*
		 * clientProjectMonitor = new ClientProjectMonitor( clientMonitorTimeInterval,
		 * clientMonitorReceptionTimeout); clientProjectMonitor.start();
		 */
	}

	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws IOException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException,
			ServerTaskException, AccessDeniedException, InterruptedException, ConnectionPoolException {

		AbstractMessage outObj = null;
		AbstractConnection conn = connectionPool.getConnection();
		try {
			outObj = conn.sendSyncInputMessage(inputMessage);
		} finally {
			connectionPool.release(conn);
		}

		return outObj;
	}

	public void sendAsynInputMessage(AbstractMessage inputMessage)
			throws InterruptedException, ConnectionPoolException, NotSupportedException, IOException,
			NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException {
		AbstractConnection conn = connectionPool.getConnection();
		try {
			conn.sendAsynInputMessage(inputMessage);
		} finally {
			connectionPool.release(conn);
		}
	}

	public AbstractConnection createConnection(String host, int port)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException {
		AbstractConnection conn = null;
		if (projectPartConfiguration.getConnectionType().equals(ConnectionType.SYNC_PRIVATE)) {
			SocketOutputStream socketOutputStream = new SocketOutputStream(charsetDecoderOfProject,
					projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), dataPacketBufferPool);

			SocketResoruceIF syncPrivateSocketResoruce = new SyncPrivateSocketResource(socketOutputStream);

			conn = new NoShareSyncConnection(projectPartConfiguration.getProjectName(), host, port,
					projectPartConfiguration.getClientSocketTimeout(), syncPrivateSocketResoruce, dataPacketBufferPool,
					messageProtocol, this);
		} else {
			if (projectPartConfiguration.getConnectionType().equals(ConnectionType.ASYN_SHARE)) {
				OutputMessageReaderIF outputMessageReader = outputMessageReaderPool.getNextOutputMessageReader();
				InputMessageWriterIF inputMessageWriter = inputMessageWriterPool.getNextInputMessageWriter();
				ClientExecutorIF clientExecutor = clientExecutorPool.getNextClientExecutor();

				SocketOutputStream socketOutputStream = new SocketOutputStream(charsetDecoderOfProject,
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), dataPacketBufferPool);

				AsynPrivateMailboxMapper asynPrivateMailboxMapper = new AsynPrivateMailboxMapper(
						projectPartConfiguration.getClientAsynPirvateMailboxCntPerPublicConnection(),
						projectPartConfiguration.getClientSocketTimeout());

				AsynSocketResourceIF asynSocketResource = new AsynSocketResource(socketOutputStream, inputMessageWriter,
						outputMessageReader, clientExecutor);

				conn = new ShareAsynConnection(projectPartConfiguration.getProjectName(), host, port,
						projectPartConfiguration.getClientSocketTimeout(), asynPrivateMailboxMapper, asynSocketResource,
						messageProtocol, this);
			} else {
				OutputMessageReaderIF outputMessageReader = outputMessageReaderPool.getNextOutputMessageReader();
				InputMessageWriterIF inputMessageWriter = inputMessageWriterPool.getNextInputMessageWriter();
				ClientExecutorIF clientExecutor = clientExecutorPool.getNextClientExecutor();

				SocketOutputStream socketOutputStream = new SocketOutputStream(charsetDecoderOfProject,
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), dataPacketBufferPool);

				AsynSocketResourceIF asynSocketResource = new AsynSocketResource(socketOutputStream, inputMessageWriter,
						outputMessageReader, clientExecutor);

				conn = new NoShareAsynConnection(projectPartConfiguration.getProjectName(), host, port,
						projectPartConfiguration.getClientSocketTimeout(), asynSocketResource, messageProtocol, this);
			}
		}

		return conn;
	}

	public MessageCodecIF getClientMessageCodec(ClassLoader classLoader, String messageID)
			throws DynamicClassCallException {
		/*
		 * String classFullName = new
		 * StringBuilder(classLoaderClassPackagePrefixName).append("message.")
		 * .append(messageID).append(".").append(messageID)
		 * .append("ClientCodec").toString();
		 */

		String classFullName = ioPartDynamicClassNameUtil.getClientMessageCodecClassFullName(messageID);

		MessageCodecIF messageCodec = null;

		Object valueObj = null;
		try {
			try {
				valueObj = objectCacheManager.getCachedObject(classLoader, classFullName);
			} catch (Exception e) {
				String errorMessage = String.format("ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::%s",
						classLoader.hashCode(), messageID, classFullName, e.toString());
				log.warn(errorMessage, e);
				throw new DynamicClassCallException(errorMessage);
			}
		} catch (IllegalArgumentException e) {
			String errorMessage = String.format(
					"ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::IllegalArgumentException::%s",
					classLoader.hashCode(), messageID, classFullName, e.getMessage());
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		}

		/*
		 * if (null == valueObj) { String errorMessage = String.format(
		 * "ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::valueObj is null"
		 * , classLoader.hashCode(), messageID, classFullName); log.warn(errorMessage);
		 * new DynamicClassCallException(errorMessage); }
		 * 
		 * if (!(valueObj instanceof MessageCodecIF)) { String errorMessage =
		 * String.format(
		 * "ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::valueObj type[%s] is not  MessageCodecIF"
		 * , classLoader.hashCode(), messageID, classFullName,
		 * valueObj.getClass().getCanonicalName()); log.warn(errorMessage); new
		 * DynamicClassCallException(errorMessage); }
		 */

		messageCodec = (MessageCodecIF) valueObj;

		return messageCodec;
	}

	@Override
	public AbstractClientTask getClientTask(String messageID) throws DynamicClassCallException {
		String classFullName = ioPartDynamicClassNameUtil.getClientTaskClassFullName(messageID);

		AbstractClientTask clientTask = null;

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

		Object valueObj = null;
		try {
			try {
				valueObj = objectCacheManager.getCachedObject(classLoader, classFullName);
			} catch (Exception e) {
				String errorMessage = String.format("ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::%s",
						classLoader.hashCode(), messageID, classFullName, e.toString());
				log.warn(errorMessage, e);
				throw new DynamicClassCallException(errorMessage);
			}
		} catch (IllegalArgumentException e) {
			String errorMessage = String.format(
					"ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::IllegalArgumentException::%s",
					classLoader.hashCode(), messageID, classFullName, e.getMessage());
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		}

		/*
		 * if (null == valueObj) { String errorMessage = String.format(
		 * "ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::valueObj is null"
		 * , classLoader.hashCode(), messageID, classFullName); log.warn(errorMessage);
		 * new DynamicClassCallException(errorMessage); }
		 * 
		 * if (!(valueObj instanceof MessageCodecIF)) { String errorMessage =
		 * String.format(
		 * "ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::valueObj type[%s] is not  MessageCodecIF"
		 * , classLoader.hashCode(), messageID, classFullName,
		 * valueObj.getClass().getCanonicalName()); log.warn(errorMessage); new
		 * DynamicClassCallException(errorMessage); }
		 */

		clientTask = (AbstractClientTask) valueObj;

		return clientTask;
	}
}
