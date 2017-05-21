package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyManager;
import kr.pe.sinnori.impl.message.PublicKeyReq.PublicKeyReq;
import kr.pe.sinnori.impl.message.PublicKeyRes.PublicKeyRes;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

public class PublicKeyReqServerTask extends AbstractServerTask {	
	
	@Override
	public void doTask(String projectName,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		doWork(projectName, letterSender, (PublicKeyReq)messageFromClient);
	}
	
	private void doWork(String projectName,
			LetterSender letterSender, PublicKeyReq echoInObj)
			throws Exception {		

		PublicKeyRes echoOutObj = new PublicKeyRes();

		byte[] publicKeyBytes = ServerSessionkeyManager.getInstance().getServerSessionkey().getDupPublicKeyBytes();
		echoOutObj.setPublicKeyBytes(publicKeyBytes);
		
		letterSender.addSyncMessage(echoOutObj);
	}	
}
