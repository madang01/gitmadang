package kr.pe.sinnori.client.connection;

public abstract class AbstractConnectionPoolParameter {
	protected int poolSize;
	protected int poolMaxSize;
	protected ConnectionPoolSupporterIF connectionPoolSupporter;
	
	public AbstractConnectionPoolParameter(int poolSize, int poolMaxSize,
			ConnectionPoolSupporterIF connectionPoolSupporter) {
		if (poolSize < 0) {
			String errorMessage = String.format("the parameter poolSize[%d] is less than zero", poolSize); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (poolMaxSize < 0) {
			String errorMessage = String.format("the parameter poolMaxSize[%d] is less than zero", poolMaxSize); 
			throw new IllegalArgumentException(errorMessage);
		}

		if (poolSize > poolMaxSize) {
			String errorMessage = String.format("the parameter poolSize[%d] is greater than the parameter poolMaxSize[%d]", poolSize, poolMaxSize); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == connectionPoolSupporter) {
			String errorMessage = "the parameter connectionPoolSupporter is null"; 
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.poolSize = poolSize;
		this.poolMaxSize = poolMaxSize;
		this.connectionPoolSupporter = connectionPoolSupporter;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public int getPoolMaxSize() {
		return poolMaxSize;
	}

	public ConnectionPoolSupporterIF getConnectionPoolSupporter() {
		return connectionPoolSupporter;
	}
}
