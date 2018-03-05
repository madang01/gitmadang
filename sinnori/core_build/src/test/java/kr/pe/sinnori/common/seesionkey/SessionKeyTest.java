package kr.pe.sinnori.common.seesionkey;

import static org.junit.Assert.fail;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyIF;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyManager;

public class SessionKeyTest extends AbstractJunitTest {
	
	@Test
	public void testSessionKeyThreadSafe() {
		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		ClientSessionKeyManager clientSessionKeyManager = ClientSessionKeyManager.getInstance();
		
		ServerSessionkeyIF mainProjectServerSessionkey = null;
		ClientSessionKeyIF mainProjectClientSessionKey = null;
		try {
			mainProjectServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
			mainProjectClientSessionKey = clientSessionKeyManager.getMainProjectClientSessionKey(RSAPublickeyGetterBuilder.build());
		} catch (SymmetricException | InterruptedException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
				
		
		int threadID = 0;
		SessionKeyTestThread sessionKeyTestThreadList[]  = {
				new SessionKeyTestThread(threadID++, mainProjectServerSessionkey, mainProjectClientSessionKey),
				new SessionKeyTestThread(threadID++, mainProjectServerSessionkey, mainProjectClientSessionKey),
				new SessionKeyTestThread(threadID++, mainProjectServerSessionkey, mainProjectClientSessionKey),
				new SessionKeyTestThread(threadID++, mainProjectServerSessionkey, mainProjectClientSessionKey),
				new SessionKeyTestThread(threadID++, mainProjectServerSessionkey, mainProjectClientSessionKey)
		};
		for (SessionKeyTestThread sessionKeyTestThread : sessionKeyTestThreadList) {
			sessionKeyTestThread.start();
		}
		
		try {
			// Thread.sleep(1000L*60*10);
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		
		for (SessionKeyTestThread sessionKeyTestThread : sessionKeyTestThreadList) {
			sessionKeyTestThread.interrupt();
		}
		
		while (! isAllTerminated(sessionKeyTestThreadList)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}
		}
		for (SessionKeyTestThread sessionKeyTestThread : sessionKeyTestThreadList) {
			if (sessionKeyTestThread.isError()) {
				fail(sessionKeyTestThread.getErrorMessage());
			}
		}		
	}
	
	private boolean isAllTerminated(SessionKeyTestThread sessionKeyTestThreadList[]) {
		for (SessionKeyTestThread sessionKeyTestThread : sessionKeyTestThreadList) {
			if (!sessionKeyTestThread.isTerminated()) {
				return false;
			}
		}
		return true;
	}
}