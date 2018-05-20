package kr.pe.codda.common.config.subset;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

import kr.pe.codda.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.type.ConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.type.ProjectType;

/**
 * Warning! 비활성한 항목의 값은 쓰레기값이므로 환경 설정 파일을 읽어와서 Value Object 에 저장할때 건너뛰어 초기값인 null 값을 갖게 된다.
 * 따라서 Value Object 에서 값을 넘겨줄때 비활성 항목의 경우 null 검사를 수행하여 ConfigErrorException 을 던져야 한다.
 * 
 * @author Won Jonghoon
 *
 */
public class ProjectPartConfiguration {	
	// private Logger log = LoggerFactory.getLogger(ProjectPartValueObject.class);
	private String projectName = null;
	private ProjectType projectType = null;
	private String prefexOfItemID = null;
	
	/************* common 변수 시작 ******************/	
	private String serverHost = null;
	private Integer  serverPort = null;
	private ByteOrder byteOrder = null;
	private Charset charset = null;
	
	private Integer dataPacketBufferMaxCntPerMessage = null;	
	private Integer dataPacketBufferSize = null;
	private Integer dataPacketBufferPoolSize = null;
	private Integer messageIDFixedSize = null;	
		
	private MessageProtocolType messageProtocolType = null;
	
	/***** 서버 동적 클래스 변수 시작 *****/
	private String firstPrefixDynamicClassFullName = null;
	/***** 서버 동적 클래스 변수 종료 *****/
	/************* common 변수 종료 ******************/
	
	/************* client 변수 시작 ******************/
	/***** 모니터 환경 변수 시작 *****/
	private Long clientMonitorTimeInterval = null;
	/***** 모니터 환경 변수 종료 *****/
	
	/***** 연결 클래스 관련 환경 변수 시작 *****/
	/** 연결 종류 */
	private ConnectionType connectionType = null;
	/** 소켓 타임 아웃 시간 */
	private Long clientSocketTimeout = null;
	
	/** 연결 클래스 갯수 */
	private Integer  clientConnectionCount = null;
	
	private Integer  clientConnectionMaxCount = null;
	
	/***** 연결 클래스 관련 환경 변수 종료 *****/	
	
	/** 비동기+공유 연결의 개인 메일함 갯수 */
	private Integer  clientAsynPirvateMailboxCntPerPublicConnection = null;
	
	/** 비동기 출력 메시지 처리자 쓰레드 갯수 */
	private Integer  clientAsynExecutorPoolSize = null;
		
	/** 입력 메시지 큐 크기 */
	private Integer  clientAsynInputMessageQueueSize = null;
	
	/** 입력 메시지 소켓 쓰기 담당 쓰레드 초기 갯수 */
	private Integer  clientAsynInputMessageWriterPoolSize = null;
	
	
	private Integer  clientAsynOutputMessageQueueSize = null;
	
	/** 출력 메시지 소켓 읽기 담당 쓰레드 갯수 */
	private Integer  clientAsynOutputMessageReaderPoolSize = null;
	
	/** 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기 */
	private Long clientWakeupIntervalOfSelectorForReadEventOnly = null;	
	/************* client 변수 종료 ******************/
	
	/************* server 변수 시작 ******************/
	/***** 모니터 환경 변수 시작 *****/
	private Long serverMonitorTimeInterval = null;
	/***** 모니터 환경 변수 종료 *****/
	
	private Integer serverMaxClients = null;		
	
	/***** 서버 비동기 입출력 지원용 자원 시작 *****/
	private Integer  serverAcceptQueueSize = null;
	private Integer  serverInputMessageQueueSize = null;
	private Integer  serverOutputMessageQueueSize = null;
	
