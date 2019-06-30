package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.jooq.tables.SbSiteLogTb.SB_SITE_LOG_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.MemberSearchRes.MemberSearchRes;
import kr.pe.codda.impl.message.MemberSearchReq.MemberSearchReq;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class MemberSearchReqServerTaskTest extends AbstractJunitTest {
	final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);
		
		{
			String userID = "admin";
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "관리자";
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
			String userID = "guest";
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "손님";
			String email = "guest@codda.pe.kr";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.GUEST, userID, nickname, email,
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
	public void testDoWork() {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		final  java.util.Date toDate = new java.util.Date();
		final  Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		
		final  java.util.Date fromDate = cal.getTime();
		
		final String testID = "test03";
		
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				create.delete(SB_SITE_LOG_TB)
				.where(SB_SITE_LOG_TB.USER_ID.eq(testID)).execute();
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(testID)).execute();

				conn.commit();
			});
		} catch (Exception e) {
			log.warn(e.getMessage(), e);

			fail(e.getMessage());
		}
		
		{
			String userID = "test03";
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "단위테스터용아이디3";
			String email = "test03@codda.pe.kr";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, email,
						passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);			
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
		}
		
		
		MemberSearchReq memberSearchReq = new MemberSearchReq();
		memberSearchReq.setRequestedUserID("admin");
		memberSearchReq.setMemberState(MemberStateType.OK.getValue());
		memberSearchReq.setSearchID("");
		memberSearchReq.setFromDateString(sdf.format(fromDate));
		memberSearchReq.setToDateString(sdf.format(toDate));
		memberSearchReq.setPageNo(1);
		memberSearchReq.setPageSize(20);
		
		log.info(memberSearchReq.toString());
		
		MemberSearchReqServerTask memberManagerReqServerTask = null;
		
		try {
			memberManagerReqServerTask = new MemberSearchReqServerTask();
		} catch(DynamicClassCallException e) {
			log.warn("회원 조회 실패", e);
			fail("dead code, 회원 조회 실패");
		}
		
		MemberSearchRes memberSearchRes = null;
		
		try {
			memberSearchRes = memberManagerReqServerTask.doWork(TEST_DBCP_NAME, memberSearchReq);
		} catch (Exception e) {
			log.warn("회원 조회 실패", e);
			fail("회원 조회 실패::errmsg="+e.getMessage());
		}
		
		log.info(memberSearchRes.toString());
	}

}
