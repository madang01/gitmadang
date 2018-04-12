package kr.pe.sinnori.weblib.common;

public abstract class WebCommonStaticFinalVars {
	public static final String WEBSITE_TITLE = "Sinnori Home";
	
	public static final String HTTPSESSION_KEY_USERID_NAME = "userID";
	// public static final String HTTPSESSION_USERGUBUN_NAME = "userGubun";
	public static final String SESSIONSTORAGE_KEY_SESSIONKEY_NAME = "kr.pe.sinnori.sessionkey";
	public static final String SESSIONSTORAGE_KEY_PRIVATEKEY_NAME = "kr.pe.sinnori.privatekey";
	
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
	
	
	
	/** 게시판 목록 갯수 */
	public static final int WEBSITE_BOARD_PAGESIZE = 20;
	
	/** 아파치 파일 업로드에서 메모리에서 직접 처리할 수 있는 최대 파일 크기, 단위 byte */
	public static int APACHE_FILEUPLOAD_MAX_MEMORY_SIZE = 1024*1024;
	
	/** 업로드 파일의 최대 크기 */
	public static long WEBSITE_FILEUPLOAD_MAX_SIZE = 10*1024*1024;
	
	/** 업로드 되는 파일의 시스템 파일명의 접두어 */
	public static String WEBSITE_FILEUPLOAD_PREFIX = "attach";
	
	/** 업로드 되는 파일의 시스템 파일명의 접미어 */
	public static String WEBSITE_FILEUPLOAD_SUFFIX = ".dat";
	
	/** 업로드 되는 파일의 최대 갯수, unsinged byte 이내의 값이어야 한다. */
	public static int WEBSITE_FILEUPLOAD_MAX_COUNT = 2;
	
	
	public static final Long GENERAL_BOARD_TYPE_ID = 2L;
	public static final Long NOTICE_BOARD_TYPE_ID = 1L;
	public static final short UPLOAD_FILENAME_SEQ_TYPE_ID = 1;
}
