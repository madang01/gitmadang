package kr.pe.codda.client.connection.asyn;

import kr.pe.codda.common.exception.ConnectionPoolException;

public interface AsynConnectedConnectionAdderIF {
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection) throws ConnectionPoolException;
	public void removeInterestedConnection(AsynConnectionIF interestedAsynConnection);
}
