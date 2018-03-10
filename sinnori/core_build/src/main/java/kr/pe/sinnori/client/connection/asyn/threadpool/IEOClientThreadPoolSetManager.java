package kr.pe.sinnori.client.connection.asyn.threadpool;

import kr.pe.sinnori.client.connection.asyn.threadpool.executor.ClientExecutorIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.ClientExecutorPoolIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterPoolIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPoolIF;

public class IEOClientThreadPoolSetManager implements IEOClientThreadPoolSetManagerIF {
	// private Logger log = LoggerFactory.getLogger(IEOClientThreadPoolSetManager.class);
	
	
	private InputMessageWriterPoolIF inputMessageWriterPool = null;
	private OutputMessageReaderPoolIF outputMessageReaderPool = null;
	private ClientExecutorPoolIF clientExecutorPool = null;
	
	public IEOClientThreadPoolSetManager(InputMessageWriterPoolIF inputMessageWriterPool,
			OutputMessageReaderPoolIF outputMessageReaderPool,
			ClientExecutorPoolIF clientExecutorPool) {
		if (null == inputMessageWriterPool) {
			throw new IllegalArgumentException("the parameter inputMessageWriterPool is null");
		}		
		if (null == outputMessageReaderPool) {
			throw new IllegalArgumentException("the parameter outputMessageReaderPool is null");
		}
		
		if (null == clientExecutorPool) {
			throw new IllegalArgumentException("the parameter clientExecutorPool is null");
		}
		
		this.inputMessageWriterPool = inputMessageWriterPool;
		this.outputMessageReaderPool = outputMessageReaderPool;
		this.clientExecutorPool = clientExecutorPool;
	}
	
	@Override
	public InputMessageWriterIF getInputMessageWriterWithMinimumNumberOfConnetion() {
		return inputMessageWriterPool.getInputMessageWriterWithMinimumNumberOfConnetion();
	}
	
	public OutputMessageReaderIF getOutputMessageReaderWithMinimumNumberOfConnetion() {
		return outputMessageReaderPool.getOutputMessageReaderWithMinimumNumberOfConnetion();
	}	

	@Override
	public ClientExecutorIF getClientExecutorWithMinimumNumberOfConnetion() {
		return clientExecutorPool.getClientExecutorWithMinimumNumberOfConnetion();
	}
	
	
}
