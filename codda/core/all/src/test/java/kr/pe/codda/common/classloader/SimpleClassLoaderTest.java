package kr.pe.codda.common.classloader;

import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.buildsystem.pathsupporter.CommonBuildSytemPathSupporter;

public class SimpleClassLoaderTest extends AbstractJunitTest {
	
	@Test
	public void testGetResourceAsStream() {
		
		String classloaderClassPathString = "temp";
		String classloaderReousrcesPathString = CommonBuildSytemPathSupporter.getCommonResourcesPathString(installedPath.getAbsolutePath());
		ServerSystemClassLoaderClassManager serverSystemClassLoaderClassManager = new ServerSystemClassLoaderClassManager();
		
		SimpleClassLoader simpleClassLoader = new SimpleClassLoader(classloaderClassPathString, classloaderReousrcesPathString, serverSystemClassLoaderClassManager);
		
		InputStream  resoruceInputStream = simpleClassLoader.getResourceAsStream("message_info/Echo.xml");
		if (null == resoruceInputStream) {
			fail("the returned value is null");
		}
	}
}
