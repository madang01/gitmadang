package kr.pe.codda.server.threadpool.executor;

import java.nio.channels.SocketChannel;

import kr.pe.codda.common.protocol.ReceivedMessageBlockingQueueIF;

public interface ServerExecutorIF extends ReceivedMessageBlockingQueueIF {
	public void addNewSocket(SocketChannel newSC);
	public int getNumberOfConnection();
	public void removeSocket(SocketChannel sc);
	
	/** Thread 상속 메소드 시작 */
	public boolean isAlive();
	public void start();
	public void interrupt();
	/** Thread 상속 메소드 종료 */
}
