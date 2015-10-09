package kr.pe.sinnori.common.exception;

@SuppressWarnings("serial")
public class ConfigKeyNotFoundException extends Exception {
	/**
	 * 생성자
	 * 
	 * @param errorMessage
	 *            에러 내용
	 */
	public ConfigKeyNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
