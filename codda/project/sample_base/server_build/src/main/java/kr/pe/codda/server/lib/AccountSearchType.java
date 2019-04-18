package kr.pe.codda.server.lib;

public enum AccountSearchType {
	ID((byte)0, "아이디"), PASSWORD((byte)1, "비빌번호");
	
	
	private byte passwordSearchTypeValue;
	private String passwordSearchTypeName;
	
	private AccountSearchType(byte passwordSearchTypeValue, String passwordSearchTypeName) {
		this.passwordSearchTypeValue = passwordSearchTypeValue;
		this.passwordSearchTypeName = passwordSearchTypeName;
	}

	public byte getValue() {
		return passwordSearchTypeValue;
	}	
	
	public String getName() {
		return passwordSearchTypeName;
	}
	
	public static AccountSearchType valueOf(byte passwoardSearchTypeValue) {
		AccountSearchType[] passwoardSearchTypes = AccountSearchType.values();
		for (AccountSearchType passwoardSearchType : passwoardSearchTypes) {
			if (passwoardSearchType.getValue() == passwoardSearchTypeValue) {
				return passwoardSearchType;
			}
		}	
		
		throw new IllegalArgumentException("the parameter passwoardSearchTypeValue["+passwoardSearchTypeValue+"] is not a element of PasswordSearchType set");
	}
}
