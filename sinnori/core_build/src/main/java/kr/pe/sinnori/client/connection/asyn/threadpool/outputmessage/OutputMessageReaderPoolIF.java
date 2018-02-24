package kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage;

import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderIF;

public interface OutputMessageReaderPoolIF {
	public OutputMessageReaderIF getOutputMessageReaderWithMinimumNumberOfConnetion();
}
