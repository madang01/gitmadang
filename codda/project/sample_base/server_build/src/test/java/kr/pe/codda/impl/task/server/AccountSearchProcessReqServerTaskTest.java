package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.jooq.tables.SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB;
import static kr.pe.codda.jooq.tables.SbSiteLogTb.SB_SITE_LOG_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.BeforeClass;
import org.junit.Test;

import jdk.nashorn.internal.ir.annotations.Ignore;
import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.AccountSearchReadyReq.AccountSearchReadyReq;
import kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReq;
import kr.pe.codda.impl.message.MemberLoginRes.MemberLoginRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.AccountSearchType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class AccountSearchProcessReqServerTaskTest extends AbstractJunitTest {
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
	public void 비밀번호찾기처리_존재하지않은회원() {
		String email = "nobody@codda.pe.kr";
		String ip = "127.0.0.1";

		AccountSearchProcessReqServerTask passwordSearchProcessReqServerTask = null;
		try {
			passwordSearchProcessReqServerTask = new AccountSearchProcessReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			passwordSearchProcessReqServerTask.doPasswordChangeProcess(TEST_DBCP_NAME, log, AccountSearchType.ID,
					email, "aa", null, ip);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = "입력한 이메일에 해당하는 일반 회원이 없습니다";

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 처리 실패");
		}
	}

	@Test
	public void 비밀번호찾기처리_준비단계없이호출된경우() {
		String email = "test02@codda.pe.kr";
		String ip = "127.0.0.1";

		AccountSearchProcessReqServerTask passwordSearchProcessReqServerTask = null;
		try {
			passwordSearchProcessReqServerTask = new AccountSearchProcessReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			passwordSearchProcessReqServerTask.doPasswordChangeProcess(TEST_DBCP_NAME, log, AccountSearchType.ID,
					email, "aa", null, ip);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = "아이디 혹은 비밀번호 찾기 준비 단계가 생략되었습니다";

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 처리 실패");
		}
	}

	@Test
	public void 비밀번호찾기처리_비밀값틀린횟수최대값초과() {
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
	}

	@Ignore
	public void 비밀번호찾기처리_시간초과() {
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
		
		try {
			Thread.sleep(ServerCommonStaticFinalVars.TIMEOUT_OF_PASSWORD_SEARCH_SERVICE);
		} catch (InterruptedException e) {
			log.warn("unknown error", e);
			fail("it failed to sleep");
		}
		
		AccountSearchProcessReqServerTask passwordSearchProcessReqServerTask = null;
		try {
			passwordSearchProcessReqServerTask = new AccountSearchProcessReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			passwordSearchProcessReqServerTask.doPasswordChangeProcess(TEST_DBCP_NAME, log, AccountSearchType.ID, 
					email, "aa", null, ip);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append("아이디 찾기에서 비밀 값 입력 제한 시간[")
					.append(ServerCommonStaticFinalVars.TIMEOUT_OF_PASSWORD_SEARCH_SERVICE)
					.append(" ms]을 초과하여 더 이상 진행할 수 없습니다, 처음 부터 다시 시작해 주시기 바랍니다").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 처리 실패");
		}
	}
	
	@Test
	public void 첫번째_로그인비밀번호최대횟수만큼틀림_두번째_비밀번호찾기성공_마지막_회원로그인_OK() {
		String testID = "test03";
		String email = "k9200544@hanmail.net";
		String ip = "127.0.0.1";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				create.delete(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(testID))
				.execute();
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(testID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'비밀번호 찾기 처리' 단위테스트를 위한 DB 환경 초기화 실패");
		}
		
		{
			
			byte[] passwordBytes = new byte[]{ (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
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
		
		
		byte passwordBytes[] = new byte[]{ (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
				(byte) '4', (byte) '*' };
		
		
		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		// serverSessionkeyManager.getMainProjectServerSessionkey().getDupPublicKeyBytes();
		
		ClientSessionKeyManager clientSessionKeyManager = ClientSessionKeyManager.getInstance();
		ClientSessionKeyIF clientSessionKey = null;
		try {
			clientSessionKey = clientSessionKeyManager
					.getNewClientSessionKey(serverSessionkeyManager.getMainProjectServerSessionkey().getDupPublicKeyBytes(), false);
		} catch (SymmetricException e) {
			fail("fail to get a ClientSessionKey");
		}
		
		ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
		
		byte[] idCipherTextBytes = null;
		try {
			idCipherTextBytes = clientSymmetricKey.encrypt(testID.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
		} catch (Exception e) {
			fail("fail to encrypt id");
		}
		byte[] passwordCipherTextBytes = null;
		
		try {
			passwordCipherTextBytes = clientSymmetricKey.encrypt(passwordBytes);
		} catch (Exception e) {
			fail("fail to encrypt password");
		}
		
		Arrays.fill(passwordBytes, CommonStaticFinalVars.ZERO_BYTE);
		
		MemberLoginReq inObj = new MemberLoginReq();
		inObj.setIdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(idCipherTextBytes));
		inObj.setPwdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(passwordCipherTextBytes));
		inObj.setSessionKeyBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupSessionKeyBytes()));
		inObj.setIvBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupIVBytes()));
		inObj.setIp(ip);
	
		MemberLoginReqServerTask loginReqServerTask = null;
		try {
			loginReqServerTask = new MemberLoginReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		for (int i=0; i < ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES; i++) {
			try {
				loginReqServerTask.doWork(TEST_DBCP_NAME, inObj);
				
				fail("no ServerServiceException");
			} catch(ServerServiceException e) {
				String errorMessage = e.getMessage();
				String expectedErrorMessage = new StringBuilder()
						.append(i+1).append(" 회 비밀 번호가 틀렸습니다").toString();
				
				assertEquals(expectedErrorMessage, errorMessage);
			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}
		}
		
		try {
			loginReqServerTask.doWork(TEST_DBCP_NAME, inObj);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder("최대 허용된 횟수[")
					.append(ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES)
					.append("]까지 비밀 번호가 틀려 더 이상 로그인 하실 수 없습니다").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
		
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
			MessageResultRes messageResultRes = passwordSearchReadyReqServerTask.doWork(TEST_DBCP_NAME, passwordSearchReadyReq);
			log.info(messageResultRes.toString());
			if (! messageResultRes.getIsSuccess()) {
				fail("비밀번호 찾기 준비 실패");
			}
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 준비 실패");
		}
		
		final HashMap<String, String> hash = new HashMap<String, String>();
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				Record1<String> passwordSearchReqRecord = create.select(SB_ACCOUNT_SERARCH_TB.LAST_SECRET_AUTH_VALUE)
				.from(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(testID))
				.fetchOne();
				
				String secretAuthenticationValue = passwordSearchReqRecord.get(SB_ACCOUNT_SERARCH_TB.LAST_SECRET_AUTH_VALUE);
							
				
				hash.put("secretAuthenticationValue", secretAuthenticationValue);
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'비밀번호 찾기 처리' 단위테스트를 위한 DB 환경 초기화 실패");
		}
		
		String secretAuthenticationValue = hash.get("secretAuthenticationValue");
		
		AccountSearchProcessReqServerTask passwordSearchProcessReqServerTask = null;
		try {
			passwordSearchProcessReqServerTask = new AccountSearchProcessReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		passwordBytes = new byte[]{ (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
				(byte) '4', (byte) '&' };
		
		try {
			passwordSearchProcessReqServerTask.doPasswordChangeProcess(TEST_DBCP_NAME, log, AccountSearchType.PASSWORD, 
					email, secretAuthenticationValue, passwordBytes, ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("비밀번호 찾기 처리 실패");
		}
		
		passwordBytes = new byte[]{ (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
				(byte) '4', (byte) '&' };
		
		try {
			passwordCipherTextBytes = clientSymmetricKey.encrypt(passwordBytes);
		} catch (Exception e) {
			fail("fail to encrypt password");
		}
		
		Arrays.fill(passwordBytes, CommonStaticFinalVars.ZERO_BYTE);
		
		inObj = new MemberLoginReq();
		inObj.setIdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(idCipherTextBytes));
		inObj.setPwdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(passwordCipherTextBytes));
		inObj.setSessionKeyBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupSessionKeyBytes()));
		inObj.setIvBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupIVBytes()));
		inObj.setIp(ip);
		
		try {
			MemberLoginRes memberLoginRes = loginReqServerTask.doWork(TEST_DBCP_NAME, inObj);
			log.info(memberLoginRes.toString());
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
}
