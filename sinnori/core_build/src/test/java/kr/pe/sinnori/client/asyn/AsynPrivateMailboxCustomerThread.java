package kr.pe.sinnori.client.asyn;

import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.client.connection.asyn.mailbox.SyncMailboxIF;
import kr.pe.sinnori.client.connection.asyn.share.SyncMailboxMapperForAsynPublic;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class AsynPrivateMailboxCustomerThread extends Thread {
private InternalLogger log = InternalLoggerFactory.getInstance(AsynPrivateMailboxProducerThread.class);	
	
	private int maxSleepingTime = 0;
	private SyncMailboxMapperForAsynPublic asynMailboxMapper = null;
	private ArrayBlockingQueue<ToLetter> toLetterQueue = null;
	
	public AsynPrivateMailboxCustomerThread(int maxSleepingTime, SyncMailboxMapperForAsynPublic asynMailboxMapper, 
			ArrayBlockingQueue<ToLetter> toLetterQueue) {
		if (maxSleepingTime < 0) {
			String errorMessage = String.format("the parameter maxSleepingTime[%d] is less than zero", maxSleepingTime);
			throw new IndexOutOfBoundsException(errorMessage);
		}
		if (null == asynMailboxMapper) {
			throw new IllegalArgumentException("the parameter asynMailboxMapper is null");		
		}
		
		
		if (null == toLetterQueue) {
			throw new IllegalArgumentException("the parameter toLetterQueue is null");
		}
		
		this.maxSleepingTime  = maxSleepingTime;
		this.asynMailboxMapper = asynMailboxMapper;
		this.toLetterQueue = toLetterQueue;
	}
	
	public void run() {	
		Random random = new Random();
		
		while (true) {
			
			if (this.isInterrupted()) {
				log.info("this thread에 interrupt 발생하여 종료");
				break;
			}
			
			try {
				ToLetter toLetter = toLetterQueue.take();
				
				//log.info("AsynPrivateMailboxCustomerThread::toLetter={}", toLetter.toString());
				
				int mailboxID = toLetter.getMailboxID();
				
				SyncMailboxIF asynMailbox = asynMailboxMapper.getAsynMailbox(mailboxID);
				
				//log.info("AsynPrivateMailboxCustomerThread::asynMailbox={}", asynMailbox.toString());
				
				WrapReadableMiddleObject wrapReadableMiddleObject = 
						new WrapReadableMiddleObject(toLetter.getMessageID(), toLetter.getMailboxID(), toLetter.getMailID(), new Object());
				
				wrapReadableMiddleObject.setFromSC(SocketChannel.open());
				
				// log.info(wrapReadableMiddleObject.toString());
				
				Thread.sleep(random.nextInt(maxSleepingTime));
				
				// FromLetter fromLetter = new FromLetter(SocketChannel.open(), wrapReadableMiddleObject);
				
				//log.info("AsynPrivateMailboxCustomerThread::before putToSyncOutputMessageQueue, {}", wrapReadableMiddleObject.toString());
				
				asynMailbox.putSyncOutputMessage(wrapReadableMiddleObject);
			} catch (Exception e) {
				String errorMessage = String.format("예외 발생하여 Thread 종료::errorMessage=%s", e.getMessage());
				log.warn(errorMessage, e);
				break;
			}
		}
	}
}
