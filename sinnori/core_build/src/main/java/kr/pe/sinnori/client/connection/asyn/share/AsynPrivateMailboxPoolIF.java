package kr.pe.sinnori.client.connection.asyn.share;

import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxIF;

public interface AsynPrivateMailboxPoolIF {
	public AsynPrivateMailboxIF poll(long timeout) throws InterruptedException;
	public boolean offer(AsynPrivateMailboxIF asynPrivateMailbox);
	public AsynPrivateMailboxMapperIF getAsynPrivateMailboxMapper();
}
