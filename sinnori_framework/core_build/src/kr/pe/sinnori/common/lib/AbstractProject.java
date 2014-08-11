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

package kr.pe.sinnori.common.lib;

import java.nio.ByteOrder;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.classload.LoaderAndName2ObjectManager;
import kr.pe.sinnori.common.configuration.CommonProjectConfig;
import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.dhb.DHBMessageProtocol;
import kr.pe.sinnori.common.protocol.djson.DJSONMessageProtocol;
import kr.pe.sinnori.common.protocol.thb.THBMessageProtocol;

/**
 * 프로젝트 부모 추상화 클래스. 서버/클라이언트 프로젝트 공통 분모를 모은 추상화 클래스이다.
 * @author Jonghoon Won
 *
 */
public abstract class AbstractProject implements CommonRootIF, DataPacketBufferQueueManagerIF {
	/** 모니터 객체 */
	private final Object dataPacketBufferQueueMonitor = new Object();
	
	protected LoaderAndName2ObjectManager loaderAndName2ObjectCacheManager = LoaderAndName2ObjectManager.getInstance();
	
	/** 프로젝트 공통 정보 */
	protected  String projectName = null;
	protected ByteOrder byteOrder = null;
	private int dataPacketBufferMaxCntPerMessage;
	protected int dataPacketBufferSize;
	protected String serverHost;
	protected int serverPort;
	
	protected MessageProtocolIF messageProtocol = null;
	
	
	/** 데이터 패킷 버퍼 큐 */
	protected LinkedBlockingQueue<WrapBuffer> dataPacketBufferQueue  = null;
	
	// private CommonProjectConfig commonProjectConfig = null;
	
	
	/**
	 * 생성자
	 * @param projectName 프로젝트 이름 
	 */
	public AbstractProject(String projectName) {
		this.projectName = projectName;
	}
	
	/**
	 * <pre>
	 * 프로젝트 관련 환경 변수를 최소한으로 읽기 위한 기법을 위해 만든 메소드.
	 *  
	 * - 프로젝트에 관련된 환경 변수는 3가지로 구분된다. - 
	 * (1) 서버/클라이언트 공통 환경 변수
	 * (2) 서버 전용 환경 변수
	 * (3) 클라이언트 전용 환경 변수
	 * 
	 * 자식 생성자에서 만든 프로젝트 관련 환경 변수를 다루는 객체를 반환받는다.
	 * 자식 생성자에서 만든 프로젝트 관련 환경 변수를 다루는 객체는 
	 * 모두 {@link CommonProjectConfig} 를 상속 받으며 2가지 종류가 있다.
	 * 
	 * 첫번째 {@link ServerProjectConfig}
	 * 두번째 {@link ClientProjectConfig}
	 * </pre>
	 * 
	 * @return 프로젝트 공통 환경 변수들을 다루는 객체
	 */
	abstract protected  CommonProjectConfig getCommonProjectConfig();
	
	/**
	 * <pre>
	 * 프로젝트 공통 변수 초기화 수행
	 * 주의점 : 자식 생성자에서 반듯이 호출되어야 한다. 
	 * {@link #getCommonProjectConfig()} 에 의존하므로 
	 * 호출전 {@link #getCommonProjectConfig()} 이 먼저 구현되어 있어야 한다.
	 * </pre>
	 */
	protected void initCommon() {
		CommonProjectConfig commonProjectConfig = getCommonProjectConfig();
		// this.commonProjectConfig = conf.getCommonProjectConfig(projectName);
		this.byteOrder = commonProjectConfig.getByteOrder();
		this.dataPacketBufferMaxCntPerMessage = commonProjectConfig.getDataPacketBufferMaxCntPerMessage();
		this.dataPacketBufferSize = commonProjectConfig.getDataPacketBufferSize();
		this.serverHost = commonProjectConfig.getServerHost();
		this.serverPort = commonProjectConfig.getServerPort();
		
		
		switch (commonProjectConfig.getMessageProtocol()) {
			case DHB : {
				messageProtocol = new DHBMessageProtocol(commonProjectConfig.getMessageIDFixedSize(), this);
				
				break;
			}
			case DJSON : {
				messageProtocol = new DJSONMessageProtocol(this);
				break;
			}
			case THB : {
				messageProtocol = new THBMessageProtocol(commonProjectConfig.getMessageIDFixedSize(), this);
				break;
			}
			default : {
				log.error(String.format("project[%s] 지원하지 않는 메시지 프로토콜[%s] 입니다.", projectName, commonProjectConfig.getMessageProtocol().toString()));
				System.exit(1);
			}
		}
	}
	
	
	@Override
	public ByteOrder getByteOrder() {
		return byteOrder;
	}
	
	@Override
	public WrapBuffer pollDataPacketBuffer() throws NoMoreDataPacketBufferException {
		WrapBuffer buffer = dataPacketBufferQueue.poll();
		if (null == buffer) {
			String errorMessage = String.format("클라이언트 프로젝트[%s]에서 데이터 패킷 버퍼 큐가 부족합니다.", projectName);
			throw new NoMoreDataPacketBufferException(errorMessage);
		}
		
		
		buffer.queueOut();
		buffer.getByteBuffer().order(byteOrder);
		
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
				log.warn(String.format("project[%s] 데이터 패킷 버퍼 2번 연속 반환 시도", projectName));
				return;
			}
			buffer.queueIn();
		}

		dataPacketBufferQueue.add(buffer);
	}
	
	@Override
	public int getDataPacketBufferMaxCntPerMessage() {
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
	
}
