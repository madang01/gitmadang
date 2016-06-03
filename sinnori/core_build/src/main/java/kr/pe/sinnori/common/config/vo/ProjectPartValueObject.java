package kr.pe.sinnori.common.config.vo;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import kr.pe.sinnori.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.etc.CommonType.CONNECTION_TYPE;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;

/**
 * Warning! 비활성한 항목의 값은 쓰레기값이므로 환경 설정 파일을 읽어와서 Value Object 에 저장할때 건너뛰어 초기값인 null 값을 갖게 된다.
 * 따라서 Value Object 에서 값을 넘겨줄때 비활성 항목의 경우 null 검사를 수행하여 ConfigErrorException 을 던져야 한다.
 * 
 * @author Won Jonghoon
 *
 */
public class ProjectPartValueObject {	
	// private Logger log = LoggerFactory.getLogger(ProjectPartValueObject.class);
	private String projectName = null;
	private CommonType.PROJECT_GUBUN projectGubun = null;
	private String prefexOfItemID = null;
	
	/************* common 변수 시작 ******************/
	/** 메시지 정보 파일들이 위치한 경로 */
	private File messageInfoPath = null;
	
	private String serverHost = null;
	private Integer  serverPort = null;
	private ByteOrder byteOrder = null;
	private Charset charset = null;
	
	private Integer dataPacketBufferMaxCntPerMessage = null;	
	private Integer dataPacketBufferSize = null;
	private Integer  messageIDFixedSize = null;	
		
	private CommonType.MESSAGE_PROTOCOL_GUBUN messageProtocol = null;
	
	/***** 서버 동적 클래스 변수 시작 *****/
	private String classLoaderClassPackagePrefixName = null;
	/***** 서버 동적 클래스 변수 종료 *****/
	/************* common 변수 종료 ******************/
	
	/************* client 변수 시작 ******************/
	/***** 모니터 환경 변수 시작 *****/
	private Long clientMonitorTimeInterval = null;
	private Long clientMonitorReceptionTimeout = null;
	/***** 모니터 환경 변수 종료 *****/
	
	/***** 연결 클래스 관련 환경 변수 시작 *****/
	/** 연결 종류 */
	private CONNECTION_TYPE connectionType = null;
	/** 소켓 타임 아웃 시간 */
	private Long clientSocketTimeout = null;
	/** 연결 생성시 자동 접속 여부 */
	private Boolean clientWhetherAutoConnection = null;
	/** 연결 클래스 갯수 */
	private Integer  clientConnectionCount = null;
	/** 데이터 패킷 버퍼 수 */
	private Integer  clientDataPacketBufferCnt = null;
	
	/***** 연결 클래스 관련 환경 변수 종료 *****/
	
	/***** 비동기 입출력 지원용 자원 관련 환경 변수 시작 *****/
	/** 클라이언트 비동기 소켓 채널의 연결 확립 최대 시도 횟수 */
	private Integer  clientAsynFinishConnectMaxCall = null;
	/** 클라이언트 비동기 소켓 채널의 연결 확립을 재 시도 간격 */
	private Long clientFinishConnectWaittingTime = null;	
	/***** 비동기 입출력 지원용 자원 관련 환경 변수 종료 *****/	
	/** 비동기 출력 메시지 처리자 쓰레드 갯수 */
	private Integer  clientAsynOutputMessageExecutorThreadCnt = null;
	/** 메일함 갯수 */
	private Integer  clientAsynShareMailboxCnt = null;	
	/** 입력 메시지 큐 크기 */
	private Integer  clientAsynInputMessageQueueSize = null;
	
	/** 입력 메시지 소켓 쓰기 담당 쓰레드 최대 갯수 */
	private Integer  clientAsynInputMessageWriterMaxSize = null;
	/** 입력 메시지 소켓 쓰기 담당 쓰레드 초기 갯수 */
	private Integer  clientAsynInputMessageWriterSize = null;
	
