package kr.pe.codda.server.lib;

public enum MemberRoleType {
	ADMIN("A", "관리자"), USER("M", "일반회원"), GUEST("G", "손님");
	
	private String memberRoleTypeValue;
	private String memberRoleTypeName;
	
	private MemberRoleType(String memberRoleTypeValue, String memberRoleTypeName) {
		this.memberRoleTypeValue = memberRoleTypeValue;
		this.memberRoleTypeName = memberRoleTypeName;
	}
	
	public String getValue() {
		return memberRoleTypeValue;
	}	
	
	public String getName() {
		return memberRoleTypeName;
	}
	
	public static MemberRoleType valueOf(String nativeMemberRoleTypeValue, boolean isSuper) {
		if (null == nativeMemberRoleTypeValue) {
			throw new IllegalArgumentException("the parameter nativeMemberRoleTypeValue is null");
		}
		
		if (isSuper) {
			return valueOf(nativeMemberRoleTypeValue);
		}
		
		MemberRoleType[] memberTypes = MemberRoleType.values();
		for (MemberRoleType memberType : memberTypes) {
			if (memberType.getValue().equals(nativeMemberRoleTypeValue)) {
				return memberType;
			}
		}	
		
		throw new IllegalArgumentException("the parameter nativeMemberRoleTypeValue["+nativeMemberRoleTypeValue+"] is not a element of MemberRoleType set");
	}
}