	// private Long serverAcceptSelectorTimeout = null;
	private Long serverWakeupIntervalOfSelectorForReadEventOnly = null;
	private Integer  serverAcceptProcessorMaxSize = null;
	private Integer  serverAcceptProcessorSize = null;
	private Integer  serverInputMessageReaderPoolMaxSize = null;
	private Integer  serverInputMessageReaderPoolSize = null;	
	private Integer  serverExecutorPoolMaxSize = null;
	private Integer  serverExecutorPoolSize = null;
	private Integer  serverOutputMessageWriterPoolMaxSize = null;
	private Integer  serverOutputMessageWriterPoolSize = null;		
	/***** 서버 비동기 입출력 지원용 자원 종료 *****/
	
	
	/******** MyBatis 시작 **********/
	private String serverMybatisConfigFileRelativePathString = null;
	/******** MyBatis 종료 **********/
	/************* server 변수 종료 ******************/
	
	
	public ProjectPartConfiguration(ProjectType projectType, String projectName) {
		this.projectName = projectName;
		this.projectType = projectType;
		if (this.projectType.equals(ProjectType.MAIN)) {
			prefexOfItemID = new StringBuilder("mainproject.").toString();
		} else {
			prefexOfItemID = new StringBuilder("subproject.").append(projectName)
					.append(".").toString();
		}
	}
	
	public String getServerHost() {
		return serverHost;
	}

	public int getServerPort() {
		return serverPort;
	}

	public ByteOrder getByteOrder() {
		return byteOrder;
	}

	public Charset getCharset() {
		return charset;
	}

	public int getDataPacketBufferMaxCntPerMessage() {
		return dataPacketBufferMaxCntPerMessage;
	}

	public int getDataPacketBufferSize() {
		return dataPacketBufferSize;
	}

	public int getMessageIDFixedSize() {
		return messageIDFixedSize;
	}

	public MessageProtocolType getMessageProtocolType() {
		return messageProtocolType;
	}

	public String getFirstPrefixDynamicClassFullName() {
		return firstPrefixDynamicClassFullName;
	}

	public ConnectionType getConnectionType() {
		return connectionType;
	}

	public int getClientConnectionCount() {
		return clientConnectionCount;
	}
	
	public int getClientConnectionMaxCount() {
		return clientConnectionMaxCount;
	}
	

	public long getClientSocketTimeout() {
		return clientSocketTimeout;
	}

	

	public int getClientAsynExecutorPoolSize() {
		return clientAsynExecutorPoolSize;
	}

	public int getClientAsynOutputMessageQueueSize() {
		return clientAsynOutputMessageQueueSize;
	}
	

	public int getClientAsynPirvateMailboxCntPerPublicConnection() {
		return clientAsynPirvateMailboxCntPerPublicConnection;
	}

	public int getClientAsynInputMessageWriterPoolSize() {
		return clientAsynInputMessageWriterPoolSize;
	}

	

	public int getClientAsynOutputMessageReaderPoolSize() {
		return clientAsynOutputMessageReaderPoolSize;
	}



	public long getClientWakeupIntervalOfSelectorForReadEventOnly() {
		return clientWakeupIntervalOfSelectorForReadEventOnly;
	}

	public int getClientAsynInputMessageQueueSize() {
		return clientAsynInputMessageQueueSize;
	}

	

	public long getClientMonitorTimeInterval() {
		return clientMonitorTimeInterval;
	}

	public int getServerAcceptProcessorSize() {
		return serverAcceptProcessorSize;
	}

	public int getServerAcceptProcessorMaxSize() {
		return serverAcceptProcessorMaxSize;
	}

	public int getServerInputMessageReaderPoolSize() {
		return serverInputMessageReaderPoolSize;
	}

	public int getServerInputMessageReaderPoolMaxSize() {
		return serverInputMessageReaderPoolMaxSize;
	}

	public long getServerWakeupIntervalOfSelectorForReadEventOnly() {
		return serverWakeupIntervalOfSelectorForReadEventOnly;
	}

	public int getServerExecutorPoolSize() {
		return serverExecutorPoolSize;
	}

