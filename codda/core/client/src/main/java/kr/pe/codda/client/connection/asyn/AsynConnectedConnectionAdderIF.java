package kr.pe.codda.client.connection.asyn;

public interface AsynConnectedConnectionAdderIF {
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection);
	public void removeInterestedConnection(AsynConnectionIF interestedAsynConnection);
}
