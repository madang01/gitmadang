package kr.pe.codda.server.threadpool;

import kr.pe.codda.server.threadpool.executor.ServerExecutorIF;
import kr.pe.codda.server.threadpool.executor.ServerExecutorPoolIF;
import kr.pe.codda.server.threadpool.outputmessage.OutputMessageWriterIF;
import kr.pe.codda.server.threadpool.outputmessage.OutputMessageWriterPoolIF;

public interface IEOServerThreadPoolSetManagerIF {
	
	// public void setInputMessageReaderPool(InputMessageReaderPoolIF inputMessageReaderPool);
	public void setExecutorPool(ServerExecutorPoolIF executorPool);
	public void setOutputMessageWriterPool(OutputMessageWriterPoolIF outputMessageWriterPool);
	
	// public InputMessageReaderIF getInputMessageReaderWithMinimumMumberOfSockets();
	public ServerExecutorIF getExecutorWithMinimumMumberOfSockets();
	public OutputMessageWriterIF getOutputMessageWriterWithMinimumMumberOfSockets();
	
}
