package kr.pe.sinnori.client.connection;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPoolSupporter extends Thread implements ConnectionPoolSupporterIF {
	protected Logger log = LoggerFactory.getLogger(ConnectionPoolSupporter.class);
	
	
	private ConnectionPoolIF pool = null;
	private long interval;
	
	private SynchronousQueue<String> synchronousQueue = new SynchronousQueue<String>();

	public ConnectionPoolSupporter(ConnectionPoolIF pool, long interval) {
		this.pool = pool;
		this.interval = interval;
		
		pool.registerConnectionPoolSupporter(this);
	}

	public void run() {
		String reasonForLoss = null;
		try {
			while (!this.isInterrupted()) {				
				pool.addAllLostConnections();

				
				reasonForLoss = synchronousQueue.poll(interval, TimeUnit.MILLISECONDS);
				if (null != reasonForLoss) {
					log.info("연결 폴 관리자 작업을 일찍 실행하는 사유[{}] 발생", reasonForLoss);
				}
				
			}
			log.warn("연결 폴 관리자::루프 종료");
		} catch(InterruptedException e) {
			log.warn("연결 폴 관리자::인터럽트에 의한 종료");
		}
	}
	
	public void notice(String reason) {
		synchronousQueue.offer(reason);
	}
}
