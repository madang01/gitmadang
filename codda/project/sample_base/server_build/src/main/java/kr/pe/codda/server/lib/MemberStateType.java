package kr.pe.codda.server.lib;

public enum MemberStateType {
	OK("Y", "정상"), BLOCK("B", "블락"), WITHDRAWAL("W", "탈퇴");
	
	private String memberStateTypeValue;
	private String memberStateTypeName;
	
	private MemberStateType(String memberStateTypeValue, String memberStateTypeName) {
		this.memberStateTypeValue = memberStateTypeValue;
		this.memberStateTypeName = memberStateTypeName;
	}
	public String getValue() {
		return memberStateTypeValue;
	}
	
	public String getName() {
		return memberStateTypeName;
	}
	
	public static MemberStateType valueOf(String memberStateTypeValue, boolean isSuper) {
		if (null == memberStateTypeValue) {
			throw new IllegalArgumentException("the parameter memberStateTypeValue is null");
		}
		
		if (isSuper) {
			return valueOf(memberStateTypeValue);
		}
		
		MemberStateType[] memeberStateTypes = MemberStateType.values();
		for (MemberStateType memeberStateType : memeberStateTypes) {
			if (memeberStateType.getValue().equals(memberStateTypeValue)) {
				return memeberStateType;
			}
		}	
		
		throw new IllegalArgumentException("the parameter memberStateTypeValue["+memberStateTypeValue+"] is a unknown member state type id\"");
		
	}
}
