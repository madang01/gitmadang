package kr.pe.codda.client.connection.asyn.executor;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.connection.ClientMessageUtility;
import kr.pe.codda.client.connection.ClientObjectCacheManagerIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;

public abstract class AbstractClientTask {
	protected InternalLogger log = InternalLoggerFactory.getInstance(AbstractClientTask.class);
	
	private ClassLoader sourceClassLoader = null;
	
	public void setSourceClassLoader(ClassLoader sourceClassLoader) {
		this.sourceClassLoader = sourceClassLoader;
	}
	 

	public void execute(int index, String projectName, AsynConnectionIF asynConnection,
			ReadableMiddleObjectWrapper readableMiddleObjectWrapper, MessageProtocolIF messageProtocol,
			ClientObjectCacheManagerIF clientObjectCacheManager)
			throws InterruptedException {

		AbstractMessage outputMessage = null;
		try {
			outputMessage = ClientMessageUtility.buildOutputMessage(messageProtocol, clientObjectCacheManager,
					sourceClassLoader, readableMiddleObjectWrapper);
		} catch (BodyFormatException | DynamicClassCallException e) {
			return;
		}

		/*
		 * if (outputMessage instanceof SelfExnRes) { SelfExnRes selfExnRes =
		 * (SelfExnRes) outputMessage; log.warn(selfExnRes.toString()); return; }
		 */

		try {
			doTask(projectName, asynConnection, outputMessage);
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception | Error e) {
			String errorReason = String.format("unknown error::fail to execuate the message[%s]'s task::%s",
					readableMiddleObjectWrapper.toSimpleInformation(), e.getMessage());

			log.warn(errorReason, e);
			return;
		}

		// long lastErraseTime = new java.util.Date().getTime() - firstErraseTime;
		// log.info(String.format("수행 시간=[%f] ms", (float) lastErraseTime));
	}

	abstract public void doTask(String projectName, AsynConnectionIF asynConnection, AbstractMessage outputMessage)
			throws Exception;
}
