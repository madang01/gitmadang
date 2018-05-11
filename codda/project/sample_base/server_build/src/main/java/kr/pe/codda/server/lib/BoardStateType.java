package kr.pe.codda.server.lib;

public enum BoardStateType {
	OK("Y", "정상"), BLOCK("B", "블락"), DELETE("D", "삭제");
	
	private String boardStateTypeValue;
	private String boardStateTypeName;
	
	private BoardStateType(String boardStateTypeValue, String boardStateTypeName) {
		this.boardStateTypeValue = boardStateTypeValue;
		this.boardStateTypeName = boardStateTypeName;
	}
	
	public String getValue() {
		return boardStateTypeValue;
	}
	
	public String getName() {
		return boardStateTypeName;
	}
	
	public static BoardStateType valueOf(String boardStateTypeValue, boolean isSuper) {
		if (null == boardStateTypeValue) {
			throw new IllegalArgumentException("the parameter boardStateTypeValue is null");
		}
		
		if (isSuper) {
			return valueOf(boardStateTypeValue);
		}
		
		BoardStateType[] deleteFlags = BoardStateType.values();
		for (BoardStateType deleteFlag : deleteFlags) {
			if (deleteFlag.getValue().equals(boardStateTypeValue)) {
				return deleteFlag;
			}
		}
		
		throw new IllegalArgumentException("the parameter boardStateTypeValue["+boardStateTypeValue+"] is a element of BoardStateType set");
	}
}
