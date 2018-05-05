package kr.pe.codda.common.protocol;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;

public interface MessageCodecIF {
	public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException;
	public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException;
}
