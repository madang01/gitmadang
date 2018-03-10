package kr.pe.sinnori.client.connection.asyn.threadpool.executor;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;

public class ClientExecutorPool extends AbstractThreadPool implements ClientExecutorPoolIF {
	private String projectName = null;
	private int outputMessageQueueSize;
	private ClientMessageUtilityIF clientMessageUtility = null;
	
	public ClientExecutorPool(String projectName, int size, 
			int outputMessageQueueSize,
			ClientMessageUtilityIF clientMessageUtility) {
		this.projectName = projectName;
		this.outputMessageQueueSize = outputMessageQueueSize;
		this.clientMessageUtility = clientMessageUtility;
		
		for (int i=0; i < size; i++) {
			addTask();
		}
	}

	@Override
	public ClientExecutorIF getClientExecutorWithMinimumNumberOfConnetion() {		
		Iterator<Thread> poolIter = pool.iterator();
		if (! poolIter.hasNext()) {
			throw new NoSuchElementException("ClientExecutorPool empty");
		}		
		
		ClientExecutorIF minClientExecutor = (ClientExecutorIF)poolIter.next();
		int min = minClientExecutor.getNumberOfAsynConnection();
		
		while (poolIter.hasNext()) {
			ClientExecutorIF clientExecutor = (ClientExecutorIF)poolIter.next();
			int numberOfAsynConnection = clientExecutor.getNumberOfAsynConnection();
			if (numberOfAsynConnection < min) {
				minClientExecutor = clientExecutor;
				min = numberOfAsynConnection;
			}
		}
		
		return minClientExecutor;
	}

	@Override
	public void addTask() throws IllegalStateException {
		synchronized (monitor) {
			int size = pool.size();
			
			LinkedBlockingQueue<FromLetter> outputMessageQueue = new LinkedBlockingQueue<FromLetter>(outputMessageQueueSize); 
			
			ClientExecutor clientExecutor = new ClientExecutor(projectName, size,
					outputMessageQueue,
					clientMessageUtility);
			
			pool.add(clientExecutor);
		}
	}

}
