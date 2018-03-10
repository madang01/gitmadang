package kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage;

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.common.asyn.ToLetter;

public interface InputMessageWriterIF {
	public void registerAsynConnection(AbstractAsynConnection asynConnection);
	public int getNumberOfAsynConnection();	
	public void removeAsynConnection(AbstractAsynConnection asynConnection);
	public void putIntoQueue(ToLetter toLetter) throws InterruptedException;
}
