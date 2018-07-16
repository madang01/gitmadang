package kr.pe.codda.common.exception;

public class ServerServiceException extends Exception {

	private static final long serialVersionUID = -8662862394949552738L;

	public ServerServiceException(String errorMessage) {
		super(errorMessage);
	}

}