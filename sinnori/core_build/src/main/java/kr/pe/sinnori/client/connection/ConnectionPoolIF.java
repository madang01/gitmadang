package kr.pe.sinnori.client.connection;

import java.io.IOException;
import java.net.SocketTimeoutException;

import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

public interface ConnectionPoolIF {
	/*public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws IOException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, AccessDeniedException, InterruptedException;*/
	public AbstractConnection getConnection() throws InterruptedException, SocketTimeoutException, ConnectionPoolException;
	
	public void release(AbstractConnection conn) throws ConnectionPoolException;
	
	
	public boolean whetherConnectionIsMissing();
	public void addConnection()
			throws InterruptedException, NoMoreDataPacketBufferException, IOException, ConnectionPoolException;
	
	public void registerPoolManager(ConnectionPoolManagerIF poolManager);
	
	
}
