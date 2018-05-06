package kr.pe.codda.client;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import kr.pe.codda.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;

public class AnyProjectConnectionPool implements AnyProjectConnectionPoolIF {
	private ProjectPartConfiguration mainProjectPartConfiguration = null;
	
	public AnyProjectConnectionPool(ProjectPartConfiguration mainProjectPartConfiguration) {
		this.mainProjectPartConfiguration = mainProjectPartConfiguration;
	}

	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException, BodyFormatException,
			DynamicClassCallException, ServerTaskException, AccessDeniedException, ConnectionPoolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendAsynInputMessage(AbstractMessage inputMessage) throws InterruptedException, NotSupportedException,
			ConnectionPoolException, IOException, NoMoreDataPacketBufferException, DynamicClassCallException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConnectionIF createConnection(String host, int port)
			throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPoolState() {
		return null;
	}

	
}
