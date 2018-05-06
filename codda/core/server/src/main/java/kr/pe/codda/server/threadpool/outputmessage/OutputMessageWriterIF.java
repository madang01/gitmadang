package kr.pe.codda.server.threadpool.outputmessage;

import java.nio.channels.SocketChannel;

import kr.pe.codda.common.asyn.ToLetter;

public interface OutputMessageWriterIF {
	public void addNewSocket(SocketChannel newSC);
	public int getNumberOfConnection();
	public void removeSocket(SocketChannel sc);
	public void putIntoQueue(ToLetter toLetter) throws InterruptedException;
	public boolean isAlive();
	public void start();
	public void interrupt();
}
