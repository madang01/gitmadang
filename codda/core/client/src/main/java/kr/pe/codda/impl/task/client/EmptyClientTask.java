package kr.pe.codda.impl.task.client;

import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.client.connection.asyn.executor.AbstractClientTask;
import kr.pe.codda.common.message.AbstractMessage;


public class EmptyClientTask extends AbstractClientTask {

	@Override
	public void doTask(String projectName, AsynConnectionIF asynConnection, AbstractMessage outputMessage) throws Exception {
		log.info("asyn connection[{}]::{}", asynConnection.hashCode(), outputMessage.toString());
	}
	
}
