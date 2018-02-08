package kr.pe.sinnori.server.threadpool;

import kr.pe.sinnori.server.threadpool.executor.ExecutorPoolIF;
import kr.pe.sinnori.server.threadpool.executor.handler.ExecutorIF;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderPoolIF;
import kr.pe.sinnori.server.threadpool.inputmessage.handler.InputMessageReaderIF;
import kr.pe.sinnori.server.threadpool.outputmessage.OutputMessageWriterPoolIF;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

public interface IEOThreadPoolSetManagerIF {
	
	public void setInputMessageReaderPool(InputMessageReaderPoolIF inputMessageReaderPool);
	public void setExecutorPool(ExecutorPoolIF executorPool);
	public void setOutputMessageWriterPool(OutputMessageWriterPoolIF outputMessageWriterPool);
	
	public InputMessageReaderIF getInputMessageReaderWithMinimumMumberOfSockets();
	public ExecutorIF getExecutorWithMinimumMumberOfSockets();
	public OutputMessageWriterIF getOutputMessageWriterWithMinimumMumberOfSockets();
	
}
