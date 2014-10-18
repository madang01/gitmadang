package kr.pe.sinnori.common.lib;

import kr.pe.sinnori.common.exception.DBNotReadyException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.SinnoriDBManager;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

public class DBCPDataSourceFactory extends UnpooledDataSourceFactory implements CommonRootIF {
	private SinnoriDBManager sinnoriDBManager = SinnoriDBManager.getInstance();
	public DBCPDataSourceFactory() {
		try {
			this.dataSource = sinnoriDBManager.getBasicDataSource();
		} catch (DBNotReadyException e) {
			log.error("DBNotReadyException", e);
			System.exit(1);
		}
	}

}
