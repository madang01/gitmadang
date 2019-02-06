package kr.pe.codda.weblib.exception;

public class WebClientException extends Exception {		
	private static final long serialVersionUID = 8262277067679796952L;
	
	private String errorMessage = null;
	private String debugMessage = null;
	
	public WebClientException(String errorMessage, String debugMessage) {
		if (null == errorMessage) {
			throw new IllegalArgumentException("the parameter errorMessage is null");
		}
		
		this.errorMessage = errorMessage;
		this.debugMessage = debugMessage;			
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public String getDebugMessage() {
		return debugMessage;
	}
}
