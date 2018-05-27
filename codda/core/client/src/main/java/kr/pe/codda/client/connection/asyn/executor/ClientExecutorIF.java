package kr.pe.codda.client.connection.asyn.executor;

import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;

public interface ClientExecutorIF {
	public void registerAsynConnection(AsynConnectionIF asynConnection);
	public int getNumberOfConnection();	
	public void removeAsynConnection(AsynConnectionIF asynConnection);	
	public void putAsynOutputMessage(ReadableMiddleObjectWrapper readableMiddleObjectWrapper) throws InterruptedException;
	
	public boolean isAlive();
	public void start();
	public void interrupt();
}
