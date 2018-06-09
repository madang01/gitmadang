package kr.pe.codda.client.connection;

import java.util.ArrayDeque;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.HeaderFormatException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnRes;

public abstract class ClientMessageUtility {
	
/*
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
	}*/

	/*public AbstractClientTask getClientTask(String messageID) throws DynamicClassCallException {
		return clientObjectCacheManager.getClientTask(messageID);
	}*/

	public static AbstractMessage buildOutputMessage(MessageProtocolIF messageProtocol, 
			ClientObjectCacheManagerIF clientObjectCacheManager, 
			ClassLoader classLoader,
			ReadableMiddleObjectWrapper readableMiddleObjectWrapper)
			throws DynamicClassCallException, BodyFormatException {
		if (null == classLoader) {
			throw new IllegalArgumentException("the parameter classLoader is null");
		}
		
		if (null == readableMiddleObjectWrapper) {
			throw new IllegalArgumentException("the parameter readableMiddleObjectWrapper is null");
		}
		InternalLogger log = InternalLoggerFactory.getInstance(ClientMessageUtility.class);
		
		
		String messageID = readableMiddleObjectWrapper.getMessageID();
		int mailboxID = readableMiddleObjectWrapper.getMailboxID();
		int mailID = readableMiddleObjectWrapper.getMailID();
		Object middleReadObj = readableMiddleObjectWrapper.getReadableMiddleObject();

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
					.append(readableMiddleObjectWrapper.toSimpleInformation()).append("]").toString();

			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (Exception e) {
			String errorMessage = new StringBuilder(
					"unknwon error::fail to get the client message codec of the output message[")
							.append(readableMiddleObjectWrapper.toSimpleInformation()).append("]::").append(e.getMessage())
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
					.append(readableMiddleObjectWrapper.toSimpleInformation()).append("] from readable middle object")
					.toString();

			log.warn(errorMessage);
			throw new BodyFormatException(errorMessage);
		} catch (Exception | Error e) {
			String errorMessage = new StringBuilder("unknwon error::fail to get a output message[")
					.append(readableMiddleObjectWrapper.toSimpleInformation()).append("] from readable middle object::")
					.append(e.getMessage()).toString();

			log.warn(errorMessage, e);
			throw new BodyFormatException(errorMessage);
		}

		return outputMessage;
	}

	/*public void S2MList(SocketOutputStream socketOutputStream, ReceivedMessageBlockingQueueIF wrapMessageBlockingQueue)
			throws HeaderFormatException, NoMoreDataPacketBufferException, InterruptedException {
		messageProtocol.S2MList(socketOutputStream, wrapMessageBlockingQueue);
	}*/

	public static ArrayDeque<WrapBuffer> buildReadableWrapBufferList(MessageProtocolIF messageProtocol, 
			ClientObjectCacheManagerIF clientObjectCacheManager,
			ClassLoader classLoader, AbstractMessage inputMessage)
			throws DynamicClassCallException, NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException {
		InternalLogger log = InternalLoggerFactory.getInstance(ClientMessageUtility.class);
		
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
		
		// FIXME!
		/*if (dataPacketBufferPool.size() < 2000) {
			log.info("dataPacketBufferPool.size={}", dataPacketBufferPool.size());
		}*/
		
		
		return wrapBufferList;
	}

	/*public void releaseWrapBuffer(WrapBuffer warpBuffer) {
		dataPacketBufferPool.putDataPacketBuffer(warpBuffer);
	}*/
}
