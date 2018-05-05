package kr.pe.codda.common.type;

/** 게시판 식별자, 0 : 공지, 1:자유, 2:FAQ */
public enum BoradType {
	NOTICE(0), FREE(1), FAQ(2);
	
	private int boardTypeID = 0;
	
	private BoradType(int boardTypeID) {
		this.boardTypeID = boardTypeID;
	}
	
	public int getBoradTypeID() {
		return boardTypeID;
	}
	
	public static BoradType valueOf(int boardTypeID) {
		BoradType[] boradTypes = BoradType.values();
		for (BoradType boardType : boradTypes) {
			if (boardType.getBoradTypeID() == boardTypeID) {
				return boardType;
			}
		}
		throw new IllegalArgumentException("the parameter boardTypeID["+boardTypeID+"] is a unknown borad type id");
	}
	
}
