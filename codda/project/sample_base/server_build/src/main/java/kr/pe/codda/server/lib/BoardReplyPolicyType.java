package kr.pe.codda.server.lib;

public enum BoardReplyPolicyType {
	NO_REPLY((byte)0, "댓글없음"), ONLY_ROOT((byte)1, "본문에만"), ALL((byte)2, "본문및 댓글 모두");
	
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
				.append("게시판 댓글 정책 유형 값[")
				.append(boardReplyPolicyTypeValue)
				.append("]이 잘못되었습니다").toString();

		throw new IllegalArgumentException(errorMessage);
	}
}
