package kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler;

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;

public interface OutputMessageReaderIF {
	public void registerAsynConnection(AbstractAsynConnection asynConn);
	public int getNumberOfAsynConnection();
}
