package kr.pe.sinnori.common.weblib;

public class WebCommonType {
	
	public enum BOARD_TYPE {
		NOTICE_BOARD(1L), FREE_BOARD(2L);
		
		private long boardTypeID;
		private BOARD_TYPE(long boardTypeID) {
			this.boardTypeID = boardTypeID;
		}
		public long getBoardTypeID() {
			return boardTypeID;
		}
	};
}
