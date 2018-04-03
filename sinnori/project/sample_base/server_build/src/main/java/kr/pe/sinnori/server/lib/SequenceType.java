package kr.pe.sinnori.server.lib;

public enum SequenceType {
	UPLOAD_FILE_NAME((short)0, "업로드 파일 이름에 사용되는 시퀀스");
	
	private short sequenceTypeValue;
	private String sequenceTypeName;
	
	private SequenceType(short sequenceTypeValue, String sequenceTypeName) {
		this.sequenceTypeValue = sequenceTypeValue;
		this.sequenceTypeName = sequenceTypeName;
	}
	
	public short getValue() {
		return sequenceTypeValue;
	}
	
	public String getName() {
		return sequenceTypeName;
	}
	
	public static SequenceType valueOf(short sequenceTypeValue) {
		SequenceType[] boradTypes = SequenceType.values();
		for (SequenceType boardType : boradTypes) {
			if (boardType.getValue() == sequenceTypeValue) {
				return boardType;
			}
		}
		
		throw new IllegalArgumentException("the parameter sequenceTypeValue["+sequenceTypeValue+"] is a element of SequenceType set");
	}
}
