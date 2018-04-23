package kr.pe.sinnori.common;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.logback.SinnoriLogbackManger;

public abstract class AbstractJunitTest {
	protected static InternalLogger log = null;
	protected static File sinnoriInstalledBasePath = null;
	protected static File sinnoriInstalledPath = null;
	protected static File wasLibPath = null;
	
	@BeforeClass
	 public static void setUpBeforeClass() throws Exception {
		sinnoriInstalledBasePath = new File("D:\\gitsinnori");
		
		if (! sinnoriInstalledBasePath.exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		
		if (! sinnoriInstalledBasePath.isDirectory()) {
			fail("the sinnori installed path isn't a directory");
		}
		
		String sinnoriInstalledPathString = new StringBuilder(sinnoriInstalledBasePath.getAbsolutePath())
		.append(File.separator)
		.append("sinnori").toString();
				
		sinnoriInstalledPath = new File(sinnoriInstalledPathString);
		
		if (! sinnoriInstalledPath.exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		
		if (! sinnoriInstalledPath.isDirectory()) {
			fail("the sinnori installed path isn't a directory");
		}
		
		wasLibPath = new File("D:\\apache-tomcat-8.5.15\\lib");
		if (! wasLibPath.exists()) {
			fail("the was libaray path doesn't exist");
		}
		
		if (! wasLibPath.isDirectory()) {
			fail("the was libaray path isn't a directory");
		}
		
		try {
			SinnoriLogbackManger.getInstance().setup(sinnoriInstalledPathString);
		} catch(IllegalArgumentException | IllegalStateException e) {
			fail(e.getMessage());
		}

		log = InternalLoggerFactory.getInstance(AbstractJunitTest.class);
		
		String sinnoriRunningProjectName = "sample_base";
		
		System
				.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
						sinnoriRunningProjectName);
		System
				.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
						sinnoriInstalledPathString);
	}

	

	@AfterClass
    public static void tearDownAfterClass() throws Exception {
		System.gc();
	}
}
