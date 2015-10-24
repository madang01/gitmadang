package kr.pe.sinnori.common.config.itemidinfo;

import java.io.File;

import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningIntegerBetweenMinAndMax;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningNoTrimString;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningPath;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterOfSessionKeyRSAKeypairSource;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningBoolean;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningInteger;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningSessionkeyPrivateKeyEncoding;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningString;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CommonPartItemIDInfoAdder {
	public static void addAllCommonPartItemIDInfo(
			CommonPartItemIDInfoMangerIF commonPartItemIDInfoManger)
			throws IllegalArgumentException, SinnoriConfigurationException {
		Logger log = LoggerFactory.getLogger(CommonPartItemIDInfoAdder.class);

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
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SERVLET_JSP_JDF_LOGIN_PAGE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.TEXT, itemID, "로그인 처리 jsp",
					"/login.jsp", isDefaultValueCheck,
					new GeneralConverterReturningNoTrimString());
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SERVLET_JSP_JDF_SERVLET_TRACE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Boolean>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"JDF framework에서 서블릿 경과시간 추적 여부", "true",
					isDefaultValueCheck,
					new SetTypeConverterReturningBoolean());
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SERVLET_JSP_WEB_LAYOUT_CONTROL_PAGE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.TEXT, itemID,
					"신놀이 웹 사이트의 레이아웃 컨트롤러 jsp", "/PageJump.jsp",
					isDefaultValueCheck,
					new GeneralConverterReturningNoTrimString());
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_SOURCE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET,
					itemID,
					"세션키에 사용되는 공개키 키쌍 생성 방법, API:내부적으로 RSA 키쌍 생성, File:외부 파일를 읽어와서 RSA  키쌍을 생성",
					"API", isDefaultValueCheck,
					new SetTypeConverterOfSessionKeyRSAKeypairSource());
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYPAIR_PATH_ITEMID;
			isDefaultValueCheck = false;
			itemIDInfo = new ItemIDInfo<File>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.PATH,
					itemID,
					"세션키에 사용되는 공개키 키쌍 파일 경로, 세션키에 사용되는 공개키 키쌍 생성 방법이 File인 경우에 유효하다",
					"[sinnnori installed path]/project/[main project name]/rsa_keypair",
					isDefaultValueCheck,
					new GeneralConverterReturningPath());
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_RSA_KEYSIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"세션키에 사용하는 공개키 크기, 단위 byte", "1024",
					isDefaultValueCheck,
					new SetTypeConverterReturningInteger("512", "1024",
							"2048"));
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_SYMMETRIC_KEY_ALGORITHM_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<String>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"세션키에 사용되는 대칭키 알고리즘", "AES", isDefaultValueCheck,
					new SetTypeConverterReturningString("AES", "DESede",
							"DES"));
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_SYMMETRIC_KEY_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"세션키에 사용되는 대칭키 크기", "16", true,
					new SetTypeConverterReturningInteger("8", "16", "24"));
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_IV_SIZE_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET, itemID,
					"세션키에 사용되는 대칭키와 같이 사용되는 IV 크기", "16",
					isDefaultValueCheck,
					new SetTypeConverterReturningInteger("8", "16", "24"));
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);

			itemID = ItemIDDefiner.CommonPartItemIDDefiner.SESSIONKEY_PRIVATE_KEY_ENCODING_ITEMID;
			isDefaultValueCheck = true;
			itemIDInfo = new ItemIDInfo<CommonType.SYMMETRIC_KEY_ENCODING>(
					ItemIDInfo.ConfigurationPart.COMMON,
					ItemIDInfo.ViewType.SINGLE_SET,
					itemID,
					"개인키 인코딩 방법, NONE : 아무 인코딩 없이 이진 데이터 그대로인 개인키 값, BASE64: base64 인코딩한 개인키 값",
					"BASE64",
					isDefaultValueCheck,
					new SetTypeConverterReturningSessionkeyPrivateKeyEncoding());
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);

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
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);

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
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);

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
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);
			
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
			commonPartItemIDInfoManger.addCommonPartItemIDInfo(itemIDInfo);
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
}
