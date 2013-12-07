package kr.pe.sinnori.common.exception;

@SuppressWarnings("serial")
public class NotLoginException extends Exception {
	/**
	 * 생성자
	 * @param errmsg 에러 내용
	 */
	public NotLoginException(String errmsg) {
		super(errmsg);
	}
}
