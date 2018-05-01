package kr.pe.sinnori.client.connection.asyn.mailbox;

import java.net.SocketTimeoutException;

import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public interface SyncMailboxIF {
	public int getMailboxID();
	public int getMailID();
	
	// public ToLetter makeNewToLetter(SocketChannel toSocketChannel, String messageID, List<WrapBuffer> wrapBufferList);
	
	public void putSyncOutputMessage(WrapReadableMiddleObject wrapReadableMiddleObject)
			throws InterruptedException;
	
	public WrapReadableMiddleObject getSyncOutputMessage() throws SocketTimeoutException, InterruptedException;
	
}
