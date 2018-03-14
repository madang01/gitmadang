package kr.pe.sinnori.client.connection;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPoolSupporter extends Thread implements ConnectionPoolSupporterIF {
	protected Logger log = LoggerFactory.getLogger(ConnectionPoolSupporter.class);
	
	private long wakeupInterval;
	
	
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
				
				connectionPool.addAllLostConnections();
			}
			log.warn("연결 폴 후원자::루프 종료");
		} catch(InterruptedException e) {
			log.warn("연결 폴 후원자::인터럽트에 의한 종료");
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
