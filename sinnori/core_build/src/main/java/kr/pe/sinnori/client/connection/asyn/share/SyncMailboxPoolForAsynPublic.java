package kr.pe.sinnori.client.connection.asyn.share;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import kr.pe.sinnori.client.connection.asyn.mailbox.SyncMailboxIF;

public class SyncMailboxPoolForAsynPublic implements SyncMailboxPoolForAsynPublicIF {
	// private Logger log = LoggerFactory.getLogger(AsynPrivateMailboxPool.class);
	// private final Object monitor = new Object();	
	
	private ArrayBlockingQueue<SyncMailboxIF> asynPrivateMailboxQueue = null;
	
	private SyncMailboxMapperForAsynPublicIF asynPrivateMailboxMapper = null;

	public SyncMailboxPoolForAsynPublic(SyncMailboxMapperForAsynPublicIF asynPrivateMailboxMapper) {
		if (null == asynPrivateMailboxMapper) {
			throw new IllegalArgumentException("the parameter asynPrivateMailboxMapper is null");
		}
		
		this.asynPrivateMailboxMapper = asynPrivateMailboxMapper;
		
		int totalNumberOfAsynPrivateMailboxs = asynPrivateMailboxMapper.getTotalNumberOfAsynPrivateMailboxs();
		
		this.asynPrivateMailboxQueue 
		= new ArrayBlockingQueue<SyncMailboxIF>(totalNumberOfAsynPrivateMailboxs);
		
		
		for (int mailboxID=1; mailboxID <= totalNumberOfAsynPrivateMailboxs; mailboxID++) {
			asynPrivateMailboxQueue.add(asynPrivateMailboxMapper.getAsynMailbox(mailboxID));
		}
	}
	
	public SyncMailboxIF poll(long timeout) throws InterruptedException {
		return asynPrivateMailboxQueue.poll(timeout, TimeUnit.MILLISECONDS);
	}
	
	public boolean offer(SyncMailboxIF asynPrivateMailbox) {
		return asynPrivateMailboxQueue.offer(asynPrivateMailbox);
	}

	public SyncMailboxMapperForAsynPublicIF getAsynPrivateMailboxMapper() {
		return asynPrivateMailboxMapper;
	}
	
	public int getSize() {
		return asynPrivateMailboxQueue.size();
	}
	
}
