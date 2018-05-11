package kr.pe.codda.impl.task.server;

import static org.junit.Assert.fail;

import java.nio.channels.SocketChannel;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import kr.pe.codda.common.AbstractJunitTest;
import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.impl.message.LoginReq.LoginReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.ServerObjectCacheManagerIF;
import kr.pe.codda.server.SocketResourceManagerIF;
import kr.pe.codda.server.task.ToLetterCarrier;

public class LoginReqServerTaskTest extends AbstractJunitTest {
	
	@Before 
	public void setUp() throws Exception { 
		 
	}
	
	@After 
	public void tearDown() throws Exception { 
		 
	} 
	
	
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
					MessageProtocolIF messageProtocol, SimpleClassLoader classLoaderOfServerTask,
					ServerObjectCacheManagerIF serverObjectCacheManager) {
				super(fromSC, inputMessage, socketResourceManager, personalMemberManager, messageProtocol, classLoaderOfServerTask);
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
