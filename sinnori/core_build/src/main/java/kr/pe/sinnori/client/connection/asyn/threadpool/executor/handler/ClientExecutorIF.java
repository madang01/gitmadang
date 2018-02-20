package kr.pe.sinnori.client.connection.asyn.threadpool.executor.handler;

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.common.asyn.FromLetter;

public interface ClientExecutorIF {
	public void addNewAsynConnection(AbstractAsynConnection asynConnection);
	public int getNumberOfAsynConnection();	
	public void removeAsynConnection(AbstractAsynConnection asynConnection);	
	public void putIntoQueue(FromLetter fromLetter) throws InterruptedException;
}
