package kr.pe.codda.common.io;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public interface ReceivedDataStreamFactoryIF {	
	public ReceivedDataStream createReceivedDataStream() throws NoMoreDataPacketBufferException;
}
