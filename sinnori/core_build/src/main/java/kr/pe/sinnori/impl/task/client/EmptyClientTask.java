package kr.pe.sinnori.impl.task.client;

import kr.pe.sinnori.client.connection.asyn.task.AbstractClientTask;
import kr.pe.sinnori.common.message.AbstractMessage;

public class EmptyClientTask extends AbstractClientTask {
	
	@Override
	public void doTask(String projectName, AbstractMessage inputMessage) throws Exception {
		log.info("inputMessage={}", inputMessage.toString());
	}
}
