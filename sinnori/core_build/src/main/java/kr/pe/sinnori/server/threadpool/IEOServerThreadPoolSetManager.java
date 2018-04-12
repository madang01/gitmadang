package kr.pe.sinnori.server.threadpool;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.server.threadpool.executor.ServerExecutorIF;
import kr.pe.sinnori.server.threadpool.executor.ServerExecutorPoolIF;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderIF;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderPoolIF;
import kr.pe.sinnori.server.threadpool.outputmessage.OutputMessageWriterIF;
import kr.pe.sinnori.server.threadpool.outputmessage.OutputMessageWriterPoolIF;

public class IEOServerThreadPoolSetManager implements IEOServerThreadPoolSetManagerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(IEOServerThreadPoolSetManager.class);
	
	private InputMessageReaderPoolIF inputMessageReaderPool = null;
	private ServerExecutorPoolIF serverExecutorPool = null;
	private OutputMessageWriterPoolIF outputMessageWriterPool = null;

	public void setInputMessageReaderPool(InputMessageReaderPoolIF inputMessageReaderPool) {
		if (null == inputMessageReaderPool) {
			throw new IllegalArgumentException("the parameter inputMessageReaderPool is null");
		}
		this.inputMessageReaderPool = inputMessageReaderPool;
	}
	
	public void setExecutorPool(ServerExecutorPoolIF serverExecutorPool) {
		if (null == serverExecutorPool) {
			throw new IllegalArgumentException("the parameter serverExecutorPool is null");
		}
		
		this.serverExecutorPool = serverExecutorPool;
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
		return inputMessageReaderPool.getInputMessageReaderWithMinimumNumberOfSockets();
	}
	
	public ServerExecutorIF getExecutorWithMinimumMumberOfSockets() {
		if (null == serverExecutorPool) {
			log.error("the var serverExecutorPool is null");
			System.exit(1);
		}
		
		
		return serverExecutorPool.getExecutorWithMinimumNumberOfSockets();
	}
	
	public OutputMessageWriterIF getOutputMessageWriterWithMinimumMumberOfSockets() {
		if (null == outputMessageWriterPool) {
			log.error("the var outputMessageWriterPool is null");
			System.exit(1);
		}		
		
		return outputMessageWriterPool.getOutputMessageWriterWithMinimumNumberOfSockets();
	}
}
