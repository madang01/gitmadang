package kr.pe.codda.impl.task.server;

import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtilTest;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * <pre>
 * test 상에서만 사용하는 EmptyServerTask 모의 객체로 
 * 클래스 풀 네임 반환을 주 목적으로 한다. 
 * {@link IOPartDynamicClassNameUtilTest#testAllIOPartDynamicClassFullNameIsValid()}에서 사용. 
 * 
 * Warning! 반듯이 서버에서 구현된 EmptyServerTask 클래스와 
 * 동일 패키지와 같은 이름을 유지할것.
 * </pre>
 * @author Won Jonghoon
 *
 */
public class EmptyServerTask extends AbstractServerTask {

	@Override
	public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		
		// FIXME!
		// log.info("call EmptyServerTask");
		
		toLetterCarrier.addBypassOutputMessage(inputMessage);
	}

}
