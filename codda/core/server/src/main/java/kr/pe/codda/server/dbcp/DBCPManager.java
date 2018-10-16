package kr.pe.codda.server.dbcp;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.AllDBCPPartConfiguration;
import kr.pe.codda.common.config.subset.DBCPParConfiguration;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

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
	private InternalLogger log = InternalLoggerFactory.getInstance(DBCPManager.class);
	private ConcurrentHashMap<String, BasicDataSource> dbcpName2BasicDataSourceHash = new ConcurrentHashMap<String, BasicDataSource>();
	private ConcurrentHashMap<BasicDataSource, String> basicDataSource2dbcpConnectionPoolNameHash = new ConcurrentHashMap<BasicDataSource, String>();

	private List<String> dbcpNameList = null;
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class DBCPManagerHolder {
		static final DBCPManager singleton = new DBCPManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static DBCPManager getInstance() {
		return DBCPManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private DBCPManager() {
		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();

		AllDBCPPartConfiguration allDBCPPart = runningProjectConfiguration.getAllDBCPPartConfiguration();

		dbcpNameList = allDBCPPart.getDBCPNameList();

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
				log.warn("when dbcp connection pool[{}]'s config file[{}] io error, errormessage=", dbcpName,
						dbcpConfigFile.getAbsolutePath(), e.getMessage());
				continue;
			} finally {
				if (null != isr) {
					try {
						isr.close();
					} catch (Exception e) {
						log.warn(String.format("fail to close the dbcp[%s] properties file's input stream reader",
								dbcpName), e);
					}
				}

				if (null != fis) {
					try {
						fis.close();
					} catch (Exception e) {
						log.warn(String.format("fail to close the dbcp[%s] properties file's file input stream",
								dbcpName), e);
					}
				}
			}

			String driverClassName = dbcpConnectionPoolConfig.getProperty("driver");
			if (null == driverClassName) {
				dbcpConnectionPoolConfig.setProperty("password", "");
				
				log.warn("dbcp connection pool[{}]'s JDBC Driver name is null, dbcpConnectionPoolConfig={}", dbcpName,
						dbcpConnectionPoolConfig.toString());
				continue;
			}

			try {
				Class.forName(driverClassName);
			} catch (ClassNotFoundException e) {
				dbcpConnectionPoolConfig.setProperty("password", "");
				
				log.warn("dbcp connection pool[{}]'s JDBC Driver[{}] not exist, dbcpConnectionPoolConfig={}", dbcpName,
						driverClassName, dbcpConnectionPoolConfig.toString());
				continue;
			}

			BasicDataSource basicDataSource = null;
			try {
				basicDataSource = BasicDataSourceFactory.createDataSource(dbcpConnectionPoolConfig);
			} catch (Exception e) {
				dbcpConnectionPoolConfig.setProperty("password", "");
				
				log.warn("dbcp connection pool[{}] fail to create data source, dbcpConnectionPoolConfig={}", dbcpName,
						dbcpConnectionPoolConfig.toString());
				continue;
			}		

			dbcpName2BasicDataSourceHash.put(dbcpName, basicDataSource);
			basicDataSource2dbcpConnectionPoolNameHash.put(basicDataSource, dbcpName);

			log.info("successfully dbcp[{}] was registed", dbcpName);
		}
	}

	public String getDBCPConnectionPoolName(DataSource dataSource) {
		if (!(dataSource instanceof BasicDataSource)) {
			String classNameOfTheParamterDataSource = dataSource.getClass().getName();
			log.warn("the parameter dataSouce[{}] is not a BasicDataSource class instance",
					classNameOfTheParamterDataSource);
			throw new IllegalArgumentException(
					String.format("the paramter dataSource[%s] is not a BasicDataSource class instance",
							classNameOfTheParamterDataSource));
		}

		return basicDataSource2dbcpConnectionPoolNameHash.get(dataSource);
	}

	
	/**
	 * 파라미터 dbcpName 로 지정한 이름을 갖는 dbcp 를 반환한다.
	 * @param dbcpName dbcp 이름
	 * @return dbcp(=data base connection pool)
	 * @throws IllegalArgumentException the parameter dbcpName is null or the parameter dbcpName is not a element of the dbcp name list of config file
	 * @throws DBCPDataSourceNotFoundException the dbcp name list of config file is empty or it failed to create a dbcp connection pool having the dbcp name that is the parameter dbcpName
	 */
	public BasicDataSource getBasicDataSource(String dbcpName) throws IllegalArgumentException, DBCPDataSourceNotFoundException {
		if (null == dbcpName) {
			throw new IllegalArgumentException("the parameter dbcpName is null");
		}
		
		if (dbcpNameList.isEmpty()) {
			throw new DBCPDataSourceNotFoundException("the dbcp name list of config file is empty");
		}
		
		if (! dbcpNameList.contains(dbcpName)) {
			throw new IllegalArgumentException(new StringBuilder("the parameter dbcpName[")
			.append(dbcpName)
			.append("] is bad, it is a element of the dbcp name list of config file").toString());
		}		
				
		BasicDataSource basicDataSource = dbcpName2BasicDataSourceHash.get(dbcpName);
		
		if (null == basicDataSource) {
			throw new DBCPDataSourceNotFoundException(
					new StringBuilder("it failed to create a dbcp connection pool having dbcp name[")
					.append(dbcpName).append("]").toString());
		}

		return basicDataSource;
	}

	public void closeAllDataSource() {
		Enumeration<BasicDataSource> basicDataSourceEnum = dbcpName2BasicDataSourceHash.elements();
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
