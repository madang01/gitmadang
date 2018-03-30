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
import kr.pe.sinnori.impl.message.LoginReq.LoginReq;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class LoginReqServerTaskTest extends AbstractJunitTest {
	
	/** FIXME! 미구현 */
	public void testDoTask_비밀번호실패() {
		
	}
	
	/** FIXME! 미구현 */
	public void testDoTask_비밀번호최대시도횟수실패바로전로그인성공() {
		
	}
	
	/** FIXME! 미구현 */
	public void testDoTask_비밀번호최대시도횟수실패후로그인() {
		
	}
	
	/** FIXME! 미구현 */
	public void testDoTask_잘못된회원종류값을가진유저로그인() {
		
	}
	
	/** FIXME! 미구현 */
	public void testDoTask_잘못된회원상태값을가진유저로그인() {
		
	}
	
	/** FIXME! 미구현 */
	public void testDoTask_블락된유저로그인() {
		
	}
	
	/** FIXME! 미구현 */
	public void testDoTask_미존재아이디() {
		
	}	
	
	/** FIXME! 미구현 */
	public void testDoTask_비밀번호잔재가있는지메모리덤프() {
		
	}	
	
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
				
				log.info("login success", syncOutputMessage.toString());
			}
		}
		PersonalLoginManagerIF personalLoginManagerMock = Mockito.mock(PersonalLoginManagerIF.class);				
		ToLetterCarrier toLetterCarrierMock = new ToLetterCarrierMock(null, null, null, null, null, null, null);
				
		String userID = "test00";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		
		
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
		
		LoginReq inObj = new LoginReq();
		inObj.setIdCipherBase64(Base64.encodeBase64String(idCipherTextBytes));
		inObj.setPwdCipherBase64(Base64.encodeBase64String(passwordCipherTextBytes));
		inObj.setSessionKeyBase64(Base64.encodeBase64String(clientSessionKey.getDupSessionKeyBytes()));
		inObj.setIvBase64(Base64.encodeBase64String(clientSessionKey.getDupIVBytes()));
	
		LoginReqServerTask loginReqServerTask= new LoginReqServerTask();
		
		try {
			loginReqServerTask.doTask(mainProjectName, 
					personalLoginManagerMock, toLetterCarrierMock, inObj);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
}
