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

package kr.pe.sinnori.common.project;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.etc.ObjectCacheManager;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolManagerIF;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.dhb.DHBMessageProtocol;
import kr.pe.sinnori.common.protocol.djson.DJSONMessageProtocol;
import kr.pe.sinnori.common.protocol.thb.THBMessageProtocol;
import kr.pe.sinnori.common.type.MessageProtocolType;

/**
 * 프로젝트 부모 추상화 클래스. 서버/클라이언트 프로젝트 공통 분모를 모은 추상화 클래스이다.
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractProject implements DataPacketBufferPoolManagerIF {
	protected Logger log = LoggerFactory.getLogger(AbstractProject.class);
	
	/** 모니터 객체 */
	private final Object dataPacketBufferQueueMonitor = new Object();
	
	protected ProjectPartConfiguration projectPartConfiguration = null;
	
	protected String projectName = null;
	protected String hostOfProject = null;
	protected int portOfProject;
	protected ByteOrder byteOrderOfProject = null;
	protected Charset charsetOfProject = null;
	protected CharsetEncoder charsetEncoderOfProject = null;
	protected CharsetDecoder charsetDecoderOfProject = null;
	protected String classLoaderClassPackagePrefixName = null;
	protected int dataPacketBufferMaxCntPerMessage;
	private int dataPacketBufferSize;
	private int dataPacketBufferPoolSize;
	

	protected ObjectCacheManager objectCacheManager = ObjectCacheManager
			.getInstance();	
	
	protected MessageProtocolIF messageProtocol = null;
	
	/** 데이터 패킷 버퍼 큐 */
	protected LinkedBlockingQueue<WrapBuffer> dataPacketBufferQueue = null;

	/**
	 * 생성자
	 * 
	 * @param projectPartConfiguration
	 *            프로젝트 파트 설정 내용
	 * @throws NoMoreDataPacketBufferException 
	 */
	public AbstractProject(ProjectPartConfiguration projectPartConfiguration) throws NoMoreDataPacketBufferException {
		this.projectPartConfiguration = projectPartConfiguration;		
		
		projectName = projectPartConfiguration.getProjectName();
		hostOfProject = projectPartConfiguration.getServerHost();
		portOfProject = projectPartConfiguration.getServerPort();
		byteOrderOfProject = projectPartConfiguration.getByteOrder();
		charsetOfProject = projectPartConfiguration.getCharset();
		charsetEncoderOfProject = CharsetUtil.createCharsetEncoder(charsetOfProject);
		charsetDecoderOfProject = CharsetUtil.createCharsetDecoder(charsetOfProject);
		classLoaderClassPackagePrefixName = projectPartConfiguration.getClassLoaderClassPackagePrefixName();
		dataPacketBufferSize = projectPartConfiguration.getDataPacketBufferSize();
		dataPacketBufferMaxCntPerMessage = projectPartConfiguration.getDataPacketBufferMaxCntPerMessage();
		
		int messageIDFixedSize = projectPartConfiguration.getMessageIDFixedSize();		
		dataPacketBufferPoolSize = projectPartConfiguration.getDataPacketBufferPoolSize();
		MessageProtocolType messageProtocolGubun = projectPartConfiguration.getMessageProtocol();
		

		switch (messageProtocolGubun) {
			case DHB: {
				messageProtocol = new DHBMessageProtocol(
						messageIDFixedSize, dataPacketBufferMaxCntPerMessage, 
						charsetEncoderOfProject, charsetDecoderOfProject, this);
	
				break;
			}
			case DJSON: {
				messageProtocol = new DJSONMessageProtocol(dataPacketBufferMaxCntPerMessage, 
						charsetEncoderOfProject, charsetDecoderOfProject, this);
				break;
			}
			case THB: {
				messageProtocol = new THBMessageProtocol(
						messageIDFixedSize, dataPacketBufferMaxCntPerMessage, 
						charsetEncoderOfProject, charsetDecoderOfProject, this);
				break;
			}
			default: {
				log.error(String.format("project[%s] 지원하지 않는 메시지 프로토콜[%s] 입니다.",
						projectPartConfiguration.getProjectName(), projectPartConfiguration
								.getMessageProtocol().toString()));
				System.exit(1);
			}
		}
		
		dataPacketBufferQueue = new LinkedBlockingQueue<WrapBuffer>(dataPacketBufferPoolSize);
		try {
			for (int i = 0; i < dataPacketBufferPoolSize; i++) {
				WrapBuffer buffer = new WrapBuffer(false, dataPacketBufferSize, byteOrderOfProject);
				dataPacketBufferQueue.add(buffer);				
			}
		} catch (OutOfMemoryError e) {
			String errorMessage = "OutOfMemoryError";
			log.error(errorMessage, e);
			throw new NoMoreDataPacketBufferException(errorMessage);
		}
	}


	@Override
	public final ByteOrder getByteOrder() {
		return byteOrderOfProject;
	}

	@Override
	public WrapBuffer pollDataPacketBuffer()
			throws NoMoreDataPacketBufferException {
		WrapBuffer buffer = dataPacketBufferQueue.poll();
		if (null == buffer) {
			String errorMessage = String.format(
					"클라이언트 프로젝트[%s]에서 데이터 패킷 버퍼 큐가 부족합니다.",
					projectName);
			throw new NoMoreDataPacketBufferException(errorMessage);
		}

		buffer.queueOut();
		return buffer;
	}

	@Override
	public void putDataPacketBuffer(WrapBuffer buffer) {
		if (null == buffer)
			return;

		/**
		 * 2번 연속 반환 막기
		 */
		synchronized (dataPacketBufferQueueMonitor) {
			if (buffer.isInQueue()) {
				log.warn("project[{}] 데이터 패킷 버퍼 2번 연속 반환 시도", projectName);
				return;
			}
			buffer.queueIn();
		}

		dataPacketBufferQueue.add(buffer);
	}


	@Override
	public int getDataPacketBufferSize() {
		return dataPacketBufferSize;
	}

	
	public String getQueueState() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("DataPacketBufferQueue size=[");
		strBuilder.append(dataPacketBufferQueue.size());
		strBuilder.append("]");
		return strBuilder.toString();
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public Charset getCharset() {
		return charsetOfProject;
	}


	public String getHostOfProject() {
		return hostOfProject;
	}


	public int getPortOfProject() {
		return portOfProject;
	}
	
	@Override
	public int getDataPacketBufferPoolSize() {
		return dataPacketBufferPoolSize;
	}

}
