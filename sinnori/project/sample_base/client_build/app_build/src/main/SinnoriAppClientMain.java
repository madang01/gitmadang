package main;
import java.net.SocketTimeoutException;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.Echo.Echo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SinnoriAppClientMain {

	public static void main(String[] args) {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori");
		log.info("start");
		
		String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);
		if (null == projectName) {
			log.error("자바 시스템 환경 변수[{}] 가 정의되지 않았습니다.", CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);
			System.exit(1);
		}
		
		String trimProjectName = projectName.trim();
		
		if (trimProjectName.length() == 0) {
			log.error("자바 시스템 환경 변수[{}] 값[{}]이 빈 문자열 있습니다.", CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME, projectName);
			System.exit(1);
		}
		
		if (! projectName.equals(trimProjectName)) {
			log.error("자바 시스템 환경 변수[{}] 값[{}] 앞뒤로 공백 문자열이 존재합니다.", 
					CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME, projectName);
			System.exit(1);
		}
		
		ClientProject clientProject = null;
		try {
			clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
		} catch (NotFoundProjectException e) {
			log.error("NotFoundProjectException", e);
			System.exit(1);
		}
		
		java.util.Random random = new java.util.Random();
		
		Echo echoInObj = new Echo();
		echoInObj.setRandomInt(random.nextInt());
		echoInObj.setStartTime(new java.util.Date().getTime());
				
		AbstractMessage messageFromServer = null;
		try {
			messageFromServer = clientProject.sendSyncInputMessage(echoInObj);
			
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
