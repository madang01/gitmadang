package impl.executor.client;

import java.net.SocketTimeoutException;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerExcecutorException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.Echo.Echo;
import kr.pe.sinnori.util.AbstractClientExecutor;

public class TestNetEcoCExtor extends AbstractClientExecutor {

	@Override
	protected void doTask(ClientProjectConfig clientProjectConfig,
			ClientProjectIF clientProject) throws 
			SocketTimeoutException, ServerNotReadyException, NoMoreDataPacketBufferException, 
			BodyFormatException, DynamicClassCallException, ServerExcecutorException, NotLoginException {
		java.util.Random random = new java.util.Random();
		
		Echo echoInObj = new Echo();
		echoInObj.setRandomInt(random.nextInt());
		echoInObj.setStartTime(new java.util.Date().getTime());
		
		// log.info(echoInObj.toString());
		
		AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(echoInObj);
		//log.info("1111111111111");
		// log.info(messageFromServer.toString());
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
	}	
}
