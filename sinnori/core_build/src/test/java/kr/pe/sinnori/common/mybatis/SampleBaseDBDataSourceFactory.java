package kr.pe.sinnori.common.mybatis;

public class SampleBaseDBDataSourceFactory extends AbstractUnpooledDataSourceFactory {

	@Override
	public String getDBCPConnectionPoolName() {
		return "sample_base_db";
	}
}
