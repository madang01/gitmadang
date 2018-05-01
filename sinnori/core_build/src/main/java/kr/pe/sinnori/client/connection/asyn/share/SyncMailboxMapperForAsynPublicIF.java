package kr.pe.sinnori.client.connection.asyn.share;

import kr.pe.sinnori.client.connection.asyn.mailbox.SyncMailboxIF;

public interface SyncMailboxMapperForAsynPublicIF {
	public SyncMailboxIF getAsynMailbox(int mailboxID);
	public int getTotalNumberOfAsynPrivateMailboxs();
}
