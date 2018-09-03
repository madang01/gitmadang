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
import kr.pe.codda.impl.message.MenuMoveUpReq.MenuMoveUpReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class MenuMoveUpReqServerTaskTest extends AbstractJunitTest {
	final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();		
		
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);		
	}

	@Test
	public void testDoService_ok() {
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		
		RootMenuAddRes toRootMenuAddRes = null;
		RootMenuAddRes fromRootMenuAddRes = null;
		
		{
			RootMenuAddReq toRootMenuAddReq = new RootMenuAddReq();
			toRootMenuAddReq.setMenuName("temp3");
			toRootMenuAddReq.setLinkURL("/temp03");
			
			try {
				toRootMenuAddRes  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, toRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		{
			RootMenuAddReq fromRootMenuAddReq = new RootMenuAddReq();
			fromRootMenuAddReq.setMenuName("temp4");
			fromRootMenuAddReq.setLinkURL("/temp04");
			
			
			try {
				fromRootMenuAddRes  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, fromRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		
		
		/*{
			RootMenuAddReq otherRootMenuAddReq = new RootMenuAddReq();
			otherRootMenuAddReq.setMenuName("temp3");
			otherRootMenuAddReq.setLinkURL("/temp03");
			
			try {
				rootMenuAddReqServerTask.doWork(otherRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}*/
		
		MenuMoveUpReq menuUpMoveReq = new MenuMoveUpReq();
		menuUpMoveReq.setMenuNo(fromRootMenuAddRes.getMenuNo());
		MenuMoveUpReqServerTask menuUpMoveReqServerTask = new MenuMoveUpReqServerTask();
		try {
			MessageResultRes messageResultRes = menuUpMoveReqServerTask.doWork(TEST_DBCP_NAME, menuUpMoveReq);
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		log.info("상단 이동 성공, fromRootMenuAddRes.menuNo={}, toRootMenuAddRes.menuNo={}", 
				fromRootMenuAddRes.getMenuNo(), toRootMenuAddRes.getMenuNo());
	}
	
	@Test
	public void testDoService_menuNoDoesNotExist() {
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		
		RootMenuAddRes fromRootMenuAddRes = null;		
		{
			RootMenuAddReq fromRootMenuAddReq = new RootMenuAddReq();
			fromRootMenuAddReq.setMenuName("temp1");
			fromRootMenuAddReq.setLinkURL("/temp01");
			
			
			try {
				fromRootMenuAddRes  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, fromRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		MenuDeleteReqServerTask menuDeleteReqServerTask = new MenuDeleteReqServerTask();
		
		MenuDeleteReq menuDeleteReq = new MenuDeleteReq();
		menuDeleteReq.setMenuNo(fromRootMenuAddRes.getMenuNo());		
		
		try {
			MessageResultRes messageResultRes = menuDeleteReqServerTask.doWork(TEST_DBCP_NAME, menuDeleteReq);
			
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		MenuMoveUpReq menuUpMoveReq = new MenuMoveUpReq();
		menuUpMoveReq.setMenuNo(fromRootMenuAddRes.getMenuNo());
		MenuMoveUpReqServerTask menuUpMoveReqServerTask = new MenuMoveUpReqServerTask();
		try {
			menuUpMoveReqServerTask.doWork(TEST_DBCP_NAME, menuUpMoveReq);			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String expectedErrorMessage  = new StringBuilder()
					.append("지정한 메뉴[")
					.append(menuUpMoveReq.getMenuNo())
					.append("]가 존재하지 않습니다").toString();
			
			assertEquals(expectedErrorMessage, e.getMessage());
			
			log.info("없는 메뉴 번호를 메뉴 상단 이동하라고 요청 테스트 성공");
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
	}

	
	@Test
	public void testDoService_상단이동할수없는메뉴() {
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		
		RootMenuAddRes rootMenuAddRes = null;		
		{
			RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
			rootMenuAddReq.setMenuName("temp1");
			rootMenuAddReq.setLinkURL("/temp01");
			
			
			try {
				rootMenuAddRes  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		ChildMenuAddReqServerTask childMenuAddReqServerTask = new ChildMenuAddReqServerTask();
		
		ChildMenuAddReq firstChildMenuAddReq = new ChildMenuAddReq();
		firstChildMenuAddReq.setParentNo(rootMenuAddRes.getMenuNo());
		firstChildMenuAddReq.setMenuName("temp1_1");
		firstChildMenuAddReq.setLinkURL("/temp01_1");
		
		ChildMenuAddRes firstChildMenuAddRes = null;
		try {
			firstChildMenuAddRes  = childMenuAddReqServerTask.doWork(TEST_DBCP_NAME, firstChildMenuAddReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}
		
		
		MenuMoveUpReq menuUpMoveReq = new MenuMoveUpReq();
		menuUpMoveReq.setMenuNo(firstChildMenuAddRes.getMenuNo());
		MenuMoveUpReqServerTask menuUpMoveReqServerTask = new MenuMoveUpReqServerTask();
		try {
			menuUpMoveReqServerTask.doWork(TEST_DBCP_NAME, menuUpMoveReq);			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String expectedErrorMessage = new StringBuilder()
					.append("최상단 메뉴[")
					.append(menuUpMoveReq.getMenuNo())
					.append("]는 상단으로 이동할 수 없습니다").toString();
			
			assertEquals(expectedErrorMessage, e.getMessage());
			
			log.info("메뉴 상단 이동이 불가능한 메뉴의 상단 이동 요청 테스트 성공");
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
	}
}
