package kr.pe.sinnori.common.io;

import java.nio.charset.CharsetDecoder;
import java.util.List;

public class SocketInputStream extends FreeSizeInputStream {

	public SocketInputStream(int dataPacketBufferMaxCount, List<WrapBuffer> dataPacketBufferList,
			CharsetDecoder streamCharsetDecoder, DataPacketBufferPoolManagerIF dataPacketBufferQueueManager) {
		super(dataPacketBufferMaxCount, dataPacketBufferList, streamCharsetDecoder, dataPacketBufferQueueManager);
	}
	
	/**
	 * <pre>
	 * 이 메소드는 decode code 로 호출된 이유 또한 없으며
	 * 이 클래스(=SocketInputStream)는 SocketOutputStream 에 종속되어 있기때문에 
	 * 데이터 패킷 버퍼 목록 반환 책임은 SocketOutputStream 에 있기때문에 아무짓도 하지 않는다.
	 * </pre>
	 */
	public void close() {
		
	}
	
	
}
