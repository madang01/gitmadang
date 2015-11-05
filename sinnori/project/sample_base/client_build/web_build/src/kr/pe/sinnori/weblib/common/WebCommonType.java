package kr.pe.sinnori.weblib.common;

public class WebCommonType {
	
	public enum BOARD_TYPE {
		NOTICE_BOARD(1L), FREE_BOARD(2L);
		
		private long boardId;
		private BOARD_TYPE(long boardTypeID) {
			this.boardId = boardTypeID;
		}
		public long getBoardId() {
			return boardId;
		}
	};
	
	
}
