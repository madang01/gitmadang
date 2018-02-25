package kr.pe.sinnori.client.connection.asyn.threadpool;

import kr.pe.sinnori.client.connection.asyn.threadpool.executor.handler.ClientExecutorIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderIF;

public interface IEOClientThreadPoolSetManagerIF {
	public InputMessageWriterIF getInputMessageWriterWithMinimumNumberOfConnetion();
	public OutputMessageReaderIF getOutputMessageReaderWithMinimumNumberOfConnetion();	
	public ClientExecutorIF getClientExecutorWithMinimumNumberOfConnetion();
}
