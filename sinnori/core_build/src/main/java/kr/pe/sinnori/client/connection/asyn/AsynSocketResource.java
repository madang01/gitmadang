package kr.pe.sinnori.client.connection.asyn;

import kr.pe.sinnori.client.connection.asyn.threadpool.IEOClientThreadPoolSetManagerIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.ClientExecutorIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderIF;
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
			IEOClientThreadPoolSetManagerIF ieoClientThreadPoolSetManager) {
		if (null == socketOutputStream) {
			throw new IllegalArgumentException("the parameter socketOutputStream is null");
		}
		
		if (null == ieoClientThreadPoolSetManager) {
			throw new IllegalArgumentException("the parameter ieoClientThreadPoolSetManager is null");
		}
		
		
		
		this.socketOutputStream = socketOutputStream;		
		
		this.inputMessageWriter = ieoClientThreadPoolSetManager.getInputMessageWriterWithMinimumNumberOfConnetion();
		this.outputMessageReader = ieoClientThreadPoolSetManager.getOutputMessageReaderWithMinimumNumberOfConnetion();
		this.clientExecutor = ieoClientThreadPoolSetManager.getClientExecutorWithMinimumNumberOfConnetion();
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
