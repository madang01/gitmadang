package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.jooq.tables.SbSiteLogTb.SB_SITE_LOG_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.jooq.Record1;
import org.jooq.types.UByte;
import org.junit.BeforeClass;
import org.junit.Test;

import jdk.nashorn.internal.ir.annotations.Ignore;
import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.MemberBlockReq.MemberBlockReq;
import kr.pe.codda.impl.message.MemberLoginReq.MemberLoginReq;
import kr.pe.codda.impl.message.MemberLoginRes.MemberLoginRes;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class MemberLoginReqServerTaskTest extends AbstractJunitTest {
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
	public void 회원로그인_미존재아이디() {
		String notExistTestID = "test03";
		String ip = "127.0.0.1";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(notExistTestID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'비밀번호 찾기 처리' 단위테스트를 위한 DB 환경 초기화 실패");
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
			idCipherTextBytes = clientSymmetricKey.encrypt(notExistTestID.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
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
		
		try {
			loginReqServerTask.doWork(TEST_DBCP_NAME, inObj);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder("아이디[").append(notExistTestID).append("]가 존재하지 않습니다").toString();
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 회원로그인_손님() {
		String guestID = "guest";
		String ip = "127.0.0.1";
		
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
			idCipherTextBytes = clientSymmetricKey.encrypt(guestID.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
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
		
		try {
			loginReqServerTask.doWork(TEST_DBCP_NAME, inObj);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = "손님은 로그인 할 수 없습니다";
			
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}


	@Test
	public void 회원로그인_비밀번호실패_최대횟수초과후에도연속시도() {
		String testID = "test03";
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.1";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				create.delete(SB_SITE_LOG_TB)
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
	}
	


	@Test
	public void 회원로그인_비밀번호최대시도횟수실패바로전로그인_OK() {
		String testID = "test03";
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.1";
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				create.delete(SB_SITE_LOG_TB)
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
		
		for (int i=1; i < ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES; i++) {
			try {
				loginReqServerTask.doWork(TEST_DBCP_NAME, inObj);
				
				fail("no ServerServiceException");
			} catch(ServerServiceException e) {
				String errorMessage = e.getMessage();
				String expectedErrorMessage = new StringBuilder()
						.append(i).append(" 회 비밀 번호가 틀렸습니다").toString();
				
				assertEquals(expectedErrorMessage, errorMessage);
			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}
		}
		
		passwordBytes = new byte[]{ (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
				(byte) '4', (byte) '$' };
		
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
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				Record1<UByte> memberRecord = create.select(SB_MEMBER_TB.PWD_FAIL_CNT).from(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(testID)).fetchOne();
				
				conn.commit();
				
				UByte passwordFailCount = memberRecord.get(SB_MEMBER_TB.PWD_FAIL_CNT);

				assertEquals("비밀번호 로그인 성공에 따른 비밀번호 틀린횟수 0으로 초기화 확인", 0, passwordFailCount.shortValue());
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'비밀번호 찾기 처리' 단위테스트를 위한 DB 환경 초기화 실패");
		}
	}
	
	@Test
	public void 회원로그인_차단된유저() {
		String testID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";

		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(testID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원 로그인' 단위테스트를 위한 DB 환경 초기화 실패");
		}
				
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, testID, nickname, email, 
					passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		MemberBlockReq userBlockReq = new MemberBlockReq();
		userBlockReq.setRequestedUserID("admin");
		userBlockReq.setTargetUserID(testID);
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
		
		passwordBytes = new byte[]{(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		
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
		
		try {
			loginReqServerTask.doWork(TEST_DBCP_NAME, inObj);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder("차단된 회원[").append(testID).append("] 입니다").toString();

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 회원로그인_탈퇴한유저() {		
		String testID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";

		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				create.delete(SB_SITE_LOG_TB)
				.execute();
				
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(testID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원 로그인' 단위테스트를 위한 DB 환경 초기화 실패");
		}
				
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, testID, nickname, email, 
					passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		/** 회원 가입시 비밀번호 초기화가 이루어 지므로 값을 복구해 준다 */
		passwordBytes = new byte[]{(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		
		try {
			MemberWithdrawReqServerTask memberWithdrawReqServerTask = new MemberWithdrawReqServerTask();
			
			memberWithdrawReqServerTask.withdrawMember(TEST_DBCP_NAME, log, testID, passwordBytes, 
					new java.sql.Timestamp(System.currentTimeMillis()), ip);		
		} catch (Exception e)  {
			log.warn("unknown error", e);
			fail("회원 탈퇴 실패::errmsg="+e.getMessage());
		}
		
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
		
		passwordBytes = new byte[]{(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		
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
		
		try {
			loginReqServerTask.doWork(TEST_DBCP_NAME, inObj);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder("탈퇴한 회원[").append(testID).append("] 입니다").toString();

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 회원로그인_ok() {
		String testID = "test03";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디3";
		String email = "test03@codda.pe.kr";
		String ip = "127.0.0.3";

		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				create.delete(SB_SITE_LOG_TB)
				.execute();				
				
				create.delete(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(testID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn("unknwon error", e);
			fail("'회원 로그인' 단위테스트를 위한 DB 환경 초기화 실패");
		}
				
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, testID, nickname, email, 
					passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
		
		
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
		
		passwordBytes = new byte[]{(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		
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
		
		try {
			loginReqServerTask.doWork(TEST_DBCP_NAME, inObj);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
	
	/**
	 * 이 테스트는 여기가 아닌 '계정 찾기' 단위테스트에서 진행함, '계정찾기' 단위 테스트 참고 : {@link AccountSearchProcessReqServerTaskTest#첫번째_로그인비밀번호최대횟수만큼틀림_두번째_비밀번호찾기성공_마지막_회원로그인_OK}
	 * 
	 */
	@Ignore
	public void 첫번째_로그인비밀번호최대횟수만큼틀림_두번째_비밀번호찾기성공_마지막_회원로그인_OK() {
	}
}
