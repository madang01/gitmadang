package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB;
import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.jooq.tables.SbSiteLogTb.SB_SITE_LOG_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.AccountSearchReadyReq.AccountSearchReadyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.AccountSearchType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class AccountSearchReadyReqServerTaskTest extends AbstractJunitTest {
	protected final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	
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
	public void 비밀번호찾기준비_존재하지않은회원() {
		String email = "nobody@codda.pe.kr";
		String ip = "127.0.0.1";
		
		AccountSearchReadyReq passwordSearchReadyReq = new AccountSearchReadyReq();
		passwordSearchReadyReq.setEmail(email);
		passwordSearchReadyReq.setIp(ip);
		
		AccountSearchReadyReqServerTask passwordSearchReadyReqServerTask = null;
		try {
			passwordSearchReadyReqServerTask = new AccountSearchReadyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			
			passwordSearchReadyReqServerTask.doWork(TEST_DBCP_NAME, passwordSearchReadyReq);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = "입력한 이메일에 해당하는 일반 회원이 없습니다";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 준비 실패");
		}
	}
	
	@Test
	public void 비밀번호찾기준비_비밀번호최대횟수초과() {
		String testID = "test03";
		String email = "k9200544@hanmail.net";
		String ip = "127.0.0.1";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(
					TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_SITE_LOG_TB)
			.execute();
			
			create.delete(SB_ACCOUNT_SERARCH_TB)
			.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(testID))
			.execute();
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(testID))
			.execute();
			
			conn.commit();
		
		} catch (Exception e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}

			log.warn(e.getMessage(), e);

			fail(e.getMessage());
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
		
		{
			
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "이메일테스터";
			

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, testID, nickname, email,
						passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(testID).append("] 입니다")
						.toString();
				String actualErrorMessag = e.getMessage();

				// log.warn(actualErrorMessag, e);

				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
		}
		
		AccountSearchReadyReq passwordSearchReadyReq = new AccountSearchReadyReq();
		passwordSearchReadyReq.setAccountSearchType(AccountSearchType.ID.getValue());
		passwordSearchReadyReq.setEmail(email);
		passwordSearchReadyReq.setIp(ip);		
		
		AccountSearchReadyReqServerTask passwordSearchReadyReqServerTask = null;
		try {
			passwordSearchReadyReqServerTask = new AccountSearchReadyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {			
			MessageResultRes messageResultRes = passwordSearchReadyReqServerTask.doWork(TEST_DBCP_NAME, passwordSearchReadyReq);
			log.info(messageResultRes.toString());
			if (! messageResultRes.getIsSuccess()) {
				fail("비밀번호 찾기 준비 실패");
			}
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 준비 실패");
		}
		
		AccountSearchProcessReqServerTask passwordSearchProcessReqServerTask = null;
		try {
			passwordSearchProcessReqServerTask = new AccountSearchProcessReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		for (int i=0; i < ServerCommonStaticFinalVars.MAX_WRONG_PASSWORD_COUNT_OF_PASSWORD_SEARCH_SERVICE; i++) {
			try {
				passwordSearchProcessReqServerTask.doPasswordChangeProcess(TEST_DBCP_NAME, log, AccountSearchType.ID, 
						email, "aa", null, ip);
				
				fail("no ServerServiceException");
			} catch(ServerServiceException e) {
				String errorMessage = e.getMessage();
				String expectedErrorMessage = new StringBuilder()						
						.append("아이디 찾기 ")
						.append(i+1)
						.append("회 비밀 값이 틀렸습니다, 처음 부터 다시 시도해 주시기 바랍니다").toString();
				
				assertEquals(expectedErrorMessage, errorMessage);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("비밀번호 찾기 처리 실패");
			}
		}
		
		
		try {
			passwordSearchProcessReqServerTask.doPasswordChangeProcess(TEST_DBCP_NAME, log, AccountSearchType.ID, 
					email, "aa", null, ip);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("아이디 찾기로 비밀 값 틀린 횟수가  최대 횟수 ")
					.append(ServerCommonStaticFinalVars.MAX_WRONG_PASSWORD_COUNT_OF_PASSWORD_SEARCH_SERVICE)
					.append("회에 도달하여 더 이상 진행할 수 없습니다, 관리자에게 문의하여 주시기 바랍니다").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 처리 실패");
		}
		
		try {			
			passwordSearchReadyReqServerTask.doWork(TEST_DBCP_NAME, passwordSearchReadyReq);
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("아이디 혹은 비밀번호 찾기로 비밀 값 틀린 횟수가  최대 횟수 ")
					.append(ServerCommonStaticFinalVars.MAX_WRONG_PASSWORD_COUNT_OF_PASSWORD_SEARCH_SERVICE)
					.append("회에 도달하여 더 이상 진행할 수 없습니다, 관리자에게 문의하여 주시기 바랍니다").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 준비 실패");
		}
	}
	
	@Test
	public void 비밀번호찾기준비_최대요청초과() {
		String testID = "test03";
		String email = "k9200544@hanmail.net";
		String ip = "127.0.0.1";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(
					TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_SITE_LOG_TB)
			.execute();
			
			create.delete(SB_ACCOUNT_SERARCH_TB)
			.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(testID))
			.execute();
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(testID))
			.execute();
			
			conn.commit();
		
		} catch (Exception e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}

			log.warn(e.getMessage(), e);

			fail(e.getMessage());
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
		
		{
			
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "이메일테스터";
			

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, testID, nickname, email,
						passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(testID).append("] 입니다")
						.toString();
				String actualErrorMessag = e.getMessage();

				// log.warn(actualErrorMessag, e);

				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
		}		
		
		AccountSearchReadyReq passwordSearchReadyReq = new AccountSearchReadyReq();
		passwordSearchReadyReq.setEmail(email);
		passwordSearchReadyReq.setIp(ip);
		passwordSearchReadyReq.setAccountSearchType(AccountSearchType.ID.getValue());
		
		AccountSearchReadyReqServerTask passwordSearchReadyReqServerTask = null;
		try {
			passwordSearchReadyReqServerTask = new AccountSearchReadyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			for (int i=0; i < ServerCommonStaticFinalVars.MAX_RETRY_COUNT_OF_PASSWORD_SEARCH_SERVICE; i++) {
				MessageResultRes messageResultRes = passwordSearchReadyReqServerTask.doWork(TEST_DBCP_NAME, passwordSearchReadyReq);
				log.info(messageResultRes.toString());
				if (! messageResultRes.getIsSuccess()) {
					fail("비밀번호 찾기 준비 실패");
				}
				
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 준비 실패");
		}
		
		try {
		
			passwordSearchReadyReqServerTask.doWork(TEST_DBCP_NAME, passwordSearchReadyReq);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("아이디 혹은 비밀번호 찾기 신청 횟수가 최대 횟수 ")
					.append(ServerCommonStaticFinalVars.MAX_RETRY_COUNT_OF_PASSWORD_SEARCH_SERVICE)
					.append("회에 도달하여 더 이상 진행할 수 없습니다, 관리자에게 문의하여 주시기 바랍니다").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 준비 실패");
		}
	}

	@Test
	public void 비밀번호찾기준비_OK() {
		String testID = "test03";
		String email = "k9200544@hanmail.net";
		String ip = "127.0.0.1";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(
					TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_SITE_LOG_TB)
			.execute();
			
			create.delete(SB_ACCOUNT_SERARCH_TB)
			.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(testID))
			.execute();
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(testID))
			.execute();
			
			conn.commit();
		
		} catch (Exception e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}

			log.warn(e.getMessage(), e);

			fail(e.getMessage());
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
		
		{
			
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "이메일테스터";
			

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, testID, nickname, email,
						passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(testID).append("] 입니다")
						.toString();
				String actualErrorMessag = e.getMessage();

				// log.warn(actualErrorMessag, e);

				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
		}		
		
		AccountSearchReadyReq passwordSearchReadyReq = new AccountSearchReadyReq();
		passwordSearchReadyReq.setEmail(email);
		passwordSearchReadyReq.setIp(ip);
		passwordSearchReadyReq.setAccountSearchType(AccountSearchType.ID.getValue());
		
		AccountSearchReadyReqServerTask passwordSearchReadyReqServerTask = null;
		try {
			passwordSearchReadyReqServerTask = new AccountSearchReadyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {			
			MessageResultRes messageResultRes = passwordSearchReadyReqServerTask.doWork(TEST_DBCP_NAME, passwordSearchReadyReq);
			log.info(messageResultRes.toString());
			if (! messageResultRes.getIsSuccess()) {
				fail("비밀번호 찾기 준비 실패");
			}			
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 준비 실패");
		}
	}
	
	/*
	 * @Test public void test() { String recipient = "k9200544@hanmail.net";
	 * 
	 * String subject = "Codda 에서 아이디 test01 이신 아리수 님깨 비밀번호 찾기에 필요한 비밀 값을 보내드립니다";
	 * String body = new StringBuilder() .append("<!DOCTYPE html>")
	 * .append(CommonStaticFinalVars.NEWLINE) .append("<html>")
	 * .append(CommonStaticFinalVars.NEWLINE) .append("<head>")
	 * .append(CommonStaticFinalVars.NEWLINE) .append("<meta charset=\"utf-8\">")
	 * .append(CommonStaticFinalVars.NEWLINE) .append("</head>")
	 * .append(CommonStaticFinalVars.NEWLINE) .append("<body>")
	 * .append(CommonStaticFinalVars.NEWLINE)
	 * .append("아이디 test01 이신 아리수 님 아이디/비밀번호 찾기를 통해 비밀 값 <b>12939393</b> 을 보내드립니다")
	 * .append(CommonStaticFinalVars.NEWLINE)
	 * .append("전송 받은 비밀 값을 입력하여 아디/비밀번호 찾기를 완료해 주시기 바랍니다")
	 * .append(CommonStaticFinalVars.NEWLINE) .append("</body>")
	 * .append(CommonStaticFinalVars.NEWLINE) .append("</html>").toString();
	 * 
	 * // Set<File> attachments = new HashSet<File>();
	 * 
	 * log.info(body);
	 * 
	 * try {
	 * 
	 * EmilUtil.sendEmail(recipient, subject, body);
	 * 
	 * } catch (Exception e) { log.warn("메일 보내기 실패", e);
	 * 
	 * fail("메일 보내기 실패"); }
	 * 
	 * }
	 */
}
