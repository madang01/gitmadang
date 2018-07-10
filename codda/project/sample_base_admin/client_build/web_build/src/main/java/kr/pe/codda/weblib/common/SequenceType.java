package kr.pe.codda.weblib.common;

public enum SequenceType {
	UPLOAD_FILE_NAME((short)0, "업로드 파일 이름에 사용되는 시퀀스"),
	NOTICE_BOARD((short)1, "공지 게시판에 사용되는 시퀀스"),
	FREE_BOARD((short)2, "공지 게시판에 사용되는 시퀀스"),
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
	
	public static SequenceType valueOf(short sequenceID) {
		SequenceType[] boradTypes = SequenceType.values();
		for (SequenceType boardType : boradTypes) {
			if (boardType.getSequenceID() == sequenceID) {
				return boardType;
			}
		}
		
		throw new IllegalArgumentException("the parameter sequenceTypeValue["+sequenceID+"] is a element of SequenceType set");
	}
}
