package kr.pe.sinnori.client.connection.asyn.threadpool.executor;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;

public class ClientExecutorPool extends AbstractThreadPool implements ClientExecutorPoolIF {
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

		LinkedBlockingQueue<FromLetter> outputMessageQueue = new LinkedBlockingQueue<FromLetter>(
				outputMessageQueueSize);

		ClientExecutor clientExecutor = new ClientExecutor(projectName, size, outputMessageQueue, clientMessageUtility);

		pool.add(clientExecutor);

	}

	@Override
	public ClientExecutorIF getClientExecutorWithMinimumNumberOfConnetion() {
		Iterator<Thread> poolIter = pool.iterator();
		if (!poolIter.hasNext()) {
			throw new NoSuchElementException("ClientExecutorPool empty");
		}

		ClientExecutorIF minClientExecutor = (ClientExecutorIF) poolIter.next();
		int min = minClientExecutor.getNumberOfAsynConnection();

		while (poolIter.hasNext()) {
			ClientExecutorIF clientExecutor = (ClientExecutorIF) poolIter.next();
			int numberOfAsynConnection = clientExecutor.getNumberOfAsynConnection();
			if (numberOfAsynConnection < min) {
				minClientExecutor = clientExecutor;
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

}
