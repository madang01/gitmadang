package kr.pe.codda.common.classloader;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;

public interface MessageDecoderMangerIF {
	public AbstractMessageDecoder getMessageDecoder(String messageID) throws DynamicClassCallException;
}
