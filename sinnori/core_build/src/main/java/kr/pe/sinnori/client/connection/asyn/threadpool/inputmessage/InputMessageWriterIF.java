package kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage;

import kr.pe.sinnori.client.connection.asyn.IOEAsynConnectionIF;
import kr.pe.sinnori.common.asyn.ToLetter;

public interface InputMessageWriterIF {
	public void registerAsynConnection(IOEAsynConnectionIF asynConnection);
	public int getNumberOfConnection();	
	public void removeAsynConnection(IOEAsynConnectionIF asynConnection);
	public void putIntoQueue(ToLetter toLetter) throws InterruptedException;
	public boolean isAlive();
	public void start();
	public void interrupt();
}
