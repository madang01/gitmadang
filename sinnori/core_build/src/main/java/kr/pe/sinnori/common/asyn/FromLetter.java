package kr.pe.sinnori.common.asyn;

import java.nio.channels.SocketChannel;

import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class FromLetter {
	private SocketChannel fromSocketChannel;
	private WrapReadableMiddleObject wrapReadableMiddleObject;
	
	public FromLetter(SocketChannel fromSocketChannel, WrapReadableMiddleObject wrapReadableMiddleObject) {
		this.fromSocketChannel = fromSocketChannel;
		this.wrapReadableMiddleObject = wrapReadableMiddleObject;
	}

	public SocketChannel getFromSocketChannel() {
		return fromSocketChannel;
	}

	public WrapReadableMiddleObject getWrapReadableMiddleObject() {
		return wrapReadableMiddleObject;
	}
	
	
}
