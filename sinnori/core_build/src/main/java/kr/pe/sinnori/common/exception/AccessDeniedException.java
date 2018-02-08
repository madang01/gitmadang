package kr.pe.sinnori.common.exception;

@SuppressWarnings("serial")
public class AccessDeniedException extends Exception {
	/**
	 * 생성자
	 * @param errorMessage 에러 내용
	 */
	public AccessDeniedException(String errorMessage) {
		super(errorMessage);
	}
}
