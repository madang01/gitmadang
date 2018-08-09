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

public class MenuDeleteReqServerTaskTest extends AbstractJunitTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();		
		
		ServerDBUtil.initializeDBEnvoroment("testAdmin");		
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
		
		MenuDeleteReqServerTask menuDeleteReqServerTask = new MenuDeleteReqServerTask();
		
		MenuDeleteReq menuDeleteReq = new MenuDeleteReq();
		menuDeleteReq.setMenuNo(rootMenuAddRes.getMenuNo());
		
		MessageResultRes messageResultRes = null;
		try {
			messageResultRes =menuDeleteReqServerTask.doService(menuDeleteReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		if (! messageResultRes.getIsSuccess()) {
			fail(messageResultRes.getResultMessage());
		}		
	}
	
	@Test
	public void testDoServiece_rootMenuDoesNotExist() {		
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
		
		MessageResultRes messageResultRes = null;
		try {
			messageResultRes =menuDeleteReqServerTask.doService(menuDeleteReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		if (! messageResultRes.getIsSuccess()) {
			fail(messageResultRes.getResultMessage());
		}	
		
		try {
			menuDeleteReqServerTask.doService(menuDeleteReq);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			String expectedErrorMessage = new StringBuilder()
					.append("삭제할 메뉴[")
					.append(menuDeleteReq.getMenuNo())
					.append("]가 존재하지 않습니다").toString();
			String acutalErrorMessage = e.getMessage();
			
			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
	}
	
	@Test
	public void testDoServiece_childMenuExist() {		
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
		ChildMenuAddReq childMenuAddReq = new ChildMenuAddReq();
		childMenuAddReq.setParentNo(rootMenuAddRes.getMenuNo());
		childMenuAddReq.setMenuName("temp1_1");
		childMenuAddReq.setLinkURL("/temp01_1");
		
		ChildMenuAddRes childMenuAddRes = null;
		try {
			childMenuAddRes  = childMenuAddReqServerTask.doService(childMenuAddReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}		
		
		MenuDeleteReqServerTask menuDeleteReqServerTask = new MenuDeleteReqServerTask();
		
		MenuDeleteReq menuDeleteReq = new MenuDeleteReq();
		menuDeleteReq.setMenuNo(rootMenuAddRes.getMenuNo());
		
		MessageResultRes messageResultRes = null;
		try {
			messageResultRes = menuDeleteReqServerTask.doService(menuDeleteReq);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			log.info(e.getMessage(), e);
			
			String expectedErrorMessage = new StringBuilder()
					.append("자식이 있는 메뉴[")
					.append(menuDeleteReq.getMenuNo())
					.append("]는 삭제 할 수 없습니다").toString();
			
			String acutalErrorMessage = e.getMessage();
			
			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		menuDeleteReq.setMenuNo(childMenuAddRes.getMenuNo());
		
		try {
			messageResultRes = menuDeleteReqServerTask.doService(menuDeleteReq);
			
			if (! messageResultRes.getIsSuccess()) {
				fail("테스트용 자식 메뉴 삭제 실패");
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		menuDeleteReq.setMenuNo(rootMenuAddRes.getMenuNo());
		
		try {
			messageResultRes = menuDeleteReqServerTask.doService(menuDeleteReq);
			
			if (! messageResultRes.getIsSuccess()) {
				fail("테스트용 루트 메뉴 삭제 실패");
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}		
	}
}
