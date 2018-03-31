package kr.pe.sinnori.server.lib;

public enum BoardType {
	NOTICE((short)0, "공지"), FREE((short)1, "자유"), FAQ((short)2, "질문과답변");
	
	private short boardTypeValue;
	private String boardTypeName;
	
	private BoardType(short boardTypeValue, String boardTypeName) {
		this.boardTypeValue = boardTypeValue;
		this.boardTypeName = boardTypeName;
	}
	
	public short getValue() {
		return boardTypeValue;
	}
	
	public String getName() {
		return boardTypeName;
	}
	
	public static BoardType valueOf(short boardTypeValue) {
		BoardType[] boradTypes = BoardType.values();
		for (BoardType boardType : boradTypes) {
			if (boardType.getValue() == boardTypeValue) {
				return boardType;
			}
		}
		
		throw new IllegalArgumentException("the parameter boardTypeValue["+boardTypeValue+"] is a element of BoardType set");
	}
}
