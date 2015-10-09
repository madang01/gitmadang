package kr.pe.sinnori.impl.server.mybatis;

import kr.pe.sinnori.common.etc.DBCPManager;
import kr.pe.sinnori.common.exception.DBNotReadyException;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TWSinnoriDBDataSourceFactory extends UnpooledDataSourceFactory {
	private Logger log = LoggerFactory.getLogger(TWSinnoriDBDataSourceFactory.class);
	
	public TWSinnoriDBDataSourceFactory() {
		DBCPManager dbcpManager = DBCPManager.getInstance();
		String dbcpConnectionPoolName = "tw_sinnoridb";
		try {
			this.dataSource = dbcpManager.getBasicDataSource(dbcpConnectionPoolName);
		} catch (DBNotReadyException e) {
			log.error("unknown dbcp connection pool name[{}]", dbcpConnectionPoolName);
			System.exit(1);
		}
	}
}
