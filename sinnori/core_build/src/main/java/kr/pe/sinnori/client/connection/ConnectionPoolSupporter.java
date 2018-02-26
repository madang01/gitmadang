package kr.pe.sinnori.client.connection;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPoolSupporter extends Thread implements ConnectionPoolSupporterIF {
	protected Logger log = LoggerFactory.getLogger(ConnectionPoolSupporter.class);
	
	
	private ConnectionPoolIF pool = null;
	private long interval;
	
	private SynchronousQueue<String> wakeupEventQueue = new SynchronousQueue<String>();

	public ConnectionPoolSupporter(ConnectionPoolIF pool, long interval) {
		this.pool = pool;
		this.interval = interval;
	}

	public void run() {
		String reasonForWakingUp = null;
		try {
			while (! this.isInterrupted()) {				
				pool.addAllLostConnections();
				
				reasonForWakingUp = wakeupEventQueue.poll(interval, TimeUnit.MILLISECONDS);
				if (null != reasonForWakingUp) {
					log.info("연결 폴 후원자 작업을 일찍 실행하는 사유[{}] 발생", reasonForWakingUp);
				}
				
			}
			log.warn("연결 폴 후원자::루프 종료");
		} catch(InterruptedException e) {
			log.warn("연결 폴 후원자::인터럽트에 의한 종료");
		}
	}
	
	public void notice(String reasonForWakingUp) {
		wakeupEventQueue.offer(reasonForWakingUp);
	}
}
