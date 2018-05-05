package kr.pe.codda.common.util;

import java.util.Arrays;

public class PasswordWrapper {
	private char[] passwordChars = null;
	
	public PasswordWrapper(char[] passwordChars) {
		if (null == passwordChars) {
			throw new IllegalArgumentException("the parameter passwordChars is null");
		}
		
		if (0 == passwordChars.length) {
			throw new IllegalArgumentException("the parameter passwordChars's length is zero");
		}
		
		for (int i=0; i < passwordChars.length; i++) {
			if (passwordChars[i] > 0xff) {
				String errorMessage = String.format("the parameter passwordChars[%d][%c] that consists of a password character ranging from 0x00 to 0xff is bad", i, passwordChars[i]);
				throw new IllegalArgumentException(errorMessage);
			}
		}	
		
		this.passwordChars = passwordChars;
	}
	
	public byte[] toBytes() {
		byte[] passwordBytes = new byte[passwordChars.length];
		for (int i=0; i < passwordChars.length; i++) {
			passwordBytes[i] = (byte)(passwordChars[i]&0xff);
		}
		return passwordBytes;
	}
	
	public void destory() {
		Arrays.fill(passwordChars, '\u0000');
	}
}
