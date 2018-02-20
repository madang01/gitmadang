package kr.pe.sinnori.common.asyn;

import java.nio.channels.SocketChannel;

import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class FromLetter {
	private SocketChannel fromSC;
	private WrapReadableMiddleObject wrapReadableMiddleObject;
	
	public FromLetter(SocketChannel fromSC, WrapReadableMiddleObject wrapReadableMiddleObject) {
		this.fromSC = fromSC;
		this.wrapReadableMiddleObject = wrapReadableMiddleObject;
	}

	public SocketChannel getFromSocketChannel() {
		return fromSC;
	}

	public WrapReadableMiddleObject getWrapReadableMiddleObject() {
		return wrapReadableMiddleObject;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FromLetter [fromSC=");
		builder.append(fromSC.hashCode());
		builder.append(", wrapReadableMiddleObject[");		
		builder.append(wrapReadableMiddleObject.toSimpleInformation());
		builder.append("]]");
		return builder.toString();
	}
	
	
}
