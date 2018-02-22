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

package kr.pe.sinnori.server;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.project.AbstractProject;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.server.classloader.ServerClassLoader;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.threadpool.IEOThreadPoolSetManager;
import kr.pe.sinnori.server.threadpool.IEOThreadPoolSetManagerIF;
import kr.pe.sinnori.server.threadpool.accept.processor.AcceptProcessorPool;
import kr.pe.sinnori.server.threadpool.accept.selector.handler.AcceptSelector;
import kr.pe.sinnori.server.threadpool.executor.ServerExecutorPool;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderPool;
import kr.pe.sinnori.server.threadpool.outputmessage.OutputMessageWriterPool;


public class AnyProjectServer extends AbstractProject implements
		ServerObjectCacheManagerIF {
	private Logger log = LoggerFactory.getLogger(AnyProjectServer.class);

	/** 모니터 객체 */
	// private final Object clientResourceMonitor = new Object();

	/** 동적 클래스인 서버 타스크 객체 운영에 관련된 변수 시작 */
	private final Object monitorOfServerTaskObj = new Object();
	// private final ClassLoader sytemClassLoader =
	// ClassLoader.getSystemClassLoader();
	private ServerClassLoader workBaseClassLoader = null;
	private HashMap<String, ServerTaskObjectInfo> className2ServerTaskObjectInfoHash = new HashMap<String, ServerTaskObjectInfo>();

	/** 동적 클래스인 서버 타스크 객체 운영에 관련된 변수 종료 */

	/** 접속 승인 큐 */
	private LinkedBlockingQueue<SocketChannel> acceptQueue = null;
	/** 입력 메시지 큐 */
	// private LinkedBlockingQueue<FromLetter> inputMessageQueue = null;
	/** 출력 메시지 큐 */
	// private LinkedBlockingQueue<ToLetter> outputMessageQueue = null;

	/** 클라이언트 접속 승인 쓰레드 */
	private AcceptSelector acceptSelector = null;
	/** 클라이 언트 등록 담당 쓰레드 폴 */
	private AcceptProcessorPool acceptProcessorPool = null;
	/** 입력 메시지 소켓 읽기 담당 쓰레드 폴 */
	private InputMessageReaderPool inputMessageReaderPool = null;
	/** 비지니스 로직 처리 담당 쓰레드 폴 */
	private ServerExecutorPool executorPool = null;
	/** 출력 메시지 소켓 쓰기 담당 쓰레드 폴 */
	private OutputMessageWriterPool outputMessageWriterPool = null;

	// private SinnoriClassLoader classLoader = null;	

	private ServerProjectMonitor serverProjectMonitor = null;
	
	
	private ProjectLoginManager projectLoginManager = null;
	private SocketResourceManagerIF socketResourceManager = null;
	
	

	/**
	 * 생성자
	 * 
	 * @param projectPartConfiguration
	 *            프로젝트 파트 설정 내용
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 부족시 던지는 예외
	 */
	public AnyProjectServer(ProjectPartConfiguration projectPartConfiguration)
			throws NoMoreDataPacketBufferException, SinnoriConfigurationException {
		super(projectPartConfiguration);
				
		
		workBaseClassLoader = new ServerClassLoader(projectPartConfiguration.getProjectName(),
				ioPartDynamicClassNameUtil);

		acceptQueue = new LinkedBlockingQueue<SocketChannel>(
				projectPartConfiguration.getServerAcceptQueueSize());
		/*inputMessageQueue = new LinkedBlockingQueue<FromLetter>(
				projectPartConfiguration.getServerInputMessageQueueSize());*/
		/*outputMessageQueue = new LinkedBlockingQueue<ToLetter>(
				projectPartConfiguration.getServerOutputMessageQueueSize());*/
		
		projectLoginManager = new ProjectLoginManager();
		
		IEOThreadPoolSetManagerIF ieoThreadPoolManager = new IEOThreadPoolSetManager();
		socketResourceManager = new SocketResourceManager(charsetDecoderOfProject, 
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), 
						dataPacketBufferPool, 
						projectLoginManager, 
						ieoThreadPoolManager);

		acceptSelector = new AcceptSelector(
				projectPartConfiguration.getProjectName(), 
				projectPartConfiguration.getServerHost(),
				projectPartConfiguration.getServerPort(), 
				projectPartConfiguration.getServerAcceptSelectorTimeout(), 
				projectPartConfiguration.getServerMaxClients(), acceptQueue, socketResourceManager);

		
		acceptProcessorPool = new AcceptProcessorPool(projectPartConfiguration.getProjectName(),
				projectPartConfiguration.getServerAcceptProcessorSize(), 
				projectPartConfiguration.getServerAcceptProcessorMaxSize(), 
				acceptQueue,
				socketResourceManager);
		
		inputMessageReaderPool = new InputMessageReaderPool(
				projectPartConfiguration.getProjectName(),
				projectPartConfiguration.getServerInputMessageReaderSize(), 
				projectPartConfiguration.getServerInputMessageReaderMaxSize(),
				projectPartConfiguration.getServerReadSelectorWakeupInterval(),
				messageProtocol, dataPacketBufferPool, 
				socketResourceManager, ieoThreadPoolManager);		

		executorPool = new ServerExecutorPool(
				projectPartConfiguration.getProjectName(), 
				projectPartConfiguration.getServerExecutorProcessorSize(),
				projectPartConfiguration.getServerExecutorProcessorMaxSize(),
				projectPartConfiguration.getServerInputMessageQueueSize(), 
				messageProtocol, 
				socketResourceManager,
				this, ieoThreadPoolManager);

		outputMessageWriterPool = new OutputMessageWriterPool(
				projectPartConfiguration.getProjectName(),
				projectPartConfiguration.getServerOutputMessageWriterSize(), 
				projectPartConfiguration.getServerOutputMessageWriterMaxSize(),
				projectPartConfiguration.getServerOutputMessageQueueSize(), 
				dataPacketBufferPool, ieoThreadPoolManager);		

		serverProjectMonitor = new ServerProjectMonitor(
				projectPartConfiguration.getServerMonitorTimeInterval(), 
				projectPartConfiguration.getServerMonitorReceptionTimeout());

	}

	/**
	 * 서버 시작
	 */	
	synchronized public void startServer() {		
		serverProjectMonitor.start();
		outputMessageWriterPool.startAll();
		executorPool.startAll();
		inputMessageReaderPool.startAll();
		acceptProcessorPool.startAll();
		if (!acceptSelector.isAlive()) {
			acceptSelector.start();
		}
		/*
		 * while (!acceptSelector.isAlive()) { try { Thread.sleep(100); } catch
		 * (InterruptedException e) { e.printStackTrace(); } }
		 */

	}

	/**
	 * 서버 종료
	 */
	synchronized public void stopServer() {
		serverProjectMonitor.interrupt();

		if (acceptSelector.isInterrupted())
			return;

		acceptSelector.interrupt();

		while (!acceptQueue.isEmpty()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		acceptProcessorPool.stopAll();
		inputMessageReaderPool.stopAll();

		
		executorPool.stopAll();

		

		outputMessageWriterPool.stopAll();
	}

	

	public MessageCodecIF getServerMessageCodec(ClassLoader classLoader,
			String messageID) throws DynamicClassCallException {
		/*String classFullName = new StringBuilder(
				projectPartConfiguration.getClassLoaderClassPackagePrefixName()).append("message.")
				.append(messageID).append(".").append(messageID)
				.append("ServerCodec").toString();*/
		
		String classFullName = ioPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageID);

		MessageCodecIF messageCodec = null;

		Object valueObj = null;
		
		try {
			valueObj = objectCacheManager.getCachedObject(classLoader,
					classFullName);
		} catch (Exception e) {
			String errorMessage = String
					.format("fail to get cached object::ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]",
							classLoader.hashCode(), messageID,
							classFullName);
			log.warn(errorMessage, e);
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

	private ServerTaskObjectInfo getServerTaskFromWorkBaseClassload(
			String classFullName) throws DynamicClassCallException {
		Class<?> retClass = null;
		AbstractServerTask serverTask = null;
		String classFileName = null;
		
		
		synchronized (monitorOfServerTaskObj) {
			try {
				retClass = workBaseClassLoader.loadClass(classFullName);
			} catch (ClassNotFoundException e) {
				// String errorMessage =
				// String.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::ClassNotFoundException",
				// this.hashCode(), classFullName);
				// log.warn("ClassNotFoundException", e);
				throw new DynamicClassCallException(e.getMessage());
			}
			
			classFileName = workBaseClassLoader
					.getClassFileName(classFullName);
		}
		
		
		

		Object retObject = null;
		try {			
			retObject = retClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException e) {
			String errorMessage = String
					.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::InstantiationException",
							this.hashCode(), classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (IllegalAccessException e) {
			String errorMessage = String
					.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::IllegalAccessException",
							this.hashCode(), classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (IllegalArgumentException e) {
			String errorMessage = String
					.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::IllegalArgumentException",
							this.hashCode(), classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (InvocationTargetException e) {
			String errorMessage = String
					.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::InvocationTargetException",
							this.hashCode(), classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (NoSuchMethodException e) {
			String errorMessage = String
					.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::NoSuchMethodException",
							this.hashCode(), classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (SecurityException e) {
			String errorMessage = String
					.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::SecurityException",
							this.hashCode(), classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		}

		/*
		 * if (! (retObject instanceof AbstractServerTask)) { // FIXME! 죽은 코드
		 * 이어여함, 발생시 원인 제거 필요함 String errorMessage =
		 * String.format("ServerClassLoader hashCode=[%d], classFullName=[%s]" +
		 * "::클래스명으로 얻은 객체 타입[%s]이 AbstractServerTask 가 아닙니다.", this.hashCode(),
		 * classFullName, retObject.getClass().getCanonicalName());
		 * 
		 * log.warn(errorMessage); throw new
		 * DynamicClassCallException(errorMessage); }
		 */
		serverTask = (AbstractServerTask) retObject;

		

		// log.info("classFileName={}", classFileName);

		File serverTaskClassFile = new File(classFileName);

		return new ServerTaskObjectInfo(serverTaskClassFile, serverTask);
	}

	public AbstractServerTask getServerTask(String messageID)
			throws DynamicClassCallException {

		/*String classFullName = new StringBuilder(
				projectPartConfiguration.getClassLoaderClassPackagePrefixName()).append("servertask.")
				.append(messageID).append("ServerTask").toString();*/
		
		String classFullName = ioPartDynamicClassNameUtil.getServerTaskClassFullName(messageID);
		
		
		ServerTaskObjectInfo serverTaskObjectInfo = null;
		synchronized (monitorOfServerTaskObj) {
			serverTaskObjectInfo = className2ServerTaskObjectInfoHash
					.get(classFullName);
			if (null == serverTaskObjectInfo) {
				serverTaskObjectInfo = getServerTaskFromWorkBaseClassload(classFullName);

				className2ServerTaskObjectInfoHash.put(classFullName,
						serverTaskObjectInfo);
			} else {
				if (serverTaskObjectInfo.isModifed()) {
					/** 새로운 서버 클래스 로더로 교체 */
					try {
						workBaseClassLoader = new ServerClassLoader(projectPartConfiguration.getProjectName(),
								ioPartDynamicClassNameUtil);
					} catch (SinnoriConfigurationException e) {
						/** dead code */
					}
					serverTaskObjectInfo = getServerTaskFromWorkBaseClassload(classFullName);
					className2ServerTaskObjectInfoHash.put(classFullName,
							serverTaskObjectInfo);
				}
			}
		}
		return serverTaskObjectInfo.getServerTask();
	}
	

	/**
	 * @return 서버 프로젝트 정보
	 */
	public MonitorServerProjectInfo getInfo(long requestTimeout) {
		MonitorServerProjectInfo projectInfo = new MonitorServerProjectInfo();

		projectInfo.projectName = projectPartConfiguration.getProjectName();
		projectInfo.dataPacketBufferQueueSize = dataPacketBufferPool.getDataPacketBufferPoolSize();

		projectInfo.acceptQueueSize = acceptQueue.size();

		// projectInfo.clientCnt = scToClientResourceHash.size();

		/*Iterator<SocketChannel> scKeyIterator = scToClientResourceHash.keySet().iterator();
		java.util.Date currentTime = new java.util.Date();

		while (scKeyIterator.hasNext()) {
			SocketChannel sc = scKeyIterator.next();

			SocketResource cr = scToClientResourceHash.get(sc);

			MonitorClientInfo monitorClientInfo = new MonitorClientInfo();
			monitorClientInfo.sc = sc;
			monitorClientInfo.cr = cr;
			monitorClientInfo.scHashCode = sc.hashCode();
			monitorClientInfo.isConnected = sc.isConnected();

			java.util.Date finalReadTime = cr.getFinalReadTime();

			long elapsedTime = currentTime.getTime() - finalReadTime.getTime();
			if (elapsedTime >= requestTimeout) {
				// 지금은 로그로만 지켜보지만 나중에는 삭제할 것이다.
				monitorClientInfo.timeout = elapsedTime;
			} else {
				monitorClientInfo.timeout = -1;
			}

			projectInfo.monitorClientInfoList.add(monitorClientInfo);
		}*/

		return projectInfo;
	}

	/**
	 * 서버 프로젝트 모니터
	 * 
	 * @author Won Jonghoon
	 * 
	 */
	private class ServerProjectMonitor extends Thread {

		private long monitorInterval;

		// private long requestTimeout;

		public ServerProjectMonitor(long monitorInterval, long requestTimeout) {
			this.monitorInterval = monitorInterval;
			// this.requestTimeout = requestTimeout;
		}

		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {

					/*
					 * MonitorServerProjectInfo projectInfo =
					 * getInfo(requestTimeout);
					 * 
					 * log.info(projectInfo.toString());
					 */

					/*
					 * int size = projectInfo.monitorClientInfoList.size();
					 * 
					 * for (int i=0; i < size; i++) { MonitorClientInfo
					 * monitorClientInfo
					 * =projectInfo.monitorClientInfoList.get(i); if (-1 !=
					 * monitorClientInfo.timeout) { // 삭제 대상
					 * log.info(String.format
					 * ("server project[%s] ServerProjectMonitor 삭제 대상, %s",
					 * projectName, monitorClientInfo.cr.toString())); //
					 * monitorClientInfo.scHashCode
					 * removeClient(monitorClientInfo.sc); } }
					 */

					Thread.sleep(monitorInterval);
				}
				log.warn(String.format(
						"server project[%s] ServerProjectMonitor loop exit",
						projectPartConfiguration.getProjectName()));
			} catch (InterruptedException e) {
				log.warn(String.format(
						"server project[%s] ServerProjectMonitor interrupt",
						projectPartConfiguration.getProjectName()), e);
			} catch (Exception e) {
				log.warn(String.format(
						"server project[%s] ServerProjectMonitor unknow error",
						projectPartConfiguration.getProjectName()), e);
			}
		}
	}

}
