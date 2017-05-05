package kr.pe.sinnori.server.mysql;

public class EnvironmentInfo {
	private String id;
	private DataSourceInfo dataSourceInfo = null;
	
	
	public EnvironmentInfo(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public DataSourceInfo getDataSourceInfo() {
		return dataSourceInfo;
	}
	public void setDataSourceInfo(DataSourceInfo dataSourceInfo) {
		this.dataSourceInfo = dataSourceInfo;
	}
	
	
}
