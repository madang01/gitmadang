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
import java.util.StringTokenizer;
import java.util.TreeSet;

import kr.pe.sinnori.common.io.dhb.header.DHBMessageHeader;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.lib.CommonType.CONNECTION_TYPE;

import org.apache.log4j.Logger;



/**
 * 신놀이 프레임 워크 설정파일 내용중 프로젝트명과 1:1 대응하는 부분중 서버/클라이언트 공통 설정 정보
 * @author Jonghoon Won
 *
 */
public class ProjectConfig implements ClientProjectConfigIF, ServerProjectConfigIF {
	private Logger log = null;
	private Properties configFileProperties = null;
	
	private String projectName;
	
	/************* common 변수 시작 ******************/
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
	
	/************* common 변수 종료 ******************/
	
	/************* client 변수 종료 ******************/
	/***** 연결 클래스 관련 환경 변수 시작 *****/
	/** 연결 종류 */
	private CONNECTION_TYPE connectionType;

	/** 연결 클래스 갯수 */
	private int clientConnectionCount;
	/** 연결 생성시 자동 접속 여부 */
	private boolean clientWhetherToAutoConnect;
	/** 소켓 타임 아웃 시간 */
	private long clientSocketTimeout;
	/***** 연결 클래스 관련 환경 변수 종료 *****/
	
	/***** 비동기 입출력 지원용 자원 관련 환경 변수 시작 *****/
	/** 클라이언트 비동기 소켓 채널의 연결 확립 최대 시도 횟수 */
	private int clientFinishConnectMaxCall;
	/** 클라이언트 비동기 소켓 채널의 연결 확립을 재 시도 간격 */
	private long clientFinishConnectWaittingTime;
	/** 출력 메시지 큐 크기 */
	private int clientOutputMessageQueueSize;
	/** 메일함 갯수 */
	private int clientShareAsynConnMailboxCnt;	
	/** 입력 메시지 소켓 쓰기 담당 쓰레드 초기 갯수 */
	private int clientInputMessageWriterSize;
	/** 입력 메시지 소켓 쓰기 담당 쓰레드 최대 갯수 */
	private int clientInputMessageWriterMaxSize;
	/** 출력 메시지 소켓 읽기 담당 쓰레드 초기 갯수 */
	private int clientOutputMessageReaderSize;
	/** 출력 메시지 소켓 읽기 담당 쓰레드 최대 갯수 */
	private int clientOutputMessageReaderMaxSize;
	/** 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기 */
	private long clientrReadSelectorWakeupInterval;
	/** 입력 메시지 큐 크기 */
	private int clientInputMessageQueueSize;
	/***** 비동기 입출력 지원용 자원 관련 환경 변수 종료 *****/
	
	/** 데이터 패킷 버퍼 수 */
	private int clientDataPacketBufferCnt;
	
	/***** 모니터 환경 변수 시작 *****/
	private long clientMonitorTimeInterval = 0L;
	private long clientRequestTimeout = 0L;
	/***** 모니터 환경 변수 종료 *****/
	/************* client 변수 종료 ******************/
	
	
	/************* server 변수 종료 ******************/
	private int serverMaxClients;
	// executor.impl.source.path
	// executor.impl.binary.path
	// executor.prefix
	// executor.suffix.value
	/******** 서버 비지니스 로직 시작 **********/
	private String serverExecutorPrefix = null;
	private String serverExecutorSuffix = null;
	private File serverExecutorSourcePath = null;
	private File serverExecutorClassPath = null;
	/******** 서버 비지니스 로직 종료 **********/
	
	/***** 서버 비동기 입출력 지원용 자원 시작 *****/
	private long acceptServerSelectorTimeout;
	private int acceptServerProcessorSize;
	private int serverAcceptProcessorMaxSize;
	private int serverInputMessageReaderSize;
	private int serverInputMessageReaderMaxSize;
	private long serverReadSelectorWakeupInterval;
	private int serverExecutorProcessorSize;
	private int serverExecutorProcessorMaxSize;
	private int serverOutputMessageWriterSize;
	private int serverOutputMessageWriterMaxSize;
	private int serverAcceptQueueSize;
	private int serverInputMessageQueueSize;
	private int serverOutputMessageQueueSize;
	/***** 서버 비동기 입출력 지원용 자원 종료 *****/
		
	
	private int serverDataPacketBufferCnt;
	
	private TreeSet<String> serverAnonymousExceptionInputMessageSet = new TreeSet<String>();
	
	/***** 모니터 환경 변수 시작 *****/
	private long serverMonitorTimeInterval = 0L;
	private long serverRequestTimeout = 0L;
	/***** 모니터 환경 변수 종료 *****/
	/************* server 변수 종료 ******************/
	
	/**
	 * <pre>
	 * 파라미터 프로젝트에 속한 공통 환경 변수의 부분 키에 1:1 대응하는 공통 환경 변수 이름을 반환한다.
	 * 공통 환경 변수의 부분 키에 1:1 대응하는 공통 환경 변수 이름 구조는 <프로젝트명>.common.<부분 키>.value 이다.
	 * </pre>
	 * @param subkey 프로젝트에 속한 공통 환경 변수의 부분 키
	 * @return 프로젝트에 속한 공통 환경 변수의 부분 키에 1:1 대응하는 공통 환경 변수 이름
	 */
	private String getCommonKeyName(String subkey) {
		StringBuffer strBuff = new StringBuffer(projectName);
		strBuff.append(".common.");
		strBuff.append(subkey);
		strBuff.append(".value");
		
		return strBuff.toString();
	}
	
