package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.MemberAllInformationReq.MemberAllInformationReq;
import kr.pe.codda.impl.message.MemberAllInformationRes.MemberAllInformationRes;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class MemberAllInformationReqServerTaskTest extends AbstractJunitTest {
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
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.ADMIN, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip, new java.sql.Timestamp(System.currentTimeMillis()));
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
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip, new java.sql.Timestamp(System.currentTimeMillis()));
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
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip, new java.sql.Timestamp(System.currentTimeMillis()));
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
			
			create.delete(SB_MEMBER_ACTIVITY_HISTORY_TB).execute();
			
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
		MemberAllInformationReqServerTask memberAllInformationReqServerTask = null;
		try {
			memberAllInformationReqServerTask = new MemberAllInformationReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		MemberAllInformationReq memberAllInformationReq = new MemberAllInformationReq();
		memberAllInformationReq.setRequestedUserID("test01");
		memberAllInformationReq.setTargetUserID("test02");
		
		try {
			memberAllInformationReqServerTask.doWork(TEST_DBCP_NAME, memberAllInformationReq);
			
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
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip, new java.sql.Timestamp(System.currentTimeMillis()));
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		try {
			MemberAllInformationReqServerTask memberAllInformationReqServerTask = null;
			try {
				memberAllInformationReqServerTask = new MemberAllInformationReqServerTask();
			} catch (DynamicClassCallException e1) {
				fail("dead code");
			}
			
			MemberAllInformationReq memberAllInformationReq = new MemberAllInformationReq();
			memberAllInformationReq.setRequestedUserID("test03");
			memberAllInformationReq.setTargetUserID("test03");
			
			try {
				MemberAllInformationRes memberAllInformationRes = memberAllInformationReqServerTask.doWork(TEST_DBCP_NAME, memberAllInformationReq);
				
				assertEquals("별명 비교", nickname,  memberAllInformationRes.getNickname());
				assertEquals("회원 상태 비교", MemberStateType.OK.getValue(),  memberAllInformationRes.getState());
				assertEquals("회원 역활 비교", MemberRoleType.MEMBER.getValue(),  memberAllInformationRes.getRole());
				assertEquals("비밀번호 분실시 힌트 비교", pwdHint,  memberAllInformationRes.getPasswordHint());
				assertEquals("비밀번호 분실시 답변 비교", pwdAnswer,  memberAllInformationRes.getPasswordAnswer());
				assertEquals("비밀번호 틀린 횟수 비교", 0,  memberAllInformationRes.getPasswordFailedCount());
				assertEquals("ip address 비교", ip,  memberAllInformationRes.getIp());
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				
				assertEquals("등록일 비교", sdf.format(new java.util.Date()),  
						sdf.format(memberAllInformationRes.getRegisteredDate()));
				assertEquals("수정일 비교", memberAllInformationRes.getRegisteredDate(),  
						memberAllInformationRes.getLastModifiedDate());
				
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
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip, new java.sql.Timestamp(System.currentTimeMillis()));
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		MemberAllInformationReqServerTask memberAllInformationReqServerTask = null;
		try {
			memberAllInformationReqServerTask = new MemberAllInformationReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}
		
		MemberAllInformationReq memberAllInformationReq = new MemberAllInformationReq();
		memberAllInformationReq.setRequestedUserID("admin");
		memberAllInformationReq.setTargetUserID("test03");
		
		try {
			try {
				MemberAllInformationRes memberAllInformationRes = memberAllInformationReqServerTask.doWork(TEST_DBCP_NAME, memberAllInformationReq);
				
				assertEquals("별명 비교", nickname,  memberAllInformationRes.getNickname());
				assertEquals("회원 상태 비교", MemberStateType.OK.getValue(),  memberAllInformationRes.getState());
				assertEquals("회원 역활 비교", MemberRoleType.MEMBER.getValue(),  memberAllInformationRes.getRole());
				assertEquals("비밀번호 분실시 힌트 비교", pwdHint,  memberAllInformationRes.getPasswordHint());
				assertEquals("비밀번호 분실시 답변 비교", pwdAnswer,  memberAllInformationRes.getPasswordAnswer());
				assertEquals("비밀번호 틀린 횟수 비교", 0,  memberAllInformationRes.getPasswordFailedCount());
				assertEquals("ip address 비교", ip,  memberAllInformationRes.getIp());
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				
				assertEquals("등록일 비교", sdf.format(new java.util.Date()),  
						sdf.format(memberAllInformationRes.getRegisteredDate()));
				assertEquals("수정일 비교", memberAllInformationRes.getRegisteredDate(),  
						memberAllInformationRes.getLastModifiedDate());
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
