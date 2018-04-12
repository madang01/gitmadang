package kr.pe.sinnori.common.buildsystem;

import static org.junit.Assert.fail;

import org.junit.Test;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.exception.BuildSystemException;

public class BuildSystemSupporterTest {
	
	private static InternalLogger log = InternalLoggerFactory.getInstance(BuildSystemSupporterTest.class);
	
	private final String projectNameForTest = "sample_test";
	private final String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
	
	
	@Test
	public void testCreateNewMainProjectBuildSystem() {		
		try {
			BuildSystemSupporter.dropProject(sinnoriInstalledPathString, projectNameForTest);
		} catch (BuildSystemException e) {
			log.info("fail to delete project path", e);
			fail(e.getMessage());
		}
		
		
		boolean isServer = false;
		
		boolean isAppClient = true;
		
		boolean isWebClient = true;
		String servletSystemLibrayPathString = "D:\\apache-tomcat-8.5.15\\lib";
		
		try {
			BuildSystemSupporter.createNewMainProjectBuildSystem( 
					sinnoriInstalledPathString,
					projectNameForTest,
					isServer, 
					isAppClient, 
					isWebClient, servletSystemLibrayPathString);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (BuildSystemException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	
	
}
