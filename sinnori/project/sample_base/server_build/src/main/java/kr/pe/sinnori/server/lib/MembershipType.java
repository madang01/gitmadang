package kr.pe.sinnori.server.lib;

public enum MembershipType {
	ADMIN((byte)0, "관리자"), USER((byte)1, "일반회원");
	
	private byte membershipTypeValue;
	private String membershipTypeName;
	
	private MembershipType(byte membershipTypeValue, String membershipTypeName) {
		this.membershipTypeValue = membershipTypeValue;
	}
	
	public byte getValue() {
		return membershipTypeValue;
	}	
	
	public String getName() {
		return membershipTypeName;
	}
	
	public static MembershipType valueOf(byte membershipTypeValue) {
		MembershipType[] membershipTypes = MembershipType.values();
		for (MembershipType membershipType : membershipTypes) {
			if (membershipType.getValue() == membershipTypeValue) {
				return membershipType;
			}
		}	
		
		throw new IllegalArgumentException("the parameter membershipTypeValue["+membershipTypeValue+"] is not a MembershipType\"");
	}
}
