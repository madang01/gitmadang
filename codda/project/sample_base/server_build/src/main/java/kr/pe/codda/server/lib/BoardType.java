package kr.pe.codda.server.lib;

public enum BoardType {
	NOTICE(SequenceType.NOTICE_BOARD, "공지"), 
	FREE(SequenceType.FREE_BOARD, "자유"), 
	ISSUE(SequenceType.ISSUE_BOARD, "이슈");
	
	private SequenceType sequenceType;
	private String boardTypeName;
	
	private BoardType(SequenceType sequenceType, String boardTypeName) {
		this.sequenceType = sequenceType;
		this.boardTypeName = boardTypeName;
	}
	
	public short getBoardID() {
		return sequenceType.getSequenceID();
	}
	
	public String getName() {
		return boardTypeName;
	}
	
	public static BoardType valueOf(short boardTypeValue) {
		BoardType[] boradTypes = BoardType.values();
		for (BoardType boardType : boradTypes) {
			if (boardType.getBoardID() == boardTypeValue) {
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
			setStringBuilder.append(boardType.getBoardID());
			setStringBuilder.append(":");
			setStringBuilder.append(boardType.getName());
		}
		return setStringBuilder.toString();
	}
	
	public SequenceType toSequenceType() {
		return sequenceType;
	}
}
