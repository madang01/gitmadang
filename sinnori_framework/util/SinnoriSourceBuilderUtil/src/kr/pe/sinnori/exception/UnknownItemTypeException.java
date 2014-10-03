package kr.pe.sinnori.exception;

@SuppressWarnings("serial")
public class UnknownItemTypeException extends Exception {

	/**
	 * 생성자
	 * 
	 * @param errorMessage 에러 내용
	 */
	public UnknownItemTypeException(String errorMessage) {
		super(errorMessage);
	}
}
