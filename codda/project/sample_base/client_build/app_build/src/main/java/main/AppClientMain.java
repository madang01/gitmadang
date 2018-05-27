package main;

import java.util.concurrent.TimeUnit;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.Echo.Echo;

public class AppClientMain {

	public static void main(String[] args) {
		InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticFinalVars.BASE_PACKAGE_NAME);

		log.info("start");

		ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();

		AnyProjectConnectionPoolIF mainProjectConnectionPool = connectionPoolManager.getMainProjectConnectionPool();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
		}
		
		java.util.Random random = new java.util.Random();

		long startTime = 0;
		long endTime = 0;

		int retryCount = 1000000;

		startTime = System.nanoTime();

		for (int i = 0; i < retryCount; i++) {
			Echo echoInObj = new Echo();
			echoInObj.setRandomInt(random.nextInt());
			echoInObj.setStartTime(new java.util.Date().getTime());

			AbstractMessage messageFromServer = null;
			try {
				messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(echoInObj);

				if (messageFromServer instanceof Echo) {
					Echo echoOutObj = (Echo) messageFromServer;
					if ((echoInObj.getRandomInt() == echoOutObj.getRandomInt())
							&& (echoInObj.getStartTime() == echoOutObj.getStartTime())) {
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

		endTime = System.nanoTime();

		log.info("loop count[{}], average time[{} microseconds]", retryCount,
				TimeUnit.MICROSECONDS.convert((endTime - startTime)/retryCount, TimeUnit.NANOSECONDS));
		
		System.exit(0);

	}
}
