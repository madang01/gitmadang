package kr.pe.codda.server.lib;

import org.jooq.types.UByte;

public enum SequenceType {
	MENU(UByte.valueOf(0), "메뉴에 사용되는 시퀀스");

	private UByte sequenceID;
	private String sequenceTypeName;

	private SequenceType(UByte sequenceID, String sequenceTypeName) {
		this.sequenceID = sequenceID;
		this.sequenceTypeName = sequenceTypeName;
	}

	public UByte getSequenceID() {
		return sequenceID;
	}

	public String getName() {
		
		return sequenceTypeName;
	}

	public static SequenceType valueOf(UByte sequenceTypeID) {
		SequenceType[] boradTypes = SequenceType.values();
		for (SequenceType boardType : boradTypes) {
			if (boardType.getSequenceID().equals(sequenceTypeID)) {
				return boardType;
			}
		}

		throw new IllegalArgumentException(
				"the parameter sequenceTypeValue[" + sequenceTypeID + "] is a element of SequenceType set");
	}
}
