package kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage;

public interface OutputMessageReaderPoolIF {
	public OutputMessageReaderIF getOutputMessageReaderWithMinimumNumberOfConnetion();
}
