package kr.pe.codda.impl.task.server;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.impl.message.ArraySiteMenuReq.ArraySiteMenuReq;
import kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;
import kr.pe.codda.server.lib.ServerDBUtil;

public class RootMenuAddReqServerTaskTest extends AbstractJunitTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();		
		
		ServerDBUtil.initializeDBEnvoroment();		
	}
	
	
	@Test
	public void testDoServie_ok() {
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
		
		ArraySiteMenuReqServerTask menuListReqServerTask = new ArraySiteMenuReqServerTask();
		ArraySiteMenuReq menuListReq = new ArraySiteMenuReq();
		ArraySiteMenuRes menuListRes = null;
		try {
			menuListRes = menuListReqServerTask.doService(menuListReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'ArraySiteMenuRes'");
		}	
		
		java.util.List<ArraySiteMenuRes.Menu> menulist = menuListRes.getMenuList();
		
		if (menulist.size() == 0) {
			fail("등록후 목록의 크기가 0 입니다, 즉 루트 메뉴 추가 실패");
		}
		
		ArraySiteMenuRes.Menu lastMenu = menulist.get(menulist.size() - 1);
		
		if (lastMenu.getMenuNo() != rootMenuAddRes.getMenuNo()) {			
			log.info("목록의 마지막 메뉴[{}]와 등록한 루트 메뉴[{}]가 다릅니다", lastMenu.getMenuNo(), rootMenuAddRes.toString());			
			fail("목록의 마지막 메뉴와 등록한 루트 메뉴가 다릅니다");
		}
	}

}
