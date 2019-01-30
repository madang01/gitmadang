package kr.pe.codda.impl.classloader;

import java.util.HashMap;

import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageCodecIF;
import kr.pe.codda.common.util.CommonStaticUtil;

public class ClientMessageCodecManger implements MessageCodecMangerIF {
	private HashMap<String, MessageCodecIF> messageID2ClientMessageCodecHash = 
			new HashMap<String, MessageCodecIF>();
	private ClassLoader classloader = this.getClass().getClassLoader();
	
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class MessageCodecMangerHolder {
		static final ClientMessageCodecManger singleton = new ClientMessageCodecManger();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static ClientMessageCodecManger getInstance() {
		return MessageCodecMangerHolder.singleton;
	}
	
	private ClientMessageCodecManger() {		
	}

	@Override
	public AbstractMessageDecoder getMessageDecoder(String messageID) throws DynamicClassCallException {
		MessageCodecIF clientMessageCodec = messageID2ClientMessageCodecHash.get(messageID);
		
		if (null == clientMessageCodec) {
			synchronized (messageID2ClientMessageCodecHash) {
				clientMessageCodec = messageID2ClientMessageCodecHash.get(messageID);
				if (null == clientMessageCodec) {
					String clientMessageCodecClassFullName = IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(messageID);
					
					Object retObject = CommonStaticUtil.getNewObjectFromClassloader(classloader, clientMessageCodecClassFullName);
					
					if (! (retObject instanceof MessageCodecIF)) {
						String errorMessage = new StringBuilder()
								.append("this instance of ")
								.append(clientMessageCodecClassFullName)
								.append(" class that was created by client classloader[")
								.append(classloader.hashCode())			
								.append("] class is not a instance of MessageCodecIF class").toString();
						throw new DynamicClassCallException(errorMessage);
					}
					clientMessageCodec = (MessageCodecIF)retObject;					
					messageID2ClientMessageCodecHash.put(messageID, clientMessageCodec);
				}
			}
		} 
		
		return clientMessageCodec.getMessageDecoder();
	}

	@Override
	public AbstractMessageEncoder getMessageEncoder(String messageID) throws DynamicClassCallException {
		MessageCodecIF clientMessageCodec = messageID2ClientMessageCodecHash.get(messageID);
		
		if (null == clientMessageCodec) {
			synchronized (messageID2ClientMessageCodecHash) {
				clientMessageCodec = messageID2ClientMessageCodecHash.get(messageID);
				if (null == clientMessageCodec) {
					String clientMessageCodecClassFullName = IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(messageID);
					
					Object retObject = CommonStaticUtil.getNewObjectFromClassloader(classloader, clientMessageCodecClassFullName);
					
					if (! (retObject instanceof MessageCodecIF)) {
						String errorMessage = new StringBuilder()
								.append("this instance of ")
								.append(clientMessageCodecClassFullName)
								.append(" class that was created by client classloader[")
								.append(classloader.hashCode())			
								.append("] class is not a instance of MessageCodecIF class").toString();
						throw new DynamicClassCallException(errorMessage);
					}
					clientMessageCodec = (MessageCodecIF)retObject;					
					messageID2ClientMessageCodecHash.put(messageID, clientMessageCodec);
				}
			}
		}
		
		return clientMessageCodec.getMessageEncoder();		
	}	
}
