package kr.pe.sinnori.server.mybatis;

import kr.pe.sinnori.common.mybatis.AbstractUnpooledDataSourceFactory;

public class SampleBaseDBDataSourceFactory extends AbstractUnpooledDataSourceFactory {
	// private Logger log = LoggerFactory.getLogger(SampleBaseDBDataSourceFactory.class);

	@Override
	public String getDBCPConnectionPoolName() {
		return "sample_base_db";
	}	
}
