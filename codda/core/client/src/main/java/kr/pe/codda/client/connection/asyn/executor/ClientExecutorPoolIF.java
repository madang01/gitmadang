package kr.pe.codda.client.connection.asyn.executor;

public interface ClientExecutorPoolIF {
	public ClientExecutorIF getClientExecutorWithMinimumNumberOfConnetion();
}
