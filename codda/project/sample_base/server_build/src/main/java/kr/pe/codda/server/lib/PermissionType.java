package kr.pe.codda.server.lib;

public enum PermissionType {
	ADMIN((byte)0, "관리자"), USER((byte)1, "일반인"), GUEST((byte)2, "손님");
	
	private byte boardWritePermissonTypeValue;
	private String boardWritePermissonTypeName;
	
	private PermissionType(byte boardWritePermissonTypeValue, String boardWritePermissonTypeName) {
		this.boardWritePermissonTypeValue = boardWritePermissonTypeValue;
		this.boardWritePermissonTypeName = boardWritePermissonTypeName;
	}
	
	public byte getValue() {
		return boardWritePermissonTypeValue;
	}
	
	public String getName() {
		return boardWritePermissonTypeName;
	}
	
	public static PermissionType valueOf(byte boardWritePermissonTypeValue) {
		PermissionType[] boardWritePermissionTypes = PermissionType.values();
		for (PermissionType boardWritePermissionType : boardWritePermissionTypes) {
			if (boardWritePermissionType.getValue() == boardWritePermissonTypeValue) {
				return boardWritePermissionType;
			}
		}
		
		String errorMessage = new StringBuilder()
				.append("the parameter boardWritePermissonTypeValue[")
				.append(boardWritePermissonTypeValue)
				.append("] is a element of BoardWritePermissionType set").toString();

		throw new IllegalArgumentException(errorMessage);
	}
}
