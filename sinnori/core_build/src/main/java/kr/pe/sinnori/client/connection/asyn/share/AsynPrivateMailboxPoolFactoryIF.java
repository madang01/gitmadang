package kr.pe.sinnori.client.connection.asyn.share;

import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxPool;

public interface AsynPrivateMailboxPoolFactoryIF {
	public AsynPrivateMailboxPool makeNewAsynPrivateMailboxPool();
}
