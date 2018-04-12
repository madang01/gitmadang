package kr.pe.sinnori.common.asyn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.logback.SinnoriLogbackManger;
import kr.pe.sinnori.common.type.LogType;

public class ToLetterTest {
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

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConstructor_theParameterToSCIsNull() {
		log.info("hello", new Throwable());		
		
		SocketChannel toSC=null;
		String messageID=null;
		int mailboxID=0;
		int mailID=0;
		List<WrapBuffer> wrapBufferList = null;
		
		try {
			new ToLetter(toSC, messageID, mailboxID, mailID, wrapBufferList);
			
			fail("not IllegalArgumentException");
			
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String expectedMessage = "the parameter toSC is null";
			
			assertEquals(errorMessage, expectedMessage);
		} catch(Exception e) {
			fail("error");
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_theParameterMessageIDIsNull() {
		SocketChannel toSC = null;
		try {
			toSC = SocketChannel.open();
		} catch (IOException e1) {
			fail("fail to open socekt");
		}
		String messageID=null;
		int mailboxID=0;
		int mailID=0;
		List<WrapBuffer> wrapBufferList = null;
		
		try {
			new ToLetter(toSC, messageID, mailboxID, mailID, wrapBufferList);
			
			fail("not IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
			throw e;
		} catch(Exception e) {
			fail("error");
		}
	}
}
