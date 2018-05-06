package kr.pe.sinnori.impl.task.server;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.JavaGarbageCollectionReq.JavaGarbageCollectionReq;
import kr.pe.sinnori.impl.message.JavaGarbageCollectionRes.JavaGarbageCollectionRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class JavaGarbageCollectionReqServerTask extends AbstractServerTask {	
	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		doWork(projectName, toLetterCarrier, (JavaGarbageCollectionReq)inputMessage);
	}
	
	private void doWork(String projectName,
			ToLetterCarrier toLetterCarrier, JavaGarbageCollectionReq javaGarbageCollectionReq)
			throws Exception {		
		System.gc();
		
		JavaGarbageCollectionRes javaGarbageCollectionRes = new JavaGarbageCollectionRes();
		
		toLetterCarrier.addBypassOutputMessage(javaGarbageCollectionRes);
	}	
}