package kr.pe.codda.weblib.common;

public enum PermissionType {
	ADMIN((byte)0, "관리자"), MEMBER((byte)1, "일반회원"), GUEST((byte)2, "손님");
	
	private byte permissonTypeValue;
	private String permissonTypeName;
	
	private PermissionType(byte boardWritePermissonTypeValue, String boardWritePermissonTypeName) {
		this.permissonTypeValue = boardWritePermissonTypeValue;
		this.permissonTypeName = boardWritePermissonTypeName;
	}
	
	public byte getValue() {
		return permissonTypeValue;
	}
	
	public String getName() {
		return permissonTypeName;
	}
	
	public static PermissionType valueOf(byte permissonTypeValue) {
		PermissionType[] boardWritePermissionTypes = PermissionType.values();
		for (PermissionType boardWritePermissionType : boardWritePermissionTypes) {
			if (boardWritePermissionType.getValue() == permissonTypeValue) {
				return boardWritePermissionType;
			}
		}
		
		String errorMessage = new StringBuilder()
				.append("the parameter permissonTypeValue[")
				.append(permissonTypeValue)
				.append("] is a element of permissonTypeValue set").toString();

		throw new IllegalArgumentException(errorMessage);
	}
}
