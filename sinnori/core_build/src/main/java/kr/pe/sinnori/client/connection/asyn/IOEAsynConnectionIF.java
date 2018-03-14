package kr.pe.sinnori.client.connection.asyn;

import java.nio.channels.SocketChannel;

import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.io.SocketOutputStream;

public interface IOEAsynConnectionIF {
	public void putToOutputMessageQueue(FromLetter fromLetter) throws InterruptedException;
	public SocketChannel getSocketChannel();
	
	public SocketOutputStream getSocketOutputStream();
	public void setFinalReadTime();
	public String getSimpleConnectionInfo();
	public void noticeThisConnectionWasRemovedFromReadyOnleySelector();
}
