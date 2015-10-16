package kr.pe.sinnori.common.protocol;

import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;

public interface MessageCodecIF {
	public AbstractMessageDecoder getMessageDecoder() throws DynamicClassCallException;
	public AbstractMessageEncoder getMessageEncoder() throws DynamicClassCallException;
}
