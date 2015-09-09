package kr.pe.sinnori.impl.server.mybatis;

import kr.pe.sinnori.common.exception.DBNotReadyException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.DBCPManager;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

public class TWSinnoriDBDataSourceFactory extends UnpooledDataSourceFactory implements CommonRootIF {
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
