package kr.pe.codda.server.lib;

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
	
	public static PermissionType valueOf(String title, byte permissonTypeValue) {
		PermissionType[] boardWritePermissionTypes = PermissionType.values();
		for (PermissionType boardWritePermissionType : boardWritePermissionTypes) {
			if (boardWritePermissionType.getValue() == permissonTypeValue) {
				return boardWritePermissionType;
			}
		}
		
		String errorMessage = new StringBuilder()
				.append(title)
				.append("권한 값[")
				.append(permissonTypeValue)
				.append("]이 잘못되었습니다").toString();

		throw new IllegalArgumentException(errorMessage);
	}
}
