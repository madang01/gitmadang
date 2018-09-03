package kr.pe.codda.impl.task.server;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.impl.message.MemberRegisterReq.MemberRegisterReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;

public class MemberRegisterReqServerTaskTest extends AbstractJunitTest {	
	final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME;
	
	@Test
	public void testDoService_ok() {		
		String userID = "test01";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디";
		String pwdHint = "힌트 그것이 알고싶다";
		String pwdAnswer = "말이여 방구여";
		
		
		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		// serverSessionkeyManager.getMainProjectServerSessionkey().getDupPublicKeyBytes();
		
		ClientSessionKeyManager clientSessionKeyManager = ClientSessionKeyManager.getInstance();
		ClientSessionKeyIF clientSessionKey = null;
		try {
			clientSessionKey = clientSessionKeyManager
					.getNewClientSessionKey(serverSessionkeyManager.getMainProjectServerSessionkey().getDupPublicKeyBytes());
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
		memberRegisterReq.setIdCipherBase64(Base64.encodeBase64String(idCipherTextBytes));
		memberRegisterReq.setPwdCipherBase64(Base64.encodeBase64String(passwordCipherTextBytes));
		memberRegisterReq.setNicknameCipherBase64(Base64.encodeBase64String(nicknameCipherTextBytes));
		memberRegisterReq.setHintCipherBase64(Base64.encodeBase64String(pwdHintCipherTextBytes));
		memberRegisterReq.setAnswerCipherBase64(Base64.encodeBase64String(pwdAnswerCipherTextBytes));
		memberRegisterReq.setSessionKeyBase64(Base64.encodeBase64String(clientSessionKey.getDupSessionKeyBytes()));
		memberRegisterReq.setIvBase64(Base64.encodeBase64String(clientSessionKey.getDupIVBytes()));
	
		MemberRegisterReqServerTask memberRegisterReqServerTask= new MemberRegisterReqServerTask();
		
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
