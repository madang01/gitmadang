package kr.pe.sinnori.server.lib;

public enum BoardType {
	NOTICE((byte)0, "공지"), FREE((byte)1, "자유"), FAQ((byte)2, "질문과답변");
	
	private byte boardTypeValue;
	private String boardTypeName;
	
	private BoardType(byte boardTypeValue, String boardTypeName) {
		this.boardTypeValue = boardTypeValue;
		this.boardTypeName = boardTypeName;
	}
	
	public byte getValue() {
		return boardTypeValue;
	}
	
	public String getName() {
		return boardTypeName;
	}
	
	public static BoardType valueOf(byte boardTypeValue) {
		BoardType[] boradTypes = BoardType.values();
		for (BoardType boardType : boradTypes) {
			if (boardType.getValue() == boardTypeValue) {
				return boardType;
			}
		}
		
		throw new IllegalArgumentException("the parameter boardTypeValue["+boardTypeValue+"] is a element of BoardType set");
	}
}
