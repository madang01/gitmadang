package kr.pe.sinnori.client.connection;

import kr.pe.sinnori.common.io.SocketOutputStream;

public interface SocketResoruceIF {
	public void releaseSocketResources();
	public SocketOutputStream getSocketOutputStream();
}
