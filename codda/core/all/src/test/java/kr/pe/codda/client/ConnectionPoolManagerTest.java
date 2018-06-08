package kr.pe.codda.client;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import junitlib.AbstractJunitTest;

public class ConnectionPoolManagerTest extends AbstractJunitTest {

	@Test
	public void testGetPoolState() {
		ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();
		AnyProjectConnectionPoolIF mainProjectConnectionPool = connectionPoolManager.getMainProjectConnectionPool();
		
		assertNotNull(mainProjectConnectionPool);
		
		assertNotNull(mainProjectConnectionPool.getPoolState());
	}
	
}
