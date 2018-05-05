package kr.pe.codda.common.exception;

@SuppressWarnings("serial")
public class ConnectionPoolException extends Exception {
	public ConnectionPoolException(String errorMessage) {
		super(errorMessage);
	}
}
