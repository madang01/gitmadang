package kr.pe.sinnori.client.asyn;

import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxIF;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxMapper;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class AsynPrivateMailboxCustomerThread extends Thread {
private Logger log = LoggerFactory.getLogger(AsynPrivateMailboxProducerThread.class);	
	
	private int maxSleepingTime = 0;
	private AsynPrivateMailboxMapper asynMailboxMapper = null;
	private LinkedBlockingQueue<ToLetter> toLetterQueue = null;
	
	public AsynPrivateMailboxCustomerThread(int maxSleepingTime, AsynPrivateMailboxMapper asynMailboxMapper, 
			LinkedBlockingQueue<ToLetter> toLetterQueue) {
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
				
				AsynPrivateMailboxIF asynMailbox = asynMailboxMapper.getAsynMailbox(mailboxID);
				
				//log.info("AsynPrivateMailboxCustomerThread::asynMailbox={}", asynMailbox.toString());
				
				WrapReadableMiddleObject wrapReadableMiddleObject = 
						new WrapReadableMiddleObject(toLetter.getMessageID(), toLetter.getMailboxID(), toLetter.getMailID(), new Object());
				
				// log.info(wrapReadableMiddleObject.toString());
				
				Thread.sleep(random.nextInt(maxSleepingTime));
				
				FromLetter fromLetter = new FromLetter(SocketChannel.open(), wrapReadableMiddleObject);
				
				//log.info("AsynPrivateMailboxCustomerThread::before putToSyncOutputMessageQueue, {}", wrapReadableMiddleObject.toString());
				
				asynMailbox.putToSyncOutputMessageQueue(fromLetter);
			} catch (Exception e) {
				String errorMessage = String.format("예외 발생하여 Thread 종료::errorMessage=%s", e.getMessage());
				log.warn(errorMessage, e);
				break;
			}
		}
	}
}
