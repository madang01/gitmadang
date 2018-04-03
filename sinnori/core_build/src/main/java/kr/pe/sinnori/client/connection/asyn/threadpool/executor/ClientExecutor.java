package kr.pe.sinnori.client.connection.asyn.threadpool.executor;

import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.client.connection.asyn.IOEAsynConnectionIF;
import kr.pe.sinnori.client.connection.asyn.task.AbstractClientTask;
import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class ClientExecutor extends Thread implements ClientExecutorIF {
	private Logger log = LoggerFactory.getLogger(ClientExecutor.class);

	private String projectName = null;
	private int index;

	private LinkedBlockingQueue<FromLetter> outputMessageQueue;

	private ClientMessageUtilityIF clientMessageUtility = null;

	private final Set<IOEAsynConnectionIF> socketChannelSet = Collections.synchronizedSet(new HashSet<IOEAsynConnectionIF>());

	public ClientExecutor(String projectName, int index, 
			LinkedBlockingQueue<FromLetter> outputMessageQueue,
			ClientMessageUtilityIF clientMessageUtility) {
		this.projectName = projectName;
		this.index = index;
		this.outputMessageQueue = outputMessageQueue;
		this.clientMessageUtility = clientMessageUtility;
	}

	public void run() {
		log.info("{} ClientExecutor[{}] start", projectName, index);

		try {
			while (!Thread.currentThread().isInterrupted()) {
				FromLetter fromLetter = outputMessageQueue.take();

				SocketChannel fromSC = fromLetter.getFromSocketChannel();
				WrapReadableMiddleObject wrapReadableMiddleObject = fromLetter.getWrapReadableMiddleObject();
				String messageID = wrapReadableMiddleObject.getMessageID();

				AbstractClientTask clientTask = clientMessageUtility.getClientTask(messageID);

				clientTask.execute(index, projectName, fromSC, wrapReadableMiddleObject, clientMessageUtility);
			}
			log.warn("{} ClientExecutor[{}] loop exit", projectName, index);
		} catch (InterruptedException e) {
			log.warn("{} ClientExecutor[{}] stop", projectName, index);
		} catch (Exception e) {
			String errorMessage = new StringBuilder(projectName).append(" ClientExecutor[").append(index)
					.append("] unknown error::").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
		}
	}

	@Override
	public void registerAsynConnection(IOEAsynConnectionIF asynConnection) {
		// log.info("add asynConnection[{}]", asynConnection.hashCode());
		socketChannelSet.add(asynConnection);
		
		log.debug("{} ClientExecutor[{}] new AsynConnection[{}][{}] added", projectName, index, asynConnection.hashCode());
	}

	@Override
	public int getNumberOfConnection() {
		return socketChannelSet.size();
	}

	@Override
	public void removeAsynConnection(IOEAsynConnectionIF asynConnection) {
		socketChannelSet.remove(asynConnection);
	}

	@Override
	public void putAsynOutputMessage(FromLetter fromLetter) throws InterruptedException {
		outputMessageQueue.put(fromLetter);
	}	

}
