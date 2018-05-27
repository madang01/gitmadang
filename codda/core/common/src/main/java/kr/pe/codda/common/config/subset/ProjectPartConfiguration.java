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
	private MessageProtocolType messageProtocolType = null;
	
	
	/************* common 변수 종료 ******************/
	
	/************* client 변수 시작 ******************/
	/***** 모니터 환경 변수 시작 *****/
	private Long clientMonitorTimeInterval = null;
	/***** 모니터 환경 변수 종료 *****/
	
	private Boolean clientDataPacketBufferIsDirect = null;
	private Integer clientDataPacketBufferMaxCntPerMessage = null;	
	private Integer clientDataPacketBufferSize = null;
	private Integer clientDataPacketBufferPoolSize = null;
	
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
	private Integer  clientAsynOutputMessageQueueSize = null;
	
	/** 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기 */
	private Long clientSelectorWakeupInterval = null;	
	/************* client 변수 종료 ******************/
	
	/************* server 변수 시작 ******************/
	/***** 모니터 환경 변수 시작 *****/
	private Long serverMonitorTimeInterval = null;
	/***** 모니터 환경 변수 종료 *****/
	
	private Boolean serverDataPacketBufferIsDirect = null;
	private Integer serverDataPacketBufferMaxCntPerMessage = null;	
	private Integer serverDataPacketBufferSize = null;
	private Integer serverDataPacketBufferPoolSize = null;
	
	private Integer serverMaxClients = null;	
	
	private Long serverSelectorWakeupInterval = null;
	
	/***** 서버 비동기 입출력 지원용 자원 시작 *****/
	private Integer  serverInputMessageQueueSize = null;
	private Integer  serverOutputMessageQueueSize = null;
	
	// private Long serverAcceptSelectorTimeout = null;
		
	private Integer  serverExecutorPoolMaxSize = null;
	private Integer  serverExecutorPoolSize = null;		
	/***** 서버 비동기 입출력 지원용 자원 종료 *****/
	
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

	public MessageProtocolType getMessageProtocolType() {
		return messageProtocolType;
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
	
	public boolean getClientDataPacketBufferIsDirect() {
		return clientDataPacketBufferIsDirect;
	}

	public int getClientDataPacketBufferMaxCntPerMessage() {
		return clientDataPacketBufferMaxCntPerMessage;
	}

	public int getClientDataPacketBufferSize() {
		return clientDataPacketBufferSize;
	}

	public int getClientDataPacketBufferPoolSize() {
		return clientDataPacketBufferPoolSize;
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



	public long getClientSelectorWakeupInterval() {
		return clientSelectorWakeupInterval;
	}

	public int getClientAsynInputMessageQueueSize() {
		return clientAsynInputMessageQueueSize;
	}	

	public long getClientMonitorTimeInterval() {
		return clientMonitorTimeInterval;
	}
	
	public boolean getServerDataPacketBufferIsDirect() {
		return serverDataPacketBufferIsDirect;
	}

	public int getServerDataPacketBufferMaxCntPerMessage() {
		return serverDataPacketBufferMaxCntPerMessage;
	}

	public int getServerDataPacketBufferSize() {
		return serverDataPacketBufferSize;
	}

	public int getServerDataPacketBufferPoolSize() {
		return serverDataPacketBufferPoolSize;
	}	

	public long getServerSelectorWakeupInterval() {
		return serverSelectorWakeupInterval;
	}

	public int getServerExecutorPoolSize() {
		return serverExecutorPoolSize;
	}

	public int getServerExecutorPoolMaxSize() {
		return serverExecutorPoolMaxSize;
	}

	public int getServerInputMessageQueueSize() {
		return serverInputMessageQueueSize;
	}

	public int getServerOutputMessageQueueSize() {
		return serverOutputMessageQueueSize;
	}

	

	public long getServerMonitorTimeInterval() {
		return serverMonitorTimeInterval;
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
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_IS_DIRECT_ITEMID)) {
			if (!(nativeValue instanceof Boolean)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientDataPacketBufferIsDirect = (Boolean)nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientDataPacketBufferMaxCntPerMessage = (Integer)nativeValue;			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientDataPacketBufferSize = (Integer)nativeValue;	
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientDataPacketBufferPoolSize = (Integer) nativeValue;
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
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_SELECTOR_WAKEUP_INTERVAL_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.clientSelectorWakeupInterval = (Long) nativeValue;
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
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_IS_DIRECT_ITEMID)) {
			if (!(nativeValue instanceof Boolean)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverDataPacketBufferIsDirect = (Boolean)nativeValue;		
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverDataPacketBufferMaxCntPerMessage = (Integer)nativeValue;			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverDataPacketBufferSize = (Integer)nativeValue;	
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverDataPacketBufferPoolSize = (Integer) nativeValue;
		
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
			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_SELECTOR_WAKEUP_INTERVAL_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
			
			this.serverSelectorWakeupInterval = (Long) nativeValue;
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
			int messageIDFixedSize,
			MessageProtocolType messageProtocolType,		
			long clientMonitorTimeInterval,
			boolean clientDataPacketBufferisDirect,
			int clientDataPacketBufferMaxCntPerMessage,
			int clientDataPacketBufferSize,
			int clientDataPacketBufferPoolSize,
			ConnectionType connectionType,
			long clientSocketTimeout,			
			int clientConnectionCount,
			int clientConnectionMaxCount,
			int clientAsynPirvateMailboxCntPerPublicConnection,
			int clientAsynInputMessageQueueSize,
			int clientAsynOutputMessageQueueSize,
			long clientAsynSelectorWakeupInterval,
			int clientAsynExecutorPoolSize,
			long serverMonitorTimeInterval,
			boolean serverDataPacketBufferisDirect,
			int serverDataPacketBufferMaxCntPerMessage,
			int serverDataPacketBufferSize,
			int serverDataPacketBufferPoolSize,
			int serverMaxClients,
			long serverSelectorWakeupInterval,
			int serverInputMessageQueueSize,
			int serverOutputMessageQueueSize,
			int serverExecutorPoolSize,
			int serverExecutorPoolMaxSize) throws IllegalArgumentException, CoddaConfigurationException {
		
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

		

		mapping(
				new StringBuilder(prefexOfItemID)
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_PROTOCOL_TYPE_ITEMID).toString(),
				messageProtocolType);

		

		mapping(
				new StringBuilder(prefexOfItemID)
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_MONITOR_TIME_INTERVAL_ITEMID).toString(),
						clientMonitorTimeInterval);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_IS_DIRECT_ITEMID)
				.toString(), clientDataPacketBufferisDirect);
		
		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID)
				.toString(), clientDataPacketBufferMaxCntPerMessage);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_SIZE_ITEMID).toString(), 
				clientDataPacketBufferSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID).toString(),
				clientDataPacketBufferPoolSize);
		
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
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_SELECTOR_WAKEUP_INTERVAL_ITEMID)
				.toString(), clientAsynSelectorWakeupInterval);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_EXECUTOR_POOL_SIZE_ITEMID).toString(), 
				clientAsynExecutorPoolSize);		

		mapping(
				new StringBuilder(prefexOfItemID)
						.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MONITOR_TIME_INTERVAL_ITEMID).toString(),
						serverMonitorTimeInterval);
		
		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_IS_DIRECT_ITEMID)
				.toString(), serverDataPacketBufferisDirect);
		
		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID)
				.toString(), serverDataPacketBufferMaxCntPerMessage);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_SIZE_ITEMID).toString(), 
				serverDataPacketBufferSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID).toString(),
				serverDataPacketBufferPoolSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MAX_CLIENTS_ITEMID).toString(), serverMaxClients);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_SELECTOR_WAKEUP_INTERVAL_ITEMID).toString(), serverSelectorWakeupInterval);
		
		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(),
				serverInputMessageQueueSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID).toString(),
				serverOutputMessageQueueSize);		

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_SIZE_ITEMID).toString(),
				serverExecutorPoolSize);

		mapping(new StringBuilder(prefexOfItemID)
				.append(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_MAX_SIZE_ITEMID)
				.toString(), serverExecutorPoolMaxSize);		

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
		builder.append(", messageProtocolType=");
		builder.append(messageProtocolType);
		builder.append(", clientMonitorTimeInterval=");
		builder.append(clientMonitorTimeInterval);
		builder.append(", clientDataPacketBufferIsDirect=");
		builder.append(clientDataPacketBufferIsDirect);
		builder.append(", clientDataPacketBufferMaxCntPerMessage=");
		builder.append(clientDataPacketBufferMaxCntPerMessage);
		builder.append(", clientDataPacketBufferSize=");
		builder.append(clientDataPacketBufferSize);
		builder.append(", clientDataPacketBufferPoolSize=");
		builder.append(clientDataPacketBufferPoolSize);	
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
		builder.append(", clientAsynOutputMessageQueueSize=");
		builder.append(clientAsynOutputMessageQueueSize);
		builder.append(", clientWakeupIntervalOfSelectorForReadEventOnly=");
		builder.append(clientSelectorWakeupInterval);
		builder.append(", serverMonitorTimeInterval=");
		builder.append(serverMonitorTimeInterval);
		builder.append(", serverDataPacketBufferIsDirect=");
		builder.append(serverDataPacketBufferIsDirect);
		builder.append(", serverDataPacketBufferMaxCntPerMessage=");
		builder.append(serverDataPacketBufferMaxCntPerMessage);
		builder.append(", serverDataPacketBufferSize=");
		builder.append(serverDataPacketBufferSize);
		builder.append(", serverDataPacketBufferPoolSize=");
		builder.append(serverDataPacketBufferPoolSize);
		builder.append(", serverMaxClients=");
		builder.append(serverMaxClients);
		builder.append(", serverSelectorWakeupInterval=");
		builder.append(serverSelectorWakeupInterval);
		builder.append(", serverInputMessageQueueSize=");
		builder.append(serverInputMessageQueueSize);
		builder.append(", serverOutputMessageQueueSize=");
		builder.append(serverOutputMessageQueueSize);				
		builder.append(", serverExecutorPoolMaxSize=");
		builder.append(serverExecutorPoolMaxSize);
		builder.append(", serverExecutorPoolSize=");
		builder.append(serverExecutorPoolSize);
		builder.append("]");
		return builder.toString();
	}	
}
