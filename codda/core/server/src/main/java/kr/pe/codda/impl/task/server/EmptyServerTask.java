package kr.pe.codda.impl.task.server;


import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class EmptyServerTask extends AbstractServerTask {

	public EmptyServerTask() throws DynamicClassCallException {
		super();
	}

	@Override
	public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		
		// FIXME!
		// log.info("call EmptyServerTask");
		
		toLetterCarrier.addBypassOutputMessage(inputMessage);
	}

}
