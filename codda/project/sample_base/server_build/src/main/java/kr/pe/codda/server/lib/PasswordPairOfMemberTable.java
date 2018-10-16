package kr.pe.codda.server.lib;

/**
 * 회원 테이블에 사용되는 비밀번호 관련 항목 2개  (1) 비밀번호와 (2) 비밀번호 소금 값을 저장하는 클래스로
 * 회원 테이블에 사용되는 비밀번호 관련 항목 2개를 한쌍으로 묶어 다루기 위한 편의 클래스이다
 * 
 * @author Won Jonghoon
 *
 */
public class PasswordPairOfMemberTable {
	public String passwordBase64 = null;
	public String passwordSaltBase64 = null;
	
	public PasswordPairOfMemberTable(String passwordBase64, String passwordSaltBase64) {
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
