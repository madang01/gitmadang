package kr.pe.codda.server.lib;

public enum BoardReplyPolicyType {
	NO_SUPPORTED((byte)0, "댓글없음"), ONLY_ROOT((byte)1, "본문에만 댓글허용"), ALL((byte)2, "본문및댓글에댓글허용");
	
	private byte boardReplyPolicyTypeValue;
	private String boardReplyPolicyTypeName;
	
	private BoardReplyPolicyType(byte boardReplyPolicyTypeValue, String boardReplyPolicyTypeName) {
		this.boardReplyPolicyTypeValue = boardReplyPolicyTypeValue;
		this.boardReplyPolicyTypeName = boardReplyPolicyTypeName;
	}
	
	public byte getValue() {
		return boardReplyPolicyTypeValue;
	}
	
	public String getName() {
		return boardReplyPolicyTypeName;
	}
	
	public static BoardReplyPolicyType valueOf(byte boardReplyPolicyTypeValue) {
		BoardReplyPolicyType[] boardReplyPolicyTypes = BoardReplyPolicyType.values();
		for (BoardReplyPolicyType boardReplyPolicyType : boardReplyPolicyTypes) {
			if (boardReplyPolicyType.getValue() == boardReplyPolicyTypeValue) {
				return boardReplyPolicyType;
			}
		}
		
		String errorMessage = new StringBuilder()
				.append("the parameter boardReplyPolicyTypeValue[")
				.append(boardReplyPolicyTypeValue)
				.append("] is a element of BoardReplyPolicyType set").toString();

		throw new IllegalArgumentException(errorMessage);
	}
}
