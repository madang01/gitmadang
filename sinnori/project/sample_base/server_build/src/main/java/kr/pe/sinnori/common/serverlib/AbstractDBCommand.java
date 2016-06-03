package kr.pe.sinnori.common.serverlib;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import kr.pe.sinnori.common.etc.DBCPManager;
import kr.pe.sinnori.common.exception.DBNotReadyException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDBCommand {
	protected Logger log = LoggerFactory.getLogger(AbstractDBCommand.class);
	private DBCPManager dbcpManager = DBCPManager.getInstance();
	private BasicDataSource basicDataSource = null;
	
	private List<String> tableNameList = new ArrayList<String>();
	private List<String> creatingTableSqlList = new ArrayList<String>();
	
	private List<String> insertingSqlList = new ArrayList<String>();
	
	abstract public String getDBCPConnectionPoolName();
	
	private String schemaName = null;
	
	public AbstractDBCommand() {
		String dbcpConnectionPoolName = getDBCPConnectionPoolName();
		
		try {
			basicDataSource = dbcpManager.getBasicDataSource(dbcpConnectionPoolName);
		} catch (DBNotReadyException e) {
			String errorMessage = new StringBuilder("The DBCP Connection Pool[").append("").toString();
			log.error(errorMessage, e);
			System.exit(1);
		}
		
		String url = basicDataSource.getUrl();
		
		int beginIndex = url.lastIndexOf("//");
		int lastIndex = url.lastIndexOf('/');		
		if (lastIndex < 0) {
			log.error("The DBCP Connection Pool[{}]'s url[{}] doesn't include the character '/'", dbcpConnectionPoolName, url);
			System.exit(1);
		}
		
		if (lastIndex == beginIndex+1) {
			log.error("The DBCP Connection Pool[{}]'s url[{}] doesn't include the DB Schema separator character '/'", dbcpConnectionPoolName, url);
			System.exit(1);
		}
		
		if (lastIndex+1 >= url.length()) {
			log.error("The DBCP Connection Pool[{}]'s url[{}]'s length is greater than or equal to {}", dbcpConnectionPoolName, url, lastIndex+1);
			System.exit(1);
		}
		try {
			schemaName = url.substring(lastIndex+1);
		} catch(IndexOutOfBoundsException e) {
			log.error("The DBCP Connection Pool[{}]'s url[{}] fail to substring from index[{}]", 
					dbcpConnectionPoolName, url, lastIndex+1);
			System.exit(1);
		}
	}
	
	public String getSchemaName() {
		return schemaName;
	}
	
	public String getTableNameFromInsertingSql(String insertingSql) throws IllegalArgumentException {
		if (null == insertingSql) {
			throw new IllegalArgumentException("The parameter 'insertingSql' is null");
		}
		
		String tableName = null;
		String[] tokens = insertingSql.split("\\s+");
		
		
		if (tokens.length < 6) {
			String errorMessage = new StringBuilder("Six more tokens need in inserting sql[")
			.append(insertingSql)
			.append("]").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		if (!tokens[0].equalsIgnoreCase("insert")) {
			String errorMessage = new StringBuilder("The first token must be 'insert' in inserting sql[")
			.append(insertingSql)
			.append("]").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		if (!tokens[1].equalsIgnoreCase("into")) {
			String errorMessage = new StringBuilder("The second token must be 'into' in inserting sql[")
			.append(insertingSql)
			.append("]").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		String tablePartString = tokens[2].replaceAll("`", "");
		
		StringTokenizer token = new StringTokenizer(tablePartString, ".");
		if (!token.hasMoreTokens()) {
			/** dead code */
			String errorMessage = new StringBuilder("The instance of StringTokenizer(\"")
			.append(tablePartString)
			.append("\", \".\") doesn't hava tokens, check inserting sql[")
			.append(insertingSql)
			.append("]").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		String first = token.nextToken();
		if (token.hasMoreTokens()) {
			String second = token.nextToken();
			if (!first.equals(schemaName)) {
				String errorMessage = new StringBuilder("The inserting sql[")
				.append(insertingSql)
				.append("]'s schema[")
				.append(first)
				.append("] is different from ths dbcp connection pool name[")
				.append(getDBCPConnectionPoolName())
				.append("]'s schema[")
				.append(schemaName)
				.append("]").toString();
				
				throw new IllegalArgumentException(errorMessage);
			}
			tableName = second;
		} else {
			tableName = first;
		}		
		
		
		return tableName;
	}
	
	public void addInsertingSql(String insertingSql) throws IllegalArgumentException {
		String tableName = getTableNameFromInsertingSql(insertingSql);
		if (! tableNameList.contains(tableName)) {
			String errorMessage = new StringBuilder("The inserting sql[")
			.append(insertingSql)
			.append("] has a bad table name[")
			.append(tableName)
			.append("] that doesn't exist").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		insertingSqlList.add(insertingSql);
	}
	
	public String getTableNameFromCreatingTableSql(String creatingTableSql, String schemaName) {
		if (null == creatingTableSql) {
			throw new IllegalArgumentException("The parameter 'creatingTableSql' is null");
		}
		
		String tableName = null;		
		String[] tokens = creatingTableSql.split("\\s+");
		
		if (tokens.length < 4) {
			String errorMessage = new StringBuilder("Four more tokens need in creating sql[")
			.append(creatingTableSql)
			.append("]").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (!tokens[0].equalsIgnoreCase("create")) {
			String errorMessage = new StringBuilder("The first token must be 'create' in creating table sql[")
			.append(creatingTableSql)
			.append("]").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		if (!tokens[1].equalsIgnoreCase("table")) {
			String errorMessage = new StringBuilder("The second token must be 'table' in creating table sql[")
			.append(creatingTableSql)
			.append("]").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		int inx =  -1;
		for (int j=0; j < tokens.length; j++) {
			String token = tokens[j];
			if (token.startsWith("(")){
				inx = j;
				break;
			}
		}
		
		if (-1 == inx) {
			String errorMessage = new StringBuilder("fail to find '(' in creating table sql[")
			.append(creatingTableSql)
			.append("]").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		if (inx <= 2) {
			String errorMessage = new StringBuilder("the token '(' index must be greater than two in creating table sql[")
			.append(creatingTableSql)
			.append("]").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		String tablePartString = tokens[inx-1];
		tablePartString = tablePartString.replaceAll("`", "");
		StringTokenizer token = new StringTokenizer(tablePartString, ".");
		if (!token.hasMoreTokens()) {
			/** dead code */
			String errorMessage = new StringBuilder("The instance of StringTokenizer(\"")
			.append(tablePartString)
			.append("\", \".\") doesn't hava tokens, check creating table sql[")
			.append(creatingTableSql)
			.append("]").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		String first = token.nextToken();
		if (token.hasMoreTokens()) {
			String second = token.nextToken();
			if (!first.equals(schemaName)) {
				String errorMessage = new StringBuilder("The creating table sql[")
				.append(creatingTableSql)
				.append("]'s schema[")
				.append(first)
				.append("] is different from ths dbcp connection pool name[")
				.append(getDBCPConnectionPoolName())
				.append("]'s schema[")
				.append(schemaName)
				.append("]").toString();
				
				throw new IllegalArgumentException(errorMessage);
			}
			tableName = second;
		} else {
			tableName = first;
		}
		
		//log.info("table name=[{}]", arryCreatingTableSql[i][0]);
		
		
		
		return tableName;
	}
	
	/**
	 * 테이블 생성 쿼리문을 목록에 추가한다. 단 생성할 테이블 중복시 종료된다.
	 * @param creatingTableSql 테이블 생성 쿼리문
	 */
	public void addCreatingTableSql(String creatingTableSql) throws IllegalArgumentException {
		if (null == schemaName) {
			log.error("schemaName is null, you moust call super() that is AbstractDBCommand()");
			System.exit(1);
		}
		String tableName = getTableNameFromCreatingTableSql(creatingTableSql, schemaName);
		
		if (tableNameList.contains(tableName)) {
			String errorMessage = new StringBuilder("The creating table sql[")
			.append(creatingTableSql)
			.append("]'s table[")
			.append(tableName)
			.append("] has already been created").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		tableNameList.add(tableName);
		creatingTableSqlList.add(creatingTableSql);
	}
	
	
	public enum EXECUTION_MODE {
		CREATION_IF_NOT_EXIST, CREATION, DELETION
	}
	
	public void execute() {
		this.execute(EXECUTION_MODE.CREATION_IF_NOT_EXIST);
	}
	
	public void execute(EXECUTION_MODE executionMode) {
		String dbcpConnectionPoolName = getDBCPConnectionPoolName();
		
		java.sql.Connection conn = null;

		try {

			conn = basicDataSource.getConnection();
			

			log.info("The DBCP Connection Pool[{}]::schemaName[{}]::conn auto commit=[{}], change auto commit false",
					dbcpConnectionPoolName, schemaName, conn.getAutoCommit());

			conn.setAutoCommit(false);
			
			checkConnectionValidation(conn);
			
			if (executionMode.equals(EXECUTION_MODE.CREATION)) {
				createAllTables(conn);
				insertItems(conn);
			} else if (executionMode.equals(EXECUTION_MODE.DELETION)) {
				dropAllTables(conn);
			} else {
				if (!isTable(conn)) {
					createAllTables(conn);
					insertItems(conn);
				}
			}

			conn.commit();

		} catch (SQLException e) {
			String errorMessage = new StringBuilder("The DBCP Connection Pool[").append(dbcpConnectionPoolName).append("] SQLException").toString();
			log.error(errorMessage, e);
			System.exit(1);

		} finally {

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					String errorMessage = new StringBuilder("fail to close connection of the DBCP Connection Pool[").append(dbcpConnectionPoolName).append("]").toString();
					log.warn(errorMessage, e);
				}
			}
		}
		
		log.info("The DBCP Connection Pool[{}] execution mode[{}] success", dbcpConnectionPoolName, executionMode.toString());
	}
	
	private void checkConnectionValidation(java.sql.Connection conn) {

		java.sql.PreparedStatement pstmt = null;

		java.sql.ResultSet rs = null;

		try {

			String sql = "select 1";

			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			if (!rs.next()) {
				log.error("db connectin validation check sql execution fail");
				System.exit(1);
			}
			
			log.info("db connectin validation check sql execution success");

			// conn.commit();

		} catch (SQLException e) {
			log.error("SQLException", e);
			System.exit(1);
		} finally {

			if (null != rs) {

				try {

					rs.close();

				} catch (Exception e) {

					log.warn("when rs close, unknown error", e);

				}

			}

			if (null != pstmt) {

				try {

					pstmt.close();

				} catch (Exception e) {

					log.warn("when pstmt close, unknown error", e);

				}

			}
		}
	}
	
	private void createTable(java.sql.Connection conn, String tableName, String sql)
			throws SQLException {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");		
		
		log.info("1.{} table creattion sql=[{}]", tableName, sql);

		Statement stmt = null;

		stmt = conn.createStatement();

		stmt.executeUpdate(sql);
		
		log.info("2.{} table creattion sql success", tableName);
	}
	
	private void dropTable(java.sql.Connection conn, String tableName)
			throws SQLException {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");		
		
		String sql = new StringBuilder("DROP TABLE IF EXISTS `").append(tableName).append("`").toString();
		
		log.info("1.{} table drop sql=[{}]", tableName, sql);

		Statement stmt = null;

		stmt = conn.createStatement();

		stmt.executeUpdate(sql);
		
		log.info("2.{} table drop sql success", tableName);
	}
	
	private void executeUpdateSql(java.sql.Connection conn, String sql)
			throws SQLException {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");		
		
		log.info("1.sql=[{}]", sql);

		Statement stmt = null;

		stmt = conn.createStatement();

		stmt.executeUpdate(sql);
		
		log.info("2. sql success");
	}
	
	private boolean isTable(java.sql.Connection conn) throws SQLException {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");
		DatabaseMetaData databaseMetaData = null;
		java.sql.ResultSet rs = null;
		boolean isTable = false;

		try {
			databaseMetaData = conn.getMetaData();

			rs = databaseMetaData.getTables(null, schemaName, null,
					new String[] { "TABLE" });

			if (rs.next()) {
				
				isTable = true;
			}

		} finally {
			if (null != rs) {

				try {

					rs.close();

				} catch (Exception e) {

					log.warn("when rs close, unknown error", e);

				}

			}
		}
		
		/** FIXME! */
		log.info("schemaName[{}] isTable=[{}]", schemaName, isTable);

		return isTable;
	}
	
	
	
	private void createAllTables(java.sql.Connection conn) {	
		
		int creatingTableSqlListSize = creatingTableSqlList.size();
		
		for (int i=0; i < creatingTableSqlListSize; i++) {
			String tableName = tableNameList.get(i);
			String creatingTableSql = creatingTableSqlList.get(i);	
			try {
				createTable(conn, tableName, creatingTableSql);
			} catch (SQLException e) {
				log.error("1.creating table sql["+creatingTableSql+"] execution fail", e);
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.warn("rollback fail when creating all table", e1);
				}
				System.exit(1);
			} catch (Exception e) {
				log.error("2.creating table sql["+creatingTableSql+"] execution fail", e);
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.warn("rollback fail when creating all table", e1);
				}
				System.exit(1);
			}
		}
		/*
		try {
			conn.commit();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.warn("rollback fail when creating all table", e1);
			}
			System.exit(1);
		}*/
	}
	
	
	
	
	private void dropAllTables(java.sql.Connection conn) {
		int creatingTableSqlListSize = creatingTableSqlList.size();
		
		/**
		 * 테이블 생성 순서 반대로 테이블 삭제
		 */
		for (int i=creatingTableSqlListSize-1; i >= 0; i--) {
			String tableName = tableNameList.get(i);
						
			try {
				dropTable(conn, tableName);
			} catch (SQLException e) {
				log.error("1.dropping table sql["+tableName+"] execution fail", e);
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.warn("rollback fail when creating all table", e1);
				}
				System.exit(1);
			} catch (Exception e) {
				log.error("2.dropping table sql["+tableName+"] execution fail", e);
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.warn("rollback fail when creating all table", e1);
				}
				System.exit(1);
			}
		}
	}
	
	private void insertItems(java.sql.Connection conn) {
		
		
		for (String insertingSql:insertingSqlList) {
						
			try {
				executeUpdateSql(conn, insertingSql);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.warn("rollback fail when creating all table", e1);
				}
				System.exit(1);
			} catch (Exception e) {
				log.error("unknown error", e);
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.warn("rollback fail when creating all table", e1);
				}
				System.exit(1);
			}
		}
	}
	
	
}
