package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.impl.jooq.tables.SbUserActionHistoryTb.SB_USER_ACTION_HISTORY_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.MemberWithdrawReq.MemberWithdrawReq;
import kr.pe.codda.impl.message.UserBlockReq.UserBlockReq;
import kr.pe.codda.impl.message.UserInformationReq.UserInformationReq;
import kr.pe.codda.impl.message.UserInformationRes.UserInformationRes;
import kr.pe.codda.impl.message.UserUnBlockReq.UserUnBlockReq;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserIntegrationTest extends AbstractJunitTest {
	final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);
		
		{
			String userID = "admin";
			byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
			String nickname = "단위테스터용어드민";
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";
			
			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.ADMIN, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
						.append(userID)
						.append("] 입니다").toString();
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
			byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
			String nickname = "단위테스터용아이디1";
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";
			
			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
						.append(userID)
						.append("] 입니다").toString();
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
			byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
			String nickname = "단위테스터용아이디2";
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";
			
			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
						.append(userID)
						.append("] 입니다").toString();
				String actualErrorMessag = e.getMessage();
				
				// log.warn(actualErrorMessag, e);
				
				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
		}
	}
	
	@Before
	public void setUp() {
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_USER_ACTION_HISTORY_TB).execute();
			
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
	}
	
	@Test
	public void 회원가입_아이디중복() {
		String userID = "test01";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(userID).append("] 입니다").toString();
			
			assertEquals("아이디 중복 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
	}

	@Test
	public void 회원가입_별명중복() {
		String userID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디1";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(userID))
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
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = new StringBuilder("기존 회원과 중복되는 별명[").append(nickname).append("] 입니다").toString();
			
			assertEquals("별명 중복 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
	}
	
	@Test
	public void 회원가입_일반회원() {
		String userID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(userID))
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
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}		
		
		UserInformationReqServerTask userInformationReqServerTask = null;
		try {
			userInformationReqServerTask = new UserInformationReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		UserInformationReq userInformationReq = new UserInformationReq();
		userInformationReq.setRequestedUserID("test03");
		userInformationReq.setTargetUserID("test03");
		
		try {
			UserInformationRes userInformationRes = userInformationReqServerTask.doWork(TEST_DBCP_NAME, userInformationReq);
			
			assertEquals("별명 비교", nickname,  userInformationRes.getNickname());
			assertEquals("회원 상태 비교", MemberStateType.OK.getValue(),  userInformationRes.getState());
			assertEquals("회원 역활 비교", MemberRoleType.MEMBER.getValue(),  userInformationRes.getRole());
			assertEquals("비밀번호 분실시 힌트 비교", pwdHint,  userInformationRes.getPasswordHint());
			assertEquals("비밀번호 분실시 답변 비교", pwdAnswer,  userInformationRes.getPasswordAnswer());
			assertEquals("비밀번호 틀린 횟수 비교", 0,  userInformationRes.getPasswordFailedCount());
			assertEquals("ip address 비교", ip,  userInformationRes.getIp());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			assertEquals("등록일 비교", sdf.format(new java.util.Date()),  
					sdf.format(userInformationRes.getRegisteredDate()));
			assertEquals("수정일 비교", userInformationRes.getRegisteredDate(),  
					userInformationRes.getLastModifiedDate());
			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}		
	}
	
	@Test
	public void 회원가입_어드민() {
		String userID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(userID))
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
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.ADMIN, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		
		UserInformationReqServerTask userInformationReqServerTask = null;
		try {
			userInformationReqServerTask = new UserInformationReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		UserInformationReq userInformationReq = new UserInformationReq();
		userInformationReq.setRequestedUserID("test03");
		userInformationReq.setTargetUserID("test03");
		
		try {
			UserInformationRes userInformationRes = userInformationReqServerTask.doWork(TEST_DBCP_NAME, userInformationReq);
			
			assertEquals("별명 비교", nickname,  userInformationRes.getNickname());
			assertEquals("회원 상태 비교", MemberStateType.OK.getValue(),  userInformationRes.getState());
			assertEquals("회원 역활 비교", MemberRoleType.ADMIN.getValue(),  userInformationRes.getRole());
			assertEquals("비밀번호 분실시 힌트 비교", pwdHint,  userInformationRes.getPasswordHint());
			assertEquals("비밀번호 분실시 답변 비교", pwdAnswer,  userInformationRes.getPasswordAnswer());
			assertEquals("비밀번호 틀린 횟수 비교", 0,  userInformationRes.getPasswordFailedCount());
			assertEquals("ip address 비교", ip,  userInformationRes.getIp());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			assertEquals("등록일 비교", sdf.format(new java.util.Date()),  
					sdf.format(userInformationRes.getRegisteredDate()));
			assertEquals("수정일 비교", userInformationRes.getRegisteredDate(),  
					userInformationRes.getLastModifiedDate());
			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}		
	}
	
	@Test
	public void 회원차단_자기자신차단() {
		UserBlockReq userBlockReq = new UserBlockReq();
		userBlockReq.setRequestedUserID("test03");
		userBlockReq.setTargetUserID("test03");
		
		UserBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new UserBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = "자기 자신을 차단 할 수 없습니다";
			
			assertEquals("자기 자신 차단 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원차단_비관리자요청() {
		UserBlockReq userBlockReq = new UserBlockReq();
		userBlockReq.setRequestedUserID("test01");
		userBlockReq.setTargetUserID("test03");
		
		UserBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new UserBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = "사용자 차단은 관리자 전용 서비스입니다";
			
			assertEquals("요청자가 비 관리자일 경우 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원차단_대상자미존재() {
		UserBlockReq userBlockReq = new UserBlockReq();
		userBlockReq.setRequestedUserID("admin");
		userBlockReq.setTargetUserID("testAA");
		
		UserBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new UserBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = new StringBuilder("차단 대상 사용자[")
			.append(userBlockReq.getTargetUserID())
			.append("] 가 회원 테이블에 존재하지 않습니다").toString();
			
			assertEquals("존재하지 않은 차단 대상자 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원차단_관리자() {
		String userID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
	
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(userID))
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
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.ADMIN, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		UserBlockReq userBlockReq = new UserBlockReq();
		userBlockReq.setRequestedUserID("admin");
		userBlockReq.setTargetUserID("test03");
		
		UserBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new UserBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = "관리자 아이디는 차단 대상이 아닙니다";
			
			assertEquals("관리자를 차단하는 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}

	@Test
	public void 회원차단_비정상상태의차단대상자_블락() {		
		String userID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(userID))
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
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}		
		
		UserBlockReq userBlockReq = new UserBlockReq();
		userBlockReq.setRequestedUserID("admin");
		userBlockReq.setTargetUserID("test03");
		
		UserBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new UserBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}		
				
		UserInformationReq userInformationReq = new UserInformationReq();
		userInformationReq.setRequestedUserID("admin");
		userInformationReq.setTargetUserID("test03");
		
		UserInformationReqServerTask userInformationReqServerTask = null;
		try {
			userInformationReqServerTask = new UserInformationReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			UserInformationRes userInformationRes = userInformationReqServerTask.doWork(TEST_DBCP_NAME, userInformationReq);
			
			assertEquals("별명 비교", nickname,  userInformationRes.getNickname());
			assertEquals("회원 상태 비교", MemberStateType.BLOCK.getValue(),  userInformationRes.getState());
			assertEquals("회원 역활 비교", MemberRoleType.MEMBER.getValue(),  userInformationRes.getRole());
			assertEquals("비밀번호 분실시 힌트 비교", pwdHint,  userInformationRes.getPasswordHint());
			assertEquals("비밀번호 분실시 답변 비교", pwdAnswer,  userInformationRes.getPasswordAnswer());
			assertEquals("비밀번호 틀린 횟수 비교", 0,  userInformationRes.getPasswordFailedCount());
			assertEquals("ip address 비교", ip,  userInformationRes.getIp());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			assertEquals("등록일 비교", sdf.format(new java.util.Date()),  
					sdf.format(userInformationRes.getRegisteredDate()));
			assertEquals("수정일 비교", userInformationRes.getRegisteredDate(),  
					userInformationRes.getLastModifiedDate());
			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = new StringBuilder("차단 대상 사용자[")
			.append(userBlockReq.getTargetUserID())
			.append("] 상태[")
			.append(MemberStateType.BLOCK.getName())
			.append("]가 정상이 아닙니다").toString();
			
			assertEquals("요청자가 비 관리자일 경우 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
		
	}
	
	
	
	@Test
	public void 회원차단_정상() {
		String userID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(userID))
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
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		UserBlockReq userBlockReq = new UserBlockReq();
		userBlockReq.setRequestedUserID("admin");
		userBlockReq.setTargetUserID("test03");
		
		UserBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new UserBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);		
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
		
		UserInformationReq userInformationReq = new UserInformationReq();
		userInformationReq.setRequestedUserID("admin");
		userInformationReq.setTargetUserID("test03");
		
		UserInformationReqServerTask userInformationReqServerTask = null;
		try {
			userInformationReqServerTask = new UserInformationReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			UserInformationRes userInformationRes = userInformationReqServerTask.doWork(TEST_DBCP_NAME, userInformationReq);
			
			assertEquals("별명 비교", nickname,  userInformationRes.getNickname());
			assertEquals("회원 상태 비교", MemberStateType.BLOCK.getValue(),  userInformationRes.getState());
			assertEquals("회원 역활 비교", MemberRoleType.MEMBER.getValue(),  userInformationRes.getRole());
			assertEquals("비밀번호 분실시 힌트 비교", pwdHint,  userInformationRes.getPasswordHint());
			assertEquals("비밀번호 분실시 답변 비교", pwdAnswer,  userInformationRes.getPasswordAnswer());
			assertEquals("비밀번호 틀린 횟수 비교", 0,  userInformationRes.getPasswordFailedCount());
			assertEquals("ip address 비교", ip,  userInformationRes.getIp());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			assertEquals("등록일 비교", sdf.format(new java.util.Date()),  
					sdf.format(userInformationRes.getRegisteredDate()));
			assertEquals("수정일 비교", userInformationRes.getRegisteredDate(),  
					userInformationRes.getLastModifiedDate());
			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원차단해제_자기자시차단해제() {
		UserUnBlockReq userUnBlockReq = new UserUnBlockReq();
		userUnBlockReq.setRequestedUserID("test03");
		userUnBlockReq.setTargetUserID("test03");
		
		UserUnBlockReqServerTask userUnBlockReqServerTask = null;
		try {
			userUnBlockReqServerTask = new UserUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userUnBlockReqServerTask.doWork(TEST_DBCP_NAME, userUnBlockReq);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = "자기 자신을 차단 해제 할 수 없습니다";
			
			assertEquals("자기 자신 차단 해제 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원차단해제_비관리자요청() {
		UserUnBlockReq userUnBlockReq = new UserUnBlockReq();
		userUnBlockReq.setRequestedUserID("test01");
		userUnBlockReq.setTargetUserID("test03");
		
		UserUnBlockReqServerTask userUnBlockReqServerTask = null;
		try {
			userUnBlockReqServerTask = new UserUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userUnBlockReqServerTask.doWork(TEST_DBCP_NAME, userUnBlockReq);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = "사용자 차단 해제는 관리자 전용 서비스입니다";
			
			assertEquals("비관리자 요청 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원차단해제_대상자미존재() {
		UserUnBlockReq userUnBlockReq = new UserUnBlockReq();
		userUnBlockReq.setRequestedUserID("admin");
		userUnBlockReq.setTargetUserID("testAA");
		
		UserUnBlockReqServerTask userUnBlockReqServerTask = null;
		try {
			userUnBlockReqServerTask = new UserUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userUnBlockReqServerTask.doWork(TEST_DBCP_NAME, userUnBlockReq);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = new StringBuilder("차단 해제 대상 사용자[")
			.append(userUnBlockReq.getTargetUserID())
			.append("]가 회원 테이블에 존재하지 않습니다").toString();
			
			assertEquals("존재 하지 않는 차단 대상자 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원차단해제_관리자() {
		String userID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
	
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(userID))
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
	
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.ADMIN, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		UserUnBlockReq userUnBlockReq = new UserUnBlockReq();
		userUnBlockReq.setRequestedUserID("admin");
		userUnBlockReq.setTargetUserID(userID);
		
		UserUnBlockReqServerTask userUnBlockReqServerTask = null;
		try {
			userUnBlockReqServerTask = new UserUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userUnBlockReqServerTask.doWork(TEST_DBCP_NAME, userUnBlockReq);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = "관리자 아이디는 차단 대상이 아니므로 차단 해제 역시 대상이 아닙니다";
			
			assertEquals("관리자를 차단 해제하는 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}	
	}

	@Test
	public void 회원차단해제_차단상태가아닌차단해제대상자_정상() {
		String userID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(userID))
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
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		UserUnBlockReq userUnBlockReq = new UserUnBlockReq();
		userUnBlockReq.setRequestedUserID("admin");
		userUnBlockReq.setTargetUserID(userID);
		
		UserUnBlockReqServerTask userUnBlockReqServerTask = null;
		try {
			userUnBlockReqServerTask = new UserUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userUnBlockReqServerTask.doWork(TEST_DBCP_NAME, userUnBlockReq);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = new StringBuilder("차단 해제 대상 사용자[사용자아이디=")
			.append(userUnBlockReq.getTargetUserID())
			.append(", 상태=")
			.append(MemberStateType.OK.getName())
			.append("]는 차단된 사용자가 아닙니다").toString();
			
			assertEquals("차단 상태가 아닌 차단 해제 대상자 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원차단해제_정상() {
		String userID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(userID))
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
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		UserBlockReq userBlockReq = new UserBlockReq();
		userBlockReq.setRequestedUserID("admin");
		userBlockReq.setTargetUserID(userID);
		
		UserBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new UserBlockReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
		
		UserInformationReq userInformationReq = new UserInformationReq();
		userInformationReq.setRequestedUserID("admin");
		userInformationReq.setTargetUserID(userID);
		
		UserInformationReqServerTask userInformationReqServerTask = null;
		try {
			userInformationReqServerTask = new UserInformationReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			UserInformationRes userInformationRes = userInformationReqServerTask.doWork(TEST_DBCP_NAME, userInformationReq);
			
			assertEquals("회원 상태 비교", MemberStateType.BLOCK.getValue(),  userInformationRes.getState());
			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
		
		UserUnBlockReq userUnBlockReq = new UserUnBlockReq();
		userUnBlockReq.setRequestedUserID("admin");
		userUnBlockReq.setTargetUserID(userID);
		
		UserUnBlockReqServerTask userUnBlockReqServerTask = null;
		try {
			userUnBlockReqServerTask = new UserUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userUnBlockReqServerTask.doWork(TEST_DBCP_NAME, userUnBlockReq);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
		
		try {
			UserInformationRes userInformationRes = userInformationReqServerTask.doWork(TEST_DBCP_NAME, userInformationReq);
			
			assertEquals("회원 상태 비교", MemberStateType.OK.getValue(),  userInformationRes.getState());
			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원탈퇴_대상자미존재() {
		MemberWithdrawReq memberWithdrawReq = new MemberWithdrawReq();
		memberWithdrawReq.setRequestedUserID("testAA");
		
		MemberWithdrawReqServerTask memberWithdrawReqServerTask = null;
		try {
			memberWithdrawReqServerTask = new MemberWithdrawReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			memberWithdrawReqServerTask.doWork(TEST_DBCP_NAME, memberWithdrawReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = new StringBuilder("탈퇴 대상 사용자[")
			.append(memberWithdrawReq.getRequestedUserID())
			.append("] 가 회원 테이블에 존재하지 않습니다").toString();
			
			assertEquals("존재 하지 않은 회원 탈퇴 대상자 에러 검증", exepcedErrorMessage, acutalErrorMessage);			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원탈퇴_관리자() {
		MemberWithdrawReq memberWithdrawReq = new MemberWithdrawReq();
		memberWithdrawReq.setRequestedUserID("admin");
		
		MemberWithdrawReqServerTask memberWithdrawReqServerTask = null;
		try {
			memberWithdrawReqServerTask = new MemberWithdrawReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			memberWithdrawReqServerTask.doWork(TEST_DBCP_NAME, memberWithdrawReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = "관리자 아이디는 탈퇴 할 수 없습니다";
			
			assertEquals("관리자가 회원 탈퇴하는 에러 검증", exepcedErrorMessage, acutalErrorMessage);			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원탈퇴_비정상상태_차단() {
		String userID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(userID))
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
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		UserBlockReq userBlockReq = new UserBlockReq();
		userBlockReq.setRequestedUserID("admin");
		userBlockReq.setTargetUserID(userID);
		
		UserBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new UserBlockReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
		
		MemberWithdrawReq memberWithdrawReq = new MemberWithdrawReq();
		memberWithdrawReq.setRequestedUserID(userID);
		
		MemberWithdrawReqServerTask memberWithdrawReqServerTask = null;
		try {
			memberWithdrawReqServerTask = new MemberWithdrawReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			memberWithdrawReqServerTask.doWork(TEST_DBCP_NAME, memberWithdrawReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = new StringBuilder("탈퇴 대상 사용자[")
			.append(userID)
			.append("]의 상태[")
			.append(MemberStateType.BLOCK.getName())
			.append("]가 정상이 아닙니다").toString();
			
			assertEquals("비정상 상태인 회원 탈퇴 대상자 에러 검증", exepcedErrorMessage, acutalErrorMessage);			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원탈퇴_정상() {
		String userID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.delete(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(userID))
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
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
			
		
		MemberWithdrawReq memberWithdrawReq = new MemberWithdrawReq();
		memberWithdrawReq.setRequestedUserID(userID);
		
		MemberWithdrawReqServerTask memberWithdrawReqServerTask = null;
		try {
			memberWithdrawReqServerTask = new MemberWithdrawReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			memberWithdrawReqServerTask.doWork(TEST_DBCP_NAME, memberWithdrawReq);		
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
		
		UserInformationReq userInformationReq = new UserInformationReq();
		userInformationReq.setRequestedUserID("admin");
		userInformationReq.setTargetUserID(userID);
		
		UserInformationReqServerTask userInformationReqServerTask = null;
		try {
			userInformationReqServerTask = new UserInformationReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			UserInformationRes userInformationRes = userInformationReqServerTask.doWork(TEST_DBCP_NAME, userInformationReq);
			
			assertEquals("회원 상태 비교", MemberStateType.WITHDRAWAL.getValue(),  userInformationRes.getState());
			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
}
