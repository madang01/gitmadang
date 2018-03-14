package kr.pe.sinnori.client.connection.asyn.mailbox;

import java.net.SocketTimeoutException;

import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public interface AsynPrivateMailboxIF {
	public int getMailboxID();
	public int getMailID();
	
	// public ToLetter makeNewToLetter(SocketChannel toSocketChannel, String messageID, List<WrapBuffer> wrapBufferList);
	
	public void putSyncOutputMessage(FromLetter fromLetter)
			throws InterruptedException;
	
	public WrapReadableMiddleObject getSyncOutputMessage() throws SocketTimeoutException, InterruptedException;
	
}
