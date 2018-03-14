package kr.pe.sinnori.client.connection;

public interface ConnectionPoolSupporterIF {
	public void registerPool(ConnectionPoolIF connectionPool);	
	public void start() throws IllegalThreadStateException;
	public void notice(String reasonForLoss);
}
