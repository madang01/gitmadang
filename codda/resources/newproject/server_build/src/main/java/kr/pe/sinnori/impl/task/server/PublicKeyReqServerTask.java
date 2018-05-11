package kr.pe.codda.impl.task.server;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.impl.message.PublicKeyReq.PublicKeyReq;
import kr.pe.codda.impl.message.PublicKeyRes.PublicKeyRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

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
