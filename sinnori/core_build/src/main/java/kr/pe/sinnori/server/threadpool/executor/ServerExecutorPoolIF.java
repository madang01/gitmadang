package kr.pe.sinnori.server.threadpool.executor;

public interface ServerExecutorPoolIF {
	public ServerExecutorIF getExecutorWithMinimumNumberOfSockets();
}
