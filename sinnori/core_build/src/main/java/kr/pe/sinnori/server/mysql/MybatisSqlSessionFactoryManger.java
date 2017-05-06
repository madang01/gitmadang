package kr.pe.sinnori.server.mysql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.vo.AllDBCPPartConfiguration;
import kr.pe.sinnori.common.config.vo.ProjectPartConfiguration;
import kr.pe.sinnori.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.sinnori.common.util.CommonStaticUtil;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * <pre>
 * MyBatis SqlSessionFactory 관리자
 * 
 * MyBatis 에서 매핑 클래스란 동적 클래스 로딩된 자바 빈즈이다.
 * MyBatis 는 매핑 클래스를 인스턴스화 할때 필요한 클래스 로더를 자신을 인스턴스한 클래스 로더부터 시스템 클래스 로더까지 훝어서 찾는다.
 * 
 * MyBatis 매핑 클래스들을 개발시 빈번하게 수정되어야 하므로 신놀이 동적 클래스 로더를 통해 적재 되어야 하는 대상으로 정의했다.
 * 따라서 이 요건을 만족하기 위해서는 신놀이 동적 클래스 로더안에서 MyBatis SqlSessionFactory 를 인스턴스해서 사용해야
 * 자신을 인스턴스한 클래스로더로 신놀이 동적 클래스 로더가 지정된다.
 * 이렇게 해야 신놀이 동적 클래스 로더에 적재된 매핑 클래스를 MyBatis 에서 사용하므로 
 * "MyBatis SqlSessionFactory 관리자" 는  반듯이 신놀이 동적 클래스 로더에 적재되어야 한다.
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public class MybatisSqlSessionFactoryManger {
	private final Logger log = LoggerFactory
			.getLogger(MybatisSqlSessionFactoryManger.class);

	private MybatisConfigXMLFileSAXParser mybatisConfigXMLFileSAXParser = null;
	private Hashtable<String, SqlSessionFactory> connectionPoolName2SqlSessionFactoryHash = new Hashtable<String, SqlSessionFactory>();
	private FileTypeResourceManager fileTypeResourceManager = null;
	private File mybatisConfigeFile = null;

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자.
	 * 
	 * @throws DBCPDataSourceNotFoundException
	 */
	private MybatisSqlSessionFactoryManger() {
		SinnoriConfiguration sinnoriConfiguration = SinnoriConfigurationManager
				.getInstance().getSinnoriRunningProjectConfiguration();

		AllDBCPPartConfiguration allDBCPPart = sinnoriConfiguration
				.getAllDBCPPartConfiguration();
		ProjectPartConfiguration mainProjetPart = sinnoriConfiguration
				.getMainProjectPartConfiguration();

		String serverClassloaderMybatisConfigFileRelativePathString = mainProjetPart
				.getServerClassloaderMybatisConfigFileRelativePathString();

		String sinnoriInstalledPathString = sinnoriConfiguration
				.getSinnoriInstalledPathString();
		String mainProjectName = sinnoriConfiguration.getMainProjectName();

		String serverAPPINFResorucePathString = BuildSystemPathSupporter
				.getServerAPPINFResourcesPathString(sinnoriInstalledPathString,
						mainProjectName);

		String mybatisConfigeFilePathString = CommonStaticUtil
				.getFilePathStringFromResourcePathAndRelativePathOfFile(
						serverAPPINFResorucePathString,
						serverClassloaderMybatisConfigFileRelativePathString);

		mybatisConfigeFile = new File(mybatisConfigeFilePathString);

		if (!mybatisConfigeFile.exists()) {
			log.warn(
					"the main project's mybatis config file[{}] doesn't exist",
					mybatisConfigeFilePathString);
			mybatisConfigeFile = null;
			return;
		}

		if (!mybatisConfigeFile.isFile()) {
			log.warn(
					"the main project's mybatis config file[{}] is not a regular file",
					mybatisConfigeFilePathString);
			mybatisConfigeFile = null;
			return;
		}

		try {
			mybatisConfigXMLFileSAXParser = new MybatisConfigXMLFileSAXParser();
		} catch (SAXException e) {
			log.warn("1.SAXException", e);
			return;
		}

		try {
			fileTypeResourceManager = mybatisConfigXMLFileSAXParser
					.parse(mybatisConfigeFile);
		} catch (IllegalArgumentException | SAXException | IOException e) {
			log.warn("2.SAXException", e);

			return;
		}

		List<String> dbcpConnectionPoolNameList = allDBCPPart.getDBCPNameList();

		for (String dbcpConnectionPoolName : dbcpConnectionPoolNameList) {
			try {
				rebuildSqlSessionFactory(dbcpConnectionPoolName);
			} catch (Exception e) {
				log.warn(
						"it failed to rebuild the new SqlSessionFactory[{}] class instance reapplied mybatis config file[{}], errormessage={}",
						dbcpConnectionPoolName,
						mybatisConfigeFile.getAbsolutePath(), e.getMessage());
				continue;
			}
		}
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class SqlSessionFactoryMangerHolder {
		static final MybatisSqlSessionFactoryManger singleton = new MybatisSqlSessionFactoryManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static MybatisSqlSessionFactoryManger getInstance() {
		return SqlSessionFactoryMangerHolder.singleton;
	}

	private SqlSessionFactory rebuildSqlSessionFactory(String enviromentID)
			throws Exception {
		SqlSessionFactory sqlSessionFactory = null;

		InputStream is = null;
		try {
			is = new FileInputStream(mybatisConfigeFile);

			sqlSessionFactory = new SqlSessionFactoryBuilder().build(is,
					enviromentID);

			connectionPoolName2SqlSessionFactoryHash.put(enviromentID,
					sqlSessionFactory);
			
			log.info("successfully SqlSessionFactory[{}] was registed at connectionPoolName2SqlSessionFactoryHash", enviromentID);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (Exception e) {
					log.warn(
							"fail to close input stream of mybatis config file",
							e);
				}
			}
		}

		return sqlSessionFactory;
	}

	/**
	 * SqlSessionFactory 객체를 반환한다.
	 * 
	 * @param connectionPoolName
	 * @return
	 * @throws DBCPDataSourceNotFoundException
	 */
	public SqlSessionFactory getSqlSessionFactory(String dbcpConnectionPoolName)
			throws DBCPDataSourceNotFoundException {
		if (null == mybatisConfigeFile) {
			String errorMessage = "mybatis config file maybe be bad path "
					+ "or does not exist or not a regular file";
			log.warn(errorMessage);
			throw new DBCPDataSourceNotFoundException(errorMessage);
		}
		if (null == mybatisConfigXMLFileSAXParser) {
			String errorMessage = "it failed to create a mybatis config xml file SAXParser class instance";
			log.warn(errorMessage);
			throw new DBCPDataSourceNotFoundException(errorMessage);
		}

		synchronized (connectionPoolName2SqlSessionFactoryHash) {
			if (null == fileTypeResourceManager) {
				try {
					FileTypeResourceManager newFileTypeResourceManager = mybatisConfigXMLFileSAXParser
							.parse(mybatisConfigeFile);
					this.fileTypeResourceManager = newFileTypeResourceManager;
				} catch (Exception e) {
					String errorMessage = String
							.format("it failed to recreate a file type resource manger class instance from my batis config file[%s], errormessage=%s",
									mybatisConfigeFile.getAbsolutePath(),
									e.getMessage());
					log.warn(errorMessage, e);
					throw new DBCPDataSourceNotFoundException(errorMessage);
				}

			}

			SqlSessionFactory sqlSessionFactory = connectionPoolName2SqlSessionFactoryHash
					.get(dbcpConnectionPoolName);
			if (null == sqlSessionFactory) {
				String errorMessage = String.format(
						"dbcp connection pool[%s] is not found",
						dbcpConnectionPoolName);
				log.warn(errorMessage);
				throw new DBCPDataSourceNotFoundException(errorMessage);
			}

			if (fileTypeResourceManager.isModified()) {				
				try {
					FileTypeResourceManager newFileTypeResourceManager = mybatisConfigXMLFileSAXParser
							.parse(mybatisConfigeFile);

					sqlSessionFactory = rebuildSqlSessionFactory(dbcpConnectionPoolName);

					this.fileTypeResourceManager = newFileTypeResourceManager;
				} catch (Exception e) {
					log.warn(
							"it failed to rebuild the new SqlSessionFactory[{}] class instance reapplied mybatis config file[{}], errormessage={}",
							dbcpConnectionPoolName,
							mybatisConfigeFile.getAbsolutePath(),
							e.getMessage());
					throw new DBCPDataSourceNotFoundException(e.getMessage());
				}
			}

			return sqlSessionFactory;
		}
	}

}
