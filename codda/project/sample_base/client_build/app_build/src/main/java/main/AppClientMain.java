package main;

import java.util.concurrent.TimeUnit;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.Echo.Echo;

public class AppClientMain {

	public static void main(String[] args) {
		InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticFinalVars.BASE_PACKAGE_NAME);
		
		int numberOfThread = 1000;
		if (args.length > 0) {
			String firstArgument = args[0];
			log.info("the first argument(=numberOfThread)=[{}]", numberOfThread);
		
			try {
				numberOfThread = Integer.parseInt(firstArgument);
			} catch(NumberFormatException e) {
				log.info("args.length={}, the first argument(=numberOfThread)[{}] is not a integer", 
						args.length, firstArgument);
			}
		}
		
		log.info("numberOfThread={}", numberOfThread);
		
		class ThreadSafeTester implements Runnable {
			private InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticFinalVars.BASE_PACKAGE_NAME);
			
			private AnyProjectConnectionPoolIF mainProjectConnectionPool = null;
		
			
			public ThreadSafeTester(AnyProjectConnectionPoolIF mainProjectConnectionPool) {
				this.mainProjectConnectionPool = mainProjectConnectionPool;
			}			

			@Override
			public void run() {
				log.info("start {}", Thread.currentThread().getName());
				
				String serverHost = null;
				int serverPort;
				
				CoddaConfigurationManager coddaConfigurationManager = CoddaConfigurationManager.getInstance();
				CoddaConfiguration coddaConfiguration = coddaConfigurationManager.getRunningProjectConfiguration();
				ProjectPartConfiguration mainProjectPartConfiguration = coddaConfiguration.getMainProjectPartConfiguration();
				serverHost = mainProjectPartConfiguration.getServerHost();
				serverPort = mainProjectPartConfiguration.getServerPort();
				
				ConnectionIF connection = null;				
				
				java.util.Random random = new java.util.Random();
				
				long startTime = 0;
				long endTime = 0;
				
				try {
					while (! Thread.currentThread().isInterrupted()) {
							
						while (null == connection) {
							
							try {
								startTime = System.nanoTime();									
								connection = mainProjectConnectionPool.createSyncThreadSafeConnection(serverHost, serverPort);
								endTime = System.nanoTime();
								log.info("연결 경과 시간[{}] microseconds",
										TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
								
							} catch (Exception e) {
								log.warn("fail to create a intance of SyncThreadSafeSingleConnection class");
								
								try {
									Thread.sleep(5000L);
								} catch (InterruptedException e1) {
									log.error("this thread[{}] InterruptedException", Thread.currentThread().getName());
									return;
								}
							}
						}
						
						Echo echoReq = new Echo();
						echoReq.setRandomInt(random.nextInt());
						echoReq.setStartTime(new java.util.Date().getTime());
						
						AbstractMessage outputMessage = null;
						try {
							
							startTime = System.nanoTime();
							outputMessage = connection.sendSyncInputMessage(echoReq);
							// outputMessage = mainProjectConnectionPool.sendSyncInputMessage(echoReq);
							endTime = System.nanoTime();
							log.info("메시지 송수신 경과 시간[{}] microseconds",
									TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
							
						} catch (Exception e) {
							log.warn("error", e);
							connection.close();
							connection = null;
							continue;
						}				
						if (outputMessage instanceof Echo) {
							Echo echoRes = (Echo) outputMessage;
							if ((echoReq.getRandomInt() != echoRes.getRandomInt()) 
									|| (echoReq.getStartTime() != echoRes.getStartTime())) {
								log.error("실패::echo 메시지 입력/출력 다름");
								System.exit(1);
							}
						} else {
							log.error("실패::출력 메시지[{}]가 echo 가 아님", outputMessage.toString());
							System.exit(1);
						}
						
						// long intervalTime = 1000L + random.nextInt(4000);
						Thread.sleep(5000L);
					}			
					
				} catch (InterruptedException e) {
					log.info("this thread[{}] InterruptedException", Thread.currentThread().getName());
				} catch (Exception e) {
					log.warn("unknow error", e);
				}
				
			}
		}
		
		ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();
		AnyProjectConnectionPoolIF mainProjectConnectionPool = connectionPoolManager.getMainProjectConnectionPool();		
		Thread[] threadSafeTester = new Thread[numberOfThread];

		for (int i=0; i < numberOfThread; i++) {
			threadSafeTester[i] = new Thread(new ThreadSafeTester(mainProjectConnectionPool));
			threadSafeTester[i].start();
		}	

		
		
		/*
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
		
		System.exit(0);*/

	}
}
