package kr.pe.codda.client.connection.asyn.share;

import java.io.IOException;

import kr.pe.codda.client.connection.ClientMessageUtilityIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporterIF;
import kr.pe.codda.client.connection.asyn.AsynClientIOEventControllerIF;
import kr.pe.codda.client.connection.asyn.AsynConnectedConnectionAdderIF;
import kr.pe.codda.client.connection.asyn.AsynThreadSafeSingleConnection;
import kr.pe.codda.client.connection.asyn.executor.ClientExecutorIF;
import kr.pe.codda.common.io.SocketOutputStream;

/**
 * <pre>
 * Note that this implementation is thread-safe.
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public final class AsynShareConnection extends AsynThreadSafeSingleConnection {
	
	public AsynShareConnection(String serverHost, int serverPort, long socketTimeout,
			int syncMessageMailboxCountPerAsynShareConnection, int inputMessageQueueSize,
			SocketOutputStream socketOutputStream, ClientMessageUtilityIF clientMessageUtility,
			AsynConnectedConnectionAdderIF asynConnectedConnectionAdder, ClientExecutorIF clientExecutor,
			AsynClientIOEventControllerIF asynClientIOEventController,
			ConnectionPoolSupporterIF connectionPoolSupporter) throws IOException {
		super(serverHost, serverPort, socketTimeout, syncMessageMailboxCountPerAsynShareConnection, inputMessageQueueSize,
				socketOutputStream, clientMessageUtility, asynConnectedConnectionAdder, clientExecutor,
				asynClientIOEventController, connectionPoolSupporter);
	}

	private boolean isQueueIn = true;

	/**
	 * 큐 속에 들어갈때 상태 변경 메소드
	 */
	protected void queueIn() {
		isQueueIn = true;
	}

	/**
	 * 큐 밖으로 나갈때 상태 변경 메소드
	 */
	protected void queueOut() {
		isQueueIn = false;
	}
	
	public boolean isInQueue() {
		return isQueueIn;
	}
}
