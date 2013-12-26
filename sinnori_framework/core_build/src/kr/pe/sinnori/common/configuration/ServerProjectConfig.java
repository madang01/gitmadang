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
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * 신놀이 프레임 워크를 이용한 개발 프로젝트의 서버 설정 정보
 * @author Jonghoon Won
 *
 */
public class ServerProjectConfig {
	private Logger log = null;
	private String projectName;
	
	
	private int maxClients;
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
	private long acceptSelectorTimeout;
	private int acceptProcessorSize;
	private int acceptProcessorMaxSize;
	private int inputMessageReaderSize;
	private int inputMessageReaderMaxSize;
	private long readSelectorWakeupInterval;
	private int executorProcessorSize;
	private int executorProcessorMaxSize;
	private int outputMessageWriterSize;
	private int outputMessageWriterMaxSize;
	private int acceptQueueSize;
	private int inputMessageQueueSize;
	private int outputMessageQueueSize;
	/***** 서버 비동기 입출력 지원용 자원 종료 *****/
		
	
	private int dataPacketBufferCnt;
	
	private TreeSet<String> anonymousExceptionInputMessageSet = new TreeSet<String>();
	
	/***** 모니터 환경 변수 시작 *****/
	// FIXME!
	private long monitorTimeInterval = 0L;
	private long requestTimeout = 0L;
	/***** 모니터 환경 변수 종료 *****/
	
	

