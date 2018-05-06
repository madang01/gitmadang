package kr.pe.codda.server.threadpool.outputmessage;

public interface OutputMessageWriterPoolIF {
	public OutputMessageWriterIF getOutputMessageWriterWithMinimumNumberOfSockets();
}
