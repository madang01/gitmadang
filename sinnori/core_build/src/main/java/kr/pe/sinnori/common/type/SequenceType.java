package kr.pe.sinnori.common.type;

public enum SequenceType {
	UPLOAD_FILE_NAME(1);
	
	private int sequenceTypeID;
	
	private SequenceType(int sequenceTypeID) {
		this.sequenceTypeID = sequenceTypeID;
	}

	public int getSequenceTypeID() {
		return sequenceTypeID;
	}
}
