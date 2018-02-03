package kr.pe.sinnori.client.asyn;

import java.net.SocketTimeoutException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailbox;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxPool;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class AsynPrivateMailboxProducerThread extends Thread {
	private Logger log = LoggerFactory.getLogger(AsynPrivateMailboxProducerThread.class);

	private int numberOfExecution;
	private AsynPrivateMailboxPool asynPrivateMailboxPool = null;
	private LinkedBlockingQueue<ToLetter> toLetterQueue = null;

	public AsynPrivateMailboxProducerThread(int numberOfExecutions,
			AsynPrivateMailboxPool asynPrivateMailboxPool,
			LinkedBlockingQueue<ToLetter> toLetterQueue) {
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
		AsynPrivateMailbox asynPrivateMailbox = null;
		for (int i = 0; i < numberOfExecution; i++) {

			if (this.isInterrupted()) {
				log.info("this thread에 interrupt 발생하여 종료");
				break;
			}

			try {
				asynPrivateMailbox = asynPrivateMailboxPool.take();
			} catch (InterruptedException e) {
				String errorMessage = String.format("InterruptedException");
				log.warn(errorMessage, e);
				break;
			}

			try {
				SocketChannel toSocketChannel = SocketChannel.open();
				String messageID = "Echo";
				List<WrapBuffer> wrapBufferList = new ArrayList<WrapBuffer>();

				ToLetter toLetter = asynPrivateMailbox.makeNewToLetter(toSocketChannel, messageID, wrapBufferList);
				
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
					asynPrivateMailboxPool.put(asynPrivateMailbox);
				}
			}
		}
	}
}
