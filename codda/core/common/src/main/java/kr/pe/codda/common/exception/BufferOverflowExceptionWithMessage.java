package kr.pe.codda.common.exception;

@SuppressWarnings("serial")
public class BufferOverflowExceptionWithMessage extends Exception {
	/**
	 * 생성자
	 * 
	 * @param errorMessage
	 *            에러 내용
	 */
	public BufferOverflowExceptionWithMessage(String errorMessage) {
		super(errorMessage);
	}
}
