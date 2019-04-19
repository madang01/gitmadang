package kr.pe.codda.server.lib;

public enum BoardListType {
	ONLY_GROUP_ROOT((byte)0, "그룹루트"), TREE((byte)1, "계층");
	
	private byte boardListTypeValue;
	private String boardListTypeName;
	
	private BoardListType(byte boardListTypeValue, String boardListTypeName) {
		this.boardListTypeValue = boardListTypeValue;
		this.boardListTypeName  = boardListTypeName;
	}
	
	public byte getValue() {
		return boardListTypeValue;
	}
	
	public String getName() {
		return boardListTypeName;
	}
	
	public static BoardListType valueOf(byte boardListTypeValue) {
		BoardListType[] boardListTypes = BoardListType.values();
		for (BoardListType boardListType : boardListTypes) {
			if (boardListType.getValue() == boardListTypeValue) {
				return boardListType;
			}
		}
		
		String errorMessage = new StringBuilder()
				.append("게시판 목록 유형 값[")
				.append(boardListTypeValue)
				.append("]이 잘못되었습니다").toString();

		throw new IllegalArgumentException(errorMessage);
	}
}
