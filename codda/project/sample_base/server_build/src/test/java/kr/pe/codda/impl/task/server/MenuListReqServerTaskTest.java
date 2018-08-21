package kr.pe.codda.impl.task.server;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.impl.message.ArraySiteMenuReq.ArraySiteMenuReq;
import kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;
import kr.pe.codda.server.lib.ServerDBUtil;

public class MenuListReqServerTaskTest extends AbstractJunitTest {	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();		
		
		ServerDBUtil.initializeDBEnvoroment();		
	}
	
	@Test
	public void testDoServie_ok() {
		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		
		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();
		
		try {
			ArraySiteMenuRes ArraySiteMenuRes = arraySiteMenuReqServerTask.doWork(arraySiteMenuReq);
			
			
			for (ArraySiteMenuRes.Menu menu : ArraySiteMenuRes.getMenuList()) {
				StringBuilder tabStringBuilder = new StringBuilder();
				for (int i=0; i < menu.getDepth(); i++) {
					tabStringBuilder.append("\t");
				}				
				log.info("{}{}", tabStringBuilder.toString(), menu.toString());
			}			
			
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'ArraySiteMenuRes'");
		}
	}	
	
	@Test
	public void testDoServie_두번루트메뉴등록하여목록점검() {
		ArraySiteMenuReqServerTask menuListReqServerTask = new ArraySiteMenuReqServerTask();
		ArraySiteMenuReq menuListReq = new ArraySiteMenuReq();
		ArraySiteMenuRes beforeMenuListRes = null;
		ArraySiteMenuRes afterMenuListRes = null;
		try {
			beforeMenuListRes = menuListReqServerTask.doWork(menuListReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'ArraySiteMenuRes'");
		}		
		
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		
		RootMenuAddReq firstRootMenuAddReq = new RootMenuAddReq();
		firstRootMenuAddReq.setMenuName("temp1");
		firstRootMenuAddReq.setLinkURL("/temp01");
		
		RootMenuAddRes firstRootMenuAddRes = null;
		try {
			firstRootMenuAddRes  = rootMenuAddReqServerTask.doWork(firstRootMenuAddReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a first output message 'RootMenuAddRes'");
		}		
		
		try {
			afterMenuListRes = menuListReqServerTask.doWork(menuListReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'ArraySiteMenuRes'");
		}
		
		java.util.List<ArraySiteMenuRes.Menu> afterMenulist = afterMenuListRes.getMenuList();
		
		if (afterMenulist.size() == 0) {
			fail("등록후 목록의 크기가 0 입니다, 즉 루트 메뉴 추가 실패");
		}
		
		Set<Long> beforeMenuNoSet = new HashSet<Long>();
		Set<Long> afterMenuNoSet = new HashSet<Long>();
		
		for (ArraySiteMenuRes.Menu menu : beforeMenuListRes.getMenuList()) {
			beforeMenuNoSet.add(menu.getMenuNo());
		}
		
		for (ArraySiteMenuRes.Menu menu : afterMenuListRes.getMenuList()) {
			afterMenuNoSet.add(menu.getMenuNo());
		}
		
		int lastIndex = afterMenuListRes.getMenuList().size() - 1;
		
		ArraySiteMenuRes.Menu lastMenu = afterMenuListRes.getMenuList().get(lastIndex);
		
		if (lastMenu.getMenuNo() != firstRootMenuAddRes.getMenuNo()) {
			log.info("after list::{}", afterMenuListRes.toString());
			log.info("registered root menu::{}", firstRootMenuAddRes.toString());			
			fail("추가된 마지막 메뉴는 등록한 루트 메뉴와 다릅니다");
		}		
		
		afterMenuNoSet.removeAll(beforeMenuNoSet);
		
		if (afterMenuNoSet.size() != 1) {
			fail("등록 전 메뉴와 등록 메뉴 차로 남겨진 메뉴 수가 1이 아닙니다");
		}
		
		if (! afterMenuNoSet.contains(firstRootMenuAddRes.getMenuNo())) {
			fail("등록 전 메뉴와 등록 메뉴 차로 남겨진 메뉴가 등록한 루트 메뉴와 다릅니다");
		}
		
		RootMenuAddReq secondRootMenuAddReq = new RootMenuAddReq();
		secondRootMenuAddReq.setMenuName("temp2");
		secondRootMenuAddReq.setLinkURL("/temp02");
		
		RootMenuAddRes secondRootMenuAddRes = null;
		try {
			secondRootMenuAddRes  = rootMenuAddReqServerTask.doWork(secondRootMenuAddReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a second output message 'RootMenuAddRes'");
		}
		
		if (secondRootMenuAddRes.getOrderSeq() != (firstRootMenuAddRes.getOrderSeq() + 1)) {
			log.info("first::{}", firstRootMenuAddRes);
			log.info("second::{}", secondRootMenuAddRes);
			
			fail("두번째 루트 메뉴의 순서의 잘못되었습니다. 두번째 메뉴의 순서는 첫번째 루트 메뉴의 순서에 1을 더한 값이어야 합니다");
		}
				
		log.info("루트 메뉴 등록 테스트 성공");
	}

}
