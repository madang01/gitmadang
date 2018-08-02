package kr.pe.codda.impl.task.server;

import static org.junit.Assert.fail;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.impl.message.TreeSiteMenuReq.TreeSiteMenuReq;
import kr.pe.codda.impl.message.TreeSiteMenuRes.TreeSiteMenuRes;

public class TreeSiteMenuReqServerTaskTest extends AbstractJunitTest {

	@Test
	public void testDoService_ok() {
		TreeSiteMenuReq treeSiteMenuReq = new TreeSiteMenuReq();
		
		TreeSiteMenuReqServerTask treeSiteMenuReqServerTask = new TreeSiteMenuReqServerTask();
		
		try {
			TreeSiteMenuRes treeSiteMenuRes = treeSiteMenuReqServerTask.doService(treeSiteMenuReq);
			
			log.info(treeSiteMenuRes.toString());
			
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("unknown error");
		}
	}

}
