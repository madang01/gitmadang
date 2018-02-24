package kr.pe.sinnori.client.connection.asyn.threadpool.executor;

import kr.pe.sinnori.client.connection.asyn.threadpool.executor.handler.ClientExecutorIF;

public interface ClientExecutorPoolIF {
	public ClientExecutorIF getClientExecutorWithMinimumNumberOfConnetion();
}
