package kr.pe.sinnori.server.threadpool.outputmessage;

import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

public interface OutputMessageWriterPoolIF {
	public OutputMessageWriterIF getOutputMessageWriterWithMinimumMumberOfSockets();
}
