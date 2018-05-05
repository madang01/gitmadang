package kr.pe.codda.common.config.itemvalue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllDBCPPartConfiguration {	
	private List<String> dbcpNameList = new ArrayList<String>();
	private HashMap<String, DBCPParConfiguration> dbcpPartConfigurationHash = new HashMap<String, DBCPParConfiguration>();
	
	
	public void clear() {
		dbcpNameList.clear();
		dbcpPartConfigurationHash.clear();
	}
	
	public void addDBCPPartValueObject(DBCPParConfiguration dbcpPartValueObject) {		
		if (null == dbcpPartValueObject) {
			throw new IllegalArgumentException("the paramter dbcpPartValueObject is null");
		}

		String dbcpName = dbcpPartValueObject.getDBCPName();
		
		if (isRegistedDBCPName(dbcpName)) {
			throw new IllegalArgumentException("the paramter dbcpPartValueObject's dbcp name was registed");
		}
		dbcpNameList.add(dbcpName);
		dbcpPartConfigurationHash.put(dbcpName, dbcpPartValueObject);
	}
	
	public List<String> getDBCPNameList() {
		return dbcpNameList;
	}

	public boolean isRegistedDBCPName(String dbcpName) {		
		return (null != dbcpPartConfigurationHash.get(dbcpName));
	}
	

	public DBCPParConfiguration getDBCPPartConfiguration(String dbcpName) {
		return dbcpPartConfigurationHash.get(dbcpName);
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
