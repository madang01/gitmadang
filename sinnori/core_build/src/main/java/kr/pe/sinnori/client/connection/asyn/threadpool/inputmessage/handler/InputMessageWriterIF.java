package kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler;

import java.nio.channels.SocketChannel;

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.common.asyn.ToLetter;

public interface InputMessageWriterIF {
	public void registerAsynConnection(AbstractAsynConnection asynConn);
	public int getNumberOfSocket();	
	public void removeSocket(SocketChannel sc);
	public void putIntoQueue(ToLetter toLetter) throws InterruptedException;
}
