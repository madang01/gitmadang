package kr.pe.codda.server.lib;

public enum SequenceType {
	MENU((short)0, "메뉴에 사용되는 시퀀스"),
	NOTICE_BOARD((short)1, "공지 게시판에 사용되는 시퀀스"),
	FREE_BOARD((short)2, "자유 게시판에 사용되는 시퀀스"),
	FAQ_BOARD((short)3, "FAQ 게시판에 사용되는 시퀀스");
	
	private short sequenceID;
	private String sequenceTypeName;
	
	private SequenceType(short sequenceID, String sequenceTypeName) {
		this.sequenceID = sequenceID;
		this.sequenceTypeName = sequenceTypeName;
	}
	
	public short getSequenceID() {
		return sequenceID;
	}
	
	public String getName() {
		return sequenceTypeName;
	}
	
	public static SequenceType valueOf(short sequenceTypeID) {
		SequenceType[] boradTypes = SequenceType.values();
		for (SequenceType boardType : boradTypes) {
			if (boardType.getSequenceID() == sequenceTypeID) {
				return boardType;
			}
		}
		
		throw new IllegalArgumentException("the parameter sequenceTypeValue["+sequenceTypeID+"] is a element of SequenceType set");
	}
}
