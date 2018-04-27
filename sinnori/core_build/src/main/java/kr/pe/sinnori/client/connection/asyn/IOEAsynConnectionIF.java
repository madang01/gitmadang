package kr.pe.sinnori.client.connection.asyn;

import java.nio.channels.SocketChannel;

import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public interface IOEAsynConnectionIF {
	public void putToOutputMessageQueue(WrapReadableMiddleObject wrapReadableMiddleObject) throws InterruptedException;
	public SocketChannel getSocketChannel();
	
	public SocketOutputStream getSocketOutputStream();
	public void setFinalReadTime();
	public String getSimpleConnectionInfo();
	public void noticeThisConnectionWasRemovedFromReadyOnleySelector();
}
