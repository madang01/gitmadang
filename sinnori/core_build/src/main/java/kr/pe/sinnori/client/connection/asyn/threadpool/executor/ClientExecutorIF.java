package kr.pe.sinnori.client.connection.asyn.threadpool.executor;

import kr.pe.sinnori.client.connection.asyn.IOEAsynConnectionIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public interface ClientExecutorIF {
	public void registerAsynConnection(IOEAsynConnectionIF asynConnection);
	public int getNumberOfConnection();	
	public void removeAsynConnection(IOEAsynConnectionIF asynConnection);	
	public void putAsynOutputMessage(WrapReadableMiddleObject wrapReadableMiddleObject) throws InterruptedException;
	public boolean isAlive();
	public void start();
	public void interrupt();
}
