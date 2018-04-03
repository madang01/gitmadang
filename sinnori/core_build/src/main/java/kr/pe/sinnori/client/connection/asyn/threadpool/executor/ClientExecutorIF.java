package kr.pe.sinnori.client.connection.asyn.threadpool.executor;

import kr.pe.sinnori.client.connection.asyn.IOEAsynConnectionIF;
import kr.pe.sinnori.common.asyn.FromLetter;

public interface ClientExecutorIF {
	public void registerAsynConnection(IOEAsynConnectionIF asynConnection);
	public int getNumberOfConnection();	
	public void removeAsynConnection(IOEAsynConnectionIF asynConnection);	
	public void putAsynOutputMessage(FromLetter fromLetter) throws InterruptedException;
	public boolean isAlive();
	public void start();
	public void interrupt();
}
