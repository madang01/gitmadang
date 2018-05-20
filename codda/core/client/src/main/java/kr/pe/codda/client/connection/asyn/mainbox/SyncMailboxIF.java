package kr.pe.codda.client.connection.asyn.mainbox;

import java.io.IOException;

import kr.pe.codda.common.protocol.WrapReadableMiddleObject;

public interface SyncMailboxIF {
	public int getMailboxID();
	public int getMailID();
	
	public WrapReadableMiddleObject getSyncOutputMessage() throws IOException, InterruptedException;
	public void putSyncOutputMessage(WrapReadableMiddleObject wrapReadableMiddleObject) throws InterruptedException;
	// public WrapReadableMiddleObject putAsynOutputMessage() throws SocketTimeoutException, InterruptedException;
}
