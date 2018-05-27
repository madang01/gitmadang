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

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
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
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.server.threadpool.executor.ServerExecutorPool;


public class AnyProjectServer {
	private InternalLogger log = InternalLoggerFactory.getInstance(AnyProjectServer.class);
	
	private String mainProjectName = null;
	private Charset charset = null;
	private ByteOrder byteOrder = null;
	private MessageProtocolType messageProtocolType = null;
	private boolean serverDataPacketBufferIsDirect;
	private int serverDataPacketBufferMaxCntPerMessage;
	private int serverDataPacketBufferSize;
	private int serverDataPacketBufferPoolSize;
		
	 
	
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	
	/** 비지니스 로직 처리 담당 쓰레드 폴 */
	private ServerExecutorPool executorPool = null;
	
	private ServerObjectCacheManager serverObjectCacheManager = null;
	
	
	private ServerIOEventController serverIOEventController = null;
	
	public AnyProjectServer(ProjectPartConfiguration projectPartConfiguration)
			throws NoMoreDataPacketBufferException, CoddaConfigurationException {
		mainProjectName = projectPartConfiguration.getProjectName();
		charset = projectPartConfiguration.getCharset();
		byteOrder = projectPartConfiguration.getByteOrder();
		messageProtocolType = projectPartConfiguration.getMessageProtocolType();
		serverDataPacketBufferIsDirect = projectPartConfiguration.getServerDataPacketBufferIsDirect();
		serverDataPacketBufferMaxCntPerMessage = projectPartConfiguration.getServerDataPacketBufferMaxCntPerMessage();
		serverDataPacketBufferSize = projectPartConfiguration.getServerDataPacketBufferSize();
		serverDataPacketBufferPoolSize = projectPartConfiguration.getServerDataPacketBufferPoolSize();
		
		CharsetEncoder charsetEncoderOfProject = CharsetUtil.createCharsetEncoder(charset);
		CharsetDecoder charsetDecoderOfProject = CharsetUtil.createCharsetDecoder(charset);
		
		
		this.dataPacketBufferPool = new DataPacketBufferPool(serverDataPacketBufferIsDirect, 
				byteOrder, 
				serverDataPacketBufferSize, 
				serverDataPacketBufferPoolSize);
		

		MessageProtocolIF messageProtocol = null;
		switch (messageProtocolType) {
			case DHB: {
				messageProtocol = new DHBMessageProtocol( 
						serverDataPacketBufferMaxCntPerMessage,
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
						serverDataPacketBufferMaxCntPerMessage, 
						charsetEncoderOfProject, charsetDecoderOfProject, dataPacketBufferPool);
				break;
			}
			default: {
				log.error(String.format("project[%s] 지원하지 않는 메시지 프로토콜[%s] 입니다.",
						mainProjectName, messageProtocolType.toString()));
				System.exit(1);
			}
		}
		
		
		SocketOutputStreamFactoryIF socketOutputStreamFactory = 
				new SocketOutputStreamFactory(charsetDecoderOfProject,
						serverDataPacketBufferMaxCntPerMessage,
						dataPacketBufferPool);
		
		serverObjectCacheManager = createNewServerObjectCacheManager();		
		
		serverIOEventController = new ServerIOEventController(projectPartConfiguration,
				socketOutputStreamFactory, 
				messageProtocol,
				dataPacketBufferPool);
		
		executorPool = new ServerExecutorPool(projectPartConfiguration,
				serverIOEventController,
				messageProtocol, 
				serverObjectCacheManager);
		
		serverIOEventController.setServerExecutorPool(executorPool);
		
				
	}

	private ServerObjectCacheManager createNewServerObjectCacheManager() throws CoddaConfigurationException {
		
		ServerClassLoaderBuilder serverSimpleClassLoaderBuilder = 
				new ServerClassLoaderBuilder();
		
		return new ServerObjectCacheManager(serverSimpleClassLoaderBuilder);
	}
	
	/**
	 * 서버 시작
	 */	
	synchronized public void startServer() {
		executorPool.startAll();
		serverIOEventController.start();
	}

	/**
	 * 서버 종료
	 */
	synchronized public void stopServer() {
		// serverProjectMonitor.interrupt();

		if (! serverIOEventController.isInterrupted()) {
			serverIOEventController.interrupt();
		}
		
		executorPool.stopAll();
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
		pollStateStringBuilder.append("numberOfAcceptedConnection=");
		pollStateStringBuilder.append(serverIOEventController.getNumberOfAcceptedConnection());
		
		// pollStateStringBuilder.append(inputMessageReaderPool.getPoolState());
		return pollStateStringBuilder.toString();
	}

}
