package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

public class BinaryPublicKeyServerTask extends AbstractServerTask {

	@Override
	public void doTask(String projectName,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage inObj)
			throws Exception {
		doWork(projectName, letterSender, (BinaryPublicKey)inObj);
	}
	
	private void doWork(String projectName,
			LetterSender letterSender, BinaryPublicKey inObj)
			throws Exception {
		// FIXME!
		log.info(inObj.toString());
		
		ClientResource clientResource = letterSender.getClientResource();
		synchronized(clientResource) {
			ClientSessionKeyManager clientSessionKeyManager = clientResource.getClientSessionKeyManager();
			if (null != clientSessionKeyManager) {
				String errorMessage = "2번이상 공개키를 요구할 수 없습니다.";
				log.warn("{}, {}", clientResource.toString(), errorMessage);
				throw new  ServerTaskException(errorMessage);
			}
			
			clientResource.setClientSessionKeyManager(new ClientSessionKeyManager(inObj.getPublicKeyBytes()));		
		}
		BinaryPublicKey outObj = new BinaryPublicKey();
		outObj.setPublicKeyBytes(ServerSessionKeyManager.getInstance().getPublicKeyBytes());
		letterSender.addSyncMessage(outObj);
	}
}
