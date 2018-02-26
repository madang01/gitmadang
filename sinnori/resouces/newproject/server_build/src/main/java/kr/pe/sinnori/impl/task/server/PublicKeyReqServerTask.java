package kr.pe.sinnori.impl.task.server;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyManager;
import kr.pe.sinnori.impl.message.PublicKeyReq.PublicKeyReq;
import kr.pe.sinnori.impl.message.PublicKeyRes.PublicKeyRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class PublicKeyReqServerTask extends AbstractServerTask {

	@Override
	public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		doWork(projectName, personalLoginManager, toLetterCarrier, (PublicKeyReq)inputMessage);
	}

	public void doWork(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			PublicKeyReq publicKeyReq) throws Exception {
		PublicKeyRes publicKeyRes = new PublicKeyRes();
		byte[] publicKeyBytes = ServerSessionkeyManager.getInstance().getMainProjectServerSessionkey().getDupPublicKeyBytes();
		
		publicKeyRes.setPublicKeyBytes(publicKeyBytes);
		toLetterCarrier.addSyncOutputMessage(publicKeyRes);
	}	
	
}
