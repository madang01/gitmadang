package kr.pe.sinnori.client.connection.sync.noshare;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.SocketOutputStreamFactoryIF;

public class SyncPrivateSocketResourceFactory implements SyncPrivateSocketResourceFactoryIF {
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	
	public SyncPrivateSocketResourceFactory(SocketOutputStreamFactoryIF socketOutputStreamFactory) {
		if (null == socketOutputStreamFactory) {
			throw new IllegalArgumentException("the parameter socketOutputStreamFactory is null");
		}
		
		this.socketOutputStreamFactory = socketOutputStreamFactory;
	}

	@Override
	public SyncPrivateSocketResource makeNewSyncPrivateSocketResource() throws NoMoreDataPacketBufferException {
		SocketOutputStream socketOutputStream = socketOutputStreamFactory.makeNewSocketOutputStream();
		return new SyncPrivateSocketResource(socketOutputStream);
	}

}
