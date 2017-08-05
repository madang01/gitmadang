package kr.pe.sinnori.impl.servertask;

import java.util.Arrays;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyManager;
import kr.pe.sinnori.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.sinnori.common.util.HexUtil;
import kr.pe.sinnori.impl.message.LoginReq.LoginReq;
import kr.pe.sinnori.impl.message.LoginRes.LoginRes;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

public class LoginReqServerTask extends AbstractServerTask {	
	
	@Override
	public void doTask(String projectName,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		doWork(projectName, loginManager, letterSender, (LoginReq)messageFromClient);
	}
	
	private void doWork(String projectName,
			LoginManagerIF loginManager,
			LetterSender letterSender, LoginReq loginReq)
			throws Exception {
		
		byte[] idEncryptedBytes = loginReq.getIdEncryptedBytes();
		byte[] pwdEncryptedBytes = loginReq.getPwdEncryptedBytes();
		byte[] sessionkeyBytes = loginReq.getSessionKeyBytes();
		byte[] ivBytes = loginReq.getIvBytes();
		
		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		
		ServerSessionkeyIF  serverSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
		
		ServerSymmetricKeyIF serverSymmetricKey = serverSessionkey.getNewInstanceOfServerSymmetricKey(sessionkeyBytes, ivBytes);
		
		byte[] idBytes = serverSymmetricKey.decrypt(idEncryptedBytes);
		byte[] pwdBytes = serverSymmetricKey.decrypt(pwdEncryptedBytes);
		
		String id = new String(idBytes, CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);
		
		LoginRes loginRes = new LoginRes();
		loginRes.setIsSuccess("Y");
		loginRes.setErrorMessage("테스트 아이디로 로그인 성공하셨습니다.");
		
		if (!id.equals("test01")) {
			loginRes.setIsSuccess("N");
			loginRes.setErrorMessage(String.format("테스트 아이디 'test01' 이 아닙니다. paramter id=[%s]", id));
			return;
		}
		
		byte[] wantedPwdBytes = "test1234!".getBytes(CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);
		if (!Arrays.equals(pwdBytes, wantedPwdBytes)) {
			loginRes.setIsSuccess("N");
			loginRes.setErrorMessage(String.format("테스트 아이디 'test01' 의 비밀번호가 틀렸습니다. parameter pwd=[%s]", HexUtil.getHexStringFromByteArray(pwdBytes)));
			return;
		}
		
		letterSender.addSyncMessage(loginRes);
		loginManager.doLoginSuccess(id, letterSender.getClientResource());
	}	

}
