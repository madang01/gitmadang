package kr.pe.sinnori.client.connection.sync.noshare;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

public interface SyncPrivateSocketResourceFactoryIF {
	public SyncPrivateSocketResource makeNewSyncPrivateSocketResource() throws NoMoreDataPacketBufferException;
}
