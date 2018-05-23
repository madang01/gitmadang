package kr.pe.codda.impl.task.client;

import java.nio.channels.SocketChannel;

import kr.pe.codda.client.connection.asyn.executor.AbstractClientTask;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtilTest;
import kr.pe.codda.common.message.AbstractMessage;

/**
 * <pre>
 * test 상에서만 사용하는 EmptyClientTask 모의 객체로 
 * 클래스 풀 네임 반환을 주 목적으로 한다.
 * {@link IOPartDynamicClassNameUtilTest#testAllIOPartDynamicClassFullNameIsValid()} 에서 사용. 
 * 
 * Warning! 반듯이 클라이언트에서 구현된 EmptyClientTask 클래스와 
 * 동일 패키지와 같은 이름을 유지할것.
 * </pre>
 * @author Won Jonghoon
 *
 */
public class EmptyClientTask extends AbstractClientTask {

	@Override
	public void doTask(String projectName, SocketChannel fromSC, AbstractMessage outputMessage) throws Exception {
		log.info(outputMessage.toString());
	}
	
}
