package kr.pe.codda.client.connection;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.classloader.MessageDecoderMangerIF;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ProtocolUtil;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.impl.message.SelfExnRes.SelfExnRes;

public abstract class ClientMessageUtility {

	/**
	 * 변수 'readableMiddleObject' 를 디코딩하여 출력 메시지를 반환한다. 단 동적 호출 클래스 호출이 실패하였거나 디코딩 실패시 SelfExnRes 을 반환한다.
	 * 
	 * @param messageCodecManger
	 * @param messageProtocol
	 * @param mailboxID
	 * @param mailID
	 * @param messageID
	 * @param readableMiddleObject
	 * @return
	 */
	public static AbstractMessage buildOutputMessage(String title,
			MessageDecoderMangerIF messageCodecManger, 
			MessageProtocolIF messageProtocol,
			int mailboxID, int mailID, String messageID, Object readableMiddleObject) {
		
		try {
			AbstractMessageDecoder messageDecoder = null;		
			try {
				messageDecoder = messageCodecManger.getMessageDecoder(messageID);
			} catch (DynamicClassCallException e) {
				String errorMessage = new StringBuilder("fail to get the client message decoder of the ")
						.append(title)
						.append(" output message")
						.append("mailboxID=")
						.append(mailboxID)
						.append(", mailID=")
						.append(mailID)
						.append(", messageID=")
						.append(messageID)
						.append("], errmsg=")
						.append(e.getMessage()).toString();

				InternalLogger log = InternalLoggerFactory.getInstance(ClientMessageUtility.class);
				log.warn(errorMessage);
					
				
				SelfExnRes selfExnRes = new SelfExnRes();
				selfExnRes.messageHeaderInfo.mailboxID = mailboxID;
				selfExnRes.messageHeaderInfo.mailID = mailID;
				selfExnRes.setErrorPlace(SelfExn.ErrorPlace.CLIENT);
				selfExnRes.setErrorType(SelfExn.ErrorType.valueOf(DynamicClassCallException.class));
			
				selfExnRes.setErrorMessageID(messageID);
				selfExnRes.setErrorReason(errorMessage);
				
				return selfExnRes;
			} catch (Exception e) {
				String errorMessage = new StringBuilder(
						"unknwon error::fail to get the client message decoder of the ")
						.append(title)
						.append(" output message[")
						.append("mailboxID=")
						.append(mailboxID)
						.append(", mailID=")
						.append(mailID)
						.append(", messageID=")
						.append(messageID)
								.append("], errmsg=").append(e.getMessage())
								.toString();
				
				InternalLogger log = InternalLoggerFactory.getInstance(ClientMessageUtility.class);
				log.warn(errorMessage, e);
				
				SelfExnRes selfExnRes = new SelfExnRes();
				selfExnRes.messageHeaderInfo.mailboxID = mailboxID;
				selfExnRes.messageHeaderInfo.mailID = mailID;
				selfExnRes.setErrorPlace(SelfExn.ErrorPlace.CLIENT);
				selfExnRes.setErrorType(SelfExn.ErrorType.valueOf(DynamicClassCallException.class));
			
				selfExnRes.setErrorMessageID(messageID);
				selfExnRes.setErrorReason(errorMessage);
				
				return selfExnRes;
			}

			AbstractMessage outputMessage = null;
			try {
				outputMessage = messageDecoder.decode(messageProtocol.getSingleItemDecoder(), readableMiddleObject);
				outputMessage.messageHeaderInfo.mailboxID = mailboxID;
				outputMessage.messageHeaderInfo.mailID = mailID;
			} catch (BodyFormatException e) {
				String errorMessage = new StringBuilder("fail to decode the var 'readableMiddleObject' of the ")
						.append(title)
						.append(" output message")
						.append("mailboxID=")
						.append(mailboxID)
						.append(", mailID=")
						.append(mailID)
						.append(", messageID=")
						.append(messageID)
						.append("], errmsg=")
						.append("")
						.append(e.getMessage())
						.toString();

				InternalLogger log = InternalLoggerFactory.getInstance(ClientMessageUtility.class);
				log.warn(errorMessage);		
				
				SelfExnRes selfExnRes = new SelfExnRes();
				selfExnRes.messageHeaderInfo.mailboxID = mailboxID;
				selfExnRes.messageHeaderInfo.mailID = mailID;
				selfExnRes.setErrorPlace(SelfExn.ErrorPlace.CLIENT);
				selfExnRes.setErrorType(SelfExn.ErrorType.valueOf(BodyFormatException.class));
			
				selfExnRes.setErrorMessageID(messageID);
				selfExnRes.setErrorReason(errorMessage);
				
				return selfExnRes;
			} catch (Exception | Error e) {
				String errorMessage = new StringBuilder("unknow error::fail to decode the var 'readableMiddleObject' of the ")
						.append(title)
						.append(" output message")
						.append("mailboxID=")
						.append(mailboxID)
						.append(", mailID=")
						.append(mailID)
						.append(", messageID=")
						.append(messageID)
						.append("], errmsg=")
						.append("")
						.append(e.getMessage())
						.toString();
				
				InternalLogger log = InternalLoggerFactory.getInstance(ClientMessageUtility.class);
				log.warn(errorMessage, e);
				
				SelfExnRes selfExnRes = new SelfExnRes();
				selfExnRes.messageHeaderInfo.mailboxID = mailboxID;
				selfExnRes.messageHeaderInfo.mailID = mailID;
				selfExnRes.setErrorPlace(SelfExn.ErrorPlace.CLIENT);
				selfExnRes.setErrorType(SelfExn.ErrorType.valueOf(BodyFormatException.class));
			
				selfExnRes.setErrorMessageID(messageID);
				selfExnRes.setErrorReason(errorMessage);
				
				return selfExnRes;
			}
			
			outputMessage.messageHeaderInfo.mailboxID = mailboxID;
			outputMessage.messageHeaderInfo.mailID = mailID;

			return outputMessage;
		} finally {
			ProtocolUtil.closeReadableMiddleObject(mailboxID, mailID, messageID, readableMiddleObject);
		}	
	}
	/*
	private static ArrayDeque<WrapBuffer> buildReadableWrapBufferList(
			MessageEncoderManagerIF messageEncoderManager, 
			MessageProtocolIF messageProtocol,
			AbstractMessage inputMessage)
			throws DynamicClassCallException, NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException {
		InternalLogger log = InternalLoggerFactory.getInstance(ClientMessageUtility.class);
		
		AbstractMessageEncoder messageEncoder = null;

		try {
			messageEncoder = messageEncoderManager.getMessageEncoder(inputMessage.getMessageID());
		} catch (DynamicClassCallException e) {
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
			throw e;
		} catch (BodyFormatException e) {
			throw e;
		} catch (HeaderFormatException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unkown error::fail to get a input message encoder::")
					.append(e.getMessage()).toString();
			log.error(errorMessage, e);
			System.exit(1);
		}
		
		
		
		
		return wrapBufferList;
	}
*/
}
