package kr.pe.codda.impl.task.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.MenuDeleteReq.MenuDeleteReq;
import kr.pe.codda.impl.message.MenuMoveDownReq.MenuMoveDownReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class MenuMoveDownReqServerTaskTest extends AbstractJunitTest {	
	final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();		
		
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);	
		
		{
			String userID = "admin";
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "단위테스터용어드민";
			String email = "admin@codda.pe.kr";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.ADMIN, userID, nickname, email,
						passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(userID).append("] 입니다")
						.toString();
				String actualErrorMessag = e.getMessage();

				// log.warn(actualErrorMessag, e);

				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
		}

		{
			String userID = "test01";
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "단위테스터용아이디1";
			String email = "test01@codda.pe.kr";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, email,
						passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(userID).append("] 입니다")
						.toString();
				String actualErrorMessag = e.getMessage();

				// log.warn(actualErrorMessag, e);

				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
		}

		{
			String userID = "test02";
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "단위테스터용아이디2";
			String email = "test02@codda.pe.kr";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, email,
						passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(userID).append("] 입니다")
						.toString();
				String actualErrorMessag = e.getMessage();

				// log.warn(actualErrorMessag, e);

				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
		}
	}

	@Test
	public void testDoService_ok() {
		String requestedUserID = "admin";
		
		RootMenuAddReqServerTask rootMenuAddReqServerTask = null;
		try {
			rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		RootMenuAddRes fromRootMenuAddRes = null;
		RootMenuAddRes toRootMenuAddRes = null;
		
		{
			RootMenuAddReq fromRootMenuAddReq = new RootMenuAddReq();
			fromRootMenuAddReq.setRequestedUserID(requestedUserID);
			fromRootMenuAddReq.setMenuName("temp1");
			fromRootMenuAddReq.setLinkURL("/temp01");
			
			
			try {
				fromRootMenuAddRes  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, fromRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		{
			RootMenuAddReq toRootMenuAddReq = new RootMenuAddReq();
			toRootMenuAddReq.setRequestedUserID(requestedUserID);
			toRootMenuAddReq.setMenuName("temp2");
			toRootMenuAddReq.setLinkURL("/temp02");
			
			try {
				toRootMenuAddRes  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, toRootMenuAddReq);
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
		
		MenuMoveDownReq menuDownMoveReq = new MenuMoveDownReq();
		menuDownMoveReq.setRequestedUserID(requestedUserID);
		menuDownMoveReq.setMenuNo(fromRootMenuAddRes.getMenuNo());
		MenuMoveDownReqServerTask menuDownMoveReqServerTask = null;
		try {
			menuDownMoveReqServerTask = new MenuMoveDownReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		try {
			MessageResultRes messageResultRes = menuDownMoveReqServerTask.doWork(TEST_DBCP_NAME, menuDownMoveReq);
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
		String requestedUserID = "admin";
		
		RootMenuAddReqServerTask rootMenuAddReqServerTask = null;
		try {
			rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		RootMenuAddRes fromRootMenuAddRes = null;		
		{
			RootMenuAddReq fromRootMenuAddReq = new RootMenuAddReq();
			fromRootMenuAddReq.setRequestedUserID(requestedUserID);
			fromRootMenuAddReq.setMenuName("temp1");
			fromRootMenuAddReq.setLinkURL("/temp01");
			
			
			try {
				fromRootMenuAddRes  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, fromRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		MenuDeleteReqServerTask menuDeleteReqServerTask = null;
		try {
			menuDeleteReqServerTask = new MenuDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		MenuDeleteReq menuDeleteReq = new MenuDeleteReq();
		menuDeleteReq.setRequestedUserID(requestedUserID);
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
		
		MenuMoveDownReq menuDownMoveReq = new MenuMoveDownReq();
		menuDownMoveReq.setRequestedUserID(requestedUserID);
		menuDownMoveReq.setMenuNo(fromRootMenuAddRes.getMenuNo());
		MenuMoveDownReqServerTask menuDownMoveReqServerTask = null;
		try {
			menuDownMoveReqServerTask = new MenuMoveDownReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		try {
			menuDownMoveReqServerTask.doWork(TEST_DBCP_NAME, menuDownMoveReq);
			
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
		String requestedUserID = "admin";
		
		RootMenuAddReqServerTask rootMenuAddReqServerTask = null;
		try {
			rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		RootMenuAddRes fromRootMenuAddRes = null;		
		{
			RootMenuAddReq fromRootMenuAddReq = new RootMenuAddReq();
			fromRootMenuAddReq.setRequestedUserID(requestedUserID);
			fromRootMenuAddReq.setMenuName("temp1");
			fromRootMenuAddReq.setLinkURL("/temp01");			
			
			try {
				fromRootMenuAddRes  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, fromRootMenuAddReq);
			} catch (Exception e) {
				log.warn("error", e);
				fail("fail to get a output message 'RootMenuAddRes'");
			}
		}
		
		MenuMoveDownReq menuDownMoveReq = new MenuMoveDownReq();
		menuDownMoveReq.setRequestedUserID(requestedUserID);
		menuDownMoveReq.setMenuNo(fromRootMenuAddRes.getMenuNo());
		MenuMoveDownReqServerTask menuDownMoveReqServerTask = null;
		try {
			menuDownMoveReqServerTask = new MenuMoveDownReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		try {
			menuDownMoveReqServerTask.doWork(TEST_DBCP_NAME, menuDownMoveReq);
			
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
