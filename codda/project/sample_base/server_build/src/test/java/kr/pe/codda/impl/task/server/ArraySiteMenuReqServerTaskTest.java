package kr.pe.codda.impl.task.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

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
			long startTime = 0;
			long endTime = 0;
			startTime = System.nanoTime();
			
			ArraySiteMenuRes arraySiteMenuRes = arraySiteMenuReqServerTask.doService(arraySiteMenuReq);
			
			endTime = System.nanoTime();
			
			log.info("elapsed={}", TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS)); 
			
			int expectedOrderSequence = 0;
			for (ArraySiteMenuRes.Menu menu : arraySiteMenuRes.getMenuList()) {
				assertEquals("메뉴 순서가 잘못되었습니다", expectedOrderSequence, menu.getOrderSeq());
				expectedOrderSequence++;
			}
						
			
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("unknown error");
		}
		
		
	}

}
