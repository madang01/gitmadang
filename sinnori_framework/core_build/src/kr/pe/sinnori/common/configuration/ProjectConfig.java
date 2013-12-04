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

package kr.pe.sinnori.common.configuration;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Properties;

import kr.pe.sinnori.common.io.dhb.header.DHBMessageHeader;
import kr.pe.sinnori.common.lib.CommonType;

import org.apache.log4j.Logger;



/**
 * 신놀이 프레임 워크 설정파일 내용중 프로젝트명과 1:1 대응하는 부분중 서버/클라이언트 공통 설정 정보
 * @author Jonghoon Won
 *
 */
public class ProjectConfig {
	private Logger log = null;
	private Properties configFileProperties = null;
	
	private String projectName;
	
	/** 메시지 정보 파일들이 위치한 경로 */
	private File messageInfoPath = null;
	
	
	private int dataPacketBufferMaxCntPerMessage;	
	private int dataPacketBufferSize;
	private int messageIDFixedSize;
	/********* 가공 데이터 시작 *********/
	/** 메시지 헤더 크기, 단위 byte */
	private int messageHeaderSize;
	/** 메시지 바디 크기, 단위 byte */
	private int messageBodyMaxSize;
	/********* 가공 데이터 종료 *********/
	private Charset charset;
	private ByteOrder byteOrder;
	private CommonType.MESSAGE_PROTOCOL messageProtocol;
	
	private String serverHost;
	private int serverPort;
	
	private ClientProjectConfig clientProjectInfo = null;
	private ServerProjectConfig serverProjectInfo = null;
	
	
	
	/**
	 * <pre>
	 * 파라미터 프로젝트에 속한 공통 환경 변수의 부분 키에 1:1 대응하는 공통 환경 변수 이름을 반환한다.
	 * 공통 환경 변수의 부분 키에 1:1 대응하는 공통 환경 변수 이름 구조는 <프로젝트명>.common.<부분 키>.value 이다.
	 * </pre>
	 * @param subkey 프로젝트에 속한 공통 환경 변수의 부분 키
	 * @return 프로젝트에 속한 공통 환경 변수의 부분 키에 1:1 대응하는 공통 환경 변수 이름
	 */
	private String getKeyName(String subkey) {
		StringBuffer strBuff = new StringBuffer(projectName);
		strBuff.append(".common.");
		strBuff.append(subkey);
		strBuff.append(".value");
		
		return strBuff.toString();
	}
	
