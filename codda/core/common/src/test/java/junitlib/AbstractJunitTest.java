package junitlib;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.logback.CoddaLogbackManger;

public abstract class AbstractJunitTest {
	protected static InternalLogger log = null;
	protected static File installedBasePath = null;
	protected static File installedPath = null;
	protected static File wasLibPath = null;
	protected final static String mainProjectName = "sample_base";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		installedBasePath = new File("D:\\gitmadang");
		
		if (! installedBasePath.exists()) {
			fail("the installed path doesn't exist");
		}
		
		if (! installedBasePath.isDirectory()) {
			fail("the installed path isn't a directory");
		}
		
		String installedPathString = new StringBuilder(installedBasePath.getAbsolutePath())
		.append(File.separator)
		.append(CommonStaticFinalVars.ROOT_PROJECT_NAME).toString();
				
		installedPath = new File(installedPathString);
		
		if (! installedPath.exists()) {
			fail("the installed path doesn't exist");
		}
		
		if (! installedPath.isDirectory()) {
			fail("the installed path isn't a directory");
		}
		
		wasLibPath = new File("D:\\apache-tomcat-8.5.15\\lib");
		if (! wasLibPath.exists()) {
			fail("the was libaray path doesn't exist");
		}
		
		if (! wasLibPath.isDirectory()) {
			fail("the was libaray path isn't a directory");
		}
		
		try {
			CoddaLogbackManger.getInstance().setup(installedPathString);
		} catch(IllegalArgumentException | IllegalStateException e) {
			fail(e.getMessage());
		}

		
				
		System
				.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME,
						mainProjectName);
		System
				.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH,
						installedPathString);
		
		log = InternalLoggerFactory.getInstance(CommonStaticFinalVars.BASE_PACKAGE_NAME);
	}

	

	@AfterClass
    public static void tearDownAfterClass() throws Exception {
		System.gc();
	}
}