package kr.pe.sinnori.common.exception;

@SuppressWarnings("serial")
public class SinnoriBufferOverflowException extends Exception {
	/**
	 * 생성자
	 * 
	 * @param errorMessage
	 *            에러 내용
	 */
	public SinnoriBufferOverflowException(String errorMessage) {
		super(errorMessage);
	}
}
