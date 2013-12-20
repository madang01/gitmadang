package kr.pe.sinnori.common.exception;

@SuppressWarnings("serial")
public class NotSupportedException extends Exception {
	/**
	 * 생성자
	 * @param errmsg 에러 내용
	 */
	public NotSupportedException(String errmsg) {
		super(errmsg);
	}
}
