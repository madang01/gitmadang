package kr.pe.sinnori.client.connection.asyn.share;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailbox;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxIF;

public class AsynPrivateMailboxMapper implements AsynPrivateMailboxMapperIF {
	@SuppressWarnings("unused")
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynPrivateMailboxMapper.class);
	
	private AsynPrivateMailboxIF[] asynPrivateMailboxs = null;
	
	public AsynPrivateMailboxMapper(int totalNumberOfAsynPrivateMailboxs, 
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
		
		asynPrivateMailboxs = new AsynPrivateMailbox[totalNumberOfAsynPrivateMailboxs+1];
		
		// AsynPublicMailbox asynPublicMailbox = new AsynPublicMailbox(socketTimeOut, outputMessageQueue);
		
		for (int mailboxID=1; mailboxID < asynPrivateMailboxs.length; mailboxID++) {
			AsynPrivateMailbox asynPrivateMailbox = new AsynPrivateMailbox(mailboxID, socketTimeOut);
			asynPrivateMailboxs[mailboxID] = asynPrivateMailbox;
		}
	}
	
	public AsynPrivateMailboxIF getAsynMailbox(int mailboxID) {
		if (mailboxID <= 0) {
			String errorMessage = String.format("the parameter mailboxID[%d] is less than or equal to zero", mailboxID);
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		if (mailboxID >= asynPrivateMailboxs.length) {
			String errorMessage = String.format("the parameter mailboxID[%d] is out of range(1 ~ [%d])", 
					mailboxID, asynPrivateMailboxs.length - 1);
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		return asynPrivateMailboxs[mailboxID];
	}
	
	public int getTotalNumberOfAsynPrivateMailboxs() {
		return (asynPrivateMailboxs.length - 1);
	}
}
