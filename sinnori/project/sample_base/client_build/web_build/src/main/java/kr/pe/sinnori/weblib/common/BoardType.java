package kr.pe.sinnori.weblib.common;

public enum BoardType {
	NOTICE_BOARD(1L), FREE_BOARD(2L);
	
	private long boardId;
	private BoardType(long boardTypeID) {
		this.boardId = boardTypeID;
	}
	public long getBoardId() {
		return boardId;
	}
}
