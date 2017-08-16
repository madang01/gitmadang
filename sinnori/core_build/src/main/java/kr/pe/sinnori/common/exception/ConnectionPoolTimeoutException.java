package kr.pe.sinnori.common.exception;


@SuppressWarnings("serial")
public class ConnectionPoolTimeoutException extends Exception {
	
	public ConnectionPoolTimeoutException(String errorMessage) {
		super(errorMessage);
	}
}
