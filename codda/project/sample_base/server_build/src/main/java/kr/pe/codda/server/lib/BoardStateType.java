package kr.pe.codda.server.lib;

/**
 * 어드민이 게시글을 차단하면 지정한 게시글은 "블락" 상태가 되고 동시에 딸린 하위 게시글들은 "트리블락" 상태가 된다.
 * 
 * @author Won Jonghoon
 *
 */
public enum BoardStateType {
	OK("Y", "정상"), BLOCK("B", "블락"), DELETE("D", "삭제"), TREEBLOCK("T", "트리블락");;
	
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
