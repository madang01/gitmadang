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

import java.util.Properties;

import kr.pe.sinnori.common.lib.CommonType;

import org.apache.log4j.Logger;

/**
 * 신놀이 프레임 워크를 이용한 개발 프로젝트의 클라이언트 설정 정보
 * @author Jonghoon Won
 *
 */
public class ClientProjectConfig {
	private Logger log = null;
	private String projectName;
	
	
	/***** 연결 클래스 관련 환경 변수 시작 *****/
	/** 소켓 채널 랩 클래스인 연결 클래스의 쓰레드 공유 모드 */
	private CommonType.THREAD_SHARE_MODE threadShareMode;
	/** 소켓 채널 blocking 모드 */
	private boolean channelBlockingMode;
	/** 연결 클래스 갯수 */
	private int connectionCount;
	/** 연결 생성시 자동 접속 여부 */
	private boolean whetherToAutoConnect;
	/** 소켓 타임 아웃 시간 */
	private long socketTimeout;
	/***** 연결 클래스 관련 환경 변수 종료 *****/
	
	/***** 비동기 입출력 지원용 자원 관련 환경 변수 시작 *****/
	/** 클라이언트 비동기 소켓 채널의 연결 확립 최대 시도 횟수 */
	private int finishConnectMaxCall;
	/** 클라이언트 비동기 소켓 채널의 연결 확립을 재 시도 간격 */
	private long finishConnectWaittingTime;
	/** 출력 메시지 큐 크기 */
	private int outputMessageQueueSize;
	/** 메일함 갯수 */
	private int multiMailboxCnt;	
	/** 입력 메시지 소켓 쓰기 담당 쓰레드 초기 갯수 */
	private int inputMessageWriterSize;
	/** 입력 메시지 소켓 쓰기 담당 쓰레드 최대 갯수 */
	private int inputMessageWriterMaxSize;
	/** 출력 메시지 소켓 읽기 담당 쓰레드 초기 갯수 */
	private int outputMessageReaderSize;
	/** 출력 메시지 소켓 읽기 담당 쓰레드 최대 갯수 */
	private int outputMessageReaderMaxSize;
	/** 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기 */
	private long readSelectorWakeupInterval;
	/** 입력 메시지 큐 크기 */
	private int inputMessageQueueSize;
	/***** 비동기 입출력 지원용 자원 관련 환경 변수 종료 *****/
	
	/** 데이터 패킷 버퍼 수 */
	private int dataPacketBufferCnt;
	