	/**
	 * <pre>
	 * 파라미터 프로젝트에 속한 클라이언트 환경 변수의 부분 키에 1:1 대응하는 클라이언트 환경 변수 이름을 반환한다.
	 * 클라이언트 환경 변수의 부분 키에 1:1 대응하는 클라이언트 환경 변수 이름 구조는 <프로젝트명>.client.<부분 키>.value 이다.
	 * </pre>
	 * @param subkey 프로젝트에 속한 클라이언트 환경 변수의 부분 키
	 * @return 프로젝트에 속한 클라이언트 환경 변수의 부분 키에 1:1 대응하는 클라이언트 환경 변수 이름
	 */
	private String getClientKeyName(String subkey) {
		StringBuffer strBuff = new StringBuffer(projectName);
		strBuff.append(".client.");
		strBuff.append(subkey);
		strBuff.append(".value");		
		return strBuff.toString();
	}
	
	/**
	 * <pre>
	 * 파라미터 프로젝트에 속한 서버 환경 변수의 부분 키에 1:1 대응하는 서버 환경 변수 이름을 반환한다.
	 * 서버 환경 변수의 부분 키에 1:1 대응하는 서버 환경 변수 이름 구조는 <프로젝트명>.server.<부분 키>.value 이다.
	 * </pre>
	 * @param subkey 프로젝트에 속한 서버 환경 변수의 부분 키
	 * @return 프로젝트에 속한 서버 환경 변수의 부분 키에 1:1 대응하는 서버 환경 변수 이름
	 */
	private String getServerKeyName(String subkey) {
		StringBuffer strBuff = new StringBuffer(projectName);
		strBuff.append(".server.");
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
		configCommon(configFileProperties);
		configClientProject(configFileProperties);
		configServerProject(configFileProperties);
	}
	
	/**
	 * 프로젝트의 공통 환경 변수를 읽어와서 저장한다.
	 * @param configFileProperties
	 */
	private void configCommon(Properties configFileProperties) {
		String propKey = null;
		String proValue = null;
		
		/******** 메시지 정보 파일이 위치한 경로 시작 **********/
		propKey = getCommonKeyName("message_info.xmlpath");
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
		propKey = getCommonKeyName("data_packet_buffer_max_cnt_per_message");
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
		
		
		propKey = getCommonKeyName("data_packet_buffer_size");
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
		
		
		propKey = getCommonKeyName("message_id_fixed_size");
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
		
		
		propKey = getCommonKeyName("charset");
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
		
		propKey = getCommonKeyName("byteorder");
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
		
		propKey = getCommonKeyName("message_protocol");
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
		
		
		
		propKey = getCommonKeyName("host");
		proValue = configFileProperties.getProperty(propKey);
		if (null == proValue) {
			serverHost = "localhost";
		} else {
			serverHost = proValue;
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, proValue, serverHost));
		
