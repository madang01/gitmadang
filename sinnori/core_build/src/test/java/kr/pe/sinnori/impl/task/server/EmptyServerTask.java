package kr.pe.sinnori.impl.task.server;

import kr.pe.sinnori.common.classloader.IOPartDynamicClassNameUtilTest;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

/**
 * {@link IOPartDynamicClassNameUtilTest#testAllIOPartDynamicClassFullNameIsValid} 에서 이용
 * @author Won Jonghoon
 *
 */
public class EmptyServerTask extends AbstractServerTask {

	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		
		//log.info("inputMessage messageHeaderInfo={}", inputMessage.messageHeaderInfo.toString());
		
		toLetterCarrier.addBypassOutputMessage(inputMessage);
	}
}
