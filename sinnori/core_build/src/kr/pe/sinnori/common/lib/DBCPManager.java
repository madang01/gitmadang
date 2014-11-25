package kr.pe.sinnori.common.lib;

import java.sql.SQLException;

import kr.pe.sinnori.common.exception.DBNotReadyException;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * <pre>
 * DB 연결 폴 관리자인 아파치 commons-dbcp 를 신놀이 설정 파일에서 
 * 지정한 DB 관련 환경 변수값에 맞쳐 설정하여 이용하는 클래스.
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public final class DBCPManager implements CommonRootIF {
	private BasicDataSource dataSource = null;

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class SinnoriDBManagerHolder {
		static final DBCPManager singleton = new DBCPManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static DBCPManager getInstance() {
		return SinnoriDBManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private DBCPManager() {
		
		/*String driverClassName = (String) conf
				.getResource("jdbc.driver_class_name.value");
		String dbUserName = (String) conf
				.getResource("jdbc.db_user_name.value");
		String dbUserPasswordHex = (String) conf
				.getResource("jdbc.db_user_password_hex.value");
		String dbConnectURI = (String) conf
				.getResource("jdbc.connection_uri.value");*/
		
		String driverClassName = "org.mariadb.jdbc.Driver";
		String dbUserName = "madangsoe01";
		String dbUserPassword = "zx#$ui09";
		String dbConnectURI = "jdbc:mysql://172.30.1.15/SINNORIDB";

		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			log.warn("JDBC Driver not exist", e);
			return;
			// throw new RuntimeException("JDBC Driver not exist");
		}
		
		
		// FIXME!, 보안을 위해 제거 필요하지만 냅두자.
		log.debug(String.format("dbUserPassword=", dbUserPassword));

		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driverClassName);
		ds.setUsername(dbUserName);
		ds.setPassword(dbUserPassword);
		ds.setUrl(dbConnectURI);
		ds.setInitialSize(3);
		ds.setMaxTotal(3);
		ds.setMaxIdle(3);
		ds.setMinIdle(0);
		ds.setMaxWaitMillis(1000);
		ds.setDefaultAutoCommit(false);
		ds.setValidationQuery("select 1");
		ds.setValidationQueryTimeout(1000);

		dataSource = ds;

	}

	/**
	 * JDBC 연결 자원 반환한다.
	 * 
	 * @return JDBC 연결 자원
	 * @throws SQLException
	 * @throws DBNotReadyException DB 사용 준비가 안되었을 경우 던지는 예외
	 */
	public BasicDataSource getBasicDataSource() throws DBNotReadyException {
		if (null == dataSource)
			throw new DBNotReadyException("JDBC Driver not ready");
		

		return dataSource;
	}
	
	protected void finalize() throws Throwable {
		super.finalize();
		try {
			if (null != dataSource) dataSource.close();
		} catch(Exception e) {
			log.warn("unknown error", e);
		}
	}
}
