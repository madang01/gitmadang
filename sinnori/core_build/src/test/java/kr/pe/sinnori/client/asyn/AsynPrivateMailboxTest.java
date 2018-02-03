package kr.pe.sinnori.client.asyn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

import kr.pe.sinnori.client.connection.asyn.mailbox.AsynMailboxMapper;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailbox;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxPool;
import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class AsynPrivateMailboxTest extends AbstractJunitTest {
	
	@Test
	public void testputToSyncOutputMessageQueue_2번연속호출하여큐를비우는로직테스트() {
		int asynPrivateMailboxQueueSize = 1;		
		int totalNumberOfAsynMailbox = asynPrivateMailboxQueueSize + 1;
		
		int outputMessageQueueSize = 5;
		LinkedBlockingQueue<WrapReadableMiddleObject> outputMessageQueue 
			= new LinkedBlockingQueue<WrapReadableMiddleObject>(outputMessageQueueSize); 
	
		int socketTimeOut = 500;
		
		AsynMailboxMapper asynMailboxMapper = AsynMailboxMapper.Builder
				.build(totalNumberOfAsynMailbox, socketTimeOut, outputMessageQueue);
		
		AsynPrivateMailbox asynPrivateMailbox = (AsynPrivateMailbox)asynMailboxMapper.getAsynMailbox(1);
		
		WrapReadableMiddleObject wrapReadableMiddleObject = 
				new WrapReadableMiddleObject("Echo", 1, Integer.MIN_VALUE, new Object());
		
		try {
			asynPrivateMailbox.putToSyncOutputMessageQueue(wrapReadableMiddleObject);
		} catch (InterruptedException e) {
			log.warn("InterruptedException", e);
		};
		
		/*wrapReadableMiddleObject = 
				new WrapReadableMiddleObject("Echo", 1, Integer.MIN_VALUE, new Object());*/
		
		try {
			asynPrivateMailbox.putToSyncOutputMessageQueue(wrapReadableMiddleObject);
		} catch (InterruptedException e) {
			log.warn("InterruptedException", e);
		};
		
	}

	@Test
	public void test_생산자소비자쓰레드테스트() {
		int numbmerOfAsynPrivateMailboxProducerThread = 5;
		int numberOfExecutions = 10;
		List<AsynPrivateMailboxProducerThread> asynPrivateMailboxProducerThreadList = new ArrayList<AsynPrivateMailboxProducerThread>();
		
		int numbmerOfAsynPrivateMailboxCustomerThread = 5;
		List<AsynPrivateMailboxCustomerThread> asynPrivateMailboxCustomerThreadList = new ArrayList<AsynPrivateMailboxCustomerThread>();
		
		int totalNumberOfAsynPrivateMailbox = 1;
		
		int totalNumberOfAsynMailbox = totalNumberOfAsynPrivateMailbox + 1;
		
		int outputMessageQueueSize = 5;
		LinkedBlockingQueue<WrapReadableMiddleObject> outputMessageQueue 
			= new LinkedBlockingQueue<WrapReadableMiddleObject>(outputMessageQueueSize); 
		
		int toLetterQueueSize = 5;
		LinkedBlockingQueue<ToLetter> toLetterQueue = new LinkedBlockingQueue<ToLetter>(toLetterQueueSize); 
		
		int socketTimeOut = 500;
		
		AsynMailboxMapper asynMailboxMapper = AsynMailboxMapper.Builder
				.build(totalNumberOfAsynMailbox, socketTimeOut, outputMessageQueue);
		
		
		int maxSleepingTime = 5000;
		
		List<AsynPrivateMailbox> asynPrivateMailboxList = new ArrayList<AsynPrivateMailbox>();
		
		for (int i=0; i < totalNumberOfAsynPrivateMailbox; i++) {
			AsynPrivateMailbox asynPrivateMailbox = (AsynPrivateMailbox)asynMailboxMapper.getAsynMailbox(i+1);
			asynPrivateMailboxList.add(asynPrivateMailbox);
		}
		
		AsynPrivateMailboxPool asynPrivateMailboxPool = new AsynPrivateMailboxPool(asynPrivateMailboxList);
		
		for (int i=0; i < numbmerOfAsynPrivateMailboxProducerThread; i++) {
			AsynPrivateMailboxProducerThread asynPrivateMailboxProducerThread 
			= new AsynPrivateMailboxProducerThread(numberOfExecutions, asynPrivateMailboxPool, toLetterQueue);
			asynPrivateMailboxProducerThreadList.add(asynPrivateMailboxProducerThread);
		}
		
		for (int i=0; i < numbmerOfAsynPrivateMailboxCustomerThread; i++) {
			AsynPrivateMailboxCustomerThread asynPrivateMailboxCustomerThread 
			= new AsynPrivateMailboxCustomerThread(maxSleepingTime, asynMailboxMapper, toLetterQueue);
			asynPrivateMailboxCustomerThreadList.add(asynPrivateMailboxCustomerThread);
		}
		
		for (AsynPrivateMailboxCustomerThread asynPrivateMailboxCustomerThread : asynPrivateMailboxCustomerThreadList) {
			asynPrivateMailboxCustomerThread.start();
		}
		
		for (AsynPrivateMailboxProducerThread asynPrivateMailboxProducerThread : asynPrivateMailboxProducerThreadList) {
			asynPrivateMailboxProducerThread.start();
		}
		
		for (AsynPrivateMailboxProducerThread asynPrivateMailboxProducerThread : asynPrivateMailboxProducerThreadList) {
			try {
				asynPrivateMailboxProducerThread.join();
			} catch (InterruptedException e) {
				log.error("InterruptedException");
				System.exit(1);
			}
		}
		
		log.info("call sleep");
		try {
			Thread.sleep(maxSleepingTime);
		} catch (InterruptedException e) {
			log.warn("InterruptedException", e);
		}
	}
}
