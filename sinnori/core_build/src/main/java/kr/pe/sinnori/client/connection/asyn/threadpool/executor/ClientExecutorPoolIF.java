package kr.pe.sinnori.client.connection.asyn.threadpool.executor;

public interface ClientExecutorPoolIF {
	public ClientExecutorIF getClientExecutorWithMinimumNumberOfConnetion();
}