	/**
	 * <pre>
	 * 파라미터 프로젝트에 속한 서버 환경 변수의 부분 키에 1:1 대응하는 서버 환경 변수 이름을 반환한다.
	 * 서버 환경 변수의 부분 키에 1:1 대응하는 서버 환경 변수 이름 구조는 <프로젝트명>.server.<부분 키>.value 이다.
	 * </pre>
	 * @param subkey 프로젝트에 속한 서버 환경 변수의 부분 키
	 * @return 프로젝트에 속한 서버 환경 변수의 부분 키에 1:1 대응하는 서버 환경 변수 이름
	 */
	private String getKeyName(String subkey) {
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
	public ServerProjectConfig(String projectName, Properties configFileProperties, Logger log) {
		this.projectName = projectName;
		this.log = log;
		
		String propKey = null;
		String propValue = null;
		String startIndexKey = null;
		
		propKey = getKeyName("max_clients");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			maxClients = 5;
		} else {
			try {
				maxClients = Integer.parseInt(propValue);
				if (maxClients < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, maxClients));
		
		
		/******** 서버 비지니스 로직 시작 **********/
		propKey = getKeyName("executor.impl.source.path");
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
		
		propKey = getKeyName("executor.impl.binary.path");
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
		
		propKey = getKeyName("executor.prefix");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue || 0 == propValue.trim().length()) {
			serverExecutorPrefix = "impl.executor.server.";
		} else {
			serverExecutorPrefix = propValue;
		}		
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, serverExecutorPrefix));
		
		
		propKey = getKeyName("executor.suffix");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue || 0 == propValue.trim().length()) {
			serverExecutorSuffix = "SExtor";
		} else {
			serverExecutorSuffix = propValue;
		}		
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, serverExecutorSuffix));
		/******** 서버 비지니스 로직 종료 **********/
		
		
		
		
		/***** 서버 비동기 입출력 지원용 자원 시작 *****/
		propKey = getKeyName("pool.accept_selector_timeout");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			acceptSelectorTimeout = 10;
		} else {
			try {
				acceptSelectorTimeout = Long.parseLong(propValue);
				if (acceptSelectorTimeout < 10) {
					log.fatal(String.format("warning:: key[%s] minimum value 10 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, acceptSelectorTimeout));
		
		
		propKey = getKeyName("pool.accept_processor.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		if (null == propValue) {
			acceptProcessorSize = 1;
		} else {
			try {
				acceptProcessorSize = Integer.parseInt(propValue);
				if (acceptProcessorSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, acceptProcessorSize));
		
		
		propKey = getKeyName("pool.accept_processor.max_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			acceptProcessorMaxSize = acceptProcessorSize;
		} else {
			try {
				acceptProcessorMaxSize = Integer.parseInt(propValue);
				if (acceptProcessorMaxSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				
				if (acceptProcessorMaxSize < acceptProcessorSize) {
					log.fatal(String.format("warning:: key[%s][%d] less than [%s][%d]", propKey, acceptProcessorMaxSize, startIndexKey, acceptProcessorSize));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, acceptProcessorMaxSize));
		
		propKey = getKeyName("pool.input_message_reader.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		if (null == propValue) {
			inputMessageReaderSize = 2;
		} else {
			try {
				inputMessageReaderSize = Integer.parseInt(propValue);
				if (inputMessageReaderSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, inputMessageReaderSize));
		
		
		propKey = getKeyName("pool.input_message_reader.max_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			inputMessageReaderMaxSize = inputMessageReaderSize;
		} else {
			try {
				inputMessageReaderMaxSize = Integer.parseInt(propValue);
				if (inputMessageReaderMaxSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				if (inputMessageReaderMaxSize < inputMessageReaderSize) {
					log.fatal(String.format("warning:: key[%s][%d] less than [%s][%d]", propKey, inputMessageReaderMaxSize, startIndexKey, inputMessageReaderSize));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, inputMessageReaderMaxSize));
		
		propKey = getKeyName("pool.read_selector_wakeup_interval");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			readSelectorWakeupInterval = 10;
		} else {
			try {
				readSelectorWakeupInterval = Long.parseLong(propValue);
				if (readSelectorWakeupInterval < 10) {
					log.fatal(String.format("warning:: key[%s] minimum value 10 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, readSelectorWakeupInterval));
		
		propKey = getKeyName("pool.executor_processor.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		if (null == propValue) {
			executorProcessorSize = 3;
		} else {
			try {
				executorProcessorSize = Integer.parseInt(propValue);
				if (executorProcessorSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, executorProcessorSize));
		
		
		propKey = getKeyName("pool.executor_processor.max_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			executorProcessorMaxSize = executorProcessorSize;
		} else {
			try {
				executorProcessorMaxSize = Integer.parseInt(propValue);
				if (executorProcessorMaxSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				if (executorProcessorMaxSize < executorProcessorSize) {
					log.fatal(String.format("warning:: key[%s][%d] less than [%s][%d]", propKey, executorProcessorMaxSize, startIndexKey, executorProcessorSize));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, executorProcessorMaxSize));
		
		
		propKey = getKeyName("pool.output_message_writer.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		if (null == propValue) {
			outputMessageWriterSize = 1;
		} else {
			try {
				outputMessageWriterSize = Integer.parseInt(propValue);
				if (outputMessageWriterSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, outputMessageWriterSize));
		
		
		propKey = getKeyName("pool.output_message_writer.max_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			outputMessageWriterMaxSize = outputMessageWriterSize;
		} else {
			try {
				outputMessageWriterMaxSize = Integer.parseInt(propValue);
				if (outputMessageWriterMaxSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
				if (outputMessageWriterMaxSize < outputMessageWriterSize) {
					log.fatal(String.format("warning:: key[%s][%d] less than [%s][%d]", propKey, outputMessageWriterMaxSize, startIndexKey, outputMessageWriterSize));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		this.log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, outputMessageWriterMaxSize));
		
		
		propKey = getKeyName("asyn.accept_queue_size");
		propValue = configFileProperties.getProperty(propKey);
		acceptQueueSize = 10;
		if (null != propValue) {
			try {
				acceptQueueSize = Integer.parseInt(propValue);
				
				if (acceptQueueSize < 1) {
					log.fatal(String.format("warning:: key[%s] minimum value 1 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, acceptQueueSize));
		
		
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
			outputMessageQueueSize = 10;
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
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, outputMessageQueueSize));
		/***** 서버 비동기 입출력 지원용 자원 종료 *****/
		
		propKey = getKeyName("anonymous_exception_inputmessage_set");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, "empty"));
		} else {
			StringTokenizer anonymousExceptionInputMessageToken = new StringTokenizer(propValue, ",");
			while (anonymousExceptionInputMessageToken.hasMoreTokens()) {
				String token = anonymousExceptionInputMessageToken.nextToken().trim();
				anonymousExceptionInputMessageSet.add(token);
			}
			log.info(String.format("%s::prop value[%s], new value[%s]", propKey, propValue, anonymousExceptionInputMessageSet.toString()));
		}
		
		/**
		 * 데이터 패킷 버퍼 수, 설정 파일에서 지정할 수 없다. 공식에 의해서 자동적으로 값이 설정된다.
		 * 서버에서 데이터 패킷 버퍼 사용처는 2곳이 있다.
		 * (1) 클라이언트 마다 읽기 전용 데이터 패킷 버퍼가 할당되며 소켓 데이터를 읽을때 사용한다.
		 * (2) 또한 출력 메시지 쓰기 쓰레드에서는 소켓 쓰기 전용 데이터 패킷 버퍼 1개를 할당 받아 소켓 쓰기 작업에 사용한다.
		 */
		propKey = getKeyName("data_packet_buffer_cnt");
		int minBufferCnt = maxClients + outputMessageWriterMaxSize;
		
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
		
		log.info(String.format("%s::prop value[%s], new value[%d], 시스템 필요 갯수[%d]", propKey, propValue, dataPacketBufferCnt, minBufferCnt));
		
		
		/******** 서버 프로젝트 모니터 시작 **********/
		propKey = getKeyName("monitor.time_interval");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			monitorTimeInterval = 10000L;
		} else {
			try {
				monitorTimeInterval = Long.parseLong(propValue);
				if (monitorTimeInterval < 1000L) {
					log.fatal(String.format("warning:: key[%s] minimum value 1000 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		
		
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, monitorTimeInterval));
		
		propKey = getKeyName("monitor.request_timeout");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			requestTimeout = 10000L;
		} else {
			try {
				requestTimeout = Long.parseLong(propValue);
				if (requestTimeout < 1000L) {
					log.fatal(String.format("warning:: key[%s] minimum value 1000 but value[%s]", propKey, propValue));
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.fatal(String.format("warning:: key[%s] integer but value[%s]", propKey, propValue));
				System.exit(1);
			}
		}
		log.info(String.format("%s::prop value[%s], new value[%d]", propKey, propValue, requestTimeout));
		/******** 서버 프로젝트 모니터 종료 **********/
	}

	/**
	 * @return 프로젝트 명
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return 서버에 접속할 수 있는 클라이언트 최대 수
	 */
	public int getMaxClients() {
		return maxClients;
	}

	/**
	 * @return 서버 비지니스 로직 클래스명 접두어
	 */
	public String getServerExecutorPrefix() {
		return serverExecutorPrefix;
	}

	/**
	 * @return 서버 비지니스 로직 클래스명 접미어
	 */
	public String getServerExecutorSuffix() {
		return serverExecutorSuffix;
	}

	/**
	 * @return 서버 비지니스 로직 클래스 소스 파일 경로
	 */
	public File getServerExecutorSourcePath() {
		return serverExecutorSourcePath;
	}

	/**
	 * @return 서버 비지니스 로직 클래스 파일 경로
	 */
	public File getServerExecutorClassPath() {
		return serverExecutorClassPath;
	}

	/**
	 * @return 접속 이벤트 전용 selector 에서 접속 이벤트 최대 대기 시간
	 */
	public long getAcceptSelectorTimeout() {
		return acceptSelectorTimeout;
	}

	/**
	 * @return 접속 요청이 승락된 클라이언트의 등록을 담당하는 쓰레드 초기 갯수
	 */
	public int getAcceptProcessorSize() {
		return acceptProcessorSize;
	}

	/**
	 * @return 접속 요청이 승락된 클라이언트의 등록을 담당하는 쓰레드 최대 갯수
	 */
	public int getAcceptProcessorMaxSize() {
		return acceptProcessorMaxSize;
	}

	/**
	 * @return 입력 메시지 소켓 읽기 담당 쓰레드 초기 갯수
	 */
	public int getInputMessageReaderSize() {
		return inputMessageReaderSize;
	}

	/**
	 * @return 입력 메시지 소켓 읽기 담당 쓰레드 최대 갯수
	 */
	public int getInputMessageReaderMaxSize() {
		return inputMessageReaderMaxSize;
	}

	/**
	 * @return 입력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기
	 */
	public long getReadSelectorWakeupInterval() {
		return readSelectorWakeupInterval;
	}

	/**
	 * @return 서버 비지니스 로직 수행 담당 쓰레드 초기 갯수
	 */
	public int getExecutorProcessorSize() {
		return executorProcessorSize;
	}

	/**
	 * @return 서버 비지니스 로직 수행 담당 쓰레드 최대 갯수
	 */
	public int getExecutorProcessorMaxSize() {
		return executorProcessorMaxSize;
	}

	/**
	 * @return 출력 메시지 소켓 쓰기 담당 쓰레드 초기 갯수
	 */
	public int getOutputMessageWriterSize() {
		return outputMessageWriterSize;
	}

	/**
	 * @return 출력 메시지 소켓 쓰기 담당 쓰레드 최대 갯수
	 */
	public int getOutputMessageWriterMaxSize() {
		return outputMessageWriterMaxSize;
	}

	/**
	 * @return 접속 승인 큐 크기
	 */
	public int getAcceptQueueSize() {
		return acceptQueueSize;
	}
	/**
	 * @return 입력 메시지 큐 크기
	 */
	public int getInputMessageQueueSize() {
		return inputMessageQueueSize;
	}

	/**
	 * @return 출력 메시지 큐 크기
	 */
	public int getOutputMessageQueueSize() {
		return outputMessageQueueSize;
	}

	

	/**
	 * @return 데이터 패킷 버퍼 수
	 */
	public int getDataPacketBufferCnt() {
		return dataPacketBufferCnt;
	}
	

	/**
	 * @return 데이터를 송신하지 않고 기다려주는 최대 시간, 단위 ms, 이 시간 초과된 클라이언트는 소켓을 닫은다. 
	 */
	public long getRequestTimeout() {
		return requestTimeout;
	}

	/**
	 * @return 프로젝트 모니터링 시간 간격, 단위 ms.
	 */
	public long getMonitorTimeInterval() {
		return monitorTimeInterval;
	}
	
	/**
	 * @return 설정파일에서 정의한 익명 예외 발생 시키는 메시지 목록
	 */
	public TreeSet<String> getAnonymousExceptionInputMessageSet() {
		return anonymousExceptionInputMessageSet;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerProjectInfo [projectName=");
		builder.append(projectName);
		builder.append(", maxClients=");
		builder.append(maxClients);
		builder.append(", serverExecutorPrefix=");
		builder.append(serverExecutorPrefix);
		builder.append(", serverExecutorSuffix=");
		builder.append(serverExecutorSuffix);
		builder.append(", serverExecutorSourcePath=");
		builder.append(serverExecutorSourcePath);
		builder.append(", serverExecutorClassPath=");
		builder.append(serverExecutorClassPath);
		builder.append(", requestTimeout=");
		builder.append(requestTimeout);
		builder.append(", monitorTimeInterval=");
		builder.append(monitorTimeInterval);
		builder.append(", acceptSelectorTimeout=");
		builder.append(acceptSelectorTimeout);
		builder.append(", acceptProcessorSize=");
		builder.append(acceptProcessorSize);
		builder.append(", acceptProcessorMaxSize=");
		builder.append(acceptProcessorMaxSize);
		builder.append(", inputMessageReaderSize=");
		builder.append(inputMessageReaderSize);
		builder.append(", inputMessageReaderMaxSize=");
		builder.append(inputMessageReaderMaxSize);
		builder.append(", readSelectorWakeupInterval=");
		builder.append(readSelectorWakeupInterval);
		builder.append(", executorProcessorSize=");
		builder.append(executorProcessorSize);
		builder.append(", executorProcessorMaxSize=");
		builder.append(executorProcessorMaxSize);
		builder.append(", outputMessageWriterSize=");
		builder.append(outputMessageWriterSize);
		builder.append(", outputMessageWriterMaxSize=");
		builder.append(outputMessageWriterMaxSize);
		builder.append(", acceptQueueSize=");
		builder.append(acceptQueueSize);
		builder.append(", inputMessageQueueSize=");
		builder.append(inputMessageQueueSize);
		builder.append(", outputMessageQueueSize=");
		builder.append(outputMessageQueueSize);
		builder.append(", dataPacketBufferCnt=");
		builder.append(dataPacketBufferCnt);
		builder.append(", anonymousExceptionInputMessageSet=");
		builder.append(anonymousExceptionInputMessageSet.toString());
		builder.append("]");
		return builder.toString();
	}	
}
