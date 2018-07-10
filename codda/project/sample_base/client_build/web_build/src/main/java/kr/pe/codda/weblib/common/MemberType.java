package kr.pe.codda.weblib.common;

public enum MemberType {
	ADMIN('A', "관리자"), USER('M', "일반회원");
	
	private char memberTypeValue;
	private String memberTypeName;
	
	private MemberType(char memberTypeValue, String memberTypeName) {
		this.memberTypeValue = memberTypeValue;
		this.memberTypeName = memberTypeName;
	}
	
	public char getValue() {
		return memberTypeValue;
	}	
	
	public String getName() {
		return memberTypeName;
	}
	
	public static MemberType valueOf(byte membershipLevelValue) {
		MemberType[] membershipTypes = MemberType.values();
		for (MemberType membershipType : membershipTypes) {
			if (membershipType.getValue() == membershipLevelValue) {
				return membershipType;
			}
		}	
		
		throw new IllegalArgumentException("the parameter membershipLevelValue["+membershipLevelValue+"] is not a element of MemberLevel set");
	}
}
