package kr.pe.codda.weblib.common;


public enum MemberRoleType {
	ADMIN((byte)'A', "관리자"), MEMBER((byte)'M', "일반회원"), GUEST((byte)'G', "손님");
	
	private byte memberRoleTypeValue;
	private String memberRoleTypeName;
	
	private MemberRoleType(byte memberRoleTypeValue, String memberRoleTypeName) {
		this.memberRoleTypeValue = memberRoleTypeValue;
		this.memberRoleTypeName = memberRoleTypeName;
	}
	
	public byte getValue() {
		return memberRoleTypeValue;
	}	
	
	public String getName() {
		return memberRoleTypeName;
	}
	
	public static MemberRoleType valueOf(byte nativeMemberRoleTypeValue) {		
		
		MemberRoleType[] memberTypes = MemberRoleType.values();
		for (MemberRoleType memberType : memberTypes) {
			if (memberType.getValue() == nativeMemberRoleTypeValue) {
				return memberType;
			}
		}	
		
		throw new IllegalArgumentException("the parameter nativeMemberRoleTypeValue["+nativeMemberRoleTypeValue+"] is not a element of MemberRoleType set");
	}
}
