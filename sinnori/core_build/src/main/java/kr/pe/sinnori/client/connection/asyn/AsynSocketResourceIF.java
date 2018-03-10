package kr.pe.sinnori.client.connection.asyn;

import kr.pe.sinnori.client.connection.SocketResoruceIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.ClientExecutorIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderIF;

public interface AsynSocketResourceIF extends SocketResoruceIF {
	public void releaseSocketResources();
	public InputMessageWriterIF getInputMessageWriter();
	public OutputMessageReaderIF getOutputMessageReader();
	public ClientExecutorIF getClientExecutor();
	public void setOwnerAsynConnection(AbstractAsynConnection ownerAsynConnection);
}
