package kr.pe.codda.common;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.logback.CoddaLogbackManger;
import kr.pe.codda.common.type.LogType;
import kr.pe.codda.server.lib.ServerDBEnvironment;

public abstract class AbstractJunitTest {
	protected static Logger log = null;
	
	protected final static String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
	protected final static String mainProjectName = "sample_base";
	
	@BeforeClass
	public static void setUpBeforeClass() {
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

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME,
				mainProjectName);
		
		LogType logType = LogType.SERVER;
		CoddaLogbackManger.getInstance().setup(sinnoriInstalledPathString, mainProjectName, logType);
		
		
		log = LoggerFactory.getLogger(AbstractJunitTest.class);
		
		try {
			ServerDBEnvironment.setup();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("서버 DB 환경 초기화 실패");
		}
	}

	@AfterClass
	public static void tearDownAfterClass() {
		System.gc();
	}
}
