package kr.pe.codda.server.lib;

public abstract class ServerCommonStaticFinalVars {
	/** 업로드 되는 파일의 최대 갯수, unsinged byte 이내의 값이어야 한다. */
	public static int WEBSITE_FILEUPLOAD_MAX_COUNT = 2;
	
	/** 서버 테이블 존재시 테이블 전체 삭제여부, 미 지정시 false,  */	
	public static final String JAVA_SYSTEM_PROPERTIES_KEY_IS_DROP_ALL_TABLE = "is.dropAllTable";
	
	
	public static final String SB_CONNECTION_POOL_NAME = "sample_base_db";
	
	/** 패스워드 최소 문자수 */
	public static final int MIN_NUMBER_OF_PASSWRORD_CHARRACTERS = 8;
	/** 패스워드 최대 문자수 */
	public static final int MAX_NUMBER_OF_PASSWRORD_CHARRACTERS = 15;
	
	/** 패스워드 최대 실패 횟수 */
	public static final int MAX_COUNT_OF_PASSWORD_FAILURES = 5;
		
	/** 사용자 아이디 최소 문자수 */
	public static final int MIN_NUMBER_OF_USER_ID_CHARRACTERS = 4;
	/** 사용자 아이디 최대 문자수 */
	public static final int MAX_NUMBER_OF_USER_ID_CHARRACTERS = 15;
	
	/** 비밀번호 힌트 최소 문자수 혹은 비밀번호 답변 최소 문자수, Warning! 유효성 검사할때 최소 글자수를 2로 강제하므로 2 미만 변경은 동작하지 않음 */
	public static final int MIN_NUMBER_OF_PASSWORD_HINT_CHARRACTERS = 2;
	/** 비밀번호 힌트 최대 문자수 혹은 비밀번호 답변 최대 문자수 */
	public static final int MAX_NUMBER_OF_PASSWORD_HINT_CHARRACTERS = 30;
	
	
	public static final int MIN_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS = 2;
	public static final int MAX_NUMBER_OF_PASSWORD_ANSWER_CHARRACTERS = 30;
	
	
	/** 별명 최소 문자수 */
	public static final int MIN_NUMBER_OF_NICKNAME_CHARRACTERS = 4;
	/** 별명 최대 문자수 */
	public static final int MAX_NUMBER_OF_NICKNAME_CHARRACTERS = 20;
	
	/** 게시판 주제 최소 문자수 */
	public static final int MIN_NUMBER_OF_SUBJECT_CHARRACTERS = 2;
	
	/** 게시판 내용 최소 문자수 */
	public static final int MIN_NUMBER_OF_CONTENTS_CHARRACTERS = 2;
	
	/** 업로드 파일명 최소 문자수 */
	public static final int MIN_NUMBER_OF_UPLOAD_FILENAME_CHARRACTERS = 1;
	/** 업로드 파일명 최대 문자수 */
	public static final int MAX_NUMBER_OF_UPLOAD_FILENAME_CHARRACTERS = 80;
}
