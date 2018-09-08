package kr.pe.codda.server.lib;

public enum BoardType {
	NOTICE((short)0, "공지"), FREE((short)1, "자유"), FAQ((short)2, "FAQ");
	
	private short boardID;
	private String boardTypeName;
	
	private BoardType(short boardID, String boardTypeName) {
		this.boardID = boardID;
		this.boardTypeName = boardTypeName;
	}
	
	public short getBoardID() {
		return boardID;
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
	
	public SequenceType toSequenceType() {
		SequenceType boardSequenceType = null;
		
		if (BoardType.NOTICE.equals(this)) {
			boardSequenceType = SequenceType.NOTICE_BOARD;
		} else if (BoardType.FAQ.equals(this)) {
			boardSequenceType = SequenceType.FAQ_BOARD;
		} else {
			boardSequenceType = SequenceType.FREE_BOARD;
		}
		
		return boardSequenceType;
	}
}
