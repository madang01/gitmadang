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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.io.SocketInputStream;
import kr.pe.sinnori.common.project.AbstractProject;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.server.classloader.ServerClassLoader;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.io.LetterFromClient;
import kr.pe.sinnori.server.io.LetterToClient;
import kr.pe.sinnori.server.threadpool.accept.processor.AcceptProcessorPool;
import kr.pe.sinnori.server.threadpool.accept.selector.handler.AcceptSelector;
import kr.pe.sinnori.server.threadpool.executor.ExecutorPool;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderPool;
import kr.pe.sinnori.server.threadpool.outputmessage.OutputMessageWriterPool;

/**
 * <pre>
 * 서버 프로젝트 클래스. 프로젝트 소속 서버 기동과 자원을 전담 관리하는 클래스.
 * - 자원 목록 -
 * (1) 데이터 패킷 버퍼 큐
 * (2) 바디 버퍼 큐
 * (3) 클라이언트 자원 해쉬
 * (4) 클라이언트 접속 승인 쓰레드
 * (5) 클라이 언트 등록 담당 쓰레드 폴
 * (6) 입력 메시지 소켓 읽기 담당 쓰레드 폴
 * (7) 비지니스 로직 처리 담당 쓰레드 폴
 * (8) 출력 메시지 소켓 쓰기 담당 쓰레드 폴
 * (9)  접속 승인 큐
 *      클라이언트 접속 승인 쓰레드와 클라이 언트 등록 담당 쓰레드 폴 사이의 큐
 * (10) 입력 메시지 큐
 *      클라이 언트 등록 담당 쓰레드와 비지니스 로직 처리 담당 쓰레드 사이의 큐  
 * (11) 출력 메시지 큐 
 *      비지니스 로직 처리 담당 쓰레드와 출력 메시지 소켓 쓰기 담당 쓰레드 사이의 큐
 * (12) 메시지 정보 해쉬
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public class AnyProjectServer extends AbstractProject implements
		ClientResourceManagerIF, ServerObjectCacheManagerIF, LoginManagerIF {
	private Logger log = LoggerFactory.getLogger(AnyProjectServer.class);

	/** 모니터 객체 */
	// private final Object clientResourceMonitor = new Object();

	/** 동적 클래스인 서버 타스크 객체 운영에 관련된 변수 시작 */
	private final Object monitorOfServerTaskObj = new Object();
	// private final ClassLoader sytemClassLoader =
	// ClassLoader.getSystemClassLoader();
	private ServerClassLoader workBaseClassLoader = null;
	private HashMap<String, ServerTaskObjectInfo> className2ServerTaskObjectInfoHash = new HashMap<String, ServerTaskObjectInfo>();

	private File serverAPPINFPath = null;
	/** 동적 클래스인 서버 타스크 객체 운영에 관련된 변수 종료 */

	/** 접속 승인 큐 */
	private LinkedBlockingQueue<SocketChannel> acceptQueue = null;
	/** 입력 메시지 큐 */
	private LinkedBlockingQueue<LetterFromClient> inputMessageQueue = null;
	/** 출력 메시지 큐 */
	private LinkedBlockingQueue<LetterToClient> outputMessageQueue = null;

	/** 클라이언트 접속 승인 쓰레드 */
	private AcceptSelector acceptSelector = null;
	/** 클라이 언트 등록 담당 쓰레드 폴 */
	private AcceptProcessorPool acceptProcessorPool = null;
	/** 입력 메시지 소켓 읽기 담당 쓰레드 폴 */
	private InputMessageReaderPool inputMessageReaderPool = null;
	/** 비지니스 로직 처리 담당 쓰레드 폴 */
	private ExecutorPool executorPool = null;
	/** 출력 메시지 소켓 쓰기 담당 쓰레드 폴 */
	private OutputMessageWriterPool outputMessageWriterPool = null;

	// private SinnoriClassLoader classLoader = null;

	/** 클라이언트 자원 해쉬 */
	private Hashtable<SocketChannel, ClientResource> scToClientResourceHash = new Hashtable<SocketChannel, ClientResource>();

	private Hashtable<String, ClientResource> loginIDToSCHash = new Hashtable<String, ClientResource>();

	private ServerProjectMonitor serverProjectMonitor = null;

	

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

		String sinnoriInstalledPathString = System
				.getProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH);
		
		String serverAPPINFPathString = BuildSystemPathSupporter
				.getServerAPPINFPathString(sinnoriInstalledPathString, projectPartConfiguration.getProjectName());

		this.serverAPPINFPath = new File(serverAPPINFPathString);
		
		if (!serverAPPINFPath.exists()) {
			String errorMessage = String.format("the server APP-INF path[%s] doesn't exist", serverAPPINFPathString);
		 	throw new SinnoriConfigurationException(errorMessage);
		}
		
		if (!serverAPPINFPath.isDirectory()) {
			String errorMessage = String.format("the server APP-INF path[%s] isn't a directory", serverAPPINFPathString);
		 	throw new SinnoriConfigurationException(errorMessage);
		}
		
		long acceptSelectTimeout = projectPartConfiguration.getServerAcceptSelectorTimeout();
		int maxClients = projectPartConfiguration.getServerMaxClients();
		int acceptProcessorSize = projectPartConfiguration.getServerAcceptProcessorSize();
		int acceptProcessorMaxSize = projectPartConfiguration
				.getServerAcceptProcessorMaxSize();

		int inputMessageReaderSize = projectPartConfiguration
				.getServerInputMessageReaderSize();
		int inputMessageReaderMaxSize = projectPartConfiguration
				.getServerInputMessageReaderMaxSize();
		long readSelectorWakeupInterval = projectPartConfiguration
				.getServerReadSelectorWakeupInterval();

		int executorProcessorSize = projectPartConfiguration
				.getServerExecutorProcessorSize();
		int executorProcessorMaxSize = projectPartConfiguration
				.getServerExecutorProcessorMaxSize();

		int outputMessageWriterSize = projectPartConfiguration
				.getServerOutputMessageWriterSize();
		int outputMessageWriterMaxSize = projectPartConfiguration
				.getServerOutputMessageWriterMaxSize();

		

		int serverAcceptQueueSize = projectPartConfiguration.getServerAcceptQueueSize();
		int serverInputMessageQueueSize = projectPartConfiguration
				.getServerInputMessageQueueSize();
		int serverOutputMessageQueueSize = projectPartConfiguration
				.getServerOutputMessageQueueSize();

		long serverMonitorTimeInterval = projectPartConfiguration
				.getServerMonitorTimeInterval();
		long serverMonitorReceptionTimeout = projectPartConfiguration
				.getServerMonitorReceptionTimeout();

		
		workBaseClassLoader = new ServerClassLoader(projectName,
				serverAPPINFPathString,
				classLoaderClassPackagePrefixName);

		acceptQueue = new LinkedBlockingQueue<SocketChannel>(
				serverAcceptQueueSize);
		inputMessageQueue = new LinkedBlockingQueue<LetterFromClient>(
				serverInputMessageQueueSize);
		outputMessageQueue = new LinkedBlockingQueue<LetterToClient>(
				serverOutputMessageQueueSize);

		acceptSelector = new AcceptSelector(projectName, hostOfProject,
				portOfProject, acceptSelectTimeout, maxClients, acceptQueue, this);

		inputMessageReaderPool = new InputMessageReaderPool(projectName,
				inputMessageReaderSize, inputMessageReaderMaxSize,
				charsetOfProject, readSelectorWakeupInterval,
				inputMessageQueue, messageProtocol, this, this);

		acceptProcessorPool = new AcceptProcessorPool(projectName,
				acceptProcessorSize, acceptProcessorMaxSize, acceptQueue,
				inputMessageReaderPool);

		executorPool = new ExecutorPool(projectName, executorProcessorSize,
				executorProcessorMaxSize, charsetOfProject,
				inputMessageQueue, outputMessageQueue, messageProtocol, this,
				this);

		outputMessageWriterPool = new OutputMessageWriterPool(projectName,
				outputMessageWriterSize, outputMessageWriterMaxSize,
				outputMessageQueue, this);

		serverProjectMonitor = new ServerProjectMonitor(
				serverMonitorTimeInterval, serverMonitorReceptionTimeout);

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

		while (!inputMessageQueue.isEmpty()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		executorPool.stopAll();

		while (!outputMessageQueue.isEmpty()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		outputMessageWriterPool.stopAll();
	}

	@Override
	public void addNewClient(SocketChannel clientSC)
			throws NoMoreDataPacketBufferException {
		// synchronized (clientResourceMonitor) {
		ClientResource clientResource = scToClientResourceHash.get(clientSC);
		if (null != clientResource) {
			log.warn(String.format("클라이언트 자원에서 신규 클라이언트[%d] 등록시 중복 시도",
					clientSC.hashCode()));
			return;
		}

		clientResource = new ClientResource(clientSC, projectName,
				new SocketInputStream(dataPacketBufferMaxCntPerMessage, this));

		scToClientResourceHash.put(clientSC, clientResource);
		// }
	}

	@Override
	public void removeClient(SocketChannel sc) {
		// synchronized (clientResourceMonitor) {
		try {
			sc.close();
		} catch (IOException e1) {
			log.warn("IOException", e1);
		}

		ClientResource clientResource = scToClientResourceHash.get(sc);
		if (null == clientResource) {
			log.warn(String.format("클라이언트 자원에서 이미 삭제된 소켓 채널[%d]입니다.",
					sc.hashCode()));
			return;
		}

		/**
		 * <pre>
		 * 소켓이 끊어진 상태이기때문에 소켓 연결 여부와 로그인 아이디 null 여부 를 판단하는 
		 * ClientResource::isLogin() 메소드를 호출할 수 없다.
		 * </pre>
		 */
		String loginID = clientResource.getLoginID();
		if (null != loginID) {
			loginIDToSCHash.remove(loginID);
			clientResource.logout();
		}

		scToClientResourceHash.remove(sc);
		// }
	}

	// FIXME!
	public void doLoginSuccess(String loginID, ClientResource clientResource) {
		clientResource.setLoginID(loginID);

		loginIDToSCHash.put(loginID, clientResource);
	}

	public boolean isLogin(String loginID) {
		/*
		 * ClientResource clientResource = loginIDToSCHash.get(loginID); if
		 * (null != clientResource) { // FIXME! 소켓 상태 디버깅
		 * log.info(clientResource.toString()); }
		 */

		return loginIDToSCHash.containsKey(loginID);
	}

	@Override
	public ClientResource getClientResource(SocketChannel sc) {
		ClientResource clientResout = scToClientResourceHash.get(sc);
		return clientResout;
	}

	@Override
	public int getCntOfAllClients() {
		return scToClientResourceHash.size();
	}

	public MessageCodecIF getServerCodec(ClassLoader classLoader,
			String messageID) throws DynamicClassCallException {
		String classFullName = new StringBuilder(
				classLoaderClassPackagePrefixName).append("message.")
				.append(messageID).append(".").append(messageID)
				.append("ServerCodec").toString();

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

	private ServerTaskObjectInfo getServerTaskFromWorkBaseClassload(
			String classFullName) throws DynamicClassCallException {
		Class<?> retClass = null;
		AbstractServerTask serverTask = null;
		try {
			retClass = workBaseClassLoader.loadClass(classFullName);
		} catch (ClassNotFoundException e) {
			// String errorMessage =
			// String.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::ClassNotFoundException",
			// this.hashCode(), classFullName);
			// log.warn("ClassNotFoundException", e);
			throw new DynamicClassCallException(e.getMessage());
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

		String classFileName = workBaseClassLoader
				.getClassFileName(classFullName);

		// log.info("classFileName={}", classFileName);

		File serverTaskClassFile = new File(classFileName);

		return new ServerTaskObjectInfo(serverTaskClassFile, serverTask);
	}

	public AbstractServerTask getServerTask(String messageID)
			throws DynamicClassCallException {

		String classFullName = new StringBuilder(
				classLoaderClassPackagePrefixName).append("servertask.")
				.append(messageID).append("ServerTask").toString();
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
					workBaseClassLoader = new ServerClassLoader(projectName,
							serverAPPINFPath.getAbsolutePath(),
							classLoaderClassPackagePrefixName);
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

		projectInfo.projectName = projectName;
		projectInfo.dataPacketBufferQueueSize = dataPacketBufferQueue.size();

		projectInfo.acceptQueueSize = acceptQueue.size();
		projectInfo.inputMessageQueueSize = inputMessageQueue.size();
		projectInfo.outputMessageQueueSize = outputMessageQueue.size();

		projectInfo.clientCnt = scToClientResourceHash.size();

		Iterator<SocketChannel> scKeyIterator = scToClientResourceHash.keySet()
				.iterator();
		java.util.Date currentTime = new java.util.Date();

		while (scKeyIterator.hasNext()) {
			SocketChannel sc = scKeyIterator.next();

			ClientResource cr = scToClientResourceHash.get(sc);

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
		}

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
						projectName));
			} catch (InterruptedException e) {
				log.warn(String.format(
						"server project[%s] ServerProjectMonitor interrupt",
						projectName), e);
			} catch (Exception e) {
				log.warn(String.format(
						"server project[%s] ServerProjectMonitor unknow error",
						projectName), e);
			}
		}
	}

}
