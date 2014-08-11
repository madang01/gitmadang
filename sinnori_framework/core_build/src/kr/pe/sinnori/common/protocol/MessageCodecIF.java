package kr.pe.sinnori.common.protocol;

import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.message.codec.MessageEncoder;

public interface MessageCodecIF {
	public MessageDecoder getMessageDecoder() throws NotSupportedException;
	public MessageEncoder getMessageEncoder() throws NotSupportedException;
}
