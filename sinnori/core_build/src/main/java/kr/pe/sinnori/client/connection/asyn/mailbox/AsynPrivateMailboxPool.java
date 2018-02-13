package kr.pe.sinnori.client.connection.asyn.mailbox;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class AsynPrivateMailboxPool {
	// private Logger log = LoggerFactory.getLogger(AsynPrivateMailboxPool.class);
	// private final Object monitor = new Object();	
	
	private LinkedBlockingQueue<AsynPrivateMailbox> asynPrivateMailboxQueue = null;

	public AsynPrivateMailboxPool(AsynPrivateMailboxMapper asynPrivateMailboxMapper) {
		if (null == asynPrivateMailboxMapper) {
			throw new IllegalArgumentException("the parameter asynPrivateMailboxMapper is null");
		}
		
		int totalNumberOfAsynPrivateMailboxs = asynPrivateMailboxMapper.getTotalNumberOfAsynPrivateMailboxs();
		
		this.asynPrivateMailboxQueue 
		= new LinkedBlockingQueue<AsynPrivateMailbox>(totalNumberOfAsynPrivateMailboxs);
		
		
		for (int mailboxID=1; mailboxID <= totalNumberOfAsynPrivateMailboxs; mailboxID++) {
			asynPrivateMailboxQueue.add(asynPrivateMailboxMapper.getAsynMailbox(mailboxID));
		}
	}
	
	public AsynPrivateMailbox poll(long timeout) throws InterruptedException {
		return asynPrivateMailboxQueue.poll(timeout, TimeUnit.MILLISECONDS);
	}
	
	public void add(AsynPrivateMailbox asynPrivateMailbox) {
		asynPrivateMailboxQueue.offer(asynPrivateMailbox);
	}
}
