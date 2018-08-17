package kr.pe.codda.impl.task.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.MenuDeleteReq.MenuDeleteReq;
import kr.pe.codda.impl.message.MenuMoveDownReq.MenuMoveDownReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;
import kr.pe.codda.server.lib.ServerDBUtil;

public class MenuMoveDownReqServerTaskTest extends AbstractJunitTest {	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();		
		
		ServerDBUtil.initializeDBEnvoroment("admin");		
	}

	@Test
	public void testDoService_ok() {
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		
		RootMenuAddRes fromRootMenuAddRes = null;
		RootMenuAddRes toRootMenuAddRes = null;
		
		{
			RootMenuAddReq fromRootMenuAddReq = new RootMenuAddReq();
			fromRootMenuAddReq.setMenuName("temp1");
			fromRootMenuAddReq.setLinkURL("/temp01");
			
			
			try {
				fromRootMenuAddRes  = rootMenuAddReqServerTask.doService(fromRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		{
			RootMenuAddReq toRootMenuAddReq = new RootMenuAddReq();
			toRootMenuAddReq.setMenuName("temp2");
			toRootMenuAddReq.setLinkURL("/temp02");
			
			try {
				toRootMenuAddRes  = rootMenuAddReqServerTask.doService(toRootMenuAddReq);
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
				rootMenuAddReqServerTask.doService(otherRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}*/
		
		MenuMoveDownReq menuDownMoveReq = new MenuMoveDownReq();
		menuDownMoveReq.setMenuNo(fromRootMenuAddRes.getMenuNo());
		MenuMoveDownReqServerTask menuDownMoveReqServerTask = new MenuMoveDownReqServerTask();
		try {
			MessageResultRes messageResultRes = menuDownMoveReqServerTask.doService(menuDownMoveReq);
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		log.info("하단 이동 성공, fromRootMenuAddRes.menuNo={}, toRootMenuAddRes.menuNo={}", 
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
				fromRootMenuAddRes  = rootMenuAddReqServerTask.doService(fromRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		MenuDeleteReqServerTask menuDeleteReqServerTask = new MenuDeleteReqServerTask();
		
		MenuDeleteReq menuDeleteReq = new MenuDeleteReq();
		menuDeleteReq.setMenuNo(fromRootMenuAddRes.getMenuNo());		
		
		try {
			MessageResultRes messageResultRes = menuDeleteReqServerTask.doService(menuDeleteReq);
			
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		MenuMoveDownReq menuDownMoveReq = new MenuMoveDownReq();
		menuDownMoveReq.setMenuNo(fromRootMenuAddRes.getMenuNo());
		MenuMoveDownReqServerTask menuDownMoveReqServerTask = new MenuMoveDownReqServerTask();
		try {
			menuDownMoveReqServerTask.doService(menuDownMoveReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String expectedErrorMessage  = new StringBuilder()
					.append("지정한 메뉴[")
					.append(menuDownMoveReq.getMenuNo())
					.append("]가 존재하지 않습니다").toString();
			
			assertEquals(expectedErrorMessage, e.getMessage());
			
			log.info("없는 메뉴 번호를 메뉴 하단 이동하라고 요청 테스트 성공");
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
	}
	
	@Test
	public void testDoService_하단이동할수없는루트메뉴() {
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		
		RootMenuAddRes fromRootMenuAddRes = null;		
		{
			RootMenuAddReq fromRootMenuAddReq = new RootMenuAddReq();
			fromRootMenuAddReq.setMenuName("temp1");
			fromRootMenuAddReq.setLinkURL("/temp01");			
			
			try {
				fromRootMenuAddRes  = rootMenuAddReqServerTask.doService(fromRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		MenuMoveDownReq menuDownMoveReq = new MenuMoveDownReq();
		menuDownMoveReq.setMenuNo(fromRootMenuAddRes.getMenuNo());
		MenuMoveDownReqServerTask menuDownMoveReqServerTask = new MenuMoveDownReqServerTask();
		try {
			menuDownMoveReqServerTask.doService(menuDownMoveReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String expectedErrorMessage = new StringBuilder()
					.append("지정한 메뉴[")
					.append(menuDownMoveReq.getMenuNo())
					.append("]보다 한칸 낮은 메뉴가 존재하지 않습니다").toString();
			
			assertEquals(expectedErrorMessage, e.getMessage());
			
			log.info("하단 이동 대상이 아닌 메뉴 번호를 메뉴 하단 이동하라고 요청 테스트 성공");
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
	}

}
