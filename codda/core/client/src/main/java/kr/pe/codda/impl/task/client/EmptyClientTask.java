package kr.pe.codda.impl.task.client;

import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.client.task.AbstractClientTask;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;


public class EmptyClientTask extends AbstractClientTask {

	public EmptyClientTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, AsynConnectionIF asynConnection, AbstractMessage outputMessage) throws Exception {
		// log.info("socket={}, output message={}", asynConnection.hashCode(), outputMessage.toString());
	}
}
