package kr.pe.codda.weblib.common;

public class LoginedUserInformation {
	private String loginedUserID = null;
	private MemberRoleType memberRoleType = null;
	
	public LoginedUserInformation(String loginedUserID, MemberRoleType memberRoleType) {
		this.loginedUserID = loginedUserID;
		this.memberRoleType = memberRoleType;
	}

	public String getLoginedUserID() {
		return loginedUserID;
	}
	
	public MemberRoleType getMemberRoleType() {
		return memberRoleType;
	}
}