	/** 출력 메시지 큐 크기 */
	private Integer  clientAsynOutputMessageQueueSize = null;	
	/** 출력 메시지 소켓 읽기 담당 쓰레드 최대 갯수 */
	private Integer  clientAsynOutputMessageReaderMaxSize = null;
	/** 출력 메시지 소켓 읽기 담당 쓰레드 갯수 */
	private Integer  clientAsynOutputMessageReaderSize = null;
	
	/** 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기 */
	private Long clientReadSelectorWakeupInterval = null;	
	/************* client 변수 종료 ******************/
	
	/************* server 변수 시작 ******************/
	/***** 모니터 환경 변수 시작 *****/
	private Long serverMonitorTimeInterval = null;
	private Long serverMonitorReceptionTimeout = null;
	/***** 모니터 환경 변수 종료 *****/
	
	private Integer serverMaxClients = null;
	private Integer  serverDataPacketBufferCnt = null;	
	
	/***** 서버 비동기 입출력 지원용 자원 시작 *****/
	private Integer  serverAcceptQueueSize = null;
	private Integer  serverInputMessageQueueSize = null;
	private Integer  serverOutputMessageQueueSize = null;
	
	private Long serverAcceptSelectorTimeout = null;
	private Long serverReadSelectorWakeupInterval = null;
	private Integer  serverAcceptProcessorMaxSize = null;
	private Integer  serverAcceptProcessorSize = null;
	private Integer  serverInputMessageReaderMaxSize = null;
	private Integer  serverInputMessageReaderSize = null;	
	private Integer  serverExecutorProcessorMaxSize = null;
	private Integer  serverExecutorProcessorSize = null;
	private Integer  serverOutputMessageWriterMaxSize = null;
	private Integer  serverOutputMessageWriterSize = null;		
	/***** 서버 비동기 입출력 지원용 자원 종료 *****/

	/***** 서버 동적 클래스 변수 시작 *****/
	private File serverClassloaderAPPINFPath = null;
	/***** 서버 동적 클래스 변수 종료 *****/
	
	
	/******** MyBatis 시작 **********/
	private String serverClassloaderMybatisConfigFileRelativePathString = null;
	/******** MyBatis 종료 **********/
	/************* server 변수 종료 ******************/
	
	
	public ProjectPartValueObject(CommonType.PROJECT_GUBUN projectGubun, String projectName) {
		this.projectName = projectName;
		this.projectGubun = projectGubun;
		if (this.projectGubun.equals(CommonType.PROJECT_GUBUN.MAIN_PROJECT)) {
			prefexOfItemID = new StringBuilder("mainproject.").toString();
		} else {
			prefexOfItemID = new StringBuilder("subproject.").append(projectName)
					.append(".").toString();
		}
	}
	
