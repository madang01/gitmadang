package kr.pe.sinnori.client.connection;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPoolManager extends Thread implements ConnectionPoolManagerIF {
	protected Logger log = LoggerFactory.getLogger(ConnectionPoolManager.class);
	
	
	private ConnectionPoolIF pool = null;
	private long interval;
	
	private SynchronousQueue<String> synchronousQueue = new SynchronousQueue<String>();

	public ConnectionPoolManager(ConnectionPoolIF pool, long interval) {
		this.pool = pool;
		this.interval = interval;
	}

	public void run() {
		String reasonForLoss = null;
		try {
			while (!this.isInterrupted()) {
				while (pool.whetherConnectionIsMissing()) {
					try {
						pool.addConnection();
						log.info("결손된 동기 개인 연결 추가 작업 완료");
						
					} catch (InterruptedException e) {
						throw e;
					} catch (Exception e) {
						log.warn("에러 발생에 따른 결손된 동기 개인 연결 추가 작업 잠시 중지 ", e);
						break;
					}
				}

				
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
