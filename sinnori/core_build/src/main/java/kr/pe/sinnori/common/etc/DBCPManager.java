package kr.pe.sinnori.common.etc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.itemvalue.AllDBCPPartConfiguration;
import kr.pe.sinnori.common.config.itemvalue.DBCPParConfiguration;
import kr.pe.sinnori.common.exception.DBCPDataSourceNotFoundException;

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
	private Hashtable<String, BasicDataSource> dbcpName2BasicDataSourceHash = new Hashtable<String, BasicDataSource>();
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
		SinnoriConfiguration sinnoriRunningProjectConfiguration = 
				SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		
		AllDBCPPartConfiguration allDBCPPart =  sinnoriRunningProjectConfiguration.getAllDBCPPartConfiguration();
		
		List<String> dbcpNameList = allDBCPPart.getDBCPNameList();		

		for (String dbcpName : dbcpNameList) {			
			DBCPParConfiguration dbcpPart = allDBCPPart.getDBCPPartConfiguration(dbcpName);
			if (null == dbcpPart) {
				log.warn("the dbcp name[{}] is bad, check dbcp part of config file", dbcpName);
				continue;
			}
			File dbcpConfigFile = dbcpPart.getDBCPConfigFile();			

			Properties dbcpConnectionPoolConfig = new Properties();
			FileInputStream fis = null;
			InputStreamReader isr = null;
			try {
				fis = new FileInputStream(dbcpConfigFile);
				isr = new InputStreamReader(fis, "UTF-8");
				dbcpConnectionPoolConfig.load(isr);				
			} catch (Exception e) {
				log.warn(
						"when dbcp connection pool[{}]'s config file[{}] io error, errormessage=",
						dbcpName, dbcpConfigFile.getAbsolutePath(), e.getMessage());
				continue;
			} finally {
				if (null != isr) {
					try {
						isr.close();
					} catch (Exception e) {
						log.warn(String.format("fail to close the dbcp[%s] properties file's input stream reader", dbcpName), e);
					}
				}
				
				if (null != fis) {
					try {
						fis.close();
					} catch (Exception e) {
						log.warn(String.format("fail to close the dbcp[%s] properties file's file input stream", dbcpName), e);
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
			
			dbcpName2BasicDataSourceHash.put(dbcpName, basicDataSource);
			basicDataSource2dbcpConnectionPoolNameHash.put(basicDataSource, dbcpName);

			// FIXME!
			log.info("dbcp[{}] was registed successfully", dbcpName);			
		}
	}
	
	public String getDBCPConnectionPoolName(DataSource dataSource) {
		if (!(dataSource instanceof BasicDataSource)) {
			String classNameOfTheParamterDataSource = dataSource.getClass().getName();
			log.warn("the parameter dataSouce[{}] is not a BasicDataSource class instance", classNameOfTheParamterDataSource);
			throw new IllegalArgumentException(String.format("the paramter dataSource[%s] is not a BasicDataSource class instance", 
					classNameOfTheParamterDataSource));
		}
		
		return basicDataSource2dbcpConnectionPoolNameHash.get(dataSource);
	}	

	/**
	 * JDBC 연결 자원 반환한다.
	 * 
	 * @return JDBC 연결 자원
	 * @throws SQLException
	 * @throws DBCPDataSourceNotFoundException
	 *             DB 사용 준비가 안되었을 경우 던지는 예외
	 */
	public BasicDataSource getBasicDataSource(String dbcpName)
			throws DBCPDataSourceNotFoundException {
		BasicDataSource basicDataSource = dbcpName2BasicDataSourceHash
				.get(dbcpName);
		if (null == basicDataSource) {
			throw new DBCPDataSourceNotFoundException(new StringBuilder(
					"dbcp connection pool[").append(dbcpName)
					.append("] not ready").toString());
		}
		
		return basicDataSource;
	}

	
	public void closeAllDataSource() {
		Enumeration<BasicDataSource> basicDataSourceEnum = dbcpName2BasicDataSourceHash
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
	
	protected void finalize() throws Throwable {
		closeAllDataSource();
	}
}
