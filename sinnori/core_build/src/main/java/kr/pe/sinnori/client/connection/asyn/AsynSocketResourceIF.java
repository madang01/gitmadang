package kr.pe.sinnori.client.connection.asyn;

import kr.pe.sinnori.client.connection.SocketResoruceIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.handler.ClientExecutorIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderIF;

public interface AsynSocketResourceIF extends SocketResoruceIF {
	public void releaseSocketResources();
	public InputMessageWriterIF getInputMessageWriter();
	public OutputMessageReaderIF getOutputMessageReader();
	public ClientExecutorIF getClientExecutor();
	public void setOwnerAsynConnection(AbstractAsynConnection ownerAsynConnection);
}
