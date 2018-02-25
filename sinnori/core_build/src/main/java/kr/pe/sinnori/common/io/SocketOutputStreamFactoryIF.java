package kr.pe.sinnori.common.io;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

public interface SocketOutputStreamFactoryIF {	
	public SocketOutputStream makeNewSocketOutputStream() throws NoMoreDataPacketBufferException;
}
