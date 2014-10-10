package kr.pe.sinnori.impl.message.BinaryPublicKey;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.SinnoriSqlSessionFactoryIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

public class BinaryPublicKeyServerTask extends AbstractServerTask {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			SinnoriSqlSessionFactoryIF sqlSessionFactory,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());
		
		// BinaryPublicKey inObj = (BinaryPublicKey)messageFromClient;
		/*ClientResource clientResource = letterSender.getClientResource();
		synchronized(clientResource) {
			ClientSessionKeyManager clientSessionKeyManager = clientResource.getClientSessionKeyManager();
			if (null != clientSessionKeyManager) {
				String errorMessage = "2번이상 공개키를 요구할 수 없습니다.";
				log.warn("{}, {}", clientResource.toString(), errorMessage);
				throw new  ServerTaskException(errorMessage);
			}
			
			clientResource.setClientSessionKeyManager(new ClientSessionKeyManager(inObj.getPublicKeyBytes()));		
		}*/
		BinaryPublicKey outObj = new BinaryPublicKey();
		outObj.setPublicKeyBytes(ServerSessionKeyManager.getInstance().getPublicKeyBytes());
		letterSender.addSyncMessage(outObj);
	}
}
