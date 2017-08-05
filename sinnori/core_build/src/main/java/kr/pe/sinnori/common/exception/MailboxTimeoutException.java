package kr.pe.sinnori.common.exception;

@SuppressWarnings("serial")
public class MailboxTimeoutException extends ConnectionTimeoutException {

	public MailboxTimeoutException(String errorMessage) {
		super(errorMessage);
	}
}
