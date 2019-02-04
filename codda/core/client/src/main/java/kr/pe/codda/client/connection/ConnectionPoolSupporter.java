package kr.pe.codda.client.connection;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ConnectionPoolSupporter extends Thread implements ConnectionPoolSupporterIF {

protected InternalLogger log = InternalLoggerFactory.getInstance(ConnectionPoolSupporter.class);
	
	private long wakeupInterval=0;
	
	
	private ConnectionPoolIF connectionPool = null;
	private SynchronousQueue<String> wakeupEventQueue = new SynchronousQueue<String>();

	public ConnectionPoolSupporter(long wakeupInterval) {
		if (wakeupInterval <= 0) {
			String errorMessage = String.format("the parameter wakeupInterval[%d] is less than or equal to zero", wakeupInterval);
			throw new IllegalArgumentException(errorMessage);
		}
		this.wakeupInterval = wakeupInterval;
	}

	public void run() {
		log.info("연결 폴 후원자 시작");
		
		String reasonForWakingUp = null;
		try {
			while (! Thread.currentThread().isInterrupted()) {
				reasonForWakingUp = wakeupEventQueue.poll(wakeupInterval, TimeUnit.MILLISECONDS);
				if (null != reasonForWakingUp) {
					log.info("연결 폴 후원자 작업을 일찍 실행하는 사유[{}] 발생", reasonForWakingUp);
				}
				
				//log.debug("start the work adding the all missing connection");
				log.info("reasonForWakingUp={}", reasonForWakingUp);
				
				try {
					connectionPool.fillAllConnection();
				} catch(InterruptedException e) {
					throw e;
				} catch(Exception e) {
					log.warn("연결 폴 후원자에서 통제된 에러 발생하여 루프 계속", e);
					continue;
				}
				
				log.debug("end the work adding the all missing connection");
			}
			log.warn("연결 폴 후원자::루프 종료");
		} catch(InterruptedException e) {
			log.warn("연결 폴 후원자::인터럽트에 의한 종료");
		} catch(Exception e) {
			log.warn("연결 폴 후원자::에러에 의한 종료", e);
		}
	}
	
	public void registerPool(ConnectionPoolIF connectionPool) {
		if (null == connectionPool) {
			throw new IllegalArgumentException("the parameter connectionPool is null");
		}
		this.connectionPool = connectionPool;
	}
	
	public void notice(String reasonForWakingUp) {
		wakeupEventQueue.offer(reasonForWakingUp);
	}
}
