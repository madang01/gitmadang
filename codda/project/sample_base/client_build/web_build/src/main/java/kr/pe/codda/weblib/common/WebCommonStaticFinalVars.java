package kr.pe.codda.weblib.common;

public abstract class WebCommonStaticFinalVars {
	public static final String USER_WEBSITE_TITLE = "Codda Home";
	public static final String ADMIN_WEBSITE_TITLE = "Codda Admin";
	
	public static final String BOARD_HASH_ALGORITHM = "SHA-256";
	
	
	//public static final String PARAMETER_KEY_NAME_OF_REQUEST_TYPE = "requestType";
	public static final String PARAMETER_KEY_NAME_OF_SESSION_KEY = "sessionkeyBase64";
	public static final String PARAMETER_KEY_NAME_OF_SESSION_KEY_IV = "ivBase64";	
	
	public static final String SERVLET_INIT_PARM_KEY_NAME_OF_MENU_GROUP_URL = "menuGroupURL";
	
	public static final String REQUEST_KEY_NAME_OF_MENU_GROUP_URL = "menuGroupURL";
	public static final String REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY = "webServerSymmetricKey";
	public static final String REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING = "modulusHexString";
		
	
	public static final String HTTPSESSION_KEY_NAME_OF_LOGINED_USER_INFORMATION = "userLoginRes";
	public static final String HTTPSESSION_KEY_NAME_OF_CLIENT_SESSIONKEY = "clientSessionKey";
	
	//public static final String HTTPSESSION_KEY_NAME_OF_LOGIN_REQUEST_PAGE_INFORMATION = "loginRequestPageInformation";
	
	public static final String SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY = "kr.pe.codda.privatekey";
	
	/**
	 * <pre>
	 * AES 의 경우 키와 소금값(=iv)의 크기로 16 이 무난하다.
	 * - OS별로 다른 경우 -
	 * (1) 윈도7 32 bit 자바7 1.7.0_71 버전의 경우 키크기 16까지 확인, 우분투에서는 키크기 24까지 확인
	 * </pre>
	 */
	public static final int WEBSITE_PRIVATEKEY_SIZE = 16;
	public static final int WEBSITE_IV_SIZE = 16;
	/**
	 * WEBSITE_SYMMETRIC_KEY_ALGORITHM_NAME 는 로그인/회원가입시 사용하는 대칭키 알고리즘 이름이다.
	 * 2014.10.10 일 기준 3가지 대칭키 알고리즘 이름은 자바의 경우 (1) AES, (2) DES, (3) DESede 가 있다.
	 * 자바 스크립트 암호화 모듈의 경우에는 (1) AES, (2) DES (3) TripleDES 가 있다.
	 * 주) 자바단과 자바스크립트단은 동일 알고리즘을 가져야 한다.
	 */
	public static final String WEBSITE_JAVA_SYMMETRIC_KEY_ALGORITHM_NAME = "AES";
	public static final String WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME = "AES";
	
	/** 게시판 페이지당 목록 갯수, WARNING! unsigned short type 으로 최대값이 65535 이다 */
	public static final int WEBSITE_BOARD_LIST_SIZE_PER_PAGE = 20;
	
	/** 게시판 쪽수 목록의 크기 */
	public static final int WEBSITE_BOARD_PAGE_LIST_SIZE = 5;
	
	
	
	
	
	/** 아파치 파일 업로드에서 메모리에서 직접 처리할 수 있는 최대 파일 크기, 단위 byte */
	public static final int APACHE_FILEUPLOAD_MAX_MEMORY_SIZE = 1024*1024;
	
	/** 업로드 파일의 최대 크기 */
	public static final long ATTACHED_FILE_MAX_SIZE = 10*1024*1024;
	/** 업로드 파일의 최대 총 합 크기 */
	public static final long TOTAL_ATTACHED_FILE_MAX_SIZE = 20*1024*1024;
	
	
	
	
	/** 업로드 되는 파일의 시스템 파일명의 접두어 */
	public static final String WEBSITE_ATTACHED_FILE_PREFIX = "AttachedFile";
	
	/** 업로드 되는 파일의 시스템 파일명의 접미어 */
	public static final String WEBSITE_ATTACHED_FILE_SUFFIX = ".dat";
	
	/** 업로드 되는 파일의 최대 갯수, unsinged byte 이내의 값이어야 한다. */
	public static final int WEBSITE_ATTACHED_FILE_MAX_COUNT = 2;
	
	
	public static final short UPLOAD_FILENAME_SEQ_TYPE_ID = 1;
	
	public static final char[] FILENAME_FORBIDDEN_CHARS = {'&', '/', '\\', '?',  '%', '*', ':', '|', '\"', '<', '>', '\''};
	
	
	public static final String USER_WEBSITE_MENU_INFO_FILE = "userWebsiteMenuInfo.txt";
	public static final int USER_WEBSITE_MENU_INFO_FILE_MAX_SIZE= 1*1024*1024;
	
	
	/** 사용자 아이디 최소 문자수 */
	public static final int MIN_NUMBER_OF_USER_ID_CHARRACTERS = 4;
	/** 사용자 아이디 최대 문자수 */
	public static final int MAX_NUMBER_OF_USER_ID_CHARRACTERS = 15;
	
	/** 패스워드 최소 문자수 */
	public static final int MIN_NUMBER_OF_PASSWRORD_CHARRACTERS = 8;
	/** 패스워드 최대 문자수 */
	public static final int MAX_NUMBER_OF_PASSWRORD_CHARRACTERS = 15;
	
	/** 별명 최소 문자수 */
	public static final int MIN_NUMBER_OF_NICKNAME_CHARRACTERS = 2;
	/** 별명 최대 문자수 */
	public static final int MAX_NUMBER_OF_NICKNAME_CHARRACTERS = 20;
	
	/** 비밀번호 힌트 최소 문자수 혹은 비밀번호 답변 최소 문자수, Warning! 유효성 검사할때 최소 글자수를 2로 강제하므로 2 미만 변경은 동작하지 않음 */
	public static final int MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS = 2;
	/** 비밀번호 힌트 최대 문자수 혹은 비밀번호 답변 최대 문자수 */
	public static final int MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS = 30;
	
	public static final int MIN_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS = 2;
	public static final int MAX_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS = 30;
	
	/** 게시판 주제 최소 문자수 */
	public static final int MIN_NUMBER_OF_SUBJECT_CHARRACTERS = 2;
	
	/** 게시판 내용 최소 문자수 */
	public static final int MIN_NUMBER_OF_CONTENTS_CHARRACTERS = 2;
	
	/** 게시판 이름 최소 문자수 */
	public static final int MIN_NUMBER_OF_BOARDNAME_CHARRACTERS = 2;
	/** 게시판 이름 최대 문자수 */
	public static final int MAX_NUMBER_OF_BOARDNAME_CHARRACTERS = 10;
	
	public static final AccessedUserInformation GUEST_USER_SESSION_INFORMATION = new AccessedUserInformation(false, "guest", "손님", MemberRoleType.GUEST);
}