	/**
	 * 생성자
	 * @param projectName 프로젝트 명
	 * @param configFileProperties 환경 설정 파일 내용이 담긴 프로퍼티
	 * @param log 로그
	 */
	public ProjectConfig(String projectName, Properties configFileProperties, Logger log) {
		this.projectName = projectName;
		this.configFileProperties = configFileProperties;
		this.log = log;
		

		String propKey = null;
		String proValue = null;
		
		/******** 메시지 정보 파일이 위치한 경로 시작 **********/
		propKey = getKeyName("message_info.xmlpath");
		proValue = configFileProperties.getProperty(propKey);
		if (null == proValue) {
			log.fatal(String.format("warning:: 메시지 정보 파일 경로[%s][%s]를 지정해 주세요", propKey, proValue));
			System.exit(1);
		} else {
			messageInfoPath = new File(proValue);
		}
		
		if (!messageInfoPath.exists()) {
			log.fatal(String.format("메시지 정보 파일 경로[%s][%s]가 존재하지 않습니다.", propKey, proValue));
			System.exit(1);
		}
		if (!messageInfoPath.isDirectory() || !messageInfoPath.canRead()) {
			log.fatal(String.format("메시지 정보 파일 경로[%s][%s][%s]가 잘못 되었습니다.", 
					propKey, proValue, messageInfoPath.getAbsolutePath()));
			System.exit(1);
		}		
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, proValue, messageInfoPath.getAbsolutePath()));
		/******** 메시지 정보 파일이 위치한 경로 종료 **********/
		
		// body_buffer_max_cnt_per_message
		propKey = getKeyName("data_packet_buffer_max_cnt_per_message");
		proValue = configFileProperties.getProperty(propKey);
		if (null == proValue) {
			dataPacketBufferMaxCntPerMessage = 10;
		} else {
			try {
				dataPacketBufferMaxCntPerMessage = Integer.parseInt(proValue);
				if (dataPacketBufferMaxCntPerMessage < 2) dataPacketBufferMaxCntPerMessage = 10;
			} catch(NumberFormatException nfe) {
				dataPacketBufferMaxCntPerMessage = 10;
			}
		}
		
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, proValue, dataPacketBufferMaxCntPerMessage));
		
		
		propKey = getKeyName("data_packet_buffer_size");
		proValue = configFileProperties.getProperty(propKey);
		if (null == proValue) {
			dataPacketBufferSize = 4096;
		} else {
			try {
				dataPacketBufferSize = Integer.parseInt(proValue);
				/** 1024byte 의 배수 아니면 종료 */
				if ((dataPacketBufferSize % 1024) != 0) {
					log.fatal(String.format("%s::prop value[%s], 데이터 패킷 크기는 1024byte 의 배수이어야 합니다.", propKey, proValue));
					System.exit(1);
				}
				if (dataPacketBufferSize < 1024) dataPacketBufferSize = 1024;
			} catch(NumberFormatException nfe) {
				dataPacketBufferSize = 4096;
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, proValue, dataPacketBufferSize));
		
		
		propKey = getKeyName("message_id_fixed_size");
		proValue = configFileProperties.getProperty(propKey);
		if (null == proValue) {
			messageIDFixedSize = 24;
		} else {
			try {
				messageIDFixedSize = Integer.parseInt(proValue);
				if (messageIDFixedSize < 2) messageIDFixedSize = 2;
			} catch(NumberFormatException nfe) {
				messageIDFixedSize = 24;
			}
		}		
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, proValue, messageIDFixedSize));
		
		
		propKey = getKeyName("charset");
		proValue = configFileProperties.getProperty(propKey);
		if (null == proValue) {
			charset = Charset.forName("UTF-8");
		} else {
			try {
				charset = Charset.forName(proValue);
			} catch(IllegalCharsetNameException e) {
				charset = Charset.forName("UTF-8");
			} catch(UnsupportedCharsetException e) {
				charset = Charset.forName("UTF-8");
			}
		}		
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, proValue, charset.name()));
		
		propKey = getKeyName("byteorder");
		proValue = configFileProperties.getProperty(propKey);
		if (null == proValue) {
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			if (proValue.equals("LITTLE_ENDIAN")) {
				byteOrder = ByteOrder.LITTLE_ENDIAN;
			} else if (proValue.equals("BIG_ENDIAN")) {
				byteOrder = ByteOrder.BIG_ENDIAN;
			} else {
				byteOrder = ByteOrder.LITTLE_ENDIAN;
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, proValue, byteOrder.toString()));
		
		propKey = getKeyName("message_protocol");
		proValue = configFileProperties.getProperty(propKey);
		if (null == proValue) {
			messageProtocol = CommonType.MESSAGE_PROTOCOL.DHB;
		} else {
			if (proValue.equals("DHB")) {
				messageProtocol = CommonType.MESSAGE_PROTOCOL.DHB;
			} else if (proValue.equals("DJSON")) {
				messageProtocol = CommonType.MESSAGE_PROTOCOL.DJSON;
			} else {
				log.fatal(String.format("지원하지 않는 이진형식[%s] 입니다.", proValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, proValue, messageProtocol.toString()));
		
		
		
		propKey = getKeyName("host");
		proValue = configFileProperties.getProperty(propKey);
		if (null == proValue) {
			serverHost = "localhost";
		} else {
			serverHost = proValue;
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, proValue, serverHost));
		
		propKey = getKeyName("port");
		proValue = configFileProperties.getProperty(propKey);
		if (null == proValue) {
			serverPort = 9090;
		} else {
			try {
				serverPort = Integer.parseInt(proValue);
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, proValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, proValue, serverPort));
		
		
		/** 가공 데이터 시작 */
		DHBMessageHeader messageHeader = new DHBMessageHeader(messageIDFixedSize);
		messageHeaderSize = messageHeader.messageHeaderSize;
		messageBodyMaxSize = dataPacketBufferSize - messageHeaderSize;
		/** 가공 데이터 종료 */
		
		clientProjectInfo = new ClientProjectConfig(projectName, configFileProperties, log);
		serverProjectInfo = new ServerProjectConfig(projectName, configFileProperties, log);
	}

	/**
	 * @return 프로젝트 명
	 */
	public String getProjectName() {
		return projectName;
	}
	
	/**
	 * @return 메시지 정보 파일들이 위치한 경로
	 */
	public File getMessageInfoPath() {
		return messageInfoPath;
	}

	/**
	 * @return 1개 메시지당 할당 받을 수 있는 바디 버퍼 최대수, 다른 말로 가변 크기 스트림에서 가질 수 있는 바디 버퍼 최대수
	 */
	public int getDataPacketBufferMaxCntPerMessage() {
		return dataPacketBufferMaxCntPerMessage;
	}
	
	/**
	 * @return 데이터 패킷 최대 크기
	 */
	public int getDataPacketBufferSize() {
		return dataPacketBufferSize;
	}
	
	/**
	 * @return 문자열인 메시지 식별자 최대 크기, 단위 byte
	 */
	public int getMessageIDFixedSize() {
		return messageIDFixedSize;
	}
	
	/**
	 * @return 메시시 헤더 크기
	 */
	public int getMessageHeaderSize() {
		return messageHeaderSize;
	}

	
	
	/**
	 * @return 메시지 바디 최대 크기
	 */
	public int getMessageBodyMaxSize() {
		return messageBodyMaxSize;
	}

	
	
	/**
	 * @return 문자셋
	 */
	public Charset getCharset() {
		return charset;
	}
	
	/**
	 * @return 바이트 오더
	 */
	public ByteOrder getByteOrder() {
		return byteOrder;
	}
	
	/**
	 * @return 이진 형식 종류
	 */
	public CommonType.MESSAGE_PROTOCOL getBinaryFormatType() {
		return messageProtocol;
	}
	
	
	/**
	 * @return 서버 호스트 주소
	 */
	public String getServerHost() {
		return serverHost;
	}
	
	/**
	 * @return 서버 포트
	 */	
	public int getServerPort() {
		return serverPort;
	}
	
	/**
	 * 특정 프로젝트의 서버 호스트 정보(주소, 포트)를 변경한다.
	 * @param newServerHost 새로운 서버 호스트 주소
	 * @param newServerPrt 새로운 서버 포트
	 */
	public void changeServerHostInfo(String newServerHost, int newServerPort) {
		serverHost  = newServerHost;
		serverPort = newServerPort;
		
		String prop_key = getKeyName("host");
		configFileProperties.setProperty(prop_key, newServerHost);

		prop_key = getKeyName("port");		
		configFileProperties.setProperty(prop_key, String.valueOf(newServerPort));
	}
	
	/**
	 * @return 프로젝트에 속한 클라이언트 정보
	 */
	public ClientProjectConfig getClientProjectInfo() {
		return clientProjectInfo;
	}
	
	/**
	 * @return 프로젝트에 속한 서버 정보
	 */
	public ServerProjectConfig getServerProjectInfo() {
		return serverProjectInfo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProjectInfo [projectName=");
		builder.append(projectName);
		builder.append(", messageInfoPath=");
		builder.append(messageInfoPath.getAbsolutePath());
		builder.append(", dataPacketBufferMaxCntPerMessage=");
		builder.append(dataPacketBufferMaxCntPerMessage);
		builder.append(", dataPacketBufferSize=");
		builder.append(dataPacketBufferSize);
		builder.append(", messageIDFixedSize=");
		builder.append(messageIDFixedSize);
		builder.append(", messageHeaderSize=");
		builder.append(messageHeaderSize);
		builder.append(", messageBodyMaxSize=");
		builder.append(messageBodyMaxSize);
		builder.append(", charset=");
		builder.append(charset);
		builder.append(", byteOrder=");
		builder.append(byteOrder);
		builder.append(", messageProtocol=");
		builder.append(messageProtocol);		
		builder.append(", serverHost=");
		builder.append(serverHost);
		builder.append(", serverPort=");
		builder.append(serverPort);
		builder.append(", clientProjectInfo=");
		builder.append(clientProjectInfo.toString());
		builder.append(", serverProjectInfo=");
		builder.append(serverProjectInfo.toString());		
		builder.append("]");
		return builder.toString();
	}	
}
