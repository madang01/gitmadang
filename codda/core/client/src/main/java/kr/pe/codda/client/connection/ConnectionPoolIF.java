package kr.pe.codda.client.connection;

import java.io.IOException;
import java.net.SocketTimeoutException;

import kr.pe.codda.client.ConnectionIF;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public interface ConnectionPoolIF {
	public ConnectionIF getConnection() throws InterruptedException, SocketTimeoutException, ConnectionPoolException;	
	public void release(ConnectionIF conn) throws ConnectionPoolException;
	
	
	public void addAllLostConnection() throws NoMoreDataPacketBufferException, IOException, InterruptedException;
		
	public String getPoolState();
}
