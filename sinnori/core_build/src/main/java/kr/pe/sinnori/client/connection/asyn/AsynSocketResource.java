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
	
	public void releaseSocketResources() {
		socketOutputStream.close();
		if (null == ownerAsynConnection) {
			/**
			 * <pre> 
			 * 비동기 연결 객체 생성 실패시 미리 만든 이 비동기 자원을 해지하는 경우 이 자원을 소유한  비동기 연결을 뜻하는 변수 ownerAsynConnection 는  null 이다.
			 * 비동기 연결 객체가 생성될때  비동기 입력/출력/처리 담당 쓰레드에 연결이 등록된다.
			 * 비동기 연결 객체 실패시 즉   ownerAsynConnection 이 null 인 경우
			 * 비동기 입력/출력/처리 담당 쓰레드에 비동기 연결이 미등록 되어 있으므로 따로 삭제할 필요 없다.
			 *  
			 * </pre>
			 */
			return;
		}
		
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
