package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB;
import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.jooq.tables.SbSiteLogTb.SB_SITE_LOG_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.MemberAllInformationReq.MemberAllInformationReq;
import kr.pe.codda.impl.message.MemberAllInformationRes.MemberAllInformationRes;
import kr.pe.codda.impl.message.MemberBlockReq.MemberBlockReq;
import kr.pe.codda.impl.message.MemberUnBlockReq.MemberUnBlockReq;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class MemberIntegrationTest extends AbstractJunitTest {
	final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	
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
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void 회원가입_아이디중복() {
		String userID = "test01";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String email = "test01@codda.pe.kr";
		String ip = "127.0.0.3";
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, 
					email, passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
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
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				create.delete(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID))
				.execute();
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원관련 차단및해제' 단위테스트를 위한 DB 환경 초기화 실패");
		}
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, email, passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
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
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				create.delete(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID))
				.execute();
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원관련 차단및해제' 단위테스트를 위한 DB 환경 초기화 실패");
		}
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, email, 
					passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
		} catch (Exception e) {
			fail("fail to create a test ID");
		}		
		
		MemberAllInformationReqServerTask MemberAllInformationReqServerTask = null;
		try {
			MemberAllInformationReqServerTask = new MemberAllInformationReqServerTask();
		} catch (DynamicClassCallException e1) {
			log.warn("DynamicClassCallException", e1);
			fail("dead code");
		}
		
		MemberAllInformationReq memberAllInformationReq = new MemberAllInformationReq();
		memberAllInformationReq.setRequestedUserID("test03");
		memberAllInformationReq.setTargetUserID("test03");
		
		try {
			MemberAllInformationRes userInformationRes = MemberAllInformationReqServerTask.doWork(TEST_DBCP_NAME, memberAllInformationReq);
			
			assertEquals("별명 비교", nickname,  userInformationRes.getNickname());
			assertEquals("회원 상태 비교", MemberStateType.OK.getValue(),  userInformationRes.getState());
			assertEquals("회원 역활 비교", MemberRoleType.MEMBER.getValue(),  userInformationRes.getRole());
			assertEquals("이메일 비교", email,  userInformationRes.getEmail());			
			assertEquals("비밀번호 틀린 횟수 비교", 0,  userInformationRes.getPasswordFailedCount());			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			assertEquals("등록일 비교", sdf.format(new java.util.Date()),  
					sdf.format(userInformationRes.getRegisteredDate()));
			
			assertEquals("마지막 별명 수정일  비교", userInformationRes.getRegisteredDate(),  
					userInformationRes.getLastNicknameModifiedDate());
			
			assertEquals("마지막 이메일 수정일  비교", userInformationRes.getRegisteredDate(),  
					userInformationRes.getLastEmailModifiedDate());
			
			assertEquals("마지막 비밀번호 수정일  비교", userInformationRes.getRegisteredDate(),  
					userInformationRes.getLastPasswordModifiedDate());
			
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
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				create.delete(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID))
				.execute();
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원관련 차단및해제' 단위테스트를 위한 DB 환경 초기화 실패");
		}

		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.ADMIN, userID, nickname, email, passwordBytes, 
					new java.sql.Timestamp(System.currentTimeMillis()), ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		
		MemberAllInformationReqServerTask MemberAllInformationReqServerTask = null;
		try {
			MemberAllInformationReqServerTask = new MemberAllInformationReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		MemberAllInformationReq memberAllInformationReq = new MemberAllInformationReq();
		memberAllInformationReq.setRequestedUserID("test03");
		memberAllInformationReq.setTargetUserID("test03");
		
		try {
			MemberAllInformationRes userInformationRes = MemberAllInformationReqServerTask.doWork(TEST_DBCP_NAME, memberAllInformationReq);
			
			assertEquals("별명 비교", nickname,  userInformationRes.getNickname());
			assertEquals("회원 상태 비교", MemberStateType.OK.getValue(),  userInformationRes.getState());
			assertEquals("회원 역활 비교", MemberRoleType.ADMIN.getValue(),  userInformationRes.getRole());
			assertEquals("이메일 비교", email,  userInformationRes.getEmail());
			assertEquals("비밀번호 틀린 횟수 비교", 0,  userInformationRes.getPasswordFailedCount());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			assertEquals("등록일 비교", sdf.format(new java.util.Date()),  
					sdf.format(userInformationRes.getRegisteredDate()));
			
			assertEquals("마지막 별명 수정일  비교", userInformationRes.getRegisteredDate(),  
					userInformationRes.getLastNicknameModifiedDate());
			
			assertEquals("마지막 이메일 수정일  비교", userInformationRes.getRegisteredDate(),  
					userInformationRes.getLastEmailModifiedDate());
			
			assertEquals("마지막 비밀번호 수정일  비교", userInformationRes.getRegisteredDate(),  
					userInformationRes.getLastPasswordModifiedDate());
			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}		
	}
	
	@Test
	public void 회원차단_자기자신차단() {
		MemberBlockReq userBlockReq = new MemberBlockReq();
		userBlockReq.setRequestedUserID("test03");
		userBlockReq.setTargetUserID("test03");
		userBlockReq.setIp("127.0.0.3");
		
		MemberBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new MemberBlockReqServerTask();
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
		MemberBlockReq userBlockReq = new MemberBlockReq();
		userBlockReq.setRequestedUserID("test01");
		userBlockReq.setTargetUserID("test03");
		userBlockReq.setIp("127.0.0.3");
		
		MemberBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new MemberBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = "회원 차단 서비스는 관리자 전용 서비스입니다";
			
			assertEquals("요청자가 비 관리자일 경우 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원차단_대상자미존재() {
		MemberBlockReq userBlockReq = new MemberBlockReq();
		userBlockReq.setRequestedUserID("admin");
		userBlockReq.setTargetUserID("testAA");
		userBlockReq.setIp("127.0.0.3");
		
		MemberBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new MemberBlockReqServerTask();
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
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				create.delete(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID))
				.execute();
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원관련 차단및해제' 단위테스트를 위한 DB 환경 초기화 실패");
		}
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.ADMIN, userID, nickname, email, 
					passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		MemberBlockReq userBlockReq = new MemberBlockReq();
		userBlockReq.setRequestedUserID("admin");
		userBlockReq.setTargetUserID("test03");
		userBlockReq.setIp(ip);
		
		MemberBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new MemberBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			log.info("DynamicClassCallException", e1);
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
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				create.delete(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID))
				.execute();
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원관련 차단및해제' 단위테스트를 위한 DB 환경 초기화 실패");
		}
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, email, 
					passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}		
		
		MemberBlockReq userBlockReq = new MemberBlockReq();
		userBlockReq.setRequestedUserID("admin");
		userBlockReq.setTargetUserID("test03");
		userBlockReq.setIp(ip);
		
		MemberBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new MemberBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}		
				
		MemberAllInformationReq memberAllInformationReq = new MemberAllInformationReq();
		memberAllInformationReq.setRequestedUserID("admin");
		memberAllInformationReq.setTargetUserID("test03");
		
		MemberAllInformationReqServerTask MemberAllInformationReqServerTask = null;
		try {
			MemberAllInformationReqServerTask = new MemberAllInformationReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			MemberAllInformationRes userInformationRes = MemberAllInformationReqServerTask.doWork(TEST_DBCP_NAME, memberAllInformationReq);
			
			assertEquals("별명 비교", nickname,  userInformationRes.getNickname());
			assertEquals("회원 상태 비교", MemberStateType.BLOCK.getValue(),  userInformationRes.getState());
			assertEquals("회원 역활 비교", MemberRoleType.MEMBER.getValue(),  userInformationRes.getRole());
			assertEquals("이메일 비교", email,  userInformationRes.getEmail());
			assertEquals("비밀번호 틀린 횟수 비교", 0,  userInformationRes.getPasswordFailedCount());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			assertEquals("등록일 비교", sdf.format(new java.util.Date()),  
					sdf.format(userInformationRes.getRegisteredDate()));
			
			assertEquals("마지막 별명 수정일  비교", userInformationRes.getRegisteredDate(),  
					userInformationRes.getLastNicknameModifiedDate());
			
			assertEquals("마지막 이메일 수정일  비교", userInformationRes.getRegisteredDate(),  
					userInformationRes.getLastEmailModifiedDate());
			
			assertEquals("마지막 비밀번호 수정일  비교", userInformationRes.getRegisteredDate(),  
					userInformationRes.getLastPasswordModifiedDate());
			
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
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";
		
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				create.delete(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID))
				.execute();
				
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원관련 차단및해제' 단위테스트를 위한 DB 환경 초기화 실패");
		}
				
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, email, 
					passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		MemberBlockReq userBlockReq = new MemberBlockReq();
		userBlockReq.setRequestedUserID("admin");
		userBlockReq.setTargetUserID("test03");
		userBlockReq.setIp(ip);
		
		MemberBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new MemberBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);		
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
		
		MemberAllInformationReq memberAllInformationReq = new MemberAllInformationReq();
		memberAllInformationReq.setRequestedUserID("admin");
		memberAllInformationReq.setTargetUserID("test03");
		
		MemberAllInformationReqServerTask MemberAllInformationReqServerTask = null;
		try {
			MemberAllInformationReqServerTask = new MemberAllInformationReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			MemberAllInformationRes userInformationRes = MemberAllInformationReqServerTask.doWork(TEST_DBCP_NAME, memberAllInformationReq);
			
			assertEquals("별명 비교", nickname,  userInformationRes.getNickname());
			assertEquals("회원 상태 비교", MemberStateType.BLOCK.getValue(),  userInformationRes.getState());
			assertEquals("회원 역활 비교", MemberRoleType.MEMBER.getValue(),  userInformationRes.getRole());
			assertEquals("이메일 비교", email,  userInformationRes.getEmail());
			assertEquals("비밀번호 틀린 횟수 비교", 0,  userInformationRes.getPasswordFailedCount());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			assertEquals("등록일 비교", sdf.format(new java.util.Date()),  
					sdf.format(userInformationRes.getRegisteredDate()));			
			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원차단해제_자기자시차단해제() {
		MemberUnBlockReq userUnBlockReq = new MemberUnBlockReq();
		userUnBlockReq.setRequestedUserID("test03");
		userUnBlockReq.setTargetUserID("test03");
		userUnBlockReq.setIp("127.0.0.3");
		
		MemberUnBlockReqServerTask userUnBlockReqServerTask = null;
		try {
			userUnBlockReqServerTask = new MemberUnBlockReqServerTask();
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
		MemberUnBlockReq userUnBlockReq = new MemberUnBlockReq();
		userUnBlockReq.setRequestedUserID("test01");
		userUnBlockReq.setTargetUserID("test03");
		userUnBlockReq.setIp("127.0.0.3");
		
		MemberUnBlockReqServerTask userUnBlockReqServerTask = null;
		try {
			userUnBlockReqServerTask = new MemberUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			userUnBlockReqServerTask.doWork(TEST_DBCP_NAME, userUnBlockReq);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = "회원 차단 해제 서비스는 관리자 전용 서비스입니다";
			
			assertEquals("비관리자 요청 에러 검증", exepcedErrorMessage, acutalErrorMessage);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원차단해제_대상자미존재() {
		MemberUnBlockReq userUnBlockReq = new MemberUnBlockReq();
		userUnBlockReq.setRequestedUserID("admin");
		userUnBlockReq.setTargetUserID("testAA");
		userUnBlockReq.setIp("127.0.0.3");
		
		MemberUnBlockReqServerTask userUnBlockReqServerTask = null;
		try {
			userUnBlockReqServerTask = new MemberUnBlockReqServerTask();
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
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				create.delete(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID))
				.execute();
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원관련 차단및해제' 단위테스트를 위한 DB 환경 초기화 실패");
		}
	
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.ADMIN, userID, nickname, email, 
					passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		MemberUnBlockReq userUnBlockReq = new MemberUnBlockReq();
		userUnBlockReq.setRequestedUserID("admin");
		userUnBlockReq.setTargetUserID(userID);
		userUnBlockReq.setIp(ip);
		
		MemberUnBlockReqServerTask userUnBlockReqServerTask = null;
		try {
			userUnBlockReqServerTask = new MemberUnBlockReqServerTask();
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
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				create.delete(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID))
				.execute();
				
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원관련 차단및해제' 단위테스트를 위한 DB 환경 초기화 실패");
		}
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, email, 
					passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		MemberUnBlockReq userUnBlockReq = new MemberUnBlockReq();
		userUnBlockReq.setRequestedUserID("admin");
		userUnBlockReq.setTargetUserID(userID);
		userUnBlockReq.setIp(ip);
		
		MemberUnBlockReqServerTask userUnBlockReqServerTask = null;
		try {
			userUnBlockReqServerTask = new MemberUnBlockReqServerTask();
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
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				create.delete(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID))
				.execute();
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원관련 차단및해제' 단위테스트를 위한 DB 환경 초기화 실패");
		}
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, email, 
					passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		MemberBlockReq userBlockReq = new MemberBlockReq();
		userBlockReq.setRequestedUserID("admin");
		userBlockReq.setTargetUserID(userID);
		userBlockReq.setIp(ip);
		
		MemberBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new MemberBlockReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
		
		MemberAllInformationReq memberAllInformationReq = new MemberAllInformationReq();
		memberAllInformationReq.setRequestedUserID("admin");
		memberAllInformationReq.setTargetUserID(userID);
		
		MemberAllInformationReqServerTask MemberAllInformationReqServerTask = null;
		try {
			MemberAllInformationReqServerTask = new MemberAllInformationReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			MemberAllInformationRes userInformationRes = MemberAllInformationReqServerTask.doWork(TEST_DBCP_NAME, memberAllInformationReq);
			
			assertEquals("회원 상태 비교", MemberStateType.BLOCK.getValue(),  userInformationRes.getState());
			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
		
		MemberUnBlockReq userUnBlockReq = new MemberUnBlockReq();
		userUnBlockReq.setRequestedUserID("admin");
		userUnBlockReq.setTargetUserID(userID);
		userUnBlockReq.setIp(ip);
		
		MemberUnBlockReqServerTask userUnBlockReqServerTask = null;
		try {
			userUnBlockReqServerTask = new MemberUnBlockReqServerTask();
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
			MemberAllInformationRes userInformationRes = MemberAllInformationReqServerTask.doWork(TEST_DBCP_NAME, memberAllInformationReq);
			
			assertEquals("회원 상태 비교", MemberStateType.OK.getValue(),  userInformationRes.getState());
			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원탈퇴_대상자미존재() {
		String requestedUserID = "testAA";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String ip = "127.0.0.4";
		
		try {
			MemberWithdrawReqServerTask memberWithdrawReqServerTask = new MemberWithdrawReqServerTask();
			
			memberWithdrawReqServerTask.withdrawMember(TEST_DBCP_NAME, log, requestedUserID, passwordBytes, 
					new java.sql.Timestamp(System.currentTimeMillis()), ip);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = new StringBuilder("회원 탈퇴 요청자[")
			.append(requestedUserID)
			.append("]가 회원 테이블에 존재하지 않습니다").toString();
			
			assertEquals("존재 하지 않은 회원 탈퇴 대상자 에러 검증", exepcedErrorMessage, acutalErrorMessage);			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
	
	@Test
	public void 회원탈퇴_관리자() {
		String requestedUserID = "admin";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String ip = "127.0.0.4";
		
		try {
			MemberWithdrawReqServerTask memberWithdrawReqServerTask = new MemberWithdrawReqServerTask();
			
			memberWithdrawReqServerTask.withdrawMember(TEST_DBCP_NAME, log, requestedUserID, passwordBytes, 
					new java.sql.Timestamp(System.currentTimeMillis()), ip);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String exepcedErrorMessage = "회원 탈퇴 요청자[역활:관리자]가 일반 회원이 아닙니다";
			
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
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				create.delete(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID))
				.execute();
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원관련 차단및해제' 단위테스트를 위한 DB 환경 초기화 실패");
		}
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, email, 
					passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		MemberBlockReq userBlockReq = new MemberBlockReq();
		userBlockReq.setRequestedUserID("admin");
		userBlockReq.setTargetUserID(userID);
		userBlockReq.setIp(ip);
		
		MemberBlockReqServerTask userBlockReqServerTask = null;
		try {
			userBlockReqServerTask = new MemberBlockReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}		
		try {
			userBlockReqServerTask.doWork(TEST_DBCP_NAME, userBlockReq);
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
		
		passwordBytes = new byte[]{(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		
		try {
			MemberWithdrawReqServerTask memberWithdrawReqServerTask = new MemberWithdrawReqServerTask();
			
			memberWithdrawReqServerTask.withdrawMember(TEST_DBCP_NAME, log, userID, passwordBytes, 
					new java.sql.Timestamp(System.currentTimeMillis()), ip);
			
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
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				create.delete(SB_ACCOUNT_SERARCH_TB)
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID))
				.execute();
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원관련 차단및해제' 단위테스트를 위한 DB 환경 초기화 실패");
		}
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, email, 
					passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		
		/** 회원 가입시 비밀번호 초기화가 이루어 지므로 값을 복구해 준다 */
		passwordBytes = new byte[]{(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		
		try {
			MemberWithdrawReqServerTask memberWithdrawReqServerTask = new MemberWithdrawReqServerTask();
			
			memberWithdrawReqServerTask.withdrawMember(TEST_DBCP_NAME, log, userID, passwordBytes, 
					new java.sql.Timestamp(System.currentTimeMillis()), ip);		
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("회원 탈퇴 실패::errmsg="+e.getMessage());
		}
		
		MemberAllInformationReq memberAllInformationReq = new MemberAllInformationReq();
		memberAllInformationReq.setRequestedUserID("admin");
		memberAllInformationReq.setTargetUserID(userID);
		
		MemberAllInformationReqServerTask MemberAllInformationReqServerTask = null;
		try {
			MemberAllInformationReqServerTask = new MemberAllInformationReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			MemberAllInformationRes userInformationRes = MemberAllInformationReqServerTask.doWork(TEST_DBCP_NAME, memberAllInformationReq);
			
			assertEquals("회원 상태 비교", MemberStateType.WITHDRAWAL.getValue(),  userInformationRes.getState());
			
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("사용자 정보 조회 실패::errmsg="+e.getMessage());
		}
	}
}
