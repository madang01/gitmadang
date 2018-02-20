package kr.pe.sinnori.server.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.server.threadpool.executor.ServerExecutorPoolIF;
import kr.pe.sinnori.server.threadpool.executor.handler.ServerExecutorIF;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderPoolIF;
import kr.pe.sinnori.server.threadpool.inputmessage.handler.InputMessageReaderIF;
import kr.pe.sinnori.server.threadpool.outputmessage.OutputMessageWriterPoolIF;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

public class IEOThreadPoolSetManager implements IEOThreadPoolSetManagerIF {
	private Logger log = LoggerFactory.getLogger(IEOThreadPoolSetManager.class);
	
	private InputMessageReaderPoolIF inputMessageReaderPool = null;
	private ServerExecutorPoolIF executorPool = null;
	private OutputMessageWriterPoolIF outputMessageWriterPool = null;

	public void setInputMessageReaderPool(InputMessageReaderPoolIF inputMessageReaderPool) {
		if (null == inputMessageReaderPool) {
			throw new IllegalArgumentException("the parameter inputMessageReaderPool is null");
		}
		this.inputMessageReaderPool = inputMessageReaderPool;
	}
	
	public void setExecutorPool(ServerExecutorPoolIF executorPool) {
		if (null == executorPool) {
			throw new IllegalArgumentException("the parameter executorPool is null");
		}
		
		this.executorPool = executorPool;
	}
	
	public void setOutputMessageWriterPool(OutputMessageWriterPoolIF outputMessageWriterPool) {
		if (null == outputMessageWriterPool) {
			throw new IllegalArgumentException("the parameter outputMessageWriterPool is null");
		}
		
		this.outputMessageWriterPool = outputMessageWriterPool;
	}
	
	public InputMessageReaderIF getInputMessageReaderWithMinimumMumberOfSockets() {
		if (null == inputMessageReaderPool) {
			log.error("the var inputMessageReaderPool is null");
			System.exit(1);
		}
		return inputMessageReaderPool.getInputMessageReaderWithMinimumMumberOfSockets();
	}
	
	public ServerExecutorIF getExecutorWithMinimumMumberOfSockets() {
		if (null == executorPool) {
			log.error("the var executorPool is null");
			System.exit(1);
		}
		
		
		return executorPool.getExecutorWithMinimumMumberOfSockets();
	}
	
	public OutputMessageWriterIF getOutputMessageWriterWithMinimumMumberOfSockets() {
		if (null == outputMessageWriterPool) {
			log.error("the var outputMessageWriterPool is null");
			System.exit(1);
		}		
		
		return outputMessageWriterPool.getOutputMessageWriterWithMinimumMumberOfSockets();
	}
}
