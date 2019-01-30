package kr.pe.codda.impl.task.server;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.JavaGarbageCollectionReq.JavaGarbageCollectionReq;
import kr.pe.codda.impl.message.JavaGarbageCollectionRes.JavaGarbageCollectionRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class JavaGarbageCollectionReqServerTask extends AbstractServerTask {	
	public JavaGarbageCollectionReqServerTask() throws DynamicClassCallException {
		super();
	}

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