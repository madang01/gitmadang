package kr.pe.sinnori.common.exception;


@SuppressWarnings("serial")
public class ConnectionTimeoutException extends Exception {
	
	public ConnectionTimeoutException(String errorMessage) {
		super(errorMessage);
	}
}
