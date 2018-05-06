package kr.pe.codda.server.threadpool.executor;

public interface ServerExecutorPoolIF {
	public ServerExecutorIF getExecutorWithMinimumNumberOfSockets();
}
