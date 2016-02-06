package kr.pe.sinnori.impl.server.mybatis;

import kr.pe.sinnori.common.etc.DBCPManager;
import kr.pe.sinnori.common.exception.DBNotReadyException;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleBaseDBDataSourceFactory extends UnpooledDataSourceFactory {
	private Logger log = LoggerFactory.getLogger(SampleBaseDBDataSourceFactory.class);
	
	public SampleBaseDBDataSourceFactory() {
		DBCPManager dbcpManager = DBCPManager.getInstance();
		String dbcpConnectionPoolName = "sample_base_db";
		try {
			this.dataSource = dbcpManager.getBasicDataSource(dbcpConnectionPoolName);
		} catch (DBNotReadyException e) {
			log.error("unknown dbcp connection pool name[{}]", dbcpConnectionPoolName);
			System.exit(1);
		}
	}
}
