package kr.pe.codda.common.serverlib;

import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;

public class ServerDBEnvironmentTest {
	public static void main(String[] args) {
		Logger log = LoggerFactory.getLogger(CommonStaticFinalVars.BASE_PACKAGE_NAME);
		
		DataSource dataSource = null;;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e1) {
			log.error("fail to get dataSource");
			System.exit(1);
		}

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			
			log.warn("success");
		} catch (Exception e) {
			log.warn("fail to get connection");
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch(Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}
}
