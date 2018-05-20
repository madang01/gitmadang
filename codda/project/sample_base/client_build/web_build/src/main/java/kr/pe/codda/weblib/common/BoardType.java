package kr.pe.codda.weblib.common;

public enum BoardType {
	NOTICE((short)0, "공지"), FREE((short)1, "자유"), FAQ((short)2, "FAQ");
	
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
	
	public static String getSetString() {
		StringBuilder setStringBuilder = new StringBuilder();
		boolean isFirst = true;
		for (BoardType boardType : BoardType.values()) {
			if (isFirst) {
				isFirst = false;
			} else {
				setStringBuilder.append(", ");
			}
			setStringBuilder.append(boardType.getValue());
			setStringBuilder.append(":");
			setStringBuilder.append(boardType.getName());
		}
		return setStringBuilder.toString();
	}
}
