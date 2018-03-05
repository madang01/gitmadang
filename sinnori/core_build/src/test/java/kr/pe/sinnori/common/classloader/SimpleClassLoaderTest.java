package kr.pe.sinnori.common.classloader;

import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.server.ServerClassLoaderBuilder;

public class SimpleClassLoaderTest extends AbstractJunitTest {
	
	@Test
	public void testGetResourceAsStream() {
		String firstPrefixDynamicClassFullName = "kr.pe.sinnori.impl.";
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil(firstPrefixDynamicClassFullName);
		ServerClassLoaderBuilder serverClassLoaderBuilder = null;
		try {
			serverClassLoaderBuilder = new ServerClassLoaderBuilder(ioPartDynamicClassNameUtil);
		} catch (SinnoriConfigurationException e) {
			fail("fail to create a instance of ServerClassLoaderBuilder");
		}
		
		SimpleClassLoader simpleClassLoader = serverClassLoaderBuilder.build();
		
		InputStream  resoruceInputStream = simpleClassLoader.getResourceAsStream("mybatis/mybatisConfig.xml");
		if (null == resoruceInputStream) {
			fail("the returned value is null");
		}
	}
}
