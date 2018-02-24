package kr.pe.sinnori.server;

import static org.junit.Assert.fail;

import org.junit.Test;

import kr.pe.sinnori.client.AnyProjectConnectionPoolIF;
import kr.pe.sinnori.client.ConnectionPoolManager;
import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.Empty.Empty;

public class MainServerManagerTest extends AbstractJunitTest {
	
	@Test
	public void test() {
		
		AnyProjectServer anyProjectServer = null;
		
		try {
			anyProjectServer = MainServerManager.getInstance().getMainProjectServer();
		} catch (IllegalStateException e) {
			fail(e.getMessage());
		}
		
		anyProjectServer.startServer();
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		
		Empty emptyReq = new Empty();
		AbstractMessage emptyRes = null; 
		try {
			emptyRes = mainProjectConnectionPool.sendSyncInputMessage(emptyReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail(e.getMessage());
		}
		
		log.info(emptyRes.toString());
	}
}
