package main;
import java.io.IOException;
import java.util.Date;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.client.AnyProjectConnectionPoolIF;
import kr.pe.sinnori.client.ConnectionPoolManager;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.Echo.Echo;


public class SinnoriAppClientMain {

	public static void main(String[] args) {
		InternalLogger log = InternalLoggerFactory.getInstance("kr.pe.sinnori");
		
		log.info("start");
		
		ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = connectionPoolManager.getMainProjectConnectionPool();
		
		
		String host = "172.30.1.15";
		int port=9090;
		
		AbstractConnection conn = null;
		
		try {
			conn = mainProjectConnectionPool.createConnection(host, port);
		} catch (Exception e) {
			log.error("fail to create a connection", e);
			System.exit(1);
		}
		
		
		java.util.Random random = new java.util.Random();
		
		long beforeTime = 0;
		long afterTime = 0;
		
		
		int retryCount = 1;
		
		beforeTime= new Date().getTime();
		try {
		for (int i=0; i < retryCount; i++) {	
			Echo echoInObj = new Echo();
			echoInObj.setRandomInt(random.nextInt());
			echoInObj.setStartTime(new java.util.Date().getTime());
					
			AbstractMessage messageFromServer = null;
			try {
				messageFromServer = conn.sendSyncInputMessage(echoInObj);
				
				if (messageFromServer instanceof Echo) {
					Echo echoOutObj = (Echo)messageFromServer;
					if ((echoInObj.getRandomInt() == echoOutObj.getRandomInt()) && (echoInObj.getStartTime() == echoOutObj.getStartTime())) {
						// log.info("성공::echo 메시지 입력/출력 동일함");
					} else {
						log.info("실패::echo 메시지 입력/출력 다름");
					}
				} else {
					log.warn("messageFromServer={}", messageFromServer.toString());
				}
			} catch (Exception e) {
				log.warn("SocketTimeoutException", e);
			}
		}
		
		afterTime= new Date().getTime();
		
		log.info("{} 번 시간차={} ms, 평균={} ms", retryCount, (afterTime-beforeTime), (double)(afterTime-beforeTime)/retryCount);
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (IOException e) {
					log.error("fail to close the connection", e);
				}
			}
		}
	}
}
