package kr.pe.sinnori.impl.task.server;

import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyManager;
import kr.pe.sinnori.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BinaryPublicKeyServerTask extends AbstractServerTask {

	@Override
	public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		doWork(projectName, toLetterCarrier, (BinaryPublicKey)inputMessage);
		
	}
	private void doWork(String projectName,
			ToLetterCarrier toLetterCarrier, BinaryPublicKey binaryPublicKey)
			throws Exception {
		
		ServerSessionkeyIF serverSessionkey  = null;
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
			serverSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();			
		} catch (SymmetricException e) {
			log.warn("ServerSessionkeyManger instance init error, errormessage=[{}]", e.getMessage());
			
			String debugMessage = String.format("ServerSessionkeyManger instance init error, errormessage=[%s]", e.getMessage());
			
			MessageResult messageResult = new MessageResult();
			messageResult.setIsSuccess(false);
			messageResult.setTaskMessageID(binaryPublicKey.getMessageID());
			messageResult.setResultMessage(debugMessage);			
			
			toLetterCarrier.addSyncOutputMessage(messageResult);
			return;
		}
		
		
		binaryPublicKey.setPublicKeyBytes(serverSessionkey.getDupPublicKeyBytes());
		toLetterCarrier.addSyncOutputMessage(binaryPublicKey);
	}
}
