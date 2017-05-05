package kr.pe.sinnori.common.config.itemidinfo;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.AbstractDependOnInactiveChecker;
import kr.pe.sinnori.common.config.AbstractDependencyValidator;
import kr.pe.sinnori.common.config.dependoninactivechecker.RSAKeyFileDependOnSourceInActiveChecker;
import kr.pe.sinnori.common.config.dependonvalidchecker.MinAndMaxDependencyValidator;
import kr.pe.sinnori.common.config.fileorpathstringgetter.AbstractFileOrPathStringGetter;
import kr.pe.sinnori.common.config.fileorpathstringgetter.DBCPConfigFilePathStringGetter;
import kr.pe.sinnori.common.config.fileorpathstringgetter.SessionkeyRSAPrivatekeyFilePathStringGetter;
import kr.pe.sinnori.common.config.fileorpathstringgetter.SessionkeyRSAPublickeyFilePathStringGetter;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo.ConfigurationPart;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningCharset;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningEmptyOrNoTrimString;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningIntegerBetweenMinAndMax;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningLongBetweenMinAndMax;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningNoTrimString;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningRegularFile;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterOfSessionKeyRSAKeypairSource;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningBoolean;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningByteOrder;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningConnectionType;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningInteger;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningMessageProtocol;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningString;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.util.SequencedProperties;

