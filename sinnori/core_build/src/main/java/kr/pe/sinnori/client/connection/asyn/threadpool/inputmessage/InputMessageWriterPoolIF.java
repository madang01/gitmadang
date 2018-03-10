package kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage;

public interface InputMessageWriterPoolIF {
	public InputMessageWriterIF getInputMessageWriterWithMinimumNumberOfConnetion();
}
