package kr.pe.sinnori.client.asyn;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Test;

import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailbox;
import kr.pe.sinnori.client.connection.asyn.share.AsynPrivateMailboxMapper;
import kr.pe.sinnori.client.connection.asyn.share.AsynPrivateMailboxPool;
import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class AsynPrivateMailboxTest extends AbstractJunitTest {
	
	@Test
	public void testputToSyncOutputMessageQueue_2번연속호출하여큐를비우는로직테스트() {
		int asynPrivateMailboxQueueSize = 1;		
		int totalNumberOfAsynMailbox = asynPrivateMailboxQueueSize + 1;	
		
	
		int socketTimeOut = 500;
		
		AsynPrivateMailboxMapper asynMailboxMapper = new AsynPrivateMailboxMapper(totalNumberOfAsynMailbox, socketTimeOut);
		
		AsynPrivateMailbox asynPrivateMailbox = (AsynPrivateMailbox)asynMailboxMapper.getAsynMailbox(1);
		
		WrapReadableMiddleObject wrapReadableMiddleObject = 
				new WrapReadableMiddleObject("Echo", 1, Integer.MIN_VALUE, new Object());
		
		/*FromLetter fromLetter = null;
		try {
			fromLetter = new FromLetter(SocketChannel.open(), wrapReadableMiddleObject);
		} catch (IOException e1) {
			fail("fail to open a socket");
		}*/
		
		try {
			wrapReadableMiddleObject.setFromSC(SocketChannel.open());
		} catch (IOException e1) {
			fail("fail to open a socket");
		}
		
		try {
			asynPrivateMailbox.putSyncOutputMessage(wrapReadableMiddleObject);
		} catch (InterruptedException e) {
			log.warn("InterruptedException", e);
		};
		
		/*wrapReadableMiddleObject = 
				new WrapReadableMiddleObject("Echo", 1, Integer.MIN_VALUE, new Object());*/
		
		try {
			asynPrivateMailbox.putSyncOutputMessage(wrapReadableMiddleObject);
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
		
		int toLetterQueueSize = 5;
		ArrayBlockingQueue<ToLetter> toLetterQueue = new ArrayBlockingQueue<ToLetter>(toLetterQueueSize); 
		
		int socketTimeOut = 500;
		
		AsynPrivateMailboxMapper asynMailboxMapper = new AsynPrivateMailboxMapper(totalNumberOfAsynMailbox, socketTimeOut);
		
		
		int maxSleepingTime = 5000;		
		
		AsynPrivateMailboxPool asynPrivateMailboxPool = new AsynPrivateMailboxPool(asynMailboxMapper);
		
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
