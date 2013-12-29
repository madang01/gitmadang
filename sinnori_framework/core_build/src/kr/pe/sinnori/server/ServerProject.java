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
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.configuration.ServerProjectConfigIF;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.dhb.header.DHBMessageHeader;
import kr.pe.sinnori.common.lib.AbstractProject;
import kr.pe.sinnori.common.lib.ClassFileFilter;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.MessageInputStreamResourcePerSocket;
import kr.pe.sinnori.common.lib.ReadFileInfo;
import kr.pe.sinnori.common.lib.SinnoriClassLoader;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.server.executor.AbstractServerExecutor;
import kr.pe.sinnori.server.executor.SererExecutorClassLoaderManagerIF;
import kr.pe.sinnori.server.io.LetterFromClient;
import kr.pe.sinnori.server.io.LetterToClient;
import kr.pe.sinnori.server.threadpool.accept.processor.AcceptProcessorPool;
import kr.pe.sinnori.server.threadpool.accept.selector.handler.AcceptSelector;
import kr.pe.sinnori.server.threadpool.executor.ExecutorProcessorPool;
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
 * @author Jonghoon Won
 *
 */
public class ServerProject extends AbstractProject implements ClientResourceManagerIF, SererExecutorClassLoaderManagerIF {
	/** 모니터 객체 */
	// private final Object clientResourceMonitor = new Object();
	
	/** 클래스 파일 정보 해쉬, 키  클래스명, 값 동적으로 로딩한 클래스 파일 정보. */
	private Hashtable<String, ReadFileInfo> loadedClassFileInfoHash = new Hashtable<String, ReadFileInfo>();
	
	/** 접속 승인 큐 */
	private LinkedBlockingQueue<SocketChannel> acceptQueue = null;
	/** 입력 메시지 큐 */
	private LinkedBlockingQueue<LetterFromClient> inputMessageQueue = null;
	/** 출력 메시지 큐  */
	private LinkedBlockingQueue<LetterToClient> outputMessageQueue = null;

	/** 클라이언트 접속 승인 쓰레드 */
	private AcceptSelector acceptSelector = null;
	/** 클라이 언트 등록 담당 쓰레드 폴 */
	private AcceptProcessorPool acceptProcessorPool = null;
	/** 입력 메시지 소켓 읽기 담당 쓰레드 폴 */
	private InputMessageReaderPool inputMessageReaderPool = null;
	/** 비지니스 로직 처리 담당 쓰레드 폴 */
	private ExecutorProcessorPool executorProcessorPool = null;
	/** 출력 메시지 소켓 쓰기 담당 쓰레드 폴 */
	private OutputMessageWriterPool outputMessageWriterPool = null;
	
	/******** 서버 비지니스 로직 시작 **********/
	private String serverExecutorPrefix = null;
	private String serverExecutorSuffix = null;
	private File serverExecutorClassPath = null;
	
	private String classNameRegex = null;
	private SinnoriClassLoader classLoader = null;
	/******** 서버 비지니스 로직 종료 **********/
	
	// private SinnoriClassLoader classLoader = null;
	
	/** 클라이언트 자원 해쉬 */
	private Hashtable<SocketChannel, ClientResource> scToClientResourceHash = new Hashtable<SocketChannel, ClientResource>();

	private Hashtable<String, ClientResource> loginIDToSCHash = new Hashtable<String, ClientResource>();
	
	
	private ServerProjectMonitor serverProjectMonitor = null;
	
