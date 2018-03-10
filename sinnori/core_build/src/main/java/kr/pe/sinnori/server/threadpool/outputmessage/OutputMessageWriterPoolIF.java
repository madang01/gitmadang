package kr.pe.sinnori.server.threadpool.outputmessage;

public interface OutputMessageWriterPoolIF {
	public OutputMessageWriterIF getOutputMessageWriterWithMinimumNumberOfSockets();
}
