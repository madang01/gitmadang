package kr.pe.sinnori.common.configuration;

import java.util.Properties;

import kr.pe.sinnori.common.lib.CommonType.CONNECTION_TYPE;

import org.slf4j.Logger;

public class ClientProjectConfig extends CommonProjectConfig {
	/************* client 변수 시작 ******************/
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
	// FIXME!
	/** 비동기 출력 메시지 처리자 쓰레드 갯수 */
	private int clientAsynOutputMessageExecutorThreadCnt;
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
	private long clientReceptionTimeout = 0L;
	/***** 모니터 환경 변수 종료 *****/
	/************* client 변수 종료 ******************/
	
	public ClientProjectConfig(String projectName,
			Properties configFileProperties, Logger log) {
		super(projectName, configFileProperties, log);
		configClientProject(configFileProperties);
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
		StringBuffer strBuff = new StringBuffer("project.");
		strBuff.append(projectName);
		strBuff.append(".client.");
		strBuff.append(subkey);
		strBuff.append(".value");		
		return strBuff.toString();
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
				log.error("warning:: key[{}] set Multi, Single but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, connectionType);

		propKey = getClientKeyName("connection.count");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			clientConnectionCount = 4;
		} else {
			try {
				clientConnectionCount = Integer.parseInt(propValue);
				if (clientConnectionCount < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientConnectionCount);
		
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
				log.error("warning:: key[{}] set true, false but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientWhetherToAutoConnect);
		
		
		propKey = getClientKeyName("connection.socket_timeout");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			clientSocketTimeout = 5000;
		} else {
			try {
				clientSocketTimeout = Long.parseLong(propValue);
				if (clientSocketTimeout < 1000) {
					log.error("warning:: key[{}] minimum value 1000 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] long but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientSocketTimeout);
		
		
		/***** 비동기 입출력 지원용 자원 시작 *****/
		propKey = getClientKeyName("asyn.finish_connect.max_call");		
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		clientFinishConnectMaxCall = 10;
		if (null != propValue) {
			try {
				clientFinishConnectMaxCall = Integer.parseInt(propValue);
				if (clientFinishConnectMaxCall < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientFinishConnectMaxCall);
		
		propKey = getClientKeyName("asyn.finish_connect.waitting_time");		
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		clientFinishConnectWaittingTime = 10;
		if (null != propValue) {
			try {
				clientFinishConnectWaittingTime = Long.parseLong(propValue);
				if (clientFinishConnectWaittingTime < 0) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientFinishConnectWaittingTime);
		
		// FIXME!
		propKey = getClientKeyName("asyn.output_message_executor_thread_cnt");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		clientAsynOutputMessageExecutorThreadCnt = 1;
		if (null != propValue) {
			try {
				clientAsynOutputMessageExecutorThreadCnt = Integer.parseInt(propValue);
				if (clientAsynOutputMessageExecutorThreadCnt < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientAsynOutputMessageExecutorThreadCnt);
		
		
		propKey = getClientKeyName("asyn.input_message_writer.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		clientInputMessageWriterSize = 2;
		if (null != propValue) {
			try {
				clientInputMessageWriterSize = Integer.parseInt(propValue);
				if (clientInputMessageWriterSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientInputMessageWriterSize);
		
		
		propKey = getClientKeyName("asyn.input_message_writer.max_size");
		propValue = configFileProperties.getProperty(propKey);
		clientInputMessageWriterMaxSize = clientInputMessageWriterSize;
		if (null != propValue) {
			try {
				clientInputMessageWriterMaxSize = Integer.parseInt(propValue);
				
				if (clientInputMessageWriterMaxSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				
				if (clientInputMessageWriterMaxSize < clientInputMessageWriterSize) {
					log.error("warning:: key[{}][{}] less than [{}][{}]", 
							propKey, clientInputMessageWriterMaxSize, startIndexKey, clientInputMessageWriterSize);
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientInputMessageWriterMaxSize);
		
		
		
		propKey = getClientKeyName("asyn.output_message_reader.size");
		propValue = configFileProperties.getProperty(propKey);
		startIndexKey = propKey;
		clientOutputMessageReaderSize = 4;
		if (null != propValue) {
			try {
				clientOutputMessageReaderSize = Integer.parseInt(propValue);
				
				if (clientOutputMessageReaderSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientOutputMessageReaderSize);
				
		propKey = getClientKeyName("asyn.output_message_reader.max_size");
		propValue = configFileProperties.getProperty(propKey);
		clientOutputMessageReaderMaxSize = clientOutputMessageReaderSize;
		if (null != propValue) {
			try {
				clientOutputMessageReaderMaxSize = Integer.parseInt(propValue);
				if (clientOutputMessageReaderMaxSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
				
				if (clientOutputMessageReaderMaxSize < clientOutputMessageReaderSize) {
					log.error("warning:: key[{}][{}] less than [{}][{}]", 
							propKey, clientOutputMessageReaderMaxSize, startIndexKey, clientOutputMessageReaderSize);
					System.exit(1);
				}
				
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientOutputMessageReaderMaxSize);
		
		propKey = getClientKeyName("asyn.read_selector_wakeup_interval");
		propValue = configFileProperties.getProperty(propKey);
		clientrReadSelectorWakeupInterval = 10;
		if (null != propValue) {
			try {
				clientrReadSelectorWakeupInterval = Long.parseLong(propValue);
				
				if (clientrReadSelectorWakeupInterval < 10) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] long but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientrReadSelectorWakeupInterval);
		
		
		propKey = getClientKeyName("asyn.input_message_queue_size");
		propValue = configFileProperties.getProperty(propKey);
		clientInputMessageQueueSize = 10;
		if (null != propValue) {
			try {
				clientInputMessageQueueSize = Integer.parseInt(propValue);
				if (clientInputMessageQueueSize < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientInputMessageQueueSize);
		
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
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientOutputMessageQueueSize);
		
		propKey = getClientKeyName("asyn.share.mailbox_cnt");
		propValue = configFileProperties.getProperty(propKey);
		if (null == propValue) {
			clientShareAsynConnMailboxCnt = 2;
		} else {
			try {
				clientShareAsynConnMailboxCnt = Integer.parseInt(propValue);
				if (clientShareAsynConnMailboxCnt < 1) {
					log.error("warning:: key[{}] minimum value 1 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientShareAsynConnMailboxCnt);
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
					log.error("warning:: key[{}] minimum value 100 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException nfe) {
				log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		clientDataPacketBufferCnt += minBufferCnt;
		
		log.info("{}::prop value[{}], new value[{}], 시스템 필요 갯수[{}]", propKey, propValue, clientDataPacketBufferCnt, minBufferCnt);
		
		/***** 모니터 시작 *****/
		propKey = getClientKeyName("monitor.time_interval");		
		propValue = configFileProperties.getProperty(propKey);		
		clientMonitorTimeInterval = 10000L;
		if (null != propValue) {
			try {
				clientMonitorTimeInterval = Long.parseLong(propValue);
				if (clientMonitorTimeInterval < 1000) {
					log.error("warning:: key[{}] minimum value 1000 but value[{}]", propKey, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientMonitorTimeInterval);
		
		
		propKey = getClientKeyName("monitor.receiption_timeout");		
		propValue = configFileProperties.getProperty(propKey);		
		clientReceptionTimeout = clientSocketTimeout*2;
		if (null != propValue) {
			try {
				clientReceptionTimeout = Long.parseLong(propValue);
				if (clientReceptionTimeout < clientSocketTimeout) {
					log.error("warning:: key[{}] minimum value[{}] but value[{}]", propKey, clientSocketTimeout, propValue);
					System.exit(1);
				}
			} catch(NumberFormatException e) {
				this.log.error("warning:: key[{}] integer but value[{}]", propKey, propValue);
				System.exit(1);
			}
		}
		log.info("{}::prop value[{}], new value[{}]", propKey, propValue, clientReceptionTimeout);
		/***** 모니터 종료 *****/
	}
	
	/*******************************************************************/
	public CONNECTION_TYPE getConnectionType() {
		return connectionType;
	}
	
	public int getClientConnectionCount() {
		return clientConnectionCount;
	}

	public boolean getClientWhetherToAutoConnect() {
		return clientWhetherToAutoConnect;
	}
	
	public long getClientSocketTimeout() {
		return clientSocketTimeout;
	}

	public int getClientOutputMessageQueueSize() {
		return clientOutputMessageQueueSize;
	}

	public int getClientShareAsynConnMailboxCnt() {
		return clientShareAsynConnMailboxCnt;
	}
	
	public int getClientFinishConnectMaxCall() {
		return clientFinishConnectMaxCall;
	}
	
	public long getClientFinishConnectWaittingTime() {
		return clientFinishConnectWaittingTime;
	}
	
	public int getClientAsynOutputMessageExecutorThreadCnt() {
		return clientAsynOutputMessageExecutorThreadCnt;
	}
	
	public int getClientInputMessageWriterSize() {
		return clientInputMessageWriterSize;
	}

	public int getClientInputMessageWriterMaxSize() {
		return clientInputMessageWriterMaxSize;
	}

	public int getClientOutputMessageReaderSize() {
		return clientOutputMessageReaderSize;
	}
	
	public int getClientOutputMessageReaderMaxSize() {
		return clientOutputMessageReaderMaxSize;
	}

	public long getClientReadSelectorWakeupInterval() {
		return clientrReadSelectorWakeupInterval;
	}
	
	public int getClientInputMessageQueueSize() {
		return clientInputMessageQueueSize;
	}	
	
	public int getClientDataPacketBufferCnt() {
		return clientDataPacketBufferCnt;
	}
	
	
	public long getClientMonitorTimeInterval() {
		return clientMonitorTimeInterval;
	}
	
	public long getClientReceptionTimeout() {
		return clientReceptionTimeout;
	}
	
}
