package kr.pe.codda.weblib.common;

public enum BoardType {
	NOTICE((short)1, "공지"), 
	FREE((short)2, "자유"), 
	ISSUE((short)3, "이슈");
	
	private short boardID;
	private String boardTypeName;
	
	private BoardType(short boardTypeID, String boardTypeName) {
		this.boardID = boardTypeID;
		this.boardTypeName = boardTypeName;
	}
	
	public short getBoardID() {
		return boardID;
	}
	
	public String getName() {
		return boardTypeName;
	}
	
	public static BoardType valueOf(short boardID) {
		BoardType[] boradTypes = BoardType.values();
		for (BoardType boardType : boradTypes) {
			if (boardType.getBoardID() == boardID) {
				return boardType;
			}
		}
		
		throw new IllegalArgumentException("the parameter boardID["+boardID+"] is a element of BoardType set");
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
			setStringBuilder.append(boardType.getBoardID());
			setStringBuilder.append(":");
			setStringBuilder.append(boardType.getName());
		}
		return setStringBuilder.toString();
	}
	
}
