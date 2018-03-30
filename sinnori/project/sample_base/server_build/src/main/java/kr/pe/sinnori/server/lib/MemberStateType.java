package kr.pe.sinnori.server.lib;

public enum MemberStateType {
	OK((byte)0, "정상"), BLOCK((byte)1, "블락"), WITHDRAWAL((byte)2, "탈퇴");
	
	private byte memberStateTypeValue;
	private String memberStateTypeName;
	
	private MemberStateType(byte memberStateTypeValue, String memberStateTypeName) {
		this.memberStateTypeValue = memberStateTypeValue;
		this.memberStateTypeName = memberStateTypeName;
	}
	public byte getValue() {
		return memberStateTypeValue;
	}
	
	public String getName() {
		return memberStateTypeName;
	}
	
	public static MemberStateType valueOf(byte memberStateTypeValue) {		
		MemberStateType[] memeberStateTypes = MemberStateType.values();
		for (MemberStateType memeberStateType : memeberStateTypes) {
			if (memeberStateType.getValue() == memberStateTypeValue) {
				return memeberStateType;
			}
		}	
		
		throw new IllegalArgumentException("the parameter memberStateTypeValue["+memberStateTypeValue+"] is a unknown member state type id\"");
		
	}
}
