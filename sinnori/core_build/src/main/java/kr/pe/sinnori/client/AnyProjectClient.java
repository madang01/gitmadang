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
import java.nio.charset.Charset;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.AbstractConnectionPool;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPublicMailbox;
import kr.pe.sinnori.client.connection.asyn.noshare.NoShareAsynConnectionPool;
import kr.pe.sinnori.client.connection.asyn.share.ShareAsynConnectionPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPool;
import kr.pe.sinnori.client.connection.sync.noshare.NoShareSyncConnectionPool;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.project.AbstractProject;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
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

	/** 서버에서 보내는 불특정 다수 메시지를 받는 큐 */
	private LinkedBlockingQueue<WrapReadableMiddleObject> asynOutputMessageQueue = null;

	/** 비동기 방식에서 사용되는 출력 메시지 읽기 쓰레드 */
	private OutputMessageReaderPool outputMessageReaderPool = null;
	
	private InputMessageWriterPool inputMessageWriterPool = null;

	/** 프로젝트의 연결 클래스 폴 */
	private AbstractConnectionPool connectionPool = null;

	private AsynOutputMessageExecutorThread[] asynOutputMessageExecutorThreadList = null;
	// private AsynOutputMessageExecutorThread asynOutputMessageExecutorThread =
	// null;

	// private ClientProjectMonitor clientProjectMonitor = null;

	
	public AnyProjectClient(ProjectPartConfiguration projectPartConfiguration)
			throws NoMoreDataPacketBufferException,
			NoMoreOutputMessageQueueException, InterruptedException {
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
			asynOutputMessageQueue = new LinkedBlockingQueue<WrapReadableMiddleObject>(
					projectPartConfiguration
					.getClientAsynOutputMessageQueueSize());
			
			AsynPublicMailbox asynPublicMailbox = new AsynPublicMailbox(projectPartConfiguration.getClientSocketTimeout(), asynOutputMessageQueue);

			
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

			outputMessageReaderPool.startAll();

			if (projectPartConfiguration.getConnectionType().equals(ConnectionType.ASYN_SHARE)) {				

				connectionPool = new ShareAsynConnectionPool(projectPartConfiguration.getProjectName(), 
						projectPartConfiguration.getServerHost(),
						projectPartConfiguration.getServerPort(),
						projectPartConfiguration.getClientConnectionCount(),
						projectPartConfiguration.getClientSocketTimeout(), 
						projectPartConfiguration.getClientWhetherAutoConnection(),						 
						projectPartConfiguration.getClientAsynShareMailboxCnt(),
						asynPublicMailbox,						
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
						charsetDecoderOfProject,
						messageProtocol,
						inputMessageWriterPool,
						outputMessageReaderPool, 
						dataPacketBufferPoolManager, this);
			} else {
				connectionPool = new NoShareAsynConnectionPool(projectPartConfiguration.getProjectName(), 
						projectPartConfiguration.getServerHost(),
						projectPartConfiguration.getServerPort(),
						projectPartConfiguration.getClientConnectionCount(),  
						projectPartConfiguration.getClientSocketTimeout(), 
						projectPartConfiguration.getClientWhetherAutoConnection(),
						asynPublicMailbox,						
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
						charsetDecoderOfProject,
						messageProtocol, 
						inputMessageWriterPool,
						outputMessageReaderPool, 
						dataPacketBufferPoolManager, this);
			}

			asynOutputMessageExecutorThreadList = new AsynOutputMessageExecutorThread[projectPartConfiguration
			                                                          				.getClientAsynOutputMessageExecutorThreadCnt()];

			for (int i = 0; i < asynOutputMessageExecutorThreadList.length; i++) {
				asynOutputMessageExecutorThreadList[i] = new AsynOutputMessageExecutorThread(projectPartConfiguration.getProjectName(),
						projectPartConfiguration.getCharset(),
						this);
				asynOutputMessageExecutorThreadList[i].start();
			}

		}

		/*clientProjectMonitor = new ClientProjectMonitor(
				clientMonitorTimeInterval, clientMonitorReceptionTimeout);
		clientProjectMonitor.start();*/
	}

	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws IOException, 
			NoMoreDataPacketBufferException, BodyFormatException,
			DynamicClassCallException, ServerTaskException, AccessDeniedException, InterruptedException {
		return connectionPool.sendSyncInputMessage(inputMessage);
	}

	@Override
	public AbstractConnection getConnection() throws InterruptedException,
			NotSupportedException, SocketTimeoutException {
		return connectionPool.getConnection();
	}

	@Override
	public void releaseConnection(AbstractConnection conn)
			throws NotSupportedException {
		connectionPool.release(conn);
	}

	/**
	 * 서버에서 보낸 불특정 다수로 출력 메시지를 얻는다. 단 서버에서 보낸 불특정 다수로 출력 메시지가 들어올 때까지 블락 된다.
	 * 
	 * @return 서버에서 보낸 불특정 다수로 출력 메시지
	 * @throws InterruptedException
	 *             쓰레드 인터럽트
	 */
	public WrapReadableMiddleObject takeServerOutputMessageQueue()
			throws InterruptedException {
		WrapReadableMiddleObject receivedLetter = null;
		receivedLetter = asynOutputMessageQueue.take();
		return receivedLetter;
	}

	/**
	 * 비동기 입출력용 소켓 읽기/쓰기 쓰레드들을 중지한다. 동기 모드인 경우에 호출할 경우 아무 동작 없다.
	 */
	public void stopAsynPool() {
		

		if (null != outputMessageReaderPool) {
			outputMessageReaderPool.stopAll();
		}
	}


	

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
		} else {
			clientProjectMonitorInfo.AsynOutputMessageQueueSize = asynOutputMessageQueue
					.size();
		}

		return clientProjectMonitorInfo;
	}

	/**
	 * <pre>
	 * 서버에서 보내는 익명 메시지 처리 쓰레드.
	 * 처음 지정되는 익명 메시지 처리자는 디폴트 처리자({@link DefaultAsynOutputMessageTask }) 로 익명 메시지 로그만 찍는다. 
	 * 주) 비동기에서만 동작한다.
	 * </pre>
	 * 
	 * @author Won Jonghoon
	 * 
	 */
	private class AsynOutputMessageExecutorThread extends Thread {
		// private final Object monitor = new Object();

		// private LinkedBlockingQueue<OutputMessage> serverOutputMessageQueue =
		// null;
		private AsynOutputMessageTaskIF asynOutputMessageTask = null;
		private ClientObjectCacheManagerIF clientObjectCacheManager = null;
		private ClassLoader systemClassLoader = ClassLoader
				.getSystemClassLoader();
		
		private String projectName = null;
		// private Charset charsetOfProject = null;
		

		/**
		 * 생성자
		 * 
		 * @param projectName
		 *            프로젝트 이름
		 * @param asynOutputMessageQueue
		 *            서버 익명 출력 메시지 큐
		 */
		public AsynOutputMessageExecutorThread(
				String projectName,
				Charset charsetOfProject,
				ClientObjectCacheManagerIF clientObjectCacheManager) {
			// this.serverOutputMessageQueue= serverOutputMessageQueue;
			this.asynOutputMessageTask = new DefaultAsynOutputMessageTask();
			this.clientObjectCacheManager = clientObjectCacheManager;
			this.projectName = projectName;
			// this.charsetOfProject = charsetOfProject;
		}

		private AbstractMessage getMessageFromMiddleReadObj(
				ClassLoader classLoader, WrapReadableMiddleObject receivedLetter)
				throws DynamicClassCallException, BodyFormatException {
			String messageID = receivedLetter.getMessageID();
			int mailboxID = receivedLetter.getMailboxID();
			int mailID = receivedLetter.getMailID();
			Object middleReadObj = receivedLetter.getReadableMiddleObject();

			MessageCodecIF messageCodec = clientObjectCacheManager
					.getClientCodec(classLoader, messageID);

			AbstractMessageDecoder messageDecoder = null;
			try {
				messageDecoder = messageCodec.getMessageDecoder();
			} catch (DynamicClassCallException e) {
				String errorMessage = String.format(
						"클라이언트에서 메시지 식별자[%s]에 해당하는 디코더를 얻는데 실패하였습니다.",
						messageID);
				log.warn("{}, mailboxID=[{}], mailID=[{}]", errorMessage,
						mailboxID, mailID);
				throw new DynamicClassCallException(errorMessage);
			} catch (Exception e) {
				String errorMessage = String
						.format("알 수 없는 원인으로 클라이언트에서 메시지 식별자[%s]에 해당하는 디코더를 얻는데 실패하였습니다.",
								messageID);
				log.warn("{}, mailboxID=[{}], mailID=[{}]", errorMessage,
						mailboxID, mailID);
				throw new DynamicClassCallException(errorMessage);
			}

			AbstractMessage messageObj = null;
			try {
				messageObj = messageDecoder.decode(
						messageProtocol.getSingleItemDecoder(), middleReadObj);
				messageObj.messageHeaderInfo.mailboxID = mailboxID;
				messageObj.messageHeaderInfo.mailID = mailID;
			} catch (BodyFormatException e) {
				String errorMessage = String
						.format("클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s",
								messageID, mailboxID, mailID, e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (OutOfMemoryError e) {
				String errorMessage = String
						.format("메모리 부족으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s",
								messageID, mailboxID, mailID, e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			} catch (Exception e) {
				String errorMessage = String
						.format("알 수 없는 원인으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s",
								messageID, mailboxID, mailID, e.getMessage());
				log.warn(errorMessage);
				throw new BodyFormatException(errorMessage);
			}

			return messageObj;
		}

		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					WrapReadableMiddleObject receivedLetter = asynOutputMessageQueue
							.take();
					AbstractMessage outObj;
					try {
						outObj = getMessageFromMiddleReadObj(systemClassLoader,
								receivedLetter);
					} catch (DynamicClassCallException e) {
						continue;
					} catch (BodyFormatException e) {
						continue;
					}

					try {
						asynOutputMessageTask.doTask(outObj);
					} catch (Exception e) {
						log.warn("unkonwo error in asynOutputMessageTask", e);
					}
				}

				log.info(String
						.format("client project[%s] AnonymousServerMessageProcessorThread loop exit",
								projectName));
				log.warn("Thread loop exit");
			} catch (InterruptedException e) {
				log.warn(
						String.format(
								"client project[%s] AnonymousServerMessageProcessorThread interrupt",
								projectName), e);
			} catch (Exception e) {
				log.warn(
						String.format(
								"client project[%s] AnonymousServerMessageProcessorThread unknown error",
								projectName), e);
			}
		}

		/**
		 * 새로운 서버 익명 메시지 비지니스 로직으로 교체를 한다.
		 * 
		 * @param newAnonymousServerMessageTask
		 *            새로운 서버 익명 메시지 비지니스 로직
		 */
		public void changeAsynOutputMessageTask(
				AsynOutputMessageTaskIF newAsynOutputMessageTask) {
			if (null == newAsynOutputMessageTask) {
				String errorMessage = "parameter newAsynOutputMessageTask is null";
				IllegalArgumentException e = new IllegalArgumentException(
						errorMessage);
				log.warn("IllegalArgumentException", e);
				throw e;
			}
			// synchronized (monitor) {
			asynOutputMessageTask = newAsynOutputMessageTask;
			// }
		}
	}

	@Override
	public void changeAsynOutputMessageTask(
			AsynOutputMessageTaskIF newAsynOutputMessageTask) {
		/**
		 * asynOutputMessageExecutorThread 는 비동기일때만 초기화 되므로 동기일때에는 null 값이다. 따라서
		 * null 값이면 비동기이므로 무시한다.
		 */
		if (null == asynOutputMessageExecutorThreadList)
			return;

		for (int i = 0; i < asynOutputMessageExecutorThreadList.length; i++) {
			asynOutputMessageExecutorThreadList[i]
					.changeAsynOutputMessageTask(newAsynOutputMessageTask);
		}
	}

	/**
	 * 서버 비동기 출력 메시지 처리자 쓰레드 종료
	 */
	public void stopAsynOutputMessageExecutorThread() {
		if (null != asynOutputMessageExecutorThreadList) {
			for (int i = 0; i < asynOutputMessageExecutorThreadList.length; i++) {
				asynOutputMessageExecutorThreadList[i].interrupt();
			}
		}
	}


	public void stop() {
		// log.info(String.format("project[%s] client project stop", projectName));

		stopAsynPool();
		stopAsynOutputMessageExecutorThread();
	}

	@Override
	public MessageProtocolIF getMessageProtocol() {
		return messageProtocol;
	}

	@Override
	public MessageCodecIF getClientCodec(ClassLoader classLoader,
			String messageID) throws DynamicClassCallException {
		String classFullName = null;
		/*new StringBuilder(classLoaderClassPackagePrefixName).append("message.")
				.append(messageID).append(".").append(messageID)
				.append("ClientCodec").toString();*/

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
	public void changeServerAddress(String newServerHost, int newServerPort) throws NotSupportedException {
		/*List<AbstractConnection> connList = connectionPool.getConnectionList();
		for (AbstractConnection conn : connList) {			
			conn.changeServerAddress(newServerHost, newServerPort);			
		}
		
		try {
			SinnoriConfigurationManager.getInstance().getSinnoriRunningProjectConfiguration().changeServerAddressIfDifferent(newServerHost, newServerPort);
		} catch (IOException e) {
			log.warn("It failed to save new server address to the Sinnori config file and Ignore this exception for quiet processing", e);
		}*/
	}

	
	
	/*public void saveSinnoriConfiguration() throws IllegalArgumentException, SinnoriConfigurationException, IOException {
		SinnoriConfigurationManager sinnoriConfigurationManager = SinnoriConfigurationManager.getInstance();
		SinnoriConfiguration sinnoriConfiguration = sinnoriConfigurationManager.getSinnoriRunningProjectConfiguration();
		
		sinnoriConfiguration.applyModifiedSinnoriConfigSequencedProperties();
	}*/
}
