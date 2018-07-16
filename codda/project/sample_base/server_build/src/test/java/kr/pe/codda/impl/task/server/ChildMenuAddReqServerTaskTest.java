package kr.pe.codda.impl.task.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.ChildMenuAddReq.ChildMenuAddReq;
import kr.pe.codda.impl.message.ChildMenuAddRes.ChildMenuAddRes;
import kr.pe.codda.impl.message.MenuDeleteReq.MenuDeleteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;
import kr.pe.codda.server.lib.ServerDBUtil;

public class ChildMenuAddReqServerTaskTest extends AbstractJunitTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();		
		
		ServerDBUtil.initializeDBEnvoroment();		
	}

	@Test
	public void testDoServiece_ok() {
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		
		RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
		rootMenuAddReq.setMenuName("temp1");
		rootMenuAddReq.setLinkURL("/temp01");
		
		RootMenuAddRes rootMenuAddRes = null;
		try {
			rootMenuAddRes  = rootMenuAddReqServerTask.doService(rootMenuAddReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}
		
		ChildMenuAddReqServerTask childMenuAddReqServerTask = new ChildMenuAddReqServerTask();
		
		ChildMenuAddReq firstChildMenuAddReq = new ChildMenuAddReq();
		firstChildMenuAddReq.setParentNo(rootMenuAddRes.getMenuNo());
		firstChildMenuAddReq.setMenuName("temp1_1");
		firstChildMenuAddReq.setLinkURL("/temp01_1");
		
		ChildMenuAddRes firstChildMenuAddRes = null;
		try {
			firstChildMenuAddRes  = childMenuAddReqServerTask.doService(firstChildMenuAddReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}
		
		ChildMenuAddReq secondChildMenuAddReq = new ChildMenuAddReq();
		secondChildMenuAddReq.setParentNo(rootMenuAddRes.getMenuNo());
		secondChildMenuAddReq.setMenuName("temp1_2");
		secondChildMenuAddReq.setLinkURL("/temp01_2");
		
		ChildMenuAddRes secondChildMenuAddRes = null;
		try {
			secondChildMenuAddRes  = childMenuAddReqServerTask.doService(secondChildMenuAddReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}	
		
		if (secondChildMenuAddRes.getOrderSeq() != (firstChildMenuAddRes.getOrderSeq() + 1)) {
			log.info("first::{}", firstChildMenuAddRes);
			log.info("second::{}", secondChildMenuAddRes);
			
			fail("두번째 자식 메뉴의 순서의 잘못되었습니다. 두번째 자식 메뉴의 순서는 첫번째 자식 메뉴의 순서에 1을 더한 값이어야 합니다");
		}
	}

	
	@Test
	public void testDoServiece_parentMenuDoesNotExist() {
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		
		RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
		rootMenuAddReq.setMenuName("temp1");
		rootMenuAddReq.setLinkURL("/temp01");
		
		RootMenuAddRes rootMenuAddRes = null;
		try {
			rootMenuAddRes  = rootMenuAddReqServerTask.doService(rootMenuAddReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}
		
		
		MenuDeleteReqServerTask menuDeleteReqServerTask = new MenuDeleteReqServerTask();
		
		MenuDeleteReq menuDeleteReq = new MenuDeleteReq();
		menuDeleteReq.setMenuNo(rootMenuAddRes.getMenuNo());

		try {
			MessageResultRes messageResultRes = menuDeleteReqServerTask.doService(menuDeleteReq);
		
			if (! messageResultRes.getIsSuccess()) {
				log.info(messageResultRes.getResultMessage());
				fail("추가한 루트 메뉴 삭제 실패");
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		
		ChildMenuAddReqServerTask childMenuAddReqServerTask = new ChildMenuAddReqServerTask();
		
		ChildMenuAddReq childMenuAddReq = new ChildMenuAddReq();
		childMenuAddReq.setParentNo(rootMenuAddRes.getMenuNo());
		childMenuAddReq.setMenuName("temp1_1");
		childMenuAddReq.setLinkURL("/temp01_1");
		
		try {
			childMenuAddReqServerTask.doService(childMenuAddReq);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			//log.info(e.getMessage(), e);
			
			String expectedErrorMessage = new StringBuilder()
					.append("부모 메뉴[")
					.append(childMenuAddReq.getParentNo())
					.append("]가 존재하지 않습니다")
					.toString();
			
			String acutalErrorMessage = e.getMessage();
			
			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}
		
		
	}
}
