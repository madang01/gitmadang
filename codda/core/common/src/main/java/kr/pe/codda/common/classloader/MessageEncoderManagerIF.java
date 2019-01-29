package kr.pe.codda.common.classloader;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;

public interface MessageEncoderManagerIF {
	public AbstractMessageEncoder getMessageEncoder(String messageID) throws DynamicClassCallException;
}
