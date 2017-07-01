package main;

import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.applib.sessionkey.RSAPublickeyGetterBuilder;
import kr.pe.sinnori.client.AnyProjectClient;
import kr.pe.sinnori.client.MainClientManager;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.impl.message.Echo.Echo;


public class SinnoriAppClientMain {

	public static void main(String[] args) {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori");
		
		log.info("start");
		
		try {
			ClientSessionKeyManager.getInstance().getMainProjectClientSessionKey(RSAPublickeyGetterBuilder.build());
		} catch (SymmetricException e) {
			log.warn("fail to getting the main project's instance of the ClientSessionKey class", e);
			System.exit(1);
		}
		
		log.info("successfully getting the main project's instance of the ClientSessionKey class");
		
		MainClientManager mainClientManager = MainClientManager.getInstance();
		AnyProjectClient mainProjectClient = mainClientManager.getMainProjectClient();			
		
		java.util.Random random = new java.util.Random();
		
		Echo echoInObj = new Echo();
		echoInObj.setRandomInt(random.nextInt());
		echoInObj.setStartTime(new java.util.Date().getTime());
				
		AbstractMessage messageFromServer = null;
		try {
			messageFromServer = mainProjectClient.sendSyncInputMessage(echoInObj);
			
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
		} catch (SocketTimeoutException e) {
			log.warn("SocketTimeoutException", e);
		} catch (ServerNotReadyException e) {
			log.warn("ServerNotReadyException", e);
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
		} catch (ServerTaskException e) {
			log.warn("ServerTaskException", e);
		} catch (NotLoginException e) {
			log.warn("NotLoginException", e);
		}
		
		System.exit(1);
	}
}