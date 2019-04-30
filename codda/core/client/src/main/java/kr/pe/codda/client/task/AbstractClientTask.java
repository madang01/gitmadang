package kr.pe.codda.client.task;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.util.CommonStaticUtil;

public abstract class AbstractClientTask {
	protected InternalLogger log = InternalLoggerFactory.getInstance(AbstractClientTask.class);
	
	private ClassLoader taskClassLoader = this.getClass().getClassLoader();
	// private MessageCodecIF clientOutputMessageCodec = null;
	private AbstractMessageDecoder outputMessageDecoder = null;
	
	public AbstractClientTask() throws DynamicClassCallException {
		String className = this.getClass().getName();
		int startIndex = className.lastIndexOf(".") + 1;		
		int endIndex = className.indexOf("ClientTask");
		String messageID = className.substring(startIndex, endIndex);
		
		String classFullName = IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(messageID);
		Object retObject = CommonStaticUtil.getNewObjectFromClassloader(taskClassLoader, classFullName);		
		
		if (! (retObject instanceof MessageCodecIF)) {
			log.warn("this instance[classloader={}] of {} class is not a instance of MessageCodecIF class",
					taskClassLoader.hashCode(),
					classFullName);
		}
		
		MessageCodecIF clientOutputMessageCodec = (MessageCodecIF)retObject;
		
		outputMessageDecoder = clientOutputMessageCodec.getMessageDecoder();
	}

	public void execute(int index, String projectName, AsynConnectionIF asynConnection,
			int mailboxID, int mailID, String messageID, Object readableMiddleObject, 
			MessageProtocolIF messageProtocol)
			throws InterruptedException {

		AbstractMessage outputMessage = null;
		try {
			outputMessage = outputMessageDecoder.decode(messageProtocol.getSingleItemDecoder(), readableMiddleObject);
		} catch (BodyFormatException e) {
			log.warn("fail to get a output message, errmsg=", e.getMessage());
			return;
		} catch (Exception e) {
			String errorMessage = "fail to get a output message";
			log.warn(errorMessage, e);
			return;
		}

		try {
			doTask(projectName, asynConnection, outputMessage);
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception | Error e) {
			
			String errorReason = new StringBuilder()
					.append("unknown error::fail to execuate the message[")
					.append("mailboxID=")
					.append(mailboxID)
					.append(", mailID=")
					.append(mailID)
					.append(", messageID=")
					.append(messageID)
					.append("]'s task::")
					.append(e.getMessage()).toString();

			log.warn(errorReason, e);
			return;
		}

		// long lastErraseTime = new java.util.Date().getTime() - firstErraseTime;
		// log.info(String.format("수행 시간=[%f] ms", (float) lastErraseTime));
	}

	abstract public void doTask(String projectName, AsynConnectionIF asynConnection, AbstractMessage outputMessage)
			throws Exception;
}
