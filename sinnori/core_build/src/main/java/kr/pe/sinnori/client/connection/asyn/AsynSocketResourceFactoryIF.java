package kr.pe.sinnori.client.connection.asyn;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

public interface AsynSocketResourceFactoryIF {
	public AsynSocketResourceIF makeNewAsynSocketResource() throws NoMoreDataPacketBufferException;
}
