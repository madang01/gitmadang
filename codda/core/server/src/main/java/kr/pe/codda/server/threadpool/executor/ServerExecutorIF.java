package kr.pe.codda.server.threadpool.executor;

import java.nio.channels.SocketChannel;

import kr.pe.codda.common.protocol.ReceivedMessageBlockingQueueIF;

public interface ServerExecutorIF {
	public void addNewSocket(SocketChannel newSC);
	public int getNumberOfConnection();
	public void removeSocket(SocketChannel sc);
	public boolean isAlive();
	public void start();
	public void interrupt();
	public ReceivedMessageBlockingQueueIF getWrapMessageBlockingQueue();
	
}
