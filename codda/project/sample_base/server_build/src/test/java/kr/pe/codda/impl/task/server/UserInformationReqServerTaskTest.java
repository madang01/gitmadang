package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbUserActionHistoryTb.SB_USER_ACTION_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.UserInformationReq.UserInformationReq;
import kr.pe.codda.impl.message.UserInformationRes.UserInformationRes;
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

public class UserInformationReqServerTaskTest extends AbstractJunitTest {
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
				
				log.warn(actualErrorMessag, e);
				
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
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.USER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
						.append(userID)
						.append("] 입니다").toString();
				String actualErrorMessag = e.getMessage();
				
				log.warn(actualErrorMessag, e);
				
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
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.USER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
						.append(userID)
						.append("] 입니다").toString();
				String actualErrorMessag = e.getMessage();
				
				log.warn(actualErrorMessag, e);
				
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
	public void 사용자정보조회_일반유저_타인() {
		UserInformationReqServerTask userInformationReqServerTask 
		= new UserInformationReqServerTask();
		
		UserInformationReq userInformationReq = new UserInformationReq();
		userInformationReq.setRequestedUserID("test01");
		userInformationReq.setTargetUserID("test02");
		
		try {
			userInformationReqServerTask.doWork(TEST_DBCP_NAME, userInformationReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e)  {
			String acutalErrorMessage = e.getMessage();
			//log.info(acutalErrorMessage);
			String expectedErrorMessage = "타인의 사용자 정보는 검색할 수 없습니다";
			assertEquals("비 어드민(=일반유저)의 타인 정보 조회시 에러로 처리하는지 검사",  expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 사용자정보조회_일반유저_본인() {
		String userID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.USER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		try {
			UserInformationReqServerTask userInformationReqServerTask 
			= new UserInformationReqServerTask();
			
			UserInformationReq userInformationReq = new UserInformationReq();
			userInformationReq.setRequestedUserID("test03");
			userInformationReq.setTargetUserID("test03");
			
			try {
				UserInformationRes userInformationRes = userInformationReqServerTask.doWork(TEST_DBCP_NAME, userInformationReq);
				
				assertEquals("별명 비교", nickname,  userInformationRes.getNickname());
				assertEquals("회원 상태 비교", MemberStateType.OK.getValue(),  userInformationRes.getState());
				assertEquals("회원 역활 비교", MemberRoleType.USER.getValue(),  userInformationRes.getRole());
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
		} finally {
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
				.where(SB_MEMBER_TB.USER_ID.eq("test03"))
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
		}
	}
	
	@Test
	public void 사용자정보조회_어드민() {
		String userID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String pwdHint = "힌트 그것이 알고싶다3";
		String pwdAnswer = "힌트답변 말이여 방구여3";
		String ip = "127.0.0.3";		
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.USER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		UserInformationReqServerTask userInformationReqServerTask 
		= new UserInformationReqServerTask();
		
		UserInformationReq userInformationReq = new UserInformationReq();
		userInformationReq.setRequestedUserID("admin");
		userInformationReq.setTargetUserID("test03");
		
		try {
			try {
				UserInformationRes userInformationRes = userInformationReqServerTask.doWork(TEST_DBCP_NAME, userInformationReq);
				
				assertEquals("별명 비교", nickname,  userInformationRes.getNickname());
				assertEquals("회원 상태 비교", MemberStateType.OK.getValue(),  userInformationRes.getState());
				assertEquals("회원 역활 비교", MemberRoleType.USER.getValue(),  userInformationRes.getRole());
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
		} finally {
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
				.where(SB_MEMBER_TB.USER_ID.eq("test03"))
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
		}
	}
}