/**
 * 신놀이 환경 설정 정보 클래스. 언어 종속적인 타입으로 변환할 정보, 특정 항목의 값에 영향을 받는 의존 관계 정보, 특정 항목의 특정
 * 값들에 의해서 비활성화 되는 정보를 구축한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class SinnoriItemIDInfoManger {
	private Logger log = LoggerFactory.getLogger(SinnoriItemIDInfoManger.class);
	

	private List<ItemIDInfo<?>> itemIDInfoList = new ArrayList<ItemIDInfo<?>>();

	private Map<String, ItemIDInfo<?>> itemIDInfoHash = new HashMap<String, ItemIDInfo<?>>();

	private Map<String, AbstractDependOnInactiveChecker> inactiveCheckerHash = new HashMap<String, AbstractDependOnInactiveChecker>();
	private Map<String, AbstractDependencyValidator> dependencyValidationHash = new HashMap<String, AbstractDependencyValidator>();
	private Map<String, AbstractFileOrPathStringGetter> fileOrPathStringGetterHash =
			new HashMap<String, AbstractFileOrPathStringGetter>();
	
	
	private List<ItemIDInfo<?>> dbcpPartItemIDInfoList = new ArrayList<ItemIDInfo<?>>();
	private List<ItemIDInfo<?>> commonPartItemIDInfoList = new ArrayList<ItemIDInfo<?>>();
	private List<ItemIDInfo<?>> projectPartItemIDInfoList = new ArrayList<ItemIDInfo<?>>();

	/** 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스 */
	private static final class SinnoriConfigItemIDInfoMangerHolder {
		static final SinnoriItemIDInfoManger singleton = new SinnoriItemIDInfoManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static SinnoriItemIDInfoManger getInstance() {
		return SinnoriConfigItemIDInfoMangerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자
	 */
	private SinnoriItemIDInfoManger() {
		try {
			addAllDBCPPartItemIDInfo();
		} catch (IllegalArgumentException | SinnoriConfigurationException e) {
			log.error(
					"fail to add all of dbcp part item identification informtion",
					e);
			System.exit(1);
		}
		try {
			addAllCommonPartItemIDInfo();
		} catch (IllegalArgumentException | SinnoriConfigurationException e) {
			log.error(
					"fail to add all of common part item identification informtion",
					e);
			System.exit(1);
		}
		try {
			addAllProjectPartItemIDInfo();
		} catch (IllegalArgumentException | SinnoriConfigurationException e) {
			log.error(
					"fail to add all of project part item identification informtion",
					e);
			System.exit(1);
		}

		try {
			addDependencyValidation();
		} catch (IllegalArgumentException | SinnoriConfigurationException e) {
			log.error("fail to add valid checker", e);
			System.exit(1);
		}
		try {
			addInactiveChecker();
		} catch (IllegalArgumentException | SinnoriConfigurationException e) {
			log.error("fail to add inactive checker", e);
			System.exit(1);
		}
		
		try {
			addFileOrPathStringGetter();			
		} catch (IllegalArgumentException | SinnoriConfigurationException e) {
			log.error("fail to add inactive checker", e);
			System.exit(1);
		}
		
		itemIDInfoList = Collections
				.unmodifiableList(itemIDInfoList);		
		itemIDInfoHash = Collections.unmodifiableMap(itemIDInfoHash);
		
		inactiveCheckerHash = Collections.unmodifiableMap(inactiveCheckerHash);
		dependencyValidationHash = Collections.unmodifiableMap(dependencyValidationHash);
		
		dbcpPartItemIDInfoList = Collections
				.unmodifiableList(dbcpPartItemIDInfoList);
		commonPartItemIDInfoList = Collections
				.unmodifiableList(commonPartItemIDInfoList);
		projectPartItemIDInfoList = Collections
				.unmodifiableList(projectPartItemIDInfoList);
	}
	
	private void addAllCommonPartItemIDInfo()
			throws IllegalArgumentException, SinnoriConfigurationException {
		

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
			itemIDInfo = new ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET,
					itemID,
					"세션키에 사용되는 공개키 키쌍 생성 방법, API:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성",
					CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.SERVER.toString(), isDefaultValueCheck,
					new SetTypeConverterOfSessionKeyRSAKeypairSource());
			addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID;
			isDefaultValueCheck = false;
			itemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.FILE,
					itemID,
					"세션키에 사용되는 RSA 공개키 파일",
					"[sinnnori installed path]/project/[main project name]/resouces/rsa_keypair/sinnori.publickey",
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
					"[sinnnori installed path]/project/[main project name]/resouces/rsa_keypair/sinnori.privatekey",
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
		} catch (SinnoriConfigurationException | IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"fail to add common part item identification[")
					.append(itemID).append("] information").toString();

			log.info(errorMessage, e);

			throw new SinnoriConfigurationException(new StringBuilder(errorMessage)
			.append(", errrorMessage=").append(e.getMessage()).toString());
		}
		/** Common end */
	}
	
	private void addAllDBCPPartItemIDInfo()
			throws IllegalArgumentException, SinnoriConfigurationException {		
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
						"[sinnori installed path]/project/[main project name]/config/[dbcp name].properties",
						isDefaultValueCheck,
						new GeneralConverterReturningRegularFile(
								isWritePermissionChecking));
			}

			addDBCPPartItemIDInfo(itemIDInfo);
		} catch (SinnoriConfigurationException | IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"fail to add dbcp part item identification[")
					.append(itemID).append("] information").toString();

			log.info(errorMessage, e);

			throw new SinnoriConfigurationException(new StringBuilder(errorMessage)
			.append(", errrorMessage=").append(e.getMessage()).toString());
		}

		/** DBCP end */
	}
	
	public void addAllProjectPartItemIDInfo()
			throws IllegalArgumentException, SinnoriConfigurationException {
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

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID;
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

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_DATA_PACKET_BUFFER_SIZE_ITEMID;
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

			/** 메시지 식별자 크기의 최소 크기는 내부적으로 사용하는 SelfExn 메시지를 기준으로 정했음. */
			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_ID_FIXED_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"메시지 식별자 최소 크기",
					"50",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							7, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_MESSAGE_PROTOCOL_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<CommonType.MESSAGE_PROTOCOL_GUBUN>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"메시지 프로토콜, DHB:교차 md5 헤더+바디, DJSON:길이+존슨문자열, THB:길이+바디",
					"DHB", isDefaultValueCheck,
					new SetTypeConverterReturningMessageProtocol());
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.COMMON_CLASSLOADER_CLASS_PACKAGE_PREFIX_NAME_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID,
					"동적 클래스 패키지명 접두어, 동적 클래스 여부를 판단하는 기준",
					"kr.pe.sinnori.impl.", isDefaultValueCheck,
					new GeneralConverterReturningNoTrimString());
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

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_MONITOR_RECEPTION_TIMEOUT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"데이터를 수신하지 않고 기다려주는 최대 시간, 최소값은 소켓 타임아웃 시간에 종속, 권장 값은 소켓 타임 아웃 시간*2, 단위 ms",
					"20000", isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1000L, (long) Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_TYPE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<CommonType.CONNECTION_TYPE>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.SINGLE_SET,
					itemID,
					"소캣 랩퍼 클래스인 연결 종류, NoShareAsyn:비공유+비동기, ShareAsyn:공유+비동기, NoShareSync:비공유+동기",
					"NoShareAsyn", isDefaultValueCheck,
					new SetTypeConverterReturningConnectionType());
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_SOCKET_TIMEOUT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID, "소켓 타임아웃, 단위 ms", "5000",
					isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1000L, (long) Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_WHETHER_AUTO_CONNECTION_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Boolean>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.SINGLE_SET, itemID, "연결 생성시 자동 접속 여부",
					"false", isDefaultValueCheck,
					new SetTypeConverterReturningBoolean());
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_COUNT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"연결 갯수",
					"4",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_DATA_PACKET_BUFFER_CNT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"클라이언트 프로젝트가 가지는 데이터 패킷 버퍼 갯수",
					"1000",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_FINISH_CONNECT_MAX_CALL_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"클라이언트 비동기 소켓 채널의 연결 확립 최대 시도 횟수",
					"10",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_FINISH_CONNECT_WAITTING_TIME_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID,
					"클라이언트 비동기 소켓 채널의 연결 확립을 재 시도 간격", "10",
					isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							0L, (long) Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_EXECUTOR_THREAD_CNT_ITEMID;
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

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_SHARE_MAILBOX_CNT_ITEMID;
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

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"클라이언트 비동기 입출력 지원용 입력 메시지 소켓 쓰기 담당 쓰레드 최대 갯수",
					"2",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_WRITER_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"클라이언트 비동기 입출력 지원용 입력 메시지 소켓 쓰기 담당 쓰레드 갯수",
					"2",
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
					"출력 메시지 큐 크기",
					"10",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_READER_MAX_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"클라이언트 비동기 입출력 지원용 입력 메시지 소켓 읽기 담당 쓰레드 최대 갯수",
					"4",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_READER_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"클라이언트 비동기 입출력 지원용 입력 메시지 소켓 읽기 담당 쓰레드 갯수",
					"4",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_READ_SELECTOR_WAKEUP_INTERVAL_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"클라이언트 비동기 입출력 지원용 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기. 단위 ms",
					"10", isDefaultValueCheck,
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

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MONITOR_RECEPTION_TIMEOUT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID,
					"데이터를 수신하지 않고 기다려주는 최대 시간, 권장 값은 소켓 타임 아웃 시간*2, 단위 ms",
					"20000", isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1000L, (long) Integer.MAX_VALUE));
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

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_DATA_PACKET_BUFFER_CNT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"서버 프로젝트가 가지는 데이터 패킷 버퍼 수",
					"1000",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_QUEUE_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"접속 승인 큐 크기",
					"10",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							10, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_QUEUE_SIZE_ITEMID;
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

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID;
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

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_ACCEPT_SELECTOR_TIMEOUT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID,
					"접속 이벤트 전용 selector 에서 접속 이벤트 최대 대기 시간, 단위 ms", "10",
					isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							10L, (long) Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_READ_SELECTOR_WAKEUP_INTERVAL_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"입력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기. 단위 ms",
					"10", isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							10L, (long) Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_PROCESSOR_MAX_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"접속 요청이 승락된 클라이언트의 등록을 담당하는 쓰레드 최대 갯수",
					"1",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_PROCESSOR_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"접속 요청이 승락된 클라이언트의 등록을 담당하는 쓰레드 갯수",
					"1",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_READER_MAX_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"입력 메시지 소켓 읽기 담당 쓰레드 최대 갯수",
					"1",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_READER_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"입력 메시지 소켓 읽기 담당 쓰레드 갯수",
					"1",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_PROCESSOR_MAX_SIZE_ITEMID;
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

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_PROCESSOR_SIZE_ITEMID;
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

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"출력 메시지 소켓 쓰기 담당 쓰레드 최대 갯수",
					"1",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"출력 메시지 소켓 쓰기 담당 쓰레드 갯수",
					"1",
					isDefaultValueCheck,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));
			addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_CLASSLOADER_MYBATIS_CONFIG_FILE_RELATIVE_PATH_STRING_ITEMID;
			isDefaultValueCheck = false;
			itemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"mybatis 설정 파일의 상대 경로, [서버 동적 클래스 APP-INF 경로]/resources 경로 기준으로 읽어오며 구별자가 '/' 문자로된 상대 경로로 기술되어야 한다. ex) kr/pe/sinnori/mybatis/mybatisConfig.xml",
					"", isDefaultValueCheck,
					new GeneralConverterReturningEmptyOrNoTrimString());
			addProjectPartItemIDInfo(itemIDInfo);

		} catch (SinnoriConfigurationException | IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"fail to add project part item identification[")
					.append(itemID).append("] information").toString();

			log.info(errorMessage, e);

			throw new SinnoriConfigurationException(new StringBuilder(errorMessage)
					.append(", errrorMessage=").append(e.getMessage())
					.toString());
		}
	}
	
	public ItemIDInfo<?> getItemIDInfo(String itemID) {
		return itemIDInfoHash.get(itemID);
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
					.append(itemIDInfo.getItemID()).append("] is registed")
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
			SinnoriConfigurationException {
		{
			String dependentTargetItemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_MONITOR_RECEPTION_TIMEOUT_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			String dependentSourceItemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_CONNECTION_SOCKET_TIMEOUT_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			
			dependencyValidationHash.put(dependentSourceItemID,
					new MinAndMaxDependencyValidator<Long>(
							(ItemIDInfo<Long>) dependentSourceitemIDConfigInfo,
							(ItemIDInfo<Long>) dependentTargetItemIDInfo,
							
							Long.class));
		}
		{
			String dependentTargetItemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_READER_MAX_SIZE_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			String dependentSourceItemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_OUTPUT_MESSAGE_READER_SIZE_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			dependencyValidationHash
					.put(dependentSourceItemID,
							new MinAndMaxDependencyValidator<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}
		{
			String dependentTargetItemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			String dependentSourceItemID = ItemIDDefiner.ProjectPartItemIDDefiner.CLIENT_ASYN_INPUT_MESSAGE_WRITER_SIZE_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			dependencyValidationHash
					.put(dependentSourceItemID,
							new MinAndMaxDependencyValidator<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}

		{
			String dependentTargetItemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_PROCESSOR_MAX_SIZE_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			String dependentSourceItemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_ACCEPT_PROCESSOR_SIZE_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			dependencyValidationHash
					.put(dependentSourceItemID,
							new MinAndMaxDependencyValidator<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}
		{
			String dependentTargetItemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_READER_MAX_SIZE_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			String dependentSourceItemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_INPUT_MESSAGE_READER_SIZE_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			dependencyValidationHash
					.put(dependentSourceItemID,
							new MinAndMaxDependencyValidator<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}
		{
			String dependentTargetItemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_PROCESSOR_MAX_SIZE_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			String dependentSourceItemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_EXECUTOR_PROCESSOR_SIZE_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			dependencyValidationHash
					.put(dependentSourceItemID,
							new MinAndMaxDependencyValidator<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}
		{
			String dependentTargetItemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID;
			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"dependentTargetItemID[").append(dependentTargetItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			String dependentSourceItemID = ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_POOL_OUTPUT_MESSAGE_WRITER_SIZE_ITEMID;
			ItemIDInfo<?> dependentSourceitemIDConfigInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentSourceitemIDConfigInfo) {
				String errorMessage = new StringBuilder(
						"dependentSourceItemID[").append(dependentSourceItemID)
						.append("]'s itemIDConfigInfo not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			dependencyValidationHash
					.put(dependentSourceItemID,
							new MinAndMaxDependencyValidator<Integer>(
									(ItemIDInfo<Integer>) dependentSourceitemIDConfigInfo,
									(ItemIDInfo<Integer>) dependentTargetItemIDInfo,
									Integer.class));
		}
	}

	private void addInactiveChecker() throws IllegalArgumentException,
			SinnoriConfigurationException {
		{
			String dependentSourceItemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID;
			String dependentTargetItemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID;

			ItemIDInfo<?> dependentSourceItemIDInfo = getItemIDInfo(dependentSourceItemID);
			if (null == dependentSourceItemIDInfo) {
				String errorMessage = new StringBuilder(
						"the dependent source item identification[")
						.append(dependentSourceItemID)
						.append("] information is not ready").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}

			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"the dependent target item identification[")
						.append(dependentTargetItemID)
						.append("] information is not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			inactiveCheckerHash
					.put(dependentSourceItemID,
							new RSAKeyFileDependOnSourceInActiveChecker(
									dependentSourceItemIDInfo,
									dependentTargetItemIDInfo,
									new String[] { CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.SERVER
											.toString() }));
		}
		
		{
			String dependentSourceItemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID;
			String dependentTargetItemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID;

			ItemIDInfo<?> dependentSourceItemIDInfo = getItemIDInfo(dependentSourceItemID);
			if (null == dependentSourceItemIDInfo) {
				String errorMessage = new StringBuilder(
						"the dependent source item identification[")
						.append(dependentSourceItemID)
						.append("] information is not ready").toString();
				throw new SinnoriConfigurationException(errorMessage);
			}

			ItemIDInfo<?> dependentTargetItemIDInfo = getItemIDInfo(dependentTargetItemID);
			if (null == dependentTargetItemIDInfo) {
				String errorMessage = new StringBuilder(
						"the dependent target item identification[")
						.append(dependentTargetItemID)
						.append("] information is not ready").toString();
				// log.error(errorMessage);
				throw new SinnoriConfigurationException(errorMessage);
			}

			inactiveCheckerHash
					.put(dependentSourceItemID,
							new RSAKeyFileDependOnSourceInActiveChecker(
									dependentSourceItemIDInfo,
									dependentTargetItemIDInfo,
									new String[] { CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.SERVER
											.toString() }));
		}

	}

	
	private void addFileOrPathStringGetter() throws IllegalArgumentException,
	SinnoriConfigurationException {
		String itemID = null;
		
	
		itemID = ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID;
		fileOrPathStringGetterHash.put(itemID, new DBCPConfigFilePathStringGetter(itemID));
	
		itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PUBLICKEY_FILE_ITEMID;
		fileOrPathStringGetterHash.put(itemID, new SessionkeyRSAPublickeyFilePathStringGetter(itemID));
		
		itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_PRIVATEKEY_FILE_ITEMID;
		fileOrPathStringGetterHash.put(itemID, new SessionkeyRSAPrivatekeyFilePathStringGetter(itemID));
	}
	
	
	
	/**
	 * 구축한 항목 식별자 정보를 바탕으로 주어진 메인 프로젝트 이름과 설치 경로에 맞도록 신규 생성된 신놀이 설정 시퀀스 프로퍼티를
	 * 반환한다.
	 * 
	 * @param mainProjectName
	 *            메인 프로젝트 이름
	 * @param sinnoriInstalledPathString
	 *            신놀이 설치 경로
	 * @return 구축한 항목 식별자 정보를 바탕으로 주어진 메인 프로젝트 이름과 설치 경로에 맞도록 신규 생성된 신놀이 설정 시퀀스
	 *         프로퍼티
	 */
	public SequencedProperties getNewSinnoriConfigSequencedProperties(String sinnoriInstalledPathString, String mainProjectName) {
		
		SequencedProperties sinnoriConfigSequencedProperties = new SequencedProperties();		

		/** common */
		{
			String prefixOfItemID = "";
			for (ItemIDInfo<?> itemIDConfigInfo : commonPartItemIDInfoList) {
				String itemID = itemIDConfigInfo.getItemID();
				String itemKey = itemID;

				String itemDescKey = itemIDConfigInfo
						.getItemDescKey(prefixOfItemID);

				String itemValue = itemIDConfigInfo.getDefaultValue();
				
				AbstractFileOrPathStringGetter fileOrPathStringGetter = 
						fileOrPathStringGetterHash.get(itemID);
				
				if (null != fileOrPathStringGetter) {
					itemValue = fileOrPathStringGetter
							.getFileOrPathStringDependingOnSinnoriInstalledPath(sinnoriInstalledPathString, mainProjectName);
				}
				
				sinnoriConfigSequencedProperties.put(itemDescKey,
						itemIDConfigInfo.getDescription());
				sinnoriConfigSequencedProperties.put(itemKey,
						itemValue);
			}
		}
		
		/** DBCP */
		{
			sinnoriConfigSequencedProperties.setProperty(
					CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING,
					"");
		}
		

		/** main project */
		{
			String prefixOfItemID = new StringBuilder("mainproject.").toString();
			for (ItemIDInfo<?> itemIDConfigInfo : projectPartItemIDInfoList) {
				
				String itemID = itemIDConfigInfo.getItemID();
				String itemKey = new StringBuilder(prefixOfItemID).append(
						itemID).toString();

				String itemDescKey = itemIDConfigInfo
						.getItemDescKey(prefixOfItemID);
				
				String itemValue = itemIDConfigInfo.getDefaultValue();
				
				AbstractFileOrPathStringGetter fileOrPathStringGetter = 
						fileOrPathStringGetterHash.get(itemID);
				
				if (null != fileOrPathStringGetter) {
					itemValue = fileOrPathStringGetter
							.getFileOrPathStringDependingOnSinnoriInstalledPath(sinnoriInstalledPathString, mainProjectName);
				}
				
				sinnoriConfigSequencedProperties.put(itemDescKey,
						itemIDConfigInfo.getDescription());
				sinnoriConfigSequencedProperties.put(itemKey,
						itemValue);
			}
		}
		
		
		/** sub project */
		{
			sinnoriConfigSequencedProperties.setProperty(
					CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING,
					"");

			
		}

		return sinnoriConfigSequencedProperties;
	}
	
	public boolean isInactive(String itemID, String prefixOfItemID, Properties sourceProperties) {
		boolean isInactive = false;
		AbstractDependOnInactiveChecker inactiveChecker = inactiveCheckerHash
				.get(itemID);

		if (null != inactiveChecker) {
			isInactive = inactiveChecker.isInactive(sourceProperties,
					prefixOfItemID);
		}
		return isInactive;
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

		StringTokenizer stringTokenizer = new StringTokenizer(itemKey, ".");

		if (!stringTokenizer.hasMoreTokens()) {
			String errorMessage = new StringBuilder(
					"first token does not exist in the parameter itemKey[")
					.append(itemKey).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		String firstToken = stringTokenizer.nextToken();

		if (firstToken.equals("mainproject")) {
			if (!stringTokenizer.hasMoreTokens()) {
				String errorMessage = new StringBuilder(
						"second token does not exist in the parameter itemKey[")
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
						"third token does not exist in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			String thirdToken = stringTokenizer.nextToken();
			if (thirdToken.equals("")) {
				String errorMessage = new StringBuilder(
						"third Token is a empty string in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			String prefixOfItemID = firstToken + ".";
			itemID = itemKey.substring(prefixOfItemID.length());

			itemIDInfo = itemIDInfoHash.get(itemID);
			if (null == itemIDInfo) {
				String errorMessage = new StringBuilder(
						"the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not registed, check it")
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			ItemIDInfo.ConfigurationPart configPart = itemIDInfo
					.getConfigurationPart();
			if (!configPart.equals(ItemIDInfo.ConfigurationPart.PROJECT)) {
				String errorMessage = new StringBuilder(
						"the configuration part[")
						.append(configPart.toString())
						.append("] of the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not a project part").toString();
				throw new IllegalArgumentException(errorMessage);
			}			
		} else if (firstToken.equals("subproject")) {
			/** project part */
			if (!stringTokenizer.hasMoreTokens()) {
				String errorMessage = new StringBuilder(
						"second token does not exist in the parameter itemKey[")
						.append(itemKey).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			String projectName = stringTokenizer.nextToken();
			if (projectName.equals("")) {
				String errorMessage = "project name is an empty string at the project part of the parameter sinnoriConfigSequencedProperties";
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

			itemIDInfo = itemIDInfoHash.get(itemID);
			if (null == itemIDInfo) {
				String errorMessage = new StringBuilder(
						"the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not registed, check it")
						.toString();

				/*
				 * log.info(
				 * "item id is not registed, itemKey=[{}], prefixOfItemID=[{}], itemI=[{}]"
				 * , itemKey, prefixOfItemID, itemID);
				 */

				throw new IllegalArgumentException(errorMessage);
			}

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			ItemIDInfo.ConfigurationPart configPart = itemIDInfo
					.getConfigurationPart();
			if (!configPart.equals(ItemIDInfo.ConfigurationPart.PROJECT)) {
				String errorMessage = new StringBuilder(
						"the configuration part[")
						.append(configPart.toString())
						.append("] of the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not a project part").toString();
				throw new IllegalArgumentException(errorMessage);
			}

		} else if (firstToken.equals("dbcp")) {
			/** dbcp part */
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

			itemIDInfo = itemIDInfoHash.get(itemID);
			if (null == itemIDInfo) {
				String errorMessage = new StringBuilder(
						"the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not registed, check it")
						.toString();

				/*
				 * log.info(
				 * "item id is not registed, itemKey=[{}], prefixOfItemID=[{}], itemI=[{}]"
				 * , itemKey, prefixOfItemID, itemID);
				 */

				throw new IllegalArgumentException(errorMessage);
			}

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			ItemIDInfo.ConfigurationPart configPart = itemIDInfo
					.getConfigurationPart();
			if (!configPart.equals(ItemIDInfo.ConfigurationPart.DBCP)) {
				String errorMessage = new StringBuilder(
						"the configuration part[")
						.append(configPart.toString())
						.append("] of the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not a dbcp part").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		} else {
			/** common part */
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

			itemIDInfo = itemIDInfoHash.get(itemID);
			if (null == itemIDInfo) {
				String errorMessage = new StringBuilder(
						"the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not registed, check it")
						.toString();

				/*
				 * log.info(
				 * "item id is not registed, itemKey=[{}], prefixOfItemID=[{}], itemI=[{}]"
				 * , itemKey, "", itemID);
				 */

				throw new IllegalArgumentException(errorMessage);
			}

			/** 파트를 달리한 항목 식별자 사용 방지를 위해서 파트 검사 필요 */
			ItemIDInfo.ConfigurationPart configPart = itemIDInfo
					.getConfigurationPart();
			if (!configPart.equals(ItemIDInfo.ConfigurationPart.COMMON)) {
				String errorMessage = new StringBuilder(
						"the configuration part[")
						.append(configPart.toString())
						.append("] of the parameter itemKey[").append(itemKey)
						.append("]'s itemID is not a common part").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}

		return itemIDInfo;
	}

	public Object getNativeValueAfterValidChecker(String itemKey,
			Properties sourceProperties) throws IllegalArgumentException,
			SinnoriConfigurationException {
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

			throw new SinnoriConfigurationException(errorMessage);
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

					throw new SinnoriConfigurationException(errorMessage);
				}
			} catch (IllegalArgumentException e) {
				String errorMessage = new StringBuilder(
						"the parameter itemValueKey[").append(itemKey)
						.append("]'s invalid check fails errrorMessage=")
						.append(e.getMessage()).toString();
				/** 다른 예외로 변환 되므로 이력 남긴다. */
				log.debug(errorMessage, e);

				throw new SinnoriConfigurationException(errorMessage);
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

			throw new SinnoriConfigurationException(
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
