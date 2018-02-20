package kr.pe.sinnori.client.connection.asyn.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public abstract class AbstractClientTask {
protected Logger log = LoggerFactory.getLogger(AbstractClientTask.class);
	
	private ClassLoader classLoaderOfSererTask = this.getClass().getClassLoader();
	
	public void execute(int index, String projectName,
			AbstractAsynConnection asynConnection,
			WrapReadableMiddleObject wrapReadableMiddleObject,
			MessageProtocolIF messageProtocol, 
			ClientObjectCacheManagerIF clientObjectCacheManager) throws InterruptedException {
		
		MessageCodecIF serverInputMessageCodec = null;

		try {			
			serverInputMessageCodec = clientObjectCacheManager.getClientMessageCodec(classLoaderOfSererTask, wrapReadableMiddleObject.getMessageID());
		} catch (DynamicClassCallException e) {
			String errorReason = String.format("fail to get a input message[%s] server codec::%s", 
					wrapReadableMiddleObject.toSimpleInformation(), e.getMessage());
			
			log.warn(errorReason, e);
			return;
		} catch (Exception e) {						
			
			String errorReason = String.format("unknown error::fail to get a input message[%s] server codec::%s", 
					wrapReadableMiddleObject.toSimpleInformation(), e.getMessage());
			
			log.warn(errorReason, e);
			
			return;
		}

		AbstractMessageDecoder inputMessageDecoder = null;
		try {
			inputMessageDecoder = serverInputMessageCodec.getMessageDecoder();
		} catch (DynamicClassCallException e) {
			String errorReason = String.format("fail to get a input message[%s] decoder::%s", 
					wrapReadableMiddleObject.toSimpleInformation(), e.getMessage());
			
			log.warn(errorReason, e);
			return;
		} catch(Exception | Error e) {
			String errorReason = String.format("unknown error::fail to get a input message[%s] decoder::%s", 
					wrapReadableMiddleObject.toSimpleInformation(), e.getMessage());
			log.warn(errorReason, e);
			return;
		}

		/*log.info("classLoader[{}], serverTask[{}], create new messageDecoder", 
				classLoaderOfSererTask.hashCode(),
				inputMessageID);*/
			
		AbstractMessage inputMessage = null;
		try {
			inputMessage = inputMessageDecoder.decode(messageProtocol.getSingleItemDecoder(), wrapReadableMiddleObject.getReadableMiddleObject());
			inputMessage.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
			inputMessage.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
		} catch (BodyFormatException e) {
			String errorReason = String.format("fail to get a input message[%s] from readable middle object::%s", 
					wrapReadableMiddleObject.toSimpleInformation(), e.getMessage());
			
			log.warn(errorReason);
			return;		
		} catch(Exception | Error e) {
			String errorReason = String.format("unknown error::fail to get a input message[%s] from readable middle object::%s", 
					wrapReadableMiddleObject.toSimpleInformation(), e.getMessage());
			
			log.warn(errorReason, e);
			return;
		}
		
		// PersonalLoginManagerIF personalLoginManagerOfFromSC = socketResourceOfFromSC.getPersonalLoginManager();
					

		try {
			doTask(projectName, inputMessage);
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
	
	abstract public void doTask(String projectName, AbstractMessage inputMessage) throws Exception;
}