	/**
	 * 생성자
	 * @param projectName 프로젝트 이름
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼 부족시 던지는 예외
	 */
	public ServerProject(String projectName) throws NoMoreDataPacketBufferException  {
		super(projectName);
		
		// ServerProjectConfig projectInfo = projectInfo.getServerProjectInfo();
		ServerProjectConfigIF serverProjectConfig = (ServerProjectConfigIF)projectConfig;		
		
		serverExecutorPrefix = serverProjectConfig.getServerExecutorPrefix();
		serverExecutorSuffix = serverProjectConfig.getServerExecutorSuffix();
		serverExecutorClassPath = serverProjectConfig.getServerExecutorClassPath();
		StringBuffer regexBuff = new StringBuffer(serverExecutorPrefix.replaceAll(".", "\\."));
		regexBuff.append("[a-zA-Z][a-zA-Z0-9]*");
		regexBuff.append(serverExecutorSuffix);
		classNameRegex = regexBuff.toString();
		
		/**
		 * <pre>
		 * 클래스 로더는 클래스를 1번만 정의할 수 있다. 신규 일때에는 그대로 두어도 되지만 
		 * 변경일 경우에는 클래스 중복 정의를 할 수 없어 문제가 된다.
		 * 클래스 로더를 다시 생성하면 신규로 생성된 클래스 로더는 
		 * 변경된 클래스를 정의한적이 없기때문에 변경된 클래스를 로딩할 수 있다.
		 * 따라서 매번 클래스 로더를 다시 생성하면 클래스 중복 정의 문제를 피할 수 있게 된다.
		 * 이것이 아래 처럼 매번 클래스 로더를 신규 인스턴화 시킨후 바로 클래스를 로딩하도록 코딩한 이유이다.
		 * </pre>
		 */
		classLoader = new SinnoriClassLoader(
				SinnoriClassLoader.class.getClassLoader(), classNameRegex);
		
		
		loadServerExecutorClass();
		
		int dataPacketBufferCnt = serverProjectConfig.getServerDataPacketBufferCnt();
		
		
		long acceptSelectTimeout = serverProjectConfig.getServerAcceptSelectorTimeout();
		int maxClients = serverProjectConfig.getServerMaxClients();
		
		int acceptProcessorSize = serverProjectConfig.getServerAcceptProcessorSize();
		int acceptProcessorMaxSize = serverProjectConfig.getServerAcceptProcessorMaxSize();
		
		int inputMessageReaderSize = serverProjectConfig.getServerInputMessageReaderSize();
		int inputMessageReaderMaxSize = serverProjectConfig.getServerInputMessageReaderMaxSize();
		long readSelectorWakeupInterval = serverProjectConfig.getServerReadSelectorWakeupInterval();
		
		int executorProcessorSize = serverProjectConfig.getServerExecutorProcessorSize();
		int executorProcessorMaxSize = serverProjectConfig.getServerExecutorProcessorMaxSize();
		
		int outputMessageWriterSize = serverProjectConfig.getServerOutputMessageWriterSize();
		int outputMessageWriterMaxSize = serverProjectConfig.getServerOutputMessageWriterMaxSize();
		
		TreeSet<String> anonymousExceptionInputMessageSet = serverProjectConfig.getServerAnonymousExceptionInputMessageSet();
		Iterator<String> anonymousExceptionInputMessageIter = anonymousExceptionInputMessageSet.iterator();
		while(anonymousExceptionInputMessageIter.hasNext()) {
			try {
				this.createInputMessage(anonymousExceptionInputMessageIter.next());
			} catch (IllegalArgumentException e) {
				log.fatal(String.format("projectName[%s] %s", projectName, e.getMessage()), e);
				System.exit(1);
			} catch (MessageInfoNotFoundException e) {
				log.fatal(String.format("projectName[%s] %s", projectName, e.getMessage()), e);
				System.exit(1);
			}
		}
		
		
		dataPacketBufferQueue = new LinkedBlockingQueue<WrapBuffer>(dataPacketBufferCnt);
		
		try {
			for (int i = 0; i < dataPacketBufferCnt; i++) {
				WrapBuffer buffer = new WrapBuffer(projectConfig.getDataPacketBufferSize());
				dataPacketBufferQueue.add(buffer);
				buffer.getByteBuffer().order(projectConfig.getByteOrder());
			}
		} catch (OutOfMemoryError e) {
			String errorMessage = "OutOfMemoryError";
			log.fatal(errorMessage, e);
			throw new NoMoreDataPacketBufferException(errorMessage);
		}
		
		acceptQueue = new LinkedBlockingQueue<SocketChannel>(
				serverProjectConfig.getServerAcceptQueueSize());
		inputMessageQueue = new LinkedBlockingQueue<LetterFromClient>(
				serverProjectConfig.getServerInputMessageQueueSize());
		outputMessageQueue = new LinkedBlockingQueue<LetterToClient>(
				serverProjectConfig.getServerOutputMessageQueueSize());
		
		
		acceptSelector = new AcceptSelector(projectName, serverProjectConfig.getServerHost(), 
				serverProjectConfig.getServerPort(), acceptSelectTimeout, maxClients, acceptQueue, this);
		
		inputMessageReaderPool = new InputMessageReaderPool(
				inputMessageReaderSize, inputMessageReaderMaxSize,
				readSelectorWakeupInterval,
				serverProjectConfig, inputMessageQueue, messageExchangeProtocol, 
				this, this, this);

		acceptProcessorPool = new AcceptProcessorPool(acceptProcessorSize,
				acceptProcessorMaxSize, serverProjectConfig, acceptQueue, inputMessageReaderPool);		
		
		executorProcessorPool = new ExecutorProcessorPool(
				executorProcessorSize, executorProcessorMaxSize,
				anonymousExceptionInputMessageSet,
				serverProjectConfig,
				inputMessageQueue, outputMessageQueue, 
				this, this, this);

		outputMessageWriterPool = new OutputMessageWriterPool(
				outputMessageWriterSize, outputMessageWriterMaxSize,
				serverProjectConfig, outputMessageQueue, messageExchangeProtocol,
				this, this);
		
		serverProjectMonitor = new ServerProjectMonitor(serverProjectConfig.getServerMonitorTimeInterval(), serverProjectConfig.getServerRequestTimeout());
		
	}
	
	
	/**
	 * 서버 시작
	 */
	synchronized public void startServer() {
		serverProjectMonitor.start();
		outputMessageWriterPool.startAll();
		executorProcessorPool.startAll();
		inputMessageReaderPool.startAll();
		acceptProcessorPool.startAll();
		if (!acceptSelector.isAlive()) acceptSelector.start();
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
		
		if (acceptSelector.isInterrupted()) return;
		
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
		
		executorProcessorPool.stopAll();
		
		while (!outputMessageQueue.isEmpty()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		outputMessageWriterPool.stopAll();
	}
	
	
	private void loadServerExecutorClass() {
		FilenameFilter classFilter = new ClassFileFilter();
		serverExecutorClassPath.list(classFilter);
		
		File[] fileList = serverExecutorClassPath.listFiles();

		for (int i = 0; i < fileList.length; i++) {
			File f = fileList[i];
			if (!f.isFile()) {
				log.warn(String.format("warning :: not file , file name=[%s]", f.getName()));
				continue;
			}

			if (!f.canRead()) {
				log.warn(String.format("warning :: can't read, file name=[%s]",
						f.getName()));
				continue;
			}

			String messageID = null;
			String fileName = f.getName();
					
			// int beginIndex = fileName.indexOf(prefix.replaceAll(".", File.separator));
			int endIndex = fileName.lastIndexOf(serverExecutorSuffix);
			if (-1 == endIndex) {
				log.warn(String.format("warning :: 서버 비지니스 로직 클래스 파일명[%s]을 구성하는 접미어[%s]를 다시 확인해 주세요", 
						f.getName(), serverExecutorSuffix));
				continue;
			}
			
			messageID = fileName.substring(0, endIndex);
			
			String className = getServerExecutorClassName(messageID);
			
			AbstractServerExecutor loadedObject = null;
			try {
				loadedObject = getObejctFromClassFile(className, f);
				ReadFileInfo classResource = new ReadFileInfo(loadedObject, f.lastModified());				
				loadedClassFileInfoHash.put(className, classResource);
				
				log.info(String.format("신규 서버 비지니스 로직 클래스[%s][%d] 로딩 완료", className, loadedObject.hashCode()));
			} catch (DynamicClassCallException e) {
				log.warn("DynamicClassCallException", e);
				continue;
			}
		}
	}
	
	/**
	 * 클래스명을 가지는 클래스 파일을 읽어 인스턴스 객체를 반환한다.
	 * @param className 클래스명
	 * @return 클래스명을 가지는 클래스 파일을 읽어 얻은 인스턴스 객체
	 * @throws DynamicClassCallException 동적 클래스 로딩시 생기는 에러 발생시 던지는 예외
	 */
	private AbstractServerExecutor getObejctFromClassFile(String className, File classFile) throws DynamicClassCallException {
		// FIXME!
		// log.info("className=[%s], classFile=[%s]", className, f.getAbsolutePath());
		
		AbstractServerExecutor loadedObject = null;
		try {
			Class<?> loadedClass = null;
			try {
				loadedClass = classLoader.loadClass(className, classFile);
			} catch (LinkageError e) {				
				log.info("클래스 중복 정의 문제 발생시 신규 클래스 로더를 생성하여 이를 피한다.", e);
				
				/**
				 * <pre>
				 * 클래스 로더는 클래스를 1번만 정의할 수 있다. 신규 일때에는 그대로 두어도 되지만 
				 * 변경일 경우에는 클래스 중복 정의를 할 수 없어 문제가 된다.
				 * 클래스 로더를 다시 생성하면 신규로 생성된 클래스 로더는 
				 * 변경된 클래스를 정의한적이 없기때문에 변경된 클래스를 로딩할 수 있다.
				 * 따라서 클래스가 수정 되어 재 로딩해야 한다면 클래스 로더를 다시 생성해 주어야 
				 * 클래스 중복 정의 문제를 피할 수 있게 된다.
				 * </pre>
				 */
				classLoader = new SinnoriClassLoader(
						SinnoriClassLoader.class.getClassLoader(), classNameRegex);
				
				loadedClass = classLoader.loadClass(className, classFile);
			}
			
			loadedObject = (AbstractServerExecutor)loadedClass.newInstance();
			
		
			
		} catch (ClassFormatError e) {
			String errorMessage = String.format("ClassFormatError::className=[%s]",
					className);
			log.warn( errorMessage, e);

			throw new DynamicClassCallException(errorMessage);
		} catch (ClassNotFoundException e) {
			String errorMessage = String.format("ClassNotFoundException::className=[%s]",
					className);
			log.warn( errorMessage, e);

			throw new DynamicClassCallException(errorMessage);
		} catch (InstantiationException e) {
			String errorMessage = String.format("InstantiationException::className=[%s]",
					className);
			log.warn( errorMessage, e);

			throw new DynamicClassCallException(errorMessage);
		} catch (IllegalAccessException e) {
			String errorMessage = String.format("IllegalAccessException::className=[%s]",
					className);
			log.warn( errorMessage, e);

			throw new DynamicClassCallException(errorMessage);
		} catch (LinkageError e) {
			String errorMessage = String.format("LinkageError::className=[%s]",
					className);
			log.warn( errorMessage, e);

			throw new DynamicClassCallException(errorMessage);
		}
		
		return loadedObject;
	}
	
	/**
	 * 메시지 식별자에 대응하는 비지니스 로직 클래스 이름을 반환한다.
	 * @param messageID 메시지 식별자
	 * @return 메시지 식별자에 대응하는 비지니스 로직 클래스 이름
	 */
	private String getServerExecutorClassName(String messageID) {
		StringBuffer classNameStringBuffer = new StringBuffer(serverExecutorPrefix);
		classNameStringBuffer.append(messageID);
		classNameStringBuffer.append(serverExecutorSuffix);
		return classNameStringBuffer.toString();
	}
	
	@Override
	public AbstractServerExecutor getServerExecutorObject(String messageID) throws IllegalArgumentException, DynamicClassCallException {
		// File serverExecutorClassPath = (File)conf.getResource("server.executor.impl.binary.path.value");
		// String prefix = (String)conf.getResource("server.executor.prefix.value");
		// String suffix = (String)conf.getResource("server.executor.suffix.value");
		
		if (null == messageID) {
			String errorMessage = "파라미터 메시지 식별자가 null 입니다.";
			throw new IllegalArgumentException(errorMessage);
		}

		if (!DHBMessageHeader.IsValidMessageID(messageID)) {
			String errorMessage = String.format("파라미터 메시지 식별자[%s]는 메시지 식별자 이름 규칙을 위반 하였습니다.", messageID);
			/** 중복 로그 일지라도 로그를 남겨서 원인 제거가 필요함. */
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		String className = getServerExecutorClassName(messageID);

		AbstractServerExecutor returnObj = null;
		ReadFileInfo classResource = null;
		
		StringBuffer classFileNameStringBuffer = new StringBuffer(
				serverExecutorClassPath.getAbsolutePath());				
		classFileNameStringBuffer.append(File.separator);
		classFileNameStringBuffer.append(messageID);
		classFileNameStringBuffer.append(serverExecutorSuffix);
		classFileNameStringBuffer.append(".class");
		String classFileName = classFileNameStringBuffer.toString();

		File classFile = new File(classFileName);
		if (!classFile.exists()) {
			String errorMessage = String.format("warning :: not exist file , file name=[%s]", classFileName);					
			throw new DynamicClassCallException(errorMessage);
		}

		if (!classFile.isFile()) {
			String errorMessage = String.format("warning :: not file , file name=[%s]", classFileName);					
			throw new DynamicClassCallException(errorMessage);
		}

		if (!classFile.canRead()) {
			String errorMessage = String.format("warning :: can't read , file name=[%s]", classFileName);					
			throw new DynamicClassCallException(errorMessage);
		}

		// synchronized (loadedClassFileInfoHash) {			
			classResource = loadedClassFileInfoHash.get(className);
			
			if (null == classResource) {

				/** 신규 메시지 정보 파일 추가 */
				AbstractServerExecutor loadedObject = getObejctFromClassFile(className, classFile);
				classResource = new ReadFileInfo(loadedObject, classFile.lastModified());
				loadedClassFileInfoHash.put(className, classResource);
				
				log.info(String.format("신규 서버 비지니스 로직 클래스[%s][%d] 로딩 완료", className, loadedObject.hashCode()));
			} else {
				/** 최근 수정되었다면 재 로딩한다. */
				long lastModified = classFile.lastModified();
				if (lastModified > classResource.lastModified) {
					
					
					
					AbstractServerExecutor newLoadedObject = getObejctFromClassFile(className, classFile);
					classResource.chanageNewClassObject(newLoadedObject, lastModified);
					
					log.info(String.format("수정된 서버 비지니스 로직 클래스[%s][%d] 로딩 완료", className, newLoadedObject.hashCode()));
				}
			}
			returnObj = (AbstractServerExecutor)classResource.resultObject;
		// }

		return returnObj;
	}

	
	@Override
	public void addNewClient(SocketChannel sc)
			throws NoMoreDataPacketBufferException {
		// synchronized (clientResourceMonitor) {
			ClientResource clientResource = scToClientResourceHash.get(sc);
			if (null != clientResource) {
				log.warn(String.format("클라이언트 자원에서 신규 클라이언트[%d] 등록시 중복 시도", sc.hashCode()));
				return;
			}

			clientResource = new ClientResource(sc,
					projectConfig,
					new MessageInputStreamResourcePerSocket(projectConfig.getByteOrder(), this));

			scToClientResourceHash.put(sc, clientResource);
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
				log.warn(String.format("클라이언트 자원에서 이미 삭제된 소켓 채널[%d]입니다.", sc.hashCode()));
				return;
			}
			
			// clientResout.getMessageInputStreamResource().destory();
			if (clientResource.isLogin()) {
				String loginID = clientResource.getLoginID();
				loginIDToSCHash.remove(loginID);
			}
			clientResource.logout();
			

			scToClientResourceHash.remove(sc);			
		// }
	}
	
	// FIXME!
	public void loginOK(String loginID, ClientResource clientResource) {
		clientResource.setLoginID(loginID);
		
		loginIDToSCHash.put(loginID, clientResource);
	}
	
	public boolean isLogin(String loginID) {
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
	
	
	/**
	 * @return 서버 프로젝트 정보
	 */
	public MonitorServerProjectInfo getInfo(long requestTimeout) {
		MonitorServerProjectInfo projectInfo = new MonitorServerProjectInfo();
		
		projectInfo.projectName = projectConfig.getProjectName();
		projectInfo.dataPacketBufferQueueSize = dataPacketBufferQueue.size();
		
		projectInfo.acceptQueueSize = acceptQueue.size();
		projectInfo.inputMessageQueueSize = inputMessageQueue.size();
		projectInfo.outputMessageQueueSize = outputMessageQueue.size();
		
		projectInfo.clientCnt = scToClientResourceHash.size();
		
		Iterator<SocketChannel> scKeyIterator = scToClientResourceHash.keySet().iterator();
		java.util.Date currentTime = new java.util.Date();
		
		while(scKeyIterator.hasNext()) {
			SocketChannel sc = scKeyIterator.next();
			
			ClientResource cr = scToClientResourceHash.get(sc);
			
			MonitorClientInfo monitorClientInfo = new MonitorClientInfo();
			monitorClientInfo.sc = sc;
			monitorClientInfo.cr = cr;
			monitorClientInfo.scHashCode =  sc.hashCode();
			monitorClientInfo.isConnected = sc.isConnected();

			java.util.Date finalReadTime = cr.getFinalReadTime();
			
			
			long elapsedTime = currentTime.getTime() - finalReadTime.getTime();
			if (elapsedTime  >= requestTimeout) {
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
	 * @author Jonghoon won
	 *
	 */
	private class ServerProjectMonitor extends Thread implements CommonRootIF {
		
		private long monitorInterval;
		private long requestTimeout;
		
		public ServerProjectMonitor(long monitorInterval, long requestTimeout) {
			this.monitorInterval = monitorInterval;
			this.requestTimeout = requestTimeout;
		}
		
		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					
					MonitorServerProjectInfo projectInfo = getInfo(requestTimeout);
					
					log.info(projectInfo.toString());
					
					int size = projectInfo.monitorClientInfoList.size();
					
					for (int i=0; i < size; i++) {
						MonitorClientInfo monitorClientInfo =projectInfo.monitorClientInfoList.get(i);
						if (-1 != monitorClientInfo.timeout) {
							// 삭제 대상
							log.info(String.format("server project[%s] ServerProjectMonitor 삭제 대상, %s", projectConfig.getProjectName(), monitorClientInfo.cr.toString()));
							// monitorClientInfo.scHashCode
							removeClient(monitorClientInfo.sc);
						}
					}
					
					Thread.sleep(monitorInterval);
				}
				log.warn(String.format("server project[%s] ServerProjectMonitor loop exit", projectConfig.getProjectName()));
			} catch (InterruptedException e) {
				log.warn(String.format("server project[%s] ServerProjectMonitor interrupt", projectConfig.getProjectName()), e);
			} catch (Exception e) {
				log.warn(String.format("server project[%s] ServerProjectMonitor unknow error", projectConfig.getProjectName()), e);
			}
		}
	}
	
}
