package kr.pe.sinnori.client.connection.sync.noshare;

import kr.pe.sinnori.client.connection.SocketResoruceIF;
import kr.pe.sinnori.common.io.SocketOutputStream;

public class SyncPrivateSocketResource implements SocketResoruceIF {

	private SocketOutputStream socketOutputStream = null;
	
	public SyncPrivateSocketResource(SocketOutputStream socketOutputStream) {
		this.socketOutputStream = socketOutputStream;
	}	
	
	@Override
	public void releaseSocketResources() {
		socketOutputStream.close();
	}

	@Override
	public SocketOutputStream getSocketOutputStream() {
		return socketOutputStream;
	}

}
