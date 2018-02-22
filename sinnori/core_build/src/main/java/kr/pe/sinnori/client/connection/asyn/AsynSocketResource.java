package kr.pe.sinnori.client.connection.asyn;

import kr.pe.sinnori.client.connection.asyn.threadpool.executor.handler.ClientExecutorIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderIF;
import kr.pe.sinnori.common.io.SocketOutputStream;

public class AsynSocketResource implements AsynSocketResourceIF {
	// private volatile boolean isDone = false;
	// private final Object monitor = new Object();
	// private final Object doneMonitor = new Object();
	
	
	private SocketOutputStream socketOutputStream = null;
	private InputMessageWriterIF inputMessageWriter = null;
	private OutputMessageReaderIF outputMessageReader = null;
	private ClientExecutorIF clientExecutor = null;
	
	private AbstractAsynConnection ownerAsynConnection = null;
	
	public AsynSocketResource(SocketOutputStream socketOutputStream,
			InputMessageWriterIF inputMessageWriter,
			OutputMessageReaderIF outputMessageReader,
			ClientExecutorIF clientExecutor) {
		this.socketOutputStream = socketOutputStream;
		this.inputMessageWriter = inputMessageWriter;
		this.outputMessageReader = outputMessageReader;
		this.clientExecutor = clientExecutor;
	}	
	
	public void setOwnerAsynConnection(AbstractAsynConnection ownerAsynConnection) {
		this.ownerAsynConnection = ownerAsynConnection;
	}
	
	/*public void releaseSocketResources() {
		*//** do nothing because #doReleaseSocketResources() called from OutputmessageReader#run when socket closed *//*
	}*/
	
	public void releaseSocketResources() {
		socketOutputStream.close();
		inputMessageWriter.removeAsynConnection(ownerAsynConnection);
		clientExecutor.removeAsynConnection(ownerAsynConnection);
	}

	public SocketOutputStream getSocketOutputStream() {
		return socketOutputStream;
	}

	public InputMessageWriterIF getInputMessageWriter() {
		return inputMessageWriter;
	}

	public OutputMessageReaderIF getOutputMessageReader() {
		return outputMessageReader;
	}

	public ClientExecutorIF getClientExecutor() {
		return clientExecutor;
	}

	
}
