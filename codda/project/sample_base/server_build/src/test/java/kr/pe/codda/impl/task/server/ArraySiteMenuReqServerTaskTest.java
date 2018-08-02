package kr.pe.codda.impl.task.server;

import static org.junit.Assert.fail;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.impl.message.ArraySiteMenuReq.ArraySiteMenuReq;
import kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes;

public class ArraySiteMenuReqServerTaskTest extends AbstractJunitTest {

	@Test
	public void testDoService_ok() {
		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();
		
		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		
		try {
			ArraySiteMenuRes arraySiteMenuRes = arraySiteMenuReqServerTask.doService(arraySiteMenuReq);
			
			for (ArraySiteMenuRes.Menu menu : arraySiteMenuRes.getMenuList()) {
				log.info(menu.toString());
			}
			
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("unknown error");
		}
	}

}
