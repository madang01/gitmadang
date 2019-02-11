package kr.pe.codda.common.io;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public interface ReceivedDataOnlyStreamFactoryIF {	
	public ReceivedDataOnlyStream createReceivedDataOnlyStream() throws NoMoreDataPacketBufferException;
}
