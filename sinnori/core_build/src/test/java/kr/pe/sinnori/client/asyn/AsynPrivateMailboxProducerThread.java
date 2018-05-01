package kr.pe.sinnori.client.asyn;

import java.net.SocketTimeoutException;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.concurrent.ArrayBlockingQueue;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.client.connection.asyn.mailbox.SyncMailboxIF;
import kr.pe.sinnori.client.connection.asyn.share.SyncMailboxPoolForAsynPublic;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class AsynPrivateMailboxProducerThread extends Thread {
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynPrivateMailboxProducerThread.class);

	private int numberOfExecution;
	private SyncMailboxPoolForAsynPublic asynPrivateMailboxPool = null;
	private ArrayBlockingQueue<ToLetter> toLetterQueue = null;

	public AsynPrivateMailboxProducerThread(int numberOfExecutions,
			SyncMailboxPoolForAsynPublic asynPrivateMailboxPool,
			ArrayBlockingQueue<ToLetter> toLetterQueue) {
		if (numberOfExecutions < 0) {
			String errorMessage = String.format("the parameter numberOfExecutions[%d] is less than zero",
					numberOfExecutions);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == asynPrivateMailboxPool) {
			throw new IllegalArgumentException("the parameter asynPrivateMailboxPool is null");
		}

		if (null == toLetterQueue) {
			throw new IllegalArgumentException("the parameter toLetterQueue is null");
		}

		this.numberOfExecution = numberOfExecutions;
		this.asynPrivateMailboxPool = asynPrivateMailboxPool;
		this.toLetterQueue = toLetterQueue;
	}

	public void run() {
		SyncMailboxIF asynPrivateMailbox = null;
		
		try {
			for (int i = 0; i < numberOfExecution; i++) {

				if (this.isInterrupted()) {
					log.info("this thread에 interrupt 발생하여 종료");
					break;
				}
				
				asynPrivateMailbox = asynPrivateMailboxPool.poll(1000L);
				if (null == asynPrivateMailbox) {
					log.info("소켓 타임 아웃 발생");
					continue;
				}

				try {
					SocketChannel toSocketChannel = SocketChannel.open();
					String messageID = "Echo";
					ArrayDeque<WrapBuffer> wrapBufferList = new ArrayDeque<WrapBuffer>();
					
					ToLetter toLetter = new ToLetter(toSocketChannel, 
							messageID,
							asynPrivateMailbox.getMailboxID(),
							asynPrivateMailbox.getMailID(),
							wrapBufferList);				
					
					log.info("AsynPrivateMailboxProducerThread::toLetter={}", toLetter.toString());				
					toLetterQueue.put(toLetter);
					
					//Thread.sleep(5000);
					
					try {
						WrapReadableMiddleObject wrapReadableMiddleObject = asynPrivateMailbox.getSyncOutputMessage();
						
						log.info("메시지 수신 성공, {}", wrapReadableMiddleObject.toString());
					} catch(SocketTimeoutException e) {
						// log.info(e.getMessage(), e);
						log.warn(e.getMessage());
					}		

				} catch (Exception e) {
					String errorMessage = String.format("예외 발생하여 Thread 종료::errorMessage=%s", e.getMessage());
					log.warn(errorMessage, e);
					break;
				} finally {
					if (null != asynPrivateMailbox) {
						
						asynPrivateMailboxPool.offer(asynPrivateMailbox);
						
					}
				}
			}
		} catch(InterruptedException e) {
			String errorMessage = String.format("인터럽트 발생, 루프 횟수[%d] 만에 강제 종료", numberOfExecution);
			log.warn(errorMessage, e);
		}
	}
}
