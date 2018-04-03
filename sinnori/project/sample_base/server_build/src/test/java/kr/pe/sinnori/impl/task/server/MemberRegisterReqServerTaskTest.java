package kr.pe.sinnori.impl.task.server;

import static org.junit.Assert.fail;

import java.nio.channels.SocketChannel;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.mockito.Mockito;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyIF;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyManager;
import kr.pe.sinnori.impl.message.MemberRegisterReq.MemberRegisterReq;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class MemberRegisterReqServerTaskTest extends AbstractJunitTest {
	@Test
	public void testDoTask_ok() {
		class ToLetterCarrierMock extends ToLetterCarrier {

			public ToLetterCarrierMock(SocketChannel fromSC, AbstractMessage inputMessage,
					SocketResourceManagerIF socketResourceManager, PersonalLoginManagerIF personalMemberManager,
					MessageProtocolIF messageProtocol, ClassLoader classLoaderOfServerTask,
					ServerObjectCacheManagerIF serverObjectCacheManager) {
				super(fromSC, inputMessage, socketResourceManager, personalMemberManager, messageProtocol, classLoaderOfServerTask,
						serverObjectCacheManager);
			}

			public void addSyncOutputMessage(AbstractMessage syncOutputMessage) throws InterruptedException {
				if (! (syncOutputMessage instanceof MessageResultRes)) {
					fail("the parameter syncOutputMessage is not a instance of MessageResultRes class");
				}
				
				MessageResultRes messageResultRes = (MessageResultRes)syncOutputMessage;
				if (! messageResultRes.getIsSuccess()) {
					fail("fail to login");
				}
				
				log.info("member register success", syncOutputMessage.toString());
			}
		}
		PersonalLoginManagerIF personalLoginManagerMock = Mockito.mock(PersonalLoginManagerIF.class);				
		ToLetterCarrier toLetterCarrierMock = new ToLetterCarrierMock(null, null, null, null, null, null, null);
				
		String userID = "test00";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "임시가입자";
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
			idCipherTextBytes = clientSymmetricKey.encrypt(userID.getBytes(CommonStaticFinalVars.SINNORI_CIPHER_CHARSET));
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
			nicknameCipherTextBytes = clientSymmetricKey.encrypt(nickname.getBytes(CommonStaticFinalVars.SINNORI_CIPHER_CHARSET));
		} catch (Exception e) {
			fail("fail to encrypt id");
		}
		
		byte[] pwdHintCipherTextBytes = null;
		try {
			pwdHintCipherTextBytes = clientSymmetricKey.encrypt(pwdHint.getBytes(CommonStaticFinalVars.SINNORI_CIPHER_CHARSET));
		} catch (Exception e) {
			fail("fail to encrypt id");
		}
		
		byte[] pwdAnswerCipherTextBytes = null;
		try {
			pwdAnswerCipherTextBytes = clientSymmetricKey.encrypt(pwdAnswer.getBytes(CommonStaticFinalVars.SINNORI_CIPHER_CHARSET));
		} catch (Exception e) {
			fail("fail to encrypt id");
		}
		
		MemberRegisterReq inObj = new MemberRegisterReq();
		inObj.setIdCipherBase64(Base64.encodeBase64String(idCipherTextBytes));
		inObj.setPwdCipherBase64(Base64.encodeBase64String(passwordCipherTextBytes));
		inObj.setNicknameCipherBase64(Base64.encodeBase64String(nicknameCipherTextBytes));
		inObj.setHintCipherBase64(Base64.encodeBase64String(pwdHintCipherTextBytes));
		inObj.setAnswerCipherBase64(Base64.encodeBase64String(pwdAnswerCipherTextBytes));
		inObj.setSessionKeyBase64(Base64.encodeBase64String(clientSessionKey.getDupSessionKeyBytes()));
		inObj.setIvBase64(Base64.encodeBase64String(clientSessionKey.getDupIVBytes()));
	
		MemberRegisterReqServerTask memberRegisterReqServerTask= new MemberRegisterReqServerTask();
		
		try {
			memberRegisterReqServerTask.doTask(mainProjectName, 
					personalLoginManagerMock, toLetterCarrierMock, inObj);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
}
