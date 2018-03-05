package kr.pe.sinnori.common.mybatis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.sinnori.common.classloader.SimpleClassLoader;
import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.itemvalue.AllDBCPPartConfiguration;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.exception.MybatisException;
import kr.pe.sinnori.common.util.CommonStaticUtil;

/**
 * MyBatis SqlSessionFactory 관리자
 *  
 * @author Won Jonghoon
 * 
 */
public class MybatisSqlSessionFactoryManger {
	private final Logger log = LoggerFactory
			.getLogger(MybatisSqlSessionFactoryManger.class);

	private MybatisConfigXMLFileSAXParser mybatisConfigXMLFileSAXParser = null;
	private HashMap<String, SqlSessionFactory> enviromentID2SqlSessionFactoryHash = new HashMap<String, SqlSessionFactory>();
	private MybatisFileTypeResourceModificationChecker currentMybatisFileTypeResourceModificationChecker = null;
	private File mybatisConfigeFile = null;
	
	private SimpleClassLoader simpleClassLoader = null;

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자.
	 * 
	 * @throws MybatisException
	 */
	private MybatisSqlSessionFactoryManger() {
		SinnoriConfiguration sinnoriConfiguration = SinnoriConfigurationManager
				.getInstance().getSinnoriRunningProjectConfiguration();

		AllDBCPPartConfiguration allDBCPPartConfiguration = sinnoriConfiguration
				.getAllDBCPPartConfiguration();
		
		{	
			String sinnoriInstalledPathString = sinnoriConfiguration
					.getSinnoriInstalledPathString();
			String mainProjectName = sinnoriConfiguration.getMainProjectName();
			
			ProjectPartConfiguration mainProjetPartConfiguration = sinnoriConfiguration
					.getMainProjectPartConfiguration();
			
			
			String serverAPPINFClassPathString = BuildSystemPathSupporter
					.getServerAPPINFClassPathString(sinnoriInstalledPathString, mainProjectName);
			String projectResourcesPathString = BuildSystemPathSupporter.getProjectResourcesPathString(sinnoriInstalledPathString, mainProjectName);
			IOPartDynamicClassNameUtil  ioPartDynamicClassNameUtil= new IOPartDynamicClassNameUtil(mainProjetPartConfiguration
					.getFirstPrefixDynamicClassFullName());
			
			this.simpleClassLoader = new SimpleClassLoader(serverAPPINFClassPathString, projectResourcesPathString, ioPartDynamicClassNameUtil);
			
			Resources.setDefaultClassLoader(simpleClassLoader);
				
			String mainProjectResorucesPathString = BuildSystemPathSupporter
					.getProjectResourcesPathString(sinnoriInstalledPathString,
							mainProjectName);
			
			String mybatisConfigeFilePathString = CommonStaticUtil
					.getFilePathStringFromResourcePathAndRelativePathOfFile(
							mainProjectResorucesPathString,
							mainProjetPartConfiguration
							.getServerMybatisConfigFileRelativePathString());
			
			mybatisConfigeFile = new File(mybatisConfigeFilePathString);
			
			if (! mybatisConfigeFile.exists()) {
				log.warn(
						"the main project's mybatis config file[{}] doesn't exist",
						mybatisConfigeFilePathString);
				mybatisConfigeFile = null;
				return;
			}

			if (! mybatisConfigeFile.isFile()) {
				log.warn(
						"the main project's mybatis config file[{}] is not a regular file",
						mybatisConfigeFilePathString);
				mybatisConfigeFile = null;
				return;
			}
		}		

		try {
			mybatisConfigXMLFileSAXParser = new MybatisConfigXMLFileSAXParser();
		} catch (SAXException e) {
			log.warn("1.SAXException", e);
			return;
		}

		try {
			currentMybatisFileTypeResourceModificationChecker = mybatisConfigXMLFileSAXParser
					.parse(mybatisConfigeFile);
		} catch (IllegalArgumentException | SAXException | IOException e) {
			log.warn("2.SAXException", e);

			return;
		}

		List<String> dbcpNameList = allDBCPPartConfiguration.getDBCPNameList();
		
		for (String dbcpName : dbcpNameList) {
			try {
				SqlSessionFactory newSqlSessionFactory = buildNewInstanceOfSqlSessionFactory(mybatisConfigeFile, dbcpName);
				enviromentID2SqlSessionFactoryHash.put(dbcpName, newSqlSessionFactory);		
				log.info("successfully SqlSessionFactory[{}] was registed", dbcpName);							
			} catch (Exception e) {
				String errorMessage = String.format("it failed to rebuild the new SqlSessionFactory[%s] class instance reapplied mybatis config file[%s], errormessage=%s",
						dbcpName,
						mybatisConfigeFile.getAbsolutePath(), e.getMessage());
				log.warn(errorMessage, e);
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

	private SqlSessionFactory buildNewInstanceOfSqlSessionFactory(File mybatisConfigeFile, String enviromentID)
			throws Exception {
		SqlSessionFactory sqlSessionFactory = null;

		InputStream is = null;
		try {
			is = new FileInputStream(mybatisConfigeFile);

			// Resources.setDefaultClassLoader(
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(is,
					enviromentID);			
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

	private SqlSessionFactory renewSqlSessionFactory(String enviromentID)
			throws MybatisException {
		SqlSessionFactory newSqlSessionFactory = null;
		try {
			MybatisFileTypeResourceModificationChecker newFileTypeResourceManager = mybatisConfigXMLFileSAXParser
					.parse(mybatisConfigeFile);
			
			newSqlSessionFactory = buildNewInstanceOfSqlSessionFactory(mybatisConfigeFile, enviromentID);
			
				
			enviromentID2SqlSessionFactoryHash.put(enviromentID, newSqlSessionFactory);
			
			
			this.currentMybatisFileTypeResourceModificationChecker = newFileTypeResourceManager;
			
			log.info("successfully SqlSessionFactory[{}] was renewed", enviromentID);
		} catch (Exception e) {
			String errorMessage = String
					.format("It failed to rebuild the new SqlSessionFactory[%s] class instance reapplied mybatis config file[%s], errormessage=%s",
					enviromentID,
					mybatisConfigeFile.getAbsolutePath(),
					e.getMessage());
			log.warn(errorMessage, e);
			throw new MybatisException(errorMessage);
		}
		return newSqlSessionFactory;
	}

	/**
	 * SqlSessionFactory 객체를 반환한다.
	 * 
	 * @param enviromentID
	 * @return
	 * @throws MybatisException
	 */
	public SqlSessionFactory getSqlSessionFactory(String enviromentID)	
			throws MybatisException {
		if (null == enviromentID) {
			throw new IllegalArgumentException("the parameter enviromentID is null");
		}
		
		if (null == mybatisConfigeFile) {
			String errorMessage = "mybatis config file maybe be bad path "
					+ "or does not exist or not a regular file";
			log.warn(errorMessage);
			throw new MybatisException(errorMessage);
		}
		if (null == mybatisConfigXMLFileSAXParser) {
			String errorMessage = "it failed to create a mybatis config xml file SAXParser class instance";
			log.warn(errorMessage);
			throw new MybatisException(errorMessage);
		}

		synchronized (enviromentID2SqlSessionFactoryHash) {
			if (null == currentMybatisFileTypeResourceModificationChecker) {
				try {
					this.currentMybatisFileTypeResourceModificationChecker = mybatisConfigXMLFileSAXParser
							.parse(mybatisConfigeFile);
				} catch (Exception e) {
					String errorMessage = String
							.format("it failed to recreate a file type resource manger class instance from my batis config file[%s], errormessage=%s",
									mybatisConfigeFile.getAbsolutePath(),
									e.getMessage());
					log.warn(errorMessage, e);
					throw new MybatisException(errorMessage);
				}
			}
			
			SqlSessionFactory sqlSessionFactory = enviromentID2SqlSessionFactoryHash
					.get(enviromentID);
			if (null == sqlSessionFactory) {
				String errorMessage = String.format(
						"dbcp connection pool[%s] is not found",
						enviromentID);
				log.warn(errorMessage);
				throw new MybatisException(errorMessage);
			}

			if (currentMybatisFileTypeResourceModificationChecker.isModified()) {
				sqlSessionFactory = renewSqlSessionFactory(enviromentID);
			}

			return sqlSessionFactory;
		}
	}
}
