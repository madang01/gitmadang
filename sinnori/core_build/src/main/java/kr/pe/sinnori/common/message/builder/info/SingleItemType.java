package kr.pe.sinnori.common.message.builder.info;

public enum SingleItemType {
	BYTE(0, "byte"),
	UNSIGNED_BYTE(1, "unsigned byte"),
	SHORT(2, "short"),
	UNSIGNED_SHORT(3, "unsigned short"), 
	INTEGER(4, "integer"), 
	UNSIGNED_INTEGER(5, "unsigned integer"),
	LONG(6, "long"),
	UB_PASCAL_STRING(7, "ub pascal string"),
	US_PASCAL_STRING(8, "us pascal string"),
	SI_PASCAL_STRING(9, "si pascal string"),
	FIXED_LENGTH_STRING(10, "fixed length string"),
	UB_VARIABLE_LENGTH_BYTES(11, "ub variable length byte[]"),
	US_VARIABLE_LENGTH_BYTES(12, "us variable length byte[]"),
	SI_VARIABLE_LENGTH_BYTES(13, "si variable length byte[]"),
	FIXED_LENGTH_BYTES(14, "fixed length byte[]"),
	JAVA_SQL_DATE(15, "java sql date"),
	JAVA_SQL_TIMESTAMP(16, "java sql timestamp"),
	BOOLEAN(17, "boolean")
	;
	
	
	private String itemTypeName;
	private int itemTypeID;
	
	
	private SingleItemType(int itemTypeID, String itemTypeName) {
		this.itemTypeID = itemTypeID;
		this.itemTypeName = itemTypeName;
	}
	
	public int getItemTypeID() {
		return itemTypeID;
	}
	
	public String getItemTypeName() {
		return itemTypeName;
	}
}
