package kr.pe.sinnori.client.connection.asyn.share;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxIF;

public class AsynPrivateMailboxPool implements AsynPrivateMailboxPoolIF {
	// private Logger log = LoggerFactory.getLogger(AsynPrivateMailboxPool.class);
	// private final Object monitor = new Object();	
	
	private ArrayBlockingQueue<AsynPrivateMailboxIF> asynPrivateMailboxQueue = null;
	
	private AsynPrivateMailboxMapperIF asynPrivateMailboxMapper = null;

	public AsynPrivateMailboxPool(AsynPrivateMailboxMapperIF asynPrivateMailboxMapper) {
		if (null == asynPrivateMailboxMapper) {
			throw new IllegalArgumentException("the parameter asynPrivateMailboxMapper is null");
		}
		
		this.asynPrivateMailboxMapper = asynPrivateMailboxMapper;
		
		int totalNumberOfAsynPrivateMailboxs = asynPrivateMailboxMapper.getTotalNumberOfAsynPrivateMailboxs();
		
		this.asynPrivateMailboxQueue 
		= new ArrayBlockingQueue<AsynPrivateMailboxIF>(totalNumberOfAsynPrivateMailboxs);
		
		
		for (int mailboxID=1; mailboxID <= totalNumberOfAsynPrivateMailboxs; mailboxID++) {
			asynPrivateMailboxQueue.add(asynPrivateMailboxMapper.getAsynMailbox(mailboxID));
		}
	}
	
	public AsynPrivateMailboxIF poll(long timeout) throws InterruptedException {
		return asynPrivateMailboxQueue.poll(timeout, TimeUnit.MILLISECONDS);
	}
	
	public boolean offer(AsynPrivateMailboxIF asynPrivateMailbox) {
		return asynPrivateMailboxQueue.offer(asynPrivateMailbox);
	}

	public AsynPrivateMailboxMapperIF getAsynPrivateMailboxMapper() {
		return asynPrivateMailboxMapper;
	}
	
}
