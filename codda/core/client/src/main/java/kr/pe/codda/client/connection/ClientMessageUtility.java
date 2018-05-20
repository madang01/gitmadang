package kr.pe.codda.client.connection;

import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.connection.asyn.executor.AbstractClientTask;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.HeaderFormatException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReceivedMessageBlockingQueueIF;
import kr.pe.codda.common.protocol.WrapReadableMiddleObject;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnRes;

public class ClientMessageUtility implements ClientMessageUtilityIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ClientMessageUtility.class);

	private MessageProtocolIF messageProtocol = null;
	private ClientObjectCacheManagerIF clientObjectCacheManager = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;

	public ClientMessageUtility(MessageProtocolIF messageProtocol, ClientObjectCacheManagerIF clientObjectCacheManager,
			DataPacketBufferPoolIF dataPacketBufferPool) {
		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}

		if (null == clientObjectCacheManager) {
			throw new IllegalArgumentException("the parameter clientObjectCacheManager is null");
		}

		if (null == dataPacketBufferPool) {
			throw new IllegalArgumentException("the parameter dataPacketBufferPool is null");
		}

		this.messageProtocol = messageProtocol;
		this.clientObjectCacheManager = clientObjectCacheManager;
		this.dataPacketBufferPool = dataPacketBufferPool;
	}

	public AbstractClientTask getClientTask(String messageID) throws DynamicClassCallException {
		return clientObjectCacheManager.getClientTask(messageID);
	}

	public AbstractMessage buildOutputMessage(ClassLoader classLoader,
			WrapReadableMiddleObject wrapReadableMiddleObject)
			throws DynamicClassCallException, BodyFormatException {
		if (null == classLoader) {
			throw new IllegalArgumentException("the parameter classLoader is null");
		}
		
		if (null == wrapReadableMiddleObject) {
			throw new IllegalArgumentException("the parameter wrapReadableMiddleObject is null");
		}
		
		String messageID = wrapReadableMiddleObject.getMessageID();
		int mailboxID = wrapReadableMiddleObject.getMailboxID();
		int mailID = wrapReadableMiddleObject.getMailID();
		Object middleReadObj = wrapReadableMiddleObject.getReadableMiddleObject();

		if (middleReadObj instanceof SelfExnRes) {
			/** 소켓 쓰기시 IO Exception 발생 */
			SelfExnRes selfExnRes = (SelfExnRes) middleReadObj;

			return selfExnRes;
		}

		MessageCodecIF messageCodec = clientObjectCacheManager.getClientMessageCodec(classLoader, messageID);

		AbstractMessageDecoder messageDecoder = null;
		try {
			messageDecoder = messageCodec.getMessageDecoder();
		} catch (DynamicClassCallException e) {
			String errorMessage = new StringBuilder("fail to get the client message codec of the output message[")
					.append(wrapReadableMiddleObject.toSimpleInformation()).append("]").toString();

			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (Exception e) {
			String errorMessage = new StringBuilder(
					"unknwon error::fail to get the client message codec of the output message[")
							.append(wrapReadableMiddleObject.toSimpleInformation()).append("]::").append(e.getMessage())
							.toString();

			log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}

		AbstractMessage outputMessage = null;
		try {
			outputMessage = messageDecoder.decode(messageProtocol.getSingleItemDecoder(), middleReadObj);
			outputMessage.messageHeaderInfo.mailboxID = mailboxID;
			outputMessage.messageHeaderInfo.mailID = mailID;
		} catch (BodyFormatException e) {
			String errorMessage = new StringBuilder("fail to get a output message[")
					.append(wrapReadableMiddleObject.toSimpleInformation()).append("] from readable middle object")
					.toString();

			log.warn(errorMessage);
			throw new BodyFormatException(errorMessage);
		} catch (Exception | Error e) {
			String errorMessage = new StringBuilder("unknwon error::fail to get a output message[")
					.append(wrapReadableMiddleObject.toSimpleInformation()).append("] from readable middle object::")
					.append(e.getMessage()).toString();

			log.warn(errorMessage, e);
			throw new BodyFormatException(errorMessage);
		}

		return outputMessage;
	}

	public void S2MList(SocketChannel fromSC, SocketOutputStream socketOutputStream, ReceivedMessageBlockingQueueIF wrapMessageBlockingQueue)
			throws HeaderFormatException, NoMoreDataPacketBufferException, InterruptedException {
		messageProtocol.S2MList(fromSC, socketOutputStream, wrapMessageBlockingQueue);
	}

	public ArrayDeque<WrapBuffer> buildReadableWrapBufferList(ClassLoader classLoader, AbstractMessage inputMessage)
			throws DynamicClassCallException, NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException {
		MessageCodecIF messageCodec = null;

		try {
			messageCodec = clientObjectCacheManager.getClientMessageCodec(classLoader, inputMessage.getMessageID());
		} catch (DynamicClassCallException e) {
			/*String errorMessage = new StringBuilder("fail to get a client input message codec::").append(e.getMessage())
					.toString();

			log.warn(errorMessage);*/

			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unknown error::fail to get a client input message codec::")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);

			throw new DynamicClassCallException(errorMessage);
		}

		AbstractMessageEncoder messageEncoder = null;

		try {
			messageEncoder = messageCodec.getMessageEncoder();
		} catch (DynamicClassCallException e) {
			/*String errorMessage = new StringBuilder("fail to get a input message encoder::").append(e.getMessage())
					.toString();
			log.warn(errorMessage);*/
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unkown error::fail to get a input message encoder::")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}

		ArrayDeque<WrapBuffer> wrapBufferList = null;
		try {
			wrapBufferList = messageProtocol.M2S(inputMessage, messageEncoder);
		} catch (NoMoreDataPacketBufferException e) {
			/*String errorMessage = new StringBuilder("fail to build a input message stream[")
					.append(inputMessage.getMessageID()).append("]::").append(e.getMessage()).toString();
			log.warn(errorMessage);*/

			throw e;
		} catch (BodyFormatException e) {
			/*String errorMessage = new StringBuilder("fail to build a input message stream[")
					.append(inputMessage.getMessageID()).append("]::").append(e.getMessage()).toString();
			log.warn(errorMessage);*/

			throw e;
		} catch (HeaderFormatException e) {
			/*String errorMessage = new StringBuilder("fail to build a input message stream[")
					.append(inputMessage.getMessageID()).append("]::").append(e.getMessage()).toString();
			log.warn(errorMessage, e);*/

			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unkown error::fail to get a input message encoder::")
					.append(e.getMessage()).toString();
			log.error(errorMessage, e);
			System.exit(1);
		}

		return wrapBufferList;
	}

	public void releaseWrapBuffer(WrapBuffer warpBuffer) {
		dataPacketBufferPool.putDataPacketBuffer(warpBuffer);
	}
}
