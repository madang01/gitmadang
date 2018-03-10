package kr.pe.sinnori.server.threadpool.outputmessage;

import java.nio.channels.SocketChannel;

import kr.pe.sinnori.common.asyn.ToLetter;

public interface OutputMessageWriterIF {
	public void addNewSocket(SocketChannel newSC);
	public int getNumberOfSocket();	
	public void removeSocket(SocketChannel sc);
	public void putIntoQueue(ToLetter toLetter) throws InterruptedException;
}
