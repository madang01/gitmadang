package kr.pe.sinnori.common.configuration;

import java.util.Properties;

import org.slf4j.Logger;

public class ServerProjectConfig extends CommonProjectConfig {
	private int serverMaxClients;	
	
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
	
	
	
	/***** 모니터 환경 변수 시작 *****/
	private long serverMonitorTimeInterval = 0L;
	private long serverRequestTimeout = 0L;
	/***** 모니터 환경 변수 종료 *****/
	
	public ServerProjectConfig(String projectName,
			Properties configFileProperties, Logger log) {
		super(projectName, configFileProperties, log);
		configServerProject(configFileProperties);
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
	
	private void configServerProject(Properties configFileProperties) {		
		String propKey = null;
		String propValue = null;
		String compPropKey = null;
		
		propKey = getServerKeyName("max_clients");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverMaxClients = 5;
		} else {
			try {
				serverMaxClients = Integer.parseInt(propValue);
				if (serverMaxClients < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverMaxClients);
		
		
		/***** 서버 비동기 입출력 지원용 자원 시작 *****/
		propKey = getServerKeyName("pool.accept_selector_timeout");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			acceptServerSelectorTimeout = 10;
		} else {
			try {
				acceptServerSelectorTimeout = Long.parseLong(propValue);
				if (acceptServerSelectorTimeout < 10) {
					log.error("warning:: key[{}] minimum value 10 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, acceptServerSelectorTimeout);
		
		
		propKey = getServerKeyName("pool.accept_processor.size");
		propValue = configFileProperties.getProperty(propKey);
		compPropKey = propKey;
		if (null == propValue) {
			acceptServerProcessorSize = 1;
		} else {
			try {
				acceptServerProcessorSize = Integer.parseInt(propValue);
				if (acceptServerProcessorSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, acceptServerProcessorSize);
		
		
		propKey = getServerKeyName("pool.accept_processor.max_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverAcceptProcessorMaxSize = acceptServerProcessorSize;
		} else {
			try {
				serverAcceptProcessorMaxSize = Integer.parseInt(propValue);
				if (serverAcceptProcessorMaxSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				
				if (serverAcceptProcessorMaxSize < acceptServerProcessorSize) {
					log.error("warning:: key[{}][{}] less than [{}][{}]", propKey, serverAcceptProcessorMaxSize, compPropKey, acceptServerProcessorSize);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverAcceptProcessorMaxSize);
		
		propKey = getServerKeyName("pool.input_message_reader.size");
		propValue = configFileProperties.getProperty(propKey);
		compPropKey = propKey;
		if (null == propValue) {
			serverInputMessageReaderSize = 2;
		} else {
			try {
				serverInputMessageReaderSize = Integer.parseInt(propValue);
				if (serverInputMessageReaderSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverInputMessageReaderSize);
		
		
		propKey = getServerKeyName("pool.input_message_reader.max_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverInputMessageReaderMaxSize = serverInputMessageReaderSize;
		} else {
			try {
				serverInputMessageReaderMaxSize = Integer.parseInt(propValue);
				if (serverInputMessageReaderMaxSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				if (serverInputMessageReaderMaxSize < serverInputMessageReaderSize) {
					log.error("warning:: key[{}][{}] less than [{}][{}]", propKey, serverInputMessageReaderMaxSize, compPropKey, serverInputMessageReaderSize);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		this.log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverInputMessageReaderMaxSize);
		
		propKey = getServerKeyName("pool.read_selector_wakeup_interval");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverReadSelectorWakeupInterval = 10;
		} else {
			try {
				serverReadSelectorWakeupInterval = Long.parseLong(propValue);
				if (serverReadSelectorWakeupInterval < 10) {
					log.error("warning:: key[{}] minimum value 10 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		this.log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverReadSelectorWakeupInterval);
		
		propKey = getServerKeyName("pool.executor_processor.size");
		propValue = configFileProperties.getProperty(propKey);
		compPropKey = propKey;
		if (null == propValue) {
			serverExecutorProcessorSize = 3;
		} else {
			try {
				serverExecutorProcessorSize = Integer.parseInt(propValue);
				if (serverExecutorProcessorSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		this.log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverExecutorProcessorSize);
		
		
		propKey = getServerKeyName("pool.executor_processor.max_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverExecutorProcessorMaxSize = serverExecutorProcessorSize;
		} else {
			try {
				serverExecutorProcessorMaxSize = Integer.parseInt(propValue);
				if (serverExecutorProcessorMaxSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				if (serverExecutorProcessorMaxSize < serverExecutorProcessorSize) {
					log.error("warning:: key[{}][{}] less than [{}][{}]", propKey, serverExecutorProcessorMaxSize, compPropKey, serverExecutorProcessorSize);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		this.log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverExecutorProcessorMaxSize);
		
		
		propKey = getServerKeyName("pool.output_message_writer.size");
		propValue = configFileProperties.getProperty(propKey);
		compPropKey = propKey;
		if (null == propValue) {
			serverOutputMessageWriterSize = 1;
		} else {
			try {
				serverOutputMessageWriterSize = Integer.parseInt(propValue);
				if (serverOutputMessageWriterSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		this.log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverOutputMessageWriterSize);
		
		
		propKey = getServerKeyName("pool.output_message_writer.max_size");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverOutputMessageWriterMaxSize = serverOutputMessageWriterSize;
		} else {
			try {
				serverOutputMessageWriterMaxSize = Integer.parseInt(propValue);
				if (serverOutputMessageWriterMaxSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				if (serverOutputMessageWriterMaxSize < serverOutputMessageWriterSize) {
					log.error("warning:: key[{}][{}] less than [{}][{}]", propKey, serverOutputMessageWriterMaxSize, compPropKey, serverOutputMessageWriterSize);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		this.log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverOutputMessageWriterMaxSize);
		
		
		propKey = getServerKeyName("asyn.accept_queue_size");
		propValue = configFileProperties.getProperty(propKey);
		serverAcceptQueueSize = 10;
		if (null != propValue) {
			try {
				serverAcceptQueueSize = Integer.parseInt(propValue);
				
				if (serverAcceptQueueSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverAcceptQueueSize);
		
		
		propKey = getServerKeyName("asyn.input_message_queue_size");
		propValue = configFileProperties.getProperty(propKey);
		serverInputMessageQueueSize = 10;
		if (null != propValue) {
			try {
				serverInputMessageQueueSize = Integer.parseInt(propValue);
				
				if (serverInputMessageQueueSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverInputMessageQueueSize);
		
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
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverOutputMessageQueueSize);
		/***** 서버 비동기 입출력 지원용 자원 종료 *****/
		
		
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
					log.error("warning:: key[{}] minimum value 100 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		
		serverDataPacketBufferCnt += minBufferCnt;
		
		log.info("{}::prop value[{}], new value[{}], 시스템 필요 갯수[{}]", propKey, propValue, serverDataPacketBufferCnt, minBufferCnt);
		
		
		/******** 서버 프로젝트 모니터 시작 **********/
		propKey = getServerKeyName("monitor.time_interval");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverMonitorTimeInterval = 10000L;
		} else {
			try {
				serverMonitorTimeInterval = Long.parseLong(propValue);
				if (serverMonitorTimeInterval < 1000L) {
					log.error("warning:: key[{}] minimum value 1000 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}		
		
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverMonitorTimeInterval);
		
		propKey = getServerKeyName("monitor.request_timeout");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			serverRequestTimeout = 10000L;
		} else {
			try {
				serverRequestTimeout = Long.parseLong(propValue);
				if (serverRequestTimeout < 1000L) {
					log.error("warning:: key[{}] minimum value 1000 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, serverRequestTimeout);
		/******** 서버 프로젝트 모니터 종료 **********/
	}
	
	
	public int getServerMaxClients() {
		return serverMaxClients;
	}
	
	public long getServerAcceptSelectorTimeout() {
		return acceptServerSelectorTimeout;
	}
	
	public int getServerAcceptProcessorSize() {
		return acceptServerProcessorSize;
	}
	
	
	public int getServerAcceptProcessorMaxSize() {
		return serverAcceptProcessorMaxSize;
	}
	
	
	public int getServerInputMessageReaderSize() {
		return serverInputMessageReaderSize;
	}
	
	
	public int getServerInputMessageReaderMaxSize() {
		return serverInputMessageReaderMaxSize;
	}
	
	
	public long getServerReadSelectorWakeupInterval() {
		return serverReadSelectorWakeupInterval;
	}
	
	
	public int getServerExecutorProcessorSize() {
		return serverExecutorProcessorSize;
	}

	
	public int getServerExecutorProcessorMaxSize() {
		return serverExecutorProcessorMaxSize;
	}

	public int getServerOutputMessageWriterSize() {
		return serverOutputMessageWriterSize;
	}

	
	public int getServerOutputMessageWriterMaxSize() {
		return serverOutputMessageWriterMaxSize;
	}

	
	public int getServerAcceptQueueSize() {
		return serverAcceptQueueSize;
	}

	
	public int getServerInputMessageQueueSize() {
		return serverInputMessageQueueSize;
	}
	
	
	public int getServerOutputMessageQueueSize() {
		return serverOutputMessageQueueSize;
	}

	
	public int getServerDataPacketBufferCnt() {
		return serverDataPacketBufferCnt;
	}

	
	public long getServerRequestTimeout() {
		return serverRequestTimeout;
	}

	
	public long getServerMonitorTimeInterval() {
		return serverMonitorTimeInterval;
	}
	
	public String toServerString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerProjectConfig [");
		builder.append("serverMaxClients=");
		builder.append(serverMaxClients);		
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
		builder.append(", serverMonitorTimeInterval=");
		builder.append(serverMonitorTimeInterval);
		builder.append(", serverRequestTimeout=");
		builder.append(serverRequestTimeout);
		builder.append("]");
		return builder.toString();
	}
}
