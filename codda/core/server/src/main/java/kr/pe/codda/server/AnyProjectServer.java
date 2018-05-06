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

package kr.pe.codda.server;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CharsetUtil;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.DataPacketBufferPool;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.SocketOutputStreamFactory;
import kr.pe.codda.common.io.SocketOutputStreamFactoryIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.dhb.DHBMessageProtocol;
import kr.pe.codda.common.protocol.thb.THBMessageProtocol;
import kr.pe.codda.server.threadpool.IEOServerThreadPoolSetManager;
import kr.pe.codda.server.threadpool.IEOServerThreadPoolSetManagerIF;
import kr.pe.codda.server.threadpool.accept.selector.AcceptSelector;
import kr.pe.codda.server.threadpool.executor.ServerExecutorPool;
import kr.pe.codda.server.threadpool.outputmessage.OutputMessageWriterPool;


public class AnyProjectServer {
	private InternalLogger log = InternalLoggerFactory.getInstance(AnyProjectServer.class);
	
	private ProjectPartConfiguration projectPartConfiguration = null;
	 
	
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	
	/** 클라이언트 접속 승인 쓰레드 */
	private AcceptSelector acceptSelector = null;
	
	
	/** 비지니스 로직 처리 담당 쓰레드 폴 */
	private ServerExecutorPool executorPool = null;
	/** 출력 메시지 소켓 쓰기 담당 쓰레드 폴 */
	private OutputMessageWriterPool outputMessageWriterPool = null;	
	
	private ServerObjectCacheManager serverObjectCacheManager = null;
	
	private SocketResourceManagerIF socketResourceManager = null;
	
	public AnyProjectServer(ProjectPartConfiguration projectPartConfiguration)
			throws NoMoreDataPacketBufferException, CoddaConfigurationException {
		this.projectPartConfiguration = projectPartConfiguration;		
		
		CharsetEncoder charsetEncoderOfProject = CharsetUtil.createCharsetEncoder(projectPartConfiguration.getCharset());
		CharsetDecoder charsetDecoderOfProject = CharsetUtil.createCharsetDecoder(projectPartConfiguration.getCharset());
		
		boolean isDirect = false;
		this.dataPacketBufferPool = new DataPacketBufferPool(isDirect, 
				projectPartConfiguration.getByteOrder()
						, projectPartConfiguration.getDataPacketBufferSize()
						, projectPartConfiguration.getDataPacketBufferPoolSize());
		

		MessageProtocolIF messageProtocol = null;
		switch (projectPartConfiguration.getMessageProtocolType()) {
			case DHB: {
				messageProtocol = new DHBMessageProtocol( 
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
						charsetEncoderOfProject, charsetDecoderOfProject, 
						dataPacketBufferPool);
	
				break;
			}
			/*case DJSON: {
				messageProtocol = new DJSONMessageProtocol(
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), 
						charsetEncoderOfProject, charsetDecoderOfProject, 
						dataPacketBufferPool);
				break;
			}*/
			case THB: {
				messageProtocol = new THBMessageProtocol( 
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
		
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = new IEOServerThreadPoolSetManager();
		
		SocketOutputStreamFactoryIF socketOutputStreamFactory = 
				new SocketOutputStreamFactory(charsetDecoderOfProject,
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
						dataPacketBufferPool);
		
		
		socketResourceManager = 
				new SocketResourceManager(socketOutputStreamFactory, ieoThreadPoolManager);

		acceptSelector = new AcceptSelector(
				projectPartConfiguration.getProjectName(), 
				projectPartConfiguration.getServerHost(),
				projectPartConfiguration.getServerPort(),  
				projectPartConfiguration.getServerMaxClients(), messageProtocol, socketResourceManager);

		
		serverObjectCacheManager = createNewServerObjectCacheManager();		

		executorPool = new ServerExecutorPool(				 
				projectPartConfiguration.getServerExecutorPoolSize(),
				projectPartConfiguration.getServerExecutorPoolMaxSize(),
				projectPartConfiguration.getProjectName(),
				projectPartConfiguration.getServerInputMessageQueueSize(), 
				messageProtocol, 
				socketResourceManager,
				serverObjectCacheManager,
				ieoThreadPoolManager);

		outputMessageWriterPool = new OutputMessageWriterPool(
				projectPartConfiguration.getServerOutputMessageWriterPoolSize(), 
				projectPartConfiguration.getServerOutputMessageWriterPoolMaxSize(),
				projectPartConfiguration.getProjectName(),
				projectPartConfiguration.getServerOutputMessageQueueSize(), 
				dataPacketBufferPool, ieoThreadPoolManager);

		

	}

	private ServerObjectCacheManager createNewServerObjectCacheManager() throws CoddaConfigurationException {
		
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil(projectPartConfiguration
				.getFirstPrefixDynamicClassFullName());
		
		ServerClassLoaderBuilder serverSimpleClassLoaderBuilder = 
				new ServerClassLoaderBuilder(ioPartDynamicClassNameUtil);
		
		return new ServerObjectCacheManager(serverSimpleClassLoaderBuilder, ioPartDynamicClassNameUtil);
	}
	
	/**
	 * 서버 시작
	 */	
	synchronized public void startServer() {		
		// serverProjectMonitor.start();
		outputMessageWriterPool.startAll();
		executorPool.startAll();
		if (!acceptSelector.isAlive()) {
			acceptSelector.start();
		}
	}

	/**
	 * 서버 종료
	 */
	synchronized public void stopServer() {
		// serverProjectMonitor.interrupt();

		if (! acceptSelector.isInterrupted()) {
			acceptSelector.interrupt();
		}
		
		executorPool.stopAll();		

		outputMessageWriterPool.stopAll();
	}	

	
	
	public String getProjectServerState() {
		StringBuilder pollStateStringBuilder = new StringBuilder();		
		pollStateStringBuilder.append("dataPacketBufferPool.activeSize=");
		pollStateStringBuilder.append(dataPacketBufferPool.size());
		pollStateStringBuilder.append(", ");
		pollStateStringBuilder.append("dataPacketBufferPool.size=");
		pollStateStringBuilder.append(dataPacketBufferPool.getDataPacketBufferPoolSize());
		pollStateStringBuilder.append(", ");
		pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		pollStateStringBuilder.append("socketResourceManager.count=");
		pollStateStringBuilder.append(socketResourceManager.getNumberOfSocketResources());
		
		// pollStateStringBuilder.append(inputMessageReaderPool.getPoolState());
		return pollStateStringBuilder.toString();
	}

}