	public int getServerExecutorPoolMaxSize() {
		return serverExecutorPoolMaxSize;
	}

	public int getServerOutputMessageWriterPoolSize() {
		return serverOutputMessageWriterPoolSize;
	}

	public int getServerOutputMessageWriterPoolMaxSize() {
		return serverOutputMessageWriterPoolMaxSize;
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

	public int getDataPacketBufferPoolSize() {
		return dataPacketBufferPoolSize;
	}

	public long getServerMonitorTimeInterval() {
		return serverMonitorTimeInterval;
	}


	public String getServerMybatisConfigFileRelativePathString() {
		return serverMybatisConfigFileRelativePathString;
	}

	

	public Integer getServerMaxClients() {
		return serverMaxClients;
	}
	
	public String getProjectName() {
		return projectName;
	}
		
	public void changeServerAddress(String newServerHost, int newServerPort) {
		this.serverHost = newServerHost;
		this.serverPort = newServerPort;
	}

	public void mapping(String itemKey, Object nativeValue) 
			throws IllegalArgumentException, CoddaConfigurationException {
		if (null == itemKey) {
			throw new IllegalArgumentException("the parameter itemKey is null");
		}
		
		if (null == nativeValue) {
			throw new IllegalArgumentException("the parameter nativeValue is null");
		}
		
		if (! itemKey.startsWith(prefexOfItemID)) {
			String errorMessage = new StringBuilder("the parameter itemKey[")
			.append("] doesn't start with prefix[")
			.append(prefexOfItemID).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		/**
		 * no IndexOutOfBoundsException because the variable itemKey starts with the variable prefexOfItemID
		 */
		String itemID = itemKey.substring(prefexOfItemID.length());	
		
		
		if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_HOST_ITEMID)) {
			if (!(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverHost = (String) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_PORT_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			this.serverPort = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_BYTEORDER_ITEMID)) {
			if (!(nativeValue instanceof ByteOrder)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(ByteOrder.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.byteOrder = (ByteOrder)nativeValue;			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_CHARSET_ITEMID)) {
			if (!(nativeValue instanceof Charset)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Charset.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.charset = (Charset)nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.dataPacketBufferMaxCntPerMessage = (Integer)nativeValue;			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.dataPacketBufferSize = (Integer)nativeValue;	
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.dataPacketBufferPoolSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_ID_FIXED_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.messageIDFixedSize = (Integer)nativeValue;			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_PROTOCOL_TYPE_ITEMID)) {
			if (!(nativeValue instanceof MessageProtocolType)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(MessageProtocolType.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.messageProtocolType = (MessageProtocolType)nativeValue;
			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_FIRST_PREFIX_DYNAMIC_CLASS_FULL_NAME_ITEMID)) {
			if (!(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.firstPrefixDynamicClassFullName = (String)nativeValue;
			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_MONITOR_TIME_INTERVAL_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientMonitorTimeInterval = (Long)nativeValue;
			
		
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_SOCKET_TIMEOUT_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientSocketTimeout = (Long) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_TYPE_ITEMID)) {
			if (!(nativeValue instanceof ConnectionType)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(ConnectionType.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.connectionType = (ConnectionType) nativeValue;
		
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_COUNT_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientConnectionCount = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_MAX_COUNT_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientConnectionMaxCount = (Integer) nativeValue;		
		
		
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_PIRVATE_MAILBOX_CNT_PER_PUBLIC_CONNECTION_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientAsynPirvateMailboxCntPerPublicConnection = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_QUEUE_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientAsynInputMessageQueueSize = (Integer) nativeValue;
		
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_WRITER_POOL_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientAsynInputMessageWriterPoolSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientAsynOutputMessageQueueSize = (Integer) nativeValue;	
		
		
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_READER_POOL_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientAsynOutputMessageReaderPoolSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_READ_ONLY_SELECTOR_WAKEUP_INTERVAL_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientWakeupIntervalOfSelectorForReadEventOnly = (Long) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_EXECUTOR_POOL_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientAsynExecutorPoolSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MONITOR_TIME_INTERVAL_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverMonitorTimeInterval = (Long) nativeValue;
		
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MAX_CLIENTS_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverMaxClients = (Integer) nativeValue;		
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_QUEUE_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverAcceptQueueSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_QUEUE_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverInputMessageQueueSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverOutputMessageQueueSize = (Integer) nativeValue;
		
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_READ_ONLY_SELECTOR_WAKEUP_INTERVAL_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverWakeupIntervalOfSelectorForReadEventOnly = (Long) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_PROCESSOR_MAX_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverAcceptProcessorMaxSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_PROCESSOR_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverAcceptProcessorSize = (Integer) nativeValue;
			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_READER_MAX_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverInputMessageReaderPoolMaxSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_READER_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverInputMessageReaderPoolSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_MAX_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverExecutorPoolMaxSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverExecutorPoolSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverOutputMessageWriterPoolMaxSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverOutputMessageWriterPoolSize = (Integer) nativeValue;		
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MYBATIS_CONFIG_FILE_RELATIVE_PATH_STRING_ITEMID)) {
			if (!(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverMybatisConfigFileRelativePathString = (String) nativeValue;
		} else {
			String errorMessage = new StringBuilder("unknown porject's part item id(=the parameter itemIDInfo[")
			.append(itemID)
			.append("]), check it").toString();
			throw new CoddaConfigurationException(errorMessage);
		}		
	}
	
	
	public void build(String host, 
			int port,
			ByteOrder byteOrder,
			Charset charset,
			int dataPacketBufferMaxCntPerMessage,
			int dataPacketBufferSize,
			int dataPacketBufferPoolSize,
			int messageIDFixedSize,
			MessageProtocolType messageProtocolType,
			String firstPrefixDynamicClassFullName,			
			long clientMonitorTimeInterval,
			ConnectionType connectionType,
			long clientSocketTimeout,			
			int clientConnectionCount,
			int clientConnectionMaxCount,
			int clientAsynPirvateMailboxCntPerPublicConnection,
			int clientAsynInputMessageQueueSize,
			int clientAsynOutputMessageQueueSize,
			long clientWakeupIntervalOfSelectorForReadEventOnly,			
			int clientAsynInputMessageWriterPoolSize,			
			int clientAsynOutputMessageReaderPoolSize,			
			int clientAsynExecutorPoolSize,
			long serverMonitorTimeInterval,
			int serverMaxClients,
			int serverAcceptQueueSize,
			int serverInputMessageQueueSize,
			int serverOutputMessageQueueSize,
			long serverWakeupIntervalOfSelectorForReadEventOnly,			
			int serverAcceptProcessorSize,
			int serverAcceptProcessorMaxSize,			
			int serverInputMessageReaderPoolSize,
			int serverInputMessageReaderPoolMaxSize,
			int serverExecutorPoolSize,
			int serverExecutorPoolMaxSize,
			int serverOutputMessageWriterPoolSize,
			int serverOutputMessageWriterPoolMaxSize,			
			String serverMybatisConfigFileRelativePathString) throws IllegalArgumentException, CoddaConfigurationException {
		
		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_HOST_ITEMID).toString(), host);
		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_PORT_ITEMID).toString(), port);
		mapping(
						new StringBuilder(prefexOfItemID)
								.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_BYTEORDER_ITEMID).toString(),
								byteOrder);
		mapping(
						new StringBuilder(prefexOfItemID)
								.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_CHARSET_ITEMID).toString(),
								charset);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID)
				.toString(), dataPacketBufferMaxCntPerMessage);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_SIZE_ITEMID).toString(), 
				dataPacketBufferSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID).toString(),
				dataPacketBufferPoolSize);

		mapping(
				new StringBuilder(prefexOfItemID)
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_ID_FIXED_SIZE_ITEMID).toString(),
						messageIDFixedSize);

		mapping(
				new StringBuilder(prefexOfItemID)
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_PROTOCOL_TYPE_ITEMID).toString(),
				messageProtocolType);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_FIRST_PREFIX_DYNAMIC_CLASS_FULL_NAME_ITEMID)
				.toString(), firstPrefixDynamicClassFullName);

		mapping(
				new StringBuilder(prefexOfItemID)
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_MONITOR_TIME_INTERVAL_ITEMID).toString(),
						clientMonitorTimeInterval);
		
		mapping(
				new StringBuilder(prefexOfItemID)
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_TYPE_ITEMID).toString(),
						connectionType);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_SOCKET_TIMEOUT_ITEMID).toString(), 
				clientSocketTimeout);

		

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_COUNT_ITEMID).toString(), 
				clientConnectionCount);
		
		mapping(
				new StringBuilder(prefexOfItemID)
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_MAX_COUNT_ITEMID).toString(),
						clientConnectionMaxCount);		

		mapping(new StringBuilder(prefexOfItemID).append(
				ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_PIRVATE_MAILBOX_CNT_PER_PUBLIC_CONNECTION_ITEMID)
				.toString(), clientAsynPirvateMailboxCntPerPublicConnection);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(),
				clientAsynInputMessageQueueSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(),
				clientAsynOutputMessageQueueSize);
		
		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_READ_ONLY_SELECTOR_WAKEUP_INTERVAL_ITEMID)
				.toString(), clientWakeupIntervalOfSelectorForReadEventOnly);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_WRITER_POOL_SIZE_ITEMID)
				.toString(), clientAsynInputMessageWriterPoolSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_READER_POOL_SIZE_ITEMID)
				.toString(), clientAsynOutputMessageReaderPoolSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_EXECUTOR_POOL_SIZE_ITEMID).toString(), 
				clientAsynExecutorPoolSize);		

		mapping(
				new StringBuilder(prefexOfItemID)
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MONITOR_TIME_INTERVAL_ITEMID).toString(),
						serverMonitorTimeInterval);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MAX_CLIENTS_ITEMID).toString(), serverMaxClients);

		mapping(
				new StringBuilder(prefexOfItemID)
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_QUEUE_SIZE_ITEMID).toString(),
						serverAcceptQueueSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(),
				serverInputMessageQueueSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(),
				serverOutputMessageQueueSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_READ_ONLY_SELECTOR_WAKEUP_INTERVAL_ITEMID)
				.toString(), serverWakeupIntervalOfSelectorForReadEventOnly);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_PROCESSOR_SIZE_ITEMID).toString(), 
				serverAcceptProcessorSize);
		
		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_PROCESSOR_MAX_SIZE_ITEMID).toString(),
				serverAcceptProcessorMaxSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_READER_SIZE_ITEMID).toString(),
				serverInputMessageReaderPoolSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_READER_MAX_SIZE_ITEMID)
				.toString(), serverInputMessageReaderPoolMaxSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_SIZE_ITEMID).toString(),
				serverExecutorPoolSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_MAX_SIZE_ITEMID)
				.toString(), serverExecutorPoolMaxSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_SIZE_ITEMID)
				.toString(), serverOutputMessageWriterPoolSize);
		

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID)
				.toString(), serverOutputMessageWriterPoolMaxSize);		

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MYBATIS_CONFIG_FILE_RELATIVE_PATH_STRING_ITEMID)
				.toString(), serverMybatisConfigFileRelativePathString);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProjectPartConfiguration [projectName=");
		builder.append(projectName);
		builder.append(", projectType=");
		builder.append(projectType);
		builder.append(", prefexOfItemID=");
		builder.append(prefexOfItemID);
		builder.append(", serverHost=");
		builder.append(serverHost);
		builder.append(", serverPort=");
		builder.append(serverPort);
		builder.append(", byteOrder=");
		builder.append(byteOrder);
		builder.append(", charset=");
		builder.append(charset);
		builder.append(", dataPacketBufferMaxCntPerMessage=");
		builder.append(dataPacketBufferMaxCntPerMessage);
		builder.append(", dataPacketBufferSize=");
		builder.append(dataPacketBufferSize);
		builder.append(", dataPacketBufferPoolSize=");
		builder.append(dataPacketBufferPoolSize);
		builder.append(", messageIDFixedSize=");
		builder.append(messageIDFixedSize);
		builder.append(", messageProtocolType=");
		builder.append(messageProtocolType);
		builder.append(", firstPrefixDynamicClassFullName=");
		builder.append(firstPrefixDynamicClassFullName);
		builder.append(", clientMonitorTimeInterval=");
		builder.append(clientMonitorTimeInterval);
		builder.append(", connectionType=");
		builder.append(connectionType);
		builder.append(", clientSocketTimeout=");
		builder.append(clientSocketTimeout);
		builder.append(", clientConnectionCount=");
		builder.append(clientConnectionCount);
		builder.append(", clientConnectionMaxCount=");
		builder.append(clientConnectionMaxCount);
		builder.append(", clientAsynPirvateMailboxCntPerPublicConnection=");
		builder.append(clientAsynPirvateMailboxCntPerPublicConnection);
		builder.append(", clientAsynExecutorPoolSize=");
		builder.append(clientAsynExecutorPoolSize);
		builder.append(", clientAsynInputMessageQueueSize=");
		builder.append(clientAsynInputMessageQueueSize);
		builder.append(", clientAsynInputMessageWriterPoolSize=");
		builder.append(clientAsynInputMessageWriterPoolSize);
		builder.append(", clientAsynOutputMessageQueueSize=");
		builder.append(clientAsynOutputMessageQueueSize);
		builder.append(", clientAsynOutputMessageReaderPoolSize=");
		builder.append(clientAsynOutputMessageReaderPoolSize);
		builder.append(", clientWakeupIntervalOfSelectorForReadEventOnly=");
		builder.append(clientWakeupIntervalOfSelectorForReadEventOnly);
		builder.append(", serverMonitorTimeInterval=");
		builder.append(serverMonitorTimeInterval);
		builder.append(", serverMaxClients=");
		builder.append(serverMaxClients);
		builder.append(", serverAcceptQueueSize=");
		builder.append(serverAcceptQueueSize);
		builder.append(", serverInputMessageQueueSize=");
		builder.append(serverInputMessageQueueSize);
		builder.append(", serverOutputMessageQueueSize=");
		builder.append(serverOutputMessageQueueSize);
		builder.append(", serverWakeupIntervalOfSelectorForReadEventOnly=");
		builder.append(serverWakeupIntervalOfSelectorForReadEventOnly);
		builder.append(", serverAcceptProcessorMaxSize=");
		builder.append(serverAcceptProcessorMaxSize);
		builder.append(", serverAcceptProcessorSize=");
		builder.append(serverAcceptProcessorSize);
		builder.append(", serverInputMessageReaderPoolMaxSize=");
		builder.append(serverInputMessageReaderPoolMaxSize);
		builder.append(", serverInputMessageReaderPoolSize=");
		builder.append(serverInputMessageReaderPoolSize);
		builder.append(", serverExecutorPoolMaxSize=");
		builder.append(serverExecutorPoolMaxSize);
		builder.append(", serverExecutorPoolSize=");
		builder.append(serverExecutorPoolSize);
		builder.append(", serverOutputMessageWriterPoolMaxSize=");
		builder.append(serverOutputMessageWriterPoolMaxSize);
		builder.append(", serverOutputMessageWriterPoolSize=");
		builder.append(serverOutputMessageWriterPoolSize);
		builder.append(", serverMybatisConfigFileRelativePathString=");
		builder.append(serverMybatisConfigFileRelativePathString);
		builder.append("]");
		return builder.toString();
	}	
}
