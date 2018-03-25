package kr.pe.sinnori.common.mybatis;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
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
import kr.pe.sinnori.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.sinnori.common.classloader.SimpleClassLoader;
import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDDefiner;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.exception.MybatisException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.type.LogType;
import kr.pe.sinnori.common.type.MybatisMapperType;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.common.util.SequencedPropertiesUtil;

public class MybatisSqlSessionFactoryMangerTest {
	Logger log = null;	
	
	private final String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
	private String mainProjectName = "sample_test";
	private final String serverMybatisConfigFileRelativePathString = "mybatis/mybatisConfig.xml";
	private File mybatisConfigeFile = null;	
	
	
	@Before
	public void setup() {
		File sinnoriInstalledPath = new File(sinnoriInstalledPathString);
		if (! sinnoriInstalledPath.exists()) {
			String errorMessage = String.format("the sinnori installed path[%s] doesn't exist", sinnoriInstalledPathString);			
			fail(errorMessage);
		}

		if (! sinnoriInstalledPath.isDirectory()) {
			String errorMessage = String.format("the sinnori installed path[%s] is not a directory", sinnoriInstalledPathString);
			fail(errorMessage);
		}	
		
		
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		
		/** Logback 로그 경로 지정에 비 의존하기 위해서 셋업 */	
		LogType logType = LogType.SERVER;
		SinnoriLogbackManger.getInstance().setup(sinnoriInstalledPathString, "sample_base", logType);
		
		log = LoggerFactory.getLogger(MybatisConfigSAXParserTest.class);
		
		
		mybatisConfigeFile = getMybatisConfigFile(serverMybatisConfigFileRelativePathString);
		
		boolean isServer = true;
		boolean isAppClient = true;
		boolean isWebClient = true;
		String servletSystemLibraryPathString = "D:\\apache-tomcat-8.5.15\\lib";
		
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
		
		
		String mybatisConfigDTDFilePathString = BuildSystemPathSupporter.getMybatisConfigDTDFilePathString(sinnoriInstalledPathString);
		final String defaultDBCPName = "sample_base_db";
		String dbcpConfigFilePathString = BuildSystemPathSupporter.getDBCPConfigFilePathString(sinnoriInstalledPathString, mainProjectName, defaultDBCPName);
		
		List<MybatisEnviroment> mybatisEnviromentList = new ArrayList<MybatisEnviroment>();
		
		
		
		mybatisEnviromentList.add(new MybatisEnviroment(defaultDBCPName, dbcpConfigFilePathString, "kr.pe.sinnori.common.mybatis.SampleBaseDBDataSourceFactory"));
		
		List<MybatisFileTypeMapper> mybatisMapperList = new ArrayList<MybatisFileTypeMapper>();
		mybatisMapperList.add(new MybatisFileTypeMapper(MybatisMapperType.RESOURCE, 
				sinnoriInstalledPathString, mainProjectName, 
				"mybatis/mybatisMapper.xml"));
		
		
		{
			String mainProjectResorucesPathString = BuildSystemPathSupporter.getProjectResourcesPathString(sinnoriInstalledPathString, mainProjectName);
			String mybatisMapperFilePathString = CommonStaticUtil
					.getFilePathStringFromResourcePathAndRelativePathOfFile(mainProjectResorucesPathString, "mybatis/mybatisMapper2.xml");
			
			File urlTypeMapperFile = new File(mybatisMapperFilePathString);
			
			String mapperTypeValue = null;
			
			try {
				mapperTypeValue = urlTypeMapperFile.toURI().toURL().toString();
			} catch (MalformedURLException e) {
				log.warn("fail to get a url string", e);
			}
			
			mybatisMapperList.add(new MybatisFileTypeMapper(MybatisMapperType.URL, 
					sinnoriInstalledPathString, mainProjectName, 
					mapperTypeValue));
		}
		
		
			
		String mybatisConfigFileContents = BuildSystemFileContents.getMybatisConfigFileContents(mybatisConfigDTDFilePathString, defaultDBCPName,
				mybatisEnviromentList,
				mybatisMapperList);
		
		try {
			CommonStaticUtil.saveFile(mybatisConfigeFile, mybatisConfigFileContents, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		
		for (MybatisFileTypeMapper mybatisMapper : mybatisMapperList) {
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
	
	private SimpleClassLoader getSimpleClassLoader() throws SinnoriConfigurationException {
		SinnoriConfiguration sinnoriRunningProjectConfiguration =  SinnoriConfigurationManager.getInstance().getSinnoriRunningProjectConfiguration();
		
		String mainProjectName = sinnoriRunningProjectConfiguration.getMainProjectName();
		String sinnoriInstalledPathString = sinnoriRunningProjectConfiguration.getSinnoriInstalledPathString();
		
		
		String serverAPPINFClassPathString = BuildSystemPathSupporter
				.getServerAPPINFClassPathString(sinnoriInstalledPathString, mainProjectName);
		
		File serverAPPINFClassPath = new File(serverAPPINFClassPathString);
		
		if (!serverAPPINFClassPath.exists()) {
			String errorMessage = String.format("the server APP-INF class path[%s] doesn't exist", serverAPPINFClassPathString);
		 	throw new SinnoriConfigurationException(errorMessage);
		}
		
		if (!serverAPPINFClassPath.isDirectory()) {
			String errorMessage = String.format("the server APP-INF class path[%s] isn't a directory", serverAPPINFClassPathString);
		 	throw new SinnoriConfigurationException(errorMessage);
		}
		
		String projectResourcesPathString = BuildSystemPathSupporter.getProjectResourcesPathString(sinnoriInstalledPathString, mainProjectName);
		
		File projectResourcesPath = new File(projectResourcesPathString);
		
		if (! projectResourcesPath.exists()) {
			String errorMessage = String.format("the project resources path[%s] doesn't exist", projectResourcesPathString);
		 	throw new SinnoriConfigurationException(errorMessage);
		}
		
		if (! projectResourcesPath.isDirectory()) {
			String errorMessage = String.format("the project resources path[%s] isn't a directory", projectResourcesPathString);
		 	throw new SinnoriConfigurationException(errorMessage);
		}
		
		ProjectPartConfiguration mainProjectPart = sinnoriRunningProjectConfiguration.getMainProjectPartConfiguration();
		
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil(mainProjectPart
				.getFirstPrefixDynamicClassFullName());
		
		return new SimpleClassLoader(serverAPPINFClassPathString, projectResourcesPathString, ioPartDynamicClassNameUtil);
	}
	
	@Test
	public void testGetSqlSessionFactory_파일타입리로스수정시새롭게갱신되는지테스트() {
		SimpleClassLoader simpleClassLoader = null;
		try {
			simpleClassLoader = getSimpleClassLoader();
		} catch (SinnoriConfigurationException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		
		MybatisSqlSessionFactoryManger mybatisSqlSessionFactoryManger = MybatisSqlSessionFactoryManger.getInstance();
		
		String enviromentID = "sample_base_db";
		SqlSessionFactory sqlSessionFactory = null;
		try {
			sqlSessionFactory = mybatisSqlSessionFactoryManger.getSqlSessionFactory(simpleClassLoader, enviromentID);
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
		
		MybatisParsingInformationForModification fileTypeResourceManger = null;
		
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
			sqlSessionFactory = mybatisSqlSessionFactoryManger.getSqlSessionFactory(simpleClassLoader, enviromentID);
		} catch (MybatisException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		int newHashCode = sqlSessionFactory.hashCode();
		
		if (oldHashCode == newHashCode) {
			fail("no SqlSessionFactory renewal");
		}
		
		// log.info("oldHashCode=[{}], newHashCode=[{}]", oldHashCode, newHashCode);
		
	}
}
