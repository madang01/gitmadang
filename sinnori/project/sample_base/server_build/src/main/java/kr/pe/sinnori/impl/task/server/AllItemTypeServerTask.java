package kr.pe.sinnori.impl.task.server;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.AllItemType.AllItemType;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class AllItemTypeServerTask extends AbstractServerTask {
	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		doWork(projectName, toLetterCarrier, (AllItemType)inputMessage);
	}
	
	private void doWork(String projectName,
			ToLetterCarrier toLetterCarrier, AllItemType allDataTypeInObj)
			throws Exception {
		toLetterCarrier.addBypassOutputMessage(allDataTypeInObj);
	}
}
