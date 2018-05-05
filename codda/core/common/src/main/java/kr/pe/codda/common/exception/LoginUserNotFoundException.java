package kr.pe.codda.common.exception;

@SuppressWarnings("serial")
public class LoginUserNotFoundException extends Exception {

	public LoginUserNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
