package kr.pe.codda.weblib.common;

public class AccessedUserInformation {
	private boolean isLogined = false;
	private String userID = null;
	private String userName = null;
	private MemberRoleType memberRoleType = null;
	
	/**
	 * 접속한 사용자 정보, '로그인 여부'가 거짓인 경우 '사용자 아이디', '사용자 이름', '회원 역활' 는 강제적으로 손님에 맞추어 진다 
	 * 
	 * @param isLogined 로그인 여부
	 * @param userID 사용자 아이디
	 * @param userName 사용자 이름
	 * @param memberRoleType 회원 역활
	 */
	public AccessedUserInformation(boolean isLogined, String userID, String userName, MemberRoleType memberRoleType) {
		this.isLogined = isLogined;		

		if (isLogined) {
			this.userID = userID;
			this.userName = userName;
			this.memberRoleType = memberRoleType;
		} else {
			this.userID = "guest";
			this.userName = "손님";
			this.memberRoleType = MemberRoleType.GUEST;
		}
	}
	
	public boolean isAdmin() {
		return  MemberRoleType.ADMIN.equals(memberRoleType);
	}
	
	public boolean isGuest() {
		return  MemberRoleType.GUEST.equals(memberRoleType);
	}
	
	public boolean isLoginedIn() {
		return isLogined;
	}

	public String getUserID() {
		return userID;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public MemberRoleType getMemberRoleType() {
		return memberRoleType;
	}
}
