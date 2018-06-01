package kr.pe.codda.common.io;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public interface SocketOutputStreamFactoryIF {	
	public SocketOutputStream createSocketOutputStream() throws NoMoreDataPacketBufferException;
}
