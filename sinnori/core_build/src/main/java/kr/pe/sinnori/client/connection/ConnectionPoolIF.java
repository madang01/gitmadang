package kr.pe.sinnori.client.connection;

import java.io.IOException;
import java.net.SocketTimeoutException;

import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;

public interface ConnectionPoolIF {
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws IOException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, AccessDeniedException, InterruptedException;
	public AbstractConnection getConnection() throws InterruptedException, NotSupportedException, SocketTimeoutException;
	
	public void release(AbstractConnection conn) throws NotSupportedException;
}
