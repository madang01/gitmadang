package kr.pe.codda.impl.task.server;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.AllItemType.AllItemType;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

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
