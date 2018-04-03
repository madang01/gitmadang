package kr.pe.sinnori.server.threadpool.executor;

import java.nio.channels.SocketChannel;

import kr.pe.sinnori.common.asyn.FromLetter;

public interface ServerExecutorIF {
	public void addNewSocket(SocketChannel newSC);
	public int getNumberOfConnection();
	public void removeSocket(SocketChannel sc);	
	public void putIntoQueue(FromLetter fromLetter) throws InterruptedException;
	public boolean isAlive();
	public void start();
	public void interrupt();
}
