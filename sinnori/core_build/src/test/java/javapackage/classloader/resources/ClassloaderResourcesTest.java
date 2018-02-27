package javapackage.classloader.resources;

import static org.junit.Assert.fail;

import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitSupporter;
import kr.pe.sinnori.common.exception.MybatisException;
import kr.pe.sinnori.common.mybatis.MybatisSqlSessionFactoryManger;

public class ClassloaderResourcesTest extends AbstractJunitSupporter {
	
	@Test
	public void classpathResourcesTest() {
		
		InputStream is =  ClassloaderResourcesTest.class.getClassLoader().getResourceAsStream("kr/pe/sinnori/impl/mybatis/mybatisConfig.xml");
		if (null == is) {
			fail("fail to get reousrces of system classpath");
		}
		try {
			is.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		try {
		
				@SuppressWarnings("unused")
				SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance().getSqlSessionFactory("sample_base_db");
		} catch (MybatisException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		 
		System.out.println("success");
	}
	
	
}
