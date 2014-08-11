package kr.pe.sinnori.impl.message.Echo;

import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;

public final class EchoClientCodec implements MessageCodecIF {

	@Override
	public MessageDecoder getMessageDecoder() throws NotSupportedException {
		return new EchoDecoder();
	}

	@Override
	public MessageEncoder getMessageEncoder() throws NotSupportedException {
		return new EchoEncoder();
	}
	
}
