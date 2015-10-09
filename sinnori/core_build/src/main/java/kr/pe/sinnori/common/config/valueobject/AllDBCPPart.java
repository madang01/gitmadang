package kr.pe.sinnori.common.config.valueobject;

import java.util.HashMap;
import java.util.List;

public class AllDBCPPart {	
	private List<String> dbcpNameList = null;
	private HashMap<String, DBCPPart> dbcpPartHash = null;
	
	public AllDBCPPart(List<String> dbcpNameList, HashMap<String, DBCPPart> dbcpPartHash) {
		if (null == dbcpNameList) {
			String errorMessage = "the paramter dbcpNameList is null";
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == dbcpPartHash) {
			String errorMessage = "the paramter dbcpPartHash is null";
			throw new IllegalArgumentException(errorMessage);
		}
		this.dbcpNameList = dbcpNameList;
		this.dbcpPartHash = dbcpPartHash;
	}
	
	public List<String> getDBCPNameList() {
		return dbcpNameList;
	}

	public boolean isRegistedDBCPName(String dbcpName) {		
		return (null != dbcpPartHash.get(dbcpName));
	}
	

	public DBCPPart getDBCPPart(String dbcpName) {
		return dbcpPartHash.get(dbcpName);
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("AllDBCPPart [dbcpNameList=");
		builder.append(dbcpNameList != null ? dbcpNameList.subList(0,
				Math.min(dbcpNameList.size(), maxLen)) : null);
		builder.append("]");
		return builder.toString();
	}
}
