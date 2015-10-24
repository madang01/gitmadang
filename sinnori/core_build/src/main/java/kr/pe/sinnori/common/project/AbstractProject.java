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
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.config.part.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.etc.ObjectCacheManager;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.dhb.DHBMessageProtocol;
import kr.pe.sinnori.common.protocol.djson.DJSONMessageProtocol;
import kr.pe.sinnori.common.protocol.thb.THBMessageProtocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 프로젝트 부모 추상화 클래스. 서버/클라이언트 프로젝트 공통 분모를 모은 추상화 클래스이다.
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractProject implements DataPacketBufferQueueManagerIF {
	protected Logger log = LoggerFactory.getLogger(AbstractProject.class);
	
	/** 모니터 객체 */
	private final Object dataPacketBufferQueueMonitor = new Object();
	
	protected String projectName = null;
	protected String hostOfProject = null;
	protected int portOfProject;
	protected ByteOrder byteOrderOfProject = null;
	protected Charset charsetOfProject = null;
	protected String classLoaderClassPackagePrefixName = null;
	private int dataPacketBufferMaxCntPerMessage;
	private int dataPacketBufferSize;
	

	protected ObjectCacheManager objectCacheManager = ObjectCacheManager
			.getInstance();	
	
	protected MessageProtocolIF messageProtocol = null;
	
	/** 데이터 패킷 버퍼 큐 */
	protected LinkedBlockingQueue<WrapBuffer> dataPacketBufferQueue = null;

	/**
	 * 생성자
	 * 
	 * @param projectName
	 *            프로젝트 이름
	 * @throws NoMoreDataPacketBufferException 
	 */
	public AbstractProject(ProjectPartConfiguration projectPart) throws NoMoreDataPacketBufferException {
		//this.projectPart = projectPart;		
		projectName = projectPart.getProjectName();
		hostOfProject = projectPart.getServerHost();
		portOfProject = projectPart.getServerPort();
		byteOrderOfProject = projectPart.getByteOrder();
		charsetOfProject = projectPart.getCharset();
		classLoaderClassPackagePrefixName = projectPart.getClassLoaderClassPackagePrefixName();
		dataPacketBufferSize = projectPart.getDataPacketBufferSize();
		dataPacketBufferMaxCntPerMessage = projectPart.getDataPacketBufferMaxCntPerMessage();
		
		int messageIDFixedSize = projectPart.getMessageIDFixedSize();		
		int dataPacketBufferCnt = projectPart.getServerDataPacketBufferCnt();
		CommonType.MESSAGE_PROTOCOL_GUBUN messageProtocolGubun = projectPart.getMessageProtocol();
		

		switch (messageProtocolGubun) {
			case DHB: {
				messageProtocol = new DHBMessageProtocol(
						messageIDFixedSize, this);
	
				break;
			}
			case DJSON: {
				messageProtocol = new DJSONMessageProtocol(this);
				break;
			}
			case THB: {
				messageProtocol = new THBMessageProtocol(
						messageIDFixedSize, this);
				break;
			}
			default: {
				log.error(String.format("project[%s] 지원하지 않는 메시지 프로토콜[%s] 입니다.",
						projectPart.getProjectName(), projectPart
								.getMessageProtocol().toString()));
				System.exit(1);
			}
		}
		
		dataPacketBufferQueue = new LinkedBlockingQueue<WrapBuffer>(dataPacketBufferCnt);
		try {
			for (int i = 0; i < dataPacketBufferCnt; i++) {
				WrapBuffer buffer = new WrapBuffer(dataPacketBufferSize, byteOrderOfProject);
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
	public final int getDataPacketBufferMaxCntPerMessage() {
		return dataPacketBufferMaxCntPerMessage;
	}

	@Override
	public int getDataPacketBufferSize() {
		return dataPacketBufferSize;
	}

	@Override
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

}
