package main;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.applib.sessionkey.RSAPublickeyGetterBuilder;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.impl.message.Echo.Echo;
import kr.pe.codda.common.etc.CommonStaticFinalVars;



public class AppClientMain {

	public static void main(String[] args) {
		InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticFinalVars.BASE_PACKAGE_NAME);
		
		log.info("start");
		
		try {
			ClientSessionKeyManager.getInstance().getMainProjectClientSessionKey(RSAPublickeyGetterBuilder.build());
		} catch (SymmetricException | InterruptedException e) {
			log.warn("fail to getting the main project's instance of the ClientSessionKey class", e);
			System.exit(1);
		}
		
		log.info("successfully getting the main project's instance of the ClientSessionKey class");
				
		
		java.util.Random random = new java.util.Random();
		
		Echo echoInObj = new Echo();
		echoInObj.setRandomInt(random.nextInt());
		echoInObj.setStartTime(new java.util.Date().getTime());
				
		AbstractMessage messageFromServer = null;
		try {
			messageFromServer = ConnectionPoolManager.getInstance().getMainProjectConnectionPool().sendSyncInputMessage(echoInObj);
			
			if (messageFromServer instanceof Echo) {
				Echo echoOutObj = (Echo)messageFromServer;
				if ((echoInObj.getRandomInt() == echoOutObj.getRandomInt()) && (echoInObj.getStartTime() == echoOutObj.getStartTime())) {
					log.info("성공::echo 메시지 입력/출력 동일함");
				} else {
					log.info("실패::echo 메시지 입력/출력 다름");
				}
			} else {
				log.warn("messageFromServer={}", messageFromServer.toString());
			}
			
			System.exit(0);
		} catch (Exception e) {
			log.warn("Exception", e);
		}
		
		System.exit(1);
	}
}