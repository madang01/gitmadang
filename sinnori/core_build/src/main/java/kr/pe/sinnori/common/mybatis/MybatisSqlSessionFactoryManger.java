package kr.pe.sinnori.common.mybatis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.vo.AllDBCPPartConfiguration;
import kr.pe.sinnori.common.config.vo.ProjectPartConfiguration;
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
	private Hashtable<String, SqlSessionFactory> enviromentID2SqlSessionFactoryHash = new Hashtable<String, SqlSessionFactory>();
	private FileTypeResourceManager fileTypeResourceManager = null;
	private File mybatisConfigeFile = null;

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
		ProjectPartConfiguration mainProjetPartConfiguration = sinnoriConfiguration
				.getMainProjectPartConfiguration();

		String serverMybatisConfigFileRelativePathString = mainProjetPartConfiguration
				.getServerMybatisConfigFileRelativePathString();

		String sinnoriInstalledPathString = sinnoriConfiguration
				.getSinnoriInstalledPathString();
		String mainProjectName = sinnoriConfiguration.getMainProjectName();

		String mainProjectResorucesPathString = BuildSystemPathSupporter
				.getProjectResourcesPathString(sinnoriInstalledPathString,
						mainProjectName);

		String mybatisConfigeFilePathString = CommonStaticUtil
				.getFilePathStringFromResourcePathAndRelativePathOfFile(
						mainProjectResorucesPathString,
						serverMybatisConfigFileRelativePathString);

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

		List<String> dbcpNameList = allDBCPPartConfiguration.getDBCPNameList();

		for (String dbcpName : dbcpNameList) {
			try {
				SqlSessionFactory newSqlSessionFactory = getNewInstanceOfSqlSessionFactory(dbcpName);
				registerNewSqlSessionFactory(dbcpName, newSqlSessionFactory);								
			} catch (Exception e) {
				log.warn(
						"it failed to rebuild the new SqlSessionFactory[{}] class instance reapplied mybatis config file[{}], errormessage={}",
						dbcpName,
						mybatisConfigeFile.getAbsolutePath(), e.getMessage());
				continue;
			}
		}
	}

	
	private void registerNewSqlSessionFactory(String dbcpName, SqlSessionFactory newSqlSessionFactory) throws Exception {		
		enviromentID2SqlSessionFactoryHash.put(dbcpName, newSqlSessionFactory);		
		log.info("successfully SqlSessionFactory[{}] was registed", dbcpName);
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

	private SqlSessionFactory getNewInstanceOfSqlSessionFactory(String enviromentID)
			throws Exception {
		SqlSessionFactory sqlSessionFactory = null;

		InputStream is = null;
		try {
			is = new FileInputStream(mybatisConfigeFile);

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

			if (fileTypeResourceManager.isModified()) {				
				try {
					sqlSessionFactory = rebuildSqlSessionFactory(enviromentID);
				} catch (Exception e) {
					String errorMessage = String
							.format("It failed to rebuild the new SqlSessionFactory[%s] class instance reapplied mybatis config file[%s], errormessage=%s",
							enviromentID,
							mybatisConfigeFile.getAbsolutePath(),
							e.getMessage());
					log.warn(errorMessage, e);
					throw new MybatisException(errorMessage);
				}
			}

			return sqlSessionFactory;
		}
	}

	/**
	 * <pre>
	 * 마이베티스 설정 관련 파일들이 변경될 경우 로직은 크게 3부분으로 구성된다.
	 * 
	 * 첫번째 변경된 마이베티스 설정 파일의 파일 타입 리소스 관리자 얻기
	 * 두번째 두번째 변경된 마이베티스 설정 파일의 SqlSessionFactory 얻은후 등록
	 * 마지막 세번째는 구 파일 타입 리소스 관리자를 변경된 마이베티스 설정 파일의 파일 타입 리소스 관리자로 교체
	 * 
	 * 변경된 마이베티스 설정 파일의 파일 타입 리소스 관리자 얻기를
	 * 두번째 변경된 마이베티스 설정 파일의 SqlSessionFactory 얻은후 등록보다 선행하는 이유는
	 * 마이베티스 설정 파일에 대한 신놀이 제약 사항을 먼저 점검하기 위함이다.
	 * </pre>
	 * 
	 * @param enviromentID dbcp 이름과 1:1 매치되는 mybatis enviromentID
	 * @return 변경된 설정 파일들을 반영한 신규 SqlSessionFactory 인스턴스
	 * @throws Exception 에러 발생시 던지는 예외
	 */
	private SqlSessionFactory rebuildSqlSessionFactory(String enviromentID)
			throws Exception {		
		/** 첫번째 변경된 마이베티스 설정 파일의 파일 타입 리소스 관리자 얻기  */
		FileTypeResourceManager newFileTypeResourceManager = mybatisConfigXMLFileSAXParser
				.parse(mybatisConfigeFile);
		
		/** 두번째 변경된 마이베티스 설정 파일의 SqlSessionFactory 얻은후 등록  */
		SqlSessionFactory newSqlSessionFactory = getNewInstanceOfSqlSessionFactory(enviromentID);
		registerNewSqlSessionFactory(enviromentID, newSqlSessionFactory);
							
		/** 마지막 세번째는 구 파일 타입 리소스 관리자를 변경된 마이베티스 설정 파일의 파일 타입 리소스 관리자로 교체 */
		this.fileTypeResourceManager = newFileTypeResourceManager;
		
		log.info("successfully SqlSessionFactory[{}] was modifyed", enviromentID);
		return newSqlSessionFactory;
	}

}
