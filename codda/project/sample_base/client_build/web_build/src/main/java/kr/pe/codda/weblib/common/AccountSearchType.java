package kr.pe.codda.weblib.common;

public enum AccountSearchType {
	ID((byte)0, "아이디"), PASSWORD((byte)1, "비빌번호");
	
	
	private byte accountSearchTypeValue;
	private String accountSearchTypeName;
	
	private AccountSearchType(byte accountSearchTypeValue, String accountSearchTypeName) {
		this.accountSearchTypeValue = accountSearchTypeValue;
		this.accountSearchTypeName = accountSearchTypeName;
	}

	public byte getValue() {
		return accountSearchTypeValue;
	}	
	
	public String getName() {
		return accountSearchTypeName;
	}
	
	public static AccountSearchType valueOf(byte accountSearchTypeValue) {
		AccountSearchType[] accountSearchTypes = AccountSearchType.values();
		for (AccountSearchType accountSearchType : accountSearchTypes) {
			if (accountSearchType.getValue() == accountSearchTypeValue) {
				return accountSearchType;
			}
		}	
		
		throw new IllegalArgumentException("the parameter accountSearchTypeValue["+accountSearchTypeValue+"] is not a element of PasswordSearchType set");
	}
}
