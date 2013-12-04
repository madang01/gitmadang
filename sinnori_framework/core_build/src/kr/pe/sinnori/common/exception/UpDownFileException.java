package kr.pe.sinnori.common.exception;

/**
 * 파일 송수신과 관련된 파일 관련 작업시 발생시 던지는 예외
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class UpDownFileException extends Exception {
	public UpDownFileException(String errmsg) {
		super(errmsg);
	}
}
