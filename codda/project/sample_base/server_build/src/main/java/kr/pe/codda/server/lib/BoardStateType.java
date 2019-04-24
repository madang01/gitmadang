package kr.pe.codda.server.lib;

/**
 * 어드민이 게시글을 차단하면 지정한 게시글은 "블락" 상태가 되고 동시에 딸린 하위 게시글들은 "트리블락" 상태가 된다.
 * 
 * @author Won Jonghoon
 *
 */
public enum BoardStateType {
	OK((byte)'Y', "정상"), BLOCK((byte)'B', "블락"), DELETE((byte)'D', "삭제"), TREEBLOCK((byte)'T', "트리블락");
	
	private byte boardStateTypeValue;
	private String boardStateTypeName;
	
	private BoardStateType(byte boardStateTypeValue, String boardStateTypeName) {
		this.boardStateTypeValue = boardStateTypeValue;
		this.boardStateTypeName = boardStateTypeName;
	}
	
	public byte getValue() {
		return boardStateTypeValue;
	}
	
	public String getName() {
		return boardStateTypeName;
	}
	
	public static BoardStateType valueOf(byte boardStateTypeValue) {		
		
		BoardStateType[] boardStateTypes = BoardStateType.values();
		for (BoardStateType boardStateType : boardStateTypes) {
			if (boardStateType.getValue() == boardStateTypeValue) {
				return boardStateType;
			}
		}
		
		throw new IllegalArgumentException("게시글 상태["+boardStateTypeValue+"] 값이 잘못되었습니다");
	}
}
