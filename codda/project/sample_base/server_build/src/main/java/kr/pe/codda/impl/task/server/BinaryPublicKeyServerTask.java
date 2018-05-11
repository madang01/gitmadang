package kr.pe.codda.impl.task.server;

import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

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
			
			MessageResultRes messageResult = new MessageResultRes();
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
