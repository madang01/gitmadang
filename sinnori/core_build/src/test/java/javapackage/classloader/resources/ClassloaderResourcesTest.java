package javapackage.classloader.resources;

import static org.junit.Assert.fail;

import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.sinnori.server.mysql.MybatisSqlSessionFactoryManger;

public class ClassloaderResourcesTest {
	@Before
	public void setup() {
		SinnoriLogbackManger.getInstance().setup();
		
		
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				"sample_base");
		System.setProperty(
				CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				"D:\\gitsinnori\\sinnori");
		
		
	}
	
	@Test
	public void classpathResourcesTest() {
		// byte[] buffer = new byte[1024];
		
		
		InputStream is =  ClassloaderResourcesTest.class.getClassLoader().getResourceAsStream("kr/pe/sinnori/impl/mybatis/mybatisConfig.xml");
		if (null == is) {
			fail("fail to get reousrces of system classpath");
		}
		
		/*try {
			int readBytes  = -1;
			do {
				readBytes = is.read(buffer);
			} while (readBytes != -1);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		try {
			is.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance().getSqlSessionFactory("sample_base_db");
		} catch (DBCPDataSourceNotFoundException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		 
		System.out.println("success");
	}
	
	
}
