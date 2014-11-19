package kr.pe.sinnori.common.weblib;

import java.io.File;

public abstract class WebCommonStaticFinalVars {
	public static final String WEBSITE_TITLE = "Sinnori Development Framework";
	
	public static final String HTTPSESSION_USERID_NAME = "userID";
	// public static final String HTTPSESSION_USERGUBUN_NAME = "userGubun";
	public static final String SESSIONSTORAGE_SESSIONKEY_NAME = "kr.pe.sinnori.sessionkey";
	public static final String SESSIONSTORAGE_PRIVATEKEY_NAME = "kr.pe.sinnori.privatekey";
	
	public static final int WEBSITE_PRIVATEKEY_SIZE = 24;
	public static final int WEBSITE_IV_SIZE = 16;
	/**
	 * WEBSITE_SYMMETRIC_KEY_ALGORITHM_NAME 는 로그인/회원가입시 사용하는 대칭키 알고리즘 이름이다.
	 * 2014.10.10 일 기준 3가지 대칭키 알고리즘 이름은 자바의 경우 (1) AES, (2) DES, (3) DESede 가 있다.
	 * 자바 스크립트 암호화 모듈의 경우에는 (1) AES, (2) DES (3) TripleDES 가 있다.
	 * 주) 자바단과 자바스크립트단은 동일 알고리즘을 가져야 한다.
	 */
	public static final String WEBSITE_JAVA_SYMMETRIC_KEY_ALGORITHM_NAME = "AES";
	public static final String WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME = "AES";
	
	// public static final Long FREE_BOARD_TYPE_ID = 2L;
	// public static final Long NOTICE_BOARD_TYPE_ID = 1L;
	
	/** 게시판 목록 갯수 */
	public static final int WEBSITE_BOARD_PAGESIZE = 20;
	
	/** 아파치 파일 업로드에서 메모리에서 직접 처리할 수 있는 최대 파일 크기, 단위 byte */
	public static int APACHE_FILEUPLOAD_MAX_MEMORY_SIZE = 1024*1024;

	/** 업로드 되는 파일들이 저장되는 임시 디렉토리 */
	public static File WEBSITE_FILEUPLOAD_TEMP_DIR = new File("/home/madang01/gitsinnori/sinnori_framework/project/sample_test/web_app_base/temp");
	
	/** 업로드 파일의 최대 크기 */
	public static long WEBSITE_FILEUPLOAD_MAX_SIZE = 10*1024*1024;
	
	/** 업로드 되는 파일들이 저장되는 디렉토리 */
	public static File WEBSITE_FILEUPLOAD_DIR = new File("/home/madang01/gitsinnori/sinnori_framework/project/sample_test/web_app_base/upload");
	
	/** 업로드 되는 파일의 시스템 파일명의 접두어 */
	public static String WEBSITE_FILEUPLOAD_PREFIX = "attach";
	
	/** 업로드 되는 파일의 시스템 파일명의 접미어 */
	public static String WEBSITE_FILEUPLOAD_SUFFIX = ".dat";
	
	/** 업로드 되는 파일의 최대 갯수, unsinged byte 이내의 값이어야 한다. */
	public static int WEBSITE_FILEUPLOAD_MAX_COUNT = 2;
	
	public static final short UPLOAD_FILENAME_SEQ_TYPE_ID = 1;
	
	public static final Line2BrStringReplacer LINE2BR_STRING_REPLACER = new Line2BrStringReplacer();
}
