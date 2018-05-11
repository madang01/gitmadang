package kr.pe.codda.server.lib;

public enum MembershipLevel {
	ADMIN((byte)0, "관리자"), USER((byte)1, "일반회원");
	
	private byte membershipLevelValue;
	private String membershipLevelName;
	
	private MembershipLevel(byte membershipLevelValue, String membershipLevelName) {
		this.membershipLevelValue = membershipLevelValue;
		this.membershipLevelName = membershipLevelName;
	}
	
	public byte getValue() {
		return membershipLevelValue;
	}	
	
	public String getName() {
		return membershipLevelName;
	}
	
	public static MembershipLevel valueOf(byte membershipLevelValue) {
		MembershipLevel[] membershipTypes = MembershipLevel.values();
		for (MembershipLevel membershipType : membershipTypes) {
			if (membershipType.getValue() == membershipLevelValue) {
				return membershipType;
			}
		}	
		
		throw new IllegalArgumentException("the parameter membershipLevelValue["+membershipLevelValue+"] is not a element of MemberLevel set");
	}
}
