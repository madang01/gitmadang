package kr.pe.sinnori.common.exception;

@SuppressWarnings("serial")
public class MybatisException extends Exception {
	public MybatisException(String errorMessage) {
		super(errorMessage);
	}
}
