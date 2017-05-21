package kr.pe.sinnori.common.mysql;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.DBCPManager;
import kr.pe.sinnori.common.exception.DBCPDataSourceNotFoundException;

public abstract class AbstractUnpooledDataSourceFactory extends UnpooledDataSourceFactory {
	protected Logger log = LoggerFactory.getLogger(AbstractUnpooledDataSourceFactory.class);
	
	public AbstractUnpooledDataSourceFactory() {
		DBCPManager dbcpManager = DBCPManager.getInstance();
		String dbcpConnectionPoolName = getDBCPConnectionPoolName();
		try {
			this.dataSource = dbcpManager.getBasicDataSource(dbcpConnectionPoolName);
		} catch (DBCPDataSourceNotFoundException e) {
			log.error("unknown dbcp connection pool name[{}]", dbcpConnectionPoolName);
		}
		
	}
	public abstract String getDBCPConnectionPoolName();
}
