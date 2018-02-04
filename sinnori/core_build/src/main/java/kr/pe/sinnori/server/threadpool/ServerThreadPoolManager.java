package kr.pe.sinnori.server.threadpool;

import kr.pe.sinnori.server.threadpool.executor.ExecutorPoolIF;
import kr.pe.sinnori.server.threadpool.executor.handler.ExecutorIF;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderPoolIF;
import kr.pe.sinnori.server.threadpool.inputmessage.handler.InputMessageReaderIF;
import kr.pe.sinnori.server.threadpool.outputmessage.OutputMessageWriterPoolIF;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

public class ServerThreadPoolManager implements ServerThreadPoolManagerIF {
	private InputMessageReaderPoolIF inputMessageReaderPool = null;
	private ExecutorPoolIF executorPool = null;
	private OutputMessageWriterPoolIF outputMessageWriterPool = null;
	
	
	/**
	 * 3개 폴에 대한 설정을 뒤로 미루기 위한 메소드
	 * 
	 * @param inputMessageReaderPool
	 * @param executorPool
	 * @param outputMessageReaderThreadPool
	 */
	@Override
	public void register(InputMessageReaderPoolIF inputMessageReaderPool,
			ExecutorPoolIF executorPool,
			OutputMessageWriterPoolIF outputMessageWriterPool) {
		
		if (null == inputMessageReaderPool) {
			throw new IllegalArgumentException("the parameter inputMessageReaderPool is null");
		}
		if (null == executorPool) {
			throw new IllegalArgumentException("the parameter executorPool is null");
		}
		if (null == outputMessageWriterPool) {
			throw new IllegalArgumentException("the parameter outputMessageWriterPool is null");
		}
		
		this.inputMessageReaderPool = inputMessageReaderPool;
		this.executorPool = executorPool;
		this.outputMessageWriterPool = outputMessageWriterPool;
	}
	
	public InputMessageReaderIF getInputMessageReaderWithMinimumMumberOfSockets() {
		return inputMessageReaderPool.getInputMessageReaderWithMinimumMumberOfSockets();
	}
	
	public ExecutorIF getExecutorWithMinimumMumberOfSockets() {
		return executorPool.getExecutorWithMinimumMumberOfSockets();
	}
	
	public OutputMessageWriterIF getOutputMessageWriterWithMinimumMumberOfSockets() {
		return outputMessageWriterPool.getOutputMessageWriterWithMinimumMumberOfSockets();
	}
}
