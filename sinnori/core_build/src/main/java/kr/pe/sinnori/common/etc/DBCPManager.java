package kr.pe.sinnori.common.etc;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.valueobject.AllDBCPPart;
import kr.pe.sinnori.common.config.valueobject.DBCPPart;
import kr.pe.sinnori.common.exception.DBNotReadyException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * DB 연결 폴 관리자인 아파치 commons-dbcp 를 신놀이 설정 파일에서 
 * 지정한 DB 관련 환경 변수값에 맞쳐 설정하여 이용하는 클래스.
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public final class DBCPManager {
	private Logger log = LoggerFactory.getLogger(DBCPManager.class);
	private Hashtable<String, BasicDataSource> dbcpConnectionPoolName2BasicDataSourceHash = new Hashtable<String, BasicDataSource>();
	private Hashtable<BasicDataSource, String> basicDataSource2dbcpConnectionPoolNameHash = new Hashtable<BasicDataSource, String>();

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
		SinnoriConfigurationManager conf = SinnoriConfigurationManager.getInstance();
		AllDBCPPart allDBCPPart =  conf.getAllDBCPPart();
		
		List<String> dbcpNameList = allDBCPPart.getDBCPNameList();
		//HashMap<String, File> dbcpConfigFileHash = commonPart.getDbcpConfigFileHash();
		
		/*
		 * String driverClassName = (String) conf
		 * .getResource("jdbc.driver_class_name.value"); String dbUserName =
		 * (String) conf .getResource("jdbc.db_user_name.value"); String
		 * dbUserPasswordHex = (String) conf
		 * .getResource("jdbc.db_user_password_hex.value"); String dbConnectURI
		 * = (String) conf .getResource("jdbc.connection_uri.value");
		 */

		for (String dbcpName : dbcpNameList) {
			
			/*String propKey = new StringBuilder("dbcp.")
					.append(dbcpConnectionPoolName)
					.append(".confige_file.value").toString();
			File configeFile = (File) conf.getResource(propKey);*/
			DBCPPart dbcpPart = allDBCPPart.getDBCPPart(dbcpName);
			if (null == dbcpPart) {
				log.warn("the dbcp name[{}] is bad, check dbcp part of config file", dbcpName);
				continue;
			}
			File dbcpConfigFile = dbcpPart.getDBCPConfigFile();			

			Properties dbcpConnectionPoolConfig = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(dbcpConfigFile);
				dbcpConnectionPoolConfig.load(fis);				
			} catch (Exception e) {
				log.warn(
						"when dbcp connection pool[{}]'s config file[{}] io error, errormessage=",
						dbcpName, dbcpConfigFile.getAbsolutePath(), e.getMessage());
				continue;
			} finally {
				if (null != fis) {
					try {
						fis.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			

			String driverClassName = dbcpConnectionPoolConfig.getProperty("driver");
			if(null == driverClassName) {
				log.warn(
						"dbcp connection pool[{}]'s JDBC Driver name is null, dbcpConnectionPoolConfig={}",
						dbcpName, dbcpConnectionPoolConfig.toString());
				continue;
			}
			
			try {
				Class.forName(driverClassName);
			} catch (ClassNotFoundException e) {
				log.warn(
						"dbcp connection pool[{}]'s JDBC Driver[{}] not exist, dbcpConnectionPoolConfig={}",
						dbcpName, driverClassName, dbcpConnectionPoolConfig.toString());
				continue;
			}
			
			BasicDataSource basicDataSource = null;
			try {
				basicDataSource = BasicDataSourceFactory.createDataSource(dbcpConnectionPoolConfig);
			} catch (Exception e) {
				log.warn(
						"dbcp connection pool[{}] fail to create data source, dbcpConnectionPoolConfig={}",
						dbcpName, dbcpConnectionPoolConfig.toString());
				continue;
			}
			
			dbcpConnectionPoolName2BasicDataSourceHash.put(dbcpName, basicDataSource);
			basicDataSource2dbcpConnectionPoolNameHash.put(basicDataSource, dbcpName);

			/*setupDataSource(dbcpConnectionPoolName,
					dbcpConnectionPoolConfig);*/
			// FIXME!
			log.info("dbcp[{}] was registed successfully", dbcpName);			
		}
		/*
		 * String driverClassName = "org.mariadb.jdbc.Driver"; String dbUserName
		 * = "madangsoe01"; String dbUserPassword = "zx#$ui09"; String
		 * dbConnectURI = "jdbc:mysql://172.30.1.15/SINNORIDB";
		 * 
		 * try { Class.forName(driverClassName); } catch (ClassNotFoundException
		 * e) { log.warn("JDBC Driver not exist", e); return; // throw new
		 * RuntimeException("JDBC Driver not exist"); }
		 * 
		 * 
		 * // FIXME!, 보안을 위해 제거 필요하지만 냅두자.
		 * log.debug(String.format("dbUserPassword=", dbUserPassword));
		 * 
		 * BasicDataSource ds = new BasicDataSource();
		 * ds.setDriverClassName(driverClassName); ds.setUsername(dbUserName);
		 * ds.setPassword(dbUserPassword); ds.setUrl(dbConnectURI);
		 * ds.setInitialSize(3); ds.setMaxTotal(3); ds.setMaxIdle(3);
		 * ds.setMinIdle(0); ds.setMaxWaitMillis(1000);
		 * ds.setDefaultAutoCommit(false); ds.setValidationQuery("select 1");
		 * ds.setValidationQueryTimeout(1000);
		 * 
		 * dataSource = ds;
		 */

	}
	
	public String getDBCPConnectionPoolName(DataSource dataSource) {
		if (!(dataSource instanceof BasicDataSource)) {
			// FIXME!
			log.info("parameter dataSouce is not a BasicDataSource class instance");
			return null;
		}
		
		return basicDataSource2dbcpConnectionPoolNameHash.get(dataSource);
	}

	/*private void setupDataSource(String dbcpConnectionPoolName,
			Properties dbcpConnectionPoolConfig) {
		String connectURI = new StringBuilder(connectURIBaseString).append(
				dbcpConnectionPoolName).toString();
		//
		// First, we'll create a ConnectionFactory that the
		// pool will use to create Connections.
		// We'll use the DriverManagerConnectionFactory,
		// using the connect string passed in the command line
		// arguments.
		//
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
				connectURI, dbcpConnectionPoolConfig);

		//
		// Next we'll create the PoolableConnectionFactory, which wraps
		// the "real" Connections created by the ConnectionFactory with
		// the classes that implement the pooling functionality.
		//
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
				connectionFactory, null);

		//
		// Now we'll need a ObjectPool that serves as the
		// actual pool of connections.
		//
		// We'll use a GenericObjectPool instance, although
		// any ObjectPool implementation will suffice.
		//
		ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(
				poolableConnectionFactory);

		// Set the factory's pool property to the owning pool
		poolableConnectionFactory.setPool(connectionPool);

		//
		// Finally, we create the PoolingDriver itself,
		// passing in the object pool we created.
		//
		PoolingDataSource<PoolableConnection> dataSource = new PoolingDataSource<>(
				connectionPool);

		dbcpConnectionPoolHash.put(dbcpConnectionPoolName, dataSource);
	}
*/
	/*public void setupDriver(String dbcpConnectionPoolName,
			Properties dbcpConnectionPoolConfig) throws Exception {
		String connectURI = new StringBuilder(connectURIBaseString).append(
				dbcpConnectionPoolName).toString();
        //
        // First, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        //
        ConnectionFactory connectionFactory =
            new DriverManagerConnectionFactory(connectURI,dbcpConnectionPoolConfig);

        //
        // Next, we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        PoolableConnectionFactory poolableConnectionFactory =
            new PoolableConnectionFactory(connectionFactory, null);

        //
        // Now we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        ObjectPool<PoolableConnection> connectionPool =
            new GenericObjectPool<>(poolableConnectionFactory);

        //
        // Finally, we create the PoolingDriver itself...
        //
        Class.forName("org.apache.commons.dbcp2.PoolingDriver");
        PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");

        //
        // ...and register our pool with it.
        //
        driver.registerPool(dbcpConnectionPoolName,connectionPool);

        //
        // Now we can just use the connect string "jdbc:apache:commons:dbcp:example"
        // to access our pool of Connections.
        //
        
    }*/

	/**
	 * JDBC 연결 자원 반환한다.
	 * 
	 * @return JDBC 연결 자원
	 * @throws SQLException
	 * @throws DBNotReadyException
	 *             DB 사용 준비가 안되었을 경우 던지는 예외
	 */
	public BasicDataSource getBasicDataSource(String dbcpConnectionPoolName)
			throws DBNotReadyException {
		BasicDataSource basicDataSource = dbcpConnectionPoolName2BasicDataSourceHash
				.get(dbcpConnectionPoolName);
		if (null == basicDataSource) {
			throw new DBNotReadyException(new StringBuilder(
					"dbcp connection pool[").append(dbcpConnectionPoolName)
					.append("] not ready").toString());
		}
		
		// FIXME! 추적용
		// log.info("dbcp connection pool name={}", dbcpConnectionPoolName, new Throwable());
		
		return basicDataSource;
	}

	protected void finalize() throws Throwable {
		super.finalize();
		Enumeration<BasicDataSource> basicDataSourceEnum = dbcpConnectionPoolName2BasicDataSourceHash
				.elements();
		while (basicDataSourceEnum.hasMoreElements()) {
			BasicDataSource basicDataSource = basicDataSourceEnum.nextElement();
			try {
				if (null != basicDataSource) {
					basicDataSource.close();
				}
			} catch (Exception e) {
				log.warn("unknown error", e);
			}
		}
	}
}
