package kr.pe.sinnori.common.classloader;

import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.protocol.MessageCodecIF;

public interface ServerSimpleClassLoaderIF {
	public MessageCodecIF getMessageCodec(String messageID) throws DynamicClassCallException;
}
