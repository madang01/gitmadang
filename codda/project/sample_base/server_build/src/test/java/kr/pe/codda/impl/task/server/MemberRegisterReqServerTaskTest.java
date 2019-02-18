package kr.pe.codda.impl.task.server;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.MemberRegisterReq.MemberRegisterReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class MemberRegisterReqServerTaskTest extends AbstractJunitTest {	
	// final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.LOAD_TEST_DBCP_NAME;
	final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);
	}
	
	@Test
	public void testDoService_ok() {		
		String userID = "test01";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디";
		String pwdHint = "힌트 그것이 알고싶다";
		String pwdAnswer = "말이여 방구여";
		String ip = "127.0.0.1";
		
		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		
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
			idCipherTextBytes = clientSymmetricKey.encrypt(userID.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
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
		
		byte[] nicknameCipherTextBytes = null;
		try {
			nicknameCipherTextBytes = clientSymmetricKey.encrypt(nickname.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
		} catch (Exception e) {
			fail("fail to encrypt id");
		}
		
		byte[] pwdHintCipherTextBytes = null;
		try {
			pwdHintCipherTextBytes = clientSymmetricKey.encrypt(pwdHint.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
		} catch (Exception e) {
			fail("fail to encrypt id");
		}
		
		byte[] pwdAnswerCipherTextBytes = null;
		try {
			pwdAnswerCipherTextBytes = clientSymmetricKey.encrypt(pwdAnswer.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
		} catch (Exception e) {
			fail("fail to encrypt id");
		}
		
		MemberRegisterReq memberRegisterReq = new MemberRegisterReq();
		memberRegisterReq.setIdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(idCipherTextBytes));
		memberRegisterReq.setPwdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(passwordCipherTextBytes));
		memberRegisterReq.setNicknameCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(nicknameCipherTextBytes));
		memberRegisterReq.setHintCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(pwdHintCipherTextBytes));
		memberRegisterReq.setAnswerCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(pwdAnswerCipherTextBytes));
		memberRegisterReq.setIp(ip);
		memberRegisterReq.setSessionKeyBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupSessionKeyBytes()));
		memberRegisterReq.setIvBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSessionKey.getDupIVBytes()));
	
		MemberRegisterReqServerTask memberRegisterReqServerTask = null;
		try {
			memberRegisterReqServerTask = new MemberRegisterReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			@SuppressWarnings("unused")
			MessageResultRes messageResultRes = 
					memberRegisterReqServerTask.doWork(TEST_DBCP_NAME, memberRegisterReq);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
}
