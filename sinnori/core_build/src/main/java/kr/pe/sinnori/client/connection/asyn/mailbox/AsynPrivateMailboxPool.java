package kr.pe.sinnori.client.connection.asyn.mailbox;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsynPrivateMailboxPool {
	private Logger log = LoggerFactory.getLogger(AsynPrivateMailboxPool.class);
	// private final Object monitor = new Object();	
	
	private LinkedBlockingQueue<AsynPrivateMailbox> asynPrivateMailboxQueue = null;

	public AsynPrivateMailboxPool(List<AsynPrivateMailbox> asynPrivateMailboxList) {
		if (null == asynPrivateMailboxList) {
			throw new IllegalArgumentException("the parameter asynPrivateMailboxList is null");
		}
		
		if (0 == asynPrivateMailboxList.size()) {
			throw new IllegalArgumentException("the parameter asynPrivateMailboxList is empty");
		}

		this.asynPrivateMailboxQueue 
		= new LinkedBlockingQueue<AsynPrivateMailbox>(asynPrivateMailboxList.size());
		
		for (AsynPrivateMailbox asynPrivateMailbox : asynPrivateMailboxList) {
			asynPrivateMailboxQueue.add(asynPrivateMailbox);
		}
	}
	
	public AsynPrivateMailbox take() throws InterruptedException {
		return asynPrivateMailboxQueue.take();
	}
	
	public void put(AsynPrivateMailbox asynPrivateMailbox) {
		boolean result = asynPrivateMailboxQueue.offer(asynPrivateMailbox);
		if (! result) {
			log.error(
					"fail to insert asynPrivateMailbox[%s] into queue, 고정 갯수로 운영되는 LinkedBlockingQueue 에서 빼온 후 다시 넣는것을 실패할 수 없다. 원인 찾아 해결 필요",
					asynPrivateMailbox.toString());
			System.exit(1);
		}
	}
}
