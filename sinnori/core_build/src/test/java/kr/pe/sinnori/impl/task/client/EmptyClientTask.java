package kr.pe.sinnori.impl.task.client;

import java.nio.channels.SocketChannel;

import kr.pe.sinnori.client.connection.asyn.task.AbstractClientTask;
import kr.pe.sinnori.common.classloader.IOPartDynamicClassNameUtilTest;
import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * {@link IOPartDynamicClassNameUtilTest#testAllIOPartDynamicClassFullNameIsValid} 에서 이용
 * @author Won Jonghoon
 *
 */
public class EmptyClientTask extends AbstractClientTask {
	
	@Override
	public void doTask(String projectName, SocketChannel fromSC, AbstractMessage inputMessage) throws Exception {
		log.info("inputMessage={}", inputMessage.toString());
	}
}
