package kr.pe.codda.common.exception;

/**
 * 파일 송수신과 관련된 파일 관련 작업시 발생시 던지는 예외
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class UpDownFileException extends Exception {
	/**
	 * 생성자
	 * @param errorMessage 에러 메시지
	 */
	public UpDownFileException(String errorMessage) {
		super(errorMessage);
	}
}
