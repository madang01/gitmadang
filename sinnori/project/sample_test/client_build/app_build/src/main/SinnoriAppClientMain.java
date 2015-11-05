package main;
import java.net.SocketTimeoutException;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.Echo.Echo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SinnoriAppClientMain {

	public static void main(String[] args) {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori");
		
		log.info("start");
		
		ClientProjectManager clientProjectManager = ClientProjectManager.getInstance();
		ClientProject mainClientProject = clientProjectManager.getMainClientProject();
		
		
		java.util.Random random = new java.util.Random();
		
		Echo echoInObj = new Echo();
		echoInObj.setRandomInt(random.nextInt());
		echoInObj.setStartTime(new java.util.Date().getTime());
				
		AbstractMessage messageFromServer = null;
		try {
			messageFromServer = mainClientProject.sendSyncInputMessage(echoInObj);
			
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
	}
}