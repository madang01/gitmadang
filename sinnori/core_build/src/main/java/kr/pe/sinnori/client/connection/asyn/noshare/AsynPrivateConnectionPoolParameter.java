package kr.pe.sinnori.client.connection.asyn.noshare;

import kr.pe.sinnori.client.connection.AbstractConnectionPoolParameter;
import kr.pe.sinnori.client.connection.ConnectionPoolSupporterIF;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceFactoryIF;

public class AsynPrivateConnectionPoolParameter extends AbstractConnectionPoolParameter {
	private AsynSocketResourceFactoryIF asynSocketResourceFactory = null;

	public AsynPrivateConnectionPoolParameter(int poolSize, int poolMaxSize,
			ConnectionPoolSupporterIF connectionPoolSupporter,
			AsynSocketResourceFactoryIF asynSocketResourceFactory) {
		super(poolSize, poolMaxSize, connectionPoolSupporter);
		
		if (null == asynSocketResourceFactory) {
			String errorMessage = "the parameter asynSocketResourceFactory is null"; 
			throw new IllegalArgumentException(errorMessage);
		}
		this.asynSocketResourceFactory = asynSocketResourceFactory;
	}

	public AsynSocketResourceFactoryIF getAsynSocketResourceFactory() {
		return asynSocketResourceFactory;
	}
}
