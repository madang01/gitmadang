package kr.pe.sinnori.client.connection.asyn.share;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.client.connection.asyn.mailbox.SyncMailboxForAsynPublic;
import kr.pe.sinnori.client.connection.asyn.mailbox.SyncMailboxIF;

public class SyncMailboxMapperForAsynPublic implements SyncMailboxMapperForAsynPublicIF {
	@SuppressWarnings("unused")
	private InternalLogger log = InternalLoggerFactory.getInstance(SyncMailboxMapperForAsynPublic.class);
	
	private SyncMailboxIF[] syncMailboxs = null;
	
	public SyncMailboxMapperForAsynPublic(int totalNumberOfAsynPrivateMailboxs, 
			long socketTimeOut) {
		if (totalNumberOfAsynPrivateMailboxs < 1) {
			String errorMessage = String
					.format("the parameter totalNumberOfAsynPrivateMailboxs[%d] is less than one"
							, totalNumberOfAsynPrivateMailboxs);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (socketTimeOut < 0) {
			String errorMessage = String.format("the parameter socketTimeOut[%d] is less than zero", socketTimeOut);
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		syncMailboxs = new SyncMailboxForAsynPublic[totalNumberOfAsynPrivateMailboxs+1];
		
		// AsynPublicMailbox asynPublicMailbox = new AsynPublicMailbox(socketTimeOut, outputMessageQueue);
		
		for (int mailboxID=1; mailboxID < syncMailboxs.length; mailboxID++) {
			SyncMailboxForAsynPublic syncMailboxForAsynPublic = new SyncMailboxForAsynPublic(mailboxID, socketTimeOut);
			syncMailboxs[mailboxID] = syncMailboxForAsynPublic;
		}
	}
	
	public SyncMailboxIF getAsynMailbox(int mailboxID) {
		if (mailboxID <= 0) {
			String errorMessage = String.format("the parameter mailboxID[%d] is less than or equal to zero", mailboxID);
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		if (mailboxID >= syncMailboxs.length) {
			String errorMessage = String.format("the parameter mailboxID[%d] is out of range(1 ~ [%d])", 
					mailboxID, syncMailboxs.length - 1);
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		return syncMailboxs[mailboxID];
	}
	
	public int getTotalNumberOfAsynPrivateMailboxs() {
		return (syncMailboxs.length - 1);
	}
}
