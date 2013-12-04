import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.SinnoriDBManager;

public class JDBCTestMain implements CommonRootIF {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SinnoriDBManager sinnoriDBManager = SinnoriDBManager.getInstance();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		String sql = "select * from member";

		try {
			System.out.println("Creating connection.");
			conn = sinnoriDBManager.getConnection();
			log.info("Creating statement.");
			stmt = conn.createStatement();
			log.info("Executing statement.");
			rset = stmt.executeQuery(sql);
			
			// StringBuffer retStringBuffer = new StringBuffer("Results:");
			
			// System.out.println("Results:");
			java.sql.ResultSetMetaData rsm = rset.getMetaData();
            int columnCount = rsm.getColumnCount();
            StringBuffer strBuff = new StringBuffer();
            String columnName = rsm.getColumnName(1);

            strBuff.append(columnName);

            for (int i=2; i < columnCount; i++) {
                strBuff.append(", ");
                columnName = rsm.getColumnName(i);
                strBuff.append(columnName);
            }
            //java.sql.Types sqlType =            
            log.info("table, columnNames=[%s]", strBuff.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
			} catch (Exception e) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}

	}

}
