package kr.pe.sinnori.client.connection.asyn.threadpool.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.threadpool.ThreadPoolIF;

public class ClientExecutorPool implements ThreadPoolIF, ClientExecutorPoolIF {
	private Logger log = LoggerFactory.getLogger(ClientExecutorPool.class);
	private final List<ClientExecutorIF> pool = new ArrayList<ClientExecutorIF>();
	
	private String projectName = null;
	private int outputMessageQueueSize;
	private ClientMessageUtilityIF clientMessageUtility = null;

	public ClientExecutorPool(int poolSize, String projectName, int outputMessageQueueSize,
			ClientMessageUtilityIF clientMessageUtility) {
		if (poolSize <= 0) {
			String errorMessage = String.format("the parameter poolSize[%d] is less than or equal to zero", poolSize);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == projectName) {
			throw new IllegalArgumentException("the parameter projectName is null");
		}

		if (outputMessageQueueSize <= 0) {
			String errorMessage = String.format(
					"the parameter outputMessageQueueSize[%d] is less than or equal to zero", outputMessageQueueSize);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == clientMessageUtility) {
			throw new IllegalArgumentException("the parameter clientMessageUtility is null");
		}

		this.projectName = projectName;
		this.outputMessageQueueSize = outputMessageQueueSize;
		this.clientMessageUtility = clientMessageUtility;

		for (int i = 0; i < poolSize; i++) {
			try {
				innserAddTask();
			} catch (IllegalStateException e) {
				log.error(e.getMessage(), e);
				System.exit(1);
			}
		}
	}

	private void innserAddTask() throws IllegalStateException {

		int size = pool.size();

		ArrayBlockingQueue<FromLetter> outputMessageQueue = new ArrayBlockingQueue<FromLetter>(
				outputMessageQueueSize);

		ClientExecutor clientExecutor = new ClientExecutor(projectName, size, outputMessageQueue, clientMessageUtility);

		pool.add(clientExecutor);

	}

	@Override
	public ClientExecutorIF getClientExecutorWithMinimumNumberOfConnetion() {
		if (pool.isEmpty()) {
			throw new NoSuchElementException("ClientExecutorPool empty");
		}
		
		int min = Integer.MAX_VALUE;
		ClientExecutorIF minClientExecutor = null;		

		for (ClientExecutorIF handler : pool) {
			int numberOfAsynConnection = handler.getNumberOfConnection();
			if (numberOfAsynConnection < min) {
				minClientExecutor = handler;
				min = numberOfAsynConnection;
			}
		}		
		
		return minClientExecutor;
	}

	@Override
	public void addTask() throws IllegalStateException, NotSupportedException {
		String errorMessage = "this ClientExecutorPool dosn't support this addTask method";
		throw new NotSupportedException(errorMessage);
	}

	@Override
	public int getPoolSize() {
		return pool.size();
	}

	@Override
	public void startAll() {
		for (ClientExecutorIF handler: pool) {			
			if (handler.isAlive()) continue;
			handler.start();
		}
	}

	@Override
	public void stopAll() {
		for (ClientExecutorIF handler: pool) {			
			if (handler.isAlive()) continue;
			handler.interrupt();
		}
	}
}
