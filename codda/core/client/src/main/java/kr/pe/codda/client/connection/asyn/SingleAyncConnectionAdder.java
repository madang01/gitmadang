package kr.pe.codda.client.connection.asyn;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import kr.pe.codda.common.exception.ConnectionPoolException;

public class SingleAyncConnectionAdder implements AsynConnectedConnectionAdderIF {
	private ArrayBlockingQueue<AsynConnectionIF> connectionQueue = new ArrayBlockingQueue<AsynConnectionIF>(1);
	 

	@Override
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection) throws ConnectionPoolException {
		connectionQueue.offer(connectedAsynConnection);
		
	}

	@Override
	public void removeInterestedConnection(AsynConnectionIF interestedAsynConnection) {
		
	}
	
	
	public AsynConnectionIF poll(long timeout) throws InterruptedException {
		return connectionQueue.poll(timeout, TimeUnit.MILLISECONDS);
	}

}
