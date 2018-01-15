package kr.pe.sinnori.common.seesionkey;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.sessionkey.ClientRSA;
import kr.pe.sinnori.common.sessionkey.ServerRSA;

public class RSATest {
	Logger log = LoggerFactory
			.getLogger(RSATest.class);
	
	@Before
	public void setup() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_base";
		LOG_TYPE logType = LOG_TYPE.SERVER;
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);		
		

		SinnoriLogbackManger.getInstance().setup(sinnoriInstalledPathString, mainProjectName, logType);
		
	}
	
	@Test
	public void testRSAThreadSafe() {
		ServerRSA serverRSA = null;
		ClientRSA clientRSA = null;
		try {
			serverRSA = new ServerRSA();
			clientRSA = new ClientRSA(serverRSA.getDupPublicKeyBytes());
		} catch (SymmetricException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		
		
		
		int threadID = 0;
		RSATestThread rasTestThreadList[]  = {
				new RSATestThread(threadID++, serverRSA, clientRSA),
				new RSATestThread(threadID++, serverRSA, clientRSA),
				new RSATestThread(threadID++, serverRSA, clientRSA),
				new RSATestThread(threadID++, serverRSA, clientRSA),
				new RSATestThread(threadID++, serverRSA, clientRSA)
		};
		for (RSATestThread rasTestThread : rasTestThreadList) {
			rasTestThread.start();
		}
		
		try {
			Thread.sleep(1000L*60*10);
		} catch (InterruptedException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		
		for (RSATestThread rasTestThread : rasTestThreadList) {
			rasTestThread.interrupt();
		}
		
		while (! isAllTerminated(rasTestThreadList)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}
		}
		for (RSATestThread rasTestThread : rasTestThreadList) {
			if (rasTestThread.isError()) {
				fail(rasTestThread.getErrorMessage());
			}
		}		
	}
	
	private boolean isAllTerminated(RSATestThread rasTestThreadList[]) {
		for (RSATestThread rasTestThread : rasTestThreadList) {
			if (!rasTestThread.isTerminated()) {
				return false;
			}
		}
		return true;
	}
}
