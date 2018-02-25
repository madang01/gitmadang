package kr.pe.sinnori.client.connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.asyn.task.AbstractClientTask;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;

public class ClientMessageUtility implements ClientMessageUtilityIF {
	private Logger log = LoggerFactory.getLogger(ClientMessageUtility.class);

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

	public ArrayList<WrapReadableMiddleObject> getWrapReadableMiddleObjectList(SocketOutputStream socketOutputStream)
			throws HeaderFormatException, NoMoreDataPacketBufferException {
		return messageProtocol.S2MList(socketOutputStream);
	}

	public List<WrapBuffer> buildReadableWrapBufferList(ClassLoader classLoader, AbstractMessage inputMessage)
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

		List<WrapBuffer> wrapBufferList = null;
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

	public void releaseWrapBufferList(List<WrapBuffer> warpBufferList) {
		if (null != warpBufferList) {
			Iterator<WrapBuffer> wrapBufferIterator = warpBufferList.iterator();
			while (wrapBufferIterator.hasNext()) {
				WrapBuffer wrapBuffer = wrapBufferIterator.next();
				dataPacketBufferPool.putDataPacketBuffer(wrapBuffer);
			}
		}
	}
}
