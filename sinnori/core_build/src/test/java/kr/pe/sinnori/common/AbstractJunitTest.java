package kr.pe.sinnori.common;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.logback.SinnoriLogbackManger;
import kr.pe.sinnori.common.type.LogType;

public abstract class AbstractJunitTest {
	protected static InternalLogger log = null;
	
	protected final static String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
	protected final static String mainProjectName = "sample_base";
	
	@BeforeClass
	 public static void setUpBeforeClass() throws Exception {
		File sinnoriInstalledPath = new File(sinnoriInstalledPathString);
		if (! sinnoriInstalledPath.exists()) {
			String errorMessage = String.format("the sinnori installed path[%s] doesn't exist", sinnoriInstalledPathString);			
			fail(errorMessage);
		}

		if (! sinnoriInstalledPath.isDirectory()) {
			String errorMessage = String.format("the sinnori installed path[%s] is not a directory", sinnoriInstalledPathString);
			fail(errorMessage);
		}
		
		String projectBasePathString = BuildSystemPathSupporter.getProjectBasePathString(sinnoriInstalledPathString);
		File projectBasePath = new File(projectBasePathString);
		
		if (! projectBasePath.exists()) {
			String errorMessage = String.format("the project[%s]'s path[%s] doesn't exist", mainProjectName, projectBasePathString);
			fail(errorMessage);
		}

		if (! projectBasePath.isDirectory()) {
			String errorMessage = String.format("the project[%s]'s path[%s] is not a directory", mainProjectName, projectBasePathString);			
			fail(errorMessage);
		}

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);
		
		LogType logType = LogType.SERVER;
		SinnoriLogbackManger.getInstance().setup(sinnoriInstalledPathString, mainProjectName, logType);

		log = InternalLoggerFactory.getInstance(AbstractJunitTest.class);
	}

	@AfterClass
    public static void tearDownAfterClass() throws Exception {
		System.gc();
	}
}
