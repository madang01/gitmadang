package kr.pe.sinnori.client.connection.asyn.share;

import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxIF;

public interface AsynPrivateMailboxMapperIF {
	public AsynPrivateMailboxIF getAsynMailbox(int mailboxID);
	public int getTotalNumberOfAsynPrivateMailboxs();
}
