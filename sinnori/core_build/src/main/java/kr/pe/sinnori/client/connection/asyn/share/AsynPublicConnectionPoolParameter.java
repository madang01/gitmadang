package kr.pe.sinnori.client.connection.asyn.share;

import kr.pe.sinnori.client.connection.AbstractConnectionPoolParameter;
import kr.pe.sinnori.client.connection.ConnectionPoolSupporterIF;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceFactoryIF;

public class AsynPublicConnectionPoolParameter extends AbstractConnectionPoolParameter {	
	private AsynSocketResourceFactoryIF asynSocketResourceFactory;
	private	SyncMailboxPoolFactoryForAsynPublicIF asynPrivateMailboxPoolFactory;

	public AsynPublicConnectionPoolParameter(int poolSize, int poolMaxSize,
			ConnectionPoolSupporterIF connectionPoolSupporter, 
			AsynSocketResourceFactoryIF asynSocketResourceFactory,
			SyncMailboxPoolFactoryForAsynPublicIF asynPrivateMailboxPoolFactory) {
		super(poolSize, poolMaxSize, connectionPoolSupporter);
		
		if (null == asynSocketResourceFactory) {
			String errorMessage = "the parameter asynSocketResourceFactory is null"; 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == asynPrivateMailboxPoolFactory) {
			String errorMessage = "the parameter asynPrivateMailboxPoolFactory is null"; 
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.asynSocketResourceFactory = asynSocketResourceFactory;
		this.asynPrivateMailboxPoolFactory = asynPrivateMailboxPoolFactory;
		
	}

	public SyncMailboxPoolFactoryForAsynPublicIF getAsynPrivateMailboxPoolFactory() {
		return asynPrivateMailboxPoolFactory;
	}

	public AsynSocketResourceFactoryIF getAsynSocketResourceFactory() {
		return asynSocketResourceFactory;
	}	
}
