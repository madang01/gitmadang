package kr.pe.sinnori.server.lib;

@SuppressWarnings("serial")
public class RollbackException extends Exception {
	public RollbackException(String errorMessage) {
		super(errorMessage);
	}
}
