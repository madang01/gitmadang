package kr.pe.codda.common.config.itemidinfo;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.config.AbstractDependencyValidator;
import kr.pe.codda.common.config.AbstractDisabledItemChecker;
import kr.pe.codda.common.config.dependoninactivechecker.RSAKeyFileDisabledItemChecker;
import kr.pe.codda.common.config.dependonvalidchecker.MinAndMaxDependencyValidator;
import kr.pe.codda.common.config.fileorpathstringgetter.AbstractFileOrPathStringGetter;
import kr.pe.codda.common.config.fileorpathstringgetter.DBCPConfigFilePathStringGetter;
import kr.pe.codda.common.config.fileorpathstringgetter.SessionkeyRSAPrivatekeyFilePathStringGetter;
import kr.pe.codda.common.config.fileorpathstringgetter.SessionkeyRSAPublickeyFilePathStringGetter;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfo.ConfigurationPart;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningCharset;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningIntegerBetweenMinAndMax;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningLongBetweenMinAndMax;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningNoTrimString;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningRegularFile;
import kr.pe.codda.common.config.nativevalueconverter.GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterOfSessionKeyRSAKeypairSource;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningBoolean;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningByteOrder;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningConnectionType;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningInteger;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningMessageProtocolType;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterReturningString;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.type.ConnectionType;
import kr.pe.codda.common.type.MessageProtocolType;
import kr.pe.codda.common.type.SessionKey;
import kr.pe.codda.common.util.SequencedProperties;

