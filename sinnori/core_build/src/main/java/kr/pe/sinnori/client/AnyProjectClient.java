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
import java.net.SocketTimeoutException;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.ConnectionPoolIF;
import kr.pe.sinnori.client.connection.asyn.noshare.NoShareAsynConnectionPool;
import kr.pe.sinnori.client.connection.asyn.share.ShareAsynConnectionPool;
import kr.pe.sinnori.client.connection.asyn.task.AbstractClientTask;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.ClientExecutorPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPool;
import kr.pe.sinnori.client.connection.sync.noshare.NoShareSyncConnectionPool;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.project.AbstractProject;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
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
public class AnyProjectClient extends AbstractProject implements ClientProjectIF, ClientObjectCacheManagerIF {
	/** 모니터 객체 */
	//private final Object outputMessageQueuerQueueMonitor = new Object();

	

	/** 비동기 방식에서 사용되는 출력 메시지 읽기 쓰레드 */
	private OutputMessageReaderPool outputMessageReaderPool = null;
	
	private InputMessageWriterPool inputMessageWriterPool = null;
	
	private ClientExecutorPool  clientExecutorPool = null;

	/** 프로젝트의 연결 클래스 폴 */
	private ConnectionPoolIF connectionPool = null;

	// null;

	// private ClientProjectMonitor clientProjectMonitor = null;

	
	public AnyProjectClient(ProjectPartConfiguration projectPartConfiguration)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException {
		super(projectPartConfiguration);
		
		

		if (projectPartConfiguration.getConnectionType().equals(ConnectionType.SYNC_PRIVATE)) {
			connectionPool = new NoShareSyncConnectionPool(projectPartConfiguration.getProjectName(), 
					projectPartConfiguration.getServerHost(),
					projectPartConfiguration.getServerPort(), 
					projectPartConfiguration.getClientConnectionCount(), 
					projectPartConfiguration.getClientSocketTimeout(), 
					projectPartConfiguration.getClientWhetherAutoConnection(),					
					projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
					charsetDecoderOfProject,					
					messageProtocol,  
					dataPacketBufferPoolManager, this);
		} else {
			
			
			// AsynPublicMailbox asynPublicMailbox = new AsynPublicMailbox(projectPartConfiguration.getClientSocketTimeout(), asynOutputMessageQueue);

			
			inputMessageWriterPool = new InputMessageWriterPool(projectPartConfiguration.getProjectName(),
					projectPartConfiguration.getClientAsynInputMessageWriterSize(),
					projectPartConfiguration.getClientAsynInputMessageWriterMaxSize(),
					projectPartConfiguration.getClientAsynInputMessageQueueSize(), dataPacketBufferPoolManager);
			
			
			outputMessageReaderPool = new OutputMessageReaderPool(
					projectPartConfiguration.getProjectName(),
					projectPartConfiguration
					.getClientAsynOutputMessageReaderSize(),
					projectPartConfiguration
					.getClientAsynOutputMessageReaderMaxSize(),
					projectPartConfiguration
					.getClientReadSelectorWakeupInterval(), 
					messageProtocol);						
			
			clientExecutorPool = new ClientExecutorPool(projectPartConfiguration.getProjectName(),
					1, 1, projectPartConfiguration.getClientAsynOutputMessageQueueSize(), messageProtocol, this);
			
			inputMessageWriterPool.startAll();
			clientExecutorPool.startAll();
			outputMessageReaderPool.startAll();

			if (projectPartConfiguration.getConnectionType().equals(ConnectionType.ASYN_SHARE)) {				

				connectionPool = new ShareAsynConnectionPool(projectPartConfiguration.getProjectName(), 
						projectPartConfiguration.getServerHost(),
						projectPartConfiguration.getServerPort(),
						projectPartConfiguration.getClientConnectionCount(),
						projectPartConfiguration.getClientSocketTimeout(), 
						projectPartConfiguration.getClientWhetherAutoConnection(),
						inputMessageWriterPool,
						outputMessageReaderPool,
						clientExecutorPool,
						projectPartConfiguration.getClientAsynShareMailboxCnt(),
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
						charsetDecoderOfProject,
						messageProtocol,						 
						dataPacketBufferPoolManager, this);
			} else {
				connectionPool = new NoShareAsynConnectionPool(projectPartConfiguration.getProjectName(), 
						projectPartConfiguration.getServerHost(),
						projectPartConfiguration.getServerPort(),
						projectPartConfiguration.getClientConnectionCount(),  
						projectPartConfiguration.getClientSocketTimeout(), 
						projectPartConfiguration.getClientWhetherAutoConnection(),
						inputMessageWriterPool,
						outputMessageReaderPool,
						clientExecutorPool,
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
						charsetDecoderOfProject,
						messageProtocol, 
						 
						dataPacketBufferPoolManager, this);
			}

		}

		/*clientProjectMonitor = new ClientProjectMonitor(
				clientMonitorTimeInterval, clientMonitorReceptionTimeout);
		clientProjectMonitor.start();*/
	}

	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws IOException, 
			NoMoreDataPacketBufferException, BodyFormatException,
			DynamicClassCallException, ServerTaskException, AccessDeniedException, InterruptedException {
		return connectionPool.sendSyncInputMessage(inputMessage);
	}


