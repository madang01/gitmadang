package kr.pe.sinnori.client.connection.asyn.task;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public abstract class AbstractClientTask {
protected Logger log = LoggerFactory.getLogger(AbstractClientTask.class);
	
	private ClassLoader classLoaderOfSererTask = this.getClass().getClassLoader();
	
	public void execute(int index, String projectName,
			SocketChannel fromSC,
			WrapReadableMiddleObject wrapReadableMiddleObject,
			ClientMessageUtilityIF clientMessageUtility) throws InterruptedException {
		
		AbstractMessage outputMessage = null;	
		try {
			outputMessage = clientMessageUtility.buildOutputMessage(classLoaderOfSererTask, wrapReadableMiddleObject);
		} catch (BodyFormatException | DynamicClassCallException e) {
			return;
		}
		
		/*if (outputMessage instanceof SelfExnRes) {
			SelfExnRes selfExnRes = (SelfExnRes) outputMessage;
			log.warn(selfExnRes.toString());
			return;
		}*/
						

		try {
			doTask(projectName, fromSC, outputMessage);
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception | Error e) {
			String errorReason = String.format("unknown error::fail to execuate the message[%s]'s task::%s", 
					wrapReadableMiddleObject.toSimpleInformation(), e.getMessage());
			
			log.warn(errorReason, e);
			return;
		}

		
		// long lastErraseTime = new java.util.Date().getTime() - firstErraseTime;
		// log.info(String.format("수행 시간=[%f] ms", (float) lastErraseTime));
	}
	
	abstract public void doTask(String projectName, SocketChannel fromSC, AbstractMessage outputMessage) throws Exception;
}
