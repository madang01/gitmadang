package kr.pe.sinnori.client.connection;

import java.io.IOException;
import java.net.SocketTimeoutException;

import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

public interface ConnectionPoolIF {	
	public AbstractConnection getConnection() throws InterruptedException, SocketTimeoutException, ConnectionPoolException;	
	public void release(AbstractConnection conn) throws ConnectionPoolException;	
	
	public void addAllLostConnections() throws InterruptedException, ConnectionPoolException, NoMoreDataPacketBufferException, IOException;
	
	public int getNumberOfConnection();
	
	public String getPoolState();
	
	public boolean addConnection() throws InterruptedException, ConnectionPoolException, NoMoreDataPacketBufferException, IOException;
	
}
