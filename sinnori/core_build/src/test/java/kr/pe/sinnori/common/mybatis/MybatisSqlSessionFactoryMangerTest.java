package kr.pe.sinnori.common.mybatis;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import kr.pe.sinnori.common.buildsystem.BuildSystemFileContents;
import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.buildsystem.ProjectBuilder;
import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.exception.MybatisException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.common.util.SequencedPropertiesUtil;

public class MybatisSqlSessionFactoryMangerTest {
	Logger log = null;	
	
	private String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
	private String mainProjectName = "sample_test";
	private String serverMybatisConfigFileRelativePathString = "mybatis/mybatisConfig.xml";
	private File mybatisConfigeFile = getMybatisConfigFile(serverMybatisConfigFileRelativePathString);
	
	
	@Before
	public void setup() {
		
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		
		/*System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				BuildSystemPathSupporter.getLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName));
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH,
				BuildSystemPathSupporter.getLogPathString(sinnoriInstalledPathString, mainProjectName, LOG_TYPE.SERVER));*/
		
		/** Logback 로그 경로 지정에 비 의존하기 위해서 셋업 */
		SinnoriLogbackManger.getInstance().setup();
		
		log = LoggerFactory.getLogger(MybatisConfigSAXParserTest.class);
		
		boolean isServer = true;
		boolean isAppClient = true;
		boolean isWebClient = true;
		String servletSystemLibraryPathString = "D:\\apache-tomcat-8.5.11\\lib";
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(sinnoriInstalledPathString, mainProjectName);			

			if (projectBuilder.whetherOnlyProjectPathExists()) {
				projectBuilder.dropProject();
			}
			
			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);			
		} catch (BuildSystemException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		
		
		final String defaultDBCPName = "sample_base_db";
		List<MybatisEnviroment> mybatisEnviromentList = new ArrayList<MybatisEnviroment>();
		mybatisEnviromentList.add(new MybatisEnviroment(
				sinnoriInstalledPathString, mainProjectName, defaultDBCPName, "kr.pe.sinnori.common.mybatis.SampleBaseDBDataSourceFactory"));
		
		List<MybatisMapper> mybatisMapperList = new ArrayList<MybatisMapper>();
		mybatisMapperList.add(new MybatisMapper(sinnoriInstalledPathString, mainProjectName, 
				"mybatis/mybatisMapper.xml"));
			
		String mybatisConfigFileContents = BuildSystemFileContents.getMybatisConfigFileContents(sinnoriInstalledPathString, defaultDBCPName,
				mybatisEnviromentList,
				mybatisMapperList);
		
		try {
			CommonStaticUtil.saveFile(mybatisConfigeFile, mybatisConfigFileContents, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		
		for (MybatisMapper mybatisMapper : mybatisMapperList) {
			File mybatisMapperFile = mybatisMapper.getMybatisMapperFile();
			
			String emptyMybatisMapperFileContents = BuildSystemFileContents
					.getSample1MybatisMapperFileContents(sinnoriInstalledPathString, "kr.pr.sinnori.samplebase");
			
			try {
				CommonStaticUtil.saveFile(mybatisMapperFile, emptyMybatisMapperFileContents, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}
		}
		 
		createDBCPConfigFIle(sinnoriInstalledPathString, mainProjectName, defaultDBCPName);
		
		
		SinnoriConfiguration sinnoriConfiguration = 
				SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		
		SequencedProperties sinnoriConfigSequencedProperties = sinnoriConfiguration.getSinnoriConfigurationSequencedPropties();
		
		
		sinnoriConfigSequencedProperties.setProperty
		("mainproject."+ItemIDDefiner.ProjectPartItemIDDefiner.SERVER_MYBATIS_CONFIG_FILE_RELATIVE_PATH_STRING_ITEMID, 
				serverMybatisConfigFileRelativePathString);
		
		StringBuilder dbcpNameListStringBuilder = new StringBuilder();
		for (MybatisEnviroment mybatisEnviroment : mybatisEnviromentList) {
			if (!dbcpNameListStringBuilder.toString().equals("")) {
				dbcpNameListStringBuilder.append(", ");
			}			
			dbcpNameListStringBuilder.append(mybatisEnviroment.getDBCPName());
		}
		sinnoriConfigSequencedProperties.setProperty(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING, dbcpNameListStringBuilder.toString());
		for (MybatisEnviroment mybatisEnviroment : mybatisEnviromentList) {
			String dbcpConfigFileItemKey = 
					new StringBuilder("dbcp.").append(mybatisEnviroment.getDBCPName())
					.append(".").append(ItemIDDefiner.DBCPPartItemIDDefiner.DBCP_CONFIGE_FILE_ITEMID).toString();
			
			sinnoriConfigSequencedProperties.setProperty(dbcpConfigFileItemKey, mybatisEnviroment.getDBCPConfigFilePathString());
		}
		
		try {
			sinnoriConfiguration.applyModifiedSinnoriConfigSequencedProperties();
		} catch (IOException | SinnoriConfigurationException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	private File getMybatisConfigFile(String serverMybatisConfigFileRelativePathString) {
		File mybatisConfigeFile  = null;
		{			
			String mainProjectResorucesPathString = BuildSystemPathSupporter
					.getProjectResourcesPathString(sinnoriInstalledPathString, mainProjectName);						
			
			String mybatisConfigeFilePathString = CommonStaticUtil
					.getFilePathStringFromResourcePathAndRelativePathOfFile(
							mainProjectResorucesPathString,
							serverMybatisConfigFileRelativePathString);			
			mybatisConfigeFile = new File(mybatisConfigeFilePathString);		
		}
		return mybatisConfigeFile;
	}
	
	private void createDBCPConfigFIle(String sinnoriInstalledPathString, 
			String mainProjectName, final String dbcpName) {
		String dbcpSampleConfigFilePathString = BuildSystemPathSupporter.getDBCPConfigFilePathString(sinnoriInstalledPathString, mainProjectName, dbcpName); 

		
		File dbcpSampleConfigFile = new File(dbcpSampleConfigFilePathString);
		
		SequencedProperties dbcpSampleConfig = new SequencedProperties();
		dbcpSampleConfig.setProperty("driver", "com.mysql.jdbc.Driver");
		dbcpSampleConfig.setProperty("url", "jdbc:mysql://172.30.1.15:3306/SB_DB");
		dbcpSampleConfig.setProperty("username", "madangsoe01");
		dbcpSampleConfig.setProperty("password", "test1234");
		dbcpSampleConfig.setProperty("initialSize", "3");
		dbcpSampleConfig.setProperty("maxActive", "3");
		dbcpSampleConfig.setProperty("maxIdle", "3");
		dbcpSampleConfig.setProperty("minIdle", "0");
		dbcpSampleConfig.setProperty("maxWait", "1000");
		dbcpSampleConfig.setProperty("defaultAutoCommit", "false");
		dbcpSampleConfig.setProperty("validationQuery", "select 1");
		
		// dbcpSampleConfig.store(out, comments);
		try {
			if (dbcpSampleConfigFile.exists()) {
					SequencedPropertiesUtil.overwriteSequencedPropertiesFile(dbcpSampleConfig, "dbcp configuration file", 
							dbcpSampleConfigFilePathString, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			} else {
				SequencedPropertiesUtil.createNewSequencedPropertiesFile(dbcpSampleConfig, "dbcp configuration file", 
						dbcpSampleConfigFilePathString, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			}
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		// return dbcpSampleConfigFilePathString;
	}
	
	@Test
	public void testGetSqlSessionFactory() {
		MybatisSqlSessionFactoryManger mybatisSqlSessionFactoryManger = MybatisSqlSessionFactoryManger.getInstance();
		
		String enviromentID = "sample_base_db";
		SqlSessionFactory sqlSessionFactory = null;
		try {
			sqlSessionFactory = mybatisSqlSessionFactoryManger.getSqlSessionFactory(enviromentID);
		} catch (MybatisException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		int oldHashCode = sqlSessionFactory.hashCode();
		

		SqlSession sqlSession = null;
		try {
			sqlSession = sqlSessionFactory.openSession(false);
			sqlSession.commit();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		} finally {
			if (null != sqlSession) {
				sqlSession.close();
			}
		}
		
		FileTypeResourceManager fileTypeResourceManger = null;
		
		MybatisConfigXMLFileSAXParser mybatisConfigSAXParser = null;
		try {
			mybatisConfigSAXParser = new MybatisConfigXMLFileSAXParser();
		} catch (SAXException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}	
		try {
			fileTypeResourceManger = mybatisConfigSAXParser.parse(mybatisConfigeFile);
		} catch (IllegalArgumentException | SAXException | IOException e) {
			log.warn("2.SAXException", e);
			
			fail(e.getMessage());
		}
		
		List<File> mapperFileList = fileTypeResourceManger.getMapperFileList();
		
		if (0 == mapperFileList.size()) {
			fail("mapper file list size is zero");
		}
		
		File targetFile = null;
		if (1 == mapperFileList.size()) {
			targetFile =  mapperFileList.get(0);
		} else {
			Random random = new Random();
			int targetIndex = random.nextInt(mapperFileList.size()-1);
			targetFile =  mapperFileList.get(targetIndex);
		}
		
		boolean modifiedResult = targetFile.setLastModified(new Date().getTime());
		if (!modifiedResult) {
			String errorMessage = String.format("fali to set last modified time of the mapper file[%s]", 
					targetFile.getAbsolutePath());
			fail(errorMessage);
		}
		
		try {
			sqlSessionFactory = mybatisSqlSessionFactoryManger.getSqlSessionFactory(enviromentID);
		} catch (MybatisException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		int newHashCode = sqlSessionFactory.hashCode();
		
		log.info("oldHashCode=[{}], newHashCode=[{}]", oldHashCode, newHashCode);
		
	}
}
