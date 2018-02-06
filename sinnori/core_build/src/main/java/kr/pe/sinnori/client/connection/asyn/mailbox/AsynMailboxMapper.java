package kr.pe.sinnori.client.connection.asyn.mailbox;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class AsynMailboxMapper {
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(AsynMailboxMapper.class);	
	// private final Object monitor = new Object();
	
	private AsynMailboxIF[] asynMailboxes = null; 
	
	public AsynMailboxMapper(int totalNumberOfAsynMailbox, 
			long socketTimeOut, 
			LinkedBlockingQueue<WrapReadableMiddleObject> outputMessageQueue) {
		if (totalNumberOfAsynMailbox < 2) {
			String errorMessage = String
					.format("the parameter totalNumberOfAsynMailbox[%d] is less than two"
							, totalNumberOfAsynMailbox);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (socketTimeOut < 0) {
			String errorMessage = String.format("the parameter socketTimeOut[%d] is less than zero", socketTimeOut);
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		if (null == outputMessageQueue) {
			throw new IndexOutOfBoundsException("the parameter outputMessageQueue is null");
		}
		
		asynMailboxes = new AsynMailboxIF[totalNumberOfAsynMailbox];
		
		AsynPublicMailbox asynPublicMailbox = new AsynPublicMailbox(socketTimeOut, outputMessageQueue);
		asynMailboxes[0] = asynPublicMailbox;
		for (int mailboxID=1; mailboxID < totalNumberOfAsynMailbox; mailboxID++) {
			AsynPrivateMailbox asynPrivateMailbox = new AsynPrivateMailbox(mailboxID, socketTimeOut);
			asynMailboxes[mailboxID] = asynPrivateMailbox;
		}
	}
	
	public AsynMailboxIF getAsynMailbox(int mailboxID) {
		if (mailboxID < 0) {
			String errorMessage = String.format("the parameter mailboxID[%d] is less than zero", mailboxID);
			throw new IndexOutOfBoundsException(errorMessage);
		}
		if (mailboxID >= asynMailboxes.length) {
			String errorMessage = String.format("the parameter mailboxID[%d] is out of range(0 ~ [%d])", 
					mailboxID, asynMailboxes.length - 1);
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		return asynMailboxes[mailboxID];
	}
	
	public int getTotalNumberOfAsynMailbox() {
		return asynMailboxes.length;
	}
}
