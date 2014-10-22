package main;

import java.sql.SQLException;

import kr.pe.sinnori.common.exception.DBNotReadyException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.DBCPManager;

import org.apache.commons.dbcp2.BasicDataSource;

public class DBCPTestMain implements CommonRootIF {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		kr.pe.sinnori.common.lib.DBCPManager sinnoriDBManager = DBCPManager.getInstance();
		BasicDataSource basicDataSource = null;
		try {
			basicDataSource = sinnoriDBManager.getBasicDataSource();
			
			
		} catch (DBNotReadyException e) {
			e.printStackTrace();
			return;
		}

		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rs = null;
		
		try {
			conn = basicDataSource.getConnection();
			
			log.info("conn auto commit="+conn.getAutoCommit());
			
			String sql = "select 1";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int result  = rs.getInt(1);
				
				log.info("result={}", result);
			}
			
			conn.commit();
		} catch (SQLException e) {
			log.warn("SQLException", e);
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch(Exception e) {
					log.warn("unknown error", e);
				}
			}
			
			if (null != pstmt) {
				try {
					pstmt.close();
				} catch(Exception e) {
					log.warn("unknown error", e);
				}
			}
			
			if (null != conn) {
				try {
					conn.close();
				} catch(Exception e) {
					log.warn("unknown error", e);
				}
			}
		}
	}

}
