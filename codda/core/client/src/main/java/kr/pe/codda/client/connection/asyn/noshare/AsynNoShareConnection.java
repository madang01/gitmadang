package kr.pe.codda.client.connection.asyn.noshare;

import java.io.IOException;

import kr.pe.codda.client.classloader.ClientTaskMangerIF;
import kr.pe.codda.client.connection.asyn.AsynConnectedConnectionAdderIF;
import kr.pe.codda.client.connection.asyn.AsynThreadSafeSingleConnection;
import kr.pe.codda.client.connection.asyn.ClientIOEventControllerIF;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.ReceivedDataStream;
import kr.pe.codda.common.protocol.MessageProtocolIF;

public class AsynNoShareConnection extends AsynThreadSafeSingleConnection {

	private boolean isQueueIn = true;	

	public AsynNoShareConnection(String projectName, String serverHost,
			int serverPort, long socketTimeout,
			int syncMessageMailboxCountPerAsynShareConnection,
			int clientAsynInputMessageQueueCapacity,
			ReceivedDataStream receivedDataOnlyStream,
			MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferPool,
			ClientTaskMangerIF clientTaskManger,
			AsynConnectedConnectionAdderIF asynConnectedConnectionAdder,
			ClientIOEventControllerIF asynClientIOEventController)
			throws IOException {
		
		super(projectName, serverHost, serverPort, socketTimeout, syncMessageMailboxCountPerAsynShareConnection,
				clientAsynInputMessageQueueCapacity, receivedDataOnlyStream, messageProtocol, dataPacketBufferPool,
				clientTaskManger, asynConnectedConnectionAdder, asynClientIOEventController);
		
	}

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
