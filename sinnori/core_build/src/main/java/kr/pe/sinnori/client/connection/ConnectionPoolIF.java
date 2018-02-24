package kr.pe.sinnori.client.connection;

import java.net.SocketTimeoutException;

import kr.pe.sinnori.common.exception.ConnectionPoolException;

public interface ConnectionPoolIF {	
	public AbstractConnection getConnection() throws InterruptedException, SocketTimeoutException, ConnectionPoolException;	
	public void release(AbstractConnection conn) throws ConnectionPoolException;
	
	
	/*public boolean whetherConnectionIsMissing();
	public void addConnection()
			throws InterruptedException, NoMoreDataPacketBufferException, IOException, ConnectionPoolException;*/
	
	public void addAllLostConnections()
			throws InterruptedException;
	
	public void registerConnectionPoolSupporter(ConnectionPoolSupporterIF connectionPoolSupporter);
	
	
}
