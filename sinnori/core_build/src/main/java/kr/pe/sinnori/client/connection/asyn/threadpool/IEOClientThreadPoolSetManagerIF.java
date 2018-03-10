package kr.pe.sinnori.client.connection.asyn.threadpool;

import kr.pe.sinnori.client.connection.asyn.threadpool.executor.ClientExecutorIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderIF;

public interface IEOClientThreadPoolSetManagerIF {
	public InputMessageWriterIF getInputMessageWriterWithMinimumNumberOfConnetion();
	public OutputMessageReaderIF getOutputMessageReaderWithMinimumNumberOfConnetion();	
	public ClientExecutorIF getClientExecutorWithMinimumNumberOfConnetion();
}
