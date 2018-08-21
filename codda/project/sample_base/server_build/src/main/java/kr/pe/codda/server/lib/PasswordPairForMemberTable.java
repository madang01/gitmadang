package kr.pe.codda.server.lib;

public class PasswordPairForMemberTable {
	public String passwordBase64 = null;
	public String passwordSaltBase64 = null;
	
	public PasswordPairForMemberTable(String passwordBase64, String passwordSaltBase64) {
		this.passwordBase64 = passwordBase64;
		this.passwordSaltBase64 = passwordSaltBase64;
	}
	
	public String getPasswordBase64() {
		return passwordBase64;
	}
	
	public String getPasswordSaltBase64() {
		return passwordSaltBase64;
	}			
}
