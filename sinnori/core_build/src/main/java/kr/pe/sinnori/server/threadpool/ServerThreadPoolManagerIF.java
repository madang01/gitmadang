package kr.pe.sinnori.server.threadpool;

import kr.pe.sinnori.server.threadpool.executor.ExecutorPoolIF;
import kr.pe.sinnori.server.threadpool.executor.handler.ExecutorIF;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderPoolIF;
import kr.pe.sinnori.server.threadpool.inputmessage.handler.InputMessageReaderIF;
import kr.pe.sinnori.server.threadpool.outputmessage.OutputMessageWriterPoolIF;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

public interface ServerThreadPoolManagerIF {
	/**
	 * 서버 메시지 입력/처리/출력을 위한 3개 폴에 대한 설정을 뒤로 미루기 위한 메소드
	 * @param inputMessageReaderPool
	 * @param executorPool
	 * @param outputMessageWriterPool
	 */
	public void register(InputMessageReaderPoolIF inputMessageReaderPool,
			ExecutorPoolIF executorPool,
			OutputMessageWriterPoolIF outputMessageWriterPool);
	
	public InputMessageReaderIF getInputMessageReaderWithMinimumMumberOfSockets();
	public ExecutorIF getExecutorWithMinimumMumberOfSockets();
	public OutputMessageWriterIF getOutputMessageWriterWithMinimumMumberOfSockets();
	
}
