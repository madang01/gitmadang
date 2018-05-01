package kr.pe.sinnori.client.connection.asyn.share;

import kr.pe.sinnori.client.connection.asyn.mailbox.SyncMailboxIF;

public interface SyncMailboxPoolForAsynPublicIF {
	public SyncMailboxIF poll(long timeout) throws InterruptedException;
	public boolean offer(SyncMailboxIF asynPrivateMailbox);
	public SyncMailboxMapperForAsynPublicIF getAsynPrivateMailboxMapper();
	public int getSize();
}
