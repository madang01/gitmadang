package kr.pe.sinnori.server.threadpool;

import kr.pe.sinnori.server.threadpool.executor.ServerExecutorPoolIF;
import kr.pe.sinnori.server.threadpool.executor.handler.ServerExecutorIF;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderPoolIF;
import kr.pe.sinnori.server.threadpool.inputmessage.handler.InputMessageReaderIF;
import kr.pe.sinnori.server.threadpool.outputmessage.OutputMessageWriterPoolIF;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

public interface IEOThreadPoolSetManagerIF {
	
	public void setInputMessageReaderPool(InputMessageReaderPoolIF inputMessageReaderPool);
	public void setExecutorPool(ServerExecutorPoolIF executorPool);
	public void setOutputMessageWriterPool(OutputMessageWriterPoolIF outputMessageWriterPool);
	
	public InputMessageReaderIF getInputMessageReaderWithMinimumMumberOfSockets();
	public ServerExecutorIF getExecutorWithMinimumMumberOfSockets();
	public OutputMessageWriterIF getOutputMessageWriterWithMinimumMumberOfSockets();
	
}
