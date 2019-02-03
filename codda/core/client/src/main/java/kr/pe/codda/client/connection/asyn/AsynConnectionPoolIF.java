package kr.pe.codda.client.connection.asyn;

import java.io.IOException;

import kr.pe.codda.client.connection.ConnectionPoolIF;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public interface AsynConnectionPoolIF extends ConnectionPoolIF {
	public void removeUnregisteredConnection(ClientIOEventHandlerIF asynInterestedConnection);
	//public ClientInterestedConnectionIF newUnregisteredConnection() throws NoMoreDataPacketBufferException, IOException;
	// public void addCountOfUnregisteredConnection();
	public boolean canHasMoreInterestedConnection();
	public void addConnection() throws NoMoreDataPacketBufferException, IOException;
	
	public void setAsynSelectorManger(ClientIOEventControllerIF asynSelectorManger);
}
