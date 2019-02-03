package kr.pe.codda.common.exception;

import java.net.SocketTimeoutException;

public class ConnectionPoolTimeoutException extends SocketTimeoutException {

	private static final long serialVersionUID = 2720180667897458907L;
	
	public ConnectionPoolTimeoutException(String errorMessage) {
		super(errorMessage);
	}

}
