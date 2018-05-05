package kr.pe.codda.common.exception;

@SuppressWarnings("serial")
public class AccessDeniedExceptionWithMessage extends Exception {
	/**
	 * 생성자
	 * @param errorMessage 에러 내용
	 */
	public AccessDeniedExceptionWithMessage(String errorMessage) {
		super(errorMessage);
	}
}
