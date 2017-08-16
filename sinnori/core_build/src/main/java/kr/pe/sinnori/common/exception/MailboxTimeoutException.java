package kr.pe.sinnori.common.exception;

@SuppressWarnings("serial")
public class MailboxTimeoutException extends ConnectionPoolTimeoutException {

	public MailboxTimeoutException(String errorMessage) {
		super(errorMessage);
	}
}
