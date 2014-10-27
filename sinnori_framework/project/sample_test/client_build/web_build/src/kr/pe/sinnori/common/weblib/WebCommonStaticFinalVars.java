package kr.pe.sinnori.common.weblib;

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
	
}
