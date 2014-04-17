package kr.pe.sinnori.common.exception;

@SuppressWarnings("serial")
public class NotLoginException extends Exception {
	/**
	 * 생성자
	 * @param errorMessage 에러 내용
	 */
	public NotLoginException(String errorMessage) {
		super(errorMessage);
	}
}
