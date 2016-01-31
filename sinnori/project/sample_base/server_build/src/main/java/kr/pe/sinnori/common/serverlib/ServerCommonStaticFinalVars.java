package kr.pe.sinnori.common.serverlib;

public class ServerCommonStaticFinalVars {
	/** 업로드 되는 파일의 최대 갯수, unsinged byte 이내의 값이어야 한다. */
	public static int WEBSITE_FILEUPLOAD_MAX_COUNT = 2;
	
	/** 서버 테이블 존재시 테이블 전체 삭제여부, 미 지정시 false,  */	
	public static final String JAVA_SYSTEM_PROPERTIES_KEY_IS_DROP_ALL_TABLE = "is.dropAllTable";

}