	/***** 모니터 환경 변수 시작 *****/
	// FIXME!
	private long monitorTimeInterval = 0L;
	private long requestTimeout = 0L;
	/***** 모니터 환경 변수 종료 *****/
	
	
	/**
	 * <pre>
	 * 파라미터 프로젝트에 속한 클라이언트 환경 변수의 부분 키에 1:1 대응하는 클라이언트 환경 변수 이름을 반환한다.
	 * 클라이언트 환경 변수의 부분 키에 1:1 대응하는 클라이언트 환경 변수 이름 구조는 <프로젝트명>.client.<부분 키>.value 이다.
	 * </pre>
	 * @param subkey 프로젝트에 속한 클라이언트 환경 변수의 부분 키
	 * @return 프로젝트에 속한 클라이언트 환경 변수의 부분 키에 1:1 대응하는 클라이언트 환경 변수 이름
	 */
	private String getKeyName(String subkey) {
		StringBuffer strBuff = new StringBuffer(projectName);
		strBuff.append(".client.");
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
	public ClientProjectConfig(String projectName, Properties configFileProperties, Logger log) {
		this.projectName = projectName;
		this.log = log;
		
		String propKey = null;
		String propValue = null;
		String startIndexKey = null;
		
		propKey = getKeyName("connection.thread_share_mode");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			threadShareMode = CommonType.THREAD_SHARE_MODE.Single;
		} else {
			if (propValue.equals("Multi")) {
				threadShareMode = CommonType.THREAD_SHARE_MODE.Multi;
			} else if (propValue.equals("Single")) {
				threadShareMode = CommonType.THREAD_SHARE_MODE.Single;
			} else {
				log.fatal(String.format("warning:: key[%s] set Multi, Single but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, threadShareMode));
		
		propKey = getKeyName("connection.channel_blocking_mode");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			channelBlockingMode = false;
		} else {
			if (propValue.equals("true")) {
				channelBlockingMode = true;
			} else if (propValue.equals("false")) {
				channelBlockingMode = false;
			} else {
				log.fatal(String.format("warning:: key[%s] set true, false but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, channelBlockingMode));
		
		propKey = getKeyName("connection.count");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			connectionCount = 4;
		} else {
			try {
				connectionCount = Integer.parseInt(propValue);
				if (connectionCount < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, connectionCount));
		
		propKey = getKeyName("connection.whether_to_auto_connect");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			whetherToAutoConnect = false;
		} else {
			if (propValue.equals("true")) {
				whetherToAutoConnect = true;
			} else if (propValue.equals("false")) {
				whetherToAutoConnect = false;
			} else {
				log.fatal(String.format("warning:: key[%s] set true, false but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, whetherToAutoConnect));
		
		
		propKey = getKeyName("connection.socket_timeout");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			socketTimeout = 5000;
		} else {
			try {
				socketTimeout = Long.parseLong(propValue);
				if (socketTimeout < 1000) {
					log.fatal(String.format("warning:: key[%s] minimum value 1000 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] long but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, socketTimeout));
		
		
		/***** 비동기 입출력 지원용 자원 시작 *****/
		propKey = getKeyName("asyn.finish_connect.max_call");		
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		finishConnectMaxCall = 10;
		if (null != propValue) {
			try {
				finishConnectMaxCall = Integer.parseInt(propValue);
				if (finishConnectMaxCall < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, finishConnectMaxCall));
		
		propKey = getKeyName("asyn.finish_connect.waitting_time");		
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		finishConnectWaittingTime = 10;
		if (null != propValue) {
			try {
				finishConnectWaittingTime = Long.parseLong(propValue);
				if (finishConnectWaittingTime < 0) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, finishConnectWaittingTime));
		
		propKey = getKeyName("asyn.input_message_writer.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		inputMessageWriterSize = 2;
		if (null != propValue) {
			try {
				inputMessageWriterSize = Integer.parseInt(propValue);
				if (inputMessageWriterSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, inputMessageWriterSize));
		
		
		propKey = getKeyName("asyn.input_message_writer.max_size");
		propValue = configFileProperties.getProperty(propKey);
		inputMessageWriterMaxSize = inputMessageWriterSize;
		if (null != propValue) {
			try {
				inputMessageWriterMaxSize = Integer.parseInt(propValue);
				
				if (inputMessageWriterMaxSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				
				if (inputMessageWriterMaxSize < inputMessageWriterSize) {
					log.fatal(String.format("warning:: key[%s][%d] less than [%s][%d]", 
							propKey, inputMessageWriterMaxSize, startIndexKey, inputMessageWriterSize));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, inputMessageWriterMaxSize));
		
		
		
		propKey = getKeyName("asyn.output_message_reader.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		outputMessageReaderSize = 4;
		if (null != propValue) {
			try {
				outputMessageReaderSize = Integer.parseInt(propValue);
				
				if (outputMessageReaderSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, outputMessageReaderSize));
				
		propKey = getKeyName("asyn.output_message_reader.max_size");
		propValue = configFileProperties.getProperty(propKey);
		outputMessageReaderMaxSize = outputMessageReaderSize;
		if (null != propValue) {
			try {
				outputMessageReaderMaxSize = Integer.parseInt(propValue);
				if (outputMessageReaderMaxSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				
				if (outputMessageReaderMaxSize < outputMessageReaderSize) {
					log.fatal(String.format("warning:: key[%s][%d] less than [%s][%d]", 
							propKey, outputMessageReaderMaxSize, startIndexKey, outputMessageReaderSize));
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, outputMessageReaderMaxSize));
		
		propKey = getKeyName("asyn.read_selector_wakeup_interval");
		propValue = configFileProperties.getProperty(propKey);
		readSelectorWakeupInterval = 10;
		if (null != propValue) {
			try {
				readSelectorWakeupInterval = Long.parseLong(propValue);
				
				if (readSelectorWakeupInterval < 10) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] long but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, readSelectorWakeupInterval));
		
		
		propKey = getKeyName("asyn.input_message_queue_size");
		propValue = configFileProperties.getProperty(propKey);
		inputMessageQueueSize = 10;
		if (null != propValue) {
			try {
				inputMessageQueueSize = Integer.parseInt(propValue);
				if (inputMessageQueueSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, inputMessageQueueSize));
		
		/**
		 * 출력 메시지 큐는 Multi 모드이면 메일함의 출력 메시지 큐, Single 모드이면 연결 클래스의 출력 메시지 큐를 뜻한다.
		 */
		propKey = getKeyName("asyn.output_message_queue_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			outputMessageQueueSize = 3;
		} else {
			try {
				outputMessageQueueSize = Integer.parseInt(propValue);
				if (outputMessageQueueSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, outputMessageQueueSize));
		
		propKey = getKeyName("asyn.multi.mailbox_cnt");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			multiMailboxCnt = 2;
		} else {
			try {
				multiMailboxCnt = Integer.parseInt(propValue);
				if (multiMailboxCnt < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, multiMailboxCnt));
		/***** 비동기 입출력 지원용 자원 종료 *****/
		
		/**
		 * 데이터 패킷 버퍼 수, 설정 파일에서 지정할 수 없다. 공식에 의해서 자동적으로 값이 설정된다.
		 * 참고) 소켓 채널 마다 읽기 전용 버퍼가 필요하다.
		 * client.data_packet_buffer_cnt : 소켓 채널 none-blocking 모드일 경우에는 연결 클래스 갯수 만큼의 읽기 전용 버퍼+입력 메시지 소켓 쓰기 담당 쓰레드 최대 갯수
		 * client.data_packet_buffer_cnt : 소켓 채널 blocking 모드일 경우에는 연결 클래스 갯수*2 = 연결 클래스 갯수 만큼의 읽기 전용 버퍼+연결 클래스 갯수 만큼의 쓰기 전용 버퍼
		 */
		propKey = getKeyName("data_packet_buffer_cnt");
		int minBufferCnt = 100;
		if (CommonType.THREAD_SHARE_MODE.Multi == threadShareMode) {
			minBufferCnt = connectionCount + inputMessageWriterMaxSize;
		} else {
			minBufferCnt = connectionCount*2;
		}
		
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			dataPacketBufferCnt = 100;
		} else {
			try {
				dataPacketBufferCnt = Integer.parseInt(propValue);
				if (dataPacketBufferCnt < 100) {
					log.fatal(String.format("warning:: key[%s] minimum value 100 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		dataPacketBufferCnt += minBufferCnt;
		
		this.log.info(String.format("%s::prop value[%s], new value[%d], 시스템 필요 갯수[%d]", propKey, propValue, dataPacketBufferCnt, minBufferCnt));
		
		/***** 모니터 시작 *****/
		// FIXME!
		propKey = getKeyName("monitor.time_interval");		
		propValue = configFileProperties.getProperty(propKey);		
		monitorTimeInterval = 10000L;
		if (null != propValue) {
			try {
				monitorTimeInterval = Long.parseLong(propValue);
				if (monitorTimeInterval < 1000) {
					log.fatal(String.format("warning:: key[%s] minimum value 1000 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, monitorTimeInterval));
		
		
		propKey = getKeyName("monitor.request_timeout");		
		propValue = configFileProperties.getProperty(propKey);		
		requestTimeout = socketTimeout*2;
		if (null != propValue) {
			try {
				requestTimeout = Long.parseLong(propValue);
				if (requestTimeout < socketTimeout) {
					log.fatal(String.format("warning:: key[%s] minimum value %d but value[%s]", propKey, socketTimeout, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, requestTimeout));
		/***** 모니터 종료 *****/
	}

	
	/**
	 * 프로젝트 이름을 반환한다
	 * @return 프로젝트 이름
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * 소켓 채널 랩 클래스인 연결 클래스의 쓰레드 공유 모드를 반환한다.
	 * @return 소켓 채널 랩 클래스인 연결 클래스의 쓰레드 공유 모드
	 */
	public CommonType.THREAD_SHARE_MODE getThreadShareMode() {
		return threadShareMode;
	}

	/**
	 * 소켓 채널 blocking 모드를 반환한다.
	 * @return 소켓 채널 blocking 모드
	 */
	public boolean getChannelBlockingMode() {
		return channelBlockingMode;
	}

	/**
	 * 연결 클래스 갯수를 반환한다.
	 * @return 연결 클래스 갯수
	 */
	public int getConnectionCount() {
		return connectionCount;
	}

	/**
	 * 연결 생성시 자동 접속 여부
	 * @return 연결 생성시 자동 접속 여부
	 */
	public boolean getWhetherToAutoConnect() {
		return whetherToAutoConnect;
	}

	/**
	 * 소켓 타임 아웃 시간
	 * @return 소켓 타임 아웃 시간
	 */
	public long getSocketTimeout() {
		return socketTimeout;
	}

	/**
	 * 출력 메시지 큐 크기, 소켓 채널 blocking 모드가 false 즉 비동기 방식 연결 클래스에서만 유효하다.
	 * @return 출력 메시지 큐 크기
	 */
	public int getOutputMessageQueueSize() {
		return outputMessageQueueSize;
	}

	/**
	 * 메일함 갯수, 소켓 채널 blocking 모드가 false 즉 비동기 방식 연결 클래스에서만 유효하다.
	 * @return 메일함 갯수
	 */
	public int getMultiMailboxCnt() {
		return multiMailboxCnt;
	}
	
	/**
	 * @return 클라이언트 비동기 소켓 채널의 연결 확립 최대 시도 횟수
	 */
	public int getFinishConnectMaxCall() {
		return finishConnectMaxCall;
	}
	
	/**
	 * @return 클라이언트 비동기 소켓 채널의 연결 확립을 재 시도 간격
	 */
	public long getFinishConnectWaittingTime() {
		return finishConnectWaittingTime;
	}
	
	/**
	 * @return 입력 메시지 소켓 쓰기 담당 쓰레드 초기 갯수
	 */
	public int getInputMessageWriterSize() {
		return inputMessageWriterSize;
	}

	/**
	 * @return 입력 메시지 소켓 쓰기 담당 쓰레드 최대 갯수
	 */
	public int getInputMessageWriterMaxSize() {
		return inputMessageWriterMaxSize;
	}

	/**
	 * @return 출력 메시지 소켓 읽기 담당 쓰레드 초기 갯수
	 */
	public int getOutputMessageReaderSize() {
		return outputMessageReaderSize;
	}

	/**
	 * @return 출력 메시지 소켓 읽기 담당 쓰레드 최대 갯수
	 */
	public int getOutputMessageReaderMaxSize() {
		return outputMessageReaderMaxSize;
	}

	/**
	 * @return 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기
	 */
	public long getReadSelectorWakeupInterval() {
		return readSelectorWakeupInterval;
	}

	/**
	 * @return 입력 메시지 큐 크기
	 */
	public int getInputMessageQueueSize() {
		return inputMessageQueueSize;
	}
	
	/**
	 * 데이터 패킷 버퍼 수
	 * @return 데이터 패킷 버퍼 수
	 */
	public int getDataPacketBufferCnt() {
		return dataPacketBufferCnt;
	}
	
	/**
	 * @return 프로젝트 모니터링 시간 간격, 단위 ms.
	 */
	public long getMonitorTimeInterval() {
		return monitorTimeInterval;
	}
	
	/**
	 * @return 데이터를 송신하지 않고 기다려주는 최대 시간, 단위 ms, 이 시간 초과된 클라이언트는 소켓을 닫은다. 
	 */
	public long getRequestTimeout() {
		return requestTimeout;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProjectClientInfo [projectName=");
		builder.append(projectName);
		builder.append(", threadShareMode=");
		builder.append(threadShareMode);
		builder.append(", channelBlockingMode=");
		builder.append(channelBlockingMode);
		builder.append(", connectionCount=");
		builder.append(connectionCount);
		builder.append(", whetherToAutoConnect=");
		builder.append(whetherToAutoConnect);
		builder.append(", socketTimeout=");
		builder.append(socketTimeout);
		builder.append(", outputMessageQueueSize=");
		builder.append(outputMessageQueueSize);
		builder.append(", multiMailboxCnt=");
		builder.append(multiMailboxCnt);
		builder.append(", finishConnectMaxCall=");
		builder.append(finishConnectMaxCall);
		builder.append(", finishConnectWaittingTime=");
		builder.append(finishConnectWaittingTime);		
		builder.append(", inputMessageWriterSize=");
		builder.append(inputMessageWriterSize);
		builder.append(", inputMessageWriterMaxSize=");
		builder.append(inputMessageWriterMaxSize);
		builder.append(", outputMessageReaderSize=");
		builder.append(outputMessageReaderSize);
		builder.append(", outputMessageReaderMaxSize=");
		builder.append(outputMessageReaderMaxSize);
		builder.append(", readSelectorWakeupInterval=");
		builder.append(readSelectorWakeupInterval);
		builder.append(", inputMessageQueueSize=");
		builder.append(inputMessageQueueSize);		
		builder.append(", dataPacketBufferCnt=");
		builder.append(dataPacketBufferCnt);
		builder.append(", monitorTimeInterval=");
		builder.append(monitorTimeInterval);
		builder.append(", requestTimeout=");
		builder.append(requestTimeout);
		builder.append("]");
		return builder.toString();
	}	
	
}