	public void mapping(String itemKey, Object nativeValue) 
			throws IllegalArgumentException, SinnoriConfigurationException, ClassCastException {
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
		
		
		if (itemID.equals("common.message_info.xmlpath.value")) {
			if (!(nativeValue instanceof File)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(File.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.messageInfoPath = (File) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_HOST_ITEMID)) {
			if (!(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
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
				throw new SinnoriConfigurationException(errorMessage);
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
				throw new SinnoriConfigurationException(errorMessage);
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
				throw new SinnoriConfigurationException(errorMessage);
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
				throw new SinnoriConfigurationException(errorMessage);
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
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.dataPacketBufferSize = (Integer)nativeValue;			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_ID_FIXED_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.messageIDFixedSize = (Integer)nativeValue;			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_PROTOCOL_ITEMID)) {
			if (!(nativeValue instanceof CommonType.MESSAGE_PROTOCOL_GUBUN)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(CommonType.MESSAGE_PROTOCOL_GUBUN.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.messageProtocol = (CommonType.MESSAGE_PROTOCOL_GUBUN)nativeValue;
			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_CLASSLOADER_CLASS_PACKAGE_PREFIX_NAME_ITEMID)) {
			if (!(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.classLoaderClassPackagePrefixName = (String)nativeValue;
			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_MONITOR_TIME_INTERVAL_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientMonitorTimeInterval = (Long)nativeValue;
			
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_MONITOR_RECEPTION_TIMEOUT_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientMonitorReceptionTimeout = (Long) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_TYPE_ITEMID)) {
			if (!(nativeValue instanceof CommonType.CONNECTION_TYPE)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(CommonType.CONNECTION_TYPE.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.connectionType = (CommonType.CONNECTION_TYPE) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_SOCKET_TIMEOUT_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientSocketTimeout = (Long) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_WHETHER_AUTO_CONNECTION_ITEMID)) {
			if (!(nativeValue instanceof Boolean)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Boolean.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientWhetherAutoConnection = (Boolean) nativeValue;	
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_COUNT_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientConnectionCount = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_CNT_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientDataPacketBufferCnt = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_FINISH_CONNECT_MAX_CALL_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientAsynFinishConnectMaxCall = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_FINISH_CONNECT_WAITTING_TIME_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientFinishConnectWaittingTime = (Long) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_EXECUTOR_THREAD_CNT_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientAsynOutputMessageExecutorThreadCnt = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_SHARE_MAILBOX_CNT_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientAsynShareMailboxCnt = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_QUEUE_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientAsynInputMessageQueueSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientAsynInputMessageWriterMaxSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_WRITER_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientAsynInputMessageWriterSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientAsynOutputMessageQueueSize = (Integer) nativeValue;	
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_READER_MAX_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientAsynOutputMessageReaderMaxSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_READER_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientAsynOutputMessageReaderSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_READ_SELECTOR_WAKEUP_INTERVAL_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.clientReadSelectorWakeupInterval = (Long) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MONITOR_TIME_INTERVAL_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverMonitorTimeInterval = (Long) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MONITOR_RECEPTION_TIMEOUT_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverMonitorReceptionTimeout = (Long) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MAX_CLIENTS_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverMaxClients = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_CNT_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverDataPacketBufferCnt = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_QUEUE_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
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
				throw new SinnoriConfigurationException(errorMessage);
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
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverOutputMessageQueueSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_ACCEPT_SELECTOR_TIMEOUT_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverAcceptSelectorTimeout = (Long) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_READ_SELECTOR_WAKEUP_INTERVAL_ITEMID)) {
			if (!(nativeValue instanceof Long)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Long.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverReadSelectorWakeupInterval = (Long) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_PROCESSOR_MAX_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
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
				throw new SinnoriConfigurationException(errorMessage);
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
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverInputMessageReaderMaxSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_READER_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverInputMessageReaderSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_PROCESSOR_MAX_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverExecutorProcessorMaxSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_PROCESSOR_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverExecutorProcessorSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverOutputMessageWriterMaxSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_SIZE_ITEMID)) {
			if (!(nativeValue instanceof Integer)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(Integer.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverOutputMessageWriterSize = (Integer) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_CLASSLOADER_APPINF_PATH_ITEMID)) {
			if (!(nativeValue instanceof File)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(File.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverClassloaderAPPINFPath = (File) nativeValue;
		} else if (itemID.equals(ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_CLASSLOADER_MYBATIS_CONFIG_FILE_RELATIVE_PATH_STRING_ITEMID)) {
			if (!(nativeValue instanceof String)) {
				String errorMessage = new StringBuilder("the generic type[")
				.append(nativeValue.getClass().getName())
				.append("] of the parameter itemIDInfo[")
				.append(itemID).append("] is differnet from the mapped variable's type[")
				.append(String.class.getName())
				.append("]").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}
			
			this.serverClassloaderMybatisConfigFileRelativePathString = (String) nativeValue;
		} else {
			String errorMessage = new StringBuilder("unknown porject's part item id(=the parameter itemIDInfo[")
			.append(itemID)
			.append("]), check it").toString();
			throw new SinnoriConfigurationException(errorMessage);
		}
		
	}

	public File getMessageInfoPath() {
		return messageInfoPath;
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

	public CommonType.MESSAGE_PROTOCOL_GUBUN getMessageProtocol() {
		return messageProtocol;
	}

	public String getClassLoaderClassPackagePrefixName() {
		return classLoaderClassPackagePrefixName;
	}

	public CONNECTION_TYPE getConnectionType() {
		return connectionType;
	}

	public int getClientConnectionCount() {
		return clientConnectionCount;
	}

	public boolean isClientWhetherToAutoConnect() {
		return clientWhetherAutoConnection;
	}

	public long getClientSocketTimeout() {
		return clientSocketTimeout;
	}

	public int getClientAsynFinishConnectMaxCall() {
		return clientAsynFinishConnectMaxCall;
	}

	public long getClientFinishConnectWaittingTime() {
		return clientFinishConnectWaittingTime;
	}

	public int getClientAsynOutputMessageExecutorThreadCnt() {
		return clientAsynOutputMessageExecutorThreadCnt;
	}

	public int getClientAsynOutputMessageQueueSize() {
		return clientAsynOutputMessageQueueSize;
	}

	public int getClientAsynShareMailboxCnt() {
		return clientAsynShareMailboxCnt;
	}

	public int getClientAsynInputMessageWriterSize() {
		return clientAsynInputMessageWriterSize;
	}

	public int getClientAsynInputMessageWriterMaxSize() {
		return clientAsynInputMessageWriterMaxSize;
	}

	public int getClientAsynOutputMessageReaderSize() {
		return clientAsynOutputMessageReaderSize;
	}

	public int getClientAsynOutputMessageReaderMaxSize() {
		return clientAsynOutputMessageReaderMaxSize;
	}

	public long getClientReadSelectorWakeupInterval() {
		return clientReadSelectorWakeupInterval;
	}

	public int getClientAsynInputMessageQueueSize() {
		return clientAsynInputMessageQueueSize;
	}

	public int getClientDataPacketBufferCnt() {
		return clientDataPacketBufferCnt;
	}

	public long getClientMonitorTimeInterval() {
		return clientMonitorTimeInterval;
	}

	public long getClientMonitorReceptionTimeout() {
		return clientMonitorReceptionTimeout;
	}

	public long getServerAcceptSelectorTimeout() {
		return serverAcceptSelectorTimeout;
	}

	public int getServerAcceptProcessorSize() {
		return serverAcceptProcessorSize;
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

	public long getServerMonitorTimeInterval() {
		return serverMonitorTimeInterval;
	}

	public long getServerMonitorReceptionTimeout() {
		return serverMonitorReceptionTimeout;
	}

	public File getServerClassloaderAPPINFPath() {
		return serverClassloaderAPPINFPath;
	}

	public String getServerClassloaderMybatisConfigFileRelativePathString() {
		return serverClassloaderMybatisConfigFileRelativePathString;
	}

	public Boolean getClientWhetherAutoConnection() {
		return clientWhetherAutoConnection;
	}

	public Integer getServerMaxClients() {
		return serverMaxClients;
	}
	
	public String getProjectName() {
		return projectName;
	}
		

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProjectPart [messageInfoPath=");
		builder.append(messageInfoPath);
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
		builder.append(", messageIDFixedSize=");
		builder.append(messageIDFixedSize);
		builder.append(", messageProtocol=");
		builder.append(messageProtocol);
		builder.append(", classLoaderClassPackagePrefixName=");
		builder.append(classLoaderClassPackagePrefixName);
		builder.append(", clientMonitorTimeInterval=");
		builder.append(clientMonitorTimeInterval);
		builder.append(", clientMonitorReceptionTimeout=");
		builder.append(clientMonitorReceptionTimeout);
		builder.append(", connectionType=");
		builder.append(connectionType);
		builder.append(", clientSocketTimeout=");
		builder.append(clientSocketTimeout);
		builder.append(", clientWhetherAutoConnection=");
		builder.append(clientWhetherAutoConnection);
		builder.append(", clientConnectionCount=");
		builder.append(clientConnectionCount);
		builder.append(", clientDataPacketBufferCnt=");
		builder.append(clientDataPacketBufferCnt);
		builder.append(", clientAsynFinishConnectMaxCall=");
		builder.append(clientAsynFinishConnectMaxCall);
		builder.append(", clientFinishConnectWaittingTime=");
		builder.append(clientFinishConnectWaittingTime);
		builder.append(", clientAsynOutputMessageExecutorThreadCnt=");
		builder.append(clientAsynOutputMessageExecutorThreadCnt);
		builder.append(", clientAsynShareMailboxCnt=");
		builder.append(clientAsynShareMailboxCnt);
		builder.append(", clientAsynInputMessageQueueSize=");
		builder.append(clientAsynInputMessageQueueSize);
		builder.append(", clientAsynInputMessageWriterMaxSize=");
		builder.append(clientAsynInputMessageWriterMaxSize);
		builder.append(", clientAsynInputMessageWriterSize=");
		builder.append(clientAsynInputMessageWriterSize);
		builder.append(", clientAsynOutputMessageQueueSize=");
		builder.append(clientAsynOutputMessageQueueSize);
		builder.append(", clientAsynOutputMessageReaderMaxSize=");
		builder.append(clientAsynOutputMessageReaderMaxSize);
		builder.append(", clientAsynOutputMessageReaderSize=");
		builder.append(clientAsynOutputMessageReaderSize);
		builder.append(", clientrReadSelectorWakeupInterval=");
		builder.append(clientReadSelectorWakeupInterval);
		builder.append(", serverMonitorTimeInterval=");
		builder.append(serverMonitorTimeInterval);
		builder.append(", serverMonitorReceptionTimeout=");
		builder.append(serverMonitorReceptionTimeout);
		builder.append(", serverMaxClients=");
		builder.append(serverMaxClients);
		builder.append(", serverDataPacketBufferCnt=");
		builder.append(serverDataPacketBufferCnt);
		builder.append(", serverAcceptQueueSize=");
		builder.append(serverAcceptQueueSize);
		builder.append(", serverInputMessageQueueSize=");
		builder.append(serverInputMessageQueueSize);
		builder.append(", serverOutputMessageQueueSize=");
		builder.append(serverOutputMessageQueueSize);
		builder.append(", serverAcceptSelectorTimeout=");
		builder.append(serverAcceptSelectorTimeout);
		builder.append(", serverReadSelectorWakeupInterval=");
		builder.append(serverReadSelectorWakeupInterval);
		builder.append(", serverAcceptProcessorMaxSize=");
		builder.append(serverAcceptProcessorMaxSize);
		builder.append(", serverAcceptProcessorSize=");
		builder.append(serverAcceptProcessorSize);
		builder.append(", serverInputMessageReaderMaxSize=");
		builder.append(serverInputMessageReaderMaxSize);
		builder.append(", serverInputMessageReaderSize=");
		builder.append(serverInputMessageReaderSize);
		builder.append(", serverExecutorProcessorMaxSize=");
		builder.append(serverExecutorProcessorMaxSize);
		builder.append(", serverExecutorProcessorSize=");
		builder.append(serverExecutorProcessorSize);
		builder.append(", serverOutputMessageWriterMaxSize=");
		builder.append(serverOutputMessageWriterMaxSize);
		builder.append(", serverOutputMessageWriterSize=");
		builder.append(serverOutputMessageWriterSize);
		builder.append(", serverClassLoaderAPPINFPath=");
		builder.append(serverClassloaderAPPINFPath);
		builder.append(", serverMybatisConfigFileRelativePath=");
		builder.append(serverClassloaderMybatisConfigFileRelativePathString);
		builder.append("]");
		return builder.toString();
	}	
}
