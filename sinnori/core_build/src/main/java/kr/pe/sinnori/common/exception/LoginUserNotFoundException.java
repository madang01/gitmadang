package kr.pe.sinnori.common.exception;

@SuppressWarnings("serial")
public class LoginUserNotFoundException extends Exception {

	public LoginUserNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
