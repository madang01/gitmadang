package kr.pe.sinnori.common.config.itemidinfo;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningCharset;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningEmptyOrNoTrimString;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningIntegerBetweenMinAndMax;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningLongBetweenMinAndMax;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningNoTrimString;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningPath;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningBoolean;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningByteOrder;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningConnectionType;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningMessageProtocol;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.ConfigErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProjectPartItemIDInfoAdder {
	public static void addAllProjectPartItemIDInfo(
			ProjectPartItemIDInfoMangerIF projectPartItemIDInfoManger)
			throws IllegalArgumentException, ConfigErrorException {
		Logger log = LoggerFactory.getLogger(ProjectPartItemIDInfoAdder.class);
		ItemIDInfo<?> itemIDInfo = null;
		String itemID = null;
		boolean isDefaultValueCheck = false;

		try {
			/** 프로젝트 공통 설정 부분 */
			itemID = ItemID.ProjectPartItemID.COMMON_MESSAGE_INFO_XMLPATH_ITEMID;
			isDefaultValueCheck = false;
			itemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.PATH,
					itemID,
					"메시지 정보 파일 경로",
					"[sinnori installed path]/project/[main project name]/impl/message/info",
					isDefaultValueCheck, new GeneralConverterReturningPath());
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.COMMON_HOST_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID, "클라이언트에서 접속할 서버 주소",
					"localhost", isDefaultValueCheck,
					new GeneralConverterReturningNoTrimString());
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.COMMON_PORT_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.COMMON_BYTEORDER_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<ByteOrder>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"바이트 오더, LITTLE_ENDIAN:리틀 엔디안, BIG_ENDIAN:빅 엔디안",
					"LITTLE_ENDIAN", isDefaultValueCheck,
					new SetTypeConverterReturningByteOrder());
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.COMMON_CHARSET_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Charset>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID, "문자셋", "UTF-8",
					isDefaultValueCheck, new GeneralConverterReturningCharset());
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.COMMON_DATA_PACKET_BUFFER_MAX_CNT_PER_MESSAGE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.COMMON_DATA_PACKET_BUFFER_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			/** 메시지 식별자 크기의 최소 크기는 내부적으로 사용하는 SelfExn 메시지를 기준으로 정했음. */
			itemID = ItemID.ProjectPartItemID.COMMON_MESSAGE_ID_FIXED_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.COMMON_MESSAGE_PROTOCOL_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<CommonType.MESSAGE_PROTOCOL_GUBUN>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"메시지 프로토콜, DHB:교차 md5 헤더+바디, DJSON:길이+존슨문자열, THB:길이+바디",
					"DHB", isDefaultValueCheck,
					new SetTypeConverterReturningMessageProtocol());
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.COMMON_CLASSLOADER_CLASS_PACKAGE_PREFIX_NAME_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID,
					"동적 클래스 패키지명 접두어, 동적 클래스 여부를 판단하는 기준",
					"kr.pe.sinnori.impl.", isDefaultValueCheck,
					new GeneralConverterReturningNoTrimString());
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			/** 프로젝트 클라이언트 설정 부분 */
			itemID = ItemID.ProjectPartItemID.CLIENT_MONITOR_TIME_INTERVAL_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID, "모니터링 주기, 단위 ms", "5000",
					isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1000L, (long) Integer.MAX_VALUE));
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_MONITOR_RECEPTION_TIMEOUT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"데이터를 수신하지 않고 기다려주는 최대 시간, 최소값은 소켓 타임아웃 시간에 종속, 권장 값은 소켓 타임 아웃 시간*2, 단위 ms",
					"20000", isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1000L, (long) Integer.MAX_VALUE));
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_CONNECTION_TYPE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<CommonType.CONNECTION_TYPE>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.SINGLE_SET,
					itemID,
					"소캣 랩퍼 클래스인 연결 종류, NoShareAsyn:비공유+비동기, ShareAsyn:공유+비동기, NoShareSync:비공유+동기",
					"NoShareAsyn", isDefaultValueCheck,
					new SetTypeConverterReturningConnectionType());
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_CONNECTION_SOCKET_TIMEOUT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID, "소켓 타임아웃, 단위 ms", "5000",
					isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1000L, (long) Integer.MAX_VALUE));
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_CONNECTION_WHETHER_AUTO_CONNECTION_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Boolean>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.SINGLE_SET, itemID, "연결 생성시 자동 접속 여부",
					"false", isDefaultValueCheck,
					new SetTypeConverterReturningBoolean());
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_CONNECTION_COUNT_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_DATA_PACKET_BUFFER_CNT_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_ASYN_FINISH_CONNECT_MAX_CALL_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_ASYN_FINISH_CONNECT_WAITTING_TIME_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID,
					"클라이언트 비동기 소켓 채널의 연결 확립을 재 시도 간격", "10",
					isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							0L, (long) Integer.MAX_VALUE));
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_ASYN_OUTPUT_MESSAGE_EXECUTOR_THREAD_CNT_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_ASYN_SHARE_MAILBOX_CNT_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_ASYN_INPUT_MESSAGE_QUEUE_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_ASYN_INPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_ASYN_INPUT_MESSAGE_WRITER_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_ASYN_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_ASYN_OUTPUT_MESSAGE_READER_MAX_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_ASYN_OUTPUT_MESSAGE_READER_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.CLIENT_ASYN_READ_SELECTOR_WAKEUP_INTERVAL_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"클라이언트 비동기 입출력 지원용 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기. 단위 ms",
					"10", isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1L, (long) Integer.MAX_VALUE));
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			/** 프로젝트 서버 설정 부분 */
			itemID = ItemID.ProjectPartItemID.SERVER_MONITOR_TIME_INTERVAL_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID, "모니터링 주기, 단위 ms", "5000",
					isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1000L, (long) Integer.MAX_VALUE));
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_MONITOR_RECEPTION_TIMEOUT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID,
					"데이터를 수신하지 않고 기다려주는 최대 시간, 권장 값은 소켓 타임 아웃 시간*2, 단위 ms",
					"20000", isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1000L, (long) Integer.MAX_VALUE));
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_MAX_CLIENTS_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_DATA_PACKET_BUFFER_CNT_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_POOL_ACCEPT_QUEUE_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_POOL_INPUT_MESSAGE_QUEUE_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_POOL_OUTPUT_MESSAGE_QUEUE_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_ACCEPT_SELECTOR_TIMEOUT_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, itemID,
					"접속 이벤트 전용 selector 에서 접속 이벤트 최대 대기 시간, 단위 ms", "10",
					isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							10L, (long) Integer.MAX_VALUE));
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_POOL_READ_SELECTOR_WAKEUP_INTERVAL_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"입력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기. 단위 ms",
					"10", isDefaultValueCheck,
					new GeneralConverterReturningLongBetweenMinAndMax(
							10L, (long) Integer.MAX_VALUE));
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_POOL_ACCEPT_PROCESSOR_MAX_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_POOL_ACCEPT_PROCESSOR_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_POOL_INPUT_MESSAGE_READER_MAX_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_POOL_INPUT_MESSAGE_READER_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_POOL_EXECUTOR_PROCESSOR_MAX_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_POOL_EXECUTOR_PROCESSOR_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_POOL_OUTPUT_MESSAGE_WRITER_MAX_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_POOL_OUTPUT_MESSAGE_WRITER_SIZE_ITEMID;
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
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_CLASSLOADER_APPINF_PATH_ITEMID;
			isDefaultValueCheck = false;
			itemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.PATH,
					itemID,
					"서버 동적 클래스 APP-INF 경로, 디폴트 값 [sinnori installed path]/project/[main project name]/server_build/APP-INF",
					"[sinnori installed path]/project/[main project name]/server_build/APP-INF",
					isDefaultValueCheck, new GeneralConverterReturningPath());
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

			itemID = ItemID.ProjectPartItemID.SERVER_CLASSLOADER_MYBATIS_CONFIG_FILE_RELATIVE_PATH_ITEMID;
			isDefaultValueCheck = false;
			itemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					itemID,
					"mybatis 설정 파일의 상대 경로, [서버 동적 클래스 APP-INF 경로]/resources 경로 기준으로 읽어오며 구별자가 '/' 문자로된 상대 경로로 기술되어야 한다. ex) kr/pe/sinnori/mybatis/mybatisConfig.xml",
					"", isDefaultValueCheck,
					new GeneralConverterReturningEmptyOrNoTrimString());
			projectPartItemIDInfoManger.addProjectPartItemIDInfo(itemIDInfo);

		} catch (ConfigErrorException | IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"fail to add project part item identification[")
					.append(itemID).append("] information").toString();

			log.info(errorMessage, e);

			throw new ConfigErrorException(new StringBuilder(errorMessage)
					.append(", errrorMessage=").append(e.getMessage())
					.toString());
		}
	}
}
