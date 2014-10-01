package kr.pe.sinnori.impl.message.MemberSessionKey;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

public class MemberSessionKeyServerTask extends AbstractServerTask {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager, LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception {
		MemberSessionKey inObj = (MemberSessionKey)messageFromClient;
		String idCipherBase64 = inObj.getIdCipherBase64();
		String sessionKeyBase64 = inObj.getSessionKeyBase64();
		String ivBase64 = inObj.getIvBase64();
		String pwdCipherBase64 = inObj.getPwdCipherBase64();
		String nicknameCipherBase64 = inObj.getNicknameCipherBase64();
		String questionCipherBase64 = inObj.getQuestionCipherBase64();
		String answerCipherBase64 = inObj.getAnswerCipherBase64();
		
		
		
		MessageResult outObj = new MessageResult();
		outObj.setTaskMessageID(inObj.getMessageID());
		
		
		SymmetricKey symmetricKey = null;
		try {
			symmetricKey = ServerSessionKeyManager.getInstance()
					.getSymmetricKey(sessionKeyBase64, ivBase64);
		} catch (IllegalArgumentException e) {
			outObj.setTaskResult("N");
			outObj.setResultMessage(""+e.getMessage());
			
			
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			outObj.setTaskResult("N");
			outObj.setResultMessage(""+e.getMessage());
			letterSender.addSyncMessage(outObj);
			return;
		}	
		
		
		outObj.setTaskResult("N");
		outObj.setResultMessage("회원 가입이 실패하였습니다.");
		
		String id = null;
		String password = null;
		String nickname = null;
		String question = null;
		String answer = null;
		
		try {
			id = symmetricKey.decryptStringBase64(idCipherBase64);
		} catch (IllegalArgumentException e) {
			outObj.setResultMessage("id::IllegalArgumentException");

			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			outObj.setResultMessage("id::SymmetricException");			
			letterSender.addSyncMessage(outObj);
			return;
		}

		try {
			password = symmetricKey.decryptStringBase64(pwdCipherBase64);
		} catch (IllegalArgumentException e) {
			outObj.setResultMessage("password::IllegalArgumentException");
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			outObj.setResultMessage("password::SymmetricException");
			letterSender.addSyncMessage(outObj);
			return;
		}

		try {
			nickname = symmetricKey
					.decryptStringBase64(nicknameCipherBase64);
		} catch (IllegalArgumentException e) {
			outObj.setResultMessage("nickname::IllegalArgumentException");
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			outObj.setResultMessage("nickname::SymmetricException");
			letterSender.addSyncMessage(outObj);
			return;
		}

		try {
			question = symmetricKey
					.decryptStringBase64(questionCipherBase64);
		} catch (IllegalArgumentException e) {
			outObj.setResultMessage("question::IllegalArgumentException");
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			outObj.setResultMessage("question::SymmetricException");
			letterSender.addSyncMessage(outObj);
			return;
		}

		try {
			answer = symmetricKey.decryptStringBase64(answerCipherBase64);
		} catch (IllegalArgumentException e) {
			outObj.setResultMessage("answer::IllegalArgumentException");
			letterSender.addSyncMessage(outObj);
			return;
		} catch (SymmetricException e) {
			outObj.setResultMessage("answer::SymmetricException");
			letterSender.addSyncMessage(outObj);
			return;
		}

		log.info(String.format("id=[%s], password=[%s], nickname=[%s], question=[%s], answer=[%s]"
				, id, password, nickname, question, answer));
		
		
		
		letterSender.addSyncMessage(outObj);
		
	}

}
