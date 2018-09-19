package kr.pe.codda.weblib.common;

public enum MemberType {
	ADMIN("A", "관리자"), USER("M", "일반회원");
	
	private String memberTypeValue;
	private String memberTypeName;
	
	private MemberType(String memberTypeValue, String memberTypeName) {
		this.memberTypeValue = memberTypeValue;
		this.memberTypeName = memberTypeName;
	}
	
	public String getValue() {
		return memberTypeValue;
	}	
	
	public String getName() {
		return memberTypeName;
	}
	
	public static MemberType valueOf(String nativeMemberTypeValue, boolean isSuper) {
		if (null == nativeMemberTypeValue) {
			throw new IllegalArgumentException("the parameter nativeMemberTypeValue is null");
		}
		
		if (isSuper) {
			return valueOf(nativeMemberTypeValue);
		}
		
		MemberType[] memberTypes = MemberType.values();
		for (MemberType memberType : memberTypes) {
			if (memberType.getValue().equals(nativeMemberTypeValue)) {
				return memberType;
			}
		}	
		
		throw new IllegalArgumentException("the parameter memberTypeValue["+nativeMemberTypeValue+"] is not a element of MemberLevel set");
	}
}
