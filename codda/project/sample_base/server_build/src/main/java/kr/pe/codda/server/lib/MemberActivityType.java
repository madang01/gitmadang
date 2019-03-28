package kr.pe.codda.server.lib;

public enum MemberActivityType {
	WRITE((byte)'W', "게시글 작성"), REPLY((byte)'R', "댓글 작성"), DELETE((byte)'D', "게시글 삭제"), VOTE((byte)'V', "게시글 추천");
	
	private byte memberActivityTypeValue;
	private String memberActivityTypeName;
	
	private MemberActivityType(byte memberActivityTypeValue, String memberActivityTypeName) {
		this.memberActivityTypeValue = memberActivityTypeValue;
		this.memberActivityTypeName = memberActivityTypeName;
	}
	
	public byte getValue() {
		return memberActivityTypeValue;
	}
	
	public String getName() {
		return memberActivityTypeName;
	}
	
	public static MemberActivityType valueOf(byte memberActivityTypeValue) {
		MemberActivityType[] userActivityTypes = MemberActivityType.values();
		for (MemberActivityType userActivityType : userActivityTypes) {
			if (userActivityType.getValue() == memberActivityTypeValue) {
				return userActivityType;
			}
		}
		
		throw new IllegalArgumentException("the parameter memberActivityTypeValue["+memberActivityTypeValue+"] is a element of MemberActivityType set");
	}
}
