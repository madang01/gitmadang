package kr.pe.sinnori.server.threadpool;

import kr.pe.sinnori.server.threadpool.executor.ServerExecutorIF;
import kr.pe.sinnori.server.threadpool.executor.ServerExecutorPoolIF;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderIF;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderPoolIF;
import kr.pe.sinnori.server.threadpool.outputmessage.OutputMessageWriterIF;
import kr.pe.sinnori.server.threadpool.outputmessage.OutputMessageWriterPoolIF;

public interface IEOServerThreadPoolSetManagerIF {
	
	public void setInputMessageReaderPool(InputMessageReaderPoolIF inputMessageReaderPool);
	public void setExecutorPool(ServerExecutorPoolIF executorPool);
	public void setOutputMessageWriterPool(OutputMessageWriterPoolIF outputMessageWriterPool);
	
	public InputMessageReaderIF getInputMessageReaderWithMinimumMumberOfSockets();
	public ServerExecutorIF getExecutorWithMinimumMumberOfSockets();
	public OutputMessageWriterIF getOutputMessageWriterWithMinimumMumberOfSockets();
	
}
