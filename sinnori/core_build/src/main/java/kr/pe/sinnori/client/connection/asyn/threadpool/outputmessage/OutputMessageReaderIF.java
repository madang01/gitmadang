package kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage;

import kr.pe.sinnori.client.connection.asyn.IOEAsynConnectionIF;

public interface OutputMessageReaderIF {
	public void registerAsynConnection(IOEAsynConnectionIF asynConn) throws InterruptedException;
	public int getNumberOfAsynConnection();
}
