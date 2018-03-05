package kr.pe.sinnori.common.mybatis;

public class MybatisEnviroment {
	private String dbcpName = null;
	private String dataSourceFacotryClassFullName = null;
	
	private String dbcpConfigFilePathString = null;
	
	// public MybatisEnviroment(String sinnoriInstalledPathString, String mainProjectName, String dbcpName, String dataSourceFacotryClassFullName) {
	public MybatisEnviroment(String dbcpName, String dbcpConfigFilePathString, String dataSourceFacotryClassFullName) {
		super();
		this.dbcpName = dbcpName;
		this.dataSourceFacotryClassFullName = dataSourceFacotryClassFullName;		
		// this.dbcpConfigFilePathString = BuildSystemPathSupporter.getDBCPConfigFilePathString(sinnoriInstalledPathString, mainProjectName, dbcpName);
		this.dbcpConfigFilePathString = dbcpConfigFilePathString;
	}
	public String getDBCPName() {
		return dbcpName;
	}
	public String getDataSourceFacotryClassFullName() {
		return dataSourceFacotryClassFullName;
	}
	
	public String getDBCPConfigFilePathString() {
		return dbcpConfigFilePathString;
	}
	
}
