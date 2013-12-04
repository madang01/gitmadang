package kr.pe.sinnori.common.exception;

/**
 * 원하는 출력 메시지를 얻지 못했을때 던지는 예외
 * 
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class NoMatchOutputMessage extends Exception {

	/**
	 * 생성자
	 * @param errorMessage 에러 내용
	 */
	public NoMatchOutputMessage(String errorMessage) {
		super(errorMessage);
	}
}
