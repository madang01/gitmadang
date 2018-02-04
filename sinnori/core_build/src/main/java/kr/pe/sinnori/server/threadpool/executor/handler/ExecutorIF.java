package kr.pe.sinnori.server.threadpool.executor.handler;

import java.nio.channels.SocketChannel;

import kr.pe.sinnori.common.asyn.FromLetter;

public interface ExecutorIF {
	public void addNewSocket(SocketChannel newSC);
	public int getNumberOfSocket();	
	public void removeSocket(SocketChannel sc);	
	public void putIntoQueue(FromLetter fromLetter) throws InterruptedException;
}
