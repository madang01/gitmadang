package kr.pe.sinnori.common.config.configvo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllDBCPPartConfigurationVO {	
	private List<String> dbcpNameList = new ArrayList<String>();
	private HashMap<String, DBCPParConfigurationVO> dbcpPartValueObjectHash = new HashMap<String, DBCPParConfigurationVO>();
	
	
	public void clear() {
		dbcpNameList.clear();
		dbcpPartValueObjectHash.clear();
	}
	
	public void addDBCPPartValueObject(DBCPParConfigurationVO dbcpPartValueObject) {		
		if (null == dbcpPartValueObject) {
			throw new IllegalArgumentException("the paramter dbcpPartValueObject is null");
		}

		String dbcpName = dbcpPartValueObject.getDBCPName();
		
		if (isRegistedDBCPName(dbcpName)) {
			throw new IllegalArgumentException("the paramter dbcpPartValueObject's dbcp name was registed");
		}
		dbcpNameList.add(dbcpName);
		dbcpPartValueObjectHash.put(dbcpName, dbcpPartValueObject);
	}
	
	public List<String> getDBCPNameList() {
		return dbcpNameList;
	}

	public boolean isRegistedDBCPName(String dbcpName) {		
		return (null != dbcpPartValueObjectHash.get(dbcpName));
	}
	

	public DBCPParConfigurationVO getDBCPPartValueObject(String dbcpName) {
		return dbcpPartValueObjectHash.get(dbcpName);
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
