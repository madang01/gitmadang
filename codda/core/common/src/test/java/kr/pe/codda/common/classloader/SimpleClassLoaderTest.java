package kr.pe.codda.common.classloader;

import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.buildsystem.BuildSystemPathSupporter;

public class SimpleClassLoaderTest extends AbstractJunitTest {
	
	@Test
	public void testGetResourceAsStream() {
		String firstPrefixDynamicClassFullName = "kr.pe.sinnori.impl.";
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil(firstPrefixDynamicClassFullName);
		
		String classloaderClassPathString = "temp";
		String classloaderReousrcesPathString = BuildSystemPathSupporter.getRootResourcesPathString(installedPath.getAbsolutePath());
		ServerSystemClassLoaderClassManager serverSystemClassLoaderClassManager = new ServerSystemClassLoaderClassManager(ioPartDynamicClassNameUtil);
		
		SimpleClassLoader simpleClassLoader = new SimpleClassLoader(classloaderClassPathString, classloaderReousrcesPathString, serverSystemClassLoaderClassManager);
		
		InputStream  resoruceInputStream = simpleClassLoader.getResourceAsStream("message_info/Echo.xml");
		if (null == resoruceInputStream) {
			fail("the returned value is null");
		}
	}
}
