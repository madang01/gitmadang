package kr.pe.codda.client.connection;

public interface ConnectionPoolSupporterIF {
	public void notice(String reasonForLoss);
	public void registerPool(ConnectionPoolIF connectionPool);
}