	public AbstractConnection getConnection() throws InterruptedException,
			NotSupportedException, SocketTimeoutException {
		return connectionPool.getConnection();
	}


	public void releaseConnection(AbstractConnection conn)
			throws NotSupportedException {
		connectionPool.release(conn);
	}

	

	/**
	 * 비동기 입출력용 소켓 읽기/쓰기 쓰레드들을 중지한다. 동기 모드인 경우에 호출할 경우 아무 동작 없다.
	 */
	/*public void stopPool() {
		if (null != outputMessageReaderPool) {
			outputMessageReaderPool.stopAll();
		}
	}
*/

	

	/**
	 * @return 클라이언트 프로젝트 정보
	 */
	public ClientProjectMonitorInfo getInfo() {
		ClientProjectMonitorInfo clientProjectMonitorInfo = new ClientProjectMonitorInfo();
		// clientProjectMonitorInfo.projectName = projectName;
		// clientProjectMonitorInfo.dataPacketBufferQueueSize = dataPacketBufferQueue.size();

		/*clientProjectMonitorInfo.usedMailboxCnt = connectionPool
				.getUsedMailboxCnt();
		clientProjectMonitorInfo.totalMailbox = connectionPool
				.getTotalMailbox();*/

		if (connectionPool instanceof NoShareSyncConnectionPool) {
			clientProjectMonitorInfo.inputMessageQueueSize = -1;
			clientProjectMonitorInfo.syncOutputMessageQueueQueueSize = -1;
			clientProjectMonitorInfo.AsynOutputMessageQueueSize = -1;
		}

		return clientProjectMonitorInfo;
	}

	

	

	
	public MessageCodecIF getClientMessageCodec(ClassLoader classLoader,
			String messageID) throws DynamicClassCallException {
		/*String classFullName = new StringBuilder(classLoaderClassPackagePrefixName).append("message.")
				.append(messageID).append(".").append(messageID)
				.append("ClientCodec").toString();*/
		
		String classFullName = ioPartDynamicClassNameUtil.getClientMessageCodecClassFullName(messageID);

		MessageCodecIF messageCodec = null;

		Object valueObj = null;
		try {
			try {
				valueObj = objectCacheManager.getCachedObject(classLoader,
						classFullName);
			} catch (Exception e) {
				String errorMessage = String
						.format("ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::%s",
								classLoader.hashCode(), messageID,
								classFullName, e.toString());
				log.warn(errorMessage, e);
				throw new DynamicClassCallException(errorMessage);
			}
		} catch (IllegalArgumentException e) {
			String errorMessage = String
					.format("ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::IllegalArgumentException::%s",
							classLoader.hashCode(), messageID, classFullName,
							e.getMessage());
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		}

		/*
		 * if (null == valueObj) { String errorMessage = String.format(
		 * "ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::valueObj is null"
		 * , classLoader.hashCode(), messageID, classFullName);
		 * log.warn(errorMessage); new DynamicClassCallException(errorMessage);
		 * }
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
				valueObj = objectCacheManager.getCachedObject(classLoader,
						classFullName);
			} catch (Exception e) {
				String errorMessage = String
						.format("ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::%s",
								classLoader.hashCode(), messageID,
								classFullName, e.toString());
				log.warn(errorMessage, e);
				throw new DynamicClassCallException(errorMessage);
			}
		} catch (IllegalArgumentException e) {
			String errorMessage = String
					.format("ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::IllegalArgumentException::%s",
							classLoader.hashCode(), messageID, classFullName,
							e.getMessage());
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		}

		/*
		 * if (null == valueObj) { String errorMessage = String.format(
		 * "ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::valueObj is null"
		 * , classLoader.hashCode(), messageID, classFullName);
		 * log.warn(errorMessage); new DynamicClassCallException(errorMessage);
		 * }
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

	
	
	/*public void saveSinnoriConfiguration() throws IllegalArgumentException, SinnoriConfigurationException, IOException {
		SinnoriConfigurationManager sinnoriConfigurationManager = SinnoriConfigurationManager.getInstance();
		SinnoriConfiguration sinnoriConfiguration = sinnoriConfigurationManager.getSinnoriRunningProjectConfiguration();
		
		sinnoriConfiguration.applyModifiedSinnoriConfigSequencedProperties();
	}*/
}
