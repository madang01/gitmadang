package kr.pe.codda.client.connection;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

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
		String reasonForWakingUp = null;
		try {
			while (! this.isInterrupted()) {
				reasonForWakingUp = wakeupEventQueue.poll(wakeupInterval, TimeUnit.MILLISECONDS);
				if (null != reasonForWakingUp) {
					log.info("연결 폴 후원자 작업을 일찍 실행하는 사유[{}] 발생", reasonForWakingUp);
				}
				
				log.debug("start the work adding the all missing connection");
				
				try {
					connectionPool.addAllLostConnection();
				} catch(IOException | NoMoreDataPacketBufferException e) {
					log.warn("통제된 에러에 의한 루프 종료", e);
					break;
				} catch(Exception e) {
					log.warn("알수 없는 에러에 의한 루프 종료", e);
					break;
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