/**
 * 신놀이 환경 설정 정보 클래스. 언어 종속적인 타입으로 변환할 정보, 특정 항목의 값에 영향을 받는 의존 관계 정보, 특정 항목의 특정
 * 값들에 의해서 비활성화 되는 정보를 구축한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class ItemIDInfoManger {
	private InternalLogger log = InternalLoggerFactory.getInstance(ItemIDInfoManger.class);
	

	private List<ItemIDInfo<?>> itemIDInfoList = new ArrayList<ItemIDInfo<?>>();

	private Map<String, ItemIDInfo<?>> itemIDInfoHash = new HashMap<String, ItemIDInfo<?>>();

	private Map<String, AbstractDisabledItemChecker> diabledItemCheckerHash = new HashMap<String, AbstractDisabledItemChecker>();
	private Map<String, AbstractDependencyValidator> dependencyValidationHash = new HashMap<String, AbstractDependencyValidator>();
	private Map<String, AbstractFileOrPathStringGetter> fileOrPathStringGetterHash =
			new HashMap<String, AbstractFileOrPathStringGetter>();
	
	
	private List<ItemIDInfo<?>> dbcpPartItemIDInfoList = new ArrayList<ItemIDInfo<?>>();
	private List<ItemIDInfo<?>> commonPartItemIDInfoList = new ArrayList<ItemIDInfo<?>>();
	private List<ItemIDInfo<?>> projectPartItemIDInfoList = new ArrayList<ItemIDInfo<?>>();

	/** 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스 */
	private static final class ItemIDInfoMangerHolder {
		static final ItemIDInfoManger singleton = new ItemIDInfoManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static ItemIDInfoManger getInstance() {
		return ItemIDInfoMangerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자
	 */
	private ItemIDInfoManger() {
		try {
			addAllDBCPPartItemIDInfo();
		} catch (IllegalArgumentException | CoddaConfigurationException e) {
			log.error(
					"fail to add all of dbcp part item identification informtion",
					e);
			System.exit(1);
		}
		try {
			addAllCommonPartItemIDInfo();
		} catch (IllegalArgumentException | CoddaConfigurationException e) {
			log.error(
					"fail to add all of common part item identification informtion",
					e);
			System.exit(1);
		}
		try {
			addAllProjectPartItemIDInfo();
		} catch (IllegalArgumentException | CoddaConfigurationException e) {
			log.error(
					"fail to add all of project part item identification informtion",
					e);
			System.exit(1);
		}

		try {
			addDependencyValidation();
		} catch (IllegalArgumentException | CoddaConfigurationException e) {
			log.error("fail to add valid checker", e);
			System.exit(1);
		}
		try {
			addAllDisabledItemChecker();
		} catch (IllegalArgumentException | CoddaConfigurationException e) {
			log.error("fail to add inactive checker", e);
			System.exit(1);
		}
		
		try {
			addAllFileOrPathStringGetter();			
		} catch (IllegalArgumentException | CoddaConfigurationException e) {
			log.error("fail to add inactive checker", e);
			System.exit(1);
		}
		
		itemIDInfoList = Collections
				.unmodifiableList(itemIDInfoList);		
		itemIDInfoHash = Collections.unmodifiableMap(itemIDInfoHash);
		
		diabledItemCheckerHash = Collections.unmodifiableMap(diabledItemCheckerHash);
		dependencyValidationHash = Collections.unmodifiableMap(dependencyValidationHash);
		
		dbcpPartItemIDInfoList = Collections
				.unmodifiableList(dbcpPartItemIDInfoList);
		commonPartItemIDInfoList = Collections
				.unmodifiableList(commonPartItemIDInfoList);
		projectPartItemIDInfoList = Collections
				.unmodifiableList(projectPartItemIDInfoList);
	}
	
	private void addAllCommonPartItemIDInfo()
			throws IllegalArgumentException, CoddaConfigurationException {
		

		ItemIDInfo<?> itemIDInfo = null;
		String itemID = null;
		boolean isDefaultValueCheck = false;

		/** Common start */
		try {
			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SERVLET_JSP_JDF_ERROR_MESSAGE_PAGE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"JDF framework 에서 에러 발생시 에러 내용을 보여주는 사용자 친화적인 화면을 전담할 jsp",
					"/errorMessagePage.jsp", isDefaultValueCheck,
					new GeneralConverterReturningNoTrimString());
			addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SERVLET_JSP_JDF_LOGIN_PAGE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.TEXT, itemID, "로그인 처리 jsp",
					"/menu/member/login.jsp", isDefaultValueCheck,
					new GeneralConverterReturningNoTrimString());
			addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SERVLET_JSP_JDF_SERVLET_TRACE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Boolean>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"JDF framework에서 서블릿 경과시간 추적 여부", "true",
					isDefaultValueCheck,
					new SetTypeConverterReturningBoolean());
			addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SERVLET_JSP_WEB_LAYOUT_CONTROL_PAGE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.TEXT, itemID,
					"신놀이 웹 사이트의 레이아웃 컨트롤러 jsp", "/PageJump.jsp",
					isDefaultValueCheck,
					new GeneralConverterReturningNoTrimString());
			addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<SessionKey.RSAKeypairSourceType>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET,
					itemID,
					"세션키에 사용되는 공개키 키쌍 생성 방법, API:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성",
					SessionKey.RSAKeypairSourceType.SERVER.toString(), isDefaultValueCheck,
					new SetTypeConverterOfSessionKeyRSAKeypairSource());
			addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID;
			isDefaultValueCheck = false;
			itemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.FILE,
					itemID,
					"세션키에 사용되는 RSA 공개키 파일",
					"<installed path>/project/<main project name>/resouces/rsa_keypair/"+CommonStaticFinalVars.PUBLIC_KEY_FILE_NAME,
					isDefaultValueCheck,
					new GeneralConverterReturningRegularFile(false));
			addCommonPartItemIDInfo(itemIDInfo);
			
			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID;
			isDefaultValueCheck = false;
			itemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.FILE,
					itemID,
					"세션키에 사용되는 RSA 개인키 파일",
					"<sinnnori installed path>/project/<main project name>/resouces/rsa_keypair/"+CommonStaticFinalVars.PRIVATE_KEY_FILE_NAME,
					isDefaultValueCheck,
					new GeneralConverterReturningRegularFile(false));
			addCommonPartItemIDInfo(itemIDInfo);
			

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYSIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"세션키에 사용하는 공개키 크기, 단위 byte", "1024",
					isDefaultValueCheck,
					new SetTypeConverterReturningInteger("512", "1024",
							"2048"));
			addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_SYMMETRIC_KEY_ALGORITHM_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"세션키에 사용되는 대칭키 알고리즘", "AES", isDefaultValueCheck,
					new SetTypeConverterReturningString("AES", "DESede",
							"DES"));
			addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_SYMMETRIC_KEY_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"세션키에 사용되는 대칭키 크기", "16", true,
					new SetTypeConverterReturningInteger("8", "16", "24"));
			addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_IV_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"세션키에 사용되는 대칭키와 같이 사용되는 IV 크기", "16",
					isDefaultValueCheck,
					new SetTypeConverterReturningInteger("8", "16", "24"));
			addCommonPartItemIDInfo(itemIDInfo);

		

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.COMMON_UPDOWNFILE_LOCAL_SOURCE_FILE_RESOURCE_CNT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"로컬 원본 파일 자원 갯수",
					"10",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.COMMON_UPDOWNFILE_LOCAL_TARGET_FILE_RESOURCE_CNT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"로컬 목적지 파일 자원 갯수",
					"10",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.COMMON_UPDOWNFILE_FILE_BLOCK_MAX_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"파일 송수신 파일 블락 최대 크기, 1024 배수, 단위 byte",
					"1048576",
					isDefaultValueCheck,
					new GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax(
							1024, Integer.MAX_VALUE));
			addCommonPartItemIDInfo(itemIDInfo);
			
			itemID = ItemIDDefiner.CommonPartItemIDDefiner.COMMON_CACHED_OBJECT_MAX_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"싱글턴 객체 캐쉬 관리자에서 캐쉬로 관리할 객체의 최대 갯수. 주로 캐쉬되는 대상 객체는 xxxServerCodec, xxxClientCodec 이다",
					"100",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addCommonPartItemIDInfo(itemIDInfo);
		} catch (CoddaConfigurationException | IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"fail to add common part item identification[")
					.append(itemID).append("] information").toString();

			log.info(errorMessage, e);

			throw new CoddaConfigurationException(new StringBuilder(errorMessage)
			.append(", errrorMessage=").append(e.getMessage()).toString());
		}
		/** Common end */
	}
	
	private void addAllDBCPPartItemIDInfo()
			throws IllegalArgumentException, CoddaConfigurationException {		
		ItemIDInfo<?> itemIDInfo = null;
		String itemID = null;

		boolean isDefaultValueCheck = false;

		/** DBCP start */
		try {
			itemID = ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID;
			isDefaultValueCheck = false;
			{
				boolean isWritePermissionChecking = false;
				itemIDInfo = new ItemIDInfo<File>(
						ItemIDInfo.ConfigurationPart.DBCP,
						ItemIDInfo.ViewType.FILE,
						itemID,
						"dbcp 설정 파일 경로명",
						"<installed path>/project/<main project name>/config/<dbcp name>.properties",
						isDefaultValueCheck,
						new GeneralConverterReturningRegularFile(
								isWritePermissionChecking));
			}

			addDBCPPartItemIDInfo(itemIDInfo);
		} catch (CoddaConfigurationException | IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"fail to add dbcp part item identification[")
					.append(itemID).append("] information").toString();

			log.info(errorMessage, e);

			throw new CoddaConfigurationException(new StringBuilder(errorMessage)
			.append(", errrorMessage=").append(e.getMessage()).toString());
		}

		/** DBCP end */
	}
	
	private void addAllProjectPartItemIDInfo()
			throws IllegalArgumentException, CoddaConfigurationException {
		ItemIDInfo<?> itemIDInfo = null;
		String itemID = null;
		boolean isDefaultValueCheck = false;
	
		try {
			/** 프로젝트 공통 설정 부분 */
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_HOST_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID, "클라이언트에서 접속할 서버 주소",
					"localhost", isDefaultValueCheck,
					new GeneralConverterReturningNoTrimString());
			addProjectPartItemIDInfo(itemIDInfo);
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_PORT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"포트 번호",
					"9090",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1024, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_BYTEORDER_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<ByteOrder>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"바이트 오더, LITTLE_ENDIAN:리틀 엔디안, BIG_ENDIAN:빅 엔디안",
					"LITTLE_ENDIAN", isDefaultValueCheck,
					new SetTypeConverterReturningByteOrder());
			addProjectPartItemIDInfo(itemIDInfo);
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_CHARSET_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Charset>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID, "문자셋", "UTF-8",
					isDefaultValueCheck, new GeneralConverterReturningCharset());
			addProjectPartItemIDInfo(itemIDInfo);
			
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_PROTOCOL_TYPE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<MessageProtocolType>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"메시지 프로토콜, DHB:교차 md5 헤더+바디, DJSON:길이+존슨문자열, THB:길이+바디",
					"DHB", isDefaultValueCheck,
					new SetTypeConverterReturningMessageProtocolType());
			addProjectPartItemIDInfo(itemIDInfo);
	
			
			/** 프로젝트 클라이언트 설정 부분 */
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_MONITOR_TIME_INTERVAL_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID, "모니터링 주기, 단위 ms", "5000",
					isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1000L, (long) Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
			
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_IS_DIRECT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Boolean>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"whether or not this byte buffer is direct", "true",
					isDefaultValueCheck,
					new SetTypeConverterReturningBoolean());
			addProjectPartItemIDInfo(itemIDInfo);
			
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"1개 메시지당 할당 받을 수 있는 데이터 패킷 버퍼 최대수",
					"1000",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"데이터 패킷 버퍼 크기, 단위 byte",
					"4096",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1024, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
			
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"데이터 패킷 버퍼 큐 크기",
					"1000",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
	
			
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_TYPE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<ConnectionType>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.SINGLE_SET,
					itemID,
					"소캣 랩퍼 클래스인 연결 종류, ASYN_PUBLIC:공유+비동기, SYNC_PRIVATE:비공유+동기",
					"ASYN_PUBLIC", isDefaultValueCheck,
					new SetTypeConverterReturningConnectionType());
			addProjectPartItemIDInfo(itemIDInfo);
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_SOCKET_TIMEOUT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID, "소켓 타임아웃, 단위 ms", "5000",
					isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1000L, (long) Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);			
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_COUNT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"연결 갯수",
					"1",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							0, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
			
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_POOL_SUPPORTOR_TIME_INTERVAL_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"연결 지원자 수행 주기",
					"600000",
					isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1000L, Long.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
			
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_MAX_COUNT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"연결 최대 갯수",
					"5",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							0, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
								
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_EXECUTOR_POOL_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"비동기 출력 메시지 처리자 쓰레드 갯수",
					"1",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_SYNC_MESSAGE_MAILBOX_COUNT_PER_ASYN_NOSHARE_CONNECTION_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"비동기+공유 연결 클래스(ShareAsynConnection)의 메일함 갯수",
					"2",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_QUEUE_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"클라이언트 비동기 입출력 지원용 입력 메시지 큐 크기",
					"10",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
	
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"비동기 메시지에 대한 1:1 비지니스 로직 처리기(ClientExecutor) 가  갖는 출력 메시지 큐 크기",
					"10",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);			
					
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_SELECTOR_WAKEUP_INTERVAL_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"클라이언트 비동기 입출력 지원용 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기. 단위 ms",
					"1", isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1L, (long) Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
	
			/** 프로젝트 서버 설정 부분 */
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MONITOR_TIME_INTERVAL_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID, "모니터링 주기, 단위 ms", "5000",
					isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1000L, (long) Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_IS_DIRECT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Boolean>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"whether or not this byte buffer is direct", "true",
					isDefaultValueCheck,
					new SetTypeConverterReturningBoolean());
			addProjectPartItemIDInfo(itemIDInfo);
			
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"1개 메시지당 할당 받을 수 있는 데이터 패킷 버퍼 최대수",
					"1000",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"데이터 패킷 버퍼 크기, 단위 byte",
					"4096",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1024, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
			
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_POOL_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"데이터 패킷 버퍼 큐 크기",
					"1000",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
			
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MAX_CLIENTS_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"서버로 접속할 수 있는 최대 클라이언트 수",
					"5",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
			
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_QUEUE_CAPACITY_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"입력 메시지 큐 크기",
					"10",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							10, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_QUEUE_CAPACITY_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"출력 메시지 큐 크기",
					"10",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							10, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_MAX_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수",
					"1",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
	
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수",
					"1",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);
			
	
		} catch (CoddaConfigurationException | IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"fail to add project part item identification[")
					.append(itemID).append("] information").toString();
	
			log.info(errorMessage, e);
	
			throw new CoddaConfigurationException(new StringBuilder(errorMessage)
					.append(", errrorMessage=").append(e.getMessage())
					.toString());
		}
	}

	private void addItemIDInfo(ItemIDInfo<?> itemIDInfo)
			throws IllegalArgumentException, UnsupportedOperationException {
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder(
					"the paramter itemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
	
		ItemIDInfo<?> olditemIDConfigInfo = itemIDInfoHash.get(itemIDInfo
				.getItemID());
		if (null != olditemIDConfigInfo) {
			String errorMessage = new StringBuilder("the item id[")
					.append(itemIDInfo.getItemID()).append("] was registed")
					.toString();
	
			// log.warn(errorMessage);
	
			throw new IllegalArgumentException(errorMessage);
		}
	
		itemIDInfoHash.put(itemIDInfo.getItemID(), itemIDInfo);
		itemIDInfoList.add(itemIDInfo);
	
		ConfigurationPart itemConfigPart = itemIDInfo.getConfigurationPart();
	
		if (ItemIDInfo.ConfigurationPart.DBCP == itemConfigPart) {
			dbcpPartItemIDInfoList.add(itemIDInfo);
		} else if (ItemIDInfo.ConfigurationPart.COMMON == itemConfigPart) {
			commonPartItemIDInfoList.add(itemIDInfo);
		} else {
			projectPartItemIDInfoList.add(itemIDInfo);
		}
	}

	private void addDBCPPartItemIDInfo(ItemIDInfo<?> itemIDInfo)
			throws IllegalArgumentException, UnsupportedOperationException {
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder(
					"the paramter itemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (!itemIDInfo.getConfigurationPart().equals(
				ItemIDInfo.ConfigurationPart.DBCP)) {
			String errorMessage = new StringBuilder("the parameter itemIDInfo[")
					.append(itemIDInfo.getItemID())
					.append("] is not a dbcp part item id").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		addItemIDInfo(itemIDInfo);
	}

	private void addCommonPartItemIDInfo(ItemIDInfo<?> itemIDInfo)
			throws IllegalArgumentException, UnsupportedOperationException {
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder(
					"the paramter itemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
	
		if (!itemIDInfo.getConfigurationPart().equals(
				ItemIDInfo.ConfigurationPart.COMMON)) {
			String errorMessage = new StringBuilder("the parameter itemIDInfo[")
					.append(itemIDInfo.getItemID())
					.append("] is not a common part item id").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		addItemIDInfo(itemIDInfo);
	}

	private void addProjectPartItemIDInfo(ItemIDInfo<?> itemIDInfo)
			throws IllegalArgumentException, UnsupportedOperationException {
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder(
					"the paramter itemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
	
		if (!itemIDInfo.getConfigurationPart().equals(
				ItemIDInfo.ConfigurationPart.PROJECT)) {
			String errorMessage = new StringBuilder("the parameter itemIDInfo[")
					.append(itemIDInfo.getItemID())
					.append("] is not a project common part item id")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
	
		addItemIDInfo(itemIDInfo);
	}

	@SuppressWarnings("unchecked")
	private void addDependencyValidation() throws IllegalArgumentException,
			CoddaConfigurationException {
		
		{
			String dependentTargetItemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_MAX_COUNT_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new CoddaConfigurationException(errorMessage);
			}
	
			String dependentSourceItemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_COUNT_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new CoddaConfigurationException(errorMessage);
			}
	
			dependencyValidationHash
					.put(dependentSourceItemID,
							new MinAndMaxDependencyValidator<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}
		
	
		
		{
			String dependentTargetItemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_MAX_SIZE_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new CoddaConfigurationException(errorMessage);
			}
	
			String dependentSourceItemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_SIZE_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new CoddaConfigurationException(errorMessage);
			}
	
			dependencyValidationHash
					.put(dependentSourceItemID,
							new MinAndMaxDependencyValidator<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}
	}

	private void addAllDisabledItemChecker() throws IllegalArgumentException,
			CoddaConfigurationException {
		{
			String disabledTargetItemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID;
			String dependentItemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID;
	
			ItemIDInfo<?> disbaledTargetItemIDInfo = getItemIDInfo(disabledTargetItemID);
			if (null == disbaledTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"there is no RSA Public Key File item[")
						.append(disabledTargetItemID)
						.append("] identifier information, the RSA Public Key File item depends on RSA Keypair Source item[")
						.append(dependentItemID)
						.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
	
			ItemIDInfo<?> dependentItemIDInfo = getItemIDInfo(dependentItemID);
			if (null == dependentItemIDInfo) {
				String errorMessage = new StringBuilder(
						"there is no RSA Keypair Source item[")
						.append(dependentItemID)
						.append("] identifier information, the RSA Public Key File item[")
						.append(disabledTargetItemID)
						.append("] depends on RSA Keypair Source item").toString();
				// log.error(errorMessage);
				throw new CoddaConfigurationException(errorMessage);
			}
	
			diabledItemCheckerHash
					.put(disabledTargetItemID,
							new RSAKeyFileDisabledItemChecker(
									disbaledTargetItemIDInfo,
									dependentItemIDInfo,
									new String[] { SessionKey.RSAKeypairSourceType.SERVER
											.toString() }));
		}
		
		{
			String disabledTargetItemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID;
			String dependentItemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID;
	
			ItemIDInfo<?> disbaledTargetItemIDInfo = getItemIDInfo(disabledTargetItemID);
			if (null == disbaledTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"there is no RSA Public Key File item[")
						.append(disabledTargetItemID)
						.append("] identifier information, the RSA Public Key File item depends on RSA Keypair Source item[")
						.append(dependentItemID)
						.append("]").toString();
				throw new CoddaConfigurationException(errorMessage);
			}
	
			ItemIDInfo<?> dependentItemIDInfo = getItemIDInfo(dependentItemID);
			if (null == dependentItemIDInfo) {
				String errorMessage = new StringBuilder(
						"there is no RSA Keypair Source item[")
						.append(dependentItemID)
						.append("] identifier information, the RSA Public Key File item[")
						.append(disabledTargetItemID)
						.append("] depends on RSA Keypair Source item").toString();
				// log.error(errorMessage);
				throw new CoddaConfigurationException(errorMessage);
			}
	
			diabledItemCheckerHash
					.put(disabledTargetItemID,
							new RSAKeyFileDisabledItemChecker(
									disbaledTargetItemIDInfo,
									dependentItemIDInfo,
									new String[] { SessionKey.RSAKeypairSourceType.SERVER
											.toString() }));
		}
	
	}

	private void addAllFileOrPathStringGetter() throws IllegalArgumentException,
	CoddaConfigurationException {
		String itemID = null;
		
	
		itemID = ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID;
		fileOrPathStringGetterHash.put(itemID, new DBCPConfigFilePathStringGetter(itemID));
	
		itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID;
		fileOrPathStringGetterHash.put(itemID, new SessionkeyRSAPublickeyFilePathStringGetter(itemID));
		
		itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID;
		fileOrPathStringGetterHash.put(itemID, new SessionkeyRSAPrivatekeyFilePathStringGetter(itemID));
	}

	private ItemIDInfo<?> getItemIDInfo(String itemID) {
		return itemIDInfoHash.get(itemID);
	}

	/**
	 * 구축한 항목 식별자 정보를 바탕으로 주어진 메인 프로젝트 이름과 설치 경로에 맞도록 신규 생성된 신놀이 설정 시퀀스 프로퍼티를
	 * 반환한다.
	 * 
	 * @param mainProjectName
	 *            메인 프로젝트 이름
	 * @param installedPathString
	 *            신놀이 설치 경로
	 * @return 구축한 항목 식별자 정보를 바탕으로 주어진 메인 프로젝트 이름과 설치 경로에 맞도록 신규 생성된 신놀이 설정 시퀀스
	 *         프로퍼티
	 */
	public SequencedProperties getNewConfigSequencedProperties(String installedPathString, String mainProjectName) {
		
		SequencedProperties configSequencedProperties = new SequencedProperties();		

		/** common */
		{
			String prefixOfItemID = "";
			for (ItemIDInfo<?> commonPartItemIDInfo : commonPartItemIDInfoList) {
				String itemID = commonPartItemIDInfo.getItemID();
				String itemKey = itemID;
				String itemValue = commonPartItemIDInfo.getDefaultValue();
				
				String itemDescriptionKey = commonPartItemIDInfo
						.getItemDescKey(prefixOfItemID);
				String itemDescriptionValue = commonPartItemIDInfo.getDescription();
				
				AbstractFileOrPathStringGetter fileOrPathStringGetter = 
						fileOrPathStringGetterHash.get(itemID);
				
				if (null != fileOrPathStringGetter) {
					itemValue = fileOrPathStringGetter
							.getFileOrPathStringDependingOnInstalledPath(installedPathString, mainProjectName);
				}
				
				configSequencedProperties.put(itemDescriptionKey, itemDescriptionValue);
				configSequencedProperties.put(itemKey, itemValue);
			}
		}
		
		/** DBCP */
		{
			configSequencedProperties.setProperty(
					CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING,
					"");
		}
		

		/** main project */
		{
			String prefixOfItemID = new StringBuilder("mainproject.").toString();
			for (ItemIDInfo<?> mainProjectPartItemIDInfo : projectPartItemIDInfoList) {
				
				String itemID = mainProjectPartItemIDInfo.getItemID();
				String itemKey = new StringBuilder(prefixOfItemID).append(
						itemID).toString();				
				String itemValue = mainProjectPartItemIDInfo.getDefaultValue();
				
				String itemDescriptionKey = mainProjectPartItemIDInfo
						.getItemDescKey(prefixOfItemID);
				String itemDescriptionValue = mainProjectPartItemIDInfo.getDescription();
				
				AbstractFileOrPathStringGetter fileOrPathStringGetter = 
						fileOrPathStringGetterHash.get(itemID);
				
				if (null != fileOrPathStringGetter) {
					itemValue = fileOrPathStringGetter
							.getFileOrPathStringDependingOnInstalledPath(installedPathString, mainProjectName);
				}
				
				configSequencedProperties.put(itemDescriptionKey, itemDescriptionValue);
				configSequencedProperties.put(itemKey, itemValue);
			}
		}
		
		
		/** sub project */
		{
			configSequencedProperties.setProperty(
					CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING,
					"");

			
		}

		return configSequencedProperties;
	}
	
	public boolean isDisabled(String itemID, String prefixOfItemID, Properties sourceProperties) {
		boolean isDisabled = false;
		AbstractDisabledItemChecker disabledItemChecker = diabledItemCheckerHash
				.get(itemID);

		if (null != disabledItemChecker) {
			isDisabled = disabledItemChecker.isDisabled(sourceProperties,
					prefixOfItemID);
		}
		return isDisabled;
	}

	public boolean isFileOrPathStringGetter(String itemID) {
		boolean result = (null == fileOrPathStringGetterHash.get(itemID)) ? false : true;
		return result;
	}
	
	/**
	 * 
	 * @param itemID
	 * @return
	 */
	public AbstractFileOrPathStringGetter getFileOrPathStringGetter(String itemID) {
		if (null == itemID) {
			throw new IllegalArgumentException("the paramter itemID is null");
		}
		return fileOrPathStringGetterHash.get(itemID);
	}
	
	public ItemIDInfo<?> getItemIDInfoFromKey(String itemKey,
			Set<String> dbcpNameSet, Set<String> subProjectNameSet)
			throws IllegalArgumentException {
		if (null == itemKey) {
			String errorMessage = "the parameter itemKey is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemKey.equals(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING)
				|| itemKey
						.equals(CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING)) {
			return null;
		}

		if (! itemKey.endsWith(".value")) {
			return null;
		}

		ItemIDInfo<?> itemIDInfo = null;
		String itemID = null;

		StringTokenizer itemKeyStringTokenizer = new StringTokenizer(itemKey, ".");

		if (!itemKeyStringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder(
					"first token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		String firstToken = itemKeyStringTokenizer.nextToken();

		if (firstToken.equals("mainproject")) {
			itemID = getItemIDOfMainProjectPart(itemKey, itemKeyStringTokenizer, firstToken);

			itemIDInfo = getItemID(itemKey, itemID);

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			throwExceptionIfInvalidConfigurationPart(itemKey, itemIDInfo, ItemIDInfo.ConfigurationPart.PROJECT);		
		} else if (firstToken.equals("subproject")) {
			/** project part */
			itemID = getItemIDOfSubProjectPart(itemKey, subProjectNameSet, itemKeyStringTokenizer, firstToken);

			itemIDInfo = getItemID(itemKey, itemID);

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			throwExceptionIfInvalidConfigurationPart(itemKey, itemIDInfo, ItemIDInfo.ConfigurationPart.PROJECT);

		} else if (firstToken.equals("dbcp")) {
			/** dbcp part */
			itemID = getItemIDOfDBCPPart(itemKey, dbcpNameSet, itemKeyStringTokenizer, firstToken);

			itemIDInfo = getItemID(itemKey, itemID);

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			throwExceptionIfInvalidConfigurationPart(itemKey, itemIDInfo, ItemIDInfo.ConfigurationPart.DBCP);
		} else {
			/** common part */
			itemID = getItemIDOfCommonPart(itemKey, itemKeyStringTokenizer);

			itemIDInfo = getItemID(itemKey, itemID);

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			throwExceptionIfInvalidConfigurationPart(itemKey, itemIDInfo, ItemIDInfo.ConfigurationPart.COMMON);
		}

		return itemIDInfo;
	}

	private void throwExceptionIfInvalidConfigurationPart(String itemKey, ItemIDInfo<?> itemIDInfo, ItemIDInfo.ConfigurationPart wantedConfigurationPart) {
		ItemIDInfo.ConfigurationPart configPartOfItemID = itemIDInfo
				.getConfigurationPart();
		if (!configPartOfItemID.equals(wantedConfigurationPart)) {
			String errorMessage = new StringBuilder(
					"the configuration part[")
					.append(configPartOfItemID.toString())
					.append("] of the var itemIDInfo getting from the parameter itemKey[").append(itemKey)
					.append("] is not same to the wanted configuration part[")
					.append(wantedConfigurationPart.toString())
					.append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}

	private String getItemIDOfCommonPart(String itemKey, StringTokenizer stringTokenizer) {
		String itemID;
		if (!stringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder(
					"second token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		String secondToken = stringTokenizer.nextToken();
		if (secondToken.equals("")) {
			String errorMessage = new StringBuilder(
					"second token is a empty string in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		itemID = itemKey;
		return itemID;
	}

	private ItemIDInfo<?> getItemID(String itemKey, String itemID) {
		ItemIDInfo<?> itemIDInfo;
		itemIDInfo = itemIDInfoHash.get(itemID);
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder(
					"the parameter itemKey[").append(itemKey)
					.append("]'s itemID is not registed, check it")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		return itemIDInfo;
	}

	private String getItemIDOfDBCPPart(String itemKey, Set<String> dbcpNameSet, StringTokenizer stringTokenizer,
			String firstToken) {
		String itemID;
		if (!stringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder(
					"second token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		String dbcpName = stringTokenizer.nextToken();

		if (null != dbcpNameSet) {
			if (!dbcpNameSet.contains(dbcpName)) {
				String errorMessage = new StringBuilder("the item key[")
						.append(itemKey)
						.append("] has a wrong dbcp name not existing in the parameter dbcpNameList[")
						.append(dbcpNameSet.toString()).append("]")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (!stringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder(
					"third token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		String thirdToken = stringTokenizer.nextToken();
		if (thirdToken.equals("")) {
			String errorMessage = new StringBuilder(
					"third token is a empty string in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		String prefixOfItemID = firstToken + "." + dbcpName + ".";
		itemID = itemKey.substring(prefixOfItemID.length());
		return itemID;
	}

	private String getItemIDOfSubProjectPart(String itemKey, Set<String> subProjectNameSet,
			StringTokenizer stringTokenizer, String firstToken) {
		String itemID;
		if (!stringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder(
					"second token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		String projectName = stringTokenizer.nextToken();
		if (projectName.equals("")) {
			String errorMessage = "project name is an empty string at the project part";
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != subProjectNameSet) {
			if (!subProjectNameSet.contains(projectName)) {
				String errorMessage = new StringBuilder("the item key[")
						.append(itemKey)
						.append("] has a wrong project name not existing in the parameter projectNameList[")
						.append(subProjectNameSet.toString()).append("]")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		if (!stringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder(
					"third token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		String subPartName = stringTokenizer.nextToken();
		if (subPartName.equals("")) {
			String errorMessage = new StringBuilder(
					"subPartName is a empty string in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!subPartName.equals("common") && !subPartName.equals("client")
				&& !subPartName.equals("server")) {
			String errorMessage = new StringBuilder("the sub part[")
					.append(subPartName)
					.append("] of the parameter itemKey[")
					.append(itemKey)
					.append("]'s itemID is bad, it must have one element of set {'common', 'client', 'server'}")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!stringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder(
					"fourth token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		String fourthToken = stringTokenizer.nextToken();
		if (fourthToken.equals("")) {
			String errorMessage = new StringBuilder(
					"fourth Token is a empty string in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		String prefixOfItemID = firstToken + "." + projectName + ".";
		itemID = itemKey.substring(prefixOfItemID.length());
		return itemID;
	}

	private String getItemIDOfMainProjectPart(String itemKey, StringTokenizer itemKeyStringTokenizer, String firstToken) {
		String itemID;
		if (!itemKeyStringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder(
					"second token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		String subPartName = itemKeyStringTokenizer.nextToken();
		if (subPartName.equals("")) {
			String errorMessage = new StringBuilder(
					"subPartName is a empty string in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!subPartName.equals("common") && !subPartName.equals("client")
				&& !subPartName.equals("server")) {
			String errorMessage = new StringBuilder("the sub part[")
					.append(subPartName)
					.append("] of the parameter itemKey[")
					.append(itemKey)
					.append("]'s itemID is bad, it must have one element of set {'common', 'client', 'server'}")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!itemKeyStringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder(
					"third token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		String thirdToken = itemKeyStringTokenizer.nextToken();
		if (thirdToken.equals("")) {
			String errorMessage = new StringBuilder(
					"third Token is a empty string in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		String prefixOfItemID = firstToken + ".";
		itemID = itemKey.substring(prefixOfItemID.length());
		return itemID;
	}

	public Object getNativeValueAfterValidChecker(String itemKey,
			Properties sourceProperties) throws IllegalArgumentException,
			CoddaConfigurationException {
		if (null == itemKey) {
			throw new IllegalArgumentException("the parameter itemKey is null");
		}
		if (null == sourceProperties) {
			throw new IllegalArgumentException(
					"the parameter sourceProperties is null");
		}

		
		ItemIDInfo<?> itemIDInfo = null;
		
		try {
			itemIDInfo = getItemIDInfoFromKey(itemKey, null, null);
		} catch(IllegalArgumentException e) {
			/**
			 * same parameter name 'itemKey' so error message is same
			 */
			throw e;
		}				
				
		
		if (null == itemIDInfo) {
			String errorMessage = new StringBuilder(
					"the parameter itemValueKey[").append(itemKey)
					.append("](=dependentSourceKey) is bad, itemID is null").toString();

			log.warn(errorMessage);

			throw new CoddaConfigurationException(errorMessage);
		}

		String itemID = itemIDInfo.getItemID();
		int inx = itemKey.indexOf(itemID);
		String prefixOfItemID = itemKey.substring(0, inx);

		AbstractDependencyValidator dependOnValidCheck = dependencyValidationHash
				.get(itemID);

		if (null != dependOnValidCheck) {

			try {
				boolean isValid = dependOnValidCheck.isValid(sourceProperties,
						prefixOfItemID);
				if (!isValid) {
					String errorMessage = new StringBuilder(
							"the dependent source item")
							.append(prefixOfItemID)
							.append(dependOnValidCheck
									.getDependentSourceItemID())
							.append("] doesn't depend on the dependent target item[")
							.append(prefixOfItemID)
							.append(dependOnValidCheck
									.getDependentTargetItemID()).append("]")
							.toString();

					// log.warn(errorMessage);

					throw new CoddaConfigurationException(errorMessage);
				}
			} catch (IllegalArgumentException e) {
				String errorMessage = new StringBuilder(
						"the parameter itemValueKey[").append(itemKey)
						.append("]'s invalid check fails errrorMessage=")
						.append(e.getMessage()).toString();
				/** 다른 예외로 변환 되므로 이력 남긴다. */
				log.debug(errorMessage, e);

				throw new CoddaConfigurationException(errorMessage);
			}
		}

		Object itemNativeValue = null;
		String itemValue = sourceProperties.getProperty(itemKey);
		try {
			itemNativeValue = itemIDInfo.getItemValueConverter().valueOf(
					itemValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"fail to convert the parameter itemValueKey[")
					.append(itemKey).append("]'s value[")
					.append(sourceProperties.getProperty(itemKey))
					.append("] to a native value").toString();
			/** 다른 예외로 변환 되므로 이력 남긴다. */
			log.warn(errorMessage, e);

			throw new CoddaConfigurationException(
					new StringBuilder(errorMessage).append(", errormessage=")
					.append(e.getMessage()).toString());
		}

		return itemNativeValue;
	}

	public List<ItemIDInfo<?>> getUnmodifiableDBCPPartItemIDInfoList() {		
		return Collections.unmodifiableList(dbcpPartItemIDInfoList);
	}

	public List<ItemIDInfo<?>> getUnmodifiableCommonPartItemIDInfoList() {		
		return Collections.unmodifiableList(commonPartItemIDInfoList);
	}

	public List<ItemIDInfo<?>> getUnmodifiableProjectPartItemIDInfoList() {		
		return Collections.unmodifiableList(projectPartItemIDInfoList);
	}
			
}
