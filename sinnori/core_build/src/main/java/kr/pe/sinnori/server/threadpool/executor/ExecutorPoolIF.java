package kr.pe.sinnori.server.threadpool.executor;

import kr.pe.sinnori.server.threadpool.executor.handler.ExecutorIF;

public interface ExecutorPoolIF {
	public ExecutorIF getExecutorWithMinimumMumberOfSockets();
}
