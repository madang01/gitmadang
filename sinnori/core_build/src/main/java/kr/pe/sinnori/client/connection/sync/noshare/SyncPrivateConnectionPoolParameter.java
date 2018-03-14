package kr.pe.sinnori.client.connection.sync.noshare;

import kr.pe.sinnori.client.connection.AbstractConnectionPoolParameter;
import kr.pe.sinnori.client.connection.ConnectionPoolSupporterIF;

public class SyncPrivateConnectionPoolParameter extends AbstractConnectionPoolParameter {
	private SyncPrivateSocketResourceFactoryIF syncPrivateSocketResourceFactory =null;

	public SyncPrivateConnectionPoolParameter(int poolSize, int poolMaxSize,
			ConnectionPoolSupporterIF connectionPoolSupporter,
			SyncPrivateSocketResourceFactoryIF syncPrivateSocketResourceFactory) {
		super(poolSize, poolMaxSize, connectionPoolSupporter);
		
		if (null == syncPrivateSocketResourceFactory) {
			String errorMessage = "the parameter syncPrivateSocketResourceFactory is null"; 
			throw new IllegalArgumentException(errorMessage);
		}
				
		this.syncPrivateSocketResourceFactory = syncPrivateSocketResourceFactory;
	}

	public SyncPrivateSocketResourceFactoryIF getSyncPrivateSocketResourceFactory() {
		return syncPrivateSocketResourceFactory;
	}
	
}
