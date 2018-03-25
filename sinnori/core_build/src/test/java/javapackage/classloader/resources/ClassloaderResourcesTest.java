package javapackage.classloader.resources;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.sinnori.common.classloader.SimpleClassLoader;
import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;

public class ClassloaderResourcesTest extends AbstractJunitTest {
	
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
	public void classpathResourcesTest() {
		SimpleClassLoader simpleClassLoader = null;
		try {
			simpleClassLoader = getSimpleClassLoader();
		} catch (SinnoriConfigurationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		InputStream is =  simpleClassLoader
				.getResourceAsStream("mybatis/mybatisConfig.xml");
		if (null == is) {
			fail("fail to get reousrces of system classpath");
		}
		try {
			is.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		/*try {		
				@SuppressWarnings("unused")
				SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance()
				.getSqlSessionFactory(simpleClassLoader, "sample_base_db");
		} catch (MybatisException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		*/
		 
		System.out.println("success");
	}
	
	
}
