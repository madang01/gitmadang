package kr.pe.sinnori.client.connection.asyn.mailbox;

import java.net.SocketTimeoutException;
import java.nio.channels.SocketChannel;
import java.util.List;

import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public interface AsynMailboxIF {
	/*public int getMailboxID();
	public int getNextMailID();*/
	
	public ToLetter makeNewToLetter(SocketChannel toSocketChannel, String messageID, List<WrapBuffer> wrapBufferList);
	public void putToSyncOutputMessageQueue(WrapReadableMiddleObject wrapReadableMiddleObject)
			throws InterruptedException;
	
	public WrapReadableMiddleObject getSyncOutputMessage() throws SocketTimeoutException, InterruptedException;
	
}