		propKey = getCommonKeyName("port");
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
		messageHeaderSize = DHBMessageHeader.getMessageHeaderSize(messageIDFixedSize);
		messageBodyMaxSize = dataPacketBufferSize - messageHeaderSize;
		/** 가공 데이터 종료 */	
	}
	
	/**
	 * 프로젝트의 클라이언트 환경 변수를 읽어와서 저장한다.
	 * @param configFileProperties
	 */
	private void configClientProject(Properties configFileProperties) {		
		String propKey = null;
		String propValue = null;
		String startIndexKey = null;
		
		propKey = getClientKeyName("connection.type");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			connectionType = CONNECTION_TYPE.NoShareAsyn;
		} else {
			if (propValue.equals("NoShareAsyn")) {
				connectionType = CONNECTION_TYPE.NoShareAsyn;
			} else if (propValue.equals("ShareAsyn")) {
				connectionType = CONNECTION_TYPE.ShareAsyn;
			} else if (propValue.equals("NoShareSync")) {
				connectionType = CONNECTION_TYPE.NoShareSync;
			} else {
				log.fatal(String.format("warning:: key[%s] set Multi, Single but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, connectionType));

		propKey = getClientKeyName("connection.count");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			clientConnectionCount = 4;
		} else {
			try {
				clientConnectionCount = Integer.parseInt(propValue);
				if (clientConnectionCount < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientConnectionCount));
		
		propKey = getClientKeyName("connection.whether_to_auto_connect");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			clientWhetherToAutoConnect = false;
		} else {
			if (propValue.equals("true")) {
				clientWhetherToAutoConnect = true;
			} else if (propValue.equals("false")) {
				clientWhetherToAutoConnect = false;
			} else {
				log.fatal(String.format("warning:: key[%s] set true, false but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, clientWhetherToAutoConnect));
		
		
		propKey = getClientKeyName("connection.socket_timeout");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			clientSocketTimeout = 5000;
		} else {
			try {
				clientSocketTimeout = Long.parseLong(propValue);
				if (clientSocketTimeout < 1000) {
					log.fatal(String.format("warning:: key[%s] minimum value 1000 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] long but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientSocketTimeout));
		
		
		/***** 비동기 입출력 지원용 자원 시작 *****/
		propKey = getClientKeyName("asyn.finish_connect.max_call");		
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		clientFinishConnectMaxCall = 10;
		if (null != propValue) {
			try {
				clientFinishConnectMaxCall = Integer.parseInt(propValue);
				if (clientFinishConnectMaxCall < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientFinishConnectMaxCall));
		
		propKey = getClientKeyName("asyn.finish_connect.waitting_time");		
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		clientFinishConnectWaittingTime = 10;
		if (null != propValue) {
			try {
				clientFinishConnectWaittingTime = Long.parseLong(propValue);
				if (clientFinishConnectWaittingTime < 0) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientFinishConnectWaittingTime));
		
		propKey = getClientKeyName("asyn.input_message_writer.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		clientInputMessageWriterSize = 2;
		if (null != propValue) {
			try {
				clientInputMessageWriterSize = Integer.parseInt(propValue);
				if (clientInputMessageWriterSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientInputMessageWriterSize));
		
		
		propKey = getClientKeyName("asyn.input_message_writer.max_size");
		propValue = configFileProperties.getProperty(propKey);
		clientInputMessageWriterMaxSize = clientInputMessageWriterSize;
		if (null != propValue) {
			try {
				clientInputMessageWriterMaxSize = Integer.parseInt(propValue);
				
				if (clientInputMessageWriterMaxSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				
				if (clientInputMessageWriterMaxSize < clientInputMessageWriterSize) {
					log.fatal(String.format("warning:: key[%s][%d] less than [%s][%d]", 
							propKey, clientInputMessageWriterMaxSize, startIndexKey, clientInputMessageWriterSize));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientInputMessageWriterMaxSize));
		
		
		
		propKey = getClientKeyName("asyn.output_message_reader.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		clientOutputMessageReaderSize = 4;
		if (null != propValue) {
			try {
				clientOutputMessageReaderSize = Integer.parseInt(propValue);
				
				if (clientOutputMessageReaderSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientOutputMessageReaderSize));
				
		propKey = getClientKeyName("asyn.output_message_reader.max_size");
		propValue = configFileProperties.getProperty(propKey);
		clientOutputMessageReaderMaxSize = clientOutputMessageReaderSize;
		if (null != propValue) {
			try {
				clientOutputMessageReaderMaxSize = Integer.parseInt(propValue);
				if (clientOutputMessageReaderMaxSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				
				if (clientOutputMessageReaderMaxSize < clientOutputMessageReaderSize) {
					log.fatal(String.format("warning:: key[%s][%d] less than [%s][%d]", 
							propKey, clientOutputMessageReaderMaxSize, startIndexKey, clientOutputMessageReaderSize));
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientOutputMessageReaderMaxSize));
		
		propKey = getClientKeyName("asyn.read_selector_wakeup_interval");
		propValue = configFileProperties.getProperty(propKey);
		clientrReadSelectorWakeupInterval = 10;
		if (null != propValue) {
			try {
				clientrReadSelectorWakeupInterval = Long.parseLong(propValue);
				
				if (clientrReadSelectorWakeupInterval < 10) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] long but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientrReadSelectorWakeupInterval));
		
		
		propKey = getClientKeyName("asyn.input_message_queue_size");
		propValue = configFileProperties.getProperty(propKey);
		clientInputMessageQueueSize = 10;
		if (null != propValue) {
			try {
				clientInputMessageQueueSize = Integer.parseInt(propValue);
				if (clientInputMessageQueueSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientInputMessageQueueSize));
		
		/**
		 * 출력 메시지 큐는 Multi 모드이면 메일함의 출력 메시지 큐, Single 모드이면 연결 클래스의 출력 메시지 큐를 뜻한다.
		 */
		propKey = getClientKeyName("asyn.output_message_queue_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			clientOutputMessageQueueSize = 3;
		} else {
			try {
				clientOutputMessageQueueSize = Integer.parseInt(propValue);
				if (clientOutputMessageQueueSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientOutputMessageQueueSize));
		
		propKey = getClientKeyName("asyn.share.mailbox_cnt");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			clientShareAsynConnMailboxCnt = 2;
		} else {
			try {
				clientShareAsynConnMailboxCnt = Integer.parseInt(propValue);
				if (clientShareAsynConnMailboxCnt < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientShareAsynConnMailboxCnt));
		/***** 비동기 입출력 지원용 자원 종료 *****/
		
		/**
		 * 데이터 패킷 버퍼 수, 설정 파일에서 지정할 수 없다. 공식에 의해서 자동적으로 값이 설정된다.
		 * 참고) 소켓 채널 마다 읽기 전용 버퍼가 필요하다.
		 * client.data_packet_buffer_cnt : 소켓 채널 none-blocking 모드일 경우에는 연결 클래스 갯수 만큼의 읽기 전용 버퍼+입력 메시지 소켓 쓰기 담당 쓰레드 최대 갯수
		 * client.data_packet_buffer_cnt : 소켓 채널 blocking 모드일 경우에는 연결 클래스 갯수*2 = 연결 클래스 갯수 만큼의 읽기 전용 버퍼+연결 클래스 갯수 만큼의 쓰기 전용 버퍼
		 */
		propKey = getClientKeyName("data_packet_buffer_cnt");
		int minBufferCnt = 100;
		if (CONNECTION_TYPE.NoShareSync == connectionType) {
			minBufferCnt = clientConnectionCount*2;			
		} else {
			minBufferCnt = clientConnectionCount + clientInputMessageWriterMaxSize;
		}
		
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			clientDataPacketBufferCnt = 100;
		} else {
			try {
				clientDataPacketBufferCnt = Integer.parseInt(propValue);
				if (clientDataPacketBufferCnt < 100) {
					log.fatal(String.format("warning:: key[%s] minimum value 100 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		clientDataPacketBufferCnt += minBufferCnt;
		
		this.log.info(String.format("%s::prop value[%s], new value[%d], 시스템 필요 갯수[%d]", propKey, propValue, clientDataPacketBufferCnt, minBufferCnt));
		
		/***** 모니터 시작 *****/
		propKey = getClientKeyName("monitor.time_interval");		
		propValue = configFileProperties.getProperty(propKey);		
		clientMonitorTimeInterval = 10000L;
		if (null != propValue) {
			try {
				clientMonitorTimeInterval = Long.parseLong(propValue);
				if (clientMonitorTimeInterval < 1000) {
					log.fatal(String.format("warning:: key[%s] minimum value 1000 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientMonitorTimeInterval));
		
		
		propKey = getClientKeyName("monitor.request_timeout");		
		propValue = configFileProperties.getProperty(propKey);		
		clientRequestTimeout = clientSocketTimeout*2;
		if (null != propValue) {
			try {
				clientRequestTimeout = Long.parseLong(propValue);
				if (clientRequestTimeout < clientSocketTimeout) {
					log.fatal(String.format("warning:: key[%s] minimum value %d but value[%s]", propKey, clientSocketTimeout, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, clientRequestTimeout));
		/***** 모니터 종료 *****/
	}

	private void configServerProject(Properties configFileProperties) {		
		String propKey = null;
		String propValue = null;
		String startIndexKey = null;
		
		propKey = getServerKeyName("max_clients");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverMaxClients = 5;
		} else {
			try {
				serverMaxClients = Integer.parseInt(propValue);
				if (serverMaxClients < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, serverMaxClients));
		
		
		/******** 서버 비지니스 로직 시작 **********/
		propKey = getServerKeyName("executor.impl.source.path");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			log.fatal(String.format("warning:: 서버 비지니스 로직 클래스 파일 경로[%s][%s]를 지정해 주세요", propKey, propValue));
			System.exit(1);
		}
		serverExecutorSourcePath = new File(propValue);
		if (!serverExecutorSourcePath.exists()) {
			log.fatal(String.format("서버 비지니스 로직 클래스 파일 경로[%s][%s]가 존재하지 않습니다.", propKey, propValue));
			System.exit(1);
		}
		if (!serverExecutorSourcePath.isDirectory() || !serverExecutorSourcePath.canRead()) {
			log.fatal(String.format("서버 비지니스 로직 클래스 파일 경로[%s][%s][%s]가 잘못 되었습니다.", propKey, propValue, serverExecutorSourcePath.getAbsolutePath()));
			System.exit(1);
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, serverExecutorSourcePath.getAbsolutePath()));
		
		propKey = getServerKeyName("executor.impl.binary.path");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			log.fatal(String.format("warning:: 서버 비지니스 로직 클래스 파일 경로[%s][%s]를 지정해 주세요", propKey, propValue));
			System.exit(1);
		}
		serverExecutorClassPath = new File(propValue);
		if (!serverExecutorClassPath.exists()) {
			log.fatal(String.format("서버 비지니스 로직 클래스 파일 경로[%s][%s]가 존재하지 않습니다.", propKey, propValue));
			System.exit(1);
		}
		if (!serverExecutorClassPath.isDirectory() || !serverExecutorClassPath.canRead()) {
			log.fatal(String.format("서버 비지니스 로직 클래스 파일 경로[%s][%s][%s]가 잘못 되었습니다.", propKey, propValue, serverExecutorClassPath.getAbsolutePath()));
			System.exit(1);
		}		
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, serverExecutorClassPath.getAbsolutePath()));
		
		propKey = getServerKeyName("executor.prefix");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue || 0 == propValue.trim().length()) {
			serverExecutorPrefix = "impl.executor.server.";
		} else {
			serverExecutorPrefix = propValue;
		}		
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, serverExecutorPrefix));
		
		
		propKey = getServerKeyName("executor.suffix");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue || 0 == propValue.trim().length()) {
			serverExecutorSuffix = "SExtor";
		} else {
			serverExecutorSuffix = propValue;
		}		
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, serverExecutorSuffix));
		/******** 서버 비지니스 로직 종료 **********/
		
		
		
		
		/***** 서버 비동기 입출력 지원용 자원 시작 *****/
		propKey = getServerKeyName("pool.accept_selector_timeout");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			acceptServerSelectorTimeout = 10;
		} else {
			try {
				acceptServerSelectorTimeout = Long.parseLong(propValue);
				if (acceptServerSelectorTimeout < 10) {
					log.fatal(String.format("warning:: key[%s] minimum value 10 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, acceptServerSelectorTimeout));
		
		
		propKey = getServerKeyName("pool.accept_processor.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		if (null == propValue) {
			acceptServerProcessorSize = 1;
		} else {
			try {
				acceptServerProcessorSize = Integer.parseInt(propValue);
				if (acceptServerProcessorSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, acceptServerProcessorSize));
		
		
		propKey = getServerKeyName("pool.accept_processor.max_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverAcceptProcessorMaxSize = acceptServerProcessorSize;
		} else {
			try {
				serverAcceptProcessorMaxSize = Integer.parseInt(propValue);
				if (serverAcceptProcessorMaxSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				
				if (serverAcceptProcessorMaxSize < acceptServerProcessorSize) {
					log.fatal(String.format("warning:: key[%s][%d] less than [%s][%d]", propKey, serverAcceptProcessorMaxSize, startIndexKey, acceptServerProcessorSize));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, serverAcceptProcessorMaxSize));
		
		propKey = getServerKeyName("pool.input_message_reader.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		if (null == propValue) {
			serverInputMessageReaderSize = 2;
		} else {
			try {
				serverInputMessageReaderSize = Integer.parseInt(propValue);
				if (serverInputMessageReaderSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, serverInputMessageReaderSize));
		
		
		propKey = getServerKeyName("pool.input_message_reader.max_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverInputMessageReaderMaxSize = serverInputMessageReaderSize;
		} else {
			try {
				serverInputMessageReaderMaxSize = Integer.parseInt(propValue);
				if (serverInputMessageReaderMaxSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				if (serverInputMessageReaderMaxSize < serverInputMessageReaderSize) {
					log.fatal(String.format("warning:: key[%s][%d] less than [%s][%d]", propKey, serverInputMessageReaderMaxSize, startIndexKey, serverInputMessageReaderSize));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, serverInputMessageReaderMaxSize));
		
		propKey = getServerKeyName("pool.read_selector_wakeup_interval");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverReadSelectorWakeupInterval = 10;
		} else {
			try {
				serverReadSelectorWakeupInterval = Long.parseLong(propValue);
				if (serverReadSelectorWakeupInterval < 10) {
					log.fatal(String.format("warning:: key[%s] minimum value 10 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, serverReadSelectorWakeupInterval));
		
		propKey = getServerKeyName("pool.executor_processor.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		if (null == propValue) {
			serverExecutorProcessorSize = 3;
		} else {
			try {
				serverExecutorProcessorSize = Integer.parseInt(propValue);
				if (serverExecutorProcessorSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, serverExecutorProcessorSize));
		
		
		propKey = getServerKeyName("pool.executor_processor.max_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverExecutorProcessorMaxSize = serverExecutorProcessorSize;
		} else {
			try {
				serverExecutorProcessorMaxSize = Integer.parseInt(propValue);
				if (serverExecutorProcessorMaxSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				if (serverExecutorProcessorMaxSize < serverExecutorProcessorSize) {
					log.fatal(String.format("warning:: key[%s][%d] less than [%s][%d]", propKey, serverExecutorProcessorMaxSize, startIndexKey, serverExecutorProcessorSize));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, serverExecutorProcessorMaxSize));
		
		
		propKey = getServerKeyName("pool.output_message_writer.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		if (null == propValue) {
			serverOutputMessageWriterSize = 1;
		} else {
			try {
				serverOutputMessageWriterSize = Integer.parseInt(propValue);
				if (serverOutputMessageWriterSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, serverOutputMessageWriterSize));
		
		
		propKey = getServerKeyName("pool.output_message_writer.max_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverOutputMessageWriterMaxSize = serverOutputMessageWriterSize;
		} else {
			try {
				serverOutputMessageWriterMaxSize = Integer.parseInt(propValue);
				if (serverOutputMessageWriterMaxSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				if (serverOutputMessageWriterMaxSize < serverOutputMessageWriterSize) {
					log.fatal(String.format("warning:: key[%s][%d] less than [%s][%d]", propKey, serverOutputMessageWriterMaxSize, startIndexKey, serverOutputMessageWriterSize));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, serverOutputMessageWriterMaxSize));
		
		
		propKey = getServerKeyName("asyn.accept_queue_size");
		propValue = configFileProperties.getProperty(propKey);
		serverAcceptQueueSize = 10;
		if (null != propValue) {
			try {
				serverAcceptQueueSize = Integer.parseInt(propValue);
				
				if (serverAcceptQueueSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, serverAcceptQueueSize));
		
		
		propKey = getServerKeyName("asyn.input_message_queue_size");
		propValue = configFileProperties.getProperty(propKey);
		serverInputMessageQueueSize = 10;
		if (null != propValue) {
			try {
				serverInputMessageQueueSize = Integer.parseInt(propValue);
				
				if (serverInputMessageQueueSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, serverInputMessageQueueSize));
		
		/**
		 * 출력 메시지 큐는 Multi 모드이면 메일함의 출력 메시지 큐, Single 모드이면 연결 클래스의 출력 메시지 큐를 뜻한다.
		 */
		propKey = getServerKeyName("asyn.output_message_queue_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverOutputMessageQueueSize = 10;
		} else {
			try {
				serverOutputMessageQueueSize = Integer.parseInt(propValue);
				if (serverOutputMessageQueueSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, serverOutputMessageQueueSize));
		/***** 서버 비동기 입출력 지원용 자원 종료 *****/
		
		propKey = getServerKeyName("anonymous_exception_inputmessage_set");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, "empty"));
		} else {
			StringTokenizer anonymousExceptionInputMessageToken = new StringTokenizer(propValue, ",");
			while (anonymousExceptionInputMessageToken.hasMoreTokens()) {
				String token = anonymousExceptionInputMessageToken.nextToken().trim();
				serverAnonymousExceptionInputMessageSet.add(token);
			}
			log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, serverAnonymousExceptionInputMessageSet.toString()));
		}
		
		/**
		 * 데이터 패킷 버퍼 수, 설정 파일에서 지정할 수 없다. 공식에 의해서 자동적으로 값이 설정된다.
		 * 서버에서 데이터 패킷 버퍼 사용처는 2곳이 있다.
		 * (1) 클라이언트 마다 읽기 전용 데이터 패킷 버퍼가 할당되며 소켓 데이터를 읽을때 사용한다.
		 * (2) 또한 출력 메시지 쓰기 쓰레드에서는 소켓 쓰기 전용 데이터 패킷 버퍼 1개를 할당 받아 소켓 쓰기 작업에 사용한다.
		 */
		propKey = getServerKeyName("data_packet_buffer_cnt");
		int minBufferCnt = serverMaxClients + serverOutputMessageWriterMaxSize;
		
		propValue = configFileProperties.getProperty(propKey);		
		if (null == propValue) {
			serverDataPacketBufferCnt = 100;
		} else {
			try {
				serverDataPacketBufferCnt = Integer.parseInt(propValue);
				if (serverDataPacketBufferCnt < 100) {
					log.fatal(String.format("warning:: key[%s] minimum value 100 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		
		serverDataPacketBufferCnt += minBufferCnt;
		
		log.info(String.format("%s::prop value[%s], new value[%d], 시스템 필요 갯수[%d]", propKey, propValue, serverDataPacketBufferCnt, minBufferCnt));
		
		
		/******** 서버 프로젝트 모니터 시작 **********/
		propKey = getServerKeyName("monitor.time_interval");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverMonitorTimeInterval = 10000L;
		} else {
			try {
				serverMonitorTimeInterval = Long.parseLong(propValue);
				if (serverMonitorTimeInterval < 1000L) {
					log.fatal(String.format("warning:: key[%s] minimum value 1000 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}		
		
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, serverMonitorTimeInterval));
		
		propKey = getServerKeyName("monitor.request_timeout");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverRequestTimeout = 10000L;
		} else {
			try {
				serverRequestTimeout = Long.parseLong(propValue);
				if (serverRequestTimeout < 1000L) {
					log.fatal(String.format("warning:: key[%s] minimum value 1000 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, serverRequestTimeout));
		/******** 서버 프로젝트 모니터 종료 **********/
	}
	
	
	@Override
	public String getProjectName() {
		return projectName;
	}
	
	@Override
	public File getMessageInfoPath() {
		return messageInfoPath;
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
	public int getMessageIDFixedSize() {
		return messageIDFixedSize;
	}
	
	@Override
	public int getMessageHeaderSize() {
		return messageHeaderSize;
	}
	
	@Override
	public int getMessageBodyMaxSize() {
		return messageBodyMaxSize;
	}
	
	@Override
	public Charset getCharset() {
		return charset;
	}
	
	@Override
	public ByteOrder getByteOrder() {
		return byteOrder;
	}
	
	@Override
	public CommonType.MESSAGE_PROTOCOL getMessageProtocol() {
		return messageProtocol;
	}
	
	
	@Override
	public String getServerHost() {
		return serverHost;
	}
	
	@Override
	public int getServerPort() {
		return serverPort;
	}
	
	
	
	@Override
	public void setServerHost(String newServerHost) {
		Throwable t = new Throwable("추적용 가짜 예외");
		log.info(String.format("old serverHost[%s] to new serverHost[%s]", 
				serverHost, newServerHost), t);
		
		
		this.serverHost = newServerHost;
		
		String prop_key = getCommonKeyName("host");
		configFileProperties.setProperty(prop_key, newServerHost);
	}

	@Override
	public void setServerPort(int newServerPort) {
		Throwable t = new Throwable("추적용 가짜 예외");
		log.info(String.format("old serverPost[%d] to new serverPost[%d]", 
				serverPort, newServerPort), t);
		
		this.serverPort = newServerPort;
		String prop_key = getCommonKeyName("port");		
		configFileProperties.setProperty(prop_key, String.valueOf(newServerPort));
	}
	
	@Override
	public void changeServerAddress(String newServerHost, int newServerPort) {
		Throwable t = new Throwable("추적용 가짜 예외");
		log.info(String.format("old serverHost[%s] to new serverHost[%s], old serverPost[%d] to new serverPost[%d]", 
				serverHost, newServerHost, serverPort, newServerPort), t);
		
		this.serverHost = newServerHost;
		String prop_key = getCommonKeyName("host");
		configFileProperties.setProperty(prop_key, newServerHost);
		
		this.serverPort = newServerPort;
		prop_key = getCommonKeyName("port");		
		configFileProperties.setProperty(prop_key, String.valueOf(newServerPort));
	}

	/*******************************************************************/
	@Override
	public CONNECTION_TYPE getConnectionType() {
		return connectionType;
	}

	

	@Override
	public int getClientConnectionCount() {
		return clientConnectionCount;
	}

	@Override
	public boolean getClientWhetherToAutoConnect() {
		return clientWhetherToAutoConnect;
	}

	@Override
	public long getClientSocketTimeout() {
		return clientSocketTimeout;
	}

	@Override
	public int getClientOutputMessageQueueSize() {
		return clientOutputMessageQueueSize;
	}

	@Override
	public int getClientShareAsynConnMailboxCnt() {
		return clientShareAsynConnMailboxCnt;
	}
	
	@Override
	public int getClientFinishConnectMaxCall() {
		return clientFinishConnectMaxCall;
	}
	
	@Override
	public long getClientFinishConnectWaittingTime() {
		return clientFinishConnectWaittingTime;
	}
	
	@Override
	public int getClientInputMessageWriterSize() {
		return clientInputMessageWriterSize;
	}

	@Override
	public int getClientInputMessageWriterMaxSize() {
		return clientInputMessageWriterMaxSize;
	}

	@Override
	public int getClientOutputMessageReaderSize() {
		return clientOutputMessageReaderSize;
	}

	@Override
	public int getClientOutputMessageReaderMaxSize() {
		return clientOutputMessageReaderMaxSize;
	}

	@Override
	public long getClientReadSelectorWakeupInterval() {
		return clientrReadSelectorWakeupInterval;
	}

	@Override
	public int getClientInputMessageQueueSize() {
		return clientInputMessageQueueSize;
	}
	
	@Override
	public int getClientDataPacketBufferCnt() {
		return clientDataPacketBufferCnt;
	}
	
	@Override
	public long getClientMonitorTimeInterval() {
		return clientMonitorTimeInterval;
	}
	
	@Override
	public long getClientRequestTimeout() {
		return clientRequestTimeout;
	}

	/*******************************************************************/
	
	@Override
	public int getServerMaxClients() {
		return serverMaxClients;
	}

	@Override
	public String getServerExecutorPrefix() {
		return serverExecutorPrefix;
	}

	@Override
	public String getServerExecutorSuffix() {
		return serverExecutorSuffix;
	}

	@Override
	public File getServerExecutorSourcePath() {
		return serverExecutorSourcePath;
	}

	@Override
	public File getServerExecutorClassPath() {
		return serverExecutorClassPath;
	}

	@Override
	public long getServerAcceptSelectorTimeout() {
		return acceptServerSelectorTimeout;
	}

	@Override
	public int getServerAcceptProcessorSize() {
		return acceptServerProcessorSize;
	}

	@Override
	public int getServerAcceptProcessorMaxSize() {
		return serverAcceptProcessorMaxSize;
	}

	@Override
	public int getServerInputMessageReaderSize() {
		return serverInputMessageReaderSize;
	}

	@Override
	public int getServerInputMessageReaderMaxSize() {
		return serverInputMessageReaderMaxSize;
	}

	@Override
	public long getServerReadSelectorWakeupInterval() {
		return serverReadSelectorWakeupInterval;
	}

	@Override
	public int getServerExecutorProcessorSize() {
		return serverExecutorProcessorSize;
	}

	@Override
	public int getServerExecutorProcessorMaxSize() {
		return serverExecutorProcessorMaxSize;
	}

	@Override
	public int getServerOutputMessageWriterSize() {
		return serverOutputMessageWriterSize;
	}

	@Override
	public int getServerOutputMessageWriterMaxSize() {
		return serverOutputMessageWriterMaxSize;
	}

	@Override
	public int getServerAcceptQueueSize() {
		return serverAcceptQueueSize;
	}

	@Override
	public int getServerInputMessageQueueSize() {
		return serverInputMessageQueueSize;
	}

	@Override
	public int getServerOutputMessageQueueSize() {
		return serverOutputMessageQueueSize;
	}

	@Override
	public int getServerDataPacketBufferCnt() {
		return serverDataPacketBufferCnt;
	}	

	@Override
	public long getServerRequestTimeout() {
		return serverRequestTimeout;
	}

	@Override
	public long getServerMonitorTimeInterval() {
		return serverMonitorTimeInterval;
	}
	
	@Override
	public TreeSet<String> getServerAnonymousExceptionInputMessageSet() {
		return serverAnonymousExceptionInputMessageSet;
	}

	@Override
	public String toCommonString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CommonProject [projectName=");
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
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public String toClientString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientProject [projectName=");
		builder.append(projectName);
		builder.append(", connectionType=");
		builder.append(connectionType);		
		builder.append(", clientConnectionCount=");
		builder.append(clientConnectionCount);
		builder.append(", clientWhetherToAutoConnect=");
		builder.append(clientWhetherToAutoConnect);
		builder.append(", clientSocketTimeout=");
		builder.append(clientSocketTimeout);
		builder.append(", clientOutputMessageQueueSize=");
		builder.append(clientOutputMessageQueueSize);
		builder.append(", clientShareAsynConnMailboxCnt=");
		builder.append(clientShareAsynConnMailboxCnt);
		builder.append(", clientFinishConnectMaxCall=");
		builder.append(clientFinishConnectMaxCall);
		builder.append(", clientFinishConnectWaittingTime=");
		builder.append(clientFinishConnectWaittingTime);		
		builder.append(", clientInputMessageWriterSize=");
		builder.append(clientInputMessageWriterSize);
		builder.append(", clientInputMessageWriterMaxSize=");
		builder.append(clientInputMessageWriterMaxSize);
		builder.append(", clientOutputMessageReaderSize=");
		builder.append(clientOutputMessageReaderSize);
		builder.append(", clientOutputMessageReaderMaxSize=");
		builder.append(clientOutputMessageReaderMaxSize);
		builder.append(", clientrReadSelectorWakeupInterval=");
		builder.append(clientrReadSelectorWakeupInterval);
		builder.append(", clientInputMessageQueueSize=");
		builder.append(clientInputMessageQueueSize);		
		builder.append(", clientDataPacketBufferCnt=");
		builder.append(clientDataPacketBufferCnt);
		builder.append(", clientMonitorTimeInterval=");
		builder.append(clientMonitorTimeInterval);
		builder.append(", clientRequestTimeout=");
		builder.append(clientRequestTimeout);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public String toServerString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerProject [projectName=");
		builder.append(projectName);
		builder.append(", serverMaxClients=");
		builder.append(serverMaxClients);
		builder.append(", serverExecutorPrefix=");
		builder.append(serverExecutorPrefix);
		builder.append(", serverExecutorSuffix=");
		builder.append(serverExecutorSuffix);
		builder.append(", serverExecutorSourcePath=");
		builder.append(serverExecutorSourcePath);
		builder.append(", serverExecutorClassPath=");
		builder.append(serverExecutorClassPath);
		builder.append(", serverRequestTimeout=");
		builder.append(serverRequestTimeout);
		builder.append(", serverMonitorTimeInterval=");
		builder.append(serverMonitorTimeInterval);
		builder.append(", acceptServerSelectorTimeout=");
		builder.append(acceptServerSelectorTimeout);
		builder.append(", acceptServerProcessorSize=");
		builder.append(acceptServerProcessorSize);
		builder.append(", serverAcceptProcessorMaxSize=");
		builder.append(serverAcceptProcessorMaxSize);
		builder.append(", serverInputMessageReaderSize=");
		builder.append(serverInputMessageReaderSize);
		builder.append(", serverInputMessageReaderMaxSize=");
		builder.append(serverInputMessageReaderMaxSize);
		builder.append(", serverReadSelectorWakeupInterval=");
		builder.append(serverReadSelectorWakeupInterval);
		builder.append(", serverExecutorProcessorSize=");
		builder.append(serverExecutorProcessorSize);
		builder.append(", serverExecutorProcessorMaxSize=");
		builder.append(serverExecutorProcessorMaxSize);
		builder.append(", serverOutputMessageWriterSize=");
		builder.append(serverOutputMessageWriterSize);
		builder.append(", serverOutputMessageWriterMaxSize=");
		builder.append(serverOutputMessageWriterMaxSize);
		builder.append(", serverAcceptQueueSize=");
		builder.append(serverAcceptQueueSize);
		builder.append(", serverInputMessageQueueSize=");
		builder.append(serverInputMessageQueueSize);
		builder.append(", serverOutputMessageQueueSize=");
		builder.append(serverOutputMessageQueueSize);
		builder.append(", serverDataPacketBufferCnt=");
		builder.append(serverDataPacketBufferCnt);
		builder.append(", serverAnonymousExceptionInputMessageSet=");
		builder.append(serverAnonymousExceptionInputMessageSet.toString());
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProjectConfig [projectName=");
		builder.append(projectName);
		builder.append(", messageInfoPath=");
		builder.append(messageInfoPath);
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
		builder.append(", connectionType=");
		builder.append(connectionType);		
		builder.append(", clientConnectionCount=");
		builder.append(clientConnectionCount);
		builder.append(", clientWhetherToAutoConnect=");
		builder.append(clientWhetherToAutoConnect);
		builder.append(", clientSocketTimeout=");
		builder.append(clientSocketTimeout);
		builder.append(", clientFinishConnectMaxCall=");
		builder.append(clientFinishConnectMaxCall);
		builder.append(", clientFinishConnectWaittingTime=");
		builder.append(clientFinishConnectWaittingTime);
		builder.append(", clientOutputMessageQueueSize=");
		builder.append(clientOutputMessageQueueSize);
		builder.append(", clientShareAsynConnMailboxCnt=");
		builder.append(clientShareAsynConnMailboxCnt);
		builder.append(", clientInputMessageWriterSize=");
		builder.append(clientInputMessageWriterSize);
		builder.append(", clientInputMessageWriterMaxSize=");
		builder.append(clientInputMessageWriterMaxSize);
		builder.append(", clientOutputMessageReaderSize=");
		builder.append(clientOutputMessageReaderSize);
		builder.append(", clientOutputMessageReaderMaxSize=");
		builder.append(clientOutputMessageReaderMaxSize);
		builder.append(", clientrReadSelectorWakeupInterval=");
		builder.append(clientrReadSelectorWakeupInterval);
		builder.append(", clientInputMessageQueueSize=");
		builder.append(clientInputMessageQueueSize);
		builder.append(", clientDataPacketBufferCnt=");
		builder.append(clientDataPacketBufferCnt);
		builder.append(", clientMonitorTimeInterval=");
		builder.append(clientMonitorTimeInterval);
		builder.append(", clientRequestTimeout=");
		builder.append(clientRequestTimeout);
		builder.append(", serverMaxClients=");
		builder.append(serverMaxClients);
		builder.append(", serverExecutorPrefix=");
		builder.append(serverExecutorPrefix);
		builder.append(", serverExecutorSuffix=");
		builder.append(serverExecutorSuffix);
		builder.append(", serverExecutorSourcePath=");
		builder.append(serverExecutorSourcePath);
		builder.append(", serverExecutorClassPath=");
		builder.append(serverExecutorClassPath);
		builder.append(", acceptServerSelectorTimeout=");
		builder.append(acceptServerSelectorTimeout);
		builder.append(", acceptServerProcessorSize=");
		builder.append(acceptServerProcessorSize);
		builder.append(", serverAcceptProcessorMaxSize=");
		builder.append(serverAcceptProcessorMaxSize);
		builder.append(", serverInputMessageReaderSize=");
		builder.append(serverInputMessageReaderSize);
		builder.append(", serverInputMessageReaderMaxSize=");
		builder.append(serverInputMessageReaderMaxSize);
		builder.append(", serverReadSelectorWakeupInterval=");
		builder.append(serverReadSelectorWakeupInterval);
		builder.append(", serverExecutorProcessorSize=");
		builder.append(serverExecutorProcessorSize);
		builder.append(", serverExecutorProcessorMaxSize=");
		builder.append(serverExecutorProcessorMaxSize);
		builder.append(", serverOutputMessageWriterSize=");
		builder.append(serverOutputMessageWriterSize);
		builder.append(", serverOutputMessageWriterMaxSize=");
		builder.append(serverOutputMessageWriterMaxSize);
		builder.append(", serverAcceptQueueSize=");
		builder.append(serverAcceptQueueSize);
		builder.append(", serverInputMessageQueueSize=");
		builder.append(serverInputMessageQueueSize);
		builder.append(", serverOutputMessageQueueSize=");
		builder.append(serverOutputMessageQueueSize);
		builder.append(", serverDataPacketBufferCnt=");
		builder.append(serverDataPacketBufferCnt);
		builder.append(", serverAnonymousExceptionInputMessageSet=");
		builder.append(serverAnonymousExceptionInputMessageSet);
		builder.append(", serverMonitorTimeInterval=");
		builder.append(serverMonitorTimeInterval);
		builder.append(", serverRequestTimeout=");
		builder.append(serverRequestTimeout);
		builder.append("]");
		return builder.toString();
	}
}
