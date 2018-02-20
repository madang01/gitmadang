package kr.pe.sinnori.server.threadpool.executor;

import kr.pe.sinnori.server.threadpool.executor.handler.ServerExecutorIF;

public interface ServerExecutorPoolIF {
	public ServerExecutorIF getExecutorWithMinimumMumberOfSockets();
}
