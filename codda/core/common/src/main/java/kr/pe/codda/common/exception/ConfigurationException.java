package kr.pe.codda.common.exception;


@SuppressWarnings("serial")
public class ConfigurationException extends Exception {
	public ConfigurationException(String errorMessage) {
		super(errorMessage);
	}
}
